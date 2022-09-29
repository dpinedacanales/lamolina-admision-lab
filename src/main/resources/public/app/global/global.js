$(function () {
    var page = window.location.pathname;
    var dateExpire = new Date();
    var minutes = 20;


    var Global = {
        init: function () {
            if (page == '/talleres') {
                Global.topTalleres();
                Global.loadSliders();
                Global.loadGallery();
            }
        },
        body: $('body'),
        modal: function () {
            var objectid = $.cookie("objectId");
            var page = $.cookie("page");
            var title = $.cookie("title");
            $("#viewModal").on('hide.bs.modal', function () {
                if (page !== undefined) {
                    $.removeCookie("page", {path: '/'});
                }
                if (objectid !== undefined) {
                    $.removeCookie("objectId", {path: '/'});
                }
                if (title !== undefined) {
                    $.removeCookie("title", {path: '/'});
                }
            });
        },
        showTaller: function (e, $this) {
            var rel;
            if (!isNaN($this)) {
                rel = $this;
            } else {
                rel = $this.attr("rel");
            }
            dateExpire.setTime(dateExpire.getTime() + (minutes * 60 * 1000));
            $.cookie("page", page, {expires: dateExpire, path: '/'});
            $.cookie("objectId", rel, {expires: dateExpire, path: '/'});
            $.ajax({
                method: 'POST',
                url: 'taller/mostrar',
                data: {taller: rel},
                success: function (response) {
                    $('#tallerModal').html(response);
                    $('#viewModal').modal('show');
                    var modalTitle = document.getElementById("modalTitle").innerHTML;
                    $.cookie("title", modalTitle, {expires: dateExpire, path: '/'});
                    $('#viewModal').find('[name="carreraNueva.id"]').on("change", function (e) {
                        var self = $(e.currentTarget);
                        var template = '<label>Especifique el nombre de la carrera</label>' +
                                '<input type="text" maxlength="199" class="form-control" name="otraCarrera" required="true"/>';
                        if (self.val() < 0) {
                            $('#especificarCarrera').html(template);
                        } else {
                            $('#especificarCarrera').html('');
                        }
                    });
                    Global.modal();
                    $('#viewModal').parsley('destroy');
                    $('#viewModal').parsley();
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        inscribirseTaller: function ($this) {
            var page = $.cookie("page");
            var objectid = $.cookie("objectId");
            var form = $("#confInteresado");

            if (!form.parsley().validate()) {
                return;
            }
            var rel = $this.attr('rel');
            form.find('[name=idTaller]').val(rel);
            $.ajax({
                method: 'POST',
                url: 'taller/inscribirse',
                data: form.serialize(),
                success: function (response) {
                    if (response.success) {
                        if (page !== undefined) {
                            $.removeCookie("page", {path: '/'});
                        }
                        if (objectid !== undefined) {
                            $.removeCookie("objectId", {path: '/'});
                        }
                        var datos = response.data;
                        var visitaDiv = '';
                        if (datos.visita !== null && datos.costo !== null) {
                            visitaDiv = '<div class="alert alert-orange text-center">En caso desees una visita guiada por la UNALM esta tendrá un costo de ' + datos.costo + ' soles</div>';
                        }

                        swal({
                            title: '<h3 style="color: #017840;">Gracias <i>' + datos.nombre + '</i> por inscribirte, te esperamos</h3>',
                            type: 'success',
                            html:
                                    '<p style="font-family: Arial; color: #3e3d3d; line-height: 1.5;"><b>Evento: </b>' + datos.taller + '<br/>' +
                                    '<b><i class="fa fa-calendar"></i> </b> ' + datos.fecha +
                                    ' <b><i class="fa fa-clock-o"></i> </b>' + datos.hora + '<br/>' +
                                    '<b>Lugar: </b>' + datos.ubicacion + '<br/><br/></p>' +
                                    visitaDiv,
                            showCloseButton: true
                        });
                        $('#viewModal').modal('hide');
                    } else {
                        notify(response.message, 'error');
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        topTalleres: function () {
            var lst = '';
            $.ajax({
                url: ('/taller/topTalleres'),
                type: 'POST',
                data: {top: 20},
                success: function (response) {
                    if (response.success) {
                        var data = response.data;
                        for (var k in data) {
                            var tmp = '';
                            if (data[k].estado == 'INA') {
                                tmp += '<li class="col-md-3 ' + data[k].estilo + '" rel="' + data[k].id + '" >';
                            } else {
                                tmp += '<li class="col-md-3 showTaller ' + data[k].estilo + '" rel="' + data[k].id + '" >';
                            }
                            tmp += '<a>';
                            tmp += '<div class="img-containered">';
                            tmp += '<img src="/archivo/download/' + data[k].banner + '" alt="">';
                            tmp += '</div>';
                            tmp += '<strong>' + data[k].diaNum + '<i>' + data[k].mes + '</i></strong>';
                            if (data[k].estado == 'INA') {
                                tmp += ' <span class="label label-danger">Finalizado</span>';
                            } else if (data[k].estado == 'PROX') {
                                tmp += ' <span class="label label-info">Próximamente</span>';
                            }
                            ;
                            tmp += '<div class="events">';
              //tmp += '<p class="sub-title">';
             //if (data[k].carrera == '') {
             //     tmp += 'Charlas Agraria';
             // }else {
             //     tmp += ' ';
             // }
             // tmp += '</p>';
              tmp += '<h4 style="min-height: 20px; padding-top: 30px;">' + data[k].titulo +  '</h4>';
              tmp += '<h5 style="margin-top: 0px; font-size: 15px; text-transform: uppercase;">';
              tmp += data[k].carrera;
              tmp += '</h5>';                 
              tmp += '<p>';
              tmp += data[k].descripcion;                 
              tmp += '</p>';
              tmp += '<p>';
              tmp += '<span class="fa fa-map-marker"></span> ';
              tmp += data[k].ubicacion;
              tmp += '</p>';
              tmp += '<p>';
              tmp += '<span class="fa fa-calendar"></span> ';
              tmp += data[k].dia + ' ' + data[k].hora;
              tmp += '</p>';
              tmp += '</div>';
              tmp += '</a>';
              tmp += '</li>';
              lst += tmp;
                        }
                        $("#ulTaller").append(lst);

                        var $height = 0;
                        $(".calendario li .events").each(function () {
                            if (($(this).height()) > $height) {
                                $height = $(this).height();
                            }
                        });
                        $(".calendario li .events").each(function () {
                            $(this).css("height", $height + 10);
                            $(this).css("padding-bottom", "10px");
                        });

                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        loadSliders: function () {
            var lst = '';
            $.ajax({
                url: ('/taller/sliders'),
                type: 'POST',
                success: function (response) {
                    if (response.success) {
                        var data = response.data;
                        for (var k in data) {
                            var tmp = '<img alt="" src="/archivo/download/' + data[k].id + '"/>';
                            lst += tmp;
                        }
                        $("#imgSlider").append(lst);
                        $("#imgSlider").addClass("cycle-slideshow");
                        $('#imgSlider').cycle();

                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        loadGallery: function () {

            var lst = '';
            $.ajax({
                url: ('/taller/gallery'),
                type: 'POST',
                success: function (response) {
                    if (response.success) {
                        var data = response.data;
                        for (var k in data) {
                            var tmp = '<a class="isImg" data-src="/archivo/download/' + data[k].id + '" src="/archivo/download/' + data[k].id + '">';
                            tmp += '<img src="/archivo/download/' + data[k].id + '"/>';
                            tmp += '</a>';

                            lst += tmp;
                        }
                        $("#lightgallery").append(lst);
                        setTimeout(function () {
                            $("#lightgallery").lightGallery({
                                thumbnail: true,
                                animateThumb: true,
                                showThumbByDefault: true,
                                selector: ".isImg"
                            });

                            $('.trigers').on('click', function (event) {
                                event.preventDefault();
                                $('#lightgallery a:first-child').trigger('click');
                            });
                        }, 1000);

                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
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
                            Global.validandoMinMaxDocumento(nroDocumento, lgtExacta, longitud);

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
        },
        showCallMeModal: function () {
            $.ajax({
                method: 'POST',
                url: 'contactoForm/llamame',
                success: function (response) {
                    $('#callMeModal').html(response);
                    $('#llamameModal').modal('show');
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        callMeNow: function () {
            let n = $("#numberC").val();
            let c = $("#codeNumber").val();
            let ctype = $('[name="callType"]:checked').val();
            if (n.trim() == '' || !/^\d+$/.test(n)) {
                swal("Error", "Ingresa un número válido por favor", "error");
                return;
            }
            c = (c == '01' || c == null) ? '' : c;
            let nFinal = ctype == 'TEL' ? c.trim() + n.trim() : n.trim();
            console.log(nFinal);
            $.ajax({
                method: 'POST',
                url: 'contactoForm/callMeNow',
                data: {numero: nFinal},
                success: function (response) {
                    if (response.success) {
                        $('#llamameModal').modal('hide');
                        swal(response.message, "Espera nuestra llamada", "success");
                    } else {
                        $('#llamameModal').modal('hide');
                        swal(response.message, "", "warning");
                    }
                },
                error: function () {
                    swal("Error", MESSAGES.errorComunicacion, "error");
                }
            });
        },
        selCallType() {
            let ctype = $('[name="callType"]:checked').val();
            $("#numberC").val('');
            if (ctype == 'CEL') {
                $("#codeNumber").attr("disabled", true);
                $("#codeNumber").val('');
                $("#numberC").attr("maxlength", '9');
            } else {
                $("#numberC").attr("maxlength", '7');
                $("#codeNumber").attr("disabled", false);
            }
        }
    };



    $("body").delegate(".showTaller", "click", function (e) {
        Global.showTaller(e, $(this));
    });
    $("body").delegate(".inscribirse", "click", function () {
        Global.inscribirseTaller($(this));
    });
    $("body").delegate("[name='tipoDocumento.id']", "change", function () {
        Global.changeTipoDocumento($(this));
    });
    $("body").delegate(".showCallme", "click", function () {
        Global.showCallMeModal();
    });
    $("body").delegate("#callMeNow", "click", function () {
        Global.callMeNow();
    });
    $("body").delegate("[name='callType']", "change", function () {
        Global.selCallType();
    });

    Global.init();

});
