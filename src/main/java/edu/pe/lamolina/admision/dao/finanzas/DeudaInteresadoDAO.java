package edu.pe.lamolina.admision.dao.finanzas;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.DeudaEstadoEnum;
import pe.edu.lamolina.model.finanzas.CuentaBancaria;
import pe.edu.lamolina.model.finanzas.DeudaInteresado;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.Postulante;

public interface DeudaInteresadoDAO extends EasyDAO<DeudaInteresado> {

    List<DeudaInteresado> allByInteresado(Interesado interesado);

    List<DeudaInteresado> allByInteresadoEstado(Interesado interesado, DeudaEstadoEnum estado);

    List<DeudaInteresado> allByInteresadoEstados(Interesado interesado, List<DeudaEstadoEnum> asList);

    DeudaInteresado findDeudaPagada(Postulante postulante);

    List<DeudaInteresado> allDeudaPagada(Postulante postulante);

    DeudaInteresado findByInteresadoCuentaBancaria(Interesado interesado, CuentaBancaria cuentaBancaria);

    List<DeudaInteresado> allByInteresadoCuentasBancarias(Interesado interesado, List<CuentaBancaria> cuentas);

    DeudaInteresado findActivaByInteresadoCtaBanco(Interesado interesado, CuentaBancaria cuentaBancaria);

    Long countlByInteresadoEstados(Interesado interesado, List<DeudaEstadoEnum> estados);

    List<DeudaInteresado> allUtilizablesByInteresado(Interesado interesado, CicloPostula ciclo);

}
