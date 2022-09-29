package edu.pe.lamolina.admision.controller.general.archivo;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import pe.edu.lamolina.model.general.Archivo;

public interface ArchivoService {

    List<Archivo> all(List<Long> ids);

    Archivo find(Long idFile);

    void deleteArchivo(Long archivo);

    String uploadFile(MultipartFile file);

    public String uploadAndCompressFile(MultipartFile file);

}
