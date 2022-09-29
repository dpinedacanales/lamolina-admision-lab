package edu.pe.lamolina.admision.controller.admision.inscripcion.postulante.cambios;

import edu.pe.lamolina.admision.config.DespliegueConfig;
import edu.pe.lamolina.admision.controller.admision.inscripcion.postulante.PostulanteService;
import edu.pe.lamolina.admision.dao.finanzas.AcreenciaDAO;
import edu.pe.lamolina.admision.dao.finanzas.CampagnaDescuentoDAO;
import edu.pe.lamolina.admision.dao.finanzas.ConceptoPagoDAO;
import edu.pe.lamolina.admision.dao.finanzas.ConceptoPrecioDAO;
import edu.pe.lamolina.admision.dao.finanzas.CuentaBancariaDAO;
import edu.pe.lamolina.admision.dao.finanzas.DeudaInteresadoDAO;
import edu.pe.lamolina.admision.dao.finanzas.ItemDeudaInteresadoDAO;
import edu.pe.lamolina.admision.dao.general.ColegioDAO;
import edu.pe.lamolina.admision.dao.general.OficinaDAO;
import edu.pe.lamolina.admision.dao.general.PaisDAO;
import edu.pe.lamolina.admision.dao.general.PersonaDAO;
import edu.pe.lamolina.admision.dao.general.UniversidadDAO;
import edu.pe.lamolina.admision.dao.inscripcion.CarreraPostulaDAO;
import edu.pe.lamolina.admision.dao.inscripcion.DescuentoExamenDAO;
import edu.pe.lamolina.admision.dao.inscripcion.EventoCicloDAO;
import edu.pe.lamolina.admision.dao.inscripcion.EventoDAO;
import edu.pe.lamolina.admision.dao.inscripcion.ModalidadIngresoCicloDAO;
import edu.pe.lamolina.admision.dao.inscripcion.ModalidadIngresoDAO;
import edu.pe.lamolina.admision.dao.inscripcion.OpcionCarreraDAO;
import edu.pe.lamolina.admision.dao.inscripcion.PostulanteDAO;
import edu.pe.lamolina.admision.dao.inscripcion.PrelamolinaDAO;
import edu.pe.lamolina.admision.dao.inscripcion.SolicitudCambioCarreraDAO;
import edu.pe.lamolina.admision.dao.inscripcion.SolicitudCambioInfoDAO;
import edu.pe.lamolina.admision.dao.inscripcion.TipoCambioInfoDAO;
import edu.pe.lamolina.admision.dao.vacantes.VacanteCarreraDAO;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.constantines.AdmisionConstantine;
import static pe.edu.lamolina.model.constantines.AdmisionConstantine.CODE_DIFERENCIA_PRECIO;
import static pe.edu.lamolina.model.constantines.AdmisionConstantine.CODE_EXTEMPORANEO;
import pe.edu.lamolina.model.enums.DescuentoEnum;
import pe.edu.lamolina.model.enums.DeudaEstadoEnum;
import static pe.edu.lamolina.model.enums.DeudaEstadoEnum.ACT;
import static pe.edu.lamolina.model.enums.DeudaEstadoEnum.PAG;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.EventoEnum;
import pe.edu.lamolina.model.enums.MotivoDescuemtoEnum;
import pe.edu.lamolina.model.enums.NombreTablasEnum;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.enums.PostulanteEstadoEnum;
import pe.edu.lamolina.model.enums.SolicitudCambioInfoEstadoEnum;
import pe.edu.lamolina.model.enums.TipoCambioInfoEnum;
import static pe.edu.lamolina.model.enums.TipoCambioInfoEnum.CDATGEN;
import static pe.edu.lamolina.model.enums.TipoCambioInfoEnum.CDIFEMOD;
import static pe.edu.lamolina.model.enums.TipoCambioInfoEnum.CMODAAI;
import static pe.edu.lamolina.model.enums.TipoCambioInfoEnum.CMODADI;
import static pe.edu.lamolina.model.enums.TipoCambioInfoEnum.COPCIONAI;
import static pe.edu.lamolina.model.enums.TipoCambioInfoEnum.COPCIONDI;
import pe.edu.lamolina.model.enums.TipoGestionEnum;
import pe.edu.lamolina.model.finanzas.Acreencia;
import pe.edu.lamolina.model.finanzas.CampagnaDescuento;
import pe.edu.lamolina.model.finanzas.ConceptoPago;
import pe.edu.lamolina.model.finanzas.ConceptoPrecio;
import pe.edu.lamolina.model.finanzas.DeudaInteresado;
import pe.edu.lamolina.model.finanzas.ItemDeudaInteresado;
import pe.edu.lamolina.model.finanzas.SolicitudCambioCarrera;
import pe.edu.lamolina.model.finanzas.SolicitudCambioInfo;
import pe.edu.lamolina.model.finanzas.TipoCambioInfo;
import pe.edu.lamolina.model.general.Colegio;
import pe.edu.lamolina.model.general.GestionColegio;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.Universidad;
import pe.edu.lamolina.model.inscripcion.CarreraPostula;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.DescuentoExamen;
import pe.edu.lamolina.model.inscripcion.Evento;
import pe.edu.lamolina.model.inscripcion.EventoCiclo;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.model.inscripcion.ModalidadIngresoCiclo;
import pe.edu.lamolina.model.inscripcion.OpcionCarrera;
import pe.edu.lamolina.model.inscripcion.Postulante;
import pe.edu.lamolina.model.inscripcion.Prelamolina;
import pe.edu.lamolina.model.vacantes.VacanteCarrera;
import static pe.edu.lamolina.model.enums.TipoCambioInfoEnum.CCOLEUNIAI;
import pe.edu.lamolina.model.general.Pais;

@Service
@Transactional(readOnly = true)
public class CambioServiceImp implements CambioService {

    @Autowired
    PostulanteDAO postulanteDAO;
    @Autowired
    ModalidadIngresoCicloDAO modalidadIngresoCicloDAO;
    @Autowired
    OpcionCarreraDAO opcionCarreraDAO;
    @Autowired
    CarreraPostulaDAO carreraPostulaDAO;
    @Autowired
    PrelamolinaDAO prelamolinaDAO;
    @Autowired
    EventoCicloDAO eventoCicloDAO;
    @Autowired
    ModalidadIngresoDAO modalidadIngresoDAO;
    @Autowired
    ConceptoPrecioDAO conceptoPrecioDAO;
    @Autowired
    SolicitudCambioInfoDAO solicitudCambioInfoDAO;
    @Autowired
    ConceptoPagoDAO conceptoPagoDAO;
    @Autowired
    UniversidadDAO universidadDAO;
    @Autowired
    ColegioDAO colegioDAO;
    @Autowired
    DeudaInteresadoDAO deudaInteresadoDAO;
    @Autowired
    CuentaBancariaDAO cuentaBancariaDAO;
    @Autowired
    ItemDeudaInteresadoDAO itemDeudaInteresadoDAO;
    @Autowired
    AcreenciaDAO acreenciaDAO;
    @Autowired
    OficinaDAO oficinaDAO;
    @Autowired
    VacanteCarreraDAO vacanteCarreraDAO;
    @Autowired
    PersonaDAO personaDAO;
    @Autowired
    EventoDAO eventoDAO;
    @Autowired
    CampagnaDescuentoDAO campagnaDescuentoDAO;
    @Autowired
    DescuentoExamenDAO descuentoExamenDAO;
    @Autowired
    SolicitudCambioCarreraDAO solicitudCambioCarreraDAO;
    @Autowired
    TipoCambioInfoDAO tipoCambioInfoDAO;

    @Autowired
    DespliegueConfig despliegueConfig;
    @Autowired
    PostulanteService postulanteService;
    @Autowired
    PaisDAO paisDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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

    private Prelamolina findNoIngresantePrelamolina(Postulante postulante, ModalidadIngreso modalidadPostula, CicloPostula ciclo) {
        if (modalidadPostula != null) {
            if (!modalidadPostula.isConcursoOrdinario()) {
                return null;
            }
        }

        List<Prelamolina> preLamolinas = prelamolinaDAO.allByDocumento(postulante.getPersona(), ciclo);
        if (preLamolinas.isEmpty()) {
            return null;
        }

        Collections.sort(preLamolinas, new Prelamolina.CompareDescuento());
        for (Prelamolina preLamolina : preLamolinas) {
            if (preLamolina.getEsIngresante() == 0 || (preLamolina.getEsIngresante() == 1 && preLamolina.getEstadoEnum() == PostulanteEstadoEnum.REN)) {
                return preLamolina;
            }
        }

        return null;
    }

