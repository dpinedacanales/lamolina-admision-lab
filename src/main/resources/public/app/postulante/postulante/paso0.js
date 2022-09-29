$(function () {

    Postulante = {
        form: "formPostulante",
        sendForm: function (paso, e) {

            e.preventDefault();
            var form = $("#" + Postulante.form);
            form.parsley().destroy();
            form.parsley();
            if (!form.parsley().validate()) {
                return;
            }
            var content = '<b style="font-size: 17px;">Recuerda completar correctamente tus datos, todo cambio posterior a tu registro tendrá un costo adicional.</b>';
            if (esSimulacro) {
                content = '<b style="font-size: 17px;">Recuerda completar correctamente tus datos. No podrás realizar ningún cambio posterior.</b>';
            }
            swal({
                title: "¡Advertencia!",
                html: content,
                type: "warning",
                showCancelButton: false,
                confirmButtonColor: "#04B45F",
                confirmButtonText: 'De acuerdo'
            }).then((result) => {
                if (result.value) {
                    $(".boton").attr("disabled", true);
                    $(".boton").addClass('disabled');
                    $("#spin").removeClass('hide');
                    $.ajax({
                        url: APP.url('inscripcion/postulante/savePaso/' + paso + '/paso'),
                        type: 'POST',
                        async: false,
                        data: form.serialize(),
                        success: function (response) {
                            if (response.success) {
                                location.href = APP.url('inscripcion/postulante/siguientePaso');
                            } else {
                                $(".boton").attr("disabled", false);
                                $(".boton").removeClass('disabled');
                                $("#spin").addClass('hide');
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
                            $(".boton").attr("disabled", false);
                            $(".boton").removeClass('disabled');
                            $("#spin").addClass('hide');
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
            });
        }
    };

    $(".numerico").numeric({negative: false});

    $("body").delegate(".send-form", "click", function (e) {
        Postulante.sendForm(0, e);
    });

});