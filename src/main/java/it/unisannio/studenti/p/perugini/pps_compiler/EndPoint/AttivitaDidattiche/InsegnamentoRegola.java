package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche;

import lombok.*;
import java.util.Optional;
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class InsegnamentoRegola {
    @EqualsAndHashCode.Include
    @NonNull
    private String codiceInsegnamento;
    @NonNull
    private String denominazioneInsegnamento;
    @NonNull
    private int cfu;
    @NonNull
    private String settoreScientificoDisciplinare;
    @NonNull
    private String semestre;
    @NonNull
    private boolean annualeFlag = false;
    @NonNull
    private boolean insegnamentoIntegratoFlag = false;
    private String codiceCorsoDiStudioMuoto;

    public Optional<String> getCodiceCorsoDiStudioMuoto() {
        return Optional.ofNullable(codiceCorsoDiStudioMuoto);
    }
}
