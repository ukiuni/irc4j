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
renderExternalTemplate("#header", "/resource/templates/header.html");