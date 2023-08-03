package io.thingsaware.service.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User {
	@Id
	@GeneratedValue
	public Long id;

	@OneToOne(cascade = CascadeType.ALL, optional = false)
	@JoinColumn(name = "email_id", nullable = false, unique = true)
	private Email email;

	@OneToOne(cascade = CascadeType.ALL, optional = false)
	@JoinColumn(name = "password_id", nullable = false)
	private Password password;

}
