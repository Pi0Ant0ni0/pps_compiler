package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.ManifestoDegliStudi;

import it.unisannio.studenti.p.perugini.pps_compiler.API.ChiaveManifestoDegliStudi;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ManifestoDegliStudi;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche.AttivitaDidatticaPPSDTO;

import java.util.ArrayList;


public class ManifestiDegliStudiMapper {
    public static ManifestoDegliStudi toManifestoDegliStudi(ManifestoDegliStudiDTO manifestoDegliStudiDTO) {
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
        manifestoDegliStudi.setFinestreDiCompilazione(manifestoDegliStudiDTO.getFinestreDiCompilazione());
        if(manifestoDegliStudiDTO.getAttivitaDidatticheAScelta()== null || manifestoDegliStudiDTO.getAttivitaDidatticheAScelta().isEmpty())
            manifestoDegliStudi.setAttivitaDidatticheAScelta(null);
        else manifestoDegliStudi.setAttivitaDidatticheAScelta(manifestoDegliStudiDTO.getAttivitaDidatticheAScelta());
        return manifestoDegliStudi;
    }

    public static ManifestoPreviewDTO toManifestoPreview(ManifestoDegliStudi manifestoDegliStudi){
        ManifestoPreviewDTO dto = new ManifestoPreviewDTO();
        dto.setAnno(manifestoDegliStudi.getChiaveManifestoDegliStudi().getCoorte());
        if(manifestoDegliStudi.getChiaveManifestoDegliStudi().getCurricula().isPresent())
            dto.setCurricula(manifestoDegliStudi.getChiaveManifestoDegliStudi().getCurricula().get());
        else dto.setCurricula("");
        return dto;
    }

    public static ManifestoDegliStudiDTO toManifestoDegliStudiDTO(ManifestoDegliStudi manifestoDegliStudi){
        return new ManifestoDegliStudiDTO(
                manifestoDegliStudi.getChiaveManifestoDegliStudi().getCoorte(),
                manifestoDegliStudi.getChiaveManifestoDegliStudi().getCodiceCorsoDiStudio(),
                manifestoDegliStudi.getAnnoOrdinamento(),
                manifestoDegliStudi.getCfuASceltaLibera(),
                manifestoDegliStudi.getCfuOrientamento(),
                manifestoDegliStudi.getCfuTotali(),
                manifestoDegliStudi.getCfuExtra(),
                manifestoDegliStudi.getAnniAccademici(),
                manifestoDegliStudi.getChiaveManifestoDegliStudi().getCurricula().isPresent()?
                        manifestoDegliStudi.getChiaveManifestoDegliStudi().getCurricula().get():
                        "",
                manifestoDegliStudi.getAttivitaDidatticheAScelta().isPresent()?
                        manifestoDegliStudi.getAttivitaDidatticheAScelta().get():
                        new ArrayList<AttivitaDidatticaPPSDTO>(),
                manifestoDegliStudi.getFinestreDiCompilazione()
        );
    }
}
