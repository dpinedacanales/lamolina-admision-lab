package edu.pe.lamolina.admision.controller.admision.inscripcion.numeroidentidad;

import edu.pe.lamolina.admision.dao.general.PersonaDAO;
import edu.pe.lamolina.admision.dao.general.TipoDocIdentidadDAO;
import edu.pe.lamolina.admision.dao.inscripcion.CarreraPostulaDAO;
import edu.pe.lamolina.admision.dao.inscripcion.CicloPostulaDAO;
import edu.pe.lamolina.admision.dao.inscripcion.InteresadoDAO;
import edu.pe.lamolina.admision.dao.inscripcion.ModalidadIngresoCicloDAO;
import edu.pe.lamolina.admision.dao.inscripcion.OpcionCarreraDAO;
import edu.pe.lamolina.admision.dao.inscripcion.PostulanteDAO;
import edu.pe.lamolina.admision.dao.inscripcion.SolicitudCambioInfoDAO;
import edu.pe.lamolina.admision.dao.inscripcion.TipoCambioInfoDAO;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.constantines.AdmisionConstantine;
import pe.edu.lamolina.model.enums.SolicitudCambioInfoEstadoEnum;
import pe.edu.lamolina.model.enums.TipoCambioInfoEnum;
import pe.edu.lamolina.model.finanzas.SolicitudCambioInfo;
import pe.edu.lamolina.model.finanzas.TipoCambioInfo;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.inscripcion.CarreraPostula;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.ModalidadIngresoCiclo;
import pe.edu.lamolina.model.inscripcion.OpcionCarrera;
import pe.edu.lamolina.model.inscripcion.Postulante;

