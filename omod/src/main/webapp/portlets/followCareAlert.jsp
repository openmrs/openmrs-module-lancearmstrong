<%@ include file="/WEB-INF/view/module/personalhr/template/include.jsp" %>
<personalhr:require privilege="PHR Authenticated" otherwise="/phr/login.htm" redirect="/module/lancearmstrong/portlets/followCareAlert.htm" />

<div id="followup-alert-div"  >
		<iframe src ="${pageContext.request.contextPath}/module/lancearmstrong/followCareAlertForm.form?patientId=${model.patientId}" width="100%" height="200">
		Loading Follow-up Care Alert...
		</iframe>
</div>