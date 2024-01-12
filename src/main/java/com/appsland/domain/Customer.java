package com.appsland.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 *
 * Classe matérialisant un client de la banque
 */
@Entity
@Table(name = "customers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Integer id;
    @Column
    @NotNull
    private String firstName;
    @Column
    @NotNull
    private String lastName;
    @Column
    @NotNull
    private Integer age;
    @Column
    private String email;
    @Column
    @JsonIgnore
    private LocalDate createdAt;
}
