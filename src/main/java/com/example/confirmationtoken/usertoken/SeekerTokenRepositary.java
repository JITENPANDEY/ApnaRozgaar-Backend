package com.example.confirmationtoken.usertoken;


import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;

public interface SeekerTokenRepositary extends CrudRepository <SeekerToken,Long> {

    SeekerToken findByToken(String token);

    void deleteByToken(String token);


}
