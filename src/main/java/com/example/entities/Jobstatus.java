package com.example.entities;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "jobstatus")
@Data
@Getter
@Setter
public class Jobstatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="seeker_id" , referencedColumnName ="seeker_id")
    private  SeekerSignup seekerSignup;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="jobid" , referencedColumnName ="jobid")
    private  JobPost jobPost;

    private  String status;
    @Transient
    private  Integer job_id;
    private  String applicationstatus;
    private String gender;
    public Jobstatus() {
    }


    public Jobstatus(SeekerSignup seekerSignup, JobPost jobPost, String status) {
        this.seekerSignup = seekerSignup;
        this.jobPost = jobPost;
        this.status = status;
//        if(status=="Applied")this.applicationstatus="InProcess";
    }

    public void setApplicationstatus(String applicationstatus) {
        this.applicationstatus = applicationstatus;
    }
}
