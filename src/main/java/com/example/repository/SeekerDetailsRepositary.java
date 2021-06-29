package com.example.repository;

import com.example.entities.SeekerDetails;
import com.example.entities.SeekerSignup;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SeekerDetailsRepositary extends CrudRepository<SeekerDetails,Integer> {

    //SeekerDetails findBySeeker_id(SeekerSignup seekerSignup);

//         @Query("SELECT u FROM SeekerDetails u ")
//   public List<SeekerDetails> findUserByStatus(SeekerSignup seeker_id);

    @Query("select u from SeekerDetails u where u.seekerSignup = :id")
    SeekerDetails findBysid(@Param("id") SeekerSignup id);


}
