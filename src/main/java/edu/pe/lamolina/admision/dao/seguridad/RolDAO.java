package edu.pe.lamolina.admision.dao.seguridad;

import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.model.enums.RolEnum;
import pe.edu.lamolina.model.seguridad.Rol;

public interface RolDAO extends Crud<Rol> {

    public Rol findCodigoEnum(RolEnum rolEnum);

}
