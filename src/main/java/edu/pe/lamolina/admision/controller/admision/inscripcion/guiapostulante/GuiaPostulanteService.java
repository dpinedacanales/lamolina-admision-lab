package edu.pe.lamolina.admision.controller.admision.inscripcion.guiapostulante;

import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.ContenidoCartaEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.finanzas.ConceptoPrecio;
import pe.edu.lamolina.model.finanzas.DeudaInteresado;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.Postulante;

public interface GuiaPostulanteService {

    Postulante findPostulante(Postulante postulanteSession);

    List<TipoDocIdentidad> allTiposDocIdentidad();

    ModalidadEstudio findModalidadEstudio(ModalidadEstudioEnum modalidadEstudioEnum);

    CicloPostula findCicloPostulaActivo(ModalidadEstudio modalidad);

    Postulante savePostulante(Postulante postulanteForm, Interesado interesadoForm, HttpSession session);

    Interesado findInteresado(Interesado interesadoSession);

    Postulante findPostulanteByInteresado(Interesado interesado);

    ConceptoPrecio findConceptoGuiaPostulanteByCiclo(String tipoProspecto, CicloPostula ciclo);

    Boolean validandoExamenVirtual(Interesado interesado, List<DeudaInteresado> deudas);

    Boolean pagoGuiaPostulante(Interesado interesado, List<DeudaInteresado> deudas);

    List<DeudaInteresado> allByInteresado(Interesado interesado);

    Boolean validandoAdquirirExamen(Interesado interesado, List<DeudaInteresado> deudas);

    Boolean validandoDescargaBoleta(Interesado interesado, List<DeudaInteresado> deudas);

    void saveAdquirirExamen(Postulante postulanteForm, CicloPostula ciclo);

    ContenidoCarta findContenidoCartaByCodigo(ContenidoCartaEnum contenidoCartaEnum);

    Date findUltimaFechaInscripciones(CicloPostula ciclo);

    Date findFechaExamen(CicloPostula ciclo);

}
