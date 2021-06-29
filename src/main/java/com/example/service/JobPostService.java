package com.example.service;

import com.example.entities.JobPost;
import com.example.entities.Jobstatus;
import com.example.messaging.SmsRequest;
import com.example.repository.JobPostRepository;
import com.example.repository.JobStatusRepositary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import  com.example.messaging.MsgService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class JobPostService {
    @Autowired
    private JobPostRepository jobPostRepository;
    @Autowired
    private MsgService msgService;
    @Autowired
    private JobStatusRepositary jobStatusRepositary;

    public List<JobPost> filterrequest(String cate, String loc) {
        List<JobPost> resultjobs=new ArrayList<>();
        List<JobPost> jobPostList1=new ArrayList();
        if(!cate.equals("all") && !loc.equals("all")){
            resultjobs=jobPostRepository.findByCategoryAndLocation(cate,loc);
        }
        else if(cate.equals("all") && !loc.equals("all") ){
            resultjobs=jobPostRepository.findByLocation(loc);
        }
        else{
            resultjobs=jobPostRepository.findByCategory(cate);
        }
        for (JobPost j: resultjobs) {
            if(j.getClosehirinig().equals("No"))
                jobPostList1.add(j);
        }
        return  jobPostList1;
    }

    @Async
    public CompletableFuture<String> closehirnig(Integer jobId) {
        JobPost jobPost=jobPostRepository.findByJobId(jobId);

        jobPost.setClosehirinig("Yes");
        List<Jobstatus> jobstatusList = jobStatusRepositary.findAllByjobid(jobPost, "Applied");
        //System.out.println(jobstatusList);
        if (jobstatusList.isEmpty() == false) {
            for (Jobstatus js : jobstatusList) {
                if (js.getApplicationstatus().equals("hired") == false) {
                    js.setApplicationstatus("reject");
                    jobStatusRepositary.save(js);
                }
                if(js.getApplicationstatus().equals("InProcess")==false){
                    String message="Hi You are "+js.getApplicationstatus() + " for " +js.getJobPost().getCategory()
                            +" at " + js.getJobPost().getCompanyName();
                    try{
                        sendMessage(js.getSeekerSignup().getPhonenumber(),message);
                    }catch(Exception e){}
                    
                }
            }
        }
        jobPostRepository.save(jobPost);
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        completableFuture.complete("Hiring Closed");
        return completableFuture;
    }


    private void sendMessage(String phone,String message) {
        SmsRequest smsRequest = new SmsRequest("+91"+phone,message);
        msgService.sendSms(smsRequest);
    }


}
