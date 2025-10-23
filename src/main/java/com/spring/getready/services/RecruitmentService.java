package com.spring.getready.services;

import com.spring.getready.model.Application;
import com.spring.getready.model.JobPosting;
import com.spring.getready.repository.ApplicationRepository;
import com.spring.getready.repository.JobPostingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class RecruitmentService {

    @Autowired
    private JobPostingRepository jobPostingRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private AIResumeScreeningService aiScreeningService;

    public List<JobPosting> getAllActiveJobs() {
        return jobPostingRepository.findByIsActiveTrueOrderByPostedOnDesc();
    }

    public JobPosting saveJobPosting(JobPosting jobPosting) {
        return jobPostingRepository.save(jobPosting);
    }

    public Application submitApplication(Application application, String resumeText) {
        JobPosting job = application.getJobPosting();
        
        Map<String, Object> aiResult = aiScreeningService.analyzeResume(
            resumeText, 
            job.getRequiredSkills()
        );
        
        application.setAiScore((Double) aiResult.get("score"));
        application.setAiMatchKeywords((String) aiResult.get("matchedSkills"));
        application.setStatus("Submitted");
        
        return applicationRepository.save(application);
    }

    public List<Application> getApplicationsByJob(JobPosting jobPosting) {
        return applicationRepository.findByJobPostingOrderByAiScoreDesc(jobPosting);
    }

    public Application updateApplicationStatus(Integer applicationId, String status) {
        Application app = applicationRepository.findById(applicationId).orElse(null);
        if (app != null) {
            app.setStatus(status);
            return applicationRepository.save(app);
        }
        return null;
    }
}
