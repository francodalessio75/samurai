package deprecated;


import com.dalessio.samurai.Config;
import com.dalessio.samurai.DataAccessObject;
import com.dalessio.samurai.Quote;
import com.dalessio.samurai.SplitTexUtil;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;



/**
 *
 * @author Franco
 */
public class QuotePdfPrinter_1 
{
    public static final String IMG = "C:\\AppResources\\Samurai\\QUOTES\\empty_quote.jpg";
    
    
    public void printQuote( Long quote_id ) throws IOException, SQLException, ClassNotFoundException
    {
        DateTimeFormatter IsoDtf = DateTimeFormatter.ISO_LOCAL_DATE;
        
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        
        //Styles
        Style bigBold = new Style() ;
        bigBold.setFont(PdfFontFactory.createFont( FontConstants.TIMES_BOLD)).setFontSize(15).setBold() ;
        
        Style bigNormal = new Style() ;
        bigBold.setFont(PdfFontFactory.createFont( FontConstants.TIMES_BOLD)).setFontSize(15) ;
        
        //Styles
        Style dateNumber = new Style() ;
        dateNumber.setFont(PdfFontFactory.createFont( FontConstants.TIMES_BOLD)).setFontSize(12) ;
        
        //Styles
        Style normalBold = new Style() ;
        normalBold.setFont(PdfFontFactory.createFont( FontConstants.TIMES_ROMAN)).setFontSize(10).setVerticalAlignment(VerticalAlignment.BOTTOM).setPaddingTop(0).setPaddingBottom(0).setBold() ;
        
        //Styles
        Style normal = new Style() ;
        normal.setFont(PdfFontFactory.createFont( FontConstants.TIMES_ROMAN)).setFontSize(10).setVerticalAlignment(VerticalAlignment.BOTTOM).setPaddingTop(0).setPaddingBottom(0);
        
        //Data Access Object
        DataAccessObject dao = new DataAccessObject();
        
        //Quote
        Quote quote = dao.readQuote( quote_id );
        
        //useful quantities to lines content insertion
        int firstLine_Y_value = 492;
        int line_Y_offset = 14;
        
        //max number of characters for a description
        int maxChar = 55;
        
        //total document lines
        int totalLines = 0;
        for( int i = 0; i < quote.rows.size(); i++ )
        {
                    //totalLines += SplitTexUtil.getParagraphs( quote.rows.get(i).description, maxChar, 10).size();
        }
        //since the max number of lines of an invoice page is 26 here the document pages number computing
        int pageRows = 31;
        int pages = totalLines/pageRows;
        if( totalLines%pageRows > 0 )
            pages++;
        
        //creates file
        File file = new File( Config.QUOTES_DIR + "PREVENTIVO_DUESSE_" + quote.number + "_" + IsoDtf.format(quote.date).substring(2,4) + ".pdf" );
        file.getParentFile().mkdirs();
        
        //Initialize PDF document
        PdfDocument pdfDoc = new PdfDocument( new PdfWriter(Config.QUOTES_DIR + "PREVENTIVO_DUESSE_" + quote.number + "_" + IsoDtf.format(quote.date).substring(2,4) + ".pdf" ));
        
        //empty invoice image as watermark placed for each new page
        Image img = new Image(ImageDataFactory.create(IMG));
        IEventHandler handler = new TransparentImage(img);
        pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE, handler);
        
