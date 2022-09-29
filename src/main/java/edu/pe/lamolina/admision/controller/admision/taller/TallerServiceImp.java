package edu.pe.lamolina.admision.controller.admision.taller;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.pe.lamolina.admision.dao.academico.CarreraDAO;
import edu.pe.lamolina.admision.dao.general.ArchivoDAO;
import edu.pe.lamolina.admision.dao.general.TipoDocIdentidadDAO;
import edu.pe.lamolina.admision.dao.inscripcion.CarreraNuevaDAO;
import edu.pe.lamolina.admision.dao.inscripcion.InscritoTallerDAO;
import edu.pe.lamolina.admision.dao.inscripcion.InteresadoDAO;
import edu.pe.lamolina.admision.dao.inscripcion.TallerDAO;
import edu.pe.lamolina.admision.zelper.model.DataSessionAdmision;
import java.util.HashMap;
import java.util.Map;
import org.springframework.util.CollectionUtils;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.EstadoInscritoEnum;
import pe.edu.lamolina.model.enums.InstanciaEnum;
import pe.edu.lamolina.model.general.Archivo;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.inscripcion.CarreraNueva;
import pe.edu.lamolina.model.inscripcion.InscritoTaller;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.Taller;

@Service
@Transactional(readOnly = true)
public class TallerServiceImp implements TallerService {

    @Autowired
    TallerDAO tallerDAO;
    @Autowired
    InscritoTallerDAO inscritoTallerDAO;
    @Autowired
    InteresadoDAO interesadoDAO;
    @Autowired
    TipoDocIdentidadDAO tipoDocIdentidadDAO;
    @Autowired
    CarreraDAO carreraDAO;
    @Autowired
    CarreraNuevaDAO carreraNuevaDAO;
    @Autowired
    ArchivoDAO archivoDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Taller findTaller(Long taller) {
        return tallerDAO.find(taller);
    }

    @Override
    @Transactional
    public void saveInscritoTaller(Interesado interesado, Long taller, DataSessionAdmision ds, JsonResponse json) {

        Interesado interesadoDB = interesadoDAO.find(ds.getInteresado().getId());
        Taller tallerDB = tallerDAO.find(taller);

        inscribirseTaller(interesadoDB, tallerDB, json);

        if (interesado.getCarreraNueva() != null && interesado.getCarreraInteres() != null && interesado.getTipoDocumento() != null) {
            if (interesado.getCarreraNueva().getId() < 0) {
                interesadoDB.setOtraCarrera(interesado.getOtraCarrera());
            }
            interesadoDB.setCarreraInteres(interesado.getCarreraInteres());
            interesadoDB.setCarreraNueva(interesado.getCarreraNueva());
            interesadoDB.setTipoDocumento(interesado.getTipoDocumento());
            interesadoDB.setNumeroDocIdentidad(interesado.getNumeroDocIdentidad());
            interesadoDAO.update(interesadoDB);
        }

    }

    @Override
    @Transactional
    public void inscribirseTaller(Interesado interesadoDB, Taller tallerDB, JsonResponse json) {

        InscritoTaller findInsTaller = inscritoTallerDAO.findByInteresadoTaller(interesadoDB, tallerDB.getId());

        if (tallerDB == null || interesadoDB == null) {
            return;
        }

        if (findInsTaller != null) {
            json.setMessage("Ya estas registrado");
            return;
        }

        InscritoTaller insTaller = new InscritoTaller();

        insTaller.setEstado(EstadoInscritoEnum.INS.name());
        insTaller.setFechaInscripcion(new Date());
        insTaller.setInteresado(interesadoDB);
        insTaller.setTaller(tallerDB);

        inscritoTallerDAO.save(insTaller);

        ObjectNode objJson = new ObjectNode(JsonNodeFactory.instance);
        objJson.put("taller", tallerDB.getTituloEnum().getValue());
        objJson.put("ubicacion", tallerDB.getUbicacion());
        objJson.put("fecha", new DateTime(tallerDB.getFecha()).toString("dd/MM/yyyy"));
        objJson.put("hora", tallerDB.getHora());
        objJson.put("visita", tallerDB.getVisita());
        objJson.put("costo", tallerDB.getVisitaCosto());
        objJson.put("nombre", insTaller.getInteresado().getNombreCompleto());

        json.setMessage("Has sido registrado satisfactoriamente");
        json.setData(objJson);

        tallerDB.setInscritos(tallerDB.getInscritos() + 1);
        tallerDAO.update(tallerDB);
    }

