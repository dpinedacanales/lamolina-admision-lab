$(function () {

    Postulante = {
        modalidax: {},
        colegioSelect: {},
        enviar: true,
        form: "formPostulante",
        opciones: [],
        primeraves: 1,
        carrs: [],
        body: $("body"),
        init: function () {

            Postulante.loadColegiosCOAR();
            Postulante.bucarColegioProcedencia();
            Postulante.loadModalidax();
            Postulante.buscarPais();

            $(".opcion-carrera").select2({allowClear: true});
            Postulante.verCarreras();
            $("#yearColegio").select2();
            $("#univ-peru").select2();
            var codePaisUniv = $("#codePaisUniv").val();
            Postulante.mostrarUniversidad(codePaisUniv);
            Postulante.distritoColegio();
            $("[name='modalidadIngreso.id']").select2({
                minimumInputLength: -1,
                allowClear: true
            });
            Postulante.initModalidad();
            Postulante.reloadOpciones();
        },
        loadModalidax() {
            var modalidad = $("[name='modalidadIngreso.id']").val();
            Postulante.modalidax = {};
            for (var i = 0; i < modalidadez.length; i++) {
                if (modalidadez[i].id == modalidad) {
                    Postulante.modalidax = modalidadez[i];
                }
            }
        },
        reloadOpciones: function () {
            $.each($('#' + Postulante.form + ' .opcion-carrera'), function (i, v) {
                if ($(v).val()) {
                    $(v).trigger('change');
                } else {
                    var opcionid = $(v).attr('rev');
                    if (opcionid) {
                        $(v).select2('val', opcionid);
                        $(v).trigger('change');
                    }
                }
            })
        },
        verCarreras: function () {
            var form = $("#" + Postulante.form);
            var carrs = carreras.slice();
            for (var i = 2; i <= 5; i++) {
                carrs = Postulante.loadCarreras(i, carrs, form);
            }
            Postulante.carrs = carrs;
        },
        loadCarreras: function (nro, carrs, form) {
            if (nro > 5) {
                return;
            }
            var opt1 = form.find("#opcion" + (nro - 1)).val();
            var carr2 = form.find("[name='opcionCarrera[" + (nro - 1) + "].carreraPostula.id']");
            if (opt1 == "") {
                carr2.empty();
                carr2.select2("val", null);
                return carrs;
            }
            var opt2 = $("#opcion" + nro).val();
            Postulante.opciones.push(parseInt(opt1));
            var idx = -1;
            $.each(carrs, function (i, v) {
                if (v.id == opt1) {
                    idx = i;
                }
            });
            if (idx > -1) {
                carrs.splice(idx, 1);
            }
            carr2.html('');
            carr2.append('<option></option>');
            carr2.append($.map(carrs, function (v, i) {
                return $('<option>', {val: v.id, text: v.nombre});
            }));
            carr2.select2("val", opt2);
            return carrs;
        },
        loadColegiosCOAR() {
            if (colegiosCOAR.length > 0) {
                return;
            }

            $.ajax({
                url: APP.url('comun/buscar/colegiosCOAR'),
                type: 'POST',
                success: function (response) {
                    if (response.success) {
                        colegiosCOAR = response.data;
                        for (var i = 0; i < colegiosCOAR.length; i++) {
                            colegiosCOAR[i].text = colegiosCOAR[i].nombre;
                        }
                        if (Postulante.modalidax.tipo == "COAR") {
                            Postulante.bucarColegioProcedencia();
                        }
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        bucarColegioProcedencia: function () {
            if (Postulante.modalidax.tipo == "COAR") {

                $("#colegio-procedencia").select2({
                    minimumInputLength: 0,
                    data: colegiosCOAR,
                    initSelection: function (element, callback) {
                        if (element.val() != "") {
                            callback({
                                id: element.val(),
                                nombreLargo: element.attr("rel"),
                                ubicacion: element.attr("nombre-ubicacion"),
                                idUbicacion: element.attr("id-ubicacion")
                            });
                        }
                    },
                    formatResult: function (info) {
                        var data = '<span class="block text-success bold">' + info.nombre + '</span>';
                        data += '<span class="block"><strong>Cód.Modular:</strong> ' + info.codigoModular;
                        data += " - <strong>Anexo:</strong> " + info.anexo + ' - <strong>Atención:</strong> ' + info.formaAtencion;
                        data += " - <strong>Gestión:</strong> " + info.gestion + ' </span>';
                        data += '<span class="block"><strong>Modalidad:</strong> ' + info.nivelModalidad + ' - <strong>Dirección:</strong> ' + info.direccion + '</span>';
                        return data;
                    },
                    formatSelection: function (info) {
                        $("#distritoColegioCoar").val(info.ubicacion);
                        Postulante.colegioSelect = info;
                        return info.nombreLargo;
                    },
                    escapeMarkup: function (m) {
                        return m;
                    }
                });
                return;
            }

            $("#colegio-procedencia").select2({
                minimumInputLength: 2,
                ajax: {
                    url: APP.url("comun/buscar/colegioProcedencia"),
                    dataType: 'json',
                    type: 'post',
                    data: function (term, page) {
                        console.log("????");
                        var distrito = $("#distritoColegio").select2('val');
                        var modalidad = $("[name='modalidadIngreso.id']").val();
                        return {nombre: term, page: page, distrito: distrito, modalidad: modalidad};
                    },
                    results: function (response, page) {
                        console.log("????");
                        return {results: response.data};
                    }
                },
                initSelection: function (element, callback) {
                    if (element.val() != "") {
                        callback({
                            id: element.val(),
                            nombreLargo: element.attr("rel"),
                            ubicacion: element.attr("nombre-ubicacion"),
                            idUbicacion: element.attr("id-ubicacion")
                        });
                    }
                },
                formatResult: function (info) {
                    var data = '<span class="block text-success bold">' + info.nombre + '</span>';
                    data += '<span class="block"><strong>Cód.Modular:</strong> ' + info.codigoModular;
                    data += " - <strong>Anexo:</strong> " + info.anexo + ' - <strong>Atención:</strong> ' + info.formaAtencion;
                    data += " - <strong>Gestión:</strong> " + info.gestion + ' </span>';
                    data += '<span class="block"><strong>Modalidad:</strong> ' + info.nivelModalidad + ' - <strong>Dirección:</strong> ' + info.direccion + '</span>';
                    return data;
                },
                formatSelection: function (info) {
                    Postulante.colegioSelect = info;
                    return info.nombreLargo;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            });
        },
        mostrarUniversidad: function (codigo) {
            if (codigo == "") {
                return;
            }
            var form = $("#" + Postulante.form);
            var unipe = form.find("[name='universidadProcedencia.id']");
            var uniex = form.find("[name='universidadExtranjera']");
            $("#universidadPeruana").removeClass("hide");
            $("#universidadExtranjera").removeClass("hide");
            $("#univ-peru").removeAttr("required");
            $("#universidadExtranjeraName").removeAttr("required");
            if (codigo == "PE") {
                uniex.val("");
                $("#universidadExtranjera").addClass("hide");
                $("#univ-peru").attr("required", "true");
            } else {
                unipe.select2("val", "");
                $("#universidadPeruana").addClass("hide");
                $("#universidadExtranjeraName").attr("required", "true");
            }
        },
        mostrarColegio: function (codigo) {

            if (codigo == "") {
                return;
            }

            var form = $("#" + Postulante.form);
            form.parsley().destroy();
            $("#distritoColegio").select2('val', '');
            $("#distritoColegioCoar").val('');
            $("#codigoModular").val('');
            $("#colegio-procedencia").select2('val', '');
            $("#grupoColegioNac").addClass("hide");
            $("#grupoColegioExt").addClass("hide");
            $("#distritoColegio").removeAttr("required");
            $("#codigoModular").removeAttr("required");
            $("#colegio-procedencia").removeAttr("required");
            $("#colegioExtranjero").removeAttr("required");
            $("#codigoAlumno").removeAttr("required");
            if (codigo == "PE") {
                $("#grupoColegioNac").removeClass("hide");
                if (Postulante.modalidax.tipo != "COAR") {
                    $("#distritoColegio").attr("required", "true");
                }
                $("#codigoModular").attr("required", "true");
                $("#colegio-procedencia").attr("required", "true");

                if ($("#codigoAlumno").attr('rel') != 2) {
                    $("#codigoAlumno").attr("required", "true");
                }

            } else {
                $("#grupoColegioExt").removeClass("hide");
                $("#colegioExtranjero").attr("required", "true");
            }

            form.parsley();
        },
        buscarPais: function () {

            $(".buscar-pais").select2({
                minimumInputLength: 2,
                ajax: {
                    url: APP.url("comun/buscar/allPaisesColegio"),
                    dataType: 'json',
                    type: 'post',
                    data: function (term, page) {
                        return {nombre: term, page: page};
                    },
                    results: function (response, page) {
                        return {results: response.data};
                    }
                },
                initSelection: function (element, callback) {
                    if (element.val() != "") {
                        callback({id: element.val(), nombre: element.attr("rel"), codigo: element.attr("codigo")});
                    }
                },
                formatResult: function (info) {
                    return info.nombre + " | " + info.codigo;
                },
                formatSelection: function (info) {
                    return info.nombre;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            }).on("select2-selecting", function (e) {
                Postulante.mostrarUniversidad(e.choice.codigo);
            });

            $(".buscar-pais-colegio").select2({
                minimumInputLength: 2,
                ajax: {
                    url: APP.url("comun/buscar/allPaisesColegio"),
                    dataType: 'json',
                    type: 'post',
                    data: function (term, page) {
                        return {nombre: term, page: page};
                    },
                    results: function (response, page) {
                        return {results: response.data};
                    }
                },
                initSelection: function (element, callback) {
                    if (element.val() != "") {
                        callback({id: element.val(), nombre: element.attr("rel"), codigo: element.attr("codigo")});
                        Postulante.mostrarColegio(element.attr("codigo"));
                    }
                },
                formatResult: function (info) {
                    return info.nombre + " | " + info.codigo;
                },
                formatSelection: function (info) {

                    return info.nombre;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            }).on("select2-selecting", function (e) {
                Postulante.mostrarColegio(e.choice.codigo);
            });
        },
        modalidadIngresoChange: function (e) {
            Postulante.activarBtnSubmit();
            var self = $(e.target);
            var modalidad = self.val();
            if (!modalidad) {
                $('#detModalidad').html('');
                return;
            }

            $('#detModalidad').html('');
            $("#submitForm").attr('disabled', true);
            $("#submitForm").html('<i class="fa fa-spinner fa-spin" id="spin"/> Cargando modalidad...');
            $("#cancelar").addClass("disabled");

            Postulante.loadModalidax();

            $.ajax({
                url: APP.url('inscripcion/modalidad/modalidadCiclo'),
                type: 'POST',
                async: false,
                data: {modalidad: modalidad},
                success: function (response) {
                    $("#submitForm").attr('disabled', false);
                    $("#submitForm").html('<i class="fa fa-spinner fa-spin hide" id="spin"/> Siguiente');
                    $("#cancelar").removeClass("disabled");

                    if (response.success) {
                        $('#detModalidad').html(response.data);
                        $(".opcion-carrera").select2({allowClear: true});
                        Postulante.loadModalidadCiclo();
                        Postulante.buscarPais();
                        Postulante.distritoColegio();
                        Postulante.bucarColegioProcedencia();
                        $("#grado").trigger('change');
                        Postulante.resetParsley();
                    } else {
                        $('[name="modalidadIngreso.id"]').select2('val', "");
                        $('#detModalidad').html('');
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    $("#submitForm").attr('disabled', false);
                    $("#submitForm").html('<i class="fa fa-spinner fa-spin hide" id="spin"/> Siguiente');
                    $("#cancelar").removeClass("disabled");

                    $('[name="modalidadIngreso.id"]').select2('val', "");
                    $('#detModalidad').html('');
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
            Postulante.reloadOpciones();
        },
        loadModalidadCiclo: function () {

            $("#yearColegio").select2({
                minimumInputLength: -1,
                allowClear: true
            });
            $("#univ-peru").select2({
                minimumInputLength: -1,
                allowClear: true
            });
            $("#grado").select2({
                minimumInputLength: -1,
                allowClear: true
            });
            $("#modalidadSimu").select2({
                minimumInputLength: -1,
                allowClear: true
            });
        },
        opcionCarrera: function (e) {
            var self = $(e.target);
            var nro = parseInt(self.attr("rel"));
            var opt = self.val() == "" ? "" : parseInt(self.val());
            $("#opcion" + nro).val(opt);
            var form = $("#" + Postulante.form);
            for (var i = nro + 1; i <= 5; i++) {
                $("#opcion" + i).val("");
                var carr2 = form.find("[name='opcionCarrera[" + (i - 1) + "].carreraPostula.id']");
                carr2.empty();
                carr2.select2("val", "");
            }
            Postulante.verCarreras();
            Postulante.resetParsley();
        },
        initModalidad: function () {
            if ($("[name='modalidadIngreso.id']").val() != '') {
                $("[name='modalidadIngreso.id']").trigger('change');
            }
        },
        distritoColegio: function () {
            $("#distritoColegio").select2({
                minimumInputLength: 2,
                ajax: {
                    url: APP.url("comun/buscar/allDistritos"),
                    dataType: 'json',
                    type: 'post',
                    data: function (term, page) {
                        return {nombre: term, page: page};
                    },
                    results: function (response, page) {
                        return {results: response.data};
                    }
                },
                initSelection: function (element, callback) {
                    if (element.val() != "") {
                        callback({id: element.val(), nombre: element.attr("rel")});
                    }
                },
                formatResult: function (info) {
                    return info.nombre;
                },
                formatSelection: function (info) {
                    if (info.id != Postulante.colegioSelect.idUbicacion) {
                        $("#colegio-procedencia").select2("val", "");
                    }
                    return info.nombre;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            });
        },
        resetParsley: function () {
            var form = $("#" + Postulante.form);
            form.parsley().destroy();
            form.parsley();
        },
        queesesto: function () {

            bootbox.alert({
                message: 'El código del estudiante lo puede obtener solicitando un "Historial de Matrícula" a través de un Formulario Único de Trámite en la UGEL a la que pertenece dicho colegio.',
                buttons: {
                    ok: {
                        label: 'Aceptar',
                        className: 'btn-link'
                    }
                }
            });
        },
        activarBtnSubmit: function (e) {
            var modalidad = $('[name="modalidadIngreso.id"]').select2('val');
            $('#submitForm').attr('disabled', modalidad == '');
        },
        institutoProcedencia: function (e) {
            e.preventDefault();
            var self = $(e.target);
            var form = $("#" + Postulante.form);

            var modal = form.find("[name='modalidadIngreso.id']");

            $.ajax({
                url: APP.url('inscripcion/modalidad/institucionProcedencia'),
                type: 'POST',
                async: false,
                data: {modalidad: modal.val(), tipo: self.attr("rel")},
                success: function (response) {
                    if (response.success) {
                        $("#conteInstitucion").html(response.data);
                        Postulante.loadModalidadCiclo();
                        Postulante.buscarPais();
                        Postulante.distritoColegio();
                        Postulante.bucarColegioProcedencia();
                        Postulante.resetParsley();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        changeGrado: function (e) {
            e.preventDefault();
            var self = $(e.target);
            var orden = self.find(':selected').attr('rel');
            var form = $("#" + Postulante.form);
            form.parsley().destroy();
            if (orden == 5) {
                $('#yearColegio').removeAttr('disabled');
                $('#yearColegio').attr('required', true);
                $("#divYearColegio").removeClass("hide");
            } else {
                $('#yearColegio').removeAttr('required');
                $('#yearColegio').attr('disabled', 'true');
                $("#divYearColegio").addClass("hide");
            }
            form.parsley();
        },
        submitForm: function (e) {
            e.preventDefault();

            var form = $("#" + Postulante.form);
            form.parsley().destroy();
            form.parsley();
            if (!form.parsley().validate()) {
                return;
            }

            Postulante.sendForm(3, e);

        },
        sendForm: function (paso, e) {
            e.preventDefault();

            Postulante.enviar = false;
            $("#submitForm").attr('disabled', true);
            $("#spin").removeClass('hide');

            var form = $("#" + Postulante.form);
            $.ajax({
                url: APP.url('inscripcion/postulante/savePaso/' + paso + '/paso'),
                type: 'POST',
                async: false,
                data: form.serialize(),
                success: function (response) {

                    if (response.success) {
                        location.href = APP.url('inscripcion/postulante/siguientePaso');
                        Postulante.enviar = true;
                    } else {
                        $("#submitForm").attr('disabled', false);
                        $("#spin").addClass('hide');
                        swal({
                            title: "¡Error!",
                            html: response.message,
                            type: "error",
                            confirmButtonColor: "#DD6B55",
                            confirmButtonText: 'Aceptar'
                        });
                        Postulante.enviar = true;
                    }
                },
                error: function () {
                    $("#submitForm").attr('disabled', false);
                    $("#spin").addClass('hide');
                    swal({
                        title: "¡Error de comunicación!",
                        html: MESSAGES.errorComunicacion,
                        type: "error",
                        confirmButtonColor: "#DD6B55",
                        confirmButtonText: 'Aceptar'
                    });
                    Postulante.enviar = true;
                }
            });
        }
    };

    Postulante.body.delegate("[name='modalidadIngreso.id']", "change", function (e) {
        Postulante.modalidadIngresoChange(e);
    });

    Postulante.body.delegate(".opcion-carrera", "change", function (e) {
        Postulante.opcionCarrera(e);
    });

    Postulante.body.delegate("#queesesto", "click", function (e) {
        Postulante.queesesto(e);
    });

    Postulante.body.delegate(".institucion-procedencia", "change", function (e) {
        Postulante.institutoProcedencia(e);
    });

    Postulante.body.delegate("#grado", "change", function (e) {
        Postulante.changeGrado(e);
    });

    Postulante.body.delegate(".send-form", "click", function (e) {
        Postulante.submitForm(e);
    });

    Postulante.init();

    $('#scrolled').parent().parent().addClass("col-md-offset-2");
    $('#scrolled').parent().parent().addClass("col-md-8");
});
