$(function () {
    var page = $.cookie("page");
    var objectid = $.cookie("objectId");

    if (page !== undefined) {
        window.location.href = "/route66?page=" + page + "&objectid=" + objectid;
    } else {
        window.location.href = "/route66";
    }
});