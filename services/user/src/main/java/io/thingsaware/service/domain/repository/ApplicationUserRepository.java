package io.thingsaware.service.domain.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.thingsaware.service.domain.ApplicationUser;

import javax.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class ApplicationUserRepository implements PanacheRepositoryBase<ApplicationUser, UUID> {
}
