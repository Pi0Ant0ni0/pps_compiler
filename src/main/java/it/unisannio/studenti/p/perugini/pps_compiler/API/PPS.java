package it.unisannio.studenti.p.perugini.pps_compiler.API;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PPS {
    @Getter @Setter @NonNull @EqualsAndHashCode.Include
    private Studente studente;
    @Getter @Setter @NonNull
    private List<AttivitaDidattica> insegnamentiASceltaLibera;
    @Setter
    private List<AttivitaDidattica> orientamento;
    @Setter
    private String curriculum;
    @Getter @Setter @NonNull
    private boolean approvato;
    @Getter @Setter @NonNull
    private boolean rifiutato;
    @Getter @Setter @NonNull
    private LocalDate dataCompilazione;
    @Setter
    private LocalDate dataVisione;

    public Optional<List<AttivitaDidattica>> getOrientamento() {
        return Optional.ofNullable(orientamento);
    }

    public Optional<String>getCurriculum(){
        return Optional.ofNullable(curriculum);
    }

    public Optional<LocalDate> getDataVisione() {
        return Optional.ofNullable(dataVisione);
    }
}
