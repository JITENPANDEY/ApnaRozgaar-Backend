package com.example.service;

import com.example.confirmationtoken.companytoken.CompanyToken;
import com.example.confirmationtoken.companytoken.CompanyTokenRepository;
import com.example.confirmationtoken.companytoken.CompanyTokenService;
import com.example.entities.Company;
import com.example.entities.CompanyDetail;
import com.example.entities.JobPost;
import com.example.entities.Jobstatus;
import com.example.repository.CompanyDetailRepository;
import com.example.repository.CompanyRepository;
import com.example.repository.JobPostRepository;
import com.example.repository.JobStatusRepositary;
import com.example.request.CompanyLogin;
import com.example.response.TokenResponse;
import com.example.util.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
import java.util.*;
import java.util.concurrent.CompletableFuture;


@Service
public class CompanyService implements UserDetailsService {
    private CompanyRepository companyRepository;
    private CompanyDetailRepository companyDetailRepository;
    private CompanyTokenRepository companyTokenRepository;
    private JobPostRepository jobPostRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CompanyTokenService companyTokenService;
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private JobStatusRepositary jobStatusRepositary;

    @Autowired
    public CompanyService(CompanyRepository companyRepository,
                          CompanyDetailRepository companyDetailRepository,
                          CompanyTokenRepository companyTokenRepository,
                          JobPostRepository jobPostRepository) {
        this.companyRepository = companyRepository;
        this.companyDetailRepository=companyDetailRepository;
        this.companyTokenRepository=companyTokenRepository;
        this.jobPostRepository = jobPostRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Company company = companyRepository.findByEmail(email);
        return new org.springframework.security.core.userdetails.User(company.getEmail(), company.getPassword(), new ArrayList<>());
    }

    // REGISTER

