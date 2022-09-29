package edu.pe.lamolina.admision.dao.academico.hibernate;

import java.util.Arrays;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.academico.CicloAcademicoDAO;
import java.util.List;
import pe.albatross.octavia.Octavia;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.CicloAcademicoEstadoEnum;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.PRE;

@Repository
public class CicloAcademicoDAOH extends AbstractDAO<CicloAcademico> implements CicloAcademicoDAO {

    public CicloAcademicoDAOH() {
        super();
        setClazz(CicloAcademico.class);
    }

    @Override
    public CicloAcademico findActivo(ModalidadEstudio modalidad) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("ca")
                .parents("modalidadEstudio me")
                .filter("me.id", modalidad)
                .filter("ca.estado", CicloAcademicoEstadoEnum.ACT.name());
        return this.find(sqlUtil);
    }

    @Override
    public CicloAcademico findByYearNumero(CicloAcademico cicloAcad) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("ca")
                .parents("modalidadEstudio me")
                .filter("me.id", cicloAcad.getModalidadEstudio())
                .filter("ca.year", cicloAcad.getYear())
                .filter("ca.numeroCiclo", cicloAcad.getNumeroCiclo());
        return this.find(sqlUtil);
    }

    @Override
    public CicloAcademico findUltimo(ModalidadEstudio modalidad) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("ca")
                .parents("modalidadEstudio me")
                .filter("me.id", modalidad)
                .filterIn("ca.numeroCiclo", Arrays.asList(1, 2))
                .orderBy("ca.year desc", "ca.numeroCiclo desc")
                .setPageSize(1);
        return this.find(sqlUtil);
    }

    @Override
    public List<CicloAcademico> allAnterioresByCicloCantidad(CicloAcademico academico, Integer cantidad) {
        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .join("modalidadEstudio me")
                .filter("me.id", academico.getModalidadEstudio())
                .filter("ca.tipo", "REG")
                .filter("ca.codigo", "<", academico.getCodigo())
                .orderBy("ca.year desc", "ca.numeroCiclo desc")
                .limit(cantidad);

        return sql.all(getCurrentSession());
    }

    @Override
    public CicloAcademico findCicloAnteriorRegular(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(CicloAcademico.class, "ca")
                .join("modalidadEstudio me")
                .filter("tipo", "REG")
                .filter("codigo", "<", cicloAcademico.getCodigo())
                .filter("me.codigo", PRE)
                .orderBy("ca.year DESC", "ca.numeroCiclo DESC")
                .limit(1);

        return (CicloAcademico) sql.find(getCurrentSession());
    }
}
