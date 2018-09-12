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
<script type="text/javascript">

$(document).delegate("#setDateTimeDialog", "pageinit", function() {
    $("#okay").click(function(event) {
    	var dateTime = $("#datetime").val();
		$.get('<c:url value="/control/button"/>/setDateTime_' + dateTime);
	});
    // preset current date
    $("#datetime").val($.formatDateTime('yymmddhhiiss', new Date()));
});
	
$(document).ready(function(){

	<%--
    //Open a WebSocket connection.
	var wsUri = "ws://localhost:9292/WebControl/websocket-pub";
    var websocket = new WebSocket(wsUri);
   
    //Connected to server
    websocket.onopen = function(ev) {
        $('#input-status').text('Connected to server');
    };
   
    //Connection close
    websocket.onclose = function(ev) {
        $('#input-status').text('Disconnected from server');
    };
   
    //Message Received
    websocket.onmessage = function(ev) {
		var msgText = ev.data;

    	if (msgText == "keep-alive") {
    		//websocket.send("echo-alive");
    		return;
    	}

		var msgClass = "debugOn";
		if (msgText.substring(13, 16) == ">>>") {
			msgClass = "sent";
		}
		else if (msgText.substring(13, 16) == "<<<") {
			msgClass = "received";
		}

    	var msg = $('<div>').addClass("inputdata " + msgClass).text(msgText);
    	$('#input').append(msg);

    	if (!isTouchDevice() && isStickingToBottom()) {
    		scrollToBottom();
    	}
    };

	//Error
	websocket.onerror = function(ev) {
		$('#input-status').text(ev.data);
	};

	function getWebSocketRootUri() {
	    return "ws://" + (document.location.hostname == "" ? "localhost" : document.location.hostname) + ":9292";
	}
	
	function isTouchDevice() {
		try {
			document.createEvent("TouchEvent");
			return true;
		} catch(e) {
			return false;
		}
	}
	
    function scrollToBottom() {
    	var scrollHeight = $("#input").prop("scrollHeight");
    	//$('#input').scrollTop(scrollHeight);
    	$("#input").animate({scrollTop: scrollHeight}, 150);
    }
    
    function isStickingToBottom() {
    	var scrollHeight = $("#input").prop("scrollHeight");
    	var innerHeight = $("#input").innerHeight();
    	var scrollMaxPos = scrollHeight - innerHeight;
    	var scrollPos = $('#input').scrollTop();
    	return (scrollMaxPos - scrollPos) < 25;
    }
    --%>

	$("#dim").bind("slidestop vclick", function(event) {
		var value = $(this)[0].value;
		$.get('<c:url value="/control/dim"/>/' + value);
	});

	$("#manual-dim").bind("change", function(event) {
		if ($("#manual-dim").val() === 'on') {
			$("#dim").slider('enable');
			var value = $("#dim").val();
			$.get('<c:url value="/control/dim"/>/' + value);
		} else {
			$("#dim").slider('disable');
			$.get('<c:url value="/control/dim"/>/' + "auto");
		}
	});
	// set initial state flispwith to Automatic (off)
	$("#manual-dim").val('off').slider('refresh');

    $(".common").click(function(event) {
		$.get('<c:url value="/control/button"/>/' + $(this)[0].id);
	});

	$(".effect").click(function(event) {
		$.get('<c:url value="/control/effect"/>/' + $(this)[0].id);
	});

});
</script>
<div data-role="page">
	<div data-role="header" data-theme="a">
		<h1>LED control</h1>
	</div><!-- /header -->

	<div data-role="content">

		<table>
			<tr>
				<%--
				<td><a href="<c:url value="/control/setDateTime"/>" id="setDateTime" data-rel="dialog" data-role="button" data-theme="b" data-inline="true">Set Date/time</a></td>
				--%>
				<td><a href="#" id="status" class="common" data-role="button" data-theme="b" data-inline="true">Get Status</a></td>
				<td><a href="#" id="reset" class="common" data-role="button" data-theme="b" data-inline="true">Reset Arduino</a></td>
				<td><a href="#" id="command" class="common" data-role="button" data-theme="b" data-inline="true">Send command</a></td>
			</tr>
			<tr>
				<td>
					<div class="containing-element">
						<select name="manual-dim" id="manual-dim" data-role="slider">
							<option value="on">Brightness Manual</option>
							<option value="off">Brightness Auto</option>
						</select>
					</div>
				</td>
			</tr>
			<tr>
				<td colspan="3">
					<input type="range" name="dim" id="dim" value="50" min="1" max="255" data-highlight="true" disabled="disabled">
				</td>
			</tr>
			<%--
			<tr>
				<td><a href="#" id="getVersion" class="common" data-role="button" data-theme="b" data-inline="true">Get Version</a></td>
				<td><a href="#" id="hello" class="common" data-role="button" data-theme="b" data-inline="true">Send Hello</a></td>				
				<td><a href="#" id="register" class="common" data-role="button" data-theme="b" data-inline="true">Register slave</a></td>
			</tr>
			--%>
			<tr>
				<td><a href="#" id="0" class="effect" data-role="button" data-theme="b" data-inline="true">Blank</a></td>
				<td><a href="#" id="1" class="effect" data-role="button" data-theme="b" data-inline="true">Rainbow March</a></td>
				<td><a href="#" id="2" class="effect" data-role="button" data-theme="b" data-inline="true">Soft Twinkles</a></td>
			</tr>
			<tr>
				<td><a href="#" id="3" class="effect" data-role="button" data-theme="b" data-inline="true">Color Twinkles</a></td>
				<td><a href="#" id="4" class="effect" data-role="button" data-theme="b" data-inline="true">Dutch Flag</a></td>
				<td><a href="#" id="5" class="effect" data-role="button" data-theme="b" data-inline="true">Cylon</a></td>
			</tr>
			<tr>
				<td><a href="#" id="6" class="effect" data-role="button" data-theme="b" data-inline="true">Pride 2015 demo</a></td>
				<td><a href="#" id="7" class="effect" data-role="button" data-theme="b" data-inline="true">Disco Strobe</a></td>
				<td><a href="#" id="8" class="effect" data-role="button" data-theme="b" data-inline="true">Gradient</a></td>
			</tr>
			<tr>
				<td><a href="#" id="9" class="effect" data-role="button" data-theme="b" data-inline="true">AntiAlias bar</a></td>
				<td><a href="#" id="10" class="effect" data-role="button" data-theme="b" data-inline="true">Fireworks</a></td>
				<td><a href="#" id="11" class="effect" data-role="button" data-theme="b" data-inline="true">Running dot</a></td>
			</tr>
			<tr>
				<td><a href="#" id="12" class="effect" data-role="button" data-theme="b" data-inline="true">Ripple</a></td>
			</tr>
		</table>

		<%--
		<table style="width: 100%">
			<tr>
				<td style="width: 100%">
					<div id="input-container">
						<div id="input-header">
							<div id="input-title">Monitor</div><div id="input-status">Not connected</div>
						</div>
						<div id="input"></div>
					</div>
				</td>
				<td>
					<a href="#" id="debugOn" class="common" data-role="button" data-theme="b" data-inline="true">Debug On</a>
					<a href="#" id="debugOff" class="common" data-role="button" data-theme="b" data-inline="true">Debug Off</a>
				</td>
			</tr>
		</table>
		--%>
		
	</div><!-- /content -->
	
	<div data-role="footer">
		<h4>LED Control</h4>
	</div><!-- /footer -->
	
</div><!-- /page -->

</body>
</html>