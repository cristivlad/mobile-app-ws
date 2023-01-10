package com.example.mobileappws.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "address")
public class AddressEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 7190030542675529913L;

    @Id
    @GeneratedValue
    private long id;

    @Column(name = "address_id", length = 30, nullable = false)
    private String addressId;

    @Column(name = "city", length = 20, nullable = false)
    private String city;

    @Column(name = "country", length = 20, nullable = false)
    private String country;

    @Column(name = "street_name", length = 100, nullable = false)
    private String streetName;

    @Column(name = "postal_code", length = 7, nullable = false)
    private String postalCode;

    @Column(name = "type", length = 10, nullable = false)
    private String type;

    @ManyToOne
    @JoinColumn(name = "users_id")
    private UserEntity userDetails;
}
