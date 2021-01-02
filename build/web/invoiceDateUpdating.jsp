<%@page import="java.util.Date"%>
<%@page import="com.dalessio.samurai.Quote"%>
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
    DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
    
    Long timestamp = new Date().getTime();
    
    Long user_id = (Long) request.getSession().getAttribute("user_id");
    
    String amount = "";
    
    String number = "";
   
    if(user_id==null){
%>

    <script>window.open("/Samurai/landing.jsp","_self")</script>
    
<%}else
{
    String date = LocalDate.now().toString();
    
    DbResult dbr_user = dao.readUsers( user_id);

    String user_role = dbr_user.getString("role");

    String userName = dbr_user.getString("firstName") + " " + dbr_user.getString("lastName");

    DbResult dbr_customers = dao.readAllCustomers();

%>

<html>
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>Workline 2.0</title>
            <link href="/Samurai/common.css" rel="stylesheet">
            <link href="/Samurai/invoices.css" rel="stylesheet">
            <link href="/Samurai/Resources/font-awesome-4.7.0/css/font-awesome.min.css" rel="stylesheet">
        </head>
        <body>
        
            <div class="Header">
                <span class="SWName">Workline 2.0</span><span class="PageTitle"> Cambio Data Fattura Salvata </span>
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
                <span class=" Button" onclick="window.open('invoice_details.jsp','_self');">FATTURE</span>
                <span class=" Button" onclick="window.open('quote_details.jsp','_self');">PREVENTIVI</span>
                <%if( "admin".equals(user_role) ){%><span class=" Button" onclick="window.open('creditNote_details.jsp','_self');">NOTE ACCREDITO</span><%}%>
            </div>
            
            <!-- FILTERS SECTION -->
            <div class="Filters" >
                
                <div id="dateUpdateConfirmedButton" class="Button Operation Confirm" onclick="app.changeInvoiceDate()" >CONFERMA</div>
                
            </div>
        <!-- PAGE CONTENT -->
        <div class="Content WithFilter">
            <div id="dateUpdateBodyHeader"> ATTENZIONE:</div>
            <div id="dateUpdateTableInfo">Il cambio di data di una fattura salvata, potrebbe corrompere la sequenza temporale dei documenti</div>
            <table class="Details_table">

                <tr class="Details_row ">
                    <th class="Detail_header Input" id="dateToUpdate">NUOVA DATA</th>
                    <td id="dateNumberRow">
                        <span>
                            <input type="date" id="date_input" value= "<%=date%>"  >
                        </span>
                    </td> 
                </tr>
                <tr class="Details_row ">
                    <th class="Detail_header Input" id="date">NUMERO FATTURA</th>
                    <td id="dateNumberRow">
                        <span>
                            <input type="text" id="number_input" value= "">
                        </span>
                    </td> 
                </tr>
                
            </table>
        </div>

        <div class="Footer">
            <span class="User_name"> Utente : <%=userName%></span>
            <span class="Footer_message"></span>
        </div>

        </body>
       
        <!-- sezione script -->
        <script src="quoteElasticTable.js?<%=timestamp%>"></script>
        <script src="/Samurai/dbi.js?<%=timestamp%>"></script>
        <script src="/Samurai/invoices.js?<%=timestamp%>"></script>
        <script src="/Samurai/dropDown.js?<%=timestamp%>"></script> 
        <script>setInterval(function(){app.ping();},60000);</script>
        <script>input = document.querySelector(".RowAmount");
                input.addEventListener("mousewheel", function(evt){ evt.preventDefault(); });
        </script>
        
    </html>

<%}%>