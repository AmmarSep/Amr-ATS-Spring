package com.spring.getready.controller;

import com.spring.getready.model.Application;
import com.spring.getready.model.JobPosting;
import com.spring.getready.model.UploadFile;
import com.spring.getready.model.UserDetail;
import com.spring.getready.services.RecruitmentService;
import com.spring.getready.services.UploadFileService;
import com.spring.getready.repository.JobPostingRepository;
import com.spring.getready.repository.UserDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Autowired
    private UserDetailRepository userDetailRepository;

    @GetMapping("/jobs")
    public String listJobs(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDetail userDetail = userDetailRepository.findByEmailEquals(username);
        
        List<JobPosting> jobs = recruitmentService.getAllActiveJobs();
        model.addAttribute("jobs", jobs);
        model.addAttribute("username", userDetail.getUsername());
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
        
        System.out.println("=== APPLICATION SUBMISSION START ===");
        System.out.println("Job ID: " + jobRef);
        System.out.println("User: " + auth.getName());
        System.out.println("File name: " + (resume != null ? resume.getOriginalFilename() : "NULL"));
        System.out.println("File size: " + (resume != null ? resume.getSize() : "NULL"));
        System.out.println("File empty: " + (resume != null ? resume.isEmpty() : "NULL"));
        
        try {
            // Check if file is empty or null
            if (resume == null || resume.isEmpty()) {
                System.out.println("ERROR: Resume file is empty or null");
                redirectAttributes.addFlashAttribute("error", "Please select a resume file.");
                return "redirect:/recruitment/apply/" + jobRef;
            }
            
            System.out.println("Step 1: Saving file...");
            UploadFile uploadedResume = uploadFileService.saveFile(resume, auth.getName());
            System.out.println("Step 1 completed - File ID: " + uploadedResume.getFileId());
            
            System.out.println("Step 2: Extracting text...");
            String resumeText = uploadFileService.extractTextFromFile(uploadedResume);
            System.out.println("Step 2 completed - Text length: " + resumeText.length());
            
            System.out.println("Step 3: Finding candidate...");
            UserDetail candidate = userDetailRepository.findByEmailEquals(auth.getName());
            System.out.println("Step 3 completed - Candidate ID: " + (candidate != null ? candidate.getUserId() : "NULL"));
            
            System.out.println("Step 4: Creating application...");
            Application application = new Application();
            application.setJobPosting(jobPostingRepository.findById(jobRef).orElse(null));
            application.setCandidate(candidate);
            application.setResume(uploadedResume);
            application.setNotes(notes);
            
            System.out.println("Step 5: Submitting application...");
            Application savedApp = recruitmentService.submitApplication(application, resumeText);
            System.out.println("Step 5 completed - Application ID: " + savedApp.getApplicationId());
            
            redirectAttributes.addFlashAttribute("success", "Application submitted successfully!");
            System.out.println("=== APPLICATION SUBMISSION SUCCESS ===");
        } catch (Exception e) {
            System.out.println("ERROR in application submission: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to submit application: " + e.getMessage());
            return "redirect:/recruitment/apply/" + jobRef;
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
