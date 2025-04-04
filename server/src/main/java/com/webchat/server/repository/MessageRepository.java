package com.webchat.server.repository;

import com.webchat.server.entity.Message;
import com.webchat.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    List<Message> findByReceiver(User receiver);

    void deleteAllByReceiver(User receiver);

}
