package edu.pe.lamolina.admision.controller.admision.inscripcion.postulante;

import com.google.common.collect.Lists;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.pe.lamolina.admision.dao.academico.AlumnoDAO;
import edu.pe.lamolina.admision.dao.academico.CarreraDAO;
import edu.pe.lamolina.admision.dao.academico.ModalidadEstudioDAO;
import edu.pe.lamolina.admision.dao.finanzas.ConceptoPagoDAO;
import edu.pe.lamolina.admision.dao.finanzas.ConceptoPrecioDAO;
import edu.pe.lamolina.admision.dao.finanzas.DeudaInteresadoDAO;
import edu.pe.lamolina.admision.dao.general.ColegioDAO;
import edu.pe.lamolina.admision.dao.general.ContenidoCartaDAO;
import edu.pe.lamolina.admision.dao.general.PaisDAO;
import edu.pe.lamolina.admision.dao.general.PersonaDAO;
import edu.pe.lamolina.admision.dao.general.TipoDocIdentidadDAO;
import edu.pe.lamolina.admision.dao.general.UniversidadDAO;
import edu.pe.lamolina.admision.dao.inscripcion.CarreraPostulaDAO;
import edu.pe.lamolina.admision.dao.inscripcion.CicloPostulaDAO;
import edu.pe.lamolina.admision.dao.inscripcion.DescuentoExamenDAO;
import edu.pe.lamolina.admision.dao.inscripcion.EventoCicloDAO;
import edu.pe.lamolina.admision.dao.inscripcion.InteresadoDAO;
import edu.pe.lamolina.admision.dao.inscripcion.ModalidadIngresoCicloDAO;
import edu.pe.lamolina.admision.dao.inscripcion.ModalidadIngresoDAO;
import edu.pe.lamolina.admision.dao.inscripcion.OpcionCarreraDAO;
import edu.pe.lamolina.admision.dao.inscripcion.PostulanteDAO;
import edu.pe.lamolina.admision.dao.inscripcion.PrelamolinaDAO;
import edu.pe.lamolina.admision.zelper.mail.MailerService;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import edu.pe.lamolina.admision.dao.academico.GradoSecundariaDAO;
import edu.pe.lamolina.admision.dao.finanzas.AcreenciaDAO;
import edu.pe.lamolina.admision.dao.finanzas.CampagnaDescuentoDAO;
import edu.pe.lamolina.admision.dao.finanzas.CuentaBancariaDAO;
import edu.pe.lamolina.admision.dao.finanzas.ItemDeudaInteresadoDAO;
import edu.pe.lamolina.admision.dao.general.ArchivoDAO;
import edu.pe.lamolina.admision.dao.general.OficinaDAO;
import edu.pe.lamolina.admision.dao.inscripcion.EventoDAO;
import edu.pe.lamolina.admision.dao.inscripcion.MetalesPostulanteDAO;
import edu.pe.lamolina.admision.dao.inscripcion.PostulanteBlacklistDAO;
import edu.pe.lamolina.admision.dao.inscripcion.SolicitudCambioInfoDAO;
import edu.pe.lamolina.admision.dao.inscripcion.TerminosPostulanteDAO;
import edu.pe.lamolina.admision.zelper.uploadS3.UploadFileS3;
import java.io.File;
import java.math.RoundingMode;
import javax.servlet.http.HttpSession;
import org.joda.time.LocalDate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Propagation;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.constantines.AdmisionConstantine;
import static pe.edu.lamolina.model.constantines.AdmisionConstantine.CODE_PROSPECTO;
import pe.edu.lamolina.model.constantines.BienestarConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.enums.ContenidoCartaEnum;
import pe.edu.lamolina.model.enums.CuentaBancariaEnum;
import pe.edu.lamolina.model.enums.DescuentoEnum;
import pe.edu.lamolina.model.enums.DeudaEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.EventoEnum;
import static pe.edu.lamolina.model.enums.EventoEnum.CEPRE;
import pe.edu.lamolina.model.enums.InteresadoEstadoEnum;
import static pe.edu.lamolina.model.enums.InteresadoEstadoEnum.POST;
import static pe.edu.lamolina.model.enums.InteresadoEstadoEnum.PROS;
import static pe.edu.lamolina.model.enums.InteresadoEstadoEnum.REG;
import pe.edu.lamolina.model.enums.MotivoDescuemtoEnum;
import pe.edu.lamolina.model.enums.NombreTablasEnum;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.enums.persona.PersonaEstadoEnum;
import pe.edu.lamolina.model.enums.PostulanteEstadoEnum;
import static pe.edu.lamolina.model.enums.PostulanteEstadoEnum.ING;
import static pe.edu.lamolina.model.enums.PostulanteEstadoEnum.INS;
import static pe.edu.lamolina.model.enums.PostulanteEstadoEnum.PAGO;
import pe.edu.lamolina.model.enums.SexoEnum;
import pe.edu.lamolina.model.enums.SolicitudCambioInfoEstadoEnum;
import pe.edu.lamolina.model.enums.TipoGestionEnum;
import pe.edu.lamolina.model.enums.TipoProspectoEnum;
import static pe.edu.lamolina.model.enums.VariableContenidoEnum.CICLO_ACADEMICO;
import static pe.edu.lamolina.model.enums.VariableContenidoEnum.IDENTIFICADO;
import static pe.edu.lamolina.model.enums.VariableContenidoEnum.NOMBRE_PERSONA;
import static pe.edu.lamolina.model.enums.VariableContenidoEnum.NRO_DOCUMENTO;
import static pe.edu.lamolina.model.enums.VariableContenidoEnum.TIPO_DOCUMENTO;
import pe.edu.lamolina.model.finanzas.Acreencia;
import pe.edu.lamolina.model.finanzas.CampagnaDescuento;
import pe.edu.lamolina.model.finanzas.ConceptoPago;
import pe.edu.lamolina.model.finanzas.ConceptoPrecio;
import pe.edu.lamolina.model.finanzas.CuentaBancaria;
import pe.edu.lamolina.model.finanzas.DeudaInteresado;
import pe.edu.lamolina.model.finanzas.ItemDeudaInteresado;
import pe.edu.lamolina.model.finanzas.SolicitudCambioInfo;
import pe.edu.lamolina.model.general.Archivo;
import pe.edu.lamolina.model.general.Colegio;
import pe.edu.lamolina.model.general.GestionColegio;
import pe.edu.lamolina.model.general.GradoSecundaria;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Pais;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.general.Universidad;
import pe.edu.lamolina.model.inscripcion.CarreraPostula;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.inscripcion.DescuentoExamen;
import pe.edu.lamolina.model.inscripcion.Evento;
import pe.edu.lamolina.model.inscripcion.EventoCiclo;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.MetalesPostulante;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.model.inscripcion.ModalidadIngresoCiclo;
import pe.edu.lamolina.model.inscripcion.OpcionCarrera;
import pe.edu.lamolina.model.inscripcion.Postulante;
import pe.edu.lamolina.model.inscripcion.PostulanteBlacklist;
import pe.edu.lamolina.model.inscripcion.Prelamolina;
import pe.edu.lamolina.model.inscripcion.TerminosPostulante;
import pe.edu.lamolina.model.seguridad.Usuario;

@Service
@Transactional(readOnly = true)
public class PostulanteServiceImp implements PostulanteService {

    @Autowired
    PersonaDAO personaDAO;
    @Autowired
    PostulanteDAO postulanteDAO;
    @Autowired
    OpcionCarreraDAO opcionCarreraDAO;
    @Autowired
    CarreraDAO carreraDAO;
    @Autowired
    CicloPostulaDAO cicloPostulaDAO;
    @Autowired
    CarreraPostulaDAO carreraPostulaDAO;
    @Autowired
    ModalidadIngresoDAO modalidadIngresoDAO;
    @Autowired
    UniversidadDAO universidadDAO;
    @Autowired
    PaisDAO paisDAO;
    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;
    @Autowired
    TipoDocIdentidadDAO tipoDocIdentidadDAO;
    @Autowired
    InteresadoDAO interesadoDAO;
    @Autowired
    PrelamolinaDAO prelamolinaDAO;
    @Autowired
    ConceptoPagoDAO conceptoPagoDAO;
    @Autowired
    ConceptoPrecioDAO conceptoPrecioDAO;
    @Autowired
    DeudaInteresadoDAO deudaInteresadoDAO;
    @Autowired
    ModalidadIngresoCicloDAO modalidadIngresoCicloDAO;
    @Autowired
    DescuentoExamenDAO descuentoExamenDAO;
    @Autowired
    ColegioDAO colegioDAO;
    @Autowired
    GradoSecundariaDAO gradoDAO;
    @Autowired
    EventoCicloDAO eventoCicloDAO;
    @Autowired
    ContenidoCartaDAO contenidoCartaDAO;
    @Autowired
    PostulanteBlacklistDAO postulanteBlacklistDAO;
    @Autowired
    AlumnoDAO alumnoDAO;
    @Autowired
    MetalesPostulanteDAO metalesPostulanteDAO;
    @Autowired
    EventoDAO eventoDAO;
    @Autowired
    AcreenciaDAO acreenciaDAO;
    @Autowired
    OficinaDAO oficinaDAO;
    @Autowired
    CampagnaDescuentoDAO campagnaDescuentoDAO;
    @Autowired
    SolicitudCambioInfoDAO solicitudCambioInfoDAO;
    @Autowired
    ItemDeudaInteresadoDAO itemDeudaInteresadoDAO;
    @Autowired
    CuentaBancariaDAO cuentaBancariaDAO;
    @Autowired
    TerminosPostulanteDAO terminosPostulante;

    @Autowired
    ArchivoDAO archivoDAO;
    @Autowired
    MailerService mailerService;

    @Autowired
    UploadFileS3 uploadFileS3;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<TipoDocIdentidad> allTiposDocIdentidad() {
        return tipoDocIdentidadDAO.allForPersonaNatural();
    }

    @Override
    @Transactional
    public Postulante saveDatosPersonales(Postulante postulanteForm, Persona personaSession, Interesado interesadoSession, CicloPostula ciclo) {

        this.validarDocumentoAndVerificarPersona(postulanteForm, personaSession, ciclo);

        return crearPostulante(postulanteForm, interesadoSession);
    }

    @Override
    public void validarDocumentoAndVerificarPersona(Postulante postulanteForm, Persona personaSession, CicloPostula ciclo) {

        this.completarDNI(personaSession, postulanteForm);

        this.verificarPersona(postulanteForm, ciclo);

    }

