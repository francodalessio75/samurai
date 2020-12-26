/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dalessio.samurai;

//ITEXT imports
import com.itextpdf.io.font.FontConstants;
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
import com.itextpdf.kernel.color.DeviceGray;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.Canvas;
import com.itextpdf.io.image.ImageDataFactory;

import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.Document;


import java.io.File;
import java.net.MalformedURLException;

import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;

/**
 *
 * @author Franco D'Alessio
 */
public class ITextDeliveryNote
{
    
    public static final String DEST = "C:/Users/Franco/Documents/NetBeansProjects/Samurai/PDF/deliveryNote.pdf";
    
    public String deliveryNoteDate = "31/ 10 / 2018";
    public String deliveryNoteNumber = "326";
    
    

    //fonts declaring
    static PdfFont font = null;
    
    public static void main(String[] args) throws Exception {
        
        font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
        
        File file = new File(DEST);
        
        file.getParentFile().mkdirs();
        
        new ITextDeliveryNote().createPdf(DEST);
        
    }
 
    public void createPdf(String dest ) throws Exception {
        
        //Initialize PDF document
        PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
       
        //adds page to the document
        PageSize ps = PageSize.A4;
        PdfPage page = pdf.addNewPage(ps);
        
        Document document = new Document(pdf,ps);
        document.setMargins(321, 0, 33, 25);
       
       
        
        //adds event handler and event end of page to the document
        pdf.addEventHandler(PdfDocumentEvent.END_PAGE, new MyEventHandler());
        
        
        /************* TABLE ****************************************/
           
        
        float[] columnWidths = {100, 100, 355};
        Table table = new Table(columnWidths);
        table.setWidth(555);
        
        
        
        
        Cell cell_0 = new Cell(1, 1)
                .add(new Paragraph("1234"))
                .setFont(font)
                .setFontSize(10)
                .setFontColor(DeviceGray.BLACK)
                .setHeight(20)
                .setTextAlignment(TextAlignment.CENTER).setHeight(20);
        table.addCell(cell_0);
        
        Cell cell_1 = new Cell(1, 1)
                .add(new Paragraph("20"))
                .setFont(font)
                .setFontSize(10)
                .setFontColor(DeviceGray.BLACK)
                .setHeight(20)
                .setTextAlignment(TextAlignment.CENTER).setHeight(20);
        table.addCell(cell_1);
        
        Cell cell_2 = new Cell(1, 1)
                .add(new Paragraph("Descrizione"))
                .setFont(font)
                .setFontSize(10)
                .setFontColor(DeviceGray.BLACK)
                .setHeight(20)
                .setTextAlignment(TextAlignment.CENTER).setHeight(20);
        table.addCell(cell_2);
        
        for( int j = 0; j<100; j++)
        {
            table.addCell(new Cell( ).add( new Paragraph("")));
            table.addCell(new Cell( ).add( new Paragraph("")));
            table.addCell(new Cell( ).add(new Paragraph("")));
        }
           
        document.add(table);
        //Close document
        pdf.close();
       
        document.close();
        
    
    }
  

   
    public class MyEventHandler implements IEventHandler 
    {
        public MyEventHandler() {}
        @Override
        public void handleEvent(Event event) 
        {
            
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfDocument pdfDoc = docEvent.getDocument();
            
            //the handler takes each page of the document
            PdfPage page = docEvent.getPage();
            
            //creates a pdf canvas
            PdfCanvas pdfCanvas = new PdfCanvas(page);
            //creates a pdf canvas
            PdfCanvas pdfCanvasContacts = new PdfCanvas(page);
            
            /*   CREATES RECTANGLES  */
            
            /********** HEADER ********************************/
            
            //main rectangle
            Rectangle mainRect = new Rectangle(25, 22, 555,820);
            
            
            //Logo 
            Rectangle rectLogo = new Rectangle(25,783 , 200, 59);
            
            //copy and document model
            Rectangle rectCopyDestination = new Rectangle(25+555-90, 790+22, 90, 30);
            
            //DUESSE contacts under the logo
            Rectangle rectContacts = new Rectangle(25, 701+22+17, 330, 53);
            
            //ddt data label
            Rectangle rectDdtLabel = new Rectangle(25+330+5, 701+22+17, 110, 53);
            
            //ddt data
            Rectangle rectDdtData = new Rectangle(25+330+5+110, 701+22+17, 110, 53);
            
            //Customer label
            Rectangle rectCustomerLabel = new Rectangle(25, 701+22+17-120, 20, 120);
            
            //Customer data labels
            Rectangle rectCustomerDataLabels = new Rectangle(25+20, 701+22+17-120, 20, 120);
            
            //Customer ddenomination
            Rectangle rectCustomerDenomination = new Rectangle(25+20+20, 701+22+17-50, 215, 50);
            
            //Customer address
            Rectangle rectCustomerAddress = new Rectangle(25+20+20, 701+22+17-50-35, 215, 35);
            
            //Customer city
            Rectangle rectCustomerCity = new Rectangle(25+20+20, 701+22+17-50-35-35, 215, 35);
            
            //Destination label
            Rectangle rectDestinationLabel = new Rectangle(25+300, 701+22+17-120, 20, 120);
            
            //Destination data labels
            Rectangle rectDestinationDataLabels = new Rectangle(25+20+299, 701+22+17-120, 20, 120);
            
            //Destination denomination
            Rectangle rectDestinationDenomination = new Rectangle(25+20+20+300, 701+22+17-50, 215, 50);
            
            //Destination address
            Rectangle rectDestinationAddress = new Rectangle(25+20+20+300, 701+22+17-50-35, 215, 35);
            
            //Destination city
            Rectangle rectDestinationCity = new Rectangle(25+20+20+300, 701+22+17-50-35-35, 215, 35);
           
            //Transport responsable label
            Rectangle rectTransportResponsableLabel = new Rectangle(25, 595, 135, 25);
            
            //Transport responsable 
            Rectangle rectTransportResponsable = new Rectangle(25+135+5, 595, 135, 25);
            
            //Transport reason label
            Rectangle rectTransportReasonLabel = new Rectangle(25+135+5+135+5, 595, 135, 25);
            
            //Transport reason 
            Rectangle rectTransportReason = new Rectangle(25+135+5+135+5+135+5, 595, 135, 25);
            
            //Goods aspec label
            Rectangle rectGoodsAspectLabel = new Rectangle(25, 570, 135, 25);
            
            //Goods Aspect
            Rectangle rectGoodsAspect = new Rectangle(25+135+5, 570, 135, 25);
            
            //Packages number label
            Rectangle rectPackagesNumberLabel = new Rectangle(25+135+5+135+5, 570, 135, 25);
            
            //Packages number
            Rectangle rectPackagesNumber = new Rectangle(25+135+5+135+5+135+5, 570, 135, 25);
            
            //weight label
            Rectangle rectWeightLabel = new Rectangle(25, 545, 135, 25);
            
            //weight
            Rectangle rectWeight = new Rectangle(25+135+5, 545, 135, 25);
            
            //Transport begins label
            Rectangle rectTransportBeginsLabel = new Rectangle(25+135+5+135+5, 545, 135, 25);
            
            //Transport begins
            Rectangle rectTransportBegins = new Rectangle(25+135+5+135+5+135+5, 545, 135, 25);
            
            //First column label
            Rectangle rectFirstColumnLabel = new Rectangle(25, 520, 100, 25);
            
            //Second column label
            Rectangle rectSecondColumnLabel = new Rectangle(25+100, 520, 100, 25);
            
            //Third column lable
            Rectangle rectThirdColumnLabel = new Rectangle(25+200, 520, 355, 25);
            
            
            
            /************  FOOTER ***********************************/
            
            //footer
            Rectangle rectFooter = new Rectangle(25, 10, 555, 25);
            
            /*INSERTS RECTANGLES*/
            pdfCanvasContacts.rectangle(rectContacts);
           
            
            //pdfCanvas.rectangle(mainRect);
            pdfCanvas.rectangle(rectLogo);
            pdfCanvas.rectangle(rectCopyDestination);
            
            pdfCanvas.rectangle(rectDdtLabel);
            pdfCanvas.rectangle(rectDdtData);
            pdfCanvas.rectangle(rectCustomerLabel);
            pdfCanvas.rectangle(rectCustomerDataLabels);
            pdfCanvas.rectangle(rectCustomerDenomination);
            pdfCanvas.rectangle(rectCustomerAddress);
            pdfCanvas.rectangle(rectCustomerCity);
            pdfCanvas.rectangle(rectDestinationLabel);
            pdfCanvas.rectangle(rectDestinationDataLabels);
            pdfCanvas.rectangle(rectDestinationDenomination);
            pdfCanvas.rectangle(rectDestinationAddress);
            pdfCanvas.rectangle(rectDestinationCity);
            pdfCanvas.rectangle(rectTransportResponsableLabel);
            pdfCanvas.rectangle(rectTransportResponsable);
            pdfCanvas.rectangle(rectTransportReasonLabel);
            pdfCanvas.rectangle(rectTransportReason);
            pdfCanvas.rectangle(rectGoodsAspectLabel);
            pdfCanvas.rectangle(rectGoodsAspect);
            pdfCanvas.rectangle(rectPackagesNumberLabel);
            pdfCanvas.rectangle(rectPackagesNumber);
            pdfCanvas.rectangle(rectWeightLabel);
            pdfCanvas.rectangle(rectWeight);
            pdfCanvas.rectangle(rectTransportBeginsLabel);
            pdfCanvas.rectangle(rectTransportBegins);
            pdfCanvas.rectangle(rectFirstColumnLabel);
            pdfCanvas.rectangle(rectSecondColumnLabel);
            pdfCanvas.rectangle(rectThirdColumnLabel);
            
            pdfCanvas.rectangle(rectFooter);
            //pdfCanvas;
            
            //creates  canvas
            Canvas canvasLogo = new Canvas(pdfCanvas, pdfDoc, rectLogo);//.setBorder(Border.NO_BORDER);
            Canvas canvasCopyDestination = new Canvas(pdfCanvas, pdfDoc, rectCopyDestination);
            
            Canvas canvasContacts = new Canvas(pdfCanvasContacts, pdfDoc, rectContacts);
            
            Canvas canvasRectDdtLabel = new Canvas(pdfCanvas, pdfDoc, rectDdtLabel);
            Canvas canvasRectDdtData = new Canvas(pdfCanvas, pdfDoc, rectDdtData);
            Canvas canvasRectCustomerLabel = new Canvas(pdfCanvas, pdfDoc, rectCustomerLabel);
            Canvas canvasRectCustomerDataLabels = new Canvas(pdfCanvas, pdfDoc, rectCustomerDataLabels);
            Canvas canvasRectCustomerDenomination = new Canvas(pdfCanvas, pdfDoc, rectCustomerDenomination);
            Canvas canvasRectCustomerAddress = new Canvas(pdfCanvas, pdfDoc, rectCustomerAddress);
            Canvas canvasRectCustomerCity = new Canvas(pdfCanvas, pdfDoc, rectCustomerCity);
            Canvas canvasRectDestinationLabel = new Canvas(pdfCanvas, pdfDoc, rectDestinationLabel);
            Canvas canvasRectDestinationDataLabels = new Canvas(pdfCanvas, pdfDoc, rectDestinationDataLabels);
            Canvas canvasRectDestinationDenomination = new Canvas(pdfCanvas, pdfDoc, rectDestinationDenomination);
            Canvas canvasRectDestinationAddress = new Canvas(pdfCanvas, pdfDoc, rectDestinationAddress);
            Canvas canvasRectDestinationCity = new Canvas(pdfCanvas, pdfDoc, rectDestinationCity);
            Canvas canvasRectTransportResponsableLabel = new Canvas(pdfCanvas, pdfDoc, rectTransportResponsableLabel);
            Canvas canvasRectTransportResponsable = new Canvas(pdfCanvas, pdfDoc, rectTransportResponsable);
            Canvas canvasRectTransportReasonLabel = new Canvas(pdfCanvas, pdfDoc, rectTransportReasonLabel);
            Canvas canvasRectTransportReason = new Canvas(pdfCanvas, pdfDoc, rectTransportReason);
            Canvas canvasRectGoodsAspectLabel = new Canvas(pdfCanvas, pdfDoc, rectGoodsAspectLabel);
            Canvas canvasRectGoodsAspect = new Canvas(pdfCanvas, pdfDoc, rectGoodsAspect);
            Canvas canvasRectPackagesNumberLabel = new Canvas(pdfCanvas, pdfDoc, rectPackagesNumberLabel);
            Canvas canvasRectPackagesNumber = new Canvas(pdfCanvas, pdfDoc, rectPackagesNumber);
            Canvas canvasRectWeightLabel = new Canvas(pdfCanvas, pdfDoc, rectWeightLabel);
            Canvas canvasRectWeight = new Canvas(pdfCanvas, pdfDoc, rectWeight);
            Canvas canvasRectTransportBeginsLabel = new Canvas(pdfCanvas, pdfDoc, rectTransportBeginsLabel);
            Canvas canvasRectTransportBegins = new Canvas(pdfCanvas, pdfDoc, rectTransportBegins);
            Canvas canvasRectFirstColumnLabel = new Canvas(pdfCanvas, pdfDoc, rectFirstColumnLabel);
            Canvas canvasRectSecondColumnLabel = new Canvas(pdfCanvas, pdfDoc, rectSecondColumnLabel);
            Canvas canvasRectThirdColumnLabel = new Canvas(pdfCanvas, pdfDoc, rectThirdColumnLabel);
            
            Canvas canvasRectFooter = new Canvas(pdfCanvas, pdfDoc, rectFooter);
            
            /* CREATES COTENTS FOR RECTANGLES AND SHOWS THEM IN RELATED CANVAS*/
            
            //logo
            try{
            Image logo = new Image(ImageDataFactory.create(Config.LOGO_IMG));
            canvasLogo.add(logo);
            }catch(MalformedURLException  ex){}
            
            //copy data
            Text  copy= new Text("COPIA PER DUESSE ").setFont(font).setFontSize(6);
            Text dpr = new Text("\nMod. 07-07 Rev.1").setFont(font).setFontSize(6).setBold();
            Paragraph pCopy = new Paragraph().add(copy).add(dpr);
            canvasCopyDestination.add(pCopy);
            
            //Contacts
            Text contactsBold = new Text("\nDUESSE  s.r.l.").setFont(font).setFontSize(7).setBold();
            Text contacts = new Text("   Tel. +39 0331220913\n Via Agusta,51 Fax +39 0331 220914\n 21017 Samarate(VA) Italy   Cod. Fisc.- P. IVA 02677820124").setFont(font).setFontSize(6);
            Paragraph pContacts = new Paragraph().add(contactsBold).add(contacts);
            canvasContacts.add(pContacts);
            
            //ddt data label
            Text ddtLabelBold = new Text("D.D.T.").setFont(font).setFontSize(12).setBold();
            Text cddtLabel = new Text("\n(D.P.R. 14/08/96 N° 472)").setFont(font).setFontSize(6).setBold();
            Paragraph pDdtLabel = new Paragraph().add(ddtLabelBold).add(cddtLabel).setTextAlignment(TextAlignment.CENTER);
            canvasRectDdtLabel.add(pDdtLabel);
            
            //ddt data 
            Text ddtNumber = new Text("N° " + deliveryNoteNumber ).setFont(font).setFontSize(12).setBold();
            Text ddtDate = new Text("\ndel " + deliveryNoteDate).setFont(font).setFontSize(12).setBold();
            Paragraph pDdtData = new Paragraph().add(ddtNumber).add(ddtDate).setTextAlignment(TextAlignment.CENTER);
            canvasRectDdtData.add(pDdtData);
            
            //Customer  vertical label
            Text customerLabel = new Text("Destinatario" ).setFont(font).setFontSize(10).setBold().setItalic();
            Paragraph pCustomerLabel = new Paragraph().add(customerLabel).setRotationAngle(-((2 * Math.PI) - Math.PI/ 2)).setPaddingRight(15);
            canvasRectCustomerLabel.add(pCustomerLabel).setTextAlignment(TextAlignment.CENTER);
            
            //Customer  data vertical labels
            Text customerDataLabels = new Text("Città         Via            Spett" ).setFont(font).setFontSize(8).setItalic();
            Paragraph pCustomerDataLabels = new Paragraph().add(customerDataLabels).setRotationAngle(-((2 * Math.PI) - Math.PI/ 2)).setPaddingLeft(2).setPaddingRight(0);
            canvasRectCustomerDataLabels.add(pCustomerDataLabels).setTextAlignment(TextAlignment.CENTER);
            
            //Customer  denomination
            Text customerDenomination = new Text("Vodafone Automotive S.p.a." ).setFont(font).setFontSize(12).setBold();
            Paragraph pCustomerDenomination = new Paragraph().add(customerDenomination).setPaddingLeft(4).setPaddingTop(4);
            canvasRectCustomerDenomination.add(pCustomerDenomination);
            
            //Customer  address
            Text customerAddress = new Text("Via Astico, 41" ).setFont(font).setFontSize(10).setBold();
            Paragraph pCustomerAddress = new Paragraph().add(customerAddress).setPaddingLeft(4).setPaddingTop(4);
            canvasRectCustomerAddress.add(pCustomerAddress);
            
            //Customer  city
            Text customerCity = new Text("2100    Varese" ).setFont(font).setFontSize(10).setBold();
            Paragraph pCustomerCity = new Paragraph().add(customerCity).setPaddingLeft(4).setPaddingTop(4);
            canvasRectCustomerCity.add(pCustomerCity);
            
            //Destination  vertical label
            Text destinationLabel = new Text("Destinazione" ).setFont(font).setFontSize(10).setBold().setItalic();
            Paragraph pDestinationLabel = new Paragraph().add(destinationLabel).setRotationAngle(-((2 * Math.PI) - Math.PI/ 2)).setPaddingRight(15);
            canvasRectDestinationLabel.add(pDestinationLabel).setTextAlignment(TextAlignment.CENTER);
            
            //Destination  data vertical labels
            Text destinationDataLabels = new Text("Città         Via            Spett" ).setFont(font).setFontSize(8).setItalic();
            Paragraph pDestinationDataLabels = new Paragraph().add(destinationDataLabels).setRotationAngle(-((2 * Math.PI) - Math.PI/ 2)).setPaddingLeft(2).setPaddingRight(0);
            canvasRectDestinationDataLabels.add(pDestinationDataLabels).setTextAlignment(TextAlignment.CENTER);
            
            //Destination  denomination
            Text destinationDenomination = new Text("Vodafone Automotive S.p.a." ).setFont(font).setFontSize(12).setBold();
            Paragraph pDestinationDenomination = new Paragraph().add(destinationDenomination).setPaddingLeft(4).setPaddingTop(4);
            canvasRectDestinationDenomination.add(pDestinationDenomination);
            
            //Destination  address
            Text destinationAddress = new Text("Via Astico, 41" ).setFont(font).setFontSize(10).setBold();
            Paragraph pDestinationAddress = new Paragraph().add(destinationAddress).setPaddingLeft(4).setPaddingTop(4);
            canvasRectDestinationAddress.add(pDestinationAddress);
            
            //Destination  city
            Text destinationCity = new Text("2100    Varese" ).setFont(font).setFontSize(10).setBold();
            Paragraph pDestinationCity = new Paragraph().add(destinationCity).setPaddingLeft(4).setPaddingTop(4);
            canvasRectDestinationCity.add(pDestinationCity);
            
            //transport responsable label
            Text transportResponsableLabel = new Text("Trasporto a cura del" ).setFont(font).setFontSize(8).setBold();
            Paragraph pTransportResponsableLabel = new Paragraph().add(transportResponsableLabel);
            canvasRectTransportResponsableLabel.add(pTransportResponsableLabel);
            
            //transport responsable label
            Text transportResponsable = new Text("e-mail" ).setFont(font).setFontSize(10).setBold();
            Paragraph pTransportResponsable = new Paragraph().add(transportResponsable);
            canvasRectTransportResponsable.add(pTransportResponsable);
            
            //transport reason label
            Text transportReasonLabel = new Text("Causale del trasporto" ).setFont(font).setFontSize(8).setBold();
            Paragraph pTransportReasonLabel = new Paragraph().add(transportReasonLabel);
            canvasRectTransportReasonLabel.add(pTransportReasonLabel);
            
            //transport reason 
            Text transportReason = new Text("Vendita" ).setFont(font).setFontSize(10).setBold();
            Paragraph pTransportReason = new Paragraph().add(transportReason);
            canvasRectTransportReason.add(pTransportReason);
            
            //Goods aspect label
            Text goodsAspectLabel = new Text("Aspetto esteriore dei beni" ).setFont(font).setFontSize(8).setBold();
            Paragraph pGoodsAspectLabel = new Paragraph().add(goodsAspectLabel);
            canvasRectGoodsAspectLabel.add(pGoodsAspectLabel);
            
            //Goods aspect
            Text goodsAspect = new Text("File" ).setFont(font).setFontSize(10).setBold();
            Paragraph pGoodsAspect = new Paragraph().add(goodsAspect);
            canvasRectGoodsAspect.add(pGoodsAspect);
            
            //Packages number label
            Text packagesNumberLabel = new Text("N° colli" ).setFont(font).setFontSize(8).setBold();
            Paragraph pPackagesNumberLabel = new Paragraph().add(packagesNumberLabel);
            canvasRectPackagesNumberLabel.add(pPackagesNumberLabel);
            
            //Packages number 
            Text packagesNumber = new Text("15" ).setFont(font).setFontSize(10).setBold();
            Paragraph pPackagesNumber = new Paragraph().add(packagesNumber);
            canvasRectPackagesNumber.add(pPackagesNumber);
            
            //Weight label
            Text weightLabel = new Text("Peso Kg" ).setFont(font).setFontSize(8).setBold();
            Paragraph pWeightLabel = new Paragraph().add(weightLabel);
            canvasRectWeightLabel.add(pWeightLabel);
            
            //Weight 
            Text weight = new Text("20" ).setFont(font).setFontSize(10).setBold();
            Paragraph pWeight = new Paragraph().add(weight);
            canvasRectWeight.add(pWeight);
            
            //transport begins label
            Text transportBeginsLabel = new Text("Data inizio trasporto" ).setFont(font).setFontSize(8).setBold();
            Paragraph pTransportBeginsLabel = new Paragraph().add(transportBeginsLabel);
            canvasRectTransportBeginsLabel.add(pTransportBeginsLabel);
            
            //transport begins 
            Text transportBegins = new Text("31/12/2017" ).setFont(font).setFontSize(10).setBold();
            Paragraph pTransportBegins = new Paragraph().add(transportBegins);
            canvasRectTransportBegins.add(pTransportBegins);
            
            //first column label
            Text firstColumnLabel = new Text("Ns Cod." ).setFont(font).setFontSize(10).setBold();
            Paragraph pFirstColumnLabel = new Paragraph().add(firstColumnLabel).setTextAlignment(TextAlignment.CENTER);
            canvasRectFirstColumnLabel.add(pFirstColumnLabel);
            
            //second column label
            Text secondColumnLabel = new Text("Quantità" ).setFont(font).setFontSize(10).setBold();
            Paragraph pSecondColumnLabel = new Paragraph().add(secondColumnLabel).setTextAlignment(TextAlignment.CENTER);
            canvasRectSecondColumnLabel.add(pSecondColumnLabel);
            
            //third column label
            Text thirdColumnLabel = new Text("Descrizione" ).setFont(font).setFontSize(10).setBold();
            Paragraph pThirdColumnLabel = new Paragraph().add(thirdColumnLabel).setTextAlignment(TextAlignment.CENTER);
            canvasRectThirdColumnLabel.add(pThirdColumnLabel);
            
            //foooter
            Text footer = new Text("Sede legale: Via Scipione Ronchetti n.189/2 - 21044 Cavaria con Premezzo ( VA )\nCapitale Sociale Euro 10.000,00 I.V. - Registro Imprese di Varese 02677820124 - REA VA - 276830" ).setFont(font).setFontSize(6);
            Paragraph pFooter = new Paragraph().add(footer).setTextAlignment(TextAlignment.CENTER);
            canvasRectFooter.add(pFooter);
           
            
        }
    }
}
