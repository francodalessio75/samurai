<%@page import="java.util.Date"%>
<%@page import="com.dalessio.samurai.Invoice"%>
+<%@page import="com.dps.dbi.DbResult"%>
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
    
    String firstAmount = "";
    String secondAmount = "";
    String thirdAmount = "";
   
    if(user_id==null){
%>

    <script>window.open("/Samurai/landing.jsp","_self")</script>
    
<%}else
{

     //English date format
    DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    //European date format
    DateTimeFormatter DTFE = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
    
    Invoice invoice = null;
    
    DbResult dbr_invoice = null;
    
    DbResult dbr_invoiceRows = null;
    
    Long invoice_id =  0L;
    
    String date = DTFE.format(LocalDate.now());
    String firstAmountDate = "";
    String secondAmountDate = "";
    String thirdAmountDate = "";
    String number = "";
    String year = "";
    
    //confirm button text
    String confirmButtonText = "CONFERMA CREAZIONE";
    //if invoice_id is not null invoice details must be shown in the page
    try{
        invoice_id = Long.parseLong(request.getParameter("invoice_id"));
    }catch(NumberFormatException ex ){}
    //the page is requested by clicking on an invoices table row in invoices.jsp page
    if( invoice_id != 0L )
    { 
        dbr_invoice = dao.readInvoices( invoice_id,null,null ,null,null);
        dbr_invoiceRows = dao.readInvoiceRows(invoice_id);
        
        invoice = new Invoice(dbr_invoice,dbr_invoiceRows);
 
        StringBuilder dateSB = new StringBuilder( invoice.date);
        dateSB.insert(4, "-");
        dateSB.insert(7,"-");
        date = dateSB.toString();

        StringBuilder firstAmountDateSB = invoice.firstAmountDate == null ? new StringBuilder( "" ) : new StringBuilder( invoice.firstAmountDate);
        if( !firstAmountDateSB.toString().equals(""))
        {
            firstAmountDateSB.insert(4, "-");
            firstAmountDateSB.insert(7,"-");
            firstAmountDate = firstAmountDateSB.toString();
        }

        StringBuilder secondAmountDateSB = invoice.secondAmountDate == null ? new StringBuilder( "" ) : new StringBuilder( invoice.secondAmountDate);
        if( !secondAmountDateSB.toString().equals(""))
        {
            secondAmountDateSB.insert(4, "-");
            secondAmountDateSB.insert(7,"-");
            secondAmountDate = secondAmountDateSB.toString();
        }

        StringBuilder thirdAmountDateSB = invoice.thirdAmountDate == null ? new StringBuilder( "" ) : new StringBuilder( invoice.thirdAmountDate);
        if( !thirdAmountDateSB.toString().equals(""))
        {
            thirdAmountDateSB.insert(4, "-");
            thirdAmountDateSB.insert(7,"-");
            thirdAmountDate = thirdAmountDateSB.toString();
        }

        firstAmount = new BigDecimal(invoice.firstAmount).setScale(2, RoundingMode.HALF_UP)+"";
        secondAmount = new BigDecimal(invoice.secondAmount).setScale(2, RoundingMode.HALF_UP)+"";
        thirdAmount = new BigDecimal(invoice.thirdAmount).setScale(2, RoundingMode.HALF_UP)+"";

        year = dbr_invoice.getInteger("year")+"";
        number = dbr_invoice.getLong("number")+"";
        
        //UPDATE 
        confirmButtonText = "CONFERMA MODIFICHE";
    }
    //sets the taskdate to current day if task_id is null
    else
        date = LocalDate.now().toString();
    
    DbResult dbr_user = dao.readUsers( user_id);
    String user_role = dbr_user.getString("role");
    String userName = dbr_user.getString("firstName") + " " + dbr_user.getString("lastName");
    DbResult dbr_customers = dao.readAllCustomers();

    String lastInvoiceDate = "";
    StringBuilder lastInvoiceDateSB = new StringBuilder( dao.getLastInvoiceDate() );
    if( !lastInvoiceDateSB.equals(""))
    {
        lastInvoiceDateSB.insert(4, "-");
        lastInvoiceDateSB.insert(7,"-");
        lastInvoiceDate = lastInvoiceDateSB.toString();
    }
%>

<html>
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>Workline 2.0</title>
            <link href="/Samurai/common.css" rel="stylesheet">
            <link href="/Samurai/invoices.css" rel="stylesheet">
            <link href="/Samurai/elastictable.css" rel="stylesheet">
            <link href="/Samurai/Resources/font-awesome-4.7.0/css/font-awesome.min.css" rel="stylesheet">
        </head>
        <body <%if(invoice != null)
                {%>
                    onload="app.getInvoiceRows(<%=invoice.invoice_id%>,false);/*Becouse of this methods chain: getInvoicesRows -> checkBoxChanged -> setDuesInputs in invoices.js the
 *                                                                             boolean is necessary to avoid that on the first page opening in read modality amunts dates will be resetted*/
                    /* customer-id is necessary for exemption text lokks at method app.invoiceDetailsChanged at line 262*/
                    app.customer_id = <%=invoice.customer_id%>;
                    "
              <%}%> >
        
            <div class="Header">
                <span class="SWName">Workline 2.0</span><span class="PageTitle"> Fattura </span>
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
                <%if( "admin".equals(user_role) ){%><span class=" Button" onclick="window.open('quote_details.jsp','_self');">PREVENTIVI</span><%}%>
                <%if( "admin".equals(user_role) ){%><span class=" Button" onclick="window.open('creditNote_details.jsp','_self');">NOTE ACCREDITO</span><%}%>
            </div>
            
            <!-- FILTERS SECTION -->
            <div class="Filters">
                
                <div class="Filters_label">RICERCA</div>
                
                <div class="Filters_number_label">NUMERO FATTURA</div>
                <div><input id="numberFilter" ></div>
               
                <select id="customer_select_options" >
                    <option value="" >CLIENTE</option>
                    <%for(int i=0; i<dbr_customers.rowsCount(); i++){%>
                        <option value="<%=dbr_customers.getLong(i,"customer_id")%>"><%=dbr_customers.getString(i,"denomination")%></option>
                    <%}%>
                </select>
                
                <div class="Filters_period_label">PERIODO</div>
                
                <table id="dates" style="border-collapse: collapse;">
                    <tr><td>Da:</td><td><input type="date" id="from_date" max="<%=LocalDate.now().toString()%>"></td></tr>
                    <tr><td>A:</td><td><input type="date" id="to_date" max="<%=LocalDate.now().toString()%>"></td></tr>
                </table>
                                                                                                                            <!-- any straight search by number  needed? -->
                <div id="refresh" class="Button" onclick="app.openInvoicesPage();"><i  id="searchIcon" class="fa fa-search" aria-hidden="true"></i> RICERCA</div>
            <% if ( ( invoice == null && user_role.equals("operator") ) || ( invoice == null && user_role.equals("admin" ) ) || ( invoice != null && user_role.equals("admin" ) )  ){%>
                <div id="confirm_label" class="Filters_label"> OPERAZIONI</div>
                <div id="invoiceConfirmedButton" class="Button Operation Confirm" onclick="app.invoiceConfirmed(<%=invoice_id%>);" <% if ( invoice != null && user_role.equals("operator") ){%>display ="none" <%}%>><%=confirmButtonText%></div>
                <% if ( invoice != null ){%>
                    <div id="pdfButton" class="Button Operation" onclick="window.open('resources/INVOICES/FATTURA_DUESSE_'+<%=number%>+'_'+<%=year%>+'.pdf','_blank');">APRI PDF</div>
                    
                        <a id="xmlButton" href='resources/DIG_INVOICES/<%=dbr_invoice.getString("idTrasmittente_paese")%><%=dbr_invoice.getString("idTrasmittente_codice")%>_<%=dbr_invoice.getLong("number")%><%=dbr_invoice.getInteger("year")%>.xml' download>
                            SCARICA XML
                        </a> 
                    
                    <div id="newInvoiceButton" class="Button Operation" onclick="window.open('invoice_details.jsp','_self');">CREA NUOVA</div>
                <%}%>
                
            <%}%>
              
        </div>

        <!-- PAGE CONTENT -->
        <div class="Content WithFilter">
            <table class="Details_table">

                <tr class="Details_row ">
                    <th class="Detail_header Input" id="invoice">FATTURA</th>
                    <td id="dateNumberRow">
                        <span class="TableLable">Data:</span>
                        <span>
                            <input type="date" id="date_input" value= "<%=date%>" style="width:10rem;" min="<%=lastInvoiceDate%>" max="<%=LocalDate.now().toString()%>">
                        </span>
                    <%if( invoice != null ){%>
                        <span class="TableLable">Numero:</span>
                        <span>
                            <input type="text" id="number_input" value= "<%=invoice.number%> - <%=invoice.year%>" >
                        </span>
                    <%}%>    
                    </td> 
                </tr>

                <tr class="Details_row ">
                    <th class="Detail_header Input" id="customer" >CLIENTE</th>
                    <td>
                        <select onchange = "app.suggestInvoiceFields(null);" id="denomination_select_options" <% if ( invoice != null && user_role.equals("operator") ){%>disabled <%}%>>
                            <option value="" >NOME CLIENTE</option>
                            <%for(int i=0; i<dbr_customers.rowsCount(); i++){%>
                                <option value="<%=dbr_customers.getLong(i,"customer_id")%>" ><%=dbr_customers.getString(i,"denomination")%></option>
                            <%}%>
                        </select> 
                        
                        <script>document.getElementById("denomination_select_options").value= "<%=invoice == null ? "" : invoice.customer_id%>"</script>

                        <input id="address_input" placeholder = " VIA" value= "<%=invoice == null ? "" : dbr_invoice.getString("address") %>" onchange="app.invoiceDetailsChanged('address_input');"  disabled>

                        <input id="houseNumber_input" placeholder = " N° CIVICO" value= "<%=invoice == null ? "" : dbr_invoice.getString("houseNumber") %>" onchange="app.invoiceDetailsChanged('houseNumber_input');"  disabled>

                        <input id="postalCode_input" placeholder = " C.A.P." value= "<%=invoice == null ? "" : dbr_invoice.getString("postalCode") %>" onchange="app.invoiceDetailsChanged('postalCode_input');"  disabled>

                        <input id="city_input" placeholder = " CITTA'" value= "<%=invoice == null ? "" : dbr_invoice.getString("city") %>" onchange="app.invoiceDetailsChanged('city_input');"  disabled>

                        <input id="province_input" placeholder = " PROVINCIA" value= "<%=invoice == null ? "" : dbr_invoice.getString("province") %>" onchange="app.invoiceDetailsChanged('province_input');"  disabled>

                        <input id="vatCode_input" placeholder = " P. IVA" value= "<%=invoice == null ? "" : dbr_invoice.getString("vatCode") %>" onchange="app.invoiceDetailsChanged('vatCode_input');"  disabled>

                        <input id="fiscalCode_input" placeholder = " COD. FISCALE" value= "<%=invoice == null ? "" : dbr_invoice.getString("fiscalCode") %>" onchange="app.invoiceDetailsChanged('fiscalCode_input');"  disabled>
                        <!--Each customer has got his payment conditions that are one with customer register. There's another payment conditions that is realted with the single invoice. So 
                            the application shows as invoice payment conditions customer payment condtions. The user can change them and in any case each invoice will have its payment conditions -->
                        <input id="paymentConditions_input" placeholder = " CONDIZIONI DI PAGAMENTO" value= "<%=invoice == null ? "" : dbr_invoice.getString("paymentConditions") %>" onchange="app.invoiceDetailsChanged('paymentConditions_input');" <% if( invoice != null && user_role.equals("operator") ) { %> disabled <% } %>  >
                        
                    </td> 
                </tr>
                
                <tr class="Details_row">
                    <th class="Detail_header Input" id="vat_exemption" > FATTURA IN ESENZIONE IVA</th>
                    <td ><input class="Checkbox" id="vat_exempt_checkbox" type="checkbox" onchange="app.invoiceDetailsChanged('vat_exempt_checkbox');" <% if ( invoice != null && user_role.equals("operator") ){%>disabled <%}%>/></td>
                    <script>
                        document.getElementById("vat_exempt_checkbox").checked = <%= dbr_invoice == null ? false : dbr_invoice.getBoolean("exempt")%>
                    </script>
                </tr>

                <tr class="Details_row ">
                    <th class="Detail_header Input" id="notes" rows="4" cols="50">NOTE</th>
                    <td><textarea id="notes_input"  onchange="app.invoiceDetailsChanged('notes_input');" <% if ( invoice != null && user_role == "operator" ){%>disabled <%}%> ><%=invoice == null ? "" : invoice.notes%></textarea></td>
                </tr>

            </table>

            <!--------- ELASTIC TABLE ---------->
            <table id="invoiceItemsTable" class="Table">
                <col width="10%">
                <col width="40%">
                <col width="15%">
                <col width="5%">
                <col width="10%">
                <col width="10%">
                <col width="10%">
                <thead>
                    <tr>
                        <th>Codice</th>
                        <th>Descrizione</th>
                        <th>Rif. DDT</th>
                        <th>Quantità</th>
                        <th>Importo Unitario €</th>
                        <th>Totale €</th>
                    <% if ( ( invoice == null && user_role.equals("operator") ) || ( invoice == null && user_role.equals("admin" ) ) || ( invoice != null && user_role.equals("admin" ) )  ){%>
                        <th>Azioni</th>
                    <%}%>
                    </tr>
                </thead>

                <tbody></tbody>
            </table>
            
            <table class="Details_table">
                <tr class="Details_row">
                    <th class="Detail_header Input" id="amounts">SCADENZE</th>
                    <td id="dueDatesRow">
                        <span class="TableLable">1-</span>
                        <input   id="first_amount_input"  min="0"  type="number" step=0.01  onchange="app.refreshDues('first_amount_input', true);"  value= "<%=invoice == null ? "0.00" :  firstAmount %>" disabled>  
                        <span class="TableLable">Data:</span>
                            <input type="date" id="first_amount_date_input" min="<%=LocalDate.now().toString()%>" value= "<%=invoice == null ? LocalDate.now().toString() : firstAmountDate%>" >
                        <% if ( ( invoice == null && user_role.equals("operator") ) || ( invoice == null && user_role.equals("admin" ) ) || ( invoice != null && user_role.equals("admin" ) )  ){%>
                        <span>
                            <input type="checkbox" id="second_amount_checkbox" onchange="app.checkBoxChanged('second_amount_checkbox',true);">
                        </span>
                        <%}%>
                        <span class="TableLable">2-</span>
                            <input   id="second_amount_input" min="0" type="number" step="0.01"  onchange="app.refreshDues('second_amount_input',true);" value= "<%=invoice == null ? "0.00" : secondAmount%>" disabled>  
                        <span class="TableLable">Data:</span>
                            <input type="date" id="second_amount_date_input" min="<%=LocalDate.now().toString()%>"  value= "<%=invoice == null ? "" : secondAmountDate%>" disabled>
                        <span>
                            <input type="checkbox" id="third_amount_checkbox" onchange="app.checkBoxChanged('third_amount_checkbox',true);" disabled>
                        </span>
                        <span class="TableLable">3-</span>
                            <input   id="third_amount_input" min="0" type="number" step=0.01  onchange="app.refreshDues('third_amount_input',true);" value= "<%=invoice == null ? "0.00" : thirdAmount%>" disabled>  
                        <span class="TableLable">Data:</span>
                            <input type="date" id="third_amount_date_input" min="<%=LocalDate.now().toString()%>" value= "<%=invoice == null ? "" : thirdAmountDate%>" disabled>
                    </td> 
                </tr>

                <tr class="Details_row ">
                    <th class="Detail_header Input" id="amounts">IMPORTI</th>
                    <td id="amountsRow">
                        <span class="TableLable">IMPONIBILE</span>
                            <input   id="taxable_amount_input"  type="number" min="0" step=0.01  onchange="app.invoiceDetailsChanged('taxable_amount_input');" value= "<%=invoice == null ? "0.00" :  new BigDecimal(invoice.taxableAmount).setScale(2, RoundingMode.HALF_UP)+""%>" disabled>  
                            <span class="TableLable">ALIQUOTA IVA</span>
                                <select id="vatRate" onchange="app.invoiceDetailsChanged('vatRate');">    
                                    <option value="22" <%if( invoice == null ||  dbr_invoice.getDouble("aliquotaIVA") == 22 ){%>selected="selected"<%}%>>22%</option>
                                    <option value="4" <%if( invoice != null &&  dbr_invoice.getDouble("aliquotaIVA") == 4 ){%>selected="selected"<%}%>>4%</option>
                                    <option value="exempt" <%if( invoice != null &&  dbr_invoice.getDouble("aliquotaIVA") == 0 ){%>selected="selected"<%}%>>ESENTE</option>
                                </select>                                  
                            <input type="number" min="0" step=0.01 id="tax_amount_input"  onchange="app.invoiceDetailsChanged('tax_amount_input');" onwheel="this.blur()" value= "<%=invoice == null ? "0.00" : new BigDecimal(invoice.taxAmount).setScale(2, RoundingMode.HALF_UP)+""%>" disabled>
                        <span class="TableLable">TOTALE FATTURA</span>
                            <input type="number" min="0" id="total_amount_input"  onchange="app.invoiceDetailsChanged('total_amount_input');" onwheel="this.blur()" value= "<%=invoice == null ? "0.00" : new BigDecimal(invoice.totalAmount).setScale(2, RoundingMode.HALF_UP)+""%>" disabled>
                    </td> 
                </tr>

            </table>     

        </div>

            <div class="Footer">
                <span class="User_name"> Utente : <%=userName%></span>
                <span class="Footer_message"></span>
            </div>

        </body>
        
                <--------- ELASTIC TABLE ITEM TEMPLATE ---------->
        <template id="item_template">
            <tr>
                <td class="Code"><input id="code" type="text" placeholder="CODICE" onchange = "app.suggestInvoiceFields(this);" <% if ( invoice != null && user_role.equals("operator") ){%>disabled <%}%>></td>
                <td class="Description"><textarea id="description" type="text" placeholder="DESCRIZIONE" onchange="app.checkCustomerChoosen(this);" <% if ( invoice != null && user_role.equals("operator") ){%>disabled <%}%>></textarea></td>
                <td class="DeliveryNoteReference"><input id="deliveryNoteReference"  type="text" placeholder="RIF. DDT" disabled <% if ( invoice != null && user_role.equals("operator") ){%>disabled <%}%>></td>
                <td class="Quantity"><input id="quantity" type="number" min="1" value="1" onchange="app.refreshAmounts(null);app.checkCustomerChoosen(this);"<% if ( invoice != null && user_role.equals("operator") ){%>disabled <%}%>></td>
                <td class="SingleAmount"><input id="singleAmount" type="number" onwheel="this.blur()" min="0" onchange="app.refreshAmounts(null);app.checkCustomerChoosen(this);" value="0.000" ></td>
                <td class="TotalAmount"><input id="totalAmount" type="number" onwheel="this.blur()" min="0"  onchange="app.refreshAmounts(null);" value="0.00" disabled></td>
            <% if ( ( invoice == null && user_role.equals("operator") ) || ( invoice == null && user_role.equals("admin" ) ) || ( invoice != null && user_role.equals("admin" ) )  ){%>
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
            <%}%>
            </tr>
        </template>
                
        <-- sezione script -->
        <script src="invoiceElasticTable.js?<%=timestamp%>"></script>
        <script src="/Samurai/dbi.js?<%=timestamp%>"></script>
        <script src="/Samurai/invoices.js?<%=timestamp%>"></script>
        <script src="/Samurai/dropDown.js?<%=timestamp%>"></script> 
        <script>app.addItemRowBefore(null);</script>
        <script>setInterval(function(){app.ping();},60000);</script>
        <script>input = document.querySelector(".SingleAmount");
                input.addEventListener("mousewheel", function(evt){ evt.preventDefault(); });
        </script>
        
        <!--script>
            document.getElementById("customer_select_options").value = "22";
            document.getElementById("transport_responsable_input").value = "PROVA";
            document.getElementById("transport_reason_input").value = "PROVA";
            document.getElementById("goods_exterior_aspect_input").value = "PROVA";
        </script-->
    </html>

<%}%>