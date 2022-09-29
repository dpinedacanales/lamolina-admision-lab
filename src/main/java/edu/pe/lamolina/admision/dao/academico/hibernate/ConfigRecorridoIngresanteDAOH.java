package edu.pe.lamolina.admision.dao.academico.hibernate;

import edu.pe.lamolina.admision.dao.academico.ConfigRecorridoIngresanteDAO;
import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ConfigRecorridoIngresante;

@Repository
public class ConfigRecorridoIngresanteDAOH extends AbstractEasyDAO<ConfigRecorridoIngresante> implements ConfigRecorridoIngresanteDAO {

    public ConfigRecorridoIngresanteDAOH() {
        super();
        setClazz(ConfigRecorridoIngresante.class);
    }

    @Override
    public List<ConfigRecorridoIngresante> allByCicloAcademico(CicloAcademico ciclo) {
        Octavia sql = Octavia.query(ConfigRecorridoIngresante.class, "cri")
                .join("cicloAcademico ci", "tipoActividadIngresante ta")
                .join("ta.oficinaRecorrido orec")
                .join("orec.oficina ofi")
                .filter("ci.id", ciclo)
                .orderBy("cri.numero");
        return all(sql);
    }

    @Override
    public List<ConfigRecorridoIngresante> allByCicloAcademico(CicloAcademico ciclo, String orderBy) {
        Octavia sql = Octavia.query(ConfigRecorridoIngresante.class, "cri")
                .join("cicloAcademico ci", "tipoActividadIngresante ta")
                .join("ta.oficinaRecorrido orec")
                .join("orec.oficina ofi")
                .filter("ci.id", ciclo)
                .orderBy(orderBy);
        return all(sql);
    }

}
