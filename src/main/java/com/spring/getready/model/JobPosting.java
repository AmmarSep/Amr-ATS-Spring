package com.spring.getready.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name="job_postings")
public class JobPosting implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="job_id")
    private Integer jobId;

    @Column(name="job_title")
    private String jobTitle;

    @Column(name="job_description", columnDefinition="TEXT")
    private String jobDescription;

    @Column(name="required_skills", columnDefinition="TEXT")
    private String requiredSkills;

    @Column(name="experience_required")
    private String experienceRequired;

    @Column(name="location")
    private String location;

    @Column(name="job_type")
    private String jobType;

    @Column(name="posted_on")
    private Timestamp postedOn;

    @Column(name="deadline")
    private Timestamp deadline;

    @Column(name="is_active")
    private Boolean isActive;

    @ManyToOne
    @JoinColumn(name="posted_by")
    private UserDetail userDetail;

    @OneToMany(mappedBy="jobPosting")
    private List<Application> applications;

    public JobPosting() {}

    public Integer getJobId() { return jobId; }
    public void setJobId(Integer jobId) { this.jobId = jobId; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getJobDescription() { return jobDescription; }
    public void setJobDescription(String jobDescription) { this.jobDescription = jobDescription; }

    public String getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(String requiredSkills) { this.requiredSkills = requiredSkills; }

    public String getExperienceRequired() { return experienceRequired; }
    public void setExperienceRequired(String experienceRequired) { this.experienceRequired = experienceRequired; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }

    public Timestamp getPostedOn() { return postedOn; }
    public void setPostedOn(Timestamp postedOn) { this.postedOn = postedOn; }

    public Timestamp getDeadline() { return deadline; }
    public void setDeadline(Timestamp deadline) { this.deadline = deadline; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public UserDetail getUserDetail() { return userDetail; }
    public void setUserDetail(UserDetail userDetail) { this.userDetail = userDetail; }

    public List<Application> getApplications() { return applications; }
    public void setApplications(List<Application> applications) { this.applications = applications; }
}
