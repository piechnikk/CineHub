package com.agh.cinehub_backend.service;

import com.agh.cinehub_backend.DTO.RegisterRequest;
import com.agh.cinehub_backend.DTO.UserDto;
import com.agh.cinehub_backend.model.User;
import com.agh.cinehub_backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public void registerUser(RegisterRequest request) {

        User newUser = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(roleService.findByName("USER"))
                .build();

        if (userRepository.findByEmail(newUser.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with email " + newUser.getEmail() + " already exists.");
        }

        userRepository.save(newUser);
    }


    public User getUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with id: " + id + " does not exist."));
    }
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with email: " + email + " does not exist."));
    }

    public UserDto userDtoMapper(User user){
        return new UserDto(user.getUserId(), user.getRole(),
                user.getEmail(), user.getFirstname(), user.getLastname());
    }


}
