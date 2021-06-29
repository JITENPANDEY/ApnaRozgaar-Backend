package com.example.confirmationtoken.companytoken;

import com.example.confirmationtoken.usertoken.SeekerToken;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class CompanyTokenService {

    @Autowired
    private CompanyTokenRepository companyTokenRepository;

    public void saveToken(CompanyToken companyToken) {
        companyTokenRepository.save(companyToken);
    }

    public CompanyToken getToken(String token) {
        return companyTokenRepository.findByToken(token);
    }

    public void setConfirmedAt(String token) {
        CompanyToken companyToken =  companyTokenRepository.findByToken(token);
        companyToken.setConfirmedAt(LocalDateTime.now());
        companyTokenRepository.save(companyToken);
    }

    public void deletetok() {
        LocalDateTime date=LocalDateTime.now();
        List<CompanyToken> companyTokens= (List<CompanyToken>) companyTokenRepository.findAll();
        for (CompanyToken temp: companyTokens
        ) {
            if(temp.getExpiresAt().isBefore(date))companyTokenRepository.delete(temp);

        }
        System.out.println("succes");
    }

}
