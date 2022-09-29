package edu.pe.lamolina.admision.zelper.asterisk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.asteriskjava.manager.AuthenticationFailedException;
import org.asteriskjava.manager.ManagerConnection;
import org.asteriskjava.manager.ManagerConnectionState;
import org.asteriskjava.manager.TimeoutException;
import org.asteriskjava.manager.action.CommandAction;
import org.asteriskjava.manager.action.ExtensionStateAction;
import org.asteriskjava.manager.action.OriginateAction;
import org.asteriskjava.manager.response.ManagerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.albatross.zelpers.miscelanea.PhobosException;

@Service
public class AsteriskServiceImp implements AsteriskService {

    @Autowired
    ManagerConnection managerConnection;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void loginToAsterisk() {
        try {
            logger.info("Login Asterisk");

            ManagerConnectionState state = managerConnection.getState();
            logger.info("ESTADO {}", state.name());
            if (state != ManagerConnectionState.CONNECTED) {
                managerConnection.login();
            }

        } catch (IllegalStateException | IOException | AuthenticationFailedException | TimeoutException ex) {
            ex.printStackTrace();
            throw new PhobosException("Error al conectar a Asterisk " + ex.getMessage(), ex);
        }
    }

    @Override
    public List<String> allExtension() {

        CommandAction action = new CommandAction("sip show peers");
        List<String> extensiones = new ArrayList();

        try {
            ManagerResponse response = managerConnection.sendAction(action);

            logger.info("All Extensions: " + response.getAttribute("Response"));

            String respuest = response.getAttribute("__result__");
            logger.info("respuest: " + respuest);
            for (String line : respuest.split("\n")) {
                logger.info("line: {}", line);
                for (String col : line.replaceAll("(\\s)+", " ").split(" ")) {
                    logger.info("col: {}", col);
                    if (col.contains("/")) {
                        extensiones.add(col.substring(0, 4));
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {
            return extensiones;
        }
    }

    @Override
    public boolean getExtensionStatus(String extension) {

        ExtensionStateAction originate = new ExtensionStateAction(extension, "default");

        try {
            ManagerResponse response = managerConnection.sendAction(originate);

            logger.info("Extension Status: {} - {}", extension, response.getResponse());

            if (!response.getResponse().equals("Success")) {
                return false;
            }

            logger.info("\tStatus Identify {}", response.getAttribute("status"));
            if (!response.getAttribute("status").equals("0")) {
                return false;
            }

            logger.info("\tExtension is ok and iddle...");

            return true;

        } catch (IOException | IllegalArgumentException | IllegalStateException | TimeoutException e) {
            logger.warn("Error al obtener Status :: ", e);
            return false;
        }

    }

    @Override
    public boolean generateDial(String extension, String numero) {

        logger.info("DIAL Between {} {} ", extension, numero);

        OriginateAction originate = new OriginateAction();
        originate.setContext("from-internal");
        originate.setChannel(String.format("SIP/%s", extension));
        originate.setCallerId(numero);
        originate.setExten(numero);
        originate.setAsync(true);
        originate.setPriority(1);

        try {
            ManagerResponse response = managerConnection.sendAction(originate, 3000);

            System.out.println("\n");
            logger.info("Response {} ", response.getResponse());

            return response.getResponse().equals("Success");

        } catch (IOException | IllegalArgumentException | IllegalStateException | TimeoutException e) {
            logger.warn("Error al generar la llamada :: ", e);
            return false;
        }

    }

}
