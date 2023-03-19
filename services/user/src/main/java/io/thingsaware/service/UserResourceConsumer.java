package io.thingsaware.service;

import io.thingsaware.kafka.model.UserCreateMessage;
import io.thingsaware.service.model.error.UserCreationException;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class UserResourceConsumer {

    @Inject
    UserService userService;

    @Incoming("user-create")
    @Acknowledgment(Acknowledgment.Strategy.POST_PROCESSING)
    public void consumeUserCreateMessage(UserCreateMessage userCreateMessage) throws UserCreationException {
        userService.validateAndCreate(userCreateMessage);
    }

    @Incoming("password-status")
    public boolean consumePasswordStatusMessage(long userId) {
        return userService.isPasswordExpired(userId);
    }
}
