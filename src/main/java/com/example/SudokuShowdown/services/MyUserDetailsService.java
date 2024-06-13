package com.example.SudokuShowdown.services;

import com.example.SudokuShowdown.models.User;
import com.example.SudokuShowdown.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.util.List;


@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.example.SudokuShowdown.models.User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return new CustomUserDetails(user);
    }

    public List<User> getFirstTenItemsSortedAscending() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "winrate"));
        return userRepository.findAll(pageable).getContent();
    }
}
