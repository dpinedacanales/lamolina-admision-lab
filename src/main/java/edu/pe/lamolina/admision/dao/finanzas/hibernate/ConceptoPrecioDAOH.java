package edu.pe.lamolina.admision.dao.finanzas.hibernate;

import java.math.BigDecimal;
import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.finanzas.ConceptoPrecioDAO;
import pe.albatross.octavia.Octavia;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.finanzas.ConceptoPago;
import pe.edu.lamolina.model.finanzas.ConceptoPrecio;
import pe.edu.lamolina.model.inscripcion.CicloPostula;

@Repository
public class ConceptoPrecioDAOH extends AbstractDAO<ConceptoPrecio> implements ConceptoPrecioDAO {

    public ConceptoPrecioDAOH() {
        super();
        setClazz(ConceptoPrecio.class);
    }

    @Override
    public ConceptoPrecio find(Long id) {
        Octavia sql = Octavia.query()
                .from(ConceptoPrecio.class, "pre")
                .join("cicloPostula ci", "conceptoPago cp", "cp.cuentaBancaria cb")
                .leftJoin("cp.modalidadIngreso")
                .filter("pre.id", id);
        return (ConceptoPrecio) sql.find(getCurrentSession());
    }

    @Override
    public List<ConceptoPrecio> allByCicloImporte(CicloPostula ciclo, BigDecimal importe) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("pre")
                .parents("cicloPostula ci", "conceptoPago cp", "left _cp.modalidadIngreso", "left _cp.cuentaBancaria")
                .filter("ci.id", ciclo)
                .filter("pre.monto", importe);
        return all(sqlUtil);
    }

    @Override
    public ConceptoPrecio findByConceptoCiclo(ConceptoPago concepto, CicloPostula ciclo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("pre")
                .parents("cicloPostula ci", "conceptoPago cp", "left _cp.conceptoOrigen", "left _cp.modalidadIngreso", "left _cp.cuentaBancaria")
                .filter("ci.id", ciclo)
                .filter("pre.estado", EstadoEnum.ACT.name())
                .filter("cp.id", concepto);
        return find(sqlUtil);
    }

    @Override
    public ConceptoPrecio findByCodigoConceptoPago(String CODIGO_ANU) {

        Octavia sql = Octavia.query(ConceptoPrecio.class)
                .join("conceptoPago cp")
                .filter("cp.codigo", CODIGO_ANU);
        return (ConceptoPrecio) sql.find(getCurrentSession());
    }

    @Override
    public List<ConceptoPrecio> allCambiosByCiclo(CicloPostula ciclo, Integer ai, Integer ae) {
        Octavia sql = Octavia.query(ConceptoPrecio.class, "cp")
                .join("cicloPostula ci")
                .join("tipoCambioInfo tci")
                .join("conceptoPago cpa")
                .filter("cp.monto", ">", BigDecimal.ZERO)
                .filter("ci.id", ciclo)
                .beginBlock()
                .__().beginBlock()
                .__().__().filter("tci.antesExamen", ae)
                .__().__().filter("tci.antesInscripcion", ai)
                .__().endBlock()
                .__().beginBlock()
                .__().__().filter("tci.antesExamen", -1)
                .__().__().filter("tci.antesInscripcion", -1)
                .__().endBlock()
                .__().beginBlock()
                .__().__().filter("tci.antesExamen", ae)
                .__().__().filter("tci.antesInscripcion", -1)
                .__().endBlock()
                .endBlock();
        return sql.all(getCurrentSession());
    }

    @Override
    public List<ConceptoPrecio> allModalidadByCicloPostula(CicloPostula cicloPostula) {
        Octavia sql = Octavia.query(ConceptoPrecio.class, "cpr")
                .join("conceptoPago cpa")
                .join("cicloPostula cp", "cpa.modalidadIngreso mi")
                .filter("cp.id", cicloPostula)
                .filter("cpr.estado", EstadoEnum.ACT);
        return sql.all(getCurrentSession());
    }

    @Override
    public ConceptoPrecio findByCicloCodigoConceptoPago(CicloPostula cicloPostula, String CODE_PRUEBAS) {
        Octavia sql = Octavia.query(ConceptoPrecio.class, "cpre")
                .join("conceptoPago cpag")
                .join("cicloPostula cp")
                .filter("cp.id", cicloPostula)
                .filter("cpag.codigo", CODE_PRUEBAS);
        return (ConceptoPrecio) sql.find(getCurrentSession());
    }

}
