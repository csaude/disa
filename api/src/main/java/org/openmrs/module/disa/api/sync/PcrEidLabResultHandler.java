package org.openmrs.module.disa.api.sync;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.Provider;
import org.openmrs.module.disa.api.LabResult;
import org.openmrs.module.disa.api.LabResultStatus;
import org.openmrs.module.disa.api.NotProcessingCause;
import org.openmrs.module.disa.api.PcrEidLabResult;
import org.openmrs.module.disa.api.SampleType;
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
            
            validateResult(pcrEid);
            
            boolean valid = false;
            if (pcrEid.getFinalResult() !=null && !pcrEid.getFinalResult().isEmpty()) { 
            	boolean negative  = pcrEid.getFinalResult().contains(Constants.PCR_EID_NEGATIVE);
            	boolean positive = pcrEid.getFinalResult().contains(Constants.PCR_EID_POSITIVE); 
            	boolean indeterminate = pcrEid.getFinalResult().contains(Constants.PCR_EID_INDETERM);
            	
            	valid = negative || positive || indeterminate;
            	
            	if (valid) {
            		Encounter encounter = getElabEncounter(patient, provider, location, pcrEid);
            		
            		addPcrObs(pcrEid, encounter);
            		
            		pcrEid.setLabResultStatus(LabResultStatus.PROCESSED);
                    getSyncContext().put(ENCOUNTER_KEY, encounter);
            	}
            }
            
            if (!valid) {
            	pcrEid.setLabResultStatus(LabResultStatus.NOT_PROCESSED);
            	pcrEid.setNotProcessingCause(NotProcessingCause.INVALID_RESULT);
            }
		}
		
		return super.handle(labResult);
	}

	private void validateResult(PcrEidLabResult pcrEid) {
		if(hasNoResult(pcrEid)) {
			updateNotProcessed(pcrEid, NotProcessingCause.INVALID_RESULT);
		}
	}

	private boolean hasNoResult(PcrEidLabResult pcrEid) {
		return (pcrEid.getFinalResult()==null || pcrEid.getFinalResult().isEmpty());
	}
	
	private void updateNotProcessed(PcrEidLabResult pcrEid, NotProcessingCause invalidResult) {
		pcrEid.setLabResultStatus(LabResultStatus.NOT_PROCESSED);
		pcrEid.setNotProcessingCause(invalidResult);
	}


	private void addPcrObs(PcrEidLabResult pcrEid, Encounter encounter) {
        Person person = personService.getPersonByUuid(encounter.getPatient().getUuid());
        Date obsDatetime = encounter.getEncounterDatetime();
        Location location = encounter.getLocation();
        
        // Specimen type
        SampleType sampleType = pcrEid.getSampleType();
        List<SampleType> validSampleTypes = Arrays.asList(SampleType.SA, SampleType.DBS, SampleType.PL);
        if (sampleType != null && validSampleTypes.contains(sampleType)) {
            Concept concept = conceptService.getConceptByUuid(Constants.SAMPLE_TYPE);
            Obs obs23832 = new Obs(person, concept, obsDatetime, location);
            obs23832.setValueCoded(conceptService.getConceptByUuid(sampleType.getConceptUuid()));
            encounter.addObs(obs23832);
        }
        
        // type of collection
        if (pcrEid.getPositivityLevel()!=null && !pcrEid.getPositivityLevel().isEmpty()) {
            Obs obs165502 = new Obs();
            obs165502.setPerson(person);
            obs165502.setObsDatetime(obsDatetime);
            obs165502.setConcept(conceptService.getConceptByUuid(Constants.SAMPLE_EID));
            obs165502.setLocation(location);
            obs165502.setEncounter(encounter);
            processAttribute1(pcrEid.getPositivityLevel(), obs165502, encounter); 
		}
        
        // final result
        if (pcrEid.getFinalResult()!=null && !pcrEid.getFinalResult().isEmpty()) {
            Obs obs1030 = new Obs();
            obs1030.setPerson(person);
            obs1030.setObsDatetime(obsDatetime);
            obs1030.setConcept(conceptService.getConceptByUuid(Constants.HIV_PCR_QUAL));
            obs1030.setLocation(location);
            obs1030.setEncounter(encounter);
            processResult(pcrEid.getFinalResult(), obs1030, encounter);
		}
	}

	private void processResult(String finalResult, Obs obs1030, Encounter encounter) {
		if (finalResult.contains(Constants.NEGATIV)) {
			obs1030.setValueCoded(conceptService.getConceptByUuid(Constants.HIV_PCR_NEGATIVE));
			encounter.addObs(obs1030); 
        } else if (finalResult.contains(Constants.POSITIV)) {
			obs1030.setValueCoded(conceptService.getConceptByUuid(Constants.HIV_PCR_POSITIVE));
			encounter.addObs(obs1030); 
        } else if (finalResult.contains(Constants.INDETERMINATE)) {
			obs1030.setValueCoded(conceptService.getConceptByUuid(Constants.HIV_PCR_INDETERMINATE));
			encounter.addObs(obs1030); 
        }
	}

	private void processAttribute1(String typeOfCollection, Obs obs165502, Encounter encounter) {  
		// map associating attribute values with type of collection uuid 
		Map<String, Concept> attributeToUuidMap = new HashMap<>();
		attributeToUuidMap.put(Constants._PC9M, conceptService.getConceptByUuid(Constants.PC9M)); 
		attributeToUuidMap.put(Constants._CPC9M, conceptService.getConceptByUuid(Constants.CPC9M));
		attributeToUuidMap.put(Constants._RCR9M, conceptService.getConceptByUuid(Constants.RCR9M));
		attributeToUuidMap.put(Constants._CS9M, conceptService.getConceptByUuid(Constants.CS9M));
		attributeToUuidMap.put(Constants._PC9_17M, conceptService.getConceptByUuid(Constants.PC9_17M));
		attributeToUuidMap.put(Constants._CPC9_17M, conceptService.getConceptByUuid(Constants.CPC9_17M));
		attributeToUuidMap.put(Constants._RCR9_17M, conceptService.getConceptByUuid(Constants.RCR9_17M));
		attributeToUuidMap.put(Constants._CS9_17M, conceptService.getConceptByUuid(Constants.CS9_17M));
		Concept concept = attributeToUuidMap.get(typeOfCollection);
		if (concept !=null) {
			obs165502.setValueCoded(concept);
			encounter.addObs(obs165502); 
		}
	}
}
