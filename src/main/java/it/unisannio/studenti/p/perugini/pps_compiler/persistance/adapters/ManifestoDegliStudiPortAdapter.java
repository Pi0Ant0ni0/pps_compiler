package it.unisannio.studenti.p.perugini.pps_compiler.persistance.adapters;

import it.unisannio.studenti.p.perugini.pps_compiler.API.ChiaveManifestoDegliStudi;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ManifestoDegliStudi;
import it.unisannio.studenti.p.perugini.pps_compiler.core.manifestiDegliStudi.port.*;
import it.unisannio.studenti.p.perugini.pps_compiler.persistance.Repositories.ManifestiDegliStudiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ManifestoDegliStudiPortAdapter implements ReadManifestoDegliStudiPort, CreateManifestoPort, ListManifestiDegliStudiPort, UpdateManifestoPort, DeleteManifestoPort {
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

    @Override
    public void delete(ChiaveManifestoDegliStudi chiaveManifestoDegliStudi) {
        this.manifestiDegliStudiRepository.deleteById(chiaveManifestoDegliStudi);
    }

    @Override
    public void update(ChiaveManifestoDegliStudi chiaveManifestoDegliStudi, ManifestoDegliStudi manifestoDegliStudi) {
        this.delete(chiaveManifestoDegliStudi);
        this.manifestiDegliStudiRepository.save(manifestoDegliStudi);

    }
}
