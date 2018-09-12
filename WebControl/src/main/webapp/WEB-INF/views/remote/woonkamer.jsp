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
	    
	    function showPercentage(id, value) {
			var perc = Math.round((Number(value) * 100) / 15);
			$("#" + id).html(perc + "%");
	    };
	    function toHex(strValue) {
	    	return Number(strValue).toString(16).toUpperCase();
	    };
	    		
		$(".dimkaku").bind("mouseup touchend", function(event) {
			var value = $(this)[0].value;
			//var hexVal = Number(value).toString(16).toUpperCase();
			showPercentage($(this)[0].id + "_val", value);
			//var perc = Math.round((Number(value) * 100) / 15);
			//$("#" + $(this)[0].id + "_val").html(perc + "%");
			$.get(
				'<c:url value="/remote/newkakudim"/>/' + $(this)[0].id + "_" + toHex(value)
			);
		});
		
		$(".dimkaku").bind("mousemove touchmove", function(event) {
			var value = $(this)[0].value;
			//var perc = Math.round((Number(value) * 100) / 15);
			//$("#" + $(this)[0].id + "_val").html(perc + "%");
			showPercentage($(this)[0].id + "_val", value);
		});
		
		$(".oldkaku").click(function(event) {
			$.get(
				'<c:url value="/remote/kaku"/>/' + $(this)[0].id
			);
		});
		
		$(".newkaku").click(function(event) {
			$.get(
				'<c:url value="/remote/newkaku"/>/' + $(this)[0].id
			);
		});
		
		showPercentage("kaku_0_val", "1");
		showPercentage("kaku_1_val", "1");
		showPercentage("kaku_2_val", "1");
		
	});	
</script>
<table>
	<tr>
		<td><div id="kaku_0_1" class="kakuButton newkaku">TV On</div></td>
		<td><input id="kaku_0" type="range" class="dimkaku" min="1" max="15" value="1"><div id="kaku_0_val" class="dimvalue"></div></td>
		<td><div id="kaku_0_0" class="kakuButton newkaku">TV Off</div></td>
	</tr><tr>
		<td><div id="kaku_1_1" class="kakuButton newkaku">Zit On</div></td>
		<td><input id="kaku_1" type="range" class="dimkaku" min="1" max="15" value="1"><div id="kaku_1_val" class="dimvalue"></div></td>
		<td><div id="kaku_1_0" class="kakuButton newkaku">Zit Off</div></td>
	</tr><tr>
		<td><div id="kaku_2_1" class="kakuButton newkaku">Eet On</div></td>
		<td><input id="kaku_2" type="range" class="dimkaku" min="1" max="15" value="1"><div id="kaku_2_val" class="dimvalue"></div></td>
		<td><div id="kaku_2_0" class="kakuButton newkaku">Eet Off</div></td>
	</tr><tr>
		<td><div id="kaku_A_01_00_1" class="kakuButton oldkaku">Vnst On</div></td>
		<td></td>
		<td><div id="kaku_A_01_00_0" class="kakuButton oldkaku">Vnst Off</div></td>
	</tr><tr>
	
</table>
</body>
</html>