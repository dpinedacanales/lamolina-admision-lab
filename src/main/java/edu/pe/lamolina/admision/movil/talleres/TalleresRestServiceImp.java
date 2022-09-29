package edu.pe.lamolina.admision.movil.talleres;

import edu.pe.lamolina.admision.controller.admision.taller.TallerService;
import edu.pe.lamolina.admision.dao.inscripcion.InscritoTallerDAO;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.edu.lamolina.model.inscripcion.InscritoTaller;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.Taller;

@Service
@Transactional(readOnly = true)
public class TalleresRestServiceImp implements TalleresRestService {

    @Autowired
    TallerService service;

    @Autowired
    InscritoTallerDAO inscritoTallerDAO;

    @Override
    public List<Taller> all() {
        return service.allTaller();
    }

    @Override
    public List<Taller> allByInteresado(Interesado interesado) {
        List<Taller> talleres = inscritoTallerDAO.allByInteresado(interesado)
                .stream()
                .map(InscritoTaller::getTaller)
                .collect(Collectors.toList());

        service.procesarDescripcionAndBanners(talleres);

        return talleres;
    }

    @Override
    public Taller find(Interesado interesado, Taller taller) {
        InscritoTaller inscripcion = inscritoTallerDAO.findByInteresadoTaller(interesado, taller.getId());

        Taller tallerBD = service.findTaller(taller.getId());
        tallerBD.setInscrito(inscripcion != null);

        service.procesarDescripcionAndBanners(Arrays.asList(tallerBD));

        return tallerBD;
    }

    @Override
    @Transactional
    public void inscribirse(Interesado interesado, Taller taller, JsonResponse json) {
        Interesado interesadoBD = service.findInteresado(interesado);
        Taller tallerBD = service.findTaller(taller.getId());

        service.inscribirseTaller(interesadoBD, tallerBD, json);
    }

}
