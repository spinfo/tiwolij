$(document).ready(function() {

	$('a[data-action="deleteRow"]').click(function(e) {
		e.preventDefault();

		var url = $(this).attr('href');
		var row = $(this).closest('tr');

		if (confirm(MESSAGES_BACKEND_CONFIRM))
		$.ajax(url).done(row.remove());
	});

	$('button#submit[formaction]').closest('form').submit(function(e) {
		$(this).find('button#submit').attr('disabled', true);
		e.preventDefault();

		$.ajax({
			url: $(this).find('button#submit').attr('formaction'),
			method: $(this).attr('method'),
			data: new FormData(this),
			processData: false,
			contentType: false
		});

		var progress = window.setInterval(function() {
			$.get('/tiwolij/data/import/progress', function(data) {
				if ($(data).find('#bar').length > 0) {
					clearInterval(progress);
					$.get('/tiwolij/data/import/process');
					window.location.replace('/tiwolij/data/import/progress');
				}
			});
		}, 250);
	});

	if ($('#bar').length > 0) {
		var progress = window.setInterval(function() {
			$.get('/tiwolij/data/import/progress', function(data) {
				if ($(data).find('#bar').length > 0) {
					$('#txt').text($(data).find('#txt').text());
					$('#bar').addClass('progress-bar-striped active').css('width', $(data).find('#bar').css('width'));
				} else {
					clearInterval(progress);
					window.location.replace('/tiwolij/data/import/progress');
				}
			});
		}, 250);
	}

});
