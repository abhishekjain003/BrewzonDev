package com.asb.details;

import java.io.Serializable;

public class RegistrationDetail implements Serializable {

	public String username = "";

	public String password = "";

	public String email = "";

	public String firstName = "";

	public String lastName = "";

	public String userLocation = "";

	public String userLocationPoint = "";

	public int endYear;

	public int startYear;

	public String verificationCode = "";

	public String profile = "";

	public String imageUrl = "";

	public String GplusUserId = "";

	public String GPlusAuthcode = "";

	public String UniversityId = "";

	public String WorkId = "";

	public String getUniversityId() {
		return UniversityId;
	}

	public void setUniversityId(String universityId) {
		UniversityId = universityId;
	}

	public String getWorkId() {
		return WorkId;
	}

	public void setWorkId(String workId) {
		WorkId = workId;
	}

	public String getGplusUserId() {
		return GplusUserId;
	}

	public void setGplusUserId(String gplusUserId) {
		GplusUserId = gplusUserId;
	}

	public String getGPlusAuthcode() {
		return GPlusAuthcode;
	}

	public void setGPlusAuthcode(String gPlusAuthcode) {
		GPlusAuthcode = gPlusAuthcode;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public String getUserLocation() {
		return userLocation;
	}

	public void setUserLocation(String userLocation) {
		this.userLocation = userLocation;
	}

	public String getUserLocationPoint() {
		return userLocationPoint;
	}

	public void setUserLocationPoint(String userLocationPoint) {
		this.userLocationPoint = userLocationPoint;
	}

	public int getEndYear() {
		return endYear;
	}

	public void setEndYear(int endYear) {
		this.endYear = endYear;
	}

	public int getStartYear() {
		return startYear;
	}

	public void setStartYear(int startYear) {
		this.startYear = startYear;
	}

	public String getVerificationCode() {
		return verificationCode;
	}

	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

}
