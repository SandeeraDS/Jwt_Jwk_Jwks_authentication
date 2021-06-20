package com.sandeera.resourceserver.Bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "CUSMM_CUSTOMER")
public class Customer {

    @Id
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String country;
}
