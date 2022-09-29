package edu.pe.lamolina.admision.controller.admision.inscripcion.modalidad;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.pe.lamolina.admision.dao.general.ColegioDAO;
import edu.pe.lamolina.admision.dao.general.PaisDAO;
import edu.pe.lamolina.admision.dao.general.UniversidadDAO;
import edu.pe.lamolina.admision.dao.inscripcion.CarreraPostulaDAO;
import edu.pe.lamolina.admision.dao.inscripcion.ModalidadIngresoCicloDAO;
import edu.pe.lamolina.admision.dao.inscripcion.ModalidadIngresoDAO;
import edu.pe.lamolina.admision.dao.inscripcion.OpcionCarreraDAO;
import edu.pe.lamolina.admision.dao.inscripcion.PostulanteDAO;
import edu.pe.lamolina.admision.dao.vacantes.VacanteCarreraDAO;
import pe.edu.lamolina.model.constantines.AdmisionConstantine;
import edu.pe.lamolina.admision.zelper.model.DataSessionAdmision;
import pe.albatross.zelpers.miscelanea.PhobosException;
import edu.pe.lamolina.admision.dao.academico.GradoSecundariaDAO;
import edu.pe.lamolina.admision.dao.finanzas.AcreenciaDAO;
import edu.pe.lamolina.admision.dao.finanzas.ConceptoPrecioDAO;
import edu.pe.lamolina.admision.dao.finanzas.DeudaInteresadoDAO;
import edu.pe.lamolina.admision.dao.finanzas.ItemCargaAbonoDAO;
import edu.pe.lamolina.admision.dao.inscripcion.EventoCicloDAO;
import java.util.stream.Collectors;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.enums.DeudaEstadoEnum;
import pe.edu.lamolina.model.enums.EventoEnum;
import pe.edu.lamolina.model.enums.NombreTablasEnum;
import pe.edu.lamolina.model.enums.PostulanteEstadoEnum;
import pe.edu.lamolina.model.finanzas.Acreencia;
import pe.edu.lamolina.model.finanzas.ConceptoPrecio;
import pe.edu.lamolina.model.finanzas.DeudaInteresado;
import pe.edu.lamolina.model.finanzas.ItemCargaAbono;
import pe.edu.lamolina.model.general.Colegio;
import pe.edu.lamolina.model.general.GradoSecundaria;
import pe.edu.lamolina.model.general.Pais;
import pe.edu.lamolina.model.general.Universidad;
import pe.edu.lamolina.model.inscripcion.CarreraPostula;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Evento;
import pe.edu.lamolina.model.inscripcion.EventoCiclo;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.model.inscripcion.ModalidadIngresoCiclo;
import pe.edu.lamolina.model.inscripcion.OpcionCarrera;
import pe.edu.lamolina.model.inscripcion.Postulante;
import pe.edu.lamolina.model.vacantes.VacanteCarrera;

@Service
@Transactional(readOnly = true)
public class ModalidadServiceImp implements ModalidadService {

    @Autowired
    ModalidadIngresoDAO modalidadIngresoDAO;

    @Autowired
    ModalidadIngresoCicloDAO modalidadIngresoCicloDAO;

    @Autowired
    UniversidadDAO universidadDAO;

    @Autowired
    PostulanteDAO postulanteDAO;

    @Autowired
    CarreraPostulaDAO carreraPostulaDAO;

    @Autowired
    PaisDAO paisDAO;

    @Autowired
    GradoSecundariaDAO gradoSecundariaDAO;

    @Autowired
    OpcionCarreraDAO opcionCarreraDAO;

    @Autowired
    VacanteCarreraDAO vacanteCarreraDAO;

    @Autowired
    DeudaInteresadoDAO deudaInteresadoDAO;

    @Autowired
    ColegioDAO colegioDAO;

    @Autowired
    EventoCicloDAO eventoCicloDAO;

    @Autowired
    ItemCargaAbonoDAO itemCargaAbonoDAO;

    @Autowired
    ConceptoPrecioDAO conceptoPrecioDAO;

    @Autowired
    AcreenciaDAO acreenciaDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public ModalidadIngreso getModalidad(Long modalidad) {
        return modalidadIngresoDAO.find(modalidad);
    }

    @Override
    public ModalidadIngresoCiclo getModalidadIngresoCiclo(ModalidadIngreso modalidadIngreso, CicloPostula cicloPostula) {
        return modalidadIngresoCicloDAO.findByModalidadIngresoCiclo(modalidadIngreso, cicloPostula);
    }

    @Override
    public List<Universidad> allUniversidadPeru() {
        Pais peru = paisDAO.findByCode("PE");
        return universidadDAO.allActivasByPais(peru);
    }

