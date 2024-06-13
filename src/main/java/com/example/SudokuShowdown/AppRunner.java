package com.example.SudokuShowdown;

import com.example.SudokuShowdown.models.User;
import com.example.SudokuShowdown.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AppRunner implements ApplicationRunner {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        // for test
        User test = userRepository.findByUsername("test");
        if (test == null) {
            test = new User();
            test.setUsername("test");
            test.setPassword(passwordEncoder.encode("1234"));
            test.setEmail("test@test.com");
            test.setRole("USER");
            test.setGamesWon(2);
            test.setGamesLost(1);
            test.setWinrate(66.6);
            userRepository.save(test);
        }
        User test2 = userRepository.findByUsername("test2");
        if (test2 == null) {
            test2 = new User();
            test2.setUsername("test2");
            test2.setPassword(passwordEncoder.encode("1234"));
            test2.setEmail("test2@test.com");
            test2.setRole("USER");
            test2.setGamesWon(1);
            test2.setGamesLost(2);
            test2.setWinrate(33.3);
            userRepository.save(test2);
        }
        User test3 = userRepository.findByUsername("test3");
        if (test3 == null) {
            test3 = new User();
            test3.setUsername("test3");
            test3.setPassword(passwordEncoder.encode("1234"));
            test3.setEmail("test3@test.com");
            test3.setRole("USER");
            test3.setGamesWon(0);
            test3.setGamesLost(3);
            test3.setWinrate(0);
            userRepository.save(test3);
        }
    }
}