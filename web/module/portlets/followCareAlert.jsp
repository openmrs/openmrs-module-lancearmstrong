<%@ include file="/WEB-INF/template/include.jsp" %>
<div id="followup-alert-div"  >
		<iframe src ="${pageContext.request.contextPath}/module/lancearmstrong/followCareAlertForm.form?patientId=${model.patientId}" width="100%" height="200">
		Loading Follow-up Care Alert...
		</iframe>
</div>