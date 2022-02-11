package it.unisannio.studenti.p.perugini.pps_compiler.persistance.adapters;

import it.unisannio.studenti.p.perugini.pps_compiler.API.PPS;
import it.unisannio.studenti.p.perugini.pps_compiler.API.Studente;
import it.unisannio.studenti.p.perugini.pps_compiler.persistance.entity.PPSEntity;
import it.unisannio.studenti.p.perugini.pps_compiler.persistance.mappers.PPSRepositoryMapper;
import it.unisannio.studenti.p.perugini.pps_compiler.persistance.Repositories.User;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.PPSNonValidoException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.PPSNotFoundException;
import it.unisannio.studenti.p.perugini.pps_compiler.persistance.Repositories.PPSRepository;
import it.unisannio.studenti.p.perugini.pps_compiler.core.pps.port.CreatePPSPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.pps.port.ListPPSPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.pps.port.ReadPPSPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.pps.port.UpdatePPSPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PPSAdapter implements ReadPPSPort, CreatePPSPort, ListPPSPort, UpdatePPSPort {
    @Autowired
    private PPSRepository ppsRepository;
    @Autowired
    private PPSRepositoryMapper ppsRepositoryMapper;

    @Override
    public void save(PPS pps) {
        this.ppsRepository.save(ppsRepositoryMapper.toEntity(pps));
    }

    @Override
    public Optional<PPS> findPPSById(Studente studente) {
        return this.ppsRepository.findById(studente).map(ppsRepositoryMapper::toDomain);
    }

    @Override
    public List<PPS> findPPSInSospesoByUser(User user) {
        List<String> codici = user.getCorsoDiStudio().get()
                .stream()
                .map(corso->corso.getCodice())
                .collect(Collectors.toList());
        return this.ppsRepository
                .findAll()
                .stream()
                .filter(pps -> !pps.isApprovato())
                .filter(pps -> !pps.isRifiutato())
                .filter(pps -> codici.contains(pps.getStudente().getCorsoDiStudio().getCodice()))
                .map(ppsRepositoryMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<PPS> findPPSVisionatiByUser(User user) {
        List<String> codici = user.getCorsoDiStudio().get()
                .stream()
                .map(corso->corso.getCodice())
                .collect(Collectors.toList());
        return this.ppsRepository
                .findAll()
                .stream()
                .filter(pps -> pps.isApprovato() || pps.isRifiutato())
                .filter(pps -> codici.contains(pps.getStudente().getCorsoDiStudio().getCodice()))
                .map(ppsRepositoryMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void accettaPPS(Studente studente) throws PPSNotFoundException, PPSNonValidoException {
        Optional<PPSEntity> ppsOptional = this.ppsRepository.findById(studente);
        if(ppsOptional.isPresent()){
            PPSEntity pps = ppsOptional.get();
            if(pps.isRifiutato() || pps.isApprovato())
                throw new PPSNonValidoException("Il PPS è stato gia accettato o rifiutato.");
            pps.setApprovato(true);
            pps.setDataVisione(LocalDate.now());
            this.ppsRepository.deleteById(studente);
            this.ppsRepository.save(pps);
            return;
        }
        throw new PPSNotFoundException("Impossibile accettare il PPS perchè non è presente nessun PPS");
    }

    @Override
    public void rifiutaPPS(Studente studente) throws PPSNotFoundException, PPSNonValidoException {
        Optional<PPSEntity> ppsOptional = this.ppsRepository.findById(studente);
        if(ppsOptional.isPresent()){
            PPSEntity pps = ppsOptional.get();
            if(pps.isRifiutato() || pps.isApprovato())
                throw new PPSNonValidoException("Il PPS è stato gia accettato o rifiutato.");
            pps.setRifiutato(true);
            pps.setDataVisione(LocalDate.now());
            this.ppsRepository.deleteById(studente);
            this.ppsRepository.save(pps);
            return;
        }
        throw new PPSNotFoundException("Impossibile rifiutare il PPS perchè non è presente nessun PPS");

    }
}
