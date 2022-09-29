Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#cambiosVUE',
    data: {
        cambios: [],
        checkeds: [],
        modalidades: [],
        paises: [],
        modaOpen: false,
        coleOpen: false,
        pais: null,
        isLoading: false,
        distrito: null,
        distritos: [],
        modalidad: null,
        colegio: null,
        isUni: false,
        isColeUni: false,
        isCole: false,
        universidades: [],
        universidad: null,
        selCU: null,
        uniText: null,
        coleText: null,
        postulante: JSON.parse(postulante),
        colegios: [],
        deudaPagada: JSON.parse(deudaPagada),
        diferencia: null,
        acaProce: null,
        btnEnviar: true,
        showInfo1: false,
        showInfo2: false,
        step1: true,
        step2: false,
        step3: false
    },
    mounted: function () {
        let $vue = this;
        $vue.getCambios();
        $vue.getModas();
        $vue.getProceAca();
    },
    computed: {
        total() {
            let t = 0;
            for (var i = 0; i < this.checkeds.length; i++) {
                t += this.checkeds[i].monto;
            }
            return t;
        }
    },
    watch: {
        selCU(val) {
            let $vue = this;
            if (val == 'cole') {
                $vue.isCole = true;
                $vue.isUni = false;
            } else {
                $vue.isCole = false;
                $vue.isUni = true;
            }
        }
    },
    methods: {
        sendRequest() {
            let $vue = this;
            var form = $("#form");
            form.parsley().destroy();
            form.parsley();
            if (!form.parsley().validate()) {
                return;
            }
            $vue.checkeds[0].modalidadIngreso = $vue.modalidad == null ? $vue.postulante.modalidadIngreso : $vue.modalidad.modalidadIngreso;
            $vue.checkeds[0].universidad = $vue.universidad;
            $vue.checkeds[0].universidadExtranjera = $vue.uniText;
            $vue.checkeds[0].colegioExtranjero = $vue.coleText;
            let colegio = $vue.colegio != null ? {id: $vue.colegio.id} : null;
            $vue.checkeds[0].colegio = colegio;
            $vue.btnEnviar = false;
            axios.post('/inscripcion/postulante/cambios/solicitud', $vue.checkeds)
                    .then(response => {
                        if (response.data.success) {
                            notify(response.data.message, 'success');
                            setTimeout(function () {
                                location.reload();
                            }, 1000);
                        } else {
                            $vue.btnEnviar = true;
                            notify(response.data.message, 'error');
                        }
                    });
        },
        getCambios() {
            let $vue = this;
            $.ajax({
                method: 'GET',
                url: '/inscripcion/postulante/cambios/list',
                success: function (response) {
                    $vue.cambios = response.data;
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        getModas() {
            let $vue = this;
            axios.get('/inscripcion/postulante/cambios/modalidades')
                    .then(response => {
                        if (response.data.success) {
                            $vue.modalidades = response.data.data;
                        } else {
                            notify(response.data.message, 'error');
                        }
                    });
        },
        getPais(nombre) {
            let $vue = this;

            axios.get('/comun/buscar/allPaisesColegio', {params: {nombre: nombre, idModalidad: $vue.modalidad == null ? null : $vue.modalidad.modalidadIngreso.id}})
                    .then(response => {
                        if (response.data.success) {
                            $vue.paises = response.data.data;
                        } else {
                            notify(response.data.message, 'error');
                        }
                    });
        },
        getDistrito(nombre) {
            let $vue = this;
            axios.get('/comun/buscar/allDistritos', {params: {nombre: nombre}})
                    .then(response => {
                        if (response.data.success) {
                            $vue.distritos = response.data.data;
                        } else {
                            notify(response.data.message, 'error');
                        }
                    });
        },
        openModas(e) {
            let $vue = this;
            $vue.modaOpen = e.target.checked;
            if (!$vue.modaOpen) {
                $vue.checkConceptoDiferencia();
                $vue.modalidad = null;
                if ($vue.coleOpen) {
                    $("#coleCheck").trigger("click");
                }
            }
        },
        openCole(e) {
            let $vue = this;
            $vue.coleOpen = e.target.checked;
            if (!$vue.coleOpen) {
                $vue.isCole = null;
                $vue.pais = null;
                $vue.distrito = null;
                $vue.colegio = null;
                $vue.universidad = null;
                $vue.coleText = null;
                $vue.uniText = null;
                $vue.modalidad = null;
                $vue.isUni = false;
                $vue.isColeUni = false;
                $vue.selCU = null;
            }

            if ($vue.modalidad != null) {
                $vue.checkRequiere($vue.modalidad);
            } else {
                $vue.checkRequiere($vue.postulante.modalidadIngresoCiclo);
            }
        },
        modaNombre(item) {
            return item.modalidadIngreso.nombre;
        },
        selMod(item) {
            let $vue = this;
            $vue.isUni = false;
            $vue.isColeUni = false;
            $vue.isCole = false;
            $vue.colegio = null;
            $vue.universidad = null;
            $vue.coleText = null;
            $vue.uniText = null;
            $vue.selCU = null;
            if ($vue.coleOpen) {
                $("#coleCheck").trigger("click");
            }

            $vue.calcDiff(item);
            $vue.checkRequiere(item);
        },
        checkRequiere(item) {
            let $vue = this;
            if (item.requiereColegioUniversidad) {
                $vue.isColeUni = true;
            } else if (item.requiereSoloUniversidad) {
                $vue.isUni = true;
                $vue.isCole = false;
                if ($vue.acaProce == 'COLE') {
                    if (!$("#coleCheck").is(':checked')) {
                        $("#coleCheck").trigger("click");
                    }
                }
            } else if (item.requiereSoloColegio) {
                $vue.isCole = true;
                $vue.isUni = false;
                if ($vue.acaProce == 'UNI') {
                    if (!$("#coleCheck").is(':checked')) {
                        $("#coleCheck").trigger("click");
                    }
                }
            }
        },
        selInst() {
            let $vue = this;
            $vue.calcDiff($vue.modalidad);
        },
        calcDiff(item) {
            let $vue = this;
            let precio = $vue.getConceptoPrecio(item);
            console.log($vue.deudaPagada);
            console.log(precio);
            if ($vue.deudaPagada != null && precio != null) {
                let precMod = precio.monto;
                let dif = precMod - $vue.deudaPagada.monto;
                if (dif > 0) {
                    $vue.setConceptoDiferencia(dif);
                } else {
                    $vue.checkConceptoDiferencia();
                }
            }
        },
        getUniversidad(nombre) {
            let $vue = this;
            axios.get('/inscripcion/postulante/cambios/universidades', {params: {nombre: nombre}})
                    .then(response => {
                        if (response.data.success) {
                            $vue.universidades = response.data.data;
                        } else {
                            notify(response.data.message, 'error');
                        }
                    });
        },
        getColegio(nombre) {
            let $vue = this;
            let modalidad = $vue.modalidad == null ? $vue.postulante.modalidadIngreso.id : $vue.modalidad.modalidadIngreso.id;
            if ($vue.distrito == null) {
                return;
            }
            axios.get('/comun/buscar/colegioProcedencia', {params: {nombre: nombre, distrito: $vue.distrito.id, modalidad: modalidad}})
                    .then(response => {
                        if (response.data.success) {
                            $vue.colegios = response.data.data;
                        } else {
                            notify(response.data.message, 'error');
                        }
                    });
        },
        getProceAca() {
            let $vue = this;
            if ($vue.postulante.modalidadIngresoCiclo.requiereColegioUniversidad) {
                $vue.acaProce = 'AMB';
            } else if ($vue.postulante.modalidadIngresoCiclo.requiereSoloUniversidad) {
                $vue.acaProce = 'UNI';
            } else {
                $vue.acaProce = 'COLE';
            }
        },
        setConceptoDiferencia(mont) {
            let $vue = this;
            $vue.checkConceptoDiferencia();
            let dife = {id: -1, monto: mont, tipoCambioInfo: {id: -1, codigo: 'NEW'}, conceptoPago: {descripcion: 'DIFERENCIA COSTO MODALIDAD'}};
            $vue.checkeds.push(dife);
        },
        checkConceptoDiferencia() {
            let $vue = this;
            let index = $vue.checkeds.findIndex(item => item.id == '-1');
            if (index != '-1') {
                $vue.checkeds.splice(index, 1);
            }
        },
        getConceptoPrecio(item) {
            let $vue = this;
            let universidad = $vue.universidad != null ? $vue.universidad.id : null;
            let colegio = $vue.colegio != null ? $vue.colegio.id : null;
            let precio = null;
            let moda = item != null ? item.modalidadIngreso.id : $vue.postulante.modalidadIngreso.id;
            let parametros = {modalidad: moda, colegio: colegio, colegioNombre: $vue.coleText,
                universidad: universidad, universidadNombre: $vue.uniText};

            $.ajax({
                url: APP.url('inscripcion/postulante/cambios/findPrecio'),
                type: 'POST',
                async: false,
                data: parametros,
                success: function (response) {
                    if (response.success) {
                        console.log(response.data)
                        precio = response.data;
                    }
                },
                error: function () {
                    console.log("Error");
                }
            });
            return precio;
        },
        anular() {
            axios.get('/inscripcion/postulante/cambios/anular')
                    .then(response => {
                        if (response.data.success) {
                            notify(response.data.message, 'success');
                            setTimeout(function () {
                                location.reload();
                            }, 1500);
                        } else {
                            notify(response.data.message, 'error');
                        }
                    });
        },
        showInfo(e) {
            let $vue = this;
            let isChecked = e.target.checked;
            let codigo = e.target._value.tipoCambioInfo.codigo;
            console.log(codigo);
            if (codigo == 'CDATGEN') {
                if (isChecked) {
                    $vue.showInfo1 = true;
                } else {
                    $vue.showInfo1 = false;
                }
            } else {
                if (isChecked) {
                    $vue.showInfo2 = true;
                } else {
                    $vue.showInfo2 = false;
                }
            }

        }
    }
});