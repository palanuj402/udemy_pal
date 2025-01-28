package com.pal.portal.Jobportal.Controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.pal.portal.Jobportal.Entity.JobPostActivity;
import com.pal.portal.Jobportal.Entity.JobSeekerApply;
import com.pal.portal.Jobportal.Entity.JobSeekerProfile;
import com.pal.portal.Jobportal.Entity.JobSeekerSave;
import com.pal.portal.Jobportal.Entity.RecruiterProfile;
import com.pal.portal.Jobportal.Entity.Users;
import com.pal.portal.Jobportal.Service.JobPostActivityService;
import com.pal.portal.Jobportal.Service.JobProfileSeekerService;
import com.pal.portal.Jobportal.Service.JobSeekerApplyService;
import com.pal.portal.Jobportal.Service.JobSeekerSaveService;
import com.pal.portal.Jobportal.Service.RecruiterProfileService;
import com.pal.portal.Jobportal.Service.UserService;

@Controller
public class JobSeekerApplyController {

	private final JobPostActivityService jobPostActivityService;
	private final UserService userService;
	private final JobSeekerApplyService jobSeekerApplyService;
	private final JobSeekerSaveService jobSeekerSaveService;
	private final RecruiterProfileService recruiterProfileService;
	private final JobProfileSeekerService jobProfileSeekerService;
	
	@Autowired
	public JobSeekerApplyController(JobPostActivityService jobPostActivityService, UserService userService,
			JobSeekerApplyService jobSeekerApplyService,JobSeekerSaveService jobSeekerSaveService,
			RecruiterProfileService recruiterProfileService,JobProfileSeekerService jobProfileSeekerService) {
		super();
		this.jobPostActivityService = jobPostActivityService;
		this.userService = userService;
		this.jobSeekerApplyService = jobSeekerApplyService;
		this.jobSeekerSaveService = jobSeekerSaveService;
		this.recruiterProfileService = recruiterProfileService;
		this.jobProfileSeekerService = jobProfileSeekerService;
	}
	
	@GetMapping("job-details-apply/{id}")
	public String display(@PathVariable("id") int id, Model model) {
		JobPostActivity jobPostActivity = jobPostActivityService.getOne(id);
		
		List<JobSeekerApply> jobSeekerApplyList = jobSeekerApplyService.getJobCandidates(jobPostActivity);
        List<JobSeekerSave> jobSeekerSaveList = jobSeekerSaveService.getJobCandidates(jobPostActivity);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))) {
                RecruiterProfile user = recruiterProfileService.getCurrentRecruiterProfile();
                if (user != null) {
                    model.addAttribute("applyList", jobSeekerApplyList);
                }
            } else {
                JobSeekerProfile user = jobProfileSeekerService.getCurrentSeekerProfile();
                if (user != null) {
                    boolean exists = false;
                    boolean saved = false;
                    for (JobSeekerApply jobSeekerApply : jobSeekerApplyList) {
                        if (jobSeekerApply.getUserId().getUserAccountId() == user.getUserAccountId()) {
                            exists = true;
                            break;
                        }
                    }
                    for (JobSeekerSave jobSeekerSave : jobSeekerSaveList) {
                        if (jobSeekerSave.getUserId().getUserAccountId() == user.getUserAccountId()) {
                            saved = true;
                            break;
                        }
                    }
                    model.addAttribute("alreadyApplied", exists);
                    model.addAttribute("alreadySaved", saved);
                }
            }
        }

        JobSeekerApply jobSeekerApply = new JobSeekerApply();
        model.addAttribute("applyJob", jobSeekerApply);
        
		model.addAttribute("jobDetails", jobPostActivity);
		model.addAttribute("user", userService.getCurrentUserProfile());
		
		return "job-details";
	}
	
	@PostMapping("job-details/apply/{id}")
    public String apply(@PathVariable("id") int id, JobSeekerApply jobSeekerApply) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            Users user = userService.findByEmail(currentUsername);
            Optional<JobSeekerProfile> seekerProfile = jobProfileSeekerService.getOne(user.getUserId());
            JobPostActivity jobPostActivity = jobPostActivityService.getOne(id);
            if (seekerProfile.isPresent() && jobPostActivity != null) {
                jobSeekerApply = new JobSeekerApply();
                jobSeekerApply.setUserId(seekerProfile.get());
                jobSeekerApply.setJob(jobPostActivity);
                jobSeekerApply.setApplyDate(new Date());
            } else {
                throw new RuntimeException("User not found");
            }
            jobSeekerApplyService.addNew(jobSeekerApply);
        }

        return "redirect:/dashboard/";
    }
}
