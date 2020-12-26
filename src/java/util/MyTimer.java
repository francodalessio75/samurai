package util;

import com.dps.dbi.DbResult;
import com.dps.dbi.impl.SqlServerInterface;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

public class MyTimer
{
    public void killConnections(SqlServerInterface dbi)
    {
        Timer timer = new Timer("AMMAZZA CONNESSIONI DORMIENTI");
        Calendar today = new GregorianCalendar();
        today.set(Calendar.HOUR,0);
        today.set(Calendar.MINUTE,0);
        today.set(Calendar.SECOND,0);
        
        timer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                System.out.println("CANCELLAZIONE PROCESSI DORMIENTI");
                try
                {
                    DbResult dbr = dbi.execAndCheck("EXEC sp_who;").result();
                    dbr.stream()
                        .filter(record->"WorkLine".equals(record.getString("dbname")) && "sleeping".equals(record.getString("status")))
                        .forEach(record->
                        {
                            try
                            {
                                dbi.execAndCheck("KILL "+record.getLong("spid"));
                            }
                            catch (SQLException ex)
                            {
                                System.out.println("EXCEPTION "+ex);
                            }
                        });
                }
                catch (SQLException ex)
                {
                    System.out.println("EXCEPTION "+ex);
                }
                System.out.println("FINITO!!!");
            }
        },today.getTime(),24*60*60*1000);
    }
    public static void main(String[] args)
    {
        Timer timer = new Timer("AMMAZZA CONNESSIONI DORMIENTI");
        SqlServerInterface dbi = new SqlServerInterface();
        Calendar today = new GregorianCalendar();
        today.set(Calendar.HOUR,0);
        today.set(Calendar.MINUTE,0);
        today.set(Calendar.SECOND,0);
        
        timer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                System.out.println("CANCELLAZIONE PROCESSI DORMIENTI");
                try
                {
                    DbResult dbr = dbi.execAndCheck("EXEC sp_who;").result();
                    dbr.stream()
                        .filter(record->"WorkLine".equals(record.getString("dbname")) && "sleeping".equals(record.getString("status")))
                        .forEach(record->
                        {
                            try
                            {
                                dbi.execAndCheck("KILL "+record.getLong("spid"));
                            }
                            catch (SQLException ex)
                            {
                                System.out.println("EXCEPTION "+ex);
                            }
                        });
                }
                catch (SQLException ex)
                {
                    System.out.println("EXCEPTION "+ex);
                }
                System.out.println("FINITO!!!");
            }
        },today.getTime(),24*60*60*1000);
    }
}
