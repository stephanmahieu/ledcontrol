<%@ include file="/WEB-INF/views/include/include.jsp"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<title>New Kaku Remote dimmer</title>
	<link rel="stylesheet" href="<c:url value="/css/kaku.css" />" type="text/css"/>
	<script src="<c:url value="/javascript/jquery-2.0.3.min.js" />" type="text/javascript"></script>
</head>
<body>
<script type="text/javascript">
	$(document).ready(function() {
	    $.ajaxSetup ({  
	        cache: false  
	    });  
		$(".dim").click(function(event) {
			$.get(
				'<c:url value="/remote/newkakudim"/>/' + $(this)[0].id
			);
		});
		$(".off").click(function(event) {
			$.get(
				'<c:url value="/remote/newkaku"/>/' + $(this)[0].id
			);
		});
	});	
</script>
<table>
	<tr>
		<td><div id="kaku_0_0" class="kakuButton off">1 Off</div></td>
		<td><div id="kaku_0_1" class="kakuButton dim">1 Dim 1</div></td>
		<td><div id="kaku_0_4" class="kakuButton dim">1 Dim 4</div></td>
		<td><div id="kaku_0_8" class="kakuButton dim">1 Dim 8</div></td>
		<td><div id="kaku_0_B" class="kakuButton dim">1 Dim B</div></td>
		<td><div id="kaku_0_F" class="kakuButton dim">1 Dim F</div></td>
	</tr><tr>
		<td><div id="kaku_1_0" class="kakuButton off">2 Off</div></td>
		<td><div id="kaku_1_1" class="kakuButton dim">2 Dim 1</div></td>
		<td><div id="kaku_1_4" class="kakuButton dim">2 Dim 4</div></td>
		<td><div id="kaku_1_8" class="kakuButton dim">2 Dim 8</div></td>
		<td><div id="kaku_1_B" class="kakuButton dim">2 Dim B</div></td>
		<td><div id="kaku_1_F" class="kakuButton dim">2 Dim F</div></td>
	</tr><tr>
		<td><div id="kaku_2_0" class="kakuButton off">3 Off</div></td>
		<td><div id="kaku_2_1" class="kakuButton dim">3 Dim 1</div></td>
		<td><div id="kaku_2_4" class="kakuButton dim">3 Dim 4</div></td>
		<td><div id="kaku_2_8" class="kakuButton dim">3 Dim 8</div></td>
		<td><div id="kaku_2_B" class="kakuButton dim">3 Dim B</div></td>
		<td><div id="kaku_2_F" class="kakuButton dim">3 Dim F</div></td>
	</tr>
</table>
</body>
</html>