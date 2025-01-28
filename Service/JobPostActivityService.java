package com.pal.portal.Jobportal.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pal.portal.Jobportal.Entity.IRecruiterJobs;
import com.pal.portal.Jobportal.Entity.JobCompany;
import com.pal.portal.Jobportal.Entity.JobLocation;
import com.pal.portal.Jobportal.Entity.JobPostActivity;
import com.pal.portal.Jobportal.Entity.RecruiterJobsDto;
import com.pal.portal.Jobportal.Repository.JobPostActivityRepository;

@Service
public class JobPostActivityService {

	private JobPostActivityRepository jobPostActivityRepository;

	@Autowired
	public JobPostActivityService(JobPostActivityRepository jobPostActivityRepository) {
		super();
		this.jobPostActivityRepository = jobPostActivityRepository;
	}
	
	
	
	public JobPostActivity addNew(JobPostActivity jobPostActivity) {
		return jobPostActivityRepository.save(jobPostActivity);
	}
	
	public List<RecruiterJobsDto> getrecruitersJob(int recruiter){
		
		List<IRecruiterJobs> recruiterJobsDtos = jobPostActivityRepository.getRecruiterJobs(recruiter);
		
		List<RecruiterJobsDto> recruiterJobsDtosList = new ArrayList<>();
		
		for(IRecruiterJobs recruiterJobs : recruiterJobsDtos) {
			
			JobLocation jobLocation = new JobLocation(recruiterJobs.getLocationId(),recruiterJobs.getCity(),
					recruiterJobs.getState(),recruiterJobs.getCountry());
			JobCompany jobCompany = new JobCompany(recruiterJobs.getCompanyId(),recruiterJobs.getName(),"");
			
			recruiterJobsDtosList.add(new RecruiterJobsDto(recruiterJobs.getTotalCandidates(),
					recruiterJobs.getJob_post_id(),recruiterJobs.getJob_title(),jobLocation,jobCompany));
			
			
		}
		return recruiterJobsDtosList;
	}



	public JobPostActivity getOne(int id) {
		
		return jobPostActivityRepository.findById(id).orElseThrow(()-> new RuntimeException("Job not found"));
	}



	public List<JobPostActivity> getAll() {
		
		return jobPostActivityRepository.findAll();
	}



	public List<JobPostActivity> search(String job, String location, 
			List<String> type, List<String> remote, LocalDate searchDate) {
		
        return Objects.isNull(searchDate) ? 
        		jobPostActivityRepository.searchWithoutDate(job, location, remote,type) :
                jobPostActivityRepository.search(job, location, remote, type, searchDate);
    }
	
	
}
