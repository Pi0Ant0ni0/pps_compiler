package it.unisannio.studenti.p.perugini.pps_compiler.Components;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.TextAlignment;

import com.itextpdf.layout.properties.UnitValue;
import it.unisannio.studenti.p.perugini.pps_compiler.API.*;
import it.unisannio.studenti.p.perugini.pps_compiler.API.AttivitaDidatticheVincolateDalCorsoDiStudio;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.SEMESTRE;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche.AttivitaDidatticaPPSDTO;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche.InsegnamentoRegola;
import it.unisannio.studenti.p.perugini.pps_compiler.API.Orientamento;
import it.unisannio.studenti.p.perugini.pps_compiler.Services.CorsoDiStudioService;
import it.unisannio.studenti.p.perugini.pps_compiler.core.attivitaDidattica.port.ReadAttivitaDidatticaPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.corsoDiStudio.port.ReadCorsoDiStudioPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ManifestoDegliStudiMaker {
    private static int size = 8;
    private static Color blu = Color.convertRgbToCmyk(new DeviceRgb(1, 33, 105));
    private static Color oro = Color.convertRgbToCmyk(new DeviceRgb(177, 122, 0));
    private static Color nuvole = Color.convertRgbToCmyk(new DeviceRgb(66, 66, 66));
    private static Color magenta = Color.convertRgbToCmyk(new DeviceRgb(165, 0, 80));

    public static Logger logger = LoggerFactory.getLogger(ManifestoDegliStudiMaker.class);
    @Autowired
    private CorsoDiStudioService corsoDiStudioService;
    @Autowired
    private ReadAttivitaDidatticaPort readAttivitaDidatticaPort;

    public Document getManifestoDegliStudi(ManifestoDegliStudi manifestoDegliStudi, OutputStream outputStream) throws FileNotFoundException {
        logger.info("Sto per creare il pdf per il manifesto con chiave: " + manifestoDegliStudi.getChiaveManifestoDegliStudi());

        Optional<CorsoDiStudio> corsoDiStudio = corsoDiStudioService.getCorsoDiStudioById(manifestoDegliStudi.getChiaveManifestoDegliStudi().getCodiceCorsoDiStudio());
        if (!corsoDiStudio.isPresent())
            return null;

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outputStream));
        Document document = new Document(pdfDocument);


        Table externalTable = new Table(1);
        Table table = new Table(5);
        addTableTitle(table, corsoDiStudio.get(), manifestoDegliStudi.getChiaveManifestoDegliStudi().getCoorte(), manifestoDegliStudi.getChiaveManifestoDegliStudi().getCurricula());
        addHeaderInsegnamenti(table, blu);

        //ordino gli anni
        Set<Integer> anni = new TreeSet<>(manifestoDegliStudi.getAnniAccademici().keySet());
        for (Integer anno : anni) {
            //recupero lo schema di un anno
            AnnoAccademico annoAccademico = manifestoDegliStudi.getAnniAccademici().get(anno);
            //cfu  totali per un anno
            int cfuTotali = annoAccademico.getCfuTotali();
            //aggiungo header dell'anno
            addHeaderAnno(table, anno, cfuTotali);
            //aggiungo insegnmaenti obbligatori
            addInsegnamentiRow(table, annoAccademico.getInsegnamentiObbligatori());

            //aggiungo gli insegnamenti vincolati se ci sono
            if (annoAccademico.getAttivitaDidatticheVincolateDalCorsoDiStudio().isPresent() &&
                    !annoAccademico.getAttivitaDidatticheVincolateDalCorsoDiStudio().get().isEmpty()) {
                for (AttivitaDidatticheVincolateDalCorsoDiStudio insegnamenti : annoAccademico.getAttivitaDidatticheVincolateDalCorsoDiStudio().get()) {
                    addHeaderVincolati(table, insegnamenti);
                    addHeaderInsegnamenti(table, magenta);
                    addInsegnamentiRow(table, insegnamenti.getInsegnamentiRegola());
                }
            }
            if (annoAccademico.getOrientamenti().isPresent() && !annoAccademico.getOrientamenti().get().isEmpty()) {
                addOrientamentoRow(table, annoAccademico.getCfuOrientamento());
            }
            if (annoAccademico.getCfuAScelta() != 0) {
                addLiberiRow(table, annoAccademico.getCfuAScelta());
            }

            //inserisco orientamento se ci sono
            if (annoAccademico.getOrientamenti().isPresent()) {
                for (Orientamento orientamento : annoAccademico.getOrientamenti().get()) {
                    addHeaderOrientamento(table, orientamento.getDenominazione(), orientamento.getQuotaCFULiberi() + orientamento.getQuotaCFUVincolati());

                    if (orientamento.getInsegnamentiVincolati().isPresent() && !orientamento.getInsegnamentiVincolati().get().isEmpty()) {
                        addHeaderOrientamentoVincolati(table, orientamento.getDenominazione(), manifestoDegliStudi.getCfuOrientamento());
                        addHeaderInsegnamenti(table, oro);
                        addInsegnamentiRow(table, orientamento.getInsegnamentiVincolati().get());
                    }
                    if (orientamento.getInsegnamentiLiberi().isPresent() && !orientamento.getInsegnamentiLiberi().get().isEmpty()) {
                        addHeaderOrientamentoLiberi(table, orientamento.getQuotaCFULiberi());
                        addInsegnamentiRow(table, orientamento.getInsegnamentiLiberi().get());
                    }
                }
            }

        }

        addFooterNote(table);
        table.setWidth(UnitValue.createPercentValue(100));
        externalTable.setWidth(UnitValue.createPercentValue(100));
        externalTable.addCell(new Cell()
                .add(table)
                .setBorder(new SolidBorder(ColorConstants.BLACK, 2))
                .setPadding(0));
        document.add(externalTable);


        //liberi
        if (manifestoDegliStudi.getAttivitaDidatticheAScelta().isPresent()) {
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            Table externalTableLiberi = new Table(1);
            Table tableLiberi = new Table(6);
            addHeaderLiberi(tableLiberi);
            addHeaderTabellaLiberi(tableLiberi, nuvole);
            addInsegnamentiLiberi(tableLiberi, manifestoDegliStudi.getAttivitaDidatticheAScelta().get());
            tableLiberi.setWidth(UnitValue.createPercentValue(100));
            externalTableLiberi.setWidth(UnitValue.createPercentValue(100));
            externalTableLiberi.addCell(new Cell()
                    .add(tableLiberi)
                    .setBorder(new SolidBorder(ColorConstants.BLACK, 2))
                    .setPadding(0));
            document.add(externalTableLiberi);
        }

        document.close();
        return document;
    }

    private void addOrientamentoRow(Table table, int cfuO) {
        Cell ssd = new Cell();
        Cell codice = new Cell();
        Cell denominazione = new Cell().add(new Paragraph("INSEGNAMENTI DI ORIENTAMENTO").setFontSize(size));
        Cell cfu = new Cell().add(new Paragraph(String.valueOf(cfuO)).setFontSize(size));
        Cell semestre = new Cell().add(new Paragraph(SEMESTRE.annuale).setFontSize(size));
        table.addCell(ssd).addCell(codice).addCell(denominazione).addCell(cfu).addCell(semestre);
    }

    private void addLiberiRow(Table table, int cfuAScelta) {
        Cell ssd = new Cell();
        Cell codice = new Cell();
        Cell denominazione = new Cell().add(new Paragraph("INSEGNAMENTI LIBERI").setFontSize(size));
        Cell cfu = new Cell().add(new Paragraph(String.valueOf(cfuAScelta)).setFontSize(size));
        Cell semestre = new Cell().add(new Paragraph(SEMESTRE.annuale).setFontSize(size));
        table.addCell(ssd).addCell(codice).addCell(denominazione).addCell(cfu).addCell(semestre);

    }


    private void addTableTitle(Table table, CorsoDiStudio corsoDiStudio, int coorte, Optional<String> curricula) {

        Cell intestazione = new Cell(1, 5)
                .add(new Paragraph("CORSO DI LAUREA IN: " + corsoDiStudio.getDenominazione() + "\n" +
                        "MANIFESTO DEGLI STUDI " + coorte)
                        .setBold()
                        .setFontColor(magenta)
                        .setFontSize(size))
                .setBackgroundColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER);
        if (curricula.isPresent()) {
            intestazione.add(new Paragraph("Curriculum " + curricula.get()).
                    setBold()
                    .setFontColor(magenta)
                    .setFontSize(size))
                    .setBackgroundColor(ColorConstants.WHITE)
                    .setTextAlignment(TextAlignment.CENTER);
        }

        table.addCell(intestazione);
    }

    private void addHeaderOrientamento(Table table, String denominazione, int cfu) {
        Cell intestazione = new Cell(1, 5)
                .add(new Paragraph("Insegnamenti di orientamento " + denominazione + "(" + cfu + " CFU)")
                        .setBold()
                        .setFontSize(size)
                        .setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(oro)
                .setTextAlignment(TextAlignment.CENTER);
        table.addCell(intestazione);

    }


    private void addHeaderOrientamentoVincolati(Table table, String denominazione, int cfu) {

        Cell intestazione2 = new Cell(1, 5)
                .add(new Paragraph("obbligatori:")
                        .setBold()
                        .setFontSize(size)
                        .setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(oro)
                .setTextAlignment(TextAlignment.CENTER);
        table.addCell(intestazione2);
    }

    private void addHeaderOrientamentoLiberi(Table table, int cfu) {
        //aggiungo intestazione
        Cell intestazione = new Cell(1, 5)
                .add(new Paragraph("(" + cfu + " CFU) a scelta tra gli insegnamenti proposti")
                        .setBold()
                        .setFontSize(size))
                .setBackgroundColor(oro)
                .setFontColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER);
        table.addCell(intestazione);
    }


    private void addHeaderVincolati(Table table, AttivitaDidatticheVincolateDalCorsoDiStudio insegnamentiVincolati) {

        String denominazione = this.corsoDiStudioService.getCorsiDiStudio()
                .stream()
                .filter(corsoDiStudio ->
                        corsoDiStudio.getCodice().equals(insegnamentiVincolati.getCorsoDiStudioVincolante())
                )
                .findFirst()
                .get()
                .getDenominazione();
        //aggiungo intestazione
        Cell intestazione = new Cell(1, 5)
                .add(new Paragraph("Questi insegnamenti sono proposti per coloro che provengo da: " + denominazione + " CFUTotali: " + insegnamentiVincolati.getNumeroCfuDaScegliere())
                        .setBold()
                        .setFontSize(size)
                        .setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(magenta)
                .setTextAlignment(TextAlignment.CENTER);
        table.addCell(intestazione);
    }

    private void addFooterNote(Table table) {
        //aggiungo intestazione
        Cell intestazione = new Cell(1, 5)
                .add(new Paragraph("NOTE")
                        .setBold()
                        .setFontSize(size)
                        .setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(blu)
                .setTextAlignment(TextAlignment.CENTER);
        Cell note = new Cell(1, 5)
                .add(new Paragraph("(^) Insegnamento annuale  (*)Insegnamento integrato")
                        .setFontSize(size))
                .setTextAlignment(TextAlignment.CENTER);
        table.addCell(intestazione);
        table.addCell(note);
    }


    private void addInsegnamentiRow(Table table, List<InsegnamentoRegola> insegnamenti) {
        insegnamenti = insegnamenti.stream()
                .sorted(Comparator.comparing(o -> o.getSemestre()))
                .collect(Collectors.toList());
        for (InsegnamentoRegola insegnamentoRegola : insegnamenti) {
            Cell ssd = new Cell();
            Cell codice = new Cell();
            Cell denominazione = new Cell();
            Cell cfu = new Cell();
            Cell semestre = new Cell();

            if (insegnamentoRegola.isInsegnamentoIntegratoFlag()) {
                List<AttivitaDidattica> integrati = this.readAttivitaDidatticaPort.findAttivitaById(insegnamentoRegola.getCodiceInsegnamento()).get().getUnitaDidattiche().get();
                if (integrati.size() == 1) {
                    integrati = new ArrayList<>();
                    AttivitaDidattica attivitaDidattica1 = this.readAttivitaDidatticaPort.findAttivitaById(insegnamentoRegola.getCodiceInsegnamento()).get();
                    attivitaDidattica1.setCfu(attivitaDidattica1.getCfu() / 2);
                    integrati.add(attivitaDidattica1);

                    AttivitaDidattica attivitaDidattica2 = this.readAttivitaDidatticaPort.findAttivitaById(insegnamentoRegola.getCodiceInsegnamento()).get();
                    attivitaDidattica2.setCfu(attivitaDidattica2.getCfu() / 2);
                    integrati.add(attivitaDidattica2);
                }
                int sem = 1;
                for (AttivitaDidattica attivitaDidattica : integrati) {
                    ssd.add(new Paragraph(insegnamentoRegola.getSettoreScientificoDisciplinare()).setFontSize(size));
                    cfu.add(new Paragraph(String.valueOf(attivitaDidattica.getCfu())).setFontSize(size));
                    codice.add(new Paragraph(attivitaDidattica.getCodiceAttivitaDidattica()).setFontSize(size));
                    semestre.add(new Paragraph(String.valueOf(sem)).setFontSize(size));
                    if (insegnamentoRegola.getCodiceCorsoDiStudioMuoto().isPresent() && insegnamentoRegola.getCodiceCorsoDiStudioMuoto().get().length() != 0) {
                        String denominazioneCorsoMutuo = corsoDiStudioService.getCorsiDiStudio()
                                .stream()
                                .filter(corsoDiStudio ->
                                        corsoDiStudio.getCodice().equals(insegnamentoRegola.getCodiceCorsoDiStudioMuoto().get()))
                                .findFirst().get()
                                .getDenominazione();
                        denominazione.add(
                                new Paragraph(" * (Corso Mutuo con " + denominazioneCorsoMutuo + " ) ")
                                        .setFontSize(size - 3)
                        );
                    } else {
                        denominazione.add(new Paragraph(attivitaDidattica.getDenominazioneAttivitaDidattica() + " *")
                                .setFontSize(size));
                    }
                    table.addCell(ssd);
                    table.addCell(codice);
                    table.addCell(denominazione);
                    table.addCell(cfu);
                    table.addCell(semestre);
                    sem++;

                    //nuova riga
                    ssd = new Cell();
                    codice = new Cell();
                    denominazione = new Cell();
                    cfu = new Cell();
                    semestre = new Cell();
                }
            } else {
                String denominazioneInsegnamento = insegnamentoRegola.getDenominazioneInsegnamento();
                if (insegnamentoRegola.isAnnualeFlag()) {
                    denominazione.add(new Paragraph(denominazioneInsegnamento + "^")
                            .setFontSize(size));
                } else {
                    denominazione.add(new Paragraph(denominazioneInsegnamento)
                            .setFontSize(size));
                }

                if (insegnamentoRegola.getCodiceCorsoDiStudioMuoto().isPresent() && insegnamentoRegola.getCodiceCorsoDiStudioMuoto().get().length() != 0) {
                    String denominazioneCorsoMutuo = corsoDiStudioService.getCorsiDiStudio()
                            .stream()
                            .filter(corsoDiStudio ->
                                    corsoDiStudio.getCodice().equals(insegnamentoRegola.getCodiceCorsoDiStudioMuoto().get()))
                            .findFirst().get()
                            .getDenominazione();
                    denominazione.add(
                            new Paragraph(" (Corso Mutuo con " + denominazioneCorsoMutuo + " ) ")
                                    .setFontSize(size - 3)
                    );
                }


                ssd.add(new Paragraph(insegnamentoRegola.getSettoreScientificoDisciplinare()).setFontSize(size));
                codice.add(new Paragraph(insegnamentoRegola.getCodiceInsegnamento())
                        .setFontSize(size));
                cfu.add(new Paragraph(String.valueOf(insegnamentoRegola.getCfu()))
                        .setFontSize(size));
                semestre.add(new Paragraph(insegnamentoRegola.getSemestre())
                        .setFontSize(size));

                //aggiungo insegnamento
                table.addCell(ssd);
                table.addCell(codice);
                table.addCell(denominazione);
                table.addCell(cfu);
                table.addCell(semestre);
            }
        }

    }

    private void addHeaderAnno(Table table, int anno, int cfu) {
        //aggiungo intestazione
        Cell intestazione = new Cell(1, 5)
                .add(new Paragraph(anno + "° ANNO - CFU TOTALI: " + cfu)
                        .setBold()
                        .setFontSize(size)
                        .setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(blu)
                .setTextAlignment(TextAlignment.CENTER);
        table.addCell(intestazione);
    }

    private void addHeaderInsegnamenti(Table table, Color color) {
        Stream.of("SSD", "Codice", "Insegnamento", "CFU", "Semestre")
                .forEach(columnTitle -> {
                    Cell header = new Cell();
                    header.setBackgroundColor(color);
                    header.add(new Paragraph(columnTitle)
                            .setBold()
                            .setFontSize(size)
                            .setFontColor(ColorConstants.WHITE));
                    table.addCell(header);
                });
    }


    //liberi
    private void addHeaderLiberi(Table table) {
        //aggiungo intestazione
        Cell intestazione = new Cell(1, 6)
                .add(new Paragraph("INSEGNAMENTI A SCELTA DI AUTOMATICA APPROVAZIONE")
                        .setBold()
                        .setFontSize(size)
                        .setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(nuvole)
                .setTextAlignment(TextAlignment.CENTER);
        table.addCell(intestazione);
    }

    private void addInsegnamentiLiberi(Table table, List<AttivitaDidatticaPPSDTO> insegnamenti) {
        for (AttivitaDidatticaPPSDTO insegnamentoRegola : insegnamenti) {
            Cell ssd = new Cell();
            Cell codice = new Cell();
            Cell denominazione = new Cell();
            Cell cfu = new Cell();
            Cell codiceCorsoDiStudio = new Cell();
            Cell corsoDiStudio = new Cell();

            ssd.add(new Paragraph(insegnamentoRegola.getSettoreScientificoDisciplinare()).setFontSize(size));
            denominazione.add(new Paragraph(insegnamentoRegola.getDenominazioneAttivitaDidattica())).setFontSize(size);
            codice.add(new Paragraph(insegnamentoRegola.getCodiceAttivitaDidattica())
                    .setFontSize(size));
            cfu.add(new Paragraph(String.valueOf(insegnamentoRegola.getCfu()))
                    .setFontSize(size));
            codiceCorsoDiStudio.add(new Paragraph(insegnamentoRegola.getCodiceCorsoDiStudio())
                    .setFontSize(size));
            corsoDiStudio.add(new Paragraph(insegnamentoRegola.getDenominazioneCorsoDiStudio())
                    .setFontSize(size));

            //aggiungo insegnamento
            table.addCell(ssd);
            table.addCell(codice);
            table.addCell(denominazione);
            table.addCell(cfu);
            table.addCell(codiceCorsoDiStudio);
            table.addCell(corsoDiStudio);
        }

    }

    private void addHeaderTabellaLiberi(Table table, Color color) {
        Stream.of("SSD", "Codice", "Insegnamento", "CFU", "Codice Corso Di Studio", "Denominazione Corso Di Studio")
                .forEach(columnTitle -> {
                    Cell header = new Cell();
                    header.setBackgroundColor(color);
                    header.add(new Paragraph(columnTitle)
                            .setBold()
                            .setFontSize(size)
                            .setFontColor(ColorConstants.WHITE));
                    table.addCell(header);
                });
    }
}
