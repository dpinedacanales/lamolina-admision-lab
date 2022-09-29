package edu.pe.lamolina.admision.dao.general.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.general.CaracteristicaColegioDAO;
import pe.edu.lamolina.model.general.CaracteristicaColegio;

@Repository
public class CaracteristicaColegioDAOH extends AbstractDAO<CaracteristicaColegio> implements CaracteristicaColegioDAO {

    public CaracteristicaColegioDAOH() {
        super();
        setClazz(CaracteristicaColegio.class);
    }
}
