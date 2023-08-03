package io.thingsaware.service.impl;

import io.thingsaware.service.PasswordService;
import io.thingsaware.service.domain.Password;
import io.thingsaware.service.domain.User;
import io.thingsaware.service.model.error.PasswordValidationException;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.Optional;

@ApplicationScoped
public class PasswordServiceImpl implements PasswordService {
    @Override
    public Password initiate(String password) {
        return new Password(password);
    }

    @Override
    public void validateForExpiry(User user) throws PasswordValidationException {
        if (LocalDateTime.now().isAfter(user.getPassword().getExpiryDatetime())) {
            throw new PasswordValidationException("Password is expired for user '%s'.".formatted(user.getEmail().getEmailAddress()));
        }
    }
}
