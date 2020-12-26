/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dalessio.samurai;

import static com.dalessio.samurai.SplitTexUtil.splitText;
import com.dps.dbi.DbResult;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.image.ImageData;
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
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Franco
 * 
 *
 */
public class DeliveryNotePdfPrinter_1
{
    public static final String IMG = "C:\\AppResources\\Samurai\\DDT\\emptyDDT.jpg";
    
    private  final String WHITE = "C:\\AppResources\\Samurai\\DDT\\white.png";
    
    public void printDeliveryNote( Long deliveryNote_id ) throws IOException, SQLException, ClassNotFoundException
    {
        //white area
        ImageData  whiteImgData = ImageDataFactory.create(WHITE);
        
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
        normalBold.setFont(PdfFontFactory.createFont( FontConstants.TIMES_ROMAN)).setFontSize(9).setVerticalAlignment(VerticalAlignment.BOTTOM).setPaddingTop(0).setPaddingBottom(0).setBold() ;
        
        //Styles
        Style normal = new Style() ;
        normal.setFont(PdfFontFactory.createFont( FontConstants.TIMES_ROMAN)).setFontSize(8).setVerticalAlignment(VerticalAlignment.BOTTOM).setPaddingTop(0).setPaddingBottom(0);
        
        //Data Access Object
        DataAccessObject dao = new DataAccessObject();
        
        //DeliveryNote data
        DbResult deliveryNoteDbr = dao.readDeliveryNotes(deliveryNote_id, null, null, null, null,null);
        DbResult deliveryNoteItems = dao.readDeliveryNoteRows(deliveryNote_id);
        
        //useful quantities to lines content insertion
        int firstLine_Y_value = 510;
        int line_Y_offset = 12;
        
        //max number of characters for a description
        int maxChar = 70;
        
        //total document lines
        int totalLines = 0;
        for( int i = 0; i < deliveryNoteItems.rowsCount(); i++ )
        {
            totalLines += SplitTexUtil.splitText(deliveryNoteItems.getString(i,"description"), maxChar).size();
        }
        //since the max number of lines of a delivery note page is 34 here the document pages number computing
        int pages = totalLines/34;
        if( totalLines%34 > 0 )
            pages++;
        
        //creates file
//        File file = new File(Config.DDT_DIR + "DDT_" + deliveryNote_id + ".pdf");
        File file = new File(Config.DDT_DIR + "DDT_DUESSE_" + deliveryNoteDbr.getInteger("number") + "_" + deliveryNoteDbr.getInteger("year") + ".pdf");
        file.getParentFile().mkdirs();
        
        //Initialize PDF document
        PdfDocument pdfDoc = new PdfDocument( new PdfWriter(file));
        
        //empty invoice image as watermark placed for each new page
        Image img = new Image(ImageDataFactory.create(IMG));
        IEventHandler handler = new DeliveryNotePdfPrinter_1.TransparentImage(img);
        pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE, handler);
        
        //variable to test if there are enough empty lines in the current page to insert the current item description
        int testPageFilledLines = 0;
        //real current page filled lines
        int filledLines = 0;
        //index of the last inserted item useful to know from wich item to start to write data when a new page begins
        int lastInsertedItem = 0;
        
