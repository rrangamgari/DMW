<%@page import="com.maxim.event.log.service.WebEventLogService"%>
<%
	WebEventLogService eventLog = new WebEventLogService();
	String dataSourceName = "jdbc/mppc";
	eventLog.insertLoginEvent(request, dataSourceName);
%>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport"
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<link rel="icon" type="image/png" href="/web-commons/favicon.ico">

<!-- <link rel="stylesheet" type="text/css"	href="/web-commons/extjs/ext-5.1.3/packages/ext-theme-neptune/build/resources/ext-theme-neptune-all.css">
<script type="text/javascript"	src="/web-commons/extjs/ext-5.1.3/build/ext-all-debug.js"></script>
<script type="text/javascript"	src="/web-commons/extjs/ext-5.1.3/packages/ext-theme-neptune/build/ext-theme-neptune.js"></script>
 <link rel="stylesheet" type="text/css"	href="/web-commons/extjs/ext-5.1.3/packages/ext-theme-classic/build/resources/ext-theme-classic-all.css">-->
<!-- <script type="text/javascript" src="/web-commons/extjs/ext-5.1.3/build/ext-all-debug.js"></script>
<script type="text/javascript"	src="/web-commons/extjs/ext-5.1.3/packages/ext-theme-classic/build/ext-theme-classic.js"></script>
  -->
    <script type="text/javascript" src="/web-commons/extjs/ext-5.1.3/build/examples/shared/include-ext.js"></script>
   <script type="text/javascript" src="/web-commons/extjs/ext-5.1.3/build/examples/shared/options-toolbar.js"></script>

 <style type="text/css">
 .rec-add {
background-image:url(/web-commons/extjs/ext-5.1.3/examples/shared/icons/fam/add.png);
}
.rec-remove {
background-image:url(/web-commons/extjs/ext-5.1.3/examples/shared/icons/fam/delete.gif);
}
.rec-download {
background-image:url(../images/download.png);
}
.rec-upload {
background-image:url(../images/Upload.png);
}
.rec-copy {
background-image:url(../images/copy.png);
}
.pivot-view {
background-image:url(../images/pivot.png);
}
.mass-update {
background-image:url(../images/mass-update.png);
}
.fav-dis {
background-image:url(../images/fav-dis.png);
}
.fav-ena {
background-image:url(../images/fav-ean.png);
}

.x-form-multiselect-body .x-boundlist .x-mask {
    background: none;
}

.x-form-itemselector-body .x-form-item {
    margin: 0;
}

.x-form-itemselector-top {
    background-image: url(/web-commons/extjs/ext-5.1.3/examples/ux/css/images/top2.gif);
}
.x-form-itemselector-up {
    background-image: url(/web-commons/extjs/ext-5.1.3/examples/ux/css/images/up2.gif);
}
.x-form-itemselector-add {
    background-image: url(/web-commons/extjs/ext-5.1.3/examples/ux/css/images/right2.gif);
}
.x-form-itemselector-remove {
    background-image: url(/web-commons/extjs/ext-5.1.3/examples/ux/css/images/left2.gif);
}
.x-form-itemselector-down {
    background-image: url(/web-commons/extjs/ext-5.1.3/examples/ux/css/images/down2.gif);
}
.x-form-itemselector-bottom {
    background-image: url(/web-commons/extjs/ext-5.1.3/examples/ux/css/images/bottom2.gif);
}

.red-back 
{
    background:#ffe2e2 !important;
}
.yellow-back 
{
    background:#fdffc9 !important;
}
.x-grid-filters-filtered-column {
    font-style: italic;
    font-weight: bold;
    text-decoration: underline;
    /* text-decoration: inherit;  */
    text-align:center;
   /*  background:url(../images/filter-icon.png) no-repeat !important ; */
    background: url("../images/filter-icon.png");
    background-repeat: no-repeat; 
     background-position: right center;
    color : #04408c;
}

 </style>
<script type="text/javascript" src="/DMW/secure/app/app.js"></script>
<script type="text/javascript">
function setCookie(cname, cvalue, exdays) {
    var d = new Date();
    d.setTime(d.getTime() + (exdays*24*60*60*1000));
    var expires = "expires="+ d.toUTCString();
    document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}
setCookie("theme", "<%=request.getParameter("theme")%>", 100)
</script>

<title>DMW UI</title>
</head>
<body >
</body>
</html>