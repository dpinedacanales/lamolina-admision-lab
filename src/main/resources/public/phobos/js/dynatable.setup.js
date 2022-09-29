$(document.body).on('dynatable:init', '#dynaTable', function (e, dynatable) {
    $('.dynatable-search').wrapAll('<div class="row m-b-sm"><div class="col-md-12" id="opopop"/></div>');

    $('.dynatable-record-count, .dynatable-paginate').wrapAll('<div class="footer m-t-lg"/>');

    $('.dynatable-search').addClass('col-md-2 pull-right');
    $('.dynatable-search').find('input')
            .addClass('form-control input-sm')
            .attr('placeholder', 'Buscar');

});

$(document).on('dynatable:afterUpdate','#dynaTable', function (e, dynatable) {
    $('.dynatable-paginate li').first().remove();
});


$.dynatableSetup({
    inputs: {
        paginationClass: 'pagination dynatable-paginate pull-right',
        paginationActiveClass: 'active',
        paginationDisabledClass: 'disabled',
        paginationPrev: 'Anterior',
        paginationNext: 'Siguiente',
        recordCountPageBoundTemplate: '{pageLowerBound} al {pageUpperBound} de',
        recordCountPageUnboundedTemplate: '{recordsShown} de',
        recordCountTotalTemplate: '{recordsQueryCount}',
        recordCountFilteredTemplate: ' (Filtrados de un total de {recordsTotal} registros)',
        recordCountText: '',
        recordCountTextTemplate: '{text} {pageTemplate} {totalTemplate} {filteredTemplate}',
        processingText: 'Cargando Información <i class="fa fa-spinner fa-spin fa-pulse"/>'
    },
    features: {
        sort: false,
        perPageSelect: false
    },
    params: {
        records: 'data',
        totalRecordCount: 'total',
        queryRecordCount: 'filtered'
    },
    dataset: {
        ajax: true,
        ajaxOnLoad: true,
        ajaxDataType: 'json',
        ajaxMethod: 'POST',
        records: [],
        perPageDefault: 8
    },
    table: {
        bodyRowSelector: 'div'
    }
});