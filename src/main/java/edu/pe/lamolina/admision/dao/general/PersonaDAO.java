package edu.pe.lamolina.admision.dao.general;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.model.enums.TipoDocIdentidadEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;

public interface PersonaDAO extends Crud<Persona> {

    Persona findByDocumento(TipoDocIdentidadEnum tipoDoc, String numeroDocumento);

    Persona findByDocumento(TipoDocIdentidad tipoDoc, String numeroDocumento);

    List<Persona> allPersona();

    void findLock(Long id);

}
