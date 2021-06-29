package com.example.controller;

import com.example.entities.CompanyDetail;
import com.example.entities.Jobstatus;
import com.example.response.MessageResponse;
import com.example.service.JobStatusService;
import com.example.util.JwtUtil;
import lombok.AllArgsConstructor;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@RestController
@CrossOrigin("https://bluecollar-dot-hu18-groupa-java.et.r.appspot.com")
//@CrossOrigin(origins = "http://localhost:4200")

public class JobStatusController {
    @Autowired
    private JobStatusService jobStatusService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/seeker/newJobStatus")
    public MessageResponse newJobStatus(@RequestBody Jobstatus jobstatus) throws Exception {
        return new MessageResponse(jobStatusService.newJobStatus(jobstatus));
    }

    @DeleteMapping("/seeker/remove/{savedjob}")
    public MessageResponse removesavedjob(@PathVariable(value = "savedjob") int jid) throws Exception {
        return new MessageResponse(jobStatusService.removesavedJob(jid));
    }

    @GetMapping("/seeker/savedjob")
    public ArrayList<Jobstatus> savedJob() throws JSONException {
        return  jobStatusService.savedjob();
    }

    @GetMapping("/seeker/appliedjob")
    public ArrayList<Jobstatus> aplliedJob() throws JSONException {
        return  jobStatusService.appliedjob();
    }

    @GetMapping("/company/userapplied/{id}")
    public List<Jobstatus> fetchuseraById(@PathVariable("id") Integer id) throws Exception {
        return jobStatusService.fetchusersId(id);
    }

    @PutMapping("/company/applicationstatus")
    public MessageResponse applicationstatus(@RequestBody Jobstatus jobstatus){
        return  new MessageResponse(jobStatusService.applicationstatus(jobstatus));

    }
}
