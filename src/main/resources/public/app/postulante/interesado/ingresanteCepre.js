$(function () {
    Cepre = {
        enviarCepre: function ($this, e) {
            var form = $("#formCepre");
            form.parsley().destroy();
            form.parsley();
            if (!form.parsley().validate()) {
                return;
            }

            swal({
                title: "!Advertencia!",
                html: '¿Estás seguro que tus datos de ingresante CEPRE son los correctos?',
                type: "info",
                showCancelButton: true,
                confirmButtonColor: "#04B45F",
                confirmButtonText: 'Sí, estoy seguro',
                cancelButtonText: "No, voy a revisar",
                reverseButtons: true
            }).then((isConfirm) => {
                console.log(isConfirm)
                if (isConfirm) {
                    $.ajax({
                        url: APP.url('inscripcion/facebook/verificarCepre'),
                        type: 'POST',
                        async: false,
                        data: form.serialize(),
                        success: function (response) {
                            if (response.success) {
                                location.href = APP.url("inscripcion/postulante/0/paso");
                            } else {
                                notify(response.message, "error");
                            }
                        },
                        error: function () {
                            notify(MESSAGES.errorComunicacion, "error");
                        }
                    });

                }
            });
        },
        regresarProcesoGeneral: function ($this, e) {
            swal({
                title: "!Advertencia!",
                html: '¿Estás seguro que no eres estudiante CEPRE?',
                type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#DD6B55",
                confirmButtonText: 'Sí, estoy seguro',
                cancelButtonText: "Cancelar"
            }).then((isConfirm) => {
                if (isConfirm) {
                    location.href = APP.url("inscripcion/facebook/opcion");
                }
            });
        }
    };

    $(".numerico").numeric({negative: false});

    $("body").delegate(".sin-espacios", "change", function () {
        APP.eliminarEspacios($(this));
    });

    $("body").delegate(".enviar-cepre", "click", function (e) {
        Cepre.enviarCepre($(this), e);
    });

    $("body").delegate(".no-es-cepre", "click", function (e) {
        Cepre.regresarProcesoGeneral($(this), e);
    });

});
