package it.unisannio.studenti.p.perugini.pps_compiler.core.pps.adapter;

import it.unisannio.studenti.p.perugini.pps_compiler.API.PPS;
import it.unisannio.studenti.p.perugini.pps_compiler.API.User;
import it.unisannio.studenti.p.perugini.pps_compiler.Repositories.PPSRepository;
import it.unisannio.studenti.p.perugini.pps_compiler.core.pps.port.CreatePPSPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.pps.port.ReadPPSPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PPSAdapter implements ReadPPSPort, CreatePPSPort {
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
}
