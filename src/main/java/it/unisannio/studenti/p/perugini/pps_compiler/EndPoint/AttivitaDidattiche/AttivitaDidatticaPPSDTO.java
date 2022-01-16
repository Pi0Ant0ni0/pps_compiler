package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AttivitaDidatticaPPSDTO {
    private int cfu;
    @EqualsAndHashCode.Include
    private String codiceAttivitaDidattica;
    private String denominazioneAttivitaDidattica;
    private String codiceCorsoDiStudio;
    private String denominazioneCorsoDiStudio;
    private String settoreScientificoDisciplinare;
}
