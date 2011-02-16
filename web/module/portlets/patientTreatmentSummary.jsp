<%@ include file="/WEB-INF/template/include.jsp" %>

<div id="treatmentSummaryPortlet"">
		<c:forEach items='${openmrs:sort(model.patientEncounters, "encounterDatetime", true)}' var="enc" varStatus="encStatus">
			<c:if test="${enc.encounterType.name == 'CANCER TREATMENT SUMMARY'}">
				<iframe src ="${pageContext.request.contextPath}/module/htmlformentry/htmlFormEntry.form?encounterId=${enc.encounterId}&mode=EDIT&inTab=true" width="100%" height="1000">
				Loading Cancer Treatment Summary ...
				</iframe>
				<%--
				<personalhr:portlet url="../module/lancearmstrong/portlets/htmlFormEntryForm.portlet?encounterId=${enc.encounterId}&mode=EDIT&inTab=true" parameters="encounterId=${enc.encounterId}&mode=EDIT&inTab=true" />
				--%>
	        </c:if>					
		</c:forEach>
</div>

