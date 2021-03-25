package com.dalessio.samurai;

import com.dps.dbi.DbResult;
import com.dps.utils.json.JSON;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;

public class DeliveryNote
{
    //all dyn_DeliveryNote table attributes
    public Long deliveryNote_id;
    public Long customer_id;
    public Long transporter_id;
    public Integer number;
    public Integer year;
    public String date;
    public String destDenomination;
    public String destCity;
    public String destAddress;
    public String destHouseNumber;
    public String destPostalCode;
    public String destProvince;
    public String transportResponsable;
    public String transportReason;
    public String goodsExteriorAspect;
    public Integer packagesNumber;
    public Double weight;
    public String notes;
    public Boolean invoiced;
    
    public final List<Item> items = new ArrayList<>();

    public DeliveryNote(){}

    public DeliveryNote(String json)
    {
        init((JsonObject)new JsonParser().parse(json));
    }
    
    public DeliveryNote(JsonObject json){this.init(json);}
    
    public DeliveryNote(DbResult dbr, DbResult items_dbr){this.init(dbr,items_dbr);}
    
    public DeliveryNote(DbResult dbr, List<Item> items){this.init(dbr,items);}

    public final DeliveryNote init(JsonObject json)
    {
        try{deliveryNote_id = JSON.getLong(json,"deliveryNote_id");}catch(RuntimeException ex){}
        
        customer_id = JSON.getLong(json,"customer_id");
        transporter_id = JSON.getLong(json,"transporter_id");
        number = JSON.getInteger(json, "number");
        year = JSON.getInteger(json, "year");
        date = JSON.getString(json,"date");
        destDenomination = JSON.getString(json,"destDenomination");
        destCity = JSON.getString(json,"destCity");
        destAddress = JSON.getString(json,"destAddress");
        destHouseNumber = JSON.getString(json,"destHouseNumber");
        destPostalCode = JSON.getString(json,"destPostalCode");
        destProvince = JSON.getString(json,"destProvince");
        transportResponsable = JSON.getString(json,"transportResponsable");
        transportReason = JSON.getString(json,"transportReason");
        goodsExteriorAspect = JSON.getString(json,"goodsExteriorAspect");
        packagesNumber = JSON.getInteger(json,"packagesNumber");
        weight = JSON.getDouble(json,"weight");
        notes = JSON.getString(json,"notes");
        invoiced = JSON.getBoolean(json,"invoiced");
        
        items.clear();
        JsonArray rows = JSON.getJsonArray(json,"items");
        
        for(int i=0; i<rows.size(); i++)
            items.add(new Item().init(JSON.getJsonObject(rows,i)));
        
        return this;
    }

    public final DeliveryNote init(DbResult dbr, DbResult items_dbr)
    {
        try{deliveryNote_id = dbr.getLong("deliveryNote_id");}catch(RuntimeException ex){}
        
        customer_id = dbr.getLong("customer_id");
        transporter_id = dbr.getLong("transporter_id");
        number = dbr.getInteger("number");
        year = dbr.getInteger("year");
        date = dbr.getString("date");
        destDenomination = dbr.getString("destDenomination");
        destCity = dbr.getString("destCity");
        destAddress = dbr.getString("destAddress");
        destHouseNumber = dbr.getString("destHouseNumber");
        destPostalCode = dbr.getString("destPostalCode");
        destProvince = dbr.getString("destProvince");
        transportResponsable = dbr.getString("transportResponsable");
        transportReason = dbr.getString("transportReason");
        goodsExteriorAspect = dbr.getString("goodsExteriorAspect");
        packagesNumber = dbr.getInteger("packagesNumber");
        weight = dbr.getDouble("weight");
        notes = dbr.getString("notes");
        invoiced = dbr.getBoolean("invoiced");
        
        items.clear();
        
        for(int i=0; i<items_dbr.rowsCount(); i++)
            items.add(new Item().init(items_dbr,i));
        
        return this;
    }
    
