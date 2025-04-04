package com.webchat.server.service;

import com.webchat.server.entity.Message;
import com.webchat.server.entity.User;
import com.webchat.server.mapper.MessageMapper;
import com.webchat.server.model.MessageDTO;
import com.webchat.server.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    @Override
    public void saveNewMessage(String message, User sender, User receiver) {
        Message messageEntity = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .message(message)
                .build();

        messageRepository.save(messageEntity);
    }

    @Override
    public List<MessageDTO> getMessagesByUser(User receiver) {
        List<Message> messages = messageRepository.findByReceiver(receiver);
        List<MessageDTO> messagesDTO = messageMapper.toDTOList(messages);

        messageRepository.deleteAll(messages);
        return messagesDTO;

    }
}
