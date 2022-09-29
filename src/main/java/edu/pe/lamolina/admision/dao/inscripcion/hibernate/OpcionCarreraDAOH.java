package edu.pe.lamolina.admision.dao.inscripcion.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.inscripcion.OpcionCarreraDAO;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.OpcionCarrera;
import pe.edu.lamolina.model.inscripcion.Postulante;

@Repository
public class OpcionCarreraDAOH extends AbstractDAO<OpcionCarrera> implements OpcionCarreraDAO {

    public OpcionCarreraDAOH() {
        super();
        setClazz(OpcionCarrera.class);
    }

    @Override
    public List<OpcionCarrera> allByPostulante(Postulante postulante) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("op")
                .parents("postulante po", "carreraPostula cap", "_cap.carrera", "_cap.cicloPostula cip", "_cip.cicloAcademico")
                .filter("po.id", postulante)
                .orderBy("op.prioridad");
        return this.all(sqlUtil);
    }

    @Override
    public List<OpcionCarrera> allByPostulantes(List<Postulante> postulantes) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("op")
                .parents("postulante po", "carreraPostula cap", "_cap.carrera", "_cap.cicloPostula cip", "_cip.cicloAcademico")
                .filterIn("po.id", postulantes)
                .orderBy("op.prioridad");
        return this.all(sqlUtil);
    }

    @Override
    public List<OpcionCarrera> allOpcionCarrera(CicloPostula cicloPostula) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("op")
                .parents("postulante po", "carreraPostula cap", "_cap.carrera", "left _po.colegioProcedencia", "left _po.universidadProcedencia", "_cap.cicloPostula cip", "_cip.cicloAcademico")
                .filter("cip.id", cicloPostula)
                .orderBy("op.prioridad");
        return this.all(sqlUtil);
    }

    @Override
    public OpcionCarrera findByPostulante(Postulante postulante) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("op")
                .parents("postulante po", "carreraPostula cap", "_cap.carrera", "_cap.cicloPostula cip", "_cip.cicloAcademico")
                .filter("po.id", postulante);
        return find(sqlUtil);
    }
}
