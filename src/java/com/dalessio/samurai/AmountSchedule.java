
package com.dalessio.samurai;

import com.dps.dbi.DbResult;
import com.google.gson.JsonObject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class AmountSchedule
{
    public Long invoice_id;
    public Long number;
    public Integer year;
    public String denomination;
    public Integer ordinal;
    public Double amount;
    public LocalDate amountDate;
    
    public AmountSchedule(){}
    
    public AmountSchedule( DbResult.Record dbr )
    {
        this.invoice_id = dbr.getLong("invoice_id");
        this.number = dbr.getLong("number");
        this.year = dbr.getInteger("year");
        this.denomination = dbr.getString("denomination");
        this.ordinal = dbr.getInteger("ordinal");
        this.amount = dbr.getDouble("amount");
        String dateString = dbr.getString("amountDate");
        String year = dateString.substring(0,4);
        String month = dateString.substring(4,6);
        String day = dateString.substring(6);
        String date = year+"-"+month+"-"+day;
        this.amountDate = LocalDate.parse(date);
    }
    
    public static JsonObject getJson( AmountSchedule schedule)
    {
        JsonObject json = new JsonObject();
        
        json.addProperty("invoice_id", schedule.invoice_id);
        json.addProperty("number", schedule.number);
        json.addProperty("year", schedule.year);
        json.addProperty("denomination", schedule.denomination);
        json.addProperty("ordinal", schedule.ordinal);
        json.addProperty("amount", schedule.amount);
        json.addProperty("amountDate", DateTimeFormatter.ISO_LOCAL_DATE.format(schedule.amountDate));
        
        return json;
    }
    
    public static JsonObject getJsonByDbResults( DbResult.Record dbr )
    {
        JsonObject json = new JsonObject();
        
        json.addProperty("invoice_id", dbr.getLong("invoice_id"));
        json.addProperty("number", dbr.getLong("number"));
        json.addProperty("year", dbr.getInteger("year"));
        json.addProperty("denomination", dbr.getString("denomination"));
        json.addProperty("ordinal", dbr.getInteger("ordinal"));
        json.addProperty("amount", dbr.getDouble("amount"));
        json.addProperty("amountDate", dbr.getString("amountDate"));
        
        return json;
    }
}
