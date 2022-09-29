package edu.pe.lamolina.admision.dao.general.hibernate;

import java.util.List;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import edu.pe.lamolina.admision.dao.general.PersonaDAO;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.model.enums.TipoDocIdentidadEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.inscripcion.Postulante;

@Repository
public class PersonaDAOH extends AbstractDAO<Persona> implements PersonaDAO {

    public PersonaDAOH() {
        super();
        setClazz(Persona.class);
    }

    @Override
    public Persona find(long id) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("per")
                .parents("tipoDocumento td", "left paisNacer", "left nacionalidad")
                .parents("left ubicacionNacer din", "left _din.ubicacionSuperior prn", "left _prn.ubicacionSuperior den")
                .parents("left _din.tipoUbicacion", "left _prn.tipoUbicacion", "left _den.tipoUbicacion")
                .parents("left ubicacionDomicilio did", "left _did.ubicacionSuperior prd", "left _prd.ubicacionSuperior ded")
                .parents("left _did.tipoUbicacion", "left _prd.tipoUbicacion", "left _ded.tipoUbicacion")
                .filter("per.id", id);
        return find(sqlUtil);
    }

    @Override
    public Persona findByDocumento(TipoDocIdentidadEnum tipoDoc, String numeroDocumento) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("per")
                .parents("tipoDocumento td")
                .filter("td.simbolo", tipoDoc.name())
                .filter("per.numeroDocIdentidad", numeroDocumento);
        return find(sqlUtil);
    }

    @Override
    public Persona findByDocumento(TipoDocIdentidad tipoDoc, String numeroDocumento) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("per")
                .parents("tipoDocumento td")
                .filter("td.id", tipoDoc)
                .filter("per.numeroDocIdentidad", numeroDocumento);
        return find(sqlUtil);
    }

    @Override
    public List<Persona> allPersona() {
        StringBuilder sql = new StringBuilder();
        sql.append(" select distinct per ");
        sql.append(" from ").append(Postulante.class.getName()).append(" as pos ");
        sql.append("    inner join pos.persona per ");
        sql.append("    left join fetch per.tipoDocumento td ");
        Query query = getCurrentSession().createQuery(sql.toString());
        return query.list();
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.MANDATORY)
    public void findLock(Long id) {
        getCurrentSession().load(Persona.class, id, LockOptions.UPGRADE);
    }

}
