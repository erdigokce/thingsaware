package io.thingsaware.service.domain.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.thingsaware.service.domain.User;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
}
