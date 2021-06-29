package com.example.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Entity
@Table
public class JobPost {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer jobId;
    private String title;
    @Column(columnDefinition="text")
    private String description;
    private String experience;
    private Integer minSalary;
    private Integer maxSalary;
    private LocalDateTime createdate=LocalDateTime.now();
    private String lastDate;
    private String category;
    private String location;
    private String companyName;
    private Integer applicant=0;
    private  String closehirinig="No";
}
