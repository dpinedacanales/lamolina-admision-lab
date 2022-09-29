package edu.pe.lamolina.admision.dao.finanzas.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.finanzas.ItemCargaAbonoDAO;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.finanzas.ItemCargaAbono;
import pe.edu.lamolina.model.inscripcion.Postulante;

@Repository
public class ItemCargaAbonoDAOH extends AbstractEasyDAO<ItemCargaAbono> implements ItemCargaAbonoDAO {

    public ItemCargaAbonoDAOH() {
        super();
        setClazz(ItemCargaAbono.class);
    }

    @Override
    public List<ItemCargaAbono> allActivosByPostulante(Postulante postulante) {
        Octavia sql = Octavia.query()
                .from(ItemCargaAbono.class, "ica")
                .join("postulante po", "cargaAbonos ca", "ca.cuentaBancaria cb", "ca.cicloPostula cp")
                .leftJoin("po.persona", "po.modalidadIngreso", "conceptoPago")
                .filter("po.id", postulante)
                .filter("ica.redundante", 0)
                .filter("ica.extornado", 0);

        return all(sql);
    }

}
