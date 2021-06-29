package com.example.service;


import com.example.confirmationtoken.usertoken.SeekerToken;
import com.example.confirmationtoken.usertoken.SeekerTokenRepositary;
import com.example.confirmationtoken.usertoken.SeekerTokenService;
import com.example.entities.SeekerDetails;
import com.example.entities.SeekerSignup;
import com.example.repository.SeekerDetailsRepositary;
import com.example.repository.SeekerSignupRepositary;
import com.example.request.CompanyLogin;
import com.example.response.TokenResponse;
import com.example.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
public class SeekerSignupService implements UserDetailsService {
    private SeekerSignupRepositary seekerSignupRepositary;
    PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SeekerTokenService seekerTokenService;
    @Autowired
    private SeekerTokenRepositary seekerTokenRepositary;
    @Autowired
    private SeekerDetailsRepositary seekerDetailsRepositary;



    public SeekerSignupService(SeekerSignupRepositary seekerSignupRepositary) {
        this.seekerSignupRepositary = seekerSignupRepositary;
        this.passwordEncoder= new  BCryptPasswordEncoder();
    }

    @Async
    public CompletableFuture<String> addseeker(SeekerSignup aduser) throws MessagingException, UnsupportedEncodingException {
        SeekerSignup seekerexist=seekerSignupRepositary.findByEmail(aduser.getEmail());
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        String password=passwordEncoder.encode(aduser.getPassword());
        aduser.setPassword(password);
        SeekerSignup result= seekerSignupRepositary.save(aduser);

        String token = UUID.randomUUID().toString();
        SeekerToken companyToken = new SeekerToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                aduser);
        seekerTokenService.saveToken(companyToken);

        // Done: SEND EMAIL
        String link = "https://bluecollarbackend-dot-hu18-groupa-java.et.r.appspot.com/seeker/register/confirm?token=" + token;
        //String link="http://localhost:8080/seeker/register/confirm?token=" + token;
        sendVerificationEmail(aduser,link);

