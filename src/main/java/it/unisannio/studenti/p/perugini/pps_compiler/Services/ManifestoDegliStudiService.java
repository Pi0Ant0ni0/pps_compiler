package it.unisannio.studenti.p.perugini.pps_compiler.Services;

import it.unisannio.studenti.p.perugini.pps_compiler.API.*;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.SEMESTRE;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche.AttivitaDidatticaPPSDTO;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche.InsegnamentoRegola;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.constants.ERR_MESSAGES;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.OrdinamentoNotFoundException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.ManifestoDegliStudiNonValidoException;
import it.unisannio.studenti.p.perugini.pps_compiler.core.attivitaDidattica.port.ReadAttivitaDidatticaPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.manifestiDegliStudi.port.CreateManifestoPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.manifestiDegliStudi.port.ListManifestiDegliStudiPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.manifestiDegliStudi.usecase.AggiungiManfiestoUseCase;
import it.unisannio.studenti.p.perugini.pps_compiler.core.manifestiDegliStudi.usecase.ManifestoPDFUseCase;
import it.unisannio.studenti.p.perugini.pps_compiler.core.manifestiDegliStudi.port.ReadManifestoDegliStudiPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.manifestiDegliStudi.usecase.VisualizzaManifestoUseCase;
import it.unisannio.studenti.p.perugini.pps_compiler.core.ordinamento.port.ReadOrdinamentoPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ManifestoDegliStudiService implements ManifestoPDFUseCase, AggiungiManfiestoUseCase, VisualizzaManifestoUseCase {
    @Autowired
    private ReadManifestoDegliStudiPort readManifestoDegliStudiPort;
    @Autowired
    private ListManifestiDegliStudiPort listManifestiDegliStudiPort;
    @Autowired
    private ReadOrdinamentoPort readOrdinamentoPort;
    @Autowired
    private ReadAttivitaDidatticaPort readAttivitaDidatticaPort;
    @Autowired
    private CreateManifestoPort createManifestoPort;

    @Override
    public Optional<ManifestoDegliStudi> manifestoPDF(int coorte, String codiceCorsoDiStudio, String curricula) {
        ChiaveManifestoDegliStudi chiave = new ChiaveManifestoDegliStudi();
        chiave.setCoorte(coorte);
        chiave.setCodiceCorsoDiStudio(codiceCorsoDiStudio);
        if(curricula.length()!=0)
            chiave.setCurricula(curricula);
        else chiave.setCurricula(null);
        return  this.readManifestoDegliStudiPort.findManifestoById(chiave);
    }

    @Override
    public void addManifesto(ManifestoDegliStudi manifestoDegliStudi) throws ManifestoDegliStudiNonValidoException, OrdinamentoNotFoundException {

        if(this.readManifestoDegliStudiPort.findManifestoById(manifestoDegliStudi.getChiaveManifestoDegliStudi()).isPresent())
            throw new ManifestoDegliStudiNonValidoException(ERR_MESSAGES.MANIFESTO_GIA_PRESENTE);

        //controllo se l'ordinamento inserito esiste
        ChiaveOrdinamento chiaveOrdinamento = new ChiaveOrdinamento();
        chiaveOrdinamento.setAnnoDiRedazione(manifestoDegliStudi.getAnnoOrdinamento());
        chiaveOrdinamento.setCodiceCorsoDiStudio(manifestoDegliStudi.getChiaveManifestoDegliStudi().getCodiceCorsoDiStudio());
        Optional<Ordinamento> ordinamento = this.readOrdinamentoPort.findOrdinamentoById(chiaveOrdinamento);
        if (!(ordinamento.isPresent()))
            throw  new OrdinamentoNotFoundException(ERR_MESSAGES.MANIFESTO_ORDINAMENTO_NOT_FOUND);
        //controllo che i cfu a scelta libera siano nel range prestabilito
        if (manifestoDegliStudi.getCfuASceltaLibera() > ordinamento.get().getCfuMassimiAScelta()
                || manifestoDegliStudi.getCfuASceltaLibera()< ordinamento.get().getCfuMinimiAScelta())
            throw new ManifestoDegliStudiNonValidoException(ERR_MESSAGES.MANIFESTO_CFU_A_SCELTA_ORDINAMENTO);
        //controllo che i cfu totali siano nel range prestabilito
        if (manifestoDegliStudi.getCfuTotali() > ordinamento.get().getCfuMassimiCorsoDiLaurea()
                || manifestoDegliStudi.getCfuTotali()<ordinamento.get().getCfuMinimiCorsoDiLaurea())
            throw new ManifestoDegliStudiNonValidoException(ERR_MESSAGES.MANIFESTO_CFU_TOTALI_ORDINAMENTO);
        //controllo che i cfu di orientamento siano nel range prestabilito

        if (manifestoDegliStudi.getCfuOrientamento() > ordinamento.get().getCfuMassimiOrientamento()
                || manifestoDegliStudi.getCfuOrientamento() < ordinamento.get().getCfuMinimiOrientamento())
            throw new ManifestoDegliStudiNonValidoException(ERR_MESSAGES.MANIFESTO_CFU_ORIENTAMENTO_ORDINAMENTO);

        if(manifestoDegliStudi.getCfuTotali()+ manifestoDegliStudi.getCfuExtra() > ordinamento.get().getCfuMassimiCorsoDiLaurea())
            throw new ManifestoDegliStudiNonValidoException(ERR_MESSAGES.MANIFESTO_CFU_TOTALI);

        //controllo se i cfu totali sono >= di quelli inseriti nella regola
        int cfuTotaliEffettivi=0;

        //controllo che non ci siano duplicati
        //e che tutti gli insegnamenti siano insegnamenti presenti nel database
        Set<InsegnamentoRegola> insegnamentiTotali = new HashSet<>();
        for(Integer anno: manifestoDegliStudi.getAnniAccademici().keySet()){
            for(InsegnamentoRegola insegnamento: manifestoDegliStudi.getAnniAccademici().get(anno).getInsegnamentiObbligatori()){
                if(!insegnamentiTotali.add(insegnamento) && !insegnamento.isInsegnamentoIntegratoFlag())
                    throw new ManifestoDegliStudiNonValidoException(ERR_MESSAGES.MANIFESTO_ATTIVITA_DUPLICATA+insegnamento.getDenominazioneInsegnamento());
                if(!this.readAttivitaDidatticaPort.findAttivitaById(insegnamento.getCodiceInsegnamento()).isPresent())
                    throw new ManifestoDegliStudiNonValidoException(ERR_MESSAGES.ATTIVITA_NOT_FOUND+insegnamento.getCodiceInsegnamento());
                cfuTotaliEffettivi += insegnamento.getCfu();
            }

            //controllo le attivita a scelta che non siano duplicate e siano tutte presenti nel database
            if(manifestoDegliStudi.getAttivitaDidatticheAScelta().isPresent()){
                for(AttivitaDidatticaPPSDTO attivitaDidatticaPPSDTO: manifestoDegliStudi.getAttivitaDidatticheAScelta().get())
                    if(!readAttivitaDidatticaPort.findAttivitaById(attivitaDidatticaPPSDTO.getCodiceAttivitaDidattica()).isPresent())
                        throw new ManifestoDegliStudiNonValidoException(ERR_MESSAGES.ATTIVITA_NOT_FOUND+attivitaDidatticaPPSDTO.getCodiceAttivitaDidattica());
            }


            if(manifestoDegliStudi.getAnniAccademici().get(anno).getOrientamenti().isPresent()) {
                for (Orientamento orientamento : manifestoDegliStudi.getAnniAccademici().get(anno).getOrientamenti().get()) {
                    int cfuVincolatiEffettivi = 0;
                    int cfuLiberiEffettivi = 0;
                    if (orientamento.getInsegnamentiLiberi().isPresent()) {
                        for (InsegnamentoRegola insegnamento : orientamento.getInsegnamentiLiberi().get()) {
                            if (!insegnamentiTotali.add(insegnamento))
                                throw new ManifestoDegliStudiNonValidoException(ERR_MESSAGES.MANIFESTO_ATTIVITA_DUPLICATA+insegnamento.getDenominazioneInsegnamento());
                            if(!this.readAttivitaDidatticaPort.findAttivitaById(insegnamento.getCodiceInsegnamento()).isPresent())
                                throw new ManifestoDegliStudiNonValidoException(ERR_MESSAGES.ATTIVITA_NOT_FOUND+insegnamento.getCodiceInsegnamento());
                            cfuTotaliEffettivi += insegnamento.getCfu();
                            cfuLiberiEffettivi += insegnamento.getCfu();
                        }
                        if (orientamento.getQuotaCFULiberi() > cfuLiberiEffettivi)
                            throw new ManifestoDegliStudiNonValidoException(ERR_MESSAGES.MANIFESTO_CFU_ORIENTAMENTO);
                    }
                    if (orientamento.getInsegnamentiVincolati().isPresent()) {
                        for (InsegnamentoRegola insegnamento : orientamento.getInsegnamentiVincolati().get()) {
                            if (!insegnamentiTotali.add(insegnamento))
                                throw new ManifestoDegliStudiNonValidoException(ERR_MESSAGES.MANIFESTO_ATTIVITA_DUPLICATA+insegnamento.getDenominazioneInsegnamento());
                            if(!this.readAttivitaDidatticaPort.findAttivitaById(insegnamento.getCodiceInsegnamento()).isPresent())
                                throw new ManifestoDegliStudiNonValidoException(ERR_MESSAGES.ATTIVITA_NOT_FOUND+insegnamento.getCodiceInsegnamento());
                            cfuTotaliEffettivi += insegnamento.getCfu();
                            cfuVincolatiEffettivi += insegnamento.getCfu();
                        }
                        if (orientamento.getQuotaCFUVincolati() > cfuVincolatiEffettivi)
                            throw new ManifestoDegliStudiNonValidoException(ERR_MESSAGES.MANIFESTO_CFU_ORIENTAMENTO);

                    }
                }
            }

            if(manifestoDegliStudi.getAnniAccademici().get(anno).getAttivitaDidatticheVincolateDalCorsoDiStudio().isPresent()){
                for(AttivitaDidatticheVincolateDalCorsoDiStudio insegnamenti: manifestoDegliStudi.getAnniAccademici().get(anno).getAttivitaDidatticheVincolateDalCorsoDiStudio().get()) {
                    for (InsegnamentoRegola insegnamento : insegnamenti.getInsegnamentiRegola()) {
                        if (!insegnamentiTotali.add(insegnamento))
                            throw new ManifestoDegliStudiNonValidoException(ERR_MESSAGES.MANIFESTO_ATTIVITA_DUPLICATA+insegnamento.getDenominazioneInsegnamento());
                        if(!this.readAttivitaDidatticaPort.findAttivitaById(insegnamento.getCodiceInsegnamento()).isPresent())
                            throw new ManifestoDegliStudiNonValidoException(ERR_MESSAGES.ATTIVITA_NOT_FOUND+insegnamento.getCodiceInsegnamento());
                        cfuTotaliEffettivi += insegnamento.getCfu();
                    }
                }
            }
        }
        //dato che gli insegnamenti a scelta sono corretti li aggiungo
        cfuTotaliEffettivi+=manifestoDegliStudi.getCfuASceltaLibera();
        //dato che gli orientamenti sono corretti
        cfuTotaliEffettivi+=manifestoDegliStudi.getCfuOrientamento();
        //controllo il conteggio totale
        if(manifestoDegliStudi.getCfuTotali()>cfuTotaliEffettivi)
            throw new ManifestoDegliStudiNonValidoException(ERR_MESSAGES.MANIFESTO_CFU_TOTALI);

        for(InsegnamentoRegola insegnamento: insegnamentiTotali){
            if(insegnamento.isAnnualeFlag() && !(insegnamento.getSemestre().equals(SEMESTRE.annuale)))
                throw new ManifestoDegliStudiNonValidoException(ERR_MESSAGES.ATTIVITA_NOT_VALID+insegnamento.getCodiceInsegnamento());
        }

        //controllo che le date di compilazioni siano corrette
        if(manifestoDegliStudi.getDataInizioCompilazionePiano().isAfter(manifestoDegliStudi.getDataFineCompilazionePiano()))
            throw new ManifestoDegliStudiNonValidoException(ERR_MESSAGES.MANIFESTO_FINESTRA_COMPILAZIONE_NON_VALIDA);
        this.createManifestoPort.save(manifestoDegliStudi);
    }

    @Override
    public List<ManifestoDegliStudi> getManifesti(String codiceCorsoDiStudio) {
        return this.listManifestiDegliStudiPort.list()
                .stream()
                .filter(manifestoDegliStudi -> manifestoDegliStudi.getChiaveManifestoDegliStudi().getCodiceCorsoDiStudio().equals(codiceCorsoDiStudio))
                .collect(Collectors.toList());
    }
}
