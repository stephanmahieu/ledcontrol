<%@ include file="/WEB-INF/views/include/include.jsp"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<title>New Kaku Remote</title>
	<link rel="stylesheet" href="<c:url value="/css/kaku.css" />" type="text/css"/>
	<script src="<c:url value="/javascript/jquery-2.0.3.min.js" />" type="text/javascript"></script>
</head>
<body>
<script type="text/javascript">
	$(document).ready(function() {
	    $.ajaxSetup ({  
	        cache: false  
	    });  
		$(".kakuButton").click(function(event) {
			$.get(
				'<c:url value="/remote/newkaku"/>/' + $(this)[0].id
			);
		});
	});	
</script>
<table>
	<tr>
		<td><div id="kaku_0_1" class="kakuButton">1 On</div></td>
		<td><div id="kaku_0_0" class="kakuButton">1 Off</div></td>
	</tr><tr>
		<td><div id="kaku_1_1" class="kakuButton">2 On</div></td>
		<td><div id="kaku_1_0" class="kakuButton">2 Off</div></td>
	</tr><tr>
		<td><div id="kaku_2_1" class="kakuButton">3 On</div></td>
		<td><div id="kaku_2_0" class="kakuButton">3 Off</div></td>
	</tr><tr>
		<td><div id="kaku_3_1" class="kakuButton">4 On</div></td>
		<td><div id="kaku_3_0" class="kakuButton">4 Off</div></td>
	</tr><tr>
		<td><div id="kaku_0_3" class="kakuButton">G On</div></td>
		<td><div id="kaku_0_2" class="kakuButton">G Off</div></td>
	</tr>
</table>
</body>
</html>