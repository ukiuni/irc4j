var sessionKey;
var sessionId;
var chatMessageTemplate;
var channelTabTemplate;
var myNickName;
var CHANNEL_NAME_PREFIX = "AIRC_CHANNEL_";
var joinedChannels = new Array();
var minMessageIdArray = new Array();
var maxMessageIdArray = new Array();
var maxMessageId = 0;
var notificationKeywordArray = new Array();
function tryLogin(loginId, password) {
	if (!loginId) {
		$("#userArea").addClass("has-error");
		return;
	}
	$.post("/login", {
		nickName : loginId,
		password : password
	}, function(sessionData) {
		$("#headerTitle").click(function() {
			showChatPane();
		});
		$("#userArea").removeClass("has-error");
		$("#passwordArea").removeClass("has-error");
		myNickName = sessionData.nickName;
		$("#headerSettingArea").css("display", "inline");
		if (sessionData.iconImage) {
			$("#headerSettingArea").prepend("<li><img id=\"selfHeaderImage\" style=\"width:30px;height:30px;margin-top:10px\" src=" + sessionData.iconImage + "/></li>")
		}
		$("#selfNameArea").text(myNickName);
		if (sessionData.userId && 0 != sessionData.userId) {
			$("#headerPluginMenu").show();
		}
		notify = sessionData.notify;
		if (notify && sessionData.notificationKeyword) {
			setNotificationKeyword(sessionData.notificationKeyword);
		}
		sessionId = sessionData.sessionId;
		sessionKey = sessionData.sessionKey;
		if (sessionData.channelNames) {
			initChatPane(myNickName, function() {
				for ( var i in sessionData.channelNames) {
					addChannel(CHANNEL_NAME_PREFIX + sessionData.channelNames[i].substring(1));
				}
			});
		} else {
			initChatPane(myNickName);
		}
	}).error(function(data) {
		if (409 == data.status) {
			$("#userArea").addClass("has-error");
			$("#errorMessageArea").html("User is aleady in");
		} else if (403 == data.status) {
			$("#passwordArea").addClass("has-error");
			$("#errorMessageArea").html("password is wrong.");
		} else {
			$("#errorMessageArea").html("something is wrong.");
		}
		$("#errorMessageArea").show(500);
	}, "json");
}
function initChatPane(nickName, addchannelFunction) {
	renderExternalTemplate("#content", "/resource/templates/chatPane.html", {}, function() {
		loadTemplate("/resource/templates/channelTab.html", function(template) {
			channelTabTemplate = template;
			if (addchannelFunction) {
				addchannelFunction();
			} else {
				addChannel(CHANNEL_NAME_PREFIX + "home");
			}
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
	messageObjforPaint.tempId = "messageId_" + new Date().getTime();
	messageObjforPaint.message = message;
	var newChatMessagePane = chatMessageTemplate.render(messageObjforPaint);
	newChatMessagePane = replaceToLink(newChatMessagePane, myNickName);
	var channelPane_messageArea = $("#channelPane_messageArea_" + channelName);
	channelPane_messageArea.prepend(newChatMessagePane);
	if ((!document.hasFocus() || !channelPane_messageArea.isVisible()) && notify && webkitNotifications && webkitNotifications.createNotification && senderNickName != myNickName) {
		if (0 == webkitNotifications.checkPermission()) {
			var containsNotificationKeyword = false;
			for ( var i in notificationKeywordArray) {
				if (0 <= message.indexOf(notificationKeywordArray[i])) {
					containsNotificationKeyword = true;
					break;
				}
			}
			var icon = containsNotificationKeyword ? "/images/notify_warn.gif" : "/images/notify_standard.gif"
			var loadChannelName = (channelName.startsWith(CHANNEL_NAME_PREFIX)) ? "#" + channelName.substring(CHANNEL_NAME_PREFIX.length) : channelName;
			var notifyWindow = webkitNotifications.createNotification(icon, "[" + loadChannelName + "] " + senderNickName, message);
			notifyWindow.onclick = function() {
				window.focus();
				showChatPane();
				openChannelPane(channelName);
				document.location.href = "#" + messageObjforPaint.tempId;
			};
			notifyWindow.show();
			if (!containsNotificationKeyword) {
				setTimeout(function() {
					if (notifyWindow.clear) {
						notifyWindow.clear();
					} else if (notifyWindow.close()) {
						notifyWindow.close();
					}
				}, 3000);
			}
		}
	}
}
function addChannel(channelName, onSuccessAddChannelFunction) {
	if (0 == channelName.length || 0 <= channelName.indexOf("#")) {
		$("#channelAddArea").addClass("has-error");
		return;
	}
	if (document.getElementById("channelPane_" + channelName)) {
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
				channelName : channelName,
				displayChannelName : channelName.startsWith(CHANNEL_NAME_PREFIX) ? "#" + channelName.substring(CHANNEL_NAME_PREFIX.length) : channelName
			});
			$("#tabContentChannelPlus").before(channelPane);
			$("#tabChannelPlus").before(channelTabTemplate.render({
				channelDisplayName : channelName.startsWith(CHANNEL_NAME_PREFIX) ? channelName.substring(CHANNEL_NAME_PREFIX.length) : channelName,
				channelName : channelName
			}));
			openChannelPane(channelName);
			$("#messageInput_" + channelName).keypress(function(e) {
				if (e.which == 13) {
					sendMessage(channelName, $('#messageInput_' + channelName).val(), function() {
						$('#messageInput_' + channelName).val('')
					});
				}
			});
			$.post("/channel/message", {
				channelName : loadChannelName,
				sessionId : sessionId,
				sessionKey : sessionKey
			}, function(data) {
				if (0 == data.length) {
					return;
				}
				$("#channelPane_nameArea_" + channelName).html($.templates("<div id=\"channelPane_" + channelName + "userArea_{{>#data}}\" onclick=\"openPrivateMessageDialog(\'{{>#data}}\')\">{{>#data}}</div>").render(data.users));
				for ( var i in data.messages) {
					var message = data.messages[i];
					setMaxAndMin(channelName, message);
				}
				renderExternalTemplate("#channelPane_messageArea_" + channelName, "/resource/templates/chatMessage.html", data.messages, function(template, renderd) {
					chatMessageTemplate = template;
					$("#channelPane_messageArea_" + channelName).toLink(myNickName);
					if (channelName.startsWith(CHANNEL_NAME_PREFIX)) {
						joinedChannels.push("#" + channelName.substring(CHANNEL_NAME_PREFIX.length));
					}
					if (onSuccessAddChannelFunction) {
						onSuccessAddChannelFunction();
					}
					if (0 < data.messages.length) {
						$("#channelPane_loadNextButtonRow_" + channelName).show();
					}
				});
			}, "json").error(function(data) {
				$("#channelPane_messageArea_" + channelName).html("error " + data.textStatus);
			});
		});
	}, "json");
}
function openChannelPane(channelName) {
	$(".nav-tabs li").removeClass("active");
	$(".tab-content div").removeClass("active");
	$("#tabChannel_" + channelName).addClass("active");
	$("#tabChannel_" + channelName).click(function() {
		clearBadge(channelName);
	});
	$("#channelPane_" + channelName).addClass("active");
	$("#channelPane_" + channelName).addClass("in");
}
function setMaxAndMin(channelName, message) {
	if (!minMessageIdArray[channelName] || minMessageIdArray[channelName] > message.id) {
		minMessageIdArray[channelName] = message.id;
	}
	if (!maxMessageIdArray[channelName] || maxMessageIdArray[channelName] < message.id) {
		maxMessageIdArray[channelName] = message.id;
	}
	if (maxMessageId < message.id) {
		maxMessageId = message.id;
	}
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
function uploadFile(channelName, uploadButtonOrg) {
	var uploadButton = $(uploadButtonOrg);
	var form = document.createElement("form");
	form.action = "/channel/sendFile";
	form.style.display = "none";
	form.method = "post";
	form.enctype = "multipart/form-data";
	var fileInput = document.createElement("input");
	fileInput.type = "file";
	fileInput.name = "file";
	form.appendChild(fileInput);
	var channelNameInput = document.createElement("input");
	channelNameInput.type = "hidden";
	channelNameInput.name = "channelName";
	channelNameInput.value = (channelName.startsWith(CHANNEL_NAME_PREFIX)) ? "#" + channelName.substring(CHANNEL_NAME_PREFIX.length) : channelName;
	form.appendChild(channelNameInput);
	var sessionIdInput = document.createElement("input");
	sessionIdInput.type = "hidden";
	sessionIdInput.name = "sessionId";
	sessionIdInput.value = sessionId;
	form.appendChild(sessionIdInput);
	var sessionKeyInput = document.createElement("input");
	sessionKeyInput.type = "hidden";
	sessionKeyInput.name = "sessionKey";
	sessionKeyInput.value = sessionKey;
	form.appendChild(sessionKeyInput);

	jFileInput = $(fileInput);
	jFileInput.change(function() {
		$(form).ajaxForm({
			beforeSend : function() {
				uploadButton.addClass("disabled");
				var percentVal = '0%';
				uploadButton.html(percentVal);
			},
			uploadProgress : function(event, position, total, percentComplete) {
				var percentVal = percentComplete + '%';
				uploadButton.html(percentVal);
			},
			success : function() {
				var percentVal = '100%';
				uploadButton.html(percentVal);
			},
			complete : function(xhr) {
				uploadButton.removeClass("disabled");
				setTimeout(function() {
					uploadButton.html("Upload File");
				}, 1000);
			}
		}).submit();
	});
	jFileInput.click();

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
		channelNames : joinedChannels.join(","),
		maxMessageId : maxMessageId
	}, function(data) {
		$("#connectionStatus").hide(1000);
		for ( var channelName in data.messages) {
			var messages = data.messages[channelName];
			var channelNameWithoutCharp = CHANNEL_NAME_PREFIX + channelName.substring(1);
			for ( var i in messages) {
				var message = messages[i];
				appendMessageToChannelPane(channelNameWithoutCharp, message.createdAt, message.senderNickName, message.message);
			}
		}
	}, "json");
}
function loadOlderMessage(channelName) {
	var loadChannelName = (channelName.startsWith(CHANNEL_NAME_PREFIX)) ? "#" + channelName.substring(CHANNEL_NAME_PREFIX.length) : channelName;
	$.post("/channel/message", {
		sessionId : sessionId,
		sessionKey : sessionKey,
		channelName : loadChannelName,
		olderThan : minMessageIdArray[channelName]
	}, function(data) {
		for ( var i in data.messages) {
			var message = data.messages[i];
			setMaxAndMin(channelName, message);
			var messageArea = replaceToLink(chatMessageTemplate.render(message));
			$("#channelPane_messageArea_" + channelName).append(messageArea);
		}
		if (0 == data.messages.length) {
			$("#channelPane_loadNextButtonRow_" + channelName).hide();
		}
	}, "json");
}
function partFromChannel(channelName) {
	var loadChannelName = (channelName.startsWith(CHANNEL_NAME_PREFIX)) ? "#" + channelName.substring(CHANNEL_NAME_PREFIX.length) : channelName;

	var channelTabsArea = $("#channelTabsArea").children(":first");
	channelTabsArea.addClass("active");
	var channelMessagesArea = $("#tabContent").children(":first");
	channelMessagesArea.addClass("active");
	channelMessagesArea.addClass("in");

	$("#channelPane_" + channelName).remove();
	$("#tabChannel_" + channelName).remove();
	$.post("/channel/part", {
		sessionId : sessionId,
		sessionKey : sessionKey,
		channelName : loadChannelName
	});
}
function openSettingPane() {
	var paddingSettingPane = function() {
		$.post("/user", {
			sessionId : sessionId,
			sessionKey : sessionKey
		}, function(user) {
			$("#settingPane").show(500);
			$("#chatPane").hide(500);
			$("#pluginPane").hide(500);
			$("#inputNickName").val(user.nickName);
			$("#inputName").val(user.name);
			$("#inputRealName").val(user.realName);
			$("#inputEmail").val(user.email);
			// $("#inputPassword").val();
			$("#descriptionTextArea").val(user.description);
			if (user.notificationKeyword && "" != user.notificationKeyword) {
				$("#notificationInput").val(user.notificationKeyword);
			} else {
				$("#notificationInput").val(myNickName);
			}
			if (user.notify) {
				$("#notificationCheckbox").attr("checked", true);
				$("#notificationInput").removeAttr("disabled");
				$("#notificationButton").text("ON");
			} else {
				$("#notificationCheckbox").attr("checked", false);
				$("#notificationInput").attr("disabled", "disabled");
				$("#notificationButton").text("OFF");
			}
		}, "json");
	}
	if (document.getElementById("settingPane")) {
		paddingSettingPane();
	} else {
		loadTemplate("/resource/templates/settingPane.html", function(renderd) {
			$("#content").append(renderd.render());
			paddingSettingPane();
		})
	}
}

