package edu.pe.lamolina.admision.zelper.misc;

import java.util.Date;
import java.util.Locale;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateFormat {

    public String format(Date fecha, String formato) {
        DateTime hoy = new DateTime(fecha);
        DateTimeFormatter fmt = DateTimeFormat.forPattern(formato);
        return fmt.print(hoy);
    }

    public String format(Date fecha, String formato, String language) {
        DateTime hoy = new DateTime(fecha);
        DateTimeFormatter fmt = DateTimeFormat.forPattern(formato);
        return fmt.withLocale(new Locale(language)).print(hoy);
    }

    public String format(Date fecha, String formato, String language, String country) {
        DateTime hoy = new DateTime(fecha);
        DateTimeFormatter fmt = DateTimeFormat.forPattern(formato);
        return fmt.withLocale(new Locale(language, country)).print(hoy);
    }
}
