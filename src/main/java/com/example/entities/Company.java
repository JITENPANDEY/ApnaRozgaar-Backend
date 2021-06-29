package com.example.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Entity
@Table
public class Company{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer companyId;
    private String companyName;
    private String email;
    private String password;
    private String country;
    private Boolean enabled=Boolean.FALSE;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="companyId",referencedColumnName = "companyId")
    private List<JobPost> jobPostList = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="companyDetailId",referencedColumnName = "companyDetailId")
    private CompanyDetail companyDetail;

    public Company(String companyName, String email, String password, String country) {
        this.companyName = companyName;
        this.email = email;
        this.password = password;
        this.country = country;
        this.enabled=Boolean.FALSE;
    }
}
