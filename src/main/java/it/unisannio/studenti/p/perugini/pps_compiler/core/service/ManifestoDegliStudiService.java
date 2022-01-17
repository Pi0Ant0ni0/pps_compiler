package it.unisannio.studenti.p.perugini.pps_compiler.core.service;

import it.unisannio.studenti.p.perugini.pps_compiler.API.*;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.SEMESTRE;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche.InsegnamentoRegola;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.InsegnamentoNotFoundException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.OrdinamentoNotFoundException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.RegolaNonValidaException;
import it.unisannio.studenti.p.perugini.pps_compiler.Services.InsegnamentoService;
import it.unisannio.studenti.p.perugini.pps_compiler.core.attivitaDidattica.port.ReadAttivitaDidatticaPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.manifestiDegliStudi.port.CreateManifestoPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.manifestiDegliStudi.usecase.AggiungiManfiestoUseCase;
import it.unisannio.studenti.p.perugini.pps_compiler.core.manifestiDegliStudi.usecase.ManifestoPDFUseCase;
import it.unisannio.studenti.p.perugini.pps_compiler.core.manifestiDegliStudi.port.ReadManifestoDegliStudiPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.ordinamento.port.ReadOrdinamentoPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class ManifestoDegliStudiService implements ManifestoPDFUseCase, AggiungiManfiestoUseCase {
    @Autowired
    private ReadManifestoDegliStudiPort readManifestoDegliStudiPort;
    @Autowired
    private ReadOrdinamentoPort readOrdinamentoPort;
    @Autowired
    private ReadAttivitaDidatticaPort readAttivitaDidatticaPort;
    @Autowired
    private CreateManifestoPort createManifestoPort;

    @Override
    public Optional<ManifestoDegliStudi> manifestoPDF(int coorte, String codiceCorsoDiStudio) {
        ChiaveManifestoDegliStudi chiave = new ChiaveManifestoDegliStudi();
        chiave.setCoorte(coorte);
        chiave.setCodiceCorsoDiStudio(codiceCorsoDiStudio);
        return  this.readManifestoDegliStudiPort.findManifestoById(chiave);
    }

    @Override
    public void addManifesto(ManifestoDegliStudi manifestoDegliStudi) throws RegolaNonValidaException, OrdinamentoNotFoundException {
        if(this.readManifestoDegliStudiPort.findManifestoById(manifestoDegliStudi.getChiaveManifestoDegliStudi()).isPresent())
            throw new RegolaNonValidaException("La regola è già presente nel database");

        //controllo se l'ordinamento inserito esiste
        Optional<Ordinamento> ordinamento = this.readOrdinamentoPort.findOrdinamentoById(manifestoDegliStudi.getAnnoOrdinamento());
        if (!(ordinamento.isPresent()))
            throw  new OrdinamentoNotFoundException("La regola non è associato ad un ordinamento valido");
        //controllo che i cfu a scelta libera siano nel range prestabilito
        if (manifestoDegliStudi.getCfuASceltaLibera() > ordinamento.get().getCfuMassimiAScelta()
                || manifestoDegliStudi.getCfuASceltaLibera()< ordinamento.get().getCfuMinimiAScelta())
            throw new RegolaNonValidaException("Il numero di cfu a scelta non è nel range specificato dall'ordinamento ");
        //controllo che i cfu totali siano nel range prestabilito
        if (manifestoDegliStudi.getCfuTotali() > ordinamento.get().getCfuMassimiCorsoDiLaurea()
                || manifestoDegliStudi.getCfuTotali()<ordinamento.get().getCfuMinimiCorsoDiLaurea())
            throw new RegolaNonValidaException("Il numero di cfu del corso di laurea non è nel range specificato dall'ordinamento");
        //controllo che i cfu di orientamento siano nel range prestabilito

        if (manifestoDegliStudi.getCfuOrientamento() > ordinamento.get().getCfuMassimiOrientamento()
                || manifestoDegliStudi.getCfuOrientamento() < ordinamento.get().getCfuMinimiOrientamento())
            throw new RegolaNonValidaException("Il numero di cfu di orientamento non è nel range specificato dall'ordinamento");

        if(manifestoDegliStudi.getCfuTotali()+ manifestoDegliStudi.getCfuExtra() > ordinamento.get().getCfuMassimiCorsoDiLaurea())
            throw new RegolaNonValidaException("La somma dei cfu totali e dei cfu extra supera il numero di cfu massimi del corso di laurea specificato nell'ordinamento");

        //controllo se i cfu totali sono >= di quelli inseriti nella regola
        int cfuTotaliEffettivi=0;

        //controllo che non ci siano duplicati
        //e che tutti gli insegnamenti siano insegnamenti presenti nel database
        Set<InsegnamentoRegola> insegnamentiTotali = new HashSet<>();
        for(Integer anno: manifestoDegliStudi.getAnniAccademici().keySet()){
            for(InsegnamentoRegola insegnamento: manifestoDegliStudi.getAnniAccademici().get(anno).getInsegnamentiObbligatori()){
                if(!insegnamentiTotali.add(insegnamento) && !insegnamento.isInsegnamentoIntegratoFlag())
                    throw new RegolaNonValidaException("Non può essere presente più volte lo stesso insegnamento: "+insegnamento.getDenominazioneInsegnamento());
                cfuTotaliEffettivi += insegnamento.getCfu();
            }

            if(manifestoDegliStudi.getAnniAccademici().get(anno).getAttivitaDidatticheAScelta().isPresent()) {
                for (InsegnamentoRegola insegnamento : manifestoDegliStudi.getAnniAccademici().get(anno).getAttivitaDidatticheAScelta().get().getInsegnamenti()) {
                    if (!insegnamentiTotali.add(insegnamento) && !insegnamento.isInsegnamentoIntegratoFlag())
                        throw new RegolaNonValidaException("Non può essere presente più volte lo stesso insegnamento: "+insegnamento.getDenominazioneInsegnamento());
                    cfuTotaliEffettivi += insegnamento.getCfu();
                }
            }

            if(manifestoDegliStudi.getAnniAccademici().get(anno).getOrientamenti().isPresent()) {
                for (Orientamento orientamento : manifestoDegliStudi.getAnniAccademici().get(anno).getOrientamenti().get()) {
                    int cfuVincolatiEffettivi = 0;
                    int cfuLiberiEffettivi = 0;
                    if (orientamento.getInsegnamentiLiberi().isPresent()) {
                        for (InsegnamentoRegola insegnamento : orientamento.getInsegnamentiLiberi().get()) {
                            if (!insegnamentiTotali.add(insegnamento) && !insegnamento.isInsegnamentoIntegratoFlag())
                                throw new RegolaNonValidaException("Non può essere presente più volte lo stesso insegnamento: " + insegnamento.getDenominazioneInsegnamento());
                            cfuTotaliEffettivi += insegnamento.getCfu();
                            cfuLiberiEffettivi += insegnamento.getCfu();
                        }
                        if (orientamento.getQuotaCFULiberi() > cfuLiberiEffettivi)
                            throw new RegolaNonValidaException("L'orientamento: " + orientamento.getDenominazione() + " presenta degli insegnamenti a scelta libera non coerenti con quanto dichiarato preliminarmente");
                    }
                    if (orientamento.getInsegnamentiVincolati().isPresent()) {
                        for (InsegnamentoRegola insegnamento : orientamento.getInsegnamentiVincolati().get()) {
                            if (!insegnamentiTotali.add(insegnamento) && !insegnamento.isInsegnamentoIntegratoFlag())
                                throw new RegolaNonValidaException("Non può essere presente più volte lo stesso insegnamento: " + insegnamento.getDenominazioneInsegnamento());
                            cfuTotaliEffettivi += insegnamento.getCfu();
                            cfuVincolatiEffettivi += insegnamento.getCfu();
                        }
                        if (orientamento.getQuotaCFUVincolati() > cfuVincolatiEffettivi)
                            throw new RegolaNonValidaException("L'orientamento: " + orientamento.getDenominazione() + " presenta degli insegnamenti vincolati non coerenti con quanto dichiarato preliminarmente");

                    }
                }
            }

            if(manifestoDegliStudi.getAnniAccademici().get(anno).getAttivitaDidatticheVincolateDalCorsoDiStudio().isPresent()){
                for(AttivitaDidatticheVincolateDalCorsoDiStudio insegnamenti: manifestoDegliStudi.getAnniAccademici().get(anno).getAttivitaDidatticheVincolateDalCorsoDiStudio().get()) {
                    for (InsegnamentoRegola insegnamento : insegnamenti.getInsegnamentiRegola()) {
                        if (!insegnamentiTotali.add(insegnamento) && !insegnamento.isInsegnamentoIntegratoFlag())
                            throw new RegolaNonValidaException("Non può essere presente più volte lo stesso insegnamento: " + insegnamento.getDenominazioneInsegnamento());
                        cfuTotaliEffettivi += insegnamento.getCfu();
                    }
                }
            }
        }

        for(InsegnamentoRegola insegnamentoRegola: insegnamentiTotali){
            Optional<AttivitaDidattica> attivitaDidattica = this.readAttivitaDidatticaPort.findAttivitaById(insegnamentoRegola.getCodiceInsegnamento());
            if(!attivitaDidattica.isPresent())
                throw new RegolaNonValidaException("l'insegnamento con codice: "+insegnamentoRegola.getCodiceInsegnamento()+" non è presente nel database");
        }


        if(manifestoDegliStudi.getCfuTotali() >cfuTotaliEffettivi)
            throw new RegolaNonValidaException("Nella regola sono presenti meno insegnamenti di quelli richiesti");

        for(InsegnamentoRegola insegnamento: insegnamentiTotali){
            if(insegnamento.isAnnualeFlag() && !(insegnamento.getSemestre().equals(SEMESTRE.annuale)))
                throw new RegolaNonValidaException("Un corso annuale deve avere come valore del semstre \"1-2\"");
        }
        this.createManifestoPort.save(manifestoDegliStudi);
    }
}
