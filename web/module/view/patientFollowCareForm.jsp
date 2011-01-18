<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/headerMinimal.jsp" %>
	<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js" />
	<script type="text/javascript">
		$j = jQuery.noConflict();
	</script>
	<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />
	<openmrs:htmlInclude file="/dwr/engine.js" />
	<openmrs:htmlInclude file="/dwr/util.js" />
	<openmrs:htmlInclude file="/dwr/interface/DWRHtmlFormEntryService.js" />
	<openmrs:htmlInclude file="/moduleResources/htmlformentry/htmlFormEntry.js" />
	<openmrs:htmlInclude file="/moduleResources/htmlformentry/htmlFormEntry.css" />
	<openmrs:htmlInclude file="/moduleResources/htmlformentry/jquery-ui-1.8.2.custom.css" />
	<openmrs:htmlInclude file="/moduleResources/htmlformentry/jquery-1.4.2.min.js" />
	<openmrs:htmlInclude file="/moduleResources/htmlformentry/jquery-ui-1.8.2.custom.min.js" />
<script type="text/javascript">
	$j(document).ready(function() {
	});


	function onChange(reminderId) {
		$j('#saveChanges'+reminderId).attr("disabled", false);
	}	
	
	function onUpdate(index, reminderId) {
		$j('#reminderIdField').val(index);
		$j('#saveChanges'+reminderId).attr("disabled", false);
	}	
			
</script>

<div class="tooltip">
Below is a list of recommended follow-up care. Please keep these records up-to-date by updating corresponding fields when you respond to an alert that reminds you of individual care at appropriate time.  
</div>

<div id="followup-div"  >
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
	<center>
	<table border="1">
		  <thead>
			  <tr>
			    <th>Target Dates</th>
			    <th>Recommended Care</th>
			    <th>Completed?</th>
			    <th>Dates Completed</th>
			    <th>Comments</th>
			    <th>Action</th>
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
			<spring:bind path="patient.reminders[${status.index}].responseType">
					<select name="${status.expression}" onChange="onChange(${reminder.id})">
					    <option value="Select one">Select one</option>
						<option value="Completed"
							<c:if test="${'Completed' == status.value}">selected="selected"</c:if>>Completed
						</option>
						<option value="Skipped"
							<c:if test="${'Skipped' == status.value}">selected="selected"</c:if>>Skipped
						</option>
			    	</select>
			</spring:bind>
			    </td>
			    <td>
			<spring:bind path="patient.reminders[${status.index}].completeDate">
				    <input type="date" name="${status.expression}" value="${status.value}" id="completeDate${reminder.id}" onClick="showCalendar(this)" onChange="onChange(${reminder.id})"/>
			</spring:bind>
			    </td>
			    <td>
			<spring:bind path="patient.reminders[${status.index}].responseComments">
				    <input type="text" name="${status.expression}" value="${status.value}" id="comments${reminder.id}" onChange="onChange(${reminder.id})"/>
			</spring:bind>
			    </td>
			    <td align="center">
					<input type="submit" value="<spring:message code="general.save" />" name="command" id="saveChanges${reminder.id}" onClick="onUpdate(${status.index}, ${reminder.id});return true;" disabled="true"/>
			    </td>
			  </tr> 
	 	  </c:forEach>  
		  </tbody>   
	</table>
	</center>
</form>
</div>
<%@ include file="/WEB-INF/template/footerMinimal.jsp" %>
