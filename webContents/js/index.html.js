var sessionKey;
var sessionId;
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
		nickName = sessionData.nickName;
		sessionId = sessionData.sessionId;
		sessionKey = sessionData.sessionKey;
		initChatPane(nickName);
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
	//continueListen();
}
function continueListen() {
	$.post("/listenEvent", {
		sessionId : sessionId,
		sessionKey : sessionKey
	}, function(data) {
		for (var i in data) {
			var user = data.user[i];
			if ("part" == user.action) {
				$("#channelPane_"+channelName+"userArea_"+user.nickname).remove();
			} else if ("join" == user.action) {

			}
		}
	}, "json").complete(function() {
		continueListen();
	})
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
			$("#tabChannelPlus").before("<li class=\"active\"><a href=\"#channelPane_" + channelName + "\" data-toggle=\"tab\">" + channelName + "</a></li>");
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
				$("#channelPane_nameArea_" + channelName).html($.templates("<div id=\"channelPane_"+channelName+"userArea_{{>#data}}\">{{>#data}}</div>").render(data.users));
				renderExternalTemplate("#channelPane_messageArea_" + channelName, "/resource/templates/chatMessage.html", data.messages);
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