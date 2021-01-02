/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dalessio.samurai.digitalinvoice;

import com.dalessio.samurai.DataAccessObject;
import com.dps.dbi.DbResult;
import java.sql.SQLException;


public class AmountSheduleFiller
{
    DataAccessObject dao;
    DbResult invoice_dbr;
    
    AmountSheduleFiller(){
        try{
            //Data Access Object
            dao = new DataAccessObject();
            DbResult invoice_dbr;
            
        }catch( ClassNotFoundException ex  ){ ex.printStackTrace(); }
        
        try{
            if( dao != null )
                invoice_dbr = (DbResult) dao.readInvoices( null, null, null, null, null );
        }catch( SQLException ex ){ ex.printStackTrace(); }
        
         
    }
    
    
    public static void main(String[] args)
    {
        AmountSheduleFiller ASF =  new AmountSheduleFiller();
    }
    
}
