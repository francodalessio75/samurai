/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dalessio.samurai;

import static com.dalessio.samurai.ITextDeliveryNote.font;
import com.dps.dbi.DbResult;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceCmyk;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.CanvasArtifact;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagutils.IAccessibleElement;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.Background;
import com.itextpdf.layout.property.BorderRadius;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import static com.itextpdf.layout.property.UnitValue.POINT;
import com.itextpdf.layout.property.VerticalAlignment;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.ParagraphRenderer;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import static com.dalessio.samurai.CellBorderType.*;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.kernel.utils.PdfMerger;
import com.itextpdf.kernel.pdf.PdfReader;

/**
 *
 * @author Franco D'Alessio
 */
public class PdfPrinter
{
    public static final String LOGO = "C:\\AppResources\\Samurai\\Logo\\logoDuesse.jpg";
   
    private static final int RIGHT_MARGIN = 15;

    private static final int LEFT_MARGIN = 30;

    private static final int BOTTOM_MARGIN = 10;

    private static final float PAGE_WIDTH = PageSize.A4.getWidth();

    private static final float MIDDLE = LEFT_MARGIN + ( PAGE_WIDTH - RIGHT_MARGIN - LEFT_MARGIN ) / 2;

    private static final int HORIZONTAL_SPACE = 10;

    private static final int CUSTOMER_DATA_TABLE_HEIGHT = 100;

    private static final int CUSTOMER_DATA_TABLE_WIDTH = 235;

    private static final int SHADOW_THICKNESS = 3;
    
    private static final int DOCUMENT_TOP_MARGIN = ( int ) PageSize.A4.getHeight()-497;
    
    private static final int DOCUMENT_BOTTOM_MARGIN = 120;
    
    private static final Color BACKGROUND_GRAY = new DeviceRgb(240, 240, 240);
    
    private static final Color SHADOW_GRAY = new DeviceRgb(80, 80, 80);
    
    //currency utility
    CurrencyUtility cu = CurrencyUtility.getCurrencyUtilityInstance();
    //data access object
   
    public void createDeliveryNotePdf(Long deliveryNote_id, DbResult deliveryNote, DbResult deliveryNoteRows) throws IOException
    { 
        //font declaration and file creation pdf create method calling
        font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
        
        //files names
        final String SRC0 = Config.DDT_DIR+"DDT_"+deliveryNote_id+"_"+"0.pdf";
                
        final String SRC1 = Config.DDT_DIR+"DDT_"+deliveryNote_id+"_"+"1.pdf";
                
        final String SRC2 = Config.DDT_DIR+"DDT_"+deliveryNote_id+"_"+"2.pdf";
                
        final String DEST = Config.DDT_DIR+"DDT_"+deliveryNote_id+".pdf";
        
        //pdf document with destination file
        PdfDocument destPdf = new PdfDocument(new PdfWriter(DEST));
        
        //creates  files
        File file = new File( DEST );
        File tmpFile0 = new File( SRC0 );
        File tmpFile1 = new File( SRC1 );
        File tmpFile2 = new File( SRC2 );
        
        //makes directories
        file.getParentFile().mkdirs();
        tmpFile0.getParentFile().mkdirs();
        tmpFile1.getParentFile().mkdirs();
        tmpFile2.getParentFile().mkdirs();
        
        //instantiates a temporary pdfDocument
        PdfDocument tmpPdf = new PdfDocument(new PdfWriter(tmpFile0));
        //instance of utility class to print pages numbers that implements the IEventHandler
        PdfPageNumbersWriter pageNumberWriter = new PdfPageNumbersWriter(tmpPdf, ( float )PAGE_WIDTH-RIGHT_MARGIN-40, (float) BOTTOM_MARGIN);
        
        //creates three copies : DUESSE, Transporter, Customer
        for( int copyNumber = 0; copyNumber < 3; copyNumber++ )
        {    
            try
            {
                //initialiazes the pdf document with the correct temporary file
                if( copyNumber == 0 )
                    tmpPdf = new PdfDocument(new PdfWriter(tmpFile0));
                else if( copyNumber == 1 ) 
                    tmpPdf = new PdfDocument(new PdfWriter(tmpFile1));
                else if( copyNumber == 2 ) 
                    tmpPdf = new PdfDocument(new PdfWriter(tmpFile2));
                
                //instance of utility class to print pages numbers that implements the IEventHandler
                pageNumberWriter = new PdfPageNumbersWriter(tmpPdf, ( float )PAGE_WIDTH-RIGHT_MARGIN-40, (float) BOTTOM_MARGIN);
                
                //event handler binding, each end of page places a placeholder
                tmpPdf.addEventHandler(PdfDocumentEvent.END_PAGE, pageNumberWriter);
                
                //adds the event handler necessary to manage header and footer infact takes as event parameter END_PAGE
                tmpPdf.addEventHandler(PdfDocumentEvent.END_PAGE, new DeliveryNoteMyEventHandler(deliveryNote, copyNumber));

                //adds page to the pdf document this docuemtn manages header and footer
                PageSize ps = PageSize.A4;
                PdfPage page = tmpPdf.addNewPage(ps);

                //Inititlaize the document that is the content between header and footer
                Document document = new Document(tmpPdf,ps,true);
                document.setMargins(DOCUMENT_TOP_MARGIN, 0, DOCUMENT_BOTTOM_MARGIN, 0);

                //THE TABLE OF DELIVERYNOTE ROWS

                //sets table dimensions
                float[] columnWidths = {70, 70, 415};
                Table table = new Table(columnWidths);
                table.setWidth(555);
                table.setMarginLeft(LEFT_MARGIN);
                //iterates deliveryNoteRows to fill the table
                for( int i = 0; i < deliveryNoteRows.rowsCount(); i++)
                {
                    Cell cell = new Cell();
                    cell.add(new Paragraph( deliveryNoteRows.getString(i,"code")).setTextAlignment(TextAlignment.CENTER).setFontSize(8)).setBold();
                    cell.setBorder(Border.NO_BORDER);
                    cell.setBorderBottom(new SolidBorder(.5f));
                    cell.setMinWidth(70);
                    cell.setMinHeight(13);
                    table.addCell(cell);
                    cell = new Cell();
                    cell.add(new Paragraph( deliveryNoteRows.getInteger(i,"quantity").toString()).setTextAlignment(TextAlignment.CENTER).setFontSize(8)).setBold();
                    cell.setBorder(Border.NO_BORDER);
                    cell.setBorderBottom(new SolidBorder(.5f));
                    cell.setMinWidth(70);
                    table.addCell(cell);
                    cell = new Cell();
                    cell.add(new Paragraph( deliveryNoteRows.getString(i,"description")).setMarginLeft(10).setFontSize(8)).setBold();
                    cell.setBorder(Border.NO_BORDER);
                    cell.setBorderBottom(new SolidBorder(.5f));
                    cell.setMinWidth(70);
                    table.addCell(cell);

                }   
                //adds the table to the document 
                document.add(table);
                //places pages number
                pageNumberWriter.writeTotal(tmpPdf);
                //closes the document
                document.close();
            }
            catch(IOException | RuntimeException ex)
            {
                System.err.println("EXCEPTION CREATING DELIVERY NOTE : "+ex);
                ex.printStackTrace(System.err);
            }
        }//end of for cycle
        
        //MERGES FILES AND CLOSES DOCUMENT
   
        //the merger
        PdfMerger merger = new PdfMerger(destPdf);
        
        //Add pages from the first document
        PdfDocument firstSourcePdf = new PdfDocument(new PdfReader(SRC0));
        merger.merge(firstSourcePdf, 1, firstSourcePdf.getNumberOfPages());
        
        //Add pages from the second document
        PdfDocument secondSourcePdf = new PdfDocument(new PdfReader(SRC1));
        merger.merge(secondSourcePdf, 1, secondSourcePdf.getNumberOfPages());
        
        //Add pages from the third document
        PdfDocument thirdSourcePdf = new PdfDocument(new PdfReader(SRC2));
        merger.merge(thirdSourcePdf, 1, thirdSourcePdf.getNumberOfPages());
        
        //closes pdf documets
        firstSourcePdf.close();
        secondSourcePdf.close();
        thirdSourcePdf.close();
        destPdf.close();
        
        //destroids useless files
        tmpFile0.delete();
        tmpFile1.delete();
        tmpFile2.delete();
    }
    
    public void createInvoicePdf(Long invoice_id, DbResult invoice, DbResult invoiceRows) throws Exception
    {
        //data access object
        DataAccessObject dao = new DataAccessObject();
        //font declaration and file creation pdf create method calling
        font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
        //currency utility
        CurrencyUtility cu = CurrencyUtility.getCurrencyUtilityInstance();
        //related deliverynotes
        DbResult relatedDeliveryNotes = dao.getInvoiceDeliveryNotes(invoice_id);
        //related deliverynotesRows
        DbResult relatedDeliveryNotesRows = dao.getInvoiceRelatedDeliveryNotesRows(invoice_id);
        
        File file = new File(Config.INVOICES_DIR+"INVOICE_"+invoice_id+".pdf");
        
        file.getParentFile().mkdirs();

        //Initialize PDF document
        PdfDocument pdf = new PdfDocument(new PdfWriter(file));
       
        //adds the event handler necessary to manage heder and footer infact takes as event parameter END_PAGE
        pdf.addEventHandler(PdfDocumentEvent.END_PAGE, new InvoiceMyEventHandler(invoice));
            
        //adds page to the pdf document this docuemtn manages header and footer
        PageSize ps = PageSize.A4;
        PdfPage page = pdf.addNewPage(ps);
        
        //Inititlaize the document that is the content between header and footer
        Document document = new Document(pdf,ps,true);
        document.setMargins(290, 0, 137, 25);
        
        
        //THE TABLE OF DELIVERYNOTE ROWS
        
        //sets table dimensions
        float[] columnWidths = {70, 70, 345,70};
        Table table = new Table(columnWidths);
        table.setWidth(555);
        
        String relatedDeliveryNoteData = "";
        
        //iterates invoiceRows to fill the table
        for( int i = 0; i < invoiceRows.rowsCount(); i++)
        {
            //reset
            relatedDeliveryNoteData = "";
            
            //retrieves related deliveryNote data
            if( invoiceRows.getLong( i,"deliveryNoteRow_id") != null )
            {
                relatedDeliveryNoteData = dao.getDeliveryNoteNumberAndDate(dao.getDeliveryNoteByDeliveryNoteRowId(invoiceRows.getLong( i,"deliveryNoteRow_id")).getLong("deliveryNote_id"));
            }
            table.addCell(new Cell().add(new Paragraph( invoiceRows.getString(i,"code"))));
            table.addCell(new Cell().add(new Paragraph( invoiceRows.getInteger(i,"quantity").toString())));
            table.addCell( new Cell().add( new Paragraph(invoiceRows.getString(i,"description") + relatedDeliveryNoteData)));
            table.addCell(new Cell().add(new Paragraph( cu.getCurrency( invoiceRows.getDouble(i,"amount")))));
            
        }
       
        //adds the table to the document then close the docuemnt and the pdf document
        document.add(table);
        //Close document
     
        document.close();
    }
    
    public class DeliveryNoteMyEventHandler implements IEventHandler 
    {
        DbResult deliveryNote;
        //table cell instance
        Cell cell;
        //copy text 
        String copyText;
        
        public DeliveryNoteMyEventHandler(DbResult deliveryNote, int copyNumber) 
        {
            this.deliveryNote = deliveryNote;
            this.cell = new Cell();
            if( copyNumber == 0 )
                this.copyText = "COPIA PER DUESSE";
            else if ( copyNumber == 1 )
                this.copyText = "COPIA PER VETTORE";
            else if ( copyNumber == 2 )
                this.copyText = "COPIA PER CLIENTE";
        }
    
