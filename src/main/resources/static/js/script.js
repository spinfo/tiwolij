$(document).ready(function() {
	$('a[data-action=deleteRow]').click(function(e) {
		e.preventDefault();

		var url = $(this).attr('href');
		var row = $(this).closest('tr');

		if (confirm(MESSAGES_GENERAL_CONFIRM))
			$.ajax(url).done(row.remove());
	});
});
