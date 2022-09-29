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

            $.ajax({
                url: APP.url('inscripcion/postulante/savePaso/' + paso + '/paso'),
                type: 'POST',
                async: false,
                data: form.serialize(),
                success: function (response) {
                    if (response.success) {
                        location.href = APP.url('inscripcion/postulante/siguientePaso');
                    } else {
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

    $("body").delegate(".send-form", "click", function (e) {
        Postulante.sendForm(2, e);
    });

});