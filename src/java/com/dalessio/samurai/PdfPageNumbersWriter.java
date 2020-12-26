/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dalessio.samurai;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import java.io.IOException;

/**
 *
 * @author Franco
 * //Class implementing event handler that manages page nummber writing
 */
public class PdfPageNumbersWriter implements IEventHandler
{
    //creates a PdfFormXObject that is an external snippet of code.
        //We can put it in different places in the document and execute it one time for all
        //at the right moment. What is done here is locating the PdfFormXObject where total pages number
        //should be rendered and call its executing when the last page has been located in the pdf document.
        protected PdfFormXObject placeholder;
        //the side of the box containing the total pags number
        protected float side = 15;
        //box position
        private float x;
        private float y;
        //position measures utility
        protected float space = 4.5f;
        protected float descent = 3;
 
        //the constructor initialiaze the OdfFormXObject and receives the reference of the pdfDocument instance
        public PdfPageNumbersWriter(PdfDocument pdf, float x, float y) {
            placeholder = new PdfFormXObject(new Rectangle(0, 0, side, side));
            this.x = x;
            this.y = y;
        }
        
        //method called each time a page is ended up
        @Override
        public void handleEvent(Event event) {
            //retrieves the and of page event
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            //retrieves the pdfdocument reference stored inside the event instance
            PdfDocument pdf = docEvent.getDocument();
            //retrieves the page from the pdf document
            PdfPage page = docEvent.getPage();
            //retrieves the current page number from the page
            //int pageNumber = pdf.getPageNumber(page);
            //creates a rectangle having page dimensions
            //Rectangle pageSize = page.getPageSize();
            //instantiates a pdf canvas to write in to the content stream
            PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdf);
            //creates a canvas to write onto the pdf canvas
            //Canvas canvas = new Canvas(pdfCanvas, pdf, pageSize);
            //creates a paragraph containing known data that is the current page number
            //Paragraph p = new Paragraph().add("Page ").add(String.valueOf(pageNumber)).add(" of");
            //shows the known data
            //canvas.showTextAligned(p, x, y, TextAlignment.RIGHT);
            //places the placeholder
            pdfCanvas.addXObject(placeholder, x, y);
            //release the pdfcanvas content to save the memory
            pdfCanvas.release();
        }
        
        //this is the method called at the end of the last document page that writes the total pages values
        public void writeTotal(PdfDocument pdf) throws IOException{
            Canvas canvas = new Canvas(placeholder, pdf);
            Paragraph prgPagesNumber = new Paragraph( new Text( pdf.getNumberOfPages()+"")).setFontSize(10).setFont(PdfFontFactory.createFont(FontConstants.TIMES_ROMAN));
            canvas.add(prgPagesNumber);
        }
    
}
