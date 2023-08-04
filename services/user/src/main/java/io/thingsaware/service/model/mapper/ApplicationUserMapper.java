package io.thingsaware.service.model.mapper;

import io.thingsaware.service.domain.ApplicationUser;
import io.thingsaware.service.model.dto.ApplicationUserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface ApplicationUserMapper {

    @Mapping(source = "email.emailAddress", target = "emailAddress")
    ApplicationUserDTO entityToDto(ApplicationUser applicationUser);
}
