<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="xmlgetter.*"%>
<%@page import="xmlgetter.data.*"%>
<%@page import="java.util.*"%>
<%
	Login login = (Login) session.getAttribute("LoginS");
	if(login == null)
	{
		response.setHeader("Location", "/XmlGetter"); 
	}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Upload IShop Order</title>
</head>
<body>
	Hello
	<%=login.toString() %><BR>
	<form action="/XmlGetter/SrvController" method="post"
		enctype="multipart/form-data">
		<input type="hidden" name="Action" value="UPLOAD_ISHOP_ORDER" /> <input
			type="file" name="xmlFile" /> <input type="submit" />
	</form>
<%
Map<String, Invoice> invoiceMap;
invoiceMap =  (Map<String, Invoice>)session.getAttribute("invoiceList");
session.setAttribute("invoiceList", null);
if(invoiceMap!=null)
{
	for(String str: invoiceMap.keySet())
	{
		%>Создана накладная №<%=str %> на основании заказа № <%=invoiceMap.get(str).getId() %><BR><% 
	}
}
String mdbFileFilePath = (String) session.getAttribute("mdbFile");
session.setAttribute("mdbFile", null);
if(mdbFileFilePath != null) {
	%>
	Создан файл <%=mdbFileFilePath%><BR><%
}
%>	
</body>
</html>