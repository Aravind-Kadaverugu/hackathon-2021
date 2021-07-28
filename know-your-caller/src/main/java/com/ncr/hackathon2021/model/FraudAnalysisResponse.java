package com.ncr.hackathon2021.model;

public class FraudAnalysisResponse {
	
	public boolean isFraud;
	public String message;
	public String callTranscript;
	public String callRecordId;
	public String mobileNumber;
	
	/**
	 * @return the callRecordId
	 */
	public String getCallRecordId() {
		return callRecordId;
	}
	/**
	 * @param callRecordId the callRecordId to set
	 */
	public void setCallRecordId(String callRecordId) {
		this.callRecordId = callRecordId;
	}
	/**
	 * @return the isFraud
	 */
	public boolean isFraud() {
		return isFraud;
	}
	/**
	 * @param isFraud the isFraud to set
	 */
	public void setFraud(boolean isFraud) {
		this.isFraud = isFraud;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/**
	 * @return the callTranscript
	 */
	public String getCallTranscript() {
		return callTranscript;
	}
	/**
	 * @param callTranscript the callTranscript to set
	 */
	public void setCallTranscript(String callTranscript) {
		this.callTranscript = callTranscript;
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
	@Override
	public String toString() {
		return "FraudAnalysisResponse [isFraud=" + isFraud + ", message=" + message + ", callTranscript="
				+ callTranscript + ", callRecordId=" + callRecordId + ", mobileNumber=" + mobileNumber + "]";
	}
	
	
}
