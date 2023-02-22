package io.thingsaware.service;

import io.thingsaware.dashboard.model.UserCreateMessage;
import io.thingsaware.service.model.error.UserCreationException;

public interface UserService {

    void validateAndCreate(UserCreateMessage userCreateMessage) throws UserCreationException;

    boolean isPasswordExpired(long userId);
}
