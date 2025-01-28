package com.pal.portal.Jobportal.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.pal.portal.Jobportal.Entity.JobPostActivity;
import com.pal.portal.Jobportal.Entity.JobSeekerProfile;
import com.pal.portal.Jobportal.Entity.JobSeekerSave;
import com.pal.portal.Jobportal.Entity.Users;
import com.pal.portal.Jobportal.Service.JobPostActivityService;
import com.pal.portal.Jobportal.Service.JobProfileSeekerService;
import com.pal.portal.Jobportal.Service.JobSeekerApplyService;
import com.pal.portal.Jobportal.Service.JobSeekerSaveService;
import com.pal.portal.Jobportal.Service.UserService;

@Controller
public class JobSeekerSaveController {
	private final UserService usersService;
    private final JobProfileSeekerService jobSeekerProfileService;
    private final JobPostActivityService jobPostActivityService;
    private final JobSeekerSaveService jobSeekerSaveService;

    public JobSeekerSaveController(UserService usersService, JobProfileSeekerService jobSeekerProfileService, JobPostActivityService jobPostActivityService, JobSeekerSaveService jobSeekerSaveService) {
        this.usersService = usersService;
        this.jobSeekerProfileService = jobSeekerProfileService;
        this.jobPostActivityService = jobPostActivityService;
        this.jobSeekerSaveService = jobSeekerSaveService;
    }


    
    @PostMapping("job-details/save/{id}") 
    public String save(@PathVariable("id") int id, JobSeekerSave jobSeekerSave) 
    {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	if (!(authentication instanceof AnonymousAuthenticationToken)) 
    	{
    		String currentUsername = authentication.getName();         
    		Users user = usersService.findByEmail(currentUsername);         
    		Optional<JobSeekerProfile> seekerProfile = jobSeekerProfileService.getOne(user.getUserId()); 
    		JobPostActivity jobPostActivity = jobPostActivityService.getOne(id);
    		if (seekerProfile.isPresent() && jobPostActivity != null) 
    		{
    			jobSeekerSave = new JobSeekerSave();             
    			jobSeekerSave.setUserId(seekerProfile.get());             
    			jobSeekerSave.setJob(jobPostActivity);            
    			jobSeekerSaveService.addNew(jobSeekerSave);         
    		} else {
    			throw new RuntimeException("User not found");
    		}
    	}     
    	return "redirect:/dashboard/"; 
    }
    
    @GetMapping("saved-jobs/")
    public String savedJobs(Model model) {

        List<JobPostActivity> jobPost = new ArrayList<>();
        Object currentUserProfile = usersService.getCurrentUserProfile();

        List<JobSeekerSave> jobSeekerSaveList = jobSeekerSaveService.getCandidatesJob((JobSeekerProfile) currentUserProfile);
        for (JobSeekerSave jobSeekerSave : jobSeekerSaveList) {
            jobPost.add(jobSeekerSave.getJob());
        }

        model.addAttribute("jobPost", jobPost);
        model.addAttribute("user", currentUserProfile);

        return "saved-jobs";
    }

}
