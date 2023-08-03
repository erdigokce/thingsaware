package io.thingsaware.service.model.mapper;

import io.thingsaware.service.domain.User;
import io.thingsaware.service.model.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UserMapper {

    @Mapping(source = "email.emailAddress", target = "emailAddress")
    UserDTO entityToDto(User user);
}
