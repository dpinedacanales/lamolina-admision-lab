package edu.pe.lamolina.admision.controller.admision.inscripcion.guiapostulante;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.pe.lamolina.admision.controller.admision.inscripcion.inscripcion.InscripcionService;
import edu.pe.lamolina.admision.controller.admision.inscripcion.postulante.PostulanteService;
import edu.pe.lamolina.admision.dao.academico.ModalidadEstudioDAO;
import edu.pe.lamolina.admision.dao.finanzas.AcreenciaDAO;
import edu.pe.lamolina.admision.dao.finanzas.ConceptoPagoDAO;
import edu.pe.lamolina.admision.dao.finanzas.ConceptoPrecioDAO;
import edu.pe.lamolina.admision.dao.finanzas.DeudaInteresadoDAO;
import edu.pe.lamolina.admision.dao.finanzas.ItemDeudaInteresadoDAO;
import edu.pe.lamolina.admision.dao.general.ContenidoCartaDAO;
import edu.pe.lamolina.admision.dao.general.OficinaDAO;
import edu.pe.lamolina.admision.dao.general.PersonaDAO;
import edu.pe.lamolina.admision.dao.general.TipoDocIdentidadDAO;
import edu.pe.lamolina.admision.dao.inscripcion.CicloPostulaDAO;
import edu.pe.lamolina.admision.dao.inscripcion.EventoCicloDAO;
import edu.pe.lamolina.admision.dao.inscripcion.EventoDAO;
import edu.pe.lamolina.admision.dao.inscripcion.InteresadoDAO;
import edu.pe.lamolina.admision.dao.inscripcion.PostulanteDAO;
import pe.edu.lamolina.model.constantines.AdmisionConstantine;
import edu.pe.lamolina.admision.zelper.mail.MailerService;
import edu.pe.lamolina.admision.zelper.model.DataSessionAdmision;
import java.util.Map;
import java.util.stream.Collectors;
import org.joda.time.LocalDate;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.constantines.AdmisionConstantine;
import pe.edu.lamolina.model.enums.ContenidoCartaEnum;
import pe.edu.lamolina.model.enums.DeudaEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.InteresadoEstadoEnum;
import static pe.edu.lamolina.model.enums.InteresadoEstadoEnum.REG;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.NombreTablasEnum;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.enums.persona.PersonaEstadoEnum;
import pe.edu.lamolina.model.enums.PostulanteEstadoEnum;
import pe.edu.lamolina.model.enums.TipoProspectoEnum;
import pe.edu.lamolina.model.finanzas.Acreencia;
import pe.edu.lamolina.model.finanzas.ConceptoPago;
import pe.edu.lamolina.model.finanzas.ConceptoPrecio;
import pe.edu.lamolina.model.finanzas.DeudaInteresado;
import pe.edu.lamolina.model.finanzas.ItemDeudaInteresado;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.inscripcion.Evento;
import pe.edu.lamolina.model.inscripcion.EventoCiclo;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.Postulante;

