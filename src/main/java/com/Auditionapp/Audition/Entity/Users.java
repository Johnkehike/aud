package com.Auditionapp.Audition.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;

    @Column(unique = true)
    private String name;

    @Column(unique = true)
    private String email;

    @Column(name = "phone_number")
    private String phone_number;

    private String address;

    @Column(name = "full_name")
    private String fullName;

    @Transient
    private String roleTest;
    private String password;

    @Enumerated(EnumType.STRING)
    private Roles role;


    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "created_by")
    private String createdBy;

}
