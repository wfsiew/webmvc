<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html>
<html>
<head>
<title>Login Page</title>
</head>
<body>

	<div id="login-box">

		<c:if test="${not empty error}">
			<div class="error">${error}</div>
		</c:if>
		<c:if test="${not empty msg}">
			<div class="msg">${msg}</div>
		</c:if>

		<form id="loginForm" name='loginForm'
			action="<c:url value='/j_spring_security_check' />" method='POST'>

			<table>
				<tr>
					<td>User:</td>
					<td><input type='text' name='username' value="<%= request.getAttribute("googleEmail") %>" /></td>
				</tr>
				<tr>
					<td>Password:</td>
					<td><input type='password' name='password' value="###" /></td>
				</tr>
				<tr>
					<td colspan='2'>
            <input name="submit" type="submit"
						       value="submit" />
          </td>
				</tr>
			</table>

			<input type="hidden" name="${_csrf.parameterName}"
				value="${_csrf.token}" />

		</form>
	</div>
  <script type="text/javascript">
  function submit() {
	  document.loginForm.submit();
  }

  submit();
  </script>
</body>
</html>