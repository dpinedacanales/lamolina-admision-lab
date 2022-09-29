$(function () {

    $('#dynaTable').dynatable({
        features: {
            sort: false,
            perPageSelect: false,
            paginate: true,
            search: false,
            recordCount: false
        },
        dataset: {
            ajaxUrl: APP.url('inscripcion/evaluacion/list'),
            perPageDefault: 10
        },
        writers: {
            _rowWriter: function (rowIndex, record, columns, cellWriter) {
                var html = $.templates("#templateResultado").render(record);
                return html;
            }
        },
        table: {
            bodyRowSelector: 'tbody'
        }
    }).data('dynatable');

});
