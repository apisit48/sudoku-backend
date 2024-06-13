package com.example.SudokuShowdown.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CompletionMessage implements PlayerMessage {

    private String roomId;
    private String completionType;
    private int completionRow;
    private int completionCol;


}
