package it.unisannio.studenti.p.perugini.pps_compiler.API;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "attività didattiche")
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AttivitaDidattica {
    @EqualsAndHashCode.Include
    @Id @Getter @Setter @NonNull
    private String codiceAttivitaDidattica;
    @Getter @Setter @NonNull
    private String denominazioneAttivitaDidattica;
    @Getter @Setter @NonNull
    private int cfu;
    @Getter @Setter @NonNull
    private String codiceCorsoDiStudio;
    @Getter @Setter @NonNull
    private boolean nonErogabile;
    @Getter @Setter @NonNull
    private String settoreScientificoDisciplinare;
    @Getter @Setter @NonNull
    private boolean programmato;
    @Getter @Setter @NonNull
    private String contenuti;
    @Getter @Setter @NonNull
    private String metodiDidattici;
    @Getter @Setter @NonNull
    private String modalitaVerificaApprendimento;
    @Getter @Setter @NonNull
    private String obiettivi;
    @Getter @Setter @NonNull
    private String prerequisiti;

}
