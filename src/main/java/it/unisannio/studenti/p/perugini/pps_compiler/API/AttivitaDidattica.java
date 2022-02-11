package it.unisannio.studenti.p.perugini.pps_compiler.API;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;
import java.util.Optional;


@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class AttivitaDidattica {
    @EqualsAndHashCode.Include
    @Getter @Setter @NonNull
    private String codiceAttivitaDidattica;
    @Getter @Setter @NonNull
    private String denominazioneAttivitaDidattica;
    @Getter @Setter @NonNull
    private int cfu;
    @Getter @Setter @NonNull
    private CorsoDiStudio corsoDiStudio;
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
    @Setter @JsonIgnoreProperties
    private List<AttivitaDidattica>unitaDidattiche;

    public Optional<List<AttivitaDidattica>>getUnitaDidattiche(){
        return Optional.ofNullable(unitaDidattiche);
    }

}
