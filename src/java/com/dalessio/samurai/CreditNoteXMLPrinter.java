/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dalessio.samurai;
import com.dalessio.samurai.digitalinvoice.DigitalCreditNote_Duesse;
import com.dps.dbi.DbResult;
import com.dps.diginvoice.xml.DigitalInvoice;
import com.dps.diginvoice.xml.XmlHelper;
import java.sql.SQLException;
import java.text.ParseException;

/**
 *
 * @author Franco
 */
public class CreditNoteXMLPrinter
{
    public CreditNoteXMLPrinter(){}
    
    DigitalInvoice digCreditNote;
    
    public void  createXML( Long creditNote_id, String filePath ) throws SQLException, ClassNotFoundException,ParseException,XmlHelper.XmlNodeException
    {
        //Data Access Object
        DataAccessObject dao = new DataAccessObject();
        
        DbResult creditNote_dbr = dao.readDigCreditNote(creditNote_id);
        DbResult creditNoteRows_dbr = dao.readDigCreditNoteRows(creditNote_id);
        DbResult cessionarioCommittente_dbr = dao.readCNCessionarioCommittente(creditNote_id);
        
        //instantiates the class
        digCreditNote = new DigitalCreditNote_Duesse(creditNote_dbr, creditNoteRows_dbr, cessionarioCommittente_dbr );
        
        //adds the attachment
       // digInvoice.nuovoAllegato().loadAttachment("C:\\AppResources\\Samurai\\INVOICES\\FATTURA_DUESSE_"+invoice_dbr.getLong("number")+"_"+invoice_dbr.getInteger("year")+".pdf");
        
        //calls the creating xml method
        digCreditNote.toFile(filePath);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        // TODO code application logic here
    }
    
}
