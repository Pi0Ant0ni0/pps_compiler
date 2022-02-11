package it.unisannio.studenti.p.perugini.pps_compiler.Components;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import it.unisannio.studenti.p.perugini.pps_compiler.API.AttivitaDidattica;
import it.unisannio.studenti.p.perugini.pps_compiler.API.PPS;
import it.unisannio.studenti.p.perugini.pps_compiler.API.Studente;
import it.unisannio.studenti.p.perugini.pps_compiler.core.corsoDiStudio.port.ReadCorsoDiStudioPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.stream.Stream;

@Component
public class PPSMaker {

    private static int fontSize = 8;
    private static int fontSubTitleSize = 9;
    private static int fontTitleSize = 11;
    private static Logger logger = LoggerFactory.getLogger(PPSMaker.class);
    @Autowired
    private ReadCorsoDiStudioPort readCorsoDiStudioPort;

    public Document makePPS(PPS pps, OutputStream outputStream) throws MalformedURLException {
        logger.info("Sto per generare il pdf del modulo pps di: "+pps.getStudente().getEmail());
        PdfDocument pdfDocument = new PdfDocument( new PdfWriter(outputStream));
        Document document = new Document(pdfDocument);
        //metto il logo a sinistra
        ImageData imageData = ImageDataFactory.create("C:\\Users\\Pio Antonio\\Desktop\\pps_compiler\\src\\main\\resources\\logoUnisannio.png");
        Image pdfImg = new Image(imageData).setMaxWidth(60)
                .setTextAlignment(TextAlignment.LEFT);
        document.add(pdfImg);
        //metto il titolo a destra
        document.add(new Paragraph("AL MAGNIFICO RETTORE DELL'UNIVERSITA' DEGLI STUDI DEL SANNIO")
                .setBold()
                .setTextAlignment(TextAlignment.CENTER));
        //metto l'intestazione
        addIntestazione(document,pps.getStudente());


        document.add(new Paragraph("CHIEDE")
                .setBold()
                .setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("l' inserimento dei seguenti insegnamenti a completamento del proprio piano di studi così come definito dal certificato allegato," +
                "considerato parte integrante del presente:")
                .setBold()
                .setTextAlignment(TextAlignment.LEFT)
                .setFontSize(fontSubTitleSize));
        document.add(new Paragraph("A SCELTA LIBERA DELLO STUDENTE")
                .setBold()
                .setTextAlignment(TextAlignment.LEFT)
                .setFontSize(fontTitleSize));

        addTabellaInsegnamentiLiberi(document, pps.getInsegnamentiASceltaLibera());

        if(pps.getOrientamento().isPresent()){
            document.add(new Paragraph("\nda compilare solo da parte degli studenti iscritti ad un corso di studio che prevede la scelta di insegnamenti all’interno di un gruppo già definito")
                    .setBold()
                    .setTextAlignment(TextAlignment.LEFT)
                    .setUnderline()
                    .setFontSize(fontSubTitleSize));
            document.add(new Paragraph("OBBLIGATORI (INDIVIDUATI DAL CONSIGLIO DI CORSO IN UN GRUPPO DI INSEGNAMENTI)")
                    .setBold()
                    .setTextAlignment(TextAlignment.LEFT)
                    .setFontSize(fontTitleSize));

            addTabellaOrientamento(document, pps.getOrientamento().get());

        }
        //paragrafo di spazio
        document.add(new Paragraph(""));
        //data
        Table dataTable =new Table(2).setBorder(Border.NO_BORDER);
        dataTable.addCell(new Cell(1,1)
                .add(new Paragraph("Data: ")
                    .setTextAlignment(TextAlignment.LEFT)
                    .setBold())
                .setBorder(Border.NO_BORDER));
        dataTable.addCell(new Cell()
                .add(new Paragraph(String.valueOf(pps.getDataCompilazione())))
                .setBorder(Border.NO_BORDER));
        document.add(dataTable);

        //firma
        document.add(new Paragraph("Firma* _____________________")
                .setTextAlignment(TextAlignment.RIGHT));
        document.add(
                new Paragraph("*La presente istanza è sottoscritta dall’interessato in presenza del dipendente addetto ovvero sottoscritta e presentata unitamente a copia fotostatica non autenticata di un valido documento di identità del sottoscrittore.")
                .setFontSize(6)
                .setTextAlignment(TextAlignment.LEFT)
        );

        logger.info("aggiungo la revisione al documento");
        logger.info("rifiutato: "+pps.isRifiutato());
        logger.info("accettato: "+pps.isApprovato());
        logger.info("data di revisione: "+(pps.getDataVisione().isPresent()?pps.getDataVisione().get():"non c'è"));
        addTabellaRevisione(document, pps);

        document.close();
        return document;

    }

