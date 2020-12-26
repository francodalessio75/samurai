/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dalessio.samurai;

import com.dps.dbi.DbResult;
import com.dps.utils.json.JSON;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Franco
 */
public class Quote
{
    public Long quote_id;
    public Long customer_id;
    public Long user_id;
    public Long firstTitle_id;
    public Long secondTitle_id;
    public Long number;
    public int year;
    public LocalDate date;
    public String customerDenomination;/* from view */
    public String firstTitle;/* from view */
    public String secondTitle;/* from view */
    public String address;
    public String houseNumber;
    public String postalCode;
    public String city;
    public String province;
    public String firstForAttention;
    public String secondForAttention;
    public String subject;
    public Double amount;
    public String firstName;/*from view*/
    public String lastName;/*from view*/
    
    public final List<Row> rows = new ArrayList<>();
    
    public Quote(){}
    
    public Quote( DbResult.Record quote_view_dbr, DbResult quoteRows_dbr  )
    {
        quote_id = quote_view_dbr.getLong("quote_id");
        customer_id = quote_view_dbr.getLong("customer_id");
        user_id = quote_view_dbr.getLong("user_id");
        firstTitle_id = quote_view_dbr.getLong("firstTitle_id");
        secondTitle_id = quote_view_dbr.getLong("secondTitle_id");
        number = quote_view_dbr.getLong("number");
        year = quote_view_dbr.getInteger("year");
        date = LocalDate.parse(quote_view_dbr.getDate("date").toString().substring(0,10));
        customerDenomination = quote_view_dbr.getString("customerDenomination");
        firstTitle = quote_view_dbr.getString("firstTitle") == null ? "" : quote_view_dbr.getString("firstTitle") ;
        secondTitle = quote_view_dbr.getString("secondTitle") == null ? "" : quote_view_dbr.getString("secondTitle");
        customerDenomination = quote_view_dbr.getString("customerDenomination");
        address = quote_view_dbr.getString( "address") == null ? "" : quote_view_dbr.getString( "address");
        houseNumber = quote_view_dbr.getString( "houseNumber") == null ? "" : quote_view_dbr.getString( "houseNumber");
        postalCode = quote_view_dbr.getString(  "postalCode") == null ? "" : quote_view_dbr.getString(  "postalCode");
        city = quote_view_dbr.getString(  "city") == null ? "" : quote_view_dbr.getString(  "city");
        province = quote_view_dbr.getString(  "province") == null ? "" : quote_view_dbr.getString(  "province");
        firstForAttention = quote_view_dbr.getString(  "firstForAttention") == null ? "" : quote_view_dbr.getString(  "firstForAttention");
        secondForAttention = quote_view_dbr.getString(  "secondForAttention") == null ? "" : quote_view_dbr.getString(  "secondForAttention");
        subject = quote_view_dbr.getString(  "subject") == null ? "" : quote_view_dbr.getString(  "subject");  
        amount = quote_view_dbr.getDouble("amount");
        firstName = quote_view_dbr.getString("firstName");
        lastName = quote_view_dbr.getString("lastName");
        
        rows.clear();
        
        for(int i=0; i<quoteRows_dbr.rowsCount(); i++)
            rows.add(new Row( quoteRows_dbr.record(i)));
    }
    
    public Quote( JsonObject json )
    {
        quote_id = JSON.getLong(json, "quote_id");
        customer_id = JSON.getLong(json, "customer_id");
        user_id = JSON.getLong(json, "user_id");
        firstTitle_id = JSON.getLong(json, "firstTitle_id");
        secondTitle_id = JSON.getLong(json, "secondTitle_id");
        number = JSON.getLong(json, "number");
        year = JSON.getInteger(json, "year");
        date = LocalDate.parse(JSON.getString( json, "date"));
        customerDenomination = JSON.getString( json, "customerDenomination");
        firstTitle = JSON.getString( json, "firstTitle");
        secondTitle = JSON.getString( json, "secondTitle");
        address = JSON.getString( json, "address");
        houseNumber = JSON.getString( json, "houseNumber");
        postalCode = JSON.getString( json, "postalCode");
        city = JSON.getString( json, "city");
        province = JSON.getString( json, "province");
        firstForAttention = JSON.getString( json, "firstForAttention");
        secondForAttention = JSON.getString( json, "secondForAttention");
        subject = JSON.getString( json, "subject");   
        amount = JSON.getDouble( json, "amount");
        firstName = JSON.getString( json, "firstName"); 
        lastName = JSON.getString( json, "lastName"); 
        
        rows.clear();
        
        JsonArray items = JSON.getJsonArray(json,"rows");
        
        for(int i=0; i<items.size(); i++)
            rows.add(new Row( JSON.getJsonObject(items,i )));
    }
    
    class Row
    {
        public Long quoteRow_id;
        public Long quote_id;
        public String description;
        public Double rowAmount;
        
        public Row(){}
        
        public Row( DbResult.Record record )
        {
            quoteRow_id = record.getLong("quoteRow_id" );
            quote_id = record.getLong("quote_id");
            description = record.getString("description" );
            rowAmount = record.getDouble("rowAmount");
        }
        
        public Row( JsonObject json)
        {
            quoteRow_id = JSON.getLong( json, "quoteRow_id" );
            quote_id = JSON.getLong( json, "quote_id");
            description = JSON.getString( json, "description" );
            rowAmount = JSON.getDouble(json, "rowAmount");
        }
        
