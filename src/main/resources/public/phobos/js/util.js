$(function () {
    $('input').attr('autocomplete', 'off');

    $('.scrollable').scroll(function () {

        var limit = ($('#profileContainer').length === 1) ? 230 : 0;
        var nav = $('.subbar');

        if ($(this).scrollTop() > limit) {

            nav.addClass("f-nav");
        } else {

            nav.removeClass("f-nav");
        }
    });
});

Messenger.options = {
    extraClasses: 'messenger-fixed messenger-on-bottom messenger-on-right',
    theme: 'flat',
}

function notify(message, type) {
    var t = (type == null) ? 'success' : type;
    setTimeout(function () {
        Messenger().post({
            message: message,
            type: t,
            hideAfter: 12,
            showCloseButton: true
        });
    }, 900);
}

function randString(n) {
    if (!n) {
        n = 5;
    }

    var text = '';
    var possible = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    for (var i = 0; i < n; i++) {
        text += possible.charAt(Math.floor(Math.random() * possible.length));
    }

    return text;
}

$.fn.datepicker.defaults.format = "dd/mm/yyyy";
$.fn.datepicker.defaults.language = "es";
$.fn.datepicker.defaults.autoclose = true;
$.fn.datepicker.defaults.todayHighlight = false;
$.fn.datepicker.defaults.showButtonPanel = false;


MODAL = {
    idModalMd: "modalVik",
    idModalLg: "modalVikLarge",
    idModalSm: "modalVikSmall",
    idModalFoto: "modalVikFoto",
    modalActivo: null,
    idContent: "contentModalVik",
    idTitle: "titleModalVik",
    idSize: "modalVik-size",
    idBody: "bodyModalVik",
    idFooter: "footerModalVik",
    idBtnClose: "btnCerrarModalVik",
    idDivButtons: "buttonsModalVik",
    textButtonAffected: "",
    textModalWait: "",
    buttonAffected: null,
    init: function (type) {
        if (type == "sm") {
            MODAL.modalActivo = $("#" + MODAL.idModalSm);
        } else if (type == "lg") {
            MODAL.modalActivo = $("#" + MODAL.idModalLg);
        } else if (type == "md") {
            MODAL.modalActivo = $("#" + MODAL.idModalMd);
        } else if (type == "foto") {
            MODAL.modalActivo = $("#" + MODAL.idModalFoto);
        }
        MODAL.body("");
        MODAL.buttons("");
        MODAL.activateButtons();
    },
    size: function (size) {
        var divSize = MODAL.modalActivo.find("#" + MODAL.idSize);
        divSize.removeClass("modal-sm");
        divSize.removeClass("modal-lg");
        divSize.removeClass("modal-md");

        if (size == "sm") {
            divSize.addClass("modal-sm");
        } else if (size == "lg") {
            divSize.addClass("modal-lg");
        } else if (size == "md") {
            divSize.addClass("modal-md");
        }
    },
    modalDefault: function () {
        if (MODAL.modalActivo == null) {
            MODAL.modalActivo = $("#" + MODAL.idModalMd);
        }
    },
    title: function (html) {
        MODAL.modalDefault();
        var title = MODAL.modalActivo.find("#" + MODAL.idTitle);
        var div = title.closest("div");
        if (html != "") {
            div.removeClass("hide");
            title.html(html);
        } else {
            div.addClass("hide");
        }
    },
    buttons: function (html) {
        MODAL.modalDefault();
        MODAL.modalActivo.find("#" + MODAL.idDivButtons).html(html);
    },
    body: function (html) {
        MODAL.modalDefault();
        if (html == null) {
            return MODAL.modalActivo.find("#" + MODAL.idBody);
        }
        MODAL.modalActivo.find("#" + MODAL.idBody).html(html);
    },
    getBody: function () {
        MODAL.modalDefault();
        return MODAL.modalActivo.find("#" + MODAL.idBody);
    },
    getFooter: function () {
        MODAL.modalDefault();
        return MODAL.modalActivo.find("#" + MODAL.idFooter);
    },
    show: function () {
        MODAL.modalDefault();
        MODAL.modalActivo.modal();
    },
    invisible: function () {
        MODAL.modalDefault();
        MODAL.modalActivo.modal("hide");
    },
    hide: function () {
        MODAL.modalDefault();
        MODAL.modalActivo.modal("hide");
        MODAL.limpiar(MODAL.modalActivo);
    },
    limpiar: function (modal) {
        MODAL.modalDefault();
        modal.find("#" + MODAL.idTitle).html("");
        modal.find("#" + MODAL.idBody).html("");
        modal.find("#" + MODAL.idDivButtons).html("");
    },
    disableButtons: function (btn, htmlBtn) {
        MODAL.modalActivo.find("button").each(function (i, item) {
            $(item).attr("disabled", "disabled");
        });
        MODAL.modalActivo.find("a").each(function (i, item) {
            $(item).attr("disabled", "disabled");
        });
        if (btn != null) {
            MODAL.buttonAffected = btn;
            if (htmlBtn != null) {
                MODAL.textButtonAffected = btn.html();
                btn.html(htmlBtn);
            } else {
                MODAL.textButtonAffected = btn.html();
                btn.html('<i class="fa fa-spinner fa-spin fa-lg"></i> Procesando');
            }
        }
    },
    activateButtons: function (btn, htmlBtn) {
        setTimeout(function () {
            var footer = MODAL.modalActivo.find("#" + MODAL.idFooter);
            footer.find("button").each(function (i, item) {
                $(item).removeAttr("disabled");
            });
            footer.find("a").each(function (i, item) {
                $(item).removeAttr("disabled");
            });
            if (btn != null) {
                btn.html(htmlBtn);
                return;
            }
            if (MODAL.buttonAffected != null && htmlBtn != null) {
                MODAL.buttonAffected.html(htmlBtn);
                return;
            }
            if (MODAL.buttonAffected != null) {
                MODAL.buttonAffected.html(MODAL.textButtonAffected);
            }
        }, 800);
    },
    showWait: function (msg) {
        if (msg != null) {
            MODAL.textModalWait = $("#bodyModalWait").html();
            $("#bodyModalWait").html(msg);
        }
        $("#modalWait").modal();
    },
    hideWait: function () {
        setTimeout(function () {
            $("#bodyModalWait").html(MODAL.textModalWait);
            $("#modalWait").modal('hide');
        }, 1000);
    }
};


