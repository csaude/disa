package org.openmrs.module.disa.web.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.disa.Disa;
import org.openmrs.module.disa.extension.util.Constants;
import org.openmrs.module.disa.web.delegate.ViralLoadResultsDelegate;
import org.openmrs.module.disa.web.model.SearchForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class ViralLoadResultsController {

	private ViralLoadResultsDelegate delegate;

	public MessageSourceService messageSourceService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {

		SimpleDateFormat dateFormat = Context.getDateFormat();
		// Allow converting empty String to null when binding Dates.
		// Without this validation will throw a typeMismatch error.
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	@Autowired
	public void setMessageSourceService(MessageSourceService messageSourceService) {
		this.messageSourceService = messageSourceService;
	}

	@RequestMapping(value = "/module/disa/viralLoadStatusList", method = RequestMethod.GET)
	public void showViralLoadStatusList(ModelMap model) {
		delegate = new ViralLoadResultsDelegate();
		model.addAttribute("user", Context.getAuthenticatedUser());
		model.addAttribute(new SearchForm());
	}

	@RequestMapping(value = "/module/disa/viralLoadStatusList", method = RequestMethod.POST)
	public String showViralLoadList(
			@Valid SearchForm searchForm,
			BindingResult result,
			ModelMap model,
			HttpServletRequest request,
			HttpSession session) throws Exception {

		if (result.hasErrors()) {
			return "/module/disa/viralLoadStatusList";
		}

		session.setAttribute("vlState", Constants.NOT_PROCESSED);
		session.setAttribute("startDate", searchForm.getStartDate());
		session.setAttribute("endDate", searchForm.getEndDate());

		return "redirect:viralLoadResultsList.form";
	}

	@RequestMapping(value = "/module/disa/viralLoadResultsList", method = RequestMethod.GET)
	public void showViralLoadResultList(HttpServletRequest request, HttpSession session, ModelMap model,
			@RequestParam(required = false, value = "openmrs_msg") String openmrs_msg) throws Exception {

		String vlState = (String) session.getAttribute("vlState");
		Date startDate = (Date) session.getAttribute("startDate");
		Date endDate = (Date) session.getAttribute("endDate");

		List<Disa> vlDataLst = delegate.getViralLoadDataList(startDate, endDate, vlState);
		session.setAttribute("vlDataLst", vlDataLst);
		session.setAttribute("openmrs_msg", openmrs_msg);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/module/disa/viralLoadResultsList", method = RequestMethod.POST)
	public void downloadExcelFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<Disa> vlDataLst = (List<Disa>) request.getSession().getAttribute("vlDataLst");
		delegate.createExcelFile(vlDataLst, response, messageSourceService);
	}

	@SuppressWarnings({ "unchecked" })
	@RequestMapping(value = "/module/disa/mapPatientIdentifierForm", method = RequestMethod.GET)
	public void patientIdentifierMapping(@ModelAttribute("patient") Patient patient, HttpSession session,
			HttpServletRequest request,
			@RequestParam(required = false, value = "errorPatientRequired") String errorPatientRequired,
			@RequestParam(required = false, value = "errorSelectPatient") String errorSelectPatient) {

		String nid = (String) request.getParameter("nid");
		List<Patient> matchingPatients = null;

		if (nid == null) {
			nid = (String) session.getAttribute("nid");
			matchingPatients = (List<Patient>) session.getAttribute("patients");
		}

		List<Disa> vlDataLst = (List<Disa>) session.getAttribute("vlDataLst");
		Disa selectedPatient = null;
		for (Disa disa : vlDataLst) {
			if (disa.getNid().equals(nid)) {
				selectedPatient = disa;
				break;
			}
		}

		if (matchingPatients == null) {
			matchingPatients = delegate.getPatients(selectedPatient);
		}

		session.setAttribute("selectedPatient", selectedPatient);
		session.setAttribute("patients", matchingPatients);
		session.setAttribute("nid", nid);
		session.setAttribute("errorPatientRequired", errorPatientRequired);
		session.setAttribute("errorSelectPatient", errorSelectPatient);
	}

	@RequestMapping(value = "/module/disa/mapPatientIdentifierForm", method = RequestMethod.POST)
	public ModelAndView mapPatientIdentifier(HttpServletRequest request, HttpSession session,
			@RequestParam(required = false, value = "patientUuid") String patientUuid) throws Exception {

		ModelAndView modelAndView = new ModelAndView(
				new RedirectView(request.getContextPath() + "/module/disa/mapPatientIdentifierForm.form"));

		if (patientUuid == null) {
			modelAndView.addObject("errorSelectPatient", "disa.select.patient");
			return modelAndView;
		}

		String nidDisa = (String) session.getAttribute("nid");
		Disa selectedPatient = (Disa) session.getAttribute("selectedPatient");
		delegate.doMapIdentifier(patientUuid, nidDisa, selectedPatient.getRequestId());

		return new ModelAndView(new RedirectView(request.getContextPath() + "/module/disa/viralLoadResultsList.form"));
	}

	@SuppressWarnings({ "unchecked" })
	@RequestMapping(value = "/module/disa/addPatient.form", method = RequestMethod.POST)
	public ModelAndView addPatient(@ModelAttribute("patient") Patient patient, BindingResult result,
			HttpServletRequest request, HttpSession session) throws Exception {

		ModelAndView model = new ModelAndView(
				new RedirectView(request.getContextPath() + "/module/disa/mapPatientIdentifierForm.form"));
		if (patient.getId() == null) {
			model.addObject("errorPatientRequired", "disa.error.patient.required");
			return model;
		}

		List<Patient> patients = (List<Patient>) session.getAttribute("patients");
		delegate.addPatientToList(patients, patient);

		session.setAttribute("patients", patients);

		return model;
	}
}
