package com.spring.getready.controller;

import com.spring.getready.model.Application;
import com.spring.getready.model.JobPosting;
import com.spring.getready.model.UploadFile;
import com.spring.getready.services.RecruitmentService;
import com.spring.getready.services.UploadFileService;
import com.spring.getready.repository.JobPostingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/recruitment")
public class RecruitmentController {

    @Autowired
    private RecruitmentService recruitmentService;

    @Autowired
    private JobPostingRepository jobPostingRepository;

    @Autowired
    private UploadFileService uploadFileService;

    @GetMapping("/jobs")
    public String listJobs(Model model) {
        List<JobPosting> jobs = recruitmentService.getAllActiveJobs();
        model.addAttribute("jobs", jobs);
        return "recruitment/job-list";
    }

    @GetMapping("/job/{id}")
    public String viewJob(@PathVariable Integer id, Model model) {
        JobPosting job = jobPostingRepository.findById(id).orElse(null);
        if (job == null) {
            return "redirect:/recruitment/jobs";
        }
        model.addAttribute("job", job);
        return "recruitment/job-detail";
    }

    @GetMapping("/apply/{id}")
    public String showApplicationForm(@PathVariable Integer id, Model model) {
        JobPosting job = jobPostingRepository.findById(id).orElse(null);
        if (job == null) {
            return "redirect:/recruitment/jobs";
        }
        model.addAttribute("job", job);
        return "recruitment/apply";
    }

    @PostMapping("/apply")
    public String applyForJob(
            @RequestParam Integer jobRef,
            @RequestParam("resume") MultipartFile resume,
            @RequestParam(required = false) String notes,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        
        try {
            UploadFile uploadedResume = uploadFileService.saveFile(resume, auth.getName());
            String resumeText = uploadFileService.extractTextFromFile(uploadedResume);
            
            Application application = new Application();
            application.setJobPosting(jobPostingRepository.findById(jobRef).orElse(null));
            application.setResume(uploadedResume);
            application.setNotes(notes);
            
            recruitmentService.submitApplication(application, resumeText);
            
            redirectAttributes.addFlashAttribute("success", "Application submitted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to submit application: " + e.getMessage());
        }
        
        return "redirect:/recruitment/jobs";
    }

    @GetMapping("/job/{id}/applications")
    public String viewApplications(@PathVariable Integer id, Model model) {
        JobPosting job = jobPostingRepository.findById(id).orElse(null);
        if (job == null) {
            return "redirect:/recruitment/jobs";
        }
        
        List<Application> applications = recruitmentService.getApplicationsByJob(job);
        model.addAttribute("job", job);
        model.addAttribute("applications", applications);
        return "recruitment/applications";
    }
}
