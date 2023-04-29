/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dalessio.samurai;

import com.dps.dbi.DbResult;
import com.dps.utils.json.JSON;
import com.google.gson.JsonObject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Franco
 */
public class ComplianceCertificate
{
    public Long complianceCertificate_id;
    public Long order_id;
    public Long customer_id;/* from view */
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
    public String orderCode;
    public String customerJobOrderCode;
    public String orderDescription;
    public String firstName;
    public String lastName;
    
    public ComplianceCertificate(){}
    
    public ComplianceCertificate( DbResult complianceCertificateViewDbr )
    {
        complianceCertificate_id = complianceCertificateViewDbr.getLong("complianceCertificate_id");
        order_id = complianceCertificateViewDbr.getLong("order_id");
        customer_id = complianceCertificateViewDbr.getLong("customer_id");
        firstTitle_id = complianceCertificateViewDbr.getLong("firstTitle_id");
        secondTitle_id = complianceCertificateViewDbr.getLong("secondTitle_id");
        number = complianceCertificateViewDbr.getLong("number");
        year = complianceCertificateViewDbr.getInteger("year");
        date = LocalDate.parse( complianceCertificateViewDbr.getDate("date").toString().substring(0,10) );
        customerDenomination = complianceCertificateViewDbr.getString("customerDenomination");/* from view */
        firstTitle = complianceCertificateViewDbr.getString("compCertFirstTitle");/* from view */
        secondTitle = complianceCertificateViewDbr.getString("compCertSecondTitle");/* from view */
        address = complianceCertificateViewDbr.getString("customerAddress");/* from view */
        houseNumber = complianceCertificateViewDbr.getString("customerHouseNumber");/* from view */
        postalCode = complianceCertificateViewDbr.getString("customerPostalCode");/* from view */
        city = complianceCertificateViewDbr.getString("customerCity");/* from view */
        province = complianceCertificateViewDbr.getString("customerProvince");/* from view */
        firstForAttention = complianceCertificateViewDbr.getString("compCertFirstForAttention");/* from view */
        secondForAttention = complianceCertificateViewDbr.getString("compCertSecondForAttention");/* from view */
        orderCode = complianceCertificateViewDbr.getString("code");/* from view */
        customerJobOrderCode = complianceCertificateViewDbr.getString("compCertCustomerJobCode");/* from view */
        orderDescription = complianceCertificateViewDbr.getString("compCertOrderDescription");/* from view */
        firstName = complianceCertificateViewDbr.getString("creatorFirstName");/* from view */
        lastName = complianceCertificateViewDbr.getString("creatorLastName");/* from view */
    }
    
    public ComplianceCertificate( JsonObject json )
    {
        complianceCertificate_id = JSON.getLong(json, "complianceCertificate_id");
        order_id = JSON.getLong(json, "order_id");
        customer_id = JSON.getLong(json, "customer_id");
        firstTitle_id = JSON.getLong(json, "firstTitle_id");
        secondTitle_id = JSON.getLong(json, "secondTitle_id");
        number = JSON.getLong(json, "number");
        year = JSON.getInteger(json, "year");
        date = LocalDate.parse(JSON.getString( json, "date").substring(0,10));
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
        orderCode = JSON.getString( json, "orderCode");
        customerJobOrderCode = JSON.getString( json, "customerJobOrderCode");
        orderDescription = JSON.getString( json, "orderDescription");
        firstName = JSON.getString( json, "firstName");
        lastName = JSON.getString( json, "lastName");
    }
    
    public static JsonObject getJson( ComplianceCertificate compCert  )
    {
        JsonObject jsonDN = new JsonObject();
        //all dyn_Invoice table attributes
        jsonDN.addProperty("complianceCertificate_id", compCert.complianceCertificate_id);
        jsonDN.addProperty("order_id", compCert.order_id);
        jsonDN.addProperty("customer_id", compCert.customer_id);
        jsonDN.addProperty("firstTitle_id", compCert.firstTitle_id);
        jsonDN.addProperty("secondTitle_id", compCert.secondTitle_id);
        jsonDN.addProperty("number", compCert.number);
        jsonDN.addProperty("year", compCert.year);
        jsonDN.addProperty("date", DateTimeFormatter.ISO_LOCAL_DATE.format(compCert.date));
        jsonDN.addProperty("customerDenomination", compCert.customerDenomination);
        jsonDN.addProperty("firstTitle", compCert.firstTitle);
        jsonDN.addProperty("secondTitle", compCert.secondTitle);
        jsonDN.addProperty("address", compCert.address);
        jsonDN.addProperty("houseNumber", compCert.houseNumber);
        jsonDN.addProperty("postalCode", compCert.postalCode);
        jsonDN.addProperty("city", compCert.city);
        jsonDN.addProperty("province", compCert.province);
        jsonDN.addProperty("firstForAttention", compCert.firstForAttention);
        jsonDN.addProperty("secondForAttention", compCert.secondForAttention);
        jsonDN.addProperty("orderCode", compCert.orderCode);
        jsonDN.addProperty("customerJobOrderCode", compCert.customerJobOrderCode);
        jsonDN.addProperty("orderDescription", compCert.orderDescription);
        jsonDN.addProperty("firstName", compCert.firstName);
        jsonDN.addProperty("lastName", compCert.lastName);
        
        return jsonDN;
    };
}
