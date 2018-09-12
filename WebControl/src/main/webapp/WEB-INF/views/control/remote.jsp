<%@ include file="/WEB-INF/views/include/include.jsp"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<title>Remote</title>
	<link rel="stylesheet" href="<c:url value="/css/webcontrol.css" />" type="text/css"/>
	<link rel="stylesheet" href="<c:url value="/css/remote.css" />" type="text/css"/>
	<script src="<c:url value="/javascript/jquery-2.0.3.min.js" />" type="text/javascript"></script>
</head>
<body>
<script type="text/javascript">
	$(document).ready(function() {
	    $.ajaxSetup ({
	        cache: false
	    });

	    $(".common").click(function(event) {
			$.get(
				'<c:url value="/remote/button"/>/' + $(this)[0].id
			);
		});

	});	
</script>
<div id="logo"></div>
<div class="container">
	<h1>
		Test Arduino Led Control
	</h1>

	<div id="brightness" class="remoteButton common">Set brightness</div>
	<div id="status" class="remoteButton common">Get Status</div>
	<br/>
	<br/>
	<br/>


</div>
<jsp:include page="/WEB-INF/views/include/footer.jsp" flush="false"/>
</body>
</html>