$(function () {

    Postulante = {
        cerrar: function () {

            $.ajax({
                url: APP.url('logout'),
                type: 'POST',
                async: true,
                success: function (response) {

                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        }
    };


    $("body").delegate("#distNacimiento", "change", function () {
        Postulante.deleteMensaje();
    });

    Postulante.cerrar();
    setTimeout(function () {
        location.href = APP.url('');
    }, 7000);

    var i = 1;

    setInterval(function () {
        $("#mensaje").html(i);
        i++;
    }, 1000);

});