package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.CorsoDiStudio;

import it.unisannio.studenti.p.perugini.pps_compiler.API.CorsoDiStudio;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.TipoCorsoDiLaurea;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.CorsoDiStudioNotFoundException;
import it.unisannio.studenti.p.perugini.pps_compiler.Services.CorsoDiStudioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CorsoDiStudioMapper {

    @Autowired
    private CorsoDiStudioService corsoDiStudioService;

    public CorsoDiStudioDTO fromCorsoDiStudioToCorsoDiStudioDto(CorsoDiStudio corsoDiStudio){
        CorsoDiStudioDTO dto = new CorsoDiStudioDTO();
        dto.setCodice(corsoDiStudio.getCodice());
        dto.setDenominazione(corsoDiStudio.getDenominazione());
        dto.setDenominazioneFacolta(corsoDiStudio.getDenominazioneFacolta());
        dto.setTipoCorsoDiLaurea(corsoDiStudio.getTipoCorsoDiLaurea().toString());
        return dto;
    }

    public Optional<CorsoDiStudio> fromCorsoDiStudioDTOToCorsoDiStudio(CorsoDiStudioDTO dto) {
           return corsoDiStudioService.getCorsoDiStudioById(dto.getCodice());


    }
}
