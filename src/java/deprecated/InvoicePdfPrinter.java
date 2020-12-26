/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deprecated;

import com.dalessio.samurai.CellBorderDrawer;
import static com.dalessio.samurai.CellBorderType.SOLID;
import com.dalessio.samurai.Config;
import com.dalessio.samurai.CurrencyException;
import com.dalessio.samurai.CurrencyUtility;
import com.dalessio.samurai.DataAccessObject;
import com.dalessio.samurai.DrawCellUtil;
import com.dalessio.samurai.PdfPageNumbersWriter;
import com.dalessio.samurai.RoundedColouredTable;
import com.dps.dbi.DbResult;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
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
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.BorderRadius;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import static com.itextpdf.layout.property.UnitValue.POINT;
import com.itextpdf.layout.property.VerticalAlignment;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;

/**
 *
 * @author Franco
 */
public class InvoicePdfPrinter
{
    public static final String LOGO = "C:\\AppResources\\Samurai\\Logo\\logoDuesse.jpg";
   
    private static final int RIGHT_MARGIN = 10;

    private static final int LEFT_MARGIN = 30;

    private static final int BOTTOM_MARGIN = 10;

    private static final float PAGE_WIDTH = PageSize.A4.getWidth();

    private static final float MIDDLE = LEFT_MARGIN + ( PAGE_WIDTH - RIGHT_MARGIN - LEFT_MARGIN ) / 2;

    private static final int HORIZONTAL_SPACE = 10;

    private static final int CUSTOMER_DATA_TABLE_HEIGHT = 100;

    private static final int CUSTOMER_DATA_TABLE_WIDTH = 235;

    private static final int SHADOW_THICKNESS = 3;
    
    private static final int DOCUMENT_TOP_MARGIN = ( int ) PageSize.A4.getHeight()-485;
    
    private static final int DOCUMENT_BOTTOM_MARGIN = 155;
    
    private static final Color BACKGROUND_GRAY = new DeviceRgb(240, 240, 240);
    
    private static final Color SHADOW_GRAY = new DeviceRgb(80, 80, 80);
    
    //currency utility
    CurrencyUtility cu = CurrencyUtility.getCurrencyUtilityInstance();
    
    //DAO
    DataAccessObject dao;
    
    public InvoicePdfPrinter() throws ClassNotFoundException
    {
        dao = new DataAccessObject();
    }
    
    public void createInvoicePdf( Long invoice_id, DbResult invoice, DbResult invoiceRows) throws IOException, SQLException, ClassNotFoundException
    { 
        //font declaration and file creation pdf create method calling
        //font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
        
        final String DEST = Config.INVOICES_DIR + "INVOICE_" + invoice_id + ".pdf";
        
        DataAccessObject dao = new DataAccessObject();
         
        //pdf document with destination file
        PdfDocument destPdf = new PdfDocument(new PdfWriter(DEST));
        
        //creates  file
        File file = new File( DEST );
        
        //makes directory
        file.getParentFile().mkdirs();
        
        //instance of utility class to print pages numbers that implements the IEventHandler
        PdfPageNumbersWriter pageNumberWriter = new PdfPageNumbersWriter(destPdf, ( float )PAGE_WIDTH-RIGHT_MARGIN-40, (float) BOTTOM_MARGIN  +1 );
                  
        //event handler binding, each end of page places a placeholder
        destPdf.addEventHandler(PdfDocumentEvent.END_PAGE, pageNumberWriter);

        //adds the event handler necessary to manage header and footer infact takes as event parameter END_PAGE
        destPdf.addEventHandler(PdfDocumentEvent.END_PAGE, new InvoiceMyEventHandler( invoice ));

        //adds page to the pdf document this docuemtn manages header and footer
        PageSize ps = PageSize.A4;
        PdfPage page = destPdf.addNewPage(ps);

        //Inititlaize the document that is the content between header and footer
        Document document = new Document(destPdf,ps,true);
        document.setMargins(DOCUMENT_TOP_MARGIN, RIGHT_MARGIN, DOCUMENT_BOTTOM_MARGIN, LEFT_MARGIN);
        //THE TABLE OF DELIVERYNOTE ROWS

        //sets table dimensions
        Table table  = new Table(new UnitValue[]{new UnitValue(POINT,60),new UnitValue(POINT,60),new UnitValue(POINT,355),new UnitValue(POINT,60)});
 
        //table.setMarginLeft( LEFT_MARGIN );
        //iterates deliveryNoteRows to fill the table
        for( int i = 0; i < invoiceRows.rowsCount(); i++)
        {
            //code
            Cell cell = new Cell();
            cell.add(new Paragraph( invoiceRows.getString( i, "code" ) ).setTextAlignment(TextAlignment.CENTER).setFontSize(8)).setBold();
            cell.setBorder(Border.NO_BORDER);
            cell.setVerticalAlignment(VerticalAlignment.BOTTOM );
            cell.setBorderBottom(new SolidBorder(.5f));
            //cell.setMinWidth(60);
            //cell.setMaxWidth(60);
            cell.setMinHeight(13);
            table.addCell(cell);
            //quantity
            cell = new Cell();
            cell.add( new Paragraph( invoiceRows.getInteger( i, "quantity" ).toString()).setTextAlignment(TextAlignment.CENTER).setFontSize(8)).setBold();
            cell.setBorder(Border.NO_BORDER);
            cell.setVerticalAlignment(VerticalAlignment.BOTTOM );
            //cell.setPaddings(0,5,0,0);
            cell.setBorderBottom(new SolidBorder(.5f));
            //cell.setMinWidth(60);
            //cell.setMaxWidth(60);
            table.addCell(cell);
            //description
            cell = new Cell();
            cell.add(new Paragraph( invoiceRows.getString( i, "description" )).setFontSize(8)).setBold();
            cell.setBorder(Border.NO_BORDER);
            cell.setPaddings(0,0,0,5);
            cell.setBorderBottom(new SolidBorder(.5f));
            cell.setVerticalAlignment(VerticalAlignment.BOTTOM );
            //cell.setMinWidth(355);
            //cell.setMaxWidth(355);
            table.addCell(cell);
            //amount
            cell = new Cell();
            Double amount = invoiceRows.getDouble( i, "amount" );
            try{
                cell.add(new Paragraph( cu.getCurrency( amount )).setFontSize(8)).setBold();
            }catch( CurrencyException ex ){}
            cell.setBorder(Border.NO_BORDER);
            cell.setPaddings(0,0,0,5);
            cell.setBorderBottom(new SolidBorder(.5f));
            cell.setVerticalAlignment(VerticalAlignment.BOTTOM );
            cell.setMinWidth(80);
            //cell.setMaxWidth(80);
            table.addCell(cell);

        }

        //adds the table to the document 
        document.add(table);
        //places pages number
        pageNumberWriter.writeTotal(destPdf);
        //closes the document
        document.close();
            
    }
    
    public class InvoiceMyEventHandler implements IEventHandler 
    {
        DbResult invoice;
        //table cell instance
        Cell cell;
        
        public InvoiceMyEventHandler(DbResult deliveryNote ) 
        {
            this.invoice = deliveryNote;
            this.cell = new Cell();
        }
    
