package com.sainnt.server.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Table(name="users_credentials")
public class UserCredentials {
    @Id
    private String username;
    @Lob
    @Column(name = "enc_password", columnDefinition = "BLOB")
    private byte[] encPassword;
    @OneToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;
}
