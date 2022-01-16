package it.unisannio.studenti.p.perugini.pps_compiler.API;

import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche.InsegnamentoRegola;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Data
public class AnnoAccademico {
    private List<AttivitaDidatticheVincolateDalCorsoDiStudio> attivitaDidatticheVincolateDalCorsoDiStudio;
    private List<InsegnamentoRegola>insegnamentiObbligatori;
    private List<Orientamento> orientamenti;
    private AttivitaDidatticheAScelta attivitaDidatticheAScelta;


    public void addOrientamento(Orientamento orientamento){
        if(this.orientamenti == null)
            this.orientamenti = new ArrayList<>();
        orientamenti.add(orientamento);
    }

    public void addInsegnamentoVincolato(AttivitaDidatticheVincolateDalCorsoDiStudio attivitaDidatticheVincolateDalCorsoDiStudio){
        if(this.attivitaDidatticheVincolateDalCorsoDiStudio == null)
            this.attivitaDidatticheVincolateDalCorsoDiStudio = new ArrayList<>();
        this.attivitaDidatticheVincolateDalCorsoDiStudio.add(attivitaDidatticheVincolateDalCorsoDiStudio);
    }

    public Optional<List<AttivitaDidatticheVincolateDalCorsoDiStudio>> getAttivitaDidatticheVincolateDalCorsoDiStudio() {
        return Optional.ofNullable(attivitaDidatticheVincolateDalCorsoDiStudio);
    }

    public Optional<List<Orientamento>> getOrientamenti() {
        return Optional.ofNullable(orientamenti);
    }


    public Optional<AttivitaDidatticheAScelta> getAttivitaDidatticheAScelta() {
        return Optional.ofNullable(attivitaDidatticheAScelta);
    }


    public int getCfuObbligatori() {
        int count = 0;
        for (InsegnamentoRegola insegnamentoRegola: insegnamentiObbligatori)
            count+= insegnamentoRegola.getCfu();
        return count;
    }

    public int getCfuLiberi() {
        if(this.attivitaDidatticheAScelta == null)
            return 0;
        return this.attivitaDidatticheAScelta.getCfuDaScegliere();
    }

    public int getCfuOrientamento(){
        if (this.orientamenti == null || this.orientamenti.isEmpty())
            return 0;
        return this.orientamenti.get(0).getQuotaCFULiberi()+ this.orientamenti.get(0).getQuotaCFUVincolati();
    }
    public int getcfuVincolatiDalCorso() {
        if (this.attivitaDidatticheVincolateDalCorsoDiStudio == null || this.attivitaDidatticheVincolateDalCorsoDiStudio.isEmpty())
            return 0;
        return this.attivitaDidatticheVincolateDalCorsoDiStudio.get(0).getNumeroCfuDaScegliere();
    }


    public int getCfuTotali(){
        return this.getCfuObbligatori()+this.getCfuOrientamento()+this.getCfuLiberi()+this.getcfuVincolatiDalCorso();
    }



}
