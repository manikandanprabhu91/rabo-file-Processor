package com.rabo.assignment.pojo;

import java.util.List;

public class RaboRecordResponse {
	
	private List<RaboResponse> reboResponse;
	
	private String message;

	public List<RaboResponse> getReboResponse() {
		return reboResponse;
	}

	public void setReboResponse(List<RaboResponse> reboResponse) {
		this.reboResponse = reboResponse;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
