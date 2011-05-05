<%@ include file="/WEB-INF/view/module/personalhr/template/include.jsp" %>
<personalhr:require privilege="PHR Authenticated" otherwise="/phr/login.htm" redirect="/module/lancearmstrong/portlets/patientFollowCare.htm" />

<div id="followup-div"  >
		<div class="tooltip">
		<spring:message code="lancearmstrong.tooltip.followup.care"/>
		</div>
		<iframe src ="${pageContext.request.contextPath}/module/lancearmstrong/patientFollowCareForm.form?patientId=${model.patientId}" width="100%" height="1000">
		Loading Follow-up Care...
		</iframe>
</div>