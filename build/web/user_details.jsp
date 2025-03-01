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
    //logged user data
    DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
    DbResult dbr_user = dao.readUsers(user_id);
    String userName = dbr_user.getString("firstName") + " " + dbr_user.getString("lastName");//logged user
    String user_role = dbr_user.getString("role");
    //data of selected table ro user in users.jsp
    DbResult dbr_selected_user = dao.readUsers(Long.parseLong(request.getParameter("selected_user_id")));
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Workline 2.0</title>
        <link href="/Samurai/common.css" rel="stylesheet">
    </head>
    <body>

        <div class="loader" id="loader"></div>
        <div id="modal" class="modal"></div>

        <div class="Header">
            <span class="SWName">Workline 2.0</span><span class="PageTitle"> Scheda Operatore </span>
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
            <%if ("admin".equals(user_role)) {%><span class=" Button" onclick="window.open('quote_details.jsp', '_self');">PREVENTIVI</span><%}%>
            <%if ("admin".equals(user_role)) {%><span class=" Button" onclick="window.open('creditNote_details.jsp', '_self');">NOTE ACCREDITO</span><%}%>
        </div>


        <div class="Content">
            <table class="Details_table">
                <tbody>
                    <!--table class="Details_table"-->
                    <tr class="Details_row">
                        <th id="first_name" class="Detail_header">NOME</th><td><input id='first_name_input'  value="<%=dbr_selected_user.getString("firstName")%>" onchange="app.userDetailsChanged('#first_name_input');"></td>
                    </tr>
                    <tr class="Details_row">
                        <th id="last_name" class="Detail_header">COGNOME</th><td><input id='last_name_input' value="<%=dbr_selected_user.getString("lastName")%>" onchange="app.userDetailsChanged('#last_name_input');"></td>
                    </tr>
                    <tr class="Details_row">
                        <th id="phone" class="Detail_header">TELEFONO</th><td><input id='phone_input'  value="<%=dbr_selected_user.getString("phoneNumber")%>" onchange="app.userDetailsChanged('#phone_input');"></td>
                    </tr>
                    <tr class="Details_row">
                        <th id="mobile" class="Detail_header">CELLULARE</th><td><input id='mobile_input'  value="<%=dbr_selected_user.getString("cellNumber")%>" onchange="app.userDetailsChanged('#mobile_input');"></td>
                    </tr>
                    <tr class="Details_row">
                        <th id="e_mail" class="Detail_header">E-MAIL</th><td><input id='e_mail_input'  value="<%=dbr_selected_user.getString("email")%>" onchange="app.userDetailsChanged('#e_mail_input');"></td>
                    </tr>
                    <tr class="Details_row">
                        <th id="notes" class="Detail_header">NOTE</th><td><input id='notes_input'  value="<%=dbr_selected_user.getString("notes")%>" onchange="app.userDetailsChanged('#notes_input');"></td>
                    </tr>
                    <tr class="Details_row">
                        <th id="username" class="Detail_header">USERNAME</th><td><input id='username_input'  value="<%=dbr_selected_user.getString("username")%>" onchange="app.userDetailsChanged('#username_input');"></td>
                    </tr>
                    <tr class="Details_row">
                        <th id="password" class="Detail_header">PASSWORD</th><td><input type="password" id='password_input'  value="<%=dbr_selected_user.getString("password")%>" onchange="app.userDetailsChanged('#password_input');"></td>
                    </tr>
                    <tr class="Details_row">
                        <th id="hourlyCost" class="Detail_header">COSTO ORARIO</th><td><input   id="hourlyCost_input"  type="number" step=0.01  onchange="app.userDetailsChanged('#hourlyCost_input');" value="<%=dbr_selected_user.getDouble("hourlyCost")%>"></td>
                    </tr>
                    <tr class="Details_row">
                        <th id="role" class="Detail_header">RUOLO</th>
                        <td>
                            <select id="role_select_options" onchange="app.userDetailsChanged('#role_select_options');" >
                                <option value="admin">Amministratore</option>
                                <option value="operator">Operatore</option>
                                <option value="consultant">Consulente</option>
                            </select>
                            <script>document.getElementById("role_select_options").value = "<%=dbr_selected_user.getString("role")%>"</script>
                        </td>
                    </tr>
                    <tr class="Details_row">
                        <th id="active" class="Detail_header">STATO</th><td>
                            <!--input id='active_input' onchange="app.userDetailsChanged('#active_input');"-->
                            <select id="active_select_options" onchange="app.userDetailsChanged('#active_select_options');">
                                <option value="true">Attivo</option>
                                <option value="false">Sospeso</option>
                            </select>
                            <script>document.getElementById("active_select_options").value = "<%=dbr_selected_user.getBoolean("active")%>"</script>
                        </td>
                    </tr>
                    <!--tr class="Details_row">
                        <th id="fiscal_code" class="Detail_header">CODICE FISCALE</th><td><input id='fiscal_code_input' value=<!--%=dbr_selected_user.getString("fiscalCode")%> onchange="app.userDetailsChanged('#fiscal_code_input');"></td>
                    </tr>
                    <!--/table-->
                </tbody>
            </table>   
            <span id="updateUserButton" class="Button Operation Update" onclick="app.userUpdated(<%=request.getParameter("selected_user_id")%>);">CONFERMA MODIFICHE</span>
        </div>

        <div class="Footer">
            <span class="User_name"> Utente : <%=userName%></span>
            <span class="Footer_message"></span>
        </div>

    </body>

    <script src="/Samurai/dbi.js?<%=timestamp%>"></script>
    <script src="/Samurai/users.js?<%=timestamp%>"></script>
    <script src="/Samurai/dropDown.js?<%=timestamp%>"></script>
    <script>setInterval(function () {
                    app.ping();
                }, 60000);</script>
</html>

<%}%>