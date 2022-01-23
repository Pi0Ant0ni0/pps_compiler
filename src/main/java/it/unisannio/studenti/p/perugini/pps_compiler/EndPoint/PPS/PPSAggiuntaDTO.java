package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.PPS;


import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche.AttivitaDidatticaPPSDTO;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche.InsegnamentoRegola;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Data
public class PPSAggiuntaDTO {

    //se non ci sono insegnamenti a scelta libera non ha senso compilare il modulo
    @NotEmpty @NotNull
    private List<AttivitaDidatticaPPSDTO> attivitaDidatticheAScelta;
    @NotNull
    private int coorte;
    //potrebbe non esserci
    private List<InsegnamentoRegola> orientamento;
    //potrebbe non eserci
    private String curriculum;

    public Optional<List<InsegnamentoRegola>> getOrientamento() {
        return Optional.ofNullable(orientamento);
    }
}
