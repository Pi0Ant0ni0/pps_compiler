package it.unisannio.studenti.p.perugini.pps_compiler.Services;

import it.unisannio.studenti.p.perugini.pps_compiler.API.*;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Email;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Role;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche.InsegnamentoRegola;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.PPS.PPSAggiuntaDTO;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.PPS.PPSMapper;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.User.UserMapper;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.*;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.constants.ERR_MESSAGES;
import it.unisannio.studenti.p.perugini.pps_compiler.persistance.Repositories.User;
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
    @Autowired
    private UserMapper userMapper;

    @Override
    public void compila(PPSAggiuntaDTO pps, Email email) throws InsegnamentoNotFoundException, PPSNonValidoException, RegolaNotFoundException {
        User user = this.readUserPort.findUserById(email).get();
        PPS ppsEntity = ppsMapper.fromPPSDTOToPPS(pps,user);
        this.validate(ppsEntity, pps.getCoorte(),pps.getCurriculum());
        //se non vengo fermato prima salvo
        this.createPPSPort.save(ppsEntity);
    }

    private void validate(PPS pps, int coorte, String curriculum) throws PPSNonValidoException, RegolaNotFoundException {
        if(coorte>LocalDate.now().getYear())
            throw new PPSNonValidoException("La coorte non puo essere maggiore all'anno corrente");

        //controllo che sia possibile aggiungere un nuovo pps
        if(this.readPPSPort.findPPSById(pps.getStudente()).isPresent() &&
                this.readPPSPort.findPPSById(pps.getStudente()).get().isApprovato())
            throw new PPSNonValidoException(ERR_MESSAGES.PPS_COMPILATO_ACCETTATO);

        if(this.readPPSPort.findPPSById(pps.getStudente()).isPresent() &&
                !this.readPPSPort.findPPSById(pps.getStudente()).get().isApprovato() &&
                !this.readPPSPort.findPPSById(pps.getStudente()).get().isRifiutato())
            throw new PPSNonValidoException(ERR_MESSAGES.PPS_COMPILATO_IN_REVISIONE);


        ChiaveManifestoDegliStudi chiaveManifestoDegliStudi = new ChiaveManifestoDegliStudi();
        chiaveManifestoDegliStudi.setCoorte(coorte);
        chiaveManifestoDegliStudi.setCodiceCorsoDiStudio(pps.getStudente().getCorsoDiStudio().getCodice());
        if(curriculum!= null && curriculum.length()!=0)
            chiaveManifestoDegliStudi.setCurricula(curriculum);
        else chiaveManifestoDegliStudi.setCurricula(null);
        Optional<ManifestoDegliStudi> manifesto = this.readManifestoDegliStudiPort.findManifestoById(chiaveManifestoDegliStudi);
        if(!manifesto.isPresent())
            throw new RegolaNotFoundException(ERR_MESSAGES.MANIFESTO_NOT_FOUND+chiaveManifestoDegliStudi.getCodiceCorsoDiStudio()+", "+chiaveManifestoDegliStudi.getCoorte());


        //controllo che quanto passato sia coerente con la regola
        if(!pps.getOrientamento().isPresent() && manifesto.get().getCfuOrientamento()!= 0)
            throw new PPSNonValidoException(ERR_MESSAGES.PPS_ORIENTAMENTO_NOT_FOUND);
        if(pps.getOrientamento().isPresent() && manifesto.get().getCfuOrientamento()==0)
            throw new PPSNonValidoException(ERR_MESSAGES.PPS_ORIENTAMENTO_NOT_REQUIRED);
        //conto i cfu a scelta
        int countCfu = 0;
        for (AttivitaDidattica insegnamento: pps.getInsegnamentiASceltaLibera())
            countCfu+=insegnamento.getCfu();

        if(countCfu != manifesto.get().getCfuASceltaLibera() &&
                countCfu-manifesto.get().getCfuASceltaLibera() > manifesto.get().getCfuExtra())
            throw new PPSNonValidoException(ERR_MESSAGES.PPS_ORIENTAMENTO_NOT_VALID);

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
                throw new PPSNonValidoException(ERR_MESSAGES.PPS_ORIENTAMENTO_NOT_VALID);
        }

        Set<String> insegnamentiTotali = new HashSet<>();
        boolean valid=false;
        for(AttivitaDidattica attivitaDidatticaPPSDTO : pps.getInsegnamentiASceltaLibera()){
            if(!insegnamentiTotali.add(attivitaDidatticaPPSDTO.getCodiceAttivitaDidattica())){
                throw new PPSNonValidoException(ERR_MESSAGES.PPS_ATTIVITA_DUPLICATA);
            }//se c'è almeno un corso che non è di automatica approvazione è valido il pps
            if(! (manifesto.get().getAttivitaDidatticheAScelta().get().contains(attivitaDidatticaPPSDTO)) ){
                valid=true;
            }
        }

        if(!valid)
            throw new PPSNonValidoException(ERR_MESSAGES.PPS_AUTOMATICA_APPROVAZIONE);

        //controllo che effettivamente lo studente possa compilare il modulo
        if(LocalDate.now().isBefore(manifesto.get().getDataInizioCompilazionePiano()) ||
                LocalDate.now().isAfter(manifesto.get().getDataFineCompilazionePiano()))
            throw new PPSNonValidoException(ERR_MESSAGES.PPS_FINESTRA_COMPILAZIONE);

    }

    private boolean checkVincolatiOLiberi(List<AttivitaDidattica> insegnamentiOrientamento, List<InsegnamentoRegola> insegnamentoRegolas) {
        Set<String>codici = new HashSet<>();
        for(AttivitaDidattica attivitaDidatticaDiOrientamentoDTO : insegnamentiOrientamento)
            codici.add(attivitaDidatticaDiOrientamentoDTO.getCodiceAttivitaDidattica());
        //se inserendo un vincolato, me lo fa inserire, non va bene
        for(InsegnamentoRegola insegnamentoRegola: insegnamentoRegolas)
            if(codici.add(insegnamentoRegola.getCodiceInsegnamento()))
                return false;
        return true;
    }

    private boolean checkInsegnamentiLiberiEVincolati(List<AttivitaDidattica> insegnamentiOrientamento, List<InsegnamentoRegola> insegnamentiVincolati, List<InsegnamentoRegola>insegnamentiLiberi) {
        List<String>codici = new ArrayList<>();
        for(InsegnamentoRegola insegnamentoRegola: insegnamentiVincolati)
            codici.add(insegnamentoRegola.getCodiceInsegnamento());

        //controllo che ci siano tutti i vincolati
        int count =0;
        for(AttivitaDidattica attivitaDidatticaDiOrientamentoDTO : insegnamentiOrientamento)
            if (codici.contains(attivitaDidatticaDiOrientamentoDTO.getCodiceAttivitaDidattica()))
                count++;

        if(count != codici.size())
            return false;
        //ora controllo quelli liberi aggiungendo anche loro al set
        for(InsegnamentoRegola insegnamentoRegola: insegnamentiLiberi)
            codici.add(insegnamentoRegola.getCodiceInsegnamento());

        //devono stare tutti in questo insieme di elementi
        for(AttivitaDidattica attivitaDidatticaDiOrientamentoDTO : insegnamentiOrientamento)
            if(!codici.contains(attivitaDidatticaDiOrientamentoDTO.getCodiceAttivitaDidattica()))
                return false;
        return true;
    }

    @Override
    public Optional<PPS> getPPS(Email emailRichiedente, Email emailPPS) throws RichiestaNonValidaException {
        User userRichiedente = this.readUserPort.findUserById(emailRichiedente).get();
        Optional<User> user = this.readUserPort.findUserById(emailPPS);
        if(!user.isPresent())
            throw new RichiestaNonValidaException(ERR_MESSAGES.PPS_NOT_FOUND);

        if(userRichiedente.getRole().equals(Role.STUDENTE)){
            if (!user.get().getEmail().equals(userRichiedente.getEmail()))
                throw new RichiestaNonValidaException(ERR_MESSAGES.PPS_FORBIDDEN);
        }
        //se sono uno studente che richiede il proprio modulo pps oppure un docente
        //posso accedere al modulo se presente
        Optional<PPS> pps = this.readPPSPort.findPPSById(userMapper.fromUserToStudente(user.get()));
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
        if(user.isPresent()) this.updatePPSPort.accettaPPS(userMapper.fromUserToStudente(user.get()));
        else throw new UserNotFound(ERR_MESSAGES.PPS_NOT_FOUND);
    }

    @Override
    public void rififutaPPS(Email email) throws PPSNotFoundException, UserNotFound, PPSNonValidoException {
        Optional<User> user = this.readUserPort.findUserById(email);
        if(user.isPresent()) this.updatePPSPort.rifiutaPPS(userMapper.fromUserToStudente(user.get()));
        else throw new UserNotFound(ERR_MESSAGES.PPS_NOT_FOUND);
    }
}
