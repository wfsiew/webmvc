<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    import="app.models.*"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<% GoogleProfile g = (GoogleProfile) request.getAttribute("profile"); %>
<img alt="" src="<%= g.image.url %>">
<ul>
<% for (GoogleEmail x : g.emails) { %>
<li><%= x.value %>
<% } %>
</ul>
<h1><%= g.displayName %></h1>
</body>
</html>