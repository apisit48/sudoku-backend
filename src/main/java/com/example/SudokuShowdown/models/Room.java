package com.example.SudokuShowdown.models;

import com.example.SudokuShowdown.lobby.Difficulty;
import com.example.SudokuShowdown.lobby.GameStatus;
import com.example.SudokuShowdown.models.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.*;

@Transactional
@Getter
@Setter
@Entity
@Table(name= "room")
@EqualsAndHashCode(exclude = "users")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(unique = true)
    private String roomName;
    private int maxUsers;
    private boolean isFull;
    private String password;
    @Enumerated(EnumType.STRING)
    private GameStatus status;
    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_room",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    @JsonManagedReference
    private List<User> users = new ArrayList<>();


    public void join(User user) {
        if (this.status == GameStatus.FULL){
            throw new IllegalArgumentException("The room is already full.");
        }
        if (!users.contains(user)) {
            users.add(user);
        }
        if (users.size() >= maxUsers) {
            this.isFull = true;
            this.status = GameStatus.FULL;
        }
        if (!user.getRooms().contains(this)) {
            user.getRooms().add(this);
        }
    }

    public void leave(User user) {
        if (users.contains(user)) {
            users.remove(user);
            if (users.size() < maxUsers) {
                this.isFull = false;
                this.status = GameStatus.AVAILABLE;
            }
            if (user.getRooms().contains(this)) {
                user.getRooms().remove(this);
            }
        }
    }

}


