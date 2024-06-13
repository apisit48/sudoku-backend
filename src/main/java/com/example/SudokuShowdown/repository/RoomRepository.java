package com.example.SudokuShowdown.repository;

import com.example.SudokuShowdown.models.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Room findByRoomName(String username);

}
