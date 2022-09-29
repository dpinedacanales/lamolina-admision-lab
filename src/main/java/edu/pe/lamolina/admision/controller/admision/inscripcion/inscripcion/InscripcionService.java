package edu.pe.lamolina.admision.controller.admision.inscripcion.inscripcion;

import edu.pe.lamolina.admision.zelper.model.DataSessionAdmision;
import java.util.List;
import javax.servlet.http.HttpSession;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.enums.ContenidoCartaEnum;
import pe.edu.lamolina.model.finanzas.DeudaInteresado;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.inscripcion.CarreraNueva;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.inscripcion.EventoCiclo;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.Prelamolina;
import pe.edu.lamolina.model.inscripcion.Taller;

public interface InscripcionService {

    List<TipoDocIdentidad> allTiposDocIdentidad();

    void updateInteresado(Interesado interesado);

    Prelamolina buscarComoCepre(Interesado interesado, CicloPostula ciclo, Integer loop);

    void verificarCepre(FormCepre formCepre, HttpSession session);

    void updateCepre(HttpSession session);

    Boolean esPrelamolinaActiva(List<EventoCiclo> eventos, CicloPostula ciclo);

    Boolean esInscripcionActiva(List<EventoCiclo> eventos, CicloPostula ciclo);

    Boolean esExtemporaneaActiva(List<EventoCiclo> eventos, CicloPostula ciclo);

    List<EventoCiclo> allEventosDiaByCiclo(CicloPostula ciclo);

    List<Carrera> allCarreras();

    List<CarreraNueva> allCarreraNuevas();

    Boolean esFinalInscripciones(CicloPostula ciclo);

    List<DeudaInteresado> allDeudasInteresado(Interesado interesado);

    Taller findTaller(Long id);

    void saveInscritoTaller(Interesado interesado);

    public ContenidoCarta findContenidoCartaByCodigoEnum(ContenidoCartaEnum contenidoCartaEnum);

    public CicloPostula findCicloPostula(CicloPostula cicloPostula);

    public Boolean esIngresantePre(DataSessionAdmision ds);

    public CicloPostula getCicloPostulaActivo();


}
