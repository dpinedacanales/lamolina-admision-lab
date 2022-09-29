package edu.pe.lamolina.admision.dao.inscripcion.hibernate;

import java.util.List;
import org.hibernate.LockOptions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import edu.pe.lamolina.admision.dao.inscripcion.PostulanteDAO;
import java.util.Arrays;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.PostulanteEstadoEnum;
import static pe.edu.lamolina.model.enums.PostulanteEstadoEnum.IDOC;
import static pe.edu.lamolina.model.enums.PostulanteEstadoEnum.ING;
import static pe.edu.lamolina.model.enums.PostulanteEstadoEnum.NEXM;
import static pe.edu.lamolina.model.enums.PostulanteEstadoEnum.NING;
import static pe.edu.lamolina.model.enums.PostulanteEstadoEnum.REND;
import pe.edu.lamolina.model.enums.TipoDocIdentidadEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.Postulante;

@Repository
public class PostulanteDAOH extends AbstractEasyDAO<Postulante> implements PostulanteDAO {

    public PostulanteDAOH() {
        super();
        setClazz(Postulante.class);
    }

    @Override
    public Postulante find(long id) {
        Octavia sql = Octavia.query()
                .from(Postulante.class, "po")
                .join("cicloPostula cip", "cip.cicloAcademico ca")
                .leftJoin("persona per", "modalidadIngreso mod", "interesado", "colegioProcedencia col", "col.ubicacion")
                .leftJoin("col.gestion ge", "universidadProcedencia uni", "per.tipoDocumento", "descuentoExamen")
                .filter("po.id", id);
        return find(sql);
    }

    @Override
    public Postulante findActivoByDocumento(TipoDocIdentidadEnum tipoEnum, String numeroDocumento, CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(Postulante.class, "po")
                .join("cicloPostula cip", "cip.cicloAcademico ca", "persona per", "per.tipoDocumento td")
                .leftJoin("modalidadIngreso mod", "interesado", "colegioProcedencia col", "col.ubicacion")
                .leftJoin("col.gestion ge", "universidadProcedencia uni", "descuentoExamen")
                .filter("td.simbolo", tipoEnum)
                .filter("per.numeroDocIdentidad", numeroDocumento)
                .filter("cip.id", ciclo);
        return find(sql);
    }

    @Override
    public Postulante findByInteresado(Interesado interesado) {
        Octavia sql = Octavia.query()
                .from(Postulante.class, "po")
                .join("cicloPostula cip", "cip.cicloAcademico ca", "interesado inte")
                .leftJoin("persona per", "modalidadIngreso mod", "colegioProcedencia col", "col.ubicacion")
                .leftJoin("col.gestion ge", "universidadProcedencia uni", "per.tipoDocumento", "descuentoExamen")
                .filter("inte.id", interesado)
                .filter("cip.id", interesado.getCicloPostula())
                .notIn("po.estado", Arrays.asList(PostulanteEstadoEnum.ANU.name(), PostulanteEstadoEnum.REN.name()));
        return find(sql);
    }

    @Override
    public Postulante findActivoByInteresadoSimple(Interesado interesado) {
        Octavia sql = Octavia.query()
                .from(Postulante.class, "po")
                .join("cicloPostula cip", "cip.cicloAcademico ca", "interesado inte")
                .leftJoin("persona per", "modalidadIngreso mod")
                .filter("inte.id", interesado)
                .filter("cip.id", interesado.getCicloPostula())
                .notIn("po.estado", Arrays.asList(PostulanteEstadoEnum.ANU.name(), PostulanteEstadoEnum.REN.name()));
        return find(sql);
    }

    @Override
    public Postulante findActivoByCodigoCiclo(String codigoPostulante, CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(Postulante.class, "po")
                .join("cicloPostula cip", "cip.cicloAcademico ca", "interesado inte")
                .leftJoin("persona per", "modalidadIngreso mod")
                .filter("po.codigo", codigoPostulante)
                .filter("cip.id", ciclo)
                .notIn("po.estado", Arrays.asList(PostulanteEstadoEnum.ANU.name(), PostulanteEstadoEnum.REN.name()));
        return find(sql);
    }

    @Override
    public List<Postulante> allByPersonaCiclo(Persona persona, CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(Postulante.class, "pos")
                .join("interesado inte", "cicloPostula cip", "persona per", "cip.cicloAcademico ca")
                .leftJoin("modalidadIngreso")
                .filter("per.id", persona)
                .filter("cip.id", ciclo)
                .notIn("pos.estado", Arrays.asList(PostulanteEstadoEnum.ANU.name(), PostulanteEstadoEnum.REN.name()));
        return sql.all(getCurrentSession());
    }

    @Override
    public Postulante allByPersonaCicloRenuncianteCepre(Persona persona, CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(Postulante.class, "pos")
                .join("interesado inte", "cicloPostula cip", "persona per", "cip.cicloAcademico ca")
                .leftJoin("modalidadIngreso")
                .filter("per.id", persona)
                .filter("cip.id", ciclo)
                .filter("pos.estado", PostulanteEstadoEnum.REN.name());
        
        return find(sql);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.MANDATORY)
    public void findLock(Long id) {
        getCurrentSession().load(Postulante.class, id, LockOptions.UPGRADE);
    }

    @Override
    public List<Postulante> allIngresoAnuladoByPersonaCiclo(Persona persona, List<CicloPostula> ciclos) {
        Octavia sql = Octavia.query()
                .from(Postulante.class, "pos")
                .join("interesado inte", "cicloPostula cip", "persona per", "cip.cicloAcademico ca")
                .filter("per.id", persona)
                .filter("pos.estado", PostulanteEstadoEnum.AING)
                .in("cip.id", ciclos);
        return sql.all(getCurrentSession());
    }

    @Override
    public Postulante findLastByInteresadoCicloEstado(Postulante postulante, CicloPostula ciclo, PostulanteEstadoEnum estado) {
        Octavia sql = Octavia.query(Postulante.class, "po")
                .join("interesado inte")
                .join("cicloPostulante cipo")
                .join("modalidadIngreso")
                .filter("inte.id", postulante.getInteresado())
                .filter("cipo.id", ciclo)
                .filter("po.id", "<>", postulante)
                .filter("po.estado", estado)
                .orderBy("po.id desc")
                .limit(1);

        return find(sql);
    }

    @Override
    public Postulante findLastActivoByPersona(Persona persona) {
        Octavia sql = Octavia.query(Postulante.class, "po")
                .join("interesado inte", "cicloPostula cipo", "cipo.cicloAcademico ca", "persona per")
                .filter("per.id", persona)
                .in("po.estado", Arrays.asList(ING, NING, REND, NEXM, IDOC))
                .orderBy("ca.codigo desc")
                .limit(1);

        return find(sql);
    }

}
