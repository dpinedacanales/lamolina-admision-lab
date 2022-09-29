package edu.pe.lamolina.admision.controller.admision.inscripcion.token;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;
import pe.edu.lamolina.model.inscripcion.Postulante;

@Component
public class TokenAdmin {

    private final Map<String, TokenPostulante> tokens;
    private final Integer lapso = 10;

    public TokenAdmin() {
        tokens = new HashMap();
    }

    public TokenPostulante findToken(String token) {
        TokenPostulante tk = tokens.get(token);
        if (tk == null) {
            return tk;
        }

        DateTime dt = new DateTime(tk.getFecha()).plusSeconds(lapso);
        if (!dt.isBeforeNow()) {
            tokens.remove(token);
            return null;
        }

        return tk;
    }

    public TokenPostulante createToken(Postulante postulante) {
        String token = StringUtils.randomAlphanumeric(12);
        TokenPostulante tk = new TokenPostulante();
        tk.setFecha(new Date());
        tk.setPostulante(postulante);
        tk.setToken(token);
        tokens.put(token, tk);

        return tk;
    }

    public void limpiarTokens() {
        List<String> remover = new ArrayList();
        Set set = tokens.entrySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry mentry = (Map.Entry) iterator.next();
            TokenPostulante tk = (TokenPostulante) mentry.getValue();
            String token = (String) mentry.getKey();

            DateTime dt = new DateTime(tk.getFecha()).plusSeconds(lapso);
            if (!dt.isBeforeNow()) {
                remover.add(token);
            }
        }
        for (String token : remover) {
            tokens.remove(token);
        }
    }

}
