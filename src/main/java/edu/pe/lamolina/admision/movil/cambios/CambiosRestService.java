package edu.pe.lamolina.admision.movil.cambios;

import java.util.List;
import pe.edu.lamolina.model.finanzas.ConceptoPrecio;
import pe.edu.lamolina.model.finanzas.SolicitudCambioInfo;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.model.inscripcion.ModalidadIngresoCiclo;
import pe.edu.lamolina.model.inscripcion.Postulante;

public interface CambiosRestService {
     
    List<SolicitudCambioInfo> allSolicitudesByPostulante(Postulante postulante);

    List<ConceptoPrecio> allConceptoPrecioCambiosByCiclo(Postulante postulante);

    Postulante findPostulante(Long idPostulante);

    List<ModalidadIngreso> allModalidadesByCicloActual();

    List<ModalidadIngresoCiclo> allModalidadIngresoCicloDisponible(List<ModalidadIngreso> modalidades);

    void saveCambioDatosPersonales(Postulante postulante);

    void saveCambioSolicitud(Long postulante, List<ConceptoPrecio> conceptos);

    void saveCambioDocumento(Long idPostulante, Long idTipoDoc, String numeroDocumento);

     void anularCambios(Long idPostulante);
    
}
