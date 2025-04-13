package com.webchat.server.service;

import com.webchat.server.entity.User;
import com.webchat.server.mapper.UserMapper;
import com.webchat.server.model.UserDTO;
import com.webchat.server.repository.UserRepository;
import com.webchat.server.security.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final EmailService emailService;
    private final JWTUtil jwtUtil;

    @Value("${BACKEND_URL}")
    private String backendUrl;

    public String generateTokenForUser(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("jwtCode", user.getJwtTokenCode());
        return jwtUtil.createTokenShort(claims, user.getId());
    }

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
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
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

    @Override
    public boolean sendAccountDeleteRequest(User user) {
        String username = user.getUsername();

        String token = generateTokenForUser(user);

        String deleteUrl = backendUrl + "/delete-account?token=" + token;

        String receiver = user.getEmail();
        String subject = "ChatSocket Account Delete Request";
        String htmlBody = "<html lang='en'>" +
                "<head><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<title>ChatSocket Account Delete Request</title><style>" +
                "body {background-color: black; color: white; font-family: Arial, sans-serif; margin: 0; padding: 0;}" +
                ".container {display: flex; flex-direction: column; align-items: center; justify-content: center; " +
                "text-align: center; min-height: 100vh; padding: 20px;}" +
                "h1 {color: white; font-size: 24px; margin-bottom: 10px;}" +
                "p {color: white; font-size: 16px; max-width: 600px;}" +
                ".button {display: inline-block; padding: 10px 20px; margin-top: 20px; background-color: rgb(18, 194, 86);" +
                "color: white; text-decoration: none; font-size: 16px; border-radius: 5px;}" +
                ".button:hover {background-color: red;}" +
                "</style></head>" +
                "<body><div class='container'>" +
                "<h1>ChatSocket Account Delete Request</h1>" +
                "<p>Hi %s,</p>" +
                "<p>Sorry to hear that you want to delete your account. To complete your request, please follow this link:</p>" +
                "<a href='%s' class='button'>Continue Deletion</a>" +
                "</div></body></html>";


        String formattedHtmlBody = String.format(htmlBody, username, deleteUrl);
        return emailService.sendEmail(receiver, subject, formattedHtmlBody, true);
    }

    @Override
    public boolean deleteAccount(String token) {
        UUID userId = jwtUtil.extractUserId(token);
        User user = userRepository.findById(userId).orElse(null);

        if (user != null && jwtUtil.validateTokenUser(token, user)){
            userRepository.delete(user);
            return true;
        }
        return false;
    }


}
