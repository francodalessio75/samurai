
package com.dalessio.samurai;

import com.dps.dbi.DbResult;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 *
 * @author Franco
 */
public class ComplianceCertificatePdfPrinter
{
    private static final String IMG = "C:\\AppResources\\Samurai\\ComplianceCertificates\\emptyComplianceCertificate.jpg";
    
    //DATE MANAGMENT
    private static final DateTimeFormatter ISO_DTF = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ClassNotFoundException, IOException, SQLException
    {
        new ComplianceCertificatePdfPrinter().printComplianceCertificate(80046L);
    }
    
    public static void printComplianceCertificate( Long order_id )throws ClassNotFoundException, SQLException, IOException
    {
        //DATA ACCESS OBJECT
        DataAccessObject dao = new DataAccessObject();
        
        DbResult certificateDbr = dao.readComplianceCertificateDbr( order_id );
        
        //Styles
        Style normalBold = new Style() ;
        normalBold.setFont(PdfFontFactory.createFont( FontConstants.TIMES_ROMAN)).setFontSize(10).setVerticalAlignment(VerticalAlignment.BOTTOM).setPaddingTop(0).setPaddingBottom(0).setBold() ;
        
        Style dateNumber = new Style() ;
        dateNumber.setFont(PdfFontFactory.createFont( FontConstants.TIMES_BOLD)).setFontSize(12) ;
        
        //Styles
        Style normal = new Style() ;
        normalBold.setFont(PdfFontFactory.createFont( FontConstants.TIMES_ROMAN)).setFontSize(10).setVerticalAlignment(VerticalAlignment.BOTTOM).setPaddingTop(0).setPaddingBottom(0) ;
    
        //CREATES THE FILE
        File file = new File( Config.COMPLIANCE_CERTIFICATES_DIR + "MODULO_CONFORMITA_LAVORO_" + certificateDbr.getString("code") + ".pdf" );
        file.getParentFile().mkdirs();
        
        //INITIALIZES THE PDF DOCUMENT
        PdfDocument pdfDoc = new PdfDocument( new PdfWriter( Config.COMPLIANCE_CERTIFICATES_DIR + "MODULO_CONFORMITA_LAVORO_" + certificateDbr.getString("code") + ".pdf" ));
        
        //***** HANDLERS *****
        
        //HANDLER THAT PLACES AN EMPTY QUOTE IMAGE AS WATERMARK FOR EACH NEW DOCUMENT PAGE
        Image img = new Image(ImageDataFactory.create(IMG));
        IEventHandler waterMarkHandler = new ComplianceCertificatePdfPrinter.BackgroundImage(img);
        pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE, waterMarkHandler);
        
        //add a new page to the document
        PdfPage page = pdfDoc.addNewPage();
        
        //fills content
        //writes quote number
        Rectangle rec_number = new Rectangle(480, 736, 55, 25);
        PdfCanvas canv_number = new PdfCanvas(page).rectangle(rec_number);
        new Canvas(canv_number, pdfDoc, rec_number).add(new Paragraph().add(new Text( certificateDbr.getLong("number") + "-" + ISO_DTF.format(LocalDate.parse(certificateDbr.getDate("date").toString().substring(0,10))).substring(2,4))).addStyle(dateNumber).setTextAlignment(TextAlignment.CENTER));
        //canv_number.stroke();

        //writes quote date
        Rectangle rec_date = new Rectangle(476, 716, 58, 25);
        PdfCanvas canv_date = new PdfCanvas(page).rectangle(rec_date);
        new Canvas(canv_date, pdfDoc, rec_date).add(new Paragraph().add(new Text(DTF.format(LocalDate.parse(certificateDbr.getDate("date").toString().substring(0,10)))).addStyle(dateNumber)).setTextAlignment(TextAlignment.CENTER));
        //canv_date.stroke();

        //writes customer denomination
        Rectangle rec_customerDenomination = new Rectangle(310, 658, 255, 50);
        PdfCanvas canv_customer = new PdfCanvas(page).rectangle(rec_customerDenomination);
        //checks length
        List<String> lines = SplitTexUtil.splitText(certificateDbr.getString("customerDenomination"), 27);
        String denomination = "";
        for( String s : lines )
            denomination += s+"\n";
        new Canvas(canv_customer, pdfDoc, rec_customerDenomination).add(new Paragraph().add(new Text(denomination).addStyle(dateNumber)).setTextAlignment(TextAlignment.CENTER).setTextAlignment(TextAlignment.LEFT));
        //canv_customer.stroke();

        //writes customer adress
        Rectangle rec_customerAddress = new Rectangle(310, 638, 255, 23);
        PdfCanvas canv_customerAddress = new PdfCanvas(page).rectangle(rec_customerAddress);
        new Canvas(canv_customerAddress, pdfDoc, rec_customerAddress).add(new Paragraph().add(new Text( certificateDbr.getString("customerAddress") + ", " + certificateDbr.getString("customerHouseNumber")).addStyle(normalBold)));
        //canv_customerAddress.stroke();

