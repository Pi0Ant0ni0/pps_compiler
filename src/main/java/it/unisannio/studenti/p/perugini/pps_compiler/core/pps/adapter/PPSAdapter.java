package it.unisannio.studenti.p.perugini.pps_compiler.core.pps.adapter;

import it.unisannio.studenti.p.perugini.pps_compiler.API.PPS;
import it.unisannio.studenti.p.perugini.pps_compiler.API.User;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.PPSNonValidoException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.PPSNotFoundException;
import it.unisannio.studenti.p.perugini.pps_compiler.Repositories.PPSRepository;
import it.unisannio.studenti.p.perugini.pps_compiler.core.pps.port.CreatePPSPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.pps.port.ListPPSPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.pps.port.ReadPPSPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.pps.port.UpdatePPSPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.BadRequestException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PPSAdapter implements ReadPPSPort, CreatePPSPort, ListPPSPort, UpdatePPSPort {
    @Autowired
    private PPSRepository ppsRepository;

    @Override
    public void save(PPS pps) {
        this.ppsRepository.save(pps);
    }

    @Override
    public Optional<PPS> findPPSById(User user) {
        return this.ppsRepository.findById(user);
    }

    @Override
    public List<PPS> findPPSInSospesoByUser(User user) {
        return this.ppsRepository
                .findAll()
                .stream()
                .filter(pps -> !pps.isApprovato())
                .filter(pps -> !pps.isRifiutato())
                .filter(pps ->
                        pps.getUser().getCorsoDiStudio().get().getCodice()
                                .equals(user.getCorsoDiStudio().get().getCodice()))
                .collect(Collectors.toList());
    }

    @Override
    public List<PPS> findPPSVisionatiByUser(User user) {
        return this.ppsRepository
                .findAll()
                .stream()
                .filter(pps -> pps.isApprovato() || pps.isRifiutato())
                .filter(pps ->
                        pps.getUser().getCorsoDiStudio().get().getCodice()
                                .equals(user.getCorsoDiStudio().get().getCodice()))
                .collect(Collectors.toList());
    }

    @Override
    public void accettaPPS(User user) throws PPSNotFoundException, PPSNonValidoException {
        Optional<PPS> ppsOptional = this.ppsRepository.findById(user);
        if(ppsOptional.isPresent()){
            PPS pps = ppsOptional.get();
            if(pps.isRifiutato() || pps.isApprovato())
                throw new PPSNonValidoException("Il PPS è stato gia accettato o rifiutato.");
            pps.setApprovato(true);
            pps.setDataVisione(LocalDate.now());
            this.ppsRepository.deleteById(user);
            this.ppsRepository.save(pps);
            return;
        }
        throw new PPSNotFoundException("Impossibile accettare il PPS perchè non è presente nessun PPS");
    }

    @Override
    public void rifiutaPPS(User user) throws PPSNotFoundException, PPSNonValidoException {
        Optional<PPS> ppsOptional = this.ppsRepository.findById(user);
        if(ppsOptional.isPresent()){
            PPS pps = ppsOptional.get();
            if(pps.isRifiutato() || pps.isApprovato())
                throw new PPSNonValidoException("Il PPS è stato gia accettato o rifiutato.");
            pps.setRifiutato(true);
            pps.setDataVisione(LocalDate.now());
            this.ppsRepository.deleteById(user);
            this.ppsRepository.save(pps);
            return;
        }
        throw new PPSNotFoundException("Impossibile rifiutare il PPS perchè non è presente nessun PPS");

    }
}
