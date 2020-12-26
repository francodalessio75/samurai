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
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.BorderRadius;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import static com.itextpdf.layout.property.UnitValue.POINT;
import com.itextpdf.layout.property.VerticalAlignment;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import static com.dalessio.samurai.CellBorderType.*;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.layout.border.SolidBorder;

/**
 *
 * @author Franco
 */
public class DeliveryNotePdfPrinter
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
    
    public void createDeliveryNotePdf(Long deliveryNote_id, DbResult deliveryNote, DbResult deliveryNoteRows, Long user_id) throws IOException, SQLException, ClassNotFoundException
    { 
        //font declaration and file creation pdf create method calling
        font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
        
        final String DEST = Config.DDT_DIR+"DDT_"+deliveryNote_id+".pdf";
        
        DataAccessObject dao = new DataAccessObject();
        
        DbResult user = dao.readUsers(user_id);
        
        String userName = user.getString("firstName") + " " + user.getString("lastName");
        
        //pdf document with destination file
        PdfDocument destPdf = new PdfDocument(new PdfWriter(DEST));
        
        //creates  file
        File file = new File( DEST );
        
        //makes directory
        file.getParentFile().mkdirs();
        
        //instance of utility class to print pages numbers that implements the IEventHandler
        PdfPageNumbersWriter pageNumberWriter;
        
        try
        {
            //instance of utility class to print pages numbers that implements the IEventHandler
            pageNumberWriter = new PdfPageNumbersWriter(destPdf, ( float )PAGE_WIDTH-RIGHT_MARGIN-40, (float) BOTTOM_MARGIN + 2 );

            //event handler binding, each end of page places a placeholder
            destPdf.addEventHandler(PdfDocumentEvent.END_PAGE, pageNumberWriter);

            //adds the event handler necessary to manage header and footer infact takes as event parameter END_PAGE
            destPdf.addEventHandler(PdfDocumentEvent.END_PAGE, new DeliveryNoteMyEventHandler(deliveryNote, userName));

            //adds page to the pdf document this docuemtn manages header and footer
            PageSize ps = PageSize.A4;
            PdfPage page = destPdf.addNewPage(ps);

            //Inititlaize the document that is the content between header and footer
            Document document = new Document(destPdf,ps,true);
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
            pageNumberWriter.writeTotal(destPdf);
            //closes the document
            document.close();
        }
        catch(IOException | RuntimeException ex)
        {
            System.err.println("EXCEPTION CREATING DELIVERY NOTE : "+ex);
            ex.printStackTrace(System.err);
        }
    }
    
    public class DeliveryNoteMyEventHandler implements IEventHandler 
    {
        DbResult deliveryNote;
        //table cell instance
        Cell cell;
        //writing document user
        String userName;
        
        public DeliveryNoteMyEventHandler(DbResult deliveryNote, String userName) 
        {
            this.deliveryNote = deliveryNote;
            this.cell = new Cell();
            this.userName = userName;
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
                Text txtMod = new Text("\nMod. 07-07 Rev.1").setFont(font).setFontSize(8).setBold();
                Paragraph prgCopy = new Paragraph().add(txtMod);
                pdfCanvas.rectangle(rectCopyDestination);
                Canvas cnvCopyDestination = new Canvas(pdfCanvas, pdfDoc, rectCopyDestination);
                cnvCopyDestination.add(prgCopy);
                
                //CREATES TABLE 1X1 FOR PAGE NUMBER
                //cell content
                Paragraph prgPageNumber = new Paragraph();
                Text txtPage =  new Text( "Pagina " + pdfDoc.getPageNumber( page ) + " di " ).setFont( font ).setFontSize( 10 );
                prgPageNumber.add(txtPage).setTextAlignment(TextAlignment.RIGHT);
                
                //table
                Table tblPageNumber = new Table(new float[] {1});
                //tblPageNumber.setWidth(150);
                
                //adds first cell
                cell = new Cell().add(prgPageNumber);
                cell.setBorder(Border.NO_BORDER);
                cell.setVerticalAlignment(VerticalAlignment.TOP);
                tblPageNumber.addCell(cell);
                
                //table dimensioning
                //table positioning
                tblPageNumber.setFixedPosition( PAGE_WIDTH-RIGHT_MARGIN-147, BOTTOM_MARGIN -2 , 100 );
                
                // table showing
                new Canvas(pdfCanvas, pdfDoc, new Rectangle(PAGE_WIDTH-RIGHT_MARGIN-147, BOTTOM_MARGIN -2, 100, 20)).add(tblPageNumber);
                
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
                Text txtUserSign = new Text( userName ).setFont(font).setFontSize(10).setBold();
                Paragraph prgUserSign = new Paragraph().add( txtUserSign );
                cell = new Cell().add(prgUserSign).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.BOTTOM);
                cell.setNextRenderer(new DrawCellUtil( cell, new CellBorderDrawer[]{ null, null, THIN_SOLID_DARK_GRAY, null }));/*underlined*/;
                tblSigns.addCell(cell);
                //[1][1]
                cell = new Cell().add(prgAddresseeLabel).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.BOTTOM);
                tblSigns.addCell(cell);
                //[1][2] epty cell
                tblSigns.addCell(new Cell().setBorder(Border.NO_BORDER));
                //[1][3] cell with operator sign
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
                pdfCanvas.moveTo(LEFT_MARGIN, BOTTOM_MARGIN+25-4)
                .lineTo(PAGE_WIDTH-RIGHT_MARGIN, BOTTOM_MARGIN+25-4)
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
    
    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException
    {
        DataAccessObject dao = new DataAccessObject();
        
        DeliveryNotePdfPrinter ipp = new DeliveryNotePdfPrinter();
        
        DbResult dbrDeliveryNote = dao.readDeliveryNotes(30168L, null, null, null, null, null );
        
        DbResult dbrDeliveryNoteRows = dao.readDeliveryNoteRows(10093L);
        
        ipp.createDeliveryNotePdf(10093L, dbrDeliveryNote, dbrDeliveryNoteRows, 10010L);
    }
}
