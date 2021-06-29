package com.example.service;

import com.example.entities.*;
import com.example.repository.JobPostRepository;
import com.example.repository.JobStatusRepositary;
import com.example.repository.SeekerDetailsRepositary;
import com.example.repository.SeekerSignupRepositary;
import com.example.util.JwtUtil;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
public class JobStatusService {

    @Autowired
    private JobStatusRepositary jobStatusRepositary;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private SeekerSignupRepositary seekerSignupRepositary;

    @Autowired
    private JobPostRepository jobPostRepository;

    @Autowired
    private SeekerDetailsRepositary seekerDetailsRepositary;

    public String getseekerEmail(){
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

    public String newJobStatus(Jobstatus jobstatus) {
        String email= getseekerEmail();
        ArrayList<SeekerSignup> seekerD= (ArrayList<SeekerSignup>) seekerSignupRepositary.findAllByEmail(email);
        ArrayList<Integer> temp=new ArrayList();
        temp.add(jobstatus.getJob_id());
        ArrayList<JobPost> jobPost1= (ArrayList<JobPost>) jobPostRepository.findAllById(temp);
        Jobstatus newjob = new Jobstatus(seekerD.get(0),jobPost1.get(0),jobstatus.getStatus());
        System.out.println(jobstatus.getStatus());
        if(jobstatus.getStatus().equals("Applied")) {
            newjob.setApplicationstatus("InProcess");

        }
        SeekerDetails seekerDetails=seekerDetailsRepositary.findBysid(seekerD.get(0));
        newjob.setGender(seekerDetails.getGender());
        jobStatusRepositary.save(newjob);
        return  "Succefully added";
    }

    public String removesavedJob(Integer jid) throws Exception {

        //finding email and the respective details
        String email=getseekerEmail();
        SeekerSignup seekerSignup=seekerSignupRepositary.findByEmail(email);
        JobPost jobPost =jobPostRepository.findByJobId(jid);
        //finding joub id and description
        Jobstatus jobstatus1=jobStatusRepositary.findallsavedjid(seekerSignup,"Saved",jobPost);
        jobstatus1.setJobPost(null);
        jobstatus1.setSeekerSignup(null);
        jobStatusRepositary.deleteById(jobstatus1.getId());
        return  "deleted from saved";

    }

    public ArrayList<Jobstatus> savedjob() throws JSONException {
        String email=getseekerEmail();
        SeekerSignup seekerSignup=seekerSignupRepositary.findByEmail(email);
        ArrayList<Jobstatus> jobsaved=jobStatusRepositary.findallsaved(seekerSignup,"Saved");
        ArrayList<Jobstatus> jobsaved1=new ArrayList();
        for (Jobstatus j:jobsaved) {
            if(j.getJobPost().getClosehirinig().equals("No"))jobsaved1.add(j);

        }
        return  jobsaved1;

    }

    public ArrayList<Jobstatus> appliedjob() throws JSONException {
        String email=getseekerEmail();
        SeekerSignup seekerSignup=seekerSignupRepositary.findByEmail(email);
        ArrayList<Jobstatus> jobsaved=jobStatusRepositary.findallsaved(seekerSignup,"Applied");

        return  jobsaved;

    }

    public List<Jobstatus> fetchusersId(Integer id) {
        JobPost job=jobPostRepository.findByJobId(id);
        List<Jobstatus> appliedappicant= (List<Jobstatus>) jobStatusRepositary.findAllByjobid(job,"Applied");
        //System.out.println(appliedappicant);
        return  appliedappicant;
    }

    public String applicationstatus(Jobstatus jobstatus) {
        Jobstatus jobstatus1=jobStatusRepositary.findByid(jobstatus.getId());
        jobstatus1.setApplicationstatus(jobstatus.getApplicationstatus());

        jobStatusRepositary.save(jobstatus1);
        return  "Status Changed";
    }

    //weekly
//    @Scheduled(cron = "0 0 0 * * 0")
//    public void RejectDBJob()
//    {
//        List<JobPost> jobPostList=jobPostRepository.findByClosehirinig("Yes");
//        for (JobPost j:jobPostList) {
//            List<Jobstatus> jobstatusList=jobStatusRepositary.findAllByjobid(j,"Applied");
//            for (Jobstatus js: jobstatusList) {
//                if(js.getApplicationstatus().equals("Hired")==false){
//                    js.setApplicationstatus("Rejected");
//                    jobStatusRepositary.save(js);
//                }
//
//            }
//
//        }
//    }


}