    private Postulante crearPostulante(Postulante postulanteForm, Interesado interesadoSession) {
        logger.debug("SAVE POSTULANTE {}", postulanteForm.getId());
        Postulante postulante = postulanteDAO.findActivoByInteresadoSimple(interesadoSession);

        boolean esNuevo = false;
        if (postulante == null) {
            postulante = new Postulante();
            esNuevo = true;

        } else {
            postulanteDAO.findLock(postulante.getId());
        }

        Persona personaForm = postulanteForm.getPersona();
        ObjectUtil.eliminarAttrSinId(personaForm, "ubicacionNacer");
        ObjectUtil.eliminarAttrSinId(personaForm, "ubicacionDomicilio");

        if (personaForm.getFechaNacer() == null) {
            throw new PhobosException("Es obligatorio indicar la fecha de nacimiento");
        }

        DateTime hoy = new DateTime();
        DateTime nacer = new DateTime(personaForm.getFechaNacer());
        if (nacer.getYear() < 1000) {
            throw new PhobosException("El año de la fecha de su nacimiento debe ser de 4 digitos");
        }
        int years = hoy.getYear() - nacer.getYear();
        if (years > 100) {
            throw new PhobosException("El año de la fecha de su nacimiento es incorrecta");
        }
        if (years < 12) {
            throw new PhobosException("No pueden postular personas menores a los 12 años");
        }

        Persona personaBD = this.crearPersona(personaForm, postulante);
        personaBD.setPaterno(personaForm.getPaterno());
        personaBD.setMaterno(personaForm.getMaterno());
        personaBD.setNombres(limpiarValor(personaForm.getPrimerNombre()) + (limpiarValor(personaForm.getSegundoNombre()) == null ? "" : " " + limpiarValor(personaForm.getSegundoNombre())));

        personaBD.setPrimerNombre(personaForm.getPrimerNombre());
        personaBD.setSegundoNombre(personaForm.getSegundoNombre());
        personaBD.setSexo(personaForm.getSexo());

        personaBD.setEmail(personaForm.getEmail());
        personaBD.setCelular(personaForm.getCelular());
        personaBD.setTelefono(personaForm.getTelefono());

        personaBD.setFechaNacer(personaForm.getFechaNacer());
        personaBD.setPaisNacer(personaForm.getPaisNacer());
        personaBD.setUbicacionNacer(personaForm.getUbicacionNacer());
        personaBD.setNacionalidad(personaForm.getNacionalidad());

        personaBD.setPaisDomicilio(personaForm.getPaisDomicilio());
        personaBD.setUbicacionDomicilio(personaForm.getUbicacionDomicilio());
        personaBD.setDireccion(personaForm.getDireccion());

        String nombre = postulanteForm.getPersona().getRutaArchivo();
        System.out.print("RUTA-------->" + nombre);
        String path = uploadFileS3.getPathFile(AdmisionConstantine.S3_CARNET_VACUNA, nombre);

        Archivo archivo = new Archivo();
        archivo.setFechaRegistro(new Date());
        archivo.setInstancia(NombreTablasEnum.GEN_PERSONA.name());
        archivo.setIdInstancia(personaBD.getId());
        archivo.setNombre(nombre);
        archivo.setRuta(path);
        archivo.setTipo("pdf");
        archivo.setUsuarioRegistro(new Usuario(1));
        archivoDAO.save(archivo);

        personaBD.setVacuna(archivo);

       /// this.uploadS3(archivo.getNombre());
        personaDAO.update(personaBD);

        postulante.setPersona(personaBD);
        postulante.setCodigo(personaBD.getNumeroDocIdentidad());
        postulante.setEmail(personaBD.getEmail());
        postulante.setInteresado(interesadoSession);
        postulante.setCicloPostula(interesadoSession.getCicloPostula());

        if (!interesadoSession.getNumeroDocIdentidad().equals(postulante.getCodigo())) {
            throw new PhobosException("El número de DNI ingresado en la preinscripción debe de ser el mismo en la inscripción. Caso contrario vuelva a realizar su registro.");
        }
        if (!esNuevo && !Arrays.asList(PostulanteEstadoEnum.CRE, PostulanteEstadoEnum.PROS).contains(postulante.getEstadoEnum())) {
            throw new PhobosException("Usted ya tiene un registro activo como postulante");
        }

        if (postulante.getColegioProcedencia() == null && postulante.getUniversidadProcedencia() == null
                && StringUtils.isEmpty(postulante.getColegioExtranjero()) && StringUtils.isEmpty(postulante.getUniversidadExtranjera())
                && postulante.getYearEgresoColegio() == null) {
            Postulante postulanteAntes = postulanteDAO.findLastActivoByPersona(personaBD);
            if (postulanteAntes != null) {
                postulante.setYearEgresoColegio(postulanteAntes.getYearEgresoColegio());
                postulante.setPaisColegio(postulanteAntes.getPaisColegio());
                postulante.setColegioProcedencia(postulanteAntes.getColegioProcedencia());
                postulante.setColegioExtranjero(postulanteAntes.getColegioExtranjero());
                postulante.setPaisUniversidad(postulanteAntes.getPaisUniversidad());
                postulante.setUniversidadProcedencia(postulanteAntes.getUniversidadProcedencia());
                postulante.setUniversidadExtranjera(postulanteAntes.getUniversidadExtranjera());
            }
        }

        if (esNuevo) {
            postulante.setEstado(PostulanteEstadoEnum.CRE);
            postulante.setImportePagar(BigDecimal.ZERO);
            postulante.setImporteAbonado(BigDecimal.ZERO);
            postulante.setImporteDescuento(BigDecimal.ZERO);
            postulante.setImporteTotal(BigDecimal.ZERO);
            postulante.setImporteUtilizado(BigDecimal.ZERO);
            postulanteDAO.save(postulante);
        } else {
            postulanteDAO.update(postulante);
        }

        return postulante;
    }

    private void uploadS3(String fileName) {
        //logger.debug("upload to s3    {}  {}   {}  {} {}", Constantine.S3_BUKET, Constantine.S3_DIR_FOTO_TMP, GlobalConstantine.TMP_DIR, fileName, true);
        File f = new File(GlobalConstantine.TMP_DIR + fileName);
        if (f.exists() && !f.isDirectory()) {
            uploadFileS3.uploadSync(AdmisionConstantine.S3_CARNET_VACUNA, GlobalConstantine.TMP_DIR, fileName, true);
        }
    }

    private Persona crearPersona(Persona personaForm, Postulante postulanteBD) {
        Persona personaBD = postulanteBD.getPersona();
        if (personaBD != null && !personaBD.getNumeroDocIdentidad().equals(AdmisionConstantine.CODE_POSTULANTE_DUMMY)) {
            return personaBD;
        }

        personaForm.setNumeroDocIdentidad(limpiarValor(personaForm.getNumeroDocIdentidad()));
        if (personaForm.getTipoDocumento() == null
                || (personaForm.getTipoDocumento() != null && personaForm.getTipoDocumento().getId() == null)) {
            throw new PhobosException("Debe indicar el documento de identidad");
        }
        if (personaForm.getNumeroDocIdentidad() == null) {
            throw new PhobosException("Debe indicar el número del documento de identidad");
        }
        logger.debug("BUSCAR PERSONA DOC {} NUM  {}", personaForm.getTipoDocumento(), personaForm.getNumeroDocIdentidad());
        personaBD = personaDAO.findByDocumento(personaForm.getTipoDocumento(), personaForm.getNumeroDocIdentidad());
        if (personaBD != null) {
            return personaBD;
        }

        personaBD = new Persona(personaForm.getTipoDocumento(), personaForm.getNumeroDocIdentidad());
        personaBD.setEstadoEnum(PersonaEstadoEnum.ACT);
        personaDAO.save(personaBD);
        return personaBD;
    }

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
    public List<GradoSecundaria> allGrado() {
        return gradoDAO.all();
    }

    @Override
    @Transactional
    public void crearPrelamolinaByPersona(Postulante postulante, CicloPostula ciclo) {
        System.out.println("crearPrelamolinaByPersona crearPrelamolinaByPersona");
        System.out.println("modalidad=" + ObjectUtil.getParentTree(postulante, "modalidadIngreso.id"));
        Prelamolina cepre = findIngresantePrelamolina(postulante);
        if (cepre == null) {
            return;
        }

        Persona persona = postulante.getPersona();

        ModalidadIngreso modalidadIngreso = modalidadIngresoDAO.findByCodeCliclo(AdmisionConstantine.CODE_MODALIDAD_CEPRE, ciclo);
        postulante.setModalidadIngreso(modalidadIngreso);
        postulante.setEstado(PostulanteEstadoEnum.CRE);
        postulante.setImportePagar(BigDecimal.ZERO);
        postulante.setImporteAbonado(BigDecimal.ZERO);
        postulante.setImporteDescuento(BigDecimal.ZERO);
        postulante.setImporteTotal(BigDecimal.ZERO);
        postulante.setImporteUtilizado(BigDecimal.ZERO);
        postulanteDAO.update(postulante);

        OpcionCarrera opcion = new OpcionCarrera();
        opcion.setCarreraPostula(cepre.getCarreraPostula());
        opcion.setPostulante(postulante);
        opcion.setPrioridad(1);
        opcionCarreraDAO.save(opcion);

        cepre.setPersona(persona);
        cepre.setPostulante(postulante);
        cepre.setFechaValidado(new Date());
        prelamolinaDAO.update(cepre);

        postulante.setOpcionCarrera(new ArrayList());
        postulante.getOpcionCarrera().add(opcion);
    }

