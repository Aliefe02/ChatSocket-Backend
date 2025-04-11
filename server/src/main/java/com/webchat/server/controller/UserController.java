package com.webchat.server.controller;

import com.webchat.server.entity.User;
import com.webchat.server.exception.NotFoundException;
import com.webchat.server.exception.UnauthorizedException;
import com.webchat.server.model.UserDTO;
import com.webchat.server.security.JWTUtil;
import com.webchat.server.security.SecurityUtils;
import com.webchat.server.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/user/")
public class UserController {

    private final UserService userService;
    private final JWTUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public static UserDTO getUserDTOFromTokenString(UserService userService, String token) {
        UUID userId = SecurityUtils.getUserIdFromToken(token, new JWTUtil());

        if (userId == null) {
            throw new UnauthorizedException("Invalid or expired token");
        }

        return userService.getUserById(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

    }

    public static UserDTO getUserDTOFromToken(UserService userService){
        return userService.getUserById(SecurityUtils.getAuthenticatedUserId()).orElseThrow(NotFoundException::new);
    }

    @PostMapping("login")
    public String login(@RequestBody UserDTO userDTO){
        UserDTO user = userService.getUserByUsernameDTO(userDTO.getUsername()).orElseThrow(() -> new BadCredentialsException("User not found"));

        if (userService.checkPassword(userDTO, user)){
            return jwtUtil.generateToken(user.getId(), user.getJwtTokenCode());
        }
        throw new BadCredentialsException("Password incorrect");
    }

    @PostMapping("register")
    public ResponseEntity<String> register(@Validated @RequestBody UserDTO userDTO){
        UserDTO savedUser = userService.register(userDTO);

        String token = jwtUtil.generateToken(savedUser.getId(), savedUser.getJwtTokenCode());

        return new ResponseEntity<>(token, HttpStatus.CREATED);
    }

    @PutMapping("update-password")
    public String updatePassword(@RequestBody UserDTO userDTO){
        User user = SecurityUtils.getAuthenticatedUser();

        userService.updatePassword(user, userDTO.getPassword()).orElseThrow(NotFoundException::new);

        return jwtUtil.generateToken(user.getId(), user.getJwtTokenCode());
        }

    @PutMapping("update-name")
    public ResponseEntity<Void> updateFirstLastName(@RequestBody UserDTO userDTO){
        User user =  SecurityUtils.getAuthenticatedUser();
        userService.updateFirstLastName(user, userDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("details")
    public UserDTO getUserDetails(){
        User user = SecurityUtils.getAuthenticatedUser();
        UserDTO userDTO = userService.mapUserToUserDTO(user);
        userDTO.setPassword(null);
        userDTO.setJwtTokenCode(null);
        return userDTO;
    }

    @GetMapping("exists")
    public ResponseEntity<Map<String, String>> doesUserExists(@RequestParam("username") String username){
        UserDTO user = userService.getUserByUsernameDTO(username).orElseThrow(NotFoundException::new);
        if (user != null){
            Map<String, String> response = new HashMap<>();
            response.put("username", user.getUsername());
            response.put("publicKey", "publicKeyTemp");

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
