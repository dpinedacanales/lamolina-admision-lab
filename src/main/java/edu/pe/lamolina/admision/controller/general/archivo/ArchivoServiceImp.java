package edu.pe.lamolina.admision.controller.general.archivo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import edu.pe.lamolina.admision.dao.general.ArchivoDAO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.commons.io.FilenameUtils;
import pe.albatross.zelpers.file.system.FileHelper;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.general.Archivo;

@Service
@Transactional(readOnly = true)
public class ArchivoServiceImp implements ArchivoService {

    @Autowired
    ArchivoDAO archivoDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<Archivo> all(List<Long> ids) {
        return archivoDAO.all(ids);
    }

    @Override
    public Archivo find(Long idFile) {
        return archivoDAO.find(idFile);
    }

    @Override
    @Transactional
    public void deleteArchivo(Long archivo) {
        Archivo arch = archivoDAO.find(archivo);
        try {
            FileHelper.deleteFromDisk(arch.getRuta());
            archivoDAO.delete(arch);
        } catch (Exception e) {
            logger.debug("{} {} ", arch.getRuta(), e.getMessage());
        }

    }

    @Override
    public String uploadFile(MultipartFile multipartFile) {
        logger.debug("getContentType {}", multipartFile.getContentType());
        logger.debug("getOriginalFilename {}", multipartFile.getOriginalFilename());
        logger.debug("getSize {}", multipartFile.getSize());

        FileHelper.createDirectory(GlobalConstantine.TMP_DIR);
        String absoluteName = GlobalConstantine.TMP_DIR + multipartFile.getOriginalFilename();
        try {
            FileHelper.saveToDisk(multipartFile, absoluteName);
        } catch (Exception e) {
            logger.debug("ERROR AL GUARDAR EL ARCHIVO {}", absoluteName);
        }

        Archivo adjunto = new Archivo();
        adjunto.setTipo(multipartFile.getContentType());

        return multipartFile.getOriginalFilename();
    }

    @Override
    public String uploadAndCompressFile(MultipartFile multipart) {

        try {
            String fileName = multipart.getOriginalFilename();

            File convFile = new File(multipart.getOriginalFilename());

            String fileExt = TypesUtil.getClean(FilenameUtils.getExtension(fileName)).toLowerCase();

            Boolean enDirectorioTemporal = false;

            if ("png".equals(fileExt)) {
                BufferedImage bufferedImage = ImageIO.read(convFile);
                BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(),
                        bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
                newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);
                String fileNamee = TypesUtil.getUnixTime() + ".jpeg";

                ImageIO.write(newBufferedImage, "jpeg", new File(GlobalConstantine.TMP_DIR + fileNamee));

                fileName = fileNamee;

                enDirectorioTemporal = true;
            }

            File inputt = enDirectorioTemporal ? new File(GlobalConstantine.TMP_DIR + fileName) : convFile;

            BufferedImage srcImage = ImageIO.read(inputt);
            BufferedImage scaledImage = Scalr.resize(srcImage, 2500);
            String fileNameee = TypesUtil.getUnixTime() + ".jpeg";

            File outputfile = new File(GlobalConstantine.TMP_DIR + fileNameee);
            ImageIO.write(scaledImage, "jpeg", outputfile);

            return fileNameee;

        } catch (Exception ex) {
            throw new PhobosException("Error al guardar el archivo");
        }
    }

}
