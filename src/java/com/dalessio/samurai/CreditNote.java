package com.dalessio.samurai;

import com.dps.dbi.DbResult;
import com.dps.utils.json.JSON;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;

public class CreditNote
{
    //all dyn_CreditNotes table attributes
    public Long creditNote_id;
    public Long customer_id;
    public Long number;
    public Integer year;
    public String date;
    public Double taxableAmount;
    public Double taxAmount;
    public Double vatRate;
    public Double totalAmount;
    public String notes;
    public Boolean exempt;
    
    public final List<Item> items = new ArrayList<>();

    public CreditNote(){}

    public CreditNote(String json)
    {
        init((JsonObject)new JsonParser().parse(json));
    }
    
    public CreditNote(JsonObject json){this.init(json);}
    
    public CreditNote(DbResult dbr, DbResult items_dbr){this.init(dbr,items_dbr);}
    
    public CreditNote(DbResult dbr, List<Item> items){this.init(dbr,items);}

    public final CreditNote init(JsonObject json)
    {
        try{creditNote_id = JSON.getLong(json,"creditNote_id");}catch(RuntimeException ex){}
        
        customer_id = JSON.getLong(json,"customer_id");
        number = JSON.getLong(json, "number");
        year = JSON.getInteger(json, "year");
        date = JSON.getString(json,"date");
        taxableAmount = JSON.getDouble(json, "taxableAmount");
        taxAmount = JSON.getDouble(json, "taxAmount");
        vatRate = JSON.getDouble(json, "vatRate");
        totalAmount = JSON.getDouble(json, "totalAmount");
        notes = JSON.getString(json,"notes");
        exempt = JSON.getBoolean(json, "exempt");
        
        items.clear();
        JsonArray rows = JSON.getJsonArray(json,"items");
        
        for(int i=0; i<rows.size(); i++)
            items.add(new Item().init(JSON.getJsonObject(rows,i)));
        
        return this;
    }

    public final CreditNote init(DbResult dbr, DbResult items_dbr)
    {
        try{creditNote_id = dbr.getLong("creditNote_id");}catch(RuntimeException ex){}
        
        customer_id = dbr.getLong("customer_id");
        number = dbr.getLong( "number");
        year = dbr.getInteger( "year");
        date = dbr.getString("date");
        taxableAmount = dbr.getDouble("taxableAmount");
        taxAmount = dbr.getDouble("taxAmount");
        vatRate = dbr.getDouble("aliquotaIVA");
        totalAmount = dbr.getDouble("totalAmount");
        notes = dbr.getString("notes");
        exempt = dbr.getBoolean("exempt");
        
        items.clear();
        
        for(int i=0; i<items_dbr.rowsCount(); i++)
            items.add(new Item().init(items_dbr,i));
        
        return this;
    }
    
    public final CreditNote init(DbResult dbr, List<Item> items)
    {
        try{creditNote_id = dbr.getLong("creditNote_id");}catch(RuntimeException ex){}
        
        customer_id = dbr.getLong("customer_id");
        number = dbr.getLong( "number");
        year = dbr.getInteger( "year");
        date = dbr.getString("date");
        taxableAmount = dbr.getDouble("taxableAmount");
        taxAmount = dbr.getDouble("taxAmount");
        vatRate = dbr.getDouble("aliquotaIVA");
        totalAmount = dbr.getDouble("totalAmount");
        notes = dbr.getString("notes");
        exempt = dbr.getBoolean("exempt");
        
        items.clear();
        
        for(int i=0; i<items.size(); i++)
            items.add(items.get(i));
        
        return this;
    }
    
    //nested class
    public class Item
    {
        public Long creditNoteRow_id;
        public String description;
        public Integer quantity;
        public Double singleAmount;
        public Double totalAmount;

        public Item(){}

        public Item(JsonObject json){this.init(json);}
        
        public Item( DbResult dbr, int i ){this.init( dbr,  i);}
        
        public final Item init(JsonObject json)
        {
            quantity = JSON.getInteger(json,"quantity");
            description = JSON.getString(json,"description");
            singleAmount = JSON.getDouble(json,"singleAmount");
            totalAmount = JSON.getDouble(json,"totalAmount");
            
            return this;
        }
        
        public final Item init(DbResult dbr, int i)
        {
            quantity = dbr.getInteger(i,"quantity");
            description = dbr.getString(i,"description");
            singleAmount = dbr.getDouble("singleAmount");
            totalAmount =  dbr.getDouble("totalAmount");
            return this;
        }
    }
    
    
    
