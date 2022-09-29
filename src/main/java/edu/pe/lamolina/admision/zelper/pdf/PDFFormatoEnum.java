package edu.pe.lamolina.admision.zelper.pdf;

import java.util.HashMap;
import java.util.Map;

public enum PDFFormatoEnum {

    BOLETA_PAGO("BoletaPago", "admision/pdf/boletaPago", "Boleta Pago", "Boleta Pago"),
    BOLETA_PAGO_GUIAPOSTULANTE("BoletaPagoGuiaPostulante", "admision/pdf/boletaPagoGuiaPostulante", "Boleta Pago Guía del Postulante", "Boleta Pago Guía del Postulante"),
    CARTA_COMPROMISO("CartaCompromiso", "admision/pdf/cartaCompromiso", "Carta Compromiso", "Carta Compromiso"),
    DECLARACION_JURADA("DeclaracionJurada", "admision/pdf/declaracionJurada", "Declaración Jurada", "Declaración Jurada"),
    CONSTANCIA_INGRESANTE("ConstanciaIngresante", "admision/pdf/constanciaIngresante", "Constancia Ingresante", "Constancia Ingresante"),
    BOLETA_EXAMEN_MEDICO("BoletaExamenMedico", "admision/pdf/boletaExamenMedico", "Boleta Examen Medico", "Boleta Examen Medico"),
    BOLETA_PAGO_ING("BoletaPago", "admision/pdf/boletaPagoIngreso", "Boleta Pago", "Boleta Pago");

    private final String name;
    private final String fileTemplate;
    private final String title;
    private final String subject;

    private static final Map<String, PDFFormatoEnum> lookup = new HashMap<>();

    static {
        for (PDFFormatoEnum d : PDFFormatoEnum.values()) {
            lookup.put(d.getName(), d);
        }
    }

    private PDFFormatoEnum(String name, String fileTemplate, String title, String subject) {
        this.name = name;
        this.fileTemplate = fileTemplate;
        this.title = title;
        this.subject = subject;
    }

    public static PDFFormatoEnum getEnum(String name) {
        for (PDFFormatoEnum d : PDFFormatoEnum.values()) {
            if (d.name().equalsIgnoreCase(name)) {
                return d;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public String getFileTemplate() {
        return fileTemplate;
    }

    public String getTitle() {
        return title;
    }

    public String getSubject() {
        return subject;
    }

}
