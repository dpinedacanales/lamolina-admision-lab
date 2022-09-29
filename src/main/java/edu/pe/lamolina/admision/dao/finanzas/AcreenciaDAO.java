package edu.pe.lamolina.admision.dao.finanzas;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.DeudaEstadoEnum;
import pe.edu.lamolina.model.enums.NombreTablasEnum;
import pe.edu.lamolina.model.finanzas.Acreencia;
import pe.edu.lamolina.model.finanzas.CuentaBancaria;

public interface AcreenciaDAO extends EasyDAO<Acreencia> {

    List<Acreencia> allDeudasPendientes();

    List<Acreencia> allDeudasAnuladasPendientes();

    void updateFechaEnvioRecauda(Acreencia acreencia);

    void updateFechaEnvioAnulacion(Acreencia acreencia);

    Acreencia findByCuentaAndTablaIns(CuentaBancaria cuentaBancaria, NombreTablasEnum nombreTablasEnum, Long instanciaTabla, DeudaEstadoEnum deudaEstadoEnum);

    List<Acreencia> allByInstanciasEstado(List<Long> instancias, DeudaEstadoEnum deudaEstadoEnum);
}
