<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page  import="com.dps.dbi.DbResult"%>
<%@page import="com.dalessio.samurai.DataAccessObject"%>
<%@page import="java.util.Date"%>
<%@page import="java.time.LocalDate"%>
<%@page import="java.time.format.DateTimeFormatter"%>

<!DOCTYPE html>
<%
    Long timestamp = new Date().getTime();
    
    DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
    Long user_id = (Long) request.getSession().getAttribute("user_id");
    Long customer_id = Long.parseLong( request.getParameter( "customer_id" ) );
    DbResult dbr_paymentConditions = dao.readPaymentConditions().result();
    
    //gets vat code to achieve if it is freign or italian
    DbResult dbr_customer = dao.readCustomers(customer_id,null,null,null,null,null,null,null,null,null,null,null,null,null);
    String vatCode = dbr_customer.getString("vatCode");
    Boolean isItalian = (vatCode.matches("[0-9]{11}"));
    String user_role = "";
    
    if(user_id==null){
        
%>

    <script>window.open("/Samurai/landing.jsp","_self")</script>

<%}else{
        //European date format
        DateTimeFormatter DTFE = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        DbResult dbr_user = dao.readUsers( user_id);
        user_role = dao.getUserRole(user_id);

        String userName = dbr_user.getString("firstName") + " " + dbr_user.getString("lastName");

        String vatExemptionProtocol = dbr_customer.getString("exemptionProtocol");
        String vatExemptionDate = dbr_customer.getString("exemptionDate");

        StringBuilder vatExemptionDateSB = vatExemptionDate == null ? new StringBuilder( "" ) : new StringBuilder( vatExemptionDate );
        if( !vatExemptionDate.toString().equals(""))
        {
            vatExemptionDateSB.insert(4, "-");
            vatExemptionDateSB.insert(7,"-");
            vatExemptionDate = vatExemptionDateSB.toString();
        }
%>

    <html>
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>Workline 2.0</title>
            <link href="/Samurai/common.css" rel="stylesheet">
            <link href="/Samurai/customers.css" rel="stylesheet">
        </head>
        <body>
            <div class="Header">
                <span class="SWName">Workline 2.0</span><span class="PageTitle"> Anagrafica Cliente </span>
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
            
            
            <div class="Content">
                <table class="Details_table">
                    <tbody>
                        <tr class="Details_row">
                            <th id="denomination" class="Detail_header">DENOMINAZIONE</th>
                            <td><input id='denomination_input' onchange="app.customerDetailsChanged('#denomination_input');" value="<%=dbr_customer.getString("denomination")%>"></td>
                        </tr>
                        
                        <tr class="Details_row">
                            <th id="vat_code" class="Detail_header">PARTITA IVA <%= isItalian ? "" : "ESTERA"%></th>
                            <td><input id='vat_code_input' onchange="app.customerDetailsChanged('#vat_code_input');" value="<%=dbr_customer.getString("vatCode")%>"></td>
                        </tr>

                        <tr class="Details_row">
                            <th id="fiscal_code" class="Detail_header">CODICE FISCALE</th>
                            <td><input id='fiscal_code_input' onchange="app.customerDetailsChanged('#fiscal_code_input');" value="<%=dbr_customer.getString("fiscalCode")%>"></td>
                        </tr>
                        
                        <tr class="Details_row">
                            <th id="fiscal_code" class="Detail_header">CODICE UNIVOCO</th>
                            <td><input id='univocal_code_input' onchange="app.customerDetailsChanged('#univocal_code_input');" value="<%=dbr_customer.getString("univocalCode")%>"></td>
                        </tr>
                        
                        <tr class="Details_row">
                            <th id="pec" class="Detail_header">PEC</th>
                            <td><input id='pec_input' onchange="app.customerDetailsChanged('#pec_input');" value="<%=dbr_customer.getString("pec")%>"></td>
                        </tr>
                        
                        <tr class="Details_row">
                            <th id="city" class="Detail_header">CITTA'</th>
                            <td><input id='city_input' onchange="app.customerDetailsChanged('#city_input');" value="<%=dbr_customer.getString("city")%>"></td>
                        </tr>

                        <tr class="Details_row">
                            <th id="address" class="Detail_header">INDIRIZZO</th>
                            <td><input id='address_input' onchange="app.customerDetailsChanged('#address_input');" value="<%=dbr_customer.getString("address")%>"></td>
                        </tr>

                        <tr class="Details_row">
                            <th id="house_number" class="Detail_header">N° CIVICO</th>
                            <td><input id='house_number_input' onchange="app.customerDetailsChanged('#house_number_input');" value="<%=dbr_customer.getString("houseNumber")%>"></td>
                        </tr>

                        <tr class="Details_row">
                            <th id="postal_code" class="Detail_header">CODICE POSTALE</th>
                            <td><input id='postal_code_input' onchange="app.customerDetailsChanged('#postal_code_input');" value="<%=dbr_customer.getString("postalCode")%>"></td>
                        </tr>

                        <tr class="Details_row">
                            <th id="province" class="Detail_header">PROVINCIA</th>
                            <td>
                                <select id='province_input' onchange="app.customerDetailsChanged('#province_input');">
                                    <option value="">SELEZIONA PROVINCIA</option>
                                    <option value="AL"<%if(dbr_customer != null && dbr_customer.getString("province").equals("AL")){%>selected<%}%>>Alessandria (AL)</option>
                                    <option value="AN"<%if(dbr_customer != null && dbr_customer.getString("province").equals("AN")){%>selected<%}%>>Ancona (AN)</option>
                                    <option value="AO"<%if(dbr_customer != null && dbr_customer.getString("province").equals("AO")){%>selected<%}%>>Aosta (AO)</option>
                                    <option value="AR"<%if(dbr_customer != null && dbr_customer.getString("province").equals("AR")){%>selected<%}%>>Arezzo (AR)</option>
                                    <option value="AP"<%if(dbr_customer != null && dbr_customer.getString("province").equals("AP")){%>selected<%}%>>Ascoli Piceno (AP)</option>
                                    <option value="AT"<%if(dbr_customer != null && dbr_customer.getString("province").equals("AT")){%>selected<%}%>>Asti (AT)</option>
                                    <option value="AV"<%if(dbr_customer != null && dbr_customer.getString("province").equals("AV")){%>selected<%}%>>Avellino (AV)</option>
                                    <option value="BA"<%if(dbr_customer != null && dbr_customer.getString("province").equals("BA")){%>selected<%}%>>Bari (BA)</option>
                                    <option value="BL"<%if(dbr_customer != null && dbr_customer.getString("province").equals("BL")){%>selected<%}%>>Belluno (BL)</option>
                                    <option value="BN"<%if(dbr_customer != null && dbr_customer.getString("province").equals("BN")){%>selected<%}%>>Benevento (BN)</option>
                                    <option value="BG"<%if(dbr_customer != null && dbr_customer.getString("province").equals("BG")){%>selected<%}%>>Bergamo (BG)</option>
                                    <option value="BI"<%if(dbr_customer != null && dbr_customer.getString("province").equals("BI")){%>selected<%}%>>Biella (BI)</option>
                                    <option value="BO"<%if(dbr_customer != null && dbr_customer.getString("province").equals("BO")){%>selected<%}%>>Bologna (BO)</option>
                                    <option value="BZ"<%if(dbr_customer != null && dbr_customer.getString("province").equals("BZ")){%>selected<%}%>>Bolzano (BZ)</option>
                                    <option value="BS"<%if(dbr_customer != null && dbr_customer.getString("province").equals("BS")){%>selected<%}%>>Brescia (BS)</option>
                                    <option value="BR"<%if(dbr_customer != null && dbr_customer.getString("province").equals("BR")){%>selected<%}%>>Brindisi (BR)</option>
                                    <option value="CA"<%if(dbr_customer != null && dbr_customer.getString("province").equals("CA")){%>selected<%}%>>Cagliari (CA)</option>
                                    <option value="CL"<%if(dbr_customer != null && dbr_customer.getString("province").equals("CL")){%>selected<%}%>>Caltanissetta (CL)</option>
                                    <option value="CB"<%if(dbr_customer != null && dbr_customer.getString("province").equals("CB")){%>selected<%}%>>Campobasso (CB)</option>
                                    <option value="CE"<%if(dbr_customer != null && dbr_customer.getString("province").equals("CE")){%>selected<%}%>>Caserta (CE)</option>
                                    <option value="CT"<%if(dbr_customer != null && dbr_customer.getString("province").equals("CT")){%>selected<%}%>>Catania (CT)</option>
                                    <option value="CZ"<%if(dbr_customer != null && dbr_customer.getString("province").equals("CZ")){%>selected<%}%>>Catanzaro (CZ)</option>
                                    <option value="CH"<%if(dbr_customer != null && dbr_customer.getString("province").equals("CH")){%>selected<%}%>>Chieti (CH)</option>
                                    <option value="CO"<%if(dbr_customer != null && dbr_customer.getString("province").equals("CO")){%>selected<%}%>>Como (CO)</option>
                                    <option value="CS"<%if(dbr_customer != null && dbr_customer.getString("province").equals("CS")){%>selected<%}%>>Cosenza (CS)</option>
                                    <option value="CR"<%if(dbr_customer != null && dbr_customer.getString("province").equals("CR")){%>selected<%}%>>Cremona (CR)</option>
                                    <option value="KR"<%if(dbr_customer != null && dbr_customer.getString("province").equals("KR")){%>selected<%}%>>Crotone (KR)</option>
                                    <option value="CN"<%if(dbr_customer != null && dbr_customer.getString("province").equals("CN")){%>selected<%}%>>Cuneo (CN)</option>
                                    <option value="EN"<%if(dbr_customer != null && dbr_customer.getString("province").equals("EN")){%>selected<%}%>>Enna (EN)</option>
                                    <option value="FE"<%if(dbr_customer != null && dbr_customer.getString("province").equals("FE")){%>selected<%}%>>Ferrara (FE)</option>
                                    <option value="FI"<%if(dbr_customer != null && dbr_customer.getString("province").equals("FI")){%>selected<%}%>>Firenze (FI)</option>
                                    <option value="FG"<%if(dbr_customer != null && dbr_customer.getString("province").equals("FG")){%>selected<%}%>>Foggia (FG)</option>
                                    <option value="FO"<%if(dbr_customer != null && dbr_customer.getString("province").equals("FO")){%>selected<%}%>>Forli'-Cesena (FO)</option>
                                    <option value="FR"<%if(dbr_customer != null && dbr_customer.getString("province").equals("FR")){%>selected<%}%>>Frosinone (FR)</option>
                                    <option value="GE"<%if(dbr_customer != null && dbr_customer.getString("province").equals("GE")){%>selected<%}%>>Genova (GE)</option>
                                    <option value="GO"<%if(dbr_customer != null && dbr_customer.getString("province").equals("GO")){%>selected<%}%>>Gorizia (GO)</option>
                                    <option value="GR"<%if(dbr_customer != null && dbr_customer.getString("province").equals("GR")){%>selected<%}%>>Grosseto (GR)</option>
                                    <option value="IM"<%if(dbr_customer != null && dbr_customer.getString("province").equals("IM")){%>selected<%}%>>Imperia (IM)</option>
                                    <option value="IS"<%if(dbr_customer != null && dbr_customer.getString("province").equals("IS")){%>selected<%}%>>Isernia (IS)</option>
                                    <option value="SP"<%if(dbr_customer != null && dbr_customer.getString("province").equals("SP")){%>selected<%}%>>La Spezia (SP)</option>
                                    <option value="AQ"<%if(dbr_customer != null && dbr_customer.getString("province").equals("AQ")){%>selected<%}%>>L'Aquila (AQ)</option>
                                    <option value="LT"<%if(dbr_customer != null && dbr_customer.getString("province").equals("LT")){%>selected<%}%>>Latina (LT)</option>
                                    <option value="LE"<%if(dbr_customer != null && dbr_customer.getString("province").equals("LE")){%>selected<%}%>>Lecce (LE)</option>
                                    <option value="LC"<%if(dbr_customer != null && dbr_customer.getString("province").equals("LC")){%>selected<%}%>>Lecco (LC)</option>
                                    <option value="LI"<%if(dbr_customer != null && dbr_customer.getString("province").equals("LI")){%>selected<%}%>>Livorno (LI)</option>
                                    <option value="LO"<%if(dbr_customer != null && dbr_customer.getString("province").equals("LO")){%>selected<%}%>>Lodi (LO)</option>
                                    <option value="LU"<%if(dbr_customer != null && dbr_customer.getString("province").equals("LU")){%>selected<%}%>>Lucca (LU)</option>
                                    <option value="MC"<%if(dbr_customer != null && dbr_customer.getString("province").equals("MC")){%>selected<%}%>>Macerata (MC)</option>
                                    <option value="MN"<%if(dbr_customer != null && dbr_customer.getString("province").equals("MN")){%>selected<%}%>>Mantova (MN)</option>
                                    <option value="MS"<%if(dbr_customer != null && dbr_customer.getString("province").equals("MS")){%>selected<%}%>>Massa-Carrara (MS)</option>
                                    <option value="MT"<%if(dbr_customer != null && dbr_customer.getString("province").equals("MT")){%>selected<%}%>>Matera (MT)</option>
                                    <option value="ME"<%if(dbr_customer != null && dbr_customer.getString("province").equals("ME")){%>selected<%}%>>Messina (ME)</option>
                                    <option value="MI"<%if(dbr_customer != null && dbr_customer.getString("province").equals("MI")){%>selected<%}%>>Milano (MI)</option>
                                    <option value="MO"<%if(dbr_customer != null && dbr_customer.getString("province").equals("MO")){%>selected<%}%>>Modena (MO)</option>
                                    <option value="NA"<%if(dbr_customer != null && dbr_customer.getString("province").equals("NA")){%>selected<%}%>>Napoli (NA)</option>
                                    <option value="NO"<%if(dbr_customer != null && dbr_customer.getString("province").equals("NO")){%>selected<%}%>>Novara (NO)</option>
                                    <option value="NU"<%if(dbr_customer != null && dbr_customer.getString("province").equals("NU")){%>selected<%}%>>Nuoro (NU)</option>
                                    <option value="OR"<%if(dbr_customer != null && dbr_customer.getString("province").equals("OR")){%>selected<%}%>>Oristano (OR)</option>
                                    <option value="PD"<%if(dbr_customer != null && dbr_customer.getString("province").equals("PD")){%>selected<%}%>>Padova (PD)</option>
                                    <option value="PA"<%if(dbr_customer != null && dbr_customer.getString("province").equals("PA")){%>selected<%}%>>Palermo (PA)</option>
                                    <option value="PR"<%if(dbr_customer != null && dbr_customer.getString("province").equals("PR")){%>selected<%}%>>Parma (PR)</option>
                                    <option value="PV"<%if(dbr_customer != null && dbr_customer.getString("province").equals("PV")){%>selected<%}%>>Pavia (PV)</option>
                                    <option value="PG"<%if(dbr_customer != null && dbr_customer.getString("province").equals("PG")){%>selected<%}%>>Perugia (PG)</option>
                                    <option value="PU"<%if(dbr_customer != null && dbr_customer.getString("province").equals("PS")){%>selected<%}%>>Pesaro e Urbino (PU)</option>
                                    <option value="PE"<%if(dbr_customer != null && dbr_customer.getString("province").equals("PE")){%>selected<%}%>>Pescara (PE)</option>
                                    <option value="PC"<%if(dbr_customer != null && dbr_customer.getString("province").equals("PC")){%>selected<%}%>>Piacenza (PC)</option>
                                    <option value="PI"<%if(dbr_customer != null && dbr_customer.getString("province").equals("PI")){%>selected<%}%>>Pisa (PI)</option>
                                    <option value="PT"<%if(dbr_customer != null && dbr_customer.getString("province").equals("PT")){%>selected<%}%>>Pistoia (PT)</option>
                                    <option value="PN"<%if(dbr_customer != null && dbr_customer.getString("province").equals("PN")){%>selected<%}%>>Pordenone (PN)</option>
                                    <option value="PZ"<%if(dbr_customer != null && dbr_customer.getString("province").equals("PZ")){%>selected<%}%>>Potenza (PZ)</option>
                                    <option value="PO"<%if(dbr_customer != null && dbr_customer.getString("province").equals("PO")){%>selected<%}%>>Prato (PO)</option>
                                    <option value="RG"<%if(dbr_customer != null && dbr_customer.getString("province").equals("RG")){%>selected<%}%>>Ragusa (RG)</option>
                                    <option value="RA"<%if(dbr_customer != null && dbr_customer.getString("province").equals("RA")){%>selected<%}%>>Ravenna (RA)</option>
                                    <option value="RC"<%if(dbr_customer != null && dbr_customer.getString("province").equals("RC")){%>selected<%}%>>Reggio di Calabria (RC)</option>
                                    <option value="RE"<%if(dbr_customer != null && dbr_customer.getString("province").equals("RE")){%>selected<%}%>>Reggio nell'Emilia (RE)</option>
                                    <option value="RI"<%if(dbr_customer != null && dbr_customer.getString("province").equals("RI")){%>selected<%}%>>Rieti (RI)</option>
                                    <option value="RN"<%if(dbr_customer != null && dbr_customer.getString("province").equals("RN")){%>selected<%}%>>Rimini (RN)</option>
                                    <option value="RM"<%if(dbr_customer != null && dbr_customer.getString("province").equals("RM")){%>selected<%}%>>Roma (RM)</option>
                                    <option value="RO"<%if(dbr_customer != null && dbr_customer.getString("province").equals("RO")){%>selected<%}%>>Rovigo (RO)</option>
                                    <option value="SA"<%if(dbr_customer != null && dbr_customer.getString("province").equals("SA")){%>selected<%}%>>Salerno (SA)</option>
                                    <option value="SS"<%if(dbr_customer != null && dbr_customer.getString("province").equals("SS")){%>selected<%}%>>Sassari (SS)</option>
                                    <option value="SV"<%if(dbr_customer != null && dbr_customer.getString("province").equals("SV")){%>selected<%}%>>Savona (SV)</option>
                                    <option value="SI"<%if(dbr_customer != null && dbr_customer.getString("province").equals("SI")){%>selected<%}%>>Siena (SI)</option>
                                    <option value="SR"<%if(dbr_customer != null && dbr_customer.getString("province").equals("SR")){%>selected<%}%>>Siracusa (SR)</option>
                                    <option value="SO"<%if(dbr_customer != null && dbr_customer.getString("province").equals("SO")){%>selected<%}%>>Sondrio (SO)</option>
                                    <option value="TA"<%if(dbr_customer != null && dbr_customer.getString("province").equals("TA")){%>selected<%}%>>Taranto (TA)</option>
                                    <option value="TE"<%if(dbr_customer != null && dbr_customer.getString("province").equals("TE")){%>selected<%}%>>Teramo (TE)</option>
                                    <option value="TR"<%if(dbr_customer != null && dbr_customer.getString("province").equals("TR")){%>selected<%}%>>Terni (TR)</option>
                                    <option value="TO"<%if(dbr_customer != null && dbr_customer.getString("province").equals("TO")){%>selected<%}%>>Torino (TO)</option>
                                    <option value="TP"<%if(dbr_customer != null && dbr_customer.getString("province").equals("TP")){%>selected<%}%>>Trapani (TP)</option>
                                    <option value="TN"<%if(dbr_customer != null && dbr_customer.getString("province").equals("TN")){%>selected<%}%>>Trento (TN)</option>
                                    <option value="TV"<%if(dbr_customer != null && dbr_customer.getString("province").equals("TV")){%>selected<%}%>>Treviso (TV)</option>
                                    <option value="TS"<%if(dbr_customer != null && dbr_customer.getString("province").equals("TS")){%>selected<%}%>>Trieste (TS)</option>
                                    <option value="UD"<%if(dbr_customer != null && dbr_customer.getString("province").equals("UD")){%>selected<%}%>>Udine (UD)</option>
                                    <option value="VA"<%if(dbr_customer != null && dbr_customer.getString("province").equals("VA")){%>selected<%}%>>Varese (VA)</option>
                                    <option value="VE"<%if(dbr_customer != null && dbr_customer.getString("province").equals("VE")){%>selected<%}%>>Venezia (VE)</option>
                                    <option value="VB"<%if(dbr_customer != null && dbr_customer.getString("province").equals("VB")){%>selected<%}%>>Verbano-Cusio-Ossola (VB)</option>
                                    <option value="VC"<%if(dbr_customer != null && dbr_customer.getString("province").equals("VC")){%>selected<%}%>>Vercelli (VC)</option>
                                    <option value="VR"<%if(dbr_customer != null && dbr_customer.getString("province").equals("VR")){%>selected<%}%>>Verona (VR)</option>
                                    <option value="VV"<%if(dbr_customer != null && dbr_customer.getString("province").equals("VV")){%>selected<%}%>>Vibo Valentia (VV)</option>
                                    <option value="VI"<%if(dbr_customer != null && dbr_customer.getString("province").equals("VI")){%>selected<%}%>>Vicenza (VI)</option>
                                    <option value="VT"<%if(dbr_customer != null && dbr_customer.getString("province").equals("VT")){%>selected<%}%>>Viterbo (VT)</option>
                            </select>
                            </td>
                        </tr>
                        
                        <tr class="Details_row">
                            <th id="country" class="Detail_header">CODICE PAESE</th>
                            <td><select id='country_input' onchange="app.customerDetailsChanged('#country_input');">
                                    <option value="IT"<%if(dbr_customer != null && dbr_customer.getString("idFiscaleIva_paese").equals("IT")){%>selected<%}%>>Italia(IT)</option>
                                    <option value="CH"<%if(dbr_customer != null && dbr_customer.getString("idFiscaleIva_paese").equals("CH")){%>selected<%}%>>Svizzera(CH)</option>
                                </select>
                            </td>
                        </tr>

                        <tr class="Details_row">
                            <th id="logo" class="Detail_header">LOGO</th>
                            <td>
                                <input id='logo_file' type="file" accept="image/*">
                                <input id='logo_upload' type="button" class="Button" value="CARICA IL FILE SELEZIONATO" onclick="app.uploadLogo(<%=dbr_customer.getLong("customer_id")%>);">
                                <a id="logo_download" class="Button" href="resources/Logo/<%=dbr_customer.getString("logo")%>" target="_blank">VISUALIZZA LOGO</a>
                            </td>

                        </tr>
                        
                        <tr class="Details_row">
                            <th class="Detail_header">CONDIZIONI PAGAMENTO (pdf)</th>
                            <td>
                                <input type="text" class="Datalist_input"  id="payment_conditions_datalist_input" list="payment_conditiondd_datalist" value="<%=dbr_customer.getString("paymentConditions")%>" onchange="app.customerDetailsChanged('#payment_conditiondd_datalist_input');" style="width:70%;">
                                <datalist  id="payment_conditions_datalist_input" class="Datalist_options"  >
                                    <%for(int i=0; i<dbr_paymentConditions.rowsCount(); i++){%>
                                    <option><%=dbr_paymentConditions.getString(i,"paymentConditions")%></option>
                                    <%}%>
                                </datalist>
                              </td> 
                        </tr>
                        
                        <tr class="Details_row">
                            <th id="modalitaPagamento" class="Detail_header">MODALITA' PAGAMENTO (XML)</th>
                            <td>
                                <select id="modalitaPagamento_input" onchange="app.customerDetailsChanged('#modalitaPagamento_input');">
                                    <option value="MP05"<%if(dbr_customer != null && dbr_customer.getString("modalitaPagamento").equals("MP05")){%>selected<%}%>>Bonifico (MP05)</option>
                                    <option value="MP12"<%if(dbr_customer != null && dbr_customer.getString("modalitaPagamento").equals("MP12")){%>selected<%}%>>RIBA (MP12)</option>
                                    <option value="MP01"<%if(dbr_customer != null && dbr_customer.getString("modalitaPagamento").equals("MP01")){%>selected<%}%>>Contanti (MP01)</option>
                                    <option value="MP02"<%if(dbr_customer != null && dbr_customer.getString("modalitaPagamento").equals("MP02")){%>selected<%}%>>Assegno (MP02)</option>
                                </select>
                            </td>
                        </tr>
                            
                        <tr class="Details_row">
                            <th id="bank" class="Detail_header">BANCA D'APPOGGIO</th>
                            <td><input id="bank_input" onchange="app.customerDetailsChanged('#bank_input');" value="<%=dbr_customer.getString("bank")%>"></td>
                        </tr>
                        
                        <tr class="Details_row">
                            <th id="CAB" class="Detail_header">CAB</th>
                            <td><input id='CAB_input' onchange="app.customerDetailsChanged('#CAB_input');" value="<%=dbr_customer.getString("CAB")%>"></td>
                        </tr>
                        
                        <tr class="Details_row">
                            <th id="ABI" class="Detail_header">ABI</th>
                            <td><input id='ABI_input' onchange="app.customerDetailsChanged('#ABI_input');" value="<%=dbr_customer.getString("ABI")%>"></td>
                        </tr>
                        
                        <tr class="Details_row">
                            <th id="IBAN" class="Detail_header">IBAN</th>
                            <td><input id='IBAN_input' onchange="app.customerDetailsChanged('#IBAN_input');" value="<%=dbr_customer.getString("IBAN")%>"></td>
                        </tr>
                        
                        <tr class="Details_row">
                            <th id="foreignIBAN" class="Detail_header">IBAN ESTERO</th>
                            <td><input id='foreignIBAN_input' onchange="app.customerDetailsChanged('#foreignIBAN_input');" value="<%=dbr_customer.getString("foreignIBAN")%>"></td>
                        </tr>
                        
                        <tr class="Details_row">
                            <th id="notes" class="Detail_header">NOTE</th>
                            <td><input id='notes_input' onchange="app.customerDetailsChanged('#notes_input');" value="<%=dbr_customer.getString("notes")%>"></td>
                        </tr>  
                        
                        <tr class="Details_row">
                            <th id="exemptionProtocol" class="Detail_header exemption">Protocollo di ricezione della dichiarazione di intento ed il suo progressivo separato dal segno "-" es: 08060120341234567-000001 </th>
                            <td><input id='exemptionProtocol_input' onchange="app.customerDetailsChanged('#exemptionProtocol_input');" value="<%= vatExemptionProtocol == null ? "" : vatExemptionProtocol %>"></td>
                        </tr> 
                        
                        <tr class="Details_row">
                            <th id="exemptionDate" class="Detail_header exemption">Data della ricevuta telematica rilasciata dall'Agenzia delle Entrate e contenente il protocollo della dichiarazione di intento</th>
                            <td><input type="date" id="exemptionDate_input" max="<%=LocalDate.now().toString()%>" value= "<%= vatExemptionDate == null ? "" : vatExemptionDate %>"></td>
                        </tr> 
                        
                        <tr class="Details_row">  <!--QUESTO CAMPO E' OBSOLETA DAL 01/01/2022 CON LE NUOVE MODALITA' DI GESTIONE DELLE ESENZIONI NELL'XML DELLE FATTURE DIGITALI-->
                            <th id="exemption_notes" class="Detail_header">TESTO ESENZIONE IVA ( N.B. CAMPO OBSOLETO DAL 01/02/2022 )</th>
                            <td><input id='vat_exemption_text_input' onchange="app.customerDetailsChanged('#vat_exemption_text_input');" value="<%=dbr_customer.getString("VATExemptionText")%>"></td>
                        </tr>  
                        
                    </tbody>
                </table>   
                <span id="updateCustomerButton" class="Button Operation Update" onclick="app.customerUpdated('<%=customer_id%>',<%=isItalian%>);">CONFERMA MODIFICHE CLIENTE</span>
            </div>

            <div class="Footer">
                <span class="Footer_message"> Utente : <%=userName.replace("''","'")%></span>
            </div>

        </body>
        <script src="/Samurai/dbi.js?<%=timestamp%>"></script>
        <script src="/Samurai/customers.js?<%=timestamp%>"></script>
        <script src="/Samurai/dropDown.js?<%=timestamp%>"></script>
        <script>setInterval(function(){app.ping();},60000);</script>
    </html>

<%}%>