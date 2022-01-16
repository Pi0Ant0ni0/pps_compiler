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

    public CorsoDiStudio fromCorsoDiStudioDTOToCorsoDiStudio(CorsoDiStudioDTO dto) throws CorsoDiStudioNotFoundException {
           Optional<CorsoDiStudio> optionalCorsoDiStudio =corsoDiStudioService.getCorsoDiStudioById(dto.getCodice());
           if(optionalCorsoDiStudio.isPresent())
               return optionalCorsoDiStudio.get();
           else throw new CorsoDiStudioNotFoundException("Il corso di studio ricevuto non è presente nel database");


    }
}
