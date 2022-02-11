package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.PPS;

import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche.AttivitaDidatticaDettagliata;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PPSPreviewDTO {
    private String codiceCorsoDiStudio;
    private String email;
    private LocalDate dataCompilazione;
    private boolean approvato;
    private boolean rifiutato;
    private String nome;
    private String cognome;
    private List<AttivitaDidatticaDettagliata> orientamento;
    private List<AttivitaDidatticaDettagliata> liberi;
    private String curriculum;
}
