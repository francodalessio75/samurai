<%@page import="com.dalessio.samurai.DeliveryNote"%>
<%@page import="com.dps.dbi.DbResult"%>
<%@page import="com.dps.dbi.DbResultsBundle"%>
<%@page import="com.dalessio.samurai.DataAccessObject"%>
<%@page import="java.time.LocalDate"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.Date"%>

<!DOCTYPE html>
<%
    Long timestamp = new Date().getTime();
    
     //English date format
    DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    //European date format
    DateTimeFormatter DTFE = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    
    DeliveryNote deliveryNote = null;
    
    DbResult dbr_deliveryNote = null;
    
    DbResult dbr_deliveryNoteRows = null;
    
    Long deliveryNote_id =  0L;
    
    Integer number = 0;
    
    Integer year = 0;
    
    String date = DTFE.format(LocalDate.now());
    
    DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
    
    //confirm button text
    String confirmButtonText = "CONFERMA CREAZIONE";
    //if deliveryNote_id is not null deliveryNote details must be shown in the page
    try{
        deliveryNote_id = Long.parseLong(request.getParameter("deliveryNote_id"));
    }catch(NumberFormatException ex ){}
    //the page is requested by clicking on a delivery notes table row in deliveryNotes.jsp page
    if( deliveryNote_id != 0L )
    { 
        
        dbr_deliveryNote = dao.readDeliveryNotes( deliveryNote_id,null,null ,null,null,null);
        dbr_deliveryNoteRows = dao.readDeliveryNoteRows(deliveryNote_id);
        
        deliveryNote = new DeliveryNote(dbr_deliveryNote,dbr_deliveryNoteRows);
 
        StringBuilder dateSB = new StringBuilder( deliveryNote.date);
        dateSB.insert(4, "-");
        dateSB.insert(7,"-");
        date = dateSB.toString();
        
        number = dbr_deliveryNote.getInteger("number");
        
        year = dbr_deliveryNote.getInteger("year");
        
        //UPDATE 
        confirmButtonText = "CONFERMA MODIFICHE";
    }
    //sets the taskdate to current day if task_id is null
    else
        date = LocalDate.now().toString();
    //gets user_id from session
    Long user_id = (Long) request.getSession().getAttribute("user_id");
    //user validation
    if(user_id==null){
        
%>

    <script>window.open("/Samurai/landing.jsp","_self")</script>

<%}else
{
    DbResult dbr_user = dao.readUsers( user_id);
    String user_role = dbr_user.getString("role");
    String userName = dbr_user.getString("firstName")+ " " + dbr_user.getString("lastName");
    DbResult dbr_customers = dao.readAllCustomers();
    DbResult dbr_transporters = dao.readTransporters(null,null,null,null,null,null,null,null,null,null,null,null,null,null);
    DbResult dbr_transportReasons = dao.getTransportReasons();
    DbResult dbr_transportResponsables = dao.getTransportResponsables();
    DbResult dbr_GoodsExteriorAspects = dao.getGoodsExteriorAspect();
%>

    <html>
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>Workline 2.0</title>
            <link href="/Samurai/common.css" rel="stylesheet">
            <link href="/Samurai/deliveryNotes.css" rel="stylesheet">
            <link href="/Samurai/elastictable.css" rel="stylesheet">
            <link href="/Samurai/Resources/font-awesome-4.7.0/css/font-awesome.min.css" rel="stylesheet">
        </head>
        <body <%if(deliveryNote != null)
                {%>
                    onload="app.getDeliveryNoteRows(<%=deliveryNote.deliveryNote_id%>);"
              <%}%> >
            
            <div class="Header">
                <span class="SWName">Workline 2.0</span><span class="PageTitle"> Documento di Trasporto </span>
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
                <%if( "admin".equals(user_role) ){%><span class=" Button" onclick="window.open('invoice_details.jsp','_self');">FATTURE</span><%}%>
                <%if( "admin".equals(user_role) ){%><span class=" Button" onclick="window.open('quote_details.jsp' , '_self');">PREVENTIVI</span><%}%>
                <%if( "admin".equals(user_role) ){%><span class=" Button" onclick="window.open('creditNote_details.jsp','_self');">NOTE ACCREDITO</span><%}%>
            </div>
            
            <!-- FILTERS SECTION -->
            <div class="Filters">
                
                <div class="Filters_label">RICERCA</div>
                
                <div class="Filters_number_label">NUMERO DDT</div>
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
                <div id="refresh" class="Button" onclick="app.openDeliveryNotesPage();"><i  id="searchIcon" class="fa fa-search" aria-hidden="true"></i> RICERCA</div>
                
                <% if ( ( deliveryNote == null && user_role.equals("operator")  ) || ( deliveryNote == null && user_role.equals("admin") ) || ( deliveryNote != null )  ){%>
                <div id="confirm_label" class="Filters_label">OPERAZIONI</div>
                
                <div id="deliveryNoteConfirmedButton" class="Button Operation Confirm" onclick="app.deliveryNoteConfirmed( <%=deliveryNote_id%>, <%=user_id%> );"  <% if ( deliveryNote != null && user_role.equals("operator") ) {%> display ="none" <%}%> ><%=confirmButtonText%></div>
                <% if ( deliveryNote != null ){%>
                    <div id="pdfButton" class="Button Operation" onclick="window.open('resources/DDT/DDT_DUESSE_'+<%=number%>+'_'+<%=year%>+'.pdf','_blank')">APRI PDF</div>
                    <div id="newDeliveryNoteButton" class="Button Operation" onclick="window.open('deliveryNote_details.jsp','_self');">CREA NUOVO</div>
                <%}%>
            <%}%>
              
            </div>    

            <!-- PAGE CONTENT -->
            <div class="Content WithFilter">
                <table class="Details_table">
                    
                    <tr class="Details_row ">
                        <th class="Detail_header Input" id="ddt">D.D.T.</th>
                        <td id="dateNumberRow">
                            <span class="TableLable" >Data:</span>
                            <span>
                                <input type="date" id="date_input" value= "<%=date%>" style="width:10rem;" disabled>
                            </span>
                        <%if( deliveryNote != null ){%>
                            <span class="TableLable">Numero:</span>
                            <span>
                                <input type="text" id="number_input" value= "<%=deliveryNote.number%> - <%=deliveryNote.year%>" disabled>
                            </span>
                        <%}%>    
                        </td> 
                    </tr>
                    
                    <tr class="Details_row ">
                        <th class="Detail_header Input" id="customer" >CLIENTE</th>
                        <td>
                            <select onchange = "app.suggestDeliveryNoteFields();app.deliveryNoteDetailsChanged('denomination_select_options');" id="denomination_select_options" >
                                <option value="" >NOME CLIENTE</option>
                                <%for(int i=0; i<dbr_customers.rowsCount(); i++){%>
                                    <option value="<%=dbr_customers.getLong(i,"customer_id")%>" ><%=dbr_customers.getString(i,"denomination")%></option>
                                <%}%>
                            </select> 
                            
                                <script>document.getElementById("denomination_select_options").value= "<%=deliveryNote == null ? "" : deliveryNote.customer_id%>"</script>
                                
                            <input id="address_input" placeholder = " VIA" value= "<%=deliveryNote == null ? "" : dbr_deliveryNote.getString("address") %>" onchange="app.deliveryNoteDetailsChanged('address_input');"  disabled>

                            <input id="houseNumber_input" placeholder = " N° CIVICO" value= "<%=deliveryNote == null ? "" : dbr_deliveryNote.getString("houseNumber") %>" onchange="app.deliveryNoteDetailsChanged('houseNumber_input');"  disabled>

                            <input id="postalCode_input" placeholder = " C.A.P." value= "<%=deliveryNote == null ? "" : dbr_deliveryNote.getString("postalCode") %>" onchange="app.deliveryNoteDetailsChanged('postalCode_input');"  disabled>

                            <input id="city_input" placeholder = " CITTA'" value= "<%=deliveryNote == null ? "" : dbr_deliveryNote.getString("city") %>" onchange="app.deliveryNoteDetailsChanged('city_input');"  disabled>

                            <input id="province_input" placeholder = " PROVINCIA" value= "<%=deliveryNote == null ? "" : dbr_deliveryNote.getString("province") %>" onchange="app.deliveryNoteDetailsChanged('province_input');"  disabled>
                            
                        </td> 
                    </tr>
                    <tr class="Details_row ">
                        <th class="Detail_header Input" id="destination">DESTINAZIONE</th>
                        <td> 
                            <input type="text" value="<%=deliveryNote == null ? "IDEM" : deliveryNote.destDenomination%>" id="destDenomination_input"  onchange="app.deliveryNoteDetailsChanged('destDenomination_input');" >

                            <input id="destAddress_input" placeholder = "VIA" value= "<%=deliveryNote == null ? "" : deliveryNote.destAddress%>" onchange="app.deliveryNoteDetailsChanged('destAddress_input');" >
 
                            <input id="destHouseNumber_input" placeholder = "N° CIVICO" value= "<%=deliveryNote == null ? "" : deliveryNote.destHouseNumber%>" onchange="app.deliveryNoteDetailsChanged('destHouseNumber_input');" >

                            <input id="destPostalCode_input" placeholder = "C.A.P." value= "<%=deliveryNote == null ? "" : deliveryNote.destPostalCode%>" onchange="app.deliveryNoteDetailsChanged('destPostalCode_input');" >

                            <input id="destCity_input"  placeholder="CITTA" value= "<%=deliveryNote == null ? "" : deliveryNote.destCity%>" onchange="app.deliveryNoteDetailsChanged('destCity_input');"  >
                            
                            <input id="destProvince_input" placeholder = "PROVINCIA" value= "<%=deliveryNote == null ? "" : deliveryNote.destProvince%>" onchange="app.deliveryNoteDetailsChanged('destProvince_input');" >
                        </td> 
                    </tr>
                    
                    <tr class="Details_row">
                        <th class="Detail_header " id="transportResponsable">TRASPORTO A CURA DEL:</th>
                        <td>
                            <input type="text"  class="Datalist_input" id="transport_responsable_input" list="transport_responsable_datalist" value="<%=deliveryNote == null ? "" : deliveryNote.transportResponsable%>" onchange="app.deliveryNoteDetailsChanged('transport_responsable_input');" >
                            <datalist  id="transport_responsable_datalist" class="Datalist_options" >
                                <%for(int i=0; i<dbr_transportResponsables.rowsCount(); i++){%>
                                    <option><%=dbr_transportResponsables.getString(i,"transportResponsable")%></option>
                                <%}%>
                            </datalist>
                        </td>
                    </tr>
                                     
                    <tr class="Details_row">
                        <th class="Detail_header " id="transportReason">CAUSALE DEL TRASPORTO:</th>
                        <td>
                            <input type="text"  class="Datalist_input" id="transport_reason_input" list="transport_reason_datalist" value="<%=deliveryNote == null ? "" : deliveryNote.transportReason%>"  onchange="app.deliveryNoteDetailsChanged('transport_reason_input');">
                            <datalist  id="transport_reason_datalist" class="Datalist_options">
                                <%for(int i=0; i<dbr_transportReasons.rowsCount(); i++){%>
                                    <option><%=dbr_transportReasons.getString(i,"transportReason")%></option>
                                <%}%>
                            </datalist>
                        </td>
                    </tr>
                    
                    <tr class="Details_row">
                        <th class="Detail_header " id="goodsExteriorAspect">ASPETTO ESTERIORE DEI BENI:</th>
                        <td>
                            <input type="text" class="Datalist_input"  id="goods_exterior_aspect_input" list="goods_exterior_aspect_datalist" value="<%=deliveryNote == null ? "" : deliveryNote.goodsExteriorAspect%>" onchange="app.deliveryNoteDetailsChanged('goods_exterior_aspect_input');">
                            <datalist  id="goods_exterior_aspect_datalist">
                                <%for(int i=0; i<dbr_GoodsExteriorAspects.rowsCount(); i++){%>
                                    <option><%=dbr_GoodsExteriorAspects.getString(i,"goodsExteriorAspect")%></option>
                                <%}%>
                            </datalist>
                        </td>
                    </tr>
                                    
                    <tr class="Details_row">
                        <th class="Detail_header " id="packagesNumber">N° COLLI:</th>
                        <td><input id="packagesNumber_input"  type="number" value="<%=deliveryNote == null ? "" : deliveryNote.packagesNumber%>" min="0" onchange="app.deliveryNoteDetailsChanged('packagesNumber_input');"  ></td>
                    </tr>
                    
                    <tr class="Details_row">
                        <th class="Detail_header " id="weight">PESO KG:</th>
                        <td><input id="weight_input"  type="number" value="<%=deliveryNote == null ? "" : deliveryNote.weight%>" min="0" onchange="app.deliveryNoteDetailsChanged('weight_input');"  ></td>
                    </tr>
                    
                    <tr class="Details_row ">
                        <th class="Detail_header Input" id="transporter">VETTORE</th>
                        <td>
                            <select id="transporterDenomination_select_options" onchange="app.deliveryNoteDetailsChanged( 'transporterDenomination_select_options' )"  >
                                <option value="" >VETTORE</option>
                                <%for(int i=0; i<dbr_transporters.rowsCount(); i++){%>
                                    <option value="<%=dbr_transporters.getLong(i,"transporter_id")%>"><%=dbr_transporters.getString(i,"denomination")%></option>
                                <%}%>
                            </select> 
                            <script>document.getElementById("transporterDenomination_select_options").value= "<%=deliveryNote == null ? "" : deliveryNote.transporter_id%>"</script>
                        </td>
                    </tr>
                    
                    <tr class="Details_row ">
                        <th class="Detail_header Input" id="notes" rows="4" cols="50">NOTE</th>
                        <td><textarea id="notes_input"  onchange="app.deliveryNoteDetailsChanged('notes_input');"  ><%=deliveryNote == null ? "" : deliveryNote.notes%></textarea></td>
                    </tr>
                    
                    
                    <%if( deliveryNote_id != 0L && "admin".equals(user_role) && !dbr_deliveryNote.getBoolean("invoiced") ){%>
                    <tr class="Details_row ">
                        <th class="Detail_header Input" id="suggest">NON SUGGERIRE IN FATTURA</th>
                       <td><input class="Checkbox" id="suggest_checkbox"  type="checkbox" onchange="app.deliveryNoteDetailsChanged('suggest_checkbox');"  ></td>
                       <script>
                        document.getElementById("suggest_checkbox").checked = <%= dbr_deliveryNote != null ? !dbr_deliveryNote.getBoolean("suggested") : false %>
                        </script>
                       
                    </tr>
                    <%}%>
                    
                </table>
                      
                <!--------- ELASTIC TABLE ---------->
                <table id="deliveryNoteItemsTable" class="Table">
                    <col width="10%">
                    <col width="70%">
                    <col width="10%">
                    <col width="10%">
                    <thead>
                        <tr>
                            <th>Codice</th>
                            <th>Descrizione</th>
                            <th>Quantità</th>
                            <th>Azioni</th>
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
        <template id="item_template">
            <tr>
                <td class="Code"><input id="code" type="text" placeholder="CODICE" onchange="app.fillOrderMachinaryModel(this)" ></td>
                <td class="Description"><textarea  id="description" placeholder="DESCRIZIONE" rows="1" ></textarea></td>
                <td class="Quantity"><input id="quantity" type="number" min="1" value = "1" placeholder="QUANTITA'" ></td>
                <td class="Actions" >
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

       
        <!-- sezione script -->
        <script src="deliveryNoteElasticTable.js?<%=timestamp%>"></script>
        <script src="/Samurai/dbi.js?<%=timestamp%>"></script>
        <script src="/Samurai/deliveryNotes.js?<%=timestamp%>"></script>
        <script src="/Samurai/dropDown.js?<%=timestamp%>"></script> 
        <script>app.addItemRowBefore(null);</script>
        <script>setInterval(function(){app.ping();},60000);</script>
        
        <!--script>
            document.getElementById("customer_select_options").value = "22";
            document.getElementById("transport_responsable_input").value = "PROVA";
            document.getElementById("transport_reason_input").value = "PROVA";
            document.getElementById("goods_exterior_aspect_input").value = "PROVA";
        </script-->
    </html>

<%}%>