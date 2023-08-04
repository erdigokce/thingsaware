package io.thingsaware.service;

import io.thingsaware.service.model.UserCreationRequest;
import io.thingsaware.service.model.dto.ApplicationUserDTO;
import io.thingsaware.service.model.error.EmailValidationException;
import io.thingsaware.service.model.error.PasswordValidationException;

public interface ApplicationUserService {

    void create(UserCreationRequest userValidationRequest) throws EmailValidationException, PasswordValidationException;

    ApplicationUserDTO fetchById(String userId);
}
