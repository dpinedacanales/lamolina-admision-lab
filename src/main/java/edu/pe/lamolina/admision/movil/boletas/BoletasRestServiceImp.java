package edu.pe.lamolina.admision.movil.boletas;

import edu.pe.lamolina.admision.controller.admision.inscripcion.postulante.PostulanteService;
import edu.pe.lamolina.admision.dao.finanzas.DeudaInteresadoDAO;
import edu.pe.lamolina.admision.dao.inscripcion.PostulanteDAO;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.finanzas.DeudaInteresado;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import pe.edu.lamolina.model.enums.DeudaEstadoEnum;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.Postulante;

@Slf4j
@Service
@Transactional(readOnly = true)
public class BoletasRestServiceImp implements BoletasRestService {
    
    @Autowired
    PostulanteService postulanteService;
    
    @Autowired
    PostulanteDAO postulanteDAO;
    
    @Autowired
    DeudaInteresadoDAO deudaInteresadoDAO;
    
    @Override
    public List<DeudaInteresado> allByPostulante(Long idPostulante) {
        Postulante postulante = postulanteService.findPostulante(new Postulante(idPostulante));
        return postulanteService.allBoletasByPostulante(postulante, Arrays.asList(DeudaEstadoEnum.ACT, DeudaEstadoEnum.PAG));        
    }
    
    @Override
    public Boolean tienePermiso(Interesado interesado) {
        if (interesado == null) {
            return false;
        }
        
        Long boletas = deudaInteresadoDAO.countlByInteresadoEstados(interesado, Arrays.asList(DeudaEstadoEnum.ACT, DeudaEstadoEnum.PAG));
        return boletas != null && boletas > 0;
    }
    
    @Override
    public Long countPendientes(Interesado interesado) {
        return deudaInteresadoDAO.countlByInteresadoEstados(interesado, Arrays.asList(DeudaEstadoEnum.ACT));
    }
    
}
