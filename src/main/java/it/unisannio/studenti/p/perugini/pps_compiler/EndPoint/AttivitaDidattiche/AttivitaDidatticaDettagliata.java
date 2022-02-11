package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche;

import lombok.*;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttivitaDidatticaDettagliata {

    @EqualsAndHashCode.Include
    private String codiceAttivitaDidattica;
    private String denominazioneAttivitaDidattica;
    private int cfu;
    private String codiceCorsoDiStudio;
    private String settoreScientificoDisciplinare;
    private String contenuti;
    private String metodiDidattici;
    private String modalitaVerificaApprendimento;
    private String obiettivi;
    private String prerequisiti;
}
