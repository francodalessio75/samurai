<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.dalessio.samurai.DataAccessObject"%>
<%@page import="com.dps.dbi.DbResult"%>
<%@page import="java.util.Date"%>

<!DOCTYPE html>
<%
    Long timestamp = new Date().getTime();

    Long user_id = (Long) request.getSession().getAttribute("user_id");
    if (user_id == null) {
%>

<script>window.open("/Samurai/landing.jsp", "_self")</script>

<%} else {
    DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
    DbResult dbr_user = dao.readUsers(user_id);
    String userName = dbr_user.getString("firstName") + " " + dbr_user.getString("lastName");
    String user_role = dbr_user.getString("role");
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Workline 2.0</title>
        <link href="/Samurai/common.css" rel="stylesheet">
        <link href="/Samurai/dropDownMenu.css" rel="stylesheet">
    </head>
    <body onload="app.getTransportersPage();">
        
        <div class="loader" id="loader"></div>
        <div id="modal" class="modal"></div>
        
        <div class="Header">
            <span class="SWName">Workline 2.0</span><span class="PageTitle"> Ricerca Vettori </span>
        </div>

        <div class="Dropdown_registry">
            <button onmouseover="app.showRegistry()" onmouseout="app.hideRegistry()" class="DropBtn Registry">ANAGRAFICHE</button>
            <div id="myDropdown_registry" class="Dropdown_content Registry">
                <div onmouseover="app.showRegistry()" onmouseout="app.hideRegistry()" onclick="window.open('customers.jsp', '_self');">ANAGRAFICA CLIENTI</div>
                <div onmouseover="app.showRegistry()" onmouseout="app.hideRegistry()" onclick="window.open('translationsCenters.jsp', '_self');">CENTRI TRADUZIONE</div>
                <div onmouseover="app.showRegistry()" onmouseout="app.hideRegistry()" onclick="window.open('transporters.jsp', '_self');">VETTORI</div>
                <%if ("admin".equals(user_role)) {%><div onmouseover="app.showRegistry()" onmouseout="app.hideRegistry()" onclick="window.open('users.jsp', '_self');">OPERATORI</div><%}%>
                <%if ("admin".equals(user_role)) {%><div onmouseover="app.showRegistry()" onmouseout="app.hideRegistry()" onclick="window.open('jobTypes.jsp', '_self');">TIPI LAVORO</div><%}%>
                <%if ("admin".equals(user_role)) {%><div onmouseover="app.showRegistry()" onmouseout="app.hideRegistry()" onclick="window.open('jobSubtypes.jsp', '_self');">TIPI LAVORAZIONE</div><%}%>
            </div>
        </div>

        <div class="Dropdown_tools">
            <button onmouseover="app.showTools()" onmouseout="app.hideTools()" class="DropBtn Tools">STRUMENTI</button>
            <div id="myDropdown_tools" class="Dropdown_content Tools">
                <div onmouseover="app.showTools()" onmouseout="app.hideTools()" onclick="window.open('changeCredentials.jsp', '_self');">CAMBIA PASSWORD</div>
                <%if ("admin".equals(user_role)) {%><div onmouseover="app.showTools()" onmouseout="app.hideTools()" onclick="window.open('invoiceDateUpdating.jsp', '_self');">CAMBIA DATA FATTURA</div><%}%>
                <%if ("admin".equals(user_role)) {%><div onmouseover="app.showTools()" onmouseout="app.hideTools()" onclick="window.open('scheduleDates.jsp', '_self');">SCADENZE</div><%}%>
                <div onmouseover="app.showTools()" onmouseout="app.hideTools()" onclick="window.open('landing.jsp', '_self');">LOG-OUT</div>
            </div>
        </div>

        <div class="Menu">
            <span class=" Button" onclick="window.open('orders.jsp', '_self');">ELENCO LAVORI</span>
            <span class=" Button" onclick="window.open('task_details.jsp', '_self');">ORE LAVORI</span>
            <span class=" Button" onclick="window.open('deliveryNote_details.jsp', '_self');">DDT</span>
            <%if ("admin".equals(user_role)) {%><span class=" Button" onclick="window.open('invoice_details.jsp', '_self');">FATTURE</span><%}%>
            <%if ("admin".equals(user_role)) {%><span class=" Button" onclick="window.open('quote_details.jsp', '_self');">PREVENTIVI</span><%}%>
            <%if ("admin".equals(user_role)) {%><span class=" Button" onclick="window.open('creditNote_details.jsp', '_self');">NOTE ACCREDITO</span><%}%>
        </div>

        <div class="Filters">
            <span id="newTransporterButton" class="Button Operation" onclick="document.querySelector('.Modal_screen').classList.add('Visible');">INSERISCI NUOVO VETTORE</span>
        </div>

        <div class="Content WithFilter">
            <table class="Table">
                <thead>
                <th>DENOMINAZIONE</th>
                <th>INDIRIZZO</th>
                </thead>
                <tbody></tbody>
            </table>   
        </div> 

        <div class="Footer">
            <span class="User_name"> Utente : <%=userName%></span>
            <span class="Footer_message"></span>
        </div>

        <div class="Modal_screen">
            <div class="New_record">
                <input id='new_transporter_denomination_input' placeholder="DENOMINAZIONE"><br>
                <span type='button' class='Button Accept' onclick="app.createNewTransporter();">AGGIUNGI</span>
                <span type='button' class='Button Cancel' onclick="document.querySelector('.Modal_screen').classList.remove('Visible');">ANNULLA</span>
            </div>
        </div>

    </body>

    <template id="transporter_template">
        <tr id="transporter_row">
            <td id="denomination"></td>
            <td id="address"></td>
        </tr>
    </template>  

    <script src="/Samurai/dbi.js?<%=timestamp%>"></script>
    <script src="/Samurai/transporters.js?<%=timestamp%>"></script>
    <script src="/Samurai/dropDown.js?<%=timestamp%>"></script>
    <script>setInterval(function () {
                        app.ping();
                    }, 60000);</script>
</html>

<%}%>