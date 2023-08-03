package io.thingsaware.service.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public class VerifiableEntity {

    @Column(name = "verify_datetime")
    private LocalDateTime verifyDatetime;

}
