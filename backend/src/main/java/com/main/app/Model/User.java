package com.main.app.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name='user')
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = 'user_id')
    private Integer id;

    @NotNull
    @Column(name = 'user_name')
    private String name;

    @NotNull
    @Email
    @Column(name = 'user_email')
    private String email;


    private String password;


    private String role;

}
