<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<img alt="" src="<%= request.getAttribute("image") %>">
<div><%= request.getAttribute("email") %></div>
<div><%= request.getAttribute("name") %></div>
</body>
</html>