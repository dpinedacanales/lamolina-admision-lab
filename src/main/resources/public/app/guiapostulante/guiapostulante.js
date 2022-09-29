$(function () {

    var btnBusy = false;

    GuiaPostulante = {
        enviar: function () {
            if (btnBusy) {
                return;
            }
            btnBusy = true;

            var form = $("#formInteresado");
            var tipo = $("#tipoGuiaPostulante").val();

            form.parsley().destroy();
            form.parsley();
            if (!form.parsley().validate()) {
                btnBusy = false;
                return;
            }

            $("#btnGuiaPostulante").addClass("disabled");
            $("#btnGuiaPostulante").html('<i class="fa fa-spinner fa-spin"></i> Procesando...');

            $.ajax({
                url: APP.url('inscripcion/guiapostulante/save'),
                type: 'POST',
                async: false,
                data: form.serialize(),
                success: function (response) {
                    if (response.success) {
                        location.href = APP.url('inscripcion/guiapostulante/' + tipo + '/tipo');
                    } else {
                        btnBusy = false;
                        $("#btnGuiaPostulante").removeClass("disabled");
                        $("#btnGuiaPostulante").html('Siguiente');

                        swal({
                            title: "¡Error!",
                            html: response.message,
                            type: "error",
                            confirmButtonColor: "#DD6B55",
                            confirmButtonText: 'Aceptar'
                        });
                    }
                },
                error: function () {
                    btnBusy = false;
                    $("#btnGuiaPostulante").removeClass("disabled");
                    $("#btnGuiaPostulante").html('Siguiente');
                    
                    swal({
                        title: "¡Error de comunicación!",
                        html: MESSAGES.errorComunicacion,
                        type: "error",
                        confirmButtonColor: "#DD6B55",
                        confirmButtonText: 'Aceptar'
                    });
                }
            });
        }
    };

    $(".numerico").numeric({negative: false});

    $("body").delegate(".nombre-persona", "change", function () {
        APP.revisarNombre($(this));
    });

    $("body").delegate(".sin-espacios", "change", function () {
        APP.eliminarEspacios($(this));
    });

    $("body").delegate(".revisar-email", "change", function () {
        APP.revisarEmail($(this));
    });

    $("#btnGuiaPostulante").click(function () {
        GuiaPostulante.enviar();
    });

});