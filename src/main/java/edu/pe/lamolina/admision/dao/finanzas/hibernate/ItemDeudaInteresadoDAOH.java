package edu.pe.lamolina.admision.dao.finanzas.hibernate;

import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.finanzas.ItemDeudaInteresadoDAO;
import java.util.List;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.DeudaEstadoEnum;
import pe.edu.lamolina.model.finanzas.ConceptoPrecio;
import pe.edu.lamolina.model.finanzas.DeudaInteresado;
import pe.edu.lamolina.model.finanzas.ItemDeudaInteresado;
import pe.edu.lamolina.model.finanzas.SolicitudCambioInfo;
import pe.edu.lamolina.model.finanzas.TipoCambioInfo;
import pe.edu.lamolina.model.inscripcion.Interesado;

@Repository
public class ItemDeudaInteresadoDAOH extends AbstractEasyDAO<ItemDeudaInteresado> implements ItemDeudaInteresadoDAO {

    public ItemDeudaInteresadoDAOH() {
        super();
        setClazz(ItemDeudaInteresado.class);
    }

    @Override
    public List<ItemDeudaInteresado> allBySolicitudesCambioInfo(List<SolicitudCambioInfo> solicitudes) {
        Octavia sql = Octavia.query(ItemDeudaInteresado.class, "itd")
                .join("solicitudCambioInfo sci", "deudaInteresado")
                .leftJoin("conceptoPrecio cpr")
                .in("sci.id", solicitudes);
        return all(sql);
    }

    @Override
    public List<ItemDeudaInteresado> allByDeudasInteresadoEstados(List<DeudaInteresado> deudas, List<DeudaEstadoEnum> estados) {
        Octavia sql = Octavia.query(ItemDeudaInteresado.class, "itd")
                .join("deudaInteresado di")
                .leftJoin("conceptoPrecio cpr", "cpr.conceptoPago cpa", "cpa.cuentaBancaria cba")
                .leftJoin("solicitudCambioInfo sci")
                .in("di.id", deudas)
                .in("itd.estado", estados)
                .orderBy("cba.id desc");
        return all(sql);
    }

    @Override
    public ItemDeudaInteresado findInscripcionByInteresado(Interesado interesado) {
        Octavia slq = Octavia.query(ItemDeudaInteresado.class, "itd")
                .join("conceptoPrecio cpr", "deudaInteresado di", "cpr.conceptoPago cpa")
                .join("di.interesado inte", "cpa.modalidadIngreso")
                .left("descuentoExamen dex")
                .isNull("dex.id")
                .filter("inte.id", interesado)
                .filter("itd.estado", DeudaEstadoEnum.ACT);
        return find(slq);
    }

    @Override
    public List<ItemDeudaInteresado> allDescuentosByInteresado(Interesado interesado) {
        Octavia slq = Octavia.query(ItemDeudaInteresado.class, "itd")
                .join("deudaInteresado di")
                .join("di.interesado inte")
                .left("conceptoPrecio cpr", "campagnaDescuento", "descuentoExamen")
                .left("cpr.conceptoPago cpa", "cpa.modalidadIngreso")
                .filter("inte.id", interesado)
                .filter("itd.tipo", "RESTA")
                .filter("itd.estado", DeudaEstadoEnum.ACT);
        return all(slq);
    }

    @Override
    public List<ItemDeudaInteresado> allActivasByDeudas(List<DeudaInteresado> deudas) {
        Octavia sql = Octavia.query(ItemDeudaInteresado.class, "itd")
                .join("conceptoPrecio cpr", "deudaInteresado di", "cpr.conceptoPago cpa", "cpa.cuentaBancaria cba")
                .leftJoin("solicitudCambioInfo sci")
                .in("di.id", deudas)
                .filter("itd.estado", DeudaEstadoEnum.ACT);
        return all(sql);
    }

    @Override
    public List<ItemDeudaInteresado> allActivasByDeuda(DeudaInteresado deuda) {
        Octavia sql = Octavia.query(ItemDeudaInteresado.class, "itd")
                .join("conceptoPrecio cpr", "deudaInteresado di", "cpr.conceptoPago cpa", "cpa.cuentaBancaria cba")
                .leftJoin("solicitudCambioInfo sci")
                .filter("di.id", deuda)
                .filter("itd.estado", DeudaEstadoEnum.ACT);
        return all(sql);
    }

    @Override
    public List<ItemDeudaInteresado> allByDeudasInteresadoEstados2(List<DeudaInteresado> deudas, List<DeudaEstadoEnum> estados) {
        Octavia sql = Octavia.query(ItemDeudaInteresado.class, "itd")
                .join("deudaInteresado di")
                .leftJoin("conceptoPrecio cpr", "cpr.conceptoPago cpa", "cpa.cuentaBancaria cba")
                .leftJoin("solicitudCambioInfo sci")
                .in("di.id", deudas)
                .in("itd.estado", estados)
                .orderBy("itd.tipo desc");
        return all(sql);
    }

    @Override
    public ItemDeudaInteresado findByInteresadoConceptoPrecioEstado(Interesado interesado, ConceptoPrecio precioExt, DeudaEstadoEnum deudaEstadoEnum) {
        Octavia sql = Octavia.query(ItemDeudaInteresado.class, "itd")
                .join("deudaInteresado di", "di.interesado inte")
                .join("conceptoPrecio pre")
                .filter("inte.id", interesado)
                .filter("pre.id", precioExt)
                .filter("di.estado", deudaEstadoEnum);
        return find(sql);
    }

    @Override
    public ItemDeudaInteresado findActivoByInteresadoTipoSolicitudCambio(Interesado interesado, TipoCambioInfo tipo) {
        Octavia sql = Octavia.query(ItemDeudaInteresado.class, "itd")
                .join("conceptoPrecio cpr", "deudaInteresado di", "di.interesado inte", "cpr.conceptoPago cpa", "cpa.cuentaBancaria cba", "cpr.tipoCambioInfo tci")
                .isNull("cpa.modalidadIngreso")
                .filter("inte.id", interesado)
                .filter("itd.estado", DeudaEstadoEnum.ACT)
                .filter("di.estado", DeudaEstadoEnum.ACT)
                .filter("tci.id", tipo);

        return find(sql);
    }

}
