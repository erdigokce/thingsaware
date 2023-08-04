package io.thingsaware.service.controller;

import io.thingsaware.service.ApplicationUserService;
import io.thingsaware.service.model.UserCreationRequest;
import io.thingsaware.service.model.dto.ApplicationUserDTO;
import io.thingsaware.service.model.error.EmailValidationException;
import io.thingsaware.service.model.error.PasswordValidationException;
import io.thingsaware.service.model.validation.UUID;
import lombok.RequiredArgsConstructor;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/users")
@RequiredArgsConstructor
public class UserController {

	private final ApplicationUserService applicationUserService;

	@POST
	public void createUser(UserCreationRequest creationRequest) throws EmailValidationException, PasswordValidationException {
		applicationUserService.create(creationRequest);
	}

	@GET
	@Path("{id}")
	public ApplicationUserDTO retrieveUser(@PathParam("id") @UUID String userId) {
		return applicationUserService.fetchById(userId);
	}

}
