package it.unisannio.studenti.p.perugini.pps_compiler.core.attivitaDidattica.adapter;

import it.unisannio.studenti.p.perugini.pps_compiler.API.AttivitaDidattica;
import it.unisannio.studenti.p.perugini.pps_compiler.Repositories.AttivitaDidatticheRepository;
import it.unisannio.studenti.p.perugini.pps_compiler.core.attivitaDidattica.port.ListAttivitaDidattichePort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.attivitaDidattica.port.ReadAttivitaDidatticaPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class AttivitaDidatticaAdapterPort implements ReadAttivitaDidatticaPort, ListAttivitaDidattichePort {
    @Autowired
    private AttivitaDidatticheRepository attivitaDidatticheRepository;
    @Override
    public Optional<AttivitaDidattica> findAttivitaById(String codice) {
        return this.attivitaDidatticheRepository.findById(codice);
    }

    @Override
    public List<AttivitaDidattica> listAttivitaDidattiche() {
        return this.attivitaDidatticheRepository.findAll();
    }
}
