package edu.pe.lamolina.admision.dao.inscripcion.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.inscripcion.CarreraNuevaDAO;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.model.enums.EstadoCarreraNuevaEnum;
import pe.edu.lamolina.model.inscripcion.CarreraNueva;

@Repository
public class CarreraNuevaDAOH extends AbstractDAO<CarreraNueva> implements CarreraNuevaDAO {

    public CarreraNuevaDAOH() {
        super();
        setClazz(CarreraNueva.class);

    }

    @Override
    public List<CarreraNueva> allActivo() {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("cap")
                .filter("cap.estado", EstadoCarreraNuevaEnum.ACT.name())
                .orderBy("cap.orden");
        return all(sqlUtil);
    }

}
