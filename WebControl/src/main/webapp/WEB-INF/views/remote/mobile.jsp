<%@ include file="/WEB-INF/views/include/include.jsp"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<title>Mobile Remote</title>
	<link rel="stylesheet" href="<c:url value="/css/kaku.css" />" type="text/css"/>
	<script src="<c:url value="/javascript/jquery-2.0.3.min.js" />" type="text/javascript"></script>
</head>
<body>
<script type="text/javascript">
	$(document).ready(function() {
		$(".kakuButton").click(function(event) {
			window.location.href = '<c:url value="/remote/"/>' + $(this)[0].id + '/';
		});
	});	
</script>
<table>
	<tr>
		<td><div id="dokaku" class="kakuButton" onclick="">kaku</div></td>
	</tr><tr>
		<td><div id="donewkaku" class="kakuButton">new kaku</div></td>
	</tr><tr>
		<td><div id="dodimkaku" class="kakuButton">dim kaku</div></td>
	</tr><tr>
		<td><div id="wk" class="kakuButton">Woonkamer</div></td>
	</tr>
</table>
</body>
</html>