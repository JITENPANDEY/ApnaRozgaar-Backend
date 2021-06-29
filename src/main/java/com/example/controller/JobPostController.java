package com.example.controller;


import com.example.entities.JobPost;
import com.example.response.MessageResponse;
import com.example.service.JobPostService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin(origins ="https://bluecollar-dot-hu18-groupa-java.et.r.appspot.com")
//@CrossOrigin(origins = "http://localhost:4200")
public class JobPostController {

    @Autowired
    private JobPostService jobPostService;

    @GetMapping("/user/filter/{category}/{location}")
    public List<JobPost> filterrequest(@PathVariable("category") String cate, @PathVariable("location") String loc){
        return jobPostService.filterrequest(cate,loc);
    }

    @PutMapping("company/closeJobId/{jobId}")
    public MessageResponse closehirnig(@PathVariable Integer jobId){
        jobPostService.closehirnig(jobId);
        return  new MessageResponse("Hiring Closed");
    }
}


