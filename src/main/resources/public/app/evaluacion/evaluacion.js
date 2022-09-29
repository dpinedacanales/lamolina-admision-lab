$(function () {

    var Evaluacion = {
        act: parseInt($('#meAct').val()),
        init: function () {

            var refresh = parseInt($('#meRef').val());
            setInterval(function () {
                Evaluacion.contador();
            }, refresh);

            setInterval(function () {
                Evaluacion.act++;
            }, 1000);

            $("input[type='radio']:checked").each(function () {
                var tab = $("#tabx-" + $(this).attr("ref"));
                tab.addClass("text-danger bold");
            });
        },
        saveRespuesta: function (opcion) {

            var padre = opcion.parents('.radio:first');
            var padreGroup = opcion.parents('.form-group:first').parents('div:first');

            padreGroup.find('.fa-spinne').addClass('hidden');
            padreGroup.find('.fa-close').addClass('hidden');
            padreGroup.find('.fa-check-circle').addClass('hidden');

            padre.find('.fa-spinner').removeClass('hidden');
            var tab = $("#tabx-" + opcion.attr("ref"));

            $.ajax({
                url: APP.url('inscripcion/evaluacion/saveRespuesta'),
                type: 'POST',
                data: {
                    opcion: opcion.val(),
                    pregunta: opcion.attr('name')
                },
                success: function (response) {
                    if (response.success) {

                        padre.find('.fa-check-circle').removeClass('hidden');
                        padre.find('.fa-spinner').addClass('hidden');
                        tab.addClass("text-danger bold");

                    } else {

                        padre.find('.fa-close').removeClass('hidden');
                        padre.find('.fa-spinner').addClass('hidden');

                    }
                },
                error: function () {

                    padre.find('.fa-spinner').addClass('hidden');
                    padre.find('.fa-check-circle').addClass('hidden');
                    padre.find('.fa-close').removeClass('hidden');

                }
            });

        },
        reenviarRespuesta: function (opcion) {

            var padre = opcion.parents('.radio:first');
            Evaluacion.saveRespuesta(padre.find('input[type=radio]:first'));

        },
        validarForm: function () {
            var valid = true;
        },
        finalizar: function () {

            swal({
                title: " ",
                html: "¿Seguro que desea finalizar la evaluación?",
                type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#DD6B55",
                confirmButtonText: "Sí, finalizar",
                cancelButtonText: "Cancelar",
                closeOnConfirm: false,
                showLoaderOnConfirm: true
            }).then(result => {
                if (result.value) {
                    Evaluacion.submitForm();
                }
            });

        },
        submitForm: function () {
            var form = $('<form>', {method: 'POST', ACTION: APP.url('inscripcion/evaluacion/finalizar')});
            form.hide();
            form.appendTo('body');
            form.submit();
        },
        contador: function () {
            $.ajax({
                url: APP.url('inscripcion/evaluacion/counterdown'),
                type: 'POST',
                data: {
                    tiempo: Evaluacion.act
                }
            });
        },
        otraPregunta: function (e) {

            var box = bootbox.alert({
                title: "Seleccione el número de pregunta",
                message: " ",
                size: 'large',
                buttons: {
                    ok: {
                        label: 'Cerrar',
                        className: 'btn-link'
                    }
                },
            });

            box.find('.modal-header').addClass('bg-white');
            var html = $('#wizardform').find('.navbar:first').clone();
            var htmlshow = html.removeClass('hidden');

            box.find('.bootbox-body').append(htmlshow);
            box.find('a.collapse-menu').click(function (e) {
                var numOpcionString = $(this).text();
                var numOpcion = parseInt(numOpcionString);
                $('#wizardform').bootstrapWizard('show', numOpcion - 1);
                box.modal('hide');
            });
        }
    };

    $('#wizardform').bootstrapWizard({
        onNext: function (tab, navigation, index) {
            return true;
        },
        onTabClick: function (tab, navigation, index) {
            return true;
        },
        onTabShow: function (tab, navigation, index) {

            $('#wizardform .finish').hide();

            var total = navigation.find('li').length;
            var current = index + 1;

            if (total === current) {
                $('#wizardform .next').hide();
                $('#wizardform .finish').show().removeClass('disabled');

            } else {
                $('#wizardform .next').show();
                $('#wizardform .finish').hide();
            }

        },
    });

    Evaluacion.init();

    var onlyOne = true;
    var lim = $('#meLim').val();

    EvaluacionContador = $('#clock').countdown(lim, function (event) {

        $(this).html(event.strftime('%H:%M:%S'));

    }).on('finish.countdown', function (event) {
        if (onlyOne) {

            swal({
                title: "A finalizado su evaluación",
                text: " ",
                type: "warning",
                confirmButtonColor: "#DD6B55",
                confirmButtonText: "Aceptar",
                closeOnConfirm: false
            }).then(result => {
                Evaluacion.submitForm();
            });

            onlyOne = false;
        }

    });

    $('body').delegate("#wizardform  input:radio", 'change', function (e) {
        Evaluacion.saveRespuesta($(this));
    });

    $('body').delegate("#wizardform .finish", 'click', function (e) {
        Evaluacion.finalizar();
    });

    $('body').delegate("#wizardform .otro", 'click', function (e) {
        Evaluacion.otraPregunta();
    });

    $('body').delegate("#finalizar", 'click', function (e) {
        Evaluacion.finalizar();
    });

    $('body').delegate(".fa-close", 'click', function (e) {
        Evaluacion.reenviarRespuesta($(this));
    });

    $('body').delegate("li>a", 'click', function (e) {
        e.preventDefault();
    });

});
