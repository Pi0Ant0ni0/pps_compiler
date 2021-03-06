package it.unisannio.studenti.p.perugini.pps_compiler.API;

import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche.AttivitaDidatticaPPSDTO;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Document(collection = "Manifesti Degli Studi")
public class ManifestoDegliStudi {

    @EqualsAndHashCode.Include
    @Getter @Setter @NonNull @Id
    private ChiaveManifestoDegliStudi chiaveManifestoDegliStudi;
    @Getter @Setter @NonNull
    private int annoOrdinamento;
    @Getter @Setter @NonNull
    private int cfuASceltaLibera;
    @Getter @Setter @NonNull
    private int cfuOrientamento;
    @Getter @Setter @NonNull
    private int cfuTotali;
    @Getter @Setter @NonNull
    private int cfuExtra;
    @Setter @Getter @NonNull
    private Map<Integer, AnnoAccademico> anniAccademici;
    @Setter
    private List<AttivitaDidatticaPPSDTO> attivitaDidatticheAScelta;
    @Getter  @NonNull @Setter
    private List<FinestraDiCompilazione> finestreDiCompilazione;

    public Optional<List<AttivitaDidatticaPPSDTO>>getAttivitaDidatticheAScelta(){
        return Optional.ofNullable(attivitaDidatticheAScelta);
    }


    @Data
    @ToString
    public static class FinestraDiCompilazione {
        private LocalDate dataInizioCompilazione;
        private LocalDate dataFineCompilazione;
    }
}
