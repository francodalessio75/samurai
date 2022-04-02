
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dalessio.samurai;

import com.dps.dbi.DbResult;
import com.dps.diginvoice.xml.XmlHelper.XmlNodeException;
import com.dps.utils.json.JSON;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.Part;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

/**
 *
 * @author Franco
 */
@MultipartConfig(fileSizeThreshold = 1024*1024, maxFileSize = 1024*1024*100, maxRequestSize = 1024*1024*10*10)
public class Gate extends HttpServlet implements HttpSessionListener {

    
    //English date format
    public static DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    //European date format
    public static DateTimeFormatter DTFE = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    //Directories name
    
    public static String bodyString(HttpServletRequest httpRequest)
    {
        try
        {
            final StringJoiner bodyString = new StringJoiner("\n");
            httpRequest.getReader().lines().forEach(line->bodyString.add(line));
            return bodyString.toString();
        }
        catch(IOException ex)
        {
            throw new JsonSyntaxException(ex);
        }
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        
        //reads the operation name
        String operation = request.getParameter("op");
        
        System.out.println(new Date()+" - RECEIVED OPERATION "+operation);
        
        //instantiates a Json object to put answer in
        JsonObject jsonResponse = new JsonObject();
        try
        {
            //cheks if there's an operation and if it isn't empty
            if( operation != null && !operation.isEmpty() )
            {
                switch( operation.toLowerCase() )
                {
                    case "ping": ping(request,jsonResponse); break;
                    
                    //AUTENTICATION
                    case "auth": authenticate(request,jsonResponse); break;
                    
                    //USERS
                    case "create_user" : createUser( request, jsonResponse ); break;
                    case "read_users" : readUsers( request, jsonResponse ); break;
                    case "update_user" : updateUser( request, jsonResponse ); break;
                    case "change_password" : changePassword( request, jsonResponse ); break;
                    case "unlock_user" : unlockUser( request, jsonResponse ); break;

                    //CUSTOMERS
                    case "create_customer" : createCustomer( request, jsonResponse ); break;
                    case "read_customers" : readCustomers( request, jsonResponse ); break;
                    case "update_customer" : updateCustomer( request, jsonResponse ); break;
                    case "upload_logo" : uploadLogo( request, jsonResponse ); break;
                    case "check_vatcode" : checkVatCode( request, jsonResponse ); break;
                    
                    //TRANSPORTERS
                    case "create_transporter" : createTransporter( request, jsonResponse ); break;
                    case "read_transporters" : readTransporters( request, jsonResponse ); break;
                    case "update_transporter" : updateTransporter( request, jsonResponse ); break;
                    
                    //ORDERS
                    case "create_order" : createOrder( request, jsonResponse ); break;
                    case "read_orders" : readOrders( request, jsonResponse ); break;
                    case "update_order" : updateOrder( request, jsonResponse ); break;
                    case "get_machinary_models_by_customer_id" : getMachinaryModelsByCustomerId( request, jsonResponse ); break;
                    case "get_validity_machinary_model_by_order_code" : getValidityMachinaryModelByOrderCode( request, jsonResponse ); break;
                    case "get_suggested_orders" : getSuggestedOrders( request, jsonResponse ); break;
                    case "get_valid_code" : getValidCode( request, jsonResponse ); break;
                    case "adjurn_cover" : adjurnCover( request, jsonResponse ); break;
                    
                    //TASKS
                    case "create_task" : createTask( request, jsonResponse ); break;
                    case "read_tasks" : readTasks( request, jsonResponse ); break;
                    case "update_task" : updateTask( request, jsonResponse ); break;
                    case "delete_task" : deleteTask( request, jsonResponse ); break;
                    case "create_task_attachment" : createTaskAttachment( request, jsonResponse ); break;
                    case "read_task_attachments" : readTaskAttachments( request, jsonResponse ); break;
                    case "delete_null_task_attachments" : deleteNullTaskAttachments( request, jsonResponse ); break;
                    case "delete_task_attachment" : deleteTaskAttachments( request, jsonResponse ); break;
                    
                    //JOB TYPES
                    case "create_jobtype" : createJobType( request, jsonResponse ); break;
                    case "read_jobtypes" : readJobTypes(request, jsonResponse ); break;
                    case "update_jobtype" : updateJobType( request, jsonResponse ); break;
                    
                    //TRANSLATION CENTERS
                    case "create_translations_center" : createTranslationCenter( request, jsonResponse ); break;
                    case "read_translations_centers" : readTranslationsCenters(request, jsonResponse ); break;
                    case "update_translations_center" : updateTranslationsCenter(request, jsonResponse ); break;
                    
                    //JOB SUBTYPES
                    case "create_jobsubtype" : createJobSubtype( request, jsonResponse ); break;
                    case "read_jobsubtype" : readJobSubtypes(request, jsonResponse ); break;
                    case "update_jobsubtype" : updateJobSubtype( request, jsonResponse ); break;
                    
                    //DELIVERY NOTES
                    case "create_delivery_note" : createDeliveryNote( request, jsonResponse ); break;
                    case "read_delivery_notes" : readDeliveryNotes( request, jsonResponse ); break;
                    case "read_delivery_note_rows" : readDeliveryNoteRows( request, jsonResponse ); break;
                    case "update_delivery_note" : updateDeliveryNote( request, jsonResponse ); break;
                    case "print_delivery_note" : printDeliveryNote( request, jsonResponse ); break;
                    case "create_all_new_pdf_files" : createAllNewPdfFiles( request, jsonResponse ); break;
                    
                    //INVOICES
                    case "get_suggested_delivery_notes_rows" : getSuggestedDeliveryNotesRows( request, jsonResponse ); break;
                    case "create_invoice" : createInvoice( request, jsonResponse ); break;
                    case "read_invoices" : readInvoices( request, jsonResponse ); break;
                    case "read_invoice_rows" : readInvoiceRows( request, jsonResponse ); break;
                    case "update_invoice" : updateInvoice( request, jsonResponse ); break;
                    case "print_invoice" : printInvoice( request, jsonResponse ); break;
                    case "get_xml" : getXML( request, jsonResponse ); break;
                    case "update_invoice_date" : updateInvoiceDate( request, jsonResponse ); break;
                    
                    //QUOTES
                    case "create_quote" : createQuote( request, jsonResponse ); break;
                    case "read_quotes" : readQuotes( request, jsonResponse ); break;
                    case "read_quote_rows" : readQuoteRows( request, jsonResponse ); break;
                    case "update_quote" : updateQuote( request, jsonResponse ); break;
                    case "create_quote_attachment" : createQuoteAttachment( request, jsonResponse ); break;
                    case "read_quote_attachments" : readQuoteAttachments( request, jsonResponse ); break;
                    case "delete_quote_attachment" : deleteQuoteAttachment( request, jsonResponse ); break;
                    case "print_quote" : printQuote( request, jsonResponse ); break;
                    
                    //COMPLIANCE CERTIFICATE
                    case "create_certificate" : createCertificate( request, jsonResponse ); break;
                    case "read_certificate" : readCertificate( request, jsonResponse ); break;
                    case "update_certificate" : updateCertificate( request, jsonResponse ); break;
                    
                    //CREDIT NOTES
                    case "create_credit_note" : createCreditNote( request, jsonResponse ); break;
                    case "read_credit_notes" : readCreditNotes( request, jsonResponse ); break;
                    case "read_credit_note_rows" : readCreditNoteRows( request, jsonResponse ); break;
                    case "update_credit_note" : updateCreditNote( request, jsonResponse ); break;
                    case "print_credit_note" : printCreditNote( request, jsonResponse ); break;
                    case "get_credit_note_xml" : getCreditNoteXML( request, jsonResponse ); break;
                    
                    //Amount Schedule Dates
                    case "fill_amount_schedule_dates" : fillAmountScheduleDates( request, jsonResponse ); break;
                    case "read_amount_schedule_dates" : readAmountScheduleDates( request, jsonResponse ); break;
                    
                    default:
                    {
                        jsonResponse.addProperty("success",false);
                        jsonResponse.addProperty("message","unknown operation : "+operation);
                    }
                }
            }
            else
            {
                jsonResponse.addProperty("success",false);
                jsonResponse.addProperty("message","null or empty operation");
            }
        }
        catch(Exception ex)
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","server exception : "+ex.toString());
        }
        
        response.addHeader("Access-Control-Allow-Origin","*");
        response.setContentType("text/html;charset=UTF-8");
        
        try (PrintWriter out = response.getWriter())
        {
            out.println(jsonResponse.toString());
            System.out.println(new Date()+" - RESPONSE SENT");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs 
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private void ping(HttpServletRequest request, JsonObject jsonResponse)
    {
        jsonResponse.addProperty( "success", true );
        System.out.println("ping");
    }
    
    //AUTENTICATION
    private void authenticate(HttpServletRequest request, JsonObject jsonResponse) throws SQLException, ClassNotFoundException
    {   
        //variables stored as session properties
        DataAccessObject dao = new DataAccessObject();
        Long customer_id = null;
        //gets parameters
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        Boolean active = false;
       
        //calls dao to ask the database about credentials existency
        Long user_id = dao.authenticate( username, password,request.getSession().getId());
        if( user_id != null)
            active = dao.userActive(user_id);
        
        // INF IN DEBUG MODE IGNORE USER ALREADY LOGGED IN
        if(Config.DEBUG && user_id <0L) user_id = -user_id;
        
        //if the user exhists , is active and nobody is using same credentils
        if( user_id > 0L  && active )
        {
            //add the success property success to the Json response and give it the "true" value
            jsonResponse.addProperty( "success", true );
            //add the property user_id and gives it the Long coming from the dao
            jsonResponse.addProperty( "user_id", user_id );  
            //gets the session object from the requast
            HttpSession session = request.getSession();
            //creates a Json property having as value the id of the request session
            jsonResponse.addProperty( "session_id", session.getId() );
            //sets to the session an attribute and assigns to it the current dao instance
            session.setAttribute( "dao", dao );
            //sets to the session an user_id attribute and assogns to it the Long coming from the dao
            session.setAttribute( "user_id", user_id );
            //this properties will be used by customer_details section
            session.setAttribute( "customer_id", customer_id );
            /**
             * now in the Json response we have the session id that allows to get the sesion object. 
             * furthermore we have the user_id so whenever we want, we can verify if the current session 
             * has been created by the right user. If the session hasn't got the user_id attribute that means 
             * the user is not authenticated.
             */
        }
        //if the user exhists but somebody else proves to be logged with same credentials 
        else if( user_id > 0L  && !active )
        {
            jsonResponse.addProperty( "success", false );
            jsonResponse.addProperty( "message", "Utente non Attivo" );
            if(request.getSession(false)!=null) request.getSession().invalidate();
        }
        //if the user is active but his credentials 
        else if( user_id < 0L  && active )
        {
            jsonResponse.addProperty( "success", false );
            jsonResponse.addProperty( "message", "Accesso non riuscito, l'Utente risulta già collegato." );
            if(request.getSession(false)!=null) request.getSession().invalidate();
        }
        else
        {
            jsonResponse.addProperty( "success", false );
            jsonResponse.addProperty( "message", "Credenziali non riconosciute." );
            if(request.getSession(false)!=null) request.getSession().invalidate();
        }
    }
    
    /**************************** USERS ****************************************/
    
    private void createUser( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
       /*ask for a session, if it doesn't exist, it won't be created. 
        Note that this mechanism of session checking avoids malware attempts
        to enter the system without executing log-in procedure*/
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // reads operation parameters
            String firstName = request.getParameter( "firstName" );
            String lastName = request.getParameter( "lastName" );
            String username = request.getParameter( "username" );
            String password = request.getParameter( "password" );
            
            // creates the user
            Long user_id = dao.createUser( firstName, lastName, username, password );
            
            jsonResponse.addProperty("success",true);
            jsonResponse.addProperty( "user_id", user_id );
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    private void readUsers( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        /*ask for a session, if it doesn't exist, it won't be created. 
        Note that this mechanism of session checking avoids malware attempts
        to enter the system without executing log-in procedure*/
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // reads operation parameters
            Long user_id = null;
            try{ user_id = Long.parseLong( request.getParameter( "user_id" ) );}
            catch( NumberFormatException Nex){}
            
            String username = request.getParameter( "username" );
            
            String password = request.getParameter( "password" );
            
            String rolesHint = request.getParameter( "roles" );
            
            Integer active = null;
            try{ active = Integer.parseInt( request.getParameter( "active" ) );}
            catch( NumberFormatException Nex){}
            
            String fiscalCodeHint = request.getParameter( "fiscalCodeHint" );
            
            String firstNameHint = request.getParameter( "firstNameHint" );
            
            String lastNameHint = request.getParameter( "lastNameHint" );
            
            String phoneNumberHint = request.getParameter( "phoneNumberHint" );
            
            String cellNumberHint = request.getParameter( "cellNumberHint" );
            
            String emailHint = request.getParameter( "emailHint" );
            
            String notesHint = request.getParameter( "notesHint" );
            
            // asks DB for customers
            DbResult dbr = dao.readUsers( user_id, username, password, rolesHint, active, fiscalCodeHint, firstNameHint, lastNameHint, phoneNumberHint, cellNumberHint, emailHint, notesHint );
            
            jsonResponse.addProperty("success",true);
            
            JsonArray users = new JsonArray();
            
            for(int i=0; i<dbr.rowsCount(); i++)
            {
                JsonArray record = new JsonArray();
                record.add( new JsonPrimitive( dbr.getLong( i, "user_id" )));
                
                if( dbr.getString( i, "username" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "username" )));
                
                if( dbr.getString( i, "password" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "password" )));
                
                if( dbr.getString( i, "role" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "role" )));
                
                if( dbr.getBoolean( i, "active" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getBoolean( i, "active" )));
                
                if( dbr.getString( i, "fiscalCode" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "fiscalCode" )));
                
