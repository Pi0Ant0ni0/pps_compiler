package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AttivitaDidatticaDiOrientamentoDTO {
    @EqualsAndHashCode.Include
    private String codiceAttivitaDidattica;
    private String denominazioneAttivitaDidattica;
    private int cfu;
}
