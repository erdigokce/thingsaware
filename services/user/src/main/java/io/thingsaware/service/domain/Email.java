package io.thingsaware.service.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "email")
public class Email extends VerifiableEntity {
    @Column(name = "email_address", unique = true)
    private String emailAddress;

    public Email(String emailAddress) {
        super();
        this.emailAddress = emailAddress;
    }
}