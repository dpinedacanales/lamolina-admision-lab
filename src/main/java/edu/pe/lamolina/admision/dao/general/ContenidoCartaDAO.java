package edu.pe.lamolina.admision.dao.general;

import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.model.enums.ContenidoCartaEnum;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.seguridad.Sistema;

public interface ContenidoCartaDAO extends Crud<ContenidoCarta> {

    ContenidoCarta findByCodigoEnum(ContenidoCartaEnum contenidoCartaEnum);

    ContenidoCarta findByCodigo(String mailnocert, Sistema sistema);
}
