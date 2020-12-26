/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dalessio.samurai;
import com.dalessio.samurai.digitalinvoice.DigitalInvoice_DueEsse;
import com.dps.dbi.DbResult;
import com.dps.diginvoice.xml.DigitalInvoice;
import com.dps.diginvoice.xml.DigitalInvoice.TipoDocumento;
import com.dps.diginvoice.xml.XmlHelper;
import java.sql.SQLException;
import java.text.ParseException;

/**
 *
 * @author Franco
 */
public class InvoiceXMLPrinter_1
{
    public InvoiceXMLPrinter_1(){}
    
    DigitalInvoice digInvoice;
    
    public void  createXML( Long invoice_id, String filePath ) throws SQLException, ClassNotFoundException,ParseException,XmlHelper.XmlNodeException
    {
        //Data Access Object
        DataAccessObject dao = new DataAccessObject();
        
        DbResult invoice_dbr = dao.readDigInvoice(invoice_id);
        DbResult invoiceRows_dbr = dao.readDigInvoiceRows(invoice_id);
        DbResult datiDDT_dbr = dao.readDatiDDT(invoice_id);
        DbResult datiOrdineAcquisto_dbr = dao.readDatiOrdineAcquisto(invoice_id);
        DbResult cessionarioCommittente_dbr = dao.readCessionarioCommittente(invoice_id);
        
        //instantiates the class
        digInvoice = new DigitalInvoice_DueEsse(invoice_dbr, invoiceRows_dbr, datiDDT_dbr, datiOrdineAcquisto_dbr, cessionarioCommittente_dbr);
        
        //adds the attachment
       // digInvoice.nuovoAllegato().loadAttachment("C:\\AppResources\\Samurai\\INVOICES\\FATTURA_DUESSE_"+invoice_dbr.getLong("number")+"_"+invoice_dbr.getInteger("year")+".pdf");
        
        //calls the creating xml method
        digInvoice.toFile(filePath);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        // TODO code application logic here
    }
    
}
