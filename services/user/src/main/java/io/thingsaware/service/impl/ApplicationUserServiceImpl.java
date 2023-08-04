package io.thingsaware.service.impl;

import io.thingsaware.service.ApplicationUserService;
import io.thingsaware.service.EmailService;
import io.thingsaware.service.PasswordService;
import io.thingsaware.service.domain.ApplicationUser;
import io.thingsaware.service.domain.Email;
import io.thingsaware.service.domain.Password;
import io.thingsaware.service.domain.repository.ApplicationUserRepository;
import io.thingsaware.service.model.UserCreationRequest;
import io.thingsaware.service.model.dto.ApplicationUserDTO;
import io.thingsaware.service.model.error.EmailValidationException;
import io.thingsaware.service.model.error.PasswordValidationException;
import io.thingsaware.service.model.mapper.ApplicationUserMapper;
import lombok.RequiredArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.UUID;

@ApplicationScoped
@RequiredArgsConstructor
public class ApplicationUserServiceImpl implements ApplicationUserService {

    private final EmailService emailService;
    private final PasswordService passwordService;
    private final ApplicationUserRepository repository;
    private final ApplicationUserMapper mapper;

    @Transactional
    @Override
    public void create(UserCreationRequest userCreationRequest) throws EmailValidationException, PasswordValidationException {
        Email newEmail = emailService.initiate(userCreationRequest.emailAddress());
        Password newPassword = passwordService.initiate(userCreationRequest.password());
        ApplicationUser newApplicationUser = new ApplicationUser();
        newApplicationUser.setEmail(newEmail);
        newApplicationUser.setPassword(newPassword);
        repository.persist(newApplicationUser);
    }
    @Override
    public ApplicationUserDTO fetchById(String userId) {
        ApplicationUser applicationUser = repository.findById(UUID.fromString(userId));
        return mapper.entityToDto(applicationUser);
    }
}
