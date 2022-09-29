package edu.pe.lamolina.admision.dao.inscripcion.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.inscripcion.InteresadoDAO;
import org.hibernate.Query;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Interesado;

@Repository
public class InteresadoDAOH extends AbstractEasyDAO<Interesado> implements InteresadoDAO {

    public InteresadoDAOH() {
        super();
        setClazz(Interesado.class);
    }

    @Override
    public Interesado findByFacebookAndCiclo(String idFacebook, CicloPostula cicloPostulaActivo) {
        Octavia sqlUtil = Octavia.query()
                .from(Interesado.class)
                .join("cicloPostula cp")
                .left("carreraInteres", "carreraNueva", "tipoDocumento")
                .filter("facebook", idFacebook)
                .filter("cp.id", cicloPostulaActivo);
        return find(sqlUtil);
    }

    @Override
    public Interesado findByEmailAndCiclo(String email, CicloPostula cicloPostulaActivo) {
        Octavia sqlUtil = Octavia.query()
                .from(Interesado.class)
                .join("cicloPostula cp")
                .filter("email", email)
                .filter("cp.id", cicloPostulaActivo);
        return this.find(sqlUtil);
    }

    @Override
    public List<Interesado> allByFacebook(String idFacebook) {
        Octavia sqlUtil = Octavia.query()
                .from(Interesado.class)
                .join("cicloPostula cp", "cp.cicloAcademico ca")
                .left("postulante")
                .filter("facebook", idFacebook)
                .orderBy("ca.year desc", "ca.numeroCiclo desc");
        return this.all(sqlUtil);
    }

    @Override
    public void cleanPlayerCelular(String playerCelular) {

        StringBuilder sql = new StringBuilder();
        sql.append(" update ").append(Interesado.class.getSimpleName()).append(" as inte ");
        sql.append("   set inte.playerCelular = :NULL ");
        sql.append("   where inte.playerCelular = :PLAYER ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameter("NULL", null);
        query.setParameter("PLAYER", playerCelular);
        query.executeUpdate();

    }

    @Override
    public Interesado findByDocumento(String numeroDocIdentidad, CicloPostula ciclo) {
        Octavia sqlUtil = Octavia.query()
                .from(Interesado.class)
                .join("cicloPostula cp", "cp.cicloAcademico ca")
                .left("postulante")
                .filter("numeroDocIdentidad", numeroDocIdentidad)
                .filter("cp.id", ciclo)
                .orderBy("ca.year desc", "ca.numeroCiclo desc");
        return this.find(sqlUtil);
    }

    @Override
    public List<Interesado> allByDocumento(String numeroDocIdentidad) {
        Octavia sqlUtil = Octavia.query()
                .from(Interesado.class)
                .join("cicloPostula cp", "cp.cicloAcademico ca")
                .left("postulante")
                .filter("numeroDocIdentidad", numeroDocIdentidad)
                .orderBy("ca.year desc", "ca.numeroCiclo desc");
        return this.all(sqlUtil);
    }

}
