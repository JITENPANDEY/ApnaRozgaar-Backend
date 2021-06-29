package com.example.repository;

import com.example.entities.Company;
import com.example.entities.JobPost;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobPostRepository extends CrudRepository<JobPost,Integer> {
    JobPost findByJobId(Integer jobId);
    List<JobPost> findByCategoryAndLocation(String category,String location);

    List<JobPost> findByLocation(String Location);

    List<JobPost> findByCategory(String category);

    JobPost findByClosehirinig(String closehiring);
}
