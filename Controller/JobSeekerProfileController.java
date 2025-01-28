package com.pal.portal.Jobportal.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.pal.portal.Jobportal.Entity.JobSeekerProfile;
import com.pal.portal.Jobportal.Entity.Skills;
import com.pal.portal.Jobportal.Entity.Users;
import com.pal.portal.Jobportal.Repository.UserRepository;
import com.pal.portal.Jobportal.Service.JobProfileSeekerService;
import com.pal.portal.Jobportal.util.FileDownloadUtil;
import com.pal.portal.Jobportal.util.FileUploadUtil;

@Controller
@RequestMapping("/job-seeker-profile")
public class JobSeekerProfileController {

	private final JobProfileSeekerService jobProfileSeekerService;
	
	private final UserRepository userRepository;

	@Autowired
	public JobSeekerProfileController(JobProfileSeekerService jobProfileSeekerService, UserRepository userRepository) {
		super();
		this.jobProfileSeekerService = jobProfileSeekerService;
		this.userRepository = userRepository;
	}
	
	@GetMapping("/")
	public String jobSeekerProfile(Model model) {
		
		JobSeekerProfile jobSeekerProfile = new JobSeekerProfile();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		List<Skills> skills = new ArrayList<>();
		
		if(!(authentication instanceof AnonymousAuthenticationToken)) {
			Users user = userRepository.findByEmail(authentication.getName())
			.orElseThrow(()->new UsernameNotFoundException("User Not found"));
			
			Optional<JobSeekerProfile> seekerProfile = jobProfileSeekerService.getOne(user.getUserId());
			if(seekerProfile.isPresent()) {
				jobSeekerProfile = seekerProfile.get();
				if(jobSeekerProfile.getSkills().isEmpty()) {
					skills.add(new Skills());
					jobSeekerProfile.setSkills(skills);
				}
			}
			
			model.addAttribute("skills", skills);
            model.addAttribute("profile", jobSeekerProfile);
		}
		
		return "job-seeker-profile"; 
	}
	
	@PostMapping("addNew")
	public String addNew(JobSeekerProfile jobSeekerProfile,
						@RequestParam("image")MultipartFile image,
						@RequestParam("pdf")MultipartFile pdf,
						Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(!(authentication instanceof AnonymousAuthenticationToken)) {
			Users user = userRepository.findByEmail(authentication.getName())
			.orElseThrow(()->new UsernameNotFoundException("User Not found"));
			
			jobSeekerProfile.setUserId(user);
			jobSeekerProfile.setUserAccountId(user.getUserId());
			
		}
		List<Skills> skills = new ArrayList<>();
		model.addAttribute("skills", skills);
        model.addAttribute("profile", jobSeekerProfile);
        
        for(Skills skill:jobSeekerProfile.getSkills()) {
        	skill.setJobSeekerProfile(jobSeekerProfile);
        }
        
        String imageName = "";
        String resumeName = "";
        
        if(!Objects.equals(image.getOriginalFilename(), "")) {
        	imageName = StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));
        	jobSeekerProfile.setProfilePhoto(imageName);
        }
        
        if(!Objects.equals(pdf.getOriginalFilename(), "")) {
        	resumeName = StringUtils.cleanPath(Objects.requireNonNull(pdf.getOriginalFilename()));
        	jobSeekerProfile.setResume(resumeName);
        }
        
        JobSeekerProfile seekerProfile = jobProfileSeekerService.addNew(jobSeekerProfile);
        
        try {
            String uploadDir = "photos/candidate/" + jobSeekerProfile.getUserAccountId();
            if (!Objects.equals(image.getOriginalFilename(), "")) {
                FileUploadUtil.saveFile(uploadDir, imageName, image);
            }
            if (!Objects.equals(pdf.getOriginalFilename(), "")) {
                FileUploadUtil.saveFile(uploadDir, resumeName, pdf);
            }
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        
        return "redirect:/dashboard/";
	}
	
	@GetMapping("/{id}")
    public String candidateProfile(@PathVariable("id") int id, Model model) {

        Optional<JobSeekerProfile> seekerProfile = jobProfileSeekerService.getOne(id);
        model.addAttribute("profile", seekerProfile.get());
        return "job-seeker-profile";
    }

    @GetMapping("/downloadResume")
    public ResponseEntity<?> downloadResume(@RequestParam(value = "fileName") String fileName, @RequestParam(value = "userID") String userId) {

        FileDownloadUtil downloadUtil = new FileDownloadUtil();
        Resource resource = null;

        try {
            resource = downloadUtil.getFileAsResourse("photos/candidate/" + userId, fileName);
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }

        if (resource == null) {
            return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
        }

        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);

    }
}
