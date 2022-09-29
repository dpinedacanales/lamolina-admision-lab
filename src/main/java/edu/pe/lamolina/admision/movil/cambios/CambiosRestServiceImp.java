package edu.pe.lamolina.admision.movil.cambios;

import edu.pe.lamolina.admision.controller.admision.inscripcion.numeroidentidad.NumeroIdentidadService;
import edu.pe.lamolina.admision.controller.admision.inscripcion.postulante.PostulanteService;
import edu.pe.lamolina.admision.controller.admision.inscripcion.postulante.cambios.CambioService;
import edu.pe.lamolina.admision.dao.inscripcion.PostulanteDAO;
import edu.pe.lamolina.admision.movil.complemento.ComplementoRestService;
import java.util.Iterator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.edu.lamolina.model.finanzas.ConceptoPrecio;
import pe.edu.lamolina.model.finanzas.SolicitudCambioInfo;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.model.inscripcion.ModalidadIngresoCiclo;
import pe.edu.lamolina.model.inscripcion.OpcionCarrera;
import pe.edu.lamolina.model.inscripcion.Postulante;

@Service
@Transactional(readOnly = true)
public class CambiosRestServiceImp implements CambiosRestService {

    @Autowired
    CambioService cambioService;

    @Autowired
    PostulanteDAO postulanteDAO;

    @Autowired
    ComplementoRestService complementoRestService;

    @Autowired
    NumeroIdentidadService numeroIdentidadService;

    @Autowired
    PostulanteService postulanteService;

    @Override
    public List<SolicitudCambioInfo> allSolicitudesByPostulante(Postulante postulante) {
        return cambioService.allSolicitudesByPostulante(postulante);
    }

    @Override
    public List<ConceptoPrecio> allConceptoPrecioCambiosByCiclo(Postulante postulanteForm) {
        Postulante postulante = postulanteDAO.find(postulanteForm.getId());
        CicloPostula cp = complementoRestService.findCicloPostulaActivo();
        return cambioService.allConceptoPrecioCambiosByCiclo(postulante, cp);
    }

    @Override
    public Postulante findPostulante(Long idPostulante) {
        Postulante p = new Postulante(idPostulante);
        return cambioService.findPostulante(p);
    }

    @Override
    public List<ModalidadIngreso> allModalidadesByCicloActual() {
        CicloPostula cp = complementoRestService.findCicloPostulaActivo();
        return cambioService.allModalidadesByCiclo(cp);
    }

    @Override
    public List<ModalidadIngresoCiclo> allModalidadIngresoCicloDisponible(List<ModalidadIngreso> modalidades) {
        CicloPostula cp = complementoRestService.findCicloPostulaActivo();
        return cambioService.allModalidadesCicloByCicloModalidades(cp, modalidades);
    }

    @Override
    @Transactional
    public void saveCambioDatosPersonales(Postulante postulante) {
        cambioService.saveCambios(postulante);
    }

    @Override
    @Transactional
    public void saveCambioSolicitud(Long idPostulante, List<ConceptoPrecio> conceptos) {
        Postulante postulante = postulanteDAO.find(idPostulante);

        ConceptoPrecio concepto = conceptos.get(0);
        ObjectUtil.eliminarAttrSinId(concepto);

        Iterator<OpcionCarrera> opcionesIterator = concepto.getPostulante().getOpcionCarrera().iterator();
        while (opcionesIterator.hasNext()) {
            OpcionCarrera opcion = opcionesIterator.next();

            if (opcion.getCarreraPostula().getId() == null) {
                opcionesIterator.remove();
            }
        }

        cambioService.saveCambioSolicitud(postulante, conceptos);
    }

    @Override
    @Transactional
    public void saveCambioDocumento(Long idPostulante, Long idTipoDoc, String numeroDocumento) {

        Postulante temporal = postulanteDAO.find(idPostulante);

        Postulante postulante = new Postulante(idPostulante);
        postulante.setPersona(new Persona(temporal.getPersona().getId()));
        postulante.setCicloPostula(temporal.getCicloPostula());

        numeroIdentidadService.saveCambioDni(postulante, idTipoDoc, numeroDocumento, null, temporal.getCicloPostula());
    }

    @Override
    @Transactional
    public void anularCambios(Long idPostulante) {
        Postulante postulante = postulanteDAO.find(idPostulante);

        cambioService.anularSolicitud(postulante);
        postulanteService.actualizarImportes(postulante, postulante.getCicloPostula());
    }

}
