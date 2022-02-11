package it.unisannio.studenti.p.perugini.pps_compiler.persistance.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import it.unisannio.studenti.p.perugini.pps_compiler.API.AttivitaDidattica;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "attività didattiche")
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AttivitaDidatticaEntity {
    @EqualsAndHashCode.Include
    @Id
    private String codiceAttivitaDidattica;
    private String denominazioneAttivitaDidattica;
    private int cfu;
    private String codiceCorsoDiStudio;
    private boolean nonErogabile;
    private String settoreScientificoDisciplinare;
    private boolean programmato;
    private String contenuti;
    private String metodiDidattici;
    private String modalitaVerificaApprendimento;
    private String obiettivi;
    private String prerequisiti;
    private List<AttivitaDidattica> unitaDidattiche;

    public Optional<List<AttivitaDidattica>> getUnitaDidattiche(){
        return Optional.ofNullable(unitaDidattiche);
    }
}
