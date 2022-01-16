package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.CorsoDiStudio;

import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.TipoCorsoDiLaurea;
import lombok.*;

@Data
public class CorsoDiStudioDTO {
    private String codice;
    private String denominazione;
    private String tipoCorsoDiLaurea;
    private String denominazioneFacolta;
}