        /**
           Iterates pages number and for each iteration:
           * 1) inserts a new page;
           * 2) writes header data;
           * 3) fills page lines;
           * 4) if it is the last page, writes summary data
         */
        for( int i = 0; i < pages; i++ )
        {
            //add a new page to the document
            PdfPage page = pdfDoc.addNewPage();
            
            //hides COPIA CLIENTE
            Document document = new Document(pdfDoc);
            document.add(new Image(whiteImgData).scaleAbsolute(200, 44).setFixedPosition(480, 775));
            
            //writes ddt number
            Rectangle rec_number = new Rectangle(505, 750, 30, 25);
            PdfCanvas canv_number = new PdfCanvas(page).rectangle(rec_number);
            new Canvas(canv_number, pdfDoc, rec_number).add(new Paragraph().add(new Text(deliveryNoteDbr.getInteger("number")+"").addStyle(dateNumber)));
            //canv_number.stroke();
            
            //writes ddt year
            Rectangle rec_year = new Rectangle(545, 750, 20, 25);
            PdfCanvas canv_year = new PdfCanvas(page).rectangle(rec_year);
            new Canvas(canv_year, pdfDoc, rec_year).add(new Paragraph().add(new Text(deliveryNoteDbr.getInteger("year")+"").addStyle(dateNumber)));
            //canv_year.stroke();
            
            //writes ddt date
            Rectangle rec_date = new Rectangle(497, 730, 58, 25);
            PdfCanvas canv_date = new PdfCanvas(page).rectangle(rec_date);
            new Canvas(canv_date, pdfDoc, rec_date).add(new Paragraph().add(new Text(deliveryNoteDbr.getString("date").substring(6)+"/"+deliveryNoteDbr.getString("date").substring(4,6)+"/"+deliveryNoteDbr.getString("date").substring(0,4)).addStyle(dateNumber)));
            //canv_date.stroke();
            
            //writes customer denomination
            Rectangle rec_customerDenomination = new Rectangle(90, 690, 200, 35);
            PdfCanvas canv_customer = new PdfCanvas(page).rectangle(rec_customerDenomination);
            //checks length
            List<String> lines = splitText(deliveryNoteDbr.getString("denomination").replace("'", "\'"), 33);
            String denomination = "";
            for( String s : lines )
                denomination += s+"\n";
            new Canvas(canv_customer, pdfDoc, rec_customerDenomination).add(new Paragraph().add(new Text(denomination).addStyle(dateNumber)).setTextAlignment(TextAlignment.LEFT).setFixedLeading(12));
            //canv_customer.stroke();
            
            //writes customer adress
            Rectangle rec_customerAddress = new Rectangle(90, 655, 190,42);
            PdfCanvas canv_customerAddress = new PdfCanvas(page).rectangle(rec_customerAddress);
             //checks length
            List<String> lines_12 = splitText(deliveryNoteDbr.getString("address")+", "+deliveryNoteDbr.getString("houseNumber"), 35);
            String destAddress_1 = "";
            for( String s : lines_12 )
                destAddress_1 += s+"\n";
            new Canvas(canv_customerAddress, pdfDoc, rec_customerAddress).add(new Paragraph().add(new Text(destAddress_1).setFontSize(12)).setFixedLeading(11));
            //canv_customerAddress.stroke();
            
            //writes postal code
            Rectangle rec_customerPostalCode = new Rectangle(90, 651, 50, 23);
            PdfCanvas canv_customerPostalCode = new PdfCanvas(page).rectangle(rec_customerPostalCode);
            new Canvas(canv_customerPostalCode, pdfDoc, rec_customerPostalCode).add(new Paragraph().add(new Text(deliveryNoteDbr.getString("postalCode")).addStyle(bigNormal)));
            //canv_customerPostalCode.stroke();
            
            //writes customer city and province
            Rectangle rec_customerCityProvince = new Rectangle(145, 638, 135, 35);
            PdfCanvas canv_CityProvince = new PdfCanvas(page).rectangle(rec_customerCityProvince);
            //checks length
            List<String> lines_1 = splitText(deliveryNoteDbr.getString("city")+"\n"+deliveryNoteDbr.getString("province"), 25);
            String cityProvince = "";
            for( String s : lines_1 )
                cityProvince += s+"\n";
            
            new Canvas(canv_CityProvince, pdfDoc, rec_customerCityProvince).add(new Paragraph().add(new Text(cityProvince).addStyle(bigNormal)).setTextAlignment(TextAlignment.LEFT).setFixedLeading(12));
            //canv_CityProvince.stroke();
            
            //writes dest denomination
            Rectangle rec_destDenomination = new Rectangle(366, 691, 200, 35);
            PdfCanvas canv_dest = new PdfCanvas(page).rectangle(rec_destDenomination);
            //checks length
            List<String> lines_2 = splitText(deliveryNoteDbr.getString("destDenomination"), 33);
            String destDenomination = "";
            for( String s : lines_2 )
                destDenomination += s+"\n";
            new Canvas(canv_dest, pdfDoc, rec_destDenomination).add(new Paragraph().add(new Text(destDenomination).addStyle(dateNumber)).setTextAlignment(TextAlignment.LEFT).setFixedLeading(12));
            //canv_dest.stroke();
            
            //writes dest adress
            Rectangle rec_destAddress = new Rectangle(366, 658, 193,42);
            PdfCanvas canv_destAddress = new PdfCanvas(page).rectangle(rec_destAddress);
            //checks length
            List<String> lines_3 = splitText(deliveryNoteDbr.getString("destAddress")+", "+deliveryNoteDbr.getString("destHouseNumber"), 35);
            String destAddress = "";
            for( String s : lines_3 )
                destAddress += s+"\n";
            new Canvas(canv_destAddress, pdfDoc, rec_destAddress).add(new Paragraph().add(new Text(destAddress).addStyle(bigNormal).setFontSize(12)).setFixedLeading(11));
            //canv_destAddress.stroke();
            
            //writes dest postal code
            Rectangle rec_destPostalCode = new Rectangle(366, 652, 50, 23);
            PdfCanvas canv_destPostalCode = new PdfCanvas(page).rectangle(rec_destPostalCode);
            new Canvas(canv_destPostalCode, pdfDoc, rec_destPostalCode).add(new Paragraph().add(new Text(deliveryNoteDbr.getString("destPostalCode")).addStyle(bigNormal)));
            //canv_destPostalCode.stroke();
            
            //writes dest city and province
            Rectangle rec_destCityProvince = new Rectangle(421, 639, 135, 35);
            PdfCanvas canv_destCityProvince = new PdfCanvas(page).rectangle(rec_destCityProvince);
            //checks length
            List<String> lines_4 = splitText(deliveryNoteDbr.getString("destCity")+"\n"+deliveryNoteDbr.getString("destProvince"), 25);
            String destCityProvince = "";
            for( String s : lines_4 )
                destCityProvince += s+"\n";
            new Canvas(canv_destCityProvince, pdfDoc, rec_destCityProvince).add(new Paragraph().add(new Text(destCityProvince).addStyle(bigNormal)).setTextAlignment(TextAlignment.LEFT).setFixedLeading(12));
            //canv_destCityProvince.stroke();
            
            //writes transport responsable
            Rectangle rec_transportResponsable = new Rectangle(185, 610, 112, 23);
            PdfCanvas canv_transportResponsable = new PdfCanvas(page).rectangle(rec_transportResponsable);
            new Canvas(canv_transportResponsable, pdfDoc, rec_transportResponsable).add(new Paragraph().add(new Text(deliveryNoteDbr.getString("transportResponsable")).addStyle(dateNumber)).setTextAlignment(TextAlignment.CENTER));
            //canv_transportResponsable.stroke();
            
            //writes transport reason
            Rectangle rec_transportReason = new Rectangle(185, 590, 112, 23);
            PdfCanvas canv_transportReason = new PdfCanvas(page).rectangle(rec_transportReason);
            new Canvas(canv_transportReason, pdfDoc, rec_transportReason).add(new Paragraph().add(new Text(deliveryNoteDbr.getString("transportReason")).addStyle(dateNumber)).setTextAlignment(TextAlignment.CENTER));
            //canv_transportReason.stroke();
            
            //writes goods exterior aspect
            Rectangle rec_goodsExteriorAspect = new Rectangle(185, 570, 112, 23);
            PdfCanvas canv_goodsExteriorAspect = new PdfCanvas(page).rectangle(rec_goodsExteriorAspect);
            new Canvas(canv_goodsExteriorAspect, pdfDoc, rec_goodsExteriorAspect).add(new Paragraph().add(new Text(deliveryNoteDbr.getString("goodsExteriorAspect")).addStyle(dateNumber)).setTextAlignment(TextAlignment.CENTER));
            //canv_goodsExteriorAspect.stroke();
            
            //writes goods packages number
            Rectangle rec_packagesNumber = new Rectangle(100, 553, 60, 23);
            PdfCanvas canv_packagesNumber = new PdfCanvas(page).rectangle(rec_packagesNumber);
            new Canvas(canv_packagesNumber, pdfDoc, rec_packagesNumber).add(new Paragraph().add(new Text(deliveryNoteDbr.getInteger("packagesNumber") == null? "" : deliveryNoteDbr.getInteger("packagesNumber")+"" ).addStyle(dateNumber)).setTextAlignment(TextAlignment.CENTER));
            //canv_packagesNumber.stroke();
            
            //writes goods weight
            Rectangle rec_weight = new Rectangle(245, 553, 50, 23);
            PdfCanvas canv_weight = new PdfCanvas(page).rectangle(rec_weight);
            new Canvas(canv_weight, pdfDoc, rec_weight).add(new Paragraph().add(new Text(deliveryNoteDbr.getDouble("weight") == null? "" : String.format( "%.2f",deliveryNoteDbr.getDouble("weight"))).addStyle(normal)).setTextAlignment(TextAlignment.RIGHT));
            //canv_weight.stroke();
                       
            /**
             * Uses lastInsertedItem to start the iteration from the first item to be inserted.
             * Uses testPageFilledLines to check if the current item description can be inserted
             * Uses filledLines to know how many lines have been already inserted in the current page
             */
            for( int j = lastInsertedItem; j < deliveryNoteItems.rowsCount(); j++ )
            {
                //number of lines necessary to insert the description
                int descriptionLines = splitText(deliveryNoteItems.getString(j, "description"), maxChar).size();
                //fist update the test variable
                testPageFilledLines += descriptionLines;
                //if the test is ok
                if( testPageFilledLines <= 34 )
                {
                    /**
                     * creates and fills code rectangle, note: x values of line elements is always the same, while 
                     * y values must be computed, so the y value is : y = ( already filledLines * yOffset ) + firstLineYValue
                     */
                    int yValue = (firstLine_Y_value-line_Y_offset*(filledLines));
                    
                    //updates variables for the next iteration
                    filledLines += descriptionLines;
                    lastInsertedItem++;
                    
                    //code 
                    Rectangle rec_Code = new Rectangle(43, yValue, 40, 20);
                    PdfCanvas canv_Code = new PdfCanvas(page).rectangle(rec_Code);
                    new Canvas(canv_Code, pdfDoc, rec_Code).add(new Paragraph().add(new Text(deliveryNoteItems.getString(j,"code")).addStyle(normalBold).setFontSize(10)).setTextAlignment(TextAlignment.RIGHT));
                    //canv_Code.stroke();
                    
                    //quantity 
                    Rectangle rec_Quantity = new Rectangle(100, yValue, 40, 20);
                    PdfCanvas canv_Quantity = new PdfCanvas(page).rectangle(rec_Quantity);
                    new Canvas(canv_Quantity, pdfDoc, rec_Quantity).add(new Paragraph().add(new Text(deliveryNoteItems.getInteger(j,"quantity")+"").addStyle(normalBold).setFontSize(10)).setTextAlignment(TextAlignment.RIGHT));
                    //canv_Quantity.stroke();
                    
                    /**
                     * description: creates the List of description lines. Iterating the list fills the rows
                     */
                    List<String> rows = splitText(deliveryNoteItems.getString(j, "description"), maxChar);
                    for( int k=0; k < rows.size(); k++ )
                    {
                        Rectangle rec_Line = new Rectangle(156, yValue-(line_Y_offset*k), 410, 25);
                        PdfCanvas canv_Line = new PdfCanvas(page).rectangle(rec_Line);
                        new Canvas(canv_Line, pdfDoc, rec_Line).add(new Paragraph().add(new Text(rows.get(k)).addStyle(normalBold).setFontSize(10).setTextAlignment(TextAlignment.JUSTIFIED)).setCharacterSpacing(.8f));
                        //canv_Line.stroke();
                    }
                    
                }
                //else a new page must be inserted
                else
                {
                    testPageFilledLines = 0;
                    filledLines = 0;
                    break;
                }
            }
            
            // page x of y
            Rectangle rec_pageNumber = new Rectangle(500, 810, 150, 25);
            PdfCanvas canv_pageNumber = new PdfCanvas(page).rectangle(rec_pageNumber);
            new Canvas(canv_pageNumber, pdfDoc, rec_pageNumber).add(new Paragraph().add(new Text("Pagina "+ (i+1) + " di " + pages).addStyle(bigNormal)));
            //canv_pageNumber.stroke();
            
            //if it is the last page writes summary data
            if(i == pages-1)
            {
                // Transprter denomination
                String transporterDenomination = deliveryNoteDbr.getString("transporterDenomination") == null ? "" : deliveryNoteDbr.getString("transporterDenomination");
                Rectangle rec_TransporterDenomination = new Rectangle(90, 101, 105, 18);
                PdfCanvas canv_TransporterDenomination = new PdfCanvas(page).rectangle(rec_TransporterDenomination);
                new Canvas(canv_TransporterDenomination, pdfDoc, rec_TransporterDenomination).add(new Paragraph().add(new Text(transporterDenomination== null ? "":transporterDenomination).addStyle(normalBold)).setTextAlignment(TextAlignment.CENTER));
                //canv_PaymentConditions.stroke();
                
                //Transporter address
                Rectangle rec_TransporterAddress = new Rectangle(245, 101, 205, 18);
                PdfCanvas canv_TransporterAddress = new PdfCanvas(page).rectangle(rec_TransporterDenomination);
                String transporterData = "";
                if(transporterDenomination != null)
                {
                    transporterData +=  deliveryNoteDbr.getString("transporterAddress") == null ? "" : deliveryNoteDbr.getString("transporterAddress") + " "; 
                    transporterData +=  deliveryNoteDbr.getString("transporterHouseNumber") == null ? "" : deliveryNoteDbr.getString("transporterHouseNumber") + " ";
                    transporterData +=  deliveryNoteDbr.getString("transporterCity") == null ? "" : deliveryNoteDbr.getString("transporterCity") + " ";
                    transporterData +=  deliveryNoteDbr.getString("transporterProvince") == null ? "" : "," + deliveryNoteDbr.getString("transporterProvince");
                }
                new Canvas(canv_TransporterAddress, pdfDoc, rec_TransporterAddress).add(new Paragraph().add(new Text(transporterData).addStyle(bigNormal).setFontSize(8)));
                //canv_TransporterAddress.stroke();
                 
                // notes
                Rectangle rec_Notes = new Rectangle(80, 61, 245, 35);
                PdfCanvas canv_Notes = new PdfCanvas(page).rectangle(rec_Notes);
                String notes = deliveryNoteDbr.getString("notes") == null ? "" : deliveryNoteDbr.getString("notes");
                new Canvas(canv_Notes, pdfDoc, rec_Notes).add(new Paragraph().add(new Text(notes).addStyle(normal)));
                //canv_Notes.stroke();
            }
        }
        
        
        //Desktop desktop = Desktop.getDesktop();
        //desktop.open(file);
        
        pdfDoc.close();
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
                new DeliveryNotePdfPrinter_1().printDeliveryNote(20020L);
    }
}
