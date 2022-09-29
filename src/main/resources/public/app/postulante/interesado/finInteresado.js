$(function () {

    var page = $.cookie("page");
    var objectId = $.cookie("objectId");
    var sharp = "";

    if (objectId !== undefined) {
        sharp = sharp + "#" + objectId;
    }

    if (page !== undefined) {
        window.location.href = page + sharp;
    }

    FinInteresado = {

    };

});
