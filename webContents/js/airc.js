$.views.converters({
	date : function(val) {
		var date = new Date(val);
		var month = date.getMonth() + 1;
		var day = date.getDate();
		var hours = date.getHours();
		var minutes = date.getMinutes();
		if (month < 10) {
			month = "0" + month
		}
		if (day < 10) {
			day = "0" + day
		}
		if (hours < 10) {
			hours = "0" + hours
		}
		if (minutes < 10) {
			minutes = "0" + minutes
		}
		return date.getFullYear() + "/" + (month) + "/" + day + " " + hours + ":" + minutes;
	},
	time : function(val) {
		var date = new Date(val);
		var hours = date.getHours();
		var minutes = date.getMinutes();
		if (hours < 10) {
			hours = "0" + hours
		}
		if (minutes < 10) {
			minutes = "0" + minutes
		}
		return hours + ":" + minutes;
	}
});
function renderExternalTemplate(selector, template, data, onSuccessFunction) {
	if (typeof data === "undefined") {
		data = {};
	}
	$.when($.get(template)).done(function(templateData) {
		$.templates({
			lastLoadTemplate : templateData
		});
		var renderd = $.render.lastLoadTemplate(data);
		$(selector).html(renderd);
		if (onSuccessFunction) {
			onSuccessFunction($.templates(templateData), renderd);
		}
	});
}
function loadTemplate(template, onSuccessFunction) {
	$.when($.get(template)).done(function(templateData) {
		if (onSuccessFunction) {
			onSuccessFunction($.templates(templateData), templateData);
		}
	});
}
function escapeRegExp(string) {
	return string.replace(/([.*+?^=!:${}()|[\]\/\\])/g, "\\$1");
}
function replaceToLink(src, nickName, imageArray) {
	var regexSrc = '((http|https|ftp|NOTES):\\/\\/[\\w?=&.\\/-;#~%-]+(?![\\w\\s?&.\\/;#~%"=-]*>))';
	if (nickName) {
		regexSrc = regexSrc + '|(' + escapeRegExp(nickName) + ')';
	}
	return src.replace(new RegExp(regexSrc, 'g'), function(matchValue) {
		if (imageArray) {
			imageArray.push(matchValue)
		}
		if (matchValue.indexOf("http://") == 0 || matchValue.indexOf("https://") == 0 || matchValue.indexOf("ftp://") == 0 || matchValue.indexOf("NOTES://") == 0) {
			if (matchValue.match(".png$") == ".png" || matchValue.match(".jpg$") == ".jpg" || matchValue.match(".jpeg$") == ".jpeg" || matchValue.match(".gif$") == ".gif") {
				setTimeout(function() {
					$('[rel="popover"]').popover({
						html : true
					})
				}, 100);
				return '<a href="#" data-toggle="popover" rel="popover" data-placement="bottom" data-content="<img style=\'max-width:300px\' src=\'' + matchValue + '\'/>">' + matchValue + '</a>'
			} else {
				return '<a href="' + matchValue + '" target="_blank">' + matchValue + '</a>';
			}
		} else {
			return "<span class=\"text-info\">" + matchValue + "</span>"
		}
	});
}
function browserLanguage() {
	try {
		return (navigator.browserLanguage || navigator.language || navigator.userLanguage).substr(0, 2)
	} catch (e) {
		return undefined;
	}
}
$.loadTranslateValue = function(key, onLoadFunction) {
	if (!$.translateData) {
		$.getJSON("/resource/translate.js", function(data) {
			$.translateData = data;
			$.getJSON("/resource/translate_" + browserLanguage() + ".js", function(data) {
				$.extend($.translateData, data)
			}).complete(function() {
				var value = $.translateData[key];
				if (value) {
					onLoadFunction(key, value);
				}
			});
		}).error(function() {
			console.log("/resource/translate.js is not found");
			$.translateData = {};
		});
	} else {
		var value = $.translateData[key];
		if (value) {
			onLoadFunction(key, value);
		}
	}
}, $.fn.extend({
	toLink : function(nickName, imageArray) {
		$(this).html(replaceToLink($(this).html(), nickName, imageArray));
	},
	translate : function() {
		var element = this;
		$.loadTranslateValue(this.attr("id"), function(key, value) {
			element.html(value);
			element.removeClass("translate");
		});
	},
	isVisible : function() {
		return $.expr.filters.visible(this[0]);
	}
});
if (typeof String.prototype.startsWith != 'function') {
	String.prototype.startsWith = function(str) {
		return 0 == this.indexOf(str);
	}
}
renderExternalTemplate("#header", "/resource/templates/header.html");