package com.example.repository;

import com.example.entities.JobPost;
import com.example.entities.Jobstatus;
import com.example.entities.SeekerSignup;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface JobStatusRepositary extends CrudRepository<Jobstatus,Integer> {

    @Query("Select row from Jobstatus row where row.seekerSignup = :eid and row.jobPost = :jid ")
    Jobstatus findbyemailJid(@Param("eid") SeekerSignup eid,
                             @Param("jid") JobPost jid);

    @Query("Select row from Jobstatus row where row.seekerSignup = :seeker and row.status = :saved ")
    ArrayList<Jobstatus> findallsaved(@Param("seeker") SeekerSignup seeker, @Param("saved") String saved);

    @Query("Select row from Jobstatus row where row.seekerSignup = :seeker and row.status = :saved and row.jobPost = :job ")
    Jobstatus findallsavedjid(@Param("seeker") SeekerSignup seeker,
                                         @Param("saved") String saved,
                                         @Param("job") JobPost job);

    @Query("Select row from Jobstatus row where row.jobPost = :job and row.status = :applied")
    ArrayList<Jobstatus> findAllByjobid(@Param("job") JobPost job, @Param("applied") String applied);

    Jobstatus findByid(Integer integer);
//    @Query("DELETE row from Jobstatus row where row.jobPost = :job")
//    void deleteByjobid(@Param("job") JobPost job);
    @Query("Select row from Jobstatus row where row.jobPost = :job")
    ArrayList<Jobstatus> findByJobid(@Param("job") JobPost job);

}
