$(function () {

    Postulante = {
        formulario: "formPostulante",
        body: $('body'),
        submitForm: function (e) {
            e.preventDefault();

            var titleswal = esSimulacro ? '<span class="font-cell">' +
                    '¿Seguro de que tus datos personales, institución, modalidad y carreras son correctos?</span>' :
                    '<span class="font-cell">' +
                    '¿Seguro de que tus datos personales, institución, modalidad y carreras a postular son correctos?</span>';

            if (esPre) {
                titleswal = '<span class="font-cell">' +
                        '¿Seguro de que tus datos personales son correctos?</span>';
            }

            var contenido = "<span class='bold'>Si deseas realizar un cambio posterior tendrás que efectuar un pago adicional</span>";
            if (esSimulacro) {
                contenido = "<span class='bold'>No podrá realizar un cambio posterior.</span>";
            }

            swal({
                title: titleswal,
                html: contenido,
                type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#04B45F",
                confirmButtonText: "¡Sí, acepto!",
                cancelButtonText: "Regresar y revisar mis datos",
                reverseButtons: true
            }).then(result => {
                if (result.value) {
                    $(".boton").attr("disabled", true);
                    $(".boton").addClass("disabled");
                    $("#spin").removeClass('hide');
                    setTimeout(function () {
                        Postulante.sendFormulario(5, e);
                    }, 1000);
                }
            });
        },
        sendTerm: function (btn, final) {

            var cartaId = btn.attr("ref");
            var cartaNombre = btn.attr("value");
            var count = parseInt(btn.attr("name")) + 1;
            var count2 = parseInt(btn.attr("name"));
            var sigCount = count == 5 ? "1, 2, 3, 4" : count;
           var text = count == 5 ? "¡Inscripción Exitosa! " :"¡Siga al paso " + sigCount + "!";
           var final2 = count == 5 ? "Usted aceptó los pasos " + sigCount + " de la declaración jurada" : "Usted aceptó el paso " + count2 + " de la declaración jurada." ;
            var form = $("#wizardform");

            swal({
                title: "Acepto las disposiciones " + cartaNombre.toLowerCase() + " de la declaración jurada",
                html: "¿Seguro que desea aceptar las disposiciones " + cartaNombre.toLowerCase() + " de la declaración jurada?",
                type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#04B45F",
                confirmButtonText: "¡Sí, acepto!",
                cancelButtonText: "No, volver a leer",
                reverseButtons: true
            }).then(result => {
                if (result.value) {
                    $.ajax({
                        url: APP.url('inscripcion/postulante/saveTerminos/' + cartaId),
                        type: 'POST',
                        async: false,
                        data: form.serialize(),
                        success: function (response) {
                            if (!response.success) {
                                swal({
                                    title: "¡Error!",
                                    html: response.message,
                                    type: "error",
                                    confirmButtonColor: "#DD6B55",
                                    confirmButtonText: 'Aceptar'
                                });
                            } else {
                                swal({
                                    title:  text,
                                    html: final2,
                                    type: "success",
                                    confirmButtonColor: "#DD6B55",
                                    confirmButtonText: 'Aceptar'
                                }).then(result => {
                                    if (result.value) {
                                        if (final) {
                                            Postulante.sendFormularioTerm(5);
                                        } else {

                                            Postulante.goNextStep(btn);
                                        }
                                    }
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
            });

        },
        sendFormulario: function (paso, e) {
            e.preventDefault();
            var form = $("#" + Postulante.formulario);

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
                        $(".boton").attr("disabled", false);
                        $(".boton").removeClass("disabled");
                        $("#spin").addClass('hide');
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
                    $(".boton").attr("disabled", false);
                    $(".boton").removeClass("disabled");
                    $("#spin").addClass('hide');
                }
            });
        },
        sendFormularioTerm: function (paso) {

            var form = $("#wizardform");

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
                        $(".boton").attr("disabled", false);
                        $(".boton").removeClass("disabled");
                        $("#spin").addClass('hide');
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
                    $(".boton").attr("disabled", false);
                    $(".boton").removeClass("disabled");
                    $("#spin").addClass('hide');
                }
            });
        },
        init() {
            var verMsg = $("#verTerminos").val() == "true";
            if (verMsg) {
                swal({
                    title: "¡Aviso!",
                    html: 'Nuevos términos y condiciones para ingresantes',
                    type: "info",
                    confirmButtonColor: "#DD6B55",
                    confirmButtonText: 'Aceptar'
                });
            }
            Postulante.makeProgress();
            $('.f1 fieldset:first').fadeIn('slow');
        },
        findNextStep: function (numStep) {
            var step = null;
            var buscar = false;
            $(".f1-step").each(function (i, item) {
                if (buscar && !$(item).hasClass("hide")) {
                    step = $(item);
                    buscar = false;
                }
                if ($(item).attr("ref") == numStep) {
                    buscar = true;
                }
            });
            return step;
        },
        findDivStep: function (numStep) {
            var div = null;
            $("fieldset").each(function (i, item) {
                var item = $(item);
                if (item.attr("ref") == numStep) {
                    div = $(item);
                }
            });
            return div;
        },
        makeProgress: function () {
            var progressLine = $('.f1-progress-line');
            var stepLine = $('.f1-step');
            var cant = 0;
            $(".f1-step").each(function (i, item) {
                if (!$(item).hasClass("hide")) {
                    cant++;
                }
            });
            console.log(cant);
            var width = 100 / (cant * 2);
            var w2 = 100 / cant;

            stepLine.attr('style', 'width: ' + w2 + '%;');

            progressLine
                    .attr('style', 'width: ' + width + '%;')
                    .data('now-value', width)
                    .data('number-of-steps', cant);

            Postulante.recountStep();


        },
        recountStep: function () {
            var number = 1;
            var numStep = 0;
            $(".f1-step").each(function (i, item) {
                if (!$(item).hasClass("hide")) {
                    var stepIcon = $(item).find(".f1-step-icon");
                    numStep = $(item).attr("ref");
                    var divStep = Postulante.findDivStep(numStep);
                    var btnSubmit = divStep.find(".btn-submit");
                    var btnNext = divStep.find(".btn-next");
                    btnSubmit.addClass("hide");
                    btnNext.removeClass("hide");
                    stepIcon.html(number);
                    number++;
                }
            });
            var divStep = Postulante.findDivStep(numStep);
            var btnSubmit = divStep.find(".btn-submit");
            var btnNext = divStep.find(".btn-next");
            btnSubmit.removeClass("hide");
            btnNext.addClass("hide");
        },
        goNextStep: function (btn) {
            var parentFieldset = btn.parents('fieldset');
            var numStep = parentFieldset.attr("ref");
            var currentActiveStep = btn.parents('.f1').find('.f1-step.active');
            var progressLine = btn.parents('.f1').find('.f1-progress-line');

            parentFieldset.fadeOut(400, function () {
                currentActiveStep.removeClass('active').addClass('activated');
                var stepNext = Postulante.findNextStep(numStep);
                stepNext.addClass('active');
                barProgress(progressLine, 'right');

                var nroDivItem = stepNext.attr("ref");
                var divNextStep = Postulante.findDivStep(nroDivItem);
                divNextStep.fadeIn(1000);
                scrollToClass($('.f1'), 20);
            });
        }
    };

    $(".numerico").numeric({negative: false});

    $("body").delegate(".send-form", "click", function (e) {
        Postulante.submitForm(e);
    });

    $('.f1 .btn-next').on('click', function (e) {
        Postulante.sendTerm($(this), false, e);

    });

    $('.f1 .btn-submit').on('click', function (e) {
        Postulante.sendTerm($(this), true, e);

    });

    Postulante.init();

});