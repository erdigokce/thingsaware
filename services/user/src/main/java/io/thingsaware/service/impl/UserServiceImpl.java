package io.thingsaware.service.impl;

import io.thingsaware.service.EmailService;
import io.thingsaware.service.PasswordService;
import io.thingsaware.service.UserService;
import io.thingsaware.service.domain.Email;
import io.thingsaware.service.domain.Password;
import io.thingsaware.service.domain.User;
import io.thingsaware.service.domain.repository.UserRepository;
import io.thingsaware.service.model.UserCreationRequest;
import io.thingsaware.service.model.dto.UserDTO;
import io.thingsaware.service.model.error.EmailValidationException;
import io.thingsaware.service.model.error.PasswordValidationException;
import io.thingsaware.service.model.mapper.UserMapper;
import org.mapstruct.factory.Mappers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

@ApplicationScoped
public class UserServiceImpl implements UserService {

    @Inject
    EmailService emailService;

    @Inject
    PasswordService passwordService;

    @Inject
    UserRepository repository;

    UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Transactional
    @Override
    public void create(UserCreationRequest userCreationRequest) throws EmailValidationException, PasswordValidationException {
        Email newEmail = emailService.initiate(userCreationRequest.emailAddress());
        Password newPassword = passwordService.initiate(userCreationRequest.password());
        User newUser = new User();
        newUser.setEmail(newEmail);
        newUser.setPassword(newPassword);
        repository.persist(newUser);
    }
    @Override
    public UserDTO fetchUser(String userId) {
        User user = repository.findById(Long.valueOf(userId));
        return mapper.entityToDto(user);
    }
}
