package com.trainingplatform.service;

import com.trainingplatform.dto.RegisterDto;
import com.trainingplatform.entity.User;
import com.trainingplatform.exception.ValidationException;
import com.trainingplatform.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(RegisterDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ValidationException("Email is already registered");
        }
        User user = new User();
        user.setName(dto.getName().trim());
        user.setEmail(dto.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);
    }
}
