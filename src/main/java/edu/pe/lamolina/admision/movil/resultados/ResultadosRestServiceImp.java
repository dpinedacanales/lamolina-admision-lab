package edu.pe.lamolina.admision.movil.resultados;


import edu.pe.lamolina.admision.dao.inscripcion.EvaluadoDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.inscripcion.Evaluado;
import pe.edu.lamolina.model.inscripcion.Postulante;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ResultadosRestServiceImp implements ResultadosRestService {

    @Autowired
    EvaluadoDAO evaluadoDAO;

    @Override
    public List<Evaluado> allByCarrera(Long idCarrera) {
        Carrera carrera = new Carrera(idCarrera);
        return evaluadoDAO.allByCarrera(carrera);
    }

    @Override
    public Evaluado findByPostulante(Long idPostulante) {
        Postulante postulante = new Postulante(idPostulante);
        return evaluadoDAO.findByPostulante(postulante);
    }

    @Override
    public Evaluado findByDNI(String dni) {
        return evaluadoDAO.findByDNI(dni);
    }
    
}