package org.openmrs.module.disa.api.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.openmrs.api.context.Context;

public class DisaUserPropertyUtil {

	public static List<String> getUserOrgUnitCodes() {

		String prop = Context.getAuthenticatedUser().getUserProperty("orgUnitCodes");

		if (prop == null || prop.trim().isEmpty()) {
			return Collections.emptyList();
		}
		return Arrays.asList(prop.split(","));

	}

	public static List<String> validateSismaCode(List<String> validCodes) {

		List<String> mutableValidCodes = new ArrayList<>(getUserOrgUnitCodes());

	    
	    if(validCodes.equals(mutableValidCodes)) {
	    	return validCodes;	
	    }else {
	    mutableValidCodes.removeAll(validCodes);
	    }
	    return mutableValidCodes;
	}


}