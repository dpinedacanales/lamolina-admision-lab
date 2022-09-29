package edu.pe.lamolina.admision.zelper.constant;

import pe.albatross.zelpers.miscelanea.OSValidator;

public interface Constantine {

    String SESSION_USUARIO = "SESSION_USUARIO";

    String ADMISION_DIR = OSValidator.isWindows() ? "C:/tmp/" : "/sip/admision/";

    String CODIGO_PERU = "PE";
    
    Long MINUTE_TO_MILISECOND = 60000L;
    Long TIME_REFRESH = 1000L;

    String PDF_CSS = "public/pdf/css/pdf.css";
    String PDF_IMG = "/public/pdf/img/";

}
