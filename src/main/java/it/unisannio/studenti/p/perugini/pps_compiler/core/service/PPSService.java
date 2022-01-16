package it.unisannio.studenti.p.perugini.pps_compiler.core.service;

import it.unisannio.studenti.p.perugini.pps_compiler.API.*;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Email;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Role;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche.AttivitaDidatticaDiOrientamentoDTO;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche.AttivitaDidatticaPPSDTO;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche.InsegnamentoRegola;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.PPS.PPSAggiuntaDTO;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.PPS.PPSMapper;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.*;
import it.unisannio.studenti.p.perugini.pps_compiler.core.manifestiDegliStudi.port.ReadManifestoDegliStudiPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.pps.port.CreatePPSPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.pps.port.ListPPSPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.pps.port.ReadPPSPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.pps.port.UpdatePPSPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.pps.usecase.*;
import it.unisannio.studenti.p.perugini.pps_compiler.core.user.port.ReadUserPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

/**Servizio che implementa lo use case di compilazione del pps*/
@Service
public class PPSService implements CompilaPPSUseCase,
        VisualizzaStatoPPSUseCase,
        VisualizzaPPSInSospesoUseCase,
        VisualizzaPPSVisionatiUseCase,
        AccettaPPSUseCase,
        RifiutaPPSUseCase {

    /**Porta per interfacciarsi con la collezione dei manifesti in sola lettura*/
    @Autowired
    private ReadManifestoDegliStudiPort readManifestoDegliStudiPort;
    /**Porta per interfacciarsi in lettura e scrittura con la collezione dei pps*/
    @Autowired
    private CreatePPSPort createPPSPort;
    @Autowired
    private ReadPPSPort readPPSPort;
    @Autowired
    private ListPPSPort listPPSPort;
    @Autowired
    private UpdatePPSPort updatePPSPort;
    /**Porta per interfacciarsi con gli user in lettura*/
    @Autowired
    private ReadUserPort readUserPort;
    /**Mapper per portare il DTO a entità del database*/
    @Autowired
    private PPSMapper ppsMapper;

    @Override
    public void compila(PPSAggiuntaDTO pps, Email email) throws InsegnamentoNotFoundException, PPSNonValidoException, RegolaNotFoundException {
        User user = this.readUserPort.findUserById(email).get();
        PPS ppsEntity = ppsMapper.fromPPSDTOToPPS(pps,user);
        this.validate(ppsEntity, pps.getCoorte());
        //se non vengo fermato prima salvo
        this.createPPSPort.save(ppsEntity);
    }

    private void validate(PPS pps, int coorte) throws PPSNonValidoException, RegolaNotFoundException {
        if(coorte>LocalDate.now().getYear())
            throw new PPSNonValidoException("La coorte non puo essere maggiore all'anno corrente");

        //controllo che sia possibile aggiungere un nuovo pps
        if(this.readPPSPort.findPPSById(pps.getUser()).isPresent() &&
                this.readPPSPort.findPPSById(pps.getUser()).get().isApprovato())
            throw new PPSNonValidoException("Il modulo è stato gia compilato, ed è stato approvato, non è possibile compilarne di nuovi");

        if(this.readPPSPort.findPPSById(pps.getUser()).isPresent() &&
                !this.readPPSPort.findPPSById(pps.getUser()).get().isApprovato() &&
                !this.readPPSPort.findPPSById(pps.getUser()).get().isRifiutato())
            throw new PPSNonValidoException("Il modulo è stato gia compilato, è in attesa di revisione, non puoi compilarlo di nuovo nel frattempo! Abbi pazienza");


        ChiaveManifestoDegliStudi chiaveManifestoDegliStudi = new ChiaveManifestoDegliStudi();
        chiaveManifestoDegliStudi.setCoorte(coorte);
        chiaveManifestoDegliStudi.setCodiceCorsoDiStudio(pps.getUser().getCorsoDiStudio().get().getCodice());
        Optional<ManifestoDegliStudi> manifesto = this.readManifestoDegliStudiPort.findManifestoById(chiaveManifestoDegliStudi);
        if(!manifesto.isPresent())
            throw new RegolaNotFoundException("Lo studente è di una coorte di cui non si ha a disposizione il manifesto degli studi, riportare il problema alla segreteria didattica");

        //controllo che quanto passato sia coerente con la regola
        if(!pps.getOrientamento().isPresent() && manifesto.get().getCfuOrientamento()!= 0)
            throw new PPSNonValidoException("La regola prevede che ci siano dei cfu di orientamento");
        if(pps.getOrientamento().isPresent() && manifesto.get().getCfuOrientamento()==0)
            throw new PPSNonValidoException("La regola non prevede che ci siano dei cfu di orientamento");
        //conto i cfu a scelta
        int countCfu = 0;
        for (AttivitaDidatticaPPSDTO insegnamento: pps.getInsegnamentiASceltaLibera())
            countCfu+=insegnamento.getCfu();

        if(countCfu != manifesto.get().getCfuASceltaLibera() && countCfu-manifesto.get().getCfuASceltaLibera() > manifesto.get().getCfuExtra())
            throw new PPSNonValidoException("La regola prevede un numero di cfu liberi diverso da quello da te indicato");

        //controllo che l'orientamento scelto sia tra quelli proposti
        if(pps.getOrientamento().isPresent()) {
            Map<Integer, AnnoAccademico> schemaDiPianoMap = manifesto.get().getAnniAccademici();
            boolean orientamentoValido = false;
            for (Integer anno : schemaDiPianoMap.keySet()) {
                if (schemaDiPianoMap.get(anno).getOrientamenti().isPresent()) {
                    for (Orientamento orientamento : schemaDiPianoMap.get(anno).getOrientamenti().get()) {
                        if (orientamento.getInsegnamentiVincolati().isPresent() && !orientamento.getInsegnamentiLiberi().isPresent()) {
                            if (checkVincolatiOLiberi(pps.getOrientamento().get(),orientamento.getInsegnamentiVincolati().get()))
                                orientamentoValido = true;
                        }else{
                            if (!orientamento.getInsegnamentiVincolati().isPresent() && orientamento.getInsegnamentiLiberi().isPresent()){
                                if (checkVincolatiOLiberi(pps.getOrientamento().get(),orientamento.getInsegnamentiVincolati().get()))
                                    orientamentoValido = true;
                            }else{
                                if(checkInsegnamentiLiberiEVincolati(pps.getOrientamento().get(),orientamento.getInsegnamentiVincolati().get(), orientamento.getInsegnamentiLiberi().get()))
                                    orientamentoValido = true;
                            }
                        }
                        if(orientamentoValido) break;
                    }
                }

            }

            if (!orientamentoValido)
                throw new PPSNonValidoException("L'orientamento scelto non è della tua coorte");
        }

        Set<String> insegnamentiTotali = new HashSet<>();
        boolean valid=false;
        for(AttivitaDidatticaPPSDTO attivitaDidatticaPPSDTO : pps.getInsegnamentiASceltaLibera()){
            if(!insegnamentiTotali.add(attivitaDidatticaPPSDTO.getCodiceAttivitaDidattica())){
                throw new PPSNonValidoException("L'insegnamento con codice: "+ attivitaDidatticaPPSDTO.getCodiceAttivitaDidattica()+" è presente più volte");
            }//se c'è almeno un corso che non è di automatica approvazione è valido il pps
            if(!attivitaDidatticaPPSDTO.getCodiceCorsoDiStudio().equals(pps.getUser().getCorsoDiStudio().get().getCodice())){
                valid=true;
            }
        }

        if(!valid)
            throw new PPSNonValidoException("Sono tutti insegnamenti di automatica approvazione, non serve compilare questo modulo");

        //controllo che effettivamente lo studente possa compilare il modulo
        int currentYear = LocalDate.now().getYear();
        //calcolo l'anno accademico
        int annoInCuiScegliere = currentYear-coorte+1;
        //se sono della corte 2018 posso compilare nel 2020 (2020-2018+1= 3 e nel 3 anno ho la scelta)
        //se provo a compilare nel 2021 ottengo (2020-2019+1=4 che non è negli anni accademici)
        if(!manifesto.get().getAnniAccademici().keySet().contains(annoInCuiScegliere))
            throw new PPSNonValidoException("Non è possibile compilare il modulo in questo periodo");

        //se nell'anno in cui sto effettuando la scelta, non sono presenti insegnamenti a scelta oppure insegnamenti di orientamento
        //signigica che sto compilando il modulo in un periodo in cui non è consentito farlo per il mio corso di studio
        if(!manifesto.get().getAnniAccademici().get(annoInCuiScegliere).getAttivitaDidatticheAScelta().isPresent() &&
                !manifesto.get().getAnniAccademici().get(annoInCuiScegliere).getOrientamenti().isPresent())
            throw new PPSNonValidoException("Impossibile compilare il modulo PPS. Si sta cercando di compilare il modulo in un anno in cui non ci sono insegnamenti a scelta oppure di orientamento");

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

    @Override
    public Optional<PPS> getPPS(Email emailRichiedente, Email emailPPS) throws RichiestaNonValidaException {
        User userRichiedente = this.readUserPort.findUserById(emailRichiedente).get();
        Optional<User> user = this.readUserPort.findUserById(emailPPS);
        if(!user.isPresent())
            throw new RichiestaNonValidaException("L' email specificata non è associata a nessun account");

        if(userRichiedente.getRole().equals(Role.STUDENTE)){
            if (!user.get().getEmail().equals(userRichiedente.getEmail()))
                throw new RichiestaNonValidaException("Non è possibile richiedere il PPS di un altro studente");
        }
        //se sono uno studente che richiede il proprio modulo pps oppure un docente
        //posso accedere al modulo se presente
        Optional<PPS> pps = this.readPPSPort.findPPSById(user.get());
        return pps;
    }

    @Override
    public List<PPS> getPpsInSospeso(Email email) {
        User user = this.readUserPort.findUserById(email).get();
        return this.listPPSPort.findPPSInSospesoByUser(user);
    }

    @Override
    public List<PPS> getPPSVisionati(Email email) {
        User user = this.readUserPort.findUserById(email).get();
        return this.listPPSPort.findPPSVisionatiByUser(user);
    }

    @Override
    public void accettaPPS(Email email) throws PPSNotFoundException, UserNotFound, PPSNonValidoException {
        Optional<User> user = this.readUserPort.findUserById(email);
        if(user.isPresent()) this.updatePPSPort.accettaPPS(user.get());
        else throw new UserNotFound("L'utente non risulta essere presente nel database");
    }

    @Override
    public void rififutaPPS(Email email) throws PPSNotFoundException, UserNotFound, PPSNonValidoException {
        Optional<User> user = this.readUserPort.findUserById(email);
        if(user.isPresent()) this.updatePPSPort.rifiutaPPS(user.get());
        else throw new UserNotFound("L'utente non risulta essere presente nel database");
    }
}
