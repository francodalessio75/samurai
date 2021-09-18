<%@page import="java.util.Date"%>
<%@page import="com.dalessio.samurai.ComplianceCertificate"%>
<%@page import="com.dps.dbi.DbResult"%>
<%@page import="com.dps.dbi.DbResultsBundle"%>
<%@page import="com.dalessio.samurai.DataAccessObject"%>
<%@page import="java.time.LocalDate"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.math.BigDecimal"%>
<%@page import="java.math.RoundingMode"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<%
    Long timestamp = new Date().getTime();
    
    Long user_id = (Long) request.getSession().getAttribute("user_id");
    
    String number = "";
   
    if(user_id==null){
%>

    <script>window.open("/Samurai/landing.jsp","_self")</script>
    
<%}else
{
    /* The order view contains all data of interest also about compliance certificate*/
    DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
    
    ComplianceCertificate certificate = null;

    DbResult dbr_titles = dao.readTitles(null);

    Long order_id = 0L;

    Long cert_id = null;
    
    String date = "";//DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDate.now());
    
    //confirm button text
    String confirmButtonText = "CONFERMA CREAZIONE";

    //if invoice_id is not null invoice details must be shown in the page
    try{
        order_id = Long.parseLong(request.getParameter("order_id"));
    }catch(NumberFormatException ex ){}

    DbResult orderViewDbr = dao.readOrder(order_id);
        
    DbResult certDbr = dao.readComplianceCertificateDbr(order_id);

    if( certDbr.rowsCount() > 0 )
        cert_id = certDbr.getLong("complianceCertificate_id");
    
    //the page is requested by clicking on a quotes table row in quotes.jsp page
    if( cert_id != null )
    { 
        certificate = dao.readComplianceCertificate(cert_id);
         
        date = DateTimeFormatter.ISO_LOCAL_DATE.format(certificate.date);

        number = certificate.number +"-"+date.substring(2,4);

        confirmButtonText = "CONFERMA MODIFICHE";
    }
    //sets the taskdate to current day if task_id is null
    else
        date = LocalDate.now().toString();
    
    DbResult dbr_user = dao.readUsers( user_id);
    String user_role = dbr_user.getString("role");
    String userName = dbr_user.getString("firstName") + " " + dbr_user.getString("lastName");

%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Workline 2.0</title>
        <link href="/Samurai/common.css" rel="stylesheet">
        <link href="/Samurai/complianceCertificate.css" rel="stylesheet">
        <link href="/Samurai/Resources/font-awesome-4.7.0/css/font-awesome.min.css" rel="stylesheet">
    </head>
    <body>

        <div class="Header">
            <span class="SWName">Workline 2.0</span><span class="PageTitle"> MODULO RICHIESTA CONFORMITA' <%=orderViewDbr.getString("customerDenomination")%> </span>
        </div>

        <div class="Dropdown_registry">
            <button onmouseover="app.showRegistry()" onmouseout="app.hideRegistry()" class="DropBtn Registry">ANAGRAFICHE</button>
            <div id="myDropdown_registry" class="Dropdown_content Registry">
                <div onmouseover="app.showRegistry()" onmouseout="app.hideRegistry()" onclick="window.open('customers.jsp','_self');">ANAGRAFICA CLIENTI</div>
                <div onmouseover="app.showRegistry()" onmouseout="app.hideRegistry()" onclick="window.open('translationsCenters.jsp','_self');">CENTRI TRADUZIONE</div>
                <div onmouseover="app.showRegistry()" onmouseout="app.hideRegistry()" onclick="window.open('transporters.jsp','_self');">VETTORI</div>
                <%if( "admin".equals(user_role) ){%><div onmouseover="app.showRegistry()" onmouseout="app.hideRegistry()" onclick="window.open('users.jsp','_self');">OPERATORI</div><%}%>
                <%if( "admin".equals(user_role) ){%><div onmouseover="app.showRegistry()" onmouseout="app.hideRegistry()" onclick="window.open('jobTypes.jsp','_self');">TIPI LAVORO</div><%}%>
                <%if( "admin".equals(user_role) ){%><div onmouseover="app.showRegistry()" onmouseout="app.hideRegistry()" onclick="window.open('jobSubtypes.jsp','_self');">TIPI LAVORAZIONE</div><%}%>
            </div>
        </div>

        <div class="Dropdown_tools">
            <button onmouseover="app.showTools()" onmouseout="app.hideTools()" class="DropBtn Tools">STRUMENTI</button>
            <div id="myDropdown_tools" class="Dropdown_content Tools">
                <div onmouseover="app.showTools()" onmouseout="app.hideTools()" onclick="window.open('changeCredentials.jsp','_self');">CAMBIA PASSWORD</div>
                <%if( "admin".equals(user_role) ){%><div onmouseover="app.showTools()" onmouseout="app.hideTools()" onclick="window.open('invoiceDateUpdating.jsp','_self');">CAMBIA DATA FATTURA</div><%}%>
                <%if( "admin".equals(user_role) ){%><div onmouseover="app.showTools()" onmouseout="app.hideTools()" onclick="window.open('scheduleDates.jsp','_self');">SCADENZE</div><%}%>
                <div onmouseover="app.showTools()" onmouseout="app.hideTools()" onclick="window.open('landing.jsp','_self');">LOG-OUT</div>
            </div>
        </div>
            
        <div class="Menu">
            <span class=" Button" onclick="window.open('orders.jsp','_self');">ELENCO LAVORI</span>
            <span class=" Button" onclick="window.open('task_details.jsp','_self');">ORE LAVORI</span>
            <span class=" Button" onclick="window.open('deliveryNote_details.jsp','_self');">DDT</span>
            <%if( "admin".equals(user_role) ){%><span class=" Button" onclick="window.open('invoice_details.jsp','_self');">FATTURE</span><%}%>
            <%if( "admin".equals(user_role) ){%><span class=" Button" onclick="window.open('quote_details.jsp','_self');">PREVENTIVI</span><%}%>
            <%if( "admin".equals(user_role) ){%><span class=" Button" onclick="window.open('creditNote_details.jsp','_self');">NOTE ACCREDITO</span><%}%>
        </div>
          
        <!-- FILTERS SECTION -->
        <div class="Filters" >

            <div id="confirm_label" class="Filters_label"> OPERAZIONI</div>
            <div id="certificateConfirmedButton" class="Button Operation Confirm" onclick="app.certificateConfirmed(<%=cert_id%>)" ><%=confirmButtonText%></div>
            <% if ( certificate != null ){%>
                <div id="pdfButton" class="Button Operation" onclick="window.open('resources/ComplianceCertificates/MODULO_CONFORMITA_LAVORO_<%=certificate.orderCode%>.pdf','_blank');">APRI PDF</div>
                
            <%}%>  

        </div>    
            
        <!-- PAGE CONTENT -->
        <div class="Content WithFilter">
            <table class="Details_table">

                <tr class="Details_row ">
                    <th class="Detail_header Input" id="certificate" data-order_id="<%=order_id%>" data-cert_id="<%=cert_id%>">
                            MODULO CONFORMITA'  
                    </th>
                    <td id="dateNumberRow">
                        <span class="TableLable">Data:</span>
                        <span>
                            <input type="date" id="date_input" value= "<%=date%>"  disabled>
                        </span>
                    <%if( certificate != null ){%>
                        <span class="TableLable">Numero:</span>
                        <span>
                            <input type="text" id="number_input" value= "<%=number%>" disabled>
                        </span>
                    <%}%>    
                    </td> 
                </tr>

                <tr class="Details_row">
                    <th class="Detail_header Input" id="forAttention">
                        ALLA CORTESE ATTENZIONE
                    </th>
                    <td id="forAttentionRow">
                        <span>
                            <select id="firstTitle_select_options" onchange="app.quoteDetailsChanged('firstTitle_select_options');" >
                                    <%for(int i=0; i<dbr_titles.rowsCount(); i++){%>
                                    <option value="<%=dbr_titles.getLong(i,"title_id")%>" ><%=dbr_titles.getString(i,"title")%></option>
                                    <%}%>
                            </select>
                        </span>
                        <input   id="firstForAttention_input" onchange="app.quoteDetailsChanged('firstForAttention_input');" value="<%=certificate == null ? "" : certificate.firstForAttention %>">
                        <span>
                            <select id="secondTitle_select_options" onchange="app.quoteDetailsChanged('secondTitle_select_options');" >
                                    <%for(int i=0; i<dbr_titles.rowsCount(); i++){%>
                                    <option value="<%=dbr_titles.getLong(i,"title_id")%>" ><%=dbr_titles.getString(i,"title")%></option>
                                    <%}%>
                            </select>
                        </span>
                        <input   id="secondForAttention_input" onchange="app.quoteDetailsChanged('secondForAttention_input');" value="<%=certificate == null ? "" : certificate.secondForAttention %>">
                        <script>document.getElementById("firstTitle_select_options").value = "<%= certificate != null ? certificate.firstTitle_id : "" %>"</script>
                        <script>document.getElementById("secondTitle_select_options").value = "<%= certificate != null ? certificate.secondTitle_id : "" %>"</script>
                    </td>
                </tr>
               
                <tr class="Details_row">
                    <th class="Detail_header Input" id="orderCode">
                        CODICE LAVORO:
                    </th>
                    <td id="subjectRow">
                        <input id="orderCode_input"  value="<%=orderViewDbr.getString("code")%>" disabled>
                    </td>
                </tr>
                
                <tr class="Details_row">
                    <th class="Detail_header Input" id="customerOrderCode">
                        CODICE LAVORO CLIENTE:
                    </th>
                    <td id="subjectRow">
                        <input id="customerOrderCode_input" onchange="app.quoteDetailsChanged('customerOrderCode_input');" value="<%= certificate != null ? certificate.customerJobOrderCode : "" %>" >
                    </td>
                </tr>
                
                <!-- Look unfortunally becouse of Paolo orderDescription propertu name is machinaryModel :( -->
                <tr class="Details_row ">
                        <th class="Detail_header Input" id="orderDescription" rows="4" cols="50">DESCRIZIONE LAVORO</th>
                        <td><textarea id="orderDescription_input"  onchange="app.detailsChanged('orderDescription_input');"><%= certificate != null ? certificate.orderDescription : orderViewDbr.getString("machinaryModel")%></textarea></td>
                    </tr>
                
            </table>
                    
            </div>
            
            <div class="Footer">
                <span class="User_name"> Utente : <%=userName%></span>
                <span class="Footer_message"></span>
            </div>

        </body>
             
        <!-- sezione script -->
        <script src="/Samurai/dbi.js?<%=timestamp%>"></script>
        <script src="/Samurai/complianceCertificates.js?<%=timestamp%>"></script>
        <script src="/Samurai/dropDown.js?<%=timestamp%>"></script> 
        <script>setInterval(function(){app.ping();},60000);</script>
        
    </html>

<%}%>