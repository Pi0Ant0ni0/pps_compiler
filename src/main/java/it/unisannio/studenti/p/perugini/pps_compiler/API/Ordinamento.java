package it.unisannio.studenti.p.perugini.pps_compiler.API;

import lombok.*;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@ToString
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Ordinamento {
    @EqualsAndHashCode.Include @Id
    private int annoDiRedazione;
    private int cfuMinimiCorsoDiLaurea;
    private int cfuMassimiCorsoDiLaurea;
    private int cfuMinimiOrientamento;
    private int cfuMassimiOrientamento;
    private int cfuMinimiAScelta;
    private int cfuMassimiAScelta;
    private int cfuMinimiObbligatori;
    private int cfuMassimiObbligatori;
}
