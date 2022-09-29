
function scrollToClass(elementClass, removedHeight) {
    var scroll_to = $(elementClass).offset().top - removedHeight;
    if ($(window).scrollTop() != scroll_to) {
        $('html, body').stop().animate({scrollTop: scroll_to}, 0);
    }
}

function barProgress(progressLineObject, direction) {
    var numberOfSteps = progressLineObject.data('number-of-steps');
    var nowValue = progressLineObject.data('now-value');
    var newValue = 0;
    if (direction == 'right') {
        newValue = nowValue + (100 / numberOfSteps);
    } else if (direction == 'left') {
        newValue = nowValue - (100 / numberOfSteps);
    }
    progressLineObject.attr('style', 'width: ' + newValue + '%;').data('now-value', newValue);
}