    private Prelamolina findIngresantePrelamolina(Postulante postulante) {
        ModalidadIngreso modalidad = postulante.getModalidadIngreso();
        if (modalidad != null) {
            if (modalidad.isPreLaMolina()) {
                return null;
            }
        }

        Long idPersona = (Long) ObjectUtil.getParentTree(postulante, "persona.id");
        Long idCiclo = (Long) ObjectUtil.getParentTree(postulante, "cicloPostula.id");
        if (idPersona == null || idCiclo == null) {
            return null;
        }

        Prelamolina cepre = prelamolinaDAO.findByPersona(postulante.getPersona(), postulante.getCicloPostula());
        if (cepre != null && cepre.getEsIngresante() == 1 && cepre.getEstadoEnum() != PostulanteEstadoEnum.REN) {
            return cepre;
        }
        if (cepre != null && cepre.getEsIngresante() == 1 && cepre.getEstadoEnum() == PostulanteEstadoEnum.REN) {
            return null;
        }

        List<Prelamolina> preLamolinas = prelamolinaDAO.allByDocumento(postulante.getPersona(), postulante.getCicloPostula());
        if (preLamolinas.isEmpty()) {
            return null;
        }

        for (Prelamolina preLamolina : preLamolinas) {
            if (preLamolina.getEsIngresante() == 1 && preLamolina.getEstadoEnum() != PostulanteEstadoEnum.REN) {
                return preLamolina;
            }
        }

        return null;
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

    @Override
    public Persona getPersona(Persona persona) {
        Persona personaBD = personaDAO.find(persona.getId());
        if (!StringUtils.isEmpty(personaBD.getNombres())) {
            personaBD.setPrimerNombre(getPrimerNombre(personaBD.getNombres()));
            personaBD.setSegundoNombre(getSegundoNombre(personaBD.getNombres()));
            personaBD.unirNombres();
        }

        if (personaBD.getPaisNacer() == null) {
            personaBD.setPaisNacer(getPeru());
        }
        if (personaBD.getPaisDomicilio() == null) {
            personaBD.setPaisDomicilio(getPeru());
        }
        if (personaBD.getNacionalidad() == null) {
            personaBD.setNacionalidad(getPeru());
        }

        return personaBD;
    }

    @Override
    public Interesado findInteresado(Interesado interesadoSession) {
        return interesadoDAO.find(interesadoSession.getId());
    }

    private void verificarRangoFecha(Postulante postulante, CicloPostula ciclo) {
        ModalidadIngreso modalidad = modalidadIngresoDAO.find(postulante.getModalidadIngreso().getId());
        List<EventoCiclo> eventos = eventoCicloDAO.allByFechaCiclo(new Date(), ciclo);
        if (eventos.isEmpty()) {
            throw new PhobosException("No está programado inscripciones para hoy");
        }

        if (modalidad.isConcursoOrdinario()) {
            if (existeEvento(EventoEnum.INSC.name(), eventos)) {
                return;
            }
        }

        if (modalidad.isPreLaMolina()) {
            if (existeEvento(EventoEnum.CEPRE.name(), eventos)) {
                return;
            }
        }

        if (modalidad.isOtrasModalidades()) {
            if (existeEvento(EventoEnum.OTR.name(), eventos)) {
                return;
            }
        }

        if (!modalidad.isPreLaMolina()) {
            if (existeEvento(EventoEnum.EXTM.name(), eventos)) {
                return;
            }
        }

        eventos = eventoCicloDAO.allEventoInscripcionesByCiclo(ciclo);
        if (modalidad.isConcursoOrdinario()) {
            buscarRangoAtencion(EventoEnum.INSC.name(), eventos, modalidad);
            return;
        }
        if (modalidad.isPreLaMolina()) {
            buscarRangoAtencion(EventoEnum.CEPRE.name(), eventos, modalidad);
            return;
        }
        if (modalidad.isOtrasModalidades()) {
            buscarRangoAtencion(EventoEnum.OTR.name(), eventos, modalidad);
            return;
        }

        throw new PhobosException("No está programado inscripciones para hoy");
    }

    private void buscarRangoAtencion(String codigo, List<EventoCiclo> eventos, ModalidadIngreso modalidad) {
        for (EventoCiclo eventoCiclo : eventos) {
            Evento evento = eventoCiclo.getEvento();
            Locale.setDefault(new Locale("es", "PE"));
            String f1 = new DateTime(eventoCiclo.getFechaInicio()).toString("dd 'de' MMMM");
            String f2 = new DateTime(eventoCiclo.getFechaFin()).toString("dd 'de' MMMM");
            if (evento.getCodigo().equals(codigo)) {
                throw new PhobosException("Las inscripciones para la modalidad " + modalidad.getNombre().toUpperCase() + " están programadas desde el " + f1 + " al " + f2);
            }
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

    @Override
    @Transactional
    public void saveDatosAcademicos(Postulante postulanteForm, CicloPostula ciclo) {

        verificarRangoFecha(postulanteForm, ciclo);
        Postulante postulanteBD = postulanteDAO.find(postulanteForm.getId());
        ModalidadIngreso modalidad = modalidadIngresoDAO.find(postulanteForm.getModalidadIngreso().getId());
        ModalidadIngresoCiclo modalidadCiclo = modalidadIngresoCicloDAO.findByModalidadIngresoCiclo(modalidad, ciclo);

        ObjectUtil.eliminarAttrSinId(postulanteForm, "paisUniversidad");
        ObjectUtil.eliminarAttrSinId(postulanteForm, "universidadProcedencia");

        ObjectUtil.eliminarAttrSinId(postulanteForm, "paisColegio");
        ObjectUtil.eliminarAttrSinId(postulanteForm, "colegioProcedencia");
        ObjectUtil.eliminarAttrSinId(postulanteForm, "modalidadSimulacion");

        postulanteForm.setUniversidadExtranjera(limpiarValor(postulanteForm.getUniversidadExtranjera()));
        logger.debug("colegio extranjero {}", postulanteForm.getColegioExtranjero());
        postulanteForm.setColegioExtranjero(limpiarValor(postulanteForm.getColegioExtranjero()));
        logger.debug("colegio extranjero clear {}", postulanteForm.getColegioExtranjero());

        if (modalidadCiclo.isRequiereSoloUniversidad()) {
            revisarUniversidad(postulanteForm, modalidadCiclo);

        } else if (modalidadCiclo.isRequiereSoloColegio()) {
            revisarColegio(postulanteForm, modalidadCiclo);

        } else if (modalidadCiclo.isRequiereColegioUniversidad()) {
            if (postulanteForm.getPaisColegio() != null && postulanteForm.getPaisUniversidad() == null) {
                revisarColegio(postulanteForm, modalidadCiclo);

            } else if (postulanteForm.getPaisColegio() == null && postulanteForm.getPaisUniversidad() != null) {
                revisarUniversidad(postulanteForm, modalidadCiclo);

            } else {
                throw new PhobosException("Ingreso de Colegio o Universidad es incorrecto");
            }
        }

        revisarOrdenOpciones(postulanteForm);

        postulanteBD.setYearEgresoColegio(postulanteForm.getYearEgresoColegio());
        postulanteBD.setCodigoAlumno(postulanteForm.getCodigoAlumno());
        postulanteBD.setModalidadIngreso(modalidad);
        postulanteBD.setPaisUniversidad(postulanteForm.getPaisUniversidad());
        postulanteBD.setUniversidadExtranjera(postulanteForm.getUniversidadExtranjera());
        postulanteBD.setUniversidadProcedencia(postulanteForm.getUniversidadProcedencia());
        postulanteBD.setPaisColegio(postulanteForm.getPaisColegio());
        logger.debug("colegio extranjero set db {}", postulanteForm.getColegioExtranjero());
        postulanteBD.setColegioExtranjero(postulanteForm.getColegioExtranjero());
        logger.debug("colegio extranjero set from db for save {}", postulanteBD.getColegioExtranjero());
        postulanteBD.setColegioProcedencia(postulanteForm.getColegioProcedencia());
        postulanteBD.setGradoSecundaria(postulanteForm.getGradoSecundaria());
        postulanteBD.setGradoTitulo(postulanteForm.getGradoTitulo());
        postulanteBD.setModalidadSimulacion(postulanteForm.getModalidadSimulacion());

        revisarOpcionesBD(postulanteForm, postulanteBD);
        datosInstitucion(postulanteBD);

        postulanteDAO.update(postulanteBD);

    }

    @Override
    @Transactional
    public void saveCerrarInscripcion(Postulante postulanteForm, CicloPostula ciclo) {
        ciclo = cicloPostulaDAO.find(ciclo.getId());
        verificarRangoFecha(postulanteForm, ciclo);

        ModalidadIngreso modalidad = modalidadIngresoDAO.find(postulanteForm.getModalidadIngreso().getId());

        Postulante postulanteBD = postulanteDAO.find(postulanteForm.getId());
        postulanteBD.setFechaPreinscripcion(new Date());
        postulanteBD.setEstado(PostulanteEstadoEnum.PRE);
        postulanteBD.setModalidadIngreso(modalidad);
        postulanteBD.setImportePagar(BigDecimal.ZERO);
        postulanteDAO.update(postulanteBD);

        Interesado interesado = postulanteBD.getInteresado();
        interesado.setEstado(InteresadoEstadoEnum.POST);
        interesadoDAO.update(interesado);

        generarDeuda(postulanteBD, ciclo);
        revisarNoIngresantePrelamolina(postulanteBD);
        revisarIngresantePrelamolina(postulanteBD);
        sendEmailBoletaPago(postulanteBD, ciclo);

    }

    private void revisarOpcionesBD(Postulante postulanteForm, Postulante postulanteBD) {
        Map<Integer, OpcionCarrera> mapOpcionesBD = new LinkedHashMap();
        List<OpcionCarrera> opcionesBD = opcionCarreraDAO.allByPostulante(postulanteBD);
        opcionesBD.stream().forEach((opcion) -> {
            mapOpcionesBD.put(opcion.getPrioridad(), opcion);
        });

        List<OpcionCarrera> opcionesForm = postulanteForm.getOpcionCarrera();
        Map<Integer, OpcionCarrera> mapOpcionesForm = new LinkedHashMap();
        opcionesForm.stream().forEach((opcion) -> {
            mapOpcionesForm.put(opcion.getPrioridad(), opcion);
        });

        for (OpcionCarrera opcion : opcionesForm) {
            OpcionCarrera opcionBD = mapOpcionesBD.get(opcion.getPrioridad());
            if (opcionBD != null) {
                opcionBD.setCarreraPostula(opcion.getCarreraPostula());
                opcionCarreraDAO.update(opcionBD);
            } else {
                opcion.setPostulante(postulanteBD);
                opcionCarreraDAO.save(opcion);
            }
        }

        for (OpcionCarrera opcionBD : opcionesBD) {
            OpcionCarrera opcion = mapOpcionesForm.get(opcionBD.getPrioridad());
            if (opcion == null) {
                opcionCarreraDAO.delete(opcionBD);
            }
        }
    }

    private void revisarOrdenOpciones(Postulante postulante) {
        List<OpcionCarrera> opcionesForm = postulante.getOpcionCarrera();
        List<OpcionCarrera> tempos = new ArrayList();
        int prioridad = 1;
        for (OpcionCarrera opcion : opcionesForm) {
            opcion.setPrioridad(prioridad);
            if (opcion.getCarreraPostula().getId() == null) {
                tempos.add(opcion);
            }
            prioridad++;
        }

        for (OpcionCarrera tempo : tempos) {
            opcionesForm.remove(tempo);
        }

        Map<Long, OpcionCarrera> mapOpciones = new LinkedHashMap();
        prioridad = 1;
        for (OpcionCarrera opcion : opcionesForm) {
            OpcionCarrera opcionTempo = mapOpciones.get(opcion.getCarreraPostula().getId());
            if (opcionTempo != null) {
                CarreraPostula carreraPostulaBD = carreraPostulaDAO.find(opcion.getCarreraPostula().getId());
                throw new PhobosException("Esta duplicando la opción " + carreraPostulaBD.getCarrera().getNombre());
            }
            if (opcion.getPrioridad() != prioridad) {
                throw new PhobosException("Están mal ingresadas la prioridad de las opciones");
            }
            mapOpciones.put(opcion.getCarreraPostula().getId(), opcion);
            prioridad++;
        }
    }

    private void revisarUniversidad(Postulante postulante, ModalidadIngresoCiclo modalidadCiclo) {
        if (postulante.getPaisUniversidad() == null) {
            throw new PhobosException("Falta indicar el país de la universidad de procedencia");
        }

        Pais pais = paisDAO.find(postulante.getPaisUniversidad().getId());
        postulante.setPaisUniversidad(pais);
        if (pais.esPeru() && postulante.getUniversidadProcedencia() == null) {
            throw new PhobosException("Falta indicar la universidad de procedencia");

        } else if (!pais.esPeru() && postulante.getUniversidadExtranjera() == null) {
            throw new PhobosException("Falta ingresar el nombre de la universidad de procedencia");
        }

        if (!pais.esPeru() && modalidadCiclo.getSoloUniversidadPeruana() == 1) {
            throw new PhobosException("Para esta modalidad solo está permitido universidades peruanas");
        }

        if (pais.esPeru() && modalidadCiclo.getSoloUniversidadExtranjera() == 1) {
            throw new PhobosException("Para esta modalidad solo está permitido universidades extranjeras");
        }

        if (pais.esPeru()) {
            postulante.setUniversidadExtranjera(null);
        } else {
            postulante.setUniversidadProcedencia(null);
        }

        postulante.setPaisColegio(null);
        postulante.setColegioProcedencia(null);
        postulante.setColegioExtranjero(null);
    }

    private void revisarColegio(Postulante postulante, ModalidadIngresoCiclo modalidadCiclo) {
        if (postulante.getPaisColegio() == null) {
            throw new PhobosException("Falta indicar el país del colegio de procedencia");
        }

        Pais pais = paisDAO.find(postulante.getPaisColegio().getId());
        postulante.setPaisColegio(pais);
        if (pais.esPeru() && postulante.getColegioProcedencia() == null) {
            throw new PhobosException("Falta indicar el colegio de procedencia");

        } else if (!pais.esPeru() && postulante.getColegioExtranjero() == null) {
            throw new PhobosException("Falta ingresar el nombre de la universidad de procedencia");
        }

        if (!pais.esPeru() && modalidadCiclo.getSoloColegioPeruano() == 1) {
            throw new PhobosException("Para esta modalidad solo está permitido colegios peruanos");
        }

        if (pais.esPeru() && modalidadCiclo.getSoloColegioExtranjero() == 1) {
            throw new PhobosException("Para esta modalidad solo está permitido colegios extranjeros");
        }

        if (pais.esPeru()) {
            postulante.setColegioExtranjero(null);
        } else {
            postulante.setColegioProcedencia(null);
        }

        postulante.setPaisUniversidad(null);
        postulante.setUniversidadProcedencia(null);
        postulante.setUniversidadExtranjera(null);
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

    private void revisarNoIngresantePrelamolina(Postulante postulanteBD) {
        Prelamolina cepre = postulanteBD.getPrelamolina();
        if (cepre == null) {
            return;
        }

        System.out.println("CEPRE.ID.2=" + cepre.getId());

        if (cepre.isEstadoCreado()) {
            cepre.setPostulante(postulanteBD);
            cepre.setPersona(postulanteBD.getPersona());
            cepre.setEstadoEnum(PostulanteEstadoEnum.INS);
            cepre.setFechaInscripcion(new Date());
            prelamolinaDAO.update(cepre);
        }
    }

    private void revisarIngresantePrelamolina(Postulante postulanteBD) {
        ModalidadIngreso modalidad = postulanteBD.getModalidadIngreso();
        if (!modalidad.isPreLaMolina()) {
            return;
        }

        Prelamolina cepre = prelamolinaDAO.findByPostulante(postulanteBD);
        if (cepre == null) {
            return;
        }

        System.out.println("CEPRE.ID.1=" + cepre.getId());

        if (cepre.isEstadoCreado()) {
            cepre.setEstadoEnum(PostulanteEstadoEnum.INS);
            cepre.setFechaInscripcion(new Date());
            prelamolinaDAO.update(cepre);
        }
    }

    private void datosInstitucion(Postulante postulante) {
        Colegio cole = postulante.getColegioProcedencia();
        if (cole != null) {
            cole = colegioDAO.find(cole.getId());
            postulante.setColegioProcedencia(cole);
        }
        Universidad uni = postulante.getUniversidadProcedencia();
        if (uni != null) {
            uni = universidadDAO.find(uni.getId());
            postulante.setUniversidadProcedencia(uni);
        }
    }

    private void generarDeuda(Postulante postulante, CicloPostula ciclo) {
        Date fechaVencimiento = fechaVencimientoByModalidad(postulante.getModalidadIngreso(), ciclo);

        List<DeudaInteresado> deudasAnteriores = deudaInteresadoDAO.allByInteresadoEstados(postulante.getInteresado(), Arrays.asList(DeudaEstadoEnum.ANU, DeudaEstadoEnum.INA));

        List<ItemDeudaInteresado> itemsGuia = crearDeudaGuiaPostulante(postulante, ciclo, fechaVencimiento);
        EventoCiclo eventoExtemporaneo = findEventoExtemporaneo(ciclo);
        List<ItemDeudaInteresado> itemsInscripcion = crearDeudaDerechoInscripcion(postulante, ciclo, eventoExtemporaneo, deudasAnteriores);
        itemsInscripcion.addAll(itemsGuia);
        verificarAcreencias(postulante, itemsInscripcion, fechaVencimiento);

    }

    private void verificarAcreencias(Postulante postulante, List<ItemDeudaInteresado> itemsDeuda, Date fechaVencimiento) {
        List<ItemDeudaInteresado> itemsCta1 = new ArrayList();
        List<ItemDeudaInteresado> itemsCta2 = new ArrayList();

        for (ItemDeudaInteresado item : itemsDeuda) {
            DeudaInteresado deuda = item.getDeudaInteresado();
            if (deuda == null) {
                itemsCta2.add(item);
            } else {
                if (deuda.getCuentaBancaria().getCodigo().equals(CuentaBancariaEnum.GUIA_POST.getCodigoServ())) {
                    itemsCta1.add(item);
                }
                if (deuda.getCuentaBancaria().getCodigo().equals(CuentaBancariaEnum.INS_UNLM.getCodigoServ())) {
                    itemsCta2.add(item);
                }
            }
        }

        crearAcreencias(postulante, itemsCta1, fechaVencimiento, CuentaBancariaEnum.GUIA_POST.getCodigoServ());
        crearAcreencias(postulante, itemsCta2, fechaVencimiento, CuentaBancariaEnum.INS_UNLM.getCodigoServ());

        postulanteDAO.update(postulante);
    }

    private void crearAcreencias(Postulante postulante, List<ItemDeudaInteresado> itemsDeuda, Date fechaVencimiento, String codigoCta) {
        if (itemsDeuda.isEmpty()) {
            return;
        }

        BigDecimal montoPagar = BigDecimal.ZERO;
        BigDecimal montoTotal = BigDecimal.ZERO;
        BigDecimal montoDscto = BigDecimal.ZERO;
        DateTime todayDT = new DateTime();

        for (ItemDeudaInteresado item : itemsDeuda) {
            if (item.getTipo().equals("SUMA")) {
                montoPagar = montoPagar.add(item.getMonto());
                montoTotal = montoTotal.add(item.getMonto());
            } else {
                montoPagar = montoPagar.subtract(item.getMonto());
                montoDscto = montoDscto.add(item.getMonto());
            }
        }

        CuentaBancaria cta = cuentaBancariaDAO.findByCodigo(codigoCta);
        DeudaInteresado deuda = deudaInteresadoDAO.findActivaByInteresadoCtaBanco(postulante.getInteresado(), cta);
        postulante.setImportePagar(postulante.getImportePagar().add(montoPagar));
        postulante.setImporteTotal(postulante.getImporteTotal().add(montoTotal));
        postulante.setImporteDescuento(postulante.getImporteDescuento().add(montoDscto));

        if (deuda == null) {
            deuda = new DeudaInteresado();
            deuda.setInteresado(postulante.getInteresado());
            deuda.setAbono(BigDecimal.ZERO);
            deuda.setMontoAnterior(BigDecimal.ZERO);
            deuda.setMonto(montoPagar);
            deuda.setFechaRegistro(todayDT.toDate());
            deuda.setEstadoEnum(DeudaEstadoEnum.ACT);
            deuda.setPostulante(postulante);
            deuda.setCuentaBancaria(cta);
            deuda.setDescripcion(createDescripcionDeuda(itemsDeuda));
            deudaInteresadoDAO.save(deuda);

        } else {
            Acreencia acreencia = acreenciaDAO.findByCuentaAndTablaIns(
                    cta, NombreTablasEnum.FIN_DEUDA_INTERESADO,
                    deuda.getId(), DeudaEstadoEnum.DEU);

            acreencia.setEstadoEnum(DeudaEstadoEnum.ANU);
            acreencia.setFechaAnulacion(new Date());
            acreenciaDAO.update(acreencia);

            deuda.setMontoAnterior(deuda.getMonto());
            deuda.setMonto(deuda.getMonto().add(montoPagar));
            deuda.setPostulante(postulante);
            deuda.setDescripcion(createDescripcionDeuda(itemsDeuda));
            deudaInteresadoDAO.update(deuda);
        }

        for (ItemDeudaInteresado item : itemsDeuda) {
            item.setDeudaInteresado(deuda);
            itemDeudaInteresadoDAO.save(item);
        }

        Oficina oficinaCap = oficinaDAO.findByCodigo(OficinaEnum.CAP.name());

        Acreencia acreencia = new Acreencia();
        acreencia.setCuentaBancaria(cta);
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

    @Override
    public String createDescripcionDeuda(List<ItemDeudaInteresado> items) {

        if (items.isEmpty()) {
            return "";
        }
        if (items.size() == 1 && items.get(0).getConceptoPrecio() != null) {
            return items.get(0).getConceptoPrecio().getConceptoPago().getDescripcion();
        }

        String guiaPostul = null;
        String modalidad = null;
        for (ItemDeudaInteresado item : items) {
            logger.debug("item ID {}", item.getId());
            if (!item.getTipo().equals("SUMA")) {
                continue;
            }
            logger.debug("conceptoPrecio {}", item.getConceptoPrecio());
            logger.debug("conceptoPago ID {}", item.getConceptoPrecio().getConceptoPago().getCodigo());
            if (item.getConceptoPrecio().getConceptoPago().getCodigo().equals(CODE_PROSPECTO)) {
                guiaPostul = item.getConceptoPrecio().getConceptoPago().getDescripcion();
            }
            if (item.getConceptoPrecio().getConceptoPago().getModalidadIngreso() != null) {
                modalidad = item.getConceptoPrecio().getConceptoPago().getDescripcion();
            }
        }
        if (modalidad != null) {
            return modalidad + " Y OTROS";
        }
        if (guiaPostul != null) {
            return guiaPostul + " Y OTROS";
        }
        return "MODIFICACIÓN DE DATOS";
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

    private List<ItemDeudaInteresado> crearDeudaDerechoInscripcion(
            Postulante postulante,
            CicloPostula ciclo,
            EventoCiclo eventoExtemporaneo,
            List<DeudaInteresado> deudasAnteriores) {

        List<ItemDeudaInteresado> itemsDeuda = new ArrayList();

        Date todayD = new Date();

        logger.debug("MODALIDAD DE INGRESO {}", postulante.getModalidadIngreso());
        boolean exonerado = false;

        ConceptoPrecio precioOrigen = null;
        ConceptoPrecio precioModalidad = findPrecioByPostulante(postulante, ciclo);
        logger.debug("precioModalidad {}", precioModalidad);
        if (precioModalidad == null) {
            logger.debug("Precio de modalidad NULL - {}", postulante.getModalidadIngreso().getId());
            if (eventoExtemporaneo != null) {

                if (todayD.after(eventoExtemporaneo.getFechaInicio()) && !exonerado) {
                    ConceptoPrecio precio = findConceptoExtemporaneoByCiclo(ciclo);
                    DeudaInteresado deudaEx = new DeudaInteresado();
                    deudaEx.setCuentaBancaria(precio.getConceptoPago().getCuentaBancaria());

                    ItemDeudaInteresado itemDPena = new ItemDeudaInteresado();
                    itemDPena.setTipo("SUMA");
                    itemDPena.setEstado(EstadoEnum.ACT.name());
                    itemDPena.setMonto(precio.getMonto());
                    itemDPena.setFechaRegistro(new Date());
                    itemDPena.setConceptoPrecio(precio);
                    itemDPena.setDeudaInteresado(deudaEx);
                    itemsDeuda.add(itemDPena);
                }
            }
            return itemsDeuda;
        }

        DescuentoExamen dscto = null;
        MotivoDescuemtoEnum motivoDscto = null;
        logger.debug("PRECIOMODALIDAD {}", precioModalidad);

        ConceptoPago conceptoModalidad = precioModalidad.getConceptoPago();
        ConceptoPago conceptOrigen = conceptoModalidad.getConceptoOrigen() != null ? conceptoModalidad.getConceptoOrigen() : conceptoModalidad;
        CampagnaDescuento campDescuento = campagnaDescuentoDAO.findByCiclo(ciclo, postulante.getModalidadIngreso(), todayD, EstadoEnum.ACT);

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
        BigDecimal pagoAnterior = BigDecimal.ZERO;

        Evento eventoCepre = eventoDAO.findByCode(EventoEnum.CEPRE.name());

        List<EventoCiclo> eventosCepreByCiclo = eventoCicloDAO.allByEventoCiclo(eventoCepre, ciclo);
        List<Prelamolina> preLamolinas = prelamolinaDAO.allByDocumentoEsIngresante(postulante.getPersona(), ciclo, 1);

        if (!preLamolinas.isEmpty()) {
            logger.debug("es pre");

            for (Prelamolina pre : preLamolinas) {
                if (pre.getNumeroCiclo() == 0 || pre.getNumeroCiclo() == 1) {
                    exonerado = true;
                    logger.debug("extemporaneo exonerado por tener n-ciclo 0 o 1 ");
                }
            }

            int nEvnt = 1;
            boolean regOn = true;
            for (EventoCiclo evento : eventosCepreByCiclo) {
                logger.debug("buscando dentro del evento {}", evento.getEventoEnum().name());
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

                    if (nEvnt == 1
                            && todayD.compareTo(evento.getFechaInicio()) >= 0
                            && todayD.compareTo(evento.getFechaFin()) <= 0
                            && pre.getNumeroCiclo() == 0) {
                        regOn = false;
                    } else if (nEvnt == 2
                            && todayD.compareTo(evento.getFechaInicio()) >= 0
                            && todayD.compareTo(evento.getFechaFin()) <= 0
                            && pre.getNumeroCiclo() == 0) {
                        regOn = true;
                    } else if (pre.getNumeroCiclo() == 0) {
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

        if (postulante.getInteresado().getExoneradoExtemporaneo() && !exonerado) {
            logger.info("intereasado {} tiene flag de exoneracion", postulante.getInteresado().getId());
            exonerado = true;
        }

        logger.debug("exonerado == {}", exonerado);
        logger.debug("eventoCicloEx == {}", eventoExtemporaneo == null);

        if (!exonerado && !preLamolinas.isEmpty()) {
            if (eventoExtemporaneo == null || todayD.before(eventoExtemporaneo.getFechaInicio())) {

                ConceptoPrecio precio = findConceptoExtemporaneoByCiclo(ciclo);
                montoPenalidad = montoPenalidad.add(precio.getMonto());

                DeudaInteresado deudaEx = new DeudaInteresado();
                deudaEx.setCuentaBancaria(precio.getConceptoPago().getCuentaBancaria());

                ItemDeudaInteresado itemDPena = new ItemDeudaInteresado();
                itemDPena.setTipo("SUMA");
                itemDPena.setEstado(EstadoEnum.ACT.name());
                itemDPena.setMonto(precio.getMonto());
                itemDPena.setFechaRegistro(new Date());
                itemDPena.setConceptoPrecio(precio);
                itemsDeuda.add(itemDPena);
                itemDPena.setDeudaInteresado(deudaEx);
                itemsDeuda.add(itemDPena);

            }
        }

        if (eventoExtemporaneo != null) {

            if (todayD.after(eventoExtemporaneo.getFechaInicio()) && !exonerado) {
                ConceptoPrecio precio = findConceptoExtemporaneoByCiclo(ciclo);
                montoPenalidad = montoPenalidad.add(precio.getMonto());

                DeudaInteresado deudaEx = new DeudaInteresado();
                deudaEx.setCuentaBancaria(precio.getConceptoPago().getCuentaBancaria());

                ItemDeudaInteresado itemDPena = new ItemDeudaInteresado();
                itemDPena.setTipo("SUMA");
                itemDPena.setEstado(EstadoEnum.ACT.name());
                itemDPena.setMonto(precio.getMonto());
                itemDPena.setFechaRegistro(new Date());
                itemDPena.setConceptoPrecio(precio);
                itemDPena.setDeudaInteresado(deudaEx);
                itemsDeuda.add(itemDPena);
            }
        }

        Map<String, List<DeudaInteresado>> mapDeudasAnteriores = TypesUtil.convertListToMapList("estado", deudasAnteriores);
        if (mapDeudasAnteriores.get(EstadoEnum.ANU.name()) != null) {

            List<DeudaInteresado> deudasAnterior = mapDeudasAnteriores.get(EstadoEnum.ANU.name());
            for (DeudaInteresado deudaInteresado : deudasAnterior) {
                pagoAnterior = pagoAnterior.add(deudaInteresado.getAbono());
            }

            ItemDeudaInteresado itemDPena = new ItemDeudaInteresado();
            itemDPena.setTipo("RESTA");
            itemDPena.setEstado(EstadoEnum.ACT.name());
            itemDPena.setMonto(pagoAnterior);
            itemDPena.setFechaRegistro(new Date());
            itemsDeuda.add(itemDPena);

            montoTotal = montoTotal.subtract(pagoAnterior);

        }

        logger.debug("MONTO TOTAL A PAGAR {}", montoTotal);
        if (precioOrigen != null) {
            logger.debug("MONTO ANTERIOR A PAGAR {}", precioOrigen.getMonto());
        }

        DeudaInteresado deuda = new DeudaInteresado();
        deuda.setCuentaBancaria(precioModalidad.getConceptoPago().getCuentaBancaria());

        ItemDeudaInteresado itemDeuda = new ItemDeudaInteresado();
        itemDeuda.setConceptoPrecio(precioModalidad);
        itemDeuda.setEstadoEnum(DeudaEstadoEnum.ACT);
        itemDeuda.setFechaRegistro(new Date());
        itemDeuda.setMonto(precioModalidad.getMonto());
        itemDeuda.setDeudaInteresado(deuda);
        if (precioOrigen != null) {
            itemDeuda.setMonto(precioOrigen.getMonto());
        }
        itemDeuda.setTipo("SUMA");
        itemsDeuda.add(itemDeuda);

        return itemsDeuda;

    }

    private List<ItemDeudaInteresado> crearDeudaGuiaPostulante(Postulante postulante, CicloPostula ciclo, Date fechaVencimiento) {
        ModalidadIngreso modalidad = postulante.getModalidadIngreso();
        if (modalidad.isPreLaMolina() || ciclo.getEsSimulacro()) {
            return new ArrayList();
        }

        DateTime today = new DateTime();
        List<DeudaInteresado> deudas = deudaInteresadoDAO.allByInteresadoEstados(postulante.getInteresado(), Arrays.asList(DeudaEstadoEnum.ACT, DeudaEstadoEnum.PAG));

        ConceptoPrecio precioBD = findConceptoGuiaPostulanteFromDeudas(deudas);
        if (precioBD != null) {
            return new ArrayList();
        }

        ConceptoPrecio precio = findConceptoGuiaPostulanteByCiclo(TipoProspectoEnum.regular.toString(), ciclo);

        DeudaInteresado deuda = new DeudaInteresado();
        deuda.setCuentaBancaria(precio.getConceptoPago().getCuentaBancaria());

        List<ItemDeudaInteresado> itemsDeuda = new ArrayList();
        ItemDeudaInteresado itemDeuda = new ItemDeudaInteresado();
        itemDeuda.setMonto(precio.getMonto());
        itemDeuda.setDeudaInteresado(deuda);
        itemDeuda.setConceptoPrecio(precio);
        itemDeuda.setEstadoEnum(DeudaEstadoEnum.ACT);
        itemDeuda.setFechaRegistro(new Date());
        itemDeuda.setTipo("SUMA");
        itemsDeuda.add(itemDeuda);

        return itemsDeuda;
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

    private ConceptoPrecio findPrecioByPostulante(Postulante postulante, CicloPostula ciclo) {
        ConceptoPrecio precio = findPrecioByDataPostulante(
                postulante, postulante.getModalidadIngreso(), ciclo,
                postulante.getColegioProcedencia(), postulante.getColegioExtranjero(),
                postulante.getUniversidadProcedencia(), postulante.getUniversidadExtranjera()
        );
        logger.debug("PRECIO {}", precio);

        Prelamolina cepre = findNoIngresantePrelamolina(postulante, postulante.getModalidadIngreso(), ciclo);
        postulante.setPrelamolina(cepre);
        return precio;
    }

    @Override
    public ConceptoPrecio findPrecioByDataPostulante(
            Postulante postulante, ModalidadIngreso modalidadPostula, CicloPostula ciclo,
            Colegio colegio, String colegioExtranjero,
            Universidad universidad, String universidadExtranjera) {

        ModalidadIngresoCiclo modalidadCiclo = modalidadIngresoCicloDAO.findByModalidadIngresoCiclo(modalidadPostula, ciclo);
        modalidadPostula = modalidadCiclo.getModalidadIngreso();

        if (modalidadCiclo.getExoneradoPago() == 1 && !ciclo.getEsSimulacro()) {
            logger.debug("Modalidad con PAGO EXONERADO {}", modalidadCiclo.getId());
            logger.info("Modalidad con PAGO EXONERADO {}", modalidadCiclo.getId());
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
            if (universidad != null) {
                Universidad uniDB = universidadDAO.find(universidad.getId());
                return uniDB.getTipoGestion();
            }
            if (!StringUtils.isBlank(universidadExtranjera)) {
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

    private ConceptoPrecio findConceptoModalidadFromDeudas(List<DeudaInteresado> deudas, Postulante postulante) {
        List<DeudaInteresado> deudaz = Lists.reverse(deudas);
        for (DeudaInteresado deuda : deudaz) {
            Postulante postul = deuda.getPostulante();
            if (postul == null) {
                continue;
            }
            if (postul.getId().longValue() != postulante.getId()) {
                continue;
            }

            return deuda.getConceptoPrecio();
        }
        return null;
    }

    private ConceptoPrecio findConceptoGuiaPostulanteFromDeudas(List<DeudaInteresado> deudas) {
        for (DeudaInteresado deuda : deudas) {
            ConceptoPrecio conceptoPrecio = deuda.getConceptoPrecio();
            if (conceptoPrecio == null) {
                continue;
            }
            ConceptoPago conceptoPago = conceptoPrecio.getConceptoPago();
            if (conceptoPago.isProspecto()) {
                return conceptoPrecio;
            }
        }
        return null;
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

    @Override
    public Postulante findPostulanteActivoByInteresado(Interesado interesado) {
        return postulanteDAO.findActivoByInteresadoSimple(interesado);
    }

    @Override
    public void completarPostulante(Postulante postulante, Interesado interesadoSession, CicloPostula ciclo) {
        Interesado interesado = interesadoDAO.find(interesadoSession.getId());
        List<Interesado> interesados = interesadoDAO.allByFacebook(interesado.getFacebook());
        buscarPersonaPostulante(postulante, interesados, ciclo);
        if (postulante.getPersona() != null) {
            Persona persona = postulante.getPersona();
            if (StringUtils.isEmpty(persona.getPrimerNombre())) {
                persona.setPrimerNombre(getPrimerNombre(interesado.getNombres()));
                persona.setSegundoNombre(getSegundoNombre(interesado.getNombres()));
            }

            Prelamolina cepre = findIngresantePrelamolina(postulante);
            if (cepre != null) {
                verificarInfoContactoByCepre(persona, cepre);
            }
            verificarInfoContactoByInteresado(persona, interesado);

            return;
        }

        Persona persona = new Persona();
        persona.setPaterno(interesado.getPaterno());
        persona.setMaterno(interesado.getMaterno());

        persona.setPrimerNombre(getPrimerNombre(interesado.getNombres()));
        persona.setSegundoNombre(getSegundoNombre(interesado.getNombres()));
        persona.setNombres(interesado.getNombres());
        persona.unirNombres();
        persona.setPaisNacer(getPeru());
        persona.setPaisDomicilio(getPeru());
        persona.setNacionalidad(getPeru());

        Prelamolina cepre = findIngresantePrelamolina(postulante);
        if (cepre != null) {
            verificarInfoContactoByCepre(persona, cepre);
        }
        verificarInfoContactoByInteresado(persona, interesado);

        postulante.setPersona(persona);
    }

    private void verificarInfoContactoByInteresado(Persona persona, Interesado interesado) {
        if (interesado == null) {
            return;
        }
        if (StringUtils.isEmpty(persona.getEmail())) {
            persona.setEmail(interesado.getEmail());
        }
        if (StringUtils.isEmpty(persona.getCelular())) {
            persona.setCelular(interesado.getCelular());
        }
        if (StringUtils.isEmpty(persona.getTelefono())) {
            persona.setTelefono(interesado.getTelefono());
        }
    }

    private void verificarInfoContactoByCepre(Persona persona, Prelamolina cepre) {
        if (cepre == null) {
            return;
        }
        if (StringUtils.isEmpty(persona.getEmail())) {
            persona.setEmail(cepre.getEmail());
        }
        if (StringUtils.isEmpty(persona.getCelular())) {
            if (!StringUtils.isEmpty(cepre.getCelular())) {
                persona.setCelular(cepre.getCelular().split("/")[0]);
            }

        }
        if (StringUtils.isEmpty(persona.getTelefono())) {
            if (!StringUtils.isEmpty(cepre.getTelefono())) {
                persona.setTelefono(cepre.getTelefono().split("/")[0]);
            }

        }
    }

    private String getPrimerNombre(String nombres) {

        return nombres.trim().replaceAll("\\s+", " ").split(" ")[0];
    }

    private String getSegundoNombre(String nombres) {
        String[] array = nombres.trim().replaceAll("\\s+", " ").split(" ");
        array = ArrayUtils.remove(array, 0);
        return String.join(" ", array);
    }

    private void buscarPersonaPostulante(Postulante postulante, List<Interesado> interesados, CicloPostula ciclo) {
        if (interesados.isEmpty()) {
            return;
        }

        Persona persona = null;
        for (Interesado interesado : interesados) {
            List<Postulante> postuls = interesado.getPostulante();
            if (postuls == null) {
                continue;
            }
            if (postuls.isEmpty()) {
                continue;
            }
            for (Postulante postul : postuls) {
                if (postul.getPersona() == null) {
                    continue;
                }
                if (AdmisionConstantine.CODE_POSTULANTE_DUMMY.equals(postul.getPersona().getNumeroDocIdentidad())) {
                    continue;
                }
                persona = postul.getPersona();
                break;
            }
            if (persona != null) {
                break;
            }
        }

        postulante.setPersona(persona);
        if (persona == null) {
            return;
        }

        if (StringUtils.isEmpty(persona.getPrimerNombre())) {
            String segundoNombre = "";
            int indexName = persona.getNombres().split(" ").length;

            for (int i = 1; i < indexName; i++) {
                if (indexName >= 2) {
                    segundoNombre = segundoNombre + persona.getNombres().split(" ")[i] + " ";
                }
            }
            persona.setPrimerNombre(persona.getNombres().split(" ").length >= 1 ? persona.getNombres().split(" ")[0] : "");
            persona.setSegundoNombre(!segundoNombre.equals("") ? segundoNombre : "");
            persona.setPrimerNombre(persona.getPrimerNombre().trim());
            persona.setSegundoNombre(persona.getSegundoNombre().trim());
        }

        if (persona.getPaisNacer() == null) {
            persona.setPaisNacer(getPeru());
        }
        if (persona.getPaisDomicilio() == null) {
            persona.setPaisDomicilio(getPeru());
        }
        if (persona.getNacionalidad() == null) {
            persona.setNacionalidad(getPeru());
        }

    }

    @Override
    public ConceptoPrecio findConceptoGuiaPostulanteByCiclo(String tipoProspecto, CicloPostula ciclo) {
        logger.debug("tipoProspecto {}", TipoProspectoEnum.valueOf(tipoProspecto).getDisplayName());
        ConceptoPago concepto = conceptoPagoDAO.findByCodigo(TipoProspectoEnum.valueOf(tipoProspecto).getDisplayName());
        logger.debug("tipoProspecto {}", TipoProspectoEnum.valueOf(tipoProspecto).getDisplayName());
        if (concepto == null) {
            throw new PhobosException("No existe concepto para la guía del postulante");
        }
        ConceptoPrecio precio = conceptoPrecioDAO.findByConceptoCiclo(concepto, ciclo);
        logger.debug("CONCEPTO {} {}", concepto.getId(), ciclo.getId());
        if (precio == null) {
            throw new PhobosException("No se pudo determinar el monto de la guía del postulante");
        }

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

    private ConceptoPrecio findConceptoPrecioByCodigoCiclo(String codigo, CicloPostula ciclo) {
        ConceptoPago concepto = conceptoPagoDAO.findByCodigo(codigo);
        if (concepto == null) {
            throw new PhobosException("No existe concepto para el codigo " + codigo);
        }
        ConceptoPrecio precio = conceptoPrecioDAO.findByConceptoCiclo(concepto, ciclo);
        if (precio == null) {
            throw new PhobosException("No se pudo determinar el monto de " + concepto.getDescripcion());
        }

        return precio;
    }

    @Override
    public List<DeudaInteresado> allDeudaActivaByPostulante(Postulante postulante) {
        List<DeudaInteresado> deudas = deudaInteresadoDAO.allByInteresadoEstados(postulante.getInteresado(), Arrays.asList(DeudaEstadoEnum.ACT));
        List<DeudaInteresado> deudasNoPagadas = deudas.stream()
                .filter(deuda -> !deuda.isCancelada())
                .collect(Collectors.toList());

        if (deudasNoPagadas.isEmpty()) {
            return deudasNoPagadas;
        }

        List<ItemDeudaInteresado> items = itemDeudaInteresadoDAO.allByDeudasInteresadoEstados2(deudasNoPagadas, Arrays.asList(DeudaEstadoEnum.ACT));
        Map<Long, List<ItemDeudaInteresado>> mapItems = TypesUtil.convertListToMapList("deudaInteresado.id", items);
        for (DeudaInteresado deuda : deudas) {
            List<ItemDeudaInteresado> mapItem = mapItems.get(deuda.getId());
            deuda.setItemDeudaInteresado(mapItem);
        }

        return deudasNoPagadas;

    }

    @Override
    public List<DeudaInteresado> allBoletasByPostulante(Postulante postulante, List<DeudaEstadoEnum> estadosDeuda) {
        List<DeudaInteresado> deudas = deudaInteresadoDAO.allByInteresadoEstados(postulante.getInteresado(), estadosDeuda);

        List<ItemDeudaInteresado> items = itemDeudaInteresadoDAO.allByDeudasInteresadoEstados2(deudas, Arrays.asList(DeudaEstadoEnum.ACT));
        Map<Long, List<ItemDeudaInteresado>> mapItems = TypesUtil.convertListToMapList("deudaInteresado.id", items);
        for (DeudaInteresado deuda : deudas) {
            List<ItemDeudaInteresado> mapItem = mapItems.get(deuda.getId());
            deuda.setItemDeudaInteresado(mapItem);
        }

        return deudas;
    }

    private void completarDNI(Persona personaSession, Postulante postulanteForm) {
        if (personaSession == null) {
            logger.debug("salir personaSession == null");
            return;
        }
        String dni = limpiarValor(personaSession.getNumeroDocIdentidad());
        if (dni == null) {
            logger.debug("salir dni == null");
            return;
        }
        if (personaSession.getTipoDocumento() == null) {
            logger.debug("salir personaSession.getTipoDocumento() == null");
            return;
        }

        Persona personaForm = postulanteForm.getPersona();
        if (personaForm == null) {
            logger.debug("salir personaForm == null");
            return;
        }
        String dniForm = limpiarValor(personaForm.getNumeroDocIdentidad());
        if (dniForm != null) {
            logger.debug("salir dniForm != null");
            return;
        }
        if (personaForm.getTipoDocumento() != null) {
            logger.debug("salir personaForm.getTipoDocumento() != null");
            return;
        }

        logger.debug("exito completar info de DNI");
        personaForm.setTipoDocumento(personaSession.getTipoDocumento());
        personaForm.setNumeroDocIdentidad(dni);
    }

    private void verificarPersona(Postulante postulanteForm, CicloPostula ciclo) {
        Persona personaForm = postulanteForm.getPersona();
        personaForm.setNumeroDocIdentidad(limpiarValor(personaForm.getNumeroDocIdentidad()));
        if (personaForm.getTipoDocumento() == null || (personaForm.getTipoDocumento() != null && personaForm.getTipoDocumento().getId() == null)) {
            throw new PhobosException("Debe indicar el documento de identidad");
        }
        if (personaForm.getNumeroDocIdentidad() == null) {
            throw new PhobosException("Debe indicar el número del documento de identidad");
        }
        if (personaForm.getNumeroDocIdentidad().equals(AdmisionConstantine.CODE_POSTULANTE_DUMMY)) {
            throw new PhobosException("Este número de documento de identidad no está permitido");
        }

        TipoDocIdentidad tipoDoc = tipoDocIdentidadDAO.find(personaForm.getTipoDocumento().getId());
        if (tipoDoc.getLongitudExacta() == 1) {
            if (personaForm.getNumeroDocIdentidad().length() != tipoDoc.getLongitud()) {
                throw new PhobosException("El número de documento debe tener " + tipoDoc.getLongitud() + " caracteres");
            }
        } else if (tipoDoc.getLongitudExacta() == 0) {
            if (personaForm.getNumeroDocIdentidad().length() < 4) {
                throw new PhobosException("El número de documento debe tener como mínimo 4 caracteres");
            }
            if (personaForm.getNumeroDocIdentidad().length() > tipoDoc.getLongitud()) {
                throw new PhobosException("El número de documento debe tener como máximo " + tipoDoc.getLongitud() + " caracteres");
            }
        }

        Persona persona = personaDAO.findByDocumento(personaForm.getTipoDocumento(), personaForm.getNumeroDocIdentidad());
        if (persona == null) {
            return;
        }

        logger.debug("postulanteForm.getId() = {}", postulanteForm.getId());
        List<Postulante> postulantes = postulanteDAO.allByPersonaCiclo(persona, ciclo);
        if (postulanteForm.getId() != null) {
            logger.debug("mapeando los postulantes");
            Map<Long, Postulante> mapPostulantes = new LinkedHashMap();
            for (Postulante postulante : postulantes) {
                mapPostulantes.put(postulante.getId(), postulante);
            }
            logger.debug("buscando postulante form en map");
            Postulante postulanteBD = mapPostulantes.get(postulanteForm.getId());
            if (postulanteBD != null) {
                logger.debug("postulante del map se remueve, habia {} items", postulantes.size());
                postulantes.remove(postulanteBD);
                logger.debug("ahora hay {} items", postulantes.size());
            }
        }
        if (!postulantes.isEmpty()) {
            throw new PhobosException("Ya existe un postulante inscrito al proceso de admisión con este mismo documento de identidad. Corrobore su codigo de verificación.");
        }

        Postulante postulanteRenuncianteCepre = postulanteDAO.allByPersonaCicloRenuncianteCepre(persona, ciclo);

        if (postulanteRenuncianteCepre != null && !this.inscripcionRenunciante(ciclo)) {
            throw new PhobosException("Usted ha renunciado a su vacante.");
        }
        List<Postulante> postulanteAING = this.postulanteIngresoAnulado(ciclo, persona);
        if (!postulanteAING.isEmpty()) {
            CicloAcademico cicloPost = postulanteAING.get(0).getCicloPostula().getCicloAcademico();
            throw new PhobosException("Usted ha ingresado en el ciclo " + cicloPost.getDescripcion() + ", por lo tanto no puede postular en el ciclo actual. Comuníquese al " + AdmisionConstantine.CELULAR_HELPDESK + ".");
        }

        List<Alumno> alumnosExpulados = alumnoDAO.allExpuladosByDNI(postulanteForm.getPersona().getNumeroDocIdentidad());
        if (!alumnosExpulados.isEmpty()) {
            throw new PhobosException("Usted no puede postular porque ya existe como alumno con situación académica Separado");
        }
        List<Alumno> alumnosNoNormal = alumnoDAO.allNoNormalByDNI(postulanteForm.getPersona().getNumeroDocIdentidad());
        if (!alumnosNoNormal.isEmpty()) {
            throw new PhobosException("Usted no puede postular porque ya existe como alumno y no se encuentra en una situación académica Normal");
        }

        PostulanteBlacklist postulantebl = postulanteBlacklistDAO.findByDNI(postulanteForm.getPersona().getNumeroDocIdentidad(), ciclo.getCicloAcademico());
        if (postulantebl != null) {
            if (postulantebl.getCicloFin() == null) {
                throw new PhobosException("Usted se encuentra bloqueado del proceso de admisión indenifitivamente por: "
                        + postulantebl.getTipoCastigoEnum());
            }
            if (postulantebl.getCicloFin().getCicloAcademico().getCodigoInt() >= ciclo.getCicloAcademico().getCodigoInt()) {
                throw new PhobosException("Usted se encuentra bloqueado del proceso de admisión hasta el: "
                        + postulantebl.getCicloFin().getCicloAcademico().getDescripcion2() + " por: " + postulantebl.getTipoCastigoEnum());
            }
        }

        postulanteForm.getPersona().setPaterno(persona.getPaterno());
        postulanteForm.getPersona().setMaterno(persona.getMaterno());
        postulanteForm.getPersona().setNombres(persona.getNombres());
    }

    private List<Postulante> postulanteIngresoAnulado(CicloPostula ciclo, Persona persona) {
        CicloAcademico academico = ciclo.getCicloAcademico();
        List<CicloPostula> ciclos = new ArrayList();
        if (academico.getNumeroCiclo().equals("2")) {
            Integer year1 = academico.getYear();
            Integer numCiclo1 = Integer.valueOf(academico.getNumeroCiclo()) - 1;
            CicloPostula cp1 = cicloPostulaDAO.findRegularByYearNumeroCiclo(year1, numCiclo1);

            Integer year2 = academico.getYear() - 1;
            Integer numCiclo2 = Integer.valueOf(academico.getNumeroCiclo());
            CicloPostula cp2 = cicloPostulaDAO.findRegularByYearNumeroCiclo(year2, numCiclo2);

            ciclos = Arrays.asList(cp1, cp2);

        } else if (academico.getNumeroCiclo().equals("1")) {
            Integer year = academico.getYear() - 1;
            Integer numCiclo1 = Integer.valueOf(academico.getNumeroCiclo()) + 1;
            CicloPostula cp1 = cicloPostulaDAO.findRegularByYearNumeroCiclo(year, numCiclo1);

            Integer numCiclo2 = Integer.valueOf(academico.getNumeroCiclo());
            CicloPostula cp2 = cicloPostulaDAO.findRegularByYearNumeroCiclo(year, numCiclo2);
            ciclos = Arrays.asList(cp1, cp2);
        }
        List<Postulante> postulanteAING = postulanteDAO.allIngresoAnuladoByPersonaCiclo(persona, ciclos);
        return postulanteAING;
    }

    @Override
    public Boolean verificarPagoGuiaPostulante(Postulante postulante) {
        boolean debePagarGuiaPostulante = false;
        boolean pagoInscripcion = false;

        if (postulante.getCicloPostula().getId() >= AdmisionConstantine.NEW_CICLO) {

            List<DeudaInteresado> deudas = deudaInteresadoDAO.allByInteresadoEstado(postulante.getInteresado(), DeudaEstadoEnum.ACT);
            List<ItemDeudaInteresado> items = itemDeudaInteresadoDAO.allByDeudasInteresadoEstados(deudas, Arrays.asList(DeudaEstadoEnum.ACT));
            for (ItemDeudaInteresado item : items) {
                if (item.getConceptoPrecio() == null) {
                    continue;
                }
                ConceptoPago concepto = item.getConceptoPrecio().getConceptoPago();
                if (concepto.isProspecto()) {
                    debePagarGuiaPostulante = true;
                    if (item.getMonto().compareTo(item.getDeudaInteresado().getAbono()) == 0) {
                        return true;
                    }
                } else if (concepto.haveModalidadIngreso()) {
                    pagoInscripcion = (item.getMonto().compareTo(item.getDeudaInteresado().getAbono()) == 0);
                }
            }

            ModalidadIngreso modalidad = postulante.getModalidadIngreso();

            if (modalidad != null) {
                if (!modalidad.isPreLaMolina()) {
                    return !debePagarGuiaPostulante;
                }
            }

            if (pagoInscripcion) {
                return !debePagarGuiaPostulante;
            }

        } else {

            List<DeudaInteresado> deudas = deudaInteresadoDAO.allByInteresadoEstado(postulante.getInteresado(), DeudaEstadoEnum.ACT);
            for (DeudaInteresado deuda : deudas) {
                ConceptoPago concepto = deuda.getConceptoPrecio().getConceptoPago();
                if (concepto.isProspecto()) {
                    debePagarGuiaPostulante = true;
                    if (deuda.getMonto().compareTo(deuda.getAbono()) == 0) {
                        return true;
                    }
                } else if (concepto.haveModalidadIngreso()) {
                    pagoInscripcion = (deuda.getMonto().compareTo(deuda.getAbono()) == 0);
                }
            }

            ModalidadIngreso modalidad = postulante.getModalidadIngreso();

            if (modalidad != null) {
                if (!modalidad.isPreLaMolina()) {
                    return !debePagarGuiaPostulante;
                }
            }

            if (pagoInscripcion) {
                return !debePagarGuiaPostulante;
            }
        }

        return false;
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

    @Async
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendEmailBoletaPagoAsync(Postulante postulante, CicloPostula ciclo) {
        long t1 = System.currentTimeMillis();
        for (;;) {
            long t2 = System.currentTimeMillis();
            if (t2 - t1 > 500) {
                break;
            }
        }
        sendEmailBoletaPago(postulante, ciclo);
    }

    @Override
    public void sendEmailBoletaPago(Postulante postulanteForm, CicloPostula ciclo) {
        Postulante postulanteBD = postulanteDAO.find(postulanteForm.getId());
        ModalidadIngresoCiclo modalidadCiclo = null;
        if (postulanteBD.getModalidadIngreso() != null) {
            modalidadCiclo = modalidadIngresoCicloDAO.findByModalidadIngresoCiclo(postulanteBD.getModalidadIngreso(), ciclo);
        }

        ContenidoCarta contenidoCarta = findCartaEmail(postulanteBD, modalidadCiclo);
        List<DeudaInteresado> deudas = allDeudaActivaByPostulante(postulanteBD);

        for (DeudaInteresado deuda : deudas) {
            logger.debug("Cta Banco {} numero monto {}", deuda.getCuentaBancaria().getBanco(), deuda.getCuentaBancaria().getNumero(), deuda.getMonto());
            for (ItemDeudaInteresado item : deuda.getItemDeudaInteresado()) {
                logger.debug("\tConcepto {}", item.getConceptoPrecio() == null ? "DESCUENTO" : item.getConceptoPrecio().getConceptoPago().getDescripcion());
                logger.debug("\tMonto {}", item.getMonto());
            }
        }

        if (!deudas.isEmpty()) {
            mailerService.enviarPagoPostulante(postulanteBD, deudas, ciclo, contenidoCarta);
        }
    }

    private ContenidoCarta findCartaEmail(Postulante postulante, ModalidadIngresoCiclo modalidadCiclo) {
        if (postulante.getModalidadIngreso() == null) {
            return contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.HEAD_BOLETAEMAIL_GUIA);
        }

        if (postulante.isInscrito()) {
            return contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.HEAD_BOLETAEMAIL_CAMBIOS);
        }

        if (postulante.getModalidadIngreso().isPreLaMolina()) {
            Prelamolina cepre = prelamolinaDAO.findByPostulante(postulante);
            if (cepre.getNumeroCiclo() == 0) {
                return contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.HEAD_BOLETAEMAIL_CEPRE_INT);
            } else {
                return contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.HEAD_BOLETAEMAIL_CEPRE_REG);
            }
        }

        if (postulante.getModalidadIngreso().isPir()) {
            return contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.HEAD_BOLETAEMAIL_PIR);
        }
        if (modalidadCiclo.getRindeExamenAdmision() == 0) {
            return contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.HEAD_BOLETAEMAIL_NOEXAMEN);
        }

        return contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.HEAD_BOLETAEMAIL_OTROS_MODS);

    }

    @Override
    public Pais getPeru() {
        return paisDAO.findByCode(AdmisionConstantine.CODIGO_PERU);
    }

    @Override
    public ContenidoCarta findContenidoCartaByCodigoEnum(ContenidoCartaEnum contenidoCartaEnum) {
        return contenidoCartaDAO.findByCodigoEnum(contenidoCartaEnum);
    }

    @Override
    public Boolean esFinalInscripciones(CicloPostula ciclo) {
        List<EventoCiclo> eventosInscripciones = eventoCicloDAO.allEventoInscripcionesByCiclo(ciclo);
        Date ultimaFecha = eventosInscripciones.get(0).getFechaFin();
        for (EventoCiclo evento : eventosInscripciones) {
            if (evento.getFechaFin().compareTo(ultimaFecha) > 0) {
                ultimaFecha = evento.getFechaFin();
            }
        }

        Date hoy = new Date();
        return hoy.compareTo(ultimaFecha) > 0;

    }

    private Boolean inscripcionRenunciante(CicloPostula ciclo) {
        List<EventoCiclo> eventosInscripciones = eventoCicloDAO.allEventoInscripcionesByCiclo(ciclo);
        Date hoy = new Date();
        EventoCiclo eventoCicloCepre = eventosInscripciones.stream().filter(x
                -> x.getEventoEnum() == CEPRE
                && hoy.compareTo(x.getFechaInicio()) >= 0
                && hoy.compareTo(x.getFechaFin()) <= 0
        ).findAny().orElse(null);

        EventoCiclo eventoCicloNoCepre = eventosInscripciones.stream().filter(x -> x.getEventoEnum() != CEPRE
                && hoy.compareTo(x.getFechaInicio()) >= 0
                && hoy.compareTo(x.getFechaFin()) <= 0
        ).findAny().orElse(null);
        if (eventoCicloCepre != null && eventoCicloNoCepre == null) {
            return false;
        }
        return true;
    }

    @Override
    public ModalidadIngresoCiclo findModalidadCiclo(ModalidadIngreso modalidadIngreso, CicloPostula ciclo) {
        if (modalidadIngreso == null) {
            return null;
        }
        return modalidadIngresoCicloDAO.findByModalidadIngresoCiclo(modalidadIngreso, ciclo);
    }

    @Override
    public MetalesPostulante findMetalPostulanteByPostulante(Postulante postulante) {
        return metalesPostulanteDAO.findByPostulante(postulante);
    }

    @Override
    @Transactional
    public void saveMetalesPostulante(MetalesPostulante metalesPostulanteForm) {
        MetalesPostulante metalesPostulanteDB = metalesPostulanteDAO.findByPostulante(metalesPostulanteForm.getPostulante());
        if (!StringUtils.isBlank(metalesPostulanteForm.getOpcion()) && !metalesPostulanteForm.getOpcion().equals("NO")) {
            if (metalesPostulanteDB == null) {
                metalesPostulanteDAO.save(metalesPostulanteForm);

            } else {
                metalesPostulanteDB.setExplicacion(metalesPostulanteForm.getExplicacion());
                metalesPostulanteDB.setObjeto(metalesPostulanteForm.getObjeto());
                metalesPostulanteDAO.update(metalesPostulanteDB);
            }
        } else if (metalesPostulanteDB != null) {
            metalesPostulanteDAO.delete(metalesPostulanteDB);
        }

    }

    @Override
    @Transactional
    public void revisarDeudasCompletas(Postulante postulanteForm, CicloPostula ciclo) {
        Postulante postulante = postulanteDAO.find(postulanteForm.getId());
        List<DeudaInteresado> deudas = deudaInteresadoDAO.allByInteresadoEstado(postulante.getInteresado(), DeudaEstadoEnum.ACT);
        if (deudas.isEmpty()) {
            logger.debug("No tiene deudas");
            return;
        }

        Interesado interesado = postulante.getInteresado();
        boolean pagoProspecto = false;
        boolean pagoInscripcion = false;
        boolean pagoExtemporaneo = false;
        boolean debePagarProspecto = false;
        boolean debePagarInscripcion = false;
        boolean debePagarExtemporaneo = false;

        ModalidadIngreso modalidad = postulante.getModalidadIngreso();
        if (modalidad != null) {
            ModalidadIngresoCiclo mic = modalidadIngresoCicloDAO.findByModalidadIngresoCiclo(modalidad, ciclo);
            if (mic.getRindeExamenAdmision() == 1) {
                EventoCiclo examen = findEventoExamen(ciclo);
                Date fechaExamen = examen.getFechaInicio();
                if (new Date().after(fechaExamen)) {
                    logger.debug("No se revisan las deudas porque ya paso el examen");
                    return;
                }
            }
        }

        for (DeudaInteresado deuda : deudas) {
            if (deuda.getEstadoEnum() != DeudaEstadoEnum.ACT) {
                continue;
            }

            Postulante postul = deuda.getPostulante();
            if (postul != null) {
                if (postul.getId().longValue() != postulante.getId()) {
                    continue;
                }
            }

            ConceptoPago concepto = deuda.getConceptoPrecio().getConceptoPago();
            if (concepto.isSinDescripcion()) {
            } else if (concepto.isProspecto()) {
                debePagarProspecto = true;
                pagoProspecto = (deuda.getAbono().compareTo(deuda.getMonto()) >= 0);
            } else if (concepto.haveModalidadIngreso()) {
                debePagarInscripcion = true;
                pagoInscripcion = (deuda.getAbono().compareTo(deuda.getMonto()) >= 0);
            } else if (concepto.isExtemporaneo()) {
                debePagarExtemporaneo = true;
                pagoExtemporaneo = (deuda.getAbono().compareTo(deuda.getMonto()) >= 0);
            }
        }

        if (interesado.getEstadoEnum() == POST && !(pagoInscripcion || !debePagarInscripcion)) {
            Date hoy = new Date();
            EventoCiclo eventoCiclo = findEventoExtemporaneo(ciclo);
            if (eventoCiclo != null && hoy.after(eventoCiclo.getFechaInicio())) {
                debePagarExtemporaneo = true;
            }
        }

        logger.debug("pagoProspecto {}", pagoProspecto);
        logger.debug("pagoInscripcion {}", pagoInscripcion);
        logger.debug("pagoExtemporaneo {}", pagoExtemporaneo);
        logger.debug("debePagarProspecto {}", debePagarProspecto);
        logger.debug("debePagarInscripcion {}", debePagarInscripcion);
        logger.debug("debePagarExtemporaneo {}", debePagarExtemporaneo);

        if (interesado.getEstadoEnum() == REG && pagoProspecto) {
            interesado.setEstado(PROS);
            interesadoDAO.update(interesado);
            postulante.setEstado(PostulanteEstadoEnum.PROS);
            postulanteDAO.update(postulante);
            return;
        }

        if (interesado.getEstadoEnum() == PROS && !pagoProspecto && debePagarProspecto) {
            interesado.setEstado(REG);
            interesadoDAO.update(interesado);
            postulante.setEstado(PostulanteEstadoEnum.CRE);
            postulanteDAO.update(postulante);
            return;
        }

        if (interesado.getEstadoEnum() == POST
                && postulante.getEstadoEnum() == PostulanteEstadoEnum.PRE
                && (pagoProspecto || !debePagarProspecto)
                && (pagoInscripcion || !debePagarInscripcion)
                && (pagoExtemporaneo || !debePagarExtemporaneo)) {
            postulante.setEstado(PostulanteEstadoEnum.PAGO);
            if (postulante.getFechaEncuesta() != null && !modalidad.isPreLaMolina()) {
                postulante.setEstado(PostulanteEstadoEnum.INS);
            } else if (postulante.getFechaEncuesta() != null && modalidad.isPreLaMolina()) {
                postulante.setEstado(PostulanteEstadoEnum.ING);
            }
            postulanteDAO.update(postulante);
            return;
        }

        if (interesado.getEstadoEnum() == POST
                && Arrays.asList(PostulanteEstadoEnum.PAGO, PostulanteEstadoEnum.INS).contains(postulante.getEstadoEnum())
                && ((!pagoProspecto && debePagarProspecto)
                || (!pagoInscripcion && debePagarInscripcion)
                || (!pagoExtemporaneo && debePagarExtemporaneo))) {
            postulante.setEstado(PostulanteEstadoEnum.PRE);
            postulanteDAO.update(postulante);
            return;
        }

        if (interesado.getEstadoEnum() == PROS
                && postulante.getEstadoEnum() == PostulanteEstadoEnum.PROS
                && (pagoProspecto || !debePagarProspecto)
                && (pagoInscripcion || !debePagarInscripcion)
                && (pagoExtemporaneo || !debePagarExtemporaneo)) {
            if (modalidad == null) {
                interesado.setEstado(PROS);
            } else {
                interesado.setEstado(POST);
            }
            interesadoDAO.update(interesado);
            postulante.setEstado(PostulanteEstadoEnum.PAGO);
            postulanteDAO.update(postulante);
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

    @Override
    @Transactional
    public void actualizarImportes(Postulante postulanteForm, CicloPostula ciclo) {
        BigDecimal abonado = BigDecimal.ZERO;
        BigDecimal descuento = BigDecimal.ZERO;
        BigDecimal pagar = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal utilizado = BigDecimal.ZERO;

        Postulante postulante = postulanteDAO.find(postulanteForm.getId());
        List<DeudaInteresado> deudas = deudaInteresadoDAO.allUtilizablesByInteresado(postulante.getInteresado(), ciclo);
        if (deudas.isEmpty()) {
            logger.debug("No tiene deudas");
            postulante.setImporteAbonado(abonado);
            postulante.setImporteDescuento(descuento);
            postulante.setImportePagar(pagar);
            postulante.setImporteTotal(total);
            postulante.setImporteUtilizado(utilizado);
            postulanteDAO.update(postulante);
            return;
        }

        List<DeudaEstadoEnum> estados = Arrays.asList(DeudaEstadoEnum.ACT, DeudaEstadoEnum.PAG);
        List<ItemDeudaInteresado> itemsDeudas = itemDeudaInteresadoDAO.allByDeudasInteresadoEstados(deudas, estados);
        for (ItemDeudaInteresado itemDeuda : itemsDeudas) {
            if (itemDeuda.getTipo().equals("RESTA")) {
                descuento = descuento.add(itemDeuda.getMonto());
            }
        }

        for (DeudaInteresado deuda : deudas) {
            pagar = pagar.add(deuda.getMonto());
            abonado = abonado.add(deuda.getAbono());
        }

        total = pagar.add(descuento);

        postulante.setImporteAbonado(abonado);
        postulante.setImporteDescuento(descuento);
        postulante.setImportePagar(pagar);
        postulante.setImporteTotal(total);
        postulante.setImporteUtilizado(utilizado);
        postulanteDAO.update(postulante);
    }

    @Override
    public List<ConceptoPrecio> allConceptoPrecioCambiosByCiclo(Postulante postulante, CicloPostula ciclo) {

        DeudaInteresado deudaPagada = deudaInteresadoDAO.findDeudaPagada(postulante);

        Date today = new Date();

        EventoCiclo eventoCi = eventoCicloDAO.findActive(ciclo, today, EventoEnum.EXAM);
        Integer ai = deudaPagada == null ? 1 : 0;
        Integer ae = eventoCi != null ? 1 : 0;

        return conceptoPrecioDAO.allCambiosByCiclo(ciclo, ai, ae);
    }

    @Override
    public DeudaInteresado findDeudaPagada(Postulante postulante) {
        return deudaInteresadoDAO.findDeudaPagada(postulante);
    }

    @Override
    public List<ModalidadIngresoCiclo> allModalidadIngresoCicloByCicloModalidades(CicloPostula ciclo, List<ModalidadIngreso> modalidades) {

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

    @Override
    public List<SolicitudCambioInfo> allSolicitudCambioInfoByPostulante(Postulante postulante, List<SolicitudCambioInfoEstadoEnum> estados) {
        return solicitudCambioInfoDAO.allByPostulanteEstados(postulante, estados);
    }

    @Override
    public List<Colegio> allColegioCoar() {
        return colegioDAO.allCoarSecundaria();
    }

    @Override
    public ContenidoCarta findHeaderBoletaWeb(Postulante postulante, ModalidadIngresoCiclo modalidadCiclo) {
        ModalidadIngreso modalidad = postulante.getModalidadIngreso();
        if (modalidad == null) {
            return contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.HEAD_BOLETA_GUIA);

        } else if (modalidad.isPreLaMolina()) {
            Prelamolina cepre = prelamolinaDAO.findByPostulante(postulante);
            if (cepre.getNumeroCiclo() == 0) {
                return contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.HEAD_BOLETA_CEPRE_INT);
            } else {
                return contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.HEAD_BOLETA_CEPRE_REG);
            }

        } else if (modalidad.isPir()) {
            return contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.HEAD_BOLETA_PIR);

        } else if (modalidadCiclo.getRindeExamenAdmision() == 0) {
            return contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.HEAD_BOLETA_NOEXAMEN);
        }

        return contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.HEAD_BOLETA_OTROS_MODS);
    }

    @Override
    public ContenidoCarta findFooterBoletaWeb(Postulante postulante, ModalidadIngresoCiclo modalidadCiclo) {
        ModalidadIngreso modalidad = postulante.getModalidadIngreso();
        if (modalidad == null) {
            return contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.FOOT_BOLETA_GUIA);

        } else if (modalidad.isPreLaMolina()) {
            Prelamolina cepre = prelamolinaDAO.findByPostulante(postulante);
            if (cepre.getNumeroCiclo() == 0) {
                return contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.FOOT_BOLETA_CEPRE_INT);
            } else {
                return contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.FOOT_BOLETA_CEPRE_REG);
            }

        } else if (modalidad.isPir()) {
            return contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.FOOT_BOLETA_PIR);

        } else if (modalidadCiclo.getRindeExamenAdmision() == 0) {
            return contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.FOOT_BOLETA_NOEXAMEN);
        }

        return contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.FOOT_BOLETA_OTROS_MODS);
    }

