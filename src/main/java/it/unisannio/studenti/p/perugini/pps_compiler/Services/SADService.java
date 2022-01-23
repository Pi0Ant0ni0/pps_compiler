package it.unisannio.studenti.p.perugini.pps_compiler.Services;

import it.unisannio.studenti.p.perugini.pps_compiler.API.*;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.TipoCorsoDiLaurea;
import it.unisannio.studenti.p.perugini.pps_compiler.Esse3API.*;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.*;
import it.unisannio.studenti.p.perugini.pps_compiler.Repositories.*;
import it.unisannio.studenti.p.perugini.pps_compiler.Utils.CorsoDiStudiUtil;
import it.unisannio.studenti.p.perugini.pps_compiler.Utils.InsegnamentoUtil;
import it.unisannio.studenti.p.perugini.pps_compiler.core.manifestiDegliStudi.port.ReadManifestoDegliStudiPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SADService {

    @Autowired
    private AttivitaDidatticheRepository attivitaDidatticheRepository;
    @Autowired
    private OrdinamentoRepository ordinamentoRepository;
    @Autowired
    private ReadManifestoDegliStudiPort readManifestoDegliStudiPort;
    @Autowired
    private CorsiDiStudioRepository corsiDiStudioRepository;
    @Autowired
    private InsegnamentoService insegnamentoService;


    private Logger logger = LoggerFactory.getLogger(SADService.class);
    private final String endPointOfferte="https://unisannio.esse3.cineca.it/e3rest/api/offerta-service-v1/";
    private final String endPointRegole="https://unisannio.esse3.cineca.it/e3rest/api/regsce-service-v1/";
    private final String endPointFacolta = "https://unisannio.esse3.cineca.it/e3rest/api/struttura-service-v1/strutture/";
    private final String endPointLogistica= "https://unisannio.esse3.cineca.it/e3rest/api/logistica-service-v1/";


    public void updateDatabse() throws InterruptedException {
        int coorte;
        int settembre =9;
        if(LocalDate.now().getMonth().getValue()<settembre)
            coorte=LocalDate.now().getYear()-1;
        else coorte = LocalDate.now().getYear();
        logger.info("inizio l'aggiornamento del database per la coorte: "+coorte);
        logger.info("Il mese di aggiornamento e': "+LocalDate.now().getMonth().name()+"-"+LocalDate.now().getMonth().getValue());
        this.updateAttivitaDidattiche(coorte);
        this.updateAttivitaDidatticheProgrammate(coorte+2);

    }

    private void updateAttivitaDidattiche(int year) throws InterruptedException {
        //devo droppare il database e ricostruirlo
        this.dropAttivitaDidattiche();


        //recupero i corsi di studio dell'anno corrente
        Client client = ClientBuilder.newClient();
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
                                    &&(offerta.getTipiCorsoCod().equals(TipoCorsoDiLaurea.L2.toString())
                                    || offerta.getTipiCorsoCod().equals(TipoCorsoDiLaurea.LM.toString())
                                    || offerta.getTipiCorsoCod().equals(TipoCorsoDiLaurea.LM5.toString()))
                            )
                            .collect(Collectors.toList())
            );
            //incremento start di 100 in 100
            start += 100;
        } while (offertaResponse.size() != 0);
        logger.info("Recupero offerte concluso, ho trovato: "+offerte.size()+" offerte");

        //aggiungo i corsi di studio trovati al database
        this.updateCorsoDiStudi(offerte,year);


        //recupero i corsi
        logger.info("Recupero AD Contestualizzati iniziato");
        List<ADContestualizzata> adResponse;
        List<ADContestualizzata> adContestualizzatiTriennale = new ArrayList<>();
        List<ADContestualizzata> adContestualizzatiMagistrale = new ArrayList<>();
        List<ADContestualizzata> adContestualizzatiCicloUnico = new ArrayList<>();
        for (Offerta offerta : offerte) {
            start = 0;
            do {
                Invocation.Builder invocationBuilder = webTarget
                        .path("/offerte/" + year + "/" + offerta.getCdsOffId() + "/attivita")
                        .queryParam("start", start)
                        .queryParam("limit", 100)
                        .request(MediaType.APPLICATION_JSON);
                adResponse = Arrays.asList(invocationBuilder.get(ADContestualizzata[].class));
                switch (TipoCorsoDiLaurea.valueOf(offerta.getTipiCorsoCod())){
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
            } while (adResponse.size() != 0);
        }
        logger.info("Recupero AD Contestualizzati concluso");



        logger.info("Recupero SegmentiContetsualizzati Triennali iniziato");
        //ora per ogni insegnamento devo prendere tutti i dati quindi mi servono i segmenti
        for (ADContestualizzata adContestualizzata : adContestualizzatiTriennale) {
            Invocation.Builder invocationBuilder2 = webTarget
                    .path("/offerte/" + year + "/" + adContestualizzata.getChiaveAdContestualizzata().getCdsId() + "/segmenti")
                    .queryParam("adId", adContestualizzata.getChiaveAdContestualizzata().getAdId())
                    .request(MediaType.APPLICATION_JSON);
            SEGContestualizzato[] segContestualizzati = invocationBuilder2.get(SEGContestualizzato[].class);
            this.addInsegnamenti(
                    segContestualizzati,
                    adContestualizzata,
                    false,
                    TipoCorsoDiLaurea.L2
            );
            //molti insegnamenti sono
            Thread.sleep(1 * 500);
        }
        logger.info("Recupero SegmentiContetsualizzati Triennali concluso");

        logger.info("Recupero SegmentiContetsualizzati Magistrali iniziato");
        //ora per ogni insegnamento devo prendere tutti i dati quindi mi servono i segmenti
        for (ADContestualizzata adContestualizzata : adContestualizzatiMagistrale) {
            Invocation.Builder invocationBuilder2 = webTarget
                    .path("/offerte/" + year + "/" + adContestualizzata.getChiaveAdContestualizzata().getCdsId() + "/segmenti")
                    .queryParam("adId", adContestualizzata.getChiaveAdContestualizzata().getAdId())
                    .request(MediaType.APPLICATION_JSON);
            SEGContestualizzato[] segContestualizzati = invocationBuilder2.get(SEGContestualizzato[].class);
            this.addInsegnamenti(
                    segContestualizzati,
                    adContestualizzata,
                    false,
                    TipoCorsoDiLaurea.LM
            );
            Thread.sleep(1 * 500);

        }
        logger.info("Recupero SegmentiContetsualizzati Magistrale concluso");

        logger.info("Recupero SegmentiContetsualizzati CicloUnico iniziato");
        //ora per ogni insegnamento devo prendere tutti i dati quindi mi servono i segmenti
        for (ADContestualizzata adContestualizzata : adContestualizzatiCicloUnico) {
            Invocation.Builder invocationBuilder2 = webTarget
                    .path("/offerte/" + year + "/" + adContestualizzata.getChiaveAdContestualizzata().getCdsId() + "/segmenti")
                    .queryParam("adId", adContestualizzata.getChiaveAdContestualizzata().getAdId())
                    .request(MediaType.APPLICATION_JSON);
            SEGContestualizzato[] segContestualizzati = invocationBuilder2.get(SEGContestualizzato[].class);
            this.addInsegnamenti(
                    segContestualizzati,
                    adContestualizzata,
                    false,
                    TipoCorsoDiLaurea.LM5
            );
            Thread.sleep(1 * 500);
        }
        logger.info("Recupero SegmentiContetsualizzati CicloUnico concluso");
    }

    private void addInsegnamenti(SEGContestualizzato[] segContestualizzati, ADContestualizzata adContestualizzata, boolean programmato, TipoCorsoDiLaurea tipoCorsoDiLaurea) throws InterruptedException {
        Client client = ClientBuilder.newClient();
        WebTarget logisticaWebTarget = client.target(endPointLogistica);

        //inizializzo i dati da trovare
        //non tutti li presentano
        String contenuti = "";
        String metodiDidattici = "";
        String modalitaVerificaApprendimento = "";
        String obiettivi = "";
        String prerequisiti = "";
        //inizio la ricerca
        for(SEGContestualizzato segContestualizzato : segContestualizzati){
            //recupero id Logistica
            logger.info("recupero informazioni aggiuntive per insegnamento: "+segContestualizzato.getChiaveSegContestualizzato().getChiaveUdContestualizzata().getChiaveAdContestualizzata().getAdDes());
            Invocation.Builder invocationBuilderLogistica = logisticaWebTarget
                    .path("logistica")
                    .queryParam("adId",segContestualizzato.getChiaveSegContestualizzato().getChiaveUdContestualizzata().getChiaveAdContestualizzata().getAdId())
                    .request(MediaType.APPLICATION_JSON);
            AdLog[] buffer =invocationBuilderLogistica.get(AdLog[].class);
            Thread.sleep(1*1000);

            //se ho trovato dei dati di logistica, recupero le informazioni
            if(buffer.length>0) {
                int adLogId = buffer[0].getChiavePartizione().getAdLogId();
                //recupero descrizione
                Invocation.Builder invocationBuilderSyllabus = logisticaWebTarget
                        .path("logistica/" + adLogId + "/adLogConSyllabus")
                        .queryParam("order", "-aaOffId")
                        .request(MediaType.APPLICATION_JSON);
                AdLogConSyllabus[] syllabusBuffer = invocationBuilderSyllabus.get(AdLogConSyllabus[].class);
                //se ho trovato le informazioni le aggiungo
                if (syllabusBuffer.length > 0 && syllabusBuffer[0].getSyllabusAD().length>0) {
                    Thread.sleep(1*1000);
                    contenuti = syllabusBuffer[0].getSyllabusAD()[0].getContenuti();
                    metodiDidattici = syllabusBuffer[0].getSyllabusAD()[0].getContenuti();
                    modalitaVerificaApprendimento = syllabusBuffer[0].getSyllabusAD()[0].getModalitaVerificaApprendimento();
                    obiettivi = syllabusBuffer[0].getSyllabusAD()[0].getObiettiviFormativi();
                    prerequisiti = syllabusBuffer[0].getSyllabusAD()[0].getPrerequisiti();
                }
            }

            //creo l'insegnamento
            AttivitaDidattica attivitaDidattica = InsegnamentoUtil.makeInsegnamento(
                    segContestualizzato,
                    adContestualizzata,
                    tipoCorsoDiLaurea,
                    programmato,
                    contenuti,
                    metodiDidattici,
                    modalitaVerificaApprendimento,
                    obiettivi,
                    prerequisiti
            );
            //aggiungo l'insegnamento al repository
            attivitaDidatticheRepository.save(attivitaDidattica);
        }
    }

    private void updateAttivitaDidatticheProgrammate(int year) throws InterruptedException {
        //recupero gli insegnamenti dell'anno corrente
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(endPointOfferte);
        logger.info("Recupero offerte iniziato per insegnamenti programmati");
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
                                            &&(offerta.getTipiCorsoCod().equals(TipoCorsoDiLaurea.L2.toString())
                                            || offerta.getTipiCorsoCod().equals(TipoCorsoDiLaurea.LM.toString())
                                            || offerta.getTipiCorsoCod().equals(TipoCorsoDiLaurea.LM5.toString()))
                            )
                            .collect(Collectors.toList())
            );
            //incremento start di 100 in 100
            start += 100;
        } while (offertaResponse.size() != 0);
        logger.info("Recupero offerte concluso, ho trovato: "+offerte.size()+" offerte");
        this.updateCorsoDiStudiProgrammati(offerte,year);


        logger.info("Recupero AD Contestualizzati per insegnamenti programmati iniziato ");
        List<ADContestualizzata> adResponse;
        List<ADContestualizzata> adContestualizzatiTriennale = new ArrayList<>();
        List<ADContestualizzata> adContestualizzatiMagistrale = new ArrayList<>();
        List<ADContestualizzata> adContestualizzatiCicloUnico = new ArrayList<>();
        for (Offerta offerta : offerte) {
            start = 0;
            do {
                Invocation.Builder invocationBuilder = webTarget
                        .path("/offerte/" + year + "/" + offerta.getCdsOffId() + "/attivita")
                        .queryParam("start", start)
                        .queryParam("limit", 100)
                        .request(MediaType.APPLICATION_JSON);
                adResponse = Arrays.asList(invocationBuilder.get(ADContestualizzata[].class));
                switch (TipoCorsoDiLaurea.valueOf(offerta.getTipiCorsoCod())){
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
            } while (adResponse.size() != 0);
        }
        logger.info("Recupero AD Contestualizzati concluso");
        //prendo in considerazione solo gli insegnamenti che non ho nel database
        adContestualizzatiTriennale= adContestualizzatiTriennale
                .stream()
                .filter(adContestualizzata ->
                        !(this.attivitaDidatticheRepository.findById(adContestualizzata
                                .getChiaveAdContestualizzata()
                                .getAdCod())
                                .isPresent())
                ).collect(Collectors.toList());
        adContestualizzatiMagistrale= adContestualizzatiMagistrale
                .stream()
                .filter(adContestualizzata ->
                        !(this.attivitaDidatticheRepository.findById(adContestualizzata
                                .getChiaveAdContestualizzata()
                                .getAdCod())
                                .isPresent())
                ).collect(Collectors.toList());
        adContestualizzatiCicloUnico= adContestualizzatiCicloUnico
                .stream()
                .filter(adContestualizzata ->
                        !(this.attivitaDidatticheRepository.findById(adContestualizzata
                                .getChiaveAdContestualizzata()
                                .getAdCod())
                                .isPresent())
                ).collect(Collectors.toList());



        logger.info("Recupero SegmentiContetsualizzati Triennali per insegnamenti programmati iniziato");
        //ora per ogni insegnamento devo prendere tutti i dati quindi mi servono i segmenti
        for (ADContestualizzata adContestualizzata : adContestualizzatiTriennale) {
            Invocation.Builder invocationBuilder2 = webTarget
                    .path("/offerte/" + year + "/" + adContestualizzata.getChiaveAdContestualizzata().getCdsId() + "/segmenti")
                    .queryParam("adId", adContestualizzata.getChiaveAdContestualizzata().getAdId())
                    .request(MediaType.APPLICATION_JSON);
            SEGContestualizzato[] segContestualizzati = invocationBuilder2.get(SEGContestualizzato[].class);
            this.addInsegnamenti(
                    segContestualizzati,
                    adContestualizzata,
                    true,
                    TipoCorsoDiLaurea.L2
            );
            //molti insegnamenti sono
            Thread.sleep(500);

        }
        logger.info("Recupero SegmentiContetsualizzati Triennali  concluso");

        logger.info("Recupero SegmentiContetsualizzati Magistrali per insegnamenti programmati iniziato");
        //ora per ogni insegnamento devo prendere tutti i dati quindi mi servono i segmenti
        for (ADContestualizzata adContestualizzata : adContestualizzatiMagistrale) {
            Invocation.Builder invocationBuilder2 = webTarget
                    .path("/offerte/" + year + "/" + adContestualizzata.getChiaveAdContestualizzata().getCdsId() + "/segmenti")
                    .queryParam("adId", adContestualizzata.getChiaveAdContestualizzata().getAdId())
                    .request(MediaType.APPLICATION_JSON);
            SEGContestualizzato[] segContestualizzati = invocationBuilder2.get(SEGContestualizzato[].class);
            this.addInsegnamenti(
                    segContestualizzati,
                    adContestualizzata,
                    true,
                    TipoCorsoDiLaurea.LM
            );
            Thread.sleep(500);

        }
        logger.info("Recupero SegmentiContetsualizzati Magistrale concluso");

        logger.info("Recupero SegmentiContetsualizzati CicloUnico per insegnamenti programmati iniziato");
        //ora per ogni insegnamento devo prendere tutti i dati quindi mi servono i segmenti
        for (ADContestualizzata adContestualizzata : adContestualizzatiCicloUnico) {
            Invocation.Builder invocationBuilder2 = webTarget
                    .path("/offerte/" + year + "/" + adContestualizzata.getChiaveAdContestualizzata().getCdsId() + "/segmenti")
                    .queryParam("adId", adContestualizzata.getChiaveAdContestualizzata().getAdId())
                    .request(MediaType.APPLICATION_JSON);
            SEGContestualizzato[] segContestualizzati = invocationBuilder2.get(SEGContestualizzato[].class);
            this.addInsegnamenti(
                    segContestualizzati,
                    adContestualizzata,
                    true,
                    TipoCorsoDiLaurea.LM5
            );
            Thread.sleep(500);
        }
        logger.info("Recupero SegmentiContetsualizzati CicloUnico  concluso");

    }

    private void updateCorsoDiStudiProgrammati(List<Offerta> offerte, int year) throws InterruptedException {
        //devo droppare il database e ricostruirlo
        logger.info("Iniziato l'aggiornamento dei corsi di studio programmati");
        logger.info("anno di riferimento: "+year);

        //recupero gli insegnamenti dell'anno corrente
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(endPointRegole);
        logger.info("Recupero regole iniziato");
        List<RegolamentoDiScelta> regolamentiDiScelta = new ArrayList<>();
        for(Offerta offerta: offerte) {
            logger.info("Ricerca regolamento di scelta per il corso di laurea con id: "+offerta.getCdsOffId());
            Invocation.Builder invocationBuilder = webTarget
                    .path("/regsce")
                    .queryParam("cdsId",offerta.getCdsOffId())
                    .queryParam("order","-coorte")
                    .request(MediaType.APPLICATION_JSON);
            List<RegolamentoDiScelta> buffer = Arrays.asList(invocationBuilder.get(RegolamentoDiScelta[].class));
            if(!buffer.isEmpty())
                regolamentiDiScelta.add(buffer.get(0));
            //incremento start di 100 in 100
            Thread.sleep(2 * 1000);
        }
        //prendo in considerazione solo i corsi di laurea triennale,magistrale e a ciclo unico
        regolamentiDiScelta=regolamentiDiScelta
                .stream()
                .filter(regolamentoDiScelta ->
                        regolamentoDiScelta.getTipoCorsoCod().equals(TipoCorsoDiLaurea.L2.toString())
                                || regolamentoDiScelta.getTipoCorsoCod().equals(TipoCorsoDiLaurea.LM.toString())
                                || regolamentoDiScelta.getTipoCorsoCod().equals(TipoCorsoDiLaurea.LM5.toString())
                ).collect(Collectors.toList());
        logger.info("Recupero regole terminato, trovate: "+regolamentiDiScelta.size()+" regole");

        logger.info("Inizio recupero facoltà");
        webTarget = client.target(endPointFacolta);
        for (RegolamentoDiScelta regolamentoDiScelta: regolamentiDiScelta){
            Invocation.Builder invocationBuilder = webTarget
                    .path(String.valueOf(regolamentoDiScelta.getFacId()))
                    .request(MediaType.APPLICATION_JSON);
            Facolta facolta = invocationBuilder.get(Facolta.class);
            logger.info("Facoltà: "+facolta.getFacDes());
            if(!this.corsiDiStudioRepository.findById(regolamentoDiScelta.getCdsCod()).isPresent())
                this.corsiDiStudioRepository.save(CorsoDiStudiUtil.getCorsoDiStudioProgrammato(regolamentoDiScelta,facolta));
            Thread.sleep(2*1000);
        }
    }

    private void updateCorsoDiStudi(List<Offerta>offerte, int year) throws InterruptedException {
        //devo droppare il database e ricostruirlo
        logger.info("Iniziato l'aggiornamento dei corsi di studio");
        this.dropCorsiDiStudio();
        logger.info("anno di riferimento: "+year);

        //recupero gli insegnamenti dell'anno corrente
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(endPointRegole);
        logger.info("Recupero regole iniziato");
        List<RegolamentoDiScelta> regolamentiDiScelta = new ArrayList<>();
        for(Offerta offerta: offerte) {
            logger.info("Ricerca regolamento di scelta per il corso di laurea con id: "+offerta.getCdsOffId());
            Invocation.Builder invocationBuilder = webTarget
                    .path("/regsce")
                    .queryParam("cdsId",offerta.getCdsOffId())
                    .queryParam("order","-coorte")
                    .request(MediaType.APPLICATION_JSON);
            List<RegolamentoDiScelta> buffer = Arrays.asList(invocationBuilder.get(RegolamentoDiScelta[].class));
            if(!buffer.isEmpty())
                regolamentiDiScelta.add(buffer.get(0));
            //incremento start di 100 in 100
            Thread.sleep(2 * 1000);
        }
        //prendo in considerazione solo i corsi di laurea triennale,magistrale e a ciclo unico
        regolamentiDiScelta=regolamentiDiScelta
                .stream()
                .filter(regolamentoDiScelta ->
                        regolamentoDiScelta.getTipoCorsoCod().equals(TipoCorsoDiLaurea.L2.toString())
                                || regolamentoDiScelta.getTipoCorsoCod().equals(TipoCorsoDiLaurea.LM.toString())
                                || regolamentoDiScelta.getTipoCorsoCod().equals(TipoCorsoDiLaurea.LM5.toString())
                ).collect(Collectors.toList());
        logger.info("Recupero regole terminato, trovate: "+regolamentiDiScelta.size()+" regole");

        logger.info("Inizio recupero facoltà");
        webTarget = client.target(endPointFacolta);
        for (RegolamentoDiScelta regolamentoDiScelta: regolamentiDiScelta){
            Invocation.Builder invocationBuilder = webTarget
                    .path(String.valueOf(regolamentoDiScelta.getFacId()))
                    .request(MediaType.APPLICATION_JSON);
            Facolta facolta = invocationBuilder.get(Facolta.class);
            logger.info("Facoltà: "+facolta.getFacDes());
            this.corsiDiStudioRepository.save(CorsoDiStudiUtil.getCorsoDiStudio(regolamentoDiScelta,facolta));
            Thread.sleep(2*1000);
        }

    }

    public void dropCorsiDiStudio() {
        this.corsiDiStudioRepository.deleteAll();
    }


    public void dropAttivitaDidattiche(){
        attivitaDidatticheRepository.deleteAll();
    }

    public List<Orientamento> getOrientamentoManifesto(int coorte, String codiceCorsoDiStudio){
        ChiaveManifestoDegliStudi chiaveManifestoDegliStudi = new ChiaveManifestoDegliStudi();
        //se cerco gli orientamenti non devo specificare il curriculum, sono mutuamente esclusivi
        chiaveManifestoDegliStudi.setCodiceCorsoDiStudio(codiceCorsoDiStudio);
        chiaveManifestoDegliStudi.setCurricula(null);
        chiaveManifestoDegliStudi.setCoorte(coorte);
        Optional<ManifestoDegliStudi> optionalManifestoDegliStudi = this.readManifestoDegliStudiPort.findManifestoById(chiaveManifestoDegliStudi);
        if(optionalManifestoDegliStudi.isPresent()) {
            ManifestoDegliStudi manifestoDegliStudi = optionalManifestoDegliStudi.get();
            Map<Integer, AnnoAccademico> schemiDiPiano = manifestoDegliStudi.getAnniAccademici();
            List<Orientamento>orientamenti = new ArrayList<>();
            for (Integer anno: schemiDiPiano.keySet()) {
                if (schemiDiPiano.get(anno).getOrientamenti().isPresent())
                    orientamenti.addAll(schemiDiPiano.get(anno).getOrientamenti().get());
            }
            return orientamenti;
        }
        return new ArrayList<>();
    }


}
