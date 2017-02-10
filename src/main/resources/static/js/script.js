$(document).ready(function() {

	$('a[data-action="deleteRow"]').click(function(e) {
		e.preventDefault();

		var url = $(this).attr('href');
		var row = $(this).closest('tr');

		if (confirm(MESSAGES_BACKEND_CONFIRM))
			$.ajax(url).done(row.remove());
	});

	$('button#submit[formaction^="/tiwolij/data/import/"]').closest('form').submit(function(e) {
		e.preventDefault();

		var form = new FormData(this);
		var bar = $('<div class="progress"><div id="bar" class="progress-bar" style="width: 0%;">');
		var txt = $('<p id="txt" class="lead">Please wait</p>')

		var progress = window.setInterval(function() {
			$.get('/tiwolij/data/import/progress', function(data) {
				if ($('#bar').length > 0) {
					if (data.indexOf(':') !== -1) {
						$('#txt').text('Processing ' + data.split(':')[0]);
						$('#bar').addClass('progress-bar-striped active').css('width', data.split(':')[1] + '%');
					} else {
						$('#txt').text('Please wait');
						$('#bar').removeClass('progress-bar-striped active').css('width', '0%');
					}
				} else {
					clearInterval(progress);
				}
			});
		}, 250);

		$(this).find('#submit').closest('.panel-footer').remove();
		$(this).find('.panel-body').empty().append(txt, bar);

		$.ajax({
	    url: $(this).attr('formaction'),
	    method: 'POST',
	    data: form,
	    processData: false,
	    contentType: false,
		}).done(function(data) {
			$('#backend > .container').replaceWith($(data).filter('.container'));
		});
	});

});
