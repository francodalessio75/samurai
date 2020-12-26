/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deprecated;

import com.dalessio.samurai.Config;
import com.dalessio.samurai.CurrencyException;
import com.dalessio.samurai.CurrencyUtility;
import com.dalessio.samurai.PdfPageNumbersWriter;
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
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.PdfMerger;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 *
 * @author Franco
 */
public class PdfInvoicePrinter
{
    public static final String LOGO = "C:\\AppResources\\Samurai\\Logo\\logoDuesse.jpg";
    
    private static final int RIGHT_MARGIN = 15;

    private static final int LEFT_MARGIN = 40;

    private static final int BOTTOM_MARGIN = 10;

    private static final float PAGE_WIDTH = PageSize.A4.getWidth();
    
    private static final int CUSTOMER_DATA_TABLE_HEIGHT = 100;

    private static final int CUSTOMER_DATA_TABLE_WIDTH = 235;

    private static final int SHADOW_THICKNESS = 3;
    
    private static final int DOCUMENT_TOP_MARGIN = ( int ) PageSize.A4.getHeight()-497;
    
    private static final int DOCUMENT_BOTTOM_MARGIN = 120;
    
    private static final Color BACKGROUND_GRAY = new DeviceRgb(240, 240, 240);
    
    private static final Color SHADOW_GRAY = new DeviceRgb(80, 80, 80);
    
    //currency utility
    CurrencyUtility cu = CurrencyUtility.getCurrencyUtilityInstance();
    
