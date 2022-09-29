package edu.pe.lamolina.admision.dao.finanzas.hibernate;

import java.util.Date;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.finanzas.CargaAbonosDAO;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.model.enums.TipoArchivoEnum;
import pe.edu.lamolina.model.finanzas.CargaAbonos;
import pe.edu.lamolina.model.finanzas.CuentaBancaria;
import pe.edu.lamolina.model.inscripcion.CicloPostula;

@Repository
public class CargaAbonosDAOH extends AbstractDAO<CargaAbonos> implements CargaAbonosDAO {

    public CargaAbonosDAOH() {
        super();
        setClazz(CargaAbonos.class);
    }

    @Override
    public CargaAbonos findHistoricoByCtaBancoFecha(CuentaBancaria ctaBanco, CicloPostula ciclo, Date fecha) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("ca")
                .parents("cuentaBancaria cta", "cicloPostula ci")
                .filter("cta.id", ctaBanco)
                .filter("ci.id", ciclo)
                .filter("ca.fechaCarga", fecha)
                .filter("ca.tipoArchivo", TipoArchivoEnum.HI.name());
        return find(sqlUtil);
    }

    @Override
    public Long countByFecha(CuentaBancaria ctaBanco, CicloPostula ciclo, Date fecha) {
        SqlUtil sqlUtil = SqlUtil.creaCountSql("ca")
                .parents("cuentaBancaria cta", "cicloPostula ci")
                .filter("cta.id", ctaBanco)
                .filter("ci.id", ciclo)
                .filter("ca.fechaCarga", fecha);
        return count(sqlUtil);
    }

}
