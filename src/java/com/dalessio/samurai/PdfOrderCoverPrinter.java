/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dalessio.samurai;

import static com.dalessio.samurai.DataAccessObject.DTF;
import static com.dalessio.samurai.ITextDeliveryNote.font;
import com.dps.dbi.DbResult;
import com.itextpdf.io.IOException;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.DashedBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import java.io.File;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Franco
 */
public class PdfOrderCoverPrinter
{
    /**
     * What we need here is gust a table containing 
     * order customer logo;
     * order data...
     * @param order_id
     * @throws IOException 
     * @throws java.lang.ClassNotFoundException 
     * @throws java.sql.SQLException 
     */
    public void createOrderCoverPdf( Long order_id ) throws java.io.IOException, ClassNotFoundException, SQLException
    {
        //European date format
        DateTimeFormatter DTFE = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        
        //data access object
        DataAccessObject dao = new DataAccessObject();
        
        //font declaration and file creation pdf create method calling
        font = PdfFontFactory.createFont( FontConstants.HELVETICA );
        
        //file destination name
        final String DEST = Config.ORDERS_COVERS_DIR+"ORDER_COVER_"+order_id+".pdf";
        
        
        //related deliverynotes
        DbResult order = dao.readOrders(order_id,null,null,null,null,null,null,null,null,null,null,null );
        
        //pdf document with destination file, operatoes low level opeerations
        PdfDocument destPdf = new PdfDocument(new PdfWriter(DEST));
        
        //assigns the eventHandler that sees to having the page rotated
        //PageOrientationsEventHandler eventHandler = new PageOrientationsEventHandler();
        //destPdf.addEventHandler(PdfDocumentEvent.START_PAGE, eventHandler);
        
        
        //the page size
        PageSize ps = PageSize.A3;
        
        //creates file
        File file = new File(DEST);
        
        
        //creates directory
        file.getParentFile().mkdirs();
        
        
        //the document ( it operates high level operations such as setting page size and rotation )
        Document document = new Document( destPdf, ps );
        
        //it should rotate the unique document page of 90 degrees thet is landscape orientation
        //destPdf.getPage(1).setRotation(90);
        
        //sets document margins
        document.setMargins(15, 15, 15, 15 );
        
        
        Table tblCover = new Table( new float[]{156,42,122,42,122,42,122,122,42} );
        
        //table dimensions and alignment
        tblCover.setWidth( 810 );
        tblCover.setHeight( 565 );
        
        //table text alignment
        tblCover.setTextAlignment( TextAlignment.CENTER );
        
        //creates cells
        Cell logoCell = new Cell( 5, 1 );
        Cell jobTypeLabelCell = new Cell( 5, 1 );
        Cell jobTypeCell = new Cell( 5, 1 );
        Cell machinaryModelLabelCell = new Cell( 5, 1 );
        Cell machinaryModelCell = new Cell( 5, 1 );
        Cell notesLabelCell = new Cell( 5, 1 );
        Cell notesCell = new Cell( 5, 1 );
        Cell orderDateLabelCell = new Cell( 2, 1 );
        Cell orderCodeLabelCell = new Cell( 2, 1 );
        Cell orderSerialNumberLabelCell = new Cell( 2, 1 );
        Cell orderDateCell = new Cell( 2, 1 );
        Cell orderCodeCell = new Cell( 3, 1 );
        Cell orderSerialNumberCell = new Cell( 3, 1 );
        
        //cells height and rotation
        logoCell.setHeight( 565 );
        jobTypeLabelCell.setHeight( 565 );
        jobTypeCell.setHeight( 565 );
        machinaryModelLabelCell.setHeight( 565 );
        machinaryModelCell.setHeight( 565 );
        notesLabelCell.setHeight( 565 );
        notesCell.setHeight( 565 );
        orderDateLabelCell.setHeight( 200 );
        orderDateCell.setHeight( 365 );
        orderCodeLabelCell.setHeight( 200 );
        orderCodeCell.setHeight( 365 ).setPaddingTop(5).setHorizontalAlignment(HorizontalAlignment.CENTER);
        orderSerialNumberLabelCell.setHeight( 200 );
        orderSerialNumberCell.setHeight( 365 ).setPaddingTop(5).setHorizontalAlignment(HorizontalAlignment.CENTER);
        
        /*cells height and rotation
        logoCell.setWidth( 400 ).setRotationAngle(Math.PI /2);
        jobTypeCell.setMinWidth( 400 ).setRotationAngle(Math.PI /2);
        machinaryModelCell.setMinWidth( 400 ).setRotationAngle(Math.PI /2);
        notesCell.setMinWidth( 400 ).setRotationAngle(Math.PI /2);
        orderLabelsCell.setMinWidth( 100 ).setRotationAngle(Math.PI /2);
        orderDataCell.setMinWidth( 300 ).setRotationAngle(Math.PI /2);
        logoCell.setWidth( 565 );
        jobTypeCell.setWidth( 565 );
        machinaryModelCell.setWidth( 565 );
        notesCell.setWidth( 565 );
        orderLabelsCell.setWidth( 565 );
        orderDataCell.setWidth( 565 );*/
        
        //CELL CONTENTS
        //headers
        Paragraph pJobTypeHeader = new Paragraph( new Text( "TIPO LAVORO" ) ).setRotationAngle(Math.PI/2).setVerticalAlignment(VerticalAlignment.MIDDLE).setHorizontalAlignment(HorizontalAlignment.CENTER).setBorder( new DashedBorder( 0.3f) ).setWidth(555).setHeight(40).setBold().setFontSize(15);
        Paragraph pMachinaryModelHeader = new Paragraph( new Text( "DESCRIZIONE LAVORO" ) ).setRotationAngle(Math.PI/2).setVerticalAlignment(VerticalAlignment.MIDDLE).setHorizontalAlignment(HorizontalAlignment.CENTER).setBorder( new DashedBorder( 0.3f) ).setWidth(555).setHeight(40).setBold().setFontSize(15);
        Paragraph pNotesHeader = new Paragraph( new Text( "NOTE" ) ).setRotationAngle(Math.PI/2).setVerticalAlignment(VerticalAlignment.MIDDLE).setHorizontalAlignment(HorizontalAlignment.CENTER).setBorder( new DashedBorder( 0.3f) ).setWidth(555).setHeight(40).setBold().setFontSize(15);
        Paragraph pOrderCodeLabel = new Paragraph( new Text( "CODICE LAVORO" ) ).setRotationAngle(Math.PI/2).setVerticalAlignment(VerticalAlignment.MIDDLE).setHorizontalAlignment(HorizontalAlignment.CENTER).setBorder( new DashedBorder( 0.3f) ).setWidth(180).setHeight(120).setBold().setFontSize(20);
        Paragraph pOrderSerialNumberLabel = new Paragraph( new Text( "MATRICOLA" ) ).setRotationAngle(Math.PI/2).setVerticalAlignment(VerticalAlignment.MIDDLE).setHorizontalAlignment(HorizontalAlignment.CENTER).setBorder( new DashedBorder( 0.3f) ).setWidth(180).setHeight(120).setBold().setFontSize(20);
        Paragraph pOrderDateLabel = new Paragraph( new Text( "DATA" ) ).setRotationAngle(Math.PI/2).setVerticalAlignment(VerticalAlignment.MIDDLE).setHorizontalAlignment(HorizontalAlignment.CENTER).setBorder( new DashedBorder( 0.3f) ).setWidth(180).setHeight(40).setBold().setFontSize(15);
        
        //creates a null image instance and try to retrieve the customer logo
        Image logoImg = null;
        try{
            //customer logo
            String banner = dao.getLogoPath( order.getLong( "customer_id" ) );
            logoImg = new Image( ImageDataFactory.create( Config.CUSTOMERS_LOGO_DIR + banner) ) ;
            
        }catch( com.itextpdf.io.IOException | java.io.IOException ex ){ ex.printStackTrace();}
        
        //Texts
        Paragraph pJobTypeText = new Paragraph( new Text( order.getString( "jobTypeName" ) ) ).setRotationAngle(Math.PI/2).setVerticalAlignment(VerticalAlignment.MIDDLE).setHorizontalAlignment(HorizontalAlignment.CENTER).setBorder( new DashedBorder( 0.3f) ).setWidth(555).setHeight(120).setFontSize(35);
        Paragraph pMachinaryModelText = new Paragraph( new Text( order.getString( "machinaryModel" ) ) ).setRotationAngle(Math.PI/2).setVerticalAlignment(VerticalAlignment.MIDDLE).setHorizontalAlignment(HorizontalAlignment.CENTER).setBorder( new DashedBorder( 0.3f) ).setWidth(555).setHeight(120).setFontSize(35);
        Paragraph pNotesText = new Paragraph( new Text( order.getString( "notes" ) ) ).setRotationAngle(Math.PI/2).setVerticalAlignment(VerticalAlignment.MIDDLE).setHorizontalAlignment(HorizontalAlignment.CENTER).setBorder( new DashedBorder( 0.3f) ).setWidth(555).setHeight(120).setFontSize(25);
        Paragraph pCode = new Paragraph( new Text( order.getString( "code" ) ) ).setRotationAngle(Math.PI/2).setVerticalAlignment(VerticalAlignment.MIDDLE).setHorizontalAlignment(HorizontalAlignment.CENTER).setBorder( new DashedBorder( 0.3f) ).setWidth(285).setHeight(100).setFontSize(70).setFontColor(Color.WHITE).setBackgroundColor(Color.BLACK).setBold();
        Paragraph pSerialNumber = new Paragraph( new Text( order.getString( "serialNumber" ) ) ).setRotationAngle(Math.PI/2).setVerticalAlignment(VerticalAlignment.MIDDLE).setHorizontalAlignment(HorizontalAlignment.CENTER).setBorder( new DashedBorder( 0.3f) ).setWidth(285).setHeight(100).setFontSize(70).setFontColor(Color.WHITE).setBackgroundColor(Color.BLACK).setBold();
        
        //date
        String dateString = order.getString( "date" );
        //get a stringbuilder to insert dashes
        StringBuilder dateSB = new StringBuilder( dateString );
        dateSB.insert( 4, "-" );
        dateSB.insert( 7, "-" );
        dateString = DTFE.format( DTF.parse( dateSB.toString( ) ) );
        Paragraph pDate = new Paragraph( new Text( dateString ) ).setRotationAngle(Math.PI/2).setRotationAngle(Math.PI/2).setVerticalAlignment(VerticalAlignment.MIDDLE).setHorizontalAlignment(HorizontalAlignment.CENTER).setBorder( new DashedBorder( 0.3f) ).setWidth(355).setHeight(40).setFontSize(30) ;
        
        //assembling cells
        if( logoImg != null )
        {
            logoImg.setRotationAngle(Math.PI/2);
            logoImg.scaleAbsolute( 1f, 1f);
            Paragraph pImage = new Paragraph();
            pImage.add(logoImg);
            pImage.add(new Text(" "));
            //pImage.setMaxWidth(150);
            //logoImg.setFixedPosition(15f, 618f,555);
            logoCell.add(pImage);
        }
        else
        {
            logoCell.add("");
        }
        
        jobTypeLabelCell.add(pJobTypeHeader);
        jobTypeCell.add(pJobTypeText);
        machinaryModelLabelCell.add(pMachinaryModelHeader);
        machinaryModelCell.add(pMachinaryModelText);
        notesLabelCell.add(pNotesHeader);
        notesCell.add(pNotesText);
        orderDateLabelCell.add(pOrderDateLabel);
        orderDateCell.add(pDate);
        orderCodeLabelCell.add(pOrderCodeLabel);
        orderCodeCell.add(pCode);
        orderSerialNumberLabelCell.add(pOrderSerialNumberLabel);
        orderSerialNumberCell.add(pSerialNumber);
        
        //assembling table
        tblCover
            .addCell(logoCell)
            .addCell(jobTypeLabelCell)
            .addCell(jobTypeCell)
            .addCell(machinaryModelLabelCell)
            .addCell(machinaryModelCell)
            .addCell(notesLabelCell)
            .addCell(notesCell)
            .addCell(orderCodeCell)
            .addCell(orderSerialNumberCell)
            .addCell(orderDateCell)
            .addCell(orderDateLabelCell)
            .addCell(orderCodeLabelCell);
        
        
        document. add( tblCover) ;
      
        document. close( ) ;
        
    }
    
    public static class PageOrientationsEventHandler implements IEventHandler {
        
        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            docEvent.getPage().put(PdfName.Rotate, new PdfNumber(90) );
        }
    }
    
    public static void main(String[] args) throws Exception
    {
        PdfOrderCoverPrinter cover = new PdfOrderCoverPrinter( );  
        cover.createOrderCoverPdf( new Long(38940) );
    
    }
}
