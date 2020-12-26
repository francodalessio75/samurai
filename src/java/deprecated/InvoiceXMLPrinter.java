/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deprecated;

import com.dalessio.samurai.Config;
import com.dalessio.samurai.DataAccessObject;
import com.dps.dbi.DbResult;
import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 *
 * @author Franco
 */
public class InvoiceXMLPrinter
{
    //DAO
    DataAccessObject dao;
    
    public InvoiceXMLPrinter() throws ClassNotFoundException
    {
        dao = new DataAccessObject();
    }
    
    public void createInvoiceXML( Long invoice_id, DbResult invoice, DbResult invoiceRows) throws IOException, SQLException, ClassNotFoundException
    {
        //PATH DEL FILE MODELLO
        final String MODEL = Config.INVOICES_DIR + "invoiceModel.xml";
        
        //PATH DEL FILE DESTINAZIONE
        final String DEST = Config.INVOICES_DIR + "IT03637480124_001.xml";
        
        //CREAZIONE FILE MODELLO
        File source = new File(MODEL);
        
        //CREAZIONE FILE DESTINAZIONE
        File target = new File(DEST);
        target.getParentFile().mkdirs();
        
        //COPIA IL FILE MODELLO NEL FILE DESTINZAIONE
        copyWithChannels( source, target, false);
        
        //changes the customer name
        try 
        {
            //CREA ISTANZA DOCUMENTO XML
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            
            //EFFETTUA PARSING DEL FILE SORGENTE
            Document doc = docBuilder.parse(source);
            
            //INDIVIDUA IL SECONDO NODO CON NOME DI TAG DENOMINAZIONE
            Node denominazione = doc.getElementsByTagName("Denominazione").item(1);
            
            //VISUALIZZA IL VALORE DI NODO ATTUALE
            System.out.println(denominazione.getTextContent());
            
            //MODIFICA VALORE NODO
            denominazione.setTextContent("Tailor Informatica");
            
            //VISUALIZZA NUOVO VALORE ( IN CASH )
            System.out.println(denominazione.getTextContent());
            
            //SALVA LE MODIFICHE
            //crea una istanza della classe Tranformer
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            //preleva il DOM
            DOMSource DOMsource = new DOMSource(doc);
            //crea uno stream in cui scrivere il dom modificato
            StreamResult targetResult = new StreamResult(target/*System.out*/);
            //salva la trasformazione
            transformer.transform(DOMsource, targetResult);
            
            //APRE IL FILE
            Desktop desktop = Desktop.getDesktop();
            desktop.open(new File(Config.INVOICES_DIR + "IT03637480124_001.xml"));
            
            
            /*DETECTS THE DIGITAL INVOICE ROOT
            Node FatturaElettronica = doc.getFirstChild();
            System.out.println(FatturaElettronica.getNodeName());
            //DETECTS THE CUSTOMER DATA NODE
            Node  datiCessionario = doc.getElementsByTagName("CessionarioCommittente").item(0);
            System.out.println(datiCessionario.getNodeName());
            
            //DETECTS CUTOMER REGISTER TREE ROOT DATA TREE
            //Node nodoAnagraficaCommittente = datiCessionario.getFirstChild();
           // System.out.println(nodoAnagraficaCommittente.getNodeName());
            
            //CUSTOMER REGISTER DATA CHILD NODES
            //NodeList figliAnagraficaCommittente = nodoAnagraficaCommittente.getChildNodes();
            
            //Node figlioAnagrafica = doc.getElementsByTagName("Denominazione").item(1);
            
            System.out.println(figlioAnagrafica.getNodeName());
            
            System.out.println(figlioAnagrafica.getTextContent());
            
            figlioAnagrafica.setTextContent("Lamborigini 001");
            
            System.out.println(figlioAnagrafica.getTextContent());
            
          // write the content on console
         TransformerFactory transformerFactory = TransformerFactory.newInstance();
         Transformer transformer = transformerFactory.newTransformer();
         DOMSource source1 = new DOMSource(doc);
         System.out.println("-----------Modified File-----------");
         StreamResult consoleResult = new StreamResult(target/*System.out);
        // transformer.transform(source1, consoleResult);
            
            
           // System.out.println(figlioAnagrafica.getNodeName());
            
            /*CUSTOMER REGISTER DATA CHILD NODES
            NodeList nodiDenominazione = figlioAnagrafica.getChildNodes();
            
            Node denominazione = nodiDenominazione.item(0);
            
            denominazione.setTextContent("Lamborigini 001");
            
            // write the content on console
         TransformerFactory transformerFactory = TransformerFactory.newInstance();
         Transformer transformer = transformerFactory.newTransformer();
         DOMSource source1 = new DOMSource(doc);
         System.out.println("-----------Modified File-----------");
         StreamResult consoleResult = new StreamResult(System.out);
         transformer.transform(source1, consoleResult);*/
          
            
        } catch (Exception e) { e.printStackTrace();}
        /*
        Node cars = doc.getFirstChild();
        
        try {
         DocumentBuilderFactory dbFactory =
         DocumentBuilderFactory.newInstance();
         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
         Document doc = dBuilder.newDocument();
         
         // root element
         Element rootElement = doc.createElement("cars");
         doc.appendChild(rootElement);

         // supercars element
         Element supercar = doc.createElement("supercars");
         rootElement.appendChild(supercar);

         // setting attribute to element
         Attr attr = doc.createAttribute("company");
         attr.setValue("Ferrari");
         supercar.setAttributeNode(attr);

         // carname element
         Element carname = doc.createElement("carname");
         Attr attrType = doc.createAttribute("type");
         attrType.setValue("formula one");
         carname.setAttributeNode(attrType);
         carname.appendChild(doc.createTextNode("Ferrari 101"));
         supercar.appendChild(carname);

         Element carname1 = doc.createElement("carname");
         Attr attrType1 = doc.createAttribute("type");
         attrType1.setValue("sports");
         carname1.setAttributeNode(attrType1);
         carname1.appendChild(doc.createTextNode("Ferrari 202"));
         supercar.appendChild(carname1);

         // write the content into xml file
         TransformerFactory transformerFactory = TransformerFactory.newInstance();
         Transformer transformer = transformerFactory.newTransformer();
         DOMSource source = new DOMSource(doc);
         StreamResult result = new StreamResult(new File(DEST));
         transformer.transform(source, result);
         
         // Output to console for testing
         StreamResult consoleResult = new StreamResult(System.out);
         transformer.transform(source, consoleResult);
      } catch (Exception e) {e.printStackTrace();}*/
    }
    
    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException
    {
        /*File file = new File(Config.INVOICES_DIR + "INVOICE_30094.xml");
        file.getParentFile().mkdirs();*/
        
        DataAccessObject dao = new DataAccessObject();
        
        InvoiceXMLPrinter printer = new InvoiceXMLPrinter();
        
        DbResult dbrInvoice = dao.readInvoices(30094L, null, null, null, null );
        
        DbResult dbrInvoiceRows = dao.readInvoiceRows(30094L);
        
        printer.createInvoiceXML(30094L, dbrInvoice, dbrInvoiceRows);
        
        //Desktop desktop = Desktop.getDesktop();
        //desktop.open(new File(Config.INVOICES_DIR + "INVOICE_prova.xml"));
    }
    
    /** COPIA DA UN FILE ALL'ALTRO */
    private void copyWithChannels(File source, File target, boolean append)
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
