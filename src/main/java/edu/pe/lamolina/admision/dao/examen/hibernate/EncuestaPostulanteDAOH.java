package edu.pe.lamolina.admision.dao.examen.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.examen.EncuestaPostulanteDAO;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.PreguntaExamen;
import pe.edu.lamolina.model.inscripcion.EncuestaPostulante;
import pe.edu.lamolina.model.inscripcion.Postulante;

@Repository
public class EncuestaPostulanteDAOH extends AbstractEasyDAO<EncuestaPostulante> implements EncuestaPostulanteDAO {

    public EncuestaPostulanteDAOH() {
        super();
        setClazz(EncuestaPostulante.class);
    }

    @Override
    public EncuestaPostulante find(long id) {
        Octavia sql = Octavia.query()
                .from(EncuestaPostulante.class, "ep")
                .join("postulante pos", "pregunta pre", "pre.examenVirtual exa")
                .left("opcion")
                .filter("ep.id", id);

        return find(sql);
    }

    @Override
    public List<EncuestaPostulante> allByPostulante(Postulante postulante, String tipoExamenVirtual) {
        Octavia sql = Octavia.query()
                .from(EncuestaPostulante.class, "ep")
                .join("postulante pos", "pregunta pre", "pre.examenVirtual exa")
                .filter("pos.id", postulante)
                .filter("exa.tipo", tipoExamenVirtual)
                .orderBy("pre.numero");

        return all(sql);
    }

    @Override
    public List<EncuestaPostulante> allByPostulantePreguntas(Postulante postulante, List<PreguntaExamen> preguntas) {
        Octavia sql = Octavia.query()
                .from(EncuestaPostulante.class, "ep")
                .join("postulante pos", "pregunta pre", "pre.examenVirtual exa")
                .filter("pos.id", postulante)
                .in("pre.id", preguntas);

        return all(sql);
    }

    @Override
    public List<EncuestaPostulante> allByPostulanteEncuesta(Postulante postulante, ExamenVirtual encuesta) {
        Octavia sql = Octavia.query()
                .from(EncuestaPostulante.class, "ep")
                .join("postulante pos", "pregunta pre", "pre.examenVirtual exa")
                .filter("pos.id", postulante)
                .filter("exa.id", encuesta)
                .orderBy("pre.numero");
        return all(sql);
    }

    @Override
    public List<EncuestaPostulante> allByPostulantePregunta(Postulante postulante, PreguntaExamen pregunta) {
        Octavia sql = Octavia.query()
                .from(EncuestaPostulante.class, "ep")
                .join("postulante pos", "pregunta pre", "pre.examenVirtual exa")
                .filter("pos.id", postulante)
                .filter("pre.id", pregunta);

        return all(sql);
    }

}
