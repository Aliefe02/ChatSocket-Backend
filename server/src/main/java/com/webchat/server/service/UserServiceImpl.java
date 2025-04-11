package com.webchat.server.service;

import com.webchat.server.entity.User;
import com.webchat.server.mapper.UserMapper;
import com.webchat.server.model.UserDTO;
import com.webchat.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public Optional<User> getUserEntityById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<UserDTO> getUserDTOById(UUID id) {
        return Optional.ofNullable(userMapper.userToUserDTO(userRepository.findById(id)
                .orElse(null)));
    }

    @Override
    public Optional<UserDTO> getUserByUsernameDTO(String username){
        return Optional.ofNullable(userMapper.userToUserDTO(userRepository.findByUsername(username).orElse(null)));
    }


    @Override
    public User getUserByUsername(String username){
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public UserDTO register(UserDTO user){
        Random random = new Random();
        user.setJwtTokenCode(100000 + random.nextInt(900000));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userMapper.userToUserDTO(userRepository.save(userMapper.userDTOToUser(user)));
    }

    @Override
    public boolean checkPassword(UserDTO user, UserDTO savedUser) {
        return passwordEncoder.matches(user.getPassword(), savedUser.getPassword());
    }

    @Override
    public Optional<UserDTO> updatePassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        Random random = new Random();
        int newCode;
        do {
            newCode = 100000 + random.nextInt(900000);
        } while (user.getJwtTokenCode() == newCode);
        user.setJwtTokenCode(newCode);
        return Optional.of(userMapper.userToUserDTO(userRepository.save(user)));
    }

    @Override
    public void updateFirstLastName(User user, UserDTO userDTO) {
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        userRepository.save(user);
    }

    @Override
    public UserDTO mapUserToUserDTO(User user) {
        return userMapper.userToUserDTO(user);
    }

    @Override
    public Optional<UserDTO> getUserById(UUID id) {
        return Optional.ofNullable(userMapper.userToUserDTO(userRepository.findById(id)
                .orElse(null)));
    }

    @Override
    public boolean doesUserExistsWithUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}
