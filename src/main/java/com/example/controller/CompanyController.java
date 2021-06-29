package com.example.controller;

import com.example.entities.Company;
import com.example.entities.CompanyDetail;
import com.example.entities.JobPost;

import com.example.messaging.MsgService;
import com.example.messaging.SmsRequest;
import com.example.repository.CompanyRepository;
import com.example.request.CompanyLogin;
import com.example.request.EmailRequest;
import com.example.request.PasswordRequest;
import com.example.response.MessageResponse;
import com.example.response.TokenResponse;
import com.example.service.CompanyService;
import com.example.util.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
@RestController
//@CrossOrigin(origins = "http://localhost:4200")
@CrossOrigin(origins ="https://bluecollar-dot-hu18-groupa-java.et.r.appspot.com")
public class CompanyController {

    private CompanyService companyService;

    private JwtUtil jwtUtil;
    private AuthenticationManager authenticationManager;

    @Autowired
    private CompanyRepository companyRepository;

    @GetMapping("/_ah/start") public String start(){
        return "Everything working";
    }

    //register
    @PostMapping("/company/register")
    public MessageResponse registerCompany(@RequestBody Company company) throws MessagingException, UnsupportedEncodingException {
        Company companyExist = companyRepository.findByEmail(company.getEmail());
        if(companyExist!=null){

            throw new IllegalStateException (("Email Already Registered!"));
        }
        companyService.register(company);
        return new MessageResponse("Check Your Email to Register");
    }

    @GetMapping(value = "/company/register/confirm",produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String confirmRegisterCompany(@RequestParam("token") String token){
        String result= (companyService.confirmCompanyRegister(token));
        String redirect="<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Title</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<a style=\"cursor:pointer;color:Blue\" href=\"https://bluecollar-dot-hu18-groupa-java.et.r.appspot.com/login\">Click Here to Login</a>\n" +
                "</body>\n" +
                "</html>";
        return redirect;
    }

    //login
    @PostMapping("/company/login")
    public TokenResponse loginCompany(@RequestBody CompanyLogin companyLogin) throws Exception {
//        SmsRequest smsRequest = new SmsRequest("+918340457015","Hi, Jiten");
//        service.sendSms(smsRequest);
        return companyService.login(companyLogin);
    }


    // new password
    @PostMapping("/company/resetPassword")
    public MessageResponse resetPassword(@RequestBody EmailRequest emailRequest) throws MessagingException, UnsupportedEncodingException {
        return new MessageResponse( companyService.resetPassword(emailRequest.getEmail()));
    }
    @PostMapping("/company/resetPassword/confirm")
    public MessageResponse confirmResetPassword(@RequestParam("token") String token,@RequestBody PasswordRequest passwordRequest){
        return new MessageResponse(companyService.confirmResetPassword( token, passwordRequest.getPassword()));
    }

    //company details

    @PostMapping("/company/addDetails")
    public MessageResponse updateCompanyDetails(@RequestBody CompanyDetail companyDetail) throws Exception {
        return new MessageResponse(companyService.updateCompanyDetails(companyDetail));
    }

    @GetMapping("/company/companyDetails")
    public CompanyDetail fetchCompanyDetails() throws Exception {
        return companyService.fetchCompanyDetails();
    }


    @GetMapping("/company/companyDetailsbyId/{id}")
    public CompanyDetail fetchCompanyDetailsById(@PathVariable("id") Integer id) throws Exception {
        return companyService.fetchCompanyDetailsById(id);
    }

    // job Post
    @PostMapping("/company/newJobPost")
    public MessageResponse newJobPost(@RequestBody JobPost jobPost) throws Exception {
        return new MessageResponse(companyService.newJobPost(jobPost));
    }

    @PostMapping("/company/updateJobPost")
    public MessageResponse updateJobPost(@RequestBody JobPost jobPost) throws Exception {
        return new MessageResponse(companyService.updateJobPost(jobPost));
    }

    @GetMapping("/company/deleteJobPost/{id}")
    public MessageResponse deleteJobPost(@PathVariable("id") Integer id) throws Exception {
        return new MessageResponse(companyService.deleteJobPost(id));
    }

    @GetMapping("/company/allJobPost")
    public List<JobPost> allJobPost() throws Exception {
        return companyService.allJobPost();
    }

    @GetMapping("/seeker/alljob")
    public  List<JobPost> alljob(){
        return  companyService.allJob();
    }

    @GetMapping("/company/jobDetailsbyId/{id}")
    public JobPost fetchJobDetailsById(@PathVariable("id") Integer id) throws Exception {
        return companyService.fetchJobDetailsById(id);
    }



}
