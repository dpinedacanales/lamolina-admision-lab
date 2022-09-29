package edu.pe.lamolina.admision.dao.finanzas;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.model.finanzas.Factura;
import pe.edu.lamolina.model.finanzas.ItemFactura;

public interface ItemFacturaDAO extends Crud<ItemFactura> {

    List<ItemFactura> allByFactura(Factura facturaBD);

}
