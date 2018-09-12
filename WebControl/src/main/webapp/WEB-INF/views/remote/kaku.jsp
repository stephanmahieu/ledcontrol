<%@ include file="/WEB-INF/views/include/include.jsp"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<title>Kaku Remote</title>
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
				'<c:url value="/remote/kaku"/>/' + $(this)[0].id
			);
		});
	});	
</script>
<table>
	<tr>
		<td><div id="kaku_A_01_00_1" class="kakuButton">A1 On</div></td>
		<td><div id="kaku_A_01_00_0" class="kakuButton">A1 Off</div></td>
	</tr><tr>
		<td><div id="kaku_A_02_00_1" class="kakuButton">A2 On</div></td>
		<td><div id="kaku_A_02_00_0" class="kakuButton">A2 Off</div></td>
	</tr><tr>
		<td><div id="kaku_A_03_00_1" class="kakuButton">A3 On</div></td>
		<td><div id="kaku_A_03_00_0" class="kakuButton">A3 Off</div></td>
	</tr><tr>
		<td><div id="kaku_A_04_00_1" class="kakuButton">A4 On</div></td>
		<td><div id="kaku_A_04_00_0" class="kakuButton">A4 Off</div></td>
	</tr><tr>
		<td><div id="kaku_A_01_01_1" class="kakuButton">AG On</div></td>
		<td><div id="kaku_A_01_01_0" class="kakuButton">AG Off</div></td>
	</tr>
</table>
</body>
</html>