    private boolean existeEvento(String codigo, List<EventoCiclo> eventos) {
        for (EventoCiclo eventoCiclo : eventos) {
            Evento evento = eventoCiclo.getEvento();
            if (evento.getCodigo().equals(codigo)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<ModalidadIngreso> allModalidadesByCiclo(CicloPostula ciclo) {
        List<EventoCiclo> eventos = eventoCicloDAO.allByFechaCiclo(new Date(), ciclo);
        if (eventos.isEmpty()) {
            return new ArrayList();
        }

        List<ModalidadIngreso> modalidades = modalidadIngresoDAO.allByCiclo(ciclo);
        filtrarModalidadesByEvento(modalidades, ciclo);

        Map<Long, ModalidadIngreso> mapModalidades = new LinkedHashMap();
        modalidades.stream().forEach((modalidad) -> {
            modalidad.setModalidadInferior(new ArrayList());
            mapModalidades.put(modalidad.getId(), modalidad);
        });

        List<ModalidadIngreso> superiores = new ArrayList();

        modalidades.stream().forEach((modalidad) -> {
            ModalidadIngreso superior = modalidad.getModalidadSuperior();
            if (!(superior == null)) {
                superior = mapModalidades.get(superior.getId());
                modalidad.setModalidadSuperior(superior);
                superior.getModalidadInferior().add(modalidad);
                superiores.add(superior);
            }
        });

        superiores.stream().forEach((superior) -> {
            modalidades.remove(superior);
        });
        return modalidades;
    }

    private void filtrarModalidadesByEvento(List<ModalidadIngreso> modalidades, CicloPostula ciclo) {
        List<ModalidadIngreso> noUtilizables = new ArrayList();
        List<EventoCiclo> eventos = eventoCicloDAO.allByFechaCiclo(new Date(), ciclo);
        for (ModalidadIngreso modalidad : modalidades) {
            boolean utilizable = false;
            if (modalidad.isConcursoOrdinario()) {
                if (existeEvento(EventoEnum.INSC.name(), eventos)) {
                    utilizable = true;
                }
                if (!utilizable && existeEvento(EventoEnum.EXTM.name(), eventos)) {
                    utilizable = true;
                }
                if (!utilizable) {
                    noUtilizables.add(modalidad);
                }
            }
            if (modalidad.isOtrasModalidades()) {
                if (existeEvento(EventoEnum.OTR.name(), eventos)) {
                    utilizable = true;
                }
                if (!utilizable && existeEvento(EventoEnum.EXTM.name(), eventos)) {
                    utilizable = true;
                }
                if (!utilizable) {
                    noUtilizables.add(modalidad);
                }
            }
        }

        for (ModalidadIngreso noUtilizable : noUtilizables) {
            modalidades.remove(noUtilizable);
        }
    }

    @Override
    public ConceptoPrecio findPrecioByDataPostulante(
            Postulante postulante,
            ModalidadIngreso modalidadPostula,
            CicloPostula ciclo,
            Colegio colegio, String colegioExtranjero,
            Universidad universidad, String universidadExtranjera) {

        ModalidadIngresoCiclo modalidadCiclo = modalidadIngresoCicloDAO.findByModalidadIngresoCiclo(modalidadPostula, ciclo);
        modalidadPostula = modalidadCiclo.getModalidadIngreso();

        if (modalidadCiclo.getExoneradoPago() == 1) {
            return null;
        }
        Integer prioridad = null;

        if (modalidadCiclo.isRequiereColegioUniversidad()) {
            prioridad = -1;
        } else if (modalidadCiclo.isRequiereSoloUniversidad()) {
            prioridad = 1;
        } else {
            prioridad = 2;
        }
        logger.debug("prioirr {}", prioridad);
        if (colegio != null) {
            colegio = colegioDAO.find(colegio.getId());
            postulante.setColegioProcedencia(colegio);
        }
        Prelamolina cepre = findNoIngresantePrelamolina(postulante, modalidadPostula, ciclo);
        TipoGestionEnum tipo = getTipoGestionInstitucion(colegio, colegioExtranjero, universidad, universidadExtranjera, prioridad);

        ConceptoPago concepto;
        if (postulante.isColegioCoar() && modalidadCiclo.isTieneDescuentoCoar()) {
            concepto = findConceptoCOAR(modalidadPostula);
        } else if (cepre != null && modalidadPostula.isConcursoOrdinario()) {
            concepto = findConceptoCepre(tipo, modalidadPostula, cepre.getDescuento());
        } else {
            concepto = findConceptoBase(tipo, modalidadPostula, ciclo);
        }

        ConceptoPrecio precio = conceptoPrecioDAO.findByConceptoCiclo(concepto, ciclo);
        if (precio == null) {
            throw new PhobosException("No se pudo determinar el monto del pago del postulante");
        }

        return precio;
    }

    private TipoGestionEnum getTipoGestionInstitucion(Colegio colegio, String colegioExtranjero, Universidad universidad, String universidadExtranjera, Integer prioridad) {
        if (prioridad == -1 || prioridad == 1) {
            logger.debug("prioridad {}", prioridad);
            if (universidad != null) {
                logger.debug("universidad {}", universidad);
                Universidad uniDB = universidadDAO.find(universidad.getId());
                return uniDB.getTipoGestion();
            }
            if (!StringUtils.isBlank(universidadExtranjera)) {
                logger.debug("universidadExtranjera {}", universidadExtranjera);
                return TipoGestionEnum.PRIV;
            }
        }

        if (colegio != null) {
            Colegio coleDB = colegioDAO.find(colegio.getId());
            GestionColegio gestion = coleDB.getGestion();
            if (gestion.getNombre().startsWith("Privada")) {
                return TipoGestionEnum.PRIV;
            }
            if (gestion.getNombre().startsWith("Pública")) {
                return TipoGestionEnum.PUB;
            }
            return TipoGestionEnum.INDEF;
        }
        if (!StringUtils.isBlank(colegioExtranjero)) {
            return TipoGestionEnum.PRIV;
        }

        return TipoGestionEnum.INDEF;
    }

    private ConceptoPago findConceptoCepre(TipoGestionEnum tipo, ModalidadIngreso modalidad, int descuento) {
        List<ConceptoPago> conceptosPago = conceptoPagoDAO.allByModalidad(modalidad);
        if (conceptosPago.isEmpty() && modalidad.getModalidadSuperior() != null) {
            conceptosPago = conceptoPagoDAO.allByModalidad(modalidad.getModalidadSuperior());
        }

        ConceptoPago concepto = null;
        for (ConceptoPago cp : conceptosPago) {
            if (cp.getConceptoOrigen() != null
                    && tipo.equals(cp.getTipoGestion())
                    && cp.getDescuento() == descuento
                    && cp.getAmbitoDescuento().equals("CEPRE")) {
                concepto = cp;
            }
        }

        if (concepto == null) {
            throw new PhobosException("No se pudo determinar los conceptos de pagos del postulante");
        }

        return concepto;
    }

    private ConceptoPago findConceptoCOAR(ModalidadIngreso modalidad) {
        List<ConceptoPago> conceptosPago = conceptoPagoDAO.allByModalidad(modalidad);
        if (conceptosPago.isEmpty() && modalidad.getModalidadSuperior() != null) {
            conceptosPago = conceptoPagoDAO.allByModalidad(modalidad.getModalidadSuperior());
        }

        ConceptoPago concepto = null;
        for (ConceptoPago cp : conceptosPago) {
            if (cp.getConceptoOrigen() != null && "COAR".equals(cp.getAmbitoDescuento())) {
                concepto = cp;
            }
        }

        if (concepto == null && modalidad.getModalidadSuperior() != null) {
            concepto = findConceptoCOAR(modalidad.getModalidadSuperior());
        }

        if (concepto == null) {
            throw new PhobosException("No se pudo determinar los conceptos de pagos del postulante");
        }

        return concepto;
    }

    private ConceptoPago findConceptoBase(TipoGestionEnum tipo, ModalidadIngreso modalidad, CicloPostula cicloPostula) {
        System.out.println("modalidad == " + modalidad.getCodigo());
        System.out.println("TipoGestionEnum == " + tipo.getValue());
        System.out.println("cicloPostula == " + cicloPostula.getId());

        List<ConceptoPago> conceptosPago = conceptoPagoDAO.allByModalidadCiclo(modalidad, cicloPostula);
        if (conceptosPago.isEmpty() && modalidad.getModalidadSuperior() != null) {
            conceptosPago = conceptoPagoDAO.allByModalidadCiclo(modalidad.getModalidadSuperior(), cicloPostula);
        }

        ConceptoPago concepto = null;
        for (ConceptoPago cp : conceptosPago) {
            if (cp.getCodigo().equals(AdmisionConstantine.CODE_SIN_DESCRIPCION)) {
                continue;
            }

            if (cp.getConceptoOrigen() == null && tipo.equals(cp.getTipoGestion()) && !"COAR".equals(cp.getAmbitoDescuento())) {
                concepto = cp;
            }
        }

        if (concepto == null) {
            for (ConceptoPago cp : conceptosPago) {
                if (cp.getCodigo().equals(AdmisionConstantine.CODE_SIN_DESCRIPCION)) {
                    continue;
                }

                if (cp.getConceptoOrigen() == null && cp.getTipoGestion() == TipoGestionEnum.AMB && !"COAR".equals(cp.getAmbitoDescuento())) {
                    concepto = cp;
                }
            }
        }
        if (concepto == null) {
            for (ConceptoPago cp : conceptosPago) {
                if (cp.getCodigo().equals(AdmisionConstantine.CODE_SIN_DESCRIPCION)) {
                    continue;
                }

                if (cp.getTipoGestion() == TipoGestionEnum.AMB && !"COAR".equals(cp.getAmbitoDescuento())) {
                    concepto = cp;
                }
            }
        }

        if (concepto == null && modalidad.getModalidadSuperior() != null) {
            concepto = findConceptoBase(tipo, modalidad.getModalidadSuperior(), cicloPostula);
        }

        if (concepto == null) {
            throw new PhobosException("No se pudo determinar los conceptos de pagos del postulante");
        }

        return concepto;
    }

    @Override
    public Postulante findPostulanteActivoByInteresado(Interesado interesado) {
        return postulanteDAO.findActivoByInteresadoSimple(interesado);
    }

    @Override
    public List<ConceptoPrecio> allConceptoPrecioCambiosByCiclo(Postulante postulante, CicloPostula ciclo) {

        Postulante postulanteDB = findPostulante(postulante);
        DeudaInteresado deudaPagada = findDeudaPagada(postulanteDB, ciclo);

        Date today = new Date();

        EventoCiclo eventoExamen = eventoCicloDAO.findActive(ciclo, today, EventoEnum.EXAM);
        Integer ai = deudaPagada == null ? 1 : 0;
        Integer ae = eventoExamen != null ? 1 : 0;

        List<ConceptoPrecio> cptosCepre = new ArrayList();
        List<ConceptoPrecio> cptos = conceptoPrecioDAO.allCambiosByCiclo(ciclo, ai, ae);

        if (!postulanteDB.getModalidadIngreso().isPreLaMolina()) {
            for (ConceptoPrecio cpto : cptos) {
                cptosCepre.add(cpto);
            }
            return cptosCepre;
        }
        for (ConceptoPrecio cpto : cptos) {
            if (!Arrays.asList(CMODAAI, CMODADI, COPCIONAI, COPCIONDI).contains(cpto.getTipoCambioInfo().getCodigoEnum())) {
                cptosCepre.add(cpto);
            }
        }
        return cptosCepre;
    }

    @Override
    public DeudaInteresado findDeudaPagada(Postulante postulante, CicloPostula ciclo) {
        List<DeudaInteresado> deudasPagadas = deudaInteresadoDAO.allDeudaPagada(postulante);
        if (deudasPagadas.isEmpty()) {
            return null;
        }

        DeudaInteresado deuda = new DeudaInteresado();
        deuda.setAbono(BigDecimal.ZERO);
        deuda.setMonto(BigDecimal.ZERO);

        List<ItemDeudaInteresado> itemsPagados = itemDeudaInteresadoDAO.allByDeudasInteresadoEstados(deudasPagadas, Arrays.asList(PAG, ACT));

        boolean existeInscripcion = false;
        BigDecimal dscto = BigDecimal.ZERO;
        for (ItemDeudaInteresado item : itemsPagados) {
            if (item.getTipo().equals("RESTA")) {
                dscto = dscto.add(item.getMonto());
            }
            if (item.getConceptoPrecio() == null) {
                continue;
            }
            if (item.getConceptoPrecio().getConceptoPago().getModalidadIngreso() == null) {
                continue;
            }
            deuda.setMonto(deuda.getMonto().add(item.getMonto()));
            deuda.setAbono(deuda.getAbono().add(item.getMonto()));
            existeInscripcion = true;
        }

        if (existeInscripcion) {
            deuda.setMonto(deuda.getMonto().subtract(dscto));
            deuda.setAbono(deuda.getAbono().subtract(dscto));
        }

        for (ItemDeudaInteresado item : itemsPagados) {
            if (item.getConceptoPrecio() == null) {
                continue;
            }
            ConceptoPago cptoDiferencia = item.getConceptoPrecio().getConceptoPago();
            if (cptoDiferencia != null && cptoDiferencia.getCodigo().equals(CODE_DIFERENCIA_PRECIO)) {
                deuda.setMonto(deuda.getMonto().add(item.getMonto()));
                deuda.setAbono(deuda.getAbono().add(item.getMonto()));
                logger.debug("Pago Inscripción mediante Diferencia Modalidad");
                existeInscripcion = true;
            }
        }

        if (existeInscripcion) {
            return deuda;
        }

        ModalidadIngreso modalidad = postulante.getModalidadIngreso();
        ModalidadIngresoCiclo modalidadCiclo = modalidadIngresoCicloDAO.findByModalidadIngresoCiclo(modalidad, ciclo);
        if (modalidadCiclo.getExoneradoPago() == 1) {
            logger.debug("Deuda ZERO porque esta exonerado");
            return deuda;
        }

        return null;
    }

    @Override
    public List<ModalidadIngresoCiclo> allModalidadesCicloByCicloModalidades(CicloPostula ciclo, List<ModalidadIngreso> modalidades) {

        List<ConceptoPrecio> preciosModa = conceptoPrecioDAO.allModalidadByCicloPostula(ciclo);
        Map<Long, List<ConceptoPrecio>> mapPrecios = TypesUtil.convertListToMapList("conceptoPago.modalidadIngreso.id", preciosModa);

        List<ModalidadIngresoCiclo> modas = modalidadIngresoDAO.allByCicloModalidades(ciclo, modalidades);
        for (ModalidadIngresoCiclo moda : modas) {
            List<ConceptoPrecio> conceptos = mapPrecios.get(moda.getModalidadIngreso().getId());
            if (conceptos != null) {
                moda.setConceptoPrecio(conceptos);
            }
        }
        return modas;
    }

    private void revisarModalidadOpciones(Postulante postulanteDB, ModalidadIngreso modalidadForm, List<OpcionCarrera> opcionesForm, List<ConceptoPrecio> conceptos) {
        
        List<CarreraPostula> carrerasPostula = opcionesForm.stream()
                .map(x -> x.getCarreraPostula())
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(carrerasPostula)) {
            carrerasPostula = postulanteDB.getOpcionCarrera()
                    .stream().map(x -> x.getCarreraPostula()).collect(Collectors.toList());

        }

        for (ConceptoPrecio concepto : conceptos) {
            if (concepto.getId().longValue() == -1) {
                continue;
            }

            if (Arrays.asList(CMODAAI, CMODADI).contains(concepto.getTipoCambioInfo().getCodigoEnum())) {

                List<VacanteCarrera> vacCarrerasModalidad = vacanteCarreraDAO.allVacanteCarrera(modalidadForm, postulanteDB.getCicloPostula());

                if (vacCarrerasModalidad.isEmpty()) {
                    return;
                }

                Map<Long, CarreraPostula> mapCarreraVacantes = vacCarrerasModalidad.stream()
                        .collect(Collectors.toMap(x -> x.getCarreraPostula().getId(), x -> x.getCarreraPostula()));

                for (CarreraPostula carreraPostula : carrerasPostula) {
                    CarreraPostula carrOpc = mapCarreraVacantes.get(carreraPostula.getId());
                    Assert.isFalse(carrOpc == null, "Esta modalidad requiere cambio de opciones de carrera");
                }
            }
        }
    }

    @Override
    @Transactional
    public void saveCambioSolicitud(Postulante postulante, List<ConceptoPrecio> conceptos) {

        List<SolicitudCambioInfo> solicitudes = solicitudCambioInfoDAO.allByPostulanteEstado(postulante, EstadoEnum.ACT);
        if (!solicitudes.isEmpty()) {
            return;
        }

        DateTime todayDT = new DateTime();
        Oficina oficinaCap = oficinaDAO.findByCodigo(OficinaEnum.CAP.name());
        Postulante postulanteDB = findPostulante(postulante);

        ModalidadIngreso modalidad = conceptos.get(0).getModalidadIngreso();
        Colegio colegio = conceptos.get(0).getColegio() != null ? conceptos.get(0).getColegio() : postulanteDB.getColegioProcedencia();
        Universidad uni = conceptos.get(0).getUniversidad() != null ? conceptos.get(0).getUniversidad() : postulanteDB.getUniversidadProcedencia();
        String coleExtr = StringUtils.isBlank(conceptos.get(0).getColegioExtranjero()) ? postulanteDB.getColegioExtranjero() : conceptos.get(0).getColegioExtranjero();
        String uniExtr = StringUtils.isBlank(conceptos.get(0).getUniversidadExtranjera()) ? postulanteDB.getUniversidadExtranjera() : conceptos.get(0).getUniversidadExtranjera();
        List<OpcionCarrera> opciones = conceptos.get(0).getPostulante().getOpcionCarrera();
        Persona personaForm = conceptos.get(0).getPostulante().getPersona();
        Pais paisCole = conceptos.get(0).getPaisColegio();
        Pais paisUni = conceptos.get(0).getPaisUniversidad();

        List<String> coles = Arrays.asList("CCOLEUNIAI", "CCOLEUNIDI", "CCOLEUNIDE");
        List<String> modas = Arrays.asList("CMODAAI", "CMODADI");
        List<String> opcs = Arrays.asList("COPCIONAI", "COPCIONDI");

        this.revisarModalidadOpciones(postulanteDB, modalidad, opciones, conceptos);

        for (ConceptoPrecio concepto : conceptos) {
            if (concepto.getTipoCambioInfo().getId() == -1) {
                continue;
            }
            if (concepto.getTipoCambioInfo().getCodigoEnum() == CMODAAI) {
                if (postulanteDB.getModalidadIngresoCiclo().getExoneradoPago() == 0) {
                    ItemDeudaInteresado iDeudaIns = itemDeudaInteresadoDAO.findInscripcionByInteresado(postulanteDB.getInteresado());
                    eliminarDeudas(iDeudaIns);
                    List<ItemDeudaInteresado> descuentos = itemDeudaInteresadoDAO.allDescuentosByInteresado(postulanteDB.getInteresado());
                    for (ItemDeudaInteresado dscto : descuentos) {
                        dscto.setEstadoEnum(DeudaEstadoEnum.ANU);
                        dscto.setFechaDesactivacion(todayDT.toDate());
                        itemDeudaInteresadoDAO.update(dscto);
                    }
                } else {
                    logger.debug("Tiene PAGO EXONERADO en la Modalidad anterior");
                    logger.info("Tiene PAGO EXONERADO en la Modalidad anterior");
                }

                Postulante postulanteObj = new Postulante();
                postulanteObj.setId(postulanteDB.getId());
                postulanteObj.setModalidadIngreso(modalidad);
                postulanteObj.setCicloPostula(postulanteDB.getCicloPostula());
                postulanteObj.setInteresado(postulanteDB.getInteresado());
                postulanteObj.setImportePagar(postulanteDB.getImportePagar());
                postulanteObj.setPersona(postulanteDB.getPersona());
                postulanteObj.setColegioExtranjero(coleExtr);
                postulanteObj.setColegioProcedencia(colegio);
                postulanteObj.setUniversidadExtranjera(uniExtr);
                postulanteObj.setUniversidadProcedencia(uni);

                logger.debug("CHECK Generar nueva deuda ?");
                generarNuevaDeuda(postulanteObj);
            }
            if (concepto.getTipoCambioInfo().getCodigoEnum() == CCOLEUNIAI) {
                Integer prioridad = null;
                if (postulanteDB.getModalidadIngresoCiclo().isRequiereColegioUniversidad()) {
                    prioridad = -1;
                } else if (postulanteDB.getModalidadIngresoCiclo().isRequiereSoloUniversidad()) {
                    prioridad = 1;
                } else {
                    prioridad = 2;
                }

                paisCole = paisCole != null ? paisDAO.find(paisCole.getId()) : null;
                paisUni = paisUni != null ? paisDAO.find(paisUni.getId()) : null;
                if (paisCole != null && !paisCole.esPeru()) {
                    colegio = null;
                }
                if (paisUni != null && !paisUni.esPeru()) {
                    uni = null;
                }

                TipoGestionEnum tipoNew = getTipoGestionInstitucion(colegio, coleExtr, uni, uniExtr, prioridad);
                TipoGestionEnum tipoOld = getTipoGestionInstitucion(postulanteDB.getColegioProcedencia(), postulanteDB.getColegioExtranjero(),
                        postulanteDB.getUniversidadProcedencia(), postulante.getUniversidadExtranjera(), prioridad);
                logger.debug("TIPONEW {}", tipoNew.getValue());
                logger.debug("TIPOOLD {}", tipoOld.getValue());
                if (tipoNew != tipoOld) {

                    ItemDeudaInteresado iDeuda = itemDeudaInteresadoDAO.findInscripcionByInteresado(postulanteDB.getInteresado());
                    if (iDeuda != null) {
                        eliminarDeudas(iDeuda);
                        logger.debug("diferente?");
                        Postulante postulanteObj = new Postulante();
                        postulanteObj.setId(postulanteDB.getId());
                        postulanteObj.setModalidadIngreso(modalidad);
                        postulanteObj.setCicloPostula(postulanteDB.getCicloPostula());
                        postulanteObj.setInteresado(postulanteDB.getInteresado());
                        postulanteObj.setImportePagar(postulanteDB.getImportePagar());
                        postulanteObj.setPersona(postulanteDB.getPersona());
                        postulanteObj.setColegioExtranjero(coleExtr);
                        postulanteObj.setColegioProcedencia(colegio);
                        postulanteObj.setUniversidadExtranjera(uniExtr);
                        postulanteObj.setUniversidadProcedencia(uni);

                        logger.debug("CHECK Generar nueva deuda ?");
                        generarNuevaDeuda(postulanteObj);
                    } else {
                        logger.debug("No tiene una deuda pendiente, esta PAGADA");
                    }
                }
            }
        }

        List<DeudaInteresado> deudas = deudaInteresadoDAO.allByInteresadoEstado(postulante.getInteresado(), DeudaEstadoEnum.ACT);
        Map<Long, DeudaInteresado> mapDeudas = TypesUtil.convertListToMap("cuentaBancaria.id", deudas);
        logger.debug("Conceptos size {}", conceptos.size());
        ConceptoPago cptoPagoDiferencia = conceptoPagoDAO.findByCodigo(CODE_DIFERENCIA_PRECIO);
        ConceptoPrecio cptoPrecioDiferencia = conceptoPrecioDAO.findByCicloCodigoConceptoPago(postulanteDB.getCicloPostula(), CODE_DIFERENCIA_PRECIO);

        for (ConceptoPrecio concepto : conceptos) {
            if (concepto.getId() == -1) {
                TipoCambioInfo tipoDife = tipoCambioInfoDAO.findByEnum(CDIFEMOD);
                concepto.setConceptoPago(cptoPagoDiferencia);
                concepto.setTipoCambioInfo(tipoDife);
                concepto.setId(cptoPrecioDiferencia.getId());
            }

            DeudaInteresado deuda = mapDeudas.get(concepto.getConceptoPago().getCuentaBancaria().getId());
            if (deuda == null) {
                deuda = new DeudaInteresado();
                deuda.setAbono(BigDecimal.ZERO);
                deuda.setEstadoEnum(DeudaEstadoEnum.ACT);
                deuda.setPostulante(postulante);
                deuda.setInteresado(postulanteDB.getInteresado());
                deuda.setFechaRegistro(new Date());
                deuda.setMontoAnterior(BigDecimal.ZERO);
                deuda.setMonto(concepto.getMonto());
                deuda.setCuentaBancaria(concepto.getConceptoPago().getCuentaBancaria());
                deuda.setDescripcion("MODIFICACIÓN DE DATOS");
                deudaInteresadoDAO.save(deuda);
                mapDeudas.put(concepto.getConceptoPago().getCuentaBancaria().getId(), deuda);

            } else {
                logger.debug("DEUDA ID {}", deuda.getId());
                logger.debug("DEUDA ID {}", deuda.getEstado());
                deuda.setMonto(deuda.getMonto().add(concepto.getMonto()));
                deudaInteresadoDAO.update(deuda);

                Acreencia acreenciaDB = acreenciaDAO.findByCuentaAndTablaIns(deuda.getCuentaBancaria(), NombreTablasEnum.FIN_DEUDA_INTERESADO, deuda.getId(), DeudaEstadoEnum.DEU);

                if (acreenciaDB == null) {
                    logger.debug("No se encontró una acreencia activa para esta deuda");
                    throw new PhobosException("Hubo un error en la solicitud, comunícate con soporte");
                }
                acreenciaDB.setFechaAnulacion(todayDT.toDate());
                acreenciaDB.setEstadoEnum(DeudaEstadoEnum.ANU);
                acreenciaDAO.update(acreenciaDB);

            }

            SolicitudCambioInfo cambio = null;
            if (concepto.getTipoCambioInfo().getCodigoEnum() != CDIFEMOD) {
                cambio = new SolicitudCambioInfo();
                if (coles.contains(concepto.getTipoCambioInfo().getCodigo())) {
                    cambio.setColegioNuevo(colegio);
                    cambio.setUniversidadNueva(uni);
                    cambio.setColegioExtranjero(coleExtr);
                    cambio.setUniversidadExtranjera(uniExtr);
                    cambio.setUniversidadExtranjera(uniExtr);
                    cambio.setPaisColegio(conceptos.get(0).getPaisColegio());
                    cambio.setPaisUniversidad(conceptos.get(0).getPaisUniversidad());
                }
                if (modas.contains(concepto.getTipoCambioInfo().getCodigo())) {
                    ObjectUtil.eliminarAttrSinId(conceptos.get(0).getPostulante());
                    cambio.setModalidadIngresoNueva(modalidad);
                    cambio.setColegioNuevo(colegio);
                    cambio.setUniversidadNueva(uni);
                    cambio.setColegioExtranjero(coleExtr);
                    cambio.setUniversidadExtranjera(uniExtr);
                    cambio.setUniversidadExtranjera(uniExtr);
                    cambio.setPaisColegio(conceptos.get(0).getPaisColegio());
                    cambio.setPaisUniversidad(conceptos.get(0).getPaisUniversidad());
                    cambio.setYearEgresoColegio(conceptos.get(0).getPostulante().getYearEgresoColegio());
                    cambio.setGradoTitulo(conceptos.get(0).getPostulante().getGradoTitulo());
                    cambio.setGradoSecundaria(conceptos.get(0).getPostulante().getGradoSecundaria());
                    cambio.setModalidadSimulacion(conceptos.get(0).getPostulante().getModalidadSimulacion());
                }
                if (concepto.getTipoCambioInfo().getCodigoEnum() == CDATGEN) {

                    ObjectUtil.eliminarAttrSinId(personaForm);

                    String nombres = personaForm.getPrimerNombre().trim()
                            + (StringUtils.isBlank(personaForm.getSegundoNombre()) ? "" : " " + personaForm.getSegundoNombre().trim());
                    cambio.setNombres(nombres);
                    cambio.setCelular(personaForm.getCelular());
                    cambio.setDireccion(personaForm.getDireccion());
                    cambio.setEmail(personaForm.getEmail());
                    cambio.setFechaNacer(personaForm.getFechaNacer());
                    cambio.setMaterno(personaForm.getMaterno());
                    cambio.setNacionalidad(personaForm.getNacionalidad());
                    cambio.setPaisDomicilio(personaForm.getPaisDomicilio());
                    cambio.setPaisNacer(personaForm.getPaisNacer());
                    cambio.setPaterno(personaForm.getPaterno());
                    cambio.setPrimerNombre(personaForm.getPrimerNombre());
                    cambio.setSegundoNombre(personaForm.getSegundoNombre());
                    cambio.setSexo(personaForm.getSexo());
                    cambio.setTelefono(personaForm.getTelefono());
                    cambio.setUbicacionDomicilio(personaForm.getUbicacionDomicilio());
                    cambio.setUbicacionNacer(personaForm.getUbicacionNacer());
                }

                cambio.setEstado(EstadoEnum.ACT.name());
                cambio.setFechaRegistro(new Date());
                cambio.setPostulante(postulanteDB);
                cambio.setTipoCambioInfo(concepto.getTipoCambioInfo());
                solicitudCambioInfoDAO.save(cambio);
                if (opcs.contains(concepto.getTipoCambioInfo().getCodigo())) {
                    int prioridad = 1;
                    for (OpcionCarrera opci : opciones) {
                        SolicitudCambioCarrera cCarrera = new SolicitudCambioCarrera();
                        cCarrera.setCarreraPostula(opci.getCarreraPostula());
                        cCarrera.setSolicitudCambioInfo(cambio);
                        cCarrera.setPrioridad(prioridad);
                        cCarrera.setEsAnterior(0);
                        solicitudCambioCarreraDAO.save(cCarrera);
                        prioridad++;
                    }
                }
            } else {
                cambio = new SolicitudCambioInfo();
                cambio.setEstado(EstadoEnum.ACT.name());
                cambio.setFechaRegistro(new Date());
                cambio.setPostulante(postulanteDB);
                cambio.setTipoCambioInfo(concepto.getTipoCambioInfo());
                solicitudCambioInfoDAO.save(cambio);
                logger.debug("TIPO DIFERENCIA =)");
            }

            ItemDeudaInteresado itemDeuda = new ItemDeudaInteresado();
            itemDeuda.setDeudaInteresado(deuda);
            itemDeuda.setMonto(concepto.getMonto());
            itemDeuda.setEstado(EstadoEnum.ACT.name());
            itemDeuda.setFechaRegistro(new Date());
            itemDeuda.setTipo("SUMA");
            itemDeuda.setConceptoPrecio(concepto);
            itemDeuda.setSolicitudCambioInfo(cambio);
            itemDeudaInteresadoDAO.save(itemDeuda);

            List<ItemDeudaInteresado> itemsDeuda = Arrays.asList(itemDeuda);
            deuda.setDescripcion(postulanteService.createDescripcionDeuda(itemsDeuda));
            deudaInteresadoDAO.update(deuda);

            Date fechaVencimiento = fechaVencimientoByModalidad(postulanteDB.getModalidadIngreso(), postulanteDB.getCicloPostula());

            Acreencia acreencia = new Acreencia();
            acreencia.setCuentaBancaria(concepto.getConceptoPago().getCuentaBancaria());
            acreencia.setDescripcion(deuda.getDescripcion());
            acreencia.setEstadoEnum(DeudaEstadoEnum.DEU);
            acreencia.setFechaDocumento(todayDT.toDate());
            acreencia.setFechaVencimiento(fechaVencimiento);
            acreencia.setInstanciaTabla(deuda.getId());
            acreencia.setMonto(deuda.getMonto());
            acreencia.setOficina(oficinaCap);
            acreencia.setAbono(BigDecimal.ZERO);
            acreencia.setFechaRegistro(todayDT.toDate());
            acreencia.setPersona(postulante.getPersona());
            acreencia.setTablaEnum(NombreTablasEnum.FIN_DEUDA_INTERESADO);
            acreenciaDAO.save(acreencia);
            logger.debug("LLego al final? del bucle");
        }
        logger.debug("LLego al final de todo");

        postulanteService.sendEmailBoletaPagoAsync(postulanteDB, postulanteDB.getCicloPostula());
    }

    @Override
    public List<SolicitudCambioInfo> allSolicitudesByPostulante(Postulante postulante) {
        return solicitudCambioInfoDAO.allByPostulanteEstado(postulante, EstadoEnum.ACT);
    }

    @Override
    @Transactional
    public void anularSolicitud(Postulante postulante) {
        List<SolicitudCambioInfo> solicitudes = allSolicitudesByPostulante(postulante);

        if (solicitudes.isEmpty()) {
            throw new PhobosException("El postulante no tiene solicitudes pendientes");
        }

        List<ItemDeudaInteresado> itemsDeuda = itemDeudaInteresadoDAO.allBySolicitudesCambioInfo(solicitudes);
        List<Long> instancias = itemsDeuda.stream().map(d -> d.getDeudaInteresado().getId()).collect(Collectors.toList());
        List<Acreencia> acreencias = acreenciaDAO.allByInstanciasEstado(instancias, DeudaEstadoEnum.DEU);
        Map<Long, Acreencia> mapAcreencias = TypesUtil.convertListToMap("instanciaTabla", acreencias);
        List<SolicitudCambioCarrera> solCambioCarrera = solicitudCambioCarreraDAO.allBySolicitudes(solicitudes);
        logger.debug("acreencias {}", acreencias.size());

        if (solicitudes.size() != itemsDeuda.size()) {
            logger.debug("Los datos no coinciden, son {} solicitudes y hay {} itemsdeuda", solicitudes.size(), itemsDeuda.size());
            throw new PhobosException("Datos incongruentes comuníquese con el soporte.");
        }

        DateTime todayDT = new DateTime();
        Oficina oficinaCap = oficinaDAO.findByCodigo(OficinaEnum.CAP.name());
        for (ItemDeudaInteresado item : itemsDeuda) {
            DeudaInteresado deuda = item.getDeudaInteresado();
            BigDecimal monto = deuda.getMonto().subtract(item.getMonto());
            if (monto.compareTo(BigDecimal.ZERO) < 0) {
                logger.debug("El resultado del nuevo monto actualizado es negativo, nos están timando...");
                throw new PhobosException("Hubo un error en la anulación");
            }
            Acreencia acreencia = mapAcreencias.get(deuda.getId());
            logger.debug("acreencia.id = {} va ser anulada", acreencia.getId());
            if (monto.compareTo(BigDecimal.ZERO) == 0) {
                deuda.setEstadoEnum(DeudaEstadoEnum.ANU);
            }
            deuda.setMonto(monto);
            deudaInteresadoDAO.update(deuda);

            acreencia.setFechaAnulacion(todayDT.toDate());
            acreencia.setEstadoEnum(DeudaEstadoEnum.ANU);
            acreenciaDAO.update(acreencia);

            item.setEstado(EstadoEnum.ANU.name());
            itemDeudaInteresadoDAO.update(item);

        }

        List<DeudaInteresado> deudas = deudaInteresadoDAO.all(instancias);
        Postulante postulanteDB = findPostulante(postulante);
        Date fechaVencimiento = fechaVencimientoByModalidad(postulanteDB.getModalidadIngreso(), postulanteDB.getCicloPostula());

        for (DeudaInteresado deuda : deudas) {
            if (deuda.getMonto().compareTo(BigDecimal.ZERO) == 0 || deuda.getEstadoEnum() == DeudaEstadoEnum.ANU) {
                continue;
            }
            Acreencia acreenciaNew = acreenciaDAO.findByCuentaAndTablaIns(deuda.getCuentaBancaria(), NombreTablasEnum.FIN_DEUDA_INTERESADO, deuda.getId(), DeudaEstadoEnum.DEU);
            if (acreenciaNew != null) {
                continue;
            }
            acreenciaNew = new Acreencia();
            acreenciaNew.setCuentaBancaria(deuda.getCuentaBancaria());
            acreenciaNew.setDescripcion(deuda.getDescripcion());
            acreenciaNew.setEstadoEnum(DeudaEstadoEnum.DEU);
            acreenciaNew.setFechaDocumento(todayDT.toDate());
            acreenciaNew.setFechaVencimiento(fechaVencimiento);
            acreenciaNew.setInstanciaTabla(deuda.getId());
            acreenciaNew.setMonto(deuda.getMonto());
            acreenciaNew.setOficina(oficinaCap);
            acreenciaNew.setAbono(BigDecimal.ZERO);
            acreenciaNew.setFechaRegistro(todayDT.toDate());
            acreenciaNew.setPersona(postulante.getPersona());
            acreenciaNew.setTablaEnum(NombreTablasEnum.FIN_DEUDA_INTERESADO);
            acreenciaDAO.save(acreenciaNew);
        }

        for (SolicitudCambioInfo solicitud : solicitudes) {
            if (solicitud.getTipoCambioInfo().getCodigoEnum() == CMODAAI) {
                ItemDeudaInteresado iDeuda = itemDeudaInteresadoDAO.findInscripcionByInteresado(postulanteDB.getInteresado());
                if (iDeuda != null) {
                    eliminarDeudas(iDeuda);
                }
                generarNuevaDeuda(postulanteDB);
            }
            if (solicitud.getTipoCambioInfo().getCodigoEnum() == CCOLEUNIAI) {
                if (!postulanteDB.isInscrito()) {

                    Integer prioridad = null;
                    if (postulanteDB.getModalidadIngresoCiclo().isRequiereColegioUniversidad()) {
                        prioridad = -1;
                    } else if (postulanteDB.getModalidadIngresoCiclo().isRequiereSoloUniversidad()) {
                        prioridad = 1;
                    } else {
                        prioridad = 2;
                    }

                    TipoGestionEnum tipoNew = getTipoGestionInstitucion(solicitud.getColegioNuevo(), solicitud.getColegioExtranjero(),
                            solicitud.getUniversidadNueva(), solicitud.getUniversidadExtranjera(), prioridad);
                    TipoGestionEnum tipoOld = getTipoGestionInstitucion(postulanteDB.getColegioProcedencia(), postulanteDB.getColegioExtranjero(),
                            postulanteDB.getUniversidadProcedencia(), postulante.getUniversidadExtranjera(), prioridad);
                    logger.debug("TIPONEW {}", tipoNew.getValue());
                    logger.debug("TIPOOLD {}", tipoOld.getValue());
                    if (tipoNew != tipoOld) {
                        logger.debug("diferente?");

                        ItemDeudaInteresado iDeuda = itemDeudaInteresadoDAO.findInscripcionByInteresado(postulanteDB.getInteresado());
                        eliminarDeudas(iDeuda);
                        logger.debug("CHECK Generar nueva deuda ?");
                        generarNuevaDeuda(postulanteDB);
                    }
                }
            }

            solicitud.setEstado(EstadoEnum.ANU.name());
            solicitudCambioInfoDAO.update(solicitud);
        }

        for (SolicitudCambioCarrera ccarre : solCambioCarrera) {
            solicitudCambioCarreraDAO.delete(ccarre);
        }

    }

    @Override
    public List<SolicitudCambioInfo> allSolicitudesByPostulanteEstado(Postulante postulante, SolicitudCambioInfoEstadoEnum estado) {
        return solicitudCambioInfoDAO.allByPostulanteEstado(postulante, estado);
    }

    @Override
    public List<CarreraPostula> allCarreras(ModalidadIngreso modalidad, CicloPostula ciclo) {
        System.out.println("modalidad.id=" + ObjectUtil.getParentTree(modalidad, "id"));
        List<VacanteCarrera> vacCarrerasModalidad = vacanteCarreraDAO.allVacanteCarrera(modalidad, ciclo);
        System.out.println("vacCarrerasModalidad.size=" + vacCarrerasModalidad.size());
        if (vacCarrerasModalidad.isEmpty()) {
            return carreraPostulaDAO.allByCiclo(ciclo);
        }

        List<CarreraPostula> carrerasPostular = vacCarrerasModalidad.stream().map(x -> x.getCarreraPostula()).collect(Collectors.toList());
        System.out.println("carrerasPostular.size=" + carrerasPostular.size());
        return carrerasPostular;
    }

    @Override
    @Transactional
    public void saveCambios(Postulante postulante) {
        List<SolicitudCambioInfo> solicitudes = allSolicitudesByPostulanteEstado(postulante, SolicitudCambioInfoEstadoEnum.PEND);
        for (SolicitudCambioInfo solicitud : solicitudes) {
            if (solicitud.getTipoCambioInfo().getCodigoEnum() == TipoCambioInfoEnum.CDATGEN && postulante.getPersona() != null) {
                Persona personaDB = personaDAO.find(postulante.getPersona().getId());
                Persona personaForm = postulante.getPersona();
                personaDB.setPaterno(personaForm.getPaterno());
                personaDB.setMaterno(personaForm.getMaterno());
                personaDB.setPrimerNombre(personaForm.getPrimerNombre());
                personaDB.setSegundoNombre(personaForm.getSegundoNombre());
                personaDB.setTelefono(personaForm.getTelefono());
                personaDB.setCelular(personaForm.getCelular());
                personaDB.setEmail(personaForm.getEmail());
                personaDB.setDireccion(personaForm.getDireccion());
                personaDB.setPaisDomicilio(personaForm.getPaisDomicilio());
                personaDB.setPaisNacer(personaForm.getPaisNacer());
                personaDB.setSexo(personaForm.getSexo());
                if (personaForm.getPaisDomicilio().getCodigo().equals("PE")) {
                    personaDB.setUbicacionDomicilio(personaForm.getUbicacionDomicilio());
                }
                if (personaDB.getPaisNacer().getCodigo().equals("PE")) {
                    personaDB.setUbicacionNacer(personaForm.getUbicacionNacer());
                }
                personaDAO.save(personaDB);
                solicitud.setEstado(SolicitudCambioInfoEstadoEnum.COMP.name());
                solicitudCambioInfoDAO.update(solicitud);
            }
            if (solicitud.getTipoCambioInfo().getCodigoEnum() == TipoCambioInfoEnum.COPCIONAI
                    || solicitud.getTipoCambioInfo().getCodigoEnum() == TipoCambioInfoEnum.COPCIONDI) {
                List<OpcionCarrera> opcionesDB = opcionCarreraDAO.allByPostulante(postulante);
                List<OpcionCarrera> opcionesForm = postulante.getOpcionCarrera();
                for (OpcionCarrera opc : opcionesDB) {
                    opcionCarreraDAO.delete(opc);
                }
                int prio = 1;
                for (OpcionCarrera opc : opcionesForm) {
                    if (opc == null) {
                        continue;
                    }
                    opc.setId(null);
                    opc.setPostulante(postulante);
                    opc.setCarreraPostula(new CarreraPostula(opc.getId()));
                    opc.setPrioridad(prio);
                    opcionCarreraDAO.save(opc);
                    prio++;
                }
            }

        }
    }

    private String getDescripcionDeuda(String descripcion) {
        if (StringUtils.isBlank(descripcion)) {
            return "MODIFICACIÓN DE DATOS";
        }
        if (descripcion.contains("OTROS") || descripcion.contains("MODIFICACIÓN DE DATOS")) {
            return descripcion;
        }

        return descripcion + " Y OTROS";
    }

    private void generarNuevaDeuda(Postulante postulanteDB) {
        logger.debug("postulanteDB {}", postulanteDB.getId());
        logger.debug("getCicloPostula {}", postulanteDB.getCicloPostula().getId());
        logger.debug("MODALIDAD INGRESO {}", postulanteDB.getModalidadIngreso().getId());
        Date fechaVencimiento = fechaVencimientoByModalidad(postulanteDB.getModalidadIngreso(), postulanteDB.getCicloPostula());
        logger.debug("fechaVencimiento {}", fechaVencimiento);

        EventoCiclo eventoCiclo = findEventoExtemporaneo(postulanteDB.getCicloPostula());
        logger.debug("eventoCiclo {}", eventoCiclo.getId());
        crearDeudaDerechoInscripcion(postulanteDB, postulanteDB.getCicloPostula(), fechaVencimiento, eventoCiclo);
    }

    private Date fechaVencimientoByModalidad(ModalidadIngreso mi, CicloPostula cicloPostula) {
        if (mi.isPreLaMolina()) {
            EventoCiclo examen = findEventoExamen(cicloPostula);
            return new LocalDate(examen.getFechaFin()).dayOfMonth().withMaximumValue().toDate();
        } else {
            List<EventoCiclo> eventosInscripcion = eventoCicloDAO.allEventoInscripcionesByCiclo(cicloPostula);
            return eventosInscripcion.stream().filter(ev -> ev.getFechaFin() != null).map(ev -> ev.getFechaFin()).max(Date::compareTo).get();
        }
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

    private void crearDeudaDerechoInscripcion(Postulante postulante, CicloPostula ciclo, Date fechaVencimiento, EventoCiclo eventoCicloEx) {

        Date todayD = new Date();
        DateTime todayDT = new DateTime();

        logger.debug("MODALIDAD DE INGRESO {}", postulante.getModalidadIngreso().getId());

        CampagnaDescuento campDescuento = campagnaDescuentoDAO.findByCiclo(ciclo, postulante.getModalidadIngreso(), todayD, EstadoEnum.ACT);

        ConceptoPrecio precioOrigen = null;
        ConceptoPrecio precioModalidad = findPrecioByPostulante(postulante, ciclo);
        if (precioModalidad == null) {
            logger.debug("Precio de modalidad NULL - {}", postulante.getModalidadIngreso().getId());
            return;
        }
        logger.debug("precioModalidad {}", precioModalidad.getId());
        logger.debug("precioModalidad {}", precioModalidad.getMonto());

        List<ItemDeudaInteresado> itemsDeuda = new ArrayList();

        DescuentoExamen dscto = null;
        MotivoDescuemtoEnum motivoDscto = null;

        ConceptoPago conceptoModalidad = precioModalidad.getConceptoPago();
        ConceptoPago conceptOrigen = conceptoModalidad.getConceptoOrigen() != null ? conceptoModalidad.getConceptoOrigen() : conceptoModalidad;

        BigDecimal montoTotal = precioModalidad.getMonto();
        BigDecimal campDsc = BigDecimal.ZERO;

        if (campDescuento != null) {
            precioOrigen = conceptoPrecioDAO.findByConceptoCiclo(conceptOrigen, ciclo);
            BigDecimal mont = precioOrigen.getMonto();
            campDsc = campDescuento.getPorcentaje();
            BigDecimal por = new BigDecimal(100).subtract(campDescuento.getPorcentaje());
            montoTotal = mont.multiply(por).divide(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);
            BigDecimal montDesc = mont.subtract(montoTotal).setScale(2, RoundingMode.HALF_DOWN);
            motivoDscto = MotivoDescuemtoEnum.CAMP;

            logger.debug("MONTO ORIGINAL {}", mont);
            logger.debug("MONTO DESCUENTO {}", montDesc);
            logger.debug("MONTO TOTAL {}", montoTotal);

            ItemDeudaInteresado itemCampDesc = new ItemDeudaInteresado();
            itemCampDesc.setTipo("RESTA");
            itemCampDesc.setCampagnaDescuento(campDescuento);
            itemCampDesc.setEstado(EstadoEnum.ACT.name());
            itemCampDesc.setMonto(montDesc);
            itemCampDesc.setFechaRegistro(new Date());
            itemsDeuda.add(itemCampDesc);
        }

        logger.debug("CONCEPTO DESCUENTO {}", conceptoModalidad.getDescuento());
        logger.debug("campDsc.intValue() {}", campDsc.intValue());

        if (conceptoModalidad.getDescuento() > 0 && conceptoModalidad.getDescuento() > campDsc.intValue()) {
            montoTotal = precioModalidad.getMonto();
            precioOrigen = conceptoPrecioDAO.findByConceptoCiclo(conceptOrigen, ciclo);
            dscto = findDescuentoPublico(conceptoModalidad.getDescuento());
            motivoDscto = findMotivoDescuento(conceptoModalidad);
            BigDecimal montDesc = precioOrigen.getMonto().subtract(montoTotal);

            logger.debug("MONTO ORIGINAL {}", precioOrigen.getMonto());
            logger.debug("MONTO DESCUENTO {}", montDesc);
            logger.debug("MONTO TOTAL {}", montoTotal);

            itemsDeuda = new ArrayList();
            ItemDeudaInteresado itemDesc = new ItemDeudaInteresado();
            itemDesc.setTipo("RESTA");
            itemDesc.setEstado(EstadoEnum.ACT.name());
            itemDesc.setMonto(montDesc);
            itemDesc.setFechaRegistro(new Date());
            itemDesc.setDescuentoExamen(dscto);
            itemsDeuda.add(itemDesc);

            logger.debug("SIZE ARRAY {}", itemsDeuda.size());
        }

        BigDecimal montoPenalidad = BigDecimal.ZERO;

        Evento ev = eventoDAO.findByCode(EventoEnum.CEPRE.name());
        List<EventoCiclo> eventos = eventoCicloDAO.allByEventoCiclo(ev, ciclo);
        List<Prelamolina> preLamolinas = prelamolinaDAO.allByDocumentoEsIngresante(postulante.getPersona(), ciclo, 1);
        boolean exonerado = false;

        ConceptoPrecio precioExt = conceptoPrecioDAO.findByCicloCodigoConceptoPago(ciclo, CODE_EXTEMPORANEO);

        ItemDeudaInteresado itemEx = itemDeudaInteresadoDAO.findByInteresadoConceptoPrecioEstado(postulante.getInteresado(), precioExt, DeudaEstadoEnum.ACT);
        if (itemEx != null) {
            exonerado = true;
        }
        if (!preLamolinas.isEmpty()) {
            logger.debug("es pre");

            boolean regOn = true;
            int nEvnt = 1;
            for (Prelamolina pre : preLamolinas) {
                if (pre.getNumeroCiclo() == 0 || pre.getNumeroCiclo() == 1) {
                    exonerado = true;
                    logger.debug("exonerador por tener n-ciclo 0 o 1 ");
                }
            }
            for (EventoCiclo evento : eventos) {
                logger.debug("buscando dentro de eventos");
                for (Prelamolina pre : preLamolinas) {
                    logger.debug("Numero de ciclo {}", pre.getNumeroCiclo());
                    if (todayD.compareTo(evento.getFechaInicio()) >= 0
                            && todayD.compareTo(evento.getFechaFin()) <= 0
                            && nEvnt == 1 && pre.getNumeroCiclo() != 0) {
                        exonerado = true;
                    }
                    logger.debug("nEvnt = {}", nEvnt);
                    logger.debug("fecha inicio {}", evento.getFechaInicio());
                    logger.debug("CHECK 1 {}", nEvnt == 1);
                    logger.debug("CHECK 2 {}", todayD.compareTo(evento.getFechaInicio()) >= 0);
                    logger.debug("CHECK 3 {}", todayD.compareTo(evento.getFechaFin()) <= 0);
                    logger.debug("CHECK 4 {}", pre.getNumeroCiclo() == 0);
                    if (nEvnt == 1 && todayD.compareTo(evento.getFechaInicio()) >= 0
                            && todayD.compareTo(evento.getFechaFin()) <= 0
                            && pre.getNumeroCiclo() == 0) {
                        regOn = false;
                    }
                }
                nEvnt++;
            }
            if (!regOn) {
                throw new PhobosException("No puede realizarse la Inscripción ya que esta fuera de fecha");
            }
        } else {
            List<Prelamolina> cepreNoIngresantes = prelamolinaDAO.allByDocumentoEsIngresante(postulante.getPersona(), ciclo, 0);
            if (!cepreNoIngresantes.isEmpty()) {
                logger.debug("es cepre no-ingresante");
                for (Prelamolina noIngresante : cepreNoIngresantes) {
                    if (noIngresante.getNumeroCiclo() == 0 || noIngresante.getNumeroCiclo() == 1) {
                        exonerado = true;
                        logger.debug("exonerador por tener n-ciclo 0 o 1 ");
                    }
                }
            }
        }

        logger.debug("exonerado == {}", exonerado);
        logger.debug("eventoCicloEx == {}", eventoCicloEx == null);

        if (!exonerado && !preLamolinas.isEmpty()) {
            if (eventoCicloEx == null || todayD.before(eventoCicloEx.getFechaInicio())) {

                ConceptoPrecio precio = findConceptoExtemporaneoByCiclo(ciclo);
                montoPenalidad = montoPenalidad.add(precio.getMonto());

                DeudaInteresado deudaEx = new DeudaInteresado();

                deudaEx.setInteresado(postulante.getInteresado());
                deudaEx.setAbono(BigDecimal.ZERO);
                deudaEx.setMontoAnterior(BigDecimal.ZERO);
                deudaEx.setMonto(precio.getMonto());
                deudaEx.setFechaRegistro(todayDT.toDate());
                deudaEx.setEstadoEnum(DeudaEstadoEnum.ACT);
                deudaEx.setPostulante(postulante);
                deudaEx.setCuentaBancaria(precio.getConceptoPago().getCuentaBancaria());
                deudaInteresadoDAO.save(deudaEx);

                ItemDeudaInteresado itemDPena = new ItemDeudaInteresado();
                itemDPena.setTipo("SUMA");
                itemDPena.setEstado(EstadoEnum.ACT.name());
                itemDPena.setMonto(precio.getMonto());
                itemDPena.setFechaRegistro(new Date());
                itemDPena.setConceptoPrecio(precio);
                itemDPena.setDeudaInteresado(deudaEx);
                itemDeudaInteresadoDAO.save(itemDPena);

            }
        }

        if (eventoCicloEx != null) {

            if (todayD.after(eventoCicloEx.getFechaInicio()) && !exonerado) {
                ConceptoPrecio precio = findConceptoExtemporaneoByCiclo(ciclo);
                montoPenalidad = montoPenalidad.add(precio.getMonto());

                DeudaInteresado deudaEx = new DeudaInteresado();

                deudaEx.setInteresado(postulante.getInteresado());
                deudaEx.setAbono(BigDecimal.ZERO);
                deudaEx.setMontoAnterior(BigDecimal.ZERO);
                deudaEx.setMonto(precio.getMonto());
                deudaEx.setFechaRegistro(todayDT.toDate());
                deudaEx.setEstadoEnum(DeudaEstadoEnum.ACT);
                deudaEx.setPostulante(postulante);
                deudaEx.setCuentaBancaria(precio.getConceptoPago().getCuentaBancaria());
                deudaInteresadoDAO.save(deudaEx);

                ItemDeudaInteresado itemDPena = new ItemDeudaInteresado();
                itemDPena.setTipo("SUMA");
                itemDPena.setEstado(EstadoEnum.ACT.name());
                itemDPena.setMonto(precio.getMonto());
                itemDPena.setFechaRegistro(new Date());
                itemDPena.setConceptoPrecio(precio);
                itemDPena.setDeudaInteresado(deudaEx);
                itemDeudaInteresadoDAO.save(itemDPena);
            }
        }

        logger.debug("MONTO TOTAL A PAGAR {}", montoTotal);
        if (precioOrigen != null) {
            logger.debug("MONTO ANTERIOR A PAGAR {}", precioOrigen.getMonto());
        }

        DeudaInteresado deuda = deudaInteresadoDAO.findActivaByInteresadoCtaBanco(postulante.getInteresado(), precioModalidad.getConceptoPago().getCuentaBancaria());

        boolean esDeudaNueva = false;
        if (deuda == null) {
            deuda = new DeudaInteresado();
            deuda.setInteresado(postulante.getInteresado());
            deuda.setAbono(BigDecimal.ZERO);
            deuda.setMontoAnterior(BigDecimal.ZERO);
            deuda.setFechaRegistro(todayDT.toDate());
            deuda.setEstado(EstadoEnum.ACT.name());
            deuda.setPostulante(postulante);
            deuda.setCuentaBancaria(precioModalidad.getConceptoPago().getCuentaBancaria());
            deuda.setMonto(montoTotal);
            esDeudaNueva = true;
            deuda.setItemDeudaInteresado(new ArrayList());

        } else {
            deuda.setMonto(deuda.getMonto().add(montoTotal));
            List<ItemDeudaInteresado> itemsAntes = itemDeudaInteresadoDAO.allActivasByDeuda(deuda);
            deuda.setItemDeudaInteresado(itemsAntes);
            Acreencia acreencia = acreenciaDAO.findByCuentaAndTablaIns(deuda.getCuentaBancaria(), NombreTablasEnum.FIN_DEUDA_INTERESADO, deuda.getId(), DeudaEstadoEnum.DEU);
            if (acreencia != null) {
                acreencia.setFechaAnulacion(todayDT.toDate());
                acreencia.setEstadoEnum(DeudaEstadoEnum.ANU);
                acreenciaDAO.update(acreencia);
            }
        }

        logger.debug("postulante.getModalidadIngreso().getId() {}", postulante.getModalidadIngreso().getId());

        Postulante postulanteUpdt = postulanteDAO.find(postulante.getId());
        if (precioOrigen != null) {
            deuda.setConceptoAnterior(precioOrigen);
            deuda.setMontoAnterior(precioOrigen.getMonto());
            postulanteUpdt.setImporteDescuento(precioOrigen.getMonto().subtract(precioModalidad.getMonto()));
        }
        logger.debug("DESC {}", precioModalidad.getConceptoPago().getDescripcion());
        deuda.setDescripcion(precioModalidad.getConceptoPago().getDescripcion());
        if (esDeudaNueva) {
            deudaInteresadoDAO.save(deuda);
        } else {
            deudaInteresadoDAO.update(deuda);
        }

        ItemDeudaInteresado itemDeuda = new ItemDeudaInteresado();
        itemDeuda.setConceptoPrecio(precioModalidad);
        itemDeuda.setEstado(EstadoEnum.ACT.name());
        itemDeuda.setFechaRegistro(new Date());
        itemDeuda.setMonto(montoTotal);
        if (precioOrigen != null) {
            itemDeuda.setMonto(precioOrigen.getMonto());
        }
        itemDeuda.setTipo("SUMA");
        itemsDeuda.add(itemDeuda);

        for (ItemDeudaInteresado item : itemsDeuda) {
            item.setDeudaInteresado(deuda);
            itemDeudaInteresadoDAO.save(item);
        }
        List<ItemDeudaInteresado> itemsTotalDeuda = deuda.getItemDeudaInteresado();
        itemsTotalDeuda.addAll(itemsDeuda);
        deuda.setDescripcion(createDescripcionDeuda(deuda, itemsTotalDeuda));

        postulanteUpdt.setDescuentoExamen(dscto);
        postulanteUpdt.setMotivoDescuentoEnum(motivoDscto);
        postulanteUpdt.setImportePagar(precioModalidad.getMonto().add(postulante.getImportePagar()).add(montoPenalidad));
        logger.debug("postulanteUpdt.getModalidadIngreso().getId() {}", postulanteUpdt.getModalidadIngreso().getId());
        postulanteDAO.update(postulanteUpdt);

        Oficina oficinaCap = oficinaDAO.findByCodigo(OficinaEnum.CAP.name());

        Acreencia acreencia = new Acreencia();
        acreencia.setCuentaBancaria(conceptoModalidad.getCuentaBancaria());
        acreencia.setDescripcion(deuda.getDescripcion());
        acreencia.setEstadoEnum(DeudaEstadoEnum.DEU);
        acreencia.setFechaDocumento(todayDT.toDate());
        acreencia.setFechaVencimiento(fechaVencimiento);
        acreencia.setInstanciaTabla(deuda.getId());
        acreencia.setMonto(deuda.getMonto());
        acreencia.setOficina(oficinaCap);
        acreencia.setAbono(BigDecimal.ZERO);
        acreencia.setFechaRegistro(todayDT.toDate());
        acreencia.setPersona(postulante.getPersona());
        acreencia.setTablaEnum(NombreTablasEnum.FIN_DEUDA_INTERESADO);
        acreenciaDAO.save(acreencia);
    }

    private ConceptoPrecio findPrecioByPostulante(Postulante postulante, CicloPostula ciclo) {
        System.out.println("======================================================================================");
        logger.debug("postulante.getColegioProcedencia() {}", postulante.getColegioProcedencia());
        logger.debug("postulante.getColegioExtranjero() {}", postulante.getColegioExtranjero());
        logger.debug("postulante.getUniversidadProcedencia() {}", postulante.getUniversidadProcedencia());
        logger.debug("postulante.getUniversidadExtranjera() {}", postulante.getUniversidadExtranjera());
        logger.debug("");
        ConceptoPrecio precio = findPrecioByDataPostulante(
                postulante, postulante.getModalidadIngreso(), ciclo,
                postulante.getColegioProcedencia(), postulante.getColegioExtranjero(),
                postulante.getUniversidadProcedencia(), postulante.getUniversidadExtranjera()
        );

        logger.debug("PRECIO - L-980 {}", precio);
        if (precio != null) {
            logger.debug("PRECIO - L-980 {}", precio.getMonto());
            logger.debug("PRECIO - L-980 {}", precio.getId());
        }

        Prelamolina cepre = findNoIngresantePrelamolina(postulante, postulante.getModalidadIngreso(), ciclo);
        postulante.setPrelamolina(cepre);
        return precio;
    }

    private ConceptoPrecio findConceptoExtemporaneoByCiclo(CicloPostula ciclo) {
        ConceptoPago concepto = conceptoPagoDAO.findByCodigo(AdmisionConstantine.CODE_EXTEMPORANEO);
        if (concepto == null) {
            throw new PhobosException("No existe concepto extemporaneo");
        }
        ConceptoPrecio precio = conceptoPrecioDAO.findByConceptoCiclo(concepto, ciclo);
        if (precio == null) {
            throw new PhobosException("No se pudo determinar el monto del extemporaneo");
        }

        return precio;
    }

    private DescuentoExamen findDescuentoPublico(Integer dscto) {
        List<DescuentoExamen> descuentos = descuentoExamenDAO.all();
        for (DescuentoExamen descuento : descuentos) {
            if (dscto == 50 && descuento.getCodigoEnum() == DescuentoEnum.D50PUB) {
                return descuento;
            }
            if (dscto == 25 && descuento.getCodigoEnum() == DescuentoEnum.D25PUB) {
                return descuento;
            }
            if (dscto == 30 && descuento.getCodigoEnum() == DescuentoEnum.D30PUB) {
                return descuento;
            }
        }
        return null;
    }

    private MotivoDescuemtoEnum findMotivoDescuento(ConceptoPago conceptoModalidad) {
        if (conceptoModalidad.getDescripcion().contains(AdmisionConstantine.CEPRE) && conceptoModalidad.getDescuento() == 50) {
            return MotivoDescuemtoEnum.CEPRE50;
        }
        if (conceptoModalidad.getDescripcion().contains(AdmisionConstantine.CEPRE) && conceptoModalidad.getDescuento() == 25) {
            return MotivoDescuemtoEnum.CEPRE25;
        }
        if (conceptoModalidad.getDescripcion().contains(AdmisionConstantine.CEPRE) && conceptoModalidad.getDescuento() == 30) {
            return MotivoDescuemtoEnum.CEPRE30;
        }
        return MotivoDescuemtoEnum.MODAL;
    }

    private void eliminarDeudas(ItemDeudaInteresado iDeuda) {

        logger.debug("iDeuda {}", iDeuda);
        logger.debug("Eliminando DEUDAS ANTERIORES");
        iDeuda.setEstadoEnum(DeudaEstadoEnum.ANU);
        itemDeudaInteresadoDAO.update(iDeuda);

        DeudaInteresado deuda = iDeuda.getDeudaInteresado();
        boolean deudaAnulada = false;
        if (deuda.getMonto().compareTo(iDeuda.getMonto()) <= 0) {
            deuda.setEstadoEnum(DeudaEstadoEnum.ANU);
            deudaInteresadoDAO.update(deuda);
            deudaAnulada = true;
        } else {
            deuda.setMonto(deuda.getMonto().subtract(iDeuda.getMonto()));
            deuda.setDescripcion(createDescripcionDeuda(deuda));
            deudaInteresadoDAO.update(deuda);
        }

        Acreencia acreenciaOld = acreenciaDAO.findByCuentaAndTablaIns(deuda.getCuentaBancaria(), NombreTablasEnum.FIN_DEUDA_INTERESADO, deuda.getId(), DeudaEstadoEnum.DEU);
        if (acreenciaOld == null) {
            logger.debug("LA ACREENCIA DE LA DEUDA NO HA SIDO ENCONTRADA ID : {}", deuda.getId());
            logger.info("LA ACREENCIA DE LA DEUDA NO HA SIDO ENCONTRADA ID : {}", deuda.getId());
            throw new PhobosException("No se pudo realizar la anulación");
        }
        acreenciaOld.setEstadoEnum(DeudaEstadoEnum.ANU);
        acreenciaOld.setFechaAnulacion(new Date());
        acreenciaDAO.update(acreenciaOld);

        if (deudaAnulada) {
            return;
        }

        Acreencia acreenciaNew = new Acreencia();
        acreenciaNew.setCuentaBancaria(acreenciaOld.getCuentaBancaria());
        acreenciaNew.setDescripcion(deuda.getDescripcion());
        acreenciaNew.setEstadoEnum(DeudaEstadoEnum.DEU);
        acreenciaNew.setFechaDocumento(new Date());
        acreenciaNew.setFechaVencimiento(acreenciaOld.getFechaVencimiento());
        acreenciaNew.setInstanciaTabla(deuda.getId());
        acreenciaNew.setMonto(deuda.getMonto());
        acreenciaNew.setOficina(acreenciaOld.getOficina());
        acreenciaNew.setAbono(BigDecimal.ZERO);
        acreenciaNew.setFechaRegistro(new Date());
        acreenciaNew.setPersona(acreenciaOld.getPersona());
        acreenciaNew.setTablaEnum(NombreTablasEnum.FIN_DEUDA_INTERESADO);
        acreenciaDAO.save(acreenciaNew);
    }

    private String createDescripcionDeuda(DeudaInteresado deuda) {
        return createDescripcionDeuda(deuda, null);
    }

    private String createDescripcionDeuda(DeudaInteresado deuda, List<ItemDeudaInteresado> items) {
        if (items == null) {
            items = itemDeudaInteresadoDAO.allActivasByDeuda(deuda);
        }
        return postulanteService.createDescripcionDeuda(items);
    }

    @Override
    public List<ModalidadIngreso> allModalidadesCambioForPostulante(Postulante postulante, List<ModalidadIngreso> modalidades) {
        ModalidadIngreso modalidadPostulante = postulante.getModalidadIngreso();
        Map<Long, ModalidadIngreso> mapModalidades = TypesUtil.convertListToMap("id", modalidades);
        mapModalidades.remove(modalidadPostulante.getId());

        for (ModalidadIngreso modalidad : modalidades) {
            if (modalidad.isPreLaMolina()) {
                mapModalidades.remove(modalidad.getId());
            }
        }

        return new ArrayList(mapModalidades.values());
    }

    @Override
    public List<ModalidadIngreso> allModalidadesCambioForSimulacion(List<ModalidadIngreso> modalidades, CicloPostula ciclo) {
        List<ModalidadIngresoCiclo> modalidadesCiclo = this.allModalidadesCicloByCicloModalidades(ciclo, modalidades);
        Map<Long, ModalidadIngreso> mapModalidades = TypesUtil.convertListToMap("id", modalidades);
        Map<Long, ModalidadIngresoCiclo> mapModalidadesCiclo = TypesUtil.convertListToMap("modalidadIngreso.id", modalidadesCiclo);

        for (ModalidadIngreso modalidad : modalidades) {
            if (modalidad.isParticipanteLibre()) {
                mapModalidades.remove(modalidad.getId());
                continue;
            }

            if (modalidad.isPreLaMolina()) {
                mapModalidades.remove(modalidad.getId());
                continue;
            }

            ModalidadIngresoCiclo modalidadCiclo = mapModalidadesCiclo.get(modalidad.getId());
            if (modalidadCiclo.getRindeExamenAdmision() == 1) {
            } else {
                mapModalidades.remove(modalidad.getId());
            }
        }
        return new ArrayList(mapModalidades.values());
    }

    @Override
    public ConceptoPrecio findPrecioByDataPostulantePreview(Postulante postulante, ModalidadIngreso modalidadIngreso, CicloPostula cicloPostula,
            Colegio colegio, String colegioExtranjero, Universidad universidad, String universidadExtranjera, Pais paisCole, Pais paisUni) {

        paisCole = paisCole != null ? paisDAO.find(paisCole.getId()) : null;
        paisUni = paisUni != null ? paisDAO.find(paisUni.getId()) : null;
        if (paisCole != null && !paisCole.esPeru()) {
            colegio = null;
        }
        if (paisUni != null && !paisUni.esPeru()) {
            universidad = null;
        }

        return findPrecioByDataPostulante(postulante, modalidadIngreso, cicloPostula, colegio, colegioExtranjero, universidad, universidadExtranjera);

    }

}
