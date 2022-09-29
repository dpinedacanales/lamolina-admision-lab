Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#cambiosVUE',
    data: {
        postulante: JSON.parse(postulante),
        persona: null,
        isLoading: false,
        paises: [],
        distritos: [],
        opciones: [],
        carreras: JSON.parse(carreras),
        cambios: cambios
    },
    beforeMount: function () {
        let $vue = this;
        if ($vue.cambios.includes("CDATGEN")) {
            $vue.persona = $vue.postulante.persona;
        }
    },
    mounted: function () {
    },
    computed: {
    },
    watch: {
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
            $vue.postulante.persona = $vue.persona;
            $vue.postulante.opcionCarrera = $vue.opciones;
            console.log($vue.postulante.opcionCarrera);

            axios.post('/inscripcion/postulante/cambios/saveCambios', $vue.postulante)
                    .then(response => {
                        if (response.data.success) {
                            console.log(response.data);

                        } else {
                            $vue.btnEnviar = true;
                            notify(response.data.message, 'error');
                        }
                    });
        },
        getPais(nombre) {
            let $vue = this;
            axios.get('/comun/buscar/allPaises', {params: {nombre: nombre}})
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
        selOpcion(e) {
            let $vue = this;
        },
        remOpcion(i) {
            let $vue = this;
            for (i; i < $vue.opciones.length; i++) {
                $vue.opciones[i] = null;
            }
        },
        carreraNombre(item) {
            return item.carrera.nombre;
        }
    }
});