<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>JSP Page</title>
</head>
<body>
	<h1>Hello User</h1>
	<form name="Login" action="/XmlGetter/SrvController" method="POST">
		<input type="hidden" name="Action" value="LOGIN" /> Введите имя: <input
			type="text" name="Name" value="" /> Введите пароль: <input
			type="password" name="Password" value="" /> <input type="submit"
			value="ОК" name="ButtonOk" />
	</form>
</body>
</html>
