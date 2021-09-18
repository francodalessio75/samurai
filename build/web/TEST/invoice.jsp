<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    int I = 20;
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <link rel="stylesheet" href="invoice.css">
    </head>
    <body onload="window.print();">
        
        <table>
            <thead><tr><th>A</th><th>A^2</th></tr></thead>
            
            <tfoot><tr><td colspan="2">&nbsp;</td></tr></tfoot>            
            
            <tbody>
                <%for(int i=0; i<I; i++){%>
                <tr><td><%=i%></td><td><%=i*i%></td></tr>
                <%}%>
            </tbody>

        </table>
            
        <div id="footer">
            footer text 
        </div>

    </body>
</html>
