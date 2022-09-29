$(function () {

    Compromiso = {
        body: $('body'),
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
        init() {
            Compromiso.makeProgress();
            $('.f1 fieldset:first').fadeIn('slow');
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

            Compromiso.recountStep();


        },
        recountStep: function () {
            var number = 1;
            var numStep = 0;
            $(".f1-step").each(function (i, item) {
                if (!$(item).hasClass("hide")) {
                    var stepIcon = $(item).find(".f1-step-icon");
                    numStep = $(item).attr("ref");
                    var divStep = Compromiso.findDivStep(numStep);
                    var btnSubmit = divStep.find(".btn-submit");
                    var btnNext = divStep.find(".btn-next");
                    btnSubmit.addClass("hide");
                    btnNext.removeClass("hide");
                    stepIcon.html(number);
                    number++;
                }
            });
            var divStep = Compromiso.findDivStep(numStep);
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
                var stepNext = Compromiso.findNextStep(numStep);
                stepNext.addClass('active');
                barProgress(progressLine, 'right');

                var nroDivItem = stepNext.attr("ref");
                var divNextStep = Compromiso.findDivStep(nroDivItem);
                divNextStep.fadeIn(1000);
                scrollToClass($('.f1'), 20);
            });
        }
    };

    $(".numerico").numeric({negative: false});

    $("body").delegate(".send-form", "click", function (e) {
        Compromiso.submitForm(e);
    });

    $('.f1 .btn-next').on('click', function (e) {
        Compromiso.goNextStep($(this));
    });

    $('.f1 .btn-submit').on('click', function (e) {
        Compromiso.sendTerm($(this), true, e);

    });

    Compromiso.init();

});