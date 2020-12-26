/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dalessio.samurai;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 *
 * @author Franco
 */
public class FileCopy
{
    /** COPIA DA UN FILE ALL'ALTRO */
    public static void copyWithChannels(File source, File target, boolean append)
    {
        target.getParentFile().mkdirs();
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try
        {
            try 
            {
                inStream = new FileInputStream(source);
                inChannel = inStream.getChannel();
                outStream = new  FileOutputStream(target, append);        
                outChannel = outStream.getChannel();
                long bytesTransferred = 0;
                //defensive loop - there's usually only a single iteration :
                while(bytesTransferred < inChannel.size())
                {
                    bytesTransferred += inChannel.transferTo(0, inChannel.size(), outChannel);
                }
            }
            finally
            {
                //being defensive about closing all channels and streams 
                if (inChannel != null) inChannel.close();
                if (outChannel != null) outChannel.close();
                if (inStream != null) inStream.close();
                if (outStream != null) outStream.close();
            }
        }
        catch (FileNotFoundException  ex)
        {
          ex.printStackTrace();
        }
        catch (IOException ex){
          ex.printStackTrace();
        }
    }
}
