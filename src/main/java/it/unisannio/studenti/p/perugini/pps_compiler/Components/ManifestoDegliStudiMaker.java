package it.unisannio.studenti.p.perugini.pps_compiler.Components;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;

import com.itextpdf.layout.properties.UnitValue;
import it.unisannio.studenti.p.perugini.pps_compiler.API.*;
import it.unisannio.studenti.p.perugini.pps_compiler.API.AttivitaDidatticheVincolateDalCorsoDiStudio;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.SEMESTRE;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche.InsegnamentoRegola;
import it.unisannio.studenti.p.perugini.pps_compiler.API.Orientamento;
import it.unisannio.studenti.p.perugini.pps_compiler.Services.CorsoDiStudioService;
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

    public  Document getManifestoDegliStudi(ManifestoDegliStudi manifestoDegliStudi, OutputStream outputStream, CorsoDiStudio corsoDiStudio) throws FileNotFoundException {
        logger.info("Sto per creare il pdf per la regola con chiave: "+ manifestoDegliStudi.getChiaveManifestoDegliStudi());

        PdfDocument pdfDocument = new PdfDocument( new PdfWriter(outputStream));
        Document document = new Document(pdfDocument);


        Table externalTable = new Table(1);
        Table table = new Table(5);
        addTableTitle(table,corsoDiStudio, manifestoDegliStudi.getChiaveManifestoDegliStudi().getCoorte());
        addHeaderInsegnamenti(table, blu);

        //ordino gli anni
        Set<Integer> anni = new TreeSet<>(manifestoDegliStudi.getAnniAccademici().keySet());
        for(Integer anno : anni) {
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
                    addInsegnamentiRow(table,insegnamenti.getInsegnamentiRegola());
                }
            }
            if(annoAccademico.getOrientamenti().isPresent() && !annoAccademico.getOrientamenti().get().isEmpty()){
                addOrientamentoRow(table, manifestoDegliStudi);
            }
            if(annoAccademico.getAttivitaDidatticheAScelta().isPresent() && !annoAccademico.getAttivitaDidatticheAScelta().get().getInsegnamenti().isEmpty()){
                addLiberiRow(table, annoAccademico.getAttivitaDidatticheAScelta().get());
            }

            //inserisco orientamento se ci sono
            if (annoAccademico.getOrientamenti().isPresent()) {
                for(Orientamento orientamento : annoAccademico.getOrientamenti().get()) {

                    if(orientamento.getInsegnamentiVincolati().isPresent() && !orientamento.getInsegnamentiVincolati().get().isEmpty()) {
                        addHeaderOrientamentoVincolati(table, orientamento.getDenominazione(), manifestoDegliStudi.getCfuOrientamento());
                        addHeaderInsegnamenti(table, oro);
                        addInsegnamentiRow(table, orientamento.getInsegnamentiVincolati().get());
                    }
                    if(orientamento.getInsegnamentiLiberi().isPresent() && !orientamento.getInsegnamentiLiberi().get().isEmpty()){
                        addHeaderOrientamentoLiberi(table, orientamento.getQuotaCFULiberi());
                        addInsegnamentiRow(table, orientamento.getInsegnamentiLiberi().get());
                    }
                }
            }

            //aggiungo le scelte se ci sono
            if (annoAccademico.getAttivitaDidatticheAScelta().isPresent() && !annoAccademico.getAttivitaDidatticheAScelta().get().getInsegnamenti().isEmpty()) {
                addHeaderLiberi(table);
                addHeaderInsegnamenti(table, nuvole);
                addInsegnamentiRow(table, annoAccademico.getAttivitaDidatticheAScelta().get().getInsegnamenti());
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
        document.close();
        return document;
    }

    private  void addOrientamentoRow(Table table, ManifestoDegliStudi manifestoDegliStudi) {
        Cell ssd = new Cell();
        Cell codice = new Cell();
        Cell denominazione = new Cell().add(new Paragraph("INSEGNAMENTI DI ORIENTAMENTO").setFontSize(size));
        Cell cfu = new Cell().add(new Paragraph(String.valueOf(manifestoDegliStudi.getCfuOrientamento())).setFontSize(size));
        Cell semestre = new Cell().add(new Paragraph(SEMESTRE.annuale).setFontSize(size));
        table.addCell(ssd).addCell(codice).addCell(denominazione).addCell(cfu).addCell(semestre);
    }

    private  void addLiberiRow(Table table, AttivitaDidatticheAScelta attivitaDidatticheAScelta) {
        Cell ssd = new Cell();
        Cell codice = new Cell();
        Cell denominazione = new Cell().add(new Paragraph("INSEGNAMENTI LIBERI").setFontSize(size));
        Cell cfu = new Cell().add(new Paragraph(String.valueOf(attivitaDidatticheAScelta.getCfuDaScegliere())).setFontSize(size));
        Cell semestre = new Cell().add(new Paragraph(SEMESTRE.annuale).setFontSize(size));
        table.addCell(ssd).addCell(codice).addCell(denominazione).addCell(cfu).addCell(semestre);

    }



    private  void addTableTitle(Table table, CorsoDiStudio corsoDiStudio, int coorte) {

        Cell intestazione = new Cell(1,5)
                .add(new Paragraph("CORSO DI LAUREA IN: " + corsoDiStudio.getDenominazione()+"\n" +
                        "MANIFESTO DEGLI STUDI "+coorte)
                        .setBold()
                        .setFontColor(magenta)
                        .setFontSize(size))
                .setBackgroundColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER);

        table.addCell(intestazione);
    }



    private  void addHeaderOrientamentoVincolati(Table table, String denominazione, int cfu) {
        Cell intestazione = new Cell(1,5)
                .add(new Paragraph("Insegnamenti di orientamento: "+denominazione+"("+cfu+" CFU)")
                        .setBold()
                        .setFontSize(size)
                        .setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(oro)
                .setTextAlignment(TextAlignment.CENTER);
        Cell intestazione2 = new Cell(1,5)
                .add(new Paragraph("OBBLIGATORI:")
                        .setBold()
                        .setFontSize(size)
                        .setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(oro)
                .setTextAlignment(TextAlignment.CENTER);

        table.addCell(intestazione);
        table.addCell(intestazione2);
    }

    private  void addHeaderOrientamentoLiberi(Table table, int cfu) {
        //aggiungo intestazione
        Cell intestazione = new Cell(1,5)
                .add(new Paragraph("A SCELTA ("+cfu+" CFU)")
                        .setBold()
                        .setFontSize(size))
                .setBackgroundColor(oro)
                .setFontColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER);
        table.addCell(intestazione);
    }

    private  void addHeaderLiberi(Table table) {
        //aggiungo intestazione
        Cell intestazione = new Cell(1,5)
                .add(new Paragraph("Insegnamenti a scelta libera di automatica approvazione")
                        .setBold()
                        .setFontSize(size)
                        .setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(nuvole)
                .setTextAlignment(TextAlignment.CENTER);
        table.addCell(intestazione);


    }

    private  void addHeaderVincolati(Table table, AttivitaDidatticheVincolateDalCorsoDiStudio insegnamentiVincolati) {

        String denominazione = this.corsoDiStudioService.getCorsiDiStudio()
                .stream()
                .filter(corsoDiStudio ->
                        corsoDiStudio.getCodice().equals(insegnamentiVincolati.getCorsoDiStudioVincolante())
                )
                .findFirst()
                .get()
                .getDenominazione();
        //aggiungo intestazione
        Cell intestazione = new Cell(1,5)
                .add(new Paragraph("Questi insegnamenti sono proposti per coloro che provengo da: "+denominazione+" CFUTotali: "+insegnamentiVincolati.getNumeroCfuDaScegliere())
                        .setBold()
                        .setFontSize(size)
                        .setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(magenta)
                .setTextAlignment(TextAlignment.CENTER);
        table.addCell(intestazione);
    }

    private  void addFooterNote(Table table) {
        //aggiungo intestazione
        Cell intestazione = new Cell(1,5)
                .add(new Paragraph("NOTE")
                        .setBold()
                        .setFontSize(size)
                        .setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(blu)
                .setTextAlignment(TextAlignment.CENTER);
        Cell note = new Cell(1,5)
                .add(new Paragraph("(^) Insegnamento annuale  (*)Insegnamento integrato")
                        .setFontSize(size))
                .setTextAlignment(TextAlignment.CENTER);
        table.addCell(intestazione);
        table.addCell(note);
    }


    private  void addInsegnamentiRow(Table table, List<InsegnamentoRegola> insegnamenti){
        insegnamenti = insegnamenti.stream()
                .sorted(Comparator.comparing(o -> o.getSemestre()))
                .collect(Collectors.toList());
        for (InsegnamentoRegola insegnamentoRegola : insegnamenti) {
            Cell ssd = new Cell();
            Cell codice = new Cell();
            Cell denominazione = new Cell();
            Cell cfu = new Cell();
            Cell semestre = new Cell();

            ssd.add(new Paragraph(insegnamentoRegola.getSettoreScientificoDisciplinare()).setFontSize(size));
            String denominazioneInsegnamento = insegnamentoRegola.getDenominazioneInsegnamento();
            if(insegnamentoRegola.isAnnualeFlag()) {
                denominazione.add(new Paragraph(denominazioneInsegnamento+"^")
                        .setFontSize(size));
            }else if (insegnamentoRegola.isInsegnamentoIntegratoFlag()) {
                denominazione.add(new Paragraph(denominazioneInsegnamento + "*")
                        .setFontSize(size));
            }else {
                denominazione.add(new Paragraph(denominazioneInsegnamento)
                        .setFontSize(size));
            }
            if(insegnamentoRegola.getCodiceCorsoDiStudioMuoto().isPresent() && insegnamentoRegola.getCodiceCorsoDiStudioMuoto().get().length()!=0){
                String denominazioneCorsoMutuo = corsoDiStudioService.getCorsiDiStudio()
                        .stream()
                        .filter(corsoDiStudio ->
                                corsoDiStudio.getCodice().equals(insegnamentoRegola.getCodiceCorsoDiStudioMuoto().get()))
                        .findFirst().get()
                        .getDenominazione();
                denominazione.add(
                        new Paragraph(" (Corso Mutuo con "+denominazioneCorsoMutuo+" ) ")
                        .setFontSize(size-3)
                );
            }

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

    private  void addHeaderAnno(Table table, int anno, int cfu){
        //aggiungo intestazione
        Cell intestazione = new Cell(1,5)
                .add(new Paragraph(anno+"° ANNO - CFU TOTALI: "+cfu)
                        .setBold()
                        .setFontSize(size)
                        .setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(blu)
                .setTextAlignment(TextAlignment.CENTER);
        table.addCell(intestazione);
    }

    private  void addHeaderInsegnamenti(Table table, Color color){
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
}
