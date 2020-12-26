
import com.dalessio.samurai.DataAccessObject;
import com.dalessio.samurai.DeliveryNotePdfPrinter_1;
import com.dps.dbi.DbResult;
import java.io.IOException;
import java.sql.SQLException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Franco
 */
public class DeliveryNoteUpdater
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ClassNotFoundException,SQLException,IOException
    {
        // reads session data
        DataAccessObject dao = new DataAccessObject();
            
        // reads operation parameters
        Long deliveryNote_id = 0L;
            
            
        // retrieve parameters
        DbResult deliveryNoteDbr = dao.readDeliveryNotes(117L, null, null, null, null, null);
            
        DbResult deliveryNoteRowsDbr = dao.getDeliveryNoteRowsByDeliveryNoteId(deliveryNote_id);
            
        //intialize and call pdf printer that puts the pdf file in the destination folder 
        DeliveryNotePdfPrinter_1 pdfPrinter = new DeliveryNotePdfPrinter_1();
        pdfPrinter.printDeliveryNote(deliveryNote_id);
    }
    
}
