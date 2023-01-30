package io.thingsaware.service.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User extends PanacheEntity {
    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "email_id", nullable = false, unique = true)
    private Email email;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "password_id", nullable = false)
    private Password password;

}