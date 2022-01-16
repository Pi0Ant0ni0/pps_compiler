package it.unisannio.studenti.p.perugini.pps_compiler.API;

import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche.InsegnamentoRegola;
import lombok.Data;

import java.util.List;

@Data
public class AttivitaDidatticheAScelta {
    private int cfuDaScegliere;
    private List<InsegnamentoRegola> insegnamenti;
}
