package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.ManifestoDegliStudi;

import it.unisannio.studenti.p.perugini.pps_compiler.API.ChiaveManifestoDegliStudi;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ManifestoDegliStudi;


public class ManifestiDegliStudiMapper {
    public static ManifestoDegliStudi fromRegolaDTOToRegola(ManifestoDegliStudiDTO manifestoDegliStudiDTO) {
        ManifestoDegliStudi manifestoDegliStudi = new ManifestoDegliStudi();
        ChiaveManifestoDegliStudi chiaveManifestoDegliStudi = new ChiaveManifestoDegliStudi();
        chiaveManifestoDegliStudi.setCodiceCorsoDiStudio(manifestoDegliStudiDTO.getCodiceCorsoDiStudio());
        chiaveManifestoDegliStudi.setCoorte(manifestoDegliStudiDTO.getCoorte());
        if(manifestoDegliStudiDTO.getCurricula()!= null && manifestoDegliStudiDTO.getCurricula().length()!=0)
            chiaveManifestoDegliStudi.setCurricula(manifestoDegliStudiDTO.getCurricula());
        else chiaveManifestoDegliStudi.setCurricula(null);
        manifestoDegliStudi.setChiaveManifestoDegliStudi(chiaveManifestoDegliStudi);
        manifestoDegliStudi.setAnnoOrdinamento(manifestoDegliStudiDTO.getAnnoOrdinamento());
        manifestoDegliStudi.setCfuASceltaLibera(manifestoDegliStudiDTO.getCfuASceltaLibera());
        manifestoDegliStudi.setCfuExtra(manifestoDegliStudiDTO.getCfuExtra());
        manifestoDegliStudi.setCfuTotali(manifestoDegliStudiDTO.getCfuTotali());
        manifestoDegliStudi.setCfuOrientamento(manifestoDegliStudiDTO.getCfuOrientamento());
        manifestoDegliStudi.setAnniAccademici(manifestoDegliStudiDTO.getAnniAccademici());
        manifestoDegliStudi.setDataFineCompilazionePiano(manifestoDegliStudiDTO.getDataFineCompilazionePiano());
        manifestoDegliStudi.setDataInizioCompilazionePiano(manifestoDegliStudiDTO.getDataInizioCompilazionePiano());
        if(manifestoDegliStudiDTO.getAttivitaDidatticheAScelta()== null || manifestoDegliStudiDTO.getAttivitaDidatticheAScelta().isEmpty())
            manifestoDegliStudi.setAttivitaDidatticheAScelta(null);
        else manifestoDegliStudi.setAttivitaDidatticheAScelta(manifestoDegliStudiDTO.getAttivitaDidatticheAScelta());
        return manifestoDegliStudi;
    }

    public static ManifestoPreviewDTO fromManifestoToPreview(ManifestoDegliStudi manifestoDegliStudi){
        ManifestoPreviewDTO dto = new ManifestoPreviewDTO();
        dto.setAnno(manifestoDegliStudi.getChiaveManifestoDegliStudi().getCoorte());
        if(manifestoDegliStudi.getChiaveManifestoDegliStudi().getCurricula().isPresent())
            dto.setCurricula(manifestoDegliStudi.getChiaveManifestoDegliStudi().getCurricula().get());
        else dto.setCurricula("");
        return dto;
    }
}
