package io.thingsaware.dashboard.producer;

import io.thingsaware.dashboard.model.UserCreateMessage;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Slf4j
@Path("/user")
public class UserProducer {

    @Channel("user-create")
    Emitter<UserCreateMessage> emitter;

    @POST
    public Response enqueueMovie(UserCreateMessage userCreateMessage) {
        log.debug("Sending create user {} to Kafka", userCreateMessage.getEmailAddress());
        emitter.send(userCreateMessage);
        return Response.accepted().build();
    }
}
