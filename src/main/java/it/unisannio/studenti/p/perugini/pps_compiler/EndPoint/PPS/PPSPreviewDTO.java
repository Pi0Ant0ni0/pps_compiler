package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.PPS;

import it.unisannio.studenti.p.perugini.pps_compiler.API.AttivitaDidattica;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PPSPreviewDTO {
    private String email;
    private LocalDate dataCompilazione;
    private boolean approvato;
    private boolean rifiutato;
    private String nome;
    private String cognome;
    private List<AttivitaDidattica> orientamento;
    private List<AttivitaDidattica> liberi;
}
