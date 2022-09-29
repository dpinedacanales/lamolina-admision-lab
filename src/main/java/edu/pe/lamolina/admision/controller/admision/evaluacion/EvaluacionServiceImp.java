package edu.pe.lamolina.admision.controller.admision.evaluacion;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;
import edu.pe.lamolina.admision.dao.calificacion.TemaExamenDAO;
import edu.pe.lamolina.admision.dao.calificacion.TemaExamenModalidadDAO;
import edu.pe.lamolina.admision.dao.examen.EncuestaPostulanteDAO;
import edu.pe.lamolina.admision.dao.examen.ExamenInteresadoDAO;
import edu.pe.lamolina.admision.dao.examen.ExamenVirtualDAO;
import edu.pe.lamolina.admision.dao.examen.OpcionPreguntaDAO;
import edu.pe.lamolina.admision.dao.examen.PreguntaExamenDAO;
import edu.pe.lamolina.admision.dao.examen.RespuestaInteresadoDAO;
import edu.pe.lamolina.admision.dao.examen.TemaExamenVirtualDAO;
import edu.pe.lamolina.admision.dao.finanzas.DeudaInteresadoDAO;
import edu.pe.lamolina.admision.dao.inscripcion.PostulanteDAO;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import edu.pe.lamolina.admision.dao.examen.BloquePreguntasDAO;
import edu.pe.lamolina.admision.dao.examen.SubTituloExamenDAO;
import pe.edu.lamolina.model.calificacion.TemaExamen;
import pe.edu.lamolina.model.calificacion.TemaExamenModalidad;
import pe.edu.lamolina.model.enums.EstadoRespuestaInteresadoEnum;
import pe.edu.lamolina.model.enums.TipoExamenVirtualEnum;
import pe.edu.lamolina.model.examen.BloquePreguntas;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.OpcionPregunta;
import pe.edu.lamolina.model.examen.PreguntaExamen;
import pe.edu.lamolina.model.examen.SubTituloExamen;
import pe.edu.lamolina.model.examen.TemaExamenVirtual;
import pe.edu.lamolina.model.finanzas.DeudaInteresado;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ExamenInteresado;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.Postulante;
import pe.edu.lamolina.model.inscripcion.RespuestaInteresado;
import edu.pe.lamolina.admision.controller.admision.inscripcion.guiapostulante.GuiaPostulanteService;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.edu.lamolina.model.constantines.AdmisionConstantine;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;

@Service
@Transactional(readOnly = true)
public class EvaluacionServiceImp implements EvaluacionService {

    @Autowired
    PostulanteDAO postulanteDAO;
    @Autowired
    EncuestaPostulanteDAO encuestaPostulanteDAO;
    @Autowired
    RespuestaInteresadoDAO respuestaInteresadoDAO;
    @Autowired
    ExamenVirtualDAO examenDAO;
    @Autowired
    ExamenInteresadoDAO examenInteresadoDAO;
    @Autowired
    TemaExamenModalidadDAO temaExamenModalidadDAO;
    @Autowired
    TemaExamenVirtualDAO temaExamenVirtualDAO;
    @Autowired
    SubTituloExamenDAO subTituloExamenVirtualDAO;
    @Autowired
    BloquePreguntasDAO bloqueExamenVirtualDAO;
    @Autowired
    PreguntaExamenDAO preguntaExamenDAO;
    @Autowired
    TemaExamenDAO temaExamenDAO;
    @Autowired
    OpcionPreguntaDAO opcionPreguntaDAO;
    @Autowired
    DeudaInteresadoDAO deudaInteresadoDAO;
    @Autowired
    GuiaPostulanteService guiaPostulanteService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean tienePermiso(Interesado interesado) {
        List<DeudaInteresado> deudas = guiaPostulanteService.allByInteresado(interesado);
        return guiaPostulanteService.validandoExamenVirtual(interesado, deudas);
    }