                if( dbr.getString( i, "firstName" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "firstName" )));
                
                if( dbr.getString( i, "lastName" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "lastName" )));
                
                if( dbr.getString( i, "phoneNumber" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "phoneNumber" )));
                
                if( dbr.getString( i, "cellNumber" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "cellNumber" )));
                
                if( dbr.getString( i, "email" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "email" )));
                
                if( dbr.getString( i, "notes" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "notes" )));
                
                users.add(record);
            }
            
            jsonResponse.add("users", users);
            
            jsonResponse.addProperty("success",true);
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    private void updateUser( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute( "user_id" ) != null )
        {
            // READS SESSION DATA
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // READS OPERATION PARAMETERS
            Long user_id = null;
            try{ user_id = Long.parseLong( request.getParameter( "user_id" ) );}
            catch( NumberFormatException Nex){ jsonResponse.addProperty("message","invalid user_id format"); }
            
            String username = request.getParameter( "username" );
            
            String password = request.getParameter( "password" );
            
            Double hourlyCost = null;
            try{ hourlyCost = Double.parseDouble( request.getParameter( "hourlyCost" ) );}
            catch( RuntimeException Nex){}
            
            String roles = request.getParameter( "roles" );
            
            String active = request.getParameter( "active" );
            
            String fiscalCode = request.getParameter( "fiscalCode" );
            
            String firstName = request.getParameter( "firstName" );
            
            String lastName = request.getParameter( "lastName" );
            
            String phoneNumber = request.getParameter( "phoneNumber" );
            
            String cellNumber = request.getParameter( "cellNumber" );
            
            String email = request.getParameter( "email" );
            
            String notes = request.getParameter( "notes" );
            
            // UPDATES THE RECORD
            boolean success = dao.updateUser( user_id, username, password, hourlyCost, roles, active, fiscalCode, firstName, lastName, phoneNumber, cellNumber, email, notes );
            
            jsonResponse.addProperty("success",success);
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    /**
     * This action can be is done by all users each one for his own credentials.
     * Without holding current valid credentials the  action is not allowed
     * @param request
     * @param jsonResponse
     * @throws SQLException 
     */
    private void changePassword( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute( "user_id" ) != null )
        {
            // READS SESSION DATA
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // READS OPERATION PARAMETERS
            Long user_id = null;
            try{ user_id = Long.parseLong( session.getAttribute( "user_id" ).toString() );}
            catch( NumberFormatException Nex){ jsonResponse.addProperty("message","invalid user_id format"); }
            
            if( user_id != null)
            {
                String oldUsername = request.getParameter( "oldUsername" );
                String oldPassword = request.getParameter( "oldPassword" );
                String newPassword = request.getParameter( "newPassword" );
                
                // UPDATES THE RECORD
                boolean success = dao.changePassword( user_id, oldUsername, oldPassword, newPassword );

                jsonResponse.addProperty("success",success);
            }
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    /**************************  CUSTOMERS  ************************************/
    
    private void createCustomer( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        /*ask for a session, if it doesn't exist, it won't be created. 
        Note that this mechanism of session checking avoids malware attempts
        to enter the system without executing log-in procedure*/
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // reads operation parameters
            String denomination = request.getParameter( "denomination" );
            String vatCode = request.getParameter( "vatCode" );
            
            // creates the customer
            Long customer_id = dao.createCustomer( denomination, vatCode );
            
            jsonResponse.addProperty("success",true);
            jsonResponse.addProperty( "customer_id", customer_id );
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    private void readCustomers( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        /*ask for a session, if it doesn't exist, it won't be created. 
        Note that this mechanism of session checking avoids malware attempts
        to enter the system without executing log-in procedure*/
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // reads operation parameters
            Long customer_id = null;
            try{ customer_id = Long.parseLong( request.getParameter( "customer_id" ) );}
            catch( NumberFormatException Nex){}
            String vatCodeHint = request.getParameter( "vatCodeHint" );
            String fiscalCodeHint = request.getParameter( "fiscalCodeHint" );
            String denominationHint = request.getParameter( "denominationHint" );
            String phoneNumberHint = request.getParameter( "phoneNumberHint" );
            String faxNumberHint = request.getParameter( "faxNumberHint" );
            String cellNumberHint = request.getParameter( "cellNumberHint" );
            String emailHint = request.getParameter( "emailHint" );
            String cityHint = request.getParameter( "cityHint" );
            String addressHint = request.getParameter( "addressHint" );
            String houseNumberHint = request.getParameter( "houseNumberHint" );
            String postalCodeHint = request.getParameter( "postalCodeHint" );
            String provinceHint = request.getParameter( "provinceHint" );
            String notesHint = request.getParameter( "notesHint" );
            
            // asks DB for customers
            DbResult dbr = dao.readCustomers( customer_id, vatCodeHint, fiscalCodeHint, denominationHint, phoneNumberHint, faxNumberHint, cellNumberHint, emailHint, cityHint, addressHint, houseNumberHint, postalCodeHint, provinceHint, notesHint );
            
            jsonResponse.addProperty("success",true);
            
            JsonArray customers = new JsonArray();
            
            for(int i=0; i<dbr.rowsCount(); i++)
            {
                JsonArray record = new JsonArray();
                //0
                record.add( new JsonPrimitive( dbr.getLong( i, "customer_id" )));
                //1
                if( dbr.getString( i, "vatCode" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "vatCode" )));
                //2
                if( dbr.getString( i, "fiscalCode" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "fiscalCode" )));
                //3
                if( dbr.getString( i, "denomination" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "denomination" )));
                //4
                if( dbr.getString( i, "phoneNumber" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "phoneNumber" )));
                //5
                if( dbr.getString( i, "faxNumber" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "faxNumber" )));
                //6
                if( dbr.getString( i, "cellNumber" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "cellNumber" )));
                //7
                if( dbr.getString( i, "email" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "email" )));
                //8
                if( dbr.getString( i, "city" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "city" )));
                //9
                if( dbr.getString( i, "address" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "address" )));
                //10
                if( dbr.getString( i, "houseNumber" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "houseNumber" )));
                //11
                if( dbr.getString( i, "postalCode" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "postalCode" )));
                //12
                if( dbr.getString( i, "province" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "province" )));
                //13
                if( dbr.getString( i, "country" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "country" )));
                //14
                if( dbr.getString( i, "logo" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "logo" )));
                //15
                if( dbr.getString( i, "paymentConditions" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "paymentConditions" )));
                //16
                if( dbr.getString( i, "bank" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "bank" )));
                //17
                if( dbr.getString( i, "CAB" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "CAB" )));
                //18
                if( dbr.getString( i, "ABI" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "ABI" )));
                //19
                if( dbr.getString( i, "IBAN" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "IBAN" )));
                //20
                if( dbr.getString( i, "foreignIBAN" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "foreignIBAN" )));
                //21
                if( dbr.getString( i, "notes" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "notes" )));
                //22
                if( dbr.getString( i, "VATExemptionText" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "VATExemptionText" )));
                //23
                if( dbr.getString( i, "exemptionProtocol" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "exemptionProtocol" )));
                //24
                if( dbr.getString( i, "exemptionDate" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "exemptionDate" )));

                customers.add(record);
            }
            
            jsonResponse.add("customers", customers);
            
            jsonResponse.addProperty("success",true);
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    private void updateCustomer( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute( "user_id" ) != null )
        {
            // READS SESSION DATA
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // READS OPERATION PARAMETERS
            Long customer_id = null;
            try{ customer_id = Long.parseLong( request.getParameter( "customer_id" ) );}
            catch( NumberFormatException Nex){ jsonResponse.addProperty("message","invalid customer_id format"); }
            String vatCode = request.getParameter( "vatCode" );
            String fiscalCode = request.getParameter( "fiscalCode" );
            String denomination = request.getParameter( "denomination" );
            String phoneNumber = request.getParameter( "phoneNumber" );
            String faxNumber = request.getParameter( "faxNumber" );
            String cellNumber = request.getParameter( "cellNumber" );
            String email = request.getParameter( "email" );
            String city = request.getParameter( "city" );
            String address = request.getParameter( "address" );
            String houseNumber = request.getParameter( "houseNumber" );
            String postalCode = request.getParameter( "postalCode" );
            String province = request.getParameter( "province" );
            String country = request.getParameter( "country" );
            String logo = request.getParameter( "logo" );
            String paymentConditions = request.getParameter( "paymentConditions" );
            String bank = request.getParameter( "bank" );
            String CAB = request.getParameter( "CAB" );
            String ABI = request.getParameter( "ABI" );
            String IBAN = request.getParameter( "IBAN" );
            String foreignIBAN = request.getParameter( "foreignIBAN" );
            String notes = request.getParameter( "notes" );
            String VATExemptionText = request.getParameter( "VATExemptionText" );
            String univocalCode = request.getParameter( "univocalCode" );
            String pec = request.getParameter( "pec" );
            String modalitaPagamento = request.getParameter( "modalitaPagamento" );
            String vatExemptionProtocol = request.getParameter( "vatExemptionProtocol" );
            String vatExemptionDate = request.getParameter( "vatExemptionDate" );
            
            //calls the db
            boolean success = dao.updateCustomer( 
                customer_id, 
                vatCode, 
                fiscalCode, 
                denomination, 
                phoneNumber, 
                faxNumber, 
                cellNumber, 
                email, 
                city, 
                address, 
                houseNumber, 
                postalCode, 
                province, 
                country, 
                logo, 
                paymentConditions,  
                bank, 
                CAB, 
                ABI, 
                IBAN, 
                foreignIBAN, 
                notes, 
                VATExemptionText, 
                univocalCode, 
                pec, 
                modalitaPagamento,
                vatExemptionProtocol,
                vatExemptionDate);
            
            jsonResponse.addProperty("success",success);
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    private void uploadLogo( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute( "user_id" ) != null )
        {
            String fileName = request.getParameter("fileName");
            Integer fileSize = Integer.parseInt(request.getParameter("fileSize"));
            Long customer_id = Long.parseLong(request.getParameter("customer_id"));
            String dataBase64 = request.getParameter("file");

            HttpUploader httpUploader = new HttpUploader(Config.CUSTOMERS_LOGO_DIR);

            try
            {
                String localFileName = customer_id+"_"+fileName;
                
                httpUploader.upload(localFileName,fileSize,dataBase64);

                DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");

                dao.setCustomerLogo(customer_id,localFileName);
                
                dao.adjurnCustomerLogoByCustomerId(customer_id, Boolean.FALSE);
                
                jsonResponse.addProperty("success",true);
            }
            catch( IOException | SQLException ex)
            {
                jsonResponse.addProperty("success",false);
                jsonResponse.addProperty("message","EXCEPTION : "+ex);
            }
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    private void checkVatCode( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute( "user_id" ) != null )
        {
             // READS SESSION DATA
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // READS OPERATION PARAMETERS
            String vatCode = request.getParameter( "vatCode" );
            
            // asks DB for customers
            DbResult dbr = dao.readCustomers( null, vatCode, null, null, null, null, null, null, null, null, null, null, null, null );
            
            JsonArray denominations = new JsonArray();
            
            for(int i=0; i<dbr.rowsCount(); i++)
            {
                JsonArray record = new JsonArray();
                //0
                record.add( new JsonPrimitive( dbr.getString( i, "denomination" )));
                
                denominations.add(record);
            }
              
            jsonResponse.add("denominations", denominations);
            
            jsonResponse.addProperty("success",true);
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    
    /****************** JOB TYPES ********************************************/
    
    private void createJobType( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        /*ask for a session, if it doesn't exist, it won't be created. 
        Note that this mechanism of session checking avoids malware attempts
        to enter the system without executing log-in procedure*/
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // reads operation parameters
            String name = request.getParameter("name");
            String description = request.getParameter("description");
            
            // creates the job subtype
            Long jobType_id = dao.createJobType( name, description );
            
            jsonResponse.addProperty("success",true);
            jsonResponse.addProperty( "jobType_id", jobType_id );
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }  
    
    private void readJobTypes( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        /*ask for a session, if it doesn't exist, it won't be created. 
        Note that this mechanism of session checking avoids malware attempts
        to enter the system without executing log-in procedure*/
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // reads operation parameters
            Long jobType_id = null;
            try{ jobType_id = Long.parseLong( request.getParameter( "jobType_id" ) );}
            catch( NumberFormatException Nex){}
            
            // asks DB for customers
            DbResult dbr = dao.readJobTypes( jobType_id );
            
            jsonResponse.addProperty("success",true);
            
            JsonArray jobTypes = new JsonArray();
            
            for(int i=0; i<dbr.rowsCount(); i++)
            {
                JsonArray record = new JsonArray();
                //0
                record.add( new JsonPrimitive( dbr.getLong( i, "jobType_id" )));
                //1
                if( dbr.getString( i, "name" ) == null )
                    record.add( new JsonPrimitive( "" ));
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "name" )));
                //2
                if( dbr.getString( i, "description" ) == null )
                    record.add( new JsonPrimitive( "" ));
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "description" )));
                
                jobTypes.add(record);
            }
            
            jsonResponse.add("jobTypes", jobTypes);
            
            jsonResponse.addProperty("success",true);
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    private void updateJobType( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute( "user_id" ) != null )
        {
            // READS SESSION DATA
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // READS OPERATION PARAMETERS
            Long jobType_id = null;
            try{ jobType_id = Long.parseLong( request.getParameter( "jobType_id" ) );}
            catch( NumberFormatException Nex){ jsonResponse.addProperty("message","invalid jobType_id format"); }
            String name = request.getParameter( "name" );
            String description = request.getParameter( "description" );

            
            // UPDATES THE RECORD
            boolean success = dao.updateJobType( jobType_id, name, description );
            
            jsonResponse.addProperty("success",success);
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    /****************************  JOB SUBTYPES  ******************************/
    
    private void createJobSubtype( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        /*ask for a session, if it doesn't exist, it won't be created. 
        Note that this mechanism of session checking avoids malware attempts
        to enter the system without executing log-in procedure*/
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // reads operation parameters
            String name = request.getParameter("name");
            String description = request.getParameter("description");
            
            // creates the job subtype
            Long jobSubtype_id = dao.createJobSubtype( name, description );
            
            jsonResponse.addProperty("success",true);
            jsonResponse.addProperty( "jobSubtype_id", jobSubtype_id );
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    private void readJobSubtypes( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        /*ask for a session, if it doesn't exist, it won't be created. 
        Note that this mechanism of session checking avoids malware attempts
        to enter the system without executing log-in procedure*/
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // reads operation parameters
            Long jobSubtype_id = null;
            try{ jobSubtype_id = Long.parseLong( request.getParameter( "jobSubtype_id" ) );}
            catch( NumberFormatException Nex){}
            
            // asks DB for customers
            DbResult dbr = dao.readJobSubtypes( jobSubtype_id );
            
            jsonResponse.addProperty("success",true);
            
            JsonArray jobSubtypes = new JsonArray();
            
            for(int i=0; i<dbr.rowsCount(); i++)
            {
                JsonArray record = new JsonArray();
                //0
                record.add( new JsonPrimitive( dbr.getLong( i, "jobSubtype_id" )));
                //1
                if( dbr.getString( i, "name" ) == null )
                    record.add( new JsonPrimitive( "" ));
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "name" )));
                //2
                if( dbr.getString( i, "description" ) == null )
                    record.add( new JsonPrimitive( "" ));
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "description" )));
                
                jobSubtypes.add(record);
            }
            
            jsonResponse.add("jobSubtypes", jobSubtypes);
            
            jsonResponse.addProperty("success",true);
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    private void updateJobSubtype( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute( "user_id" ) != null )
        {
            // READS SESSION DATA
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // READS OPERATION PARAMETERS
            Long jobSubtype_id = null;
            try{ jobSubtype_id = Long.parseLong( request.getParameter( "jobSubtype_id" ) );}
            catch( NumberFormatException Nex){ jsonResponse.addProperty("message","invalid jobSubtype_id format"); }
            String name = request.getParameter( "name" );
            String description = request.getParameter( "description" );

            
            // UPDATES THE RECORD
            boolean success = dao.updateJobSubtype( jobSubtype_id, name, description );
            
            jsonResponse.addProperty("success",success);
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    /******************  TRANSLATION CENTERS **********************************/
    
    private void createTranslationCenter( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        /*ask for a session, if it doesn't exist, it won't be created. 
        Note that this mechanism of session checking avoids malware attempts
        to enter the system without executing log-in procedure*/
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // reads operation parameters
            String denomination = request.getParameter("denomination");
            //String phoneNumber = request.getParameter("phoneNumber");
            //String notes = request.getParameter("notes");
            
            // creates the job subtype
            Long translations_center_id = dao.createTranslationCenter( denomination );
            
            jsonResponse.addProperty("success",true);
            jsonResponse.addProperty( "translations_center_id", translations_center_id );
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }  
    
    private void readTranslationsCenters( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
       /*ask for a session, if it doesn't exist, it won't be created. 
        Note that this mechanism of session checking avoids malware attempts
        to enter the system without executing log-in procedure*/
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // reads operation parameters
            Long translations_center_id = null;
            try{ translations_center_id = Long.parseLong( request.getParameter( "translations_center_id" ) );}
            catch( NumberFormatException Nex){}
            
            // asks DB for customers
            DbResult dbr = dao.readTranslationsCenters( translations_center_id );
            
            jsonResponse.addProperty("success",true);
            
            JsonArray translationsCenters = new JsonArray();
            
            for(int i=0; i<dbr.rowsCount(); i++)
            {
                JsonArray record = new JsonArray();
                //0
                record.add( new JsonPrimitive( dbr.getLong( i, "translations_center_id" )));
                //1
                if( dbr.getString( i, "denomination" ) == null )
                    record.add( new JsonPrimitive( "" ));
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "denomination" )));
                //2
                if( dbr.getString( i, "notes" ) == null )
                    record.add( new JsonPrimitive( "" ));
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "notes" )));
                
                translationsCenters.add(record);
            }
            
            jsonResponse.add("translationsCenters", translationsCenters);
            
            jsonResponse.addProperty("success",true);
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
     private void updateTranslationsCenter( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute( "user_id" ) != null )
        {
            // READS SESSION DATA
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // READS OPERATION PARAMETERS
            Long translations_center_id = null;
            try{ translations_center_id = Long.parseLong( request.getParameter( "translations_center_id" ) );}
            catch( NumberFormatException Nex){ jsonResponse.addProperty("message","invalid translarions_center_id format"); }
            String denomination = request.getParameter( "denomination" );
            String notes = request.getParameter( "notes" );

            
            // UPDATES THE RECORD
            boolean success = dao.updateTranslationsCenter( translations_center_id, denomination, notes );
            
            jsonResponse.addProperty("success",success);
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
     //TRANSPORTERS
    
    private void createTransporter( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        /*ask for a session, if it doesn't exist, it won't be created. 
        Note that this mechanism of session checking avoids malware attempts
        to enter the system without executing log-in procedure*/
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // reads operation parameters
            String denomination = request.getParameter( "denomination" );
            String fiscalCode = request.getParameter( "fiscalCode" );
            
            // creates the customer
            Long transporter_id = dao.createTransporter( denomination, fiscalCode );
            
            jsonResponse.addProperty("success",true);
            jsonResponse.addProperty( "transporter_id", transporter_id );
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    private void readTransporters( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        /*ask for a session, if it doesn't exist, it won't be created. 
        Note that this mechanism of session checking avoids malware attempts
        to enter the system without executing log-in procedure*/
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // reads operation parameters
            Long transporter_id = null;
            try{ transporter_id = Long.parseLong( request.getParameter( "transporter_id" ) );}
            catch( NumberFormatException Nex){}
            String vatCodeHint = request.getParameter( "vatCodeHint" );
            String fiscalCodeHint = request.getParameter( "fiscalCodeHint" );
            String denominationHint = request.getParameter( "denominationHint" );
            String phoneNumberHint = request.getParameter( "phoneNumberHint" );
            String faxNumberHint = request.getParameter( "faxNumberHint" );
            String cellNumberHint = request.getParameter( "cellNumberHint" );
            String emailHint = request.getParameter( "emailHint" );
            String cityHint = request.getParameter( "cityHint" );
            String addressHint = request.getParameter( "addressHint" );
            String houseNumberHint = request.getParameter( "houseNumberHint" );
            String postalCodeHint = request.getParameter( "postalCodeHint" );
            String provinceHint = request.getParameter( "provinceHint" );
            String notesHint = request.getParameter( "notesHint" );
            
            // asks DB for customers
            DbResult dbr = dao.readTransporters( transporter_id, vatCodeHint, fiscalCodeHint, denominationHint, phoneNumberHint, faxNumberHint, cellNumberHint, emailHint, cityHint, addressHint, houseNumberHint, postalCodeHint, provinceHint, notesHint );
            
            jsonResponse.addProperty("success",true);
            
            JsonArray transporters = new JsonArray();
            
            for(int i=0; i<dbr.rowsCount(); i++)
            {
                JsonArray record = new JsonArray();
                //0
                record.add( new JsonPrimitive( dbr.getLong( i, "transporter_id" )));
                //1
                if( dbr.getString( i, "vatCode" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "vatCode" )));
                //2
                if( dbr.getString( i, "fiscalCode" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "fiscalCode" )));
                //3
                if( dbr.getString( i, "denomination" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "denomination" )));
                //4
                if( dbr.getString( i, "phoneNumber" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "phoneNumber" )));
                //5
                if( dbr.getString( i, "faxNumber" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "faxNumber" )));
                //6
                if( dbr.getString( i, "cellNumber" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "cellNumber" )));
                //7
                if( dbr.getString( i, "email" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "email" )));
                //8
                if( dbr.getString( i, "city" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "city" )));
                //9
                if( dbr.getString( i, "address" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "address" )));
                //10
                if( dbr.getString( i, "houseNumber" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "houseNumber" )));
                //11
                if( dbr.getString( i, "postalCode" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "postalCode" )));
                //12
                if( dbr.getString( i, "province" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "province" )));
                //13
                if( dbr.getString( i, "notes" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "notes" )));

                transporters.add(record);
            }
            
            jsonResponse.add("transporters", transporters);
            
            jsonResponse.addProperty("success",true);
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    private void updateTransporter( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute( "user_id" ) != null )
        {
            // READS SESSION DATA
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // READS OPERATION PARAMETERS
            Long transporter_id = null;
            try{ transporter_id = Long.parseLong( request.getParameter( "transporter_id" ) );}
            catch( NumberFormatException Nex){ jsonResponse.addProperty("message","invalid transporter_id format"); }
            String vatCode = request.getParameter( "vatCode" );
            String fiscalCode = request.getParameter( "fiscalCode" );
            String denomination = request.getParameter( "denomination" );
            String phoneNumber = request.getParameter( "phoneNumber" );
            String faxNumber = request.getParameter( "faxNumber" );
            String cellNumber = request.getParameter( "cellNumber" );
            String email = request.getParameter( "email" );
            String city = request.getParameter( "city" );
            String address = request.getParameter( "address" );
            String houseNumber = request.getParameter( "houseNumber" );
            String postalCode = request.getParameter( "postalCode" );
            String province = request.getParameter( "province" );
            String notes = request.getParameter( "notes" );
            
            // UPDATES THE RECORD
            boolean success = dao.updateTransporter( transporter_id, vatCode, fiscalCode, denomination, phoneNumber, faxNumber, cellNumber, email, city, address, houseNumber, postalCode, province, notes );
            
            jsonResponse.addProperty("success",success);
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    //ORDERS
    //Long customer_id, Long user_id, String jobType, String date, String machinaryModel, String notes
    private void createOrder( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        /*ask for a session, if it doesn't exist, it won't be created. 
        Note that this mechanism of session checking avoids malware attempts
        to enter the system without executing log-in procedure*/
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // reads operation parameters
            Long customer_id = null;
            try{ customer_id = Long.parseLong( request.getParameter( "customer_id" ) );}
            catch( NumberFormatException Nex){ jsonResponse.addProperty("message","invalid customer_id format"); }
            
            Long user_id = null;
            try{ user_id = Long.parseLong( request.getParameter( "user_id" ) );}
            catch( NumberFormatException Nex){ jsonResponse.addProperty("message","invalid user_id format"); }
            
            Long jobType_id = null;
            try{ jobType_id = Long.parseLong( request.getParameter( "jobType_id" ) );}
            catch( NumberFormatException Nex){ jsonResponse.addProperty("message","invalid jobType_id format"); }
            
            //Hard coded here but it should be a default value on DB. When I send to AWS I loose this default setting!!!!!!!!!!!!!!!!!!!!!
            //Long completion_state_id = 1L;//"In Corso"
            
            String date = request.getParameter( "date" );
            
            String machinaryModel = request.getParameter( "machinaryModel" );
            
            String notes = request.getParameter( "notes" );
            
            // creates the customer
            Long order_id = dao.createOrder( customer_id, user_id, jobType_id, date, machinaryModel, notes);
            
            jsonResponse.addProperty("success",true);
            jsonResponse.addProperty( "order_id", order_id );
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }    
    
   /**/
    private void readOrders( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        /*ask for a session, if it doesn't exist, it won't be created. 
        Note that this mechanism of session checking avoids malware attempts
        to enter the system without executing log-in procedure*/
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // reads operation parameters
            Long order_id = null;
            try{ order_id = Long.parseLong( request.getParameter( "order_id" ) );}
            catch( NumberFormatException Nex){}
            
            String order_code =  request.getParameter( "order_code" );
            
            String order_description =  request.getParameter( "order_description" );
            
            Long creator_id = null;
            try{ creator_id = Long.parseLong( request.getParameter( "creator_id" ) );}
            catch( NumberFormatException Nex){}
            
            Long completion_state_id = null;
            try{ completion_state_id = Long.parseLong( request.getParameter( "completion_state_id" ) );}
            catch( NumberFormatException Nex){}
            
            Long availability_id = null;
            try{ availability_id = Long.parseLong( request.getParameter( "availability_id" ) );}
            catch( NumberFormatException Nex){}
            
            String customer_idString = request.getParameter( "customer_idString" );
            
            String machinaryModelHint = request.getParameter( "machinaryModelHint" );
            
            String jobType_idString = request.getParameter( "jobType_idString" );
            
            String fromDate = request.getParameter( "fromDate" );
            String toDate = request.getParameter( "toDate" );
            
            // asks DB for orders
            //Long order_id, Long creator_id, String order_code, Long completion_state_id, String customerDenominationHint, String machinaryModelHint, String jobTypeHint, String fromDate, String toDate
            DbResult dbr = dao.readOrders( order_id, creator_id, order_code, order_description, completion_state_id, availability_id, customer_idString, machinaryModelHint, jobType_idString, fromDate, toDate );
            
            //JsonArray jsonRecords = (JsonArray)dbr.toJson(true).get("records");
            //jsonResponse.add("orders", jsonRecords);
            //jsonResponse.addProperty("success",true);
            //jsonResponse.add("orders",dbr.toJson(true));
            
            JsonArray orders = new JsonArray();
            
            for(int i=0; i<dbr.rowsCount(); i++)
            {
                JsonArray record = new JsonArray();
                //0
                record.add( new JsonPrimitive( dbr.getLong( i, "order_id" )));
                //1
                record.add( new JsonPrimitive( dbr.getLong( i, "customer_id" )));
                
                //2
                if( dbr.getString( i, "code" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "code" )));
                
                //3
                if( (dbr.getString(i, "date" )) == null )
                    record.add( new JsonPrimitive( "" ) );
                //changes the date format
                else
                {
                    try
                    {
                        //get a stringbuilder to insert dashes
                        StringBuilder dateSB = new StringBuilder(dbr.getString(i, "date" ));
                        dateSB.insert(4,"-");
                        dateSB.insert(7,"-");
                        record.add( new JsonPrimitive( DTFE.format(DTF.parse(dateSB.toString()))));
                    }
                    catch(Exception ex)
                    {
                        record.add( new JsonPrimitive( dbr.getString(i, "date" )));
                    }
                    
                }

                //4
                if( dbr.getString( i, "customerDenomination" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "customerDenomination" )));
                
                //5
                if( dbr.getString( i, "creatorFirstName" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "creatorFirstName" )));
                
                //6
                if( dbr.getString( i, "creatorLastName" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "creatorLastName" )));
                
                //7
                if( dbr.getLong( i, "jobType_id" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                {
                    //solves the name
                    //String jobType = dao.getJobTypeName(dbr.getLong( i, "jobType_id" ));
                    //record.add( new JsonPrimitive( jobType ));
                    record.add(new JsonPrimitive(dbr.getString(i,"jobTypeName")));
                }
                
                //8
                if( dbr.getLong( i, "completion_state_id" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getLong( i, "completion_state_id" )));
                
                //9
                if( dbr.getString( i, "notes" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "notes" )));
                
                //10
                if( dbr.getString( i, "storyData" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "storyData" )));
                
                //11
                if( dbr.getLong( i, "creator_id" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getLong( i, "creator_id" )));
                
                //12
                if( dbr.getString( i, "machinaryModel" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "machinaryModel" )));
                
                //13
                if( dbr.getString( i, "completionState" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "completionState" )));
                
                //14
                if( dbr.getString( i, "availability" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "availability" )));

                orders.add(record);
            }
            
            jsonResponse.add("orders", orders);
            jsonResponse.addProperty("success",true);
            
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    private void getMachinaryModelsByCustomerId( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        /*ask for a session, if it doesn't exist, it won't be created. 
        Note that this mechanism of session checking avoids malware attempts
        to enter the system without executing log-in procedure*/
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // reads operation parameters
            Long customer_id = null;
            try{ customer_id = Long.parseLong( request.getParameter( "customer_id" ) );}
            catch( NumberFormatException Nex){}
            
            // asks DB for customers
            DbResult dbr = dao.getMachinaryModelsByCustomerId( customer_id );
            
            jsonResponse.addProperty("success",true);
            
            JsonArray machinaryModels = new JsonArray();
            
            for(int i=0; i<dbr.rowsCount(); i++)
            {
                JsonArray record = new JsonArray();
                //0
                record.add( new JsonPrimitive( dbr.getString( i, "machinaryModel" )));
                
                machinaryModels.add(record);
            }
            
            jsonResponse.add("machinaryModels", machinaryModels);
            
            jsonResponse.addProperty("success",true);
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    private void getValidityMachinaryModelByOrderCode( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        /*ask for a session, if it doesn't exist, it won't be created. 
        Note that this mechanism of session checking avoids malware attempts
        to enter the system without executing log-in procedure*/
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // reads operation parameters
            String orderCode = request.getParameter( "orderCode" );
            
            Long customer_id = Long.parseLong(request.getParameter("customer_id"));
            
            // asks DB for messages
            String[] results  = dao.getValidityMachinaryModelByOrderCode( orderCode, customer_id  );
            
            jsonResponse.addProperty("success",true);
            
            JsonArray messages = new JsonArray();
            
            //pushes results in the array
            messages.add( new JsonPrimitive( results[0]));
            messages.add( new JsonPrimitive( results[1]));
            messages.add( new JsonPrimitive( results[2]));
            messages.add( new JsonPrimitive( results[3]));
            messages.add( new JsonPrimitive( results[4]));
            
            jsonResponse.add("messages", messages);
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
       
    private void getSuggestedOrders( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        /*ask for a session, if it doesn't exist, it won't be created. 
        Note that this mechanism of session checking avoids malware attempts
        to enter the system without executing log-in procedure*/
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // reads operation parameters
            Long customer_id = Long.parseLong( request.getParameter( "customer_id" )) ;
            
            // calls dao moethod
            DbResult dbr = dao.getSuggestedOrdersByCustomerId(customer_id);
            
            jsonResponse.addProperty("success",true);
            //jsonResponse.add("orders",dbr.toJson(true));
            
            JsonArray orders = new JsonArray();
            
            for(int i=0; i<dbr.rowsCount(); i++)
            {
                JsonArray record = new JsonArray();
                //0
                record.add( new JsonPrimitive( dbr.getLong( i, "order_id" )));
                //1
                record.add( new JsonPrimitive( dbr.getLong( i, "customer_id" )));
                
                //2
                if( dbr.getString( i, "code" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "code" )));
                
                //3
                if( (dbr.getString(i, "date" )) == null )
                    record.add( new JsonPrimitive( "" ) );
                //changes the date format
                else
                {
                    //get a stringbuilder to insert dashes
                    StringBuilder dateSB = new StringBuilder(dbr.getString(i, "date" ));
                    dateSB.insert(4,"-");
                    dateSB.insert(7,"-");
                    
                    record.add( new JsonPrimitive( DTFE.format(DTF.parse(dateSB.toString()))));
                }

                //4
                if( dbr.getString( i, "customerDenomination" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "customerDenomination" )));
                
                //5
                if( dbr.getString( i, "creatorFirstName" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "creatorFirstName" )));
                
                //6
                if( dbr.getString( i, "creatorLastName" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "creatorLastName" )));
                
                //7
                if( dbr.getLong( i, "jobType_id" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                {
                    //solves the name
                    String jobType = dao.getJobTypeName(dbr.getLong( i, "jobType_id" ));
                    record.add( new JsonPrimitive( jobType ));
                }
                
                //8
                if( dbr.getLong( i, "completion_state_id" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getLong( i, "completion_state_id" )));
                
                //9
                if( dbr.getString( i, "notes" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "notes" )));
                
                //10
                if( dbr.getString( i, "storyData" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "storyData" )));
                
                //11
                if( dbr.getLong( i, "creator_id" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getLong( i, "creator_id" )));
                
                //12
                if( dbr.getString( i, "machinaryModel" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "machinaryModel" )));
                
                //13
                if( dbr.getString( i, "completionState" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "completionState" )));
                
                //14
                if( dbr.getString( i, "availability" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "availability" )));

                orders.add(record);
            }
            
            jsonResponse.add("orders", orders);
            
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    public void getValidCode( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        /*ask for a session, if it doesn't exist, it won't be created. 
        Note that this mechanism of session checking avoids malware attempts
        to enter the system without executing log-in procedure*/
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // reads operation parameters
            Long customer_id = Long.parseLong( request.getParameter( "customer_id" )) ;
            
            String orderCode = request.getParameter( "orderCode" ) ;
            
            // calls dao moethod
            String message = dao.getValidCode( orderCode, customer_id);
            
            jsonResponse.addProperty("message", message);
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    
    /**
     * Checks in DB id has been inserted a new logo for the customer. If yes produces a new pdf file
     * of the order cover having the new logo
     * @param request
     * @param jsonResponse
     * @throws SQLException 
     * @throws java.io.IOException 
     * @throws java.lang.ClassNotFoundException 
     */
    public void adjurnCover( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException, com.itextpdf.io.IOException, java.io.IOException, ClassNotFoundException
    {
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            //the pdf maker
            PdfOrderCoverPrinter pdfCover = new PdfOrderCoverPrinter();
            
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // reads operation parameters
            Long order_id = Long.parseLong( request.getParameter( "order_id" )) ;
            
            // calls dao moethod
            boolean adjurned = dao.logoAdjurned( order_id );
            
            //if  the file not exists creates the pdf file and sets the logoAdjurned value to 1
            if( ! new File(Config.ORDERS_COVERS_DIR+"ORDER_COVER_"+order_id+".pdf").isFile() ) { 
                pdfCover.createOrderCoverPdf(order_id);
                dao.adjurnCustomerLogoByOrderId(order_id, true);
            }
           
            //checks in db the value of the bit logoAdjurned , if the logo has been adjurned launches a new pdf creation and sets the logoAdjurned value to 1
            if( adjurned )
            {
                pdfCover.createOrderCoverPdf(order_id);
                dao.adjurnCustomerLogoByOrderId(order_id, true);
            }
            
            jsonResponse.addProperty("success",true);
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }

    
    /**/
    private void updateOrder( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute( "user_id" ) != null )
        {
            // READS SESSION DATA
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // READS OPERATION PARAMETERS
            Long order_id = null;
            try{ order_id = Long.parseLong( request.getParameter( "order_id" ) );}
            catch( NumberFormatException Nex){ jsonResponse.addProperty("message","invalid order_id format"); }
            
            Long customer_id = null;
            if( request.getParameter( "customer_id" ) != null )
            {  
                try{ customer_id = Long.parseLong( request.getParameter( "customer_id" ) );}
                catch( NumberFormatException Nex){ jsonResponse.addProperty("message","invalid customer_id format"); }
            }
            
            Long user_id = null;
            if( request.getParameter( "user_id" ) != null )
            {  
                try{ user_id = Long.parseLong( request.getParameter( "user_id" ) );}
                catch( NumberFormatException Nex){ jsonResponse.addProperty("message","invalid user_id format"); }
            }
            
            Long jobType_id = null;
            if( request.getParameter( "jobType_id" ) != null )
            {  
                try{ jobType_id = Long.parseLong( request.getParameter( "jobType_id" ) );}
                catch( NumberFormatException Nex){ jsonResponse.addProperty("message","invalid jobType_id format"); }
            }
            
            Long completionState_id = null;
            if( request.getParameter( "completionState_id" ) != null )
            {  
                try{ completionState_id = Long.parseLong( request.getParameter( "completionState_id" ) );}
                catch( NumberFormatException Nex){ jsonResponse.addProperty("message","invalid completionState_id format"); }
            }
            
            String date = request.getParameter( "date" );
            
            Long code = null;
            if( request.getParameter( "code" ) != null )
            {  
                try{ code = Long.parseLong( request.getParameter( "code" ) );}
                catch( NumberFormatException Nex){ jsonResponse.addProperty("message","invalid code format"); }
            }
            
            String machinaryModel = request.getParameter( "machinaryModel" );
            
            String notes = request.getParameter( "notes" );
            
            String ordine = request.getParameter( "ordine" );
            
            String commessa = request.getParameter( "commessa" );
            
            String dataOrdine = request.getParameter( "dataOrdine" );
            
            String storyData = request.getParameter( "storyData" );
            
            Boolean notSuggest = null;
            if( request.getParameter( "notSuggest" ) != null )
            {  
                try{ notSuggest = Boolean.parseBoolean( request.getParameter( "notSuggest" ) );}
                catch( NumberFormatException Nex){ jsonResponse.addProperty("message","invalid notSuggest format"); }
            }
            //String closingReason = request.getParameter( "closingReason" );
            
            // UPDATES THE RECORD
            boolean success = dao.updateOrder( order_id, customer_id, user_id, jobType_id, completionState_id,  date, code, machinaryModel, notes, notSuggest, ordine, commessa, dataOrdine, storyData );
            
            jsonResponse.addProperty("success",success);
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    //TASKS
    
    private void createTask( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        /*ask for a session, if it doesn't exist, it won't be created. 
        Note that this mechanism of session checking avoids malware attempts
        to enter the system without executing log-in procedure*/
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // reads operation parameters
            
            String order_code = request.getParameter( "order_code" );
            
            Long user_id = null;
            try{ user_id = Long.parseLong( request.getParameter( "user_id" ) );}
            catch( NumberFormatException Nex){ jsonResponse.addProperty("message","invalid user_id format"); }
            
            Long jobSubtype_id = null;
            try{ jobSubtype_id = Long.parseLong( request.getParameter( "jobSubtype_id" ) );}
            catch( NumberFormatException Nex){ jsonResponse.addProperty("message","invalid jobSubtype_id format"); }
           
            String date = request.getParameter( "date" ); 
            
            Double hours = null;
            try{ hours = Double.parseDouble( request.getParameter( "hours" ) );}
            catch( NumberFormatException Nex){ jsonResponse.addProperty("message","invalid hours format"); }
            
            Long translations_center_id = null;
            try{ translations_center_id = Long.parseLong( request.getParameter( "translations_center_id" ) );}
            catch( NumberFormatException Nex){ jsonResponse.addProperty("message","invalid translations_center_id format"); }
            
            Double translationPrice = null;
            try{ translationPrice = Double.parseDouble( request.getParameter( "translationPrice" ) );}
            catch( NumberFormatException Nex){ jsonResponse.addProperty("message","invalid translationPrice format"); }
            
            Double translationCost = null;
            try{ translationCost = Double.parseDouble( request.getParameter( "translationCost" ) );}
            catch( NumberFormatException Nex){ jsonResponse.addProperty("message","invalid translationCost format"); }
            
            String externalJobsDesc = request.getParameter("externalJobsDesc");
            
            Double externalJobsHours = null;
            try{ externalJobsHours = Double.parseDouble( request.getParameter( "externalJobsHours" ) );}
            catch( NumberFormatException Nex){ jsonResponse.addProperty("message","invalid externalJobsHours format"); }
            
            Double externalJobsCost = null;
            try{ externalJobsCost = Double.parseDouble( request.getParameter( "externalJobsCost" ) );}
            catch( NumberFormatException Nex){ jsonResponse.addProperty("message","invalid externalJobsCost format"); }
            
            String variouseMaterialDesc = request.getParameter("variouseMaterialDesc");
            
            Double variouseMaterialCost = null;
            try{ variouseMaterialCost = Double.parseDouble( request.getParameter( "variouseMaterialCost" ) );}
            catch( NumberFormatException Nex){ jsonResponse.addProperty("message","invalid variouseMaterialCost format"); }
            
            Integer transfertKms = null;
            try{ transfertKms = Integer.parseInt( request.getParameter( "transfertKms" ) );}
            catch( NumberFormatException Nex){ jsonResponse.addProperty("message","invalid transfertKms format"); }
            
            Double transfertCost = null;
            try{ transfertCost = Double.parseDouble( request.getParameter( "transfertCost" ) );}
            catch( NumberFormatException Nex){ jsonResponse.addProperty("message","invalid transfertCost format"); }
            
            String notes = request.getParameter( "notes" ); 
            
            // order_id, user_id, jobSubtype_id,date,hours,translationsCenterName, translationPrice, translationQuotation, variouseMaterialDesc, variouseMaterialCost, transfertKms, transfertCost, notes  
            Long task_id = dao.createTask( order_code, user_id, jobSubtype_id, date, hours, translations_center_id, translationPrice, translationCost, externalJobsDesc, externalJobsHours,externalJobsCost, variouseMaterialDesc, variouseMaterialCost, transfertKms, transfertCost, notes );
            
            jsonResponse.addProperty("success",true);
            jsonResponse.addProperty( "task_id", task_id );
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }    
   
    // Long task_id, Long user_id, Long order_id, Long operator_id, String orderCode, Long jobType_id, Long jobSubtype_id, Long customer_id, Long creator_id, String fromDate, String toDate, Long completion_state_id
    private void readTasks( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        /*ask for a session, if it doesn't exist, it won't be created. 
        Note that this mechanism of session checking avoids malware attempts
        to enter the system without executing log-in procedure*/
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // reads operation parameters
            Long task_id = null;
            try{ task_id = Long.parseLong( request.getParameter( "task_id" ) );}
            catch( NumberFormatException Nex){}
            
            // reads operation parameters
            Long user_id = null;
            try{ user_id = Long.parseLong( request.getParameter( "user_id" ) );}
            catch( NumberFormatException Nex){}
            
            Long order_id = null;
            try{ order_id = Long.parseLong( request.getParameter( "order_id" ) );}
            catch( NumberFormatException Nex){}
            
            Long operator_id = null;
            try{ operator_id = Long.parseLong( request.getParameter( "operator_id" ) );}
            catch( NumberFormatException Nex){}
            
            String orderCode = request.getParameter( "orderCode" );
            
            Long jobType_id = null;
            try{ jobType_id = Long.parseLong( request.getParameter( "jobType_id" ) );}
            catch( NumberFormatException Nex){}   

            Long jobSubtype_id = null;
            try{ jobSubtype_id = Long.parseLong( request.getParameter( "jobSubtype_id" ) );}
            catch( NumberFormatException Nex){}   
            
            Long customer_id = null;
            try{ customer_id = Long.parseLong( request.getParameter( "customer_id" ) );}
            catch( NumberFormatException Nex){}   
            
            Long order_creator_id = null;
            try{ order_creator_id = Long.parseLong( request.getParameter( "order_creator_id" ) );}
            catch( NumberFormatException Nex){}   
            
            String fromDate = request.getParameter( "fromDate" );
            
            String toDate = request.getParameter( "toDate" );
            
            Long completion_state_id = null;
            try{ completion_state_id = Long.parseLong( request.getParameter( "completion_state_id" ) );}
            catch( NumberFormatException Nex){}         
            
            // asks DB for tasks
           DbResult dbr = dao.readTasks( task_id, user_id, order_id, operator_id,  orderCode, jobType_id, jobSubtype_id, customer_id, order_creator_id, fromDate,  toDate, completion_state_id );
           // DbResult dbr =dao.readTasks( 31L, 1L, null, null, null,  null, null, null, null, null, null,  null, null );
            jsonResponse.addProperty("success",true);
            //jsonResponse.add("orders",dbr.toJson(true));
            
            JsonArray tasks = new JsonArray();
            
            for(int i=0; i<dbr.rowsCount(); i++)
            {
                JsonArray record = new JsonArray();
                //0
                record.add( new JsonPrimitive( dbr.getLong( i, "task_id" )));
                //1
                record.add( new JsonPrimitive( dbr.getLong( i, "order_id" )));
                 //2       
                record.add( new JsonPrimitive( dbr.getLong( i, "operator_id" )));
                
                //3
                if( dbr.getLong( i, "jobSubtype_id" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                 {
                     //solves the name
                    //String jobSubtypeName = dao.getJobSubtypeName(dbr.getLong( i, "jobSubtype_id" ));
                    record.add(new JsonPrimitive( dbr.getString(i,"jobSubtypeName" )));
                 }
                //4
                if( dbr.getString( i, "taskDate" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                //changes the date format
                else
                {
                    //get a stringbuilder to insert dashes
                    StringBuilder dateSB = new StringBuilder(dbr.getString(i, "taskDate" ));
                    dateSB.insert(4,"-");
                    dateSB.insert(7,"-");
                    record.add( new JsonPrimitive( DTFE.format(DTF.parse(dateSB.toString()))));
                }
                //5
                if( dbr.getDouble( i, "hours" ) == null || dbr.getDouble( i, "hours" ) == 0.0  )
                    record.add( new JsonPrimitive( 0 ) );
                else
                    record.add( new JsonPrimitive( dbr.getDouble( i, "hours" )));
                //6
                if( dbr.getLong( i, "translations_center_id" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getLong( i, "translations_center_id" )));
                //7
                if( dbr.getDouble( i, "translationCost" ) == null || dbr.getDouble( i, "translationCost" ) == 0.0 )
                    record.add( new JsonPrimitive( 0 ) );
                else
                    record.add( new JsonPrimitive( dbr.getDouble( i, "translationCost" )));
                //8
                if( dbr.getString( i, "externalJobsDesc" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "externalJobsDesc" )));
                //9
                if( dbr.getString( i, "variouseMaterialDesc" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "variouseMaterialDesc" )));
                //10
                if( dbr.getInteger( i, "transfertKms" ) == null || dbr.getInteger( i, "transfertKms" ) == 0 )
                    record.add( new JsonPrimitive( 0 ) );
                else
                    record.add( new JsonPrimitive( dbr.getInteger( i, "transfertKms" ) ));
                //11
                if( dbr.getDouble( i, "transfertCost" ) == 0.0 || dbr.getDouble( i, "transfertCost" ) == null ) 
                    record.add( new JsonPrimitive( 0 ) );
                else
                    record.add( new JsonPrimitive( dbr.getDouble( i, "transfertCost" )));
                //12
                if( dbr.getDouble( i, "externalJobsCost" ) == null || dbr.getDouble( i, "externalJobsCost" ) == 0.0 )
                    record.add( new JsonPrimitive( 0 ) );
                else
                    record.add( new JsonPrimitive( dbr.getDouble( i, "externalJobsCost" )));
                //13
                if( dbr.getDouble( i, "variouseMaterialCost" ) == null || dbr.getDouble( i, "variouseMaterialCost" ) == 0.0 )
                    record.add( new JsonPrimitive( 0 ) );
                else
                    record.add( new JsonPrimitive( dbr.getDouble( i, "variouseMaterialCost" )));
                //14
                if( dbr.getDouble( i, "translationPrice" ) == null || dbr.getDouble( i, "translationPrice" ) == 0.0 )
                    record.add( new JsonPrimitive( 0 ) );
                else
                    record.add( new JsonPrimitive( dbr.getDouble( i, "translationPrice" )));
                //15
                if( dbr.getLong( i, "customer_id" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getLong( i, "customer_id" )));
                //16
                 if( dbr.getLong( i, "order_creator_id" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getLong( i, "order_creator_id" )));
                //17
                 if( dbr.getLong( i, "jobType_id" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                 {
                     //solves the name
                    //String jobTypeName = dao.getJobTypeName(dbr.getLong( i, "jobType_id" ));
                    record.add( new JsonPrimitive(dbr.getString(i, "jobTypeName")));
                 }
                //18
                if( dbr.getLong( i, "completion_state_id" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getLong( i, "completion_state_id" )));
                //19
                if( dbr.getString( i, "orderCompletionState" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "orderCompletionState" )));
                //20
                if( dbr.getString( i, "orderDate" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                {
                    //get a stringbuilder to insert dashes
                    StringBuilder dateSB = new StringBuilder(dbr.getString(i, "orderDate" ));
                    dateSB.insert(4,"-");
                    dateSB.insert(7,"-");
                    record.add( new JsonPrimitive( DTFE.format(DTF.parse(dateSB.toString()))));
                }
                 //21
                if( dbr.getString( i, "orderCode" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "orderCode" )));
                //22
                if( dbr.getString( i, "machinaryModel" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "machinaryModel" )));
                //23
                if( dbr.getString( i, "customerDenomination" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "customerDenomination" )));
                //24
                if( dbr.getString( i, "operatorFirstName" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "operatorFirstName" )));
                //25
                if( dbr.getString( i, "operatorLastName" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "operatorLastName" )));
                //26
                if( dbr.getString( i, "orderCreatorFirstName" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "orderCreatorFirstName" )));
                //27
                if( dbr.getString( i, "orderCreatorLastName" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "orderCreatorLastName" )));
                //28
                if( dbr.getString( i, "notes" ) == null )
                    record.add( new JsonPrimitive( "" ) );
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "notes" )));
                //29
                record.add( new JsonPrimitive( dbr.getDouble( i, "hourlyOperatorCost" )));
                //30
                if( dbr.getDouble( i, "externalJobsHours" ) == null )
                    record.add( new JsonPrimitive( 0.0 ) );
                else
                    record.add( new JsonPrimitive( dbr.getDouble( i, "externalJobsHours" )));
                //31 Has Attachment:  calls DAO to know if the task has got unless one related attachment
                record.add( new JsonPrimitive( dbr.getInteger(i, "hasAttachment") ));
      
                tasks.add(record);
            }
            
            jsonResponse.add("tasks", tasks);
            jsonResponse.addProperty("success",true);
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    //Long task_id, Long order_id, String jobSubtype, String taskDate, Double hours, String translationsCenterName, Double translationPrice, String variouseMaterialDesc, Double variouseMaterialCost, Integer transfertKms, Double transfertCost, String notes
     private void updateTask( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException, Exception
    {
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute( "user_id" ) != null )
        {
            // READS SESSION DATA
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // READS OPERATION PARAMETERS
            Long task_id = null;
            try{ task_id = Long.parseLong( request.getParameter( "task_id" ) );}
            catch( NumberFormatException | NullPointerException Nex){ throw new Exception("missing or incorrect task id");}
            
            /*Long order_id = null;
            try{ order_id = Long.parseLong( request.getParameter( "order_id" ) );}
            catch( NumberFormatException | NullPointerException Nex){ throw new Exception("missing or incorrect order_id");}*/
            
            String orderCode = request.getParameter( "order_code" );
            
            Long jobSubtype_id = null;
            try{ jobSubtype_id = Long.parseLong( request.getParameter( "jobSubtype_id" ) );}
            catch( NumberFormatException | NullPointerException Nex){ throw new Exception("missing or incorrect jobSubtype_id ");}
           
            String taskDate = request.getParameter("taskDate");
            
            
            Double hours = null;
            try{ hours = Double.parseDouble( request.getParameter( "hours" ) );}
            catch( RuntimeException Nex){}
            
            Long translations_center_id = null;
            try{ translations_center_id = Long.parseLong( request.getParameter( "translations_center_id" ) );}
            catch( NumberFormatException | NullPointerException Nex){}
            
            Double translationPrice = null;
            try{ translationPrice = Double.parseDouble( request.getParameter( "translationPrice" ) );}
            catch( RuntimeException Nex){}
            
            Double translationCost = null;
            try{ translationCost = Double.parseDouble( request.getParameter( "translationCost" ) );}
            catch( RuntimeException Nex){}
            
            String externalJobsDesc = request.getParameter( "externalJobsDesc" );
            
            Double externalJobsHours = null;
            try{ externalJobsHours = Double.parseDouble( request.getParameter( "externalJobsHours" ) );}
            catch( RuntimeException Nex){}
            
            Double externalJobsCost = null;
            try{ externalJobsCost = Double.parseDouble( request.getParameter( "externalJobsCost" ) );}
            catch( RuntimeException Nex){}
            
            String variouseMaterialDesc = request.getParameter( "variouseMaterialDesc" );
            
            Double variouseMaterialCost = null;
            try{ variouseMaterialCost = Double.parseDouble( request.getParameter( "variouseMaterialCost" ) );}
            catch( RuntimeException Nex){}

            Integer transfertKms = null;
            try{ transfertKms = Integer.parseInt( request.getParameter( "transfertKms" ) );}
            catch( RuntimeException Nex){}

            Double transfertCost = null;
            try{ transfertCost = Double.parseDouble( request.getParameter( "transfertCost" ) );}
            catch( RuntimeException Nex){}

            String notes = request.getParameter( "notes" );
           
            
            // UPDATES THE RECORD
            boolean success = dao.updateTask( task_id, orderCode, jobSubtype_id, taskDate, hours, translations_center_id, translationPrice, translationCost, externalJobsDesc, externalJobsHours, externalJobsCost, variouseMaterialDesc, variouseMaterialCost, transfertKms, transfertCost, notes );
            
            jsonResponse.addProperty("success",success);
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    private void deleteTask( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute( "user_id" ) != null )
        {
            // READS SESSION DATA
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // READS OPERATION PARAMETERS
            Long task_id = null;
            try{ task_id = Long.parseLong( request.getParameter( "task_id" ) );}
            catch( NumberFormatException Nex){ jsonResponse.addProperty("message","invalid task_id format"); }
            
            // DELETE THE RECORD
            boolean success = dao.deleteTask( task_id );
            
            jsonResponse.addProperty("success",success);
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    // ATTACHMENTS
   
    private void createTaskAttachment( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException, NumberFormatException, ServletException
    {
        HttpSession session = request.getSession(false);
        
        String UPLOAD_DIR = Config.TASKS_ATTACH_DIR;
        
        DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
        
        String originalFileName = "";
        String destFileName;
        
        if( session != null && session.getAttribute( "user_id" ) != null )
        {
            
            Long task_id = Long.parseLong(request.getParameter("task_id"));
            Long user_id = Long.parseLong( session.getAttribute( "user_id" ).toString());
            
            try
            {
                //gets original file name
                for(Part part : request.getParts())
                {
                    originalFileName = new String(part.getSubmittedFileName().getBytes(),StandardCharsets.UTF_8); 
                }
                
                //creates the attachment in DB
                Long taskAttachment_id = dao.createTaskAttachment(task_id, user_id, originalFileName);
                
                //initializes the destination file name
                destFileName =  taskAttachment_id + "_" + originalFileName;
                
                //sets the destination file name in DB
                dao.setTaskAttachmentName( originalFileName, destFileName, taskAttachment_id);
                
                //uploads the file
                for(Part part : request.getParts())
                {
                    part.write(UPLOAD_DIR+destFileName);
                }
               
                jsonResponse.addProperty("success",true);
                jsonResponse.addProperty("message","UPLOADED");
            }
            catch( IOException | SQLException ex)
            {
                jsonResponse.addProperty("success",false);
                jsonResponse.addProperty("message","EXCEPTION : "+ex);
            }
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    private void deleteNullTaskAttachments( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        /*ask for a session, if it doesn't exist, it won't be created. 
        Note that this mechanism of session checking avoids malware attempts
        to enter the system without executing log-in procedure*/
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            
            Long user_id = Long.parseLong(session.getAttribute("user_id").toString());
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            dao.deleteNullTaskAttachments( user_id );
            
            jsonResponse.addProperty("success",true);
        }  
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
     
    private void deleteTaskAttachments( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        /*ask for a session, if it doesn't exist, it won't be created. 
        Note that this mechanism of session checking avoids malware attempts
        to enter the system without executing log-in procedure*/
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            
            Long attachment_id = Long.parseLong(request.getParameter("attachment_id"));//Long.parseLong(request.getParameter("user_id"));
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            dao.deleteTaskAttachments( attachment_id );
            
            jsonResponse.addProperty("success",true);
        }  
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    private void readTaskAttachments( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        /*ask for a session, if it doesn't exist, it won't be created. 
        Note that this mechanism of session checking avoids malware attempts
        to enter the system without executing log-in procedure*/
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // reads operation parameters
            Long task_id = null;
            try{ task_id = Long.parseLong( request.getParameter( "task_id" ) );}
            catch( NumberFormatException Nex){}
            
            //If task_id is 0 retrieves attachments having task_id = null
            if( task_id == 0L)
                task_id = null;
            
            Long user_id = Long.parseLong(session.getAttribute("user_id").toString());
            
            // asks DB for customers
            DbResult dbr = dao.readTaskAttachments( task_id, user_id );
            
            jsonResponse.addProperty("success",true);
            
            JsonArray taskAttachments = new JsonArray();
            
            for(int i=0; i<dbr.rowsCount(); i++)
            {
                JsonArray record = new JsonArray();
                //0
                record.add( new JsonPrimitive( dbr.getLong( i, "taskAttachment_id" )));
                
                //1
                //get a stringbuilder to insert dashes
                StringBuilder dateSB = new StringBuilder(dbr.getString(i, "date" ));
                dateSB.insert(4,"-");
                dateSB.insert(7,"-");
                record.add( new JsonPrimitive( DTFE.format(DTF.parse(dateSB.toString()))));
                
                //2
                record.add( new JsonPrimitive( dbr.getString( i, "currentFileName" )));
                
                //3
                task_id = dbr.getLong( i, "task_id" );
                if( task_id == null )
                    record.add( new JsonPrimitive( 0L ));
                else
                    record.add( new JsonPrimitive( task_id ));
                
                taskAttachments.add(record);
            }
            
            jsonResponse.add("taskAttachments", taskAttachments);
            
            jsonResponse.addProperty("success",true);
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    private void unlockUser( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException, ClassNotFoundException
    {
        String username = request.getParameter("username");
        new DataAccessObject().restoreUserByUsername(username);
    }
    
    @Override
    public void sessionDestroyed(HttpSessionEvent event)
    {
        String session_id = event.getSession().getId();
        //HttpSession session = event.getSession();
        
        System.out.println("Session #"+session_id+" destroyed");
        try{
        new DataAccessObject().restoreUser(session_id);
        }catch(ClassNotFoundException ex ){ex.printStackTrace();}
    }    
    
    @Override
    public void sessionCreated(HttpSessionEvent event)
    {
        String session_id = event.getSession().getId();
        System.out.println("Session #"+session_id+" created");
    }
    
    //DELIVERY NOTES
    
    private void createDeliveryNote( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute( "user_id" ) != null )
        {
            DeliveryNote newDeliveryNote = null;
            try
            {
                String s = bodyString(request);
                JsonObject jsonDeliveryNote = JSON.parse(s);
                newDeliveryNote = new DeliveryNote(jsonDeliveryNote);
            } catch(RuntimeException ex){}

            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            //retrieves suggested value
            Boolean suggested = Boolean.parseBoolean(request.getParameter("suggest"));

            Long deliveryNote_id = dao.createDeliveryNote(newDeliveryNote, suggested);

            jsonResponse.addProperty("success",true);
            
            jsonResponse.addProperty("deliveryNote_id",deliveryNote_id);
        }
        
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    private void readDeliveryNotes( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        /*ask for a session, if it doesn't exist, it won't be created. 
        Note that this mechanism of session checking avoids malware attempts
        to enter the system without executing log-in procedure*/
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // reads operation parameters
            Long deliveryNote_id = null;
            try{ deliveryNote_id = Long.parseLong( request.getParameter( "deliveryNote_id" ) );}
            catch( NumberFormatException Nex){}
            
            // reads operation parameters
            Long customer_id = null;
            try{ customer_id = Long.parseLong( request.getParameter( "customer_id" ) );}
            catch( NumberFormatException Nex){}
            
            Long transporter_id = null;
            try{ transporter_id = Long.parseLong( request.getParameter( "transporter_id" ) );}
            catch( NumberFormatException Nex){}
            
            String number = request.getParameter( "number" );
                      
            LocalDate fromDate = request.getParameter( "fromDate" ) == null || request.getParameter( "fromDate" ).equals("")  ? null : LocalDate.parse( request.getParameter( "fromDate" ) );
            
            LocalDate toDate = request.getParameter( "toDate" ) == null || request.getParameter( "toDate" ).equals("") ? null : LocalDate.parse( request.getParameter( "toDate" ) );
                               
            // asks DB for deliveryNotes
           DbResult deliveryNotes_dbr = dao.readDeliveryNotes( deliveryNote_id, customer_id, transporter_id, number,  fromDate, toDate );
           
           DbResult deliveryNotesRows_dbr = null;
           //gets all delivery notes rows only if delivery notes searching gives back some results
           if( deliveryNotes_dbr.rowsCount() > 0  )
           {    
               deliveryNotesRows_dbr = dao.readDeliveryNotesRows( deliveryNotes_dbr );
           }
           
            jsonResponse.addProperty("success",true);
            
            //delivery notes Json array
            JsonArray deliveryNotes = new JsonArray();
            
            //if result is not empty fills the Json array
            if( deliveryNotes_dbr.rowsCount() > 0 )
            {
                //for each delivery note
                for( int i = 0; i < deliveryNotes_dbr.rowsCount(); i++)
                {
                    //creates an array list for delivery notes rows
                    List<DeliveryNote.Item> items = new ArrayList<>();

                    //creates a deliveryNote instance to initialize the nested class item
                    DeliveryNote deliveryNote = new DeliveryNote();

                    //gets only delivery note rows belonging to the current delivery note
                    for( int j = 0; j < deliveryNotesRows_dbr.rowsCount(); j++ )
                    {
                        if( deliveryNotes_dbr.getLong( i, "deliveryNote_id").equals( deliveryNotesRows_dbr.getLong( j, "deliveryNote_id") ) )
                        {
                            DeliveryNote.Item item;
                            item = deliveryNote.new Item( deliveryNotesRows_dbr, j  );
                            items.add(item);    
                        }
                    }

                     //creates the deliveryNOte Json object by static method in DeliveryNote java class
                    JsonObject jsonDeliveryNote = DeliveryNote.getJsonByDbResults(deliveryNotes_dbr, i, items );

                    deliveryNotes.add(jsonDeliveryNote);
                }
            }
            
            
            jsonResponse.add("deliveryNotes", deliveryNotes);
            
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    private void readDeliveryNoteRows( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        /*ask for a session, if it doesn't exist, it won't be created. 
        Note that this mechanism of session checking avoids malware attempts
        to enter the system without executing log-in procedure*/
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // reads operation parameters
            Long deliveryNote_id = null;
            try{ deliveryNote_id = Long.parseLong( request.getParameter( "deliveryNote_id" ) );}
            catch( NumberFormatException Nex){}
                               
            // asks DB for deliveryNotes
           DbResult dbr = dao.readDeliveryNoteRows( deliveryNote_id );
          
            jsonResponse.addProperty("success",true);
            
            JsonArray rows = new JsonArray();
            
            for(int i=0; i<dbr.rowsCount(); i++)
            {
                JsonArray record = new JsonArray();
                //0
                record.add( new JsonPrimitive( dbr.getLong( i, "deliveryNoteRow_id" )));
                //1
                record.add( new JsonPrimitive( dbr.getLong( i, "deliveryNote_id" )));
                //2
                record.add( new JsonPrimitive( dbr.getString( i, "code" )));
                //3
                record.add( new JsonPrimitive( dbr.getString( i, "description" )));
                 //4    
                record.add( new JsonPrimitive( dbr.getInteger( i, "quantity" )));
                                
                rows.add(record);
            }
            
            jsonResponse.add("rows", rows);
            
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    private void updateDeliveryNote( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute( "user_id" ) != null )
        {
            DeliveryNote deliveryNote = null;
            try
            {
                String s = bodyString(request);
                JsonObject jsonDeliveryNote = JSON.parse(s);
                deliveryNote = new DeliveryNote(jsonDeliveryNote);
            } catch(RuntimeException ex){}

            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");

            //retrieves suggested value
            Boolean suggested = Boolean.parseBoolean(request.getParameter("suggest"));
            
            Long deliveryNote_id = dao.updateDeliveryNote(deliveryNote, suggested);

            jsonResponse.addProperty("success",true);
            
            jsonResponse.addProperty("deliveryNote_id",deliveryNote_id);
        }
        
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    private void getSuggestedDeliveryNotesRows( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        /*ask for a session, if it doesn't exist, it won't be created. 
        Note that this mechanism of session checking avoids malware attempts
        to enter the system without executing log-in procedure*/
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // reads operation parameters
            Long customer_id = Long.parseLong( request.getParameter( "customer_id" )) ;
            String orderCode = request.getParameter( "orderCode" ) ;
            
            // calls dao moethod
            DbResult dbr = dao.getSuggestedDeliveryNotesRows(customer_id, orderCode);
            
            jsonResponse.addProperty("success",true);
            //jsonResponse.add("orders",dbr.toJson(true));
            
            JsonArray deliveryNotesRows = new JsonArray();
            
            for(int i=0; i<dbr.rowsCount(); i++)
            {
                JsonArray record = new JsonArray();
                //0
                record.add( new JsonPrimitive( dbr.getLong( i, "deliveryNoteRow_id" )));
                //1
                record.add( new JsonPrimitive( dbr.getLong( i, "deliveryNote_id" )));
                //2
                record.add( new JsonPrimitive( dbr.getLong( i, "customer_id" )));
                //3
                record.add( new JsonPrimitive( dbr.getInteger( i, "number" )));
                //4
                if( (dbr.getString(i, "date" )) == null )
                    record.add( new JsonPrimitive( "" ) );
                //changes the date format
                else
                {
                    //get a stringbuilder to insert dashes
                    StringBuilder dateSB = new StringBuilder(dbr.getString(i, "date" ));
                    dateSB.insert(4,"-");
                    dateSB.insert(7,"-");
                    
                    record.add( new JsonPrimitive( DTFE.format(DTF.parse(dateSB.toString()))));
                }
                //5
                if( (dbr.getLong(i, "order_id" )) == null )
                    record.add( new JsonPrimitive( 0 ));
                else
                    record.add( new JsonPrimitive( dbr.getLong( i, "order_id" )));
                //6
                if( (dbr.getString(i, "code" )) == null )
                    record.add( new JsonPrimitive( ""));
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "code" )));
                //7
                if( (dbr.getString(i, "description" )) == null )
                    record.add( new JsonPrimitive( ""));
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "description" )));
                //8
                record.add( new JsonPrimitive( dbr.getBoolean( i, "rowInvoiced" ) ) );
                //9
                    record.add( new JsonPrimitive( dbr.getBoolean( i, "suggested" ) ) );
                //10
                record.add( new JsonPrimitive( dbr.getBoolean( i, "deliveryNoteInvoiced" ) ) );
                //11
                record.add( new JsonPrimitive( dbr.getInteger( i, "quantity" ) ) );
                //it is necessary the check on null values becouse ordine and commessa have been introduced 
                //after the first release then there are a lot of orders having ordine = commessa = NULL
                //12
                if( dbr.getString( i, "ordine" ) != null)
                    record.add( new JsonPrimitive( dbr.getString( i, "ordine" ) ) );
                else
                    record.add( new JsonPrimitive( "" ) );
                //13
                if( dbr.getString( i, "commessa" ) != null)
                    record.add( new JsonPrimitive( dbr.getString( i, "commessa" ) ) );
                else
                    record.add( new JsonPrimitive( "" ) );

                deliveryNotesRows.add(record);
            }
            
            jsonResponse.add("deliveryNotesRows", deliveryNotesRows);
            
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    private void printDeliveryNote( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // reads operation parameters
            Long deliveryNote_id = Long.parseLong( request.getParameter( "deliveryNote_id" )) ;
            
            //intialize and call pdf printer that puts the pdf file in the destination folder 
            try{
                DeliveryNotePdfPrinter_1 pdfPrinter = new DeliveryNotePdfPrinter_1();
                pdfPrinter.printDeliveryNote(deliveryNote_id);
                jsonResponse.addProperty("success",true);
            }catch( Exception ex ){ jsonResponse.addProperty("success",false);}
              
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    private void createAllNewPdfFiles( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            DbResult deliveryNotesDbr = dao.readAllDeliveryNotesFromTable();
            
            
            for( int i = 0; i < deliveryNotesDbr.rowsCount(); i++)
            {
                //intialize and call pdf printer that puts the pdf file in the destination folder 
                try{
                    DeliveryNotePdfPrinter_1 pdfPrinter = new DeliveryNotePdfPrinter_1();
                    pdfPrinter.printDeliveryNote(deliveryNotesDbr.getLong(i,"deliveryNote_id"));
                    jsonResponse.addProperty("success",true);
                }catch( Exception ex ){ jsonResponse.addProperty("success",false);}
            }
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    //INVOICES
    
    private void createInvoice( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute( "user_id" ) != null )
        {
            Invoice newInvoice = null;
            try
            {
                String s = bodyString(request);
                JsonObject jsonInvoice = JSON.parse(s);
                newInvoice = new Invoice(jsonInvoice);
            } catch(RuntimeException ex){}

            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");

            DbResult invoice = dao.createInvoice(newInvoice);

            jsonResponse.addProperty("success",true);
            
            //for each record creates a dbr having a single invoice
            DbResult invoice_dbr = dao.readInvoices(invoice.getLong("invoice_id"), null, null, null, null);
            
            //retrieves invoice rows
            DbResult dbr_rows = dao.readInvoiceRows(invoice.getLong("invoice_id"));
            
            //creates the invoice Json object by static method in invoice java class
            JsonObject jsonInvoice = Invoice.getJsonByDbResults( invoice_dbr, dbr_rows );

            jsonResponse.add("invoice",jsonInvoice);
        }
        
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    private void readInvoices( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        /*ask for a session, if it doesn't exist, it won't be created. 
        Note that this mechanism of session checking avoids malware attempts
        to enter the system without executing log-in procedure*/
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // reads operation parameters
            Long invoice_id = null;
            try{ invoice_id = Long.parseLong( request.getParameter( "invoice_id" ) );}
            catch( NumberFormatException Nex){}
            
            // reads operation parameters
            Long customer_id = null;
            try{ customer_id = Long.parseLong( request.getParameter( "customer_id" ) );}
            catch( NumberFormatException Nex){}
            
            String number = request.getParameter( "number" );
                      
            LocalDate fromDate = request.getParameter( "fromDate" ) == null || request.getParameter( "fromDate" ).equals("")  ? null : LocalDate.parse( request.getParameter( "fromDate" ) );
            
            LocalDate toDate = request.getParameter( "toDate" ) == null || request.getParameter( "toDate" ).equals("") ? null : LocalDate.parse( request.getParameter( "toDate" ) );
                               
            long start = new Date().getTime();
            System.out.println("READING INVOICES [Gate.readInvoices]");
            
            // asks DB for invoices
            DbResult invoices_dbr = dao.readInvoices( invoice_id, customer_id, number,  fromDate, toDate );
            System.out.println("INVOICES READ [DataAccessObject.readInvoices] : elapsed msec "+(new Date().getTime()-start));
           
            start = new Date().getTime();
            
            DbResult invoicesRows_dbr = null;
            
            //gets all invoices rows only if invoces result gives back some records
            if( invoices_dbr.rowsCount() > 0 )
            {
                invoicesRows_dbr = dao.readInvoicesRows( invoices_dbr );
                System.out.println("INVOICES ROWS READ [DataAccessObject.readInvoices] : elapsed msec "+(new Date().getTime()-start));
            }
            
            jsonResponse.addProperty("success",true);
            
            //invoices Json array
            JsonArray invoices = new JsonArray();
            
            start = new Date().getTime();
            
            //fills JSON Array only if the result is not empty
            if( invoices_dbr.rowsCount() > 0 )
            {
                //for each invoice
                for( int i = 0; i < invoices_dbr.rowsCount(); i++)
                {
                    //creates an array list for invoice rows
                    List<Invoice.Item> items = new ArrayList<>();

                    //creates an Invoice instance to initialize the nested class item
                    Invoice invoice = new Invoice();

                    //gets only invioce rows belonging to the current invoice
                    for( int j = 0; j < invoicesRows_dbr.rowsCount(); j++ )
                    {
                        if( invoices_dbr.getLong( i, "invoice_id").equals( invoicesRows_dbr.getLong( j, "invoice_id") ) )
                        {
                            Invoice.Item item;
                            item = invoice.new Item( invoicesRows_dbr, j  );
                            items.add(item);
                        }
                    }

                     //creates the invoice Json object by static method in Invoice java class
                    JsonObject jsonInvoice = Invoice.getJsonByDbResults(invoices_dbr, i, items );

                    invoices.add(jsonInvoice);
                }
            }
            
            System.out.println("JSON INVOICES BUILD [DataAccessObject.readInvoices] : elapsed msec "+(new Date().getTime()-start));
            
            jsonResponse.add("invoices", invoices);
            
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    private void readInvoiceRows( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        
        /*ask for a session, if it doesn't exist, it won't be created. 
        Note that this mechanism of session checking avoids malware attempts
        to enter the system without executing log-in procedure*/
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // reads operation parameters
            Long invoice_id = null;
            try{ invoice_id = Long.parseLong( request.getParameter( "invoice_id" ) );}
            catch( NumberFormatException Nex){}
                               
            // asks DB for invoce
           DbResult dbr = dao.readInvoiceRows( invoice_id );
          
            jsonResponse.addProperty("success",true);
            
            JsonArray rows = new JsonArray();
            
            for(int i=0; i<dbr.rowsCount(); i++)
            {
                JsonArray record = new JsonArray();
                
                //0
                if( dbr.getString("code") != null)
                    record.add( new JsonPrimitive( dbr.getString( i, "code" )));
                else
                    record.add( new JsonPrimitive( dbr.getString( i, "" )));
                
                //1
                record.add( new JsonPrimitive( dbr.getString( i, "description" )));
                
                //2 delivery note reference
                if( dbr.getLong( i, "deliveryNoteRow_id" ) != null )
                {
                    String date = dbr.getString(i, "date" ).substring(6)+"-"+dbr.getString(i, "date" ).substring(4,6)+"-"+dbr.getString(i, "date" ).substring(0,4);
                    
                    
                    record.add( new JsonPrimitive( " ddt N° " + dbr.getInteger( i, "number" ) + " del " + date ) );
                }
                else
                    record.add( new JsonPrimitive(""));
                //3
                record.add( new JsonPrimitive( dbr.getInteger( i, "quantity" )));
                //4
                record.add( new JsonPrimitive( dbr.getDouble( i, "singleAmount" )));
                //5
                record.add( new JsonPrimitive( dbr.getDouble( i, "totalAmount" )));
                //6
                if( dbr.getLong( i, "deliveryNoteRow_id" ) != null )
                    record.add( new JsonPrimitive( dbr.getLong( i, "deliveryNoteRow_id" )));
                else
                    record.add( new JsonPrimitive(""));
                                
                rows.add(record);
            }
            
            jsonResponse.add("rows", rows);
            
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    private void updateInvoice( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute( "user_id" ) != null )
        {
            Invoice invoice = null;
            try
            {
                String s = bodyString(request);
                JsonObject jsonInvoice = JSON.parse(s)/*JSON.parse(request.getParameter("invoice"))*/;
                invoice = new Invoice(jsonInvoice);
            } catch(RuntimeException ex){}

            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");

            Long invoice_id = dao.updateInvoice(invoice);

            jsonResponse.addProperty("success",true);
            
            jsonResponse.addProperty("invoice_id",invoice_id);
        }
        
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    private void printInvoice( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // reads operation parameters
            Long invoice_id = Long.parseLong( request.getParameter( "invoice_id" )) ;
            
            //intialize and call pdf printer that puts the pdf file in the destination folder 
            try{
                //creates pdf
                InvoicePdfPrinter_1 pdfPrinter = new InvoicePdfPrinter_1();
                pdfPrinter.printInvoice(invoice_id );
                
                jsonResponse.addProperty("success",true);
            }catch( Exception ex ){ ex.printStackTrace(); jsonResponse.addProperty("success",false);}
              
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    private void getXML( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException, ClassNotFoundException, ParseException, ParserConfigurationException, TransformerException
    {
        
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // reads operation parameters
            Long invoice_id = Long.parseLong( request.getParameter( "invoice_id" )) ;
            
            //build the destination file name IT+DuesseVatCode+_+invoiceNumber
            String destFolder = Config.DIGITAL_INVOICES_DIR;
            
            try{
                //calls method to get the xml file
                new InvoiceXMLPrinter_1().createXML(invoice_id, destFolder);
                jsonResponse.addProperty("success",true);
            }
            catch( XmlNodeException ex )
            {
                jsonResponse.addProperty("success",false);
                jsonResponse.addProperty("message",ex.getMessage());
            }
            
            
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    private void updateInvoiceDate( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException, ClassNotFoundException, ParseException, ParserConfigurationException, TransformerException
    {
        
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // reads operation parameters
            Long number = Long.parseLong( request.getParameter( "number" )) ;
            
            // reads operation parameters
            Integer year = Integer.parseInt(request.getParameter( "year" )) ;
            
            String date = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.parse(request.getParameter( "date" )));
            
            Boolean done = dao.updateInvoiceDate(number, year, date);

            jsonResponse.addProperty("success",done);
            
            
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    
    
    //QUOTES
    
    private void createQuote( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute( "user_id" ) != null )
        {
            Quote newQuote = null;
            try
            {
                String s = bodyString(request);
                JsonObject jsonQuote = JSON.parse(s)/*JSON.parse(request.getParameter("invoice"))*/;
                newQuote = new Quote(jsonQuote);
            } catch(RuntimeException ex){}

            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");

            Quote quote = dao.createQuote(newQuote);
            
            jsonResponse.addProperty("success",true);
            
            //creates the quote Json object by static method in quote java class
            JsonObject jsonQuote = Quote.getJson( quote );

            jsonResponse.add("quote",jsonQuote);
        }
        
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    private void readQuotes( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        /*ask for a session, if it doesn't exist, it won't be created. 
        Note that this mechanism of session checking avoids malware attempts
        to enter the system without executing log-in procedure*/
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // reads operation parameters
            Long quote_id = null;
            try{ quote_id = Long.parseLong( request.getParameter( "quote_id" ) );}
            catch( NumberFormatException Nex){}
            
            // reads operation parameters
            Long customer_id = null;
            try{ customer_id = Long.parseLong( request.getParameter( "customer_id" ) );}
            catch( NumberFormatException Nex){}
            
            String number = request.getParameter( "number" );
            
            LocalDate fromDate = request.getParameter( "fromDate" ) == null || request.getParameter( "fromDate" ).equals("")  ? null : LocalDate.parse( request.getParameter( "fromDate" ) );
            
            LocalDate toDate = request.getParameter( "toDate" ) == null || request.getParameter( "toDate" ).equals("") ? null : LocalDate.parse( request.getParameter( "toDate" ) );
                               
            // asks DB for quotes
            List<Quote> quotes = dao.readQuotes( quote_id, customer_id, number,  fromDate, toDate );
            
            /*DbResult.defaultJsonMode = JsonMode.LIST_OF_OBJECTS; this is by Gianluca*/

            JsonArray jsonQuotes = new JsonArray();
            
            for( int i = 0; i < quotes.size(); i++)
            {
                JsonObject json = Quote.getJson( quotes.get(i) );
                
                jsonQuotes.add(json);
            } 
            
            jsonResponse.addProperty("success",true);
            
            jsonResponse.add("quotes",jsonQuotes);
            
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    
    private void readQuoteRows( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        
        /*ask for a session, if it doesn't exist, it won't be created. 
        Note that this mechanism of session checking avoids malware attempts
        to enter the system without executing log-in procedure*/
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // reads operation parameters
            Long quote_id = null;
            try{ quote_id = Long.parseLong( request.getParameter( "quote_id" ) );}
            catch( NumberFormatException Nex){}
                         
            // asks DB for quote
           DbResult dbr = dao.readQuoteRows( quote_id );
          
            jsonResponse.addProperty("success",true);
            
            JsonArray rows = new JsonArray();
            
            for(int i=0; i<dbr.rowsCount(); i++)
            {
                JsonArray record = new JsonArray();
                
                //0
                record.add( new JsonPrimitive( dbr.getString( i, "description" )));
                
                //1
                record.add( new JsonPrimitive( dbr.getDouble( i, "rowAmount" )));
                                
                rows.add(record);
            }
            
            jsonResponse.add("rows", rows);
            
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    private void updateQuote( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {

        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute( "user_id" ) != null )
        {
            Quote quote = null;
            try
            {
                String s = bodyString(request);
                JsonObject jsonQuote = JSON.parse(s);
                quote = new Quote(jsonQuote);
            } catch(RuntimeException ex){}

            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");

            dao.updateQuote(quote);
            
            //creates the quote Json object by static method in quote java class
            JsonObject jsonQuote = Quote.getJson( quote );

            jsonResponse.add("quote",jsonQuote);

            jsonResponse.addProperty("success",true);
        }
        
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    
    private void createQuoteAttachment( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException, NumberFormatException, ServletException, IOException
    {
        HttpSession session = request.getSession(false);
        
        String UPLOAD_DIR = Config.QUOTES_ATTACH_DIR;
        
        DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
        
        String originalFileName = "";
        String destFileName;
        
        if( session.getAttribute( "user_id" ) != null )
        {
            Long quote_id = Long.parseLong(request.getParameter("quote_id"));
            Long user_id = Long.parseLong( session.getAttribute( "user_id" ).toString());
            
            try
            {
                //gets original file name
                for(Part part : request.getParts())
                {
                    originalFileName = new String(part.getSubmittedFileName().getBytes(),StandardCharsets.UTF_8); 
                }
                //creates the attachment in DB
                Long quoteAttachment_id = dao.createQuoteAttachment(quote_id, user_id, originalFileName);
                
                //initializes the destination file name
                destFileName =  quoteAttachment_id + "_" + originalFileName;
                
                //sets the destination file name in DB
                Boolean updated = dao.setQuoteAttachmentName( originalFileName, destFileName, quoteAttachment_id);
                
                //uploads the file
                for(Part part : request.getParts())
                {
                    part.write(UPLOAD_DIR+destFileName);
                }
                
                //merges pdf
                new PdfQuotePrinter().mergePdf(quote_id );
                
                jsonResponse.addProperty("success",updated);
                jsonResponse.addProperty("message","UPLOADED");
            }
            catch( IOException | ClassNotFoundException | SQLException ex  )
            {
                jsonResponse.addProperty("success",false);
                jsonResponse.addProperty("message","EXCEPTION : "+ex);
            }
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    
    private void deleteQuoteAttachment( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException,ClassNotFoundException,IOException
    {
        /*ask for a session, if it doesn't exist, it won't be created. 
        Note that this mechanism of session checking avoids malware attempts
        to enter the system without executing log-in procedure*/
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            // reads session data
            Long attachment_id = Long.parseLong(request.getParameter("attachment_id"));
            Long quote_id = Long.parseLong(request.getParameter("quote_id"));
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            dao.deleteQuoteAttachment( attachment_id );
            
            //merges pdf
            new PdfQuotePrinter().mergePdf(quote_id );
            
            jsonResponse.addProperty("success",true);
        }  
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    private void readQuoteAttachments( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        /*ask for a session, if it doesn't exist, it won't be created. 
        Note that this mechanism of session checking avoids malware attempts
        to enter the system without executing log-in procedure*/
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE;
            
            // reads operation parameters
            Long quote_id = null;
            try{ quote_id = Long.parseLong( request.getParameter( "quote_id" ) );}
            catch( NumberFormatException Nex){}
            
            //If quote_id is 0 retrieves attachments having task_id = null
            if( quote_id == 0L)
                quote_id = null;
            
            Long user_id = Long.parseLong(session.getAttribute("user_id").toString());
            
            // asks DB for 
            DbResult dbr = dao.readQuoteAttachments( quote_id, user_id );
            
            jsonResponse.addProperty("success",true);
            
            JsonArray quoteAttachments = new JsonArray();
            
            for(int i=0; i<dbr.rowsCount(); i++)
            {
                JsonArray record = new JsonArray();
                
                //0
                record.add( new JsonPrimitive( dbr.getLong(i, "quoteAttachment_id" )));

                //1
                record.add( new JsonPrimitive( dbr.getString( i,"currentFileName" )));

                //2
                record.add( new JsonPrimitive( dbr.getLong( i, "quote_id" )));
                
                //3
                record.add( new JsonPrimitive( dtf.format( LocalDate.parse(dbr.getDate( i, "date" ).toString().substring(0,10)))));
                
                //4
                record.add( new JsonPrimitive( dbr.getString( i,"originalFileName" )));
                
                quoteAttachments.add(record);

            }
            
            jsonResponse.add("quoteAttachments", quoteAttachments);

            jsonResponse.addProperty("success",true); 
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    private void printQuote( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // reads operation parameters
            Long quote_id = Long.parseLong( request.getParameter( "quote_id" )) ;
            
            //intialize and call pdf printer that puts the pdf file in the destination folder 
            try{
                //creates pdf
                PdfQuotePrinter pdfPrinter = new PdfQuotePrinter();
                pdfPrinter.printQuote(quote_id );
                pdfPrinter.mergePdf(quote_id);
                
                jsonResponse.addProperty("success",true);
            }catch( Exception ex ){ ex.printStackTrace(); jsonResponse.addProperty("success",false);}
              
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    //COMPLIANCE CERTIFICATES
    private void createCertificate( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException, ClassNotFoundException , IOException
    {
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute( "user_id" ) != null )
        {
            ComplianceCertificate newCertificate = null;
            try
            {
                JsonObject jsonCertificate = JSON.parse(request.getParameter("newCertificate"));
                newCertificate = new ComplianceCertificate(jsonCertificate);
            } catch(RuntimeException ex){}
            
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            //DB writing
            ComplianceCertificate certificate = dao.createComplianceCertificate(newCertificate);

            jsonResponse.addProperty("success",true);
            
            //creates the pdf
            ComplianceCertificatePdfPrinter.printComplianceCertificate(certificate.order_id);
            
            //creates the quote Json object by static method in quote java class
            JsonObject jsonCertificate = ComplianceCertificate.getJson( certificate );

            jsonResponse.add("certificate",jsonCertificate);
        }
        
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    private void readCertificate( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        /*ask for a session, if it doesn't exist, it won't be created. 
        Note that this mechanism of session checking avoids malware attempts
        to enter the system without executing log-in procedure*/
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // reads operation parameters
            Long cert_id = null;
            try{ cert_id = Long.parseLong( request.getParameter( "cert_id" ) );}
            catch( NumberFormatException Nex){}
                           
            // asks DB for certificate
            ComplianceCertificate compcert = dao.readComplianceCertificate( cert_id );
    
            JsonObject jsonCert = ComplianceCertificate.getJson(compcert);
            
            jsonResponse.addProperty("success",true);
            
            jsonResponse.add("certificate",jsonCert);
            
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    private void updateCertificate( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException, ClassNotFoundException, IOException
    {

        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute( "user_id" ) != null )
        {
            ComplianceCertificate certificate = null;
            
            JsonObject jsonCert = null;
            
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            try
            {
                jsonCert = JSON.parse(request.getParameter("certificate"));
                
                //becouse of from client side not come all properties first buid the old object 
                //then updates it and finally passes it to the dao for updating the DB
               
                certificate = new ComplianceCertificate( dao.readComplianceCertificateDbr(JSON.getLong(jsonCert, "order_id")));
                
            } catch(RuntimeException ex){}
            
            /* from client side we get:
                cert_id;
                order_id;
                firstTitle_id;
                secondTitle_id;
                firstForAttention;
                secondForAttention;
                orderDescription;
                customerJobOrderCode;
            they are not enough to buil the certificate POJO 
            so  remaining properties must be assigned.
            Then retrieves the related resultset z
            */
            certificate.firstTitle_id = JSON.getLong(jsonCert, "firstTitle_id");
            certificate.secondTitle_id = JSON.getLong(jsonCert, "secondTitle_id");
            certificate.firstTitle = certificate.secondTitle_id == null ? "" : dao.getTitleName(JSON.getLong(jsonCert, "firstTitle_id"));
            certificate.secondTitle = certificate.secondTitle_id == null ? "" : dao.getTitleName(JSON.getLong(jsonCert, "secondTitle_id"));
            certificate.firstForAttention = JSON.getString(jsonCert, "firstForAttention");
            certificate.secondForAttention = JSON.getString(jsonCert, "secondForAttention");
            certificate.orderDescription = JSON.getString(jsonCert, "orderDescription");
            certificate.customerJobOrderCode = JSON.getString(jsonCert, "customerJobOrderCode");
            
            //now the object is ready to be passed to the DB
            
            dao.updateComplianceCertificate(certificate);
            
            //creates the quote Json object by static method in quote java class
            JsonObject jsonCertificate = ComplianceCertificate.getJson( certificate );
            
            //creates the pdf
            ComplianceCertificatePdfPrinter.printComplianceCertificate(certificate.order_id);

            jsonResponse.add("certificate",jsonCertificate);

            jsonResponse.addProperty("success",true);
        }
        
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    //CREDIT NOTES
    
    private void createCreditNote( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute( "user_id" ) != null )
        {
            CreditNote newCreditNote = null;
            try
            {
                String s = bodyString(request);
                JsonObject jsonCreditNote = JSON.parse(s);
                newCreditNote = new CreditNote(jsonCreditNote);
            } catch(RuntimeException ex){}

            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");

            DbResult creditNote_dbr = dao.createCreditNote(newCreditNote);

            jsonResponse.addProperty("success",true);
            
            //retrieves invoice rows
            DbResult dbr_rows = dao.readCreditNoteRows(creditNote_dbr.getLong("creditNote_id"));
            
            //creates the invoice Json object by static method in invoice java class
            JsonObject jsonCreditNote = CreditNote.getJsonByDbResults( creditNote_dbr, dbr_rows );

            jsonResponse.add("creditNote",jsonCreditNote);
        }
        
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    private void readCreditNotes( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        /*ask for a session, if it doesn't exist, it won't be created. 
        Note that this mechanism of session checking avoids malware attempts
        to enter the system without executing log-in procedure*/
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // reads operation parameters
            Long creditNote_id = null;
            try{ creditNote_id = Long.parseLong( request.getParameter( "creditNote_id" ) );}
            catch( NumberFormatException Nex){}
            
            // reads operation parameters
            Long customer_id = null;
            try{ customer_id = Long.parseLong( request.getParameter( "customer_id" ) );}
            catch( NumberFormatException Nex){}
            
            Integer number = null;
            try{ number = Integer.parseInt( request.getParameter( "number" ) );}
            catch( NumberFormatException Nex){}
                      
            String fromDate = request.getParameter( "fromDate" );
            
            String toDate = request.getParameter( "toDate" );
                               
            long start = new Date().getTime();
            System.out.println("READING CREDIT NOTE [Gate.readInvoices]");
            
            // asks DB for credit note
            DbResult creditNotes_dbr = dao.readCreditNotes( creditNote_id, customer_id, number,  fromDate, toDate );
            System.out.println("CREDIT NOTES READ [DataAccessObject.readCreditNotes] : elapsed msec "+(new Date().getTime()-start));
           
            start = new Date().getTime();
            
            //gets all invoices rows
            DbResult creditNotesRows_dbr = dao.readCreditNoteRows( creditNotes_dbr.getColumnData("creditNote_id") );
            System.out.println("CREDIT NOTE ROWS READ [DataAccessObject.readCreditNotes] : elapsed msec "+(new Date().getTime()-start));
          
            jsonResponse.addProperty("success",true);
            
            //invoices Json array
            JsonArray creditNotes = new JsonArray();
            
            start = new Date().getTime();
            
            //for each creditNote
            for( int i = 0; i < creditNotes_dbr.rowsCount(); i++)
            {
                //creates an array list for invoice rows
                List<CreditNote.Item> items = new ArrayList<>();
                
                //creates an Invoice instance to initialize the nested class item
                CreditNote creditNote = new CreditNote();
                
                //gets only invioce rows belonging to the current invoice
                for( int j = 0; j < creditNotesRows_dbr.rowsCount(); j++ )
                {
                    if( creditNotes_dbr.getLong( i, "creditNote_id").equals( creditNotesRows_dbr.getLong( j, "creditNote_id") ) )
                    {
                        CreditNote.Item item;
                        item = creditNote.new Item( creditNotesRows_dbr, j  );
                        items.add(item);
                    }
                }
                
                 //creates the invoice Json object by static method in Invoice java class
                JsonObject jsonCreditNote = CreditNote.getJsonByDbResults(creditNotes_dbr, i, items );
                
                creditNotes.add(jsonCreditNote);
            }
            
            System.out.println("JSON CREDIT NOTES BUILD [DataAccessObject.readCreditNotes] : elapsed msec "+(new Date().getTime()-start));
            
            jsonResponse.add("creditNotes", creditNotes);
            
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    private void readCreditNoteRows( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        
        /*ask for a session, if it doesn't exist, it won't be created. 
        Note that this mechanism of session checking avoids malware attempts
        to enter the system without executing log-in procedure*/
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // reads operation parameters
            Long creditNote_id = null;
            try{ creditNote_id = Long.parseLong( request.getParameter( "creditNote_id" ) );}
            catch( NumberFormatException Nex){}
                               
            // asks DB for invoce
           DbResult dbr = dao.readCreditNoteRows( creditNote_id );
          
            jsonResponse.addProperty("success",true);
            
            JsonArray rows = new JsonArray();
            
            for(int i=0; i<dbr.rowsCount(); i++)
            {
                JsonArray record = new JsonArray();
                
                //0
                record.add( new JsonPrimitive( dbr.getInteger( i, "quantity" )));
                
                //1
                record.add( new JsonPrimitive( dbr.getString( i, "description" )));
                
                //2
                record.add( new JsonPrimitive( dbr.getDouble( i, "singleAmount" )));
                
                //3
                record.add( new JsonPrimitive( dbr.getDouble( i, "totalAmount" )));
                
                //4
                if( dbr.getLong( i, "creditNote_id" ) != null )
                    record.add( new JsonPrimitive( dbr.getLong( i, "creditNote_id" )));
                else
                    record.add( new JsonPrimitive(""));
                                
                rows.add(record);
            }
            
            jsonResponse.add("rows", rows);
            
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    private void updateCreditNote( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute( "user_id" ) != null )
        {
            CreditNote creditNote = null;
            try
            {
                String s = bodyString(request);
                JsonObject jsonCreditNote = JSON.parse(s)/*JSON.parse(request.getParameter("invoice"))*/;
                creditNote = new CreditNote(jsonCreditNote);
            } catch(RuntimeException ex){}

            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");

            Long creditNote_id = dao.updateCreditNote(creditNote);

            jsonResponse.addProperty("success",true);
            
            jsonResponse.addProperty("creditNote_id",creditNote_id);
        }
        
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    /*to be implemented*/
    private void printCreditNote( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // reads operation parameters
            Long creditNote_id = Long.parseLong( request.getParameter( "creditNote_id" )) ;
            
            //intialize and call pdf printer that puts the pdf file in the destination folder 
            try{
                //creates pdf
                CreditNotePdfPrinter pdfPrinter = new CreditNotePdfPrinter();
                pdfPrinter.printCreditNote(creditNote_id );
                
                jsonResponse.addProperty("success",true);
            }catch( Exception ex ){ ex.printStackTrace(); jsonResponse.addProperty("success",false);}
              
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    /*to be implemented*/
    private void getCreditNoteXML( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException, ClassNotFoundException, ParseException, ParserConfigurationException, TransformerException
    {
        
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            // reads operation parameters
            Long creditNote_id = Long.parseLong( request.getParameter( "creditNote_id" )) ;
            
            //build the destination file name IT+DuesseVatCode+_+invoiceNumber
            String destFolder = Config.DIGITAL_CREDIT_NOTES_DIR;
            
            try{
                //calls method to get the xml file
                new CreditNoteXMLPrinter().createXML(creditNote_id, destFolder);
                jsonResponse.addProperty("success",true);
            }
            catch( XmlNodeException ex )
            {
                jsonResponse.addProperty("success",false);
                jsonResponse.addProperty("message",ex.getMessage());
            }
            
            
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    //AMOUNT SCHEDULE DATE
    
    private void readAmountScheduleDates( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        /*ask for a session, if it doesn't exist, it won't be created. 
        Note that this mechanism of session checking avoids malware attempts
        to enter the system without executing log-in procedure*/
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            
            // reads operation parameters
            Long customer_id = null;
            try{ customer_id = Long.parseLong( request.getParameter( "customer_id" ) );}
            catch( NumberFormatException Nex){}
            
            String fromDate = request.getParameter( "fromDate" ) == null || request.getParameter( "fromDate" ).equals("")  ? null : request.getParameter( "fromDate" ).replace("-","") ;
            
            String toDate = request.getParameter( "toDate" ) == null || request.getParameter( "toDate" ).equals("") ? null :  request.getParameter( "toDate" ).replace("-","") ;
                               
            // asks DB for quotes
            List<AmountSchedule> amountSchedules = dao.readAmountSchedules( customer_id,  fromDate, toDate );
            
            /*DbResult.defaultJsonMode = JsonMode.LIST_OF_OBJECTS; this is by Gianluca*/

            JsonArray jsonAmountSchedules = new JsonArray();
            
            for( int i = 0; i < amountSchedules.size(); i++)
            {
                JsonObject json = AmountSchedule.getJson( amountSchedules.get(i) );
                
                jsonAmountSchedules.add(json);
            } 
            
            jsonResponse.addProperty("success",true);
            
            jsonResponse.add("amountSchedules",jsonAmountSchedules);
            
        }
        else
        {
            jsonResponse.addProperty("success",false);
            jsonResponse.addProperty("message","not authenticated");
        }
    }
    
    /**
     * To be called once befor deploying in production the version
     * it populates the amount schedule DB table with all invoices data
     * at the moment of the execution of the method
     * @param request
     * @param jsonResponse
     * @throws SQLException 
     */
    private void fillAmountScheduleDates( HttpServletRequest request, JsonObject jsonResponse ) throws SQLException
    {
        HttpSession session = request.getSession(false);
        
        if( session != null && session.getAttribute("user_id") != null )
        {
            // reads session data
            DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
            
            //reads all invoices
            DbResult invoces_dbr = dao.readInvoices(null,null, null, null, null);
            
            jsonResponse.addProperty("success",true);
            
          
            
            //prints the statement to excute on managment studuio
            String sql = "";
            Long invoice_id = 0L;
            for( int i = 0; i < invoces_dbr.rowsCount(); i++ )
            {
                invoice_id = invoces_dbr.getLong(i,"invoice_id");
                sql += " INSERT INTO dyn_AmountScheduleDates ( invoice_id, customer_id, ordinal, amount, amountDate ) " +
                       "SELECT invoice_id, customer_id, 1, firstAmount, firstAmountDate FROM dyn_Invoices " + 
                        "WHERE invoice_id = " + invoice_id;
                sql += " INSERT INTO dyn_AmountScheduleDates ( invoice_id, customer_id, ordinal, amount, amountDate ) " +
                       "SELECT invoice_id, customer_id, 2, secondAmount, secondAmountDate FROM dyn_Invoices "+ 
                        "WHERE invoice_id = " + invoice_id;
                sql += " INSERT INTO dyn_AmountScheduleDates ( invoice_id, customer_id, ordinal, amount, amountDate ) " +
                       "SELECT invoice_id, customer_id, 3, thirdAmount, thirdAmountDate FROM dyn_Invoices "+ 
                        "WHERE invoice_id = " + invoice_id;
            }
            
            jsonResponse.addProperty("query",sql);
            
        }
        
    
    }
    
}
   
