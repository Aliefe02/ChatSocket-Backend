package com.webchat.server.mapper;

import com.webchat.server.entity.Message;
import com.webchat.server.model.MessageDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(source = "sender.username", target = "sender")
    @Mapping(source = "receiver.username", target = "receiver")
    MessageDTO toDTO(Message message);

//    @Mapping(source = "sender", target = "sender.username")
//    @Mapping(source = "receiver", target = "receiver.username")
//    Message toEntity(MessageDTO dto, @Param("sender") User sender, @Param("receiver") User receiver);

    List<MessageDTO> toDTOList(List<Message> messages);
}
