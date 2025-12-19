package org.openmrs.module.disa.api;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.openmrs.Encounter;

@Entity
@Table(name = "disa_sync_log")
public class SyncLog implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Integer id;

	@Column(name = "patient_id")
	private Integer patientId;

	@OneToOne(optional = true)
	@JoinColumn(name = "encounter_id", nullable = true)
	private Encounter encounter;

	@Column(name = "patient_identifier")
	private String patientIdentifier;

	@Column(name = "request_id")
	private String requestId;

	@Column(name = "creator")
	private Integer creator;

	@Column(name = "date_created")
	private Date dateCreated;

	@Column(name = "type_of_result", nullable = true)
	@Enumerated(EnumType.STRING)
	private TypeOfResult typeOfResult;
	
	@Column(name = "lastname")
	private String lastName;
	
	@Column(name = "firstname")
	private String firstName;
	
	@Column(name = "result_status")
	private String syncStatus;
	
	@Column(name = "rejected_description")
	private String rejectedDescription;

	public SyncLog() {
	}

	public SyncLog(String requestId, TypeOfResult typeOfResult) {
		this.requestId = requestId;
		this.typeOfResult = typeOfResult;
	}

	public Integer getPatientId() {
		return patientId;
	}

	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}

	public Encounter getEncounter() {
		return encounter;
	}

	public void setEncounter(Encounter encounter) {
		this.encounter = encounter;
	}

	public String getPatientIdentifier() {
		return patientIdentifier;
	}

	public void setPatientIdentifier(String patientIdentifier) {
		this.patientIdentifier = patientIdentifier;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Integer getCreator() {
		return creator;
	}

	public void setCreator(Integer creator) {
		this.creator = creator;
	}

	public void setTypOfResult(TypeOfResult typeOfResult) {
		this.typeOfResult = typeOfResult;
	}

	public boolean belongsTo(LabResult labResult) {
		return this.equals(new SyncLog(labResult.getRequestId(), labResult.getTypeOfResult()));
	}

	public TypeOfResult getTypeOfResult() {
		return typeOfResult;
	}

	public void setTypeOfResult(TypeOfResult typeOfResult) {
		this.typeOfResult = typeOfResult;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getSyncStatus() {
		return syncStatus;
	}

	public void setSyncStatus(String syncStatus) {
		this.syncStatus = syncStatus;
	}

	public String getRejectedDescription() {
		return rejectedDescription;
	}

	public void setRejectedDescription(String rejectedDescription) {
		this.rejectedDescription = rejectedDescription;
	}
}
