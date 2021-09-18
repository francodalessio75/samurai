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
    
    // RECUPERO L'ID DELL'ORDINE
    Long order_id = Long.parseLong(request.getParameter("order_id"));

    String sql ="SELECT * FROM dyn_Orders_view WHERE order_id = "+order_id;
    
    DbResult dbr = dao.dbi.execAndCheck(sql).result();

    // RECUPERO L'ID DEL CLIENTE
    Long customer_id = dbr.getLong("customer_id");

    // RECUPERO IL CODICE ORDINE
    String code = dbr.getString("code");

    // RECUPERO IL TIPO MACCHINA
    String machinaryModel = dbr.getString("machinaryModel");

    // RECUPERO IL TIPO LAVORO
    String jobTypeName = dbr.getString("jobTypeName");
    
    // RECUPERO LE NOTE
    String notes = dbr.getString("notes");

    // RECUPERO LA DATA DELL'ORDINE
    String dateString = dbr.getString("date");
    //get a stringbuilder to insert dashes
    StringBuilder dateSB = new StringBuilder(dateString);
    dateSB.insert(4,"-");
    dateSB.insert(7,"-");
    String date = DTFE.format(DTF.parse(dateSB.toString()));
    
    // RECUPERO IL NOME DEL BANNER
    String banner = dao.dbi.execAndCheck("SELECT logo FROM reg_Customers WHERE customer_id = "+customer_id).result().getString("logo");

%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        
        <link href="/Samurai/orderCover.css" rel="stylesheet">
    </head>
    <body <!--onload="window.print(); window.close();"--> >
        <div id="mainContainer">
            
            <img  id="banner" src="resources/Logo/<%=banner%>">
            
            <div id="jobTypeContainer">
                <div id="jobTypeLabel">TIPO LAVORO</div>
                <div id="jobTypeText">
                    <%=jobTypeName%>
                </div>
            </div>
            
            <!--<div id="languagesContainer">
                <div id="languagesLabel">LINGUE</div>
                <div id="languageCheckBox1"></div><div id="languageCheckBox2"></div><div id="languageCheckBox3"></div><div id="languageCheckBox4"></div>
                <div id="languageCheckBox5"></div><div id="languageCheckBox6"></div><div id="languageCheckBox7"></div><div id="languageCheckBox8"></div>
                <div id="languageCheckBox9"></div><div id="languageCheckBox10"></div><div id="languageCheckBox11"></div><div id="languageCheckBox12"></div>
                <div id="languageCheckBox13"></div><div id="languageCheckBox14"></div><div id="languageCheckBox15"></div><div id="languageCheckBox16"></div>
                <!--
                <div id="languageLine1"></div><div id="languageLine2"></div><div id="languageLine3"></div><div id="languageLine4"></div>
                <div id="languageLine5"></div><div id="languageLine6"></div><div id="languageLine7"></div><div id="languageLine8"></div>
                <div id="languageLine9"></div><div id="languageLine10"></div><div id="languageLine11"></div><div id="languageLine12"></div>
                <div id="languageLine13"></div><div id="languageLine14"></div><div id="languageLine15"></div><div id="languageLine16"></div>
                <div id="languageLine"></div>
               
            </div>-->
            
            
            <div id="machinaryModelContainer">
                <div id="machinaryModelLabel">
                    DESCRIZIONE LAVORO
                </div>
                <div id="machinaryModelText">
                    <%=machinaryModel%>
                </div>
            </div>
                
            <div id="notesContainer">
                <div id="notesLabel">
                    NOTE
                </div>
                <div id="notesText">
                    <%=notes%>
                </div>
            </div>
            
            <!--
            <div id="orderOpeningDataContainer">
                <div id="orderOpeningDataLabel">ORDINE</div>
                <div id="verbalCheckBox"></div><div id="verbalLabel">Verbale</div><div id="receivedFromLabel">ricevuto da</div><div id="receivedFromLine"></div>
                <div id="writtenCheckBox"></div><div id="writtenLabel">Scritto</div><div id="numberLabel">Numero</div><div id="numberLine"></div><div id="dateLabel">del</div><div id="dateLine"></div>
            </div>
            -->
            
            <div id="orderCodeContainer">
                <div id="orderCodeLabel">CODICE LAVORO:</div>
                <div id="orderCode"><%=code%></div>
            </div>
            
            <div id="orderDateContainer">
                <div id="orderOpeningDateLabel">DATA APERTURA: </div>
                <div id="orderOpeningDate"><%=date%></div>
                <div id="orderClosingDateLabel">DATA CHIUSURA:</div>
                <div id="orderClosingDate">__-__-____</div>
            </div>
           
            
        </div>
    </body>
    
    <style>
        
    </style>
</html>
