package com.pal.portal.Jobportal.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "recruiter_profile")
public class RecruiterProfile {

	@Id
	private int userAccountId;
	
	@OneToOne
	@JoinColumn(name="user_account_id")
	@MapsId
	private Users userId;
	
	private String city;
	
	private String firstName;
	
	private String lastName;
	
	private String country;
	
	private String company;
	
	private String state;
	
	@Column(nullable = true,length = 64)
	private String profilePhoto;	
	
	public RecruiterProfile() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RecruiterProfile(int userAccountId, Users userId, String city, String firstName, String lastName,
			String country, String company, String state, String profilePhoto) {
		super();
		this.userAccountId = userAccountId;
		this.userId = userId;
		this.city = city;
		this.firstName = firstName;
		this.lastName = lastName;
		this.country = country;
		this.company = company;
		this.state = state;
		this.profilePhoto = profilePhoto;
	}

	public RecruiterProfile(Users users) {
		this.userId = users;
	}

	public int getUserAccountId() {
		return userAccountId;
	}

	public void setUserAccountId(int userAccountId) {
		this.userAccountId = userAccountId;
	}

	public Users getUserId() {
		return userId;
	}

	public void setUserId(Users userId) {
		this.userId = userId;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getProfilePhoto() {
		return profilePhoto;
	}

	public void setProfilePhoto(String profilePhoto) {
		this.profilePhoto = profilePhoto;
	}
	
	@Transient
    public String getPhotosImagePath() {
        if (profilePhoto == null) return null;
        return "/photos/recruiter/" + userAccountId + "/" + profilePhoto;
    }
	
	@Override
	public String toString() {
		return "RecruiterProfile [userAccountId=" + userAccountId + ", userId=" + userId + ", city=" + city
				+ ", firstName=" + firstName + ", lastName=" + lastName + ", country=" + country + ", company="
				+ company + ", state=" + state + ", profilePhoto=" + profilePhoto + "]";
	}
	
	
	
	
}
