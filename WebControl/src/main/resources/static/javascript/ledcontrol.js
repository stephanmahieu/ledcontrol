const WEBSOCKET_URI = "ws://" + (document.location.hostname === "" ? "localhost" : document.location.hostname) + ":9292/websocket-pub";
let websocket;

let isDebugOn = true;

$(document).ready(function(){

    $("#dim").on("slidestop vclick blur", dimChangeHandler);

    $("#manual-dim")
        .prop('checked', false).flipswitch('refresh')
        .on("change", dimSwitchChangeHandler);

    $(".effect").click(effectChangeHandler);

    // status / reset
    $(".common").click(function(event) {
        restAPI(event.currentTarget.id);
    });

    $("#command").click(function(event) {
        restAPI('command/Hello-world!');
    });

    $("#debugOn")
        .prop('checked', true).checkboxradio("refresh")
        .click(debugChangeHandler);
    $("#debugOff")
        .prop('checked', false).checkboxradio("refresh")
        .click(debugChangeHandler);

    $("#clear").click(function(event) {
        $('#input').text("");
    });

    $("#connect").click(function(event) {
        if (!$('#connect').hasClass('ui-state-disabled')) {
            reconnectWebSocket();
        }
    });

    $('#uploadform').on('submit', uploadHandler);

    let themeInputs = $('#theme-selector input');
    themeInputs.prop('checked',false);
    $('#b').prop('checked',true);
    themeInputs.checkboxradio("refresh").on('change', themeSelectHandler);

    // Open a WebSocket connection.
    connectWebSocket();

});

function themeSelectHandler() {
    let themeClass = $('#theme-selector input:checked').attr('id');
    $('#ledcontrolpage')
        .removeClass('ui-page-theme-a ui-page-theme-b ui-page-theme-c')
        .addClass('ui-page-theme-' + themeClass);
}

function restAPI(path) {
    restAPIWithCallback(path, null);
}

function restAPIWithCallback(path, successCallback) {
    $.ajax({
        url: '/rest/api/' + path,
        contentType: 'text/plain',
        timeout: 10000,
        success: successCallback
    });
}

function dimSwitchChangeHandler() {
    let dimObj = $("#dim");
    if ($("#manual-dim").prop('checked')) {
        dimObj.slider('enable');
        let value = dimObj.val();
        restAPI('dim/' + value);
    } else {
        dimObj.slider('disable');
        restAPI('dim/' + 'auto');
    }
}

function dimChangeHandler() {
    let value = $("#dim").val();
    restAPI('dim/' + value);
}

function effectChangeHandler(event) {
    let effectId = event.currentTarget.id;
    restAPIWithCallback('effect/' + effectId, function() {
        $(".current-effect").removeClass("current-effect");
        event.currentTarget.classList.add("current-effect");
    });
}

function debugChangeHandler(event) {
    let debugId = event.currentTarget.id;
    if ('debugOn' === debugId) {
        restAPI('debug/true');
        websocket.debug = true;
        isDebugOn = true;
    } else {
        restAPI('debug/false');
        websocket.debug = false;
        isDebugOn = false;
    }
}

function connectWebSocket() {
    // Open a WebSocket connection.
    websocket = new ReconnectingWebSocket(WEBSOCKET_URI, null, {
        debug: false,
        reconnectInterval: 3000,
        maxReconnectInterval: 5 * 60 * 1000,
        reconnectDecay: 1.5,
        timeoutInterval: 5000,
        maxReconnectAttempts: 50
    });

    // Connected to server
    websocket.onopen = function(ev) {
        $('#input-status').text('Connected to server');
        $('#connect').addClass('ui-state-disabled');
    };

    // Connection close
    websocket.onclose = function(ev) {
        $('#input-status').text('Disconnected from server');
        $('#connect').removeClass('ui-state-disabled');
    };

    // Connecting
    websocket.onconnecting = function(ev) {
        // display status briefly
        $('#input-connect-status').text('connecting... ');
        setTimeout(function() {
            $('#input-connect-status').text('');
          }, 2500
        );
    };

    // Error
    websocket.onerror = function(ev) {
        $('#input-status').text(ev.data);
    };

    websocket.onmessage = websocketMessageHandler;
}

function reconnectWebSocket() {
    websocket.open();
}

