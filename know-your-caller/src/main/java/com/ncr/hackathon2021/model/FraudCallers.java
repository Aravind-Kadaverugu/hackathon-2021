package com.ncr.hackathon2021.model;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class FraudCallers {

	@Id
	 public ObjectId _id;
	public List<String> fraudMobileNumbers;
	public String sampleFraudCallId;
	
	public FraudCallers() {
		 
	 }
	
	public FraudCallers(List<String> fraudMobileNumbers, String sampleFraudCallId) {				
		this.fraudMobileNumbers = fraudMobileNumbers;
		this.sampleFraudCallId = sampleFraudCallId;
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
	 * @return the fraudMobileNumbers
	 */
	public List<String> getFraudMobileNumbers() {
		return fraudMobileNumbers;
	}
	/**
	 * @param fraudMobileNumbers the fraudMobileNumbers to set
	 */
	public void setFraudMobileNumbers(List<String> fraudMobileNumbers) {
		this.fraudMobileNumbers = fraudMobileNumbers;
	}
	/**
	 * @return the sampleFraudCallId
	 */
	public String getSampleFraudCallId() {
		return sampleFraudCallId;
	}
	/**
	 * @param sampleFraudCallId the sampleFraudCallId to set
	 */
	public void setSampleFraudCallId(String sampleFraudCallId) {
		this.sampleFraudCallId = sampleFraudCallId;
	}
	@Override
	public String toString() {
		return "FraudCallers [_id=" + _id + ", fraudMobileNumbers=" + fraudMobileNumbers + ", sampleFraudCallId="
				+ sampleFraudCallId + "]";
	}
	
}
