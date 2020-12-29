
package util;

import com.dalessio.samurai.DataAccessObject;
import com.dps.dbi.DbResult;
import java.sql.SQLException;
import java.time.LocalDate;


public class ScheduleFiller 
{

    // reads session data
    static DataAccessObject dao = null;
    
    
    
    public static void main(String[] args)
    {
        String sql = "";
        
        try{
            
            dao = (DataAccessObject) new DataAccessObject();
            DbResult invoice_dbr = dao.readInvoices( null, null, null, null, null );
            
            for( int i = 0; i < invoice_dbr.rowsCount(); i++ ){
                
                sql += "INSERT INTO dyn_AmountsSchedule ( invoice_id, ordinal, amount, scheduleDate ) VALUES ( " + 
                        invoice_dbr.getLong(i, "invoice_id" ) + "," +
                        " 1, " +
                        invoice_dbr.getDouble(i, "firstAmount" ) + " , " +
                        invoice_dbr.getString(i, "firstAmountDate" ) + ";\n" +
                        "INSERT INTO dyn_AmountsSchedule ( invoice_id, ordinal, amount, scheduleDate ) VALUES ( " + 
                        invoice_dbr.getLong(i, "invoice_id" ) + "," +
                        " 2, " +
                        invoice_dbr.getDouble(i, "secondAmount" ) + " , " +
                        invoice_dbr.getString(i, "secondAmountDate" ) + ";\n" +
                        "INSERT INTO dyn_AmountsSchedule ( invoice_id, ordinal, amount, scheduleDate ) VALUES ( " + 
                        invoice_dbr.getLong(i, "invoice_id" ) + "," +
                        " 3, " +
                        invoice_dbr.getDouble(i, "thirdAmount" ) + " , " +
                        invoice_dbr.getString(i, "thirdAmountDate" ) + ";\n";
            }
        }catch( ClassNotFoundException | SQLException ex){ ex.printStackTrace(); }
        
        System.out.println(sql);
        
    }
    
}
