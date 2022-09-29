$(function () {

    Postulante = {
        form: "formPostulante",
        init: function () {
            Postulante.buscarDistrito();
            Postulante.buscarPais();
            $(".date").datepicker({
                endDate: $("#limiteFechaNacer").val()
            });
        },
        body: $("body"),
        buscarDistrito: function () {
            $(".buscar-distrito").select2({
                placeholder: "  ",
                allowClear: true,
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
                    var html = '<span class="block h4 bold">' + info.distrito + '</span>';
                    html += '<span class=""><strong>Provincia:</strong> ' + info.provincia + ' / ';
                    html += '<strong>Departamento:</strong> ' + info.departamento + '</span>';
                    return html;
                },
                formatSelection: function (info) {
                    return info.nombre;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            });
        },
        buscarPais: function () {
            $(".buscar-pais").select2({
                minimumInputLength: 2,
                ajax: {
                    url: APP.url("comun/buscar/allPaises"),
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
            });
        },
        mostrarDirNacimiento: function ($this) {
            var dataPaisNac = $("#paisNacimiento").select2("data");
            if (dataPaisNac.codigo === "PE") {
                $("#lugarNacimiento").removeClass("hide");
                $("#distNacimiento").prop('required', true);
            } else {
                $("#lugarNacimiento").addClass("hide");
                $("#distNacimiento").select2("val", "");
                $("#distNacimiento").prop('required', false);
            }
        },
        deleteMensaje: function () {
            $('#mensaje').text("");
        },
        showModalInit: function () {
            var msg = $("#mensajeError").val();
            if (msg == "") {
                if ($('[name="persona.numeroDocIdentidad"]').val() != '') {
                    return;
                }

                swal({
                    title: "¡Advertencia!",
                    html: '<span style="font-size:21px;">Estimado(a) postulante, verifica que tus nombres y apellidos estén completos e idénticos a tu DNI</span>',
                    type: "info",
                    confirmButtonText: 'Aceptar'
                });
            } else {
                swal({
                    title: "¡Duplicidad de postulación!",
                    html: msg,
                    type: "error",
                    confirmButtonColor: "#DD6B55",
                    confirmButtonText: 'Aceptar'
                });

            }
        },
        sendForm: function (paso, e) {
            e.preventDefault();
            var correo = document.getElementById("correo").value;
            console.log(correo.substr(-10));
           /* if (correo.substr(-3) !== 'com' && correo.substr(-2) !== 'pe') {
                swal({
                    title: "¡Error!",
                    html: '<span style="font-size:18px;">' + 'Ingresar un correo correcto ".com" o ".pe" ' + '<span>',
                    type: "error",
                    confirmButtonColor: "#DD6B55",
                    confirmButtonText: 'Aceptar'
                });
                return;
            
            if (correo.substr(-10).toLowerCase() !== "@gmail.com") {
                swal({
                    title: "¡Error!",
                    //html: '<span style="font-size:18px;">' + 'Ingresar un correo especificado en la Declaración Jurada en formato @gmail.com ' + '<span>',
                    html: '<span style="font-size:18px;">' + 'Ingresar un correo Gmail @gmail.com ' + '<span>',
                    type: "error",
                    confirmButtonColor: "#DD6B55",
                    confirmButtonText: 'Aceptar'
                });
                return;
            }*/

           
           
           /*var s = document.getElementById("vacunaFile");

            if (s.files[0] == undefined) {
                notify('Debe seleccionar un archivo', "error");
                return;
            }
            if (!Array.of('pdf').includes(s.files[0].type.split('/').pop())) {
                notify('Solo se permite archivos pdf', "error");
                return;
            }*/
            /*Postulante.submitFile();*/
            var form = $("#" + Postulante.form);
            /*console.log($(".nombreFila"));*/
            form.parsley().destroy();
            form.parsley();
            if (!form.parsley().validate()) {
                return;
            }
            $(".boton").attr("disabled", true);
            $(".boton").addClass("disabled");
            $("#spin").removeClass('hide');
            console.log(form.serialize())
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
                        $(".boton").removeClass("disabled");
                        $("#spin").addClass('hide');
                        swal({
                            title: "¡Error!",
                            html: '<span style="font-size:18px;">' + response.message + '<span>',
                            type: "error",
                            confirmButtonColor: "#DD6B55",
                            confirmButtonText: 'Aceptar'
                        });
                    }
                },
                error: function () {
                    $(".boton").attr("disabled", false);
                    $(".boton").removeClass("disabled");
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
        mostrarDirDomicilio: function ($this) {
            var dataPaisDomi = $("#paisDomicilio").select2("data");

            if (dataPaisDomi.codigo === "PE") {
                $("#distritoDomicilio").removeClass("hide");
                $("#distritoDomicilioIn").prop('required', true);
            } else {
                $("#distritoDomicilio").addClass("hide");
                $("#distritoDomicilioIn").select2("val", "");
                $("#distritoDomicilioIn").prop('required', false);
            }
        },
        /*submitFile: function () {

            let formData = new FormData();
               var s = document.getElementById("vacunaFile");
            formData.append("files", s.files[0]);
            $.ajax({
                url: APP.url('inscripcion/postulante/uploadArchivosFile'),
                type: 'POST',
                data: formData,
                async: false,
                contentType: false,
                processData: false,
                success: function (response) {
                    if (response.success) {
                        var value = response.data.name;
                        $(".nombreFila").val(value);
                    }
                },
                error: function () {
                    $(".boton").attr("disabled", false);
                    $(".boton").removeClass("disabled");
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
            event.target.files = null;
        }*/
    };

    $(".numerico").numeric({negative: false});

    Postulante.body.delegate("#paisNacimiento", "change", function () {
        Postulante.mostrarDirNacimiento($(this));
    });

    Postulante.body.delegate("#distNacimiento", "change", function () {
        Postulante.mostrarDistNacimiento($(this));
    });

    Postulante.body.delegate(".nombre-persona", "change", function () {
        APP.revisarNombre($(this));
    });

    Postulante.body.delegate(".sin-espacios", "change", function () {
        APP.eliminarEspacios($(this));
    });

    Postulante.body.delegate(".revisar-email", "change", function () {
        APP.revisarEmail($(this));
    });

    $("body").delegate(".send-form", "click", function (e) {
        Postulante.sendForm(1, e);
    });

   /*$("body").delegate(".vacuna-file", "click", function (e) {
        Postulante.submitFile();
    });*/

    Postulante.body.delegate("#paisDomicilio", "change", function () {
        Postulante.mostrarDirDomicilio($(this));
    });
    Postulante.body.delegate("#distritoDomicilio", "change", function () {
        Postulante.mostrarDistritoDomicilio($(this));
    });

    Postulante.init();
    
});