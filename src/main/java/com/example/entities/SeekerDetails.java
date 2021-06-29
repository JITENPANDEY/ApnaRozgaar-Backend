package com.example.entities;
import lombok.Data;
import org.springframework.scheduling.annotation.Scheduled;
import lombok.Getter;
import lombok.Setter;


import javax.persistence.*;
import java.util.ArrayList;


@Entity
@Table(name = "seekerdetails")
@Data
@Getter
@Setter

public class SeekerDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "seeker_id")
    private  SeekerSignup seekerSignup;
    @Column(name="name")
    private String name;
    @Column(name="phonenumber")
    private  String phonenumber;
    @Column(columnDefinition = "Text")
    private  String profileimage;
    private  String qualification;
    private  String language;
    private  String dob;
    private String gender;
    @Column(columnDefinition = "Text")
    private String permanentaddress;
    private  Integer experience;
    @Transient
    private String aadhar_img;



    public SeekerDetails() {

    }

    public SeekerDetails(String profileimage, String qualification, ArrayList<String> skills, String language,
                         String dob, String gender, String permanentaddress,Integer experience) {
        this.profileimage = profileimage;
        this.qualification = qualification;
        this.language = language;
        this.dob = dob;
        this.gender = gender;
        this.permanentaddress = permanentaddress;
        this.experience=experience;
    }
}
