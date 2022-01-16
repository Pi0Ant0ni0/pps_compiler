package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.ManifestoDegliStudi;

import it.unisannio.studenti.p.perugini.pps_compiler.API.ChiaveManifestoDegliStudi;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ManifestoDegliStudi;


public class ManifestiDegliStudiMapper {
    public static ManifestoDegliStudi fromRegolaDTOToRegola(ManifestoDegliStudiDTO manifestoDegliStudiDTO) {
        ManifestoDegliStudi manifestoDegliStudi = new ManifestoDegliStudi();
        ChiaveManifestoDegliStudi chiaveManifestoDegliStudi = new ChiaveManifestoDegliStudi();
        chiaveManifestoDegliStudi.setCodiceCorsoDiStudio(manifestoDegliStudiDTO.getCodiceCorsoDiStudio());
        chiaveManifestoDegliStudi.setCoorte(manifestoDegliStudiDTO.getCoorte());
        manifestoDegliStudi.setChiaveManifestoDegliStudi(chiaveManifestoDegliStudi);
        manifestoDegliStudi.setAnnoOrdinamento(manifestoDegliStudiDTO.getAnnoOrdinamento());
        manifestoDegliStudi.setCfuASceltaLibera(manifestoDegliStudiDTO.getCfuASceltaLibera());
        manifestoDegliStudi.setCfuExtra(manifestoDegliStudiDTO.getCfuExtra());
        manifestoDegliStudi.setCfuTotali(manifestoDegliStudiDTO.getCfuTotali());
        manifestoDegliStudi.setCfuOrientamento(manifestoDegliStudiDTO.getCfuOrientamento());
        manifestoDegliStudi.setAnniAccademici(manifestoDegliStudiDTO.getAnniAccademici());
        return manifestoDegliStudi;
    }
}
