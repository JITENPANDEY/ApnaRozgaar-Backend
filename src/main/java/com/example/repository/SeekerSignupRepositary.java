package com.example.repository;

import com.example.entities.SeekerSignup;
import org.springframework.data.repository.CrudRepository;

public interface SeekerSignupRepositary extends CrudRepository<SeekerSignup,String> {


    SeekerSignup findByEmail(String email);

    Iterable<SeekerSignup> findAllByEmail(String Email);


    boolean existsByEmail(String s);
}
