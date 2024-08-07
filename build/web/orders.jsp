<%@page import="java.time.LocalDate"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.dps.dbi.DbResult"%>
<%@page import="com.dalessio.samurai.DataAccessObject"%>
<%@page import="com.google.gson.JsonArray"%>
<%@page import="com.google.gson.JsonPrimitive"%>
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

    DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");

    DbResult dbr_customers = dao.readAllCustomers();
    DbResult dbr_jobTypes = dao.readJobTypes(null);

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
        <link href="/Samurai/Resources/font-awesome-4.7.0/css/font-awesome.min.css" rel="stylesheet">
    </head>
    <body onload="app.getOrdersPage();">
        <div class="loader" id="loader"></div>
        <div id="modal" class="modal"></div>
        <div class="Header">
            <span class="SWName">Workline 2.0</span><span class="PageTitle"> Elenco Lavori </span>
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

        <!-- FILTERS SECTION -->
        <div class="Filters">

            <div class="Filters_label">RICERCA</div>

            <input id="order_code_Hint" placeholder="      CODICE LAVORO">
            
            <input id="serial_number_Hint" placeholder="      MATRICOLA">

            <!--input id="customer_denomination_Hint" placeholder="NOME CLIENTE"-->
            <select id="customer_select_options" <!--onchange="app.getCustomerMachinaries( 'machinary_model_select_options' )"--> >
                <option value="" >CLIENTE</option>
                <%for (int i = 0; i < dbr_customers.rowsCount(); i++) {%>
                <option value="<%=dbr_customers.getLong(i, "customer_id")%>"><%=dbr_customers.getString(i, "denomination")%></option>
                <%}%>
            </select>

            <!--input id="machinary_model_select_options" placeholder="DESCRIZIONE LAVORO"
            <select id="machinary_model_select_options" >
               <option value="" >DESCRIZIONE LAVORO</option>
            </select>   ELIMINATED ON 2018/JUNE/20 BECOUSE IT IS A NON STRUCTURED ELEMENT THEN NOT ADAPTED TO BE A FILTER-->

            <!--input id="job_type_Hint" placeholder="TIPO LAVORO"-->
            <select id="job_type_select_options" >
                <option value="" >TIPO LAVORO</option>
                <%for (int i = 0; i < dbr_jobTypes.rowsCount(); i++) {%>
                <option value="<%=dbr_jobTypes.getLong(i, "jobType_id")%>"><%=dbr_jobTypes.getString(i, "name")%></option>
                <%}%>
            </select>

            <div class="Filters_status_label">DESCRIZIONE LAVORO</div>

            <textarea   id='search_order_description' rows="4" cols="40" ></textarea>

            <!--div class="Filters_period_label" id="period">PERIODO</div-->

            <table id="dates" style="border-collapse: collapse;">
                <tr><td>Da:</td><td><input type="date" id="from_date" value="<%=oneMonthsLessString%>" max="<%=todayString%>"></td></tr>
                <tr><td>A:</td><td><input type="date" id="to_date" value="<%=todayString%>" max="<%=todayString%>"></td></tr>
            </table>

            <div class="Filters_status_label" id="status_label">STATO LAVORO</div>

            <select id="status">
                <option value="-1">- TUTTI -</option>
                <option value="1">- IN CORSO -</option>
                <option value="2">- COMPLETATI -</option>
            </select>

            <!-- FUNZIONALITA' DISMESA DOVEVA IMPLEMENTARE IL SUGGERIMENTO PER I DDT CHE E' STATO IMPLEMENTATO 
                 DIVERSAMENTE OSSIA CON IL CHECKBOX SUGGERISCI IN order_details.jsp
            <div class="Filters_status_label">DISPONIBILITA'</div>
            
            <select id="availability">
                <option value="-1">- TUTTI -</option>
                <option value="1">- DISPONIBILE -</option>
                <option value="3">- NON DISPONIBILE -</option>
            </select>
            -->


            <div id="refresh"  class="Button" onclick="app.filterOrders();"><i id="searchIcon" class="fa fa-search" aria-hidden="true"></i>RICERCA</div>

            <!--div id="confirm_label" class="Filters_label">OPERAZIONI</div-->

            <div id="new_order_button" class="Button"  onclick="document.querySelector('.Modal_screen').classList.add('Visible');">NUOVO LAVORO</div>
        </div>    

        <div class="Content WithFilter">
            <table class="Table">
                <thead>
                <th class="Code">CODICE</th>
                <th class="SerialNumber">MATRICOLA</th>
                <th class="Date">DATA</th>
                <th class="CustomerDenomination">CLIENTE</th>
                <th class="Description">DESCRIZIONE LAVORO</th>
                <th class="JobType">TIPO LAVORO</th>
                <th class="CompletionState">STATO</th>
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
                <!--date-->
                <input type="date" id="new_order_date" value="" max="<%=todayString%>"><br>
                <script>document.getElementById('new_order_date').valueAsDate = new Date();</script>

                <!-- customers select -->
                <select id="new_customer_select_options" <!--onchange="app.getCustomerMachinaries( 'machinary_models_datalist' )"--> >
                    <option value="" >CLIENTE</option>
                    <%for (int i = 0; i < dbr_customers.rowsCount(); i++) {%>
                    <option value="<%=dbr_customers.getLong(i, "customer_id")%>"><%=dbr_customers.getString(i, "denomination")%></option>
                    <%}%>
                </select><br><br>
                
                <!--machinary model-->
                <input id='new_order_serial_number' placeholder="MATRICOLA">

                <!--machinary model-->
                <input id='new_order_machinary_model' placeholder="DESCRIZIONE LAVORO">

                <!--
                <div>
                    <input type="text"  id="new_order_machinary_model" list="machinary_models_datalist" placeholder="DESCRIZIONE LAVORO">
                    <datalist  id="machinary_models_datalist"   >
                    </datalist>
                </div>
                -->

                <!--job types select -->
                <select id="new_job_type_select_options">
                    <option value="" disabled selected>TIPO LAVORO</option>
                    <%for (int i = 0; i < dbr_jobTypes.rowsCount(); i++) {%>
                    <option value="<%=dbr_jobTypes.getLong(i, "jobType_id")%>"><%=dbr_jobTypes.getString(i, "name")%></option>
                    <%}%>
                </select><br><br>

                <div>Note</div>
                <!--input id='new_order_job_type' placeholder="TIPO LAVORO"--><br>
                <!--notes-->
                <textarea   id='new_order_notes' rows="4" cols="40" ></textarea>
                <!--input id='new_order_notes' type="text-area" placeholder="NOTE"--><br>
                <span type='button' class='Button Accept' onclick="app.createNewOrder(<%=user_id%>);">AGGIUNGI</span>
                <span type='button' class='Button Cancel' onclick="document.querySelector('.Modal_screen').classList.remove('Visible');">ANNULLA</span>
            </div>
        </div>

    </body>

    <template id="order_template">
        <tr id="order_row">
            <td class="Code" id="code"></td>
            <td class="SerialNumber" id="serial_number"></td>
            <td class="Date" id="date"></td>
            <td class="CustomerDenomination" id="customer_denomination"></td>
            <td class="MachinaryModel" id="machinary_model"></td>
            <td class="JobType" id="job_type"></td>
            <td class="CompletionState" id="completion_state"></td>
            </td>
        </tr>
    </template>  

    <script src="/Samurai/dbi.js?<%=timestamp%>"></script>
    <script src="/Samurai/orders.js?<%=timestamp%>"></script>
    <script src="/Samurai/dropDown.js?<%=timestamp%>"></script>
    <script>setInterval(function () {
                            app.ping();
                        }, 60000);</script>
</html>

<%}%>