if (location.hash) {
	$(function() {
		$.getJSON("/logs?k=" + location.hash.substring(1, location.hash.length), function(data) {
			if (0 == data.length) {
				$("#logArea").html("no data")
				return;
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