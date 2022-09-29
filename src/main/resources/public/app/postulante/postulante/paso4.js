$(function () {

    Postulante = {
        checked: function () {
            var radio = $('input[name="metalesPostulante.opcion"]')
            var checkedValue = radio.filter(':checked').val();
            if (checkedValue == 'SI') {
                $("#explicacao").attr("disabled", false);
                $("#explicacao").attr("readonly", false);
                $("#explicacao").attr("required", true);
                $("#objeto").attr("disabled", false);
                $("#objeto").attr("readonly", false);
                $("#objeto").attr("required", true);
                $("#div-explicacion").removeClass("hide");
            } else {
                $("#explicacao").attr("disabled", true);
                $("#explicacao").val('');
                $("#explicacao").attr("required", false);
                $("#objeto").attr("disabled", true);
                $("#objeto").val('');
                $("#objeto").attr("required", false);
                $("#div-explicacion").addClass("hide");
            }
            $("#submitForm").attr("disabled", false);
        },
        submitForm: function (e) {
            e.preventDefault();

            var radio = $('input[name="metalesPostulante.opcion"]')
            var checkedValue = radio.filter(':checked').val();
            if (checkedValue == 'SI') {
                var titleswal = '<span class="font-cell" style="width: 800px;"><span class="bold text-danger">¡¡Recuerda!!</span><br>';
                titleswal += 'Enviar un correo hasta el 03 de agosto a <b>inscripcionespregrado@lamolina.edu.pe</b>,';
                titleswal += ' asunto objeto metálico, adjunte su preinscripción médica y adjunte su DNI.</span>';

                swal({
                    title: titleswal,
                    html: "",
                    type: "warning",
                    width: '700px',
                    showCancelButton: false,
                    confirmButtonColor: "#04B45F",
                    confirmButtonText: "Estoy de acuerdo",
                    cancelButtonText: "No podré llevarlo",
                    reverseButtons: true
                }).then((result) => {
                    if (result.value) {
                        setTimeout(function () {
                            Postulante.checkStatus(e);
                        }, 800);
                    }
                });

            } else {
                Postulante.checkStatus(e);
            }


        },
        checkStatus: function (e) {
            e.preventDefault();

            var form = $("#form");

            form.parsley().destroy();
            form.parsley();
            if (!form.parsley().validate()) {
                return;
            }

            $(".boton").attr('disabled', true);
            $(".boton").addClass('disabled');
            $("#spin").removeClass('hide');

            $.ajax({
                url: APP.url('inscripcion/postulante/savePaso/' + 4 + '/paso'),
                type: 'POST',
                async: false,
                data: form.serialize(),
                success: function (response) {
                    if (response.success) {
                        location.href = APP.url('inscripcion/postulante/siguientePaso');
                    } else {
                        $(".boton").attr('disabled', false);
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
                    $(".boton").attr('disabled', false);
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

        },
        init() {
            var radio = $('input[name="metalesPostulante.opcion"]')
            var checkedValue = radio.filter(':checked').val();
            console.log("checkedValue")
            console.log(checkedValue)
            if (checkedValue == 'SI') {

                $("#explicacao").attr("disabled", false);
                $("#explicacao").attr("readonly", false);
                $("#explicacao").attr("required", true);
                $("#objeto").attr("disabled", false);
                $("#objeto").attr("readonly", false);
                $("#objeto").attr("required", true);
                $("#div-explicacion").removeClass("hide");
            }

            if ($('.opcion:checked').length > 0) {
                $("#submitForm").attr("disabled", false);
                console.log("checkado")
            }

        }

    };


    $("body").delegate(".opcion", "click", function (e) {
        Postulante.checked();
    });
    $("body").delegate("#submitForm", "click", function (e) {
        Postulante.submitForm(e);
    });
    Postulante.init();

});