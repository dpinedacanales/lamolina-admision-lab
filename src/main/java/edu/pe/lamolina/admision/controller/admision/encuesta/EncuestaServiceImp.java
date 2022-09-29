package edu.pe.lamolina.admision.controller.admision.encuesta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.pe.lamolina.admision.dao.examen.EncuestaPostulanteDAO;
import edu.pe.lamolina.admision.dao.examen.ExamenVirtualDAO;
import edu.pe.lamolina.admision.dao.examen.OpcionPreguntaDAO;
import edu.pe.lamolina.admision.dao.examen.PreguntaExamenDAO;
import edu.pe.lamolina.admision.dao.inscripcion.PostulanteDAO;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.enums.TipoExamenVirtualEnum;
import pe.edu.lamolina.model.enums.TipoPreguntaEncuestaEnum;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.OpcionPregunta;
import pe.edu.lamolina.model.examen.PreguntaExamen;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.EncuestaPostulante;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.Postulante;

@Service
@Transactional(readOnly = true)
public class EncuestaServiceImp implements EncuestaService {

    @Autowired
    PostulanteDAO postulanteDAO;

    @Autowired
    EncuestaPostulanteDAO encuestaPostulanteDAO;

    @Autowired
    ExamenVirtualDAO examenDAO;

    @Autowired
    PreguntaExamenDAO preguntaDAO;

    @Autowired
    OpcionPreguntaDAO opcionPreguntaDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    @Transactional
    public List<EncuestaPostulante> allRespuestasByPostulante(Postulante postulante, CicloPostula ciclo) {
        if (postulante.getEncuesta() != null && postulante.getFechaEncuesta() != null) {
            throw new PhobosException("Encuesta Finalizada.");
        }

        ExamenVirtual encuesta = examenDAO.findEncuestaActivaByCicloPostula(ciclo);
        List<EncuestaPostulante> respuestas = encuestaPostulanteDAO.allByPostulanteEncuesta(postulante, encuesta);

        if (!respuestas.isEmpty()) {
            respuestas.forEach((respuesta) -> {
                PreguntaExamen pregunta = respuesta.getPregunta();
                if (pregunta.getRespuestaMultiple() == 1) {
                    respuesta.setRespuestaOtro(null);
                    respuesta.setOrdenMultiple(null);
                    respuesta.setFechaRespuesta(null);
                    encuestaPostulanteDAO.update(respuesta);
                }
            });

        } else {
            postulante.setEncuesta(encuesta);
            postulanteDAO.update(postulante);
        }

        return respuestas;
    }

    @Override
    public List<PreguntaExamen> allPreguntasByPostulante(Postulante postulante, List<EncuestaPostulante> respuestas, CicloPostula ciclo) {
        ExamenVirtual encuesta = examenDAO.findEncuestaByCicloPostula(ciclo, TipoExamenVirtualEnum.ENC.name());
        List<PreguntaExamen> preguntas = preguntaDAO.allActivasByEncuesta(encuesta);
        List<OpcionPregunta> opciones = opcionPreguntaDAO.allByPreguntas(preguntas);

        Map<Long, List<OpcionPregunta>> mapOpciones = TypesUtil.convertListToMapList("pregunta.id", opciones);
        Map<Long, PreguntaExamen> mapPreguntas = TypesUtil.convertListToMap("id", preguntas);

        Map<Long, EncuestaPostulante> mapRespuestasUnique = TypesUtil.convertListToMap("pregunta.id", respuestas);
        Map<Long, List<EncuestaPostulante>> mapRespuestasMulti = TypesUtil.convertListToMapList("pregunta.id", respuestas);

        preguntas.forEach((pregunta) -> {
            pregunta.setOpcionPregunta(mapOpciones.get(pregunta.getId()));
            if (pregunta.getRespuestaMultiple() == 0 || pregunta.getTipo().equals(TipoPreguntaEncuestaEnum.RPTA_MULTIPLE.name())) {
                pregunta.setEncuesta(mapRespuestasUnique.get(pregunta.getId()));
            }

        });

        Map<String, OpcionPregunta> mapOpcionesStr = new LinkedHashMap();
        opciones.forEach((opcion) -> {
            PreguntaExamen pregunta = mapPreguntas.get(opcion.getPregunta().getId());
            mapOpcionesStr.put(pregunta.getNumero() + "-" + opcion.getLetra(), opcion);
            opcion.setPregunta(pregunta);
            opcion.setPreguntaReferencia(new ArrayList());

            if (pregunta.getRespuestaMultiple() == 1) { 
               Map<Long, EncuestaPostulante> mapRespuestasPgta = TypesUtil.convertListToMap("opcion.id", TypesUtil.getListNotNull(mapRespuestasMulti.get(pregunta.getId())));
                if (mapRespuestasPgta.isEmpty()) {
                    opcion.setRespuesta(new EncuestaPostulante());
                }else{
                    opcion.setRespuesta(mapRespuestasPgta.get(opcion.getId()));
                }
               
               
            }
        });

        preguntas.forEach((pgta) -> {
            if (pgta.getOpcionReferencia() != null) {
                OpcionPregunta opcion = pgta.getOpcionReferencia();
                PreguntaExamen preguntaRef = mapPreguntas.get(opcion.getPregunta().getId());
                OpcionPregunta opcionRef = mapOpcionesStr.get(preguntaRef.getNumero() + "-" + opcion.getLetra());
                pgta.setOpcionReferencia(opcionRef);
                opcionRef.getPreguntaReferencia().add(pgta);
            }
        });

        preguntas.forEach((pgta) -> {
            pgta.setOrden((pgta.getOrden() == null) ? pgta.getNumero() : pgta.getOrden());
        });

        Collections.sort(preguntas, new PreguntaExamen.CompareOrden());
        return preguntas;
    }

