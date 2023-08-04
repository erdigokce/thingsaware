package io.thingsaware.service.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "password")
public class Password extends VerifiableEntity {

	@Id
	@GeneratedValue
	public UUID id;

	@Column(name = "password_value", nullable = false, unique = true)
	private String passwordValue;

	@Column(name = "expiry_datetime")
	private LocalDateTime expiryDatetime;

	public Password(String password) {
		super();
		this.passwordValue = password;
	}
}
