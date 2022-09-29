var app = new Vue({
    el: '#mainHojaRecorrido',
    data: {
        URL: '/inscripcion/hojarecorrido',
        processing: false,
        oficinasRecorrido: null,
        educacion: {},
        careo: {bloqueado: false},
        documentsChecks: {},
        email: {},
        PANEL_NIVELACION: PANEL_NIVELACION,
        INSCRITO_NIVELACION: INSCRITO_NIVELACION
    },
    mounted: function () {
        let $vue = this;
        this.loadHojaDeRecorrido();
    },
    methods: {
        loadHojaDeRecorrido() {
            let vue = this;
            MODAL.showWait("Cargando.");
            axios.get(`${vue.URL}/loadHojaRecorrido`)
                    .then(response => {
                        if (response.data.success) {
                            vue.oficinasRecorrido = response.data.data.oficinasRecorrido;
                            vue.careo = response.data.data.careo;
                            vue.educacion = response.data.data.educacion;
                            vue.documentsChecks = response.data.data.documentsChecks;
                            vue.email = response.data.data.email;
                            console.dir(vue.oficinasRecorrido);
                        } else {
                            notify(response.data.message, 'error');
                        }
                        MODAL.hideWait();
                    });
        },
        boletaPagoAdm() {
            location.href = APP.url("inscripcion/postulante/generateBoletaPago");
        },
        getEstadoClass(estadoCode) {
            let colorEstado = {PEND: "default", INC: "warning", POST: "warning", PROC: "warning", ACT: "success", SUPL: "danger", MAT: "success"};
            return "label-" + colorEstado[estadoCode];
        },
        isAditionalDocs(actividadIngresante) {
            let vue = this;
            if (actividadIngresante.tipoActividadIngresante.tipoDOCS && !actividadIngresante.estadoAct) {
                return true;
            }
            if (actividadIngresante.tipoActividadIngresante.tipoMATRI && vue.email.emailIngresante) {
                return true;
            }
            if (actividadIngresante.tipoActividadIngresante.tipoFISOEC) {
                console.log("Es ficha")
                if (actividadIngresante.estadoPend) {
                    console.log("Actividad PEND")
                    return true;
                }
                console.log("Ficha.estado=" + actividadIngresante.fichaSocioeconomica.estado);
                return actividadIngresante.fichaSocioeconomica.estado === 'PEND';
            }
            if (actividadIngresante.tipoActividadIngresante.tipoTESTPSIC) {
                console.log("Es test")
                if (actividadIngresante.estadoPend) {
                    console.log("Actividad ACT")
                    return true;
                }
                console.log("test.estado=" + actividadIngresante.testPsicologico.estado);
                return actividadIngresante.testPsicologico.estado === 'ACT';
            }
            if (actividadIngresante.tipoActividadIngresante.tipoPAGOMATRI) {
                return actividadIngresante.tipoActividadIngresante.tieneAportes;
            }

            if (actividadIngresante.tipoActividadIngresante.tipoRPAGOADM ||
                    (actividadIngresante.tipoActividadIngresante.tipoPAGOEXAMED) ||
                    actividadIngresante.tipoActividadIngresante.tipoDOCS ||
                    actividadIngresante.tipoActividadIngresante.tipoENTREV) {
                return true;
            }

            return false;
        },
        verDetalleDos(actividad) {
            let vue = this;
            if (actividad.estadoAct) {
                return false;
            }
            if (actividad.tipoActividadIngresante.tipoRPAGOADM ||
                    actividad.tipoActividadIngresante.tipoPAGOMATRI ||
                    actividad.tipoActividadIngresante.tipoDOCS ||
                    actividad.tipoActividadIngresante.tipoENTREV ||
                    actividad.tipoActividadIngresante.tipoPAGOEXAMED) {
                return true;
            }
            return false;
        },
        tieneEmail() {
            let vue = this;
        },
        inscribirNivelacion() {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: $vue.URL + '/inscripcion',
                success: function (response) {
                    if (response.success) {
                        location.href = "https://forms.gle/3ML68R9TjMJ98XAh9";
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        goMaipi() {
            let origen = Base64.encode(window.location.href);
            location.href = `/inscripcion/postpago/goIntranet?ficha=FICHA_ING&origen=${origen}`;
        }
    }
});