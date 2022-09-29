$(function () {
    var page = $.cookie("page");
    var title = $.cookie("title");
    var objectId = $.cookie("objectId");
    var btnBusy = false;

    Interesado = {
        labelButton: "Registrarse",
        init: function () {
            var tInscr = $("#preInscripcion");
            var advVirtual = $("#advertenciaVirtual");
            var lblCorreo = $("#lblCorreo");
            var correo = $("#correo");
            var tTaller = $("#titTaller");
            var insTaller = $("#insTaller");
            var form = $("#formInteresado");
            var btn = $("#btnSaveInteresado");
            var idTaller = $("#idTaller");
            var tipoDoc = form.find("[name*='tipoDocumento.id']");
            var numDoc = form.find("[name*=numeroDocIdentidad]");
            var caInte = form.find("[name*='carreraInteres.id']");
            var caNew = form.find("[name*='carreraNueva.id']");
            var codVer = form.find("[name*='codigoVerificacion']");

            if (page === "/talleres" || page === "/" || page === "/inicio") {
                tInscr.addClass('hidden');
                advVirtual.addClass('hidden');
                tTaller.removeClass('hidden');
                lblCorreo.removeClass('color-correo');
                correo.removeClass('color-correo');
                insTaller.removeClass('hidden');
                tTaller.append(title);
                idTaller.val(objectId);
                $("#tallerDetails").removeClass('hidden');
                btn.text("Inscribirse");
                tipoDoc.prop("required", true);
                numDoc.prop("required", true);
                caInte.prop("required", true);
                caNew.prop("required", true);
                codVer.prop("required", true);
                labelButton = "Inscribirse";
            }
            Interesado.carreraNueva();
        },
        carreraNueva: function () {
            $('#formInteresado').find('select[name="carreraNueva.id"]').on("change", function (e) {
                var self = $(e.currentTarget);

                var template = '<label>Especifique el nombre de la carrera</label>' +
                        '<input type="text" maxlength="199" class="form-control" name="otraCarrera" required="true"/>';

                if (self.val() < 0) {
                    $('#especificarCarrera').html(template);
                } else {
                    $('#especificarCarrera').html('');
                }
                $('#formInteresado').parsley('destroy');
                $('#formInteresado').parsley();
            });

        },
        registrar: function ($this, e) {
            e.preventDefault();
            var correo = document.getElementById("correo").value;

            /* if (correo.substr(-3) !== 'com' && correo.substr(-2) !== 'pe') {
             swal({
             title: "¡Error!",
             html: '<span style="font-size:18px;">' + 'Ingresar un correo correcto ".com" o ".pe" ' + '<span>',
             type: "error",
             confirmButtonColor: "#DD6B55",
             confirmButtonText: 'Aceptar'
             });
             return;
             }
             if (btnBusy) {
             return;
             }
             btnBusy = true;*/

            /*if (correo.substr(-10) !== '@gmail.com') {
             swal({
             title: "¡Error!",
             //html: '<span style="font-size:18px;">' + 'Ingresar un correo correcto como indica la declaración jurada "@gmail.com" ' + '<span>',
             html: '<span style="font-size:18px;">' + 'Ingresar un correo gmail "@gmail.com" ' + '<span>',
             type: "error",
             confirmButtonColor: "#DD6B55",
             confirmButtonText: 'Aceptar'
             });
             return;
             }
             if (btnBusy) {
             return;
             }
             btnBusy = true;*/

            $this.attr("disabled", "true");
            $("#cancelar").addClass("disabled");
            $this.html('<i class="fa fa-spinner fa-spin"></i> Procesando...');

            var form = $("#formInteresado");
            var pat = form.find("[name*=paterno]");
            var mat = form.find("[name*=materno]");

            pat.prop("required", false);
            mat.prop("required", false);

            if ((pat.val() + mat.val()) === "") {
                pat.prop("required", true);
                mat.prop("required", true);
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

            var virtual = document.getElementById("virtual").value;

            if (virtual == "true" && (page !== "/talleres" && page !== "/" && page !== "/inicio")) {
                var contenido = document.getElementById("contenido").value;

                Interesado.declaracionVirtual(form, contenido, $this);
                return;
            }
            //  form.submit();
            Interesado.sumit();
        },
        sumit() {

            var form = $("#formInteresado");
            $.ajax({
                url: APP.url('contingencia' + rutaForm),
                type: 'POST',
                async: false,
                data: form.serialize(),
                success: function (response) {
                    if (response.success) {
                        location.href = APP.url(response.data);
                    } else {
                        swal({
                            title: "¡Inscripciones Cerradas!",
                            html: response.message,
                            type: "error",
                            confirmButtonColor: "#DD6B55",
                            confirmButtonText: 'Aceptar',
                            allowOutsideClick: false
                        }).then((result) => {
                            document.location.href = "/";
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
        },
        declaracionVirtual: function (form, contenido, $this) {
            const wrapper = document.createElement('div');
            wrapper.innerHTML = contenido;
            swal({
                html: wrapper,
                type: "warning",
                showCancelButton: true,
                allowOutsideClick: false,
                confirmButtonColor: "#04B45F",
                confirmButtonText: 'Acepto',
                cancelButtonText: 'No',
                customClass: 'swal-wide'
            }).then((result) => {
                if (result.value) {
                    form.submit();
                } else {
                    $this.removeAttr("disabled");
                    $("#cancelar").removeClass("disabled");
                    $this.html(Interesado.labelButton);
                    btnBusy = false;
                    return;
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