    @Override
    public ContenidoCarta findHeaderBoletaPdf(Postulante postulante, ModalidadIngresoCiclo modalidadCiclo) {
        ModalidadIngreso modalidad = postulante.getModalidadIngreso();
        if (modalidad == null) {
            return contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.HEAD_BOLETAPDF_GUIA);
        }

        if (Arrays.asList(PAGO, INS, ING).contains(postulante.getEstadoEnum())) {
            return contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.HEAD_BOLETAPDF_CAMBIOS);
        }

        if (modalidad.isPreLaMolina()) {
            Prelamolina cepre = prelamolinaDAO.findByPostulante(postulante);
            if (cepre.getNumeroCiclo() == 0) {
                return contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.HEAD_BOLETAPDF_CEPRE_INT);
            } else {
                return contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.HEAD_BOLETAPDF_CEPRE_REG);
            }

        } else if (modalidad.isPir()) {
            return contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.HEAD_BOLETAPDF_PIR);

        } else if (modalidadCiclo.getRindeExamenAdmision() == 0) {
            return contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.HEAD_BOLETAPDF_NOEXAMEN);
        }

        return contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.HEAD_BOLETAPDF_OTROS_MODS);
    }

    @Override
    public ContenidoCarta findFooterBoletaPdf(Postulante postulante, ModalidadIngresoCiclo modalidadCiclo) {
        ModalidadIngreso modalidad = postulante.getModalidadIngreso();
        if (modalidad == null) {
            return contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.FOOT_BOLETAPDF_GUIA);
        }

        if (Arrays.asList(PAGO, INS, ING).contains(postulante.getEstadoEnum())) {
            return contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.FOOT_BOLETAPDF_CAMBIOS);
        }

        if (modalidad.isPreLaMolina()) {
            Prelamolina cepre = prelamolinaDAO.findByPostulante(postulante);
            if (cepre.getNumeroCiclo() == 0) {
                return contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.FOOT_BOLETAPDF_CEPRE_INT);
            } else {
                return contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.FOOT_BOLETAPDF_CEPRE_REG);
            }

        } else if (modalidad.isPir()) {
            return contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.FOOT_BOLETAPDF_PIR);

        } else if (modalidadCiclo.getRindeExamenAdmision() == 0) {
            return contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.FOOT_BOLETAPDF_NOEXAMEN);
        }

        return contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.FOOT_BOLETAPDF_OTROS_MODS);
    }

    @Override
    public ContenidoCarta getContenidoCartaTerms(Postulante postulante, ModalidadIngresoCiclo modalidadCiclo) {
        String contenido = "";
        CicloAcademico ciclo = modalidadCiclo.getCicloPostula().getCicloAcademico();
        ContenidoCarta contenidoCarta = null;
        if (modalidadCiclo.getRindeExamenAdmision() == 1) {
            Persona persona = postulante.getPersona();
            String identi = persona.getSexoEnum() == SexoEnum.F ? "identificada"
                    : (persona.getSexoEnum() == SexoEnum.M ? "identificado" : "identificado(a)");

            ContenidoCarta carta = contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.CARTA);
            contenido = carta.getContenido();

            contenido = contenido.replace(CICLO_ACADEMICO.getValue(), ciclo.getDescripcion());
            contenido = contenido.replace(IDENTIFICADO.getValue(), identi);

            if (postulante.getPersona().getNumeroDocIdentidad().equals(AdmisionConstantine.CODE_POSTULANTE_DUMMY)) {
                Interesado interesado = postulante.getInteresado();

                contenido = contenido.replace(TIPO_DOCUMENTO.getValue(), interesado.getTipoDocumento().getSimbolo());
                contenido = contenido.replace(NRO_DOCUMENTO.getValue(), interesado.getNumeroDocIdentidad());
                contenido = contenido.replace(NOMBRE_PERSONA.getValue(), interesado.getNombreCompleto());

            } else {
                contenido = contenido.replace(NOMBRE_PERSONA.getValue(), persona.getNombreCompleto());
                contenido = contenido.replace(TIPO_DOCUMENTO.getValue(), persona.getTipoDocumento().getSimbolo());
                contenido = contenido.replace(NRO_DOCUMENTO.getValue(), persona.getNumeroDocIdentidad());

            }

            carta.setContenido(contenido);
            contenidoCarta = carta;
        } else {
            ContenidoCarta carta = contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.TERMINOSSINEXAMEN);
            contenidoCarta = carta;
        }

        return contenidoCarta;
    }

    @Override
    public ContenidoCarta getContenido(Postulante postulante, ContenidoCartaEnum tipoContenido) {
        ContenidoCarta carta = contenidoCartaDAO.findByCodigoEnum(tipoContenido);
        return carta;
    }

    @Override
    @Transactional
    public void saveTerminos(Postulante postulanteForm, Long idCarta, HttpSession session) {

        List<TerminosPostulante> terminosPostulantes = terminosPostulante.allByPostulante(postulanteForm);

        TerminosPostulante termPostulante = terminosPostulantes.stream().filter(x -> x.getContenidoCarta().getId().equals(idCarta)).findAny().orElse(null);

        if (termPostulante == null) {
            termPostulante = new TerminosPostulante();
            termPostulante.setContenidoCarta(new ContenidoCarta(idCarta));
            termPostulante.setPostulante(postulanteForm);
            termPostulante.setFechaRegistro(new Date());
            terminosPostulante.save(termPostulante);
        }
    }

}
