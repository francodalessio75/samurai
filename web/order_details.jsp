<%@page import="com.dps.dbi.DbResult"%>
<%@page import="com.dalessio.samurai.DataAccessObject"%>
<%@page import="java.time.LocalDate"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.util.Date"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<%
    Long timestamp = new Date().getTime();

    //English date format
    DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    //European date format
    DateTimeFormatter DTFE = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    Long user_id = (Long) request.getSession().getAttribute("user_id");

    //Long customer_id = (Long) request.getSession().getAttribute("customer_id");
    Long order_id = Long.parseLong(request.getParameter("order_id"));

    DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");

    Double orderHours = dao.getOrderHours(order_id);

    DbResult dbr_jobTypes = dao.readJobTypes(null);

    DbResult dbr_order = dao.readOrders(order_id, null, null, null, null, null, null, null, null, null, null, null);
    Long jobType_id = dbr_order.getLong("jobType_id");

    Long customer_id = dao.getCustomerIdByOrderId(order_id);

    // sum of all tasks costs : translations cost, transfert cost, variouse material cost and external jobs cost
    Double totalCosts = dao.getOrderTotalCosts(order_id);
    //formats the date
    StringBuilder dateSB = new StringBuilder(dbr_order.getString("date"));
    dateSB.insert(4, "-");
    dateSB.insert(7, "-");
    String orderDate = DTFE.format(DTF.parse(dateSB.toString()));
    DbResult dbr_completion_states = dao.readCompletionStates();
    DbResult dbr_customers = dao.readAllCustomers();
    //formats the data Ordine xml
    String dataOrdine = "";
    StringBuilder dataOrdineSB = new StringBuilder(dbr_order.getString("dataOrdine") == null ? "" : dbr_order.getString("dataOrdine"));
    if (dataOrdineSB.length() == 8) {
        dataOrdineSB.insert(4, "-");
        dataOrdineSB.insert(7, "-");
        dataOrdine = dataOrdineSB.toString();
    }

    if (user_id == null) {
%>

<script>window.open("/Samurai/landing.jsp", "_self")</script>

<%} else {
    DbResult dbr_user = dao.readUsers(user_id);
    String userName = dbr_user.getString("firstName") + " " + dbr_user.getString("lastName");
    String user_role = dbr_user.getString("role");
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Workline 2.0</title>
        <link href="/Samurai/common.css" rel="stylesheet">
        <link href="/Samurai/orders.css" rel="stylesheet">
        <link href="/Samurai/tasks.css" rel="stylesheet">

    </head>
    <!--body onload="app.getTasks();"-->
    <body>
        <div class="loader" id="loader"></div>
        <div id="modal" class="modal"></div>
        <input type="hidden" value="<%=user_id%>" id="hidden_user_id">
        <input type="hidden" value="<%=user_role%>" id="hidden_user_role">
        <input type="hidden" value="false" id="hidden_getFiltersValues">

        <div class="Header">
            <span class="SWName">Workline 2.0</span><span class="PageTitle"> Scheda Lavoro </span>
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

        <div class="Content">
            <table class="Details_table">
                <tr class="Details_row">
                    <th class="Detail_header" id="code_label">CODICE</th>
                    <td id="code_value"><%=dbr_order.getString("code")%></td>
                </tr>
                <tr class="Details_row">
                    <th class="Detail_header" id="serial_number_label">MATRICOLA</th>
                    <td id="serial_number_value"><%=dbr_order.getString("serialNumber")%></td>
                </tr>
                <tr class="Details_row">
                    <th class="Detail_header" id="date_label">DATA</th>
                    <td id="date_value"><%=orderDate%></td>
                </tr>
                <tr class="Details_row">
                    <th class="Detail_header" id="customer_label" <%if ("admin".equals(user_role)) {%>style="color:blue;"<%}%> >CLIENTE</th>
                    <!--td><!%=customer_denomination%></td>
                    <!-- customers select -->
                    <td>
                        <select id="customer_select_options" <%if (!"admin".equals(user_role)) {%>disabled<%}%> onchange="app.orderDetailsChanged('#customer_select_options');" >
                            <option value="" >CLIENTE</option>
                            <%for (int i = 0; i < dbr_customers.rowsCount(); i++) {%>
                            <option value="<%=dbr_customers.getLong(i, "customer_id")%>"><%=dbr_customers.getString(i, "denomination")%></option>
                            <%}%>
                        </select>
                        <script>document.getElementById("customer_select_options").value =<%=customer_id%></script>
                    </td>
                </tr>
                <tr class="Details_row">
                    <th class="Detail_header">DESCRIZIONE LAVORO</th><td><input id="machinary_model_input" type="text" onchange="app.orderDetailsChanged('#machinary_model_input');" value="<%=dbr_order.getString("machinaryModel")%>"></td>
                </tr>
                <tr class="Details_row">
                    <th class="Detail_header" id="creator_label">APERTO DA</th>
                    <td id="creator_value"><%=dbr_order.getString("creatorFirstName")%> <%=dbr_order.getString("creatorLastName")%></td>
                </tr>
                <tr class="Details_row">
                    <th class="Detail_header">TIPO LAVORO</th>
                    <td>
                        <select id="job_type_select_options" onchange="app.orderDetailsChanged('#job_type_select_options');" >
                            <%for (int i = 0; i < dbr_jobTypes.rowsCount(); i++) {%>
                            <option value="<%=dbr_jobTypes.getLong(i, "jobType_id")%>" ><%=dbr_jobTypes.getString(i, "name")%></option>
                            <%}%>
                        </select>
                        <script>document.getElementById("job_type_select_options").value =<%=jobType_id%></script>
                    </td> 
                </tr>
                <!--if it's "in progress" it will not be suggested. If it has been delivered/invoiced it cannot be switched back from "completed" to "in progress"-->
                <tr class="Details_row">
                    <th class="Detail_header">AVANZAMENTO</th>
                    <td>
                        <span>
                            <select id="completion_state_select_options"  onchange="app.orderDetailsChanged('#completion_state_select_options'); app.setSuggestion('#completion_state_select_options');" >
                                <%for (int i = 0; i < dbr_completion_states.rowsCount(); i++) {%>
                                <option value="<%=dbr_completion_states.getLong(i, "completion_state_id")%>" ><%=dbr_completion_states.getString(i, "name")%></option>
                                <%}%>
                            </select>
                        </span>
                        <span>
                            <input id="story_data_input" type="text" onchange="app.orderDetailsChanged('#story_data_input');" <%if (dbr_order.getString("storyData") != null) {%>value="<%=dbr_order.getString("storyData")%>"<%}%>>
                        </span>
                    </td>
                    <%--if( dbr_order != null ){--%>
                        <!--span><%--=dbr_order.getString("storyData")--%></span-->
                    <%--}--%>
                <script>document.getElementById("completion_state_select_options").value =<%=dbr_order.getLong("completion_state_id")%></script>
                </tr>
                <tr class="Details_row">
                    <th class="Detail_header">NOTE</th>
                    <td><textarea id="notes_input"  onchange="app.orderDetailsChanged('#notes_input');"><%=dbr_order.getString("notes")%></textarea></td>
                </tr>
                <% if (user_role.equals("admin")) {%>
                <tr class="Details_row">
                    <th class="Detail_header" id="totals_label">TOTALE</th>
                    <td id="total_hours_value"><span class="TableLable">ORE:</span><%=orderHours%><span class="TableLable">COSTI:</span><%=totalCosts%></td>
                </tr>
                <%}%>

                <tr class="Details_row">
                    <th class="Detail_header" id="title_label">CARTELLA LAVORO</th>
                    <td>
                        <span class="Button" onclick="window.open('titlepage.jsp?order_id=<%=order_id%>', '_blank');">STAMPA CARTELLA</span>
                        <span class="Button" onclick="window.open('orderCover.jsp?order_id=<%=order_id%>', '_blank');">STAMPA CARTELLA 2</span>
                    </td>
                </tr>

                <tr class="Details_row">
                    <th class="Detail_header">SUGGERISCI</th>
                    <td><input class="Checkbox" id="suggested_checkbox" type="checkbox" onchange="app.orderDetailsChanged('#suggested_checkbox');" /><span>Non suggerire l'ordine in compilazione ddt e fatture.</span></td>
                <script>
                            document.getElementById("suggested_checkbox").checked = <%=!dbr_order.getBoolean("suggested")%>
                    <%if (dbr_order.getLong("completion_state_id") == 1) {%>
                            document.getElementById("suggested_checkbox").disabled = true;
                    <%}%>
                </script>
                </tr>
                <tr class="Details_row">
                    <th class="Detail_header">ORDINE (XML) MAX 20 CARATTERI</th><td><input id="ordine_input" type="text" onchange="app.orderDetailsChanged('#ordine_input');" <%if (dbr_order.getString("ordine") != null) {%>value="<%=dbr_order.getString("ordine")%>"<%}%>></td>
                </tr>
                <tr class="Details_row">
                    <th class="Detail_header">COMMESSA (XML) MAX 100 CARATTERI</th><td><input id="commessa_input" type="text" onchange="app.orderDetailsChanged('#commessa_input');" <%if (dbr_order.getString("commessa") != null) {%>value="<%=dbr_order.getString("commessa")%>"<%}%>></td>
                </tr>
                <tr class="Details_row">
                    <th class="Detail_header">DATA ORDINE (XML)</th><td><input id="dataOrdine_input" type="date" onchange="app.orderDetailsChanged('#dataOrdine_input');" value="<%=dataOrdine%>"></td>
                </tr>

            </table>   

            <!--    ACTIONS -->    
            <div id="updateOrderButton" class="Button Operation Update" onclick="app.orderUpdated(<%=order_id%>);">CONFERMA MODIFICHE</div>

            <div id="goToCertificateButton" class="Button Operation " onclick="window.open('complianceCertificate_details.jsp?order_id=<%=order_id%>', '_self');">MODULO CONFORMITA'</div>

            <!--RELATES TASKS-->
            <div class="Tasks">
                <table class="Table">
                    <thead>
                    </thead>
                    <tbody></tbody>
                </table>   
            </div>         
        </div>   

        <div class="Footer">
            <span class="User_name"> Utente : <%=userName%></span>
            <span class="Footer_message"></span>
        </div>

    </body>

    <template id="task_in_order_details_template">
        <tr id="task_row">
            <td id="task_date"></td>
            <td id="operator"></td>
            <td id="job_sub_type"></td>
            <td id="notes"></td>
        </tr>
    </template>  

    <script src="/Samurai/dbi.js?<%=timestamp%>"></script>
    <script src="/Samurai/orders.js?<%=timestamp%>"></script>
    <script src="/Samurai/tasks.js?<%=timestamp%>"></script>
    <script src="/Samurai/dropDown.js?<%=timestamp%>"></script>
    <script>setInterval(function () {
                        app.ping();
                    }, 60000);</script>

</html>

<%}%>