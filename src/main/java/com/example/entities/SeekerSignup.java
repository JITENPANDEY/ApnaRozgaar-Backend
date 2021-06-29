package com.example.entities;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Seekersignup")
@Data
@Getter
@Setter
public class SeekerSignup {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "seeker_id",nullable = false)
    private String id;
    @Column(name="name")
    private String name;
    @Column(name="email")
    private  String email;
    @Column(name="phonenumber")
    private  String phonenumber;
    @Column(name="password")
    private  String  password;
    @Column(name = "active")
    private  boolean active=false;
    @Column(name = "adharNumber",unique = true)
    private  String  adharNumber;
    @Column(columnDefinition="text")
    private String adharCardImg;
    private Boolean verified = false;
    private String dob;
    private  String gender;

    public  SeekerSignup(){}

    public SeekerSignup(String email) {
        this.email = email;
    }

    public SeekerSignup(String name, String email, String phonenumber, String password) {
        this.name = name;
        this.email = email;
        this.phonenumber = phonenumber;
        this.password = password;
    }

    public String getId() {
        return id;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return active;
    }
}
