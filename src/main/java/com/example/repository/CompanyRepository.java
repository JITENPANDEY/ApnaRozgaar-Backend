package com.example.repository;

import com.example.entities.Company;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.entities.CompanyDetail;

import java.util.ArrayList;

@Repository
public interface CompanyRepository extends CrudRepository<Company,Integer> {
    Company findByEmail(String email);

    Company findByCompanyId(Integer id);

    @Query("Select row from Company row where row.companyId = :id ")
    Company findByid(@Param("id") Integer id);

    @Query("Select row from Company row where row.companyName = :id ")
    ArrayList<Company> findByname(String id);

    @Query("Select row from Company row where row.companyDetail = :cd ")
    Company findByCompanyDetail(CompanyDetail cd);
}
