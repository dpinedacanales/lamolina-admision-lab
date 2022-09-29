package edu.pe.lamolina.admision.controller.general.archivo;

import static com.helger.commons.io.stream.StreamHelper.close;
import java.beans.PropertyEditorSupport;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import edu.pe.lamolina.admision.config.DespliegueConfig;
import edu.pe.lamolina.admision.zelper.model.DataSessionAdmision;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import pe.albatross.zelpers.cloud.storage.StorageService;
import pe.edu.lamolina.model.constantines.AdmisionConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.constantines.GlobalMessages;
import pe.edu.lamolina.model.general.Archivo;

@Controller
@RequestMapping("archivo")
public class ArchivoController {

    @Autowired
    ArchivoService service;

    @Autowired
    StorageService storageService;

    @Autowired
    DespliegueConfig despliegueConfig;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {

        dataBinder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String value) {
                try {
                    setValue(new SimpleDateFormat("dd/MM/yyyy").parse(value));
                } catch (ParseException e) {
                    setValue(null);
                }
            }
        });

        dataBinder.registerCustomEditor(BigDecimal.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String value) {
                try {
                    setValue(new BigDecimal(value.replaceAll(",", "")));
                } catch (Exception e) {
                    setValue(null);
                }
            }
        });
    }

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
        model.addAttribute("ciclo", ds.getCicloPostula());
        return "inscripcion/taller/taller";
    }

    @RequestMapping("download/{archivoId}")
    public void download(@PathVariable Long archivoId, HttpServletResponse response) throws Exception {

        Archivo archivo = service.find(archivoId);
        String fileNameRoot = archivo.getRuta();
        String fileName = fileNameRoot.substring(fileNameRoot.lastIndexOf("/") + 1);
        String folder = despliegueConfig.getAmbiente().equalsIgnoreCase("prod") ? GlobalConstantine.S3_PUBLIC_DIR : GlobalConstantine.S3_TRASH;

        InputStream inputStream = null;
        File filex = new File(fileNameRoot);

        BufferedInputStream input = null;
        BufferedOutputStream output = null;

        if (despliegueConfig.getStorage()) {
                        if (storageService.doesExist(AdmisionConstantine.S3_BUCKET_ADMISION, folder, fileName)) {
                logger.debug("El archivo existe en archivos del s3");
                inputStream = storageService.getFile(AdmisionConstantine.S3_BUCKET_ADMISION, folder, fileName);
            } else {
                logger.debug("El archivo no existe en el s3");
                fileNameRoot = AdmisionConstantine.ADMISION_DIR + fileName;
                filex = new File(fileNameRoot);
                if (!filex.exists()) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                inputStream = new FileInputStream(filex);
            }
        } else {
            filex = new File(fileNameRoot);
            if (!filex.exists()) {
                fileNameRoot = AdmisionConstantine.ADMISION_DIR + fileName;
                filex = new File(fileNameRoot);
                if (!filex.exists()) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
            }
        }

        response.reset();
        response.setBufferSize(GlobalConstantine.DEFAULT_BUFFER_SIZE_DOWNLOAD);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "inline; filename=\"" + fileNameRoot.substring(fileNameRoot.lastIndexOf("/"), fileNameRoot.length()) + "\"");

        try {
            if (despliegueConfig.getStorage()) {
                input = new BufferedInputStream(inputStream);
            } else {
                input = new BufferedInputStream(new FileInputStream(filex));
            }
            output = new BufferedOutputStream(response.getOutputStream(), GlobalConstantine.DEFAULT_BUFFER_SIZE_DOWNLOAD);
            IOUtils.copy(input, output);
            response.flushBuffer();

        } finally {

            close(output);
            close(input);

        }
    }

    @ResponseBody
    @RequestMapping("delete")
    public JsonResponse delete(Long archivo, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);

        try {
            service.deleteArchivo(archivo);
            response.setMessage("Archivo Eliminado");

            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);

        }
        return response;
    }

    @ResponseBody
    @RequestMapping("uploadCompressFile")
    public JsonResponse uploadCompressFile(@RequestParam("file") MultipartFile file) {
        JsonResponse json = new JsonResponse();

        try {
            String fileName = service.uploadAndCompressFile(file);
            json.setMessage("Importación finalizada.");
            json.setData(fileName);
            json.setSuccess(true);

        } catch (Exception e) {
            json.setSuccess(false);
            json.setMessage(GlobalMessages.ERROR_GENERAL);
        }
        return json;

    }

    @ResponseBody
    @RequestMapping("uploadFile")
    public JsonResponse uploadFile(@RequestParam("file") MultipartFile file) {
        JsonResponse json = new JsonResponse();

        try {

            String fileName = service.uploadFile(file);

            json.setMessage("Importación finalizada.");
            json.setData(fileName);
            json.setSuccess(true);

        } catch (Exception e) {
            json.setSuccess(false);
            json.setMessage(GlobalMessages.ERROR_GENERAL);
        }
        return json;

    }
}
