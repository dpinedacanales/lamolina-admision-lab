package edu.pe.lamolina.admision.movil.complemento;

import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.pe.lamolina.admision.movil.model.EventoCalendarioMovilDTO;
import java.util.List;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.general.Colegio;
import pe.edu.lamolina.model.general.GradoSecundaria;
import pe.edu.lamolina.model.general.Pais;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.general.Ubicacion;
import pe.edu.lamolina.model.general.Universidad;
import pe.edu.lamolina.model.inscripcion.CarreraNueva;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Interesado;

public interface ComplementoRestService {

    List<EventoCalendarioMovilDTO> allCalendarioItem(Interesado interesado);

    List<Facultad> allFacultades();

    CicloPostula findCicloPostulaActivo();

    List<TipoDocIdentidad> allTipoDocIdentidad();

    List<Pais> allPaises();

    List<Ubicacion> allDistritos();

    List<Ubicacion> allUbicacion(String tipo, Long parent);

    List<Colegio> allColegiosByUbicacion(Ubicacion ubicacion);

    List<Universidad> allUniversidadesPeru();

    ObjectNode eventos();

    List<Colegio> allColegiosCOAR();

    List<GradoSecundaria> allGradoSecundaria();

    List<CarreraNueva> allCarreraNueva();

    List<Carrera> allCarreraActiva();

}
