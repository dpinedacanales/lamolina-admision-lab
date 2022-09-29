$(function () {
    $('#scrolled').parent().parent().addClass("col-md-offset-2");
    $('#scrolled').parent().parent().addClass("col-md-8");
});

Vue.component("multiselect", window.VueMultiselect.default);
new Vue({
    el: '#numeroDocVUE',
    data: {
        tiposDoc: JSON.parse(tiposDocJson),
        persona: JSON.parse(personaJson),
    },
    mounted: function () {
        let $vue = this;
    },
    methods: {
        cambiarDocumento() {
            let $vue = this;
            let numDoc = $vue.persona.numeroDocIdentidad;
            let tipoDoc = $vue.persona.tipoDocumento.id;
            let email = $vue.persona.email;

            var titulo = esVirtual ? "¿Estás seguro que deseas modificar tu documento de identidad y/o tu email?" : "¿Estás seguro que deseas modificar tu documento de identidad?";
            swal({
                title: titulo,
                html: '',
                type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#04B45F",
                confirmButtonText: 'Sí, estoy seguro',
                cancelButtonText: "No, voy a revisar",
                reverseButtons: true,
                showLoaderOnConfirm: true,
                cancelButtonClass: 'btn btn-default',
                preConfirm: (login) => {
                    console.log(login)
                    return axios.get('/inscripcion/numeroidentidad/cambiarDocumento', {params: {numero: numDoc, tipoDoc: tipoDoc, email: email}})
                            .then(response => {
                                console.log("2222")
                                return response.data;
                            })
                            .catch(error => {
                                swal.showValidationMessage(`Request failed: ${error}`)
                            });
                }

            }).then(response => {
                if (response.dismiss) {
                    return;
                }
                if (response.value.success) {
                    swal({
                        title: "",
                        html: '<span style="font-size:18px;">' + response.value.message + '<span>',
                        type: "success",
                        timer: 5000
                    });
                    location.href = APP.url("inscripcion/postulante");
                } else {
                    swal({
                        title: "¡Error!",
                        html: '<span style="font-size:18px;">' + response.value.message + '<span>',
                        type: "error",
                        confirmButtonColor: "#DD6B55",
                        confirmButtonText: 'Aceptar'
                    });
                }
            });

        }
    }
});

