package com.example.SudokuShowdown.controllers;

import com.example.SudokuShowdown.game.CompletionMessage;
import com.example.SudokuShowdown.lobby.Difficulty;
import com.example.SudokuShowdown.models.Room;
import com.example.SudokuShowdown.lobby.RoomAction;
import com.example.SudokuShowdown.lobby.RoomService;
import com.example.SudokuShowdown.models.User;
import com.example.SudokuShowdown.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;


@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost")
public class GameController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    private RoomService roomService;
    private UserRepository userRepository;

    @GetMapping("/rooms")
    @ResponseBody
    public Collection<Room> getRooms() {
        log.info("Received request to get all rooms.");
        Collection<Room> rooms = roomService.getAllRooms();
        log.info("Returning {} rooms.", rooms.size());
        return rooms;
    }
    @GetMapping("/room/{roomName}")
    public ResponseEntity<Room> getRoom(@PathVariable String roomName) {
        Room room = roomService.findRoom(roomName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));
        return new ResponseEntity<>(room, HttpStatus.OK);
    }

    @MessageMapping("/game/create")
    @SendTo({"/topic/games","/topic/rooms"})
    public Room createGame(@Payload RoomAction request){
        Room room = roomService.createRoom(request.getRoomName(), Difficulty.valueOf(request.getDifficulty()), request.getSize(), request.getPassword());
        return room;
    }

    @MessageMapping("/game/join")
    @SendTo("/topic/games")
    public Room joinGame(@Payload RoomAction request) {
        Room room = roomService.findRoom(request.getRoomName())
                .orElseThrow(() -> new IllegalArgumentException("Room does not exist"));
        User user = userRepository.findByUsername(request.getUsername());
        if (user == null) {
            throw new IllegalArgumentException("User does not exist");
        }
        room.join(user);
        roomService.updateRoomStatus(room);
        return room;
    }

    @MessageMapping("/game/leave")
    @SendTo("/topic/games")
    public Room leaveGame(@Payload RoomAction request) {
        Room room = roomService.findRoom(request.getRoomName())
                .orElseThrow(() -> new IllegalArgumentException("Room does not exist"));
        User user = userRepository.findByUsername(request.getUsername());
        if (user == null) {
            throw new IllegalArgumentException("User does not exist");
        }

        room.leave(user);
        roomService.updateRoomStatus(room);
        if (room.getUsers().isEmpty()) {
            roomService.removeRoom(request.getRoomName());
        }
        return room;
    }


    @MessageMapping("/game/start")
    public void startGame(@Payload String roomId) {
        System.out.println("Start Game");

       simpMessagingTemplate.convertAndSend("/room/"+roomId, roomId);
    }

    @MessageMapping("/game/complete")
    public void receiveCompletion(@Payload CompletionMessage completionMessage) {
        System.out.println("Room Id: " + completionMessage.getRoomId() + " Completion type: " + completionMessage.getCompletionType() + " at index: " + completionMessage.getCompletionRow() + "-" + completionMessage.getCompletionCol());

        simpMessagingTemplate.convertAndSend("/room/"+completionMessage.getRoomId(), completionMessage);
    }
}
