<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<% 
session.invalidate(); 
request.logout();
String redirectURL = request.getScheme()+"://"+request.getServerName()+(request.getServerPort() == 80 ? "" : ":"+request.getServerPort())+request.getContextPath()+"/";
response.sendRedirect(redirectURL);
%>

<html>
	<head>
	</head>
</html>