package com.dalessio.samurai;


import com.dps.dbi.DbResult;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
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
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



/**
 *
 * @author Franco
 */
public class CreditNotePdfPrinter
{
    public static final String IMG = "C:\\AppResources\\Samurai\\CREDIT_NOTES\\creditNote.jpg";
    
    
    public void printCreditNote( Long creditNote_id ) throws IOException, SQLException, ClassNotFoundException
    {
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
        
        //Invoice data
        DbResult creditNoteDbr = dao.readCreditNotes(creditNote_id, null, null, null, null);
        DbResult creditNoteItems = dao.readCreditNoteRows(creditNote_id);
        
        //useful quantities to lines content insertion
        int firstLine_Y_value = 501;
        int line_Y_offset = 12;
        
        //max number of characters for a description
        int maxChar = 58;
        
        //total document lines
        int totalLines = 0;
        for( int i = 0; i < creditNoteItems.rowsCount(); i++ )
        {
            totalLines += SplitTexUtil.splitText(creditNoteItems.getString(i,"description"), maxChar).size();
        }
        //since the max number of lines of an invoice page is 26 here the document pages number computing
        int pages = totalLines/26;
        if( totalLines%26 > 0 )
            pages++;
        
        //creates file
        File file = new File(Config.CREDIT_NOTES_DIR + "NOTA_ACCREDITO_DUESSE_" + creditNoteDbr.getLong("number")+"_"+creditNoteDbr.getInteger("year") + ".pdf");
        file.getParentFile().mkdirs();
        
        //Initialize PDF document
        PdfDocument pdfDoc = new PdfDocument( new PdfWriter(Config.CREDIT_NOTES_DIR + "NOTA_ACCREDITO_DUESSE_" + creditNoteDbr.getLong("number")+"_"+creditNoteDbr.getInteger("year") + ".pdf"));
        
        //empty invoice image as watermark placed for each new page
        Image img = new Image(ImageDataFactory.create(IMG));
        IEventHandler handler = new TransparentImage(img);
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
            
            //ROUNDED SHADOW TABLE 1x1 WITH VAT RATE
            //cells contents
            //[0][0] 
            String vatRate = "";
            if( creditNoteDbr.getDouble("aliquotaIVA") == 0 )
                vatRate = "Iva Esente";
            else if( creditNoteDbr.getDouble("aliquotaIVA") == 4 )
                vatRate = "Iva 4%";
            else if( creditNoteDbr.getDouble("aliquotaIVA") == 22 )
                vatRate = "Iva 22%";
            
            Text txtVatRateLabel = new Text(vatRate);
            Paragraph prgtxtVatRateLabel = new Paragraph().add(txtVatRateLabel).addStyle(normal).setFontSize(8);
            //Table instantiation
            Table tblVatRateLabel = new Table(1).setHeight(18);
            //cells adding
            //[0][0]
            Cell cell = new Cell().add(prgtxtVatRateLabel).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER);
            tblVatRateLabel.addCell(cell);
            //background color and dimensions assigning
            tblVatRateLabel.setBackgroundColor( new DeviceRgb(180, 180, 180));
            //renderer assigning
            tblVatRateLabel.setNextRenderer(new RoundedColouredTable(tblVatRateLabel));
            //shows tables
            new Canvas(new PdfCanvas(page), pdfDoc, new Rectangle(300, 45, 45, 40)).add(tblVatRateLabel);
            
            
            //writes invoice number
            Rectangle rec_number = new Rectangle(510, 746, 38, 25);
            PdfCanvas canv_number = new PdfCanvas(page).rectangle(rec_number);
            new Canvas(canv_number, pdfDoc, rec_number).add(new Paragraph().add(new Text(creditNoteDbr.getLong("number")+"/"+creditNoteDbr.getInteger("year")).addStyle(dateNumber)).setTextAlignment(TextAlignment.RIGHT));
            //canv_number.stroke();
            
            //writes invoice date
            Rectangle rec_date = new Rectangle(502, 724, 58, 25);
            PdfCanvas canv_date = new PdfCanvas(page).rectangle(rec_date);
            new Canvas(canv_date, pdfDoc, rec_date).add(new Paragraph().add(new Text(creditNoteDbr.getString("date").substring(6)+"/"+creditNoteDbr.getString("date").substring(4,6)+"/"+creditNoteDbr.getString("date").substring(0,4)).addStyle(dateNumber)).setTextAlignment(TextAlignment.CENTER));
            //canv_date.stroke();
            
