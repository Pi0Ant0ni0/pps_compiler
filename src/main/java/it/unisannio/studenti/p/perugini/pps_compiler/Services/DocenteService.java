package it.unisannio.studenti.p.perugini.pps_compiler.Services;


import it.unisannio.studenti.p.perugini.pps_compiler.API.PPS;
import it.unisannio.studenti.p.perugini.pps_compiler.API.User;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Email;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Role;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.PPSNotFoundException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.RichiestaNonValidaException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.UserNotFound;
import it.unisannio.studenti.p.perugini.pps_compiler.Repositories.PPSRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DocenteService {

    @Autowired
    private PPSRepository ppsRepository;
    @Autowired
    private AuthorizationService authorizationService;

    public void accettaPPS(User user) throws PPSNotFoundException, RichiestaNonValidaException {
        Optional<PPS> pps = ppsRepository.findById(user);
        if(!pps.isPresent())
            throw new PPSNotFoundException("lo studente con mail: "+user.getEmail().getEmail()+" non ha compilato nessun modulo");
        PPS pps1 = pps.get();
        if(pps1.isApprovato() || pps1.isRifiutato())
            throw new RichiestaNonValidaException("Non puoi accettare un pps gia accettato/rifiutato");
        pps1.setApprovato(true);
        pps1.setDataVisione(LocalDate.now());
        ppsRepository.save(pps1);
    }

    public void rifiutaPPS(User user) throws PPSNotFoundException, RichiestaNonValidaException {
        Optional<PPS> pps = ppsRepository.findById(user);
        if(!pps.isPresent())
            throw new PPSNotFoundException("lo studente con mail: "+user.getEmail().getEmail()+" non ha compilato nessun modulo");
        PPS pps1 = pps.get();
        if(pps1.isApprovato() || pps1.isRifiutato())
            throw new RichiestaNonValidaException("Non puoi rifiutare un pps gia accettato/rifiutato");
        pps1.setRifiutato(true);
        pps1.setDataVisione(LocalDate.now());
        ppsRepository.save(pps1);
    }


    public List<PPS> getAllPPSNonGestitiFilterdOnCorsoDiStudio(User user) {
        return this.ppsRepository.getAllPPsNonApprovatiENonRfiutati()
                .stream()
                .filter(pps -> pps.getUser().getCorsoDiStudio().equals(user.getCorsoDiStudio()))
                .collect(Collectors.toList());
    }

    public PPS getPPSByEmail(Email email, Email emailRichiedente) throws UserNotFound, PPSNotFoundException, RichiestaNonValidaException {
        User userRichiedente = this.authorizationService.getUserByEmail(emailRichiedente);
        User user = this.authorizationService.getUserByEmail(email);
        if(userRichiedente.getRole().equals(Role.STUDENTE)){
            if (!user.getEmail().equals(userRichiedente.getEmail()))
                throw new RichiestaNonValidaException("Non è possibile richiedere il PPS di un altro studente");
        }
        //se sono uno studente che richiede il proprio modulo pps oppure uno studente
        //posso accedere al modulo se presente
        Optional<PPS> pps = this.ppsRepository.findById(user);
        if(pps.isPresent())
            return pps.get();
        throw  new PPSNotFoundException("L'utente con email: "+email.getEmail()+" non ha compilato nessun modulo pps");
    }

    public List<PPS> getAllPPSVisionati(User user) {
        return this.ppsRepository.findAll()
                .stream()
                .filter(pps -> pps.getUser().getCorsoDiStudio().equals(user.getCorsoDiStudio()))
                .filter(pps -> (pps.isRifiutato() || pps.isApprovato()))
                .collect(Collectors.toList());
    }
}
