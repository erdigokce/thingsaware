package io.thingsaware.service;

import io.thingsaware.service.model.UserCreationRequest;
import io.thingsaware.service.model.dto.UserDTO;
import io.thingsaware.service.model.error.EmailValidationException;
import io.thingsaware.service.model.error.PasswordValidationException;

public interface UserService {

    void create(UserCreationRequest userValidationRequest) throws EmailValidationException, PasswordValidationException;

    UserDTO fetchUser(String userId);
}