            //writes customer denomination
            Rectangle rec_customerDenomination = new Rectangle(310, 675, 255, 50);
            PdfCanvas canv_customer = new PdfCanvas(page).rectangle(rec_customerDenomination);
            //checks length
            List<String> lines = SplitTexUtil.splitText(creditNoteDbr.getString("denomination"), 27);
            String denomination = "";
            for( String s : lines )
                denomination += s+"\n";
            new Canvas(canv_customer, pdfDoc, rec_customerDenomination).add(new Paragraph().add(new Text(denomination).addStyle(dateNumber)).setTextAlignment(TextAlignment.CENTER).setTextAlignment(TextAlignment.LEFT));
            //canv_customer.stroke();
            
            //writes customer adress
            Rectangle rec_customerAddress = new Rectangle(308, 652, 255, 23);
            PdfCanvas canv_customerAddress = new PdfCanvas(page).rectangle(rec_customerAddress);
            new Canvas(canv_customerAddress, pdfDoc, rec_customerAddress).add(new Paragraph().add(new Text(creditNoteDbr.getString("address")+", "+creditNoteDbr.getString("houseNumber")).addStyle(bigNormal)));
            //canv_customerAddress.stroke();
            
            //writes customer postal code, city, province
            Rectangle rec_customerPostCityProvince = new Rectangle(308, 633, 255, 23);
            PdfCanvas canv_customerPostCityProvince = new PdfCanvas(page).rectangle(rec_customerPostCityProvince);
            new Canvas(canv_customerPostCityProvince, pdfDoc, rec_customerPostCityProvince).add(new Paragraph().add(new Text(creditNoteDbr.getString("postalCode")+"      "+creditNoteDbr.getString("city")+"   "+creditNoteDbr.getString("province")).addStyle(dateNumber)));
            //canv_customerAddress.stroke();
            
            //writes customer vat code
            Rectangle rec_customerVatCode = new Rectangle(96, 592, 120, 23);
            PdfCanvas canv_customerVatCode = new PdfCanvas(page).rectangle(rec_customerVatCode);
            new Canvas(canv_customerVatCode, pdfDoc, rec_customerVatCode).add(new Paragraph().add(new Text(creditNoteDbr.getString("vatCode")).addStyle(dateNumber)).setTextAlignment(TextAlignment.CENTER));
            //canv_customerVatCode.stroke();
            
            //customer fiscal code
            Rectangle rec_customerFiscalCode = new Rectangle(96, 553, 120, 23);
            PdfCanvas canv_customerFiscalCode = new PdfCanvas(page).rectangle(rec_customerFiscalCode);
            new Canvas(canv_customerFiscalCode, pdfDoc, rec_customerFiscalCode).add(new Paragraph().add(new Text(creditNoteDbr.getString("fiscalCode")).addStyle(dateNumber)).setTextAlignment(TextAlignment.CENTER));
            //canv_customerFiscalCode.stroke();
            
