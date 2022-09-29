$(function () {

    Postulante = {
        init: function () {

        },
        atras: function () {
            location.href = APP.url("inscripcion/postulante/3/paso");
        },
        download: function () {
            location.href = APP.url("inscripcion/postulante/generateCartaCompromiso");
            setTimeout(function () {
                $("#btnFinalizar").removeAttr("disabled");
            }, 2000);
        },
        finzalizar: function () {
            $.ajax({
                url: APP.url('inscripcion/postulante/savePaso/4/paso'),
                type: 'POST',
                async: true,
                success: function (response) {
                    if (response.success) {
                        var paso = response.data.paso;
                        location.href = APP.url("inscripcion/postulante/" + paso + "/paso");
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        }
    };

    Postulante.init();

    $("body").delegate(".atras", "click", function () {
        Postulante.atras();
    });

    $("body").delegate(".download", "click", function () {
        Postulante.download();
    });

    $("body").delegate(".finazalizar", "click", function () {
        Postulante.finzalizar();
    });




});