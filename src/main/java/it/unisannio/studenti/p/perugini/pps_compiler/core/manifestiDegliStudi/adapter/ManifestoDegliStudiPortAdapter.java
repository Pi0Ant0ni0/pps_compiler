package it.unisannio.studenti.p.perugini.pps_compiler.core.manifestiDegliStudi.adapter;

import it.unisannio.studenti.p.perugini.pps_compiler.API.ChiaveManifestoDegliStudi;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ManifestoDegliStudi;
import it.unisannio.studenti.p.perugini.pps_compiler.Repositories.ManifestiDegliStudiRepository;
import it.unisannio.studenti.p.perugini.pps_compiler.core.manifestiDegliStudi.port.CreateManifestoPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.manifestiDegliStudi.port.ListManifestiDegliStudiPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.manifestiDegliStudi.port.ReadManifestoDegliStudiPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ManifestoDegliStudiPortAdapter implements ReadManifestoDegliStudiPort, CreateManifestoPort, ListManifestiDegliStudiPort {
    @Autowired
    ManifestiDegliStudiRepository manifestiDegliStudiRepository;

    @Override
    public Optional<ManifestoDegliStudi> findManifestoById(ChiaveManifestoDegliStudi id) {
        return this.manifestiDegliStudiRepository.findById(id);
    }

    @Override
    public void save(ManifestoDegliStudi manifestoDegliStudi) {
        this.manifestiDegliStudiRepository.save(manifestoDegliStudi);
    }

    @Override
    public List<ManifestoDegliStudi> list() {
        return this.manifestiDegliStudiRepository.findAll();
    }
}
