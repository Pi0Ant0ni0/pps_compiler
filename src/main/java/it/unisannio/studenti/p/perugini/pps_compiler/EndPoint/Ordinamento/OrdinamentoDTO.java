package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.Ordinamento;


import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class OrdinamentoDTO {
    @NotNull(message = "l'anno di redazione è richiesto")
    @Min(value = 0, message = "L'anno di redazione deve essere maggiore di zero")
    private int annoDiRedazione;
    @NotNull(message = "il codice del corso di studio è richiesto")
    @NotBlank(message = "il codice del corso di studio è richiesto")
    @NotEmpty(message = "il codice del corso di studio è richiesto")
    private String codiceCorso;
    @NotNull(message = "i cfu minimi di orientamento sono richiesti")
    @Min(value = 0, message = "i cfu minimi di orientamento devono essere maggiore di zero")
    private int cfuMinimiOrientamento;
    @NotNull(message = "i cfu massimi di orientamento sono richiesti")
    @Min(value = 0, message = "i cfu massimi di orientamento devono essere maggiore di zero ")
    private int cfuMassimiOrientamento;
    @NotNull(message = "i cfu minimi a scelta sono richiesti")
    @Min(value = 0, message = "i cfu minimi a scelta devono essere maggiore di zero ")
    private int cfuMinimiAScelta;
    @NotNull(message = "i cfu massimi a scelta sono richiesti")
    @Min(value = 0, message = "i cfu massimi a scelta devono essere maggiore di zero ")
    private int cfuMassimiAScelta;
    @NotNull(message = "i cfu minimi obbligatori sono rischiesti")
    @Min(value = 1, message = "i cfu minimi obbligatori devono essere maggiore di zero")
    private int cfuMinimiObbligatori;
    @NotNull(message = "i cfu massimi obbligatori sono richiesti")
    @Min(value = 1, message = "i cfu massimi obbligatori devono essere maggiore di zero")
    private int cfuMassimiObbligatori;
}
