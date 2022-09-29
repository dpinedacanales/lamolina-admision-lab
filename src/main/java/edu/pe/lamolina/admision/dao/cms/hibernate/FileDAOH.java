package edu.pe.lamolina.admision.dao.cms.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import edu.pe.lamolina.admision.dao.cms.FileDAO;
import pe.edu.lamolina.model.cms.File;

@Repository
public class FileDAOH extends AbstractEasyDAO<File> implements FileDAO {

    public FileDAOH() {
        super();
        setClazz(File.class);
    }
}
