<%-- 
    Document   : changeCredentials
    Created on : 6-mar-2018, 15.15.58
    Author     : Franco
--%>
<%@page import="java.util.Date"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% Long timestamp = new Date().getTime();%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <link href="/Samurai/common.css" rel="stylesheet">
    </head>
    <body>
        <div class="loader" id="loader"></div>
        <div id="modal" class="modal"></div>
        <div class="Modal_screen Visible">
            <div class="New_record">
                <input id='old_username' placeholder="username attuale"><br>
                <input type="password" id='old_password' placeholder="password attuale"><br>
                <input type="password" id='new_password' placeholder="nuova password"><br>
                <span type='button' class='Button Accept' onclick="app.changePassword();">CONFERMA</span>
                <span type='button' class='Button Cancel' onclick="window.open('dashboard.jsp', '_self');">ANNULLA</span>
            </div>
        </div>
    </body>
    <script src="/Samurai/dbi.js?<%=timestamp%>"></script>
</html>
