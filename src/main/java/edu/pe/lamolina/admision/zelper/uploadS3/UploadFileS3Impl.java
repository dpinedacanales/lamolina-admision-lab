package edu.pe.lamolina.admision.zelper.uploadS3;

import edu.pe.lamolina.admision.config.DespliegueConfig;
import fr.opensagres.poi.xwpf.converter.core.utils.StringUtils;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.albatross.zelpers.cloud.storage.StorageService;
import pe.albatross.zelpers.file.model.Inode;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.AdmisionConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;

@Service
public class UploadFileS3Impl implements UploadFileS3 {

    @Autowired
    DespliegueConfig despliegueConfig;

    @Autowired
    StorageService swiftService;

    @Override
    public void uploadSync(String remoteDirectory, String localDirectory, String fileName, Boolean publico) {
        if (despliegueConfig.getStorage()) {
            swiftService.uploadFileSync(AdmisionConstantine.S3_BUCKET_ADMISION, remoteDirectory, localDirectory, fileName, publico);
        } else {
            if (remoteDirectory.startsWith("trash")) {
                swiftService.uploadFileSync(AdmisionConstantine.S3_BUCKET_ADMISION, remoteDirectory, localDirectory, fileName, publico);
            } else {
                swiftService.uploadFileSync(AdmisionConstantine.S3_BUCKET_ADMISION, GlobalConstantine.S3_TRASH, localDirectory, fileName, publico);
            }
        }
    }

    @Override
    public InputStream getProspecto(String rutaProspecto) {
        return swiftService.getFile(AdmisionConstantine.S3_BUCKET_ADMISION, AdmisionConstantine.S3_PROSPECTO_DIR, rutaProspecto);
    }

    @Override
    public void deleteProspecto(String rutaProspecto) {
        if (despliegueConfig.getStorage()) {
            swiftService.deleteFile(AdmisionConstantine.S3_BUCKET_ADMISION, AdmisionConstantine.S3_PROSPECTO_DIR, rutaProspecto);

        } else {

            if (!StringUtils.isEmpty(rutaProspecto) && rutaProspecto.startsWith("trash")) {
                swiftService.deleteFile(AdmisionConstantine.S3_BUCKET_ADMISION, AdmisionConstantine.S3_PROSPECTO_DIR, rutaProspecto);
            }
        }

    }

    @Override
    public void deleteFile(String dirName, String fileName) {
        if (despliegueConfig.getStorage()) {
            swiftService.deleteFile(AdmisionConstantine.S3_BUCKET_ADMISION, dirName, fileName);
        } else {
            if (fileName.startsWith("trash")) {
                swiftService.deleteFile(AdmisionConstantine.S3_BUCKET_ADMISION, dirName, fileName);
            }
        }
    }

    @Override
    public void deleteFile(String fileName) {
        if (despliegueConfig.getStorage()) {
            swiftService.deleteFile(AdmisionConstantine.S3_BUCKET_ADMISION, fileName);
        } else {
            if (fileName.startsWith("trash")) {
                swiftService.deleteFile(AdmisionConstantine.S3_BUCKET_ADMISION, fileName);
            }
        }
    }

    @Override
    public InputStream getFile(String dirName, String fileName) {
        return swiftService.getFile(AdmisionConstantine.S3_BUCKET_ADMISION, dirName, fileName);
    }

    @Override
    public InputStream getFile(String fileName) {
        return swiftService.getFile(AdmisionConstantine.S3_BUCKET_ADMISION, fileName);
    }

    @Override
    public boolean doesExist(String dirName, String fileName) {
        return swiftService.doesExist(AdmisionConstantine.S3_BUCKET_ADMISION, dirName, fileName);
    }

    @Override
    public Inode allFile(String dirName, Boolean publico) {
        if (despliegueConfig.getStorage()) {
            return swiftService.allFile(AdmisionConstantine.S3_BUCKET_ADMISION, dirName, publico);
        } else {

            if (dirName.startsWith("trash")) {
                return swiftService.allFile(AdmisionConstantine.S3_BUCKET_ADMISION, dirName, publico);

            } else {
                return swiftService.allFile(AdmisionConstantine.S3_BUCKET_ADMISION, GlobalConstantine.S3_TRASH, publico);

            }
        }
    }

    @Override
    public String getPathFile(String dirName, String fileName) {
        if (despliegueConfig.getStorage()) {
            return AdmisionConstantine.S3_URL_ADMISION + dirName + fileName;
        }
        return AdmisionConstantine.S3_URL_ADMISION + GlobalConstantine.S3_TRASH + fileName;
    }

    @Override
    public void createDirectory(String folderName) {
        if (despliegueConfig.getStorage()) {
            swiftService.createDirectory(AdmisionConstantine.S3_BUCKET_ADMISION, folderName);
        } else {
            if (folderName.startsWith("trash")) {
                swiftService.createDirectory(AdmisionConstantine.S3_BUCKET_ADMISION, folderName);
            }
        }

    }

}