@Service
@Transactional(readOnly = true)
public class GuiaPostulanteServiceImp implements GuiaPostulanteService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    PostulanteDAO postulanteDAO;
    @Autowired
    TipoDocIdentidadDAO tipoDocIdentidadDAO;
    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;
    @Autowired
    CicloPostulaDAO cicloPostulaDAO;
    @Autowired
    InteresadoDAO interesadoDAO;
    @Autowired
    PersonaDAO personaDAO;
    @Autowired
    DeudaInteresadoDAO deudaInteresadoDAO;
    @Autowired
    ConceptoPrecioDAO conceptoPrecioDAO;
    @Autowired
    ConceptoPagoDAO conceptoPagoDAO;

    @Autowired
    PostulanteService postulanteService;
    @Autowired
    MailerService mailerService;
    @Autowired
    InscripcionService inscripcionService;
    @Autowired
    ContenidoCartaDAO contenidoCartaDAO;
    @Autowired
    AcreenciaDAO acreenciaDAO;
    @Autowired
    EventoDAO eventoDAO;
    @Autowired
    EventoCicloDAO eventoCicloDAO;
    @Autowired
    ItemDeudaInteresadoDAO itemDeudaInteresadoDAO;

    @Autowired
    OficinaDAO oficinaDAO;

    @Override
    public Postulante findPostulante(Postulante postulanteSession) {
        return postulanteDAO.find(postulanteSession.getId());
    }

    @Override
    public List<TipoDocIdentidad> allTiposDocIdentidad() {
        return tipoDocIdentidadDAO.allForPersonaNatural();
    }

    @Override
    public ModalidadEstudio findModalidadEstudio(ModalidadEstudioEnum modalidadEstudioEnum) {
        return modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
    }

    @Override
    public CicloPostula findCicloPostulaActivo(ModalidadEstudio modalidad) {
        return cicloPostulaDAO.findActivo(modalidad);
    }

    @Override
    @Transactional
    public Postulante savePostulante(Postulante postulanteForm, Interesado interesadoForm, HttpSession session) {
        Interesado interesado = interesadoDAO.find(interesadoForm.getId());
        Postulante postulante = postulanteDAO.findActivoByInteresadoSimple(interesado);

        ModalidadEstudio modalidad = this.findModalidadEstudio(ModalidadEstudioEnum.PRE);
        CicloPostula ciclo = this.findCicloPostulaActivo(modalidad);
        Persona personaForm = postulanteForm.getPersona();
        verificarPersonaPostulanteActivo(personaForm, ciclo);
        revisarPostulanteActivo(personaForm, ciclo);

        if (InteresadoEstadoEnum.CRE == interesado.getEstadoEnum()) {
            interesado.setPaterno(getValor(personaForm.getPaterno()));
            interesado.setMaterno(getValor(personaForm.getMaterno()));
            interesado.setNombres(getValor(personaForm.getNombres()));
            interesado.setEmail(getValor(personaForm.getEmail()));
            interesado.setTelefono(getValor(personaForm.getTelefono()));
            interesado.setCelular(getValor(personaForm.getCelular()));
            interesado.setCicloPostula(ciclo);
            interesado.setEstado(REG);
            interesadoDAO.update(interesado);
        }

        Persona persona = null;

        if (postulante == null) {

            postulante = new Postulante();
            postulante.setEstado(PostulanteEstadoEnum.CRE);
            postulante.setFechaRegistro(new Date());
            postulante.setCicloPostula(ciclo);
            postulante.setInteresado(interesado);
            postulante.setImportePagar(BigDecimal.ZERO);
            postulante.setImporteAbonado(BigDecimal.ZERO);
            postulante.setImporteDescuento(BigDecimal.ZERO);
            postulante.setImporteTotal(BigDecimal.ZERO);
            postulante.setImporteUtilizado(BigDecimal.ZERO);
            postulanteDAO.save(postulante);

            persona = this.crearPersona(personaForm, interesado);
            postulante.setPersona(persona);
            postulante.setCodigo(persona.getNumeroDocIdentidad());
            postulanteDAO.update(postulante);

        } else if (postulante.getPersona().getNumeroDocIdentidad().equals(AdmisionConstantine.CODE_POSTULANTE_DUMMY)) {

            postulante.setEstado(PostulanteEstadoEnum.CRE);
            postulante.setFechaRegistro(new Date());
            postulante.setImportePagar(BigDecimal.ZERO);
            postulante.setImporteAbonado(BigDecimal.ZERO);
            postulante.setImporteDescuento(BigDecimal.ZERO);
            postulante.setImporteTotal(BigDecimal.ZERO);
            postulante.setImporteUtilizado(BigDecimal.ZERO);

            persona = this.crearPersona(personaForm, interesado);
            postulante.setPersona(persona);
            postulante.setCodigo(persona.getNumeroDocIdentidad());
            postulanteDAO.update(postulante);
        }

        this.generarDeuda(interesado, postulanteForm.getTipoProspecto(), ciclo, postulante);
        postulanteService.revisarDeudasCompletas(postulante, ciclo);
        List<DeudaInteresado> deudas = postulanteService.allDeudaActivaByPostulante(postulante);

        BigDecimal pagar = BigDecimal.ZERO;
        BigDecimal abonado = BigDecimal.ZERO;
        for (DeudaInteresado deuda : deudas) {
            if (deuda.getPostulante() == null) {
                pagar = pagar.add(deuda.getMonto());
                abonado = abonado.add(deuda.getAbono());
            }
        }
        postulante.setImportePagar(pagar);
        postulante.setImporteAbonado(abonado);

        mailerService.enviarPagoGuiaPostulante(postulanteForm.getTipoProspecto(), postulante, deudas, ciclo);

        if (session != null) {
            DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
            ds.setPostulante(postulante);
            ds.setInteresado(interesado);
            ds.setPersona(persona);
            ds.setCicloPostula(ciclo);
            session.setAttribute(AdmisionConstantine.SESSION_USUARIO, ds);
        }

        return postulante;
    }

    private void verificarPersonaPostulanteActivo(Persona personaForm, CicloPostula ciclo) {
        String numero = personaForm.getNumeroDocIdentidad().trim().replaceAll(" ", "");
        personaForm.setNumeroDocIdentidad(numero);
        Persona personaBD = personaDAO.findByDocumento(personaForm.getTipoDocumento(), personaForm.getNumeroDocIdentidad());
        if (personaBD == null) {
            return;
        }
        List<Postulante> postulantesBD = postulanteDAO.allByPersonaCiclo(personaBD, ciclo);
        if (postulantesBD.isEmpty()) {
            return;
        }
        TipoDocIdentidad tipoDoc = tipoDocIdentidadDAO.find(personaForm.getTipoDocumento().getId());
        String simbolo = tipoDoc.getSimbolo();
        throw new PhobosException("Ya existe una persona registrada con este documento de identidad (" + simbolo + " " + numero + "). En caso de reclamo comuníquese al " + AdmisionConstantine.CELULAR_HELPDESK);
    }

    private void revisarPostulanteActivo(Persona persona, CicloPostula ciclo) {
        if (persona.getTipoDocumento() == null) {
            throw new PhobosException("Debe indicar el tipo de documento de identidad");
        }

        if (StringUtils.isEmpty(persona.getNumeroDocIdentidad())) {
            throw new PhobosException("Debe indicar el número del documento de identidad");
        }

        TipoDocIdentidad tipoDoc = tipoDocIdentidadDAO.find(persona.getTipoDocumento().getId());
        String numero = persona.getNumeroDocIdentidad().trim().replaceAll(" ", "");
        String simbolo = tipoDoc.getSimbolo();
        persona.setNumeroDocIdentidad(numero);
        Postulante postulante = postulanteDAO.findActivoByDocumento(tipoDoc.getTipoDocIdentidadEnum(), numero, ciclo);
        if (postulante != null) {
            throw new PhobosException("Ya existe una persona registrada con este de documento de identidad (" + simbolo + " " + numero + "). En caso de reclamo comuníquese al " + AdmisionConstantine.CELULAR_HELPDESK);
        }

        if (tipoDoc.getLongitudExacta() == 1) {
            if (numero.length() != tipoDoc.getLongitud()) {
                throw new PhobosException("El número de documento debe tener " + tipoDoc.getLongitud() + " caracteres");
            }
        } else if (tipoDoc.getLongitudExacta() == 0) {
            if (numero.length() < 4) {
                throw new PhobosException("El número de documento debe tener como mínimo 4 caracteres");
            }
            if (numero.length() > tipoDoc.getLongitud()) {
                throw new PhobosException("El número de documento debe tener como máximo " + tipoDoc.getLongitud() + " caracteres");
            }
        }
    }

    private Persona crearPersona(Persona personaForm, Interesado interesado) {

        personaForm.setNumeroDocIdentidad(getValor(personaForm.getNumeroDocIdentidad()));
        if (personaForm.getTipoDocumento() == null || (personaForm.getTipoDocumento() != null && personaForm.getTipoDocumento().getId() == null)) {
            throw new PhobosException("Debe indicar el documento de identidad");
        }
        if (personaForm.getNumeroDocIdentidad() == null) {
            throw new PhobosException("Debe indicar el número del documento de identidad");
        }
        logger.debug("BUSCAR PERSONA DOC {} NUM  {}", personaForm.getTipoDocumento(), personaForm.getNumeroDocIdentidad());
        Persona personaBD = personaDAO.findByDocumento(personaForm.getTipoDocumento(), personaForm.getNumeroDocIdentidad());
        if (personaBD != null) {
            return personaBD;
        }
        TipoDocIdentidad tipoDoc = tipoDocIdentidadDAO.find(personaForm.getTipoDocumento().getId());

        Persona persona = new Persona(personaForm.getTipoDocumento(), personaForm.getNumeroDocIdentidad());
        persona.setPaterno(getValor(interesado.getPaterno()));
        persona.setMaterno(getValor(interesado.getMaterno()));
        persona.setNombres(getValor(interesado.getNombres()));
        persona.setEmail(getValor(interesado.getEmail()));
        persona.setTelefono(getValor(interesado.getTelefono()));
        persona.setCelular(getValor(interesado.getCelular()));
        persona.setEstadoEnum(PersonaEstadoEnum.ACT);
        persona.setTipoDocumento(tipoDoc);
        personaDAO.save(persona);

        return persona;
    }

    private String getValor(String column) {
        if (column == null) {
            return null;
        }
        if (StringUtils.isEmpty(column.trim())) {
            return null;
        }
        return column.trim();
    }

    @Override
    public Interesado findInteresado(Interesado interesadoSession) {
        return interesadoDAO.find(interesadoSession.getId());
    }

    @Override
    public Postulante findPostulanteByInteresado(Interesado interesado) {
        return postulanteDAO.findActivoByInteresadoSimple(interesado);
    }

    @Override
    public ConceptoPrecio findConceptoGuiaPostulanteByCiclo(String tipoProspecto, CicloPostula ciclo) {
        return postulanteService.findConceptoGuiaPostulanteByCiclo(tipoProspecto, ciclo);
    }

    private void generarDeuda(Interesado interesado, String tipoProspecto, CicloPostula ciclo, Postulante postulante) {

        List<DeudaInteresado> deudas = deudaInteresadoDAO.allByInteresadoEstado(interesado, DeudaEstadoEnum.ACT);
        if ((tipoProspecto.equals(TipoProspectoEnum.sinexamen.toString()) || tipoProspecto.equals(TipoProspectoEnum.conexamen.toString()))
                && !inscripcionService.esFinalInscripciones(ciclo)) {
            throw new PhobosException("Operación incorrecta");
        }

        ConceptoPrecio precioGuiaPostulante = postulanteService.findConceptoGuiaPostulanteByCiclo(tipoProspecto, ciclo);

        ConceptoPrecio precioGuiaPostulanteDB = null;

        if (tipoProspecto.equals(TipoProspectoEnum.regular.toString())) {
            precioGuiaPostulanteDB = this.findConceptoGuiaPostulanteFromDeudas(deudas);
        } else {
            precioGuiaPostulanteDB = this.findConceptoGuiaPostulanteFromDeudas(TipoProspectoEnum.valueOf(tipoProspecto).getDisplayName(), deudas);
        }
        crearConcepto(precioGuiaPostulante, precioGuiaPostulanteDB, deudas, interesado, postulante, ciclo);

    }

    private ConceptoPrecio findConceptoGuiaPostulanteFromDeudas(String codigoProspecto, List<DeudaInteresado> deudas) {
        for (DeudaInteresado deuda : deudas) {
            ConceptoPrecio conceptoPrecio = deuda.getConceptoPrecio();
            ConceptoPago conceptoPago = conceptoPrecio.getConceptoPago();
            if (conceptoPago.getCodigo().equals(codigoProspecto)) {
                return conceptoPrecio;
            }
        }
        return null;
    }

    private ConceptoPrecio findConceptoGuiaPostulanteFromDeudas(List<DeudaInteresado> deudas) {
        for (DeudaInteresado deuda : deudas) {
            ConceptoPrecio conceptoPrecio = deuda.getConceptoPrecio();
            ConceptoPago conceptoPago = conceptoPrecio.getConceptoPago();
            if (conceptoPago.isProspecto()) {
                return conceptoPrecio;
            }
        }
        return null;
    }

    @Transactional
    private void crearConcepto(ConceptoPrecio concepto, ConceptoPrecio conceptoBD, List<DeudaInteresado> deudas, Interesado interesado, Postulante postulante, CicloPostula cicloPostula) {
        if (concepto == null) {
            return;
        }
        DateTime today = new DateTime();

        Postulante postulanteActivo = postulanteDAO.findByInteresado(interesado);

        if (conceptoBD == null) {
            DeudaInteresado deuda = new DeudaInteresado();
            deuda.setConceptoPrecio(concepto);
            deuda.setInteresado(interesado);
            deuda.setAbono(BigDecimal.ZERO);
            deuda.setMonto(concepto.getMonto());
            deuda.setFechaRegistro(new Date());
            deuda.setPostulante(postulante);
            deuda.setEstado(EstadoEnum.ACT.name());
            deuda.setMontoAnterior(BigDecimal.ZERO);
            deuda.setCuentaBancaria(concepto.getConceptoPago().getCuentaBancaria());
            deuda.setDescripcion(concepto.getConceptoPago().getDescripcion());
            deudaInteresadoDAO.save(deuda);

            deudas.add(deuda);

            ConceptoPrecio conceptoPrecioFull = conceptoPrecioDAO.find(deuda.getConceptoPrecio().getId());

            Date fechaVencimiento = fechaVencimientoByModalidad(postulante, cicloPostula);

            Oficina oficinaCap = oficinaDAO.findByCodigo(OficinaEnum.CAP.name());

            ItemDeudaInteresado itemDeuda = new ItemDeudaInteresado();
            itemDeuda.setMonto(concepto.getMonto());
            itemDeuda.setDeudaInteresado(deuda);
            itemDeuda.setConceptoPrecio(concepto);
            itemDeuda.setEstado(EstadoEnum.ACT.name());
            itemDeuda.setFechaRegistro(new Date());
            itemDeuda.setTipo("SUMA");
            itemDeudaInteresadoDAO.save(itemDeuda);

            if (fechaVencimiento == null) {
                EventoCiclo ev = findEventoExtemporaneo(cicloPostula);
                if (ev != null) {
                    fechaVencimiento = ev.getFechaFin();
                } else {
                    DateTime f = new DateTime();
                    fechaVencimiento = f.plusDays(30).toDate();
                }
            }

            Acreencia acreencia = new Acreencia();
            acreencia.setCuentaBancaria(conceptoPrecioFull.getConceptoPago().getCuentaBancaria());
            acreencia.setDescripcion(conceptoPrecioFull.getConceptoPago().getDescripcion());
            acreencia.setEstadoEnum(DeudaEstadoEnum.DEU);
            acreencia.setFechaDocumento(today.toDate());
            acreencia.setFechaVencimiento(fechaVencimiento);
            acreencia.setInstanciaTabla(deuda.getId());
            acreencia.setMonto(deuda.getMonto());
            acreencia.setOficina(oficinaCap);
            acreencia.setAbono(BigDecimal.ZERO);
            acreencia.setFechaRegistro(today.toDate());
            acreencia.setPersona(postulanteActivo.getPersona());
            acreencia.setTablaEnum(NombreTablasEnum.FIN_DEUDA_INTERESADO);
            acreenciaDAO.save(acreencia);

            return;
        }

        ConceptoPago conceptoPago = concepto.getConceptoPago();
        if (conceptoBD.getId() == concepto.getId().longValue() && conceptoPago.isProspecto()) {
            return;
        }

        DeudaInteresado deuda = new DeudaInteresado();
        deuda.setConceptoPrecio(concepto);
        deuda.setInteresado(interesado);
        deuda.setAbono(BigDecimal.ZERO);
        deuda.setMonto(concepto.getMonto());
        deuda.setFechaRegistro(new Date());
        deuda.setPostulante(postulante);
        deuda.setEstado(EstadoEnum.ACT.name());
        deuda.setMontoAnterior(BigDecimal.ZERO);
        deuda.setCuentaBancaria(concepto.getConceptoPago().getCuentaBancaria());
        deuda.setDescripcion(concepto.getConceptoPago().getDescripcion());
        deudaInteresadoDAO.save(deuda);

        ConceptoPrecio conceptoPrecioFull = conceptoPrecioDAO.find(deuda.getConceptoPrecio().getId());

        Date fechaVencimiento = fechaVencimientoByModalidad(postulante, cicloPostula);
        if (fechaVencimiento == null) {
            EventoCiclo ev = findEventoExtemporaneo(cicloPostula);
            if (ev != null) {
                fechaVencimiento = ev.getFechaFin();
            } else {
                DateTime f = new DateTime();
                fechaVencimiento = f.plusDays(30).toDate();
            }
        }

        Oficina oficinaCap = oficinaDAO.findByCodigo(OficinaEnum.CAP.name());

        Acreencia acreencia = new Acreencia();
        acreencia.setCuentaBancaria(conceptoPrecioFull.getConceptoPago().getCuentaBancaria());
        acreencia.setDescripcion(conceptoPrecioFull.getConceptoPago().getDescripcion());
        acreencia.setEstadoEnum(DeudaEstadoEnum.DEU);
        acreencia.setFechaDocumento(today.toDate());
        acreencia.setFechaVencimiento(fechaVencimiento);
        acreencia.setInstanciaTabla(deuda.getId());
        acreencia.setMonto(deuda.getMonto());
        acreencia.setOficina(oficinaCap);
        acreencia.setAbono(BigDecimal.ZERO);
        acreencia.setFechaRegistro(today.toDate());
        acreencia.setPersona(postulante.getPersona());
        acreencia.setTablaEnum(NombreTablasEnum.FIN_DEUDA_INTERESADO);
        acreenciaDAO.save(acreencia);

        deudas.add(deuda);

    }

    private Date fechaVencimientoByModalidad(Postulante postulante, CicloPostula cicloPostula) {
        if (postulante.getModalidadIngreso() != null) {
            if (postulante.getModalidadIngreso().isPreLaMolina()) {
                EventoCiclo examen = findEventoExamen(cicloPostula);
                return new LocalDate(examen).dayOfMonth().withMaximumValue().toDate();
            } else {
                List<EventoCiclo> eventosInscripcion = eventoCicloDAO.allEventoInscripcionesByCiclo(cicloPostula);
                return eventosInscripcion.stream().filter(ev -> ev.getFechaFin() != null).map(ev -> ev.getFechaFin()).max(Date::compareTo).get();
            }
        }
        return null;
    }

    private EventoCiclo findEventoExamen(CicloPostula ciclo) {
        Evento evento = eventoDAO.findByCode("EXAM");
        List<EventoCiclo> examenes = eventoCicloDAO.allByEventoCiclo(evento, ciclo);
        if (!examenes.isEmpty()) {
            return examenes.get(0);
        }
        return null;
    }

    private EventoCiclo findEventoExtemporaneo(CicloPostula ciclo) {
        Evento evento = eventoDAO.findByCode("EXTM");
        List<EventoCiclo> examenes = eventoCicloDAO.allByEventoCiclo(evento, ciclo);
        if (!examenes.isEmpty()) {
            return examenes.get(0);
        }
        return null;
    }

    @Override
    public Boolean pagoGuiaPostulante(Interesado interesado, List<DeudaInteresado> deudasTodas) {
        List<DeudaInteresado> deudas = deudasTodas.stream().filter(x -> x.isCancelada()).collect(Collectors.toList());
        for (DeudaInteresado deuda : deudas) {
            for (ItemDeudaInteresado item : deuda.getItemDeudaInteresado()) {
                ConceptoPago cpto = item.getConceptoPrecio().getConceptoPago();
                if (cpto.getCodigo().equals(AdmisionConstantine.CODE_PROSPECTO)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public Boolean validandoExamenVirtual(Interesado interesado, List<DeudaInteresado> deudasTodas) {
        List<DeudaInteresado> deudas = deudasTodas.stream().filter(x -> x.isCancelada()).collect(Collectors.toList());
        for (DeudaInteresado deuda : deudas) {
            for (ItemDeudaInteresado item : deuda.getItemDeudaInteresado()) {
                if (item.getConceptoPrecio() == null) {
                    continue;
                }
                ConceptoPago cpto = item.getConceptoPrecio().getConceptoPago();
                if (cpto.getCodigo().equals(AdmisionConstantine.CODE_PROSPECTO)) {
                    return true;
                }
            }
        }

        return false;

    }

    @Override
    public List<DeudaInteresado> allByInteresado(Interesado interesado) {
        List<DeudaInteresado> deudas = deudaInteresadoDAO.allByInteresadoEstados(interesado, Arrays.asList(DeudaEstadoEnum.ACT, DeudaEstadoEnum.PAG));
        for (DeudaInteresado deuda : deudas) {
            deuda.setItemDeudaInteresado(new ArrayList());
        }
        Map<Long, DeudaInteresado> mapDeuda = TypesUtil.convertListToMap("id", deudas);
        List<ItemDeudaInteresado> itemsDeudas = itemDeudaInteresadoDAO.allByDeudasInteresadoEstados(deudas, Arrays.asList(DeudaEstadoEnum.ACT, DeudaEstadoEnum.PAG));
        for (ItemDeudaInteresado item : itemsDeudas) {
            DeudaInteresado deuda = mapDeuda.get(item.getDeudaInteresado().getId());
            deuda.getItemDeudaInteresado().add(item);
        }
        return deudas;

    }

    @Override
    public Boolean validandoAdquirirExamen(Interesado interesado, List<DeudaInteresado> deudas) {

        if (verificarConceptoPagado(TipoProspectoEnum.sinexamen.getDisplayName(), deudas)) {
            if (verificarConcepto(TipoProspectoEnum.examenvirtual.getDisplayName(), deudas)) {
                return false;
            }
            return true;
        }
        return false;
    }

    private boolean verificarConcepto(String codeConcepto, List<DeudaInteresado> deudas) {
        for (DeudaInteresado deuda : deudas) {
            ConceptoPago concepto = deuda.getConceptoPrecio().getConceptoPago();
            if (concepto.getCodigo().equals(codeConcepto)) {
                return true;
            }
        }
        return false;
    }

    private boolean verificarConceptoPagado(String codeConcepto, List<DeudaInteresado> deudasTodas) {
        List<DeudaInteresado> deudas = deudasTodas.stream().filter(x -> x.isCancelada()).collect(Collectors.toList());
        for (DeudaInteresado deuda : deudas) {
            for (ItemDeudaInteresado item : deuda.getItemDeudaInteresado()) {
                ConceptoPago cpto = item.getConceptoPrecio().getConceptoPago();
                if (cpto.getCodigo().equals(codeConcepto)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Boolean validandoDescargaBoleta(Interesado interesado, List<DeudaInteresado> deudas) {
        List<ConceptoPrecio> precios = new ArrayList();
        deudas.stream()
                .filter(deuda -> !deuda.isCancelada())
                .forEach(deuda -> {
                    precios.add(deuda.getConceptoPrecio());
                });
        if (!precios.isEmpty()) {
            return true;
        } else {
            return false;
        }

    }

    @Override
    @Transactional
    public void saveAdquirirExamen(Postulante postulanteForm, CicloPostula ciclo) {
        ConceptoPago conceptoPago = conceptoPagoDAO.findByCodigo(TipoProspectoEnum.examenvirtual.getDisplayName());
        ConceptoPrecio conceptoPrecio = conceptoPrecioDAO.findByConceptoCiclo(conceptoPago, ciclo);

        DeudaInteresado deudaInteresado = new DeudaInteresado();
        deudaInteresado.setInteresado(new Interesado(postulanteForm.getInteresado().getId()));
        deudaInteresado.setFechaRegistro(new Date());
        deudaInteresado.setConceptoPrecio(conceptoPrecio);
        deudaInteresado.setMonto(conceptoPrecio.getMonto());
        deudaInteresado.setAbono(BigDecimal.ZERO);
        deudaInteresado.setEstado(EstadoEnum.ACT.name());

        deudaInteresadoDAO.save(deudaInteresado);
    }

    @Override
    public ContenidoCarta findContenidoCartaByCodigo(ContenidoCartaEnum contenidoCartaEnum) {
        return contenidoCartaDAO.findByCodigoEnum(contenidoCartaEnum);
    }

    @Override
    public Date findUltimaFechaInscripciones(CicloPostula ciclo) {
        List<EventoCiclo> eventosInscripciones = eventoCicloDAO.allByCodesEventosCiclo(Arrays.asList("INSC", "OTR"), ciclo);
        Date ultimaFecha = eventosInscripciones.get(0).getFechaFin();
        for (EventoCiclo evento : eventosInscripciones) {
            if (evento.getFechaFin().compareTo(ultimaFecha) > 0) {
                ultimaFecha = evento.getFechaFin();
            }
        }
        return ultimaFecha;
    }

    @Override
    public Date findFechaExamen(CicloPostula ciclo) {
        List<EventoCiclo> eventosInscripciones = eventoCicloDAO.allByCodesEventosCiclo(Arrays.asList("EXAM"), ciclo);
        return eventosInscripciones.get(0).getFechaInicio();
    }

}
