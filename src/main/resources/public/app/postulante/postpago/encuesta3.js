$(function () {

    Postulante = {
        download: function () {
            location.href = APP.url("inscripcion/postpago/generateDeclaracionJurada");
            setTimeout(function () {
                $("#btnFinalizar").removeAttr("disabled");
                Postulante.blinkButton(1);

                $('html,body').animate({
                    scrollTop: $("form").offset().top + 500
                }, 2000);

            }, 2000);
        },
        blinkButton: function (sw) {
            setTimeout(function () {
                if (sw == 1) {
                    $("#btnFinalizar").removeClass("btn-default");
                    $("#btnFinalizar").addClass("btn-danger");
                } else {
                    $("#btnFinalizar").addClass("btn-default");
                    $("#btnFinalizar").removeClass("btn-danger");
                }
                Postulante.blinkButton(sw == 1 ? 0 : 1);
            }, 600);
        }
    };

    $("body").delegate(".download", "click", function () {
        Postulante.download();
    });

});