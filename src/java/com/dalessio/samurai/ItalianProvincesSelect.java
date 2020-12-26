/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dalessio.samurai;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author Franco
 */
public class ItalianProvincesSelect
{
    //CSV file header
    private static final String [] FILE_HEADER_MAPPING = {"number","region","sNumber","code","name"};
	
	//province attributes
	private static final String CODE = "code";
	private static final String NAME = "name";
	
	public void readCsvFile(String fileName) {

		FileReader fileReader = null;
		
		CSVParser csvFileParser = null;
		
		//Create the CSVFormat object with the header mapping
                CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(FILE_HEADER_MAPPING);
                
        try {
        	
        	//Create a new list of student to be filled by CSV file data 
        	List<Province> provinces  = new ArrayList<>();
            
                //initialize FileReader object
                fileReader = new FileReader(fileName);
            
                //initialize CSVParser object
                csvFileParser = new CSVParser(fileReader, csvFileFormat.withDelimiter(';'));
            
                //Get a list of CSV file records
                List csvRecords = csvFileParser.getRecords(); 
            
                //Read the CSV file records starting from the second record to skip the header
                for (int i = 1; i < csvRecords.size(); i++)
                {
                    CSVRecord record = ( CSVRecord ) csvRecords.get(i);
                    //Create a new student object and fill his data
                    Province province = new Province( record.get(CODE), record.get(NAME));
                    provinces.add(province);	
                }
            
                String selectCode = "<select>\n";
                
                for( Province province: provinces )
                {
                    selectCode += "<option value=\""+province.code+"\">"+province.name+" ("+province.code+")</option>\n";
                }
                
                selectCode +="</select>";
                
                
                System.out.print(selectCode);
                
        } 
        catch (Exception e) {
        	System.out.println("Error in CsvFileReader !!!");
            e.printStackTrace();
        } finally {
            try {
                fileReader.close();
                csvFileParser.close();
            } catch (IOException e) {
            	System.out.println("Error while closing fileReader/csvFileParser !!!");
                e.printStackTrace();
            }
        }

	}
        

    class Province
    {
        String code;
        String name;
        Province( String code, String name)
        {
            this.code = code;
            this.name = name;
        }
    }
    public static void main(String[] args)
    {
       new ItalianProvincesSelect(). 
       readCsvFile("C:\\Users\\Franco\\Documents\\NetBeansProjects\\LAVORO\\Samurai\\Samurai\\CSV\\Province.csv");//C:\Users\Franco\Documents\NetBeansProjects\LAVORO\Samurai\Samurai\CSV\Province.csv
    }
}
