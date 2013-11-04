var sessionKey;
var sessionId;
var chatMessageTemplate;
var myNickName;
function tryLogin(loginId, password) {
	if (!loginId) {
		$("#userArea").addClass("has-error");
		return;
	}
	$.getJSON("/login", {
		nickName : loginId,
		password : password
	}, function(sessionData) {
		$("#userArea").removeClass("has-error");
		$("#passwordArea").removeClass("has-error");
		myNickName = sessionData.nickName;
		sessionId = sessionData.sessionId;
		sessionKey = sessionData.sessionKey;
		initChatPane(myNickName);
	}).error(function(data) {
		$("#userArea").addClass("has-error");
		if (data.password) {
			$("#passwordArea").addClass("has-error");
		}
	});
}
function initChatPane(nickName) {
	renderExternalTemplate("#content", "/resource/templates/chatPane.html", {}, function() {
		addChannel("home");
	});
	$(window).bind("beforeunload", function() {
		return "Do you leave from chat?";
	});
	continueListen();
}
function continueListen() {
	$.post("/listenEvent", {
		sessionId : sessionId,
		sessionKey : sessionKey
	}, function(data) {
		for ( var i in data) {
			var event = data[i];
			if ("user.part" == event.type) {
				$("#channelPane_" + event.channelName + "userArea_" + event.userNickName).remove();
			} else if ("user.join" == event.type) {
				// avoid duplicate
				$("#channelPane_" + event.channelName + "userArea_" + event.userNickName).remove();
				$("#channelPane_nameArea_" + event.channelName).prepend("<div id=\"channelPane_" + event.channelName + "userArea_" + event.userNickName + "\">" + event.userNickName + "</div>");
			} else if ("message" == event.type) {
				appendMessageToChannelPane(event.channelName.substring(1), event.createdAt, event.userNickName, event.message);
			} else if ("reload" == event.type) {
				document.location.href = event.url;
			}
		}
	}, "json").done(function(data) {
		continueListen();
	}).fail(function(data) {
		setTimeout(function() {
			continueListen()
		}, 30000);
	});
}
function appendMessageToChannelPane(channelName, createdAt, senderNickName, message) {
	var messageObjforPaint = new Object();
	messageObjforPaint.createdAt = createdAt;
	messageObjforPaint.senderNickName = senderNickName;
	messageObjforPaint.message = message;
	$("#channelPane_messageArea_" + channelName).prepend(chatMessageTemplate.render(messageObjforPaint));
}
function addChannel(channelName) {
	if (0 == channelName.length || 0 <= channelName.indexOf("#")) {
		$("#channelAddArea").addClass("has-error");
		return;
	}
	$.post("/channel/join", {
		channelName : "#" + channelName,
		sessionId : sessionId,
		sessionKey : sessionKey
	}, function(jsonSrc) {
		$("#channelAddInput").val("");
		loadTemplate("/resource/templates/channelPane.html", function(template) {
			var channelPane = template.render({
				channelName : channelName
			});
			$("#tabContentChannelPlus").before(channelPane);
			$("#tabChannelPlus").before("<li id=\"tabChannel_" + channelName + "\" class=\"active\"><a href=\"#channelPane_" + channelName + "\" data-toggle=\"tab\">" + channelName + "</a></li>");
			$(".nav-tabs li").removeClass("active");
			$(".tab-content div").removeClass("active");
			$("#tabChannel_" + channelName).addClass("active");
			$("#channelPane_" + channelName).addClass("active");
			$("#channelPane_" + channelName).addClass("in");
			$("#messageInput_" + channelName).keypress(function(e) {
				if (e.which == 13) {
					sendMessage(channelName, $('#messageInput_' + channelName).val(), function() {
						$('#messageInput_' + channelName).val('')
					});
				}
			});
			$.getJSON("/channel/message", {
				channelName : "#" + channelName,
				sessionId : sessionId,
				sessionKey : sessionKey
			}, function(data) {
				if (0 == data.length) {
					return;
				}
				$("#channelPane_nameArea_" + channelName).html($.templates("<div id=\"channelPane_" + channelName + "userArea_{{>#data}}\">{{>#data}}</div>").render(data.users));
				renderExternalTemplate("#channelPane_messageArea_" + channelName, "/resource/templates/chatMessage.html", data.messages, function(template, renderd) {
					chatMessageTemplate = template;
				});
			}).error(function(data) {
				$("#channelPane_messageArea_" + channelName).html("error " + data.textStatus);
			});
		});
	}, "json");
}
function sendMessage(channelName, message, onSuccessFunction) {
	if (!message || "" == message) {
		return;
	}
	appendMessageToChannelPane(channelName, new Date().getTime(), myNickName, message);
	$.post("/channel/post", {
		channelName : "#" + channelName,
		message : message,
		sessionId : sessionId,
		sessionKey : sessionKey
	}, function(data) {
		if (onSuccessFunction) {
			onSuccessFunction(data);
		}
	}, "json");
}
renderExternalTemplate("#content", "/resource/templates/login.html");