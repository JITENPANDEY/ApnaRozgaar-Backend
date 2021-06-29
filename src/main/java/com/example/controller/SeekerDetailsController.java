package com.example.controller;


import com.example.entities.CompanyDetail;
import com.example.entities.JobPost;
import com.example.entities.SeekerDetails;
import com.example.entities.SeekerSignup;
import com.example.response.MessageResponse;
import com.example.service.SeekerDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
//@CrossOrigin(origins = "http://localhost:4200")
@CrossOrigin(origins ="https://bluecollar-dot-hu18-groupa-java.et.r.appspot.com")
public class SeekerDetailsController {
    private SeekerDetailsService seekerDetailsService;

    public SeekerDetailsController(SeekerDetailsService seekerDetailsService) {
        this.seekerDetailsService = seekerDetailsService;
    }
    @GetMapping("/seekerdetails/geth")
    public String happy(){
        return "Happy";
    }
    @GetMapping("/seekerdetails/get")
    public SeekerDetails getdetails(){
        return  (seekerDetailsService.getdetails());
    }

    @PutMapping("/seekerdetails/update")
    public  MessageResponse updatedetails(@RequestBody SeekerDetails seekerDetails){
        return new MessageResponse(seekerDetailsService.updatedetails(seekerDetails));
    }

    @GetMapping("seeker/companydetails/{id}/{name}")
    public CompanyDetail getcompany(@PathVariable Integer id,@PathVariable String name){
        return  seekerDetailsService.getcomapny(id,name);
    }

    @GetMapping("seeker/companyjob/{cdi}")
    public List<JobPost> companyjobs(@PathVariable Integer cdi){
        return seekerDetailsService.companyjob(cdi);
    }
}
