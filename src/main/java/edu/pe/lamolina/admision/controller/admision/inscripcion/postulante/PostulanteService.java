package edu.pe.lamolina.admision.controller.admision.inscripcion.postulante;

import java.util.List;
import javax.servlet.http.HttpSession;
import pe.edu.lamolina.model.enums.ContenidoCartaEnum;
import pe.edu.lamolina.model.enums.DeudaEstadoEnum;
import pe.edu.lamolina.model.enums.SolicitudCambioInfoEstadoEnum;
import pe.edu.lamolina.model.finanzas.ConceptoPrecio;
import pe.edu.lamolina.model.finanzas.DeudaInteresado;
import pe.edu.lamolina.model.finanzas.ItemDeudaInteresado;
import pe.edu.lamolina.model.finanzas.SolicitudCambioInfo;
import pe.edu.lamolina.model.general.Colegio;
import pe.edu.lamolina.model.general.GradoSecundaria;
import pe.edu.lamolina.model.general.Pais;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.general.Universidad;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.MetalesPostulante;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.model.inscripcion.ModalidadIngresoCiclo;
import pe.edu.lamolina.model.inscripcion.Postulante;

public interface PostulanteService {

    Postulante saveDatosPersonales(Postulante postulanteForm, Persona personaSession, Interesado interesadoSession, CicloPostula ciclo);

    void validarDocumentoAndVerificarPersona(Postulante postulanteForm, Persona personaSession, CicloPostula ciclo);

    Postulante findPostulante(Postulante postulante);

    void saveDatosAcademicos(Postulante postulante, CicloPostula ciclo);

    void saveCerrarInscripcion(Postulante postulanteForm, CicloPostula ciclo);

    List<ModalidadIngreso> allModalidadesByCiclo(CicloPostula ciclo);

    String createDescripcionDeuda(List<ItemDeudaInteresado> items);

    List<TipoDocIdentidad> allTiposDocIdentidad();

    void crearPrelamolinaByPersona(Postulante postulante, CicloPostula ciclo);

    Persona getPersona(Persona persona);

    Interesado findInteresado(Interesado interesadoSession);

    Postulante findPostulanteActivoByInteresado(Interesado interesado);

    ConceptoPrecio findConceptoGuiaPostulanteByCiclo(String tipoProspecto, CicloPostula ciclo);

    void completarPostulante(Postulante postulante, Interesado interesado, CicloPostula ciclo);

    List<DeudaInteresado> allDeudaActivaByPostulante(Postulante postulante);
    
    List<DeudaInteresado> allBoletasByPostulante(Postulante postulante, List<DeudaEstadoEnum> estadosDeuda);

    List<GradoSecundaria> allGrado();

    Boolean verificarPagoGuiaPostulante(Postulante postulante);

    void sendEmailBoletaPagoAsync(Postulante postulante, CicloPostula ciclo);

    void sendEmailBoletaPago(Postulante postulante, CicloPostula ciclo);

    Pais getPeru();

    ContenidoCarta findContenidoCartaByCodigoEnum(ContenidoCartaEnum contenidoCartaEnum);

    Boolean esFinalInscripciones(CicloPostula ciclo);

    ModalidadIngresoCiclo findModalidadCiclo(ModalidadIngreso modalidadIngreso, CicloPostula ciclo);

    MetalesPostulante findMetalPostulanteByPostulante(Postulante postulante);

    void saveMetalesPostulante(MetalesPostulante metalesPostulante);

    void revisarDeudasCompletas(Postulante postulante, CicloPostula ciclo);

    void actualizarImportes(Postulante postulanteForm, CicloPostula ciclo);

    List<ConceptoPrecio> allConceptoPrecioCambiosByCiclo(Postulante postulante, CicloPostula cicloPostula);

    DeudaInteresado findDeudaPagada(Postulante postulante);

    List<ModalidadIngresoCiclo> allModalidadIngresoCicloByCicloModalidades(CicloPostula cicloPostula, List<ModalidadIngreso> modalidades);

    ConceptoPrecio findPrecioByDataPostulante(
            Postulante postulante, ModalidadIngreso modalidadPostula, CicloPostula ciclo,
            Colegio colegio, String colegioExtranjero,
            Universidad universidad, String universidadExtranjera);

    List<SolicitudCambioInfo> allSolicitudCambioInfoByPostulante(Postulante postul, List<SolicitudCambioInfoEstadoEnum> asList);

    List<Colegio> allColegioCoar();

    ContenidoCarta findHeaderBoletaWeb(Postulante postulante, ModalidadIngresoCiclo modalidadCiclo);

    ContenidoCarta findFooterBoletaWeb(Postulante postulante, ModalidadIngresoCiclo modalidadCiclo);

    ContenidoCarta findHeaderBoletaPdf(Postulante postulante, ModalidadIngresoCiclo modalidadCiclo);

    ContenidoCarta findFooterBoletaPdf(Postulante postulante, ModalidadIngresoCiclo modalidadCiclo);

    ContenidoCarta getContenidoCartaTerms(Postulante postulante, ModalidadIngresoCiclo modalidadCiclo);

    public ContenidoCarta getContenido(Postulante postulante, ContenidoCartaEnum tipoContenido);

    public void saveTerminos(Postulante postulanteForm, Long idCarta, HttpSession session);

}