function openPluginPane() {
	var paddingPluginPane = function() {
		$.post("/plugin/myList", {
			sessionId : sessionId,
			sessionKey : sessionKey
		}, function(plugins) {
			$("#settingPane").hide(500);
			$("#chatPane").hide(500);
			$("#pluginPane").show(500);
			for ( var i in plugins) {
				var plugin = plugins[i];
				addPluginTabHead(plugin);
			}
		}, "json");
	}
	if (document.getElementById("pluginPane")) {
		paddingPluginPane();
	} else {
		loadTemplate("/resource/templates/pluginPane.html", function(renderd) {
			$("#content").append(renderd.render());
			paddingPluginPane();
		});
	}
}
function addPluginTabHead(plugin) {
	$("#pluginTabHead" + plugin.id).remove();
	$("#pluginListPaneTab").prepend($('<div><li id="pluginTabHead{{:id}}" ><a href="#">{{:name}}</a></li></div>').render(plugin));
	$("#pluginTabHead" + plugin.id).click(function() {
		showPluginPanel(plugin);
	});
}
function showNewPluginPanel() {
	showPluginPanel({
		id : 0,
		name : myNickName + "_COMMAND",
		command : myNickName + "_COMMAND",
		description : myNickName + "\'s COMMAND",
		status : 0,
		script : "function execute(server, client, params) {\n    client.sendPrivateCommand(\"Hello \"+client.getNickName()+\". I am " + myNickName + "\");\n}"
	});
}
function showPluginPanel(plugin) {
	renderExternalTemplate("#pluginEditPane", "/resource/templates/pluginCreateFormPane.html", plugin, function() {
		if (0 == plugin.status) {
			$("#pluginStatus").attr("checked", true);
		} else {
			$("#pluginStatus").attr("checked", false);
		}
	});
}
function submitPluginForm() {
	var name = $("#pluginName").val();
	var command = $("#pluginCommand").val();
	var description = $("#pluginDescriptionTextArea").val();
	var script = $("#pluginScriptTextArea").val();

	$("#pluginNameFormGroup").removeClass("has-error");
	$("#pluginCommandFormGroup").removeClass("has-error");
	$("#pluginDescriptionFormGroup").removeClass("has-error");
	$("#pluginScriptFormGroup").removeClass("has-error");
	var hasError = false;
	if ("" == name) {
		$("#pluginNameFormGroup").addClass("has-error");
		hasError = true;
	}
	if ("" == command) {
		$("#pluginCommandFormGroup").addClass("has-error");
		hasError = true;
	}
	if ("" == description) {
		$("#pluginDescriptionFormGroup").addClass("has-error");
		hasError = true;
	}
	if ("" == script) {
		$("#pluginScriptFormGroup").addClass("has-error");
		hasError = true;
	}
	try {
		eval(script);
	} catch (e) {
		$("#pluginScriptFormGroup").addClass("has-error");
		hasError = true;
	}
	if (hasError) {
		$("#submitPluginFail").show(500);
		return;
	}
	$("#submitPluginSuccess").hide(500);
	$("#submitPluginFail").hide(500);
	$.post("/plugin/post", {
		sessionId : sessionId,
		sessionKey : sessionKey,
		id : $("#pluginId").val(),
		name : name,
		command : command,
		description : description,
		script : script,
		effective : $("#pluginStatus:checked").val()
	}, function(plugin, textStatus, jXhr) {
		$("#submitPluginSuccess").show(500);
		addPluginTabHead(plugin);
		$("#pluginId").val(plugin.id);
	}, "json").error(function(jXhr) {
		if (409 == jXhr.status) {
			$("#pluginNameFormGroup").addClass("has-error");
			$("#pluginCommandFormGroup").addClass("has-error");
		}
		$("#submitPluginFail").show(500);
	});
}
function showChatPane() {
	$("#settingPane").hide(500);
	$("#pluginPane").hide(500);
	$("#chatPane").show(500);
}
function submitUserSettingForm() {
	var userSettingSubmit = $("#userSettingSubmit");
	var userSettingForm = $("#userSettingForm");
	$("#emailFormGroup").removeClass("has-error");
	$("#passwordFormGroup").removeClass("has-error");
	$("#nameFormGroup").removeClass("has-error");
	$("#realNameFormGroup").removeClass("has-error");
	var hasError = false;
	if (!$("#inputEmail").val().match(/^[A-Za-z0-9.\+]+[\w-]+@[\w\.-]+\.\w{2,}$/)) {
		$("#emailFormGroup").addClass("has-error");
		hasError = true;
	}
	if ("" == $("#inputPassword").val()) {
		$("#passwordFormGroup").addClass("has-error");
		hasError = true;
	}
	if ("" == $("#inputName").val()) {
		$("#nameFormGroup").addClass("has-error");
		hasError = true;
	}
	if ("" == $("#inputRealName").val()) {
		$("#realNameFormGroup").addClass("has-error");
		hasError = true;
	}
	if (hasError) {
		$("#submitUserSettingFail").show(1000);
		return;
	}

	var sessionIdInput = document.createElement("input");
	sessionIdInput.type = "hidden";
	sessionIdInput.name = "sessionId";
	sessionIdInput.value = sessionId;
	userSettingForm.append(sessionIdInput);
	var sessionKeyInput = document.createElement("input");
	sessionKeyInput.type = "hidden";
	sessionKeyInput.name = "sessionKey";
	sessionKeyInput.value = sessionKey;
	userSettingForm.append(sessionKeyInput);

	userSettingForm.ajaxForm({
		beforeSend : function() {
			$("#submitUserSettingSuccess").hide();
			$("#submitUserSettingFail").hide();
			$("#userSettingSubmit").addClass("disabled");
			userSettingSubmit.html("0%");
		},
		uploadProgress : function(event, position, total, percentComplete) {
			var percentVal = percentComplete + '%';
			userSettingSubmit.html(percentVal);
		},
		success : function() {
			$("#submitUserSettingSuccess").show(1000);
			$("#headerPluginMenu").show();
			notify = $("#notificationCheckbox").attr("checked");
			setNotificationKeyword($("#notificationInput").val())
		},
		error : function() {
			$("#submitUserSettingFail").show(1000);
		},
		complete : function(xhr) {
			$("#userSettingSubmit").removeClass("disabled");
			setTimeout(function() {
				userSettingSubmit.html("Submit");
			}, 1000);
			$.post("/user", {
				sessionId : sessionId,
				sessionKey : sessionKey
			}, function(user) {
				if (document.getElementById("selfHeaderImage")) {
					$("#selfHeaderImage").attr("src", user.iconImage);
				} else {
					$("#headerSettingArea").prepend("<li><img id=\"selfHeaderImage\" style=\"width:30px;height:30px;margin-top:10px\" src=" + user.iconImage + "/></li>")
				}
			}, "json");
		}
	}).submit();
}
function setNotificationKeyword(notiicationkeywords) {
	var beforeTrim = notiicationkeywords.split(",");
	notificationKeywordArray = new Array();
	for ( var i in beforeTrim) {
		notificationKeywordArray.push($.trim(beforeTrim[i]));
	}
}
function signOut() {
	$.post("/logout", {
		sessionId : sessionId,
		sessionKey : sessionKey
	}, function() {
		$(window).unbind();
		document.location.href = "/";
	});
}
renderExternalTemplate("#content", "/resource/templates/login.html");