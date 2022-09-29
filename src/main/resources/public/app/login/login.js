$(function () {
    setInterval(function () {
        $("#alerta").fadeOut(function () {
            $(this).fadeIn();
        });
    }, 4000);


});
