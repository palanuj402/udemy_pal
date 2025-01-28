package com.pal.portal.Jobportal.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pal.portal.Jobportal.Entity.JobPostActivity;
import com.pal.portal.Jobportal.Entity.JobSeekerProfile;
import com.pal.portal.Jobportal.Entity.JobSeekerSave;

@Repository
public interface JobSeekerSaveRepository extends JpaRepository<JobSeekerSave, Integer>{
	
	public List<JobSeekerSave> findByUserId(JobSeekerProfile userAccountId);

    List<JobSeekerSave> findByJob(JobPostActivity job);
    
}
