package edu.pe.lamolina.admision.dao.inscripcion.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.inscripcion.ModalidadIngresoCicloDAO;
import pe.albatross.octavia.Octavia;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.model.inscripcion.ModalidadIngresoCiclo;

@Repository
public class ModalidadIngresoCicloDAOH extends AbstractDAO<ModalidadIngresoCiclo> implements ModalidadIngresoCicloDAO {

    public ModalidadIngresoCicloDAOH() {
        super();
        setClazz(ModalidadIngresoCiclo.class);
    }

    @Override
    public ModalidadIngresoCiclo find(long id) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("mic")
                .parents("cicloPostula cp", "modalidadIngreso mi", "left _mi.modalidadSuperior")
                .filter("mic.id", id);
        return find(sqlUtil);
    }

    @Override
    public List<ModalidadIngresoCiclo> allByCiclo(CicloPostula ciclo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("mic")
                .parents("cicloPostula cp", "modalidadIngreso mi", "_mi.modalidadEstudio", "left _mi.modalidadSuperior")
                .filter("cp.id", ciclo)
                .orderBy("mi.orden");
        return all(sqlUtil);
    }

    @Override
    public List<ModalidadIngresoCiclo> allSuperioresByCiclo(CicloPostula ciclo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("mic")
                .parents("cicloPostula cp", "modalidadIngreso mi", "_mi.modalidadEstudio", "left _mi.modalidadSuperior ms")
                .filter("cp.id", ciclo)
                .filterIsNull("ms.id")
                .orderBy("mi.codigo");
        return all(sqlUtil);
    }

    @Override
    public List<ModalidadIngresoCiclo> allByModalidadPadreCiclo(ModalidadIngreso modalidad, CicloPostula ciclo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("mic")
                .parents("cicloPostula cp", "modalidadIngreso mi", "_mi.modalidadSuperior ms")
                .filter("cp.id", ciclo)
                .filter("ms.id", modalidad);
        return all(sqlUtil);
    }

    @Override
    public ModalidadIngresoCiclo findByModalidadIngresoCiclo(ModalidadIngreso modalidadIngreso, CicloPostula cicloPostula) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("mic")
                .parents("cicloPostula cp", "modalidadIngreso mi", "left _mi.modalidadSuperior ms")
                .filter("cp.id", cicloPostula)
                .filter("mi.id", modalidadIngreso);
        return find(sqlUtil);
    }

    @Override
    public List<ModalidadIngresoCiclo> allByCicloAcademico(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(ModalidadIngresoCiclo.class, "mic")
                .join("cicloPostula cp", "modalidadIngreso mi", "mi.modalidadEstudio", "cp.cicloAcademico ca")
                .leftJoin("mi.modalidadSuperior")
                .filter("ca.id", ciclo)
                .orderBy("mi.orden");

        return sql.all(getCurrentSession());
    }
}
