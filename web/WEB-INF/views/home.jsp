<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html ng-app="webmvc">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<script type="text/javascript" src="/webmvc/static/js/jquery-1.11.2.min.js"></script>
<script type="text/javascript" src="/webmvc/static/js/angular.min.js"></script>
<script type="text/javascript" src="/webmvc/static/js/app.js"></script>
<script type="text/javascript" src="/webmvc/static/js/home.js"></script>
</head>
<body>
<h1>home</h1>
<div ng-controller="HomeCtrl">
  <form id=form1" novalidate ng-submit="submit()">
    <div>
      <input type="text" ng-model="model.id" placeholder="id" />
    </div>
    <div>
      <input type="text" ng-model="model.useremail" placeholder="user email" />
    </div>
    <div>
      <button type="submit">Submit</button>
    </div>
  </form>
</div>
</body>
</html>