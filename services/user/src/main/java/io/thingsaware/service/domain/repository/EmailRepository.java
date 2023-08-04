package io.thingsaware.service.domain.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.thingsaware.service.domain.Email;

import javax.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class EmailRepository implements PanacheRepositoryBase<Email, UUID> {

	public boolean existsByEmailAddress(String emailAddress) {
		return this.count("emailAddress", emailAddress) > 0;
	}
}
