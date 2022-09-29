$(function () {
    Recurso = {
        avisoRendirExamen: function () {
            $.ajax({
                url: APP.url('inscripcion/postpago/eventoVerAula'),
                type: 'POST',
                async: true,
                success: function (response) {
                    var data = response.data;
                    if (!data.mostrarAvisoAula) {
                        location.href = "https://resultados.lamolina.edu.pe";
                        return;
                    }

                    var texto = '<span class="h4 text-dark bold">El color y número de aula podrás verlo aquí el '
                            + data.fecha + ' a partir de las '
                            + data.hora + '</span> <br/>';

                    swal({
                        title: "",
                        html: texto,
                        type: "warning",
                        confirmButtonColor: "#889C9F",
                        confirmButtonText: "Cerrar"
                    });
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        goIntraner: function () {
            console.log("hola");
        }
    };

    $("#avisoRendirExamen").click(function () {
        Recurso.avisoRendirExamen();
    });
    $("#goIntraner").click(function () {
        Recurso.goIntraner();
    });

});

function urlIntranetsInit() {
    var url = window.location.href;
    $(".goIntranet").attr("href", "/" + $(".goIntranet").attr("href") + "/gomaipi");
}

urlIntranetsInit();