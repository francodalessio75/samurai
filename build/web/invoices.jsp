<%@page import="com.google.gson.JsonParser"%>
<%@page import="com.google.gson.JsonObject"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.dalessio.samurai.DataAccessObject"%>
<%@page import="com.dps.dbi.DbResult"%>
<%@page import="com.dalessio.samurai.DataAccessObject"%>
<%@page import="com.dps.dbi.DbResult"%>
<%@page import="java.time.LocalDate"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.util.Date"%>

<!DOCTYPE html>
<%
    Long timestamp = new Date().getTime();

    Long user_id = (Long) request.getSession().getAttribute("user_id");

    if (user_id == null) {
%>

<script>window.open("/Samurai/landing.jsp", "_self")</script>

<%} else {
    //last six monyhs is the period choosen as initial setting of filters
    DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate today = LocalDate.now();
    String todayString = DTF.format(today);
    LocalDate oneMonthsLess = today.minusMonths(1);
    String oneMonthsLessString = DTF.format(oneMonthsLess);

    JsonObject filter = null;
    try {
        filter = (JsonObject) new JsonParser().parse(request.getParameter("filter"));
    } catch (Exception ex) {
    }

    DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");

    DbResult dbr_user = dao.readUsers(user_id);

    String userName = dbr_user.getString("firstName") + " " + dbr_user.getString("lastName");

    String user_role = dbr_user.getString("role");

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
    <body onload="app.filterInvoices();">

        <div class="loader" id="loader"></div>
        <div id="modal" class="modal"></div>

        <div class="Header">
            <span class="SWName">Workline 2.0</span><span class="PageTitle"> Fatture </span>
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
            <span class=" Button" onclick="window.open('invoice_details.jsp', '_self');">FATTURE</span>
            <span class=" Button" onclick="window.open('quote_details.jsp', '_self');">PREVENTIVI</span>
            <%if ("admin".equals(user_role)) {%><span class=" Button" onclick="window.open('creditNote_details.jsp', '_self');">NOTE ACCREDITO</span><%}%>
        </div>

        <div class="Filters">

            <div class="Filters_label">RICERCA</div>

            <div class="Filters_number_label">NUMERO FATTURA</div>
            <div><input id="numberFilter" value="<%= filter.get("number").getAsString().equals("") ? "" : filter.get("number").getAsString()%>"></div>

            <!--input id="customer_denomination_Hint" placeholder="NOME CLIENTE"-->
            <select id="customer_select_options">
                <option value="" >CLIENTE</option>
                <%for (int i = 0; i < dbr_customers.rowsCount(); i++) {%>
                <option value="<%=dbr_customers.getLong(i, "customer_id")%>"><%=dbr_customers.getString(i, "denomination")%></option>
                <%}%>
            </select>                


            <div class="Filters_period_label">PERIODO</div>

            <table id="dates" style="border-collapse: collapse;">
                <tr><td>Da:</td><td><input type="date" id="from_date" value="<%= filter.get("from_date").getAsString().equals("") ? oneMonthsLessString : filter.get("from_date").getAsString()%>"></td></tr>
                <tr><td>A:</td><td><input type="date" id="to_date" value="<%= filter.get("to_date").getAsString().equals("") ? todayString : filter.get("to_date").getAsString()%>"></td></tr>
            </table>

            <div id="refresh" class="Button" onclick="app.filterInvoices();"><i  id="searchIcon" class="fa fa-search" aria-hidden="true"></i>RICERCA</div>
    
            <div id="aggregatedViewButton" class="Button" onclick="app.openAggregatedInvoicesPage();">VISTA AGGREGATA</div>
        </div> 

        <div class="Content WithFilter">
            <table class="Table" >
                <col width="10%">
                <col width="10%">
                <col width="40%">
                <col width="10%">
                <col width="10%">
                <col width="10%">
                <col width="10%">
                <thead>
                <th class="ThList">NUMERO</th>
                <th class="ThList">DATA</th>
                <th class="ThList">CLIENTE</th>
                <th class="ThList">IMPONIBILE</th>
                <th class="ThList">TOTALE</th>
                <th class="ThList">PROGRESSIVO</th>
                <th class="ThList">PDF</th>
                </thead>
                <tbody></tbody>
            </table>   
        </div>

        <div class="Footer">
            <span class="User_name"> Utente : <%=userName%></span>
            <span class="Footer_message"></span>
        </div>

    </body>

    <template id="invoiceTableRow">
        <tr class="InvoiceTableRow">
            <td class="InvoiceNumber"></td>
            <td class="InvoiceDate"></td>
            <td class="Customer"></td>
            <td class="Taxable"></td>
            <td class="Total"></td>
            <td class="Progressive"></td>
            <td class="Pdf">PDF</td>
        </tr>
    </template>

    <script src="/Samurai/dbi.js?<%=timestamp%>"></script>
    <script src="/Samurai/invoices.js?<%=timestamp%>"></script>
    <script src="/Samurai/dropDown.js?<%=timestamp%>"></script>
    <script>setInterval(function () {
                        app.ping();
                    }, 60000);</script>

    <script>
        if (typeof app === 'undefined' || app === null)
            app = {};
        app.user_role = '<%=user_role%>';
        app.user_id = <%=user_id%>;
    </script>

</html>

<%}%>