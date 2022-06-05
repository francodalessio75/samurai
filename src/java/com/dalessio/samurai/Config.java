package com.dalessio.samurai;

/**
 *e
 * @author Franco D'Alessio
 */
public class Config
{
    /********************* DIRECTORIES FOR ATTACHMENTS MANAGMENT ***************/
    public static final String RESOURCES_DIR = "C:\\AppResources\\Samurai";
    
    public static final String TASKS_ATTACH_DIR = RESOURCES_DIR+"\\TaskAttachments\\";
    public static final String CUSTOMERS_LOGO_DIR = RESOURCES_DIR+"\\Logo\\";
    public static final String DDT_DIR = RESOURCES_DIR+"\\DDT\\";
    public static final String INVOICES_DIR = RESOURCES_DIR+"\\INVOICES\\";
    public static final String CREDIT_NOTES_DIR = RESOURCES_DIR+"\\CREDIT_NOTES\\";
    public static final String DIG_INVOICES_DIR = RESOURCES_DIR+"\\DIG_INVOICES\\";
    public static final String LOGO_IMG = "C:\\AppResources\\Samurai\\Logo\\logoDuesse.jpg";
    public static final String ORDERS_COVERS_DIR = "C:\\AppResources\\Samurai\\ORDER_COVERS\\";
    public static final String DIGITAL_INVOICES_DIR = "C:\\AppResources\\Samurai\\DIG_INVOICES\\";
    public static final String DIGITAL_CREDIT_NOTES_DIR = "C:\\AppResources\\Samurai\\DIG_CREDIT_NOTES\\";
    public static final String QUOTES_DIR = RESOURCES_DIR+"\\QUOTES\\";
    public static final String QUOTES_ATTACH_DIR = RESOURCES_DIR+"\\QuoteAttachments\\";
    public static final String COMPLIANCE_CERTIFICATES_DIR = RESOURCES_DIR+"\\ComplianceCertificates\\";
    
    //This value is used in Data Access Obgect in order creation method. It has been used for give a
    //certain code number from wich begins to work with the aplication. The agrred number in June
    //2018 has been 30000. WARNING chenging this value involves all order code sequence corruption!!!!!
    public static final int ORDER_ID_CORRECTION = 20000;
    
    
    /********************* AMAZON WEB SERVER ADDRESS / LOCAL ADDRESS**************************/
    
    //public static final String SERVER_ADDRESS = "localhost";
    public static final String SERVER_ADDRESS = "jdbc:sqlserver://giugno2021.cuoogivijdct.eu-west-1.rds.amazonaws.com;databaseName=WorkLine;TrustServerCertificate=True;";
            //"jdbc:sqlserver://giugno2021.cuoogivijdct.eu-west-1.rds.amazonaws.com;databaseName=WorkLine;integratedSecurity=true;encrypt=true;trustServerCertificate=true;trustStore=storeName;trustStorePassword=storePassword";
    // inserts automatically the credentials : paolo paolo
    public static boolean DEBUG = false;
}
