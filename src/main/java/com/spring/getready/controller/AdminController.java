package com.spring.getready.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spring.getready.config.FilePropertyConfig;
import com.spring.getready.interceptor.FileException;
import com.spring.getready.model.Application;
import com.spring.getready.model.AssignmentDetail;
import com.spring.getready.model.CourseList;
import com.spring.getready.model.JobPosting;
import com.spring.getready.model.StaffDetail;
import com.spring.getready.model.UserDetail;
import com.spring.getready.repository.ApplicationRepository;
import com.spring.getready.repository.AssignmentDetailRepository;
import com.spring.getready.repository.CourseListRepository;
import com.spring.getready.repository.JobPostingRepository;
import com.spring.getready.repository.StaffDetailRepository;
import com.spring.getready.repository.UserDetailRepository;
import com.spring.getready.services.AssignmentService;
import com.spring.getready.services.CourseService;
import com.spring.getready.services.StaffService;
import com.spring.getready.services.UserService;
import com.spring.getready.template.model.AssignmentTemplate;

@Controller
public class AdminController {

	@Autowired
	private UserDetailRepository userDetailRepository;

	@Autowired
	private StaffDetailRepository staffDetailRepository;

	@Autowired
	private CourseListRepository courseListRepository;

	@Autowired
	private AssignmentDetailRepository assignmentDetailRepository;

	@Autowired
	private JobPostingRepository jobPostingRepository;

	@Autowired
	private ApplicationRepository applicationRepository;

	@Autowired
	private FilePropertyConfig filePropertyConfig;

	@Autowired
	private UserService userService;

	@Autowired
	private StaffService staffService;

	@Autowired
	private CourseService courseService;

	@Autowired
	private AssignmentService assignmentService;

	@RequestMapping(path = "/admin", method = RequestMethod.GET)
	public ModelAndView redirectAdminHome(ModelAndView modelAndView) {
		modelAndView.setViewName("redirect:/admin/users");
		return modelAndView;
	}