    @Override
    @Transactional
    public void finalizarEncuesta(Interesado interesado) {
        Postulante postulante = postulanteDAO.findActivoByInteresadoSimple(interesado);
        Assert.isNotNull(postulante.getEncuesta(), "Error al finalizar encuesta.");
        Assert.isNull(postulante.getFechaEncuesta(), "La encuesta ya se encuentra finalizada");

        postulante.setFechaEncuesta(new Date());
        postulanteDAO.update(postulante);
    }

    @Override
    @Transactional
    public void saveRespuesta(EncuestaPostulante respuestaForm, Postulante postulante, CicloPostula ciclo) {
        ExamenVirtual encuesta = examenDAO.findEncuestaActivaByCicloPostula(ciclo);
        Assert.isNotNull(encuesta, "No existe encuesta configurada para este ciclo");

        List<PreguntaExamen> preguntasBD = preguntaDAO.allActivasByEncuesta(encuesta);
        Map<Long, PreguntaExamen> mapPregunta = TypesUtil.convertListToMap("id", preguntasBD);
        PreguntaExamen pregunta = mapPregunta.get(respuestaForm.getPregunta().getId());
        Assert.isNotNull(pregunta, "No existe la pregunta para la respuesta que envió");
        respuestaForm.setPostulante(postulante);

        List<EncuestaPostulante> respuestasTodasBD = encuestaPostulanteDAO.allByPostulanteEncuesta(postulante, encuesta);
        Map<Long, List<EncuestaPostulante>> mapRespuestasTodas = TypesUtil.convertListToMapList("pregunta.id", respuestasTodasBD);

        List<EncuestaPostulante> respuestasBD = TypesUtil.getListNotNull(mapRespuestasTodas.get(pregunta.getId()));
        if (respuestasBD.isEmpty()) {
            respuestaForm.setFechaCreacion(new Date());
            respuestaForm.setFechaRespuesta(new Date());
            encuestaPostulanteDAO.save(respuestaForm);
            return;
        }

        OpcionPregunta opcionForm = respuestaForm.getOpcion();
        Map<Long, EncuestaPostulante> mapRespuestaOpcion = TypesUtil.convertListToMap("opcion.id", respuestasBD);
        if (pregunta.getRespuestaMultiple() == 0) {
            EncuestaPostulante respuestaExistente = mapRespuestaOpcion.get(opcionForm.getId());
            if (respuestaExistente == null) {
                EncuestaPostulante respuestaBD = respuestasBD.get(0);
                deshabilitarPreguntasRefenciadas(respuestaBD.getOpcion(), preguntasBD, mapRespuestasTodas);
                respuestaBD.setFechaRespuesta(new Date());
                respuestaBD.setOpcion(opcionForm);
                respuestaBD.setRespuestaOtro(respuestaForm.getRespuestaOtro());
                encuestaPostulanteDAO.update(respuestaBD);

            } else {
                respuestaExistente.setRespuestaOtro(respuestaForm.getRespuestaOtro());
                respuestaExistente.setFechaRespuesta(new Date());
                encuestaPostulanteDAO.update(respuestaExistente);
            }

            for (EncuestaPostulante respuestaBD : respuestasBD) {
                OpcionPregunta opcion = respuestaBD.getOpcion();
                if (opcion.getId().longValue() != opcionForm.getId()) {
                    deshabilitarPreguntasRefenciadas(opcion, preguntasBD, mapRespuestasTodas);
                    encuestaPostulanteDAO.delete(respuestaBD);
                }
            }
            return;
        }

        EncuestaPostulante respuestaExistente = mapRespuestaOpcion.get(opcionForm.getId());
        if (respuestaForm.getValido() != null) {
            if (respuestaExistente == null) {
                respuestaForm.setFechaCreacion(new Date());
                respuestaForm.setFechaRespuesta(new Date());
                encuestaPostulanteDAO.save(respuestaForm);
            }
        } else {
            if (respuestaExistente != null) {
                encuestaPostulanteDAO.delete(respuestaExistente);
            }
        }
    }

    private void deshabilitarPreguntasRefenciadas(OpcionPregunta opcion, List<PreguntaExamen> preguntas, Map<Long, List<EncuestaPostulante>> mapRespuestas) {
        List<PreguntaExamen> preguntasReferencia = new ArrayList();
        for (PreguntaExamen pregunta : preguntas) {
            OpcionPregunta opcionRef = pregunta.getOpcionReferencia();
            if (opcionRef != null && opcionRef.getId() == opcion.getId().longValue()) {
                preguntasReferencia.add(pregunta);
            }
        }

        if (preguntasReferencia.isEmpty()) {
            return;
        }

        for (PreguntaExamen pregunta : preguntasReferencia) {
            List<EncuestaPostulante> respuestasPgta = TypesUtil.getListNotNull(mapRespuestas.get(pregunta.getId()));
            for (EncuestaPostulante rptaOpcion : respuestasPgta) {
                deshabilitarPreguntasRefenciadas(rptaOpcion.getOpcion(), preguntas, mapRespuestas);
                encuestaPostulanteDAO.delete(rptaOpcion);
            }
        }

    }

    @Override
    public Postulante findPostulante(Interesado interesado) {
        return postulanteDAO.findActivoByInteresadoSimple(interesado);
    }

}