    @Override
    public Postulante findPostulante(Postulante postulanteSess) {
        return postulanteDAO.find(postulanteSess.getId());
    }

    @Override
    @Transactional
    public void aceptarCambioModalidad(Postulante postulanteSess, HttpSession session) {
        DateTime today = new DateTime();
        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);

        Postulante postulante = postulanteDAO.find(postulanteSess.getId());
        postulante.setEstado(PostulanteEstadoEnum.ANU);
        postulante.setFechaCambioModalidad(today.toDate());
        postulante.setMotivoCambio(postulanteSess.getMotivoCambio());

        List<DeudaInteresado> deudas = deudaInteresadoDAO.allByInteresadoEstado(postulante.getInteresado(), DeudaEstadoEnum.ACT);
        System.out.println("Hay " + deudas.size() + " activas para el interesado " + postulante.getInteresado().getId());

        BigDecimal abonosPerdidos = BigDecimal.ZERO;
        BigDecimal abonosRecuperables = BigDecimal.ZERO;
        BigDecimal deudasRecuperables = BigDecimal.ZERO;
        for (DeudaInteresado deuda : deudas) {

            Postulante postul = deuda.getPostulante();
            if (postul == null) {
                abonosRecuperables = abonosRecuperables.add(deuda.getAbono());
                deudasRecuperables = deudasRecuperables.add(deuda.getAbono());
            }

            if (postul != null && postul.getId().longValue() == postulante.getId()) {
                ConceptoPrecio conceptoPrecioFull = conceptoPrecioDAO.find(deuda.getConceptoPrecio().getId());
                Acreencia acreencia = acreenciaDAO.findByCuentaAndTablaIns(
                        conceptoPrecioFull.getConceptoPago().getCuentaBancaria(),
                        NombreTablasEnum.FIN_DEUDA_INTERESADO,
                        deuda.getId(),
                        DeudaEstadoEnum.DEU);

                abonosPerdidos = abonosPerdidos.add(deuda.getAbono());
                if (deuda.getAbono().compareTo(BigDecimal.ZERO) == 0) {
                    deuda.setEstadoEnum(DeudaEstadoEnum.INA);
                    deuda.setMotivoModificacion("Deuda inactivada por el postulante");
                    deuda.setFechaAnulacion(today.toDate());
                    postulante.setImportePagar(BigDecimal.ZERO);

                    // enviar a recaudacion anular la deuda
                    acreencia.setFechaAnulacion(today.toDate());
                    acreencia.setEstadoEnum(DeudaEstadoEnum.ANU);
                    acreenciaDAO.update(acreencia);

                } else if (deuda.getAbono().compareTo(BigDecimal.ZERO) > 0 && !deuda.isCancelada()) {
                    deuda.setEstadoEnum(DeudaEstadoEnum.ANT);
                    deuda.setMotivoModificacion("Deuda anulada por el postulante sin lugar a reclamo");
                    deuda.setFechaAnulacion(today.toDate());
                    postulante.setImportePagar(BigDecimal.ZERO);

                    // enviar a recaudacion anular la deuda
                    acreencia.setFechaAnulacion(today.toDate());
                    acreencia.setEstadoEnum(DeudaEstadoEnum.ANU);
                    acreenciaDAO.update(acreencia);

                } else {
                    deuda.setEstadoEnum(DeudaEstadoEnum.ANU);
                    deuda.setMotivoModificacion("Deuda anulada por el postulante sin lugar a reclamo");
                    deuda.setFechaAnulacion(today.toDate());
                    postulante.setImportePagar(BigDecimal.ZERO);

                }
                deudaInteresadoDAO.update(deuda);
            }
        }

        postulante.setImporteAbonado(abonosPerdidos);
        postulante.setImportePagar(BigDecimal.ZERO);
        postulante.setImporteDescuento(BigDecimal.ZERO);
        postulante.setImporteTotal(BigDecimal.ZERO);
        postulanteDAO.update(postulante);