    public final DeliveryNote init(DbResult dbr, List<Item> items)
    {
        try{deliveryNote_id = dbr.getLong("deliveryNote_id");}catch(RuntimeException ex){}
        
        customer_id = dbr.getLong("customer_id");
        transporter_id = dbr.getLong("transporter_id");
        number = dbr.getInteger("number");
        year = dbr.getInteger("year");
        date = dbr.getString("date");
        destDenomination = dbr.getString("destDenomination");
        destCity = dbr.getString("destCity");
        destAddress = dbr.getString("destAddress");
        destHouseNumber = dbr.getString("destHouseNumber");
        destPostalCode = dbr.getString("destPostalCode");
        destProvince = dbr.getString("destProvince");
        transportResponsable = dbr.getString("transportResponsable");
        transportReason = dbr.getString("transportReason");
        goodsExteriorAspect = dbr.getString("goodsExteriorAspect");
        packagesNumber = dbr.getInteger("packagesNumber");
        weight = dbr.getDouble("weight");
        notes = dbr.getString("notes");
        invoiced = dbr.getBoolean("invoced");
        
        items.clear();
        
        for(int i=0; i<items.size(); i++)
            items.add(items.get(i));
        
        return this;
    }
    
    //nested class
    public class Item
    {
        public String code;
        public Integer quantity;
        public String description;

        public Item(){}

        public Item(JsonObject json){this.init(json);}
        
        public Item( DbResult dbr, int i ){this.init( dbr,  i);}
        
        public final Item init(JsonObject json)
        {
            code = JSON.getString(json,"code");
            quantity = JSON.getInteger(json,"quantity");
            description = JSON.getString(json,"description");
            return this;
        }
        
        public final Item init(DbResult dbr, int i)
        {
            code = dbr.getString(i,"code");
            quantity = dbr.getInteger(i,"quantity");
            description = dbr.getString(i,"description");
            return this;
        }
        
    }
    
    /**
     * There is a difference between this Json object and java class instances
     * there is an additional property "denomination"  that's the customer name
     * This becouse the DbResult dbr coming from the deliveryNotes_view and not
     * from the deliveryNotes table. This property is needed in GUI to show delivery 
     * notes list in deliveryNotes.jsp page
     * @param dbr comes from dyn_DeliveryNotes_view
     * @param items_dbr contains delivery note rows
     * @return 
     */
    public static JsonObject getJsonByDbResults( DbResult dbr, DbResult items_dbr )
    {
        JsonObject jsonDN = new JsonObject();
        
        jsonDN.addProperty("deliveryNote_id", dbr.getLong("deliveryNote_id"));
        jsonDN.addProperty("customer_id", dbr.getLong("customer_id"));
        jsonDN.addProperty("denomination", dbr.getString("denomination"));
        jsonDN.addProperty("transporter_id", dbr.getLong("transporter_id"));
        jsonDN.addProperty("number", dbr.getInteger("number"));
        jsonDN.addProperty("year", dbr.getInteger("year"));
        jsonDN.addProperty("date", dbr.getString("date"));
        jsonDN.addProperty("destDenomination", dbr.getString("destDenomination"));
        jsonDN.addProperty("destCity", dbr.getString("destCity"));
        jsonDN.addProperty("destAddress", dbr.getString("destAddress"));
        jsonDN.addProperty("destHouseNumber", dbr.getString("destHouseNumber"));
        jsonDN.addProperty("destPostalCode", dbr.getString("destPostalCode"));
        jsonDN.addProperty("destProvince", dbr.getString("destProvince"));
        jsonDN.addProperty("transportResponsable", dbr.getString("transportResponsable"));
        jsonDN.addProperty("transportReason", dbr.getString("transportReason"));
        jsonDN.addProperty("goodsExteriorAspect", dbr.getString("goodsExteriorAspect"));
        jsonDN.addProperty("packagesNumber", dbr.getInteger("packagesNumber"));
        jsonDN.addProperty("weight", dbr.getDouble("weight"));
        jsonDN.addProperty("notes", dbr.getString("notes"));
        jsonDN.addProperty("invoiced", dbr.getBoolean("invoiced"));
        
        //creates items array
        JsonArray items = new JsonArray();
        //fills the array with delivery note rows JsonObjects
        for( int i=0; i < items_dbr.rowsCount(); i++ )
        {
            JsonObject row = new JsonObject();
            row.addProperty("code", items_dbr.getString(i,"code"));
            row.addProperty("description", items_dbr.getString(i,"description"));
            row.addProperty("quantity", items_dbr.getInteger(i,"quantity"));
            items.add(row);
        }
        
        //adds rows
        jsonDN.add("items", items);

        return jsonDN;
    };
  
