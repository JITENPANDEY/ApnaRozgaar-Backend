package com.example.confirmationtoken.companytoken;


import com.example.entities.Company;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyTokenRepository extends CrudRepository<CompanyToken,Long> {
    CompanyToken findByToken(String token);
}
