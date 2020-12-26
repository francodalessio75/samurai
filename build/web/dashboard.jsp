<%@page import="jdk.nashorn.internal.runtime.regexp.RegExp"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page  import="com.dps.dbi.DbResult"%>
<%@page import="com.dalessio.samurai.DataAccessObject"%>
<%@page import="java.util.Date"%>

<!DOCTYPE html>
<%
    Long timestamp = new Date().getTime();
    
    DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
    Long user_id = (Long) request.getSession().getAttribute("user_id");
   
    
    //gets vat code to achieve if it is freign or italian
    String user_role = "";
    
    if(user_id==null){
       
%>

    <script>window.open("/Samurai/landing.jsp","_self")</script>

<%}else{
    user_role = dao.getUserRole(user_id);
    DbResult dbr_user = dao.readUsers( user_id);
    String userName = dbr_user.getString("firstName") + " " + dbr_user.getString("lastName");
%>

    <html>
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>Workline 2.0</title>
            <link href="/Samurai/common.css" rel="stylesheet">
            <link href="/Samurai/customers.css" rel="stylesheet">
        </head>
        <body>
            <div class="Header">
                <span class="SWName">Workline 2.0</span>
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
                    <div onmouseover="app.showTools()" onmouseout="app.hideTools()" onclick="window.open('landing.jsp','_self');">LOG-OUT</div>
                </div>
            </div>
            
            <div class="Menu">
                <span class=" Button" onclick="window.open('orders.jsp','_self');">ELENCO LAVORI</span>
                <span class=" Button" onclick="window.open('task_details.jsp','_self');">ORE LAVORI</span>
                <span class=" Button" onclick="window.open('deliveryNote_details.jsp','_self');">DDT</span>
                <%if( "admin".equals(user_role) ){%><span class=" Button" onclick="window.open('invoice_details.jsp','_self');">FATTURE</span><%}%>
                <%if( "admin".equals(user_role) ){%><span class=" Button" onclick="window.open('quote_details.jsp' , '_self');">PREVENTIVI</span><%}%>
                <%if( "admin".equals(user_role) ){%><span class=" Button" onclick="window.open('creditNote_details.jsp','_self');">NOTE ACCREDITO</span><%}%>
            </div>
            
    

            <div class="Footer">
                <span class="Footer_message">Utente : <%=userName%></span>
            </div>

        </body>
        <script src="/Samurai/dbi.js?<%=timestamp%>"></script>
        <script src="/Samurai/dropDown.js?<%=timestamp%>"></script>
        <script>setInterval(function(){app.ping();},60000);</script>
    </html>

<%}%>
