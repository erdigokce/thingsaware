package io.thingsaware.service;

import io.thingsaware.service.domain.Password;
import io.thingsaware.service.domain.ApplicationUser;
import io.thingsaware.service.model.error.PasswordValidationException;

public interface PasswordService {
	Password initiate(String password) throws PasswordValidationException;

	void validateForExpiry(ApplicationUser applicationUser) throws PasswordValidationException;
}