        public JsonObject readJson()
        {
            JsonObject json = new JsonObject();
            json.addProperty("quoteRow_id", quoteRow_id);
            json.addProperty("quote_id", quote_id);
            json.addProperty("description", description);
            json.addProperty("rowAmount", rowAmount);
            return json;
        }
    }
    
    
    public static JsonObject getJsonByDbResults( DbResult quote_view_dbr, DbResult rows_dbr  )
    {
        JsonObject jsonDN = new JsonObject();
        //all dyn_Invoice table attributes
        jsonDN.addProperty("quote_id", quote_view_dbr.getLong("quote_id"));
        jsonDN.addProperty("customer_id", quote_view_dbr.getLong("customer_id"));
        jsonDN.addProperty("user_id", quote_view_dbr.getLong("user_id"));
        jsonDN.addProperty("firstTitle_id", quote_view_dbr.getLong("firstTitle_id"));
        jsonDN.addProperty("secondTitle_id", quote_view_dbr.getLong("secondTitle_id"));
        jsonDN.addProperty("number", quote_view_dbr.getLong("number"));
        jsonDN.addProperty("year", quote_view_dbr.getInteger("year"));
        jsonDN.addProperty("date", DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDate.parse(quote_view_dbr.getDate("date").toString().substring(0,10))));
        jsonDN.addProperty("customerDenomination", quote_view_dbr.getString("customerDenomination"));
        jsonDN.addProperty("firstTitle", quote_view_dbr.getString("firstTitle"));
        jsonDN.addProperty("secondTitle", quote_view_dbr.getString("secondTitle"));
        jsonDN.addProperty("customerDenomination", quote_view_dbr.getString("customerDenomination"));
        jsonDN.addProperty("address", quote_view_dbr.getString("address"));
        jsonDN.addProperty("houseNumber", quote_view_dbr.getString("houseNumber"));
        jsonDN.addProperty("postalCode", quote_view_dbr.getString("postalCode"));
        jsonDN.addProperty("city", quote_view_dbr.getString("city"));
        jsonDN.addProperty("province", quote_view_dbr.getString("province"));
        jsonDN.addProperty("firstForAttention", quote_view_dbr.getString("firstForAttention"));
        jsonDN.addProperty("secondForAttention", quote_view_dbr.getString("secondForAttention"));
        jsonDN.addProperty("subject", quote_view_dbr.getString("subject"));
        jsonDN.addProperty("amount", quote_view_dbr.getDouble("amount"));
        jsonDN.addProperty("firstName", quote_view_dbr.getString("firstName"));
        jsonDN.addProperty("lastName", quote_view_dbr.getString("lastName"));
        
        //creates rows array
       JsonArray rows = new JsonArray();
        //fills the array with quote rows JsonObjects
        for( int i=0; i < rows_dbr.rowsCount(); i++ )
        {
            JsonObject row = new JsonObject();
            row.addProperty("quoteRow_id", rows_dbr.getLong("quoteRow_id"));
            row.addProperty("quote_id", rows_dbr.getLong("quote_id"));
            row.addProperty("description", rows_dbr.getLong("description"));
            row.addProperty("rowAmount", rows_dbr.getLong("rowAmount"));
           
            rows.add(row);
        }
        
        //adds rows
        jsonDN.add("rows", rows);

        return jsonDN;
    };
    
    public static JsonObject getJson( Quote quote  )
    {
        JsonObject jsonDN = new JsonObject();
        //all dyn_Invoice table attributes
        jsonDN.addProperty("quote_id", quote.quote_id);
        jsonDN.addProperty("customer_id", quote.customer_id);
        jsonDN.addProperty("user_id", quote.user_id );
        jsonDN.addProperty("firstTitle_id", quote.firstTitle_id);
        jsonDN.addProperty("secondTitle_id", quote.secondTitle_id);
        jsonDN.addProperty("number", quote.number);
        jsonDN.addProperty("year", quote.year);
        jsonDN.addProperty("date", DateTimeFormatter.ISO_LOCAL_DATE.format(quote.date));
        jsonDN.addProperty("customerDenomination", quote.customerDenomination);
        jsonDN.addProperty("firstTitle", quote.firstTitle);
        jsonDN.addProperty("secondTitle", quote.secondTitle);
        jsonDN.addProperty("address", quote.address);
        jsonDN.addProperty("houseNumber", quote.houseNumber);
        jsonDN.addProperty("postalCode", quote.postalCode);
        jsonDN.addProperty("city", quote.city);
        jsonDN.addProperty("province", quote.province);
        jsonDN.addProperty("firstForAttention", quote.firstForAttention);
        jsonDN.addProperty("secondForAttention", quote.secondForAttention);
        jsonDN.addProperty("subject", quote.subject);
        jsonDN.addProperty("amount", quote.amount);
        jsonDN.addProperty("firstName", quote.firstName);
        jsonDN.addProperty("lastName", quote.lastName);
        
        //creates rows array
        JsonArray rows = new JsonArray();
        //fills the array with quote rows JsonObjects
        for( int i=0; i < quote.rows.size(); i++ )
        {
            JsonObject row = new JsonObject();
            row.addProperty("quoteRow_id", quote.rows.get(i).quoteRow_id);
            row.addProperty("quote_id", quote.rows.get(i).quote_id);
            row.addProperty("description", quote.rows.get(i).description);
            row.addProperty("rowAmount", quote.rows.get(i).rowAmount);

            rows.add(row);
        }
        
        //adds rows
        jsonDN.add("rows", rows);

        return jsonDN;
    };
    
}