        @Override
        public void handleEvent(Event event) 
        {
            //dates utiity vriables
            String dateUtil = "";
            String year = "";
            String month = "";
            String day = "";
            
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
                
                //CREATES TABLE 1X4 FOR COMPANY PRODUCTS
                //first cell content
                Paragraph prgCompanyProducts_1 = new Paragraph();
                Text txtCompanyProducts_1 = new Text("• Cataloghi ricambi\n•Esplosi tecnici\n•Spaccati\n•Prospettive").setFont(font).setFontSize(7).setBold();
                prgCompanyProducts_1.add(txtCompanyProducts_1);
                
                //second cell content
                Paragraph prgCompanyProducts_2 = new Paragraph();
                Text txtCompanyProducts_2 = new Text("•Libretti per istruzioni d'uso\n•Manuali di assistenza e riparazione\n•Schemi di installazione\n•Depliant tecnici").setFont(font).setFontSize(7).setBold();
                prgCompanyProducts_2.add(txtCompanyProducts_2);
                
                //third cell content
                Paragraph prgFattura = new Paragraph().setPaddings(13, 5, 10, 7);
                Text txtFattura = new Text("FATTURA").setFont(font).setFontSize(12).setBold();
                prgFattura.add(txtFattura);
                
                //forth cell content
                Paragraph prgInvoiceNumber = new Paragraph().setPaddings(4,2,2,3);
                Paragraph prgInvoiceDate = new Paragraph().setPaddings(2,2,2,3);
                //formats the date
                dateUtil =   invoice.getString("date");
                if( dateUtil.length() == 8 )
                {
                    year = dateUtil.substring( 0, 4 );
                    month = dateUtil.substring( 4, 6 );
                    day = dateUtil.substring( 6 );
                    dateUtil = day + "/" + month + "/" + year;
                }
                Text txtInvoiceNumber = new Text( "N°     " + invoice.getLong("number") + " / " + invoice.getInteger("year")).setFont(font).setFontSize(10).setBold();
                Text txtInvoiceDate = new Text( "Del     " + dateUtil ).setFont(font).setFontSize(10).setBold();
                prgInvoiceNumber.add(txtInvoiceNumber);
                prgInvoiceDate.add(txtInvoiceDate);
                
                //table
                Table tblCompanyProductsAndInvoiceData = new Table(new float[] {1, 1, 1, 1});
                
                //adds first cell
                cell = new Cell().add(prgCompanyProducts_1);
                cell.setBorder(Border.NO_BORDER);
                cell.setMinWidth(100);
                tblCompanyProductsAndInvoiceData.addCell(cell);
                
                //adds second cell
                cell = new Cell().add(prgCompanyProducts_2);
                cell.setBorder(Border.NO_BORDER);
                cell.setMinWidth( 100);
                tblCompanyProductsAndInvoiceData.addCell(cell);
                
                //adds third cell
                cell = new Cell().add( prgFattura );
                cell.setBorder(Border.NO_BORDER);
                cell.setMinWidth(100);
                cell.setNextRenderer(new DrawCellUtil( cell, new CellBorderDrawer[]{ SOLID, SOLID, SOLID, SOLID }));
                tblCompanyProductsAndInvoiceData.addCell(cell);
                
                //adds forth cell
                cell = new Cell().add( prgInvoiceNumber ).add( prgInvoiceDate );
                cell.setBorder(Border.NO_BORDER);
                cell.setMinWidth(100);
                cell.setNextRenderer(new DrawCellUtil( cell, new CellBorderDrawer[]{ SOLID, SOLID, SOLID, SOLID }));
                tblCompanyProductsAndInvoiceData.addCell(cell);
                
                //table dimensioning
                tblCompanyProductsAndInvoiceData.setHeight(50);
                tblCompanyProductsAndInvoiceData.setMinWidth(420 );
                
                //table positioning
                tblCompanyProductsAndInvoiceData.setFixedPosition(165, 725, 420);
                
                // table showing
                new Canvas(pdfCanvas, pdfDoc, new Rectangle(165, 725, 420, 50)).add(tblCompanyProductsAndInvoiceData);
                
                //CREATES TABLE 1X1 FOR COMPANY DATA
                //cell content
                Paragraph prgCompanyName = new Paragraph();
                Text txtCompanyName =new Text("DUESSE Service s.r.l.\n" ).setFont(font).setFontSize(7).setBold();
                Text contacts = new Text("Via Agusta,51\n21017 Samarate(VA) Italy\nCod.Fisc. - P.IVA 02677820124\nCapitale sociale Euro 10.000,00 i.v.\nRegistro Imprese di Varese 0267820124\nREA VA - 276830\n--------------------------------------------------\nSede legale Via Scipione Ronchetti n.189/2\n21044 Cavaria con Premezzo (VA)\n--------------------------------------------------\nTelefono  +39-0331220913\nTelefax  +39-0331220914\ninfo@duesse.it" ).setFont(font).setFontSize(7);
                prgCompanyName.add(txtCompanyName).add(contacts);
                
                //table
                Table tblCompanyNameAndContacts = new Table(new float[] {1});
                
                //adds first cell
                cell = new Cell().add(prgCompanyName);
                cell.setBorder(Border.NO_BORDER);
                cell.setVerticalAlignment(VerticalAlignment.TOP);
                tblCompanyNameAndContacts.addCell(cell);
                
                //table dimensioning
                
                
                //table positioning
                tblCompanyNameAndContacts.setFixedPosition(30, 619, 155);
                
                // table showing
                new Canvas(pdfCanvas, pdfDoc, new Rectangle(30, 619, 155, 160)).add(tblCompanyNameAndContacts);
                
                //CREATES TABLE 1X1 DESTINATION LABEL
                //cell content
                Text txtCustomerNameVerticalLabel = new Text("Spett").setFont(font).setFontSize(10).setItalic();
                Paragraph prgCustomerNameVerticalLabel = new Paragraph().add(txtCustomerNameVerticalLabel).setRotationAngle(-((2 * Math.PI) - Math.PI/ 2));
                prgCustomerNameVerticalLabel.setPaddingTop(4);
                
                //table
                Table tblDestinationLabel = new Table(new float[] {1});
                
                //adds first cell
                cell = new Cell().add(prgCustomerNameVerticalLabel);
                cell.setBorder(Border.NO_BORDER);
                tblDestinationLabel.addCell(cell);
                
                //table dimensioning
                
                
                //table positioning
                tblDestinationLabel.setFixedPosition(235, 675, 30);
                
                // table showing
                new Canvas(pdfCanvas, pdfDoc, new Rectangle(235, 675, 30, 60)).add(tblDestinationLabel);
                
                //CREATES SHADOW TABLE 1X1 FOR DESTIANATON NAME **************
                //first cell content
                Text txtCustomerName = new Text( invoice.getString("denomination") ).setFont(font).setFontSize(16).setBold();
                Paragraph prgCustomerNameVertical = new Paragraph().add(txtCustomerName).setTextAlignment(TextAlignment.CENTER);
                
                //Table instantiation
                Table tblCustomerData = new Table(new float[] {1});
                
                //cells adding
                //[0][0]
                cell = new Cell().add(prgCustomerNameVertical).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER).setPaddings(0, 0, 0, 0);
                tblCustomerData.addCell(cell);
                
                //table dimensioning
                tblCustomerData.setHeight(50);
                tblCustomerData.setMinWidth(320 );
                
                
                //table positioning
                tblCustomerData.setFixedPosition(260, 670, 320);
                
                //background color and dimensions assigning
                tblCustomerData.setBackgroundColor( BACKGROUND_GRAY);
                //renderer assigning
                tblCustomerData.setNextRenderer(new RoundedColouredTable(tblCustomerData));
                
                //the shadow of the table 
                Table sdwTblCustomerData = new Table(new float[] {1});
                
                //table dimensioning
                sdwTblCustomerData.setHeight(50);
                sdwTblCustomerData.setMinWidth(320 );
                
                
                //table positioning
                sdwTblCustomerData.setFixedPosition(265, 665, 320);
                
                sdwTblCustomerData.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
                //background color and dimensions assigning
                sdwTblCustomerData.setBackgroundColor(SHADOW_GRAY);
                //renderer assigning
                sdwTblCustomerData.setNextRenderer(new RoundedColouredTable(sdwTblCustomerData));
                
                //shows tables
                // table showing
                new Canvas(pdfCanvas, pdfDoc, new Rectangle(260, 665, 320, 50)).add(sdwTblCustomerData);
                
                new Canvas(pdfCanvas, pdfDoc, new Rectangle(265, 670, 320, 50)).add(tblCustomerData);
                
                //CREATES TABLE 1X1 FOR DESTINATION ADDRESS **************
                //first cell content
                Text txtCustomerAddress = new Text( invoice.getString("address") + ", " + invoice.getString("houseNumber")+"\n"+invoice.getString("postalCode") + "    " + invoice.getString("city")+ " ( " + invoice.getString("province") +" )" ).setFont(font).setFontSize(14).setBold();
                Paragraph prgCustomerAddress = new Paragraph().add(txtCustomerAddress).setTextAlignment(TextAlignment.LEFT).setPaddingLeft(5);
                
