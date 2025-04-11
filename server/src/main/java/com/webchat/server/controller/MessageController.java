package com.webchat.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webchat.server.entity.User;
import com.webchat.server.model.MessageDTO;
import com.webchat.server.security.SecurityUtils;
import com.webchat.server.service.MessageService;
import com.webchat.server.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/message/")
public class MessageController {

    private final MessageService messageService;
    private final UserService userService;

    @GetMapping("unreceived-messages")
    public Map<String, List<List<String>>> retrieveUnreceivedMessages(){


        User user = SecurityUtils.getAuthenticatedUser();

        ObjectMapper objectMapper = new ObjectMapper();

        List<MessageDTO> messages = messageService.getMessagesByUser(user);
        System.out.println(messages.size() + " messages retrieved for " + user.getUsername());

        return messages.stream()
                .collect(Collectors.groupingBy(
                        MessageDTO::getSender,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.stream()
                                        .sorted(Comparator.comparing(MessageDTO::getSentAt))
                                        .map(msg -> List.of(msg.getMessage(), msg.getSentAt().toString()))
                                        .collect(Collectors.toList())
                        )

                ));
    }
}