	@RequestMapping(path = "/admin/{page}", method = RequestMethod.GET)
	public String getAdminHome(@PathVariable("page") String page, Model model) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		UserDetail userDetail = userDetailRepository.findByEmailEquals(username);
		model.addAttribute("username", userDetail.getUsername());
		if (page.contentEquals("users")) {
			List<UserDetail> userDetails = userDetailRepository.findByEmailNot("admin@spring.ats");
			model.addAttribute("users", userDetails);
		} else if (page.contentEquals("jobs")) {
			List<JobPosting> jobPostings = jobPostingRepository.findAll();
			model.addAttribute("jobs", jobPostings);
		} else if (page.contentEquals("applications")) {
			List<Application> applications = applicationRepository.findAll();
			model.addAttribute("applications", applications);
		} else if (page.contentEquals("recruiters")) {
			List<UserDetail> recruiters = userDetailRepository.findByUserGroupShortGroupEquals("REC");
			model.addAttribute("recruiters", recruiters);
		}
		return "admin";
	}

	@RequestMapping(path = "/admin/upload/users", method = RequestMethod.POST)
	public ModelAndView upload(@RequestParam("users") MultipartFile file, ModelAndView modelView,
			RedirectAttributes redirectAttributes) throws FileException {
		if (file != null) {
			String fileName = new Date().getTime() + "_" + file.getOriginalFilename();
			Path path = Paths.get(new File(filePropertyConfig.getFilePath() + File.separator + fileName).toURI());
			try {
				Path outputPath = Files.write(path, file.getBytes());
				if (outputPath != null) {
					boolean result = userService.uploadUsers(path.toAbsolutePath().toString());
					if (result) {
						redirectAttributes.addFlashAttribute("message", "Users created successfully");
					}
					modelView.setViewName("redirect:/admin/users");
				}
			} catch (IOException io) {
				throw new FileException("Error while upload users");
			}
		}
		return modelView;
	}

	@RequestMapping(path = "/admin/user/reset", method = RequestMethod.POST)
	public ModelAndView upload(@RequestParam("user") int userId, ModelAndView modelView) throws FileException {
		userService.resetUser(userId);
		modelView.setViewName("redirect:/admin");
		return modelView;
	}

	@RequestMapping(path = "/admin/staff/{operation}", method = RequestMethod.POST)
	public ModelAndView staffService(@RequestParam(name = "staffname", required = false) String name,
			@RequestParam(name = "field", required = false) String field,
			@RequestParam(name = "technology", required = false) String technology,
			@RequestParam(name = "staffid", required = false) String id, @PathVariable("operation") String operation,
			ModelAndView modelView, RedirectAttributes redirectAttributes) throws FileException {
		if (operation.contentEquals("add")) {
			staffService.addNewStaff(name, field, technology);
		} else if (operation.contains("edit")) {
			boolean isUpdated = staffService.editStaff(Integer.parseInt(id), name, field, technology);
			if (isUpdated) {
				redirectAttributes.addFlashAttribute("message", "Staff detail updated");
			}
		}
		modelView.setViewName("redirect:/admin/staff");
		return modelView;
	}

	@RequestMapping(path = "/admin/course/add", method = RequestMethod.POST)
	public ModelAndView courseService(@RequestParam(name = "coursename", required = false) String name,
			@RequestParam(name = "field", required = false) String field,
			@RequestParam(name = "staff", required = false) Integer staff,
			@RequestParam(name = "support", required = false) List<Integer> support, ModelAndView modelView)
			throws FileException {
		courseService.addNewCourse(name, field, staff, support);
		modelView.setViewName("redirect:/admin/course");
		return modelView;
	}

	@RequestMapping(path = "/admin/assignment/create", method = RequestMethod.POST)
	public ModelAndView assignmentService(@ModelAttribute AssignmentTemplate assignment, ModelAndView modelView)
			throws FileException {
		if (assignment != null) {
			assignmentService.createAssignment(assignment);
		}
		modelView.setViewName("redirect:/admin/assignment");
		return modelView;
	}

	@RequestMapping(path = "/admin/job/create", method = RequestMethod.POST)
	public ModelAndView createJob(
			@RequestParam String jobTitle,
			@RequestParam String location,
			@RequestParam String jobType,
			@RequestParam String experienceRequired,
			@RequestParam String jobDescription,
			@RequestParam String requiredSkills,
			ModelAndView modelView) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		UserDetail userDetail = userDetailRepository.findByEmailEquals(username);
		
		JobPosting job = new JobPosting();
		job.setJobTitle(jobTitle);
		job.setLocation(location);
		job.setJobType(jobType);
		job.setExperienceRequired(experienceRequired);
		job.setJobDescription(jobDescription);
		job.setRequiredSkills(requiredSkills);
		job.setPostedOn(new java.sql.Timestamp(System.currentTimeMillis()));
		job.setIsActive(true);
		job.setUserDetail(userDetail);
		
		jobPostingRepository.save(job);
		modelView.setViewName("redirect:/admin/jobs");
		return modelView;
	}

	@RequestMapping(path = "/admin/job/toggle/{id}", method = RequestMethod.POST)
	public ModelAndView toggleJobStatus(@PathVariable Integer id, ModelAndView modelView) {
		JobPosting job = jobPostingRepository.findById(id).orElse(null);
		if (job != null) {
			job.setIsActive(!job.getIsActive());
			jobPostingRepository.save(job);
		}
		modelView.setViewName("redirect:/admin/jobs");
		return modelView;
	}

	@RequestMapping(path = "/admin/application/status/{id}", method = RequestMethod.POST)
	public ModelAndView updateApplicationStatus(@PathVariable Integer id, @RequestParam String status, ModelAndView modelView) {
		Application application = applicationRepository.findById(id).orElse(null);
		if (application != null) {
			application.setStatus(status);
			applicationRepository.save(application);
		}
		modelView.setViewName("redirect:/admin/applications");
		return modelView;
	}

	@RequestMapping(path = "/admin/recruiter/create", method = RequestMethod.POST)
	public ModelAndView createRecruiter(
			@RequestParam String username,
			@RequestParam String email,
			ModelAndView modelView,
			RedirectAttributes redirectAttributes) {
		try {
			userService.createRecruiter(username, email);
			redirectAttributes.addFlashAttribute("message", "Recruiter created successfully");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Failed to create recruiter: " + e.getMessage());
		}
		modelView.setViewName("redirect:/admin/recruiters");
		return modelView;
	}

	@RequestMapping(path = "/admin/recruiter/toggle/{id}", method = RequestMethod.POST)
	public ModelAndView toggleRecruiterStatus(@PathVariable Integer id, ModelAndView modelView) {
		UserDetail recruiter = userDetailRepository.findById(id).orElse(null);
		if (recruiter != null) {
			recruiter.setIsLocked(!recruiter.getIsLocked());
			userDetailRepository.save(recruiter);
		}
		modelView.setViewName("redirect:/admin/recruiters");
		return modelView;
	}

}
