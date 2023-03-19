package io.thingsaware.service;

import io.thingsaware.service.model.error.UserCreationException;
import io.thingsaware.kafka.model.UserCreateMessage;

public interface UserService {

    void validateAndCreate(UserCreateMessage userCreateMessage) throws UserCreationException;

    boolean isPasswordExpired(long userId);
}
