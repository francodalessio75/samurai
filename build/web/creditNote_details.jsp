<%@page import="java.util.Date"%>
<%@page import="com.dalessio.samurai.CreditNote"%>
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
    
    CreditNote creditNote = null;
    
    DbResult dbr_creditNote = null;
    
    DbResult dbr_creditNoteRows = null;
    
    Long creditNote_id =  0L;
    
    String date = DTFE.format(LocalDate.now());
    String number = "";
    String year = "";
    
    //confirm button text
    String confirmButtonText = "CONFERMA CREAZIONE";
    //if invoice_id is not null invoice details must be shown in the page
    try{
        creditNote_id = Long.parseLong(request.getParameter("creditNote_id"));
    }catch(NumberFormatException ex ){}
    //the page is requested by clicking on an invoices table row in invoices.jsp page
    if( creditNote_id != 0L )
    { 
        dbr_creditNote = dao.readCreditNotes( creditNote_id,null,null ,null,null);
        dbr_creditNoteRows = dao.readCreditNoteRows(creditNote_id);
        
        creditNote = new CreditNote(dbr_creditNote,dbr_creditNoteRows);
 
        StringBuilder dateSB = new StringBuilder( creditNote.date);
        dateSB.insert(4, "-");
        dateSB.insert(7,"-");
        date = dateSB.toString();

        year = dbr_creditNote.getInteger("year")+"";
        number = dbr_creditNote.getLong("number")+"";
        
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

    String lastCreditNoteDate = "";
    StringBuilder lastCreditNoteDateSB = new StringBuilder( dao.getLastCreditNoteDate() == null ? LocalDate.now().minusYears(1).toString() : dao.getLastCreditNoteDate() );//just while there aren't any credit notes
    if( lastCreditNoteDateSB != null && !lastCreditNoteDateSB.equals("") )
    {
        lastCreditNoteDateSB.insert(4, "-");
        lastCreditNoteDateSB.insert(7,"-");
        lastCreditNoteDate = lastCreditNoteDateSB.toString();
    }
%>

<html>
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>Workline 2.0</title>
            <link href="/Samurai/common.css" rel="stylesheet">
            <link href="/Samurai/creditNotes.css" rel="stylesheet">
            <link href="/Samurai/elastictable.css" rel="stylesheet">
            <link href="/Samurai/Resources/font-awesome-4.7.0/css/font-awesome.min.css" rel="stylesheet">
        </head>
        <body <%if(creditNote != null)
                {%>
                    onload="app.getCreditNoteRows(<%=creditNote.creditNote_id%>);/*Becouse of this methods chain: getInvoicesRows -> checkBoxChanged -> setDuesInputs in invoices.js the
 *                                                                             boolean is necessary to avoid that on the first page opening in read modality amunts dates will be resetted*/
                    /* customer-id is necessary for exemption text lokks at method app.invoiceDetailsChanged at line 262*/
                    app.customer_id = <%=creditNote.customer_id%>;
                    "
              <%}%> >
        
            <div class="Header">
                <span class="SWName">Workline 2.0</span><span class="PageTitle"> Nota di Accredito </span>
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

            </div>
            
            <!-- FILTERS SECTION -->
            <div class="Filters">
                
                <div class="Filters_label">RICERCA</div>
               
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
                <div id="refresh" class="Button" onclick="app.openCreditNotesPage();"><i  id="searchIcon" class="fa fa-search" aria-hidden="true"></i> RICERCA</div>
            <% if ( ( creditNote == null && user_role.equals("operator") ) || ( creditNote == null && user_role.equals("admin" ) ) || ( creditNote != null && user_role.equals("admin" ) )  ){%>
                <div id="confirm_label" class="Filters_label"> OPERAZIONI</div>
                <div id="creditNoteConfirmedButton" class="Button Operation Confirm" onclick="app.creditNoteConfirmed(<%=creditNote_id%>);" <% if ( creditNote != null && user_role.equals("operator") ){%>display ="none" <%}%>><%=confirmButtonText%></div>
                <% if ( creditNote != null ){%>
                    <div id="pdfButton" class="Button Operation" onclick="window.open('resources/CREDIT_NOTES/NOTA_ACCREDITO_DUESSE_'+<%=number%>+'_'+<%=year%>+'.pdf','_blank');">APRI PDF</div>
                    
                        <a id="xmlButton" href='resources/DIG_CREDIT_NOTES/IT08245660017_c<%=dbr_creditNote.getLong("number")%><%=dbr_creditNote.getInteger("year")%>.xml' download>
                            SCARICA XML
                        </a> 
                    
                    <div id="newCreditNoteButton" class="Button Operation" onclick="window.open('creditNote_details.jsp','_self');">CREA NUOVA</div>
                <%}%>
                
            <%}%>
              
        </div>

        <!-- PAGE CONTENT -->
        <div class="Content WithFilter">
            <table class="Details_table">

                <tr class="Details_row ">
                    <th class="Detail_header Input" id="creditNote">NOTE DI ACCREDITO</th>
                    <td id="dateNumberRow">
                        <span class="TableLable">Data:</span>
                        <span>
                            <input type="date" id="date_input" value= "<%=date%>" style="width:10rem;" min="<%=lastCreditNoteDate%>" max="<%=LocalDate.now().toString()%>">
                        </span>
                    <%if( creditNote != null ){%>
                        <span class="TableLable">Numero:</span>
                        <span>
                            <input type="text" id="number_input" value= "<%=creditNote.number%> - <%=creditNote.year%>" >
                        </span>
                    <%}%>    
                    </td> 
                </tr>

                <tr class="Details_row ">
                    <th class="Detail_header Input" id="customer" >CLIENTE</th>
                    <td>
                        <select onchange = "app.suggestCreditNoteFields(null);" id="denomination_select_options" <% if ( creditNote != null && user_role.equals("operator") ){%>disabled <%}%>>
                            <option value="" >NOME CLIENTE</option>
                            <%for(int i=0; i<dbr_customers.rowsCount(); i++){%>
                                <option value="<%=dbr_customers.getLong(i,"customer_id")%>" ><%=dbr_customers.getString(i,"denomination")%></option>
                            <%}%>
                        </select> 
                        
                        <script>document.getElementById("denomination_select_options").value= "<%=creditNote == null ? "" : creditNote.customer_id%>"</script>

                        <input id="address_input" placeholder = " VIA" value= "<%=creditNote == null ? "" : dbr_creditNote.getString("address") %>" onchange="app.creditNoteDetailsChanged('address_input');"  disabled>

                        <input id="houseNumber_input" placeholder = " N° CIVICO" value= "<%=creditNote == null ? "" : dbr_creditNote.getString("houseNumber") %>" onchange="app.credtiNoteDetailsChanged('houseNumber_input');"  disabled>

                        <input id="postalCode_input" placeholder = " C.A.P." value= "<%=creditNote == null ? "" : dbr_creditNote.getString("postalCode") %>" onchange="app.creditNoteDetailsChanged('postalCode_input');"  disabled>

                        <input id="city_input" placeholder = " CITTA'" value= "<%=creditNote == null ? "" : dbr_creditNote.getString("city") %>" onchange="app.creditNoteDetailsChanged('city_input');"  disabled>

                        <input id="province_input" placeholder = " PROVINCIA" value= "<%=creditNote == null ? "" : dbr_creditNote.getString("province") %>" onchange="app.creditNoteDetailsChanged('province_input');"  disabled>

                        <input id="vatCode_input" placeholder = " P. IVA" value= "<%=creditNote == null ? "" : dbr_creditNote.getString("vatCode") %>" onchange="app.creditNoteDetailsChanged('vatCode_input');"  disabled>

                        <input id="fiscalCode_input" placeholder = " COD. FISCALE" value= "<%=creditNote == null ? "" : dbr_creditNote.getString("fiscalCode") %>" onchange="app.creditNoteDetailsChanged('fiscalCode_input');"  disabled>
                        
                    </td> 
                </tr>
                
                <tr class="Details_row">
                    <th class="Detail_header Input" id="vat_exemption" > NOTA ACCREDITO IN ESENZIONE IVA</th>
                    <td ><input class="Checkbox" id="vat_exempt_checkbox" type="checkbox" onchange="app.creditNoteDetailsChanged('vat_exempt_checkbox');" <% if ( creditNote != null && user_role.equals("operator") ){%>disabled <%}%>/></td>
                    <script>
                        document.getElementById("vat_exempt_checkbox").checked = <%= dbr_creditNote == null ? false : dbr_creditNote.getBoolean("exempt")%>
                    </script>
                </tr>

                <tr class="Details_row ">
                    <th class="Detail_header Input" id="notes" rows="4" cols="50">NOTE</th>
                    <td><textarea id="notes_input"  onchange="app.creditNoteDetailsChanged('notes_input');" <% if ( creditNote != null && user_role == "operator" ){%>disabled <%}%> ><%=creditNote == null ? "" : creditNote.notes%></textarea></td>
                </tr>

            </table>

            <!--------- ELASTIC TABLE ---------->
            <table id="creditNoteItemsTable" class="Table">
                <col width="65%">
                <col width="5%">
                <col width="10%">
                <col width="10%">
                <col width="10%">
                <thead>
                    <tr>
                        <th>Descrizione</th>
                        <th>Quantità</th>
                        <th>Importo Unitario €</th>
                        <th>Totale €</th>
                    <% if ( ( creditNote == null && user_role.equals("operator") ) || ( creditNote == null && user_role.equals("admin" ) ) || ( creditNote != null && user_role.equals("admin" ) )  ){%>
                        <th>Azioni</th>
                    <%}%>
                    </tr>
                </thead>

                <tbody></tbody>
            </table>
            
            <table class="Details_table">
                
                <tr class="Details_row ">
                    <th class="Detail_header Input" id="amounts">IMPORTI</th>
                    <td id="amountsRow">
                        <span class="TableLable">IMPONIBILE</span>
                            <input   id="taxable_amount_input"  type="number" min="0" step=0.01  onchange="app.creditNoteDetailsChanged('taxable_amount_input');" value= "<%=creditNote == null ? "0.00" :  creditNote.taxableAmount%>" disabled>  
                            <span class="TableLable">ALIQUOTA IVA</span>
                                <select id="vatRate" onchange="app.creditNoteDetailsChanged('vatRate');">    
                                    <option value="22" <%if( creditNote == null ||  dbr_creditNote.getDouble("aliquotaIVA") == 22 ){%>selected="selected"<%}%>>22%</option>
                                    <option value="4" <%if( creditNote != null &&  dbr_creditNote.getDouble("aliquotaIVA") == 4 ){%>selected="selected"<%}%>>4%</option>
                                    <option value="exempt" <%if( creditNote != null &&  dbr_creditNote.getDouble("aliquotaIVA") == 0 ){%>selected="selected"<%}%>>ESENTE</option>
                                </select>                                  
                            <input type="number" min="0" step=0.01 id="tax_amount_input"  onchange="app.creditNoteDetailsChanged('tax_amount_input');" onwheel="this.blur()" value= "<%=creditNote == null ? "0.00" : creditNote.taxAmount%>" disabled>
                        <span class="TableLable">TOTALE NOTA ACCREDITO</span>
                            <input type="number" min="0" id="total_amount_input"  onchange="app.creditNoteDetailsChanged('total_amount_input');" onwheel="this.blur()" value= "<%=creditNote == null ? "0.00" : creditNote.totalAmount%>" disabled>
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
                <td class="Description"><textarea id="description" type="text" placeholder="DESCRIZIONE" onchange="app.checkCustomerChoosen(this);" <% if ( creditNote != null && user_role.equals("operator") ){%>disabled <%}%>></textarea></td>
                <td class="Quantity"><input id="quantity" type="number" min="1" value="1" onchange="app.refreshAmounts(null);app.checkCustomerChoosen(this);"<% if ( creditNote != null && user_role.equals("operator") ){%>disabled <%}%>></td>
                <td class="SingleAmount"><input id="singleAmount" type="number" min="0" onwheel="this.blur()" onchange="app.refreshAmounts(null);app.checkCustomerChoosen(this);" value="0.000" ></td>
                <td class="TotalAmount"><input id="totalAmount" type="number" min="0"  onchange="app.refreshAmounts(null);" value="0.00" disabled></td>
            <% if ( ( creditNote == null && user_role.equals("operator") ) || ( creditNote == null && user_role.equals("admin" ) ) || ( creditNote != null && user_role.equals("admin" ) )  ){%>
                <td class="Actions">
                    =>
                    <span class="ActionMenu">
                        <span class="Button" onclick="app.addCNItemRowBefore(this.parentNode.parentNode.parentNode);">INSERISCI RIGA SOPRA</span>
                        <span class="Button" onclick="app.addCNItemRowAfter(this.parentNode.parentNode.parentNode);">INSERISCI RIGA SOTTO</span>
                        <span class="Button" onclick="app.moveCNItemRowUp(this.parentNode.parentNode.parentNode);">SPOSTA IN ALTO</span>
                        <span class="Button" onclick="app.moveCNItemRowDown(this.parentNode.parentNode.parentNode);">SPOSTA IN BASSO</span>
                        <span class="Button" onclick="app.deleteCNItemRow(this.parentNode.parentNode.parentNode);">ELIMINA RIGA</span>
                    </span>
                </td>
            <%}%>
            </tr>
        </template>
                
        <-- sezione script -->
        <script src="creditNoteElasticTable.js?<%=timestamp%>"></script>
        <script src="/Samurai/dbi.js?<%=timestamp%>"></script>
        <script src="/Samurai/creditNotes.js?<%=timestamp%>"></script>
        <script src="/Samurai/dropDown.js?<%=timestamp%>"></script> 
        <script>app.addCNItemRowBefore(null);</script>
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