    private void addTabellaRevisione(Document document, PPS pps) {
        Table table = new Table(3)
                .setBorder(Border.NO_BORDER);
        table.addCell(new Cell(1,1)
                .add(new Paragraph("I presenti insegnamenti sono stati"))
                .add(new Paragraph("APPROVATI").setBold())
                .add(new Paragraph("dal Consiglio di Corso di Studio nella seduta del: "+( (pps.getDataVisione().isPresent() && pps.isApprovato())?pps.getDataVisione().get():"___________________")))
                .add(new Paragraph("").setTextAlignment(TextAlignment.RIGHT))
                .add(new Paragraph("\nIL PRESIDENTE").setTextAlignment(TextAlignment.RIGHT))
                .add(new Paragraph("_______________").setTextAlignment(TextAlignment.RIGHT))
                .setBorder(new SolidBorder(1))
                .setPadding(20));
        table.addCell(new Cell(1,1).setPadding(10).setBorder(Border.NO_BORDER));
        table.addCell(new Cell(1,1)
                .add(new Paragraph("I presenti insegnamenti sono stati"))
                .add(new Paragraph("RESPINTI").setBold())
                .add(new Paragraph("dal Consiglio di Corso di Studio nella seduta del: "+( (pps.getDataVisione().isPresent() && pps.isRifiutato()) ?pps.getDataVisione().get():"___________________")))
                .add(new Paragraph("").setTextAlignment(TextAlignment.RIGHT))
                .add(new Paragraph("\nIL PRESIDENTE").setTextAlignment(TextAlignment.RIGHT))
                .add(new Paragraph("_______________").setTextAlignment(TextAlignment.RIGHT))
                .setBorder(new SolidBorder(1))
                .setPadding(20));
        document.add(table);
    }

    private void addTabellaOrientamento(Document document, List<AttivitaDidattica> orientamento) {
        Table tabellaInsegnamenti = new Table(4);
        Stream.of("","CODICE INSEGNAMENTO", "DENOMINAZIONE INSEGNAMENTO", "CFU")
                .forEach(columnTitle -> {
                    Cell header = new Cell();
                    header.add(new Paragraph(columnTitle)
                            .setBold()
                            .setFontSize(fontSize));
                    tabellaInsegnamenti.addCell(header);
                });

        int i =1;
        for (AttivitaDidattica insegnamento : orientamento) {
            tabellaInsegnamenti.addCell(new Cell().add(new Paragraph(String.valueOf(i)).setFontSize(fontSize)));
            tabellaInsegnamenti.addCell(new Cell().add(new Paragraph(insegnamento.getCodiceAttivitaDidattica()).setFontSize(fontSize)));
            tabellaInsegnamenti.addCell(new Cell().add(new Paragraph(insegnamento.getDenominazioneAttivitaDidattica()).setFontSize(fontSize)));
            tabellaInsegnamenti.addCell(new Cell().add(new Paragraph(String.valueOf(insegnamento.getCfu())).setFontSize(fontSize)));
            i++;
        }

        tabellaInsegnamenti.setWidth(UnitValue.createPercentValue(100));
        document.add(tabellaInsegnamenti);
    }