        completableFuture.complete("Check your email");
        return completableFuture;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        SeekerSignup company = seekerSignupRepositary.findByEmail(email);
        return new org.springframework.security.core.userdetails.User(company.getEmail(), company.getPassword(), new ArrayList<>());
    }

    public TokenResponse login(CompanyLogin Seekerlogin) throws Exception {
        try {
            SeekerSignup seeker = seekerSignupRepositary.findByEmail(Seekerlogin.getEmail());
            if(seeker==null)
                throw new Exception("Invalid Email or Password");
            Boolean result = passwordEncoder.matches( Seekerlogin.getPassword(), seeker.getPassword());
            if(!result)
                throw new Exception("Invalid Email or Password");
            if(seeker.isActive()==false)
                throw new Exception("Confirm your email");
        } catch (Exception ex) {
            throw new Exception(ex);
        }

        return new TokenResponse( jwtUtil.generateToken(Seekerlogin.getEmail()) ,"user") ;
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

    public String updatepas(SeekerSignup seekerpas) {
        String email=getuserEmail();
        SeekerSignup userD=seekerSignupRepositary.findByEmail(email);
        if(seekerpas.getPassword()!=null) {
            String password = passwordEncoder.encode(seekerpas.getPassword());
            userD.setPassword(password);
        }
        seekerSignupRepositary.save(userD);
        return "Success";
    }



    public String updatephone(SeekerSignup seekerpas) {
        String email=getuserEmail();
        SeekerSignup userD=seekerSignupRepositary.findByEmail(email);
        userD.setPhonenumber(seekerpas.getPhonenumber());
        userD.setName(seekerpas.getName());
        seekerSignupRepositary.save(userD);
        return "Success";
    }

    private void enableseeker(String email){
        SeekerSignup seekerSignup = seekerSignupRepositary.findByEmail(email);
        seekerSignup.setActive(true);
        SeekerDetails seekerDetails= new SeekerDetails();
        seekerDetails.setName(seekerSignup.getName());
        seekerDetails.setPhonenumber(seekerSignup.getPhonenumber());
        seekerDetails.setSeekerSignup(seekerSignup);
        seekerDetailsRepositary.save(seekerDetails);

    }

    public String confirmSeekerRegister(String token) {
        SeekerToken seekerToken = seekerTokenService.getToken(token);
        if(seekerToken==null)
            throw new IllegalStateException("Invalid Token");

        if (seekerToken.getConfirmedAt() != null) {
            throw new IllegalStateException("email already confirmed");
        }
        LocalDateTime expiresAt = seekerToken.getExpiresAt();
        if (expiresAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }

        seekerTokenService.setConfirmedAt(token);
        enableseeker(seekerToken.getSeekerSignup().getEmail());
        seekerTokenService.delete(token);
        return "confirmed";
    }

    @Async
    private void sendVerificationEmail(SeekerSignup seekerSignup, String siteURL)
            throws MessagingException, UnsupportedEncodingException {
        String toAddress = seekerSignup.getEmail();
        String fromAddress = "apnarozgarportal@gmail.com";
        String senderName = "Apna Rozgar";
        String subject = "Please verify your Email";
        String content = "Dear [[name]],<br>"
                + "Please click the link below to verify your Email:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "Your Apna Rozgar.";


        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]",  seekerSignup.getName() );
        String verifyURL = siteURL;

        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        mailSender.send(message);

    }
    @Scheduled(cron = "0 48-49 14 * * ?")
    public void fetchDBJob()
    {


        seekerTokenService.deletetok();


    }

    public String resetPassword(String email) throws MessagingException, UnsupportedEncodingException {
        SeekerSignup seekerSignup = seekerSignupRepositary.findByEmail(email);
        if(seekerSignup==null || seekerSignup.isActive()==Boolean.FALSE)
            throw new IllegalStateException("You are not Registered!");
        // Done: Generate token
        String token = UUID.randomUUID().toString();
        SeekerToken seekerToken = new SeekerToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                seekerSignup);
        seekerTokenService.saveToken(seekerToken);

        // Done: SEND EMAIL
        String link = "https://bluecollar-dot-hu18-groupa-java.et.r.appspot.com/user/resetPassword/confirm?token=" + token;
        String FrontEndLink = "http:/Bluecollarjob/company/resetPassword/confirm?token=" + token;
        sendResetPasswordEmail(seekerSignup,link);
        return "Check Email to Reset Password !";
    }

    private void sendResetPasswordEmail(SeekerSignup seekerSignup, String siteURL)
            throws MessagingException, UnsupportedEncodingException {
        String toAddress = seekerSignup.getEmail();
        String fromAddress = "apnarozgarportal@gmail.com";
        String senderName = "Apna Rozgar";
        String subject = "Please Reset your Password";
        String content = "Dear [[name]],<br>"
                + "Please click the link below to reset your Password:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">RESET PASSWORD</a></h3>"
                + "Thank you,<br>"
                + "Apna Rozgar team.";
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]",  seekerSignup.getName() );
        String verifyURL = siteURL;

        content = content.replace("[[URL]]", verifyURL);
        helper.setText(content, true);
        mailSender.send(message);
    }

    public String confirmResetPassword(String token,String password) {
        SeekerToken seekerToken = seekerTokenService.getToken(token);
        if(seekerToken==null)
            throw new IllegalStateException("Invalid Link");
        LocalDateTime expiresAt = seekerToken.getExpiresAt();
        if (expiresAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Link Expired");
        }
        seekerTokenService.setConfirmedAt(token);
        // updating new password
        SeekerSignup seekerSignup = seekerToken.getSeekerSignup();
        seekerSignup.setPassword(this.passwordEncoder.encode(password));
        seekerSignupRepositary.save(seekerSignup);
        return "Password Reset Successfully!";
    }
}


