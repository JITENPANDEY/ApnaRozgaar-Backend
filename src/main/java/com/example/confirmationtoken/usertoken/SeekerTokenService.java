package com.example.confirmationtoken.usertoken;



import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class SeekerTokenService {

    @Autowired
    private SeekerTokenRepositary seekerTokenRepositary;

    public void saveToken(SeekerToken SeekerToken) {
        seekerTokenRepositary.save(SeekerToken);
    }

    public SeekerToken getToken(String token) {
        return seekerTokenRepositary.findByToken(token);
    }

    public void setConfirmedAt(String token) {
        SeekerToken seekerToken =  seekerTokenRepositary.findByToken(token);
        seekerToken.setConfirmedAt(LocalDateTime.now());
        seekerTokenRepositary.save(seekerToken);
    }

    public void delete(String token) {
        SeekerToken seekerToken=seekerTokenRepositary.findByToken(token);
        seekerTokenRepositary.deleteById(seekerToken.getId());
    }

    public void deletetok() {
        //seekerTokenRepositary.deleteByName("9aea5880-d82f-47b9-9f6e-4f5ab6b9878b");
        LocalDateTime date=LocalDateTime.now();
        List<SeekerToken> seekerTokens= (List<SeekerToken>) seekerTokenRepositary.findAll();
        for (SeekerToken temp: seekerTokens
             ) {
            if(temp.getExpiresAt().isBefore(date))seekerTokenRepositary.delete(temp);

        }
        System.out.println("succes");
    }
}
