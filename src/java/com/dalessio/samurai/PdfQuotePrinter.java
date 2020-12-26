package com.dalessio.samurai;

import com.dps.dbi.DbResult;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.kernel.utils.PdfMerger;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import com.itextpdf.layout.renderer.CanvasRenderer;
import com.itextpdf.layout.renderer.IRenderer;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class PdfQuotePrinter
{
    private static final String IMG = "C:\\AppResources\\Samurai\\QUOTES\\empty_quote.jpg";
    
    private static final String WHITE = "C:\\AppResources\\Samurai\\DDT\\white.png";
    
    //UTILITIES VARIABLES
    /*
      The idea to make the item description completely shown in a rectangle the in turn
      fit the text is the subsequent:
      the rectangle has a fixed WIDTH and an incremental height.
      The application tries to insert the item description in a miimum size rectangle.
      If the rectangle results full, then its height will be increased by a fixed height offset.
    */
    //Item description rectangle upper left corner coordinate
    private static final int START_Y = 492;
    private static final int START_X = 105;
    //description rectangle height offset in user points
    private static final int HEGHT_OFFSET = 10;
    //the starting rectangle height
    private static final int START_HEIGHT = 15;
    //description rectangle WIDTH
    private static final int WIDTH = 380;
    //description rectangle maximum ordinate lower left corner 
    private static final int MIN_Y = 92;
    //the current lower left corner ordinate that of course can't exceed the max lower left corner ordinate
    private  int currentY = START_Y;
    //rectangle incremental height 
    private int height = START_HEIGHT;
    
    //DATE MANAGMENT
    private static final DateTimeFormatter ISO_DTF = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    
    public static void main(String[] args) throws ClassNotFoundException, IOException, SQLException
    {
        PdfQuotePrinter printer = new PdfQuotePrinter();
        printer.printQuote(10L);
    }
    
    public void printQuote( Long quote_id ) throws IOException, ClassNotFoundException, SQLException
    {
        //white area
        ImageData  whiteImgData = ImageDataFactory.create(WHITE);
        
        //DATA ACCESS OBJECT
        DataAccessObject dao = new DataAccessObject();
        
        //Quote
        Quote quote = dao.readQuote( quote_id );
        
        //Styles
        Style normalBold = new Style() ;
        normalBold.setFont(PdfFontFactory.createFont( FontConstants.TIMES_ROMAN)).setFontSize(10).setVerticalAlignment(VerticalAlignment.BOTTOM).setPaddingTop(0).setPaddingBottom(0).setBold() ;
        
        //Styles
        Style normal = new Style() ;
        normalBold.setFont(PdfFontFactory.createFont( FontConstants.TIMES_ROMAN)).setFontSize(10).setVerticalAlignment(VerticalAlignment.BOTTOM).setPaddingTop(0).setPaddingBottom(0) ;
        
        //CREATES THE FILE
        File file = new File( Config.QUOTES_DIR + "PREVENTIVO_DUESSE_" + quote.number + "_" + ISO_DTF.format(quote.date).substring(2,4) + ".pdf" );
        file.getParentFile().mkdirs();
        
        //INITIALIZES THE PDF DOCUMENT
        PdfDocument pdfDoc = new PdfDocument( new PdfWriter(Config.QUOTES_DIR + "PREVENTIVO_DUESSE_" + quote.number + "_" + ISO_DTF.format(quote.date).substring(2,4) + ".pdf" ));
        
   
        //***** HANDLERS *****
        
        //HANDLER THAT PLACES AN EMPTY QUOTE IMAGE AS WATERMARK FOR EACH NEW DOCUMENT PAGE
        Image img = new Image(ImageDataFactory.create(IMG));
        IEventHandler waterMarkHandler = new PdfQuotePrinter.BackgroundImage(img);
        pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE, waterMarkHandler);
        
        //HANDLER THAT SHOWS ALL FIX CONTENT LIKE CUSTOMER DATA , SUBJECT, ETC. FOR EACH NEW PAGE
        IEventHandler fixedContentHandler = new PdfQuotePrinter.FixedContentHandler( quote );
        pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE, fixedContentHandler);
        
        //add a new page to the document
        pdfDoc.addNewPage();
        
        //ITERATES ALL ITEMS AND SHOWS THEIR DATA
        for( int i = 0; i < quote.rows.size(); i++ )
        {
            //retrieves the description pargraph
            Paragraph paragraph = SplitTexUtil.getParagraph(quote.rows.get(i).description);
            paragraph.setTextAlignment(TextAlignment.JUSTIFIED);
            
            //creates the rectangle
            Rectangle rec_Description = new Rectangle( START_X, currentY, WIDTH, START_HEIGHT);
            PdfCanvas canvDescription = new PdfCanvas(pdfDoc.getLastPage()).rectangle(rec_Description);
            //canvDescription.stroke();
            Canvas canvas = new Canvas(canvDescription, pdfDoc, rec_Description).setBackgroundColor(Color.WHITE, 1);
            MyCanvasRenderer_C03 renderer = new MyCanvasRenderer_C03(canvas);
            canvas.setRenderer(renderer);
            canvas.add(paragraph);
            
            
            //while the description is not completely shown
            while( renderer.isFull() )
            {
                /*This has been absolutely the most difficult point of the class. The problem was that on new page creating the white image 
                  positioned with this differnt way ( thet results correct in DeliveryNotePdfPrinter_1 and InvoicePdfPrinter_1 ):
                    
                    Document document = new Document(pdfDoc);
                    document.add(new Image(whiteImgData).scaleAbsolute(WIDTH, height).setFixedPosition(START_X, currentY));
                
                    here doesn't work! Probably becouse on Docuemnt creating the parameter pdfDoc is related with the wrong page.
                
                    So when a new page is createed dinamically is better a different approach, that is create a canvas that allows to give
                    as parameter a page so it is possible to specify getLastPage. This guarantees that the image will be positioned n the 
                    correct page. Furthermore inthis way we add the image intercepting the content stream of the page ( newContentStreamAfter() ) that is 
                    probably more effectiveness that adding the image on an already pubblished page
                    */
                //puts a white image to cover the current wrong ( too small rectangle )
                PdfCanvas imgCanvas = new PdfCanvas(pdfDoc.getLastPage().newContentStreamAfter(), pdfDoc.getLastPage().getResources(), pdfDoc );
                Rectangle area = new Rectangle( START_X, currentY, WIDTH, height );
                new Canvas( imgCanvas, pdfDoc, area ).add(new Image(whiteImgData));
                
                //updates the ordinate and the rectangle height
                currentY -= HEGHT_OFFSET;
                height += HEGHT_OFFSET;
                
                //checks new rectangle height
                //if the rectangle is too high adds a new page
                if( currentY < MIN_Y )
                {
                    //add a new page to the document
                    pdfDoc.addNewPage();
                    //updates y 
                    currentY = START_Y - ( height - START_HEIGHT );
                }
                
                //changes the rectangle with a new one higher
                rec_Description = new Rectangle( START_X, currentY, WIDTH, height);
                canvDescription = new PdfCanvas(pdfDoc.getLastPage()).rectangle(rec_Description);
                //canvDescription.stroke();
                canvas = new Canvas(canvDescription, pdfDoc, rec_Description).add( paragraph ).setBackgroundColor(Color.WHITE, 1);
                renderer = new MyCanvasRenderer_C03(canvas);
                canvas.setRenderer(renderer);
                canvas.add(paragraph);
            }
            
            //shows amount
            // item amount 
            Rectangle rec_Amount = new Rectangle(490, currentY+7, 70, START_HEIGHT);
            PdfCanvas canv_Amount = new PdfCanvas(pdfDoc.getLastPage()).rectangle(rec_Amount);
            String amount = "€  ";
            
            //ift the rowAmount is zero nothing must be rendered in the pdf
            if( quote.rows.get(i).rowAmount != 0 )
                amount += String.format("%.2f", quote.rows.get(i).rowAmount);
            else 
                amount = "";
            
  
            new Canvas(canv_Amount, pdfDoc, rec_Amount).add(new Paragraph().add( new Text( amount )).addStyle(normalBold).setFontSize(10).setTextAlignment(TextAlignment.RIGHT));
            //canv_Amount.stroke();
            
            //updates rectangle height to start with a new one
            height = START_HEIGHT;
            currentY -= START_HEIGHT;
        }
        
        /* page x of y
            note this is a different technique from the one explained in buidingBlocks book. That one 
            in this context didn't work. I don't know why.
        */
        int n = pdfDoc.getNumberOfPages();
        for (int page = 1; page <= n; page++) {
            //puts a white image to cover the current wrong ( too small rectangle )
            Rectangle hOfYRect = new Rectangle( 105, 577, 150, 25 );
            PdfCanvas xOfYCanvas = new PdfCanvas(pdfDoc.getPage(page).newContentStreamAfter(), pdfDoc.getPage(page).getResources(), pdfDoc ).rectangle(hOfYRect);
            new Canvas( xOfYCanvas, pdfDoc, hOfYRect ).add(new Paragraph().add(new Text( page + "        " + n ).addStyle(normalBold).setFontSize(13)));
     
        }
        
        pdfDoc.close();
    }
    
    public void mergePdf( Long quote_id ) throws IOException, ClassNotFoundException, SQLException
    {
        //gets all quote attachments
        //DATA ACCESS OBJECT
        DataAccessObject dao = new DataAccessObject();
        //gets the quote object
        Quote quote = dao.readQuote(quote_id);
        //destintaion path
        String DEST = Config.QUOTES_DIR + "PDF_UNICO_PREVENTIVO_DUESSE_" + quote.number + "_" + ISO_DTF.format(quote.date).substring(2,4) + ".pdf";
        //DbResult of source attachments, takes only pdf files
        DbResult attachmentsDbr = dao.readQuoteAttachments(quote_id, quote.user_id);
        
        //Initialize PDF document with output intent
        PdfDocument pdf = new PdfDocument(new PdfWriter(DEST));
        PdfMerger merger = new PdfMerger(pdf);
        
        PdfDocument quoteDocument = new PdfDocument(new PdfReader(Config.QUOTES_DIR + "PREVENTIVO_DUESSE_" + quote.number + "_" + ISO_DTF.format(quote.date).substring(2,4) + ".pdf"));
        merger.merge(quoteDocument, 1, quoteDocument.getNumberOfPages());
        quoteDocument.close();
        
        //for each attachment if it is a pdf file insert it in the merger 
        for( int i=0; i < attachmentsDbr.rowsCount(); i++ )
        {
            //if the file is a pdf
            if(attachmentsDbr.getString(i,"currentFileName").contains(".pdf"))
            {
                PdfDocument pdfDocument = new PdfDocument(new PdfReader(Config.QUOTES_ATTACH_DIR+attachmentsDbr.getString(i,"currentFileName")));
                merger.merge(pdfDocument, 1, pdfDocument.getNumberOfPages());
                pdfDocument.close();
            }
        }
         
        pdf.close();
    }
    
    
    /**
     * This event handler says if a ractangle is full. It is used to 
     * understand if an item description fits its rectangle
     */
    private static class MyCanvasRenderer_C03 extends CanvasRenderer
    {
 
        protected boolean full = false;

        MyCanvasRenderer_C03(Canvas canvas) {
            super(canvas);
        }

        @Override
        public void addChild(IRenderer renderer) {
            super.addChild(renderer);
            full = Boolean.TRUE.equals(getPropertyAsBoolean(Property.FULL));
        }

        public boolean isFull() {
            return full;
        }
    }
    
    /**
     * It is an event handler
     * In thi case the event passed to the handler is "a new page is starting"
     * the action is put on the page as watermark the image passed to the constructor
     */
    private static class BackgroundImage implements IEventHandler
    {
 
        protected PdfExtGState gState;
        protected Image img;
 
        //CONSTRUCTOR
        public BackgroundImage(Image img) {
            this.img = img;
            gState = new PdfExtGState().setFillOpacity(1);/*opacity*/
        }
 
        //HANDLER
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
     * this handler on each new page insets the content that is the same for all pages
     */
    private static class FixedContentHandler implements IEventHandler
    {
        //Styles
        Style dateNumber = new Style() ;
        Style normalBold = new Style() ;
        Style bigNormal = new Style() ;
        
        int pageNumber = 0;
        
        Quote quote;
        
        //CONSTRUCTOR
        public FixedContentHandler( Quote quote ) throws IOException
        {
            this.quote = quote;
            dateNumber.setFont(PdfFontFactory.createFont( FontConstants.TIMES_BOLD)).setFontSize(12) ;
            normalBold.setFont(PdfFontFactory.createFont( FontConstants.TIMES_ROMAN)).setFontSize(10).setVerticalAlignment(VerticalAlignment.BOTTOM).setPaddingTop(0).setPaddingBottom(0).setBold() ;
            bigNormal.setFont(PdfFontFactory.createFont( FontConstants.TIMES_BOLD)).setFontSize(15) ;
        }
 
        //HANDLER
        @Override
        public void handleEvent(Event event) {
            
            pageNumber++;
            
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfDocument pdfDoc = docEvent.getDocument();
            PdfPage page = docEvent.getPage();
            
            //writes quote number
            Rectangle rec_number = new Rectangle(500, 735, 55, 25);
            PdfCanvas canv_number = new PdfCanvas(page).rectangle(rec_number);
            new Canvas(canv_number, pdfDoc, rec_number).add(new Paragraph().add(new Text(quote.number + "-" + ISO_DTF.format(quote.date).substring(2,4))).addStyle(dateNumber).setTextAlignment(TextAlignment.CENTER));
            //canv_number.stroke();
            
            //writes quote date
            Rectangle rec_date = new Rectangle(499, 715, 58, 25);
            PdfCanvas canv_date = new PdfCanvas(page).rectangle(rec_date);
            new Canvas(canv_date, pdfDoc, rec_date).add(new Paragraph().add(new Text(DTF.format(quote.date).substring(0,10)).addStyle(dateNumber)).setTextAlignment(TextAlignment.CENTER));
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
            Rectangle rec_firstForAttention = new Rectangle(410, 583, 150, 25);
            PdfCanvas canv_firstForAttention = new PdfCanvas(page).rectangle(rec_firstForAttention);
            new Canvas(canv_firstForAttention, pdfDoc, rec_firstForAttention).add(new Paragraph().add(new Text( quote.firstForAttention ).addStyle(normalBold)));
            //canv_customerAddress.stroke();
            
            //writes second title
            Rectangle rec_secondTitle = new Rectangle(370, 568, 60, 25);
            PdfCanvas canv_secondTitle = new PdfCanvas(page).rectangle(rec_secondTitle);
            new Canvas(canv_secondTitle, pdfDoc, rec_secondTitle).add(new Paragraph().add(new Text(quote.secondTitle).addStyle(normalBold)));
            //canv_customerAddress.stroke();
            
            //writes second assignee
            Rectangle rec_secondForAttention = new Rectangle(410, 568, 150, 25);
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
            
        }
    }
}
