<%@ page language="java" contentType="text/html; charset=ISO-8859-1" import="java.util.*"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html ng-app="webmvc">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="_csrf" content="${_csrf.token}"/>
<!-- default header name is X-CSRF-TOKEN -->
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<title>Insert title here</title>
<script type="text/javascript" src="/webmvc/static/js/jquery-1.11.2.min.js"></script>
<script type="text/javascript" src="/webmvc/static/js/angular.min.js"></script>
<script type="text/javascript" src="/webmvc/static/js/app.js"></script>
<script type="text/javascript" src="/webmvc/static/js/utils.js"></script>
<script type="text/javascript" src="/webmvc/static/js/home.js"></script>
</head>
<body>
<h1>home</h1>
<div ng-controller="HomeCtrl">
  <div>
  <% 
  HashMap<String, Object> m = (HashMap<String, Object>) request.getAttribute("dic");  
  Set<String> l = m.keySet();
  for (String k : l) { %>
  <%= k %>
  <% } %>
  </div>
  <form id=form1" novalidate ng-submit="submit()">
    <div>
      <input type="text" ng-model="model.age" placeholder="age" />
    </div>
    <div>
      <input type="text" ng-model="model.name" placeholder="name" />
    </div>
    <div>
      <button type="submit">Submit</button>
    </div>
  </form>
</div>
</body>
</html>