package io.thingsaware.service.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public class VerifiableEntity extends PanacheEntity {
    @Column(name = "verify_datetime")
    private LocalDateTime verifyDatetime;
}