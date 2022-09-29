package edu.pe.lamolina.admision.dao.vacantes.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.vacantes.CarreraModalidadIngresoDAO;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.model.inscripcion.CarreraPostula;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.model.vacantes.CarreraModalidadIngreso;

@Repository
public class CarreraModalidadIngresoDAOH extends AbstractDAO<CarreraModalidadIngreso> implements CarreraModalidadIngresoDAO {

    public CarreraModalidadIngresoDAOH() {
        super();
        setClazz(CarreraModalidadIngreso.class);
    }

    @Override
    public List<CarreraModalidadIngreso> allByModalidadesCiclo(List<ModalidadIngreso> modalidades, CicloPostula ciclo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("cmi")
                .parents("modalidadIngreso mi", "carreraPostula cap", "_cap.carrera", "cicloPostula cip", "_cip.cicloAcademico")
                .filterIn("mi.id", modalidades)
                .filter("cip.id", ciclo);
        return all(sqlUtil);
    }

    @Override
    public List<CarreraModalidadIngreso> allByCiclo(CicloPostula ciclo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("cmi")
                .parents("modalidadIngreso mi", "carreraPostula cap", "_cap.carrera", "cicloPostula cip", "_cip.cicloAcademico")
                .filter("cip.id", ciclo);
        return all(sqlUtil);
    }

    @Override
    public CarreraModalidadIngreso findByCicloModalidadCarrera(CicloPostula ciclo, ModalidadIngreso modalidad, CarreraPostula carrera) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("cmi")
                .parents("modalidadIngreso mi", "carreraPostula cap", "_cap.carrera", "cicloPostula cip", "_cip.cicloAcademico")
                .filter("mi.id", modalidad)
                .filter("cap.id", carrera)
                .filter("cip.id", ciclo);
        return find(sqlUtil);
    }

    @Override
    public List<CarreraModalidadIngreso> allByModalidadCiclo(ModalidadIngreso modalidad, CicloPostula ciclo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("cmi")
                .parents("modalidadIngreso mi", "carreraPostula cap", "_cap.carrera", "cicloPostula cip", "_cip.cicloAcademico")
                .filter("mi.id", modalidad)
                .filter("cip.id", ciclo);
        return all(sqlUtil);
    }

}
