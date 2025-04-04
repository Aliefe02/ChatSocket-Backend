package com.webchat.server.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
public class MessageDTO {

    private UUID id;

    private String sender;

    private String receiver;

    private String message;

    private LocalDateTime sentAt;
}