    @Override
    @Transactional
    public List<RespuestaInteresado> findEvaluacionPostulante(ExamenInteresado evaluacion) {
        List<RespuestaInteresado> respuestas = respuestaInteresadoDAO.allByInteresado(evaluacion);

        List<PreguntaExamen> preguntas = respuestas.stream().
                map(RespuestaInteresado::getPregunta).
                collect(Collectors.toList());
        List<OpcionPregunta> opciones = opcionPreguntaDAO.allByPreguntas(preguntas);

        HashMap<Long, List<OpcionPregunta>> mapOpciones = new HashMap<>();

        opciones.forEach((opt) -> {
            Long pregunta = opt.getPregunta().getId();
            List<OpcionPregunta> item = mapOpciones.get(pregunta);
            if (item == null) {
                item = new ArrayList<>();
            }
            item.add(opt);
            mapOpciones.put(pregunta, item);
        });

        preguntas.forEach((pregunta) -> {
            List<OpcionPregunta> listOpcion = mapOpciones.get(pregunta.getId());
            long seed = System.nanoTime();
            if (listOpcion != null) {
                Collections.shuffle(listOpcion, new Random(seed));
            }
            pregunta.setOpcionPregunta(listOpcion);
        });

        return respuestas;
    }

    @Override
    @Transactional
    public void addRespuesta(Long preguntaId, Long opcionId, Map<Long, RespuestaInteresado> mapRespuestas, ExamenInteresado examen) {

        RespuestaInteresado respuesta = mapRespuestas.get(preguntaId);

        if (respuesta == null) {
            throw new PhobosException("Error al guardar respuesta.");
        }

        respuesta.setOpcion(new OpcionPregunta(opcionId));
        respuesta.setFechaCreacion(new Date());
        respuesta.setExamenInteresado(examen);
        if (respuesta.getId() != null) {
            respuestaInteresadoDAO.update(respuesta);
        } else {
            respuestaInteresadoDAO.save(respuesta);
        }

        mapRespuestas.put(preguntaId, respuesta);
        List<RespuestaInteresado> respuestas = new ArrayList(mapRespuestas.values());

        JsonNodeFactory factory = JsonNodeFactory.instance;
        ArrayNode array = new ArrayNode(factory);
        for (RespuestaInteresado rpta : respuestas) {
            ObjectNode json = JsonHelper.createJson(rpta, factory, false, new String[]{
                "*",
                "examenInteresado.id",
                "opcion.id",
                "pregunta.id"
            });
            array.add(json);
        }

        examen.setPreguntasJson(array.toString());
        examenInteresadoDAO.save(examen);

    }

    @Override
    @Transactional
    public void finalizarEvaluacionInteresado(Interesado interesado) {
        ExamenInteresado examenInteresado = examenInteresadoDAO.findExamenInteresadoActivo(interesado);
        if (examenInteresado != null) {
            this.finalizarEvaluacion(examenInteresado);
        }
    }

