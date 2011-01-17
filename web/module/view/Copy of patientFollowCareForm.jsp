<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<openmrs:htmlInclude file="/scripts/easyAjax.js" />

<openmrs:htmlInclude file="/scripts/jquery/jquery.min.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui.custom.min.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/css/redmond/jquery-ui.custom.css" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables.css" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/js/jquery.dataTables.min.js" />
<script type="text/javascript">
	$j(document).ready(function() {
	});


	function onChange() {
	}	
			
</script>

<div id="followup-div"  >
<spring:hasBindErrors name="reminders">
	<spring:message code="fix.error"/>
	<div class="error">
		<c:forEach items="${errors.allErrors}" var="error">
			<spring:message code="${error.code}" text="${error.code}"/><br/><!-- ${error} -->
		</c:forEach>
	</div>
	<br />
</spring:hasBindErrors>

<form method="post" id="followupForm">  
	<center>
	<table border="1">
		  <thead>
			  <tr>
			    <th>Target Dates</th>
			    <th>Recommended Care</th>
			    <th>Completed?</th>
			    <th>Dates Completed</th>
			    <th>Comments</th>
			    <th>Update</th>
			  </tr>
		  </thead>
		  <tbody>
		  <c:forEach var="reminder" items="${reminders}" varStatus="status">
	
			  <tr>
				<td>
				<spring:bind path="reminders[${status.index}].targetDate">
			        <input type="date" name="${status.expression}" value="${status.value}" id="target_date" onChange="onChange()"/>
			    </spring:bind>
			    </td>
			    <td>
				<spring:bind path="reminders[${status.index}].followProcedure">		    
			        <input type="text" name="${status.expression}" value="${status.value}" id="follow_procedure" onChange="onChange()"/>
			    </spring:bind>
			    </td>
			    <td> 
				<spring:bind path="reminders[${status.index}].responseType">
					<select name="${status.expression}" onChange="onChange()">
						<option value="Completed"
							<c:if test="'Completed' == status.value}">selected="selected"</c:if>>Completed
						</option>
						<option value="Skipped"
							<c:if test="'Skipped' == status.value}">selected="selected"</c:if>>Skipped
						</option>
			    	</select>
			    </spring:bind>		    
			    </td>
			    <td>
				<spring:bind path="reminders[${status.index}].responseDate">		    
				    <input type="date" name="${status.expression}" value="${status.value}" id="response_date" onChange="onChange()"/>
			    </spring:bind>
			    </td>
			    <td align="center">
	 				<input type="image" src="${pageContext.request.contextPath}/images/edit.gif" name="command" value="Update ${reminder.id}" onClick="onUpdate('${reminder.id}');return true;"/>
			    </td>
			  </tr> 
	 	  </c:forEach>  
		  </tbody>   
	</table>
	</center>
</form>
</div>
<%@ include file="/WEB-INF/template/footer.jsp" %>