        Postulante postulanteNew = new Postulante();
        postulanteNew.setPersona(postulante.getPersona());
        postulanteNew.setEmail(postulante.getEmail());
        postulanteNew.setCodigo(postulante.getCodigo());
        postulanteNew.setInteresado(postulante.getInteresado());
        postulanteNew.setCicloPostula(postulante.getCicloPostula());
        postulanteNew.setColegioProcedencia(postulante.getColegioProcedencia());
        postulanteNew.setPaisColegio(postulante.getPaisColegio());
        postulanteNew.setColegioExtranjero(postulante.getColegioExtranjero());
        postulanteNew.setCodigoAlumno(postulante.getCodigoAlumno());
        postulanteNew.setYearEgresoColegio(postulante.getYearEgresoColegio());
        postulanteNew.setUniversidadProcedencia(postulante.getUniversidadProcedencia());
        postulanteNew.setPaisUniversidad(postulante.getPaisUniversidad());
        postulanteNew.setUniversidadExtranjera(postulante.getUniversidadExtranjera());
        postulanteNew.setTipoGestion(postulante.getTipoGestion());
        postulanteNew.setProspecto(postulante.getProspecto());
        postulanteNew.setImportePagar(deudasRecuperables);
        postulanteNew.setImporteAbonado(abonosRecuperables);
        postulanteNew.setImporteDescuento(BigDecimal.ZERO);
        postulanteNew.setImporteTotal(deudasRecuperables);
        postulanteNew.setImporteUtilizado(BigDecimal.ZERO);
        postulanteNew.setFechaRegistro(new Date());
        postulanteNew.setEstado(PostulanteEstadoEnum.CRE);
        postulanteDAO.save(postulanteNew);

        List<ItemCargaAbono> abonos = itemCargaAbonoDAO.allActivosByPostulante(postulante);
        for (ItemCargaAbono abono : abonos) {
            abono.setPostulante(postulanteNew);
            itemCargaAbonoDAO.update(abono);
        }

        ds.setPostulante(postulanteNew);
        session.setAttribute(AdmisionConstantine.SESSION_USUARIO, ds);

    }

    @Override
    public List<CarreraPostula> allCarreras(ModalidadIngreso modalidad, CicloPostula ciclo) {
        List<VacanteCarrera> vacCarrerasModalidad = vacanteCarreraDAO.allVacanteCarrera(modalidad, ciclo);
        if (vacCarrerasModalidad.isEmpty()) {
            return carreraPostulaDAO.allByCiclo(ciclo);
        }
        Map<Long, CarreraPostula> carrerasMap = TypesUtil.convertListToMap("carreraPostula.id", "carreraPostula", vacCarrerasModalidad);
        if (carrerasMap == null || carrerasMap.isEmpty()) {
            return new ArrayList();
        }
        List<CarreraPostula> carreras = carrerasMap.values().stream().collect(Collectors.toList());
        return carreras;
    }

    @Override
    public Pais findPeru() {
        return paisDAO.findByCode("PE");
    }

    @Override
    public List<GradoSecundaria> allGrado() {
        return gradoSecundariaDAO.all();
    }

    @Override
    public void testCovenio(Postulante postulante, ModalidadIngreso modalidadIngreso) {
        if (modalidadIngreso.isConvenioAndresBello()) {
            logger.debug("PASIS NACIMIENTO {} NAME {}", postulante.getPersona().getPaisNacer().getId(), postulante.getPersona().getPaisNacer().getNombre());
            logger.debug("PASIS COLUMN ANDRES BELLO  {}", postulante.getPersona().getPaisNacer().getConvenioAndresBello());
            if (postulante.getPersona().getNacionalidad().esPeru()) {
                throw new PhobosException("Está restringido a extranjeros de paises miembros del convenio");
            }
            if (postulante.getPersona().getNacionalidad().getConvenioAndresBello() == null
                    || postulante.getPersona().getNacionalidad().getConvenioAndresBello() == 0) {
                throw new PhobosException("El país de origen no pertenece al convenio Andres Bello.");
            }
        }
        if (modalidadIngreso.isBecarioExtranjero()) {
            if (postulante.getPersona().getPaisDomicilio().esPeru()) {
                throw new PhobosException("Está restringido a residentes extranjeros");
            }
        }
    }

    @Override
    public CarreraPostula findCarreraIngreso(Postulante postulante) {
        ModalidadIngreso modalidad = postulante.getModalidadIngreso();
        if (modalidad == null) {
            return null;
        }
        if (!modalidad.isPreLaMolina()) {
            return null;
        }

        OpcionCarrera opcion = opcionCarreraDAO.findByPostulante(postulante);
        return opcion.getCarreraPostula();
    }

    @Override
    public Colegio findColegio(Postulante postulante) {
        if (postulante.getColegioProcedencia() == null) {
            return null;
        }
        return colegioDAO.find(postulante.getColegioProcedencia().getId());
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

    private boolean existeEvento(String codigo, List<EventoCiclo> eventos) {
        for (EventoCiclo eventoCiclo : eventos) {
            Evento evento = eventoCiclo.getEvento();
            if (evento.getCodigo().equals(codigo)) {
                return true;
            }
        }
        return false;
    }

}
