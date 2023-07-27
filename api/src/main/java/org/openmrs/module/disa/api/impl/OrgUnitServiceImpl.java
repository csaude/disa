package org.openmrs.module.disa.api.impl;

import java.net.URISyntaxException;
import java.util.List;

import org.openmrs.module.disa.OrgUnit;
import org.openmrs.module.disa.api.OrgUnitService;
import org.openmrs.module.disa.api.client.DisaAPIHttpClient;
import org.openmrs.module.disa.api.exception.DisaModuleAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrgUnitServiceImpl implements OrgUnitService {

	private DisaAPIHttpClient client;

	@Autowired
	public OrgUnitServiceImpl(DisaAPIHttpClient client) {
		this.client = client;
	}

	@Override
	public OrgUnit getOrgUnitByCode(String code) {
		try {
			return client.getOrgUnitByCode(code);
		} catch (URISyntaxException e) {
			throw new DisaModuleAPIException("disa.orgunit.get.error", new Object[] { code }, e);
		}

	}

	@Override
	public List<OrgUnit> searchOrgUnits(String q) {
		try {
			return client.searchOrgUnits(q);
		} catch (URISyntaxException e) {
			throw new DisaModuleAPIException("disa.orgunit.search.error", (Object[]) null, e);
		}

	}
}
