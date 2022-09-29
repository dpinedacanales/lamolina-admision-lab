package edu.pe.lamolina.admision.dao.academico.hibernate;

import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.academico.CarreraDAO;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.enums.EnteAcademicoEstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;

@Repository
public class CarreraDAOH extends AbstractEasyDAO<Carrera> implements CarreraDAO {

    public CarreraDAOH() {
        super();
        setClazz(Carrera.class);
    }

    @Override
    public Carrera findByUpperNombre(String nombre) {
        StringBuilder sql = new StringBuilder();
        sql.append("  from ").append(Carrera.class.getName()).append(" as car ");
        sql.append(" where upper(car.nombre) = :NOMBRE");
        nombre = nombre.toUpperCase();
        Query query = getCurrentSession().createQuery(sql.toString());
        query.setString("NOMBRE", nombre);
        return (Carrera) query.uniqueResult();
    }

    @Override
    public List<Carrera> allActivos() {
        Octavia sql = Octavia.query()
                .from(Carrera.class, "car")
                .join("modalidadEstudio me", "facultad fa")
                .filter("me.codigo", ModalidadEstudioEnum.PRE.name())
                .filter("car.estadoAdmision", EnteAcademicoEstadoEnum.ACT);
        return sql.all(getCurrentSession());
    }

}
