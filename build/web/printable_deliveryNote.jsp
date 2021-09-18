<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="com.dps.dbi.DbResult"%>
<%@page import="com.dalessio.samurai.DataAccessObject"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    DataAccessObject dao = new DataAccessObject();
    
    //English date format
    DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    //European date format
    DateTimeFormatter DTFE = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    
    // retrieves deliveryNote_id
    Long deliveryNote_id = Long.parseLong(request.getParameter("deliveryNote_id"));

    //delivery note data
    String deliveryNotesQuery ="SELECT * FROM dyn_DeliveryNotes_view WHERE deliveryNote_id = " + deliveryNote_id;
    DbResult dbr_deliveryNote = dao.execAndCheck(deliveryNotesQuery).result();

        // delivery note number
        String number = dbr_deliveryNote.getInteger("number").toString();
        
        //delivery note year
        Integer year = dbr_deliveryNote.getInteger("year");

        // delivery note date
        String dateString = dbr_deliveryNote.getString("date");
        //get a stringbuilder to insert dashes
        StringBuilder dateSB = new StringBuilder(dateString);
        dateSB.insert(4,"-");
        dateSB.insert(7,"-");
        String date = DTFE.format(DTF.parse(dateSB.toString()));

        //destination data
        String destDenomination = dbr_deliveryNote.getString("destDenomination");
        String destAddress = dbr_deliveryNote.getString("destAddress") + ", " + dbr_deliveryNote.getString("destHouseNumber");
        String destCity = dbr_deliveryNote.getString("destPostalCode") + ", " + dbr_deliveryNote.getString("destCity");

        //transport data
        String transportResponsable = dbr_deliveryNote.getString("transportResponsable");
        String transportReason = dbr_deliveryNote.getString("transportReason");
        String goodsExteriorAspect = dbr_deliveryNote.getString("goodsExteriorAspect");
        String packagesNumber = dbr_deliveryNote.getString("packagesNumber");
        String weight = dbr_deliveryNote.getString("weight");

        //notes
        String notes = dbr_deliveryNote.getString("notes");
        
    // customer data
    Long customer_id = dbr_deliveryNote.getLong("customer_id");
    String customerQuery ="SELECT * FROM reg_Customers WHERE customer_id = " + customer_id;
    DbResult dbr_customer = dao.execAndCheck(customerQuery).result();
    
    // transporter data
    Long transporter_id = dbr_deliveryNote.getLong("transporter_id");
    String transporterQuery ="SELECT * FROM reg_Transporters WHERE transporter_id = " + transporter_id;
    DbResult dbr_transporter = dao.execAndCheck(transporterQuery).result();
    
    //delivery note rows 
    String deliveryNotesRowsQuery ="SELECT * FROM dyn_DeliveryNotesRows_view WHERE deliveryNote_id = " + deliveryNote_id;
    
    DbResult dbr_dliveryNoteRows = dao.execAndCheck(deliveryNotesRowsQuery).result();

