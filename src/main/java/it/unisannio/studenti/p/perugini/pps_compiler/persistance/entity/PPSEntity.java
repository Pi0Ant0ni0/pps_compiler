package it.unisannio.studenti.p.perugini.pps_compiler.persistance.entity;

import it.unisannio.studenti.p.perugini.pps_compiler.API.AttivitaDidattica;
import it.unisannio.studenti.p.perugini.pps_compiler.API.Studente;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Data
@Document(collection = "moduli pps")
public class PPSEntity {
    @Id
    private Studente studente;
    private List<String> insegnamentiASceltaLibera;
    private List<String> orientamento;
    private String curriculum;
    private boolean approvato;
    private boolean rifiutato;
    private LocalDate dataCompilazione;
    private LocalDate dataVisione;

    public Optional<List<String>> getOrientamento() {
        return Optional.ofNullable(orientamento);
    }

    public Optional<String>getCurriculum(){
        return Optional.ofNullable(curriculum);
    }

    public Optional<LocalDate> getDataVisione() {
        return Optional.ofNullable(dataVisione);
    }
}
