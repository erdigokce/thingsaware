package io.thingsaware.service.impl;

import io.thingsaware.service.EmailService;
import io.thingsaware.service.domain.Email;
import io.thingsaware.service.domain.repository.EmailRepository;
import io.thingsaware.service.model.error.EmailValidationException;
import lombok.RequiredArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@ApplicationScoped
public class EmailServiceImpl implements EmailService {
	private static final Pattern EMAIL_REGEX = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

	private final EmailRepository emailRepository;

	@Override
	public Email initiate(String emailAddress) throws EmailValidationException {
		this.validateFormat(emailAddress);
		this.validateExistence(emailAddress);
		Email email = new Email(emailAddress);
		emailRepository.persist(email);
		return email;
	}

	private void validateFormat(String emailAddress) throws EmailValidationException {
		if (!EMAIL_REGEX.matcher(emailAddress).matches()) {
			throw new EmailValidationException("Email '%s' has invalid format.".formatted(emailAddress));
		}
	}

	private void validateExistence(String emailAddress) throws EmailValidationException {
		if (emailRepository.existsByEmailAddress(emailAddress)) {
			throw new EmailValidationException("Email '%s' already exists.".formatted(emailAddress));
		}
	}

}
