package com.example.mobileappws.entity;

import com.example.mobileappws.shared.dto.UserDto;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UserEntity getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserEntity userDetails) {
        this.userDetails = userDetails;
    }
}
