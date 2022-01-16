package it.unisannio.studenti.p.perugini.pps_compiler.API;

import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche.AttivitaDidatticaDiOrientamentoDTO;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche.AttivitaDidatticaPPSDTO;
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
    private User user;
    @Getter @Setter @NonNull
    private List<AttivitaDidatticaPPSDTO> insegnamentiASceltaLibera;
    @Setter @NonNull
    private List<AttivitaDidatticaDiOrientamentoDTO> orientamento;
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

    public Optional<LocalDate> getDataVisione() {
        return Optional.ofNullable(dataVisione);
    }
}
