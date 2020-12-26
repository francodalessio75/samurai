package com.dalessio.samurai;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

public class HttpUploader
{
    public final String basePath;
    
    public HttpUploader(String basePath)
    {
        while(basePath.endsWith("\\"))
            basePath = basePath.substring(0,basePath.length()-1);
        
        this.basePath = basePath;
    }
            
    public void upload(String fileName, long fileSize, String dataBase64) throws IOException
    {
        System.out.println("Start upload...");
        System.out.println("Current dir: "+System.getProperty("user.dir"));
        String hsep = "\n------------------------------\n";
        int maxLength = 64;

        try
        {
            System.out.println("name : "+fileName);
            System.out.println("size : "+fileSize);
            System.out.println("dataBase64 :\n"+hsep+shorten(dataBase64,maxLength)+hsep);

            dataBase64 = dataBase64.substring(dataBase64.indexOf("base64")+7);
            System.out.println("dataBase64 (no preamble):"+hsep+shorten(dataBase64,maxLength)+hsep);

            dataBase64 = dataBase64.replaceAll(" ","+");
            System.out.println("dataBase64 (no whitespaces):\n"+hsep+shorten(dataBase64,maxLength)+hsep);

            byte[] data = Base64.getDecoder().decode(dataBase64);

            File file = new File(basePath+"\\"+fileName);

            System.out.println(file.getAbsolutePath());

            try(FileOutputStream fos = new FileOutputStream(file))
            {
                fos.write(data);
                fos.close();
            }
        }
        catch(IOException ex)
        {
            System.out.println("Upload ERROR.");
            System.out.println(ex);
            throw ex;
        }

        System.out.println("Upload SUCCESS.");
    }

    public static String shorten(String string, int maxLength)
    {
        if(string.length()>maxLength)
            string = string.substring(0,maxLength/2) + " ... " + string.substring(string.length()-maxLength/2,string.length());
        
        return string;
    }
}
