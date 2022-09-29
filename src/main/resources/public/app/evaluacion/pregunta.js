$(function () {

    var Evaluacion = {
        act: parseInt($('#meAct').val()),
        init: function () {
            $('#wizardform .next').show();
            $('#wizardform .previous').show();
        },
        saveRespuesta: function (opcion) {

            var padre = opcion.parents('.radio:first');
            var padreGroup = opcion.parents('.form-group:first').parents('div:first');

            padreGroup.find('.fa-spinne').addClass('hidden');
            padreGroup.find('.fa-close').addClass('hidden');
            padreGroup.find('.fa-check-circle').addClass('hidden');

            padre.find('.fa-spinner').removeClass('hidden');

            setTimeout(function () {
                padre.find('.fa-check-circle').removeClass('hidden');
                padre.find('.fa-spinner').addClass('hidden');
            }, 800);

        },
        reenviarRespuesta: function (opcion) {

            var padre = opcion.parents('.radio:first');
            Evaluacion.saveRespuesta(padre.find('input[type=radio]:first'));

        },
        validarForm: function () {
            var valid = true;
        },
        finalizar: function () {

        },
        submitForm: function () {
            var form = $('<form>', {method: 'POST', ACTION: APP.url('inscripcion/evaluacion/finalizar')});
            form.hide();
            form.appendTo('body');
            form.submit();
        },
        contador: function () {

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
                }
            });

            box.find('.modal-header').addClass('bg-white');
            var html = $('#wizardform').find('.navbar:first').clone();
            var htmlshow = html.removeClass('hidden');

            box.find('.bootbox-body').append(htmlshow);
            box.find('a.collapse-menu').click(function (e) {
                var ruta = $(this).attr("href");
                location.href = APP.url(ruta.substring(1, 200));
            });
        }
    };

    $('#wizardform').bootstrapWizard({
        onNext: function (tab, navigation, index) {
            var next = $("#siguiente").val();
            location.href = APP.url('inscripcion/evaluacion/' + next + '/pregunta');
            return false;
        },
        onPrevious: function (tab, navigation, index) {
            var prev = $("#anterior").val();
            location.href = APP.url('inscripcion/evaluacion/' + prev + '/pregunta');
            return false;
        },
        onTabClick: function (tab, navigation, index) {
            return true;
        },
        onTabShow: function (tab, navigation, index) {
        }
    });

    Evaluacion.init();


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
