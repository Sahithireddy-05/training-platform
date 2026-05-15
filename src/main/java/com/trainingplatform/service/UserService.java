package com.trainingplatform.service;

import com.trainingplatform.entity.User;
import com.trainingplatform.exception.ResourceNotFoundException;
import com.trainingplatform.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
