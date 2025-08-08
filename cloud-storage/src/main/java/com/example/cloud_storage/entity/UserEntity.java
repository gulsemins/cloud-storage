package com.example.cloud_storage.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

   // @Id
  //  private String id; // artık Long değil, String

    // private String username;
    // private String email;
    // private String password;

    // @PrePersist
    //  public void assignId() {
    //    if (this.id == null) {
    //      this.id = UUID.randomUUID().toString(); // örnek: 8a0fa0d8-f882-4d5e-8f1d-5a18182f2d3b
    //  }
    //  }
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @Column(unique = true)
    private String username;

    private String email;
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UploadedFileEntity> uploadedFiles = new ArrayList<>();

}

