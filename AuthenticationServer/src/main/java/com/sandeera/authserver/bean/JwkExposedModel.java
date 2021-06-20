package com.sandeera.authserver.bean;

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
@Table(name = "JWK_EXPOSE_DATA")
public class JwkExposedModel {
    @Id
    String kId;
    String kty;
    String use;
    String n;
    String e;
    Long exp;
}
