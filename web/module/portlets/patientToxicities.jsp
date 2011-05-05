<%@ include file="/WEB-INF/view/module/personalhr/template/include.jsp" %>
<personalhr:require privilege="PHR Authenticated" otherwise="/phr/login.htm" redirect="/module/lancearmstrong/portlets/patientToxicities.htm" />

<div class="tooltip">
<spring:message code="lancearmstrong.tooltip.side.effects"/>
</div>
<div id="toxicities_div">
<dl>
<c:forEach var="sideEffect" items="${model.sideEffects}">
<dt><h3>${sideEffect.name.name}</h3></dt>
<dd>${sideEffect.description.description}</dd>
</c:forEach>
</dl> 
</div>
