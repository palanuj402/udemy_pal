package com.pal.portal.Jobportal.Service;

import java.util.Optional;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.pal.portal.Jobportal.Entity.JobSeekerProfile;
import com.pal.portal.Jobportal.Entity.Users;
import com.pal.portal.Jobportal.Repository.JobSeekerProfileRepository;
import com.pal.portal.Jobportal.Repository.UserRepository;

@Service
public class JobProfileSeekerService {

	private final JobSeekerProfileRepository jobSeekerProfileRepository;
	private final UserRepository userRepository;

	public JobProfileSeekerService(JobSeekerProfileRepository jobSeekerProfileRepository,
			UserRepository userRepository) {
		super();
		this.jobSeekerProfileRepository = jobSeekerProfileRepository;
		this.userRepository = userRepository;
	}
	
	public Optional<JobSeekerProfile> getOne(Integer id){
		return jobSeekerProfileRepository.findById(id);
	}

	public JobSeekerProfile addNew(JobSeekerProfile jobSeekerProfile) {
		// TODO Auto-generated method stub
		return jobSeekerProfileRepository.save(jobSeekerProfile);
	}
	
	public JobSeekerProfile getCurrentSeekerProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            Users users = userRepository.findByEmail(currentUsername).orElseThrow(() -> new UsernameNotFoundException("User not found"));
            Optional<JobSeekerProfile> seekerProfile = getOne(users.getUserId());
            return seekerProfile.orElse(null);
        } else return null;

    }
}
