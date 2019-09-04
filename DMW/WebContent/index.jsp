<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>

</head>
<body>
	<%
		String theme = "classic";
		Cookie cookie = null;
		Cookie[] cookies = null;
		// Get an array of Cookies associated with this domain
		cookies = request.getCookies();
		if (cookies != null) {
			out.println("<h2> Found Cookies Name and Value</h2>");
			for (int i = 0; i < cookies.length; i++) {
				cookie = cookies[i];
		//		out.print("Name : " + cookie.getName() + ",  ");
		//		out.print("Value: " + cookie.getValue() + " <br/>");
				if (cookie.getName().equalsIgnoreCase("theme"))
					theme = cookie.getValue();
			}
		} else {
			//out.println("<h2>No cookies founds</h2>");
			//response.sendRedirect("secure/app.jsp?theme=classic");
		}
		response.sendRedirect("secure/app.jsp?theme="+theme);
	%>
</body>
</html>