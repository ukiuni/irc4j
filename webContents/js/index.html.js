var sessionKey;
var sessionId;
var chatMessageTemplate;
var channelTabTemplate;
var myNickName;
var CHANNEL_NAME_PREFIX = "AIRC_CHANNEL_";
var joinedChannels = new Array();
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
		loadTemplate("/resource/templates/channelTab.html", function(template) {
			channelTabTemplate = template;
			addChannel(CHANNEL_NAME_PREFIX + "home");
		});
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
			var channelNameWithoutCharp;
			if (event.channelName && "#" == event.channelName.charAt(0)) {
				channelNameWithoutCharp = CHANNEL_NAME_PREFIX + event.channelName.substring(1);
			} else {
				channelNameWithoutCharp = event.channelName;
			}
			if ("user.part" == event.type) {
				$("#channelPane_" + channelNameWithoutCharp + "userArea_" + event.userNickName).remove();
			} else if ("user.join" == event.type) {
				// avoid duplicate
				$("#channelPane_" + channelNameWithoutCharp + "userArea_" + event.userNickName).remove();
				$("#channelPane_nameArea_" + channelNameWithoutCharp).prepend("<div id=\"channelPane_" + channelNameWithoutCharp + "userArea_" + event.userNickName + "\" onclick=\"openPrivateMessageDialog(\'" + event.userNickName + "\')\" >" + event.userNickName + "</div>");
			} else if ("message" == event.type) {
				var appendMessageFunction = function() {
					appendMessageToChannelPane(channelNameWithoutCharp, event.createdAt, event.userNickName, event.message);
					incrementsBadge(channelNameWithoutCharp);
				}
				if (document.getElementById("channelPane_" + channelNameWithoutCharp)) {
					appendMessageFunction();
				} else {
					addChannel(channelNameWithoutCharp, appendMessageFunction);
				}
			} else if ("rejoin" == event.type) {
				rejoin();
			} else if ("reload" == event.type) {
				document.location.href = event.url;
			}
		}
	}, "json").done(function(data) {
		continueListen();
	}).fail(function(data) {
		$("#connectionStatus").show(1000);
		setTimeout(function() {
			continueListen()
		}, 30000);
	});
}

function clearBadge(channelName) {
	var badge = $("#tabChannelBadge_" + channelName);
	badge.html("");
	badge.css("margin-left", "0px");
}
function incrementsBadge(channelName) {
	var badge = $("#tabChannelBadge_" + channelName);
	var badgeNum = badge.html();
	if ("" == badgeNum || typeof badgeNum === "undefined") {
		badge.html("1");
	} else {
		var num = parseInt(badgeNum);
		if (isNaN(num)) {
			badge.html("1");
		} else {
			badge.html(1 + num);
		}
	}
	badge.css("margin-left", "10px");
}
function appendMessageToChannelPane(channelName, createdAt, senderNickName, message) {
	var messageObjforPaint = new Object();
	messageObjforPaint.createdAt = createdAt;
	messageObjforPaint.senderNickName = senderNickName;
	messageObjforPaint.message = message;
	$("#channelPane_messageArea_" + channelName).prepend(chatMessageTemplate.render(messageObjforPaint));
	$("#channelPane_messageArea_" + channelName).toLink();
}
function addChannel(channelName, onSuccessAddChannelFunction) {
	if (0 == channelName.length || 0 <= channelName.indexOf("#")) {
		$("#channelAddArea").addClass("has-error");
		return;
	}
	var loadChannelName = channelName.startsWith(CHANNEL_NAME_PREFIX) ? "#" + channelName.substring(CHANNEL_NAME_PREFIX.length) : channelName;
	$.post("/channel/join", {
		channelName : loadChannelName,
		sessionId : sessionId,
		sessionKey : sessionKey
	}, function(jsonSrc) {
		$("#channelAddInput").val("");
		loadTemplate("/resource/templates/channelPane.html", function(template) {
			var channelPane = template.render({
				channelName : channelName
			});
			$("#tabContentChannelPlus").before(channelPane);
			$("#tabChannelPlus").before(channelTabTemplate.render({
				channelDisplayName : channelName.startsWith(CHANNEL_NAME_PREFIX) ? channelName.substring(CHANNEL_NAME_PREFIX.length) : channelName,
				channelName : channelName
			}));
			$(".nav-tabs li").removeClass("active");
			$(".tab-content div").removeClass("active");
			$("#tabChannel_" + channelName).addClass("active");
			$("#tabChannel_" + channelName).click(function() {
				clearBadge(channelName);
			});
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
				channelName : loadChannelName,
				sessionId : sessionId,
				sessionKey : sessionKey
			}, function(data) {
				if (0 == data.length) {
					return;
				}
				$("#channelPane_nameArea_" + channelName).html($.templates("<div id=\"channelPane_" + channelName + "userArea_{{>#data}}\" onclick=\"openPrivateMessageDialog(\'{{>#data}}\')\">{{>#data}}</div>").render(data.users));
				renderExternalTemplate("#channelPane_messageArea_" + channelName, "/resource/templates/chatMessage.html", data.messages, function(template, renderd) {
					chatMessageTemplate = template;
					$("#channelPane_messageArea_" + channelName).toLink();
					if (channelName.startsWith(CHANNEL_NAME_PREFIX)) {
						joinedChannels.push("#" + channelName.substring(CHANNEL_NAME_PREFIX.length));
					}
					if (onSuccessAddChannelFunction) {
						onSuccessAddChannelFunction();
					}
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
	var loadChannelName = (channelName.startsWith(CHANNEL_NAME_PREFIX)) ? "#" + channelName.substring(CHANNEL_NAME_PREFIX.length) : channelName;
	appendMessageToChannelPane(channelName, new Date().getTime(), myNickName, message);
	$.post("/channel/post", {
		channelName : loadChannelName,
		message : message,
		sessionId : sessionId,
		sessionKey : sessionKey
	}, function(data) {
		if (onSuccessFunction) {
			onSuccessFunction(data);
		}
	}, "json");
}
function openPrivateMessageDialog(targetUser) {
	if (document.getElementById("channelPane_" + targetUser) || myNickName == targetUser) {
		return;
	}
	$("#privateModalTitleArea").html("Private Message");
	$("#privateModalBodyArea").html("Start private chat with " + targetUser + "?");
	$("#privateModalStartButton").click(function() {
		if (document.getElementById("channelPane_" + targetUser)) {
			return;
		}
		addChannel(targetUser);
		$("#privateMessageModal").modal("hide");
	});
	$("#privateMessageModal").modal();
}
function rejoin() {
	$.post("/rejoin", {
		sessionId : sessionId,
		sessionKey : sessionKey,
		channelNames : joinedChannels.join(",")
	}, function() {
		$("#connectionStatus").hide(1000);
	});
}
renderExternalTemplate("#content", "/resource/templates/login.html");