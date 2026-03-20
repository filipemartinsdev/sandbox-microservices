package com.auth_service.security.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity @Table(name = "user_role")
@AllArgsConstructor @NoArgsConstructor @Data
public class Role {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "role_id" )
    private Integer id;

    @NotBlank
    private String name;
}