function websocketMessageHandler(ev) {
    // Message Received

    if (ev.data === "keep-alive" && !isDebugOn) {
        // websocket.send("echo-alive");
        return;
    }

    let msgClass = "";
    let msgData = ev.data;
    if (ev.data.match("^INT:")) {
        msgClass = "debug";
    } else if (ev.data.match("^INJ:")) {
        msgClass = "received";
    } else if (ev.data.match("^OUT:")) {
        msgClass = "sent";
    } else if (ev.data.match("^STDOUT:")) {
        msgClass = "stdout";
        msgData = msgData.replace('STDOUT: ', '');
    } else if (ev.data.match("^STDERR:")) {
        msgClass = "stderr";
        msgData = msgData.replace('STDERR: ', '');
    }

    let msg = $('<pre>').addClass("inputdata " + msgClass).text(msgData);
    $('#input').append(msg);

    if (isStatusUpdateResponse(ev.data)) {
        initControlsWithStatusInfo(ev.data);
    }

    scrollToBottom();
}

function isStatusUpdateResponse(data) {
    let indexAcc = data.indexOf('{');
    if (indexAcc >= 0) {
        let jsonData = data.substring(indexAcc);
        let json = $.parseJSON(jsonData);
        if ('STATUS' === json.type && json.status) {
            return true;
        }
    }
    return false
}

function initControlsWithStatusInfo(statusData) {
    // use received status (json) to update the controls to reflect the current state
    let dimObj = $("#dim");
    let dimSwitchObj = $("#manual-dim");

    let indexAcc = statusData.indexOf('{');
    let jsonData = statusData.substring(indexAcc);
    let json = $.parseJSON(jsonData);
    let status = json.status;

    if (status.effect) {
        let effectNo = effectToInt(json.status.effect);
        $(".current-effect").removeClass("current-effect");
        $("#" + effectNo).addClass("current-effect");
    }
    if (status.brightness) {
        if (dimObj.val() !== status.brightness) {
            // prevent triggering the changehandler and send a dim command!
            dimObj
                .off('slidestop vclick blur')
                .val(status.brightness).slider("refresh")
                .on('slidestop vclick blur', dimChangeHandler);
        }
    }
    if (status.autoBrightness === false) {
        // manual == on == checked
        if (!dimSwitchObj.prop('checked')) {
            // prevent triggering the changehandler and send a command!
            dimSwitchObj
                .off("change")
                .prop('checked',true).flipswitch('refresh')
                .on("change", dimSwitchChangeHandler);
        }
        dimObj.slider('enable');
    } else {
        // automatic == off
        if (dimSwitchObj.prop('checked')) {
            // prevent triggering the changehandler and send a command!
            dimSwitchObj
                .off("change")
                .prop('checked',false).flipswitch('refresh')
                .on("change", dimSwitchChangeHandler);
        }
        dimObj.slider('disable');
    }
    if (status.debugOn === false || status.debugOn === true) {
        let isDebugStatus = (status.debugOn === true);
        $("#debugOn")
            .off('click')
            .prop('checked',isDebugStatus).checkboxradio("refresh")
            .on('click', debugChangeHandler);
        $("#debugOff")
            .off('click')
            .prop('checked',!isDebugStatus).checkboxradio("refresh")
            .on('click', debugChangeHandler);
        websocket.debug = isDebugStatus;
        isDebugOn = isDebugStatus;
    }
}

function scrollToBottom() {
    let inputObj = $("#input");
    inputObj.stop().animate({"scrollTop": inputObj.prop("scrollHeight")}, 500);
}

function effectToInt(name) {
    switch (name) {
        case "Blank": return 0;
        case "RainbowMarch": return 1;
        case "SoftTwinkles": return 2;
        case "ColorTwinkles": return 3;
        case "DutchFlag": return 4;
        case "Cylon": return 5;
        case "Pride2015demo": return 6;
        case "DiscoStrobe": return 7;
        case "Gradient": return 8;
        case "AntiAlias": return 9;
        case "Fireworks": return 10;
        case "RunningDot": return 11;
        case "Ripple": return 12;
        default: return 1;
    }
}

function uploadHandler(event) {
    // use a js handler to prevent page reload after submit
    let device = $('#device').val();
    let comport = $('#comport').val();

    let doTest = $("#test-sketch").prop('checked');

    $.ajax( {
        url: '/rest/api/upload/' + escapeForwardSlash(comport) + '/' + device + '?doTest=' + (doTest?'True':'False'),
        type: 'POST',
        timeout: 30000,
        data: new FormData(this),
        processData: false,
        contentType: false,
        success: function(result){
            console.log(result);
        }
    } );
    event.preventDefault();
}

function escapeForwardSlash(str) {
    return str.replace(/\//g, '_');
}