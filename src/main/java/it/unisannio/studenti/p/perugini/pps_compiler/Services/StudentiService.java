package it.unisannio.studenti.p.perugini.pps_compiler.Services;

import it.unisannio.studenti.p.perugini.pps_compiler.API.*;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche.AttivitaDidatticaDiOrientamentoDTO;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche.AttivitaDidatticaPPSDTO;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche.InsegnamentoRegola;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.TipoCorsoDiLaurea;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.CorsoDiStudioNotFoundException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.PPSNonValidoException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.RegolaNotFoundException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.TipoCorsoDiLaureaNonSupportatoException;
import it.unisannio.studenti.p.perugini.pps_compiler.Repositories.CorsiDiStudioRepository;
import it.unisannio.studenti.p.perugini.pps_compiler.Repositories.AttivitaDidatticheRepository;
import it.unisannio.studenti.p.perugini.pps_compiler.Repositories.PPSRepository;
import it.unisannio.studenti.p.perugini.pps_compiler.Repositories.ManifestiDegliStudiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class StudentiService {

    @Autowired
    private AttivitaDidatticheRepository attivitaDidatticheRepository;
    @Autowired
    private CorsiDiStudioRepository corsiDiStudioRepository;
    @Autowired
    private InsegnamentoService insegnamentoService;
    @Autowired
    private ManifestiDegliStudiRepository manifestiDegliStudiRepository;
    @Autowired
    private PPSRepository ppsRepository;

    public List<AttivitaDidattica> getFreeChoiceCourses(String codiceCorsoDiStudio, int coorte) throws CorsoDiStudioNotFoundException, RegolaNotFoundException, TipoCorsoDiLaureaNonSupportatoException {
        List<AttivitaDidattica> insegnamentiLiberi = attivitaDidatticheRepository.getCorsiCompatibiliConSceltaLibera(codiceCorsoDiStudio);
        Optional<CorsoDiStudio>corsoDiStudioStudente =this.corsiDiStudioRepository.findById(codiceCorsoDiStudio);
        if(!corsoDiStudioStudente.isPresent())
            throw new CorsoDiStudioNotFoundException("il corso di studio con codice: "+codiceCorsoDiStudio+" non è presente nel database");
        ChiaveManifestoDegliStudi chiaveManifestoDegliStudi = new ChiaveManifestoDegliStudi();
        chiaveManifestoDegliStudi.setCoorte(coorte);
        chiaveManifestoDegliStudi.setCodiceCorsoDiStudio(codiceCorsoDiStudio);
        Optional<ManifestoDegliStudi> regolaOptional = this.manifestiDegliStudiRepository.findById(chiaveManifestoDegliStudi);
        if (!regolaOptional.isPresent())
            throw new RegolaNotFoundException("Non è presente la regola per la coorte: "+coorte);

        //li inserisco tutti in una lista, non mi interessa l'anno
        List<InsegnamentoRegola> insegnamentiObbligatori = new ArrayList<>();
        Map<Integer, AnnoAccademico> schemiDiPiano = regolaOptional.get().getAnniAccademici();
        for (Integer anno : schemiDiPiano.keySet())
            insegnamentiObbligatori.addAll(schemiDiPiano.get(anno).getInsegnamentiObbligatori());


        //elimino gli insegnamenti che erano obbligatori per la coorte
        //elimino gli insegnamenti che sono di corsi di laurea che non posso scegliere
        List<AttivitaDidattica> buffer = new ArrayList<>();
        buffer.addAll(insegnamentiLiberi);
        for(InsegnamentoRegola obbligatorio : insegnamentiObbligatori) {
            for (AttivitaDidattica libero : buffer) {
                if (obbligatorio.getDenominazioneInsegnamento().equals(libero.getDenominazioneAttivitaDidattica()))
                    insegnamentiLiberi.remove(libero);
            }
        }

        buffer = new ArrayList<>();
        buffer.addAll(insegnamentiLiberi);
        for(AttivitaDidattica libero: buffer){
            //recupero il corso di studio
            CorsoDiStudio corsoDiStudio = this.insegnamentoService.getCorsoDiStudioByInsegnamento(libero);
            //un triennale puo scegliere solo triennale
            if (corsoDiStudioStudente.get().getTipoCorsoDiLaurea().equals(TipoCorsoDiLaurea.L2) &&
                    corsoDiStudio.getTipoCorsoDiLaurea().equals(TipoCorsoDiLaurea.LM))
            {
                insegnamentiLiberi.remove(libero);
            }

            //un magistrale puo scegliere solo magistrale
            if (corsoDiStudioStudente.get().getTipoCorsoDiLaurea().equals(TipoCorsoDiLaurea.LM) &&
                    corsoDiStudio.getTipoCorsoDiLaurea().equals(TipoCorsoDiLaurea.L2))
            {
                insegnamentiLiberi.remove(libero);
            }
        }
        return insegnamentiLiberi;
    }

    public void addModulePPS(PPS pps, int coorte) throws TipoCorsoDiLaureaNonSupportatoException, RegolaNotFoundException, PPSNonValidoException {

       //controllo che sia possibile aggiungere un nuovo pps
        if(this.ppsRepository.findById(pps.getUser()).isPresent() &&
                this.ppsRepository.findById(pps.getUser()).get().isApprovato())
            throw new PPSNonValidoException("Il modulo è stato gia compilato, ed è stato approvato, non è possibile compilarne di nuovi");

        if(this.ppsRepository.findById(pps.getUser()).isPresent() &&
                !this.ppsRepository.findById(pps.getUser()).get().isApprovato() &&
                !this.ppsRepository.findById(pps.getUser()).get().isRifiutato())
            throw new PPSNonValidoException("Il modulo è stato gia compilato, è in attesa di revisione, non puoi compilarlo di nuovo nel frattempo! Abbi pazienza");

        //la coorte la devo passare perchè non è detto che l'anno in cui si effettua la scelta sia l'ultimo
        ChiaveManifestoDegliStudi chiaveManifestoDegliStudi = new ChiaveManifestoDegliStudi();
        chiaveManifestoDegliStudi.setCoorte(coorte);
        chiaveManifestoDegliStudi.setCodiceCorsoDiStudio(pps.getUser().getCorsoDiStudio().get().getCodice());
        Optional<ManifestoDegliStudi> regola = this.manifestiDegliStudiRepository.findById(chiaveManifestoDegliStudi);
        if(!regola.isPresent())
            throw new RegolaNotFoundException("Lo studente è di una coorte non presente nel DB del sistema");

        //controllo che quanto passato sia coerente con la regola
        if(!pps.getOrientamento().isPresent() && regola.get().getCfuOrientamento()!= 0)
            throw new PPSNonValidoException("La regola prevede che ci siano dei cfu di orientamento");
        if(pps.getOrientamento().isPresent() && regola.get().getCfuOrientamento()==0)
            throw new PPSNonValidoException("La regola non prevede che ci siano dei cfu di orientamento");
        //conto i cfu a scelta
        int countCfu = 0;
        for (AttivitaDidatticaPPSDTO insegnamento: pps.getInsegnamentiASceltaLibera())
            countCfu+=insegnamento.getCfu();

        if(countCfu != regola.get().getCfuASceltaLibera() && countCfu-regola.get().getCfuASceltaLibera() > regola.get().getCfuExtra())
            throw new PPSNonValidoException("La regola prevede un numero di cfu liberi diverso da quello da te indicato");

        //controllo che l'orientamento scelto sia tra quelli proposti
        if(pps.getOrientamento().isPresent()) {
            Map<Integer, AnnoAccademico> schemaDiPianoMap = regola.get().getAnniAccademici();
            boolean vincolati = false;
            for (Integer anno : schemaDiPianoMap.keySet()) {
                if (schemaDiPianoMap.get(anno).getOrientamenti().isPresent()) {
                    for (Orientamento orientamento : schemaDiPianoMap.get(anno).getOrientamenti().get()) {
                        if (orientamento.getInsegnamentiVincolati().isPresent() && !orientamento.getInsegnamentiLiberi().isPresent()) {
                            if (checkVincolatiOLiberi(pps.getOrientamento().get(),orientamento.getInsegnamentiVincolati().get()))
                                vincolati = true;
                        }else{
                            if (!orientamento.getInsegnamentiVincolati().isPresent() && orientamento.getInsegnamentiLiberi().isPresent()){
                                if (checkVincolatiOLiberi(pps.getOrientamento().get(),orientamento.getInsegnamentiVincolati().get()))
                                    vincolati = true;
                            }else{
                                if(checkInsegnamentiLiberiEVincolati(pps.getOrientamento().get(),orientamento.getInsegnamentiVincolati().get(), orientamento.getInsegnamentiLiberi().get()))
                                    vincolati = true;
                            }
                        }
                        if(vincolati) break;
                    }
                }

            }

            if (!vincolati)
                throw new PPSNonValidoException("L'orientamento scelto non è della tua coorte");

            int cfuOrientamento = 0;
            for(AttivitaDidatticaDiOrientamentoDTO attivitaDidatticaDiOrientamentoDTO : pps.getOrientamento().get())
                cfuOrientamento+= attivitaDidatticaDiOrientamentoDTO.getCfu();

            if(cfuOrientamento != regola.get().getCfuOrientamento())
                throw new PPSNonValidoException("Hai specificato un numero di cfu di orientamento non conforme alla regola della tua coorte");
        }
        Set<String>insegnamentiTotali = new HashSet<>();
        boolean valid=false;
        //FIXME
        //dovrei cercare nella regola se l'insegnamento è integrato altrimenti rischio di eliminare pps valdi
        for(AttivitaDidatticaPPSDTO attivitaDidatticaPPSDTO : pps.getInsegnamentiASceltaLibera()){
            if(!insegnamentiTotali.add(attivitaDidatticaPPSDTO.getCodiceAttivitaDidattica())){
                throw new PPSNonValidoException("L'insegnamento con codice: "+ attivitaDidatticaPPSDTO.getCodiceAttivitaDidattica()+" è presente più volte");
            }//se c'è almeno un corso che non è di automatica approvazione è valido il pps
            if(!attivitaDidatticaPPSDTO.getCodiceCorsoDiStudio().equals(pps.getUser().getCorsoDiStudio().get().getCodice())){
                valid=true;
            }

            if(!this.attivitaDidatticheRepository.findById(attivitaDidatticaPPSDTO.getCodiceAttivitaDidattica()).isPresent()){
                throw new PPSNonValidoException("L'insegnamento con codice: "+ attivitaDidatticaPPSDTO.getCodiceAttivitaDidattica()+" non è presente nel database");
            }
        }

        if(!valid)
            throw new PPSNonValidoException("Sono tutti insegnamenti di automatica approvazione, non serve compilare questo modulo");

        //controllo che effettivamente lo studente possa compilare il modulo
        int currentYear = LocalDate.now().getYear();
        int annoInCuiScegliere = currentYear-coorte+1;
        //se nell'anno in cui sto effettuando la scelta, non sono presenti insegnamenti a scelta oppure insegnamenti di orientamento
        //signigica che sto compilando il modulo in un periodo in cui non è consentito farlo per il mio corso di studio
        if(!regola.get().getAnniAccademici().get(annoInCuiScegliere).getAttivitaDidatticheAScelta().isPresent() &&
                !regola.get().getAnniAccademici().get(annoInCuiScegliere).getOrientamenti().isPresent())
            throw new PPSNonValidoException("Impossibile compilare il modulo PPS. Si sta cercando di compilare il modulo in un anno in cui non ci sono insegnamenti a scelta oppure di orientamento");



        //controllare che non siano presenti piu volte gli stessi insegnamenti
        //controllare che ci sia almeno un insegnamento che non sia del proprio corso di studi
        //controllare che gli insegnamenti siano validi

        //se ho passato tutti i controlli
        this.ppsRepository.save(pps);


    }

    private boolean checkVincolatiOLiberi(List<AttivitaDidatticaDiOrientamentoDTO> insegnamentiOrientamento, List<InsegnamentoRegola> insegnamentoRegolas) {
        Set<String>codici = new HashSet<>();
        for(AttivitaDidatticaDiOrientamentoDTO attivitaDidatticaDiOrientamentoDTO : insegnamentiOrientamento)
            codici.add(attivitaDidatticaDiOrientamentoDTO.getCodiceAttivitaDidattica());
        //se inserendo un vincolato, me lo fa inserire, non va bene
        for(InsegnamentoRegola insegnamentoRegola: insegnamentoRegolas)
            if(codici.add(insegnamentoRegola.getCodiceInsegnamento()))
                return false;
            return true;
    }

    private boolean checkInsegnamentiLiberiEVincolati(List<AttivitaDidatticaDiOrientamentoDTO> insegnamentiOrientamento, List<InsegnamentoRegola> insegnamentiVincolati, List<InsegnamentoRegola>insegnamentiLiberi) {
        List<String>codici = new ArrayList<>();
        for(InsegnamentoRegola insegnamentoRegola: insegnamentiVincolati)
            codici.add(insegnamentoRegola.getCodiceInsegnamento());

        //controllo che ci siano tutti i vincolati
        int count =0;
        for(AttivitaDidatticaDiOrientamentoDTO attivitaDidatticaDiOrientamentoDTO : insegnamentiOrientamento)
            if (codici.contains(attivitaDidatticaDiOrientamentoDTO.getCodiceAttivitaDidattica()))
                count++;

        if(count != codici.size())
            return false;
        //ora controllo quelli liberi aggiungendo anche loro al set
        for(InsegnamentoRegola insegnamentoRegola: insegnamentiLiberi)
            codici.add(insegnamentoRegola.getCodiceInsegnamento());

        //devono stare tutti in questo insieme di elementi
        for(AttivitaDidatticaDiOrientamentoDTO attivitaDidatticaDiOrientamentoDTO : insegnamentiOrientamento)
            if(!codici.contains(attivitaDidatticaDiOrientamentoDTO.getCodiceAttivitaDidattica()))
                return false;
        return true;
    }
}
