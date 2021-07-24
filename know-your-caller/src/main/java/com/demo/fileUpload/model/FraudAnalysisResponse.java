package com.demo.fileUpload.model;

public class FraudAnalysisResponse {

	public boolean isFraud;
	public String message;
	public String callTranscript;
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
	@Override
	public String toString() {
		return "FraudAnalysisResponse [isFraud=" + isFraud + ", message=" + message + ", callTranscript="
				+ callTranscript + "]";
	}
	
	
}
