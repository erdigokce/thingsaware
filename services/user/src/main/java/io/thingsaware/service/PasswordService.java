package io.thingsaware.service;

import io.thingsaware.service.domain.Password;
import io.thingsaware.service.domain.User;
import io.thingsaware.service.model.error.PasswordValidationException;

import java.util.Optional;

public interface PasswordService {
	Password initiate(String password) throws PasswordValidationException;

	void validateForExpiry(User user) throws PasswordValidationException;
}
