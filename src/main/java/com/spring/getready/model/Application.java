package com.spring.getready.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name="applications")
public class Application implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="application_id")
    private Integer applicationId;

    @ManyToOne
    @JoinColumn(name="job_ref")
    private JobPosting jobPosting;

    @ManyToOne
    @JoinColumn(name="candidate_ref")
    private UserDetail candidate;

    @ManyToOne
    @JoinColumn(name="resume_ref")
    private UploadFile resume;

    @Column(name="applied_on")
    private Timestamp appliedOn;

    @Column(name="status")
    private String status;

    @Column(name="ai_score")
    private Double aiScore;

    @Column(name="ai_match_keywords", columnDefinition="TEXT")
    private String aiMatchKeywords;

    @Column(name="interview_scheduled_on")
    private Timestamp interviewScheduledOn;

    @Column(name="notes", columnDefinition="TEXT")
    private String notes;

    public Application() {}

    public Integer getApplicationId() { return applicationId; }
    public void setApplicationId(Integer applicationId) { this.applicationId = applicationId; }

    public JobPosting getJobPosting() { return jobPosting; }
    public void setJobPosting(JobPosting jobPosting) { this.jobPosting = jobPosting; }

    public UserDetail getCandidate() { return candidate; }
    public void setCandidate(UserDetail candidate) { this.candidate = candidate; }

    public UploadFile getResume() { return resume; }
    public void setResume(UploadFile resume) { this.resume = resume; }

    public Timestamp getAppliedOn() { return appliedOn; }
    public void setAppliedOn(Timestamp appliedOn) { this.appliedOn = appliedOn; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getAiScore() { return aiScore; }
    public void setAiScore(Double aiScore) { this.aiScore = aiScore; }

    public String getAiMatchKeywords() { return aiMatchKeywords; }
    public void setAiMatchKeywords(String aiMatchKeywords) { this.aiMatchKeywords = aiMatchKeywords; }

    public Timestamp getInterviewScheduledOn() { return interviewScheduledOn; }
    public void setInterviewScheduledOn(Timestamp interviewScheduledOn) { this.interviewScheduledOn = interviewScheduledOn; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
