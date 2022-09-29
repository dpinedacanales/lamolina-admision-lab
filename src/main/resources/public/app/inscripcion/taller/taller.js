$(function () {
    var page = window.location.pathname;
    var dateExpire = new Date();
    var minutes = 20;

    Taller = {
        Init: function () {
            if (location.hash) {
                var hash = location.hash.substring(1);
                location.hash = '';
                history.pushState('', document.title, window.location.pathname);
                setTimeout(function (e) {
                    Taller.showTaller(e, hash);
                }, 99);
            }
        },
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
                    Taller.modal();
                    $('#viewModal').parsley('destroy');
                    $('#viewModal').parsley();
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        verGaleria: function ($this) {

            var rel = $this.attr("rel");
            $.ajax({
                method: 'POST',
                url: 'inscripcion/taller/galeria',
                data: {taller: rel},
                success: function (response) {
                    $('#tallerModal').html(response);
                    $('#viewModal').modal('show');
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        inscribirse: function ($this) {
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
        }

    };


    $("body").delegate(".showTaller", "click", function (e) {
        Taller.showTaller(e, $(this));
    });
    $("body").delegate(".verGaleria", "click", function () {
        Taller.verGaleria($(this));
    });
    $("body").delegate(".inscribirse", "click", function () {
        Taller.inscribirse($(this));
    });

    Taller.Init();
            console.log("::::::::::::::::::::::")

});