        //variable to test if there are enough empty lines in the current page to insert the current item description
        int testPageFilledLines = 0;
        //real current page filled lines
        int pageFilledLines = 0;
        //index of the last inserted item useful to know from wich item to start to write data when a new page begins
        int lastInsertedItem = 0;
        
       
        for( int i = 0; i < pages; i++ )
        {
            //add a new page to the document
            PdfPage page = pdfDoc.addNewPage();
            
            //writes quote number
            Rectangle rec_number = new Rectangle(500, 735, 55, 25);
            PdfCanvas canv_number = new PdfCanvas(page).rectangle(rec_number);
            new Canvas(canv_number, pdfDoc, rec_number).add(new Paragraph().add(new Text(quote.number + "-" + IsoDtf.format(quote.date).substring(2,4))).addStyle(dateNumber).setTextAlignment(TextAlignment.CENTER));
            //canv_number.stroke();
            
            //writes quote date
            Rectangle rec_date = new Rectangle(499, 715, 58, 25);
            PdfCanvas canv_date = new PdfCanvas(page).rectangle(rec_date);
            new Canvas(canv_date, pdfDoc, rec_date).add(new Paragraph().add(new Text(dtf.format(quote.date).substring(0,10)).addStyle(dateNumber)).setTextAlignment(TextAlignment.CENTER));
            //canv_date.stroke();
            
            //writes customer denomination
            Rectangle rec_customerDenomination = new Rectangle(307, 658, 255, 50);
            PdfCanvas canv_customer = new PdfCanvas(page).rectangle(rec_customerDenomination);
            //checks length
            List<String> lines = SplitTexUtil.splitText(quote.customerDenomination, 27);
            String denomination = "";
            for( String s : lines )
                denomination += s+"\n";
            new Canvas(canv_customer, pdfDoc, rec_customerDenomination).add(new Paragraph().add(new Text(denomination).addStyle(dateNumber)).setTextAlignment(TextAlignment.CENTER).setTextAlignment(TextAlignment.LEFT));
            //canv_customer.stroke();
            
            //writes customer adress
            Rectangle rec_customerAddress = new Rectangle(307, 638, 255, 23);
            PdfCanvas canv_customerAddress = new PdfCanvas(page).rectangle(rec_customerAddress);
            new Canvas(canv_customerAddress, pdfDoc, rec_customerAddress).add(new Paragraph().add(new Text(quote.address + ", " + quote.houseNumber).addStyle(normalBold)));
            //canv_customerAddress.stroke();
            
            //writes customer postal code, city, province
            Rectangle rec_customerPostCityProvince = new Rectangle(307, 621, 256, 23);
            PdfCanvas canv_customerPostCityProvince = new PdfCanvas(page).rectangle(rec_customerPostCityProvince);
            new Canvas(canv_customerPostCityProvince, pdfDoc, rec_customerPostCityProvince).add(new Paragraph().add(new Text( quote.postalCode + "      " + quote.city + "  " + quote.province ).addStyle(normalBold)));
            //canv_customerAddress.stroke();
            
            //writes first title
            Rectangle rec_firstTitle = new Rectangle(370, 583, 60, 25);
            PdfCanvas canv_firstTitle = new PdfCanvas(page).rectangle(rec_firstTitle);
            new Canvas(canv_firstTitle, pdfDoc, rec_firstTitle).add(new Paragraph().add(new Text(quote.firstTitle).addStyle(normalBold)));
            //canv_customerAddress.stroke();
            
            //writes first assignee
            Rectangle rec_firstForAttention = new Rectangle(410, 583, 60, 25);
            PdfCanvas canv_firstForAttention = new PdfCanvas(page).rectangle(rec_firstForAttention);
            new Canvas(canv_firstForAttention, pdfDoc, rec_firstForAttention).add(new Paragraph().add(new Text( quote.firstForAttention ).addStyle(normalBold)));
            //canv_customerAddress.stroke();
            
            //writes second title
            Rectangle rec_secondTitle = new Rectangle(370, 568, 60, 25);
            PdfCanvas canv_secondTitle = new PdfCanvas(page).rectangle(rec_secondTitle);
            new Canvas(canv_secondTitle, pdfDoc, rec_secondTitle).add(new Paragraph().add(new Text(quote.secondTitle).addStyle(normalBold)));
            //canv_customerAddress.stroke();
            
            //writes second assignee
            Rectangle rec_secondForAttention = new Rectangle(410, 568, 60, 25);
            PdfCanvas canv_secondForAttention = new PdfCanvas(page).rectangle(rec_secondForAttention);
            new Canvas(canv_secondForAttention, pdfDoc, rec_secondForAttention).add(new Paragraph().add(new Text(quote.secondForAttention).addStyle(normalBold)));
            //canv_customerAddress.stroke();
            
            //writes subject
            Rectangle rec_subject = new Rectangle(110, 520, 450, 50);
            PdfCanvas canv_subject = new PdfCanvas(page).rectangle(rec_subject);
            //checks length
            List<String> subjectLines = SplitTexUtil.splitText(quote.subject, 100);
            String subject = "";
            for( String s : subjectLines )
                subject += s+"\n";
            new Canvas(canv_subject, pdfDoc, rec_subject).add(new Paragraph().add(new Text(subject).addStyle(normalBold)).setTextAlignment(TextAlignment.CENTER).setTextAlignment(TextAlignment.LEFT));
            //canv_customer.stroke();
            
            // operator sign 
            Rectangle rec_sign = new Rectangle(455, 40, 100, 25);
            PdfCanvas canv_sign = new PdfCanvas(page).rectangle(rec_sign);
            new Canvas(canv_sign, pdfDoc, rec_sign).add(new Paragraph().add(new Text( quote.firstName + " " + quote.lastName ).addStyle(normalBold).setFontSize(10)).setTextAlignment(TextAlignment.CENTER));
            //canv_sign.stroke();
            
            /**
             * Uses lastInsertedItem to start the iteration from the first item to be inserted.
             * Uses testPageFilledLines to check if the current item description can be inserted
             * Uses filledLines to know how many lines have been already inserted in the current page
             */
            for( int j = lastInsertedItem; j < quote.rows.size(); j++ )
            {
                //collection of description paragraphs
                        //List<Paragraph> paragraphs = SplitTexUtil.getParagraphs( quote.rows.get(j).description, maxChar, 10 );
                
                //number of lines necessary to insert the description
                                //int descriptionLines = paragraphs.size();
                
                //fist update the test variable
                            //testPageFilledLines += descriptionLines;
                
                //if the test is ok
                if( testPageFilledLines <= pageRows )
                {               
                    /**
                     * creates and fills code rectangle, note: x values of line elements is always the same, while 
                     * y values must be computed, so the y value is : y = ( already filledLines * yOffset ) + firstLineYValue
                     * This is the hieght of the first line of the description
                     */
                    int topItemYValue = (firstLine_Y_value-line_Y_offset*(pageFilledLines));
                    
                    //updates variables for the next iteration
                                    //pageFilledLines += descriptionLines;
                    lastInsertedItem++;
                    
                    /* this is the height of the last line of the description*/
                    int bottomItemYVAlue = (firstLine_Y_value-line_Y_offset*(pageFilledLines))+ 2 * line_Y_offset;
                    
                                /*for each collection paragraph creates a new text line
                                for( int m=0; m < paragraphs.size(); m++ )
                                {
                                    Rectangle rec_Line = new Rectangle(105, topItemYValue-(line_Y_offset*m), 380, 20);
                                    PdfCanvas canv_Line = new PdfCanvas(page).rectangle(rec_Line);
                                    new Canvas(canv_Line, pdfDoc, rec_Line).add(paragraphs.get(m));
                                    //canv_Line.stroke();
                                }*/
                    
                    // item amount 
                    Rectangle rec_Amount = new Rectangle(490, bottomItemYVAlue, 70, 20);
                    PdfCanvas canv_Amount = new PdfCanvas(page).rectangle(rec_Amount);
                    //new Canvas(canv_Amount, pdfDoc, rec_Amount).add(new Paragraph().add(new Text(String.format( "%.2f", quote.rows.get(j).rowAmount)).addStyle(normalBold).setFontSize(10)).setTextAlignment(TextAlignment.RIGHT));
                    //canv_Amount.stroke();
                }
                //else a new page must be inserted
                else
                {
                    testPageFilledLines = 0;
                    pageFilledLines = 0;
                    break;
                }
            }
            
            // page x of y
            Rectangle rec_pageNumber = new Rectangle(105, 577, 150, 25);
            PdfCanvas canv_pageNumber = new PdfCanvas(page).rectangle(rec_pageNumber);
            new Canvas(canv_pageNumber, pdfDoc, rec_pageNumber).add(new Paragraph().add(new Text( (i+1) + "       " + pages).addStyle(bigNormal)) );
            //canv_pageNumber.stroke();
            
        }
        