    public void createInvoicePdf(/*Long invoice_id, DbResult invoice, DbResult invoiceRows*/) throws IOException, CurrencyException
    { 
        //font declaration and file creation pdf create method calling
        //font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
        
        /*files names
        final String SRC0 = Config.INVOICES_DIR + "INVOICE_"+invoice_id+"_"+"0.pdf";
                
        final String SRC1 = Config.INVOICES_DIR + "INVOICE_"+invoice_id+"_"+"1.pdf";
                
        final String DEST = Config.INVOICES_DIR + "INVOICE_"+invoice_id+".pdf";*/
        
        final String SRC0 = Config.INVOICES_DIR + "INVOICE_0.pdf";
                
        final String SRC1 = Config.INVOICES_DIR + "INVOICE_1.pdf";
                
        final String DEST = Config.INVOICES_DIR + "INVOICE_.pdf";
        
        //pdf document with destination file
        PdfDocument destPdf = new PdfDocument(new PdfWriter(DEST));
        
        //creates  files
        File file = new File( DEST );
        File tmpFile0 = new File( SRC0 );
        File tmpFile1 = new File( SRC1 );
        
        //makes directories
        file.getParentFile().mkdirs();
        tmpFile0.getParentFile().mkdirs();
        tmpFile1.getParentFile().mkdirs();
        
        //instantiates a temporary pdfDocument
        PdfDocument tmpPdf = new PdfDocument(new PdfWriter(tmpFile0));
        //instance of utility class to print pages numbers that implements the IEventHandler
        PdfPageNumbersWriter pageNumberWriter = new PdfPageNumbersWriter(tmpPdf, ( float )PAGE_WIDTH-RIGHT_MARGIN-40, (float) BOTTOM_MARGIN);
        
        //creates two copies : Duesse and Customer
        for( int copyNumber = 0; copyNumber < 2; copyNumber++ )
        {    
            try
            {
                //initialiazes the pdf document with the correct temporary file
                if( copyNumber == 0 )
                    tmpPdf = new PdfDocument(new PdfWriter(tmpFile0));
                else if( copyNumber == 1 ) 
                    tmpPdf = new PdfDocument(new PdfWriter(tmpFile1));
                
                //instance of utility class to print pages numbers that implements the IEventHandler
                pageNumberWriter = new PdfPageNumbersWriter(tmpPdf, ( float )PAGE_WIDTH-RIGHT_MARGIN-40, (float) BOTTOM_MARGIN);
                
                //event handler binding, each end of page places a placeholder
                tmpPdf.addEventHandler(PdfDocumentEvent.END_PAGE, pageNumberWriter);
                
                //adds the event handler necessary to manage header and footer infact takes as event parameter END_PAGE
                //tmpPdf.addEventHandler(PdfDocumentEvent.END_PAGE, new InvoiceMyEventHandler(invoice, copyNumber));
                tmpPdf.addEventHandler(PdfDocumentEvent.END_PAGE, new InvoiceMyEventHandler( copyNumber));

                //adds page to the pdf document this docuemtn manages header and footer
                PageSize ps = PageSize.A4;
                PdfPage page = tmpPdf.addNewPage(ps);

                //Inititlaize the document that is the content between header and footer
                Document document = new Document(tmpPdf,ps,true);
                document.setMargins(DOCUMENT_TOP_MARGIN, 0, DOCUMENT_BOTTOM_MARGIN, 0);

                //THE TABLE OF INVOICE ROWS

                /*sets table dimensions
                float[] columnWidths = {40, 40, 415,60};
                Table table = new Table(columnWidths);
                table.setWidth(555);
                table.setMarginLeft(LEFT_MARGIN);
                //iterates invoiceRows to fill the table
                for( int i = 0; i < invoiceRows.rowsCount(); i++)
                {
                    Cell cell = new Cell();
                    cell.add(new Paragraph( invoiceRows.getString(i,"code")).setTextAlignment(TextAlignment.CENTER).setFontSize(8)).setBold();
                    cell.setBorder(Border.NO_BORDER);
                    cell.setBorderBottom(new SolidBorder(.5f));
                    cell.setMinWidth(40);
                    cell.setMinHeight(13);
                    table.addCell(cell);
                    cell = new Cell();
                    cell.add(new Paragraph( invoice.getInteger(i,"quantity").toString()).setTextAlignment(TextAlignment.CENTER).setFontSize(8)).setBold();
                    cell.setBorder(Border.NO_BORDER);
                    cell.setBorderBottom(new SolidBorder(.5f));
                    cell.setMinWidth(40);
                    table.addCell(cell);
                    cell = new Cell();
                    cell.add(new Paragraph( invoice.getString(i,"description")).setMarginLeft(10).setFontSize(8)).setBold();
                    cell.setBorder(Border.NO_BORDER);
                    cell.setBorderBottom(new SolidBorder(.5f));
                    cell.setMinWidth(415);
                    table.addCell(cell);
                    cell = new Cell();
                    cell.add(new Paragraph( cu.getCurrency( invoiceRows.getDouble("amount"))).setTextAlignment(TextAlignment.CENTER).setFontSize(8)).setBold();
                    cell.setBorder(Border.NO_BORDER);
                    cell.setBorderBottom(new SolidBorder(.5f));
                    cell.setMinWidth(60);
                    table.addCell(cell);

                }   
                //adds the table to the document 
                document.add(table);*/
                //places pages number
                pageNumberWriter.writeTotal(tmpPdf);
                //closes the document
                document.close();
            }
            catch(IOException | RuntimeException ex)
            {
                System.err.println("EXCEPTION CREATING INVOICE : "+ex);
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
        
        //closes pdf documets
        firstSourcePdf.close();
        secondSourcePdf.close();
        destPdf.close();
        
        //destroids useless files
        tmpFile0.delete();
        tmpFile1.delete();
    }
    
    public class InvoiceMyEventHandler implements IEventHandler 
    {
        DbResult invoice;
        //table cell instance
        Cell cell;
        //copy text 
        String copyText;
        
        //constructor
        //public InvoiceMyEventHandler(DbResult invoice, int copyNumber) 
        public InvoiceMyEventHandler( int copyNumber) 
        {
            this.invoice = invoice;
            this.cell = new Cell();
            if( copyNumber == 0 )
                this.copyText = "COPIA PER DUESSE";
            else if ( copyNumber == 1 )
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

                    //adds page number 
                    Rectangle rectPageNumber = new Rectangle(PAGE_WIDTH-RIGHT_MARGIN-80, BOTTOM_MARGIN , 40, 20);
                    Text txtPages = new Text("Pagina " + pdfDoc.getPageNumber(page) + " di " ).setFont(font).setFontSize(8);
                    Paragraph prgPages = new Paragraph().add(txtPages);
                    pdfCanvas.rectangle(rectPageNumber);
                    Canvas cnvPageNumber = new Canvas(pdfCanvas, pdfDoc, rectPageNumber);
                    cnvPageNumber.add(prgPages);
                    
                    //CREATES TABLE 1X2 FOR COMPANY ACTIVITIES
                    //first cell content 
                    Paragraph prgCompanyActivities_1 = new Paragraph().setTextAlignment(TextAlignment.LEFT).setVerticalAlignment(VerticalAlignment.BOTTOM);
                    Text txtCompanyActivities_1 = new Text("• Cataloghi ricambi\n•Esplosi tecnici\n•Spaccati\n•Prospettive")
                            .setFont(font).setFontSize(8);
                    prgCompanyActivities_1.add(txtCompanyActivities_1);
                    //second cell content 
                    Paragraph prgCompanyActivities_2 = new Paragraph().setTextAlignment(TextAlignment.LEFT);
                    Text txtCompanyActivities_2 = new Text("•Libretti per istruzioni d'uso\n•Manuali di assistenza e riparazione\n•Schemi di installazione\n•Depliant tecnici")
                            .setFont(font).setFontSize(8);
                    prgCompanyActivities_2.add(txtCompanyActivities_2);
                    //table
                    Table tblCompanyActivities = new Table(new float[] {1, 1});
                    cell = new Cell().add(prgCompanyActivities_1).setTextAlignment(TextAlignment.CENTER);
                    cell.setBorder(Border.NO_BORDER);
                    //cell.setNextRenderer(new DrawCellUtil( cell, new CellBorderDrawer[]{ SOLID, SOLID, SOLID, SOLID }));
                    tblCompanyActivities.addCell(cell);
                    cell = new Cell().add(prgCompanyActivities_2).setTextAlignment(TextAlignment.CENTER);
                    cell.setBorder(Border.NO_BORDER);
                    //cell.setNextRenderer(new DrawCellUtil( cell, new CellBorderDrawer[]{ SOLID, SOLID, SOLID, SOLID }));
                    tblCompanyActivities.addCell(cell);
                    tblCompanyActivities.setFixedPosition(LEFT_MARGIN + 160, 740, 200);
                    tblCompanyActivities.setMaxHeight(50);

                    new Canvas(pdfCanvas, pdfDoc, new Rectangle(LEFT_MARGIN + 160, 740, 200, 100)).add(tblCompanyActivities);
                
                    
                    
                    //draws  lines
                
                    //draws horizontal line ( header )
                    pdfCanvas.moveTo(193, 790)
                    .lineTo(380, 790)
                    .setLineWidth(1);

                    //draws horizontal line ( header )
                    pdfCanvas.moveTo(220, 690)
                    .lineTo(380, 690)
                    .setLineWidth(1);
                    
                    pdfCanvas.stroke();
                    
                }catch( IOException ex ){}
            
      
                
            }
        }
    public static void main(String[] args) throws IOException,CurrencyException
    {
        new PdfInvoicePrinter().createInvoicePdf();
    }
}
