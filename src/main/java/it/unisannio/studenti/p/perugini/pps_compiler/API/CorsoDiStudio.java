package it.unisannio.studenti.p.perugini.pps_compiler.API;

import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.TipoCorsoDiLaurea;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Document(collection = "Corsi Di Studio")
public class CorsoDiStudio {

    @Getter @Setter @EqualsAndHashCode.Include @Id @NonNull
    private String codice;
    @Getter @Setter @NonNull
    private String denominazione;
    @Getter @Setter @NonNull
    private TipoCorsoDiLaurea tipoCorsoDiLaurea;
    @Getter @Setter @NonNull
    private String denominazioneFacolta;
    @Getter @Setter @NonNull
    private boolean programmato;

}
