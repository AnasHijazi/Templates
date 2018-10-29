package com.sts.docgeneration.DocGeneration.domain;

public class ApplicationResponse {

	
	private Boolean operationStatus;
	private String responseCode;
	private String responseMessgae;
	private Object result;
	
	public ApplicationResponse(boolean operationStatus, String responseCode, String responseMessgae, byte[] result) {
		// TODO Auto-generated constructor stub
		this.operationStatus=operationStatus;
		this.responseCode=responseCode;
		this.responseMessgae=responseMessgae;
		this.result=result;
	}
	
	public ApplicationResponse(boolean operationStatus, String responseCode, String responseMessgae, Object result) {
		// TODO Auto-generated constructor stub
		this.operationStatus=operationStatus;
		this.responseCode=responseCode;
		this.responseMessgae=responseMessgae;
		this.result=result;
	}
	
	public Boolean getOperationStatus() {
		return operationStatus;
	}
	public void setOperationStatus(Boolean operationStatus) {
		this.operationStatus = operationStatus;
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public String getResponseMessgae() {
		return responseMessgae;
	}
	public void setResponseMessgae(String responseMessgae) {
		this.responseMessgae = responseMessgae;
	}
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}

	
	
}
