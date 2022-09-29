/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pe.lamolina.admision.zelper.uploadS3;

import java.io.InputStream;
import pe.albatross.zelpers.file.model.Inode;

/**
 *
 * @author dpinedac
 */
public interface UploadFileS3 {

    void uploadSync(String remoteDirectory, String localDirectory, String fileName, Boolean publico);

    InputStream getProspecto(String rutaProspecto);

    void deleteProspecto(String rutaProspecto);

    void deleteFile(String dirName, String fileName);

    void deleteFile(String fileName);

    InputStream getFile(String dirName, String fileName);

    InputStream getFile(String fileName);

    boolean doesExist(String dirName, String fileName);

    Inode allFile(String dirName, Boolean publico);

    void createDirectory(String folderName);
    
    String getPathFile(String dirName, String fileName);
}
