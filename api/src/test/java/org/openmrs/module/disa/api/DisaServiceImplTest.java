package org.openmrs.module.disa.api;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.disa.api.db.DisaDAO;
import org.openmrs.module.disa.api.exception.DisaModuleAPIException;
import org.openmrs.module.disa.api.impl.DisaServiceImpl;
import org.openmrs.module.disa.api.util.Constants;
import org.openmrs.test.jupiter.BaseContextMockTest;

public class DisaServiceImplTest extends BaseContextMockTest {

    @Mock
    private LabResultService labResultService;

    @Mock
    private PatientService patientService;

    @Mock
    private LocationService locationService;

    @Mock
    private MessageSourceService messageSourceService;

    @Mock
    private DisaDAO dao;

    @InjectMocks
    private DisaServiceImpl disaServiceImpl;

    @Test
    public void mapIdentifierShouldSavePatientIdentifier() {
        String patientUuid = "patientUuid";
        LabResult disa = new HIVVLLabResult();
        disa.setLabResultStatus(LabResultStatus.NOT_PROCESSED);
        disa.setNotProcessingCause(NotProcessingCause.NID_NOT_FOUND);
        disa.setNid("12345");
        disa.setRequestId("requestId");
        Patient patient = new Patient();
        patient.addIdentifier(new PatientIdentifier());

        when(patientService.getPatientByUuid(patientUuid)).thenReturn(patient);
        PatientIdentifierType identifierType = new PatientIdentifierType();
        identifierType.setUuid(Constants.DISA_NID);
        when(patientService.getPatientIdentifierTypeByUuid(Constants.DISA_NID)).thenReturn(identifierType);
        when(patientService.getPatientIdentifiers(eq(disa.getNid()), anyListOf(PatientIdentifierType.class), any(),
                any(), any()))
                .thenReturn(new ArrayList<>());

        disaServiceImpl.mapIdentifier(patientUuid, disa);

        verify(patientService, times(1)).savePatientIdentifier(any(PatientIdentifier.class));
        verify(labResultService, times(1)).rescheduleLabResult(disa.getId());
    }

    public void mapIdentifierShouldThrowExceptionWhenResultIsProcessed() {
        String patientUuid = "patientUuid";
        LabResult disa = new HIVVLLabResult();
        disa.setLabResultStatus(LabResultStatus.PROCESSED);
        disa.setNotProcessingCause(NotProcessingCause.NID_NOT_FOUND);
        disa.setNid("12345");
        disa.setRequestId("requestId");
        Patient patient = new Patient();

        when(patientService.getPatientByUuid(patientUuid)).thenReturn(patient);
        PatientIdentifierType identifierType = new PatientIdentifierType();
        identifierType.setUuid(Constants.DISA_NID);
        when(patientService.getPatientIdentifierTypeByUuid(Constants.DISA_NID)).thenReturn(identifierType);
        when(patientService.getPatientIdentifiers(eq(disa.getNid()), anyListOf(PatientIdentifierType.class), any(),
                any(), any()))
                .thenReturn(new ArrayList<>());

        assertThrows(DisaModuleAPIException.class, () -> {
            disaServiceImpl.mapIdentifier(patientUuid, disa);
        });

        verify(patientService, times(0)).savePatientIdentifier(any(PatientIdentifier.class));
        verify(labResultService, times(0)).rescheduleLabResult(disa.getId());
    }

    public void mapIdentifierShouldThrowExceptionWhenResultIsPending() {
        String patientUuid = "patientUuid";
        LabResult disa = new HIVVLLabResult();
        disa.setLabResultStatus(LabResultStatus.PENDING);
        disa.setNotProcessingCause(NotProcessingCause.NID_NOT_FOUND);
        disa.setNid("12345");
        disa.setRequestId("requestId");
        Patient patient = new Patient();

        when(patientService.getPatientByUuid(patientUuid)).thenReturn(patient);
        PatientIdentifierType identifierType = new PatientIdentifierType();
        identifierType.setUuid(Constants.DISA_NID);
        when(patientService.getPatientIdentifierTypeByUuid(Constants.DISA_NID)).thenReturn(identifierType);
        when(patientService.getPatientIdentifiers(eq(disa.getNid()), anyListOf(PatientIdentifierType.class), any(),
                any(), any()))
                .thenReturn(new ArrayList<>());

        assertThrows(DisaModuleAPIException.class, () -> {
            disaServiceImpl.mapIdentifier(patientUuid, disa);
        });

        verify(patientService, never()).savePatientIdentifier(any(PatientIdentifier.class));
        verify(labResultService, never()).rescheduleLabResult(disa.getId());
    }

}
