package edu.pe.lamolina.admision.controller.general.comun;

import edu.pe.lamolina.admision.dao.academico.GradoSecundariaDAO;
import edu.pe.lamolina.admision.dao.finanzas.ConceptoPrecioDAO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.pe.lamolina.admision.dao.general.ColegioDAO;
import edu.pe.lamolina.admision.dao.general.PaisDAO;
import edu.pe.lamolina.admision.dao.general.PersonaDAO;
import edu.pe.lamolina.admision.dao.general.TipoDocIdentidadDAO;
import edu.pe.lamolina.admision.dao.general.UbicacionDAO;
import edu.pe.lamolina.admision.dao.general.UniversidadDAO;
import edu.pe.lamolina.admision.dao.inscripcion.ModalidadIngresoCicloDAO;
import edu.pe.lamolina.admision.dao.inscripcion.ModalidadIngresoDAO;
import edu.pe.lamolina.admision.dao.inscripcion.PostulanteDAO;
import edu.pe.lamolina.admision.zelper.model.DataSessionAdmision;
import java.util.Map;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.finanzas.ConceptoPrecio;
import pe.edu.lamolina.model.general.Colegio;
import pe.edu.lamolina.model.general.GradoSecundaria;
import pe.edu.lamolina.model.general.Pais;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.general.Ubicacion;
import pe.edu.lamolina.model.general.Universidad;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.model.inscripcion.ModalidadIngresoCiclo;
import pe.edu.lamolina.model.inscripcion.Postulante;

@Service
@Transactional(readOnly = true)
public class BuscarServiceImp implements BuscarService {

    @Autowired
    UbicacionDAO ubicacionDAO;
    @Autowired
    PersonaDAO personaDAO;
    @Autowired
    PaisDAO paisDAO;
    @Autowired
    ColegioDAO colegioDAO;
    @Autowired
    ModalidadIngresoDAO modalidadIngresoDAO;
    @Autowired
    PostulanteDAO postulanteDAO;
    @Autowired
    ModalidadIngresoCicloDAO modalidadIngresoCicloDAO;
    @Autowired
    TipoDocIdentidadDAO tipoDocIdentidadDAO;
    @Autowired
    UniversidadDAO universidadDAO;
    @Autowired
    ConceptoPrecioDAO conceptoPrecioDAO;
    @Autowired
    GradoSecundariaDAO gradoSecundariaDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<Ubicacion> allDistritosByName(String nombre) {
        return ubicacionDAO.allDistritos(forLike(nombre));
    }

    @Override
    public List<Pais> allPaisesByName(String nombre) {
        return paisDAO.allByName(forLike(nombre));
    }

    @Override
    public List<Pais> allPaisesByCodigo(String codigo) {
        return paisDAO.allByCodigo(codigo);
    }

    @Override
    public List<Pais> allPaisesByNotCodigo(String nombre, String codigo) {
        return paisDAO.allByNotCodigo(forLike(nombre), codigo);
    }

    @Override
    public List<Colegio> allColegioSecundariaByName(String nombre, Long idUbicacion, Long idModalidadIngreso) {
        ModalidadIngreso modalidad = modalidadIngresoDAO.find(idModalidadIngreso);
        if ("COAR".equals(modalidad.getTipo())) {
            idUbicacion = -1L;
            return colegioDAO.allCoarSecundariaByName(forLike(nombre), idUbicacion);
        }
        return colegioDAO.allSecundariaByName(forLike(nombre), idUbicacion);
    }

    @Override
    public List<Colegio> allColegiosCoar() {
        return colegioDAO.allCoarSecundaria();
    }

    @Override
    public List<ModalidadIngreso> allModalidadIngresoByName(String nombre) {
        return modalidadIngresoDAO.allByName(forLike(nombre));
    }

    @Override
    public ModalidadIngresoCiclo findByModalidadCiclo(ModalidadIngreso modalidad, CicloPostula ciclo) {
        return modalidadIngresoCicloDAO.findByModalidadIngresoCiclo(modalidad, ciclo);
    }

    private String forLike(String nombre) {
        return "%" + nombre.replaceAll(" ", "%") + "%";
    }

    @Override
    public List<Pais> allPaisesByNameModalidad(String nombre, DataSessionAdmision ds, ModalidadIngreso modalidadIngreso) {

        if (modalidadIngreso.getId() == null) {
            Postulante postulante = postulanteDAO.find(ds.getPostulante().getId());
            modalidadIngreso = postulante.getModalidadIngreso();
            if (modalidadIngreso == null) {
                modalidadIngreso = ds.getModalidad();
            }
        }

        CicloPostula cicloPostula = ds.getCicloPostula();

        ModalidadIngresoCiclo modalidadCiclo = this.findByModalidadCiclo(modalidadIngreso, cicloPostula);

        logger.debug("MODALIDAD {}", modalidadIngreso.getId());
        logger.debug("MODALIDAD ES CONVENIO ANDRES BELLO {}", modalidadIngreso.isConvenioAndresBello());

        List<Pais> paises;
        if (modalidadCiclo.getSoloColegioPeruano() == 1) {
            logger.debug("== SOLO COLEGIO PERUANO ==");
            paises = this.allPaisesByCodigo("PE");
        } else if (modalidadCiclo.getSoloColegioExtranjero() == 1) {
            logger.debug("== SOLO COLEGIO EXTRANGERO ==");
            paises = paisDAO.allByNotCodigo(forLike(nombre), "PE");
        } else {
            logger.debug("== OTROS ==");
            paises = paisDAO.allByName(forLike(nombre));
        }
        return paises;
    }

    @Override
    public TipoDocIdentidad findById(Long id) {
        return tipoDocIdentidadDAO.find(id);
    }

    @Override
    public List<ModalidadIngreso> allModalidadIngreso() {
        return modalidadIngresoDAO.all();
    }

    @Override
    public List<ModalidadIngreso> allModalidadIngresoByCiclo(CicloPostula cicloPostula) {
        return modalidadIngresoDAO.allByCiclo(cicloPostula);
    }

    @Override
    public List<ModalidadIngresoCiclo> allModalidadIngresoCicloByCiclo(CicloPostula cicloPostula) {

        List<ConceptoPrecio> preciosModa = conceptoPrecioDAO.allModalidadByCicloPostula(cicloPostula);
        Map<Long, List<ConceptoPrecio>> mapPrecios = TypesUtil.convertListToMapList("conceptoPago.modalidadIngreso.id", preciosModa);

        List<ModalidadIngresoCiclo> modas = modalidadIngresoCicloDAO.allByCiclo(cicloPostula);
        for (ModalidadIngresoCiclo moda : modas) {
            List<ConceptoPrecio> conceptos = mapPrecios.get(moda.getModalidadIngreso().getId());
            if (conceptos != null) {
                moda.setConceptoPrecio(conceptos);
            }
        }
        return modas;
    }

    @Override
    public List<Universidad> allUniversidadByEstadoPais(String nombre, EstadoEnum estadoEnum, String CODIGO_PERU) {
        Pais pais = paisDAO.findByCode(CODIGO_PERU);
        return universidadDAO.allByEstadoPais(nombre, estadoEnum, pais);
    }

    @Override
    public List<GradoSecundaria> allGradoSecundaria() {
        return gradoSecundariaDAO.all();
    }
}
