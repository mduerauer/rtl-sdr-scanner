var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);

}

function connect() {
    $("#baseline").html("");
    updateBaseline();

    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/alerts', function (alert) {
            showAlert(alert.body);
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function updateBaseline() {
    $.ajax({
        url: "/baseline"
    }).then(function(data) {

        for(var i=0; i<data.length; i++) {
            var element = data[i];
            $("#baseline").append(
                "<tr id=\"row" + element.frequency + "\">" +
                "<td>" + element.frequency + "</td>" +
                "<td>" + element.min + "</td>" +
                "<td>" + element.max + "</td>" +
                "<td>" + element.mean + "</td>" +
                "<td>" + element.standardDeviation + "</td>" +
                "<td>" + element.count + "</td>" +
                "<td id=\"alert" + element.frequency + "\">&nbsp;</td>" +
                "</tr>"
            );
        };

    });
}

function showAlert(alert) {
    var jsonAlert = JSON.parse(alert);
    $("#row" + jsonAlert.frequency).addClass("danger");
    $("#alert" + jsonAlert.frequency).text(jsonAlert.level);
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});