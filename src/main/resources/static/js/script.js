$(document).ready(function() {

	$('a[data-action="deleteRow"]').click(function(e) {
		e.preventDefault();

		var url = $(this).attr('href');
		var row = $(this).closest('tr');

		if (confirm(MESSAGES_BACKEND_CONFIRM))
			$.ajax(url).done(row.remove());
	});

	$('button[formaction^="/tiwolij/data/import/"]').closest('form').submit(function(e) {
		e.preventDefault();

		var form = new FormData(this);
		var bar = $('<div class="progress"><div id="progress" class="progress-bar progress-bar-success" style="width: 100%;">');

		var progress = window.setInterval(function() {
			$.get('/tiwolij/data/import/progress', function(data) {
				if ($('#progress').length > 0) {
					switch(data) {
						case 'null':
							$('#progress').removeClass('progress-bar-striped active').css('width', '100%').text('Please wait ...');
							break;
						case 'pre':
							$('#progress').removeClass('progress-bar-striped active').css('width', '100%').text('Preprocessing ...');
							break;
						default:
							$('#progress').addClass('progress-bar-striped active').css('width', data + '%').text('');
							break;
					}
				} else
					clearInterval(progress);
			});
		}, 1000);

		$(this).find('.panel-body').empty().append(bar);
		$(this).find('#submit').attr('disabled', 'disabled');

		$.ajax({
	    url: $(this).attr('formaction'),
	    method: 'POST',
	    data: form,
	    processData: false,
	    contentType: false,
		}).done(function(data) {
			$('body#backend').replaceWith($(data).find('body#backend'));
		});
	});

});