    @Override
    @Transactional
    public void finalizarEvaluacion(ExamenInteresado examenInteresado) {
        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        mapper.setDateFormat(formatter);
        List<RespuestaInteresado> respuestas = new ArrayList();
        try {
            if (!StringUtils.isEmpty(examenInteresado.getPreguntasJson())) {
                respuestas = (List<RespuestaInteresado>) mapper.readValue(examenInteresado.getPreguntasJson(), new TypeReference<List<RespuestaInteresado>>() {
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int respuestaCorrecta = 0;
        int respuestaIncorrecta = 0;
        int respuestaBlanco = 0;
        int total = 0;

        List<OpcionPregunta> opciones = new ArrayList();
        for (RespuestaInteresado respuesta : respuestas) {
            ObjectUtil.eliminarAttrSinId(respuesta);
            if (respuesta.getOpcion() != null) {
                opciones.add(respuesta.getOpcion());
            }
        }

        List<OpcionPregunta> opcionesBD = opcionPreguntaDAO.allByOpciones(opciones);
        Map<Long, OpcionPregunta> mapOpciones = TypesUtil.convertListToMap("id", opcionesBD);

        for (RespuestaInteresado respuesta : respuestas) {

            if (respuesta.getOpcion() == null) {
                respuestaBlanco++;
                continue;
            }
            respuesta.setOpcion(mapOpciones.get(respuesta.getOpcion().getId()));

            if (respuesta.getOpcion().getEsCorrecta() == 1) {
                respuestaCorrecta++;
            }

            if (respuesta.getOpcion().getEsCorrecta() == 0) {
                respuestaIncorrecta++;
            }

        }

        total = respuestaCorrecta + respuestaIncorrecta + respuestaBlanco;

        if (total == 0 || total == respuestaBlanco) {
            examenInteresadoDAO.delete(examenInteresado);
            return;
        }

        examenInteresado.setRespuestasCorrectas(respuestaCorrecta);
        examenInteresado.setRespuestasIncorrectas(respuestaIncorrecta);
        examenInteresado.setRespuestasVacias(respuestaBlanco);
        examenInteresado.setFechaFin(new Date());

        ExamenVirtual examenVirtual = examenInteresado.getExamen();

        BigDecimal respCorrecta = examenVirtual.getPuntajeRespuestaCorrecta().multiply(new BigDecimal(respuestaCorrecta));
        BigDecimal respIncorrecta = examenVirtual.getPuntajeRespuestaIncorrecta().multiply(new BigDecimal(respuestaIncorrecta));
        BigDecimal respBlanco = examenVirtual.getPuntajeRespuestaVacia().multiply(new BigDecimal(respuestaBlanco));
        BigDecimal preresultado = respCorrecta.subtract(respIncorrecta);
        BigDecimal resultado = preresultado.subtract(respBlanco);

        examenInteresado.setPuntaje(resultado);

        BigDecimal producto = resultado.multiply(new BigDecimal(20));
        BigDecimal nota = producto.divide(new BigDecimal(total), 2, BigDecimal.ROUND_HALF_UP);
        examenInteresado.setNota(nota);
        examenInteresado.setPreguntasJson(null);

        examenInteresadoDAO.update(examenInteresado);

    }

    private List<BloquePreguntas> createBloquesBySubtitulo(SubTituloExamen subtitulo) {
        if (subtitulo.getBloquesVisibles() == 0) {
            return new ArrayList();
        }

        List<BloquePreguntas> bloques = bloqueExamenVirtualDAO.allByTituloExamenVirtual(subtitulo);
        Integer min = 0;
        Integer max = bloques.size();
        while (max > subtitulo.getBloquesVisibles()) {
            int randomIndex = ThreadLocalRandom.current().nextInt(0, bloques.size());
            bloques.remove(randomIndex);
            max = bloques.size();
        }
        bloques.forEach((bloque) -> {
            bloque.setSubTituloExamen(subtitulo);
        });
        return bloques;
    }

    private List<PreguntaExamen> createPreguntasBySubtitulo(SubTituloExamen subtitulo) {
        if (subtitulo.getPreguntasVisibles() == 0) {
            return new ArrayList();
        }

        List<PreguntaExamen> preguntas = preguntaExamenDAO.allBySubTituloExamenVirtual(subtitulo);
        Integer min = 0;
        Integer max = preguntas.size();
        while (max > subtitulo.getPreguntasVisibles()) {
            int randomIndex = ThreadLocalRandom.current().nextInt(0, preguntas.size());
            preguntas.remove(randomIndex);
            max = preguntas.size();
        }
        preguntas.forEach((pregunta) -> {
            pregunta.setSubtitulo(subtitulo);
        });
        return preguntas;
    }

    private List<SubTituloExamen> createSubtitulosByTema(TemaExamenVirtual tema) {
        if (tema.getSubtitulosVisibles() == 0) {
            return new ArrayList();
        }

        List<SubTituloExamen> subtitulos = subTituloExamenVirtualDAO.allByTemaExamenVirtual(tema);
        Integer min = 0;
        Integer max = subtitulos.size();
        while (max > tema.getSubtitulosVisibles()) {
            int randomIndex = ThreadLocalRandom.current().nextInt(0, subtitulos.size());
            subtitulos.remove(randomIndex);
            max = subtitulos.size();
        }
        subtitulos.forEach((subtitulo) -> {
            subtitulo.setTemaExamen(tema);
        });
        return subtitulos;
    }

    private List<PreguntaExamen> createPreguntasByTema(TemaExamenVirtual tema) {
        if (tema.getPreguntasVisibles() == 0) {
            return new ArrayList();
        }

        List<PreguntaExamen> preguntas = preguntaExamenDAO.allByTemaExamenVirtual(tema);
        Integer min = 0;
        Integer max = preguntas.size();
        while (max > tema.getPreguntasVisibles()) {
            int randomIndex = ThreadLocalRandom.current().nextInt(0, preguntas.size());
            preguntas.remove(randomIndex);
            max = preguntas.size();
        }
        preguntas.forEach((pregunta) -> {
            pregunta.setTema(tema);
        });
        return preguntas;
    }

    private List<PreguntaExamen> createPreguntasByBloque(BloquePreguntas bloque) {
        if (bloque.getPreguntasVisibles() == 0) {
            return new ArrayList();
        }

        List<PreguntaExamen> preguntas = preguntaExamenDAO.allByBloqueExamenVirtual(bloque);
        Integer min = 0;
        Integer max = preguntas.size();
        while (max > bloque.getPreguntasVisibles()) {
            int randomIndex = ThreadLocalRandom.current().nextInt(0, preguntas.size());
            preguntas.remove(randomIndex);
            max = preguntas.size();
        }
        preguntas.forEach((pregunta) -> {
            pregunta.setBloquePreguntas(bloque);
        });
        return preguntas;
    }

    @Override
    public ExamenInteresado findOrCreateEvaluacionByInteresado(Interesado interesado, ExamenInteresado evaluacion) {
        if (evaluacion != null) {
            return evaluacion;
        }

        ExamenInteresado examen = examenInteresadoDAO.findExamenInteresadoActivo(interesado);
        if (examen != null) {
            if (examen.isVigente()) {
                return examen;
            }
            finalizarEvaluacion(examen);
        }
        return createEvaluacion(interesado);
    }

    @Override
    public ExamenInteresado findEvaluacionByInteresado(Interesado interesado, ExamenInteresado evaluacion) {
        if (evaluacion != null) {
            return evaluacion;
        }

        ExamenInteresado examen = examenInteresadoDAO.findExamenInteresadoActivo(interesado);
        if (examen != null) {
            if (examen.isVigente()) {
                return examen;
            }
            finalizarEvaluacion(examen);
        }
        return null;
    }

    @Transactional
    private ExamenInteresado createEvaluacion(Interesado interesado) {

        Postulante postulante = postulanteDAO.findActivoByInteresadoSimple(interesado);
        CicloPostula ciclo = postulante.getCicloPostula();

        ExamenVirtual evaluacion = examenDAO.findExamenByCicloPostula(ciclo, TipoExamenVirtualEnum.EVA.name());
        List<TemaExamenModalidad> temaExamenModalidades = new ArrayList();
        if (postulante.getModalidadIngreso() != null) {
            ModalidadIngreso modalidadIngreso = postulante.getModalidadIngreso();
            if (modalidadIngreso.isParticipanteLibre()) {
                temaExamenModalidades = temaExamenModalidadDAO.allByCicloModalidad(ciclo, postulante.getModalidadSimulacion());
            } else {
                temaExamenModalidades = temaExamenModalidadDAO.allByCicloModalidad(ciclo, modalidadIngreso);
            }
        }

        List<String> codigoTemas = temaExamenModalidades.stream()
                .map(TemaExamenModalidad::getTemaExamen)
                .map(TemaExamen::getCodigo)
                .collect(Collectors.toList());

        if (codigoTemas.isEmpty()) {
            List<TemaExamen> temas = temaExamenDAO.all();
            codigoTemas = temas.stream()
                    .map(TemaExamen::getCodigo)
                    .collect(Collectors.toList());
        }

        List<PreguntaExamen> preguntasExamen = new ArrayList();
        List<TemaExamenVirtual> temaExamenVirtuals = temaExamenVirtualDAO.allByExamenCodigo(evaluacion, codigoTemas);

        for (TemaExamenVirtual tema : temaExamenVirtuals) {
            if (tema.getSubtitulosVisibles() > 0) {
                List<PreguntaExamen> preguntasTema = new ArrayList();
                boolean girar = true;
                while (girar) {
                    preguntasTema = new ArrayList();
                    List<SubTituloExamen> subtitulos = createSubtitulosByTema(tema);
                    for (SubTituloExamen subtitulo : subtitulos) {
                        List<BloquePreguntas> bloques = createBloquesBySubtitulo(subtitulo);
                        for (BloquePreguntas bloque : bloques) {
                            List<PreguntaExamen> preguntasBloque = createPreguntasByBloque(bloque);
                            preguntasTema.addAll(preguntasBloque);
                        }
                        List<PreguntaExamen> preguntasSubtitulo = createPreguntasBySubtitulo(subtitulo);
                        preguntasTema.addAll(preguntasSubtitulo);
                    }
                    girar = (preguntasTema.size() != tema.getPreguntasVisibles());
                }
                preguntasExamen.addAll(preguntasTema);

            } else {
                List<PreguntaExamen> preguntasTema = createPreguntasByTema(tema);
                preguntasExamen.addAll(preguntasTema);
            }
        }

        BigDecimal totalEvaluacion = new BigDecimal(preguntasExamen.size());
        Long factorConversion = AdmisionConstantine.MINUTE_TO_MILISECOND;
        BigDecimal tiempoEvaluacion = totalEvaluacion.multiply(evaluacion.getTiempoPregunta()).multiply(new BigDecimal(factorConversion));

        ExamenInteresado examen = new ExamenInteresado();
        examen.setExamen(evaluacion);
        examen.setInteresado(interesado);
        examen.setFechaInicio(new Date());
        examen.setTiempoEvaluacion(tiempoEvaluacion.intValue());

        if (preguntasExamen.isEmpty()) {
            examenInteresadoDAO.save(examen);
            return null;
        }

        List<OpcionPregunta> opciones = opcionPreguntaDAO.allByPreguntas(preguntasExamen);
        Map<Long, List<OpcionPregunta>> mapOpciones = TypesUtil.convertListToMapList("pregunta.id", opciones);

        JsonNodeFactory factory = JsonNodeFactory.instance;
        ArrayNode array = new ArrayNode(factory);

        for (PreguntaExamen pregunta : preguntasExamen) {
            RespuestaInteresado respuesta = new RespuestaInteresado();
            respuesta.setPregunta(pregunta);
            respuesta.setExamenInteresado(examen);
            respuesta.setFechaCreacion(new Date());
            respuesta.setEstado(EstadoRespuestaInteresadoEnum.ACT.name());
            respuesta.setLetraOpcion(pregunta.getLetra());
            respuesta.setNumeroPregunta(pregunta.getNumero());

            List<OpcionPregunta> opcionesPregunta = mapOpciones.get(pregunta.getId());
            long seed = System.nanoTime();
            if (opcionesPregunta != null) {
                Collections.shuffle(opcionesPregunta, new Random(seed));
            }
            pregunta.setOpcionPregunta(opcionesPregunta);
            ObjectNode json = JsonHelper.createJson(respuesta, factory, false, new String[]{
                "*",
                "examenInteresado.id",
                "opcion.id",
                "pregunta.id"
            });
            array.add(json);
        }

        examen.setPreguntasJson(array.toString());
        examenInteresadoDAO.save(examen);

        return examen;
    }

    @Override
    public List<ExamenInteresado> allEvaluacionByInteresado(Interesado interesado) {
        return examenInteresadoDAO.allEvaluacionByInteresado(interesado);
    }

    @Override
    public Map<Long, RespuestaInteresado> allRespuestaInteresado(ExamenInteresado examen) {

        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        mapper.setDateFormat(formatter);
        List<RespuestaInteresado> respuestas = new ArrayList();
        try {
            respuestas = (List<RespuestaInteresado>) mapper.readValue(examen.getPreguntasJson(), new TypeReference<List<RespuestaInteresado>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<PreguntaExamen> preguntas = respuestas.stream().
                map(RespuestaInteresado::getPregunta).
                collect(Collectors.toList());

        List<PreguntaExamen> preguntasBD = preguntaExamenDAO.allByPreguntas(preguntas);
        Map<Long, PreguntaExamen> mapPreguntas = TypesUtil.convertListToMap("id", preguntasBD);
        for (RespuestaInteresado respuesta : respuestas) {
            PreguntaExamen pgta = mapPreguntas.get(respuesta.getPregunta().getId());
            respuesta.setPregunta(pgta);
        }

        List<OpcionPregunta> opciones = opcionPreguntaDAO.allByPreguntas(preguntas);
        Map<Long, List<OpcionPregunta>> mapOpciones = TypesUtil.convertListToMapList("pregunta.id", opciones);

        preguntasBD.forEach((pregunta) -> {
            List<OpcionPregunta> opcionesPregunta = mapOpciones.get(pregunta.getId());
            for (OpcionPregunta opcion : opcionesPregunta) {
                opcion.setPregunta(pregunta);
            }

            long seed = System.nanoTime();
            if (opcionesPregunta != null) {
                Collections.shuffle(opcionesPregunta, new Random(seed));
            }
            pregunta.setOpcionPregunta(opcionesPregunta);
        });

        Map<Long, RespuestaInteresado> mapRespuestas = TypesUtil.convertListToMap("pregunta.id", respuestas);
        return mapRespuestas;

    }

    @Override
    public PreguntaExamen findPreguntaById(Long idPregunta, CicloPostula ciclo) {
        ExamenVirtual examen = examenDAO.findExamenByCicloPostula(ciclo, TipoExamenVirtualEnum.EVA.name());
        PreguntaExamen pregunta = preguntaExamenDAO.find(idPregunta);
        if (pregunta.getExamenVirtual().getId() != examen.getId().longValue()) {
            return null;
        }

        List<OpcionPregunta> opciones = opcionPreguntaDAO.allByPregunta(pregunta);
        pregunta.setOpcionPregunta(opciones);
        opciones.forEach((opcion) -> {
            opcion.setPregunta(pregunta);
        });

        return pregunta;
    }

    @Override
    public ExamenVirtual findExamenByCiclo(CicloPostula ciclo) {
        return examenDAO.findExamenByCicloPostula(ciclo, TipoExamenVirtualEnum.EVA.name());
    }

    @Override
    public List<PreguntaExamen> allPreguntasByExamen(ExamenVirtual examen) {
        return preguntaExamenDAO.allByExamen(examen);
    }

    @Override
    public PreguntaExamen findPreguntaSiguiente(PreguntaExamen pregunta) {
        return preguntaExamenDAO.findNext(pregunta.getId(), pregunta.getExamenVirtual());
    }

    @Override
    public PreguntaExamen findPreguntaAnterior(PreguntaExamen pregunta) {
        return preguntaExamenDAO.findPrevious(pregunta.getId(), pregunta.getExamenVirtual());
    }

    @Override
    public List<ExamenInteresado> allDynatableEvaluacionByInteresado(Interesado interesado, DynatableFilter filter) {
        return examenInteresadoDAO.allDynatableEvaluacionByInteresado(interesado, filter);
    }

    @Override
    public Long cantidadEvaluaciones(Interesado interesado) {
        return examenInteresadoDAO.countByInteresado(interesado);
    }

    @Override
    public Persona findPersonaByInteresado(Interesado interesado) {
        Postulante postulante = postulanteDAO.findByInteresado(interesado);
        if (postulante == null) {
            return null;
        }
        if (postulante.getPersona() == null) {
            return null;
        }
        Persona persona = postulante.getPersona();
        if (AdmisionConstantine.CODE_POSTULANTE_DUMMY.equals(persona.getNumeroDocIdentidad())) {
            return null;
        }
        return persona;
    }

}