%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        
        <link href="/Samurai/deliveryNote_printable.css" rel="stylesheet">
    </head>
    <body >
    <!--body-->
        <div id="mainContainer">
            
            <!-- INIZIO Logo e dati DUESSE in alto a sinistra-->
            <div id="duesse_data">
                <!--Logo in alto a sinistra sopvrapposto-->
                <img  id="logo" src="/Samurai/Resources/Logo/logoDuesse.jpg">
                
                <table id="duesse_contacts">
                    <tr>
                        <td id="duesse_address"> <span id="duesse_name">DUESSE Service s.r.l.</span> Via Agusta,51 21017 Samarate (VA) Italy</td>
                        <td id="duesse_numbers"> Tel. +39 0331220913 Fax +39 0331220914 Cod.Fisc. - P. IVA 02677820124</td>
                    </tr>
                </table>
            </div>
            <!-- FINE Logo e dati DUESSE in alto a sinistra-->
            
            <!-- INIZIO Destinatario documento, modello e revisione in alto a destra-->
            <div id="copy_data">
                <!--**** Tre diciture diverse *******-->
                <div id="copy_destination">COPIA PER DUESSE</div>
                <div id = "doc_model">Mod. 07-07 Rev.1</div>
            </div>
            <!-- FINE Destinatario documento, modello e revisione in alto a destra-->
            
            <!-- INIZIO Tipo dicumento ed estremi documento in alto a destra -->
            <div id ="deliveryNote_data">
                <table>
                    <tr>
                        <td  rowspan="2"><div id="document_name">D.D.T.</div><div id="law">( D.P.R. 14/08/96 N° 472 )</div></td><td>N°... <%=number%>  <%=year%></td><td>Del... <%=date%></td>
                    </tr>
                </table>
            </div>
            <!-- FINE Tipo dicumento ed estremi documento in alto a destra -->
                    
            <!-- INIZIO Tabella destinatario e destinazione-->
            <div id="customer_destination_tables">
                <table>
                    <tr>
                        <td class="Vertical_external_label">Destinatario</td>
                        <td>
                            <table id="customer_table">
                                <tr>
                                    <td class="Vertical_internal_label">Spett</td>
                                    <td class="Customer_destination_text"><%=dbr_customer.getString("denomination")%></td>
                                </tr>
                                <tr>
                                    <td class="Vertical_internal_label">Via</td>
                                    <td class="Customer_destination_text"><%=dbr_customer.getString("address")%>, <%=dbr_customer.getString("houseNumber")%></td>
                                </tr>
                                <tr>
                                    <td class="Vertical_internal_label">Città</td>
                                    <td class="Customer_destination_text"><%=dbr_customer.getString("postalCode")%>, <%=dbr_customer.getString("city")%></td>
                                </tr>
                            </table>
                        </td>
                        <td class="Vertical_external_label">Destinazione</td>
                        <td>
                            <table>
                                <tr>
                                    <td class="Vertical_internal_label">Spett</td>
                                    <td class="Customer_destination_text"><%=destDenomination%></td>
                                </tr>
                                <tr>
                                    <td class="Vertical_internal_label">Via</td>
                                    <td class="Customer_destination_text"><%=destAddress%></td>
                                </tr>
                                <tr>
                                    <td class="Vertical_internal_label">Città</td>
                                    <td class="Customer_destination_text"><%=destCity%></td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </div>
            <!-- FINE Tabella destinatario e destinazione-->
                                
            <!--Tabella dati transporto ed aspetto della merce-->
            <div id="transport_goods_table">
                <table>
                    <tr>
                        <td>
                            <table>
                                <tr>
                                    <td class="Transport_goods_label">Transporto a cura del</td>
                                    <td class="Transport_goods_text"><%=transportResponsable%></td>
                                </tr>
                                <tr>
                                    <td class="Transport_goods_label">Aspetto esteriore dei beni</td>
                                    <td class="Transport_goods_text"><%=goodsExteriorAspect%></td>
                                </tr>
                            </table>
                        </td>
                        <td>
                            <table>
                                <tr>
                                    <td class="Transport_goods_label">Causale del transporto</td>
                                    <td class="Transport_goods_text"><%=transportReason%></td>
                                </tr>
                                <tr>
                                    <td><span class="Transport_goods_label">N° colli</span><span class="Transport_goods_text"><%=packagesNumber == null ? "" : packagesNumber%></span></td>
                                    <td><span class="Transport_goods_label">Peso Kg.</span><span class="Transport_goods_text"><%=weight == null ? "" : weight%></span></td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    <tr>
                        <td class="Transport_goods_label">Data inizio transporto</td>
                        <td class="Transport_goods_text">_______________________</td>
                    </tr>
                </table>
            </div>
            
            <!-- INIZIO Tabella righe ddt-->
            <table id="deliveryNoteItemsTable" class="Table">
                <thead>
                    <tr>
                        <th class="column_header">Ns Cod.</th>
                        <th class="column_header">Q.tà</th>
                        <th class="column_header">Descrizione</th>
                    </tr>
                    <% for( int i = 0; i < dbr_dliveryNoteRows.rowsCount(); i++)
                       {%>
                            <tr>
                                <td class="column_text"><%=dbr_dliveryNoteRows.getString(i,"code")%></td><td class="column_text"><%=dbr_dliveryNoteRows.getInteger(i,"quantity")%></td><td class="column_text"><%=dbr_dliveryNoteRows.getString(i,"description")%></td>
                            </tr>
                       <%}%>
                </thead>

                <tbody></tbody>
            </table>
            <!-- FINE Tabella righe ddt-->
            
            <!-- INIZIO Tabella trasportatore-->
            <table>
                <tr>
                    <td class="Transport_goods_label">Vettore</td>
                    <td class="Transporter_goods_text"><%=dbr_transporter.getString("denomination")%></td>
                    <td class="Transport_goods_label">Indirizzo</td>
                    <td class="Transport_goods_text"><%=dbr_transporter.getString("address")%>, <%=dbr_transporter.getString("houseNumber")%> <%=dbr_transporter.getString("postalCode")%> <%=dbr_transporter.getString("city")%></td>
                    <td class="Transport_goods_label">Data inizio trasporto</td>
                    <td class="Transporter_goods_text">____________________</td>
                    <td class="Transport_goods_label">Firma</td>
                    <td class="Transporter_goods_text">______</td>
                </tr>
            </table>
            <!-- FINE Tabella trasportatore-->
            
           
             
            <!-- INIZIO Tabella note e firme-->
            <div id="notes_signs_table">
                <table>
                    <tr>
                        <td class="Vertical_external_label colored">Note</td>
                        <td colored><%=notes%></td>
                        <td class="Vertical_external_label">Firme</td>
                        <td>
                            <table>
                                <tr>
                                    <td class="Vertical_internal_label">Conducente</td>
                                    <td class="Customer_destination_text">________</td>
                                </tr>
                                <tr>
                                    <td class="Vertical_internal_label">Destinatario</td>
                                    <td class="Customer_destination_text">__________</td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </div>
            <!-- FINE Tabella destinatario e destinazione-->
            
            <!--pié di pagina-->
            <div class="Footer">
                Sede legale: via Scipione Ronchetti n.189/92 - 21044 Cavatia con Premezzo ( VA )
                Capitale sociale Euro 10.000 i.v. - Registro Imprese di Varese 02677820124 - REA VA - 276830
            </div>
                    
        </div>
    </body>
    
    <style>
        
    </style>
</html>
