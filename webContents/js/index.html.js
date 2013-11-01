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
if (location.hash) {
	$(function() {
		$.getJSON("/logs?k=" + location.hash.substring(1, location.hash.length), function(data) {
			if (0 == data.length) {
				$("#logArea").html("no data")
				return;
			} else {
			}
			$("#leadArea").html($("#leadTemplate").render(data));
			$("#infoArea").html($("#infoTemplate").render(data));
			$("#logArea").html($("#logTemplate").render(data.logs));
			prettyPrint();
		}).error(function(data) {
			$("#logArea").html("error " + data.textStatus);
		});
	})
} else {
	$("#logArea").html("no log specified");
}