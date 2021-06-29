package com.example.repository;


import com.example.entities.CompanyDetail;
import com.example.entities.JobPost;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyDetailRepository extends CrudRepository<CompanyDetail,Integer> {

    @Query("Select row from CompanyDetail row where row.companyDetailId = :id ")
    CompanyDetail findByid(@Param("id") Integer id);
}
