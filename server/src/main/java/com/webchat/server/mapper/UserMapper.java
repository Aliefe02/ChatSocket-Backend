package com.webchat.server.mapper;

import com.webchat.server.entity.User;
import com.webchat.server.model.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UserMapper {
    @Mapping(source = "email", target = "email") // Explicit mapping if the names are different
    UserDTO userToUserDTO(User user);

    @Mapping(source = "email", target = "email") // Explicit mapping
    User userDTOToUser(UserDTO userDTO);
}
