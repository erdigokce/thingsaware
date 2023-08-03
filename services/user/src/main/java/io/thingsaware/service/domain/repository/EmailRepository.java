package io.thingsaware.service.domain.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.thingsaware.service.domain.Email;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EmailRepository implements PanacheRepository<Email> {

	public boolean existsByEmailAddress(String emailAddress) {
		return this.count("emailAddress = ?1", emailAddress) > 0;
	}
}
