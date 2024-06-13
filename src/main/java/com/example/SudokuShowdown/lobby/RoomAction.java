package com.example.SudokuShowdown.lobby;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomAction {
    private String roomName;
    private String username;
    private String difficulty;
    private String size;
    private String password;
}
