package com.webchat.server.service;

import com.webchat.server.entity.User;
import com.webchat.server.model.MessageDTO;

import java.util.List;

public interface MessageService {
    void saveNewMessage(String message, User sender, User Receiver);

    List<MessageDTO> getMessagesByUser(User receiver);

}
