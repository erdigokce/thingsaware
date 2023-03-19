package io.thingsaware.service.impl;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.thingsaware.kafka.model.UserCreateMessage;
import io.thingsaware.service.UserService;
import io.thingsaware.service.domain.Email;
import io.thingsaware.service.domain.Password;
import io.thingsaware.service.domain.User;
import io.thingsaware.service.model.error.UserCreationException;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

@ApplicationScoped
public class UserServiceImpl implements UserService {

    private static final Pattern EMAIL_REGEX = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    @Transactional
    @Override
    public void validateAndCreate(UserCreateMessage userCreateMessage) throws UserCreationException {
        String emailAddress = userCreateMessage.getEmailAddress();
        if (!EMAIL_REGEX.matcher(emailAddress).matches()) {
            throw new UserCreationException("Email '%s' has invalid format.".formatted(emailAddress));
        }
        long emailCount = PanacheEntityBase.count("emailAddress = ?1", emailAddress);
        if (emailCount > 0) {
            throw new UserCreationException("Email '%s' already exists.".formatted(emailAddress));
        }
        User newUser = new User();
        Email newEmail = new Email(emailAddress);
        Password newPassword = new Password(userCreateMessage.getPassword());
        newUser.setEmail(newEmail);
        newUser.setPassword(newPassword);
        newUser.persist();
    }

    @Transactional
    @Override
    public boolean isPasswordExpired(long userId) {
        User foundUser = PanacheEntityBase.findById(userId);
        return LocalDateTime.now().isAfter(foundUser.getPassword().getExpiryDatetime());
    }

}
