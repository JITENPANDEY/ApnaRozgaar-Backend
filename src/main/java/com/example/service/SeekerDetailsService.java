package com.example.service;

import com.example.entities.*;
import com.example.repository.*;
import com.example.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SeekerDetailsService {
    private SeekerDetailsRepositary sseekerDetailsRepositary;
    private SeekerSignupRepositary seekerSignupRepositary;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CompanyDetailRepository companyDetailRepository;
    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private JobPostRepository jobPostRepository;

    public SeekerDetailsService(SeekerDetailsRepositary sseekerDetailsRepositary, SeekerSignupRepositary seekerSignupRepositary) {
        this.sseekerDetailsRepositary = sseekerDetailsRepositary;
        this.seekerSignupRepositary = seekerSignupRepositary;
    }

    public String getuserEmail(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes())
                .getRequest();
        String authorizationHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;
        token = authorizationHeader.substring(7);
        email = jwtUtil.extractUsername(token);
        return email;
    }
    public SeekerDetails getdetails(){
        String email=getuserEmail();
        SeekerSignup seekerD=seekerSignupRepositary.findByEmail(email);
        SeekerDetails seekerDetails=sseekerDetailsRepositary.findBysid(seekerD);

        return seekerDetails;
    }


    public String updatedetails(SeekerDetails seekerDetails) {
        String email = getuserEmail();
        SeekerSignup seekerSignup=seekerSignupRepositary.findByEmail(email);
        SeekerDetails seekerDetails1=sseekerDetailsRepositary.findBysid(seekerSignup);

        seekerDetails1.setQualification(seekerDetails.getQualification());
        seekerDetails1.setLanguage(seekerDetails.getLanguage());
        seekerDetails1.setProfileimage(seekerDetails.getProfileimage());
        seekerDetails1.setDob(seekerDetails.getDob());
        seekerDetails1.setGender(seekerDetails.getGender());
        seekerDetails1.setPermanentaddress(seekerDetails.getPermanentaddress());
        seekerDetails1.setExperience(seekerDetails.getExperience());
        seekerSignup.setDob(seekerDetails.getDob());
        seekerSignup.setGender(seekerDetails.getGender());
        seekerSignup.setAdharCardImg(seekerDetails.getAadhar_img());
        seekerSignupRepositary.save(seekerSignup);
        sseekerDetailsRepositary.save(seekerDetails1);
        return  "Profile Update";
    }

    public CompanyDetail getcomapny(Integer id,String name) {
        ArrayList<Company> company=companyRepository.findByname(name);
        JobPost jobPost=jobPostRepository.findByJobId(id);
        CompanyDetail result=new CompanyDetail();
        if(company.size()==1)return  company.get(0).getCompanyDetail();
        else{
            for(Company c:company){
                if(c.getJobPostList().contains(jobPost)){
                    result=  c.getCompanyDetail();
                    break;
                }
            }
        }
        return  result;
    }

    public List<JobPost> companyjob(Integer cdi) {
        String email=getuserEmail();
        CompanyDetail cd=companyDetailRepository.findByid(cdi);
        Company company=companyRepository.findByCompanyDetail(cd);
        return  company.getJobPostList();
    }
}
