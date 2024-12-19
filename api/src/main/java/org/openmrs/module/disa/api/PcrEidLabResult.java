package org.openmrs.module.disa.api;

public class PcrEidLabResult extends LabResult {
	
	private String positivityLevel;
	
	public String getPositivityLevel() {
		return positivityLevel;
	}

	public void setPositivityLevel(String positivityLevel) {
		this.positivityLevel = positivityLevel;
	}

	public PcrEidLabResult() {
		setTypeOfResult(TypeOfResult.PCR_EID);
	}

	public PcrEidLabResult(long l) {
		super(l);
		setTypeOfResult(TypeOfResult.PCR_EID);
	}
	
	@Override
	public String toString() {
		return "PcrEidLabResult [requestId=" + getRequestId() + "]";
	}
}
