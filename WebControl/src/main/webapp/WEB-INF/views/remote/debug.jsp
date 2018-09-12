<%@ include file="/WEB-INF/views/include/include.jsp"%>
<html>
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>Remote test</title>
	<link rel="stylesheet" href="<c:url value="/css/jquery.mobile-1.3.2.min.css"/>">
	<link rel="stylesheet" href="<c:url value="/css/remotetest.css"/>" type="text/css">
	<script src="<c:url value="/javascript/jquery-2.0.3.min.js"/>" type="text/javascript"></script>
	<script src="<c:url value="/javascript/jquery.mobile-1.3.2.min.js"/>" type="text/javascript"></script>
	<script src="<c:url value="/javascript/jquery.formatDateTime.min.js"/>" type="text/javascript"></script>
</head>
<body>
<script type="text/javascript">

$(document).delegate("#setDateTimeDialog", "pageinit", function() {
    $("#okay").click(function(event) {
    	var dateTime = $("#datetime").val();
		$.get('<c:url value="/remote/button"/>/setDateTime_' + dateTime);
	});
    // preset current date
    $("#datetime").val($.formatDateTime('yymmddhhiiss', new Date()));
});
	
$(document).ready(function(){
    //Open a WebSocket connection.
	var wsUri = getWebSocketRootUri() + "/ez/domotica/websocket-pub";
    websocket = new WebSocket(wsUri);
   
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
    	if (ev.data == "keep-alive") {
    		//websocket.send("echo-alive");
    		return;
    	}

    	var json = $.parseJSON(ev.data);

    	var msgClass = "";
    	var msgText = json.date + " ";
    	if (json.type == "COMMON" && json.command == "DEBUG") {
    		msgClass = "debugOn";
    		msgText += "DBG";
    	}
    	else if (json.toUnitId == 0) {
    		msgClass = "received";
    		msgText += "<<<";
    	}
    	else {
    		msgClass = "sent";
    		msgText += ">>>";
    	}
    	msgText += (" " + json.message.safe);
    	
    	var msg = $('<div>').addClass("inputdata " + msgClass).text(msgText);
    	$('#input').append(msg);
    	
    	//var msgClass = "debugOn";
    	//if (ev.data.substring(13, 16) == ">>>") {
    	//	msgClass = "sent";
    	//}
    	//else if (ev.data.substring(13, 16) == "<<<") {
    	//	msgClass = "received";
    	//}
        //var msg = $('<div>').addClass("inputdata " + msgClass).text(ev.data);
    	//$('#input').append(msg);
    	
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
    
    $(".common").click(function(event) {
		$.get('<c:url value="/control/button"/>/' + $(this)[0].id);
	});
});
</script>
<div data-role="page">
	<div data-role="header">
		<h1>Remote test</h1>
	</div><!-- /header -->

	<div data-role="content">

		<table>
			<tr>
				<!-- td><a href="#" id="getDateTime" class="common" data-role="button" data-theme="b" data-inline="true">Get Date/time</a></td -->
				<td><a href="<c:url value="/status/setDateTime"/>" id="setDateTime" data-rel="dialog" data-role="button" data-theme="b" data-inline="true">Set Date/time</a></td>
				<td><a href="#" id="reboot" class="common" data-role="button" data-theme="b" data-inline="true">Reboot</a></td>
				<td><a href="#" id="reset" class="common" data-role="button" data-theme="b" data-inline="true">Reset</a></td>
			</tr>
			<tr>
				<td><a href="#" id="buzzer" class="common" data-role="button" data-theme="b" data-inline="true">Activate Buzzer</a></td>
				<td><a href="#" id="getVersion" class="common" data-role="button" data-theme="b" data-inline="true">Get Version</a></td>
				<td><a href="#" id="hello" class="common" data-role="button" data-theme="b" data-inline="true">Send Hello</a></td>				
				<td><a href="#" id="register" class="common" data-role="button" data-theme="b" data-inline="true">Register slave</a></td>
			</tr>
		</table>
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
		
	</div><!-- /content -->
	
	<div data-role="footer">
		<h4>Remote Test</h4>
	</div><!-- /footer -->
	
</div><!-- /page -->

</body>
</html>