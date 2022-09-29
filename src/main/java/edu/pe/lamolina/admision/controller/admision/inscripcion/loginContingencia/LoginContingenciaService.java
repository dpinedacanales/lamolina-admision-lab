/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pe.lamolina.admision.controller.admision.inscripcion.loginContingencia;

import javax.servlet.http.HttpSession;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Interesado;

/**
 *
 * @author dpinedac
 */
public interface LoginContingenciaService {

    public Boolean loginManuallyContingencia(Interesado interesado, HttpSession session);

    public Boolean verificarFechaInscripcion(CicloPostula ciclo, Interesado interesadoForm);
}