    private void addTabellaInsegnamentiLiberi(Document document, List<AttivitaDidattica> insegnamentiASceltaLibera) {
        Table tabellaInsegnamenti = new Table(6);
        Stream.of("","CODICE INSEGNAMENTO", "DENOMINAZIONE INSEGNAMENTO", "CFU", "CODICE CORSO DI STUDIO", "DENOMINAZIONE CORSO DI STUDIO")
                .forEach(columnTitle -> {
                    Cell header = new Cell();
                    header.add(new Paragraph(columnTitle)
                            .setBold()
                            .setFontSize(fontSize));
                    tabellaInsegnamenti.addCell(header);
                });
        int i =1;
        for(AttivitaDidattica insegnamento: insegnamentiASceltaLibera){
            tabellaInsegnamenti.addCell(new Cell().add(new Paragraph(String.valueOf(i)).setFontSize(fontSize)));
            tabellaInsegnamenti.addCell(new Cell().add(new Paragraph(insegnamento.getCodiceAttivitaDidattica()).setFontSize(fontSize)));
            tabellaInsegnamenti.addCell(new Cell().add(new Paragraph(insegnamento.getDenominazioneAttivitaDidattica()).setFontSize(fontSize)));
            tabellaInsegnamenti.addCell(new Cell().add(new Paragraph(String.valueOf(insegnamento.getCfu())).setFontSize(fontSize)));
            tabellaInsegnamenti.addCell(new Cell().add(new Paragraph(insegnamento.getCodiceCorsoDiStudio()).setFontSize(fontSize)));
            tabellaInsegnamenti.addCell(new Cell().add(new Paragraph(readCorsoDiStudioPort.findCorsoDiStudioById(insegnamento.getCodiceCorsoDiStudio()).get().getDenominazione()).setFontSize(fontSize)));
            i++;
        }
        tabellaInsegnamenti.setWidth(UnitValue.createPercentValue(100));
        document.add(tabellaInsegnamenti);
    }




    private static void addIntestazione(Document document, Studente studente){
        Table tabellaEsterna = new Table(1);
        //contenuto interno
        Table intestazione = new Table(4);
        //nome e cognome
        intestazione.addCell(new Cell(1,1)
                .add(new Paragraph("STUDENTE")
                        .setBold())
            .setBorder(Border.NO_BORDER));
        intestazione.addCell(new Cell(1,1)
                .add(new Paragraph(studente.getNome() +" "+studente.getCognome()))
                .setBorder(Border.NO_BORDER));
        //matricola
        intestazione.addCell(new Cell(1,1)
                .add(new Paragraph("MATRICOLA").setTextAlignment(TextAlignment.RIGHT)
                        .setBold())
                .setBorder(Border.NO_BORDER));
        intestazione.addCell(new Cell(1,1)
                .add(new Paragraph(studente.getMatricola()).setTextAlignment(TextAlignment.RIGHT))
                .setBorder(Border.NO_BORDER));
        intestazione.addCell(new Cell(1,2)
                .add(new Paragraph("EMAIL").setTextAlignment(TextAlignment.LEFT)
                        .setBold())
                .setBorder(Border.NO_BORDER));
        intestazione.addCell(new Cell(1,2)
                .add(new Paragraph(studente.getEmail().getEmail()).setTextAlignment(TextAlignment.LEFT))
                .setBorder(Border.NO_BORDER));
        //corso di studio
        intestazione.addCell(new Cell(1,2)
                .add(new Paragraph("CORSO DI STUDIO IN")
                        .setBold())
                .setBorder(Border.NO_BORDER));
        intestazione.addCell(new Cell(1,2)
                .add(new Paragraph(studente.getCorsoDiStudio().getDenominazione()))
                .setBorder(Border.NO_BORDER));
        //facolta
        intestazione.addCell(new Cell(1,2)
                .add(new Paragraph("FACOLTA' DI")
                        .setBold())
                .setBorder(Border.NO_BORDER));
        intestazione.addCell(new Cell(1,2)
                .add(new Paragraph(studente.getCorsoDiStudio().getDenominazioneFacolta()))
                .setBorder(Border.NO_BORDER));
        tabellaEsterna.addCell(new Cell()
                .add(intestazione)
                .setPadding(0)
                .setBorder(new SolidBorder(ColorConstants.BLACK, 2)));
        tabellaEsterna.setWidth(UnitValue.createPercentValue(100));
        document.add(tabellaEsterna);

    }
}