@Service
@Transactional(readOnly = true)
public class NumeroIdentidadServiceImp implements NumeroIdentidadService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    PostulanteDAO postulanteDAO;
    @Autowired
    ModalidadIngresoCicloDAO modalidadIngresoCicloDAO;
    @Autowired
    OpcionCarreraDAO opcionCarreraDAO;
    @Autowired
    CarreraPostulaDAO carreraPostulaDAO;
    @Autowired
    TipoDocIdentidadDAO tipoDocIdentidadDAO;
    @Autowired
    PersonaDAO personaDAO;
    @Autowired
    TipoCambioInfoDAO tipoCambioInfoDAO;
    @Autowired
    SolicitudCambioInfoDAO solicitudCambioInfoDAO;
    @Autowired
    CicloPostulaDAO cicloPostulaDAO;
    @Autowired
    InteresadoDAO interesadoDAO;

    @Override
    public Postulante findPostulante(Postulante postulForm) {
        logger.debug("OPCION CARRERA POSTULANTE 1 {}", postulForm.getId());
        Postulante postulante = postulanteDAO.find(postulForm.getId());
        List<ModalidadIngresoCiclo> modalidadesCiclo = modalidadIngresoCicloDAO.allByCiclo(postulante.getCicloPostula());

        Map<Long, ModalidadIngresoCiclo> mapModalidadC = TypesUtil.convertListToMap("modalidadIngreso.id", modalidadesCiclo);

        if (postulante.getModalidadIngreso() != null) {
            ModalidadIngresoCiclo modaing = mapModalidadC.get(postulante.getModalidadIngreso().getId());
            postulante.setModalidadIngresoCiclo(modaing);
        }

        List<OpcionCarrera> opciones = opcionCarreraDAO.allByPostulante(postulante);
        if (opciones.isEmpty()) {
            List<CarreraPostula> carrerasPostul = carreraPostulaDAO.allByCiclo(postulante.getCicloPostula());
            OpcionCarrera opcion = new OpcionCarrera();
            opcion.setCarreraPostula(carrerasPostul.get(0));
            opcion.setPrioridad(1);
            opcion.setPostulante(postulante);
            opciones.add(opcion);
        }
        postulante.setOpcionCarrera(opciones);
        return postulante;
    }

    @Override
    public List<TipoDocIdentidad> allTiposDocIdentidad() {
        return tipoDocIdentidadDAO.allForPersonaNatural();
    }

    private void verificarPersona(Postulante postulanteForm, CicloPostula ciclo) {
        Persona personaForm = postulanteForm.getPersona();
        personaForm.setNumeroDocIdentidad(limpiarValor(personaForm.getNumeroDocIdentidad()));

        Assert.isTrue(personaForm.getTipoDocumento() != null, "Debe indicar el documento de identidad");
        Assert.isTrue(personaForm.getNumeroDocIdentidad() != null, "Debe indicar el número del documento de identidad");
        Assert.isFalse(personaForm.getNumeroDocIdentidad().equals(AdmisionConstantine.CODE_POSTULANTE_DUMMY),
                "Este número de documento de identidad no está permitido");

        TipoDocIdentidad tipoDoc = tipoDocIdentidadDAO.find(personaForm.getTipoDocumento().getId());
        if (tipoDoc.getLongitudExacta() == 1) {
            Assert.isTrue(personaForm.getNumeroDocIdentidad().length() == tipoDoc.getLongitud(),
                    "El número de documento debe tener " + tipoDoc.getLongitud() + " caracteres");
        } else if (tipoDoc.getLongitudExacta() == 0) {
            Assert.isTrue(personaForm.getNumeroDocIdentidad().length() >= 4,
                    "El número de documento debe tener como mínimo 4 caracteres");
            Assert.isTrue(personaForm.getNumeroDocIdentidad().length() <= tipoDoc.getLongitud(),
                    "El número de documento debe tener como máximo " + tipoDoc.getLongitud() + " caracteres");
        }

        Persona personaBD = personaDAO.findByDocumento(personaForm.getTipoDocumento(), personaForm.getNumeroDocIdentidad());

        logger.debug("Persona {} ", personaBD);
        logger.debug("tipo {} ", personaForm.getTipoDocumento().getId());
        logger.debug("numero {} ", personaForm.getNumeroDocIdentidad());

        if (personaBD != null) {
            logger.debug("Persona ID {} ", personaBD.getId());

            if (personaBD.getId() == personaForm.getId().longValue()) {
                if (!ciclo.getEsVirtual()) {
                    throw new PhobosException("No ha indicado ningún cambio");
                } else {
                    if (personaBD.getEmail().equals(personaForm.getEmail())) {
                        throw new PhobosException("No ha indicado ningún cambio");
                    }
                }
            } else {
                throw new PhobosException("Ya existe otra persona en el sistema con este documento de identidad");
            }
        }

        List<Postulante> postulantes = postulanteDAO.allByPersonaCiclo(personaForm, ciclo);
        Map<Long, Postulante> mapPostulantes = TypesUtil.convertListToMap("id", postulantes);
        Postulante postulanteBD = mapPostulantes.get(postulanteForm.getId());
        if (postulanteBD != null) {
            postulantes.remove(postulanteBD);
        }
        Assert.isTrue(postulantes.isEmpty(), "Ya existe un postulante inscrito al proceso de admisión con este mismo documento de identidad");

    }

    private String limpiarValor(String valor) {
        if (valor == null) {
            return null;
        }

        valor = valor.trim();
        if (StringUtils.isEmpty(valor)) {
            return null;
        }
        return valor;
    }

    @Override
    @Transactional
    public void saveCambioDni(Postulante postulante, Long tipoDoc, String numero, String email, CicloPostula cicloPostula) {

        cicloPostula = cicloPostulaDAO.find(cicloPostula.getId());
        TipoDocIdentidad tipo = new TipoDocIdentidad(tipoDoc);
        postulante.getPersona().setTipoDocumento(tipo);
        postulante.getPersona().setNumeroDocIdentidad(numero);
        if (cicloPostula.getEsVirtual() && email != null) {
            postulante.getPersona().setEmail(email);
        }
        verificarPersona(postulante, postulante.getCicloPostula());
        Postulante postulanteDB = postulanteDAO.find(postulante.getId());
        if (postulanteDB.getCambioDni() != null && postulanteDB.getCambioDni() != 0) {
            throw new PhobosException("Ya no puedes actualizar el numero de Documento");
        }
        postulanteDB.setCambioDni(1);
        if (cicloPostula.getEsVirtual() && email != null) {
            postulanteDB.setEmail(postulante.getPersona().getEmail());
        }
        postulanteDB.setCodigo(postulante.getPersona().getNumeroDocIdentidad());

        postulanteDAO.update(postulanteDB);
        
        Interesado interesado = interesadoDAO.find(postulanteDB.getInteresado().getId());
        interesado.setFacebook(postulante.getPersona().getNumeroDocIdentidad() + "-" + interesado.getCodigoVerificacion());
        interesado.setFacebookLink(postulante.getPersona().getNumeroDocIdentidad() + "-" + interesado.getCodigoVerificacion());
        interesado.setNumeroDocIdentidad(postulante.getPersona().getNumeroDocIdentidad());
        interesadoDAO.updateColumns(interesado,"facebook","facebookLink","numeroDocIdentidad");
        
        Persona personaDB = personaDAO.find(postulante.getPersona().getId());

        TipoDocIdentidad tipoDocOld = personaDB.getTipoDocumento();
        String numeroOld = personaDB.getNumeroDocIdentidad();
        logger.debug("tipoDocOld {}", tipoDocOld);
        logger.debug("numeroOld {}", numeroOld);

        personaDB.setTipoDocumento(tipo);
        personaDB.setNumeroDocIdentidad(postulante.getPersona().getNumeroDocIdentidad());
        personaDB.setEnviarRecauda(1);
        if (cicloPostula.getEsVirtual() && email != null) {
            personaDB.setEmail(postulante.getPersona().getEmail());
        }
        personaDAO.update(personaDB);

        TipoCambioInfoEnum cambioInfoEnum = cicloPostula.getEsVirtual() ? TipoCambioInfoEnum.CNDEM : TipoCambioInfoEnum.CNUMDOC;
        TipoCambioInfo tipoCambio = tipoCambioInfoDAO.findByEnum(cambioInfoEnum);
        logger.debug("tipoCambio {}", tipoCambio);
        SolicitudCambioInfo solicitud = new SolicitudCambioInfo();
        solicitud.setTipoCambioInfo(tipoCambio);
        solicitud.setPostulante(postulante);
        solicitud.setFechaRegistro(new Date());
        solicitud.setEstado(SolicitudCambioInfoEstadoEnum.COMP.name());
        solicitud.setNumeroDocIdentidad(numeroOld);
        solicitud.setTipoDocumento(tipoDocOld);
        solicitudCambioInfoDAO.save(solicitud);
    }

}
