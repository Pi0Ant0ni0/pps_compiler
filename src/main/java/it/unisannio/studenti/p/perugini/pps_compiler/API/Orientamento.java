package it.unisannio.studenti.p.perugini.pps_compiler.API;

import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche.InsegnamentoRegola;
import lombok.*;

import java.util.List;
import java.util.Optional;


@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Orientamento {
    @EqualsAndHashCode.Include
    private String denominazione;
    private List<InsegnamentoRegola>insegnamentiVincolati;
    private List<InsegnamentoRegola> insegnamentiLiberi;
    private int quotaCFULiberi;
    private int quotaCFUVincolati;

    public Optional<List<InsegnamentoRegola>> getInsegnamentiVincolati() {
        return Optional.ofNullable(insegnamentiVincolati);
    }

    public Optional<List<InsegnamentoRegola>> getInsegnamentiLiberi() {
        return Optional.ofNullable(insegnamentiLiberi);
    }
}
