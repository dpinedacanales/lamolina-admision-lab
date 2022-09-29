Vue.component('file-upload', VueUploadComponent);
Vue.use(VueLocalStorage);

var stompClient = null;
var comunicacionSound = new Audio('/phobos/media/what-friends-are-for.mp3');
var messageSound = new Audio('/phobos/media/light.mp3');

new Vue({
    el: "#parrot-chat",
    data: {
        activeParrot: true,
        activeFull: false,
        connected: false,
        usuario: {dni: '', celular: '', identificador: ''},
        conversacion: null,
        mensajes: [],
        mensaje: '',
        filesUploaded: [],
        urlUploadFiles: '',
        uploading: false,
        loadLocal: false,
        attemps: 0
    },
    localStorage: {
        mensajes: {type: Array},
        usuario: {type: Object}
    },
    created() {
        console.log('Parrot Chat Created');
        this.urlUploadFiles = SOCKET_URL.replace("websocket", "archivo/uploadFile");
        this.reconectar();
    },
    updated() {
        if ($('.scrolled').get(0)) {
            $('.scrolled').animate({
                scrollTop: $('.scrolled').get(0).scrollHeight
            }, 300);
        }
    },
    filters: {
        urlify(mensaje) {
            mensaje = mensaje + " ";
            let nuevo = mensaje.replace(/(https*:\/\/[^\s]*)\s/gi, function (a, b) {
                return '<a href="' + b + '" class="attach">' + b + '</a>';
            })
            return nuevo;
        }
    },
    methods: {
        toggleFull() {
            this.activeFull = !this.activeFull;
        },
        ingresar() {
            let $vue = this;
            if (!$vue.usuario.dni || !$vue.usuario.celular ||
                    $vue.usuario.dni.trim().length < 5 || $vue.usuario.celular.trim().length < 8) {
                swal("Error", "Por favor ingrese datos correctos", "error");
                return;
            }

            let socket = new SockJS(SOCKET_URL);
            stompClient = Stomp.over(socket);
            stompClient.connect({}, $vue.onConnected, $vue.onError);
        },
        reconectar() {
            console.log("RECONECTAR")
            let $vue = this;
            if (!this.$cookies.isKey("sock-session")) {
                $vue.$localStorage.set('mensajes', Array);
                $vue.$localStorage.set('usuario', Object);
                return;
            }

            $vue.usuario = JSON.parse($vue.$cookies.get("sock-session"));

            $vue.activeFull = true;
            $vue.loadLocal = true;
            let usuariols = $vue.$localStorage.get('usuario');

            if ($vue.usuario.dni !== usuariols.dni) {
                $vue.loadLocal = false;
                $vue.$localStorage.set('mensajes', Array);
            }

            $vue.ingresar();
        },
        onConnected(frame) {
            console.log("ONCONNECTED")
            let $vue = this;
            $vue.connected = true;
            $vue.usuario.identificador = frame.headers['user-name'];

            $vue.$cookies.set("sock-session", JSON.stringify($vue.usuario), "0");
            $vue.$localStorage.set('usuario', $vue.usuario);

            if ($vue.loadLocal) {
                $vue.mensajes = $vue.$localStorage.get('mensajes');
            }

            stompClient.debug = () => {
            };

            stompClient.send("/app/chat/adduser", {}, JSON.stringify($vue.usuario));

            stompClient.subscribe('/user/parrot/public', $vue.onMessageReceived);
        },
        onError() {
            if (this.attemps < 2) {
                this.ingresar();
                this.attemps++;
            }
            console.log('Error al conectar al Socket.');
        },
        onMessageReceived(payload) {
            let $vue = this;
            let mensajeConversa = JSON.parse(payload.body);
            if ($vue.loadLocal && mensajeConversa.mensajes.tipo === 'REJOIN') {
                return;
            }

            if (mensajeConversa.mensajes.tipo === 'JOIN') {
                comunicacionSound.play();
            } else if (['CHAT', 'REPLY'].includes(mensajeConversa.mensajes.tipo)) {
                messageSound.play();
            }

            $vue.mensajes.push(mensajeConversa.mensajes);
            $vue.updateLocalStorage(mensajeConversa);

            if ($vue.conversacion === null) {
                $vue.conversacion = mensajeConversa.conversacion;
            }
        },
        updateLocalStorage(mensajeConversa) {
            console.log("UPDATELOCALSTORAGE")
            let $vue = this;
            let mensajes = $vue.$localStorage.get('mensajes');
            mensajes.push(mensajeConversa.mensajes);
            $vue.$localStorage.set('mensajes', mensajes);
        },
        sendMessage() {
            let $vue = this;
            let adjuntos = $vue.filesUploaded.map(x => {
                return {filename: x.name, mime: x.type};
            });

            if (this.mensaje.trim() !== '' || adjuntos.length > 0) {

                var messageToServer = {
                    identificador: $vue.usuario.identificador,
                    contenido: $vue.mensaje,
                    tipo: 'CHAT',
                    adjuntos: adjuntos
                };

                stompClient.send("/app/chat/sendMessage", {}, JSON.stringify(messageToServer));
                $vue.mensaje = '';
                this.$refs.upload.clear();
            } else {
                this.mensaje = this.mensaje.trim();
            }
        },
        inputFile(newFile, oldFile) {

            if (newFile && oldFile) {
                if (newFile.active && !oldFile.active) {
                    if (newFile.size === 0) {
                        return;
                    }
                }
                if (newFile.progress !== oldFile.progress) {
                    this.uploading = true;
                }
                if (newFile.error && !oldFile.error) {
                }
                if (newFile.success && !oldFile.success) {
                    newFile.success = newFile.response.success;
                    this.uploading = false;
                    this.sendMessage();

                }
            }
            if (Boolean(newFile) !== Boolean(oldFile)
                    || oldFile.error !== newFile.error) {
                if (!this.$refs.upload.active) {
                    this.$refs.upload.active = true;
                }
            }

        }
    }
});