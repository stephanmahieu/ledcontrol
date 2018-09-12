<%@ include file="/WEB-INF/views/include/include.jsp"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<title><fmt:message key="welcome.title"/></title>
	<link rel="stylesheet" href="<c:url value="/css/webcontrol.css" />" type="text/css"/>
</head>
<body>
<div id="logo"></div>
<div class="container">
	<h1>
		<fmt:message key="welcome.title"/>
	</h1>
	<ul id="mainmenu">
		<li><a href="remote">Remote 1</a></li>
		<li><a href="remote/m">Remote 2</a></li>
		<li><a href="rpicontroller">RPi Controller (upload XML)</a></li>
		<li><a href="messageStore">Berichten</a></li>
		<li><a href="status">Status</a></li>
	</ul>
</div>
<jsp:include page="/WEB-INF/views/include/footer.jsp" flush="false"/>
</body>
</html>