package com.example.controller;


import com.example.entities.SeekerSignup;
import com.example.repository.SeekerSignupRepositary;
import com.example.request.CompanyLogin;
import com.example.request.EmailRequest;
import com.example.request.PasswordRequest;
import com.example.response.MessageResponse;
import com.example.response.TokenResponse;
import com.example.service.SeekerSignupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


@RestController
//@CrossOrigin(origins = "http://localhost:4200")
@CrossOrigin(origins ="https://bluecollar-dot-hu18-groupa-java.et.r.appspot.com")
public class SeekerSignupController {
    private SeekerSignupService seekerSignupService;

    @Autowired
    private SeekerSignupRepositary seekerSignupRepositary;

    public SeekerSignupController(SeekerSignupService seekerSignupService) {
        this.seekerSignupService = seekerSignupService;
    }

    @GetMapping("/")
    public String Intial(){
        return "Blue-Collar-Job Backend Started";
    }


    @PostMapping("/user/add")
    public MessageResponse addseeker(@RequestBody SeekerSignup aduser) throws MessagingException, UnsupportedEncodingException, ExecutionException, InterruptedException {
        SeekerSignup seekerexist=seekerSignupRepositary.findByEmail(aduser.getEmail());
        if(seekerexist!=null) {

            throw new IllegalStateException("User already Exist");
        }
        seekerSignupService.addseeker(aduser);
        return new MessageResponse("Check Your email");
    }

    @PostMapping("/user/login")
    public TokenResponse login(@RequestBody CompanyLogin aduser) throws Exception {
        return seekerSignupService.login(aduser);
    }

    @GetMapping("/user/all")
    public String Intial2(){
        return "Blue-Collar-Job Backend Started";
    }

    @PutMapping("/update/user/password")
    public  MessageResponse updatePas(@RequestBody SeekerSignup seekerpas){
       return  new MessageResponse(seekerSignupService.updatepas(seekerpas));
    }

    @PutMapping("/update/user/details")
    public  MessageResponse updatephone(@RequestBody SeekerSignup seekerpas){
        return new MessageResponse( seekerSignupService.updatephone(seekerpas));
    }

    @GetMapping("/seeker/register/confirm")
    public String confirmRegisterCompany(@RequestParam("token") String token){
        String res=(seekerSignupService.confirmSeekerRegister(token));
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

    @PostMapping("/user/resetPassword")
    public MessageResponse resetPassword(@RequestBody EmailRequest emailRequest) throws MessagingException, UnsupportedEncodingException {
        return new MessageResponse( seekerSignupService.resetPassword(emailRequest.getEmail()));
    }
    @PostMapping("/user/resetPassword/confirm")
    public MessageResponse confirmResetPassword(@RequestParam("token") String token,@RequestBody PasswordRequest passwordRequest){
        return new MessageResponse(seekerSignupService.confirmResetPassword( token, passwordRequest.getPassword()));
    }

}
