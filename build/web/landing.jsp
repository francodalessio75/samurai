<%@page import="com.dalessio.samurai.Config"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.Date"%>
<!DOCTYPE html>
<!-- E' la pagina dove l'utente viene indirizzato al primo accesso e 
     quando qualcosa è andato storto
-->
<%
    Long timestamp = new Date().getTime();
    
    session.invalidate();
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Workline 2.0</title>
        <!-- foglio di stile con impostazioni grafiche di tema generale-->
        <link href="/Samurai/common.css" rel="stylesheet">
        <!-- foglio di stile contenente impostazioni specifiche della pagina-->
        <link href="/Samurai/landing.css" rel="stylesheet">
    </head>

    <body onload="document.getElementById('username').focus();">
        <div class="Header">
            <span class="SWName">Workline 2.0</span>
        </div>
        
        <div class="Footer">
            <span class="Footer_message">inserisci le tue credenziali</span>
        </div>
        
        <div id="credentials">
            <!--ad ogni pressione di pulsante chiama la funzione javascript -->
            <input id="username" type="text" placeholder="username" onkeydown="app.username_onKeyDown(event);" value="<%=Config.DEBUG ? "franco" : ""%>"><br>
            <input id="password" type="password" placeholder="password" onkeydown="app.password_onKeyDown(event);" value="<%=Config.DEBUG ? "franco" : ""%>">
        </div>
        
    </body>
    <!--carica il file javascript-->
    <script src="/Samurai/landing.js?<%=timestamp%>"></script>
    <script src="/Samurai/dbi.js?<%=timestamp%>"></script>
    <script>setInterval(function(){app.ping();},60000);</script>
</html>