<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ taglib prefix="laf" uri="/WEB-INF/view/module/lancearmstrong/taglibs/lancearmstrong.tld" %>
<meta http-equiv="Pragma" content="no-cache"> 
<meta http-equiv="Expires" content="0"> 		
<openmrs:htmlInclude file="/openmrs.js" />
<openmrs:htmlInclude file="/openmrs.css" />
<link href="<openmrs:contextPath/><spring:theme code='stylesheet' />" type="text/css" rel="stylesheet" />
<openmrs:htmlInclude file="/style.css" />
<openmrs:htmlInclude file="/dwr/engine.js" />
<openmrs:htmlInclude file="/dwr/interface/DWRAlertService.js" />
<c:if test="${empty DO_NOT_INCLUDE_JQUERY}">
	<openmrs:htmlInclude file="/scripts/jquery/jquery.min.js" />
	<openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui.custom.min.js" />
	<openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui-datepicker-i18n.js" />
	<openmrs:htmlInclude file="/scripts/jquery-ui/css/redmond/jquery-ui.custom.css" />
</c:if>
<link rel="icon" type="image/ico" href="<openmrs:contextPath/><spring:theme code='favicon' />">

<c:choose>
	<c:when test="${!empty pageTitle}">
		<title>${pageTitle}</title>
	</c:when>
	<c:otherwise>
		<title><spring:message code="openmrs.title"/></title>
	</c:otherwise>
</c:choose>


<script type="text/javascript">
	<c:if test="${empty DO_NOT_INCLUDE_JQUERY}">
		var $j = jQuery.noConflict();
	</c:if>
	/* variable used in js to know the context path */
	var openmrsContextPath = '${pageContext.request.contextPath}';
	var dwrLoadingMessage = '<spring:message code="general.loading" />';
	var jsDateFormat = '<openmrs:datePattern localize="false"/>';
	var jsLocale = '<%= org.openmrs.api.context.Context.getLocale() %>';
</script>

<script type="text/javascript">
	$j(document).ready(function() {
		$j('#markAsCompletedPopup').dialog({
			title: 'dynamic',
			autoOpen: false,
			draggable: false,
			resizable: false,
			width: '100%',
			modal: true,
			open: function(a, b) {  }
		});		
	});

	function loadMarkAsCompletedPopup(title) {
		$j('#markAsCompletedPopup')
			.dialog('option', 'title', title)
			.dialog('option', 'height', $j(window).height() - 50) 
			.dialog('open');
	}
	
	function markCompleted(self, alertId) {
		loadMarkAsCompletedPopup("Add follow-up care performed");
		var parent = self.parentNode;
		parent.style.display = "none";
		var unreadAlertSizeBox = document.getElementById('unreadAlertSize');
		var unreadAlertSize = parseInt(unreadAlertSizeBox.innerHTML);
		if (unreadAlertSize == 1) {
			// hide the entire alert outer div because they read the last alert
			parent = parent.parentNode.parentNode;
			parent.style.display = "none";
		}
		else {
			unreadAlertSize = unreadAlertSize - 1;
			unreadAlertSizeBox.innerHTML = unreadAlertSize;
		}
			
		return false;
	}	
	
	function saveAddedCare(patientId){
		var completeDate = parseSimpleDate($j('#completeDateNew').val(), '<openmrs:datePattern />');
		var careType = $j('#careTypeNew').val();
		var docName = $j('#docNameNew').val();
		var resultType = $j('#resultTypeNew').val();
		var comments = $j('#commentsNew').val();
		var refDate = new Date(1900,1,1);

		if(refDate.getTime()-completeDate.getTime() > 0) {
			alert("Complete date cannot be null or too early!");
			$j('#completeDateNew').focus();
			return;
		}
		DWRLafService.addFollowupCareCompleted(patientId, completeDate, careType, docName, resultType, comments);

		onAddCare(); 

		$j('#followupForm').submit();		
	}				
</script>

<div id="markAsCompletedPopup">		
<div id="addCareDetailDiv">
	<table border="1">
   <c:if test="${patient.remindersCompleted == null}">
	  <thead>
		  <tr>
		  </tr>
	  </thead>
	</c:if>
	<tbody>
	  	<tr>
		    <th>Date Completed</th>
			<td>
				 <input type="text" name="completeDateNew" id="completeDateNew" onClick="showCalendar(this)" />
		    </td>
		 </tr>
		 <tr>
		    <th>Recommended Care</th>
		    <td>
				<select name="careTypeNew" id="careTypeNew">
					<c:forEach items="${patient.careTypes}" var="careType">
						<option value="${careType}" label="${careType}"/>
					</c:forEach>
				</select>
	    	</td>			    
		 </tr>
		 <tr>
		    <th>Doctor Name</th>
		    <td>
			    <input type="text" name="docNameNew" id="docNameNew"/>
		    </td>
		 </tr>
		 <tr>
		    <th>Results</th>
		    <td>
				<select name="resultTypeNew" id="resultTypeNew">
					<c:forEach items="${patient.responseTypes}" var="responseType">
						<option value="${responseType}" label="${responseType}"/>
					</c:forEach>
		    	</select>
		    </td>
		 </tr>
		 <tr>
		    <th>Comments</th>
		    <td>
			    <textarea name="commentsNew" id="commentsNew" rows="1">${status.value}</textarea>
		    </td>
		 </tr>
	  </tbody> 
	</table>
</div>
<div id="addCareDiv">
	<button id="saveAddCare" onClick="saveAddedCare(${patient.patient.patientId});return false;">Save</button>
</div>	
</div>
<div id="alertContent">
		<laf:forEachAlert>
			<c:if test="${varStatus.first}"><div id="alertOuterBox"><div id="alertInnerBox"></c:if>
				<div class="alert">
					<a href="#markRead" onClick="return markCompleted(this, '${alert.alertId}')" HIDEFOCUS class="markAlertRead">
						<img src="${pageContext.request.contextPath}/images/markRead.gif" alt='<spring:message code="personalhr.Alert.mark"/>' title='<spring:message code="personalhr.Alert.mark"/>'/> <span class="markAlertText"><spring:message code="personalhr.Alert.markAsRead"/></span>
					</a>
					Your ${alert.text} ${alert.dateToExpire} <c:if test="${alert.satisfiedByAny}"><i class="smallMessage">(<spring:message code="personalhr.Alert.mark.satisfiedByAny"/>)</i></c:if>
				</div>
			<c:if test="${varStatus.last}">
				<div id="alertBar">
					<img src="${pageContext.request.contextPath}/images/alert.gif" align="center" alt='<spring:message code="personalhr.Alert.unreadAlert"/>' title='<spring:message code="personalhr.Alert.unreadAlert"/>'/>
					<c:if test="${varStatus.count == 1}"><spring:message code="personalhr.Alert.unreadAlert"/></c:if>
					<c:if test="${varStatus.count != 1}"><spring:message code="personalhr.Alert.unreadAlerts" arguments="${varStatus.count}" /></c:if>
				</div>
			</c:if>
		</laf:forEachAlert>
</div>			
			