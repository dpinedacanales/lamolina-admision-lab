$(document).ready(function(){

  var url= window.location.href;
  var uri= window.location.protocol + "//" + window.location.host + "/";
  if(url==uri || url==uri+"index"){
    $(window).scroll(function(){
      var h1 = $(window).height(),
          h3 = 70,
          h2 = $(window).scrollTop();

          if(h2 >= h3){
            $('.heads.indexeded').addClass('activer');
          }else{
            $('.heads.indexeded').removeClass('activer');
          }
    });
  }else {
    $('.heads').addClass('activer');
  }


  $('.lnr.lnr-magnifier').on('click',function(){
    $('.searcher-input').addClass('openly');
  });

  $('.searcher-input .below').on('click',function(){
    $('.searcher-input').removeClass('openly');
  });

  $('.map-container')
	.click(function(){
			$(this).find('iframe').addClass('clicked')})
	.mouseleave(function(){
			$(this).find('iframe').removeClass('clicked')});

  $('.fancybox').fancybox();

  // Carousel

  var admisions = [{"name":"Flujo de Inscripción","link":"flujo-inscripcion"},{"name":"Modalidades de Ingreso","link":"modalidades-ingreso"},
  {"name":"Pago de Inscripción","link":"pago-inscripcion"},{"name":"Temas del Examen","link":"temas-examen"},
  {"name":"Preguntas Frecuentes","link":"faq"}];

  var egresados = [{"name":"Molineros de Éxito","link":"egresados"}];

  var tallereos = [{"name":"Talleres de Carreras","link":"taller"},{"name":"Un Día en la Agraria","link":"dia-en-la-agraria"}];

  var agrarias = [{"name":"Mensaje del Rector","link":"nosotros"},{"name":"Líderes Académicos","link":"lideres-academicos"},
  {"name":"Convenios","link":"convenios"},{"name":"Becarios","link":"becarios"},{"name":"Ubícanos","link":"ubicanos"},
  {"name":"Contáctanos","link":"contacto"}];

  var carreras = [{"name":"Agronomía","link":"agronomia"},{"name":"Biología","link":"biologia"},{"name":"Ingeniería Forestal","link":"ingenieria-forestal"},
  {"name":"Economía","link":"economia"},{"name":"Estadística e Informática","link":"estadistica-informatica"},{"name":"Ingeniería Agrícola","link":"ingenieria-agricola"},
  {"name":"Industrias Alimentarias","link":"industrias-alimentarias"},{"name":"Ingeniería ambiental","link":"ingenieria-ambiental"},{"name":"Ingeniería en Gestión Empresarial","link":"ingenieria-gestion-empresarial"},
  {"name":"Meteorología","link":"meteorologia"},{"name":"Zootecnia","link":"zootecnia"},{"name":"Pesquería","link":"pesqueria"}];

  $('.admis').mouseover(function(){
    $('.sub-contain').html('');
    $.each(admisions, function(k, v) {
          $('.sub-contain').append('<li><a href="'+v.link+'">'+v.name+'</a></li>');
      });
  });

  $('.egres').mouseover(function(){
    $('.sub-contain').html('');
    $.each(egresados, function(k, v) {
          $('.sub-contain').append('<li><a href="'+v.link+'">'+v.name+'</a></li>');
      });
  });

  $('.tallers').mouseover(function(){
    $('.sub-contain').html('');
    $.each(tallereos, function(k, v) {
          $('.sub-contain').append('<li><a href="'+v.link+'">'+v.name+'</a></li>');
      });
  });

  $('.agras').mouseover(function(){
    $('.sub-contain').html('');
    $.each(agrarias, function(k, v) {
          $('.sub-contain').append('<li><a href="'+v.link+'">'+v.name+'</a></li>');
      });
  });

  $('.carrs').mouseover(function(){
    $('.sub-contain').html('');
    $.each(carreras, function(k, v) {
          $('.sub-contain').append('<li><a href="'+v.link+'">'+v.name+'</a></li>');
      });
  });

  $('.carousel').carousel({
    interval: 5000
  })

  $('.indications li').on('click',function(){
    $('.indications li').removeClass('active');
    $(this).addClass('active');
  });

  $('.inserted input').on('focus',function(){
    $(this).parent().addClass('focused');
  });
  $('.inserted input[type="text"]').on('blur',function(){
    $(this).parent().removeClass('focused');
  });

  $('.inserted textarea').on('focus',function(){
    $(this).parent().addClass('focused');
  });
  $('.inserted textarea').on('blur',function(){
    $(this).parent().removeClass('focused');
  });

  // Carousel fin



});
