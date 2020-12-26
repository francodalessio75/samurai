import com.dalessio.samurai.DataAccessObject;
import com.dps.dbi.impl.SqlServerInterface;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class Importer
{
    public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException
    {
        DataAccessObject dao = new DataAccessObject();
        
        BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\Franco\\Desktop\\Progetti\\Paolo\\Samurai\\Importazione\\excel\\excel.csv"));
        
        List<String[]> records = new ArrayList<>();
        
        String line;
        while((line=reader.readLine())!=null)
        {
            //System.out.println(line);
            String[] data = line.split(";");
            if(data.length<=12) records.add(data);
        }
        
        StringJoiner columns = new StringJoiner(",");
        for(String column : records.get(0))
            columns.add("["+column.replace("'","''").replace("Mod. Macchina","ModMacchina")+"]");
        
        for(int i=1; i<records.size(); i++)
        {
            StringJoiner values = new StringJoiner(",");
            for(String value : records.get(i))
                values.add("'"+value.replace("'","''")+"'");
            
            for(int j=0; j<12-records.get(i).length; j++)
                values.add("''");
            
            String sql = "INSERT INTO dbo.lavori("+columns.toString()+") VALUES("+values+")";
            
            System.out.println(sql);
        }
    }
}
