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

    //last six monyhs is the period choosen as initial setting of filters
    DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate today = LocalDate.now();
    String todayString = DTF.format(today);
    LocalDate oneMonthsLess = today.minusMonths(1);
    String oneMonthsLessString = DTF.format(oneMonthsLess);

    Long user_id = (Long) request.getSession().getAttribute("user_id");

    if (user_id == null) {
%>

<script>window.open("/Samurai/landing.jsp", "_self")</script>

<%} else {
    JsonObject filter = null;
    try {
        filter = (JsonObject) new JsonParser().parse(request.getParameter("filter"));
    } catch (Exception ex) {
    }

    DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");

    DbResult dbr_user = dao.readUsers(user_id);

    String userName = dbr_user.getString("firstName") + " " + dbr_user.getString("lastName");

    String user_role = dbr_user.getString("role");

    DbResult dbr_operators = dao.readUsers(null, null, null, null, null, null, null, null, null, null, null, null);

    DbResult dbr_jobTypes = dao.readJobTypes(null);

    DbResult dbr_jobSubtypes = dao.readJobSubtypes(null);
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Workline 2.0</title>
        <link href="/Samurai/common.css" rel="stylesheet">
        <link href="/Samurai/tasks.css" rel="stylesheet">
        <link href="/Samurai/Resources/font-awesome-4.7.0/css/font-awesome.min.css" rel="stylesheet">
    </head>
    <body onload="app.filterTasks('<%=user_id%>', '<%=user_role%>', true);">

        <div class="loader" id="loader"></div>
        <div id="modal" class="modal"></div>

        <input type="hidden" id="hidden_user_role" value="<%=user_role%>">
        <input type="hidden" id="hidden_user_id" value="<%=user_id%>">

        <div class="Header">
            <span class="SWName">Workline 2.0</span><span class="PageTitle"> Ricerca Ore Lavori </span>
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

            <div class="Filters_label">RICERCA</div>

            <% if (user_role.equals("admin")) {%>
            <select id="operator_select_options" >
                <option value="" >OPERATORE</option>
                <%for (int i = 0; i < dbr_operators.rowsCount(); i++) {%>
                <option value="<%=dbr_operators.getLong(i, "user_id")%>"><%=dbr_operators.getString(i, "firstName") + " " + dbr_operators.getString(i, "lastName")%></option>
                <%}
                        }%>
            </select>
            <% if (user_role.equals("admin")) {%>
            <script>document.getElementById('operator_select_options').value = "<%= filter != null ? filter.get("operator_id").getAsString() : ""%>";</script>
            <%}%>
            
            <input id="order_code_Hint" placeholder="CODICE LAVORO" value="<%= filter != null ? filter.get("order_code").getAsString() : ""%>">
            
            <input id="order_serial_number_Hint" placeholder="MATRICOLA" value="<%= filter != null ? filter.get("order_serial_number").getAsString() : ""%>">

            <select id="jobType_select_options" >
                <option value="" > TIPO LAVORO </option>
                <% for (int i = 0; i < dbr_jobTypes.rowsCount(); i++) {%>
                <option value="<%=dbr_jobTypes.getLong(i, "jobType_id")%>"><%=dbr_jobTypes.getString(i, "name")%></option>
                <%}%>
            </select>
            <script>document.getElementById('jobType_select_options').value = "<%= filter != null ? filter.get("jobType_id").getAsString() : ""%>";</script>

            <select id="jobSubtype_select_options" >
                <option value="" > TIPO LAVORAZIONE </option>
                <% for (int i = 0; i < dbr_jobSubtypes.rowsCount(); i++) {%>
                <option value="<%=dbr_jobSubtypes.getLong(i, "jobSubtype_id")%>"><%=dbr_jobSubtypes.getString(i, "name")%></option>
                <%}%>
            </select>
            <script>document.getElementById('jobSubtype_select_options').value = "<%= filter != null ? filter.get("jobSubtype_id").getAsString() : ""%>";</script>

            <table id="dates" style="border-collapse: collapse;">
                <tr><td>Da:</td><td><input type="date" id="from_date" value="<%= ((filter != null && !filter.get("from_date").getAsString().equals("")) || (filter != null && !filter.get("order_code").getAsString().equals(""))) ? filter.get("from_date").getAsString() : oneMonthsLessString%>"></td></tr>
                <tr><td>A:</td><td><input type="date" id="to_date" value="<%= ((filter != null && !filter.get("to_date").getAsString().equals("")) || (filter != null && !filter.get("order_code").getAsString().equals(""))) ? filter.get("to_date").getAsString() : todayString%>"></td></tr>
            </table>

            <select id="completionState_select_options">
                <option value="-1">- TUTTI -</option>
                <option value="1">- IN CORSO -</option>
                <option value="2">- COMPLETATI -</option>
            </select>
            <script>
                    document.getElementById("completionState_select_options").value = <%= filter != null ? filter.get("completionState_id").getAsLong() : ""%>;
            </script>

            <div id="refresh" class="Button" onclick="app.filterTasks('<%=user_id%>', '<%=user_role%>', false);"><i  id="searchIcon" class="fa fa-search" aria-hidden="true"></i>RICERCA</div>
            
            <%if ("admin".equals(user_role)) {%>
                <div id="go-to-aggregate" class="Button" onclick="app.openAggregateTasksPage('<%=user_role%>');"><i  id="aggregateIcon" class="fa fa-minimize" aria-hidden="true"></i>AGGREGA</div>
            <%}%>
        </div>     

        <div class="Content WithFilter" style="text-align: center;">
            <table id="DetailedTable" class="Table DetailedTable">
                <thead>
                <th>DATA</th>
                <th>OPERATORE</th>
                <th>CODICE</th>
                <th>CLIENTE</th>
                <th>STATO LAVORO</th>
                <th><i  id="searchIcon" class="fa fa-paperclip fa-3x" aria-hidden="true"></i></th>
                <th>DESC. LAVORO</th>
                <th>TIPO LAVORO</th>
                <th>TIPO LAVOR.NE</th>
                <th>ORE</th>
                <th>TOTALE ORE</th>
           <%if ("admin".equals(user_role)) {%>
                <th>COSTO ORARIO<p>(A)</p></th>
                <th>COSTO TRAD.</th>
                <th>IMPORTO TRAD.<p>(B)</p></th>
                <th>ORE LAV. EST.</th>
                <th>COSTO LAV. EST.<p>(C)</p></th>
                <th>COSTO MAT.LI<p>(D)</p></th>
                <th>COSTO TRAS.TA<p>(E)</p></th>
            <%}%>
                </thead>
                <tbody></tbody>
            </table>   
                
            <table id="AggregateTable" class="AggregateTable">
            <thead>
                <th>Cliente</th>
                <th>Codice</th>
                <th>Costi</th>
                <th>Fatturato</th>
                <th>Margine</th>
            </thead>
            <tbody></tbody>
            </table>
        </div> 

        <div class="Footer">
            <span class="User_name"> Utente : <%=userName%></span>
            <span class="Footer_message"></span>
            <!--span id="newOrderButton" class="Button Operation" onclick="document.querySelector('.Modal_screen').classList.add('Visible');">NUOVA ATTIVITA'</span-->
        </div>

    </body>

    <template id="detailedTaskTableRow">
        <tr class="TaskTableRow">
            <td class="TaskDate"></td>
            <td class="Operator"></td>
            <td class="OrderCode"></td>
            <td class="CustomerDenomination"></td>
            <td class="CompletionState"></td>
            <td class="HasAttachment"></td>
            <td class = "MachinaryModel"></td>
            <td class = "JobType"></td>
            <td class = "JobSubtype"></td>
            <td class = "Hours"></td>
            <td class = "TotalHours"></td>
            <%if ("admin".equals(user_role)) {%>
            <td class="HourlyCost"></td>
            <td class = "TranslationCost"></td>
            <td class = "TranslationPrice"></td>
            <td class = "ExternalJobsHours"></td>
            <td class = "ExternalJobsCost"></td>
            <td class = "VariouseMaterialsCost"></td>        
            <td class = "TransfertCost"></td>           
            <%}%>
        </tr>
    </template>
    
        <template id="detailedTaskTableRow">
        <tr class="TaskTableRow">
            <td class="CustomerDenomination"></td>
            <td class="OrderCode"></td>
            <td class="Costs"></td>
            <td class="Invoiced"></td>
            <td class="Margin"></td>
        </tr>
    </template>    
    
        <template id="generalTotal">
            <table>
                <th>TOTALE GENERALE(A+B+C+D+E)</th><th>DESC. LAVORO</th>
            </table>
        </template>
    

    <script src="/Samurai/dbi.js?<%=timestamp%>"></script>
    <script src="/Samurai/tasks.js?<%=timestamp%>"></script>
    <script src="/Samurai/dropDown.js?<%=timestamp%>"></script>
    <script>setInterval(function () {
                        app.ping();
                    }, 60000);</script>

    <script>
        if (typeof app === 'undefined' || app === null)
            app = {};
        app.user_role = <%=user_role%>;
        app.user_id = <%=user_id%>;
    </script>

</html>

<%}%>