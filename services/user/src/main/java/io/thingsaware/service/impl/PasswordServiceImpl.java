package io.thingsaware.service.impl;

import io.thingsaware.service.PasswordService;
import io.thingsaware.service.domain.Password;
import io.thingsaware.service.domain.ApplicationUser;
import io.thingsaware.service.model.error.PasswordValidationException;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;

@ApplicationScoped
public class PasswordServiceImpl implements PasswordService {
    @Override
    public Password initiate(String password) {
        return new Password(password);
    }

    @Override
    public void validateForExpiry(ApplicationUser applicationUser) throws PasswordValidationException {
        if (LocalDateTime.now().isAfter(applicationUser.getPassword().getExpiryDatetime())) {
            throw new PasswordValidationException("Password is expired for user '%s'.".formatted(applicationUser.getEmail().getEmailAddress()));
        }
    }
}
