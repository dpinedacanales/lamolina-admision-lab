package edu.pe.lamolina.admision.dao.calificacion.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.calificacion.InfoVacanteModalidadDAO;
import pe.edu.lamolina.model.calificacion.InfoVacanteModalidad;

@Repository
public class InfoVacanteModalidadDAOH extends AbstractDAO<InfoVacanteModalidad> implements InfoVacanteModalidadDAO {

    public InfoVacanteModalidadDAOH() {
        super();
        setClazz(InfoVacanteModalidad.class);
    }

}
