package org.openmrs.module.disa.api.sync;

import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.module.disa.api.LabResult;
import org.openmrs.module.disa.api.LabResultStatus;
import org.openmrs.module.disa.api.PcrEidLabResult;
import org.openmrs.module.disa.api.exception.DisaModuleAPIException;
import org.openmrs.module.disa.api.util.Constants;
import org.springframework.stereotype.Component;

@Component
public class PcrEidLabResultHandler extends BaseLabResultHandler {
	
	@Override
	public LabResultStatus handle(LabResult labResult) {
		if (labResult.isPending() && labResult instanceof PcrEidLabResult) {
			
            Patient patient = (Patient) getSyncContext().get(PatientNidLookup.PATIENT_KEY);
            if (patient == null) {
                throw new DisaModuleAPIException(PatientNidLookup.PATIENT_KEY + " is missing from the sync context");
            }

            Provider provider = (Provider) getSyncContext().get(ProviderLookup.PROVIDER_KEY);
            if (provider == null) {
                throw new DisaModuleAPIException(ProviderLookup.PROVIDER_KEY + " is missing from the sync context");
            }

            Location location = (Location) getSyncContext().get(LocationLookup.LOCATION_KEY);
            if (location == null) {
                throw new DisaModuleAPIException(
                        LocationLookup.LOCATION_KEY + " is missing from the sync context");
            }
            
            PcrEidLabResult pcrEid = (PcrEidLabResult) labResult;
            
            boolean valid = false;
            if (pcrEid.getFinalResult() !=null && pcrEid.getFinalResult().isEmpty()) { 
            	boolean negative  = pcrEid.getFinalResult().contains(Constants.PCR_EID_NEGATIVE);
            	boolean positive = pcrEid.getFinalResult().contains(Constants.PCR_EID_POSITIVE); 
            	boolean indeterminate = pcrEid.getFinalResult().contains(Constants.PCR_EID_INDETERM);
            	
            	valid = negative || positive || indeterminate;
            	
            	if (valid) {
            		Encounter encounter = getElabEncounter(patient, provider, location, pcrEid); 
            	}
            }
		}
		
		return super.handle(labResult);
	}
}
