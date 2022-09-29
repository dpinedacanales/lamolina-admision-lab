package edu.pe.lamolina.admision.movil.complemento;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.pe.lamolina.admision.dao.academico.CarreraDAO;
import edu.pe.lamolina.admision.dao.academico.FacultadDAO;
import edu.pe.lamolina.admision.dao.academico.GradoSecundariaDAO;
import edu.pe.lamolina.admision.dao.academico.ModalidadEstudioDAO;
import edu.pe.lamolina.admision.dao.general.ColegioDAO;
import edu.pe.lamolina.admision.dao.general.PaisDAO;
import edu.pe.lamolina.admision.dao.general.TipoDocIdentidadDAO;
import edu.pe.lamolina.admision.dao.general.UbicacionDAO;
import edu.pe.lamolina.admision.dao.general.UniversidadDAO;
import edu.pe.lamolina.admision.dao.inscripcion.CarreraNuevaDAO;
import edu.pe.lamolina.admision.dao.inscripcion.CarreraPostulaDAO;
import edu.pe.lamolina.admision.dao.inscripcion.CicloPostulaDAO;
import edu.pe.lamolina.admision.dao.inscripcion.EventoCicloDAO;
import edu.pe.lamolina.admision.dao.inscripcion.InscritoTallerDAO;
import edu.pe.lamolina.admision.movil.model.EventoCalendarioMovilDTO;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.EventoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.TallerTitulosEnum;
import pe.edu.lamolina.model.general.Colegio;
import pe.edu.lamolina.model.general.GradoSecundaria;
import pe.edu.lamolina.model.general.Pais;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.general.Ubicacion;
import pe.edu.lamolina.model.general.Universidad;
import pe.edu.lamolina.model.inscripcion.CarreraNueva;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.EventoCiclo;
import pe.edu.lamolina.model.inscripcion.InscritoTaller;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.Taller;

@Service
@Transactional(readOnly = true)
public class ComplementoRestServiceImp implements ComplementoRestService {

    private static final Logger logger = LoggerFactory.getLogger(ComplementoRestServiceImp.class);

    @Autowired
    FacultadDAO facultadDAO;

    @Autowired
    CicloPostulaDAO cicloPostulaDAO;

    @Autowired
    EventoCicloDAO eventoCicloDAO;

    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;

    @Autowired
    TipoDocIdentidadDAO tipoDocIdentidadDAO;

    @Autowired
    CarreraPostulaDAO carreraPostulaDAO;

    @Autowired
    CarreraNuevaDAO carreraNuevaDAO;

    @Autowired
    CarreraDAO carreraDAO;

    @Autowired
    PaisDAO paisDAO;

    @Autowired
    UbicacionDAO ubicacionDAO;

    @Autowired
    ColegioDAO colegioDAO;

    @Autowired
    UniversidadDAO universidadDAO;

    @Autowired
    InscritoTallerDAO inscritoTallerDAO;

    @Autowired
    GradoSecundariaDAO gradoSecundariaDAO;

    @Override
    public List<Facultad> allFacultades() {
        return facultadDAO.all();
    }

    @Override
    public CicloPostula findCicloPostulaActivo() {
        return cicloPostulaDAO.findActivo(modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE));
    }

    @Override
    public List<EventoCalendarioMovilDTO> allCalendarioItem(Interesado interesado) {

        List<EventoCalendarioMovilDTO> calendarioItems = new ArrayList();
        this.allCalendarioEventosCiclo(calendarioItems);
        this.allCalendarioTalleres(interesado, calendarioItems);

        return calendarioItems;
    }

    private void allCalendarioEventosCiclo(List<EventoCalendarioMovilDTO> calendarioItems) {
        ModalidadEstudio me = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        CicloPostula ciclo = cicloPostulaDAO.findActivo(me);
        List<EventoCiclo> eventos = eventoCicloDAO.allVisibleMovil(ciclo);
        for (EventoCiclo evento : eventos) {

            EventoCalendarioMovilDTO item = new EventoCalendarioMovilDTO();
            item.setTitulo(evento.getEvento().getDescripcion());
            item.setFecha(evento.getFechaInicio());
            item.setTipo(EventoCalendarioMovilDTO.TipoEnum.EVENTO);
            item.setImportante(true);
            item.setCampusWebsite("Website de Admisión y App Móvil");

            if (evento.getEventoEnum() == EventoEnum.EXAM) {
                item.setExamenAdmision(true);
                item.setCampusWebsite("Av. La Molina S/N - Campus Universitario");
            }

            calendarioItems.add(item);

        }
    }

    private void allCalendarioTalleres(Interesado interesado, List<EventoCalendarioMovilDTO> calendarioItems) {

        List<InscritoTaller> talleres = inscritoTallerDAO.allByInteresado(interesado);
        for (InscritoTaller inscrito : talleres) {

            Taller taller = inscrito.getTaller();

            EventoCalendarioMovilDTO item = new EventoCalendarioMovilDTO();
            item.setCampusWebsite("Campus Universitario");
            item.setUbicacion(taller.getUbicacion());

            item.setTitulo("Un dia en la Agraria");
            item.setFecha(taller.getFecha());
            if (taller.getTituloEnum() == TallerTitulosEnum.TACA) {
                item.setTitulo("Taller de Carreras");
            }

            if (taller.getCarrera() != null) {
                item.setCarrera(taller.getCarrera().getNombre());
            }

            item.setTipo(EventoCalendarioMovilDTO.TipoEnum.TALLER);

            calendarioItems.add(item);
        }

    }

    @Override
    public List<TipoDocIdentidad> allTipoDocIdentidad() {
        return tipoDocIdentidadDAO.allForPersonaNatural();
    }

    @Override
    public List<Pais> allPaises() {
        return paisDAO.all();
    }

    @Override
    public List<Ubicacion> allDistritos() {
        return ubicacionDAO.allDistritos();
    }

    @Override
    public List<Ubicacion> allUbicacion(String tipo, Long parent) {
        return ubicacionDAO.allByTipoParent(tipo, parent);
    }

    @Override
    public List<Colegio> allColegiosByUbicacion(Ubicacion ubicacion) {
        return colegioDAO.allSecundariaByUbicacion(ubicacion);
    }

    @Override
    public List<Universidad> allUniversidadesPeru() {
        return universidadDAO.all();
    }

    @Override
    public ObjectNode eventos() {
        ModalidadEstudio me = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        CicloPostula ciclo = cicloPostulaDAO.findActivo(me);
        Date today = new Date();
        EventoCiclo verResultados = eventoCicloDAO.findByCicloEvento(ciclo, EventoEnum.VER);
        EventoCiclo verAulas = eventoCicloDAO.findByCicloEvento(ciclo, EventoEnum.AULA);

        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

        node.put("verResultados", verResultados != null && today.after(verResultados.getFechaInicio()));
        node.put("verAulas", verResultados != null && today.after(verAulas.getFechaInicio()));

        return node;
    }

    @Override
    public List<Colegio> allColegiosCOAR() {
        return colegioDAO.allCoarSecundaria();
    }

    @Override
    public List<GradoSecundaria> allGradoSecundaria() {
        return gradoSecundariaDAO.all();
    }

    @Override
    public List<CarreraNueva> allCarreraNueva() {
        return carreraNuevaDAO.allActivo();
    }

    @Override
    public List<Carrera> allCarreraActiva() {
        return carreraDAO.allActivos();
    }

}
