package com.example.mobileappws.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@Data
@Entity(name = "password_reset_tokens")
public class PasswordResetTokenEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = -837398070784389633L;
    @Id
    @GeneratedValue
    private long id;

    private String token;

    @OneToOne
    @JoinColumn(name = "users_id")
    private UserEntity userDetails;
}
