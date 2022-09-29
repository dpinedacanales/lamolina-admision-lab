package edu.pe.lamolina.admision.zelper.asterisk;

import java.util.List;

public interface AsteriskService {
    
    void loginToAsterisk();
    
    List<String> allExtension();

    boolean getExtensionStatus(String extension);

    boolean generateDial(String extension, String numero);

}
