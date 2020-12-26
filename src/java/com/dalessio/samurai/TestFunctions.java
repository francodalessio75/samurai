/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dalessio.samurai;
import com.dps.dbi.DbResult;


/**
 *
 * @author Franco D'Alessio
 */
public class TestFunctions
{
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception
    {
        // TODO code application logic here
        DataAccessObject dao = new DataAccessObject();
        DbResult dbrDeliveryNote = dao.readDeliveryNotes(30137L, null, null, null, null, null);
        DbResult dbrDeliveryNoteRows = dao.getDeliveryNoteRowsByDeliveryNoteId(30137L);
        //DbResult invoice = dao.readInvoices(47L, null, null, null, null);
        //DbResult invoiceRows = dao.getInvoiceRowsByInvoiceId(47L);
        //PdfPrinter pdfPrinter = new PdfPrinter();
        //pdfPrinter.createDeliveryNotePdf(20135L,deliveryNote, deliveryNoteRows);
        //pdfPrinter.createInvoicePdf(49L,invoice, invoiceRows);
        
        DeliveryNotePdfPrinter dNPdfP = new DeliveryNotePdfPrinter();
        dNPdfP.createDeliveryNotePdf(30137L, dbrDeliveryNote, dbrDeliveryNoteRows, 10010L);
    }
    
}
