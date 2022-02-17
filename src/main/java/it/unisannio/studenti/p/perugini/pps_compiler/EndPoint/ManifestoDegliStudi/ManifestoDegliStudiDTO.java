package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.ManifestoDegliStudi;

import it.unisannio.studenti.p.perugini.pps_compiler.API.AnnoAccademico;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ManifestoDegliStudi;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche.AttivitaDidatticaPPSDTO;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManifestoDegliStudiDTO {

    @NotNull @Min(value = 1, message = "la coorte non puo' essere negativa")
    private int coorte;
    @NotNull @NotEmpty @NotBlank
    private String codiceCorsoDiStudio;
    @NotNull  @Min(value = 1, message = "l'anno di redazione dell'ordinamento non puo' essere negativo")
    private int annoOrdinamento;
    @NotNull  @Min(value = 0, message = "la quota di cfu a scelta libera non puo' essere negativa")
    private int cfuASceltaLibera;
    @NotNull  @Min(value = 0, message = "la quota di cfu di orientamento non puo' essere negativa")
    private int cfuOrientamento;
    @NotNull @Min(value = 1, message = "la quota di cfu totali non puo' essere negativa")
    private int cfuTotali;
    @NotNull @Min(value = 0,message = "la quota di cfu extra non puo' essere negativa")
    private int cfuExtra;
    @NotNull
    private Map<Integer, AnnoAccademico> anniAccademici;
    private String curricula;
    private List<AttivitaDidatticaPPSDTO> attivitaDidatticheAScelta;
    @NotNull @NotEmpty
    private List<ManifestoDegliStudi.FinestraDiCompilazione> finestreDiCompilazione;

}
