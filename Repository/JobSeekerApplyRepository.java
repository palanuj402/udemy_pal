package com.pal.portal.Jobportal.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pal.portal.Jobportal.Entity.JobPostActivity;
import com.pal.portal.Jobportal.Entity.JobSeekerApply;
import com.pal.portal.Jobportal.Entity.JobSeekerProfile;

@Repository
public interface JobSeekerApplyRepository extends JpaRepository<JobSeekerApply, Integer> {
	
	 List<JobSeekerApply> findByUserId(JobSeekerProfile userId);

	    List<JobSeekerApply> findByJob(JobPostActivity job);
}
