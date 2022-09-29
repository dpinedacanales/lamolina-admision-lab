package edu.pe.lamolina.admision.dao.vacantes.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.vacantes.VacanteCarreraDAO;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.EnteAcademicoEstadoEnum;
import pe.edu.lamolina.model.inscripcion.CarreraPostula;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.model.vacantes.VacanteCarrera;

@Repository
public class VacanteCarreraDAOH extends AbstractEasyDAO<VacanteCarrera> implements VacanteCarreraDAO {

    public VacanteCarreraDAOH() {
        super();
        setClazz(VacanteCarrera.class);
    }

    @Override
    public List<VacanteCarrera> allByCiclo(CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(VacanteCarrera.class, "vc")
                .join("cicloPostula cp", "modalidadIngreso mi")
                .leftJoin("carreraPostula cap", "cap.carrera")
                .filter("cp.id", ciclo)
                .orderBy("mi.codigo");

        return this.all(sql);
    }

    @Override
    public List<VacanteCarrera> allByCarreraCiclo(CarreraPostula carrera, CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(VacanteCarrera.class, "vc")
                .join("cicloPostula cp", "modalidadIngreso mi", "carreraPostula cap", "cap.carrera")
                .filter("cp.id", ciclo)
                .filter("cap.id", carrera)
                .filter("ca.estadoAdmision", EnteAcademicoEstadoEnum.ACT)
                .orderBy("mi.codigo");

        return this.all(sql);
    }

    @Override
    public List<VacanteCarrera> allSupernumerariasByCiclo(CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(VacanteCarrera.class, "vc")
                .join("cicloPostula cp", "modalidadIngreso mi")
                .leftJoin("carreraPostula cap", "cap.carrera")
                .filter("cp.id", ciclo)
                .isNull("cap.id")
                .orderBy("mi.codigo");

        return this.all(sql);
    }

    @Override
    public List<VacanteCarrera> allVacanteCarrera(ModalidadIngreso modalidad, CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(VacanteCarrera.class, "vc")
                .join("cicloPostula cp", "modalidadIngreso mi", "carreraPostula cap", "cap.carrera ca")
                .filter("cp.id", ciclo)
                .filter("mi.id", modalidad)
                .filter("ca.estadoAdmision", EnteAcademicoEstadoEnum.ACT)
                .orderBy("ca.nombre");

        return this.all(sql);
    }

}
