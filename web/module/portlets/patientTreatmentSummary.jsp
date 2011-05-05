<%@ include file="/WEB-INF/view/module/personalhr/template/include.jsp" %>
<personalhr:require privilege="PHR Authenticated" otherwise="/phr/login.htm" redirect="/module/lancearmstrong/portlets/patientTreatmentSummary.htm" />

<openmrs:htmlInclude file="/scripts/easyAjax.js" />

<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables.css" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/js/jquery.dataTables.min.js" />

<openmrs:globalProperty key="dashboard.encounters.showViewLink" var="showViewLink" defaultValue="true"/>
<openmrs:globalProperty key="dashboard.encounters.showEditLink" var="showEditLink" defaultValue="true"/>

<c:set var="foundSummary" value="false"/>
<div class="tooltip">
<spring:message code="lancearmstrong.tooltip.treatment.summary"/>
</div>
<div id="treatmentSummaryPortlet"">
		<c:forEach items='${openmrs:sort(model.patientEncounters, "encounterDatetime", true)}' var="enc" varStatus="encStatus">
			<c:if test="${enc.encounterType.name == 'CANCER TREATMENT SUMMARY' && foundSummary=='false'}">
				<c:set var="foundSummary" value="true"/>			
				<iframe src ="${pageContext.request.contextPath}/module/lancearmstrong/htmlFormEntryForm.form?encounterId=${enc.encounterId}&mode=EDIT&inTab=true" width="100%" height="1000">
				  Loading Cancer Treatment Summary ...
				</iframe>
				<%--
				<personalhr:portlet url="../module/lancearmstrong/portlets/htmlFormEntryForm.portlet?encounterId=${enc.encounterId}&mode=EDIT&inTab=true" parameters="encounterId=${enc.encounterId}&mode=EDIT&inTab=true" />
				--%>
	        </c:if>					
		</c:forEach>
</div>

<c:if test="${foundSummary=='false'}">
	<personalhr:portlet url="../module/personalhr/portlets/personFormEntry.portlet" id="formEntryPortlet" personId="${patient.personId}" parameters="showDecoration=true|showLastThreeEncounters=true|returnUrl=${pageContext.request.contextPath}/phr/patientDashboard.form"/>
</c:if>			

