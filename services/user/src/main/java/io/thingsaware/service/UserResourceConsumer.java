package io.thingsaware.service;

import io.smallrye.mutiny.Multi;
import io.thingsaware.dashboard.model.UserCreateMessage;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.resteasy.reactive.RestStreamElementType;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@ApplicationScoped
public class UserResourceConsumer {

    @Inject
    UserService userService;
/*
    @Incoming("user-create")
    @Transactional
    @Acknowledgment(Acknowledgment.Strategy.POST_PROCESSING)
    public void consumeUserCreateMessage(UserCreateMessage userCreateMessage) throws UserCreationException {
        userService.validateAndCreate(userCreateMessage);
    }
*/

    @Channel("user-create-messages")
    Multi<UserCreateMessage> users;

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @RestStreamElementType(MediaType.TEXT_PLAIN)
    public Multi<String> stream() {
        return users.map(movie -> String.format("'%s' from %s", movie.getEmailAddress(), movie.getPassword()));
    }

    @Incoming("password-status")
    @Transactional
    public boolean consumePasswordStatusMessage(long userId) {
        return userService.isPasswordExpired(userId);
    }
}
