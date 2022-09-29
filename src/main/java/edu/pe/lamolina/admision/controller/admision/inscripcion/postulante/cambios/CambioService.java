package edu.pe.lamolina.admision.controller.admision.inscripcion.postulante.cambios;

import java.util.List;
import pe.edu.lamolina.model.enums.SolicitudCambioInfoEstadoEnum;
import pe.edu.lamolina.model.finanzas.ConceptoPrecio;
import pe.edu.lamolina.model.finanzas.DeudaInteresado;
import pe.edu.lamolina.model.finanzas.SolicitudCambioInfo;
import pe.edu.lamolina.model.general.Colegio;
import pe.edu.lamolina.model.general.Pais;
import pe.edu.lamolina.model.general.Universidad;
import pe.edu.lamolina.model.inscripcion.CarreraPostula;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.model.inscripcion.ModalidadIngresoCiclo;
import pe.edu.lamolina.model.inscripcion.Postulante;

public interface CambioService {

    Postulante findPostulante(Postulante postulante);

    List<ModalidadIngreso> allModalidadesByCiclo(CicloPostula ciclo);

    Postulante findPostulanteActivoByInteresado(Interesado interesado);

    List<ConceptoPrecio> allConceptoPrecioCambiosByCiclo(Postulante postulante, CicloPostula cicloPostula);

    DeudaInteresado findDeudaPagada(Postulante postulante, CicloPostula ciclo);

    List<ModalidadIngresoCiclo> allModalidadesCicloByCicloModalidades(CicloPostula cicloPostula, List<ModalidadIngreso> modalidades);

    ConceptoPrecio findPrecioByDataPostulante(
            Postulante postulante,
            ModalidadIngreso modalidadPostula,
            CicloPostula ciclo,
            Colegio colegio, String colegioExtranjero,
            Universidad universidad, String universidadExtranjera);

    void saveCambioSolicitud(Postulante postulante, List<ConceptoPrecio> conceptos);

    List<SolicitudCambioInfo> allSolicitudesByPostulante(Postulante postulante);

    void anularSolicitud(Postulante postulante);

    List<SolicitudCambioInfo> allSolicitudesByPostulanteEstado(Postulante postulante, SolicitudCambioInfoEstadoEnum solicitudCambioInfoEstadoEnum);

    List<CarreraPostula> allCarreras(ModalidadIngreso modalidadIngreso, CicloPostula cicloPostula);

    void saveCambios(Postulante postulante);

    List<ModalidadIngreso> allModalidadesCambioForPostulante(Postulante postulante, List<ModalidadIngreso> modalidades);

    List<ModalidadIngreso> allModalidadesCambioForSimulacion(List<ModalidadIngreso> modalidades, CicloPostula ciclo);

    ConceptoPrecio findPrecioByDataPostulantePreview(Postulante postulante, ModalidadIngreso modalidadIngreso, CicloPostula cicloPostula, Colegio colegio, String colegioExtranjero, Universidad universidad, String universidadExtranjera, Pais paisCo, Pais paisUn);

}
