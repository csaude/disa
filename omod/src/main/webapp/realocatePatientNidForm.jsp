<%@ taglib prefix="springform"
	uri="http://www.springframework.org/tags/form"%>
<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/disa/css/disa.css"/>

<%@ include file="template/localHeader.jsp"%>

<openmrs:htmlInclude
	file="/scripts/jquery/dataTables/css/dataTables_jui.css" />
<openmrs:htmlInclude
	file="/scripts/jquery/dataTables/js/jquery.dataTables.min.js" />

<script type="text/javascript">
	$j(document).ready(function() {
		$j('#vlResultsTable1').dataTable({
			"iDisplayLength" : 10
		});
	})
</script>

<h2><openmrs:message code="disa.realocate.patients"/></h2>
<br /> 

<b class="boxHeader"><spring:message code="disa.patient" /></b>
<fieldset>
	<table  id="vlResultsTable">
		<tr>
			<th><spring:message code="disa.nid" /></th>
			<th><spring:message code="general.name" /></th>
			<th><spring:message code="disa.gender" /></th>
			<th><spring:message code="disa.age" /></th>
		</tr>	
	    <tr>   
	        <td>${selectedPatient.nid}</td>
	        <td>${selectedPatient.firstName} ${selectedPatient.lastName}</td>
	        <td>${selectedPatient.gender}</td>
	        <td>${selectedPatient.getAge()}</td>
	    </tr>
	</table>
</fieldset>
<br />

<b class="boxHeader"><spring:message code="disa.openmrs.list.healthFacilities" /></b>
<fieldset>
	<form method="post" action="realocatePatient.form">	
		  <select id="selSisma" name="vlSisma"> 
						<c:forEach items="${sismaCodes}" var="sismaCode">
	            			<option value="${sismaCode}">${sismaCode}</option>
        				</c:forEach>
		      		</select>
		<div class="submit-btn">
			<input type="submit" value='<spring:message code="disa.realocate.patient"/>'
				name="realocatePatient" id="btn-realocatePatient" />
		</div>
	</form>

	<c:if test="${not empty errorSelectPatient}">
		<div id="error_msg">
			<span> <spring:message code="${errorSelectPatient}" /></span>
			<br />
		</div>
	</c:if>

</fieldset>

<%@ include file="/WEB-INF/template/footer.jsp"%>