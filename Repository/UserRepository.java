package com.pal.portal.Jobportal.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pal.portal.Jobportal.Entity.Users;

public interface UserRepository extends JpaRepository<Users, Integer>{
	
	Optional<Users> findByEmail(String email);

}
