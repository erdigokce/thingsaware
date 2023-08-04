package io.thingsaware.service.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "email")
public class Email extends VerifiableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public UUID id;

    @Column(name = "email_address", unique = true)
    private String emailAddress;

    public Email(String emailAddress) {
        super();
        this.emailAddress = emailAddress;
    }
}