        /*
        Desktop desktop = Desktop.getDesktop();
        desktop.open(file);*/
        
        pdfDoc.close();
    }
    
    
    protected class PageXofY implements IEventHandler {
 
        protected PdfFormXObject placeholder;
        protected float side = 20;
        protected float x = 300;
        protected float y = 25;
        protected float space = 4.5f;
        protected float descent = 3;
 
        public PageXofY(PdfDocument pdf) {
            placeholder = new PdfFormXObject(new Rectangle(0, 0, side, side));
        }
 
        @Override
        public void handleEvent(Event event) {
            
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;/* the event id the END_PAGE*/
            
            PdfDocument pdf = docEvent.getDocument();
            
            PdfPage page = docEvent.getPage();
            
            int pageNumber = pdf.getPageNumber(page);
            
            Rectangle pageSize = page.getPageSize();
            
            PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdf);
            
            Canvas canvas = new Canvas(pdfCanvas, pdf, pageSize);
            
            Paragraph p = new Paragraph().add("Page ").add(String.valueOf(pageNumber)).add(" of");
            
            canvas.showTextAligned(p, x, y, TextAlignment.RIGHT);
            
            pdfCanvas.addXObject(placeholder, x + space, y - descent);
            
            pdfCanvas.release();
        }
 
        public void writeTotal(PdfDocument pdf) {
            
            Canvas canvas = new Canvas(placeholder, pdf);
            
            canvas.showTextAligned(String.valueOf(pdf.getNumberOfPages()),0, descent, TextAlignment.LEFT);
            
        }
    }
    
    protected class TransparentImage implements IEventHandler {
 
        protected PdfExtGState gState;
        protected Image img;
 
        public TransparentImage(Image img) {
            this.img = img;
            gState = new PdfExtGState().setFillOpacity(1);/*opacity*/
        }
 
        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfDocument pdf = docEvent.getDocument();
            PdfPage page = docEvent.getPage();
            Rectangle pageSize = page.getPageSize();
            PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdf);
            pdfCanvas.saveState().setExtGState(gState);
            Canvas canvas = new Canvas(pdfCanvas, pdf, page.getPageSize());
            canvas.add(img.scaleAbsolute(pageSize.getWidth(), pageSize.getHeight()));
            pdfCanvas.restoreState();
            pdfCanvas.release();
        }
    }

    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException
    {
        QuotePdfPrinter_1 printer = new QuotePdfPrinter_1();
        printer.printQuote(10091L);
    }
    
}
