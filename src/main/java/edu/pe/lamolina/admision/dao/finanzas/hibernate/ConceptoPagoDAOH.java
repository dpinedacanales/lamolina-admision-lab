package edu.pe.lamolina.admision.dao.finanzas.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.finanzas.ConceptoPagoDAO;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.finanzas.ConceptoPago;
import pe.edu.lamolina.model.finanzas.ConceptoPrecio;
import pe.edu.lamolina.model.finanzas.TipoCambioInfo;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;

@Repository
public class ConceptoPagoDAOH extends AbstractEasyDAO<ConceptoPago> implements ConceptoPagoDAO {

    public ConceptoPagoDAOH() {
        super();
        setClazz(ConceptoPago.class);
    }

    @Override
    public ConceptoPago find(long id) {
        Octavia sqlUtil = new Octavia()
                .from(ConceptoPago.class, "co")
                .leftJoin("modalidadIngreso md")
                .filter("co.id", id);
        return find(sqlUtil);
    }

    @Override
    public ConceptoPago findByModalidad(ModalidadIngreso modalidadIngreso) {
        Octavia sqlUtil = new Octavia()
                .from(ConceptoPago.class, "co")
                .join("modalidadIngreso md")
                .filter("md.id", modalidadIngreso);
        return find(sqlUtil);
    }

    @Override
    public List<ConceptoPago> allByModalidad(ModalidadIngreso modalidad) {
        Octavia sqlUtil = new Octavia()
                .from(ConceptoPago.class, "co")
                .join("modalidadIngreso md")
                .left("conceptoOrigen ori")
                .filter("md.id", modalidad);

        return all(sqlUtil);
    }

    @Override
    public List<ConceptoPago> allByModalidadCiclo(ModalidadIngreso modalidad, CicloPostula cicloPostula) {
        Octavia sqlUtil = new Octavia()
                .selectDistinct("co")
                .from(ConceptoPrecio.class, "cpre")
                .join("conceptoPago co", "co.modalidadIngreso md", "cicloPostula cp")
                .left("co.conceptoOrigen ori")
                .filter("cp.id", cicloPostula)
                .filter("cpre.estado", EstadoEnum.ACT)
                .filter("md.id", modalidad);

        return all(sqlUtil);
    }

    @Override
    public List<ConceptoPago> allSinOrigenByModalidad(ModalidadIngreso modalidad) {
        Octavia sqlUtil = new Octavia()
                .from(ConceptoPago.class, "co")
                .join("modalidadIngreso md")
                .left("conceptoOrigen ori")
                .isNull("ori.id")
                .filter("md.id", modalidad);

        return all(sqlUtil);
    }

    @Override
    public List<ConceptoPago> allConceptos() {
        Octavia sqlUtil = new Octavia()
                .from(ConceptoPago.class, "co")
                .join("modalidadIngreso md");
        return all(sqlUtil);
    }

    @Override
    public List<ConceptoPago> allSinOrigen() {
        Octavia sqlUtil = new Octavia()
                .from(ConceptoPago.class, "co")
                .left("conceptoOrigen ori")
                .isNull("ori.id");
        return all(sqlUtil);
    }

    @Override
    public ConceptoPago findByCodigo(String codigo) {
        Octavia sqlUtil = new Octavia()
                .from(ConceptoPago.class, "co")
                .left("conceptoOrigen ori", "modalidadIngreso md", "cuentaBancaria")
                .filter("co.codigo", codigo);
        return find(sqlUtil);
    }

    @Override
    public List<ConceptoPago> allByTipoCambio(List<TipoCambioInfo> tipos) {
        Octavia sql = Octavia.query()
                .select("cpa")
                .from(ConceptoPrecio.class, "cpr")
                .join("conceptoPago cpa", "tipoCambioInfo tc")
                .join("cpa.cuentaBancaria")
                .in("tc.id", tipos);
        return all(sql);
    }

}
