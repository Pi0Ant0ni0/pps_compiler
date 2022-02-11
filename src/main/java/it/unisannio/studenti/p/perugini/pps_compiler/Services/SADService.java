package it.unisannio.studenti.p.perugini.pps_compiler.Services;

import it.unisannio.studenti.p.perugini.pps_compiler.API.*;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.TipoCorsoDiLaurea;
import it.unisannio.studenti.p.perugini.pps_compiler.Esse3API.*;
import it.unisannio.studenti.p.perugini.pps_compiler.Utils.CorsoDiStudiUtil;
import it.unisannio.studenti.p.perugini.pps_compiler.Utils.AttivitaDidatticheUtil;
import it.unisannio.studenti.p.perugini.pps_compiler.Utils.SHARED;
import it.unisannio.studenti.p.perugini.pps_compiler.core.attivitaDidattica.port.CreateAttivitaDidattichaPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.attivitaDidattica.port.DeleteAttivitaDidatticaPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.attivitaDidattica.port.ReadAttivitaDidatticaPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.corsoDiStudio.port.CreateCorsoDiStudioPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.corsoDiStudio.port.DeleteCorsoDiStudioPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.corsoDiStudio.port.ReadCorsoDiStudioPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.manifestiDegliStudi.port.ReadManifestoDegliStudiPort;
import org.glassfish.jersey.client.ClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class SADService {


    @Autowired
    private ReadAttivitaDidatticaPort readAttivitaDidatticaPort;
    @Autowired
    private CreateAttivitaDidattichaPort createAttivitaDidattichaPort;
    @Autowired
    private CreateCorsoDiStudioPort createCorsoDiStudioPort;
    @Autowired
    private ReadCorsoDiStudioPort readCorsoDiStudioPort;
    @Autowired
    private ReadManifestoDegliStudiPort readManifestoDegliStudiPort;
    @Autowired
    private DeleteCorsoDiStudioPort deleteCorsoDiStudioPort;
    @Autowired
    private DeleteAttivitaDidatticaPort deleteAttivitaDidatticaPort;


    private Logger logger = LoggerFactory.getLogger(SADService.class);
    private final String endPointOfferte = "https://unisannio.esse3.cineca.it/e3rest/api/offerta-service-v1/";
    private final String endPointRegole = "https://unisannio.esse3.cineca.it/e3rest/api/regsce-service-v1/";
    private final String endPointFacolta = "https://unisannio.esse3.cineca.it/e3rest/api/struttura-service-v1/strutture/";
    private final String endPointLogistica = "https://unisannio.esse3.cineca.it/e3rest/api/logistica-service-v1/";


    @Async
    public CompletableFuture<Void> updateDatabse() throws InterruptedException {
        SHARED.updatingDatabase = true;
        int coorte;
        int settembre = 9;
        if (LocalDate.now().getMonth().getValue() < settembre)
            coorte = LocalDate.now().getYear() - 1;
        else coorte = LocalDate.now().getYear();

        logger.info("inizio l'aggiornamento del database per la coorte: " + coorte);
        logger.info("Il mese di aggiornamento e': " + LocalDate.now().getMonth().name() + "-" + LocalDate.now().getMonth().getValue());
        //devo droppare il database e ricostruirlo
        this.deleteAttivitaDidatticaPort.deleteAll();
        this.deleteCorsoDiStudioPort.deleteAll();
        this.updateAttivitaDidattiche(coorte, false);
        this.updateAttivitaDidattiche(coorte + 2, true);
        SHARED.updatingDatabase = false;
        return CompletableFuture.completedFuture(null);

    }

    private void updateAttivitaDidattiche(int year, boolean programmata) throws InterruptedException {

        //recupero i corsi di studio dell'anno corrente
        List<Offerta> offerte = this.getOfferte(year);
        Thread.sleep(1 * 1000);

        //prendo in considerazione solo i corsi di studio non presenti nel database se richiedo quelli programmati
        if (programmata) {
            offerte = offerte
                    .stream()
                    .filter(offerta -> !readCorsoDiStudioPort.findCorsoDiStudioById(offerta.getCdsCod()).isPresent())
                    .collect(Collectors.toList());
        }
        this.updateCorsoDiStudi(offerte, year, programmata);
        List<ADContestualizzata> adContestualizzatiTriennale = new ArrayList<>();
        List<ADContestualizzata> adContestualizzatiMagistrale = new ArrayList<>();
        List<ADContestualizzata> adContestualizzatiCicloUnico = new ArrayList<>();
        //recupero le ad contestualizzate
        this.getADContestualizzate(year, offerte, adContestualizzatiTriennale, adContestualizzatiMagistrale, adContestualizzatiCicloUnico);

        logger.info("ADContestualizzate triennali: " + adContestualizzatiTriennale.size());
        logger.info("ADContestualizzate magistrale: " + adContestualizzatiMagistrale.size());
        logger.info("ADContestualizzate cicloUnico: " + adContestualizzatiCicloUnico.size());

        //se sto cercando insegnamenti programmati scarto quelli gia presenti nel database
        if (programmata) {
            adContestualizzatiTriennale = adContestualizzatiTriennale
                    .stream()
                    .filter(
                            adContestualizzata -> !readAttivitaDidatticaPort.findAttivitaById(adContestualizzata.getChiaveAdContestualizzata().getAdCod()).isPresent()
                    ).collect(Collectors.toList());

            adContestualizzatiMagistrale = adContestualizzatiMagistrale
                    .stream()
                    .filter(
                            adContestualizzata -> !readAttivitaDidatticaPort.findAttivitaById(adContestualizzata.getChiaveAdContestualizzata().getAdCod()).isPresent()
                    ).collect(Collectors.toList());

            adContestualizzatiCicloUnico = adContestualizzatiCicloUnico
                    .stream()
                    .filter(
                            adContestualizzata -> !readAttivitaDidatticaPort.findAttivitaById(adContestualizzata.getChiaveAdContestualizzata().getAdCod()).isPresent()
                    ).collect(Collectors.toList());
        }


        //recupero i segmenti
        //passo lo stesso client cosi da non crearne uno ad ogni richiesta
        Client client = ClientBuilder.newClient();
        client.property(ClientProperties.CONNECT_TIMEOUT, 60000);
        client.property(ClientProperties.READ_TIMEOUT, 60000);
        WebTarget webTarget = client.target(endPointOfferte);
        for (ADContestualizzata adContestualizzata : adContestualizzatiTriennale)
            this.getSegmentiContestualizzati(programmata, year, adContestualizzata, webTarget, TipoCorsoDiLaurea.L2);
        Thread.sleep(1 * 1000);

        for (ADContestualizzata adContestualizzata : adContestualizzatiMagistrale)
            this.getSegmentiContestualizzati(programmata, year, adContestualizzata, webTarget, TipoCorsoDiLaurea.LM);
        Thread.sleep(1 * 1000);

        for (ADContestualizzata adContestualizzata : adContestualizzatiCicloUnico)
            this.getSegmentiContestualizzati(programmata, year, adContestualizzata, webTarget, TipoCorsoDiLaurea.LM5);
        Thread.sleep(1 * 1000);

        client.close();
    }

    private void updateCorsoDiStudi(List<Offerta> offerte, int year, boolean programmato) throws InterruptedException {
        //devo droppare il database e ricostruirlo
        logger.info("Iniziato l'aggiornamento dei corsi di studio");
        logger.info("anno di riferimento: " + year);

        //recupero gli insegnamenti dell'anno corrente
        Client client = ClientBuilder.newClient();
        client.property(ClientProperties.CONNECT_TIMEOUT, 20000);
        client.property(ClientProperties.READ_TIMEOUT, 20000);
        WebTarget webTarget = client.target(endPointRegole);
        logger.info("Recupero regole iniziato");
        List<RegolamentoDiScelta> regolamentiDiScelta = new ArrayList<>();
        for (Offerta offerta : offerte) {
            Optional<RegolamentoDiScelta> optional = this.getRegolamentiDiScelta(offerta, webTarget);
            if (optional.isPresent())
                regolamentiDiScelta.add(optional.get());
            Thread.sleep(2 * 1000);
        }
        logger.info("Recupero regole terminato, trovate: " + regolamentiDiScelta.size() + " regole");

        logger.info("Inizio recupero facoltà");
        webTarget = client.target(endPointFacolta);
        for (RegolamentoDiScelta regolamentoDiScelta : regolamentiDiScelta) {
            Facolta facolta = this.getFacolta(regolamentoDiScelta, webTarget);
            this.createCorsoDiStudioPort.save(CorsoDiStudiUtil.makeCorsoDiStudio(regolamentoDiScelta, facolta, programmato));
            Thread.sleep(2 * 1000);
        }
        client.close();
    }



    public List<Orientamento> getOrientamentoManifesto(int coorte, String codiceCorsoDiStudio) {
        ChiaveManifestoDegliStudi chiaveManifestoDegliStudi = new ChiaveManifestoDegliStudi();
        //se cerco gli orientamenti non devo specificare il curriculum, sono mutuamente esclusivi
        chiaveManifestoDegliStudi.setCodiceCorsoDiStudio(codiceCorsoDiStudio);
        chiaveManifestoDegliStudi.setCurricula(null);
        chiaveManifestoDegliStudi.setCoorte(coorte);
        Optional<ManifestoDegliStudi> optionalManifestoDegliStudi = this.readManifestoDegliStudiPort.findManifestoById(chiaveManifestoDegliStudi);
        if (optionalManifestoDegliStudi.isPresent()) {
            ManifestoDegliStudi manifestoDegliStudi = optionalManifestoDegliStudi.get();
            Map<Integer, AnnoAccademico> schemiDiPiano = manifestoDegliStudi.getAnniAccademici();
            List<Orientamento> orientamenti = new ArrayList<>();
            for (Integer anno : schemiDiPiano.keySet()) {
                if (schemiDiPiano.get(anno).getOrientamenti().isPresent())
                    orientamenti.addAll(schemiDiPiano.get(anno).getOrientamenti().get());
            }
            return orientamenti;
        }
        return new ArrayList<>();
    }


    /**
     * METODI DI UTILITA'
     */


    private Facolta getFacolta(RegolamentoDiScelta regolamentoDiScelta, WebTarget webTarget) throws InterruptedException {
        try {
            Invocation.Builder invocationBuilder = webTarget
                    .path(String.valueOf(regolamentoDiScelta.getFacId()))
                    .request(MediaType.APPLICATION_JSON);
            return invocationBuilder.get(Facolta.class);
        }catch (ProcessingException e){
            logger.error("Processing exception durante il recupero delle facolta");
            Thread.sleep(10*1000);
            return this.getFacolta(regolamentoDiScelta,webTarget);
        }
    }

    private Optional<RegolamentoDiScelta> getRegolamentiDiScelta(Offerta offerta, WebTarget webTarget) {
        try {
            logger.info("Ricerca regolamento di scelta per il corso di laurea con id: " + offerta.getCdsOffId());
            Invocation.Builder invocationBuilder = webTarget
                    .path("/regsce")
                    .queryParam("cdsId", offerta.getCdsOffId())
                    .queryParam("order", "-coorte")
                    .request(MediaType.APPLICATION_JSON);
            List<RegolamentoDiScelta> buffer = Arrays.asList(invocationBuilder.get(RegolamentoDiScelta[].class));
            if (!buffer.isEmpty())
                return Optional.of(buffer.get(0));
            else return Optional.empty();
        } catch (ProcessingException e) {
            return this.getRegolamentiDiScelta(offerta, webTarget);
        }
    }

    private void addInsegnamenti(SEGContestualizzato[] segContestualizzati, ADContestualizzata adContestualizzata, boolean programmato, TipoCorsoDiLaurea tipoCorsoDiLaurea) throws InterruptedException {

        try {
            Client client = ClientBuilder.newClient();
            client.property(ClientProperties.CONNECT_TIMEOUT, 60000);
            client.property(ClientProperties.READ_TIMEOUT, 60000);
            WebTarget logisticaWebTarget = client.target(endPointLogistica);

            //inizio la ricerca
            List<AttivitaDidattica> unitaDidattiche = new ArrayList<>();
            for (SEGContestualizzato segContestualizzato : segContestualizzati) {
                String contenuti = "";
                String metodiDidattici = "";
                String modalitaVerificaApprendimento = "";
                String obiettivi = "";
                String prerequisiti = "";

                AttivitaDidattica unitaDidattica = this.getInfoAggiuntiveUnitaDidattica(
                        adContestualizzata,
                        segContestualizzato,
                        contenuti,
                        metodiDidattici,
                        modalitaVerificaApprendimento,
                        obiettivi,
                        prerequisiti,
                        tipoCorsoDiLaurea,
                        programmato,
                        logisticaWebTarget
                );
                unitaDidattiche.add(unitaDidattica);
            }

            String contenuti = "";
            String metodiDidattici = "";
            String modalitaVerificaApprendimento = "";
            String obiettivi = "";
            String prerequisiti = "";

            AttivitaDidattica attivitaDidattica = this.getInfoAggiuntiveAttivitaDidattica(
                    adContestualizzata,
                    unitaDidattiche,
                    contenuti,
                    metodiDidattici,
                    modalitaVerificaApprendimento,
                    obiettivi,
                    prerequisiti,
                    tipoCorsoDiLaurea,
                    programmato,
                    logisticaWebTarget
            );
            createAttivitaDidattichaPort.save(attivitaDidattica);
            client.close();
        } catch (ProcessingException e) {
            logger.error("Processing Exception durante il recupero dell info aggiuntiva per le attivita didattiche");
            Thread.sleep(10 * 1000);
            this.addInsegnamenti(segContestualizzati, adContestualizzata, programmato, tipoCorsoDiLaurea);
        }
    }

    private AttivitaDidattica getInfoAggiuntiveUnitaDidattica(ADContestualizzata adContestualizzata,
                                                              SEGContestualizzato segContestualizzato,
                                                              String contenuti,
                                                              String metodiDidattici,
                                                              String modalitaVerificaApprendimento,
                                                              String obiettivi, String prerequisiti,
                                                              TipoCorsoDiLaurea tipoCorsoDiLaurea,
                                                              boolean programmata,
                                                              WebTarget logisticaWebTarget) throws InterruptedException {
        //recupero id Logistica
        logger.info("recupero informazioni aggiuntive per insegnamento: " + segContestualizzato.getChiaveSegContestualizzato().getChiaveUdContestualizzata().getChiaveAdContestualizzata().getAdDes());
        Invocation.Builder invocationBuilderLogistica = logisticaWebTarget
                .path("logistica")
                .queryParam("adId", segContestualizzato.getChiaveSegContestualizzato().getChiaveUdContestualizzata().getChiaveAdContestualizzata().getAdId())
                .request(MediaType.APPLICATION_JSON);
        Thread.sleep(1 * 1000);
        AdLog[] buffer = invocationBuilderLogistica.get(AdLog[].class);
        Thread.sleep(1 * 1000);


        //se ho trovato dei dati di logistica, recupero le informazioni
        if (buffer.length > 0) {
            int adLogId = buffer[0].getChiavePartizione().getAdLogId();
            //recupero descrizione
            Invocation.Builder invocationBuilderSyllabus = logisticaWebTarget
                    .path("logistica/" + adLogId + "/adLogConSyllabus")
                    .queryParam("order", "-aaOffId")
                    .request(MediaType.APPLICATION_JSON);
            Thread.sleep(1 * 1000);
            AdLogConSyllabus[] syllabusBuffer = invocationBuilderSyllabus.get(AdLogConSyllabus[].class);
            //se ho trovato le informazioni le aggiungo
            if (syllabusBuffer.length > 0 && syllabusBuffer[0].getSyllabusAD().length > 0) {
                Thread.sleep(1 * 1000);
                contenuti = syllabusBuffer[0].getSyllabusAD()[0].getContenuti();
                metodiDidattici = syllabusBuffer[0].getSyllabusAD()[0].getContenuti();
                modalitaVerificaApprendimento = syllabusBuffer[0].getSyllabusAD()[0].getModalitaVerificaApprendimento();
                obiettivi = syllabusBuffer[0].getSyllabusAD()[0].getObiettiviFormativi();
                prerequisiti = syllabusBuffer[0].getSyllabusAD()[0].getPrerequisiti();
            }
        }
        return AttivitaDidatticheUtil.makeUnitaDidattica(
                segContestualizzato,
                adContestualizzata,
                tipoCorsoDiLaurea,
                programmata,
                contenuti,
                metodiDidattici,
                modalitaVerificaApprendimento,
                obiettivi,
                prerequisiti
        );
    }

    private List<Offerta> getOfferte(int year) {
        Client client = ClientBuilder.newClient();
        client.property(ClientProperties.CONNECT_TIMEOUT, 60000);
        client.property(ClientProperties.READ_TIMEOUT, 60000);
        WebTarget webTarget = client.target(endPointOfferte);
        logger.info("Recupero offerte iniziato");
        int start = 0;
        List<Offerta> offertaResponse;
        List<Offerta> offerte = new ArrayList<>();
        do {
            Invocation.Builder invocationBuilder = webTarget
                    .path("/offerte")
                    .queryParam("aaOffId", year)
                    .queryParam("start", start)
                    .queryParam("limit", 100)
                    .request(MediaType.APPLICATION_JSON);
            offertaResponse = Arrays.asList(invocationBuilder.get(Offerta[].class));
            offerte.addAll(
                    offertaResponse
                            .stream()
                            .filter(
                                    offerta -> offerta.getOffertaExistsFlg() == 1
                                            && (offerta.getTipiCorsoCod().equals(TipoCorsoDiLaurea.L2.toString())
                                            || offerta.getTipiCorsoCod().equals(TipoCorsoDiLaurea.LM.toString())
                                            || offerta.getTipiCorsoCod().equals(TipoCorsoDiLaurea.LM5.toString()))
                            )
                            .collect(Collectors.toList())
            );
            //incremento start di 100 in 100
            start += 100;
        } while (offertaResponse.size() != 0);
        logger.info("Recupero offerte concluso, ho trovato: " + offerte.size() + " offerte");
        client.close();
        return offerte;
    }

    private void getADContestualizzate(int year, List<Offerta> offerte, List<ADContestualizzata> adContestualizzatiTriennale, List<ADContestualizzata> adContestualizzatiMagistrale, List<ADContestualizzata> adContestualizzatiCicloUnico) throws InterruptedException {
        int start;
        Client client = ClientBuilder.newClient();
        client.property(ClientProperties.CONNECT_TIMEOUT, 60000);
        client.property(ClientProperties.READ_TIMEOUT, 60000);
        WebTarget webTarget = client.target(endPointOfferte);
        List<ADContestualizzata> adResponse;
        logger.info("Recupero AD Contestualizzati iniziato");
        for (Offerta offerta : offerte) {
            start = 0;
            do {

                Invocation.Builder invocationBuilder = webTarget
                        .path("/offerte/" + year + "/" + offerta.getCdsOffId() + "/attivita")
                        .queryParam("start", start)
                        .queryParam("limit", 100)
                        .request(MediaType.APPLICATION_JSON);
                adResponse = Arrays.asList(invocationBuilder.get(ADContestualizzata[].class));
                switch (TipoCorsoDiLaurea.valueOf(offerta.getTipiCorsoCod())) {
                    case L2:
                        adContestualizzatiTriennale.addAll(adResponse);
                        break;
                    case LM:
                        adContestualizzatiMagistrale.addAll(adResponse);
                        break;
                    case LM5:
                        adContestualizzatiCicloUnico.addAll(adResponse);
                }
                //incremento start di 100 in 100
                start += 100;
                Thread.sleep(1 * 1000);

            } while (adResponse.size() != 0);
        }
        logger.info("Recupero AD Contestualizzati concluso");
        client.close();
    }

    private AttivitaDidattica getInfoAggiuntiveAttivitaDidattica(ADContestualizzata adContestualizzata,
                                                                 List<AttivitaDidattica> unitaDidattiche,
                                                                 String contenuti,
                                                                 String metodiDidattici,
                                                                 String modalitaVerificaApprendimento,
                                                                 String obiettivi, String prerequisiti,
                                                                 TipoCorsoDiLaurea tipoCorsoDiLaurea,
                                                                 boolean programmata,
                                                                 WebTarget logisticaWebTarget) throws InterruptedException {
        //recupero id Logistica
        logger.info("recupero informazioni aggiuntive per insegnamento: " + adContestualizzata.getChiaveAdContestualizzata().getAdDes());
        Invocation.Builder invocationBuilderLogistica = logisticaWebTarget
                .path("logistica")
                .queryParam("adId", adContestualizzata.getChiaveAdContestualizzata().getAdId())
                .request(MediaType.APPLICATION_JSON);
        Thread.sleep(1 * 1000);
        AdLog[] buffer = invocationBuilderLogistica.get(AdLog[].class);
        Thread.sleep(1 * 1000);

        //se ho trovato dei dati di logistica, recupero le informazioni
        if (buffer.length > 0) {
            int adLogId = buffer[0].getChiavePartizione().getAdLogId();
            //recupero descrizione
            Invocation.Builder invocationBuilderSyllabus = logisticaWebTarget
                    .path("logistica/" + adLogId + "/adLogConSyllabus")
                    .queryParam("order", "-aaOffId")
                    .request(MediaType.APPLICATION_JSON);
            Thread.sleep(1 * 1000);
            AdLogConSyllabus[] syllabusBuffer = invocationBuilderSyllabus.get(AdLogConSyllabus[].class);
            //se ho trovato le informazioni le aggiungo
            if (syllabusBuffer.length > 0 && syllabusBuffer[0].getSyllabusAD().length > 0) {
                Thread.sleep(1 * 1000);
                contenuti = syllabusBuffer[0].getSyllabusAD()[0].getContenuti();
                metodiDidattici = syllabusBuffer[0].getSyllabusAD()[0].getContenuti();
                modalitaVerificaApprendimento = syllabusBuffer[0].getSyllabusAD()[0].getModalitaVerificaApprendimento();
                obiettivi = syllabusBuffer[0].getSyllabusAD()[0].getObiettiviFormativi();
                prerequisiti = syllabusBuffer[0].getSyllabusAD()[0].getPrerequisiti();
            }
        }
        return AttivitaDidatticheUtil.makeAttivitaDidattica(
                unitaDidattiche,
                adContestualizzata,
                tipoCorsoDiLaurea,
                programmata,
                contenuti,
                metodiDidattici,
                modalitaVerificaApprendimento,
                obiettivi,
                prerequisiti
        );
    }

    private void getSegmentiContestualizzati(boolean programmata, int year, ADContestualizzata adContestualizzata, WebTarget webTarget, TipoCorsoDiLaurea tipoCorsoDiLaurea) throws InterruptedException {
        try {
            logger.info("Recupero SegmentiContetsualizzati per attivita: " + adContestualizzata.getChiaveAdContestualizzata().getAdDes());
            Invocation.Builder invocationBuilder = webTarget
                    .path("/offerte/" + year + "/" + adContestualizzata.getChiaveAdContestualizzata().getCdsId() + "/segmenti")
                    .queryParam("adId", adContestualizzata.getChiaveAdContestualizzata().getAdId())
                    .queryParam("aaOrdId", adContestualizzata.getChiaveAdContestualizzata().getAaOrdId())
                    .queryParam("pdsId", adContestualizzata.getChiaveAdContestualizzata().getPdsId())
                    .request(MediaType.APPLICATION_JSON);
            Thread.sleep(1 * 1000);
            //ho tutte le unita didattiche
            SEGContestualizzato[] segContestualizzati = invocationBuilder.get(SEGContestualizzato[].class);
            this.addInsegnamenti(
                    segContestualizzati,
                    adContestualizzata,
                    programmata,
                    tipoCorsoDiLaurea
            );
            logger.info("Segmento recuperato");
        } catch (ProcessingException e) {
            Thread.sleep(10 * 1000);
            logger.error("READ TIMEOUT ripeto la richiesta per i segmenti");
            this.getSegmentiContestualizzati(programmata, year, adContestualizzata, webTarget, tipoCorsoDiLaurea);
        }
    }

}