APP = {
    cleanForm: function (f) {
        f.find("input[type=text], textarea, #id").val("");
        f.find("input[type=checkbox]").prop("checked", false);
        f.find("input[name='*[]']").prop("checked", false);
        f.find("input[name='id*']").val("");
    },
    cleanAll: function (f) {
        f.find("input, textarea").val("");
    },
    fillFormById: function (data, f) {
        $.each(data, function (index, value) {
            f.find('#' + index).val(value);
        });
    },
    fillFormByName: function (data, f) {
        $.each(data, function (index, value) {
            f.find('[name="' + index + '"]').val(value);
        });
    },
    select2: function () {
        $(".select2single").select2({minimumResultsForSearch: -1});
        $(".select2").select2()
    },
    url: function (relative) {
        return contextPath + relative;
    },
    datePicker: {format: 'dd/mm/yyyy', language: 'es', autoclose: true, todayHighlight: true},
    timePicker: {
        minuteStep: 5,
        showInputs: true,
        disableFocus: true,
        showSeconds: false,
        showMeridian: false,
    },
    disableButtonsModal: function (idFooter, btn, htmlBtn) {
        $("#" + idFooter).find("button").each(function (i, item) {
            $(item).attr("disabled", "disabled");
        });
        $("#" + idFooter).find("a").each(function (i, item) {
            $(item).attr("disabled", "disabled");
        });
        if (btn != null) {
            if (htmlBtn != null) {
                btn.html(htmlBtn);
            } else {
                btn.html('<i class="fa fa-spinner fa-spin fa-lg"></i> Procesando');
            }
        }
    },
    activatedButtonsModal: function (idFooter, btn, htmlBtn) {
        setTimeout(function () {
            $("#" + idFooter).find("button").each(function (i, item) {
                $(item).removeAttr("disabled");
            });
            $("#" + idFooter).find("a").each(function (i, item) {
                $(item).removeAttr("disabled");
            });
            if (btn != null) {
                btn.html(htmlBtn);
            }
        }, 1000);
    },
    disableButtonsModalVik: function (btn, htmlBtn) {
        $("#footerModalVik").find("button").each(function (i, item) {
            $(item).attr("disabled", "disabled");
        });
        $("#footerModalVik").find("a").each(function (i, item) {
            $(item).attr("disabled", "disabled");
        });
        if (btn != null) {
            if (htmlBtn != null) {
                btn.html(htmlBtn);
            } else {
                btn.html('<i class="fa fa-spinner fa-spin fa-lg"></i> Procesando');
            }
        }
    },
    activatedButtonsModalVik: function (btn, htmlBtn) {
        setTimeout(function () {
            $("#footerModalVik").find("button").each(function (i, item) {
                $(item).removeAttr("disabled");
            });
            $("#footerModalVik").find("a").each(function (i, item) {
                $(item).removeAttr("disabled");
            });
            if (btn != null) {
                btn.html(htmlBtn);
            }
        }, 1000);
    },
    activatedRapidoButtonsModalVik: function (btn, htmlBtn) {
        $("#footerModalVik").find("button").each(function (i, item) {
            $(item).removeAttr("disabled");
        });
        $("#footerModalVik").find("a").each(function (i, item) {
            $(item).removeAttr("disabled");
        });
        if (btn != null) {
            btn.html(htmlBtn);
        }
    },
    verModalWait: function (msg) {
        if (msg != null) {
            $("#bodyModalWait").html(msg);
        }
        $("#modalWait").modal();
    },
    cerrarModalWait: function () {
        setTimeout(function () {
            $("#modalWait").modal('hide');
        }, 1000);
    },
    cerrarRapidoModalWait: function () {
        $("#modalWait").modal('hide');
    },
    limpiarRaros: function ($this) {
        var conte = $this.val();
        conte = conte.replace(/[\n\f\b\r\t]/g, '');
        $this.val(conte);
    },
    revisarNombre: function ($this) {
        var nom = $this.val().toLowerCase().replace(/[^a-zçñáéíóúü\s'\-]/g, '');
        nom = nom.replace(/[\n\f\b\r|,\t]/g, ' ').replace(/\s\s+/g, ' ').trim();
        nom = APP.capitalize(nom, " ");
        nom = APP.capitalize(nom, "'");
        nom = APP.capitalize(nom, "-");

        $this.val(nom);
    },
    capitalize: function (string, separator) {
        var arr = string.split(separator);
        $.each(arr, function (i, value) {
            arr[i] = value.charAt(0).toUpperCase() + value.substr(1);
        });
        return arr.join(separator);
    },
    eliminarEspacios: function ($this) {
        var conte = $this.val().replace(/[\s\n\f\b\r\t]/g, '');
        $this.val(conte);
    },
    revisarDireccion: function ($this) {
        var conte = $this.val().replace(/[\n\f\b\r\t|]/g, ' ').replace(/\s\s+/g, ' ').trim();
        $this.val(conte);
    },
    revisarEmail: function ($this) {
        var conte = $this.val().toLowerCase().replace(/[\n\f\b\r\t\s|,'"!$%&/]/g, '').trim();
        conte = APP.stripAccents(conte);
        $this.val(conte);
    },
    stripAccents: function (str) {
        var from = "àáäâèéëêìíïîòóöôùúüûñç";
        var to = "aaaaeeeeiiiioooouuuunc";
        for (var i = 0, l = from.length; i < l; i++) {
            str = str.replace(new RegExp(from.charAt(i), 'g'), to.charAt(i));
        }
        return str;
    }

}

MESSAGES = {
    errorComunicacion: 'Error de conexión con el servidor.',
    confirmDelete: '¿Seguro que desea eliminar el registro?'
}


APP.select2();

FILES = {
    css: 'Hoja de Estilos',
    js: 'JavaScript',
    json: 'JSON',
    sql: 'SQLScript',
    html: 'HTML',
    txt: 'Texto Plano',
}

ICONS = {
    folder: 'fa-folder',
    file: 'fa-file-o',
    doc: 'fa-file-word-o',
    docx: 'fa-file-word-o',
    xls: 'fa-file-excel-o',
    xlsx: 'fa-file-excel-o',
    ppt: 'fa-file-powerpoint-o',
    pptx: 'fa-file-powerpoint-oo',
    pdf: 'fa-file-pdf-o',
    svg: 'fa-file-image-o',
    tiff: 'fa-file-image-o',
    wav: 'fa-file-audio-o',
    mp3: 'fa-file-audio-o',
    ogg: 'fa-file-audio-o',
    wma: 'fa-file-audio-o',
    mov: 'fa-file-movie-o',
    mp4: 'fa-file-movie-o',
    avi: 'fa-file-movie-o',
    mkv: 'fa-file-movie-o',
    zip: 'fa-file-zip-o',
    gz: 'fa-file-zip-o',
    rar: 'fa-file-zip-o',
    '7zip': 'fa-file-zip-o',
    tgz: 'fa-file-zip-o',
    tar: 'fa-file-zip-o',
    sql: 'fa-file-code-o',
    java: 'fa-file-code-o',
    php: 'fa-file-code-o',
    python: 'fa-file-code-o',
    ruby: 'fa-file-code-o',
    html: 'fa-file-code-o',
    css: 'fa-file-code-o',
    js: 'fa-file-code-o',
    txt: 'fa-file-text-o',
    text: 'fa-file-text-o'
}

/**
 *
 *  Base64 encode / decode
 *  http://www.webtoolkit.info/
 *
 **/
var Base64 = {

// private property
    _keyStr: "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",

// public method for encoding
    encode: function (input) {
        var output = "";
        var chr1, chr2, chr3, enc1, enc2, enc3, enc4;
        var i = 0;

        input = Base64._utf8_encode(input);

        while (i < input.length) {

            chr1 = input.charCodeAt(i++);
            chr2 = input.charCodeAt(i++);
            chr3 = input.charCodeAt(i++);

            enc1 = chr1 >> 2;
            enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
            enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
            enc4 = chr3 & 63;

            if (isNaN(chr2)) {
                enc3 = enc4 = 64;
            } else if (isNaN(chr3)) {
                enc4 = 64;
            }

            output = output +
                    this._keyStr.charAt(enc1) + this._keyStr.charAt(enc2) +
                    this._keyStr.charAt(enc3) + this._keyStr.charAt(enc4);

        }

        return output;
    },

// public method for decoding
    decode: function (input) {
        var output = "";
        var chr1, chr2, chr3;
        var enc1, enc2, enc3, enc4;
        var i = 0;

        input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");

        while (i < input.length) {

            enc1 = this._keyStr.indexOf(input.charAt(i++));
            enc2 = this._keyStr.indexOf(input.charAt(i++));
            enc3 = this._keyStr.indexOf(input.charAt(i++));
            enc4 = this._keyStr.indexOf(input.charAt(i++));

            chr1 = (enc1 << 2) | (enc2 >> 4);
            chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
            chr3 = ((enc3 & 3) << 6) | enc4;

            output = output + String.fromCharCode(chr1);

            if (enc3 != 64) {
                output = output + String.fromCharCode(chr2);
            }
            if (enc4 != 64) {
                output = output + String.fromCharCode(chr3);
            }

        }

        output = Base64._utf8_decode(output);

        return output;

    },

// private method for UTF-8 encoding
    _utf8_encode: function (string) {
        string = string.replace(/\r\n/g, "\n");
        var utftext = "";

        for (var n = 0; n < string.length; n++) {

            var c = string.charCodeAt(n);

            if (c < 128) {
                utftext += String.fromCharCode(c);
            } else if ((c > 127) && (c < 2048)) {
                utftext += String.fromCharCode((c >> 6) | 192);
                utftext += String.fromCharCode((c & 63) | 128);
            } else {
                utftext += String.fromCharCode((c >> 12) | 224);
                utftext += String.fromCharCode(((c >> 6) & 63) | 128);
                utftext += String.fromCharCode((c & 63) | 128);
            }

        }

        return utftext;
    },

// private method for UTF-8 decoding
    _utf8_decode: function (utftext) {
        var string = "";
        var i = 0;
        var c = c1 = c2 = 0;

        while (i < utftext.length) {

            c = utftext.charCodeAt(i);

            if (c < 128) {
                string += String.fromCharCode(c);
                i++;
            } else if ((c > 191) && (c < 224)) {
                c2 = utftext.charCodeAt(i + 1);
                string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
                i += 2;
            } else {
                c2 = utftext.charCodeAt(i + 1);
                c3 = utftext.charCodeAt(i + 2);
                string += String.fromCharCode(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
                i += 3;
            }

        }

        return string;
    }

}