    /**
     * There is a difference between this Json object and java class instances
     * there is an additional property "denomination"  that's the customer name
     * This becouse the DbResult dbr coming from the dyn_CreditNotes_view and not
     * from the credit notes table. This property is needed in GUI to show credit
     * notes list in creditNotes.jsp page
     * @param dbr comes from dyn_CreditNOtes_view
     * @param items_dbr contains credit note rows
     * @return 
     */
    public static JsonObject getJsonByDbResults( DbResult dbr, DbResult items_dbr )
    {
        JsonObject jsonDN = new JsonObject();
        //all dyn_CreditNotes table attributes
        jsonDN.addProperty("creditNote_id", dbr.getLong("creditNote_id"));
        jsonDN.addProperty("customer_id", dbr.getLong("customer_id"));
        jsonDN.addProperty("denomination", dbr.getString("denomination"));
        jsonDN.addProperty("number", dbr.getLong("number"));
        jsonDN.addProperty("year", dbr.getInteger("year"));
        jsonDN.addProperty("date", dbr.getString("date"));
        jsonDN.addProperty("taxableAmount", dbr.getDouble("taxableAmount"));
        jsonDN.addProperty("taxAmount", dbr.getDouble("taxAmount"));
        jsonDN.addProperty("vatRate", dbr.getDouble("aliquotaIVA"));
        jsonDN.addProperty("totalAmount", dbr.getDouble("totalAmount"));
        jsonDN.addProperty("notes", dbr.getString("notes"));
        jsonDN.addProperty("exempt", dbr.getBoolean("exempt"));
        
        //creates items array
        JsonArray items = new JsonArray();
        //fills the array with delivery note rows JsonObjects
        for( int i=0; i < items_dbr.rowsCount(); i++ )
        {
            JsonObject row = new JsonObject();
            row.addProperty("creditNoteRow_id", items_dbr.getLong(i,"creditNoteRow_id"));
            row.addProperty("description", items_dbr.getString(i,"description"));
            row.addProperty("totalAmount", items_dbr.getDouble(i,"totalAmount"));
            
            items.add(row);
        }
        
        //adds rows
        jsonDN.add("items", items);

        return jsonDN;
    };
    
    /**
     * There is a difference between this Json object and java class instances
     * there is an additional property "denomination"  that's the customer name
     * This becouse the DbResult dbr coming from the dyn_Invoices_view and not
     * from the invoices table. This property is needed in GUI to show delivery 
     * notes list in deliveryNotes.jsp page
     * @param dbr comes from dyn_Invoices_view
     * @param index comes from dyn_Invoices_view
     * @param itemsArrayList contains invoice rows
     * @return 
     */
    public static JsonObject getJsonByDbResults( DbResult dbr, int index, List<Item> itemsArrayList )
    {
        JsonObject jsonDN = new JsonObject();
        //all dyn_Invoice table attributes
        jsonDN.addProperty("creditNote_id", dbr.getLong( index, "creditNote_id"));
        jsonDN.addProperty("customer_id", dbr.getLong( index, "customer_id"));
        jsonDN.addProperty("denomination", dbr.getString( index, "denomination"));
        jsonDN.addProperty("number", dbr.getLong( index, "number"));
        jsonDN.addProperty("year", dbr.getInteger( index, "year"));
        jsonDN.addProperty("date", dbr.getString( index, "date"));
        jsonDN.addProperty("taxableAmount", dbr.getDouble( index, "taxableAmount"));
        jsonDN.addProperty("taxAmount", dbr.getDouble( index, "taxAmount"));
        jsonDN.addProperty("vatRate", dbr.getDouble( index, "aliquotaIVA"));
        jsonDN.addProperty("totalAmount", dbr.getDouble( index, "totalAmount"));
        jsonDN.addProperty("paymentConditions", dbr.getString( index, "paymentConditions"));
        jsonDN.addProperty("notes", dbr.getString( index, "notes"));
        jsonDN.addProperty("exempt", dbr.getBoolean( index, "exempt"));
        
        //creates items array
        JsonArray items = new JsonArray();
        
        //fills the array with delivery note rows JsonObjects
        for( int i=0; i < itemsArrayList.size(); i++ )
        {
            JsonObject row = new JsonObject();
            row.addProperty("creditNoteRow_id", itemsArrayList.get(i).creditNoteRow_id);
            row.addProperty("description", itemsArrayList.get(i).description);
            row.addProperty("totalAmount", itemsArrayList.get(i).totalAmount);
            
            items.add(row);
        }
        
        //adds rows
        jsonDN.add("items", items);

        return jsonDN;
    };
  
}

