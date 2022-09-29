package edu.pe.lamolina.admision.dao.examen.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.examen.PreguntaExamenDAO;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.EstadoPreguntaEnum;
import pe.edu.lamolina.model.examen.BloquePreguntas;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.OpcionPregunta;
import pe.edu.lamolina.model.examen.PreguntaExamen;
import pe.edu.lamolina.model.examen.SubTituloExamen;
import pe.edu.lamolina.model.examen.TemaExamenVirtual;

@Repository
public class PreguntaExamenDAOH extends AbstractEasyDAO<PreguntaExamen> implements PreguntaExamenDAO {

    public PreguntaExamenDAOH() {
        super();
        setClazz(PreguntaExamen.class);
    }

    @Override
    public PreguntaExamen find(long id) {
        Octavia sql = Octavia.query()
                .from(PreguntaExamen.class, "p")
                .join("examenVirtual exv")
                .left("bloquePreguntas bev", "bev.subTituloExamen sev", "sev.temaExamen")
                .left("subtitulo sub", "sub.temaExamen")
                .left("tema")
                .filter("p.estado", EstadoEnum.ACT.name())
                .filter("p.id", id);

        return (PreguntaExamen) sql.find(getCurrentSession());

    }

    @Override
    public PreguntaExamen findNext(long id, ExamenVirtual examen) {
        Octavia sql = Octavia.query()
                .from(PreguntaExamen.class, "p")
                .join("examenVirtual exv")
                .left("bloquePreguntas bev", "bev.subTituloExamen sev", "sev.temaExamen")
                .left("subtitulo sub", "sub.temaExamen")
                .left("tema")
                .filter("p.estado", EstadoEnum.ACT.name())
                .filter("exv.id", examen)
                .filter("p.id", ">", id)
                .orderBy("p.id")
                .limit(1);

        return (PreguntaExamen) sql.find(getCurrentSession());

    }

    @Override
    public PreguntaExamen findPrevious(long id, ExamenVirtual examen) {
        Octavia sql = Octavia.query()
                .from(PreguntaExamen.class, "p")
                .join("examenVirtual exv")
                .left("bloquePreguntas bev", "bev.subTituloExamen sev", "sev.temaExamen")
                .left("subtitulo sub", "sub.temaExamen")
                .left("tema")
                .filter("p.estado", EstadoEnum.ACT.name())
                .filter("exv.id", examen)
                .filter("p.id", "<", id)
                .orderBy("p.id DESC")
                .limit(1);

        return (PreguntaExamen) sql.find(getCurrentSession());

    }

    @Override
    public List<PreguntaExamen> allByExamen(ExamenVirtual examen) {
        Octavia sql = Octavia.query()
                .from(PreguntaExamen.class, "p")
                .join("examenVirtual exv")
                .left("bloquePreguntas bev", "bev.subTituloExamen sev", "sev.temaExamen")
                .left("subtitulo sub", "sub.temaExamen")
                .left("tema")
                .filter("p.estado", EstadoEnum.ACT.name())
                .filter("exv.id", examen)
                .orderBy("p.id");

        return sql.all(getCurrentSession());

    }

    @Override
    public List<PreguntaExamen> allByBloqueExamenVirtual(BloquePreguntas bloquePreguntas) {
        Octavia sql = Octavia.query()
                .from(PreguntaExamen.class, "p")
                .filter("bloquePreguntas", bloquePreguntas)
                .filter("estado", EstadoPreguntaEnum.ACT.name());

        return sql.all(getCurrentSession());

    }

    @Override
    public List<PreguntaExamen> allByTemaExamenVirtual(TemaExamenVirtual temaExamenVirtual) {
        Octavia sql = Octavia.query()
                .from(PreguntaExamen.class, "p")
                .filter("tema", temaExamenVirtual)
                .filter("estado", EstadoPreguntaEnum.ACT.name());

        return sql.all(getCurrentSession());

    }

    @Override
    public List<PreguntaExamen> allBySubTituloExamenVirtual(SubTituloExamen subTituloExamen) {
        Octavia sql = Octavia.query()
                .from(PreguntaExamen.class, "p")
                .filter("subtitulo", subTituloExamen)
                .filter("estado", EstadoPreguntaEnum.ACT.name());

        return sql.all(getCurrentSession());

    }

    @Override
    public List<PreguntaExamen> allActivasByEncuesta(ExamenVirtual encuesta) {
        Octavia sql = Octavia.query()
                .from(PreguntaExamen.class, "p")
                .join("examenVirtual exv")
                .leftJoin("opcionReferencia ref", "ref.pregunta")
                .filter("p.estado", EstadoEnum.ACT.name())
                .filter("exv.id", encuesta)
                .orderBy("p.id");

        return sql.all(getCurrentSession());

    }

    @Override
    public List<PreguntaExamen> allByOpcionesRefencia(List<OpcionPregunta> opcionesReferencia) {
        Octavia sql = Octavia.query()
                .from(PreguntaExamen.class, "pre")
                .join("examenVirtual exv", "opcionReferencia opr", "opr.pregunta")
                .in("opr.id", opcionesReferencia);

        return sql.all(getCurrentSession());
    }

    @Override
    public List<PreguntaExamen> allByPreguntas(List<PreguntaExamen> preguntas) {
        Octavia sql = Octavia.query()
                .from(PreguntaExamen.class, "p")
                .left("bloquePreguntas bev", "bev.subTituloExamen sev", "sev.temaExamen")
                .left("subtitulo sub", "sub.temaExamen")
                .left("tema")
                .in("p.id", preguntas);

        return all(sql);
    }

}
