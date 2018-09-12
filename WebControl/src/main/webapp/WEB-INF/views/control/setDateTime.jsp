<%@ include file="/WEB-INF/views/include/include.jsp"%>
<html>
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>Set Date Time</title>
	<link rel="stylesheet" href="<c:url value="/css/jquery.mobile-1.3.2.min.css"/>">
	<link rel="stylesheet" href="<c:url value="/css/remotetest.css"/>" type="text/css">
	<script src="<c:url value="/javascript/jquery-2.0.3.min.js"/>" type="text/javascript"></script>
	<script src="<c:url value="/javascript/jquery.mobile-1.3.2.min.js"/>" type="text/javascript"></script>
</head>
<body>
<div data-role="dialog" id="setDateTimeDialog">
	<div data-role="header" data-theme="d">
		<h1>Enter Date and Time</h1>
	</div>

	<div data-role="content">
		<p>Enter the date and time.</p>
		<div data-role="fieldcontain">
			<label for="datetime">Date/Time:</label>
			<input type="text" name="datetime" id="datetime" value="" placeholder="Date and time" />
		</div>	
		<a href="<c:url value="/status/debugOn"/>" data-role="button" data-rel="back" data-theme="b" data-inline="true" id="okay">Okay</a>
		<a href="<c:url value="/status/debugOn"/>" data-role="button" data-rel="back" data-theme="c" data-inline="true">Cancel</a>
	</div><!-- /content -->
	
</div><!-- /dialog -->
</body>
</html>