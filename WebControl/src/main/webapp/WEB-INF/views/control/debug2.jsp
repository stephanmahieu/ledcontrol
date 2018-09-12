<%@ include file="/WEB-INF/views/include/include.jsp"%>
<html lang="en">
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>LED Control</title>
	<link rel="stylesheet" href="<c:url value="/css/jquery.mobile-1.4.5.min.css"/>">
	<link rel="stylesheet" href="<c:url value="/css/remotetest.css"/>" type="text/css">
	<script src="<c:url value="/javascript/jquery-2.1.4.min.js"/>" type="text/javascript"></script>
	<script src="<c:url value="/javascript/jquery.mobile-1.4.5.min.js"/>" type="text/javascript"></script>
	<script src="<c:url value="/javascript/jquery.formatDateTime.min.js"/>" type="text/javascript"></script>
</head>
<body>

<div data-role="page" id="page1">
	<div data-role="header">
		<h1>jQuery Mobile Example</h1>
	</div>


	<div data-role="rangeslider">
		<label for="range-1a">Rangeslider:</label>
		<input name="range-1a" id="range-1a" min="0" max="100" value="0" type="range">
		<label for="range-1b">Rangeslider:</label>
		<input name="range-1b" id="range-1b" min="0" max="100" value="100" type="range">
	</div>

		<input type="range" name="dim" id="dim" value="127" min="1" max="255" data-highlight="true">

	<div class="ui-field-contain">
		<label for="slider-2">Brightness::</label>
		<input type="range" name="slider-2" id="slider-2" value="25" min="1" max="100" data-highlight="true">
	</div>

	<div data-role="rangeslider" data-theme="a" data-highlight="true">
		<input name="range-dim" id="range-dim" type="range" min="1" max="255" value="127" data-theme="a" data-highlight="true">
	</div>


	<div data-role="footer">
		<h1>LED Control</h1>
	</div>
</div>

</body>
</html>