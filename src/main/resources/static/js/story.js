function visitChapter(chapter) {
	chapter.find("[data-animate]").each(function() {
		var animate = $(this).data("animate");
		$(this).css("visibility", "visible")
			   .addClass("animated " + animate);
	});

	chapter.find("img")
		.css("visibility", "visible")
		.addClass("animated fadeIn");
}

function chapterLoaded(chapters, i) {
	var chapter = $(chapters[i]);
	chapter.find("[data-animate]").each(function() {
		$(this).css("visibility", "hidden");
	});

	Waypoint.refreshAll();

	chapter.waypoint(function() {
		this.destroy();
		visitChapter(chapter);
		loadChapter(chapters, i+1);
	}, {offset: '90%'});
}

function loadChapter(chapters, i) {
	if (i > chapters.length) {
		return;
	}

	var chapter = $(chapters[i]);
	if (chapter.length === 0) {
		console.log(":(");
		return;
	}

	chapter.show();

	var images = chapter.find("img");
	if (images.length > 0) {
		images.each(function() {
			var img = $(this);
			img.attr('src', img.data('src'))
				.css("visibility", "hidden");
		});
	}
	chapterLoaded(chapters, i);
}

$(document).ready(function($) {
	$(window).on('beforeunload', function(){
	  $(window).scrollTop(0);
	});

	loadChapter($(".story-chapter").hide(), 0);
});