                //Table instantiation
                Table tblCustomerAddress = new Table(new float[] {1});
                
                //cells adding
                //[0][0]
                cell = new Cell().add(prgCustomerAddress).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER).setPaddings(0, 0, 0, 0);
                tblCustomerAddress.addCell(cell);
                
                //table dimensioning
                tblCustomerAddress.setHeight(50);
                tblCustomerAddress.setMinWidth(320 );
                
                //table positioning
                tblCustomerAddress.setFixedPosition(260, 615, 320);
                
                new Canvas(pdfCanvas, pdfDoc, new Rectangle(260, 615, 320, 50)).add( tblCustomerAddress );
                
                //CREATES TABLE 1X1 FOR VAT CODE LABEL **************
                //cell content
                Text txtVatCodeVerticalLabel = new Text("Partita\n   IVA").setFont(font).setFontSize(6).setItalic();
                Paragraph prgVatCodeLabel = new Paragraph().add(txtVatCodeVerticalLabel).setRotationAngle(-((2 * Math.PI) - Math.PI/ 2)).setTextAlignment(TextAlignment.CENTER);
                
                //Table instantiation
                Table tblVatCodLabel = new Table(new float[] {1});
                tblVatCodLabel.setBorder(Border.NO_BORDER);
                
                //cells adding
                //[0][0]
                cell = new Cell().add(prgVatCodeLabel).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER).setPaddings(0, 0, 0, 0);
                tblVatCodLabel.addCell(cell);
                
                //table dimensioning
                tblVatCodLabel.setHeight(25);
                tblVatCodLabel.setMinWidth(20 );
                
                //table positioning
                tblVatCodLabel.setFixedPosition(30, 570, 20);
                
                //background color and dimensions assigning
                tblVatCodLabel.setBackgroundColor( BACKGROUND_GRAY);
                //renderer assigning
                tblVatCodLabel.setNextRenderer(new RoundedColouredTable(tblVatCodLabel));
                
                new Canvas(pdfCanvas, pdfDoc, new Rectangle(30, 570, 20, 25)).add( tblVatCodLabel );
                
                //CREATES SHADOW TABLE 1X1 FOR VAT CODE  **************
                //cell content
                Text vatCode = new Text(invoice.getString("vatCode")).setFont(font).setFontSize(12).setBold();
                Paragraph pVatCode = new Paragraph().add(vatCode).setTextAlignment(TextAlignment.CENTER);
                
                //Table instantiation
                Table tblVAtCode = new Table(new float[] {1});
                
                //cells adding
                //[0][0]
                cell = new Cell().add( pVatCode ).setVerticalAlignment( VerticalAlignment.MIDDLE ).setBorder(Border.NO_BORDER).setPaddings(0, 0, 0, 0);
                tblVAtCode.addCell(cell);
                
                //table dimensioning
                tblVAtCode.setHeight(20);
                tblVAtCode.setMinWidth(130 );
                
                
                //table positioning
                tblVAtCode.setFixedPosition(50, 575, 130);
                
                //background color and dimensions assigning
                tblVAtCode.setBackgroundColor( Color.WHITE );
                
                //renderer assigning
                tblVAtCode.setNextRenderer(new RoundedColouredTable(tblVAtCode));
                
                //the shadow of the table 
                Table sdwTblVAtCode = new Table(new float[] {1});
                
                //table dimensioning
                sdwTblVAtCode.setHeight(20);
                sdwTblVAtCode.setMinWidth(130 );
                
                
                //table positioning
                sdwTblVAtCode.setFixedPosition(53, 572, 130);
                
                sdwTblVAtCode.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
                //background color and dimensions assigning
                sdwTblVAtCode.setBackgroundColor(SHADOW_GRAY);
                //renderer assigning
                sdwTblVAtCode.setNextRenderer(new RoundedColouredTable(sdwTblVAtCode));
                
                //shows tables
                // table showing
                new Canvas( pdfCanvas, pdfDoc, new Rectangle( 53, 572, 130, 20 ) ).add( sdwTblVAtCode );
                
                new Canvas( pdfCanvas, pdfDoc, new Rectangle( 50, 575, 130, 20 ) ).add( tblVAtCode );
                
                //CREATES TABLE 1X1 FOR DELIVERY NOTES NUMBERS AND DATE LABEL **************
                //cell content
                Text txtDeliveryNoteDateAndNumberLabel = new Text("D.D.T.\nNumero e Data").setFont(font).setFontSize(6).setItalic();
                Paragraph prgDeliveryNoteDateAndNumberLabel = new Paragraph().add(txtDeliveryNoteDateAndNumberLabel).setRotationAngle(-((2 * Math.PI) - Math.PI/ 2)).setTextAlignment(TextAlignment.CENTER);
                
                //Table instantiation
                Table tbltxtDeliveryNoteDateAndNumberLabel = new Table(new float[] {1});
                tbltxtDeliveryNoteDateAndNumberLabel.setBorder(Border.NO_BORDER);
                
                //cells adding
                //[0][0]
                cell = new Cell().add(prgDeliveryNoteDateAndNumberLabel).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER).setPaddings(0, 0, 0, 0);
                tbltxtDeliveryNoteDateAndNumberLabel.addCell(cell);
                
                //table dimensioning
                tbltxtDeliveryNoteDateAndNumberLabel.setHeight( 75 );
                tbltxtDeliveryNoteDateAndNumberLabel.setMinWidth( 20 );
                
                //table positioning
                tbltxtDeliveryNoteDateAndNumberLabel.setFixedPosition(190, 520, 20);
                
                //background color and dimensions assigning
                tbltxtDeliveryNoteDateAndNumberLabel.setBackgroundColor( BACKGROUND_GRAY);
                //renderer assigning
                tbltxtDeliveryNoteDateAndNumberLabel.setNextRenderer(new RoundedColouredTable(tbltxtDeliveryNoteDateAndNumberLabel));
                
                //shows table
                new Canvas(pdfCanvas, pdfDoc, new Rectangle(190, 520, 20, 75)).add( tbltxtDeliveryNoteDateAndNumberLabel );
                
                //CREATES SHADOW TABLE 1X1 FOR DELIVERY NOTE NUMBER AND DATE **************
                //cell content
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
                Paragraph prgDeliveryNotesNumberAndDates = new Paragraph().add(deliveryNotesNumberAndDates).setTextAlignment(TextAlignment.LEFT);
                
                //Table instantiation
                Table tblDeliveryNotesNumberAndDates = new Table(new float[] {1});
                
                //cells adding
                //[0][0]
                cell = new Cell().add( prgDeliveryNotesNumberAndDates ).setVerticalAlignment( VerticalAlignment.MIDDLE ).setBorder(Border.NO_BORDER).setPaddings(8, 0, 0, 8);
                tblDeliveryNotesNumberAndDates.addCell(cell);
                
                //table dimensioning
                tblDeliveryNotesNumberAndDates.setHeight(85);
                tblDeliveryNotesNumberAndDates.setMinWidth( 370 );
                
                //table positioning
                tblDeliveryNotesNumberAndDates.setFixedPosition(212, 523, 370);
                
                //background color and dimensions assigning
                tblDeliveryNotesNumberAndDates.setBackgroundColor( Color.WHITE );
                
                //renderer assigning
                tblDeliveryNotesNumberAndDates.setNextRenderer(new RoundedColouredTable(tblDeliveryNotesNumberAndDates));
                
                //the shadow of the table 
                Table sdwDeliveryNotesNumberAndDates = new Table(new float[] {1});
                
                //table dimensioning
                sdwDeliveryNotesNumberAndDates.setHeight(75);
                sdwDeliveryNotesNumberAndDates.setMinWidth(370 );
                
                
                //table positioning
                sdwDeliveryNotesNumberAndDates.setFixedPosition(215, 520, 370);
                
                sdwDeliveryNotesNumberAndDates.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
                //background color and dimensions assigning
                sdwDeliveryNotesNumberAndDates.setBackgroundColor(SHADOW_GRAY);
                //renderer assigning
                sdwDeliveryNotesNumberAndDates.setNextRenderer(new RoundedColouredTable(sdwDeliveryNotesNumberAndDates));
                
                //shows tables
                // table showing
                new Canvas( pdfCanvas, pdfDoc, new Rectangle( 215, 520, 370, 75 ) ).add( sdwDeliveryNotesNumberAndDates );
                
                new Canvas( pdfCanvas, pdfDoc, new Rectangle( 212, 523, 370, 85 ) ).add( tblDeliveryNotesNumberAndDates );
                
                //CREATES TABLE 1X1 FOR FISCAL CODE LABEL **************
                //cell content
                Text txtFiscalCodeVerticalLabel = new Text("Codice\n   Fiscale").setFont(font).setFontSize(6).setItalic();
                Paragraph prgFiscalCodeVerticalLabel = new Paragraph().add(txtFiscalCodeVerticalLabel).setRotationAngle(-((2 * Math.PI) - Math.PI/ 2)).setTextAlignment(TextAlignment.CENTER);
                
                //Table instantiation
                Table tblFiscalCodLabel = new Table(new float[] {1});
                tblFiscalCodLabel.setBorder(Border.NO_BORDER);
                
                //cells adding
                //[0][0]
                cell = new Cell().add( prgFiscalCodeVerticalLabel ).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER).setPaddings(0, 0, 0, 0);
                tblFiscalCodLabel.addCell(cell);
                
                //table dimensioning
                tblFiscalCodLabel.setHeight(25);
                tblFiscalCodLabel.setMinWidth(20 );
                
                //table positioning
                tblFiscalCodLabel.setFixedPosition(30, 520, 20);
                
                //background color and dimensions assigning
                tblFiscalCodLabel.setBackgroundColor( BACKGROUND_GRAY);
                //renderer assigning
                tblFiscalCodLabel.setNextRenderer(new RoundedColouredTable(tblFiscalCodLabel));
                
                new Canvas(pdfCanvas, pdfDoc, new Rectangle(30, 520, 20, 25)).add( tblFiscalCodLabel );
                
                //CREATES SHADOW TABLE 1X1 FOR FISCAL CODE  **************
                //cell content
                Text fiscalCode = new Text(invoice.getString("fiscalCode")).setFont(font).setFontSize(12).setBold();
                Paragraph pFiscalCode = new Paragraph().add(fiscalCode).setTextAlignment(TextAlignment.CENTER);
                
                //Table instantiation
                Table tblFiscalCode = new Table(new float[] {1});
                
                //cells adding
                //[0][0]
                cell = new Cell().add( pFiscalCode ).setVerticalAlignment( VerticalAlignment.MIDDLE ).setBorder(Border.NO_BORDER).setPaddings(0, 0, 0, 0);
                tblFiscalCode.addCell(cell);
                
                //table dimensioning
                tblFiscalCode.setHeight(20);
                tblFiscalCode.setMinWidth(130 );
                
                
                //table positioning
                tblFiscalCode.setFixedPosition(50, 523, 130);
                
                //background color and dimensions assigning
                tblFiscalCode.setBackgroundColor( Color.WHITE );
                
                //renderer assigning
                tblFiscalCode.setNextRenderer(new RoundedColouredTable(tblFiscalCode));
                
                //the shadow of the table 
                Table sdwTblFiscalCode = new Table(new float[] {1});
                
                //table dimensioning
                sdwTblFiscalCode.setHeight(20);
                sdwTblFiscalCode.setMinWidth(130 );
                
                
                //table positioning
                sdwTblFiscalCode.setFixedPosition(53, 520, 130);
                
                sdwTblFiscalCode.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
                //background color and dimensions assigning
                sdwTblFiscalCode.setBackgroundColor(SHADOW_GRAY);
                //renderer assigning
                sdwTblFiscalCode.setNextRenderer(new RoundedColouredTable(sdwTblFiscalCode));
                
                //shows tables
                // table showing
                new Canvas( pdfCanvas, pdfDoc, new Rectangle( 53, 520, 130, 20 ) ).add( sdwTblFiscalCode );
                
                new Canvas( pdfCanvas, pdfDoc, new Rectangle( 50, 523, 130, 20 ) ).add( tblFiscalCode );
                
                //ITEMS TABLE HEADER 1X3
                //cells contents
                //[0][0] 
                Text txtCode = new Text("Codice" ).setFont(font).setFontSize(10).setBold();
                Paragraph prgCode = new Paragraph().add(txtCode).setTextAlignment(TextAlignment.CENTER).setBackgroundColor(BACKGROUND_GRAY).setBorderRadius( new BorderRadius(5)).setMargins(0, 0, 0, 0).setPadding(2);
                //[0][1]
                Text txtQuantity = new Text("Quantità" ).setFont(font).setFontSize(10).setBold();
                Paragraph prgQuantity = new Paragraph().add(txtQuantity).setTextAlignment(TextAlignment.CENTER).setBackgroundColor(BACKGROUND_GRAY).setBorderRadius( new BorderRadius(5)).setMargins(0, 0, 0, 0).setPadding(2);
                //[0][2] 
                Text txtDescritpion = new Text("Descrizione" ).setFont(font).setFontSize(10).setBold();
                Paragraph prgDescription = new Paragraph().add(txtDescritpion).setTextAlignment(TextAlignment.CENTER).setBackgroundColor(BACKGROUND_GRAY).setBorderRadius( new BorderRadius(5)).setMargins(0, 0, 0, 0).setPadding(1);
                //[0][3] 
                Text txtAmount = new Text("Importo €" ).setFont(font).setFontSize(9).setBold();
                Paragraph prgAmount = new Paragraph().add(txtAmount).setTextAlignment(TextAlignment.CENTER).setBackgroundColor(BACKGROUND_GRAY).setBorderRadius( new BorderRadius(5)).setMargins(0, 0, 0, 0).setPadding(2).setTextAlignment(TextAlignment.CENTER);
                //Table instantiation
                Table tblItems = new Table(new UnitValue[]{new UnitValue(POINT,60),new UnitValue(POINT,60),new UnitValue(POINT,355),new UnitValue(POINT,80)});
                //cells adding
                //[0][0]
                cell = new Cell().add(prgCode).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER);
                cell.setBorderLeft(new SolidBorder(0.5f)).setBorderRight(new SolidBorder(0.5f));
                cell.setPaddings(0,5,0,5);
                tblItems.addCell(cell);
                //[0][1] 
                cell = new Cell().add(prgQuantity).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER);
                cell.setBorderLeft(new SolidBorder(0.5f)).setBorderRight(new SolidBorder(0.5f));
                cell.setPaddings(0,5,0,5);
                tblItems.addCell(cell);
                //[0][2]
                cell = new Cell().add(prgDescription).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER);
                cell.setBorderLeft(new SolidBorder(0.5f)).setBorderLeft(new SolidBorder(0.5f));
                cell.setPaddings(0,5,0,5);
                tblItems.addCell(cell);
                //[0][3]
                cell = new Cell().add(prgAmount).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER);
                cell.setBorderLeft(new SolidBorder(0.5f)).setBorderLeft(new SolidBorder(0.5f));
                cell.setBorderRight(new SolidBorder(0.5f)).setBorderLeft(new SolidBorder(0.5f));
                cell.setPaddings(0,5,0,5);
                tblItems.addCell(cell);
                //sets table height
                tblItems.setMinHeight(25).setMaxHeight(25);
                //shows table
                new Canvas(pdfCanvas, pdfDoc, new Rectangle( 30 , 485 , 555, 25)).add(tblItems);
                
                
                /* FOOTER */
                
                //CREATES SHADOW TABLE 1X1 TOTAL AMOUNT LABEL **************
                
                Text txtTotalAmountLabel = new Text("TOTALE\nFATTURA");
               Paragraph prgTotalAmountLabel = new Paragraph().add( txtTotalAmountLabel ).setTextAlignment( TextAlignment.CENTER ).setFontSize(10).setBold().setFont(font);
                
                //Table instantiation
                Table tblTotalAmountLabel = new Table(new float[] {1});
                
                //cells adding
                //[0][0]
                cell = new Cell().add( prgTotalAmountLabel ).setVerticalAlignment( VerticalAlignment.MIDDLE ).setBorder(Border.NO_BORDER).setPaddings(0, 0, 0, 0);
                tblTotalAmountLabel.addCell(cell);
                
                //table dimensioning
                tblTotalAmountLabel.setHeight(45);
                tblTotalAmountLabel.setMinWidth( 70 );
                
                
                //table positioning
                tblTotalAmountLabel.setFixedPosition(515, 98, 70);
                
                //background color and dimensions assigning
                tblTotalAmountLabel.setBackgroundColor( BACKGROUND_GRAY );
                
                //renderer assigning
                tblTotalAmountLabel.setNextRenderer(new RoundedColouredTable( tblTotalAmountLabel ));
                
                //the shadow of the table 
                Table sdwTblTotalAmountLabel = new Table(new float[] {1});
                
                //table dimensioning
                sdwTblTotalAmountLabel.setHeight(45);
                sdwTblTotalAmountLabel.setMinWidth( 70 );
                
                
                //table positioning
                sdwTblTotalAmountLabel.setFixedPosition(518, 95, 70);
                
                sdwTblTotalAmountLabel.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
                //background color and dimensions assigning
                sdwTblTotalAmountLabel.setBackgroundColor(SHADOW_GRAY);
                //renderer assigning
                sdwTblTotalAmountLabel.setNextRenderer(new RoundedColouredTable(sdwTblTotalAmountLabel));
                
                //shows tables
                // table showing
                new Canvas( pdfCanvas, pdfDoc, new Rectangle( 518, 95, 70, 45 ) ).add( sdwTblTotalAmountLabel );
                
                new Canvas( pdfCanvas, pdfDoc, new Rectangle( 515, 98, 70, 45 ) ).add( tblTotalAmountLabel );
                
                //CREATES SHADOW TABLE 3X1 FOR DUES  **************
                try{
                    //cell content [0][0]
                    Text txtDues_0_0 = new Text( "1-" ).setFont(font).setFontSize(8).setBold();
                    Paragraph prgDues_0_0 = new Paragraph().add( txtDues_0_0 ).setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10);

                    //cell content [0][1]
                    Text txtDues_0_1 = new Text( cu.getCurrency(invoice.getDouble("firstAmount")) ).setFont(font).setFontSize(8).setBold();
                    Paragraph prgDues_0_1 = new Paragraph().add( txtDues_0_1 ).setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10);

                    //cell content [0][2]
                    //formats the date
                    dateUtil =   invoice.getString("firstAmountDate");
                    if( dateUtil.length() == 8 )
                    {
                        year = dateUtil.substring( 0, 4 );
                        month = dateUtil.substring( 4, 6 );
                        day = dateUtil.substring( 6 );
                        dateUtil = day + "/" + month + "/" + year;
                    }
                    Text txtDues_0_2 = new Text( dateUtil ).setFont(font).setFontSize(8).setBold();
                    Paragraph prgDues_0_2 = new Paragraph().add( txtDues_0_2 ).setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10);

                    //cell content [1][0]
                    Text txtDues_1_0 = new Text( "2-" ).setFont(font).setFontSize(8).setBold();
                    Paragraph prgDues_1_0 = new Paragraph().add( txtDues_1_0 ).setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10);

                    //cell content [1][1]
                    Text txtDues_1_1 = new Text( cu.getCurrency( invoice.getDouble( "secondAmount" ) ) ).setFont(font).setFontSize(8).setBold();
                    Paragraph prgDues_1_1 = new Paragraph().add( txtDues_1_1 ).setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10);

                    //cell content [1][2]
                    //formats the date
                    dateUtil =   invoice.getString("secondAmountDate");
                    if( dateUtil.length() == 8 )
                    {
                        year = dateUtil.substring( 0, 4 );
                        month = dateUtil.substring( 4, 6 );
                        day = dateUtil.substring( 6 );
                        dateUtil = day + "/" + month + "/" + year;
                    }
                    Text txtDues_1_2 = new Text( dateUtil ).setFont(font).setFontSize(8).setBold();
                    Paragraph prgDues_1_2 = new Paragraph().add( txtDues_1_2 ).setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10);

                    //cell content [2][0]
                    Text txtDues_2_0 = new Text( "3-" ).setFont(font).setFontSize(8).setBold();
                    Paragraph prgDues_2_0 = new Paragraph().add( txtDues_2_0 ).setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10);

                    //cell content [2][1]
                    Text txtDues_2_1 = new Text( cu.getCurrency( invoice.getDouble( "thirdAmount" ) ) ).setFont(font).setFontSize(8).setBold();
                    Paragraph prgDues_2_1 = new Paragraph().add( txtDues_2_1 ).setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10);

                    //cell content [2][2]
                    //formats the date
                    dateUtil =   invoice.getString("thirdAmountDate");
                    if( dateUtil.length() == 8 )
                    {
                        year = dateUtil.substring( 0, 4 );
                        month = dateUtil.substring( 4, 6 );
                        day = dateUtil.substring( 6 );
                        dateUtil = day + "/" + month + "/" + year;
                    }
                    Text txtDues_2_2 = new Text( dateUtil ).setFont(font).setFontSize(8).setBold();
                    Paragraph prgDues_2_2 = new Paragraph().add( txtDues_2_2 ).setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10);
                
                    //Table instantiation
                    Table tblDues = new Table(new float[] {1,3,3});

                    //cells adding
                    //[0][0]
                    cell = new Cell().add( prgDues_0_0 ).setVerticalAlignment( VerticalAlignment.MIDDLE ).setBorder(Border.NO_BORDER).setPaddings(0, 0, 0, 5);
                    tblDues.addCell(cell);

                    //[0][1]
                    cell = new Cell().add( prgDues_0_1 ).setVerticalAlignment( VerticalAlignment.MIDDLE ).setBorder(Border.NO_BORDER).setPaddings(0, 0, 0, 0);
                    tblDues.addCell(cell);

                    //[0][2]
                    cell = new Cell().add( prgDues_0_2 ).setVerticalAlignment( VerticalAlignment.MIDDLE ).setBorder(Border.NO_BORDER).setPaddings(0, 15, 0, 0);
                    tblDues.addCell(cell);

                    //[1][0]
                    cell = new Cell().add( prgDues_1_0 ).setVerticalAlignment( VerticalAlignment.MIDDLE ).setBorder(Border.NO_BORDER).setPaddings(0, 0, 0, 5);
                    tblDues.addCell(cell);

                    //[1][1]
                    cell = new Cell().add( prgDues_1_1 ).setVerticalAlignment( VerticalAlignment.MIDDLE ).setBorder(Border.NO_BORDER).setPaddings(0, 0, 0, 0);
                    tblDues.addCell(cell);

                    //[1][2]
                    cell = new Cell().add( prgDues_1_2 ).setVerticalAlignment( VerticalAlignment.MIDDLE ).setBorder(Border.NO_BORDER).setPaddings(0, 15, 0, 0);
                    tblDues.addCell(cell);

                     //[2][0]
                    cell = new Cell().add( prgDues_2_0 ).setVerticalAlignment( VerticalAlignment.MIDDLE ).setBorder(Border.NO_BORDER).setPaddings(0, 0, 0, 5);
                    tblDues.addCell(cell);

                     //[2][1]
                    cell = new Cell().add( prgDues_2_1 ).setVerticalAlignment( VerticalAlignment.MIDDLE ).setBorder(Border.NO_BORDER).setPaddings(0, 0, 0, 0);
                    tblDues.addCell(cell);

                     //[2][2]
                    cell = new Cell().add( prgDues_2_2 ).setVerticalAlignment( VerticalAlignment.MIDDLE ).setBorder(Border.NO_BORDER).setPaddings(0, 15, 0, 0);
                    tblDues.addCell(cell);


                    //table dimensioning
                    tblDues.setHeight(50);
                    tblDues.setMinWidth(180 );


                    //table positioning
                    tblDues.setFixedPosition(297, 98, 180);

                    //background color and dimensions assigning
                    tblDues.setBackgroundColor( Color.WHITE );

                    //renderer assigning
                    tblDues.setNextRenderer(new RoundedColouredTable(tblDues));

                    //the shadow of the table 
                    Table sdwTblDues = new Table(new float[] {1});

                    //table dimensioning
                    sdwTblDues.setHeight(50);
                    sdwTblDues.setMinWidth(180 );


                    //table positioning
                    sdwTblDues.setFixedPosition(300, 95, 180);

                    sdwTblDues.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
                    //background color and dimensions assigning
                    sdwTblDues.setBackgroundColor(SHADOW_GRAY);
                    //renderer assigning
                    sdwTblDues.setNextRenderer(new RoundedColouredTable(sdwTblDues));

                    //shows tables
                    // table showing
                    new Canvas( pdfCanvas, pdfDoc, new Rectangle( 300, 95, 180, 50 ) ).add( sdwTblDues );

                    new Canvas( pdfCanvas, pdfDoc, new Rectangle( 297, 98, 180, 50 ) ).add( tblDues );
                
                }catch( CurrencyException ex ){}
                
                //CREATES TABLE 1X1 FOR DUES LABEL **************
                //cell content
                Text txDuesLabel = new Text("Scadenze").setFont(font).setFontSize(8).setBold();
                Paragraph prgDuesLabel = new Paragraph().add(txDuesLabel).setRotationAngle(-((2 * Math.PI) - Math.PI/ 2)).setTextAlignment(TextAlignment.CENTER).setHorizontalAlignment( HorizontalAlignment.CENTER );
                
                //Table instantiation
                Table tblDuesLabel = new Table(new float[] {1});
                tblDuesLabel.setBorder(Border.NO_BORDER);
                
                //cells adding
                //[0][0]
                cell = new Cell().add( prgDuesLabel ).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER).setPaddings(0, 0, 0, 0);
                tblDuesLabel.addCell(cell);
                
                //table dimensioning
                tblDuesLabel.setHeight( 50 );
                tblDuesLabel.setMinWidth( 20 );
                
                //table positioning
                tblDuesLabel.setFixedPosition(280, 95, 20);
                
                //background color and dimensions assigning
                tblDuesLabel.setBackgroundColor( BACKGROUND_GRAY);
                //renderer assigning
                tblDuesLabel.setNextRenderer(new RoundedColouredTable(tblDuesLabel));
                
                new Canvas(pdfCanvas, pdfDoc, new Rectangle(280, 95, 20, 50)).add( tblDuesLabel );
                
                //CREATES SHADOW TABLE 1X1 FOR NOTES  **************
                //cell content
                Text txtNotes = new Text(invoice.getString("notes")).setFont(font).setFontSize(8).setBold();
                Paragraph prgNotes = new Paragraph().add(txtNotes).setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10);
                
                //Table instantiation
                Table tblNotes = new Table(new float[] {1});
                
                //cells adding
                //[0][0]
                cell = new Cell().add( prgNotes ).setVerticalAlignment( VerticalAlignment.MIDDLE ).setBorder(Border.NO_BORDER).setPaddings(0, 0, 0, 0);
                tblNotes.addCell(cell);
                
                //table dimensioning
                tblNotes.setHeight(50);
                tblNotes.setMinWidth(215 );
                
                
                //table positioning
                tblNotes.setFixedPosition(57, 98, 215);
                
                //background color and dimensions assigning
                tblNotes.setBackgroundColor( Color.WHITE );
                
                //renderer assigning
                tblNotes.setNextRenderer(new RoundedColouredTable(tblNotes));
                
                //the shadow of the table 
                Table sdwTblNotes = new Table(new float[] {1});
                
                //table dimensioning
                sdwTblNotes.setHeight(50);
                sdwTblNotes.setMinWidth(215 );
                
                
                //table positioning
                sdwTblNotes.setFixedPosition(60, 95, 215);
                
                sdwTblNotes.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
                //background color and dimensions assigning
                sdwTblNotes.setBackgroundColor(SHADOW_GRAY);
                //renderer assigning
                sdwTblNotes.setNextRenderer(new RoundedColouredTable(sdwTblNotes));
                
                //shows tables
                // table showing
                new Canvas( pdfCanvas, pdfDoc, new Rectangle( 60, 98, 215, 50 ) ).add( sdwTblNotes );
                
                new Canvas( pdfCanvas, pdfDoc, new Rectangle( 57, 95, 215, 50 ) ).add( tblNotes );
                
                //CREATES TABLE 1X1 FOR NOTES LABEL **************
                //cell content
                Text txtNotesLabel = new Text("Note").setFont(font).setFontSize(8).setBold();
                Paragraph prgNotesLabel = new Paragraph().add(txtNotesLabel).setRotationAngle(-((2 * Math.PI) - Math.PI/ 2)).setTextAlignment(TextAlignment.CENTER).setHorizontalAlignment( HorizontalAlignment.CENTER );
                
                //Table instantiation
                Table tblPrgNotesLabelLabel = new Table(new float[] {1});
                tblPrgNotesLabelLabel.setBorder(Border.NO_BORDER);
                
                //cells adding
                //[0][0]
                cell = new Cell().add( prgNotesLabel ).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER).setPaddings(0, 0, 0, 0);
                tblPrgNotesLabelLabel.addCell(cell);
                
                //table dimensioning
                tblPrgNotesLabelLabel.setHeight(50);
                tblPrgNotesLabelLabel.setMinWidth(25 );
                
                //table positioning
                tblPrgNotesLabelLabel.setFixedPosition(30, 95, 25);
                
                //background color and dimensions assigning
                tblPrgNotesLabelLabel.setBackgroundColor( BACKGROUND_GRAY);
                //renderer assigning
                tblPrgNotesLabelLabel.setNextRenderer(new RoundedColouredTable(tblPrgNotesLabelLabel));
                
                new Canvas(pdfCanvas, pdfDoc, new Rectangle(30, 95, 25, 50)).add( tblPrgNotesLabelLabel );
                
                //CREATES TABLE 1X1 FOR EURO LABEL  **************
                //cell content
                Text txtEuroLabel = new Text( "€uro" ).setFont(font).setFontSize( 14 );
                Paragraph prgEuroLabel = new Paragraph().add(txtEuroLabel).setTextAlignment( TextAlignment.CENTER );
                
                //Table instantiation
                Table tblEuroLabel = new Table(new float[] {1});
                
                //cells adding
                //[0][0]
                cell = new Cell().add( prgEuroLabel ).setVerticalAlignment( VerticalAlignment.MIDDLE ).setBorder(Border.NO_BORDER).setPaddings(0, 0, 0, 0);
                tblEuroLabel.addCell(cell);
                
                //table dimensioning
                tblEuroLabel.setHeight(20);
                tblEuroLabel.setMinWidth(40 );
                
                
                //table positioning
                tblEuroLabel.setFixedPosition(525, 90, 40);
                
                //background color and dimensions assigning
                tblEuroLabel.setBackgroundColor( Color.WHITE );
                
                //renderer assigning
                tblEuroLabel.setNextRenderer(new RoundedColouredTable( tblEuroLabel ));
               
                //shows tables
                // table showing

                //new Canvas( pdfCanvas, pdfDoc, new Rectangle( 525, 90, 40, 20 ) ).add( tblEuroLabel );
                
                //CREATES SHADOW TABLE 1X1 TOTAL AMOUNT  **************
                Text txtTotalAmount = new Text("");
                try{
                    //cell content
                    txtTotalAmount = new Text( cu.getCurrency(invoice.getDouble( "totalAmount" ))).setFont(font).setFontSize( 12 ).setBold();
                }catch( CurrencyException ex ){}
                    Paragraph prgTotalAmount = new Paragraph().add( txtTotalAmount ).setTextAlignment( TextAlignment.CENTER );
                
                //Table instantiation
                Table tblTotalAmount = new Table(new float[] {1});
                
                //cells adding
                //[0][0]
                cell = new Cell().add( prgTotalAmount ).setVerticalAlignment( VerticalAlignment.MIDDLE ).setBorder(Border.NO_BORDER).setPaddings(0, 0, 0, 0);
                tblTotalAmount.addCell(cell);
                
                //table dimensioning
                tblTotalAmount.setHeight(45);
                tblTotalAmount.setMinWidth( 100 );
                
                
                //table positioning
                tblTotalAmount.setFixedPosition(485, 43, 100);
                
                //background color and dimensions assigning
                tblTotalAmount.setBackgroundColor( Color.WHITE );
                
                //renderer assigning
                tblTotalAmount.setNextRenderer(new RoundedColouredTable( tblTotalAmount ));
                
                //the shadow of the table 
                Table sdwTblTotalAmount = new Table(new float[] {1});
                
                //table dimensioning
                sdwTblTotalAmount.setHeight(45);
                sdwTblTotalAmount.setMinWidth( 100 );
                
                
                //table positioning
                sdwTblTotalAmount.setFixedPosition(488, 40, 70);
                
                sdwTblTotalAmount.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
                //background color and dimensions assigning
                sdwTblTotalAmount.setBackgroundColor(SHADOW_GRAY);
                //renderer assigning
                sdwTblTotalAmount.setNextRenderer(new RoundedColouredTable(sdwTblTotalAmount));
                
                //shows tables
                // table showing
                new Canvas( pdfCanvas, pdfDoc, new Rectangle( 488, 40, 70, 45 ) ).add( sdwTblTotalAmount );
                
                new Canvas( pdfCanvas, pdfDoc, new Rectangle( 485, 43, 70, 45 ) ).add( tblTotalAmount );
                
                //CREATES SHADOW TABLE 1X1 TAXABLE AMOUNT  **************
                //cell content
                Text txtTaxableAmount = new Text( cu.getCurrency(invoice.getDouble( "taxableAmount" ))).setFont(font).setFontSize( 10 ).setBold();
                Paragraph prgTaxableAmount = new Paragraph().add(txtTaxableAmount).setTextAlignment(TextAlignment.CENTER);
                
                //Table instantiation
                Table tblTaxableAmount = new Table(new float[] {1});
                
                //cells adding
                //[0][0]
                cell = new Cell().add( prgTaxableAmount ).setVerticalAlignment( VerticalAlignment.MIDDLE ).setBorder(Border.NO_BORDER).setPaddings(0, 0, 0, 0);
                tblTaxableAmount.addCell(cell);
                
                //table dimensioning
                tblTaxableAmount.setHeight(20);
                tblTaxableAmount.setMinWidth( 100 );
                
                
                //table positioning
                tblTaxableAmount.setFixedPosition(353, 73, 100);
                
                //background color and dimensions assigning
                tblTaxableAmount.setBackgroundColor( Color.WHITE );
                
                //renderer assigning
                tblTaxableAmount.setNextRenderer(new RoundedColouredTable(tblTaxableAmount));
                
                //the shadow of the table 
                Table sdwTblTaxableAmount = new Table(new float[] {1});
                
                //table dimensioning
                sdwTblTaxableAmount.setHeight(20);
                sdwTblTaxableAmount.setMinWidth( 100 );
                
                
                //table positioning
                sdwTblTaxableAmount.setFixedPosition(355, 70, 100);
                
                sdwTblTaxableAmount.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
                //background color and dimensions assigning
                sdwTblTaxableAmount.setBackgroundColor(SHADOW_GRAY);
                //renderer assigning
                sdwTblTaxableAmount.setNextRenderer(new RoundedColouredTable(sdwTblTaxableAmount));
                
                //shows tables
                // table showing
                new Canvas( pdfCanvas, pdfDoc, new Rectangle( 355, 70, 100, 20 ) ).add( sdwTblTaxableAmount );
                
                new Canvas( pdfCanvas, pdfDoc, new Rectangle( 353, 73, 100, 20 ) ).add( tblTaxableAmount );
                
                //CREATES SHADOW TABLE 1X1 VAT AMOUNT  **************
                //cell content
                Text txtVATAmount = new Text(cu.getCurrency(invoice.getDouble("taxAmount"))).setFont(font).setFontSize(10).setBold();
                Paragraph prgVATAmount = new Paragraph().add(txtVATAmount).setTextAlignment(TextAlignment.CENTER);
                
                //Table instantiation
                Table tblVATAmount = new Table(new float[] {1});
                
                //cells adding
                //[0][0]
                cell = new Cell().add( prgVATAmount ).setVerticalAlignment( VerticalAlignment.MIDDLE ).setBorder(Border.NO_BORDER).setPaddings(0, 0, 0, 0);
                tblVATAmount.addCell(cell);
                
                //table dimensioning
                tblVATAmount.setHeight( 20 );
                tblVATAmount.setMinWidth( 100 );
                
                
                //table positioning
                tblVATAmount.setFixedPosition(353, 43, 100);
                
                //background color and dimensions assigning
                tblVATAmount.setBackgroundColor( Color.WHITE );
                
                //renderer assigning
                tblVATAmount.setNextRenderer(new RoundedColouredTable(tblVATAmount));
                
                //the shadow of the table 
                Table sdwTblVATAmount = new Table(new float[] {1});
                
                //table dimensioning
                sdwTblVATAmount.setHeight(20);
                sdwTblVATAmount.setMinWidth(30 );
                
                
                //table positioning
                sdwTblVATAmount.setFixedPosition(355, 40, 100);
                
                sdwTblVATAmount.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
                //background color and dimensions assigning
                sdwTblVATAmount.setBackgroundColor(SHADOW_GRAY);
                //renderer assigning
                sdwTblVATAmount.setNextRenderer(new RoundedColouredTable(sdwTblVATAmount));
                
                //shows tables
                // table showing
                new Canvas( pdfCanvas, pdfDoc, new Rectangle( 355, 40, 100, 20 ) ).add( sdwTblVATAmount );
                
                new Canvas( pdfCanvas, pdfDoc, new Rectangle( 353, 43, 100, 20 ) ).add( tblVATAmount );
                
                //CREATES TABLE 1X1 FOR  TAXABLE AMOUNT LABEL **************
                //cell content
                Text txtTaxableAmountLabel = new Text("Imponibile").setFont(font).setFontSize(6).setBold();
                Paragraph prgTaxableAmountLabel = new Paragraph().add(txtTaxableAmountLabel).setTextAlignment(TextAlignment.CENTER);
                
                //Table instantiation
                Table tblTaxableAmountLabel = new Table(new float[] {1});
                tblTaxableAmountLabel.setBorder(Border.NO_BORDER);
                
                //cells adding
                //[0][0]
                cell = new Cell().add( prgTaxableAmountLabel ).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER).setPaddings(0, 0, 0, 0);
                tblTaxableAmountLabel.addCell(cell);
                
                //table dimensioning
                tblTaxableAmountLabel.setHeight(20);
                tblTaxableAmountLabel.setMinWidth(50 );
                
                //table positioning
                tblTaxableAmountLabel.setFixedPosition(300, 70, 50);
                
                //background color and dimensions assigning
                tblTaxableAmountLabel.setBackgroundColor( BACKGROUND_GRAY);
                //renderer assigning
                tblTaxableAmountLabel.setNextRenderer(new RoundedColouredTable(tblTaxableAmountLabel));
                
                new Canvas(pdfCanvas, pdfDoc, new Rectangle(300, 70, 50, 20)).add( tblTaxableAmountLabel );
                
                //CREATES TABLE 1X1 FOR VAT LABEL **************
                //cell content
                Text txtVATLabel = new Text("IVA 22%").setFont(font).setFontSize(6).setBold();
                Paragraph prgVATLabel = new Paragraph().add(txtVATLabel).setTextAlignment(TextAlignment.CENTER);
                
                //Table instantiation
                Table tblVATLabel = new Table(new float[] {1});
                tblVATLabel.setBorder(Border.NO_BORDER);
                
                //cells adding
                //[0][0]
                cell = new Cell().add( prgVATLabel ).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER).setPaddings(0, 0, 0, 0);
                tblVATLabel.addCell(cell);
                
                //table dimensioning
                tblVATLabel.setHeight(20);
                tblVATLabel.setMinWidth(30 );
                
                //table positioning
                tblVATLabel.setFixedPosition(300, 40, 50);
                
                //background color and dimensions assigning
                tblVATLabel.setBackgroundColor( BACKGROUND_GRAY);
                //renderer assigning
                tblVATLabel.setNextRenderer(new RoundedColouredTable(tblVATLabel));
                
                new Canvas(pdfCanvas, pdfDoc, new Rectangle(300, 40, 50, 20)).add( tblVATLabel );
                
                //CREATES SHADOW TABLE 1X1 FOR PAYMENT CONDITIONS  **************
                //cell content
                Text txtPaymentConditions = new Text(invoice.getString("paymentConditions")).setFont(font).setFontSize(8).setBold();
                Paragraph prgPaymentConditions = new Paragraph().add(txtPaymentConditions).setTextAlignment(TextAlignment.LEFT).setPaddingLeft(10);
                
                //Table instantiation
                Table tblPaymentConditions = new Table(new float[] {1});
                
                //cells adding
                //[0][0]
                cell = new Cell().add( prgPaymentConditions ).setVerticalAlignment( VerticalAlignment.MIDDLE ).setBorder(Border.NO_BORDER).setPaddings(0, 0, 0, 0);
                tblPaymentConditions.addCell(cell);
                
                //table dimensioning
                tblPaymentConditions.setHeight(50);
                tblPaymentConditions.setMinWidth(215 );
                
                
                //table positioning
                tblPaymentConditions.setFixedPosition(57, 43, 215);
                
                //background color and dimensions assigning
                tblPaymentConditions.setBackgroundColor( Color.WHITE );
                
                //renderer assigning
                tblPaymentConditions.setNextRenderer(new RoundedColouredTable(tblPaymentConditions));
                
                //the shadow of the table 
                Table sdwTbltblPaymentConditions = new Table(new float[] {1});
                
                //table dimensioning
                sdwTbltblPaymentConditions.setHeight(50);
                sdwTbltblPaymentConditions.setMinWidth(215 );
                
                
                //table positioning
                sdwTbltblPaymentConditions.setFixedPosition(60, 40, 215);
                
                sdwTbltblPaymentConditions.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
                //background color and dimensions assigning
                sdwTbltblPaymentConditions.setBackgroundColor(SHADOW_GRAY);
                //renderer assigning
                sdwTbltblPaymentConditions.setNextRenderer(new RoundedColouredTable(sdwTbltblPaymentConditions));
                
                //shows tables
                // table showing
                new Canvas( pdfCanvas, pdfDoc, new Rectangle( 60, 40, 215, 50 ) ).add( sdwTbltblPaymentConditions );
                
                new Canvas( pdfCanvas, pdfDoc, new Rectangle( 57, 43, 215, 50 ) ).add( tblPaymentConditions );
                
                //CREATES TABLE 1X1 FOR PAYMENT CONDITIONS LABEL **************
                //cell content
                Text txtPaymentConditionsLabel = new Text("Condizioni\n  pagamento").setFont(font).setFontSize(6).setBold();
                Paragraph prgPaymentConditionsLabel = new Paragraph().add(txtPaymentConditionsLabel).setRotationAngle(-((2 * Math.PI) - Math.PI/ 2)).setTextAlignment(TextAlignment.CENTER);
                
                //Table instantiation
                Table tblPaymentConditionsLabel = new Table(new float[] {1});
                tblPaymentConditionsLabel.setBorder(Border.NO_BORDER);
                
                //cells adding
                //[0][0]
                cell = new Cell().add( prgPaymentConditionsLabel ).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER).setPaddings(0, 0, 0, 0);
                tblPaymentConditionsLabel.addCell(cell);
                
                //table dimensioning
                tblPaymentConditionsLabel.setHeight(50);
                tblPaymentConditionsLabel.setMinWidth(25 );
                
                //table positioning
                tblPaymentConditionsLabel.setFixedPosition(30, 40, 25);
                
                //background color and dimensions assigning
                tblPaymentConditionsLabel.setBackgroundColor( BACKGROUND_GRAY);
                //renderer assigning
                tblPaymentConditionsLabel.setNextRenderer(new RoundedColouredTable(tblPaymentConditionsLabel));
                
                new Canvas(pdfCanvas, pdfDoc, new Rectangle(30, 40, 25, 50)).add( tblPaymentConditionsLabel );
                
                //CREATES TABLE 1X1 FOR NO DISCOUNTS **************
                //cell content
                Text txtNoDiscount = new Text("Per esigenze amministrative non si accettano arrotondamenti o sconti, preventivamente non concordati.").setFont(font).setFontSize(6).setItalic();
                Paragraph prgNoDiscount = new Paragraph().add(txtNoDiscount).setTextAlignment(TextAlignment.CENTER);
                
                //Table instantiation
                Table tblNoDiscount = new Table(new float[] {1});
                tblNoDiscount.setBorder(Border.NO_BORDER);
                
                //cells adding
                //[0][0]
                cell = new Cell().add(prgNoDiscount).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER).setPaddings(0, 0, 0, 0);
                tblNoDiscount.addCell(cell);
                
                //table dimensioning
                tblNoDiscount.setHeight( 10 );
                tblNoDiscount.setMinWidth( 555 );
                
                //table positioning
                tblNoDiscount.setFixedPosition(30, 26, 555);
                
                //shows table
                new Canvas(pdfCanvas, pdfDoc, new Rectangle(30, 26, 555, 15)).add( tblNoDiscount );
                
                //draws items columns separators
                //first
                 pdfCanvas.moveTo(LEFT_MARGIN ,  PageSize.A4.getHeight() - DOCUMENT_TOP_MARGIN  )
                .lineTo(LEFT_MARGIN , DOCUMENT_BOTTOM_MARGIN )
                .setLineWidth(.5F);
                 //second
                pdfCanvas.moveTo(LEFT_MARGIN + 60,  PageSize.A4.getHeight() - DOCUMENT_TOP_MARGIN  )
                .lineTo(LEFT_MARGIN + 60 , DOCUMENT_BOTTOM_MARGIN )
                .setLineWidth(.5F);
                //third
                pdfCanvas.moveTo(LEFT_MARGIN + 60 + 60 ,  PageSize.A4.getHeight() - DOCUMENT_TOP_MARGIN   )
                .lineTo(LEFT_MARGIN  + 60 + 60 , DOCUMENT_BOTTOM_MARGIN  )
                .setLineWidth(.5F);
                //frth
                pdfCanvas.moveTo(LEFT_MARGIN + 60 + 60 + 355,  PageSize.A4.getHeight() - DOCUMENT_TOP_MARGIN   )
                .lineTo(LEFT_MARGIN  + 60 + 60 +355, DOCUMENT_BOTTOM_MARGIN  )
                .setLineWidth(.5F);
                //fifth
                pdfCanvas.moveTo(LEFT_MARGIN + 60 + 60 + 355  + 80,  PageSize.A4.getHeight() - DOCUMENT_TOP_MARGIN )
                .lineTo(LEFT_MARGIN  + 60 + 60 + 355 + 80, DOCUMENT_BOTTOM_MARGIN )
                .setLineWidth(.5F);
                
                //horizontal line
                pdfCanvas.moveTo(LEFT_MARGIN,  DOCUMENT_BOTTOM_MARGIN)
                .lineTo( PAGE_WIDTH-RIGHT_MARGIN, DOCUMENT_BOTTOM_MARGIN )
                .setLineWidth(.5F);
                
                pdfCanvas.stroke();
                
                    
                
            }catch( IOException | CurrencyException ex ){}
            
        }
    }
    
    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException
    {
        DataAccessObject dao = new DataAccessObject();
        
        InvoicePdfPrinter ipp = new InvoicePdfPrinter();
        
        DbResult dbrInvoice = dao.readInvoices(10103L, null, null, null, null );
        
        DbResult dbrInvoiceRows = dao.readInvoiceRows(10103L);
        
        ipp.createInvoicePdf(10103L, dbrInvoice, dbrInvoiceRows);
    }
}
