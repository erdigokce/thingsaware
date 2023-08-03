package io.thingsaware.service;

import io.thingsaware.service.domain.Email;
import io.thingsaware.service.model.error.EmailValidationException;

public interface EmailService {
	Email initiate(String emailAddress) throws EmailValidationException;
}