    @Async
    public CompletableFuture<String> register(Company company) throws MessagingException, UnsupportedEncodingException {
        //Company companyExist = companyRepository.findByEmail(company.getEmail());
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
//        if(companyExist!=null){
//            if(companyExist.getEnabled()==Boolean.TRUE)
//                throw new IllegalStateException("Email Already Registered!");
//        }
        String encodedPass = this.passwordEncoder.encode(company.getPassword());
        company.setPassword(encodedPass);
        companyRepository.save(company);
        // Done: Generate token
        String token = UUID.randomUUID().toString();
        CompanyToken companyToken = new CompanyToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                company);
        companyTokenService.saveToken(companyToken);
        // Done: SEND EMAIL
        String link = "https://bluecollarbackend-dot-hu18-groupa-java.et.r.appspot.com/company/register/confirm?token=" + token;
        //String link="http://localhost:8080/company/register/confirm?token=" + token;
        sendVerificationEmail(company,link);
        completableFuture.complete("Check your email");
        return completableFuture;
    }


    private void sendVerificationEmail(Company company, String siteURL)
            throws MessagingException, UnsupportedEncodingException {
        String toAddress = company.getEmail();
        String fromAddress = "apnarozgarportal@gmail.com";
        String senderName = "Apna Rozgar";
        String subject = "Please verify your registration";
        String content = "Dear [[name]],<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "Apna Rozgar Team.";


        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]",  company.getCompanyName() );
        String verifyURL = siteURL;

        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);
        mailSender.send(message);
    }

    public String confirmCompanyRegister(String token) {
        CompanyToken companyToken = companyTokenService.getToken(token);
        if(companyToken==null)
            throw new IllegalStateException("Invalid Token");
        if (companyToken.getConfirmedAt() != null) {
            throw new IllegalStateException("email already confirmed");
        }
        LocalDateTime expiresAt = companyToken.getExpiresAt();
        if (expiresAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }

        companyTokenService.setConfirmedAt(token);
        enableCompany(companyToken.getCompany().getEmail());
        return "confirmed";
    }

    private void enableCompany(String email) {
        Company company = companyRepository.findByEmail(email);
        company.setEnabled(Boolean.TRUE);
        CompanyDetail companyDetail = new CompanyDetail();
        companyDetail.setCompanyName(company.getCompanyName());
        companyDetail.setEmail(company.getEmail());
        companyDetail.setCountry(company.getCountry());
        company.setCompanyDetail(companyDetail);
        companyRepository.save(company);

    }

    // RESET PASSWORD


    public String resetPassword(String email) throws MessagingException, UnsupportedEncodingException {
        Company company = companyRepository.findByEmail(email);
        if(company==null || company.getEnabled()==Boolean.FALSE)
            throw new IllegalStateException("You are not Registered!");
        // Done: Generate token
        String token = UUID.randomUUID().toString();
        CompanyToken companyToken = new CompanyToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                company);
        companyTokenService.saveToken(companyToken);

        // Done: SEND EMAIL
        String link = "https://bluecollar-dot-hu18-groupa-java.et.r.appspot.com/company/resetPassword/confirm?token=" + token;
        String FrontEndLink = "http:/Bluecollarjob/company/resetPassword/confirm?token=" + token;
        sendResetPasswordEmail(company,link);
        return "Check Email to Reset Password !";
    }

    private void sendResetPasswordEmail(Company company, String siteURL)
            throws MessagingException, UnsupportedEncodingException {
        String toAddress = company.getEmail();
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

        content = content.replace("[[name]]",  company.getCompanyName() );
        String verifyURL = siteURL;

        content = content.replace("[[URL]]", verifyURL);
        helper.setText(content, true);
        mailSender.send(message);
    }

    public String confirmResetPassword(String token,String password) {
        CompanyToken companyToken = companyTokenService.getToken(token);
        if(companyToken==null)
            throw new IllegalStateException("Invalid Link");
        LocalDateTime expiresAt = companyToken.getExpiresAt();
        if (expiresAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Link Expired");
        }
        companyTokenService.setConfirmedAt(token);
        // updating new password
        Company company = companyToken.getCompany();
        company.setPassword(this.passwordEncoder.encode(password));
        companyRepository.save(company);
        return "Password Reset Successfully!";
    }

    // LOGIN

    public TokenResponse login(CompanyLogin companyLogin) throws Exception {
        try {
            Company company = companyRepository.findByEmail(companyLogin.getEmail());
            if(company==null)
                throw new Exception("Invalid Email or Password");
            if(company.getEnabled()==false) {
                throw new Exception("Register your company first!");
            }
            Boolean result = passwordEncoder.matches( companyLogin.getPassword(), company.getPassword());
            if(!result)
                throw new Exception("Invalid Email or Password");
        }catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
        return new TokenResponse( jwtUtil.generateToken(companyLogin.getEmail()), "company" ) ;
    }

    public String getCompanyEmail(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes())
                .getRequest();
        String authorizationHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;
        token = authorizationHeader.substring(7);
        if(jwtUtil.extractExpiration(token).before(new Date())){
            throw new IllegalStateException("Token Expired!");
        }
        email = jwtUtil.extractUsername(token);
        return email;
    }

    // Company Detail

    public CompanyDetail fetchCompanyDetails( ){
        String email = getCompanyEmail();
        Company company = companyRepository.findByEmail(email);
        if(company==null){
            throw new IllegalStateException("Company not Registered!");
        }
        CompanyDetail companyDetail = company.getCompanyDetail();
        if(companyDetail==null){
            throw new IllegalStateException("Company not Registered!");
        }
        return companyDetail;
    }

    public CompanyDetail fetchCompanyDetailsById( Integer id){
        Company company = companyRepository.findByCompanyId(id);
        if(company==null){
            throw new IllegalStateException("Company not Registered!");
        }
        CompanyDetail companyDetail = company.getCompanyDetail();
        if(companyDetail==null){
            throw new IllegalStateException("Company not Registered!");
        }
        return companyDetail;
    }

    public String updateCompanyDetails(CompanyDetail companyDetail){
        String email = getCompanyEmail();
        Company company = companyRepository.findByEmail(email);
        if(company==null){
            throw new IllegalStateException("Company not Registered!");
        }
        CompanyDetail oldCompantDetail = company.getCompanyDetail();
        company.setCompanyDetail(companyDetail);
        companyRepository.save(company);
        if(oldCompantDetail!=null)
            companyDetailRepository.delete(oldCompantDetail);
        return "Company Details Updated!";
    }

    // New Job Post
    public String newJobPost(JobPost jobPost){
        String email = getCompanyEmail();
        Company company = companyRepository.findByEmail(email);
        if(company==null){
            throw new IllegalStateException("Company not Registered!");
        }
        jobPost.setCompanyName(company.getCompanyName());
        company.getJobPostList().add(jobPost);
        companyRepository.save(company);
        return "New job Post Added!";
    }

    public String updateJobPost(JobPost jobPost){
        String email = getCompanyEmail();
        Company company = companyRepository.findByEmail(email);
        if(company==null){
            throw new IllegalStateException("Company not Registered!");
        }
        JobPost oldjobPost = jobPostRepository.findByJobId(jobPost.getJobId());
        if(oldjobPost==null){
            throw new IllegalStateException("No Such Job Exists!");
        }
        //update values
        oldjobPost.setTitle(jobPost.getTitle());
        oldjobPost.setDescription(jobPost.getDescription());
        oldjobPost.setExperience(jobPost.getExperience());
        oldjobPost.setMinSalary(jobPost.getMinSalary());
        oldjobPost.setMaxSalary(jobPost.getMaxSalary());
        oldjobPost.setLocation(jobPost.getLocation());
        oldjobPost.setLastDate(jobPost.getLastDate());
        oldjobPost.setCategory(jobPost.getCategory());
        oldjobPost.setCreatedate(jobPost.getCreatedate());
        oldjobPost.setApplicant(jobPost.getApplicant());

        jobPostRepository.save(oldjobPost);
        return "Job Post Updated!";
    }

    public String deleteJobPost(Integer id){
        String email = getCompanyEmail();
        Company company = companyRepository.findByEmail(email);
        if(company==null){
            throw new IllegalStateException("Company not Registered!");
        }
        JobPost jobPost = jobPostRepository.findByJobId(id);
        if(jobPost==null){
            throw new IllegalStateException("No Such Job Exists!");
        }
        //jobStatusRepositary.deleteByjobid(jobPost);
        ArrayList<Jobstatus> Aljobst=jobStatusRepositary.findByJobid(jobPost);
        if(Aljobst.isEmpty()==false) {
                for (Jobstatus j : Aljobst) {
                    j.setSeekerSignup(null);
                    jobStatusRepositary.delete(j);

                }
        }
        jobPostRepository.delete(jobPost);
        return "Job Post Deleted!";
    }

    public List<JobPost> allJobPost(){
        String email = getCompanyEmail();
        Company company = companyRepository.findByEmail(email);
        if(company==null){
            throw new IllegalStateException("Company not Registered!");
        }
//        ArrayList<JobPost> alljobcomapny = (ArrayList<JobPost>) company.getJobPostList();
//        Collections.sort(alljobcomapny, new Comparator<JobPost>() {
//            @Override
//            public int compare(JobPost o1, JobPost o2) {
//                return o1.getCreatedat().compareTo(o2.getCreatedat());
//            }
//        });
//        Collections.reverse(alljobcomapny);
        List<JobPost> jobPostList1 = company.getJobPostList();
        return jobPostList1;
    }
//    public  static int compareThem(JobPost a, JobPost b){
//        return a.getLastDate().compareTo(b.getLastDate());
//    }
    public List<JobPost> allJob() {
        List<JobPost> alljob= (List<JobPost>) jobPostRepository.findAll();

        List<JobPost> jobPostList1=new ArrayList();
        for (JobPost j: alljob) {
            if(j.getClosehirinig().equals("No"))
                jobPostList1.add(j);
        }

        return  jobPostList1;
    }
    public JobPost fetchJobDetailsById( Integer id){
        JobPost jobPost = jobPostRepository.findByJobId(id);
        if(jobPost==null){
            throw new IllegalStateException("No Such Job Exists!");
        }
        return jobPost;
    }


}
