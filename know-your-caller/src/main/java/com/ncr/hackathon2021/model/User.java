package com.ncr.hackathon2021.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class User {

	 @Id
	 public ObjectId _id;
	 public String firstName;
	 public String lastName;
	 public String emailAddress;
	 public String mobileNumber;	 
	 public String voiceNoteId;
	 
	 public User() {
		 
	 }
	 
	public User(ObjectId _id, String firstName, String lastName, String emailAddress, String mobileNumber,
			String voiceNoteId) {		
		this._id = _id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.emailAddress = emailAddress;
		this.mobileNumber = mobileNumber;
		this.voiceNoteId = voiceNoteId;
	}
	
	public User( String firstName, String lastName, String emailAddress, String mobileNumber) {				
		this.firstName = firstName;
		this.lastName = lastName;
		this.emailAddress = emailAddress;
		this.mobileNumber = mobileNumber;		
	}
	/**
	 * @return the _id
	 */
	public ObjectId get_id() {
		return _id;
	}
	/**
	 * @param _id the _id to set
	 */
	public void set_id(ObjectId _id) {
		this._id = _id;
	}
	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}
	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}
	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	/**
	 * @return the emailAddress
	 */
	public String getEmailAddress() {
		return emailAddress;
	}
	/**
	 * @param emailAddress the emailAddress to set
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	/**
	 * @return the mobileNumber
	 */
	public String getMobileNumber() {
		return mobileNumber;
	}
	/**
	 * @param mobileNumber the mobileNumber to set
	 */
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	/**
	 * @return the voiceNote
	 */
	public String getVoiceNoteId() {
		return voiceNoteId;
	}
	/**
	 * @param voiceNote the voiceNote to set
	 */
	public void setVoiceNoteId(String voiceNoteId) {
		this.voiceNoteId = voiceNoteId;
	}
	@Override
	public String toString() {
		return "User [_id=" + _id + ", firstName=" + firstName + ", lastName=" + lastName + ", emailAddress="
				+ emailAddress + ", mobileNumber=" + mobileNumber + ", voiceNoteId=" + voiceNoteId + "]";
	}
	 
}
