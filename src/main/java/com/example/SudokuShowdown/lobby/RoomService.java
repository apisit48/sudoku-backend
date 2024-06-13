package com.example.SudokuShowdown.lobby;

import com.example.SudokuShowdown.models.Room;
import com.example.SudokuShowdown.models.User;
import com.example.SudokuShowdown.repository.RoomRepository;
import com.example.SudokuShowdown.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Transactional
@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private UserRepository userRepository;

    public Room createRoom(String roomName, Difficulty difficulty, String size, String password) {
        if (roomName == null) {
            throw new IllegalArgumentException("Room parameters cannot be null");
        }
        Room existingRoom = roomRepository.findByRoomName(roomName);
        if (existingRoom != null) {
            throw new IllegalArgumentException("A room with the name " + roomName + " already exists");
        }

        int maxUsersPerRoom;
        try {
            maxUsersPerRoom = Integer.parseInt(size);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid size: " + size + ". It should be an integer.");
        }

        Room room = new Room();
        room.setRoomName(roomName);
        room.setStatus(GameStatus.AVAILABLE);
        room.setDifficulty(difficulty);
        room.setMaxUsers(maxUsersPerRoom);
        room.setPassword(password);

        roomRepository.save(room);
        return room;
    }

    public Optional<Room> findRoom(String roomName) {
        Optional<Room> room = Optional.ofNullable(roomRepository.findByRoomName(roomName));
        room.ifPresent(this::updateRoomStatus);
        return room;
    }

    @Transactional
    public void updateRoomStatus(Room room) {
        if (room.getUsers().size() >= room.getMaxUsers()) {
            room.setStatus(GameStatus.FULL);
        } else {
            room.setStatus(GameStatus.AVAILABLE);
        }
        roomRepository.save(room);
    }

    public void removeRoom(String roomName) {
        Room room = roomRepository.findByRoomName(roomName);
        if (room != null) {
            roomRepository.delete(room);
        }
    }

    public void joinRoom(Long roomId, String userId) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new IllegalArgumentException("Room not found with id: " + roomId));
        User user = userRepository.findByUsername(userId);

        room.getUsers().add(user);
        userRepository.save(user);
        roomRepository.save(room);
    }

    public void leaveRoom(Long roomId, String userId) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new IllegalArgumentException("Room not found with id: " + roomId));
        User user = userRepository.findByUsername(userId);

        room.getUsers().remove(user);
        userRepository.save(user);
        roomRepository.save(room);
    }

    public Collection<Room> getAllRooms() {
        return roomRepository.findAll();
    }
}

