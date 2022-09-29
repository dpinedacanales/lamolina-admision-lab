package edu.pe.lamolina.admision.dao.finanzas;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.DeudaEstadoEnum;
import pe.edu.lamolina.model.finanzas.ConceptoPrecio;
import pe.edu.lamolina.model.finanzas.DeudaInteresado;
import pe.edu.lamolina.model.finanzas.ItemDeudaInteresado;
import pe.edu.lamolina.model.finanzas.SolicitudCambioInfo;
import pe.edu.lamolina.model.finanzas.TipoCambioInfo;
import pe.edu.lamolina.model.inscripcion.Interesado;

public interface ItemDeudaInteresadoDAO extends EasyDAO<ItemDeudaInteresado> {

    List<ItemDeudaInteresado> allBySolicitudesCambioInfo(List<SolicitudCambioInfo> solicitudes);

    List<ItemDeudaInteresado> allByDeudasInteresadoEstados(List<DeudaInteresado> deudas, List<DeudaEstadoEnum> estados);

    ItemDeudaInteresado findInscripcionByInteresado(Interesado interesado);

    List<ItemDeudaInteresado> allDescuentosByInteresado(Interesado interesado);

    List<ItemDeudaInteresado> allActivasByDeudas(List<DeudaInteresado> deudas);

    List<ItemDeudaInteresado> allActivasByDeuda(DeudaInteresado deuda);

    List<ItemDeudaInteresado> allByDeudasInteresadoEstados2(List<DeudaInteresado> deudasNoPagadas, List<DeudaEstadoEnum> asList);

    ItemDeudaInteresado findByInteresadoConceptoPrecioEstado(Interesado interesado, ConceptoPrecio precioExt, DeudaEstadoEnum deudaEstadoEnum);

    ItemDeudaInteresado findActivoByInteresadoTipoSolicitudCambio(Interesado interesado, TipoCambioInfo tipo);

}
