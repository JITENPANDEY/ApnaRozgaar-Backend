package com.example.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Entity
@Table
public class CompanyDetail{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer companyDetailId;
    @Column(columnDefinition="text")
    private String description;
    private String moto;
    @Column(columnDefinition="text")
    private String photo;
    private String address;
    private String state;
    private String city;
    private String pinCode;
    private String website;
    private String country;
    private String companyName;
    private String email;

//    for verification
    private String gstNo;
    private String adharNumber;
    @Column(columnDefinition="text")
    private String adharCardImg;
    private Boolean verified = false;


    public CompanyDetail(String description, String moto, String photo, String address, String state, String city, String pinCode, String website) {
        this.description = description;
        this.moto = moto;
        this.photo = photo;
        this.address = address;
        this.state = state;
        this.city = city;
        this.pinCode = pinCode;
        this.website = website;
    }
}
