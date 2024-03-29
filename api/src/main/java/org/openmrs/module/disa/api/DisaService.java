/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.disa.api;

import java.io.Serializable;
import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.LocationAttribute;
import org.openmrs.Patient;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This service exposes module's core functionality. It is a Spring managed bean
 * which is configured in moduleApplicationContext.xml.
 * <p>
 * It can be accessed only via Context:<br>
 * <code>
 * Context.getService(DisaService.class).someMethod();
 * </code>
 *
 * @see org.openmrs.api.context.Context
 */
@Transactional
public interface DisaService extends OpenmrsService {

	/*
	 * Add service methods here
	 *
	 */

	public List<LocationAttribute> getAllLocationAttribute(String valueReference);

	public Serializable saveSyncLog(SyncLog syncLog);

	public boolean existsInSyncLog(LabResult labResult);

	public List<Integer> getPatientByNid(String identifier);

	public List<Patient> getPatientByPatientId(Integer patientId);

	public void loadEncounters(List<LabResult> labResult);

	@Authorized("Mapear pacientes no Disa Interoperabilidade")
	Patient mapIdentifier(String patientUuid, LabResult disa);

	void handleProcessedLabResult(LabResult labResult, Encounter encounter);

	/**
	 * Get
	 * {@link org.openmrs.module.disa.api.sync.scheduler.ViralLoadFormSchedulerTask}
	 * repeat interval.
	 * This method was created to get around the "Manage Scheduler" privilege
	 * required on {@link org.openmrs.scheduler.SchedulerService}
	 * methods.
	 * 
	 * @return The sync interval.
	 */
	Long getSyncTaskRepeatInterval();
}