    @Override
    public InscritoTaller findInscritoTallerByInteresadoTaller(Interesado interesado, Long taller) {
        return inscritoTallerDAO.findByInteresadoTaller(interesado, taller);
    }

    @Override
    public Interesado findInteresado(Interesado interesado) {
        return interesadoDAO.find(interesado.getId());
    }

    @Override
    public List<TipoDocIdentidad> allTiposDocIdentidad() {
        return tipoDocIdentidadDAO.allForPersonaNatural();
    }

    @Override
    public List<Carrera> allCarrera() {
        return carreraDAO.all();
    }

    @Override
    public List<CarreraNueva> allCarreraNueva() {
        return carreraNuevaDAO.allActivo();
    }

    @Override
    public List<Taller> allTaller() {
        List<Taller> talleres = tallerDAO.allVisibles();
        talleres = talleres.stream().sorted((s1, s2) -> s1.getEstadoEnum().getPrioridad().compareTo(s2.getEstadoEnum().getPrioridad())).collect(Collectors.toList());
        this.procesarDescripcionAndBanners(talleres);
        return talleres;
    }

    @Override
    public void procesarDescripcionAndBanners(List<Taller> talleres) {
        Map<Long, Archivo> banners = this.allBanner(talleres);

        for (Taller taller : talleres) {
            String descDB = taller.getDescripcion();
            if (descDB.length() > 100) {
                descDB = descDB.substring(0, 100);
                int sp = descDB.lastIndexOf(" ");
                descDB = descDB.substring(0, sp).concat(" ...");
            }
            taller.setDescripcion(descDB);
            taller.setFechaExpiracion(new DateTime(taller.getFecha()).plusHours(1).toDate());

            Archivo banner = banners.get(TypesUtil.getLong(taller.getBanner()));
            if (banner != null) {
                taller.setImageFile(banner.getRuta());
            }
        }
    }

    private Map<Long, Archivo> allBanner(List<Taller> talleres) {

        List<Long> idArchivos = talleres.stream()
                .map(t -> TypesUtil.getLong(t.getBanner()))
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(idArchivos)) {
            return new HashMap();
        }

        return archivoDAO.all(idArchivos).stream()
                .collect(Collectors.toMap(x -> x.getId(), z -> z));
    }

    @Override
    public List<Archivo> allArchivosTaller(List<Taller> talleres) {

        List<Long> ids = talleres.stream()
                .map(t -> t.getId())
                .collect(Collectors.toList());

        return archivoDAO.allByIstancia(InstanciaEnum.TALLER, ids);
    }

    @Override
    public List<Taller> allTopTalleres(int top) {
        List<Taller> talleres = tallerDAO.allTop(top);
        talleres = talleres.stream().sorted((s1, s2) -> s1.getEstadoEnum().getPrioridad().compareTo(s2.getEstadoEnum().getPrioridad())).collect(Collectors.toList());

        return talleres;
    }

    @Override
    public List<Archivo> allArchivosTallerByInstanciaEnum(InstanciaEnum instanciaEnum) {
        return archivoDAO.allByIstancia(instanciaEnum, new ArrayList());
    }

    @Override
    public List<Carrera> allCarreraByEstado(EstadoEnum estadoEnum) {
        return carreraDAO.allActivos();
    }

    @Override
    public List<CarreraNueva> allCarreraNuevaByEstado(EstadoEnum estadoEnum) {
        return carreraNuevaDAO.allActivo();
    }

}
