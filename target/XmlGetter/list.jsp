<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="xmlgetter.*"%>
<%@page import="java.util.*"%>

<%
	Login login = (Login) session.getAttribute("LoginS");
	if(login == null)
	{
		response.setHeader("Location", "/XmlGetter"); 
	}
	Set<UserActions> actions = login.getActionSet();
	StringBuilder sb = new StringBuilder();
	for(UserActions act: actions)
	{
		sb.append("<a href=\"/XmlGetter").append(act.getPage()).append("\">").append(act.getAClass()).append("</a><BR>");
	}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>List of Actions</title>
</head>
<body>
	Hello
	<%=login.toString() %><BR>
	<%=sb.toString() %>
</body>
</html>