            /**
             * Uses lastInsertedItem to start the iteration from the first item to be inserted.
             * Uses testPageFilledLines to check if the current item description can be inserted
             * Uses filledLines to know how many lines have been already inserted in the current page
             */
            for( int j = lastInsertedItem; j < creditNoteItems.rowsCount(); j++ )
            {
                //number of lines necessary to insert the description
                int descriptionLines = SplitTexUtil.splitText(creditNoteItems.getString(j, "description"), maxChar).size();
                //fist update the test variable
                testPageFilledLines += descriptionLines;
                //if the test is ok
                if( testPageFilledLines <= 26 )
                {               
                    /**
                     * creates and fills code rectangle, note: x values of line elements is always the same, while 
                     * y values must be computed, so the y value is : y = ( already filledLines * yOffset ) + firstLineYValue
                     * This is the hieght of the first line of the description
                     */
                    int topItemYValue = (firstLine_Y_value-line_Y_offset*(filledLines));
                    
                    //updates variables for the next iteration
                    filledLines += descriptionLines;
                    lastInsertedItem++;
                    
                    /* this is the height of the last line of the description*/
                    int bottomItemYVAlue = (firstLine_Y_value-line_Y_offset*(filledLines));
                    
                    //quantity 
                    Rectangle rec_Quantity = new Rectangle(99, topItemYValue, 40, 20);
                    PdfCanvas canv_Quantity = new PdfCanvas(page).rectangle(rec_Quantity);
                    new Canvas(canv_Quantity, pdfDoc, rec_Quantity).add(new Paragraph().add(new Text(creditNoteItems.getInteger(j,"quantity")+"").addStyle(normalBold).setFontSize(10)).setTextAlignment(TextAlignment.RIGHT));
                    //canv_Quantity.stroke();String.format( "%.2f", fixedCallRightCost )
                    
                    /**
                     * description: creates the List of description lines. Iterating the list fills the rows
                     */
                    List<String> rows = SplitTexUtil.splitText(creditNoteItems.getString(j, "description"), maxChar);
                    for( int k=0; k < rows.size(); k++ )
                    {
                        Rectangle rec_Line = new Rectangle(162, topItemYValue-(line_Y_offset*k), 350, 20);
                        PdfCanvas canv_Line = new PdfCanvas(page).rectangle(rec_Line);
                        new Canvas(canv_Line, pdfDoc, rec_Line).add(new Paragraph().add(new Text(rows.get(k)).addStyle(normalBold).setFontSize(10).setTextAlignment(TextAlignment.JUSTIFIED)).setCharacterSpacing(.8f));
                        //canv_Line.stroke();
                    }
                    
                    // item amount 
                    Rectangle rec_Amount = new Rectangle(518, bottomItemYVAlue+line_Y_offset, 45, 20);
                    PdfCanvas canv_Amount = new PdfCanvas(page).rectangle(rec_Amount);
                    new Canvas(canv_Amount, pdfDoc, rec_Amount).add(new Paragraph().add(new Text(String.format( "%.2f",creditNoteItems.getDouble(j,"totalAmount"))).addStyle(normalBold).setFontSize(10)).setTextAlignment(TextAlignment.RIGHT));
                    //canv_Amount.stroke();
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
            Rectangle rec_pageNumber = new Rectangle(500, 780, 150, 25);
            PdfCanvas canv_pageNumber = new PdfCanvas(page).rectangle(rec_pageNumber);
            new Canvas(canv_pageNumber, pdfDoc, rec_pageNumber).add(new Paragraph().add(new Text("Pagina "+ (i+1) + " di " + pages).addStyle(bigNormal)));
            //canv_pageNumber.stroke();
            
            //if it is the last page writes summary data
            if(i == pages-1)
            {
                // notes
                Rectangle rec_Notes = new Rectangle(86, 127, 175, 45);
                PdfCanvas canv_Notes = new PdfCanvas(page).rectangle(rec_Notes);
                //checks length
                String notesStr = creditNoteDbr.getString("notes").replace("''","'");
                List<String> notesLines = SplitTexUtil.splitText(notesStr, 50);
                String notes = "";
                for( String s : notesLines )
                    notes += s+"\n";
                new Canvas(canv_Notes, pdfDoc, rec_Notes).add(new Paragraph().add(new Text(notes).addStyle(normal).setFontSize(8)).setMultipliedLeading(1f));
                //canv_Notes.stroke();
                
                // taxable  amount 
                Rectangle rec_taxableAmount = new Rectangle(351, 89, 65, 25);
                PdfCanvas canv_taxableAmount = new PdfCanvas(page).rectangle(rec_taxableAmount);
                new Canvas(canv_taxableAmount, pdfDoc, rec_taxableAmount).add(new Paragraph().add(new Text(String.format( "%.2f",creditNoteDbr.getDouble("taxableAmount"))).addStyle(normalBold).setFontSize(10)).setTextAlignment(TextAlignment.RIGHT));
                //canv_taxableAmount.stroke();
                
                // tax amount  
                Rectangle rec_taxAmount = new Rectangle(351, 59, 65, 25);
                PdfCanvas canv_taxAmount = new PdfCanvas(page).rectangle(rec_taxAmount);
                new Canvas(canv_taxAmount, pdfDoc, rec_taxAmount).add(new Paragraph().add(new Text(String.format( "%.2f",creditNoteDbr.getDouble("taxAmount"))).addStyle(normalBold).setFontSize(10)).setTextAlignment(TextAlignment.RIGHT));
                //canv_taxAmount.stroke();
                
                // total amount 
                Rectangle rec_totalAmount = new Rectangle(481, 75, 80, 25);
                PdfCanvas canv_totalAmount = new PdfCanvas(page).rectangle(rec_totalAmount);
                new Canvas(canv_totalAmount, pdfDoc, rec_totalAmount).add(new Paragraph().add(new Text(String.format( "%.2f",creditNoteDbr.getDouble("totalAmount"))).addStyle(normalBold).setFontSize(14)).setTextAlignment(TextAlignment.RIGHT));
                //canv_totalAmount.stroke();
            }
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
                new CreditNotePdfPrinter().printCreditNote(24L);
    }
    
}
