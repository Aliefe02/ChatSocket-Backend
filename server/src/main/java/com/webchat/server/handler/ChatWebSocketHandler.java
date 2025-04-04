package com.webchat.server.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.webchat.server.entity.User;
import com.webchat.server.model.MessageDTO;
import com.webchat.server.service.MessageService;
import com.webchat.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final MessageService messageService;
    private final UserService userService;


    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String username = (String) session.getAttributes().get("username");
        sessions.put(username, session);
        System.out.println(username + " connected");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage payload) throws IOException {
        String sender = (String) session.getAttributes().get("username");

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(payload.getPayload());

        String target = jsonNode.get("target").asText();


        switch (target){
            case "sendMessage":
                String receiver = jsonNode.get("recipient").asText();
                String message = jsonNode.get("message").asText();

                System.out.println("Received from: " + sender + " to " + receiver + " message: " + message);

                sendMessage(message, sender, receiver);
                break;

            case "retrieveMessages":
                retrieveMessages(sender);
                break;

            default:
                System.out.println("Unknown target: " + target);

        }

    }

    private void sendMessage(String message, String sender, String receiver) throws IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        WebSocketSession receiverSession = sessions.get(receiver);

        if (receiverSession != null && receiverSession.isOpen()) {
            ObjectNode responseJson = objectMapper.createObjectNode();
            responseJson.put("sender", sender);
            responseJson.put("message", message);
            receiverSession.sendMessage(new TextMessage(responseJson.toString()));

        } else { // Receiver is not connected
            System.out.println("Receiver is not connected " + receiver);
            if (!sendNotification(message, sender, receiver)){
                storeMessage(message, sender, receiver);
            }

        }
    }

    private boolean sendNotification(String message, String sender, String receiver){
        return false;
    }

    private void storeMessage(String message, String sender, String receiver){
        User senderEntity = userService.getUserByUsername(sender);
        User receiverEntity = userService.getUserByUsername(receiver);
        if (senderEntity != null && receiverEntity != null){
            messageService.saveNewMessage(message, senderEntity, receiverEntity);
        }
        // handle null case
    }

    private void retrieveMessages(String username) throws IOException{
        ObjectMapper objectMapper = new ObjectMapper();

        WebSocketSession userSession = sessions.get(username);
        if (userSession != null && userSession.isOpen()) {
            User user = userService.getUserByUsername(username);
            List<MessageDTO> messages = messageService.getMessagesByUser(user);

            Map<String, List<List<String>>> groupedMessages = messages.stream()
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

            String jsonMessages = objectMapper.writeValueAsString(groupedMessages);

            userSession.sendMessage(new TextMessage(jsonMessages));

        }   // Handle case
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String username = (String) session.getAttributes().get("username");
        sessions.values().remove(session);
        System.out.println(username + " disconnected");
    }
}
