package it.unisannio.studenti.p.perugini.pps_compiler.API;

import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche.InsegnamentoRegola;
import lombok.Data;

import java.util.List;
@Data
public class AttivitaDidatticheVincolateDalCorsoDiStudio {
    private List<InsegnamentoRegola> insegnamentiRegola;
    private String corsoDiStudioVincolante;
    private int numeroCfuDaScegliere;
}
