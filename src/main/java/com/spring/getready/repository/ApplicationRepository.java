package com.spring.getready.repository;

import com.spring.getready.model.Application;
import com.spring.getready.model.JobPosting;
import com.spring.getready.model.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {
    List<Application> findByJobPosting(JobPosting jobPosting);
    List<Application> findByCandidate(UserDetail candidate);
    List<Application> findByJobPostingOrderByAiScoreDesc(JobPosting jobPosting);
    List<Application> findByStatus(String status);
    
    @Query("SELECT a FROM Application a LEFT JOIN FETCH a.resume LEFT JOIN FETCH a.candidate LEFT JOIN FETCH a.jobPosting")
    List<Application> findAllWithResumeAndDetails();
    
    @Query("SELECT a FROM Application a LEFT JOIN FETCH a.resume WHERE a.jobPosting = :jobPosting ORDER BY a.aiScore DESC")
    List<Application> findByJobPostingWithResumeOrderByAiScoreDesc(@Param("jobPosting") JobPosting jobPosting);
    
    @Query("SELECT a FROM Application a LEFT JOIN FETCH a.resume LEFT JOIN FETCH a.candidate LEFT JOIN FETCH a.jobPosting WHERE a.status = :status ORDER BY a.appliedOn DESC")
    List<Application> findByStatusWithResumeAndDetails(@Param("status") String status);
}
