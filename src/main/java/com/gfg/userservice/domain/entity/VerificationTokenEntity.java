package com.gfg.userservice.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gfg.userservice.audit.BaseEntity;
import com.gfg.userservice.constant.AppConstant;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "verification_tokens")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerificationTokenEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private Long id;

    @Column(name = "verif_token")
    private String verifToken;

    @JsonFormat(pattern = AppConstant.LOCAL_DATE_FORMAT, shape = JsonFormat.Shape.STRING)
    @DateTimeFormat(pattern = AppConstant.LOCAL_DATE_FORMAT)
    @Column(name = "expire_date")
    private Date expireDate;

    private Long credentialId;
}
