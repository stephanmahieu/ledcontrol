let websocket;

$(document).ready(function(){
    let dimObj = $("#dim");
    let dimSwitchObj = $("#manual-dim");
    let statusObj = $('#input-status');

    dimObj.on("slidestop vclick blur", dimChangeHandler);

    // set initial state flipswitch to off
    dimSwitchObj.prop('checked', false).flipswitch('refresh');

    dimSwitchObj.on("change", dimSwitchChangeHandler);

    $(".effect").click(effectChangeHandler);

    // status / reset
    $(".common").click(function(event) {
        $.get("/rest/api/" + event.currentTarget.id);
    });

    $("#command").click(function(event) {
        $.get("/rest/api/command/" + 'Hello-world!');
    });

    $("#debugOn").click(function(event) {
        $.get("/rest/api/debug/true");
    });

    $("#debugOff").click(function(event) {
        $.get("/rest/api/debug/false");
    });

    $("#clear").click(function(event) {
        $('#input').text("");
    });

    $('#uploadform').on('submit', uploadHandler);

    // Open a WebSocket connection.
    let wsUri = getWebSocketRootUri() + "/websocket-pub";
    websocket = new WebSocket(wsUri);

    // Connected to server
    websocket.onopen = function(ev) {
        statusObj.text('Connected to server');
    };

    // Connection close
    websocket.onclose = function(ev) {
        statusObj.text('Disconnected from server');
    };

    // Error
    websocket.onerror = function(ev) {
        statusObj.text(ev.data);
    };

    // Message Received
    websocket.onmessage = function(ev) {
        if (ev.data === "keep-alive") {
            //websocket.send("echo-alive");
            return;
        }

        let msgClass = "";
        if (ev.data.match("^INT:")) {
            msgClass = "debug";
        } else if (ev.data.match("^INJ:")) {
            msgClass = "received";
        } else if (ev.data.match("^OUT:")) {
            msgClass = "sent";
        }

        let msg = $('<div>').addClass("inputdata " + msgClass).text(ev.data);
        $('#input').append(msg);

        // use received status (json) to update the controls to reflect the current state
        let indexAcc = ev.data.indexOf('{');
        if (indexAcc >= 0) {
            let jsonData = ev.data.substring(indexAcc);
            let json = $.parseJSON(jsonData);
            if ('STATUS' === json.type && json.status) {
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
                    // manual == on
                    if (!dimSwitchObj.is(':checked')) {
                        // prevent triggering the changehandler and send a command!
                        dimSwitchObj
                            .off("change")
                            .prop('checked',true).flipswitch('refresh')
                            .on("change", dimSwitchChangeHandler);
                    }
                    dimObj.slider('enable');
                } else {
                    // automatic == off
                    if (dimSwitchObj.is(':checked')) {
                        // prevent triggering the changehandler and send a command!
                        dimSwitchObj
                            .off("change")
                            .prop('checked',false).flipswitch('refresh')
                            .on("change", dimSwitchChangeHandler);
                    }
                    dimObj.slider('disable');
                }
            }
        }

        if (!isTouchDevice() && isStickingToBottom()) {
            scrollToBottom();
        }
    };
});

function dimSwitchChangeHandler() {
    let dimObj = $("#dim");
    if ( $("#manual-dim").is(':checked')) {
        dimObj.slider('enable');
        let value = dimObj.val();
        $.get('/rest/api/dim/' + value);
    } else {
        dimObj.slider('disable');
        $.get('/rest/api/dim/' + "auto");
    }
}

function dimChangeHandler() {
    let value = $("#dim").val();
    $.get('/rest/api/dim/' + value);
}

function effectChangeHandler(event) {
    let effectId = event.currentTarget.id;
    $.get('/rest/api/effect/' + effectId);
    $(".current-effect").removeClass("current-effect");
    event.currentTarget.classList.add("current-effect");
}

function getWebSocketRootUri() {
    return "ws://" + (document.location.hostname === "" ? "localhost" : document.location.hostname) + ":9292";
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
    let inputObj = $("#input");
    let scrollHeight = inputObj.prop("scrollHeight");
    //$('#input').scrollTop(scrollHeight);
    inputObj.animate({scrollTop: scrollHeight}, 150);
}

function isStickingToBottom() {
    let inputObj =  $("#input");
    let scrollHeight = inputObj.prop("scrollHeight");
    let innerHeight = inputObj.innerHeight();
    let scrollMaxPos = scrollHeight - innerHeight;
    let scrollPos = inputObj.scrollTop();
    return (scrollMaxPos - scrollPos) < 25;
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

    $.ajax( {
        url: '/rest/api/upload/' + escapeForwardSlash(comport) + '/' + device,
        type: 'POST',
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