    /**
     * There is a difference between this Json object and java class instances
     * there is an additional property "denomination"  that's the customer name
     * This becouse the DbResult dbr coming from the deliveryNotes_view and not
     * from the deliveryNotes table. This property is needed in GUI to show delivery 
     * notes list in deliveryNotes.jsp page
     * @param dbr comes from dyn_DeliveryNotes_view
     * @param itemsArrayList contains delivery note rows
     * @param index db result index to detect the correct row from wich take data
     * @return 
     */
    public static JsonObject getJsonByDbResults( DbResult dbr, int index, List<Item> itemsArrayList )
    {
        JsonObject jsonDN = new JsonObject();
        
        jsonDN.addProperty("deliveryNote_id", dbr.getLong( index, "deliveryNote_id"));
        jsonDN.addProperty("customer_id", dbr.getLong( index, "customer_id"));
        jsonDN.addProperty("denomination", dbr.getString( index, "denomination"));
        jsonDN.addProperty("transporter_id", dbr.getLong( index, "transporter_id"));
        jsonDN.addProperty("number", dbr.getInteger( index, "number"));
        jsonDN.addProperty("year", dbr.getInteger( index, "year"));
        jsonDN.addProperty("date", dbr.getString( index, "date"));
        jsonDN.addProperty("destDenomination", dbr.getString( index, "destDenomination"));
        jsonDN.addProperty("destCity", dbr.getString( index, "destCity"));
        jsonDN.addProperty("destAddress", dbr.getString( index, "destAddress"));
        jsonDN.addProperty("destHouseNumber", dbr.getString( index, "destHouseNumber"));
        jsonDN.addProperty("destPostalCode", dbr.getString( index, "destPostalCode"));
        jsonDN.addProperty("destProvince", dbr.getString( index, "destProvince"));
        jsonDN.addProperty("transportResponsable", dbr.getString( index, "transportResponsable"));
        jsonDN.addProperty("transportReason", dbr.getString( index, "transportReason"));
        jsonDN.addProperty("goodsExteriorAspect", dbr.getString( index, "goodsExteriorAspect"));
        jsonDN.addProperty("packagesNumber", dbr.getInteger( index, "packagesNumber"));
        jsonDN.addProperty("weight", dbr.getDouble( index, "weight"));
        jsonDN.addProperty("notes", dbr.getString( index, "notes"));
        jsonDN.addProperty("invoiced", dbr.getBoolean( index, "invoiced"));
        
        //creates items array
        JsonArray items = new JsonArray();
        
        //fills the array with delivery note rows JsonObjects
        for( int i=0; i < itemsArrayList.size(); i++ )
        {
            JsonObject row = new JsonObject();
            row.addProperty("code", itemsArrayList.get(i).code );
            row.addProperty("description", itemsArrayList.get(i).description );
            row.addProperty("quantity", itemsArrayList.get(i).quantity );
            items.add(row);
        }
        
        //adds rows
        jsonDN.add("items", items);

        return jsonDN;
    };
}
