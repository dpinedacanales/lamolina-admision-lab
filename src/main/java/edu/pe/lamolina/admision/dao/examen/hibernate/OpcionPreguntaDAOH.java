package edu.pe.lamolina.admision.dao.examen.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.examen.OpcionPreguntaDAO;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.OpcionPregunta;
import pe.edu.lamolina.model.examen.PreguntaExamen;

@Repository
public class OpcionPreguntaDAOH extends AbstractEasyDAO<OpcionPregunta> implements OpcionPreguntaDAO {

    public OpcionPreguntaDAOH() {
        super();
        setClazz(OpcionPregunta.class);
    }

    @Override
    public List<OpcionPregunta> allByPreguntas(List<PreguntaExamen> preguntas) {
        Octavia sql = Octavia.query()
                .from(OpcionPregunta.class, "op")
                .join("pregunta p")
                .in("p.id", preguntas)
                .orderBy("p.numero", "op.numero");

        return  all(sql);
    }

    @Override
    public List<OpcionPregunta> allByPregunta(PreguntaExamen pregunta) {
        Octavia sql = Octavia.query()
                .from(OpcionPregunta.class)
                .join("pregunta p")
                .filter("p.id", pregunta);

        return sql.all(getCurrentSession());
    }

    @Override
    public List<OpcionPregunta> allOrdenadasByEncuesta(ExamenVirtual encuesta) {
        Octavia sql = Octavia.query()
                .from(OpcionPregunta.class, "op")
                .join("pregunta p", "p.examenVirtual exv")
                .filter("exv.id", encuesta)
                .orderBy("p.numero");

        return sql.all(getCurrentSession());
    }

    @Override
    public List<OpcionPregunta> allByOpciones(List<OpcionPregunta> opciones) {
        Octavia sql = Octavia.query()
                .from(OpcionPregunta.class, "op")
                .join("pregunta p")
                .in("op.id", opciones);

        return all(sql);
    }

}