        //writes customer postal code, city, province
        Rectangle rec_customerPostCityProvince = new Rectangle(310, 621, 256, 23);
        PdfCanvas canv_customerPostCityProvince = new PdfCanvas(page).rectangle(rec_customerPostCityProvince);
        new Canvas(canv_customerPostCityProvince, pdfDoc, rec_customerPostCityProvince).add(new Paragraph().add(new Text( certificateDbr.getString("customerPostalCode") + "      " + certificateDbr.getString("customerCity") + "  " + certificateDbr.getString("customerProvince") ).addStyle(normalBold)));
        //canv_customerAddress.stroke();

        //writes first title
        Rectangle rec_firstTitle = new Rectangle(380, 583, 60, 25);
        PdfCanvas canv_firstTitle = new PdfCanvas(page).rectangle(rec_firstTitle);
        new Canvas(canv_firstTitle, pdfDoc, rec_firstTitle).add(new Paragraph().add(new Text(certificateDbr.getString("compCertFirstTitle") == null ? "" : certificateDbr.getString("compCertFirstTitle")).addStyle(normalBold)));
        //canv_customerAddress.stroke();

        //writes first assignee
        Rectangle rec_firstForAttention = new Rectangle(420, 583, 180, 25);
        PdfCanvas canv_firstForAttention = new PdfCanvas(page).rectangle(rec_firstForAttention);
        new Canvas(canv_firstForAttention, pdfDoc, rec_firstForAttention).add(new Paragraph().add(new Text( certificateDbr.getString("compCertFirstForAttention") ).addStyle(normalBold)));
        //canv_customerAddress.stroke();

        //writes second title
        Rectangle rec_secondTitle = new Rectangle(380, 568, 60, 25);
        PdfCanvas canv_secondTitle = new PdfCanvas(page).rectangle(rec_secondTitle);
        new Canvas(canv_secondTitle, pdfDoc, rec_secondTitle).add(new Paragraph().add(new Text(certificateDbr.getString("compCertSecondTitle") == null ? "" : certificateDbr.getString("compCertSecondTitle") ).addStyle(normalBold)));
        //canv_customerAddress.stroke();

        //writes second assignee
        Rectangle rec_secondForAttention = new Rectangle(420, 568, 180, 25);
        PdfCanvas canv_secondForAttention = new PdfCanvas(page).rectangle(rec_secondForAttention);
        new Canvas(canv_secondForAttention, pdfDoc, rec_secondForAttention).add(new Paragraph().add(new Text(certificateDbr.getString("compCertSecondForAttention")).addStyle(normalBold)));
        //canv_customerAddress.stroke();
        
        //writes order code
        Rectangle rec_orderCode = new Rectangle(152, 530, 60, 25);
        PdfCanvas canv_orderCode = new PdfCanvas(page).rectangle(rec_orderCode);
        new Canvas(canv_orderCode, pdfDoc, rec_orderCode).add(new Paragraph().add(new Text(certificateDbr.getString("code")).addStyle(normalBold).setFontSize(15)));
        //canv_customerAddress.stroke();
        
        //writes order code
        Rectangle rec_jobCode = new Rectangle(400, 530, 180, 25);
        PdfCanvas canv_jobCode = new PdfCanvas(page).rectangle(rec_jobCode);
        new Canvas(canv_jobCode, pdfDoc, rec_jobCode).add(new Paragraph().add(new Text(certificateDbr.getString("compCertCustomerJobCode")).addStyle(normalBold).setFontSize(15)));
        //canv_customerAddress.stroke();
        
        //retrieves the formatted orderDescription pargraph
        Paragraph paragraph = SplitTexUtil.getParagraph(certificateDbr.getString("compCertOrderDescription"));
        paragraph.setTextAlignment(TextAlignment.JUSTIFIED);
            
        //creates the rectangle
        Rectangle rec_Description = new Rectangle( 80, 430, 480, 60);
        PdfCanvas canvDescription = new PdfCanvas(pdfDoc.getLastPage()).rectangle(rec_Description);
        new Canvas(canvDescription, pdfDoc, rec_Description).add(paragraph);
        //canvDescription.stroke();
        
        // operator sign 
        Rectangle rec_sign = new Rectangle(455, 30, 100, 25);
        PdfCanvas canv_sign = new PdfCanvas(page).rectangle(rec_sign);
        new Canvas(canv_sign, pdfDoc, rec_sign).add(new Paragraph().add(new Text( certificateDbr.getString("creatorFirstName") + " " + certificateDbr.getString("creatorLastName") ).addStyle(normalBold).setFontSize(10)).setTextAlignment(TextAlignment.CENTER));
        //canv_sign.stroke();
        
        //closes docment
        pdfDoc.close();
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
}
