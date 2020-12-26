
package deprecated;


import com.dalessio.samurai.Config;
import com.dalessio.samurai.DataAccessObject;
import com.dalessio.samurai.Quote;
import com.dalessio.samurai.SplitTexUtil;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
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
public class QuotePdfPrinter 
{
    /**
     * 
     * @param quote_id
     * @param sourcePath the path of the pdf file of the quote having only descriptions and prices
     * @param destinationPath the path of the proper quote
     * @throws IOException
     * @throws SQLException
     * @throws ClassNotFoundException 
     */
    public void printQuote( Long quote_id, String sourcePath ) throws IOException, SQLException, ClassNotFoundException
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
        normalBold.setFont(PdfFontFactory.createFont( FontConstants.TIMES_BOLD)).setFontSize(10) ;
        
        //Styles
        Style normal = new Style() ;
        normal.setFont(PdfFontFactory.createFont( FontConstants.TIMES_ROMAN)).setFontSize(8).setVerticalAlignment(VerticalAlignment.BOTTOM).setPaddingTop(0).setPaddingBottom(0);
        
        //fetchs data from db
        
        DataAccessObject dao = new DataAccessObject();
        
        Quote quote = dao.readQuote(quote_id);
        
        //destination file naming
        String destinationPath = Config.QUOTES_DIR + "PREVENTIVO_DUESSE_" + quote.number + "_" + IsoDtf.format(quote.date).substring(2,4) + ".pdf";
       
        //creates the destination file
        File file = new File(destinationPath);
        file.getParentFile().mkdirs();
       
        //Initializes the pdf document getting the pdf having only the description
         PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourcePath), new PdfWriter(destinationPath));
         
        Document document = new Document(pdfDoc);
        Rectangle pageSize;
        PdfCanvas canvas;
        
        //source document pages
        int totalPages = pdfDoc.getNumberOfPages();
        
        //for each page adds header footer 
        for (int i = 1; i <= totalPages; i++) {
            
            PdfPage page = pdfDoc.getPage(i);
            pageSize = page.getPageSize();
            canvas = new PdfCanvas(page);
            
            //writes quote number
            Rectangle rec_number = new Rectangle(507, 735, 38, 25);
            PdfCanvas canv_number = new PdfCanvas(page).rectangle(rec_number);
            new Canvas(canv_number, pdfDoc, rec_number).add(new Paragraph().add(new Text(quote.number + "-" + IsoDtf.format(quote.date).substring(2,4))).addStyle(dateNumber).setTextAlignment(TextAlignment.RIGHT));
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
            
            // page x of y
            Rectangle rec_pageNumber = new Rectangle(105, 577, 150, 25);
            PdfCanvas canv_pageNumber = new PdfCanvas(page).rectangle(rec_pageNumber);
            new Canvas(canv_pageNumber, pdfDoc, rec_pageNumber).add(new Paragraph().add(new Text((i) + "      " + totalPages).addStyle(bigNormal)));
            //canv_pageNumber.stroke();
            
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
        
        /*
        Desktop desktop = Desktop.getDesktop();
        desktop.open(file);*/
        
        pdfDoc.close();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException
    {
                new QuotePdfPrinter().printQuote(97L,Config.QUOTES_DIR + "empty_quote.pdf" );
    }
    
}

