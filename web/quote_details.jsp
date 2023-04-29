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
    Long timestamp = new Date().getTime();

    Long user_id = (Long) request.getSession().getAttribute("user_id");

    //String amount = ""; Eliminated the 11th July 2019 becouse the total not always is correct, eg. when different versions of the same product are offered  
    String number = "";

    if (user_id == null) {
%>

<script>window.open("/Samurai/landing.jsp", "_self")</script>

<%} else {
    DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");

    Quote quote = null;

    DbResult dbr_titles = dao.readTitles(null);

    Long quote_id = 0L;

    String date = "";//DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDate.now());

    //confirm button text
    String confirmButtonText = "";
    //if invoice_id is not null invoice details must be shown in the page
    try {
        quote_id = Long.parseLong(request.getParameter("quote_id"));
    } catch (NumberFormatException ex) {
    }

    Boolean duplicate = Boolean.parseBoolean(request.getParameter("duplicate"));

    Boolean hasAttachments = false;

    //If it is an update
    if (quote_id != 0L && !duplicate) {

        quote = dao.readQuote(quote_id);
        date = DateTimeFormatter.ISO_LOCAL_DATE.format(quote.date);
        number = quote.number + "-" + date.substring(2, 4);
        hasAttachments = dao.readQuoteAttachments(quote_id, user_id).rowsCount() > 0;
        confirmButtonText = "CONFERMA MODIFICHE";
    } //if it is a duplication
    else if (quote_id != 0L && duplicate) {

        quote = dao.readQuote(quote_id);
        hasAttachments = dao.readQuoteAttachments(quote_id, user_id).rowsCount() > 0;
        confirmButtonText = "CONFERMA CREAZIONE";
        date = LocalDate.now().toString();

    } //if is a creation
    else if (quote_id == 0L) {
        confirmButtonText = "CONFERMA CREAZIONE";
        date = LocalDate.now().toString();
    }

    DbResult dbr_user = dao.readUsers(user_id);
    String user_role = dbr_user.getString("role");
    String userName = dbr_user.getString("firstName") + " " + dbr_user.getString("lastName");
    DbResult dbr_customers = dao.readAllCustomers();

%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Workline 2.0</title>
        <link href="/Samurai/common.css" rel="stylesheet">
        <link href="/Samurai/elastictable.css" rel="stylesheet">
        <link href="/Samurai/quotes.css" rel="stylesheet">
        <link href="/Samurai/Resources/font-awesome-4.7.0/css/font-awesome.min.css" rel="stylesheet">
    </head>
    <body
        <%if (quote != null && !duplicate) {%>
        onload="app.customer_id = <%=quote.customer_id%>;
                        app.getQuoteRows(<%=quote.quote_id%>);"
        <%}%>
        <%if (quote != null && duplicate) {%>
        onload="app.customer_id = null; app.getQuoteRows(<%=quote.quote_id%>);"
        <%}%>
    >
        
        <div class="loader" id="loader"></div>
        <div id="modal" class="modal"></div>
        
        <div class="backdrop"><h1 class="backdrop__notification">ELABORAZIONE IN CORSO...</h1></div> 
        <div class="Header">
            <span class="SWName">Workline 2.0</span><span class="PageTitle"> Preventivo </span>
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
            <%if ("admin".equals(user_role)) {%><span class=" Button" onclick="window.open('creditNote_details.jsp', '_self');">NOTE ACCREDITO</span><%}%>
        </div>

        <!-- FILTERS SECTION -->
        <div class="Filters" >

            <div class="Filters_label">RICERCA</div>

            <div class="Filters_number_label">NUMERO PREVENTIVO</div>
            <div><input id="numberFilter" ></div>

            <select id="customer_select_options" >
                <option value="" >CLIENTE</option>
                <%for (int i = 0; i < dbr_customers.rowsCount(); i++) {%>
                <option value="<%=dbr_customers.getLong(i, "customer_id")%>"><%=dbr_customers.getString(i, "denomination")%></option>
                <%}%>
            </select>

            <div class="Filters_period_label">PERIODO</div>

            <table id="dates" style="border-collapse: collapse;">
                <tr><td>Da:</td><td><input type="date" id="from_date" max="<%=DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDate.now())%>"></td></tr>
                <tr><td>A:</td><td><input type="date" id="to_date" max="<%=DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDate.now())%>"></td></tr>
            </table>



            <!-- any straight search by number  needed? -->
            <div id="refresh" class="Button" onclick="app.openQuotesPage();"><i  id="searchIcon" class="fa fa-search" aria-hidden="true"></i> RICERCA</div>


            <div id="confirm_label" class="Filters_label"> OPERAZIONI</div>
            <div id="quoteConfirmedButton" class="Button Operation Confirm" onclick="app.quoteConfirmed(<%=quote_id%>, <%=duplicate%>)" ><%=confirmButtonText%></div>
            <% if (quote != null && !duplicate) {%>
            <div id="pdfButton" class="Button Operation" onclick="window.open('resources/QUOTES/PREVENTIVO_DUESSE_<%=quote.number%>_<%=date.substring(2, 4)%>.pdf', '_blank');">APRI PDF</div>
            <% if (hasAttachments) {%><div id="mergedPdfButton" class="Button Operation" onclick="window.open('resources/QUOTES/PDF_UNICO_PREVENTIVO_DUESSE_<%=quote.number%>_<%=date.substring(2, 4)%>.pdf', '_blank');">UNICO PDF</div><%}%>
            <div id="newQuoteButton" class="Button Operation" onclick="window.open('quote_details.jsp', '_self');">CREA NUOVO</div>
            <div id="duplicateQuoteButton" class="Button Operation" onclick="window.open('quote_details.jsp?quote_id=<%=quote.quote_id%>&duplicate=true', '_self');">DUPLICA</div>
            <%}%>  

        </div>
        <!-- PAGE CONTENT -->
        <div class="Content WithFilter">
            <table class="Details_table">

                <tr class="Details_row ">
                    <th class="Detail_header Input" id="quote" data-user_id="<%=user_id%>">PREVENTIVO</th>
                    <td id="dateNumberRow">
                        <span class="TableLable">Data:</span>
                        <span>
                            <input type="date" id="date_input" value= "<%=date%>"  >
                        </span>
                        <%if (quote != null && !duplicate) {%>
                        <span class="TableLable">Numero:</span>
                        <span>
                            <input type="text" id="number_input" value= "<%=number%>" disabled>
                        </span>
                        <%}%>    
                    </td> 
                </tr>

                <tr class="Details_row ">
                    <th class="Detail_header Input" id="customer" >CLIENTE</th>
                    <td>
                        <select onchange = "app.suggestQuoteFields();" id="denomination_select_options">
                            <option value="" >NOME CLIENTE</option>
                            <%for (int i = 0; i < dbr_customers.rowsCount(); i++) {%>
                            <option value="<%=dbr_customers.getLong(i, "customer_id")%>" ><%=dbr_customers.getString(i, "denomination")%></option>
                            <%}%>
                        </select> 

                        <script>document.getElementById("denomination_select_options").value = "<%= quote != null && !duplicate ? quote.customer_id : ""%>"</script>

                        <input id="address_input" placeholder = " VIA" value= "<%= quote != null && !duplicate ? quote.address : ""%>" onchange="app.quoteDetailsChanged('address_input');" >

                        <input id="houseNumber_input" placeholder = " N° CIVICO" value= "<%=quote != null && !duplicate ? quote.houseNumber : ""%>" onchange="app.quoteDetailsChanged('houseNumber_input');">

                        <input id="postalCode_input" placeholder = " C.A.P." value= "<%=quote != null && !duplicate ? quote.postalCode : ""%>" onchange="app.quoteDetailsChanged('postalCode_input');">

                        <input id="city_input" placeholder = " CITTA'" value= "<%=quote != null && !duplicate ? quote.city : ""%>" onchange="app.quoteDetailsChanged('city_input');" >

                        <input id="province_input" placeholder = " PROVINCIA" value= "<%=quote != null && !duplicate ? quote.province : ""%>" onchange="app.quoteDetailsChanged('province_input');" >

                    </td> 
                </tr>

                <tr class="Details_row">
                    <th class="Detail_header Input" id="forAttention">
                        ALLA CORTESE ATTENZIONE
                    </th>
                    <td id="forAttentionRow">
                        <span>
                            <select id="firstTitle_select_options" onchange="app.quoteDetailsChanged('firstTitle_select_options');" >
                                <%for (int i = 0; i < dbr_titles.rowsCount(); i++) {%>
                                <option value="<%=dbr_titles.getLong(i, "title_id")%>" ><%=dbr_titles.getString(i, "title")%></option>
                                <%}%>
                            </select>
                        </span>
                        <input   id="firstForAttention_input" onchange="app.quoteDetailsChanged('firstForAttention_input');" value="<%=quote == null || duplicate ? "" : quote.firstForAttention%>">
                        <span>
                            <select id="secondTitle_select_options" onchange="app.quoteDetailsChanged('secondTitle_select_options');" >
                                <%for (int i = 0; i < dbr_titles.rowsCount(); i++) {%>
                                <option value="<%=dbr_titles.getLong(i, "title_id")%>" ><%=dbr_titles.getString(i, "title")%></option>
                                <%}%>
                            </select>
                        </span>
                        <input   id="secondForAttention_input" onchange="app.quoteDetailsChanged('secondForAttention_input');" value="<%=quote == null || duplicate ? "" : quote.secondForAttention%>">
                        <script>document.getElementById("firstTitle_select_options").value = "<%= quote != null && !duplicate ? quote.firstTitle_id : ""%>"</script>
                        <script>document.getElementById("secondTitle_select_options").value = "<%= quote != null && !duplicate ? quote.secondTitle_id : ""%>"</script>
                    </td>
                </tr>


                <tr class="Details_row">
                    <th class="Detail_header Input" id="subject">
                        OGGETTO:
                    </th>
                    <td id="subjectRow">
                        <input id="subject_input" onchange="app.quoteDetailsChanged('subject_input');" value="<%= quote != null ? quote.subject : ""%>">
                    </td>
                </tr>

                <!--            Eliminated the 11th July 2019 becouse the total not always is correct, eg. when different versions of the same product are offered                   
                                <tr class="Details_row ">
                                    <th class="Detail_header Input" id="amounts">IMPORTO</th>
                                    <td id="amountsRow">
                                        <span class="TableLable">TOTALE PREVENTIVO</span>
                                            <input type="number" min="0" id="amount_input"  step=0.01  value= "<--%=quote == null ? "0.00" : amount%>" >
                                    </td> 
                                </tr>-->

                <%if (quote != null && !duplicate) {%>
                <tr class="Details_row ">
                    <th class="Detail_header Input" id="notes">ALLEGA</th>
                    <td>
                        <input id="quoteUploadChooser" name="file" type="file">
                        <input class="Button Operation" id="submitAttach" type="submit" value="Carica Allegato" onclick="app.uploadQuoteAttachment(<%=quote.quote_id%>);">
                    </td>
                </tr>
                <%}%>

            </table>

            <% if (hasAttachments) {%>    
            <div id="attachmentsTableHeader">ALLEGATI</div>
            <%}%>

            <%if (quote != null && !duplicate) {%>
            <table class="Table" id="attachments">
                <col width="20%">
                <col width="60%">
                <col width="20%">
                <thead>
                <th>DATA ALLEGATO</th>
                <th>NOME</th>
                <th>AZIONI</th>
                </thead>
                <tbody></tbody>
            </table>
            <%}%>

            <div id="quoteBodyHeader">CORPO DEL PREVENTIVO</div>
            <div id="attachmentsTableInfo">Per andare a capo: inserisci *. Per il grassetto: # porzione di testo # </div>

            <!--------- ELASTIC TABLE ---------->
            <table id="quoteRowsTable" class="Table">
                <col width="80%">
                <col width="10%">
                <col width="10%">
                <thead>
                    <tr>
                        <th>DESCRIZIONE</th>
                        <th>IMPORTO</th>
                        <th>AZIONI</th>
                    </tr>
                </thead>
                <tbody></tbody>
            </table>

        </div>

        <div class="Footer">
            <span class="User_name"> Utente : <%=userName%></span>
            <span class="Footer_message"></span>
        </div>

    </body>

    <!--------- ELASTIC TABLE ITEM TEMPLATE ---------->
    <template id="row_template">
        <tr>
            <td class="Description"><textarea id="description" type="text" placeholder="DESCRIZIONE"></textarea></td>
            <td class="RowAmount"><input id="rowAmount" type="number" min="0"  step=0.01 onchange="app.refreshAmounts();" onwheel="this.blur()" value="0.00" ></td>
            <td class="Actions">
                =>
                <span class="ActionMenu">
                    <span class="Button" onclick="app.addItemRowBefore(this.parentNode.parentNode.parentNode);">INSERISCI RIGA SOPRA</span>
                    <span class="Button" onclick="app.addItemRowAfter(this.parentNode.parentNode.parentNode);">INSERISCI RIGA SOTTO</span>
                    <span class="Button" onclick="app.moveItemRowUp(this.parentNode.parentNode.parentNode);">SPOSTA IN ALTO</span>
                    <span class="Button" onclick="app.moveItemRowDown(this.parentNode.parentNode.parentNode);">SPOSTA IN BASSO</span>
                    <span class="Button" onclick="app.deleteItemRow(this.parentNode.parentNode.parentNode);">ELIMINA RIGA</span>
                </span>
            </td>
        </tr>
    </template>

    <!--    ATTACHMENTS TEMPLATE -->
    <template id="quote_attachments_template">
        <tr class="QuoteRow">
            <td class="Date" id="date"></td>
            <td class="CurrentFileName" id="currentFileName"></td>
            <td class="Actions" id="actions"></td>
        </tr>
    </template>

    <!-- sezione script -->
    <script src="quoteElasticTable.js?<%=timestamp%>"></script>
    <script src="/Samurai/dbi.js?<%=timestamp%>"></script>
    <script src="/Samurai/quotes.js?<%=timestamp%>"></script>
    <script src="/Samurai/dropDown.js?<%=timestamp%>"></script> 
    <script>app.addItemRowBefore(null);</script>
    <script>setInterval(function () {
            app.ping();
        }, 60000);</script>
    <script>input = document.querySelector(".RowAmount");
        input.addEventListener("mousewheel", function (evt) {
            evt.preventDefault();
        });
    </script>

</html>

<%}%>