package com.example.cloud_storage.entity;

import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name= "users")
@AllArgsConstructor
@NoArgsConstructor

public class User {
    @Id
    private Integer Id;
    private String username;
    private String email;
    private String password;


}
