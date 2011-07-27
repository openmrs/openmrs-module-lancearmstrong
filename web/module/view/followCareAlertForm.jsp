<%@ include file="/WEB-INF/view/module/personalhr/template/include.jsp" %>

<personalhr:require privilege="PHR Authenticated" otherwise="/phr/login.htm" redirect="/module/lancearmstrong/view/followCareAlertForm.jsp" />

<%@ taglib prefix="laf" uri="/WEB-INF/view/module/lancearmstrong/taglibs/lancearmstrong.tld" %>

<openmrs:htmlInclude file="/moduleResources/lancearmstrong/jquery-1.4.4.min.js" />
<openmrs:htmlInclude file="/moduleResources/lancearmstrong/jquery-ui-1.8.9.custom.css" />
<openmrs:htmlInclude file="/moduleResources/lancearmstrong/jquery-ui-1.8.9.custom.min.js" />
<openmrs:htmlInclude file="/dwr/interface/DWRLafService.js" />
<openmrs:htmlInclude file="/openmrs.js" />
		 
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
			width: '50%',
			modal: true,
			open: function(a, b) {  }
		});	
		$j('#markAsScheduledPopup').dialog({
			title: 'dynamic',
			autoOpen: false,
			draggable: false,
			resizable: false,
			width: '50%',
			modal: true,
			open: function(a, b) {  }
		});		
		$j('#markAsSnoozePopup').dialog({
			title: 'dynamic',
			autoOpen: false,
			draggable: false,
			resizable: false,
			width: '50%',
			modal: true,
			open: function(a, b) {  }
		});				
	});

	function loadPopup(title, popupId) {
		$j('#'+popupId)
			.dialog('option', 'title', title)
			.dialog('option', 'height', $j(window).height()/3) 
			.dialog('open');
	}
		
	function markAlert(self, alertId, targetDate, patientId, popupId) {
		$j('#'+popupId).dialog({
			title: 'dynamic',
			autoOpen: false,
			draggable: false,
			resizable: false,
			width: '50%',
			modal: true,
			buttons: [
			          {
			              text: "Cancel",
			              click: function() { $j('#'+popupId).dialog("close"); }
			          },
			          {
			              text: "OK",
			              click: function() {
			            	  var ret = 0;
			            	  if(popupId=='markAsCompletedPopup') {
			            		ret = saveAddedCare(patientId, alertId, targetDate);
			            	  } else if(popupId=='markAsScheduledPopup') {
			            	  	ret = saveSchedule(patientId, alertId, targetDate);
			            	  } else if(popupId=='markAsSnoozePopup', targetDate) {
			            	  	ret = saveSnooze(patientId, alertId, targetDate);
			            	  }
			            	  
			            	  if(ret == 0) {			            	  
			            	  	  $j('#'+popupId).dialog("close");
			            	  } 
			              }
			          }			          
			         ]
		});	
		loadPopup("Alert response", popupId);
			
		return false;
	}	
	
	function saveAddedCare(patientId, alertId, targetDate){
		var completeDate = parseSimpleDate($j('#completeDateNew').val(), '<openmrs:datePattern />');
		var careType = alertId;
		var docName = $j('#docNameNew').val();
		var resultType = $j('#resultTypeNew').val();
		var comments = $j('#commentsNew').val();
		var refDate = new Date(1900,1,1);
		var now = new Date();

		if(refDate.getTime()-completeDate.getTime() > 0) {
			alert("Complete date cannot be null or too early!");
			$j('#completeDateNew').focus();
			return -1;
		} else if(completeDate.getTime() - now.getTime() > 0) {
			alert("Complete date cannot be in the future!");
			$j('#completeDateNew').focus();
			return -1;
		}
		DWRLafService.followupCareCompleted(patientId, completeDate, careType, docName, resultType, comments,
				{
			  		callback:function(str) { 
			    		location.reload(true);
			  		}
			  	}
		);
				
		return 0;
	}	
	
	function saveSchedule(patientId, alertId, targetDate){
		var scheduleDate = parseSimpleDate($j('#scheduleDate').val(), '<openmrs:datePattern />');
		var careType = alertId;
		var refDate = new Date();
		var splits = targetDate.split("/");
		
		var ys = splits[2]; // Convert year, month and date to strings 
		var ms = splits[0];   
		var ds = splits[1];   
		if ( ms.length == 1 ) ms = "0" + ms; // Add leading zeros to month and date if required 
		if ( ds.length == 1 ) ds = "0" + ds; 
		
		var targetDate1 = ds + "/" + ms + "/" + ys;
		var targetDate2 = parseSimpleDate(targetDate1, '<openmrs:datePattern />');
		//alert("targetDate=" + targetDate + ", targetDate2=" + targetDate2 + ", scheduleDate=" + $j('#scheduleDate').val());

		if(refDate.getTime()-scheduleDate.getTime() > 0) {
			alert("Schedule date cannot be null or too early!");
			$j('#scheduleDate').focus();
			return -1;
		}
		DWRLafService.followupCareScheduled(patientId, scheduleDate, careType, targetDate2,
				{
			  		callback:function(str) { 
			    		location.reload(true);
		  		    }
		        }
		);
		return 0;
	}
	
	function saveSnooze(patientId, alertId, targetDate){
		var snoozeDays = $j('#snoozeDays').val();
		var careType = alertId;
		
		var splits = targetDate.split("/");
		var ys = splits[2]; // Convert year, month and date to strings 
		var ms = splits[0];   
		var ds = splits[1];   
		if ( ms.length == 1 ) ms = "0" + ms; // Add leading zeros to month and date if required 
		if ( ds.length == 1 ) ds = "0" + ds; 
		
		var targetDate1 = ds + "/" + ms + "/" + ys;
		var targetDate2 = parseSimpleDate(targetDate1, '<openmrs:datePattern />');

		//alert("targetDate=" + targetDate + ", targetDate1=" + targetDate1 +", targetDate2=" + targetDate2 + ", scheduleDate=" + $j('#scheduleDate').val());
		
		if(snoozeDays <= 0) {
			alert("Invalid number!");
			$j('#snoozeDays').focus();
			return -1;
		} else if(snoozeDays > 31){
			alert("Snooze is not allowed for more than a month!");			
			$j('#snoozeDays').focus();
			return -1;
		} 
		
		DWRLafService.followupCareSnooze(patientId, snoozeDays, careType, targetDate2,
			{
		  		callback:function(str) { 
		    		location.reload(true);
	  		    }
		    }
		);				
		return 0;
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
			    <th>Doctor Name</th>
			    <td>
				    <input type="text" name="docNameNew" id="docNameNew"/>
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
</div>

<div id="markAsScheduledPopup">		
	<div id="addScheduleDetailDiv">
		<table border="1">
	   <c:if test="${patient.remindersCompleted == null}">
		  <thead>
			  <tr>
			  </tr>
		  </thead>
		</c:if>
		<tbody>
		  	<tr>
			    <th>Date scheduled</th>
				<td>
					 <input type="text" name="scheduleDate" id="scheduleDate" onClick="showCalendar(this)" />
			    </td>
			 </tr>
		  </tbody> 
		</table>
	</div>
</div>

<div id="markAsSnoozePopup">		
	<div id="addSnoozeDetailDiv">
		<table border="1">
	   <c:if test="${patient.remindersCompleted == null}">
		  <thead>
			  <tr>
			  </tr>
		  </thead>
		</c:if>
		<tbody>
		  	<tr>
			    <th>Remind me in </th>
				<td>
					 <input type="text" name="snoozeDays" id="snoozeDays" /> days
			    </td>
			 </tr>
		  </tbody> 
		</table>
	</div>
</div>


<div id="alertContent">
<!-- 
		<laf:forEachAlert>
			<c:if test="${varStatus.first}"><div id="alertOuterBox"><div id="alertInnerBox"></c:if>
				<div class="alert">
					<a href="#markCompleted" onClick="return markAlert(this, '${alert.id}', '${alert.dateToExpire}', '${patient.patient.patientId}','markAsCompletedPopup')" HIDEFOCUS class="markAlertRead">
						 <span class="markAlertText"><spring:message code="lancearmstrong.Alert.markAsCompleted"/></span>
					</a>
					<a href="#markScheduled" onClick="return markAlert(this, '${alert.id}', '${alert.dateToExpire.date}/${alert.dateToExpire.month+1}/${alert.dateToExpire.year+1900}', '${patient.patient.patientId}','markAsScheduledPopup')" HIDEFOCUS class="markAlertRead">
						 <span class="markAlertText"><spring:message code="lancearmstrong.Alert.markAsScheduled"/></span>
					</a>
					<a href="#markSnooze" onClick="return markAlert(this, '${alert.id}', '${alert.dateToExpire.date}/${alert.dateToExpire.month+1}/${alert.dateToExpire.year+1900}', '${patient.patient.patientId}','markAsSnoozePopup')" HIDEFOCUS class="markAlertRead">
						 <span class="markAlertText"><spring:message code="lancearmstrong.Alert.markAsSnooze"/></span>
					</a>
					Your <span id="alertText">${alert.text}</span> <c:if test="${alert.satisfiedByAny}"><i class="smallMessage">(<spring:message code="lancearmstrong.Alert.mark.satisfiedByAny"/>)</i></c:if>
				</div>
			<c:if test="${varStatus.last}">
				</div>
				<div id="alertBar">
					<img src="${pageContext.request.contextPath}/images/alert.gif" align="center" alt='<spring:message code="lancearmstrong.Alert.unreadAlert"/>' title='<spring:message code="lancearmstrong.Alert.unreadAlert"/>'/>
					<c:if test="${varStatus.count == 1}"><spring:message code="lancearmstrong.Alert.unreadAlert"/></c:if>
					<c:if test="${varStatus.count != 1}"><spring:message code="lancearmstrong.Alert.unreadAlerts" arguments="${varStatus.count}" /></c:if>
				</div>
				</div>
			</c:if>
		</laf:forEachAlert>
-->		
		  <spring:message code="lancearmstrong.Alert.mark.completed" var="hoverCompleted"/>
		  <spring:message code="lancearmstrong.Alert.mark.scheduled" var="hoverScheduled"/>
		  <spring:message code="lancearmstrong.Alert.mark.snooze" var="hoverSnooze"/>
		  <div class="alert" style="background-color: lightpink; margin-top:8px"></div>
		  <c:forEach var="alert" items="${patient.alerts}" varStatus="status">	
			   <c:if test="${status.first}"><div id="alertOuterBox"><div id="alertInnerBox"></c:if>
					<div class="alert" style="background-color: lightpink; margin-top:auto">
						Your <span id="alertText">${alert.text}</span>
						<a style="float: none;" title="${hoverCompleted}" href="#markCompleted" onClick="return markAlert(this, '${alert.id}', '${alert.dateToExpire}', '${patient.patient.patientId}','markAsCompletedPopup')" HIDEFOCUS class="markAlertRead">
							 <span class="markAlertText" style="text-decoration: underline; float: none;font-weight:bold;"><spring:message code="lancearmstrong.Alert.markAsCompleted"/></span>
						</a>
						<a style="float: none;" title="${hoverScheduled}" href="#markScheduled" onClick="return markAlert(this, '${alert.id}', '${alert.dateToExpire.date}/${alert.dateToExpire.month+1}/${alert.dateToExpire.year+1900}', '${patient.patient.patientId}','markAsScheduledPopup')" HIDEFOCUS class="markAlertRead">
							 <span class="markAlertText"  style="text-decoration: underline; float: none;font-weight:bold;"><spring:message code="lancearmstrong.Alert.markAsScheduled"/></span>
						</a>
						<a style="float: none;" title="${hoverSnooze}" href="#markSnooze" onClick="return markAlert(this, '${alert.id}', '${alert.dateToExpire.date}/${alert.dateToExpire.month+1}/${alert.dateToExpire.year+1900}', '${patient.patient.patientId}','markAsSnoozePopup')" HIDEFOCUS class="markAlertRead">
							 <span class="markAlertText"  style="text-decoration: underline; float: none;font-weight:bold;"><spring:message code="lancearmstrong.Alert.markAsSnooze"/></span>
						</a>
					</div>
	  		  	<c:if test="${status.last}">
					</div>
					<div id="alertBar">
						<img src="${pageContext.request.contextPath}/images/alert.gif" align="center" alt='<spring:message code="lancearmstrong.Alert.unreadAlert"/>' title='<spring:message code="lancearmstrong.Alert.unreadAlert"/>'/>
 					    <spring:message code="lancearmstrong.Alert.summary"/>
					</div>
					</div>
			  	</c:if>		
	 	  </c:forEach>  
</div>			
<script type="text/javascript">
		$j('#markAsCompletedPopup').dialog({
			title: 'dynamic',
			autoOpen: false,
			draggable: false,
			resizable: false,
			width: '50%',
			modal: true,
			open: function(a, b) {  }
		});	
		$j('#markAsScheduledPopup').dialog({
			title: 'dynamic',
			autoOpen: false,
			draggable: false,
			resizable: false,
			width: '50%',
			modal: true,
			open: function(a, b) {  }
		});		
		$j('#markAsSnoozePopup').dialog({
			title: 'dynamic',
			autoOpen: false,
			draggable: false,
			resizable: false,
			width: '50%',
			modal: true,
			open: function(a, b) {  }
		});				
</script>			