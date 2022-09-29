package edu.pe.lamolina.admision.dao.finanzas.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.finanzas.ItemFacturaDAO;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.model.finanzas.Factura;
import pe.edu.lamolina.model.finanzas.ItemFactura;

@Repository
public class ItemFacturaDAOH extends AbstractDAO<ItemFactura> implements ItemFacturaDAO {

    public ItemFacturaDAOH() {
        super();
        setClazz(ItemFactura.class);
    }

    @Override
    public List<ItemFactura> allByFactura(Factura facturaBD) {

        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("if")
                .parents("factura f")
                .filter("f.id", facturaBD);
        return this.all(sqlUtil);

    }

}
