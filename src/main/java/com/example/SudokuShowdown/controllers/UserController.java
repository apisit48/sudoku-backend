package com.example.SudokuShowdown.controllers;

import com.example.SudokuShowdown.UserDTO;
import com.example.SudokuShowdown.models.User;
import com.example.SudokuShowdown.repository.UserRepository;
import com.example.SudokuShowdown.services.CustomUserDetails;
import com.example.SudokuShowdown.services.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    MyUserDetailsService userDetailsService;

    private boolean login = false;

    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<?> register(HttpServletRequest request) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        if(userRepository.findByUsername(username)!=null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username taken");
        }
        try {
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setEmail(email);
            user.setRole("USER");
            userRepository.save(user);
            return ResponseEntity.ok(user.getId());
        }
        catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Couldn't register for some reason");
        }
    }
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> login(HttpServletRequest request) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof org.springframework.security.core.userdetails.User) {
                request.logout();
            }
            request.login(username, password);
            CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);
            UserDTO userDTO = new UserDTO();
            userDTO.setId(userDetails.getId());
            userDTO.setName(userDetails.getUsername());
            userDTO.setEmail(userDetails.getEmail());
            userDTO.setRole(userDetails.getRole());
            login = true;
            return ResponseEntity.ok(userDTO);
        }
        catch (Exception ex) {
            System.out.println(ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid login request");
        }
    }

    @PostMapping("/logout")
    @ResponseBody
    public ResponseEntity<?> logout(HttpServletRequest request) {
        try {
            request.logout();
            login = false;
            return ResponseEntity.ok("Logged out");
        }
        catch (Exception ex) {
            System.out.println(ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request");
        }
    }

    @GetMapping("/get")
    @ResponseBody
    public ResponseEntity<?> getUser(HttpServletRequest request) {
        try {
            return ResponseEntity.ok(userRepository.findByUsername(request.getParameter("username")));
        }
        catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request");
        }
    }

    @DeleteMapping("/delete")
    @ResponseBody
    public ResponseEntity<?> delete(long id) {
        try {
            userRepository.deleteById(id);
            return ResponseEntity.ok("Deleted");
        }
        catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Couldn't delete user");
        }
    }

    @PutMapping("/update")
    @ResponseBody
    public ResponseEntity<?> updateUser(HttpServletRequest request) {
        try {
            User user = userRepository.findByUsername(request.getParameter("username"));
            user.setUsername(request.getParameter("username"));
            user.setEmail(request.getParameter("email"));
            user.setPassword(request.getParameter("password"));
            userRepository.save(user);
            return ResponseEntity.ok("Updated");
        }
        catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Couldn't update user");
        }
    }

    @GetMapping("/status")
    @ResponseBody
    public ResponseEntity<?> getLoginStatus() {
        return ResponseEntity.ok(login);
    }

    @GetMapping("/{playerUsername}/stats")
    @ResponseBody
    public Map<String, String> stats(@PathVariable("playerUsername") String playerUsername) {
        try {
            User user = userRepository.findByUsername(playerUsername);
            int gamesWon = user.getGamesWon();
            int gamesLost = user.getGamesLost();
            double winrate = user.getWinrate();
            Map<String, String> playerStats = new HashMap<>();
            playerStats.put("username", playerUsername);
            playerStats.put("won", String.valueOf(gamesWon));
            playerStats.put("lost", String.valueOf(gamesLost));
            playerStats.put("wr", String.valueOf(winrate));
            return playerStats;
        }
        catch (Exception ex) {
            Map<String, String> er = new HashMap<>();
            er.put("error", ex.toString());
            return er;
        }
    }

    @GetMapping("/ranking")
    @ResponseBody
    public ResponseEntity<Iterable<User>> ranking() {
        return ResponseEntity.ok(userDetailsService.getFirstTenItemsSortedAscending());
    }

    @GetMapping("/users")
    public List<User> FetchUsers() {
        return userRepository.findAll();
    }
}
