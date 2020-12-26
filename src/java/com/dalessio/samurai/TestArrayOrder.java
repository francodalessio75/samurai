package com.dalessio.samurai;

import com.dps.dbi.DbResult;
import com.dps.dbi.impl.SqlServerInterface;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

public class TestArrayOrder
{
    public static void main(String[] args) throws SQLException
    {
        SqlServerInterface dbi = new SqlServerInterface();
        dbi.address(Config.SERVER_ADDRESS);
        dbi.name("WorkLine");
        dbi.username("workline");
        dbi.password("workline");
        
        Date start = new Date();
        DbResult customers_dbr = dbi.read("reg_Customers")
            //.order("denomination")
            .go();
        
        Arrays.sort(customers_dbr.records,
            (r1,r2)->
            {
                String d1 = (String) r1[3];
                String d2 = (String) r2[3];
                
                if(d1==null) d1 = "";
                if(d2==null) d2 = "";
                
                return d1.compareTo(d2);
            }
        );
        
        Date end = new Date();
        
        System.out.println(customers_dbr.toConsoleTable());
        System.out.println("ELAPSED MSEC : "+(end.getTime()-start.getTime()));
    }
}
