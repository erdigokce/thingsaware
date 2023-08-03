package io.thingsaware.service.controller;

import io.thingsaware.service.UserService;
import io.thingsaware.service.model.UserCreationRequest;
import io.thingsaware.service.model.dto.UserDTO;
import io.thingsaware.service.model.error.EmailValidationException;
import io.thingsaware.service.model.error.PasswordValidationException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/users")
public class UserController {

	@Inject
	UserService userService;

	@POST
	public void createUser(UserCreationRequest creationRequest) throws EmailValidationException, PasswordValidationException {
		userService.create(creationRequest);
	}

	@GET
	@Path("{id}")
	public UserDTO retrieveUser(@PathParam("id") String userId) {
		return userService.fetchUser(userId);
	}

}
