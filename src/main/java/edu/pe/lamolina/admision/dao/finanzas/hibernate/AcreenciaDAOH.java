package edu.pe.lamolina.admision.dao.finanzas.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.finanzas.AcreenciaDAO;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.DeudaEstadoEnum;
import pe.edu.lamolina.model.enums.NombreTablasEnum;
import pe.edu.lamolina.model.finanzas.Acreencia;
import pe.edu.lamolina.model.finanzas.CuentaBancaria;

@Repository
public class AcreenciaDAOH extends AbstractEasyDAO<Acreencia> implements AcreenciaDAO {

    public AcreenciaDAOH() {
        super();
        setClazz(Acreencia.class);
    }

    @Override
    public List<Acreencia> allDeudasPendientes() {
        Octavia sql = Octavia.query()
                .from(Acreencia.class, "acre")
                .join("persona per", "cuentaBancaria cb")
                .filter("acre.estado", DeudaEstadoEnum.DEU)
                .isNull("fechaEnvioRecauda");

        return all(sql);
    }

    @Override
    public List<Acreencia> allDeudasAnuladasPendientes() {
        Octavia sql = Octavia.query()
                .from(Acreencia.class, "acre")
                .join("persona per", "cuentaBancaria cb")
                .filter("acre.estado", DeudaEstadoEnum.ANU)
                .isNull("fechaEnvioAnulacion");

        return all(sql);
    }

    @Override
    public void updateFechaEnvioRecauda(Acreencia acreencia) {
        Octavia octavia = Octavia.update(Acreencia.class);
        octavia.set(acreencia, "fechaEnvioRecauda");
        this.update(octavia);
    }

    @Override
    public void updateFechaEnvioAnulacion(Acreencia acreencia) {
        Octavia octavia = Octavia.update(Acreencia.class);
        octavia.set(acreencia, "fechaEnvioAnulacion");
        this.update(octavia);
    }

    @Override
    public Acreencia findByCuentaAndTablaIns(CuentaBancaria cuentaBancaria, NombreTablasEnum tabla, Long instanciaTabla, DeudaEstadoEnum deudaEstado) {
        Octavia sql = Octavia.query()
                .from(Acreencia.class, "acre")
                .join("persona per", "cuentaBancaria cb")
                .filter("acre.instanciaTabla", instanciaTabla)
                .filter("acre.estado", deudaEstado)
                .filter("acre.tabla", tabla)
                .filter("cb.id", cuentaBancaria);
        return this.find(sql);
    }

    @Override
    public List<Acreencia> allByInstanciasEstado(List<Long> instancias, DeudaEstadoEnum deudaEstado) {
        Octavia sql = Octavia.query(Acreencia.class)
                .filter("estado", deudaEstado)
                .in("instanciaTabla", instancias);
        return all(sql);
    }

}
