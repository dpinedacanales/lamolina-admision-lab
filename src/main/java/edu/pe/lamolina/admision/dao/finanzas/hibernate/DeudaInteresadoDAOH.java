package edu.pe.lamolina.admision.dao.finanzas.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.finanzas.DeudaInteresadoDAO;
import java.util.Arrays;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.DeudaEstadoEnum;
import pe.edu.lamolina.model.finanzas.CuentaBancaria;
import pe.edu.lamolina.model.finanzas.DeudaInteresado;
import pe.edu.lamolina.model.finanzas.ItemDeudaInteresado;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.Postulante;

@Repository
public class DeudaInteresadoDAOH extends AbstractEasyDAO<DeudaInteresado> implements DeudaInteresadoDAO {

    public DeudaInteresadoDAOH() {
        super();
        setClazz(DeudaInteresado.class);
    }

    @Override
    public List<DeudaInteresado> allByInteresadoEstado(Interesado interesado, DeudaEstadoEnum estado) {
       return this.allByInteresadoEstados(interesado, Arrays.asList(estado));
    }

    @Override
    public List<DeudaInteresado> allByInteresadoEstados(Interesado interesado, List<DeudaEstadoEnum> estados) {
        Octavia sql = Octavia.query()
                .from(DeudaInteresado.class, "di")
                .join("interesado inte")
                .leftJoin("cuentaBancaria cb")
                .leftJoin("postulante po", "conceptoPrecio cpre", "cpre.conceptoPago cpa", "cpre.cicloPostula", "cpa.cuentaBancaria")
                .filter("inte.id", interesado)
                .in("di.estado", estados)
                .orderBy("cpa.orden");

        return all(sql);
    }

    @Override
    public DeudaInteresado findDeudaPagada(Postulante postulante) {
        Octavia subquery = Octavia.query()
                .from(ItemDeudaInteresado.class, "idi")
                .join("deudaInteresado dix")
                .join("conceptoPrecio cprx", "cprx.conceptoPago cpagx", "cpagx.modalidadIngreso mox");

        Octavia sql = Octavia.query(DeudaInteresado.class, "di")
                .join("interesado inte", "postulante pos")
                .leftJoin("conceptoPrecio cpr", "cpr.conceptoPago cpag", "cpag.modalidadIngreso mo")
                .filter("inte.id", postulante.getInteresado())
                .exists(subquery)
                .linkedBy("di.id", "dix.id")
                .filterSpecial("di.abono", "di.monto");

        return find(sql);
    }

    @Override
    public List<DeudaInteresado> allDeudaPagada(Postulante postulante) {

        Octavia sql = Octavia.query(DeudaInteresado.class, "di")
                .join("interesado inte", "cuentaBancaria")
                .leftJoin("conceptoPrecio cpr", "cpr.conceptoPago cpag", "cpag.modalidadIngreso mo", "postulante pos")
                .filter("inte.id", postulante.getInteresado())
                .filter("estado", DeudaEstadoEnum.PAG);

        return all(sql);
    }

    @Override
    public List<DeudaInteresado> allByInteresado(Interesado interesado) {
        Octavia sql = Octavia.query(DeudaInteresado.class, "di")
                .join("di.interesado i")
                .leftJoin("di.conceptoPrecio cpre", "cpre.conceptoPago cpago")
                .filter("i.id", interesado)
                .orderBy("cpago.orden");

        return all(sql);
    }

    public DeudaInteresado findByInteresadoCuentaBancaria(Interesado interesado, CuentaBancaria cuentaBancaria) {
        Octavia sql = Octavia.query(DeudaInteresado.class, "di")
                .join("cuentaBancaria cb")
                .join("interesado i")
                .filter("cb.id", cuentaBancaria)
                .filter("i.id", interesado);
        return find(sql);
    }

    @Override
    public List<DeudaInteresado> allByInteresadoCuentasBancarias(Interesado interesado, List<CuentaBancaria> cuentas) {
        Octavia sql = Octavia.query(DeudaInteresado.class, "di")
                .join("interesado i", "cuentaBancaria cb")
                .filter("i.id", interesado)
                .in("cb.id", cuentas);
        return all(sql);
    }

    @Override
    public DeudaInteresado findActivaByInteresadoCtaBanco(Interesado interesado, CuentaBancaria cuentaBancaria) {
        Octavia sql = Octavia.query(DeudaInteresado.class, "di")
                .join("interesado i", "cuentaBancaria cb")
                .filter("di.abono", 0)
                .filter("di.estado", DeudaEstadoEnum.ACT)
                .filter("i.id", interesado)
                .filter("cb.id", cuentaBancaria);

        return find(sql);
    }

    @Override
    public Long countlByInteresadoEstados(Interesado interesado, List<DeudaEstadoEnum> estados) {
        Octavia sql = Octavia.query()
                .selectCount()
                .from(DeudaInteresado.class, "di")
                .join("interesado inte")
                .filter("inte.id", interesado)
                .in("di.estado", estados);

        return (Long) sql.find(getCurrentSession());

    }

    @Override
    public List<DeudaInteresado> allUtilizablesByInteresado(Interesado interesado, CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(DeudaInteresado.class, "di")
                .join("interesado inte", "inte.cicloPostula cp")
                .leftJoin("postulante po", "conceptoPrecio cpre", "cpre.conceptoPago cpa")
                .filter("cp.id", ciclo)
                .filter("inte.id", interesado)
                .in("di.estado", Arrays.asList(DeudaEstadoEnum.ACT, DeudaEstadoEnum.PAG))
                .orderBy("di.id DESC");

        return all(sql);
    }

}
