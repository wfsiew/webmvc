<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<html>
<body>
	<h1>Title : ${title}</h1>
	<h1>Message : ${message}</h1>

	<c:url value="/j_spring_security_logout" var="logoutUrl" />
	<form action="${logoutUrl}" method="post" id="logoutForm">
		<input type="hidden" name="${_csrf.parameterName}"
			value="${_csrf.token}" />
	</form>
	<script>
		function formSubmit() {
			document.getElementById("logoutForm").submit();
		}
	</script>

  <% if (request.getUserPrincipal() != null &&
         request.getUserPrincipal().getName() != null) { %>
		<h2>
			Welcome : <%= request.getUserPrincipal().getName() %> | <a
				href="javascript:formSubmit()"> Logout</a>
		</h2>
	<% } %>

</body>
</html>