package it.unisannio.studenti.p.perugini.pps_compiler.API;

import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche.AttivitaDidatticaDiOrientamentoDTO;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche.AttivitaDidatticaPPSDTO;
import it.unisannio.studenti.p.perugini.pps_compiler.Repositories.User;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Document(collection = "moduli pps")
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PPS {
    @Getter @Setter @NonNull @EqualsAndHashCode.Include
    @Id
    private Studente studente;
    @Getter @Setter @NonNull
    private List<AttivitaDidatticaPPSDTO> insegnamentiASceltaLibera;
    @Setter
    private List<AttivitaDidatticaDiOrientamentoDTO> orientamento;
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

    public Optional<List<AttivitaDidatticaDiOrientamentoDTO>> getOrientamento() {
        return Optional.ofNullable(orientamento);
    }

    public Optional<String>getCurriculum(){
        return Optional.ofNullable(curriculum);
    }

    public Optional<LocalDate> getDataVisione() {
        return Optional.ofNullable(dataVisione);
    }
}