        @Override
        public void handleEvent(Event event) 
        {
            
            try{
                //fonts declaring
                PdfFont font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);

                PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
                PdfDocument pdfDoc = docEvent.getDocument();

                //the handler takes each page of the document
                PdfPage page = docEvent.getPage();
                
                //creates pdf canvas
                PdfCanvas pdfCanvas = new PdfCanvas(page);
                
                /*****    HEADER ***************************************************/
            
                //adds the logo
                try
                {
                    pdfCanvas.addImage(ImageDataFactory.create(LOGO), LEFT_MARGIN, 765, 135, true);
                }catch(MalformedURLException  ex){}
                
                //adds copy and document model
                Rectangle rectCopyDestination = new Rectangle(PAGE_WIDTH-RIGHT_MARGIN-90, 780, 90, 30);
                Text  txtCopy= new Text(copyText).setFont(font).setFontSize(8);
                Text txtMod = new Text("\nMod. 01-18 Rev.0").setFont(font).setFontSize(8).setBold();
                Paragraph prgCopy = new Paragraph().add(txtCopy).add(txtMod);
                pdfCanvas.rectangle(rectCopyDestination);
                Canvas cnvCopyDestination = new Canvas(pdfCanvas, pdfDoc, rectCopyDestination);
                cnvCopyDestination.add(prgCopy);
                
                //adds page number 
                Rectangle rectPageNumber = new Rectangle(PAGE_WIDTH-RIGHT_MARGIN-80, BOTTOM_MARGIN , 40, 20);
                Text txtPages = new Text("Pagina " + pdfDoc.getPageNumber(page) + " di " ).setFont(font).setFontSize(8);
                Paragraph prgPages = new Paragraph().add(txtPages);
                pdfCanvas.rectangle(rectPageNumber);
                Canvas cnvPageNumber = new Canvas(pdfCanvas, pdfDoc, rectPageNumber);
                cnvPageNumber.add(prgPages); 
                
                //CREATES TABLE 1X2 FOR COMPANY DATA
                //first cell content ( data label )
                Paragraph prgCompanyData_1 = new Paragraph().setTextAlignment(TextAlignment.LEFT).setVerticalAlignment(VerticalAlignment.BOTTOM);
                Text txtCompanyData_1 = new Text("Duesse Service s.r.l.\n").setFont(font).setFontSize(7).setBold();
                Text txtCompanyData_2 = new Text("Via Agusta,51\n21017 Samarate (VA) Italy").setFont(font).setFontSize(7);
                prgCompanyData_1.add(txtCompanyData_1).add(txtCompanyData_2);
                //second cell content : document number and date
                Paragraph prgCompanyData_2 = new Paragraph().setTextAlignment(TextAlignment.LEFT);
                Text txtCompanyData_3 = new Text("Tel. +39 0331 220913\nFAx +39 0331 220914\nCos. Fisc. - P.IVA 02677820124").setFont(font).setFontSize(7);
                prgCompanyData_2.add(txtCompanyData_3);
                //table
                Table tblCompanyData = new Table(new float[] {1, 1});
                cell = new Cell().add(prgCompanyData_1).setTextAlignment(TextAlignment.CENTER);
                cell.setBorder(Border.NO_BORDER);
                //cell.setNextRenderer(new DrawCellUtil( cell, new CellBorderDrawer[]{ SOLID, SOLID, SOLID, SOLID }));
                tblCompanyData.addCell(cell);
                cell = new Cell().add(prgCompanyData_2).setTextAlignment(TextAlignment.CENTER);
                cell.setBorder(Border.NO_BORDER);
                //cell.setNextRenderer(new DrawCellUtil( cell, new CellBorderDrawer[]{ SOLID, SOLID, SOLID, SOLID }));
                tblCompanyData.addCell(cell);
                tblCompanyData.setFixedPosition(LEFT_MARGIN, 735, 200);
                tblCompanyData.setMaxHeight(53);
                
                new Canvas(pdfCanvas, pdfDoc, new Rectangle(LEFT_MARGIN, 735, 200, 53)).add(tblCompanyData);
                
                //Customer  vertical label
                //Customer label
                Rectangle rectCustomerLabel = new Rectangle(LEFT_MARGIN, 740-HORIZONTAL_SPACE-CUSTOMER_DATA_TABLE_HEIGHT, 20, CUSTOMER_DATA_TABLE_HEIGHT);
                Text txtCustomerLabel = new Text("Destinatario" ).setFont(font).setFontSize(10).setBold().setItalic().setStrokeColor(Color.BLACK);
                Paragraph prgCustomerLabel = new Paragraph().add(txtCustomerLabel).setRotationAngle(-((2 * Math.PI) - Math.PI/ 2)).setPaddingRight(15);
                pdfCanvas.rectangle(rectCustomerLabel);
                Canvas cnvCostumerLabel = new Canvas(pdfCanvas, pdfDoc, rectCustomerLabel);
                cnvCostumerLabel.add(prgCustomerLabel);
                
                //Destination  vertical label
                //Destination label
                Rectangle rectDestinationLabel = new Rectangle(PAGE_WIDTH - RIGHT_MARGIN - CUSTOMER_DATA_TABLE_WIDTH - 25, 740-HORIZONTAL_SPACE-CUSTOMER_DATA_TABLE_HEIGHT, 20, CUSTOMER_DATA_TABLE_HEIGHT);
                Text txtDestinationLabel = new Text("Destinazione" ).setFont(font).setFontSize(10).setBold().setItalic().setStrokeColor(Color.BLACK);
                Paragraph prgDestinationLabel = new Paragraph().add(txtDestinationLabel).setRotationAngle(-((2 * Math.PI) - Math.PI/ 2)).setPaddingRight(15);
                pdfCanvas.rectangle(rectDestinationLabel);
                Canvas cnvDestinationLabel = new Canvas(pdfCanvas, pdfDoc, rectDestinationLabel);
                cnvDestinationLabel.add(prgDestinationLabel);
                
                pdfCanvas.newPath();
                
                //ROUNDED SHADOW TABLE 4x4 WITH CUSTOMER DATA
                //cells contents
                //[0][0] 
                Text txtCustomerNameVerticalLabel = new Text("Spett").setFont(font).setFontSize(12).setBold();
                Paragraph prgCustomerNameVerticalLabel = new Paragraph().add(txtCustomerNameVerticalLabel).setRotationAngle(-((2 * Math.PI) - Math.PI/ 2));
                //[0][1]
                Text txtCustomerName = new Text( deliveryNote.getString("denomination") ).setFont(font).setFontSize(12).setBold();
                Paragraph prgCustomerNameVertical = new Paragraph().add(txtCustomerName);
                //[1][0] rowsapan 2
                Text txtCustomerAddressVerticalLabel = new Text("Via").setFont(font).setFontSize(12).setBold();
                Paragraph prgCustomerAddressVerticalLabel = new Paragraph().add(txtCustomerAddressVerticalLabel).setRotationAngle(-((2 * Math.PI) - Math.PI/ 2));
                //[1][1]
                Text txtCustomerAddress = new Text( deliveryNote.getString("address") + ", " + deliveryNote.getString("houseNumber") ).setFont(font).setFontSize(12).setBold();
                Paragraph prgCustomerAddress = new Paragraph().add(txtCustomerAddress);
                //[2][0] rowsapan 2
                Text txtCustomerCityVerticalLabel = new Text("Città").setFont(font).setFontSize(12).setBold();
                Paragraph prgCustomerCityVerticalLabel = new Paragraph().add(txtCustomerCityVerticalLabel).setRotationAngle(-((2 * Math.PI) - Math.PI/ 2));
                //[2][1]
                Text txtCustomerCity = new Text( deliveryNote.getString("postalCode") + ", " + deliveryNote.getString("city") ).setFont(font).setFontSize(12).setBold();
                Paragraph prgCustomerCity = new Paragraph().add(txtCustomerCity);
                //Table instantiation
                Table tblCustomerData = new Table(new UnitValue[]{new UnitValue(POINT,20),new UnitValue(POINT,210),new UnitValue(POINT,15)});
                //cells adding
                //[0][0]
                cell = new Cell().add(prgCustomerNameVerticalLabel).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddings(10, 0, 0, 0).setVerticalAlignment(VerticalAlignment.BOTTOM);
                tblCustomerData.addCell(cell);
                //[0][1]
                cell = new Cell().add(prgCustomerNameVertical).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddings(10, 0, 0, 0).setVerticalAlignment(VerticalAlignment.BOTTOM);
                cell.setNextRenderer(new DrawCellUtil( cell, new CellBorderDrawer[]{ null, null, THIN_SOLID_DARK_GRAY, null }));/*underlined*/
                tblCustomerData.addCell(cell);
                //[0][2]
                tblCustomerData.addCell(new Cell().setBorder(Border.NO_BORDER));
                //[1][0]
                cell = new Cell().add(prgCustomerAddressVerticalLabel).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddings(10, 0, 0, 0).setVerticalAlignment(VerticalAlignment.BOTTOM);
                tblCustomerData.addCell(cell);
                //[1][1]
                cell = new Cell().add(prgCustomerAddress).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddings(10, 0, 0, 0).setVerticalAlignment(VerticalAlignment.BOTTOM);
                cell.setNextRenderer(new DrawCellUtil( cell, new CellBorderDrawer[]{ null, null, THIN_SOLID_DARK_GRAY, null }));/*underlined*/
                tblCustomerData.addCell(cell);
                //[1][2]
                tblCustomerData.addCell(new Cell().setBorder(Border.NO_BORDER));
                //[2][0]
                cell = new Cell().add(prgCustomerCityVerticalLabel).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddings(10, 0, 0, 0).setVerticalAlignment(VerticalAlignment.BOTTOM);
                tblCustomerData.addCell(cell);
                //[2][1]
                cell = new Cell().add(prgCustomerCity).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddings(10, 0, 0, 0).setVerticalAlignment(VerticalAlignment.BOTTOM);
                cell.setNextRenderer(new DrawCellUtil( cell, new CellBorderDrawer[]{ null, null, THIN_SOLID_DARK_GRAY, null }));/*underlined*/
                tblCustomerData.addCell(cell);
                //[2][2]
                tblCustomerData.addCell(new Cell().setBorder(Border.NO_BORDER));
                //empty row
                tblCustomerData.addCell(new Cell().setBorder(Border.NO_BORDER)).addCell(new Cell().setBorder(Border.NO_BORDER)).addCell(new Cell().setBorder(Border.NO_BORDER));
             
                //background color and dimensions assigning
                tblCustomerData.setBackgroundColor( BACKGROUND_GRAY).setMinHeight(CUSTOMER_DATA_TABLE_HEIGHT).setMaxHeight(CUSTOMER_DATA_TABLE_HEIGHT);
                //renderer assigning
                tblCustomerData.setNextRenderer(new RoundedColouredTable(tblCustomerData));
                
                //the shadow of the table 
                Table sdwTblCustomerData = new Table(new UnitValue[]{new UnitValue(POINT,245)});
                sdwTblCustomerData.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
                //background color and dimensions assigning
                sdwTblCustomerData.setBackgroundColor(SHADOW_GRAY).setMinHeight(CUSTOMER_DATA_TABLE_HEIGHT).setMaxHeight(CUSTOMER_DATA_TABLE_HEIGHT);
                //renderer assigning
                sdwTblCustomerData.setNextRenderer(new RoundedColouredTable(sdwTblCustomerData));
                
                //shows tables
                new Canvas(pdfCanvas, pdfDoc, new Rectangle(LEFT_MARGIN + 20 + SHADOW_THICKNESS, 740-HORIZONTAL_SPACE-CUSTOMER_DATA_TABLE_HEIGHT - SHADOW_THICKNESS, CUSTOMER_DATA_TABLE_WIDTH, CUSTOMER_DATA_TABLE_HEIGHT)).add(sdwTblCustomerData);
                new Canvas(pdfCanvas, pdfDoc, new Rectangle(LEFT_MARGIN + 20, 740-HORIZONTAL_SPACE-CUSTOMER_DATA_TABLE_HEIGHT, CUSTOMER_DATA_TABLE_WIDTH, CUSTOMER_DATA_TABLE_HEIGHT)).add(tblCustomerData);
 
                //ROUNDED SHADOW TABLE 4x4 WITH DESTINATION DATA
                //cells contents
                //[0][0] 
                Text txtDestinationNameVerticalLabel = new Text("Spett").setFont(font).setFontSize(12).setBold();
                Paragraph prgDestinationNameVerticalLabel = new Paragraph().add(txtDestinationNameVerticalLabel).setRotationAngle(-((2 * Math.PI) - Math.PI/ 2));
                //[0][1]
                Text txtDestinationName = new Text( deliveryNote.getString("destDenomination") ).setFont(font).setFontSize(12).setBold();
                Paragraph prgDestinationNameVertical = new Paragraph().add(txtDestinationName);
                //[0][2]
                //empty cell
                //[1][0] rowsapan 2
                Text txtDestinationAddressVerticalLabel = new Text("Via").setFont(font).setFontSize(12).setBold();
                Paragraph prgDestinationAddressVerticalLabel = new Paragraph().add(txtDestinationAddressVerticalLabel).setRotationAngle(-((2 * Math.PI) - Math.PI/ 2));
                //[1][1]
                Text txtDestinationAddress = new Text( deliveryNote.getString("destAddress") + " " + deliveryNote.getString("destHouseNumber") ).setFont(font).setFontSize(12).setBold();
                Paragraph prgDestinationAddress = new Paragraph().add(txtDestinationAddress);
                //[1][2]
                //empty cell
                //[2][0] rowsapan 2
                Text txtDestinationCityVerticalLabel = new Text("Città").setFont(font).setFontSize(12).setBold();
                Paragraph prgDestinationCityVerticalLabel = new Paragraph().add(txtDestinationCityVerticalLabel).setRotationAngle(-((2 * Math.PI) - Math.PI/ 2));
                //[2][1]
                Text txtDestinationCity = new Text( deliveryNote.getString("destPostalCode") + " " + deliveryNote.getString("destCity") ).setFont(font).setFontSize(12).setBold();
                Paragraph prgDestinationCity = new Paragraph().add(txtDestinationCity);
                //[2][2]
                //empty cell
                //[3][0] - [3][1] - [3][2]
                //empy cell
                //Table intantiation
                Table tblDestinationData = new Table(new UnitValue[]{new UnitValue(POINT,20),new UnitValue(POINT,210),new UnitValue(POINT,15)});
                //cells adding
                cell = new Cell().add(prgDestinationNameVerticalLabel).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddings(10, 0, 0, 0).setVerticalAlignment(VerticalAlignment.BOTTOM);
                tblDestinationData.addCell(cell);
                
                cell = new Cell().add(prgDestinationNameVertical).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddings(10, 0, 0, 0).setVerticalAlignment(VerticalAlignment.BOTTOM);
                cell.setNextRenderer(new DrawCellUtil( cell, new CellBorderDrawer[]{ null, null, THIN_SOLID_DARK_GRAY, null }));/*underlined*/
                tblDestinationData.addCell(cell);
                
                tblDestinationData.addCell(new Cell().setBorder(Border.NO_BORDER));
                
                cell = new Cell().add(prgDestinationAddressVerticalLabel).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddings(10, 0, 0, 0).setVerticalAlignment(VerticalAlignment.BOTTOM);
                tblDestinationData.addCell(cell);
                
                cell = new Cell().add(prgDestinationAddress).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddings(10, 0, 0, 0).setVerticalAlignment(VerticalAlignment.BOTTOM);
                cell.setNextRenderer(new DrawCellUtil( cell, new CellBorderDrawer[]{ null, null, THIN_SOLID_DARK_GRAY, null }));/*underlined*/
                tblDestinationData.addCell(cell);
                
                tblDestinationData.addCell(new Cell().setBorder(Border.NO_BORDER));
                
                cell = new Cell().add(prgDestinationCityVerticalLabel).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddings(10, 0, 0, 0).setVerticalAlignment(VerticalAlignment.BOTTOM);
                tblDestinationData.addCell(cell);
                
                cell = new Cell().add(prgDestinationCity).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddings(10, 10, 0, 0).setVerticalAlignment(VerticalAlignment.BOTTOM);
                cell.setNextRenderer(new DrawCellUtil( cell, new CellBorderDrawer[]{ null, null, THIN_SOLID_DARK_GRAY, null }));/*underlined*/
                tblDestinationData.addCell(cell);
                
                tblDestinationData.addCell(new Cell().setBorder(Border.NO_BORDER));
                
                tblDestinationData.addCell(new Cell().setBorder(Border.NO_BORDER));tblDestinationData.addCell(new Cell().setBorder(Border.NO_BORDER));tblDestinationData.addCell(new Cell().setBorder(Border.NO_BORDER));
                
                //background color and dimensions assigning
                tblDestinationData.setBackgroundColor(Color.WHITE).setMinHeight(CUSTOMER_DATA_TABLE_HEIGHT).setMaxHeight(CUSTOMER_DATA_TABLE_HEIGHT);
                //renderer assigning
                tblDestinationData.setNextRenderer(new RoundedColouredTable(tblDestinationData));
                
                
                //the shadow of the table 
                Table sdwTblDestinationData = new Table(new UnitValue[]{new UnitValue(POINT,CUSTOMER_DATA_TABLE_WIDTH)});
                sdwTblDestinationData.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
                //background color and dimensions assigning
                sdwTblDestinationData.setBackgroundColor(SHADOW_GRAY).setMinHeight(CUSTOMER_DATA_TABLE_HEIGHT).setMaxHeight(CUSTOMER_DATA_TABLE_HEIGHT);
                //renderer assigning
                sdwTblDestinationData.setNextRenderer(new RoundedColouredTable(sdwTblDestinationData));
               
                //shows tables
                new Canvas(pdfCanvas, pdfDoc, new Rectangle(PAGE_WIDTH - RIGHT_MARGIN - CUSTOMER_DATA_TABLE_WIDTH, 740-HORIZONTAL_SPACE-CUSTOMER_DATA_TABLE_HEIGHT - SHADOW_THICKNESS, CUSTOMER_DATA_TABLE_WIDTH, CUSTOMER_DATA_TABLE_HEIGHT)).add(sdwTblDestinationData);
                new Canvas(pdfCanvas, pdfDoc, new Rectangle(PAGE_WIDTH - RIGHT_MARGIN - CUSTOMER_DATA_TABLE_WIDTH - SHADOW_THICKNESS, 740-HORIZONTAL_SPACE-CUSTOMER_DATA_TABLE_HEIGHT, CUSTOMER_DATA_TABLE_WIDTH, CUSTOMER_DATA_TABLE_HEIGHT)).add(tblDestinationData);
                
                //SHADOW EFFECT FOR RIGHT AND LEFT TRANSPORT DATA TABLES, OBTAINED WITH THREE LAYERS:
                //1) DARK GRAY DIV HAVING WIDTH = PAGE WIDTH. HEIGHT = TRANSPORT DATA TABLES HEIGHT. Y-POSITION = TRANSPORT DATA TABLES MINUS SHADOW THICKNESS;
                //2) WITHE DIV HAVING SAME DIMENSIONS OF TRANSPORT DATA TABLES JUST UNDER THEM;
                //3) TRANSPORT DATA TABLES
                //first layer
                Div div_0 = new Div();
                div_0.setBackgroundColor(SHADOW_GRAY).setHeight(CUSTOMER_DATA_TABLE_HEIGHT-10);
                div_0.setFixedPosition(LEFT_MARGIN, 740 - HORIZONTAL_SPACE - CUSTOMER_DATA_TABLE_HEIGHT +10 - HORIZONTAL_SPACE - CUSTOMER_DATA_TABLE_HEIGHT - SHADOW_THICKNESS, PAGE_WIDTH-RIGHT_MARGIN-LEFT_MARGIN);
                div_0.setBorderRadius(new BorderRadius(5));
                new Canvas(pdfCanvas, pdfDoc, new Rectangle(LEFT_MARGIN, 740 - HORIZONTAL_SPACE - CUSTOMER_DATA_TABLE_HEIGHT +10 - HORIZONTAL_SPACE - CUSTOMER_DATA_TABLE_HEIGHT - SHADOW_THICKNESS, PAGE_WIDTH-RIGHT_MARGIN-LEFT_MARGIN,CUSTOMER_DATA_TABLE_HEIGHT-10)).add(div_0);
                //second layer
                Div div_1 = new Div();
                div_1.setBackgroundColor(Color.WHITE).setHeight(CUSTOMER_DATA_TABLE_HEIGHT-10);
                div_1.setFixedPosition(LEFT_MARGIN, 740 - HORIZONTAL_SPACE - CUSTOMER_DATA_TABLE_HEIGHT +10 - HORIZONTAL_SPACE - CUSTOMER_DATA_TABLE_HEIGHT, PAGE_WIDTH-RIGHT_MARGIN-LEFT_MARGIN-SHADOW_THICKNESS);
                div_1.setBorderRadius(new BorderRadius(5));
                new Canvas(pdfCanvas, pdfDoc, new Rectangle(LEFT_MARGIN, 740 - HORIZONTAL_SPACE - CUSTOMER_DATA_TABLE_HEIGHT +10 - HORIZONTAL_SPACE - CUSTOMER_DATA_TABLE_HEIGHT, PAGE_WIDTH-RIGHT_MARGIN-LEFT_MARGIN-SHADOW_THICKNESS,CUSTOMER_DATA_TABLE_HEIGHT-10)).add(div_1);
                
                //LEFT TRANSPORT DATA TABLE 5x3
                //cells contents
                //[0][0] 
                Text txtTransportResponsableLabel = new Text("Trasporto a cura del" ).setFont(font).setFontSize(12).setBold();
                Paragraph prgTransportResponsableLabel = new Paragraph().add(txtTransportResponsableLabel);
                //[0][2]
                Text txtTransportResponsable = new Text( deliveryNote.getString("transportResponsable") ).setFont(font).setFontSize(14).setBold();
                Paragraph prgTransportResponsable = new Paragraph().add(txtTransportResponsable);
                //[1][0] 
                Text txtGoodsExteriorAspectLabel = new Text("Aspetto esteriore dei beni" ).setFont(font).setFontSize(10).setBold();
                Paragraph prgGoodsExteriorAspectLabel = new Paragraph().add(txtGoodsExteriorAspectLabel);
                //[1][2]
                Text txtGoodsExteriorAspect = new Text( deliveryNote.getString("goodsExteriorAspect") ).setFont(font).setFontSize(14).setBold();
                Paragraph prgGoodsExteriorAspect = new Paragraph().add(txtGoodsExteriorAspect);
                //[2][0] 
                Text txtWeightLabel = new Text("Peso Kg" ).setFont(font).setFontSize(12).setBold();
                Paragraph prgWeightLabel = new Paragraph().add(txtWeightLabel);
                //[2][2]
                Text txtWeight = new Text( (deliveryNote.getDouble("weight") == null || deliveryNote.getDouble("weight") == 0 ) ? "" : deliveryNote.getDouble("weight").toString()).setFont(font).setFontSize(14).setBold();
                Paragraph prgWeight = new Paragraph().add(txtWeight);
                
                //Table instantiation
                Table tblLeftTransportData = new Table(new UnitValue[]{new UnitValue(POINT,115),new UnitValue(POINT,15),new UnitValue(POINT,115)});
                //cells adding
                //[0][0]
                cell = new Cell().add(prgTransportResponsableLabel).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).setPaddings(10, 0, 0, 0).setVerticalAlignment(VerticalAlignment.BOTTOM);
                cell.setNextRenderer(new DrawCellUtil( cell, new CellBorderDrawer[]{ null, null, THIN_SOLID_DARK_GRAY, null }));/*underlined*/
                tblLeftTransportData.addCell(cell);
                //[0][1] epty cell
                tblLeftTransportData.addCell(new Cell().setBorder(Border.NO_BORDER));
                //[0][2]
                cell = new Cell().add(prgTransportResponsable).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddings(10, 0, 0, 0).setVerticalAlignment(VerticalAlignment.BOTTOM);
                cell.setNextRenderer(new DrawCellUtil( cell, new CellBorderDrawer[]{ null, THIN_SOLID_DARK_GRAY, THIN_SOLID_DARK_GRAY, null }));
                tblLeftTransportData.addCell(cell);
                //EMPTY ROW [1][X]
                tblLeftTransportData.addCell(new Cell().setBorder(Border.NO_BORDER));tblLeftTransportData.addCell(new Cell().setBorder(Border.NO_BORDER));tblLeftTransportData.addCell(new Cell().setBorder(Border.NO_BORDER));
                //[2][0]
                cell = new Cell().add(prgGoodsExteriorAspectLabel).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).setPaddings(10, 0, 0, 0).setVerticalAlignment(VerticalAlignment.BOTTOM);
                cell.setNextRenderer(new DrawCellUtil( cell, new CellBorderDrawer[]{ null, null, THIN_SOLID_DARK_GRAY, null }));/*underlined*/
                tblLeftTransportData.addCell(cell);
                //[2][1] epty cell
                tblLeftTransportData.addCell(new Cell().setBorder(Border.NO_BORDER));
                //[2][2]
                cell = new Cell().add(prgGoodsExteriorAspect).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddings(10, 0, 0, 0).setVerticalAlignment(VerticalAlignment.BOTTOM);
                cell.setNextRenderer(new DrawCellUtil( cell, new CellBorderDrawer[]{ null, THIN_SOLID_DARK_GRAY, THIN_SOLID_DARK_GRAY, null }));
                tblLeftTransportData.addCell(cell);
                //EMPTY ROW [3][X]
                tblLeftTransportData.addCell(new Cell().setBorder(Border.NO_BORDER));tblLeftTransportData.addCell(new Cell().setBorder(Border.NO_BORDER));tblLeftTransportData.addCell(new Cell().setBorder(Border.NO_BORDER));
                //[4][0]
                cell = new Cell().add(prgWeightLabel).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).setPaddings(10, 0, 0, 0).setVerticalAlignment(VerticalAlignment.BOTTOM);
                cell.setNextRenderer(new DrawCellUtil( cell, new CellBorderDrawer[]{ null, null, THIN_SOLID_DARK_GRAY, null }));/*underlined*/
                tblLeftTransportData.addCell(cell);
                //[4][1] epty cell
                tblLeftTransportData.addCell(new Cell().setBorder(Border.NO_BORDER));
                //[4][2]
                cell = new Cell().add(prgWeight).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddings(10, 0, 0, 0).setVerticalAlignment(VerticalAlignment.BOTTOM);
                cell.setNextRenderer(new DrawCellUtil( cell, new CellBorderDrawer[]{ null, THIN_SOLID_DARK_GRAY, THIN_SOLID_DARK_GRAY, null }));
                tblLeftTransportData.addCell(cell);
                //EMPTY ROW [5][X]
                tblLeftTransportData.addCell(new Cell().setBorder(Border.NO_BORDER));tblLeftTransportData.addCell(new Cell().setBorder(Border.NO_BORDER));tblLeftTransportData.addCell(new Cell().setBorder(Border.NO_BORDER));
              
                //background color and dimensions assigning
                tblLeftTransportData.setBackgroundColor(Color.WHITE).setMinHeight(CUSTOMER_DATA_TABLE_HEIGHT-10).setMaxHeight(CUSTOMER_DATA_TABLE_HEIGHT-10);
                //renderer assigning
                tblLeftTransportData.setNextRenderer(new RoundedColouredTable(tblLeftTransportData));
                
                
                //shows table
                new Canvas(pdfCanvas, pdfDoc, new Rectangle(LEFT_MARGIN + 20, 740 - HORIZONTAL_SPACE - CUSTOMER_DATA_TABLE_HEIGHT +10 - HORIZONTAL_SPACE - CUSTOMER_DATA_TABLE_HEIGHT , CUSTOMER_DATA_TABLE_WIDTH, CUSTOMER_DATA_TABLE_HEIGHT-10)).add(tblLeftTransportData);
                
                //RIGHT TRANSPORT DATA TABLE 6x3
                //cells contents
                //[0][0] 
                Text txtTransportReasonLabel = new Text("Causale del trasporto" ).setFont(font).setFontSize(12).setBold();
                Paragraph prgTransportReasonLabel = new Paragraph().add(txtTransportReasonLabel);
                //[0][2]
                Text txtTransportReason = new Text( deliveryNote.getString("transportReason") ).setFont(font).setFontSize(14).setBold();
                Paragraph prgTransportReason = new Paragraph().add(txtTransportReason);
                //[1][0] 
                Text txtPackagesNumberLabel = new Text("N° Colli" ).setFont(font).setFontSize(12).setBold();
                Paragraph prgPackagesNumberLabel = new Paragraph().add(txtPackagesNumberLabel);
                //[1][2]
                Text txtPackagesNumber = new Text( (deliveryNote.getInteger("packagesNumber") == null || deliveryNote.getInteger("packagesNumber") == 0 ) ? "" : deliveryNote.getInteger("packagesNumber").toString() ).setFont(font).setFontSize(14).setBold();
                Paragraph prgPackagesNumber = new Paragraph().add(txtPackagesNumber);
                //[2][0] 
                Text txtTransportStartDateLabel = new Text("Data inizio trasporto" ).setFont(font).setFontSize(12).setBold();
                Paragraph prgTransportStartDateLabel = new Paragraph().add(txtTransportStartDateLabel);
                //[2][2]
                Text txtTransportStartDate = new Text("" ).setFont(font).setFontSize(14).setBold();
                Paragraph prgTransportStartDate = new Paragraph().add(txtTransportStartDate);
                
                //Table instantiation
                Table tblRightTransportData = new Table(new UnitValue[]{new UnitValue(POINT,115),new UnitValue(POINT,15),new UnitValue(POINT,115)});
                //cells adding
                //[0][0]
                cell = new Cell().add(prgTransportReasonLabel).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).setPaddings(10, 0, 0, 0).setVerticalAlignment(VerticalAlignment.BOTTOM);
                cell.setNextRenderer(new DrawCellUtil( cell, new CellBorderDrawer[]{ null, null, THIN_SOLID_DARK_GRAY, null }));/*underlined*/
                tblRightTransportData.addCell(cell);
                //[0][1] epty cell
                tblRightTransportData.addCell(new Cell().setBorder(Border.NO_BORDER));
                //[0][2]
                cell = new Cell().add(prgTransportReason).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddings(10, 0, 0, 0).setVerticalAlignment(VerticalAlignment.BOTTOM);
                cell.setNextRenderer(new DrawCellUtil( cell, new CellBorderDrawer[]{ null, null, THIN_SOLID_DARK_GRAY, null }));
                tblRightTransportData.addCell(cell);
                //EMPTY ROW [1][X]
                tblRightTransportData.addCell(new Cell().setBorder(Border.NO_BORDER));tblRightTransportData.addCell(new Cell().setBorder(Border.NO_BORDER));tblRightTransportData.addCell(new Cell().setBorder(Border.NO_BORDER));
                //[2][0]
                cell = new Cell().add(prgPackagesNumberLabel).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).setPaddings(10, 0, 0, 0).setVerticalAlignment(VerticalAlignment.BOTTOM);
                cell.setNextRenderer(new DrawCellUtil( cell, new CellBorderDrawer[]{ null, null, THIN_SOLID_DARK_GRAY, null }));/*underlined*/
                tblRightTransportData.addCell(cell);
                //[2][1] epty cell
                tblRightTransportData.addCell(new Cell().setBorder(Border.NO_BORDER));
                //[2][2]
                cell = new Cell().add(prgPackagesNumber).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddings(10, 0, 0, 0).setVerticalAlignment(VerticalAlignment.BOTTOM);
                cell.setNextRenderer(new DrawCellUtil( cell, new CellBorderDrawer[]{ null, null, THIN_SOLID_DARK_GRAY, null }));
                tblRightTransportData.addCell(cell);
                //EMPTY ROW [3][X]
                tblRightTransportData.addCell(new Cell().setBorder(Border.NO_BORDER));tblRightTransportData.addCell(new Cell().setBorder(Border.NO_BORDER));tblRightTransportData.addCell(new Cell().setBorder(Border.NO_BORDER));
                //[4][0]
                cell = new Cell().add(prgTransportStartDateLabel).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).setPaddings(10, 0, 0, 0).setVerticalAlignment(VerticalAlignment.BOTTOM);
                cell.setNextRenderer(new DrawCellUtil( cell, new CellBorderDrawer[]{ null, null, THIN_SOLID_DARK_GRAY, null }));/*underlined*/
                tblRightTransportData.addCell(cell);
                //[4][1] epty cell
                tblRightTransportData.addCell(new Cell().setBorder(Border.NO_BORDER));
                //[4][2]
                cell = new Cell().add(prgTransportStartDate).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setPaddings(10, 0, 0, 0).setVerticalAlignment(VerticalAlignment.BOTTOM);
                cell.setNextRenderer(new DrawCellUtil( cell, new CellBorderDrawer[]{ null, null, THIN_SOLID_DARK_GRAY, null }));
                tblRightTransportData.addCell(cell);
                //EMPTY ROW [5][X]
                tblRightTransportData.addCell(new Cell().setBorder(Border.NO_BORDER));tblRightTransportData.addCell(new Cell().setBorder(Border.NO_BORDER));tblRightTransportData.addCell(new Cell().setBorder(Border.NO_BORDER));
                
                //background color and dimensions assigning
                tblRightTransportData.setBackgroundColor(Color.WHITE).setMinHeight(CUSTOMER_DATA_TABLE_HEIGHT-10).setMaxHeight(CUSTOMER_DATA_TABLE_HEIGHT-10);
                //renderer assigning
                tblRightTransportData.setNextRenderer(new RoundedColouredTable(tblRightTransportData));
                
                //shows table
                new Canvas(pdfCanvas, pdfDoc, new Rectangle(PAGE_WIDTH - RIGHT_MARGIN - CUSTOMER_DATA_TABLE_WIDTH - ( 2 * SHADOW_THICKNESS) , 740 - HORIZONTAL_SPACE - CUSTOMER_DATA_TABLE_HEIGHT +10 - HORIZONTAL_SPACE - CUSTOMER_DATA_TABLE_HEIGHT , CUSTOMER_DATA_TABLE_WIDTH, CUSTOMER_DATA_TABLE_HEIGHT-10)).add(tblRightTransportData);
                
                //ITEMS TABLE HEADER 1X3
                //cells contents
                //[0][0] 
                Text txtCode = new Text("Ns Cod." ).setFont(font).setFontSize(10).setBold();
                Paragraph prgCode = new Paragraph().add(txtCode).setTextAlignment(TextAlignment.CENTER).setBackgroundColor(Color.ORANGE).setBorderRadius( new BorderRadius(5)).setMargins(0, 10, 0, 10);
                //[0][1]
                Text txtQuantity = new Text("Q.TA'" ).setFont(font).setFontSize(10).setBold();
                Paragraph prgQuantity = new Paragraph().add(txtQuantity).setTextAlignment(TextAlignment.CENTER).setBackgroundColor(Color.ORANGE).setBorderRadius( new BorderRadius(5)).setMargins(0, 10, 0, 10);
                //[0][2] 
                Text txtDescritpion = new Text("DESCRIZIONE" ).setFont(font).setFontSize(10).setBold();
                Paragraph prgDescription = new Paragraph().add(txtDescritpion).setBackgroundColor(Color.ORANGE).setBorderRadius( new BorderRadius(5)).setMargins(0, 10, 0, 10);
                //Table instantiation
                Table tblItems = new Table(new UnitValue[]{new UnitValue(POINT,70),new UnitValue(POINT,70),new UnitValue(POINT,415)});
                //cells adding
                //[0][0]
                cell = new Cell().add(prgCode).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER);
                cell.setPaddings(0,5,0,0);
                tblItems.addCell(cell);
                //[0][1] 
                cell = new Cell().add(prgQuantity).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER);
                cell.setBorderLeft(new SolidBorder(0.5f)).setBorderRight(new SolidBorder(0.5f));
                cell.setPaddings(0,5,0,0);
                tblItems.addCell(cell);
                //[0][2]
                cell = new Cell().add(prgDescription).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER);
                cell.setPaddings(0,5,0,0);
                tblItems.addCell(cell);
                //sets table height
                tblItems.setMinHeight(25).setMaxHeight(25);
                //shows table
                new Canvas(pdfCanvas, pdfDoc, new Rectangle(LEFT_MARGIN , 735 - HORIZONTAL_SPACE - CUSTOMER_DATA_TABLE_HEIGHT +10 - HORIZONTAL_SPACE - CUSTOMER_DATA_TABLE_HEIGHT - SHADOW_THICKNESS - 25 , 555, 25)).add(tblItems);
                
                
                /*****    FOOTER ***************************************************/ 
                
                //TRANSPORT SIGNS TABLE 1 x 8
                //cells contents
                //[0][0] 
                Text txtTransporterLabel = new Text("Vettore").setFont(font).setFontSize(10).setBold();
                Paragraph prgTransporterLabel = new Paragraph().add(txtTransporterLabel);
                //[0][1] 
                Text txtTransporter = new Text(deliveryNote.getString("transporterDenomination") == null ? "" : deliveryNote.getString("transporterDenomination")).setFont(font).setFontSize(8).setBold();
                Paragraph prgTransporter = new Paragraph().add(txtTransporter).setTextAlignment(TextAlignment.LEFT);
                //[0][2]
                Text txtTransporterAddressLabel = new Text("Indirizzo" ).setFont(font).setFontSize(10).setBold();
                Paragraph prgTransporterAddressLabel = new Paragraph().add(txtTransporterAddressLabel);
                //[0][4] 
                Text txtStartDateLabel = new Text("Data inizio\ntrasporto" ).setFont(font).setFontSize(5).setBold();
                Paragraph prgStartDateLabel = new Paragraph().add(txtStartDateLabel);
                //[0][6] 
                Text txtTransporterSignLabel = new Text("Firma" ).setFont(font).setFontSize(10).setBold();
                Paragraph prgTransporterSignLabel = new Paragraph().add(txtTransporterSignLabel);
                
                //Table instantiation
                Table tblTransporterSign = new Table(new UnitValue[]{ new UnitValue(POINT,40), new UnitValue(POINT,100), new UnitValue(POINT,40),
                                                                      new UnitValue(POINT,150), new UnitValue(POINT,40),
                                                                      new UnitValue(POINT,40), new UnitValue(POINT,40), new UnitValue(POINT,100)});
                
                tblTransporterSign.setMaxWidth(405).setMinWidth(545);
                
                //cells adding
                //[0][0]
                cell = new Cell().add(prgTransporterLabel).setBorder(Border.NO_BORDER);
                tblTransporterSign.addCell(cell);
                //[0][1] empty cell with borders
                cell = new Cell().add(prgTransporter).setMinHeight(15);
                cell.setBorder(Border.NO_BORDER);
                cell.setNextRenderer(new DrawCellUtil( cell, new CellBorderDrawer[]{ null, THIN_SOLID_DARK_GRAY, THIN_SOLID_DARK_GRAY, null }));
                tblTransporterSign.addCell(cell);
                //[0][2] 
                cell = new Cell(2,1).add(prgTransporterAddressLabel).setBorder(Border.NO_BORDER);
                tblTransporterSign.addCell(cell);
                //[0][3] empty cell with borders
                cell = new Cell();
                cell.setBorder(Border.NO_BORDER);
                cell.setNextRenderer(new DrawCellUtil( cell, new CellBorderDrawer[]{ null, THIN_SOLID_DARK_GRAY, THIN_SOLID_DARK_GRAY, null }));
                tblTransporterSign.addCell(cell);
                //[0][4]
                cell = new Cell(2,1).add(prgStartDateLabel).setBorder(Border.NO_BORDER);
                tblTransporterSign.addCell(cell);
                //[0][5] empty cell with two borders
                cell = new Cell();
                cell.setBorder(Border.NO_BORDER);
                cell.setNextRenderer(new DrawCellUtil( cell, new CellBorderDrawer[]{ null, THIN_SOLID_DARK_GRAY, THIN_SOLID_DARK_GRAY, null }));
                tblTransporterSign.addCell(cell);
                //[0][6] 
                cell = new Cell(2,1).add(prgTransporterSignLabel).setBorder(Border.NO_BORDER);
                tblTransporterSign.addCell(cell);
                //[0][7] empty cell underlined
                cell = new Cell();
                cell.setBorder(Border.NO_BORDER);
                cell.setNextRenderer(new DrawCellUtil( cell, new CellBorderDrawer[]{ null, null, THIN_SOLID_DARK_GRAY, null }));
                tblTransporterSign.addCell(cell);
     
                //shows table
                new Canvas(pdfCanvas, pdfDoc, new Rectangle(LEFT_MARGIN , BOTTOM_MARGIN + 25 + HORIZONTAL_SPACE + CUSTOMER_DATA_TABLE_HEIGHT / 2 , 550, 20)).add(tblTransporterSign);
                
                // NOTES AREA TABLE 1 x 2
                //cells contents
                //[0][0] 
                Text txtNotesLabel = new Text("Note").setFont(font).setFontSize(12).setBold();
                Paragraph prgNotesLabel = new Paragraph().add(txtNotesLabel).setRotationAngle(-((2 * Math.PI) - Math.PI/ 2));
                //[0][1] 
                Text notes = new Text(deliveryNote.getString("notes") ).setFont(font).setFontSize(10).setBold();
                Paragraph prgNotes = new Paragraph().add(notes).setTextAlignment(TextAlignment.LEFT);
                
                //Table instantiation
                Table tblNotes = new Table(new UnitValue[]{new UnitValue(POINT,10),new UnitValue(POINT,CUSTOMER_DATA_TABLE_WIDTH+40)});
                tblNotes.setMaxHeight(CUSTOMER_DATA_TABLE_HEIGHT / 2 ).setMinHeight(CUSTOMER_DATA_TABLE_HEIGHT / 2);
                //cells adding
                //[0][0] e
                cell = cell = new Cell().add(prgNotesLabel).setPaddings(10, 0, 0, 0).setBorder(Border.NO_BORDER);
                tblNotes.addCell(cell);
                //[0][1] empty cell
                cell = new Cell().add(prgNotes).setBorder(Border.NO_BORDER);
                tblNotes.addCell(cell);
                //background color 
                tblNotes.setBackgroundColor(BACKGROUND_GRAY);
                //renderer assigning
                tblNotes.setNextRenderer(new RoundedColouredTable(tblNotes));
                
                //the shadow of the table 
                Table sdwtblNotes = new Table(new UnitValue[]{new UnitValue(POINT,CUSTOMER_DATA_TABLE_WIDTH+50)});
                sdwTblDestinationData.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
                //background color and dimensions assigning
                sdwtblNotes.setBackgroundColor(SHADOW_GRAY).setMinHeight(CUSTOMER_DATA_TABLE_HEIGHT / 2 ).setMaxHeight(CUSTOMER_DATA_TABLE_HEIGHT / 2 );
                //renderer assigning
                sdwtblNotes.setNextRenderer(new RoundedColouredTable(sdwtblNotes));
                
                //shows tables
                new Canvas(pdfCanvas, pdfDoc, new Rectangle(LEFT_MARGIN + SHADOW_THICKNESS, BOTTOM_MARGIN + 20 + HORIZONTAL_SPACE / 2, CUSTOMER_DATA_TABLE_WIDTH+50, CUSTOMER_DATA_TABLE_HEIGHT / 2)).add(sdwtblNotes);
                new Canvas(pdfCanvas, pdfDoc, new Rectangle(LEFT_MARGIN, BOTTOM_MARGIN + 20 + SHADOW_THICKNESS + HORIZONTAL_SPACE / 2, CUSTOMER_DATA_TABLE_WIDTH+50, CUSTOMER_DATA_TABLE_HEIGHT / 2)).add(tblNotes);
                
                //SIGNS TABLE 3x5
                //cells contents
                //[0][0] 
                Text txtSignsLabel = new Text("Firme").setFont(font).setFontSize(12).setBold();
                Paragraph prgSignsLabel = new Paragraph().add(txtSignsLabel).setRotationAngle(-((2 * Math.PI) - Math.PI/ 2));
                //[0][2]
                Text txtDriverLabel = new Text("Conducente" ).setFont(font).setFontSize(8).setBold();
                Paragraph prgDriverLabel = new Paragraph().add(txtDriverLabel);
                //[1][2] 
                Text txtAddresseeLabel = new Text("Destinatario" ).setFont(font).setFontSize(8).setBold();
                Paragraph prgAddresseeLabel = new Paragraph().add(txtAddresseeLabel);
                
                //Table instantiation
                Table tblSigns = new Table(new UnitValue[]{new UnitValue(POINT,10),new UnitValue(POINT,60),new UnitValue(POINT,5),new UnitValue(POINT,CUSTOMER_DATA_TABLE_WIDTH - 75)});
                //cells adding
                //[0][0]
                cell = new Cell(2,1).add(prgSignsLabel).setBorder(Border.NO_BORDER).setPaddings(10, 0, 0, 0);
                tblSigns.addCell(cell);
                //[0][1]
                cell = new Cell().add(prgDriverLabel).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.BOTTOM);
                tblSigns.addCell(cell);
                //[0][3] epty cell
                tblSigns.addCell(new Cell().setBorder(Border.NO_BORDER));
                //[0][4] epty cell underlined
                cell = new Cell().setBorder(Border.NO_BORDER);
                cell.setNextRenderer(new DrawCellUtil( cell, new CellBorderDrawer[]{ null, null, THIN_SOLID_DARK_GRAY, null }));/*underlined*/;
                tblSigns.addCell(cell);
                //[1][1]
                cell = new Cell().add(prgAddresseeLabel).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.BOTTOM);
                tblSigns.addCell(cell);
                //[1][2] epty cell
                tblSigns.addCell(new Cell().setBorder(Border.NO_BORDER));
                //[1][3] epty cell underlined
                cell = new Cell().setBorder(Border.NO_BORDER);
                cell.setNextRenderer(new DrawCellUtil( cell, new CellBorderDrawer[]{ null, null, THIN_SOLID_DARK_GRAY, null }));/*underlined*/;
                tblSigns.addCell(cell);
                //table positioning
                tblSigns.setFixedPosition( PAGE_WIDTH - RIGHT_MARGIN - CUSTOMER_DATA_TABLE_WIDTH , BOTTOM_MARGIN + 25 + HORIZONTAL_SPACE , CUSTOMER_DATA_TABLE_WIDTH );
                //background color and dimensions assigning
                tblSigns.setBackgroundColor(Color.WHITE).setMinHeight(CUSTOMER_DATA_TABLE_HEIGHT/2).setMaxHeight(CUSTOMER_DATA_TABLE_HEIGHT/2);
                
                //shows table
                new Canvas(pdfCanvas, pdfDoc, new Rectangle(PAGE_WIDTH - RIGHT_MARGIN - CUSTOMER_DATA_TABLE_WIDTH , BOTTOM_MARGIN + 15 + HORIZONTAL_SPACE , CUSTOMER_DATA_TABLE_WIDTH, CUSTOMER_DATA_TABLE_HEIGHT/2)).add(tblSigns);
                
                //COMPANY DATA FOOTER TABLEC 1X1 
                //cells contents
                //[0][0] 
                Text txtFooter = new Text("Sede legale: Via Scipione Ronchetti n.189/2 - 21044 Cavaria con Premezzo ( VA )\nCapitale Sociale Euro 10.000,00 I.V. - Registro Imprese di Varese 02677820124 - REA VA - 276830" ).setFont(font).setFontSize(6);
                Paragraph prgFooter = new Paragraph().add(txtFooter).setTextAlignment(TextAlignment.CENTER);
                
                //Table instantiation
                Table tblFooter = new Table(new UnitValue[]{new UnitValue(POINT,PAGE_WIDTH-LEFT_MARGIN-RIGHT_MARGIN)});
                tblFooter.setMaxHeight(30).setMinHeight(30);
                //cells adding
                //[0][0]
                cell = new Cell().add(prgFooter).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.BOTTOM);
                tblFooter.addCell(cell);
             
                //shows table
                new Canvas(pdfCanvas, pdfDoc, new Rectangle(LEFT_MARGIN , BOTTOM_MARGIN-3 , PAGE_WIDTH-LEFT_MARGIN-RIGHT_MARGIN, 30)).add(tblFooter);
                
                //draws black lines
                //draws vertical line between customer and destination areas
                pdfCanvas.moveTo(MIDDLE, 740-HORIZONTAL_SPACE-CUSTOMER_DATA_TABLE_HEIGHT + 5 )
                .lineTo(MIDDLE , 740-HORIZONTAL_SPACE - 5 )
                .setLineWidth(.5F);
                
                //draws vertical line between first and secdon tansport data 
                pdfCanvas.moveTo(MIDDLE, 740-HORIZONTAL_SPACE-CUSTOMER_DATA_TABLE_HEIGHT - HORIZONTAL_SPACE - CUSTOMER_DATA_TABLE_HEIGHT + 10 + 5 )
                .lineTo(MIDDLE , 740-HORIZONTAL_SPACE-CUSTOMER_DATA_TABLE_HEIGHT  - HORIZONTAL_SPACE - 5 )
                .setLineWidth(.5F);
                
                //draws items columns separators
                //first
                pdfCanvas.moveTo(LEFT_MARGIN + 70,  PageSize.A4.getHeight() - DOCUMENT_TOP_MARGIN )
                .lineTo(LEFT_MARGIN + 70 , DOCUMENT_BOTTOM_MARGIN + 5 )
                .setLineWidth(.5F);
                //second
                pdfCanvas.moveTo(LEFT_MARGIN + 70 + 70,  PageSize.A4.getHeight() - DOCUMENT_TOP_MARGIN )
                .lineTo(LEFT_MARGIN  + 70 + 70 , DOCUMENT_BOTTOM_MARGIN + 5 )
                .setLineWidth(.5F);
    
                pdfCanvas.stroke();
                
                //draws orange lines
                
                //sets stroke color to orange
                pdfCanvas.setStrokeColor(Color.ORANGE);
                
                //draws horizontal orange line ( header )
                pdfCanvas.moveTo(193, 777)
                .lineTo(380, 777)
                .setLineWidth(1);
                
                //draws horizontal orange line ( header )
                pdfCanvas.moveTo(220, 740)
                .lineTo(380, 740)
                .setLineWidth(1);
                
                //draws horizontal yellow line ( footer )
                pdfCanvas.moveTo(LEFT_MARGIN, BOTTOM_MARGIN-2)
                .lineTo(PAGE_WIDTH-RIGHT_MARGIN, BOTTOM_MARGIN-2)
                .setLineWidth(1);
                
                //draws horizontal yellow line ( footer )
                pdfCanvas.moveTo(LEFT_MARGIN, BOTTOM_MARGIN+25-3)
                .lineTo(PAGE_WIDTH-RIGHT_MARGIN, BOTTOM_MARGIN+25-2)
                .setLineWidth(1);
                
                pdfCanvas.stroke();
                
                //CREATES TABLE 1X2 FOR DOCUMENT DATE AND NUMBER
                //first cell content ( data label )
                Paragraph prgDdtDataLabel = new Paragraph();
                Text txtDdtLabelBold = new Text("D.D.T.").setFont(font).setFontSize(12).setBold();
                Text txtDateNumberLabel = new Text("\n(D.P.R. 14/08/96 N° 472)").setFont(font).setFontSize(6).setBold();
                prgDdtDataLabel.add(txtDdtLabelBold).add(txtDateNumberLabel).setStrokeColor(Color.BLACK) ;
                //second cell content : document number and date
                Paragraph prgDateNumber = new Paragraph();
                Text txtNumber = new Text( "N° " + deliveryNote.getInteger("number").toString()  ).setFont(font).setFontSize(12).setBold();
                
                //formats the date
                String date =  deliveryNote.getString("date");
                if( date.length() == 8 )
                {
                    String year = date.substring( 0, 4 );
                    String month = date.substring( 4, 6 );
                    String day = date.substring( 6 );
                    date = day + "/" + month + "/" + year;
                }
                
                Text txtDate = new Text( "\ndel " + date ).setFont(font).setFontSize(12).setBold();
                prgDateNumber.add(txtNumber).add(txtDate).setStrokeColor(Color.BLACK);
                //table
                Table tblDateNumber = new Table(new float[] {1, 1});
                cell = new Cell().add(prgDdtDataLabel).setTextAlignment(TextAlignment.CENTER);
                cell.setBorder(Border.NO_BORDER);
                cell.setNextRenderer(new DrawCellUtil( cell, new CellBorderDrawer[]{ SOLID, SOLID, SOLID, SOLID }));
                tblDateNumber.addCell(cell);
                cell = new Cell().add(prgDateNumber).setTextAlignment(TextAlignment.CENTER);
                cell.setBorder(Border.NO_BORDER);
                cell.setNextRenderer(new DrawCellUtil( cell, new CellBorderDrawer[]{ SOLID, SOLID, SOLID, SOLID }));
                tblDateNumber.addCell(cell);
                tblDateNumber.setFixedPosition(PAGE_WIDTH - RIGHT_MARGIN - 200, 740, 200);
                tblDateNumber.setMaxHeight(37).setMinHeight(37);
                new Canvas(pdfCanvas, pdfDoc, new Rectangle(PAGE_WIDTH - RIGHT_MARGIN, 740, 200, 37)).add(tblDateNumber);
                
                
            }catch( IOException ex ){}
            
        }
        
    }
    
    public class InvoiceMyEventHandler implements IEventHandler 
    {
        DbResult invoice;
        DataAccessObject dao;
        
        public InvoiceMyEventHandler(DbResult invoice) throws ClassNotFoundException
        {
            this.invoice = invoice;
            this.dao = new DataAccessObject();
        }
    
        @Override
        public void handleEvent(Event event) 
        {
            //dates utiity vriables
            String dateUtil = "";
            String year = "";
            String month = "";
            String day = "";
            
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfDocument pdfDoc = docEvent.getDocument();
            
            //the handler takes each page of the document
            PdfPage page = docEvent.getPage();
            
            //creates a pdf canvas
            PdfCanvas pdfCanvas = new PdfCanvas(page);
            
            
            /*   CREATES RECTANGLES  */
            
            /********** HEADER ********************************/
            
            //main rectangle
            Rectangle mainRect = new Rectangle(25, 22, 555,820);
            
            
            //Logo 
            Rectangle rectLogo = new Rectangle(25,783 , 170, 59);
            
            //DUESSE catalogue1
            Rectangle rectCatalogue1 = new Rectangle(25+50+50+20+50, 740, 65, 53);
            
            //DUESSE catalogue2
            Rectangle rectCatalogue2 = new Rectangle(25+165+50+20, 740, 100, 53);
            
            //invoice data label
            Rectangle rectInvoiceDataLabel = new Rectangle(25+330+5, 740, 110, 53);
            
            //invoice data
            Rectangle rectInvoiceData = new Rectangle(25+330+5+110, 740, 110, 53);
            
            //Contacts
            Rectangle rectContacts = new Rectangle(25, 783-130, 170, 120);
            
            //Customer label
            Rectangle rectCustomerLabel = new Rectangle(350, 650, 20, 90);
            
            //Customer denomination
            Rectangle rectCustomerDenomination = new Rectangle(370, 700, 210, 40);
            
            //Customer address
            Rectangle rectCustomerAddress = new Rectangle(370, 650, 210, 50);
            
            //VatCode label
            Rectangle rectVatCodeLabel = new Rectangle(25, 615, 30, 35);
            
            //VatCode
            Rectangle rectVatCode = new Rectangle(25+30, 615, 180, 35);
            
            //invoice  numbers label
            Rectangle rectDeliveryNotesNumbersLabel = new Rectangle(25+30+180, 578, 30, 70);
            
            //invoice number
            Rectangle rectDeliveryNotesNumbers = new Rectangle(25+30+180+30, 578, 315, 70);
            
            //fiscalcode label
            Rectangle rectFiscalCodeLabel = new Rectangle(25, 578, 30, 35);
            
            //fiscalcode
            Rectangle rectFiscalCode = new Rectangle(25+30, 578, 180, 35);
            
            //delivernnotes date label
            //Rectangle rectDeliveryNotesDatesLabel = new Rectangle(25+30+180, 578, 30, 35);
            
            //delivernnotes date
            //Rectangle rectDeliveryNotesDates = new Rectangle(25+30+180+30, 578, 315, 35);
            
            //First column label
            Rectangle rectFirstColumnLabel = new Rectangle(25, 550, 70, 25);
            
            //Second column label
            Rectangle rectSecondColumnLabel = new Rectangle(25+70, 550, 70, 25);
            
            //Third column label
            Rectangle rectThirdColumnLabel = new Rectangle(25+140, 550, 345, 25);
            
            //Forth column label
            Rectangle rectForthColumnLabel = new Rectangle(25+140+345, 550, 70, 25);
            
            
            
            /************  FOOTER ***********************************/
            
            //sending invoice
            Rectangle rectSendingInvoice = new Rectangle(65,132,515,15);
            
            //sending invoice label
            Rectangle rectSendinInvoiceLabel = new Rectangle(25,132,40,15);
            
            
            //total label
            Rectangle rectTotalLabel = new Rectangle(515,75,65,57);
            
            //thirdAmount
            Rectangle rectThirdAmount = new Rectangle(450,75,65,19);
            
            //secondAmount
            Rectangle rectSecondAmount = new Rectangle(450,94,65,19);
            
            //firstAmount
            Rectangle rectFirstAmount = new Rectangle(450,113,65,19);
            
            //thirdAmountlabel
            Rectangle rectThirdAmountLabel = new Rectangle(430,75,20,19);
            
            //secondAmountLabel
            Rectangle rectSecondAmountLabel = new Rectangle(430,94,20,19);
            
            //firstAmountLabel
            Rectangle rectFirstAmountLabel = new Rectangle(430,113,20,19);
            
            //thirdDue
            Rectangle rectThirdDue = new Rectangle(365,75,65,19);
            
            //secondDue
            Rectangle rectSecondDue = new Rectangle(365,94,65,19);
            
            //first due
            Rectangle rectFirstDue = new Rectangle(365,113,65,19);
            
            //third due label
            Rectangle rectThirdDueLabel = new Rectangle(345,75,20,19);
            
            //second due label
            Rectangle rectSecondDueLabel = new Rectangle(345,94,20,19);
            
            //first due label
            Rectangle rectFirstDueLabel = new Rectangle(345,113,20,19);
            
            //dues label
            Rectangle rectDuesLabel = new Rectangle(325,75,20,57);
            
            //notes
            Rectangle rectNotes = new Rectangle(45+10,75,270,57);
            
            //notes label
            Rectangle rectNotesLabel = new Rectangle(25,76,30,57);
            
            //total
            Rectangle rectTotal = new Rectangle(505,25,75,30);
            
            //euro label
            Rectangle rectEuroLabel = new Rectangle(505,55,75,20);
            
            //tax
            Rectangle rectTax = new Rectangle(385,25,120,25);
            
            //taxable
            Rectangle rectTaxable = new Rectangle(385,50,120,25);
            
            //Tax label
            Rectangle rectTaxLabel = new Rectangle(345,25,40,25);
            
            //taxable label
            Rectangle rectTaxableLabel = new Rectangle(345,50,40,25);
            
            //paymentConditions 
            Rectangle rectPaymentConditions = new Rectangle(25+20+10, 25, 270, 50);
            
            //payment conditions label
            Rectangle rectPaymentConditionsLabel = new Rectangle(25, 25, 30, 50);
            
            
            //invoice footer
            Rectangle rectFooter = new Rectangle(25, 10, 555, 15);
            
            /*INSERTS RECTANGLES*/
          
            
            //pdfCanvas.rectangle(mainRect);
            pdfCanvas.rectangle(rectLogo);
            pdfCanvas.rectangle(rectCatalogue1 );
            pdfCanvas.rectangle(rectCatalogue2 );
            pdfCanvas.rectangle(rectInvoiceDataLabel );
            pdfCanvas.rectangle(rectInvoiceData );
            pdfCanvas.rectangle(rectContacts );
            pdfCanvas.rectangle(rectCustomerLabel );
            pdfCanvas.rectangle(rectCustomerDenomination);
            pdfCanvas.rectangle(rectCustomerAddress);
            pdfCanvas.rectangle(rectVatCodeLabel );
            pdfCanvas.rectangle(rectVatCode );
            pdfCanvas.rectangle(rectDeliveryNotesNumbersLabel );
            pdfCanvas.rectangle(rectDeliveryNotesNumbers );
            pdfCanvas.rectangle(rectFiscalCodeLabel );
            pdfCanvas.rectangle(rectFiscalCode );
            //pdfCanvas.rectangle(rectDeliveryNotesDatesLabel );
            //pdfCanvas.rectangle(rectDeliveryNotesDates );
            pdfCanvas.rectangle(rectFirstColumnLabel );
            pdfCanvas.rectangle(rectSecondColumnLabel );
            pdfCanvas.rectangle(rectThirdColumnLabel );
            pdfCanvas.rectangle(rectForthColumnLabel );
            
            pdfCanvas.rectangle(rectSendingInvoice  );
            pdfCanvas.rectangle(rectSendinInvoiceLabel  );
            pdfCanvas.rectangle(rectTotalLabel  );
            pdfCanvas.rectangle(rectThirdAmount  );
            pdfCanvas.rectangle(rectSecondAmount  );
            pdfCanvas.rectangle(rectFirstAmount  );
            pdfCanvas.rectangle(rectThirdAmountLabel  );
            pdfCanvas.rectangle(rectSecondAmountLabel   );
            pdfCanvas.rectangle(rectFirstAmountLabel   );
            pdfCanvas.rectangle(rectThirdDue   );
            pdfCanvas.rectangle(rectSecondDue   );
            pdfCanvas.rectangle(rectFirstDue   );
            pdfCanvas.rectangle(rectThirdDueLabel   );
            pdfCanvas.rectangle(rectSecondDueLabel   );
            pdfCanvas.rectangle(rectFirstDueLabel   );
            pdfCanvas.rectangle(rectDuesLabel   );
            pdfCanvas.rectangle(rectNotes   );
            
            pdfCanvas.rectangle(rectNotesLabel   );
            pdfCanvas.rectangle(rectTotal   );
            pdfCanvas.rectangle(rectEuroLabel   );
            pdfCanvas.rectangle(rectTax   );
            pdfCanvas.rectangle(rectTaxable   );
            pdfCanvas.rectangle(rectTaxLabel   );
            pdfCanvas.rectangle(rectTaxableLabel   );
           
            pdfCanvas.rectangle(rectPaymentConditions    );
            pdfCanvas.rectangle(rectPaymentConditionsLabel    );
           
            pdfCanvas.rectangle(rectFooter);
            pdfCanvas.setLineWidth(0F);
            pdfCanvas.stroke();
            //pdfCanvas;
            
            //creates  canvas
            Canvas canvasLogo = new Canvas(pdfCanvas, pdfDoc, rectLogo).setBackgroundColor(Color.convertCmykToRgb(DeviceCmyk.YELLOW), 0, 0, 0, 0, 0);//.setBorder(Border.NO_BORDER);
    
            Canvas canvasRectCatalogue1 = new Canvas(pdfCanvas, pdfDoc, rectCatalogue1 );
            Canvas canvasRectCatalogue2 = new Canvas(pdfCanvas, pdfDoc, rectCatalogue2 );
            Canvas canvasRectInvoiceDataLabel = new Canvas(pdfCanvas, pdfDoc, rectInvoiceDataLabel );
            Canvas canvasRectInvoiceData = new Canvas(pdfCanvas, pdfDoc, rectInvoiceData );
            Canvas canvasRectContacts = new Canvas(pdfCanvas, pdfDoc, rectContacts ).setFontSize(6);
            Canvas canvasRectCustomerLabel = new Canvas(pdfCanvas, pdfDoc, rectCustomerLabel );
            Canvas canvasRectCustomerDenomination = new Canvas(pdfCanvas, pdfDoc, rectCustomerDenomination  );
            Canvas canvasRectCustomerAddress = new Canvas(pdfCanvas, pdfDoc, rectCustomerAddress );
            Canvas canvasRectVatCodeLabel = new Canvas(pdfCanvas, pdfDoc, rectVatCodeLabel );
            Canvas canvasRectVatCode = new Canvas(pdfCanvas, pdfDoc, rectVatCode );
            Canvas canvasRectDeliveryNotesNumbersLabel = new Canvas(pdfCanvas, pdfDoc, rectDeliveryNotesNumbersLabel );
            Canvas canvasRectDeliveryNotesNumbers = new Canvas(pdfCanvas, pdfDoc, rectDeliveryNotesNumbers );
            Canvas canvasRectFiscalCodeLabel = new Canvas(pdfCanvas, pdfDoc, rectFiscalCodeLabel );
            Canvas canvasRectFiscalCode = new Canvas(pdfCanvas, pdfDoc, rectFiscalCode );
            //Canvas canvasRectDeliveryNotesDatesLabel = new Canvas(pdfCanvas, pdfDoc, rectDeliveryNotesDatesLabel );
            //Canvas canvasRectDeliveryNotesDates = new Canvas(pdfCanvas, pdfDoc, rectDeliveryNotesDates );
            Canvas canvasRectFirstColumnLabel = new Canvas(pdfCanvas, pdfDoc, rectFirstColumnLabel );
            Canvas canvasRectSecondColumnLabel = new Canvas(pdfCanvas, pdfDoc, rectSecondColumnLabel );
            Canvas canvasRectThirdColumnLabel = new Canvas(pdfCanvas, pdfDoc, rectThirdColumnLabel );
            Canvas canvasRectForthColumnLabel = new Canvas(pdfCanvas, pdfDoc, rectForthColumnLabel );
            Canvas canvasRectSendingInvoice = new Canvas(pdfCanvas, pdfDoc, rectSendingInvoice );
            Canvas canvasRectSendinInvoiceLabel = new Canvas(pdfCanvas, pdfDoc, rectSendinInvoiceLabel );
            Canvas canvasRectTotalLabel = new Canvas(pdfCanvas, pdfDoc, rectTotalLabel );
            Canvas canvasRectThirdAmount = new Canvas(pdfCanvas, pdfDoc, rectThirdAmount );
            Canvas canvasRectSecondAmount = new Canvas(pdfCanvas, pdfDoc, rectSecondAmount );
            Canvas canvasRectFirstAmount = new Canvas(pdfCanvas, pdfDoc, rectFirstAmount );
            Canvas canvasRectThirdAmountLabel = new Canvas(pdfCanvas, pdfDoc, rectThirdAmountLabel );
            Canvas canvasRectSecondAmountLabel = new Canvas(pdfCanvas, pdfDoc, rectSecondAmountLabel );
            
            Canvas canvasRectFirstAmountLabel = new Canvas(pdfCanvas, pdfDoc, rectFirstAmountLabel   );
            Canvas canvasRectThirdDue = new Canvas(pdfCanvas, pdfDoc, rectThirdDue  );
            Canvas canvasRectSecondDue = new Canvas(pdfCanvas, pdfDoc, rectSecondDue  );
            Canvas canvasRectFirstDue = new Canvas(pdfCanvas, pdfDoc, rectFirstDue   );
            Canvas canvasRectThirdDueLabel = new Canvas(pdfCanvas, pdfDoc, rectThirdDueLabel   );
            Canvas canvasRectSecondDueLabel = new Canvas(pdfCanvas, pdfDoc, rectSecondDueLabel  );
            Canvas canvasRectFirstDueLabel = new Canvas(pdfCanvas, pdfDoc, rectFirstDueLabel  );
            Canvas canvasRectDuesLabel = new Canvas(pdfCanvas, pdfDoc, rectDuesLabel  );
            
            Canvas canvasRectNotes = new Canvas(pdfCanvas, pdfDoc, rectNotes  );
            Canvas canvasRectNotesLabel = new Canvas(pdfCanvas, pdfDoc, rectNotesLabel  );
            Canvas canvasRectTotal = new Canvas(pdfCanvas, pdfDoc, rectTotal  );
            Canvas canvasRectEuroLabel = new Canvas(pdfCanvas, pdfDoc, rectEuroLabel    );
            Canvas canvasRectTax = new Canvas(pdfCanvas, pdfDoc, rectTax  );
            Canvas canvasRectTaxLabel = new Canvas(pdfCanvas, pdfDoc, rectTaxLabel   );
            Canvas canvasRectTaxableLabel = new Canvas(pdfCanvas, pdfDoc, rectTaxableLabel  );
            Canvas canvasRectTaxable = new Canvas(pdfCanvas, pdfDoc, rectTaxable  );
            
            Canvas canvasRectPaymentConditions = new Canvas(pdfCanvas, pdfDoc, rectPaymentConditions  );
            Canvas canvasRectPaymentConditionsLabel = new Canvas(pdfCanvas, pdfDoc, rectPaymentConditionsLabel   );
            
            Canvas canvasRectFooter = new Canvas(pdfCanvas, pdfDoc, rectFooter  );
            
            
            /* CREATES COTENTS FOR RECTANGLES AND SHOWS THEM IN RELATED CANVAS*/
            
            //logo
            try{
            Image logo = new Image(ImageDataFactory.create(Config.LOGO_IMG));
            canvasLogo.add(logo);
            }catch(MalformedURLException  ex){}
            
            //Catalogue1
            Text catalogue1 = new Text( "• Cataloghi ricambi\n•Esplosi tecnici\n•Spaccati\n•Prospettive").setFont(font);
            Paragraph pCatalogue1 = new Paragraph().add(catalogue1).setTextAlignment(TextAlignment.LEFT).setFontSize(6).setBold();
            canvasRectCatalogue1.add(pCatalogue1);
            
            //Catalogue2
            Text catalogue2 = new Text( "•Libretti per istruzioni d'uso\n•Manuali di assistenza e riparazione\n•Schemi di installazione\n•Depliant tecnici").setFont(font);
            Paragraph pCatalogue2 = new Paragraph().add(catalogue2).add(catalogue2).setTextAlignment(TextAlignment.LEFT).setFontSize(6).setBold();
            canvasRectCatalogue2.add(pCatalogue2);
            
            //invoice data label
            Text invoiceLabelBold = new Text("FATTURA").setFont(font).setFontSize(12).setBold();
            Paragraph pInvoiceLabel = new Paragraph().add(invoiceLabelBold).setTextAlignment(TextAlignment.CENTER);
            canvasRectInvoiceDataLabel.add(pInvoiceLabel);
            
            //invoice date 
            Text invoiceNumber = new Text("N° " + invoice.getLong("number").toString() ).setFont(font).setFontSize(12).setBold();
            
            //formats the date
            dateUtil =   invoice.getString("date");
            if( dateUtil.length() == 8 )
            {
                year = dateUtil.substring( 0, 4 );
                month = dateUtil.substring( 4, 6 );
                day = dateUtil.substring( 6 );
                dateUtil = day + "/" + month + "/" + year;
            }
       
            Text invoiceDate = new Text("\ndel " + dateUtil).setFont(font).setFontSize(12).setBold();
            Paragraph pInvoiceData = new Paragraph().add(invoiceNumber).add(invoiceDate).setTextAlignment(TextAlignment.CENTER);
            canvasRectInvoiceData.add(pInvoiceData);
            
            //Contacts
            Text duesse = new Text("DUESSE Service s.r.l." ).setFont(font).setFontSize(6).setBold();
            Text contacts = new Text("Via Agusta,51\n21017 Samarate(VA) Italy\nCod.Fisc. - P.IVA 02677820124\nCapitale sociale Euro 10.000,00 i.v.\nRegistro Imprese di Varese 0267820124\nREA VA - 276830\n--------------------------------------------------\nSede legale Via Scipione Ronchetti n.189/2\n21044 Cavaria con Premezzo (VA)\n--------------------------------------------------\nTelefono  +39-0331220913\nTelefax  +39-0331220914\ninfo@duesse.it" ).setFont(font).setFontSize(6);
            Paragraph pContacts = new Paragraph().add(duesse).add(contacts);
            canvasRectContacts.add(pContacts);
            
            //Customer label
            Text customerLabel = new Text("Spettabile" ).setFont(font).setFontSize(10).setBold().setItalic();
            Paragraph pCustomerLabel = new Paragraph().add(customerLabel).setRotationAngle(-((2 * Math.PI) - Math.PI/ 2)).setPaddingRight(15);
            canvasRectCustomerLabel.add(pCustomerLabel).setTextAlignment(TextAlignment.CENTER);
            
            //Customer  denomination
            Text customerDenomination = new Text(invoice.getString("denomination")).setFont(font).setFontSize(12).setBold();
            Paragraph pCustomerDenomination = new Paragraph().add(customerDenomination).setPaddingLeft(4).setPaddingTop(4).setTextAlignment(TextAlignment.CENTER);
            canvasRectCustomerDenomination.add(pCustomerDenomination);
            
            //Customer  address
            Text customerAddress = new Text(invoice.getString("address") + ", " + invoice.getString("houseNumber")+"\n"+invoice.getString("postalCode") + "    " + invoice.getString("city")+ " " + invoice.getString("province") ).setFont(font).setFontSize(10).setBold();
            Paragraph pCustomerAddress = new Paragraph().add(customerAddress).setPaddingLeft(4).setPaddingTop(4).setTextAlignment(TextAlignment.CENTER);
            canvasRectCustomerAddress.add(pCustomerAddress);
            
            //Vat Code label
            Text vatCodeLabel = new Text("Partita\nIVA" ).setFont(font).setFontSize(8).setBold();
            Paragraph pVatCodeLabel = new Paragraph().add(vatCodeLabel).setRotationAngle(-((2 * Math.PI) - Math.PI/ 2)).setTextAlignment(TextAlignment.CENTER).setPaddingRight(4f);
            canvasRectVatCodeLabel.add(pVatCodeLabel);
            
            //Vat Code
            Text vatCode = new Text(invoice.getString("vatCode")).setFont(font).setFontSize(10).setBold();
            Paragraph pVatCode = new Paragraph().add(vatCode).setTextAlignment(TextAlignment.CENTER);
            canvasRectVatCode.add(pVatCode);
            
            //Delivery notes number  label
            Text deliveryNotesNumbersLabel = new Text("D.D.T.\n numero e data" ).setFont(font).setFontSize(8).setBold();
            Paragraph pDeliveryNotesNumbersLabel = new Paragraph().add(deliveryNotesNumbersLabel).setRotationAngle(-((2 * Math.PI) - Math.PI/ 2)).setTextAlignment(TextAlignment.CENTER).setPaddingRight(4f);
            canvasRectDeliveryNotesNumbersLabel.add(pDeliveryNotesNumbersLabel);
            
            //Delivery Notes Numbers and dates
            String deliveryNotesData = "";
                       
            try{
                DbResult relatedDeliveryNotes = dao.getInvoiceDeliveryNotes(invoice.getLong("invoice_id"));
                for( int i = 0; i < relatedDeliveryNotes.rowsCount(); i++ )
                {
                    deliveryNotesData += dao.getDeliveryNoteNumberAndDate(relatedDeliveryNotes.getLong(i, "deliveryNote_id"));
                }
            }catch(SQLException ex){}
            
            Text deliveryNotesNumberAndDates = new Text( deliveryNotesData ).setFont(font).setFontSize(10).setBold();
            Paragraph pDeliveryNotesNumber = new Paragraph().add(deliveryNotesNumberAndDates).setTextAlignment(TextAlignment.CENTER);
            canvasRectDeliveryNotesNumbers .add(pDeliveryNotesNumber);
            
            //fiscal code  label
            Text fiscalCodeLabel = new Text("Codice\nFiscale" ).setFont(font).setFontSize(8).setBold();
            Paragraph pFiscalCodeLabel = new Paragraph().add(fiscalCodeLabel).setRotationAngle(-((2 * Math.PI) - Math.PI/ 2)).setTextAlignment(TextAlignment.CENTER).setPaddingRight(4f);
            canvasRectFiscalCodeLabel.add(pFiscalCodeLabel);
            
            //fiscal code
            Text fiscalCode = new Text(invoice.getString("fiscalCode")).setFont(font).setFontSize(10).setBold();
            Paragraph pFiscalCode = new Paragraph().add(fiscalCode).setTextAlignment(TextAlignment.CENTER);
            canvasRectFiscalCode.add(pFiscalCode);
            
            //first column label
            Text firstColumnLabel = new Text("Codice" ).setFont(font).setFontSize(10).setBold();
            Paragraph pFirstColumnLabel = new Paragraph().add(firstColumnLabel).setTextAlignment(TextAlignment.CENTER);
            canvasRectFirstColumnLabel.add(pFirstColumnLabel);
            
            //second column label
            Text secondColumnLabel = new Text("Quantità" ).setFont(font).setFontSize(10).setBold();
            Paragraph pSecondColumnLabel = new Paragraph().add(secondColumnLabel).setTextAlignment(TextAlignment.CENTER);
            canvasRectSecondColumnLabel.add(pSecondColumnLabel);
            
            //third column label
            Text thirdColumnLabel = new Text("Descrizione" ).setFont(font).setFontSize(10).setBold();
            Paragraph pThirdColumnLabel = new Paragraph().add(thirdColumnLabel).setTextAlignment(TextAlignment.CENTER);
            canvasRectThirdColumnLabel.add(pThirdColumnLabel);
            
            //forth column label
            Text forthColumnLabel = new Text("Importo E." ).setFont(font).setFontSize(10).setBold();
            Paragraph pForthColumnLabel = new Paragraph().add(forthColumnLabel).setTextAlignment(TextAlignment.CENTER);
            canvasRectForthColumnLabel.add(pForthColumnLabel);
            
            //sending invoice label
            Text invoicetLabel = new Text("Invio Fattura" ).setFont(font).setFontSize(6).setItalic().setBold();
            Paragraph pInvoicetLabel = new Paragraph().add(invoicetLabel);
            canvasRectSendinInvoiceLabel.add(pInvoicetLabel).setTextAlignment(TextAlignment.LEFT);
            
            //sending invoice 
            Text invoicet = new Text("Fattura inviata via e-mail con valore di originale. Risoluzione Agenzia delle Entrate nr.107 del 04/07/01" ).setFont(font).setFontSize(6).setItalic().setBold();
            Paragraph pInvoicet = new Paragraph().add(invoicet).setTextAlignment(TextAlignment.CENTER);
            canvasRectSendingInvoice.add(pInvoicet);
            
            //total label
            Text totalLabel = new Text("TOTALE\nFATTURA" ).setFont(font).setFontSize(10).setBold();
            Paragraph pTotalLabel = new Paragraph().add(totalLabel).setTextAlignment(TextAlignment.CENTER);
            canvasRectTotalLabel .add(pTotalLabel);
            
            //third amount
            Text thirdAmount = new Text("");
            try{
                thirdAmount = new Text(cu.getCurrency(invoice.getDouble("thirdAmount"))).setFont(font).setFontSize(10).setBold();
            }catch( CurrencyException ex ){ex.printStackTrace();}
            
            Paragraph pThirdAmount = new Paragraph().add(thirdAmount).setTextAlignment(TextAlignment.CENTER);
            canvasRectThirdAmount.add(pThirdAmount);
            
            //second amount
            Text secondAmount = new Text("");
            try{
                secondAmount = new Text(cu.getCurrency(invoice.getDouble("secondAmount"))).setFont(font).setFontSize(10).setBold();
            }catch( CurrencyException ex ){ex.printStackTrace();}
            
            Paragraph pSecondAmount = new Paragraph().add(secondAmount).setTextAlignment(TextAlignment.CENTER);
            canvasRectSecondAmount.add(pSecondAmount);
            
            //first amount
            Text firstAmount = new Text("");
            try{
                firstAmount = new Text(cu.getCurrency(invoice.getDouble("firstAmount"))).setFont(font).setFontSize(10).setBold();
            }catch( CurrencyException ex ){ex.printStackTrace();}
            
            Paragraph pFirstAmount = new Paragraph().add(firstAmount).setTextAlignment(TextAlignment.CENTER);
            canvasRectFirstAmount.add(pFirstAmount);
            
            //third amount label
            Text thirdAmountLabel = new Text("3-").setFont(font).setFontSize(8).setBold();
            Paragraph pThirdAmountLabel = new Paragraph().add(thirdAmountLabel).setTextAlignment(TextAlignment.CENTER);
            canvasRectThirdAmountLabel.add(pThirdAmountLabel);
            
            //second amount label
            Text secondAmountLabel = new Text("2-").setFont(font).setFontSize(8).setBold();
            Paragraph pSecondAmountLabel = new Paragraph().add(secondAmountLabel).setTextAlignment(TextAlignment.CENTER);
            canvasRectSecondAmountLabel.add(pSecondAmountLabel);
            
            //first amount label
            Text firstAmountLabel = new Text("1-").setFont(font).setFontSize(8).setBold();
            Paragraph pFirstAmountLabel = new Paragraph().add(firstAmountLabel).setTextAlignment(TextAlignment.CENTER);
            canvasRectFirstAmountLabel.add(pFirstAmountLabel);
            
            //third due
            dateUtil =   invoice.getString("thirdAmountDate");
            if( dateUtil.length() == 8 )
            {
                year = dateUtil.substring( 0, 4 );
                month = dateUtil.substring( 4, 6 );
                day = dateUtil.substring( 6 );
                dateUtil = day + "/" + month + "/" + year;
            }
           
            Text thirdDue = new Text(dateUtil).setFont(font).setFontSize(8);
            Paragraph pThirdDue = new Paragraph().add(thirdDue).setTextAlignment(TextAlignment.CENTER);
            canvasRectThirdDue.add(pThirdDue);
            
            //second due
            dateUtil =   invoice.getString("secondAmountDate");
            if( dateUtil.length() == 8 )
            {
                year = dateUtil.substring( 0, 4 );
                month = dateUtil.substring( 4, 6 );
                day = dateUtil.substring( 6 );
                dateUtil = day + "/" + month + "/" + year;
            }

            Text secondDue = new Text(dateUtil).setFont(font).setFontSize(8);
            Paragraph pSecondDue = new Paragraph().add(secondDue).setTextAlignment(TextAlignment.CENTER);
            canvasRectSecondDue.add(pSecondDue);
            
            //first due
            dateUtil =   invoice.getString("firstAmountDate");
            if( dateUtil.length() == 8 )
            {
                year = dateUtil.substring( 0, 4 );
                month = dateUtil.substring( 4, 6 );
                day = dateUtil.substring( 6 );
                dateUtil = day + "/" + month + "/" + year;
            }
            
            Text firstDue = new Text(dateUtil).setFont(font).setFontSize(8);
            Paragraph pFirstDue = new Paragraph().add(firstDue).setTextAlignment(TextAlignment.CENTER);
            canvasRectFirstDue.add(pFirstDue);
            
            //third due label
            Text thirdDueLabel = new Text("€ -").setFont(font).setFontSize(8).setBold();
            Paragraph pThirdDueLabel = new Paragraph().add(thirdDueLabel).setTextAlignment(TextAlignment.CENTER);
            canvasRectThirdDueLabel.add(pThirdDueLabel);
            
            //second due label
            Text secondDueLabel = new Text("€ -").setFont(font).setFontSize(8).setBold();
            Paragraph pSecondDueLabel = new Paragraph().add(secondDueLabel).setTextAlignment(TextAlignment.CENTER);
            canvasRectSecondDueLabel.add(pSecondDueLabel);
            
            //first due label
            Text firstDueLabel = new Text("€ -").setFont(font).setFontSize(8).setBold();
            Paragraph pFirstDueLabel = new Paragraph().add(firstDueLabel).setTextAlignment(TextAlignment.CENTER);
            canvasRectFirstDueLabel.add(pFirstDueLabel);
            
            //dues label
            Text duesLabel = new Text("Scadenze" ).setFont(font).setFontSize(8).setBold();
            Paragraph pDuesLabel = new Paragraph().add(duesLabel).setRotationAngle(-((2 * Math.PI) - Math.PI/ 2)).setTextAlignment(TextAlignment.CENTER).setPaddingRight(8f);
            canvasRectDuesLabel.add(pDuesLabel);
            
            //footer
            Text footer = new Text("Per esigenze amministrative non si accettano arrotondamenti o sconti, preventivamente non concordati." ).setFont(font).setFontSize(6);
            Paragraph pFooter = new Paragraph().add(footer).setTextAlignment(TextAlignment.CENTER);
            canvasRectFooter.add(pFooter);
            
            //notes label
            Text notesLabel = new Text("Note" ).setFont(font).setFontSize(10).setBold().setItalic();
            Paragraph pNotesLabel = new Paragraph().add(notesLabel).setRotationAngle(-((2 * Math.PI) - Math.PI/ 2)).setPaddingRight(15);
            canvasRectNotesLabel.add(pNotesLabel).setTextAlignment(TextAlignment.CENTER);
            
            //Notes
            Text notes = new Text(invoice.getString("notes") ).setFont(font).setFontSize(10).setBold();
            Paragraph pNotes = new Paragraph().add(notes).setTextAlignment(TextAlignment.LEFT);
            canvasRectNotes.add(pNotes);
           
            //Total amount
            Text total = new Text("");
            try{
            total = new Text(cu.getCurrency(invoice.getDouble("totalAmount"))).setFont(font).setFontSize(10).setBold();
            }catch( CurrencyException ex ){ex.printStackTrace();}
            Paragraph pTotal = new Paragraph().add(total).setTextAlignment(TextAlignment.CENTER);
            canvasRectTotal.add(pTotal);
            
            //euro label
            Text euroLabel = new Text("€uro").setFont(font).setFontSize(10).setBold();
            Paragraph pEuroLabel = new Paragraph().add(euroLabel).setTextAlignment(TextAlignment.CENTER);
            canvasRectEuroLabel.add(pEuroLabel);
            
            //Tax amount
            Text tax = new Text("");
            try{
            tax = new Text(cu.getCurrency(invoice.getDouble("taxAmount"))).setFont(font).setFontSize(10).setBold();
            }catch( CurrencyException ex ){ex.printStackTrace();}
            Paragraph pTax = new Paragraph().add(tax).setTextAlignment(TextAlignment.CENTER);
            canvasRectTax.add(pTax);
            
            //tax label
            Text taxLabel = new Text("IVA 22% €").setFont(font).setFontSize(8).setBold();
            Paragraph pTaxLabel = new Paragraph().add(taxLabel).setTextAlignment(TextAlignment.CENTER);
            canvasRectTaxLabel.add(pTaxLabel);
            
            //Taxable amount
            Text taxable = new Text("");
            try{
            taxable = new Text(cu.getCurrency(invoice.getDouble("taxableAmount"))).setFont(font).setFontSize(10).setBold();
            }catch( CurrencyException ex ){ex.printStackTrace();}
            Paragraph pTaxable = new Paragraph().add(taxable).setTextAlignment(TextAlignment.CENTER);
            canvasRectTaxable.add(pTaxable);
            
            //taxable label
            Text taxableLabel = new Text("Imponibile €").setFont(font).setFontSize(8).setBold();
            Paragraph pTaxableLabel = new Paragraph().add(taxableLabel).setTextAlignment(TextAlignment.CENTER);
            canvasRectTaxableLabel.add(pTaxableLabel);
            
            //payment conditions label
            Text paymentConditionsLabel = new Text("  Condizioni di\n         pagamento" ).setFont(font).setFontSize(6).setBold();
            Paragraph pPaymentConditionsLabel = new Paragraph().add(paymentConditionsLabel).setRotationAngle(-((2 * Math.PI) - Math.PI/ 2)).setPaddingRight(3f);
            canvasRectPaymentConditionsLabel.add(pPaymentConditionsLabel).setTextAlignment(TextAlignment.CENTER);
            
            //payment conditions
            Text paymentConditions = new Text(invoice.getString("paymentConditions")).setFont(font).setFontSize(10).setBold();
            Paragraph pPaymentConditions = new Paragraph().add(paymentConditions).setTextAlignment(TextAlignment.LEFT);
            canvasRectPaymentConditions.add(pPaymentConditions);
            
        }
    }
    
    class MyParagraphRenderer extends ParagraphRenderer {
 
        public MyParagraphRenderer(Paragraph modelElement) {
            super(modelElement);
        }
 
        @Override
        public void drawBackground(DrawContext drawContext) {
            Background background = this.<Background>getProperty(Property.BACKGROUND);
            if (background != null) {
                Rectangle bBox = getOccupiedAreaBBox();
                boolean isTagged =
                    drawContext.isTaggingEnabled()
                    && getModelElement() instanceof IAccessibleElement;
                if (isTagged) {
                    drawContext.getCanvas().openTag(new CanvasArtifact());
                }
                Rectangle bgArea = applyMargins(bBox, false);
                if (bgArea.getWidth() <= 0 || bgArea.getHeight() <= 0) {
                    return;
                }
                drawContext.getCanvas().saveState()
                    .setFillColor(background.getColor())
                    .roundRectangle(
                    (double)bgArea.getX() - background.getExtraLeft(),
                    (double)bgArea.getY() - background.getExtraBottom(),
                    (double)bgArea.getWidth()
                        + background.getExtraLeft() + background.getExtraRight(),
                    (double)bgArea.getHeight()
                        + background.getExtraTop() + background.getExtraBottom(),
                    5)
                    .fill().restoreState();
                if (isTagged) {
                    drawContext.getCanvas().closeTag();
                }
            }
        }
    }
}
