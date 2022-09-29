package edu.pe.lamolina.admision.dao.finanzas;

import java.util.Date;
import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.model.finanzas.Factura;

public interface FacturaDAO extends Crud<Factura> {

    List<Factura> allByDynatable(DynatableFilter filter);

    List<Factura> allGeneradosByFecha(Date fechaAbono);

    Factura findByNumeroSerie(String serie, Long lastNumFactura);

    List<Factura> allBySerieRangoNumero(Long serie, Long numero, Long numeroFin);

    Factura find(Factura factura);

    List<Factura> allFacturasExportar();
}
