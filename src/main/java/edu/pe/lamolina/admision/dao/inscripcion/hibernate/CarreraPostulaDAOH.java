package edu.pe.lamolina.admision.dao.inscripcion.hibernate;

import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.inscripcion.CarreraPostulaDAO;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.EnteAcademicoEstadoEnum;
import pe.edu.lamolina.model.inscripcion.CarreraPostula;
import pe.edu.lamolina.model.inscripcion.CicloPostula;

@Repository
public class CarreraPostulaDAOH extends AbstractEasyDAO<CarreraPostula> implements CarreraPostulaDAO {

    public CarreraPostulaDAOH() {
        super();
        setClazz(CarreraPostula.class);
    }

    @Override
    public CarreraPostula find(long id) {
        Octavia sql = Octavia.query()
                .from(CarreraPostula.class, "cap")
                .join("cicloPostula cip", "carrera car")
                .filter("cap.id", id);
        return find(sql);
    }

    @Override
    public List<CarreraPostula> allByCiclo(CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(CarreraPostula.class, "cap")
                .join("cicloPostula cip", "carrera car")
                .filter("cip.id", ciclo)
                .filter("car.estadoAdmision", EnteAcademicoEstadoEnum.ACT)
                .orderBy("car.nombre");

        return this.all(sql);
    }

    @Override
    public CarreraPostula findByUpperNombre(String nombre, CicloPostula ciclo) {
        StringBuilder sql = new StringBuilder();
        sql.append("  from ").append(CarreraPostula.class.getName()).append(" as cp ");
        sql.append(" inner join fetch cp.carrera as carr ");
        sql.append(" inner join fetch cp.cicloPostula as ci ");
        sql.append(" where ci.id = :CICLO ");
        sql.append("   and upper(carr.nombre) = :NOMBRE");
        nombre = nombre.toUpperCase();
        Query query = getCurrentSession().createQuery(sql.toString());
        query.setString("NOMBRE", nombre);
        query.setLong("CICLO", ciclo.getId());
        return (CarreraPostula) query.uniqueResult();

    }

    @Override
    public List<CarreraPostula> allCarreraPostula(CicloPostula cicloPostula) {
        Octavia sql = Octavia.query()
                .from(CarreraPostula.class, "cap")
                .join("cicloPostula cip", "carrera car")
                .filter("cip.id", cicloPostula)
                .orderBy("car.nombre");

        return this.all(sql);
    }
}
