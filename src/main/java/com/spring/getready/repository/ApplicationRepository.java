package com.spring.getready.repository;

import com.spring.getready.model.Application;
import com.spring.getready.model.JobPosting;
import com.spring.getready.model.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {
    List<Application> findByJobPosting(JobPosting jobPosting);
    List<Application> findByCandidate(UserDetail candidate);
    List<Application> findByJobPostingOrderByAiScoreDesc(JobPosting jobPosting);
    List<Application> findByStatus(String status);
}
