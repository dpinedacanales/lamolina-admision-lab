$(function () {
    var page = $.cookie("page");
    var title = $.cookie("title");
    var objectId = $.cookie("objectId");
    var btnBusy = false;

    Interesado = {
        labelButton: "Registrarse",
        init: function () {
            var tInscr = $("#preInscripcion");
            var form = $("#formInteresadoContingencia");
            var btn = $("#btnSaveInteresado");
            var tipoDoc = form.find("[name*='tipoDocumento.id']");
            var numDoc = form.find("[name*=numeroDocIdentidad]");
            var codVer = form.find("[name*='codigoVerificacion']");
            if (page === "/talleres" || page === "/" || page === "/inicio") {
                tInscr.addClass('hidden');
                $("#tallerDetails").removeClass('hidden');
                btn.text("Inscribirse");
                tipoDoc.prop("required", true);
                numDoc.prop("required", true);

                labelButton = "Inscribirse";
            }
        },
        registrar: function ($this, e) {
            e.preventDefault();

            if (btnBusy) {
                return;
            }
            btnBusy = true;

            $this.attr("disabled", "true");
            $("#cancelar").addClass("disabled");
            $this.html('<i class="fa fa-spinner fa-spin"></i> Procesando...');

            var form = $("#formInteresadoContingencia");
            var tip = form.find("[name*='tipoDocumento.id']");
            var doc = form.find("[name*='numeroDocIdentidad']");
            var codVer = form.find("[name*='codigoVerificacion']");
        

            if ((tip.val() + doc.val() + codVer.val()) === "") {
                tip.prop("required", true);
                doc.prop("required", true);
                codVer.prop("required", true);
            }

            form.parsley().destroy();
            form.parsley();
            if (!form.parsley().validate()) {
                $this.removeAttr("disabled");
                $("#cancelar").removeClass("disabled");
                $this.html(Interesado.labelButton);
                btnBusy = false;
                return;
            }


            Interesado.sumit();
        },
        sumit() {

            var form = $("#formInteresadoContingencia");
            $.ajax({
                url: APP.url('contingencia/facebook/lagunas/login'),
                type: 'POST',
                async: false,
                data: form.serialize(),
                success: function (response) {
                    if (response.success) {
                        location.href = APP.url(response.data);
                    } else {
                        swal({
                            title: "¡Error!",
                            html: response.message,
                            type: "error",
                            confirmButtonColor: "#DD6B55",
                            confirmButtonText: 'Aceptar'
                        }).then((result) => {
                            document.location.href = "/";
                        });;
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
        },
        removerParametro: function (parameter) {
            var url = document.location.href;
            var urlparts = url.split('?');

            if (urlparts.length >= 2)
            {
                var urlBase = urlparts.shift();
                var queryString = urlparts.join("?");

                var prefix = encodeURIComponent(parameter) + '=';
                var pars = queryString.split(/[&;]/g);
                for (var i = pars.length; i-- > 0; )
                    if (pars[i].lastIndexOf(prefix, 0) !== -1)
                        pars.splice(i, 1);
                url = urlBase + '?' + pars.join('&');
                window.history.pushState('', document.title, url);

            }
            return url;
        },
        cancelar: function () {
            var objectid = $.cookie("objectId");
            var page = $.cookie("page");
            var title = $.cookie("title");
            if (page !== undefined) {
                $.removeCookie("page", {path: '/'});
            }
            if (objectid !== undefined) {
                $.removeCookie("objectId", {path: '/'});
            }
            if (title !== undefined) {
                $.removeCookie("title", {path: '/'});
            }

            setTimeout(function () {
                document.location.href = "/";
            }, 400);
        },
        changeTipoDocumento: function ($this) {
            var nroDocumento = $('[name="numeroDocIdentidad"]');

            if ($this) {
                $.ajax({
                    url: ('/comun/buscar/buscarDocumentoIdentidad'),
                    data: {id: $this.val()},
                    type: 'POST',
                    success: function (response) {
                        if (response.success) {
                            var data = response.data;

                            var lgtExacta = data.longitudExacta;
                            var longitud = data.longitud;

                            Interesado.validandoMinMaxDocumento(nroDocumento, lgtExacta, longitud);

                        } else {
                            notify(response.message, "error");
                        }
                    },
                    error: function () {
                        notify(MESSAGES.errorComunicacion, "error");
                    }
                });

            }
        },
        validandoMinMaxDocumento: function (nroDocumento, lgtExacta, longitud) {
            if (lgtExacta === 1) {
                nroDocumento.attr('data-parsley-minlength', longitud);
                nroDocumento.attr('data-parsley-maxlength', longitud);
                nroDocumento.attr('maxlength', longitud);
            } else {
                nroDocumento.attr('data-parsley-minlength', 4);
                nroDocumento.attr('data-parsley-maxlength', longitud);
                nroDocumento.attr('maxlength', longitud);
            }
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

    $("body").delegate("#btnSaveInteresado", "click", function (e) {
        Interesado.registrar($(this), e);
    });
    $("body").delegate("#cancelar", "click", function (e) {
        Interesado.cancelar();
    });

    $("body").delegate("[name='tipoDocumento.id']", "change", function () {
        Interesado.changeTipoDocumento($(this));
    });

    Interesado.init();

});
