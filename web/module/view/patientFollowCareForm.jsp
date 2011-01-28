<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/headerMinimal.jsp" %>
	<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js" />
	<script type="text/javascript">
		$j = jQuery.noConflict();
	</script>
	<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />
	<openmrs:htmlInclude file="/dwr/engine.js" />
	<openmrs:htmlInclude file="/dwr/util.js" />
	<openmrs:htmlInclude file="/dwr/interface/DWRLafService.js" />
	<openmrs:htmlInclude file="/moduleResources/lancearmstrong/lancearmstrong.css" />
	<openmrs:htmlInclude file="/moduleResources/htmlformentry/jquery-ui-1.8.2.custom.css" />
	<openmrs:htmlInclude file="/moduleResources/htmlformentry/jquery-1.4.2.min.js" />
	<openmrs:htmlInclude file="/moduleResources/htmlformentry/jquery-ui-1.8.2.custom.min.js" />
	<openmrs:htmlInclude file="/moduleResources/lancearmstrong/autoresize.jquery.js" />
<script type="text/javascript">
	$j(document).ready(function(){
		$('textarea.expandable').autoResize({
		    // On resize:
		    onResize : function() {
		        $(this).css({opacity:0.8});
		    },
		    // After resize:
		    animateCallback : function() {
		        $(this).css({opacity:1});
		    },
		    // Quite slow animation:
		    animateDuration : 300,
		    // More extra space:
		    extraSpace : 40
		});	});


	function onAddCare(){
		if ($("#addCareDetailDiv").is(":hidden")) {
			$("#addCareDetailDiv").slideDown("fast", function(){
				$("#addCare").toggle();
				$("#cancelAddCare").toggle();
				$("#saveAddCare").toggle();
			});
		}else{
			$("#addCareDetailDiv").slideUp("fast", function(){
				$("#addCare").toggle();
				$("#cancelAddCare").toggle();
				$("#saveAddCare").toggle();
			});
		}
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
	
	function onChange(reminderId) {
		$j('#saveChanges'+reminderId).attr("disabled", false);
	}	
	
	function onUpdate(index, reminderId) {
		$j('#reminderIdField').val(index);
		$j('#saveChanges'+reminderId).attr("disabled", false);
	}

	function onDelete(index, reminderId) {
		if(confirm("Do you really want to delete this record?")) {
  		   $j('#reminderIdField').val(index);
		   return true;
		} else {
	  	   $j('#reminderIdField').val(-1);
		   return false;
		}
	}
			
</script>

<div class="tooltip">
Below is a list of recommended follow-up care. Please keep these records up-to-date by updating corresponding fields when you respond to an alert that reminds you of individual care at appropriate time.  
</div>
<div id="guideline-div">
	<div class="sub_title">Follow-up Care per Guideline</div>
	<table border="1">
		  <thead>
			  <tr>
			    <th>Target Dates</th>
			    <th>Recommended Care</th>
			  </tr>
		  </thead>
		  <tbody>
		  <c:forEach var="reminder" items="${patient.reminders}" varStatus="status">
	
			  <tr>
				<td>
			        ${reminder.targetDate.date}/${reminder.targetDate.month+1}/${reminder.targetDate.year+1900}
			    </td>
			    <td>
			        ${reminder.followProcedure.name}
			    </td>
			    <td> 

	 	  </c:forEach>  
		  </tbody>   
	</table>
</div>
<div id="followup-div"  >
<div class="sub_title">Follow-up Care Performed</div>
<spring:hasBindErrors name="patient">
	<spring:message code="fix.error"/>
	<div class="error">
		<c:forEach items="${errors.allErrors}" var="error">
			<spring:message code="${error.code}" text="${error.code}"/><br/><!-- ${error} -->
		</c:forEach>
	</div>
	<br />
</spring:hasBindErrors>

<form method="post" id="followupForm">  
	<input type="hidden" name="reminderIdField" id="reminderIdField" />
	<table border="1">
	   <c:if test="${patient.remindersCompleted != null}">
		  <thead>
			  <tr>
			    <th>Date Completed</th>
			    <th>Recommended Care</th>
			    <th>Doctor Name</th>
			    <th>Results</th>
			    <th>Comments</th>
			    <th>Action</th>
			  </tr>
		  </thead>
		</c:if>
		  <tbody>
		  <c:forEach var="reminder" items="${patient.remindersCompleted}" varStatus="status">	
			  <tr>
				<td>
					<spring:bind path="patient.remindersCompleted[${status.index}].completeDate">
						    <input type="text" name="${status.expression}" value="${status.value}" id="completeDate${reminder.id}" onClick="showCalendar(this)" onChange="onChange(${reminder.id})"/>
					</spring:bind>
			    </td>
			    <td>
					<form:select path="patient.remindersCompleted[${status.index}].followProcedureName" onchange="onChange(${reminder.id})">
						<c:forEach items="${patient.careTypes}" var="careType">
							<option value="${careType}" label="${careType}" <c:if test="${careType == reminder.followProcedure.name}">selected="selected"</c:if>/>
						</c:forEach>
			    	</form:select>
		    	</td>			    
			    <td>
			<spring:bind path="patient.remindersCompleted[${status.index}].doctorName">
				    <input type="text" name="${status.expression}" value="${status.value}" id="docname${reminder.id}" onChange="onChange(${reminder.id})"/>
			</spring:bind>
			    </td>
			    <td>
					<form:select path="patient.remindersCompleted[${status.index}].responseType"  onchange="onChange(${reminder.id})">
						<c:forEach items="${patient.responseTypes}" var="responseType">
							<option value="${responseType}" label="${responseType}" <c:if test="${responseType == reminder.responseType}">selected="selected"</c:if>/>
						</c:forEach>
			    	</form:select>
			    </td>
			    <td>
			<spring:bind path="patient.remindersCompleted[${status.index}].responseComments">
				<textarea name="${status.expression}" id="comments${reminder.id}" onChange="onChange(${reminder.id})" rows="1">${status.value}</textarea>
			</spring:bind>
			    </td>
			    <td align="center">
					<input type="submit" value="<spring:message code="general.save" />" name="command" id="saveChanges${reminder.id}" onClick="onUpdate(${status.index}, ${reminder.id});return true;" disabled="true"/>
					<input type="submit" value="<spring:message code="general.delete" />" name="command" id="deleteChanges${reminder.id}" onClick="onDelete(${status.index}, ${reminder.id});return true;"/>
			    </td>
			  </tr> 
	 	  </c:forEach>  
		  </tbody>   
	</table>
</form>

	<div id="addCareDetailDiv">
		<table border="1">
	   <c:if test="${patient.remindersCompleted == null}">
		  <thead>
			  <tr>
			    <th>Date Completed</th>
			    <th>Recommended Care</th>
			    <th>Doctor Name</th>
			    <th>Results</th>
			    <th>Comments</th>
			  </tr>
		  </thead>
		</c:if>
		<tbody>
		  	<tr>
				<td>
					 <input type="text" name="completeDateNew" id="completeDateNew" onClick="showCalendar(this)" />
			    </td>
			    <td>
					<select name="careTypeNew" id="careTypeNew">
						<c:forEach items="${patient.careTypes}" var="careType">
							<option value="${careType}" label="${careType}"/>
						</c:forEach>
					</select>
		    	</td>			    
			    <td>
				    <input type="text" name="docNameNew" id="docNameNew"/>
			    </td>
			    <td>
					<select name="resultTypeNew" id="resultTypeNew">
						<c:forEach items="${patient.responseTypes}" var="responseType">
							<option value="${responseType}" label="${responseType}"/>
						</c:forEach>
			    	</select>
			    </td>
			    <td>
				    <textarea name="commentsNew" id="commentsNew" rows="1">${status.value}</textarea>
			    </td>
			</tr>
		  </tbody> 
		</table>
	</div>
	<div id="addCareDiv">
		<button id="addCare" onClick="onAddCare();return false;">Add Followup Care Performed</button>
		<button id="saveAddCare" onClick="saveAddedCare(${patient.patient.patientId});return false;">Save</button>
		<button id="cancelAddCare" onClick="onAddCare();return false;">Cancel</button>
	</div>	
</div>
<%@ include file="/WEB-INF/template/footerMinimal.jsp" %>
