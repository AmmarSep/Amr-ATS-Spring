package com.spring.getready.repository;

import com.spring.getready.model.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Integer> {
    List<JobPosting> findByIsActiveTrue();
    List<JobPosting> findByIsActiveTrueOrderByPostedOnDesc();
}
