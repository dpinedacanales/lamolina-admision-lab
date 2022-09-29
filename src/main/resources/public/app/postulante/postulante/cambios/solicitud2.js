Vue.component("multiselect", window.VueMultiselect.default);
Vue.component("date-picker", window.VueBootstrapDatetimePicker.default);
var d = new Date();
var y = d.getFullYear() + 1;

new Vue({
    el: '#cambiosVUE',
    data: {
        cambios: [],
        checkeds: [],
        modalidades: [],
        modalidadesSimulacion: [],
        modalidades2: [],
        modalidad: null,
        paises: [],
        modaOpen: false,
        coleOpen: false,
        paisCole: null,
        paisUni: null,
        isLoading: false,
        distrito: null,
        distritos: [],
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
        main: true,
        datgen: false,
        opcmocu: false,
        opc: false,
        coleu: false,
        persona: null,
        opciones: [],
        carrerasOri: JSON.parse(carreras),
        carreras: [],
        btnFin: false,
        configDate: {
            format: 'DD/MM/YYYY',
            useCurrent: false
        },
        disablePaisCole: false,
        opcionesCount: 0,
        warnOpc: '',
        grados: [],
        years: [],
        years2: [],
        msg1: 'Recuerda que esta modalidad solo es aplicable para postulantes que culminen sus estudios secundarios el ' + y +
                '. De no cumplir con este requisito, perderás la vacante automaticamente en caso de ingreso. '
    },
    mounted: function () {
        let $vue = this;
        $vue.getDataForCambios();
        $vue.getProceAca();
        $vue.createYears();
        $vue.create2Years();
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
                swal({
                    title: 'Debe completar todos los campos del formulario',
                    html: '',
                    type: "error",
                    confirmButtonColor: "#E74C3C",
                    confirmButtonText: 'Aceptar'
                });
                return;
            }
            $vue.btnFin = true;

            $vue.postulante.opcionCarrera = $vue.opciones.map(op => ({carreraPostula: op}));

            $vue.checkeds[0].modalidadIngreso = $vue.modalidad == null ? $vue.postulante.modalidadIngreso : $vue.modalidad.modalidadIngreso;
            $vue.checkeds[0].universidad = $vue.universidad;
            $vue.checkeds[0].universidadExtranjera = $vue.uniText;
            $vue.checkeds[0].colegioExtranjero = $vue.coleText;
            $vue.checkeds[0].postulante = $vue.postulante;
            $vue.checkeds[0].paisColegio = $vue.paisCole;
            $vue.checkeds[0].paisUniversidad = $vue.paisUni;
            let colegio = $vue.colegio != null ? {id: $vue.colegio.id} : null;
            $vue.checkeds[0].colegio = colegio;
            $vue.btnEnviar = false;

            swal({
                title: '¿Estás seguro que terminaste tus cambios?',
                html: '',
                type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#04B45F",
                confirmButtonText: 'Sí, estoy seguro',
                cancelButtonText: "No, voy a revisar",
                showLoaderOnConfirm: true,
                reverseButtons: true,
                allowEscapeKey: false,
                allowOutsideClick: false,
                preConfirm: () => {
                    return axios.post('/inscripcion/postulante/cambios/saveSolicitud', $vue.checkeds)
                            .then(response => {
                                return response.data;
                            })
                            .catch(error => {
                                swal.showValidationMessage(`Request failed: ${error}`)
                            })
                },
            }).then((response) => {
                if (response.dismiss) {
                    $vue.btnFin = false;
                    return;
                }
                if (response.value.success) {
                    swal({
                        title: "",
                        html: '<span style="font-size:18px;">' + response.value.message + '<span>',
                        type: "success",
                        timer: 5000
                    });
                    setTimeout(function () {
                        location.href = '/inscripcion/postulante';
                    }, 2500);
                } else {
                    $vue.btnFin = false;
                    swal({
                        title: "¡Error!",
                        html: '<span style="font-size:18px;">' + response.value.message + '<span>',
                        type: "error",
                        confirmButtonColor: "#DD6B55",
                        confirmButtonText: 'Aceptar'
                    });
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
        getGrados() {
            let $vue = this;
            $.ajax({
                method: 'GET',
                url: '/inscripcion/postulante/cambios/grados',
                success: function (response) {
                    $vue.grados = response.data;
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        getModas2() {
            let $vue = this;
            $.ajax({
                method: 'GET',
                url: '/inscripcion/postulante/cambios/modalidades2',
                success: function (response) {
                    $vue.modalidades2 = response.data;
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
        getDataForCambios() {
            let $vue = this;
            axios.get('/inscripcion/postulante/cambios/dataForCambios')
                    .then(response => {
                        if (response.data.success) {
                            $vue.modalidades2 = response.data.data.modalidadesCambio;
                            $vue.modalidadesSimulacion = response.data.data.modalidadesSimulacion;
                            $vue.modalidades = response.data.data.modalidadesRestricciones;
                            $vue.grados = response.data.data.grados;
                            $vue.cambios = response.data.data.cambios;
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
                            $vue.colegios = [];
                            $vue.colegio = null;
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
                $vue.setOpcionesCount();
            }
        },
        openCole(e) {
            let $vue = this;
            $vue.coleOpen = e.target.checked;
            if (!$vue.coleOpen) {
                $vue.isCole = null;
                $vue.paisCole = null;
                $vue.paisUni = null;
                $vue.distrito = null;
                $vue.colegio = null;
                $vue.universidad = null;
                $vue.coleText = null;
                $vue.uniText = null;
                $vue.modalidad = null;
                $vue.isUni = false;
                $vue.isColeUni = false;
                $vue.selCU = null;
                $vue.colegios = [];
            } else {
                if ($vue.modalidad != null) {
                    $vue.checkRequiere($vue.modalidad);
                } else {
                    $vue.checkRequiere($vue.postulante.modalidadIngresoCiclo);
                }
            }
        },
        openOpc(e) {
            let $vue = this;
            $vue.opc = e.target.checked;
            if ($vue.opc) {
                $vue.setOpcionesCount();
            }
        },
        modaNombre(item) {
            return item.modalidadIngreso.nombreInscripcion;
        },
        selMod(item) {
            let $vue = this;

            $vue.colegio = null;
            $vue.universidad = null;
            $vue.coleText = null;
            $vue.uniText = null;
            $vue.paisCole = null;
            $vue.paisUni = null;
            $vue.disablePaisCole = false;
            $vue.colegios = [];

            if ($("#coleCheck").is(':checked') != $vue.coleOpen) {
                $vue.coleOpen = $("#coleCheck").is(':checked');
            }
            if ($vue.opc) {
                $vue.getOpciones();
            }

            $vue.calcDiff(item);
            $vue.checkRequiere(item);
            $vue.checkCoar(item);
            $vue.setOpcionesCount('smod');

            if (!$vue.modalidad.modalidadIngreso.quintoSecundaria &&
                    !$vue.modalidad.modalidadIngreso.primerosPuestos &&
                    !$vue.modalidad.modalidadIngreso.participanteLibre) {
                let existe = false;
                for (var i = 0; i < $vue.years.length; i++) {
                    if ($vue.years[i] == $vue.postulante.yearEgresoColegio) {
                        existe = true;
                    }
                }
                if (!existe) {
                    $vue.postulante.yearEgresoColegio = '';
                }
            }

            if ($vue.modalidad.modalidadIngreso.primerosPuestos) {
                let existe = false;
                for (var i = 0; i < $vue.years2.length; i++) {
                    if ($vue.years2[i] == $vue.postulante.yearEgresoColegio) {
                        existe = true;
                    }
                }
                if (!existe) {
                    $vue.postulante.yearEgresoColegio = '';
                }
            }


            if (item.modalidadIngreso.codigo == '24') {
                $vue.warnOpc += $vue.msg1;
            } else {
                $vue.warnOpc = $vue.warnOpc.replace($vue.msg1, '');
            }
        },
        selGrado(item) {
            let $vue = this;
            if ($vue.modalidad.modalidadIngreso.participanteLibre &&
                    $vue.postulante.gradoSecundaria != null &&
                    $vue.postulante.gradoSecundaria.orden == 5) {
                let existe = false;
                for (var i = 0; i < $vue.years.length; i++) {
                    if ($vue.years[i] == $vue.postulante.yearEgresoColegio) {
                        existe = true;
                    }
                }
                if (!existe) {
                    $vue.postulante.yearEgresoColegio = '';
                }
            }
        },
        checkCoar(item) {
            let $vue = this;
            if (item.modalidadIngreso.tipo == 'COAR' &&
                    item.modalidadIngreso.tipo != $vue.postulante.modalidadIngreso.tipo) {
                $vue.disablePaisCole = true;
                $vue.paisCole = {codigo: 'PE', nombre: 'Perú', id: 178};
                if (!$("#coleCheck").is(':checked') && !$vue.coleOpen) {
                    $vue.coleOpen = true;
                }
            }
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
                        $vue.coleOpen = true;
                    }
                }
            } else if (item.requiereSoloColegio) {
                $vue.isCole = true;
                $vue.isUni = false;
                if ($vue.acaProce == 'UNI') {
                    if (!$("#coleCheck").is(':checked')) {
                        $vue.coleOpen = true;
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
            let distri = $vue.distrito != null ? $vue.distrito.id : -1;
            axios.get('/comun/buscar/colegioProcedencia', {params: {nombre: nombre, distrito: distri, modalidad: modalidad}})
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
        changeUniExt() {
            let $vue = this;
            $vue.calcDiff($vue.modalidad);
        },
        getConceptoPrecio(item) {
            let $vue = this;

            let universidad = $vue.universidad != null ? $vue.universidad.id : null;
            let colegio = $vue.colegio != null ? $vue.colegio.id : null;
            let paisCole = $vue.paisCole != null ? $vue.paisCole.id : null;
            let paisUni = $vue.paisUni != null ? $vue.paisUni.id : null;
            let precio = null;
            let moda = item != null ? item.modalidadIngreso.id : $vue.postulante.modalidadIngreso.id;
            let parametros = {modalidad: moda, colegio: colegio, colegioNombre: $vue.coleText,
                universidad: universidad, universidadNombre: $vue.uniText, paisCole: paisCole, paisUni: paisUni};

            $.ajax({
                url: APP.url('inscripcion/postulante/cambios/findPrecio'),
                type: 'POST',
                async: false,
                data: parametros,
                success: function (response) {
                    if (response.success) {
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
            swal({
                title: '¿Estás seguro que deseas anular tu anterior solicitud?',
                text: "Puedes volver a crearla con tus nuevas modificaciones.",
                type: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#DD6B55',
                confirmButtonText: 'Sí, estoy seguro',
                cancelButtonText: "No",
                closeOnConfirm: false,
                reverseButtons: true
            }).then((result) => {
                if (result.value) {
                    axios.get('/inscripcion/postulante/cambios/anular')
                            .then(response => {
                                if (response.data.success) {
                                    setTimeout(function () {
                                        window.location.href = '/inscripcion/postulante/cambios/solicitud';
                                    }, 1500);
                                } else {
                                    notify(response.data.message, 'error');
                                }
                            });
                }
            });
        },
        next(i) {
            let $vue = this;

            $vue.main = false;
            $vue.datgen = false;
            $vue.opcmocu = false;

            if (i == 1) {
                if ($vue.searchInArray('CDATGEN')) {
                    $vue.datgen = true;
                } else {
                    $vue.opcmocu = true;
                    if ($vue.searchInArray('COPCIONAI') || $vue.searchInArray('COPCIONDI')) {
                        $vue.opc = true;
                    }
                    if ($vue.searchInArray('CCOLEUNIAI') || $vue.searchInArray('CCOLEUNIDI') || $vue.searchInArray('CCOLEUNIDE')) {
                        $vue.coleu = true;
                    }
                }
                if ($vue.opc && !$vue.modaOpen) {
                    $vue.getOpciones();
                }

            } else {
                var form = $("#form");
                form.parsley().destroy();
                form.parsley();
                if (!form.parsley().validate()) {
                    return;
                }
                $vue.opcmocu = true;
                if ($vue.searchInArray('COPCIONAI') || $vue.searchInArray('COPCIONDI')) {
                    $vue.opc = true;
                }
                if ($vue.searchInArray('CCOLEUNIAI') || $vue.searchInArray('CCOLEUNIDI') || $vue.searchInArray('CCOLEUNIDE')) {
                    $vue.coleu = true;
                }
            }
        },
        back(i) {
            let $vue = this;
            $vue.main = false;
            $vue.datgen = false;
            $vue.opcmocu = false;
            $vue.modalidad = null;
            $vue.opciones = [];
            if (i == 3) {
                if ($vue.searchInArray('CDATGEN')) {
                    $vue.datgen = true;
                } else {
                    $vue.main = true;
                }
            } else {
                $vue.main = true;
            }
        },
        searchInArray(codigo) {
            let $vue = this;

            for (var i = 0; i < $vue.checkeds.length; i++) {
                if ($vue.checkeds[i].tipoCambioInfo.codigo === codigo) {
                    return true;
                }
            }
            return false;
        },
        carreraNombre(item) {
            return item.carrera.nombre;
        },
        selOpcion(item, i) {
            let $vue = this;
            $vue.carreras = JSON.parse(JSON.stringify($vue.carrerasOri));
            for (i; i < $vue.opciones.length; i++) {
                $vue.opciones[i] = null;
            }
            for (var j = 0; j < $vue.opciones.length; j++) {
                if ($vue.opciones[j] != null) {
                    let obj = $vue.carreras.find(c => c.id == $vue.opciones[j].id);
                    let index = $vue.carreras.indexOf(obj);
                    if (index !== -1) {
                        $vue.carreras.splice(index, 1);
                    }
                }
            }
        },
        remOpcion(i) {
            let $vue = this;
            for (i; i < $vue.opciones.length; i++) {
                $vue.opciones[i] = null;
            }
        },
        datGen(e) {
            let $vue = this;
            if (e.target.checked) {
                $vue.persona = $vue.postulante.persona;
            } else {
                $vue.persona = null;
            }
        },
        setOpcionesCount(x) {
            let $vue = this;
            $vue.opcionesCount = $vue.modalidad != null ? $vue.modalidad.opciones : $vue.postulante.modalidadIngresoCiclo.opciones;
            if (x == 'smod' && !$vue.opc) {
                if ($vue.modalidad.opciones > $vue.postulante.modalidadIngresoCiclo.opciones) {
                    $vue.warnOpc = 'La modalidad elegida, cuenta con más opciones de carrera, para elegirlas da clic en REGRESAR y selecciona CAMBIO DE CARRERA. ';
                } else if ($vue.modalidad.opciones < $vue.postulante.modalidadIngresoCiclo.opciones) {
                    let cant = $vue.modalidad.opciones > 1 ? $vue.modalidad.opciones + ' opciones' : 'una opción';
                    $vue.warnOpc = 'La modalidad a la que cambiarás, tiene sólo ' + cant + '. Si deseas cambiar tu primera opción, da clic en el botón Regresar y selecciona Cambio de Carrera. ';
                } else {
                    $vue.warnOpc = '';
                }
            }
        },
        getOpciones() {
            let $vue = this;
            $vue.remOpcion;
            let moda = $vue.modalidad != null ? $vue.modalidad.modalidadIngreso.id : $vue.postulante.modalidadIngreso.id;
            axios.get('/inscripcion/postulante/cambios/opciones', {params: {modalidad: moda}})
                    .then(response => {
                        if (response.data.success) {
                            $vue.carrerasOri = response.data.data;
                        } else {
                            notify(response.data.message, 'error');
                        }
                    });
        },
        createYears() {
            let $vue = this;
            let year = $vue.postulante.cicloPostula.cicloAcademico.year;
            for (var i = year - 1; i > year - 61; i--) {
                $vue.years.push(i);
            }
        },
        create2Years() {
            let $vue = this;
            let year = $vue.postulante.cicloPostula.cicloAcademico.year;
            for (var i = year - 1; i > year - 3; i--) {
                $vue.years2.push(i);
            }
        }
    }
});
