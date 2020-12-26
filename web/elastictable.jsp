<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>ELASTIC TABLE</title>
        <link rel="stylesheet" href="elastictable.css">
    </head>
    <body>
        
        <!--------- ELASTIC TABLE ---------->
        <table id="deliveryNoteItemsTable" class="ItemsTable">
            <thead>
                <tr>
                    <th>Codice</th>
                    <th>Descrizione</th>
                    <th>Quantità</th>
                    <th>Azioni</th>
                </tr>
            </thead>
            
            <tbody></tbody>
            
            <tfoot>
                <tr>
                    <th colspan="4">
                        <span class="Button" onclick="app.addItemRowBefore(null);">AGGIUNGI RIGA</span>
                        <span class="Button" onclick="app.display(JSON.stringify(app.itemsToJson(),null,4));">GENERA JSON</span>
                    </th>
                </tr>
            </tfoot>
        </table>
        
        <!--------- ELASTIC TABLE ITEM TEMPLATE ---------->
        <template id="item_template">
            <tr>
                <td class="Code"><input type="text" placeholder="CODICE"></td>
                <td class="Description"><input type="text" placeholder="DESCRIZIONE"></td>
                <td class="Quantity"><input type="text" placeholder="QUANTITA'"></td>
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

        <!--------- DISPLAY ---------->
        <div id="displayDiv"></div>
        
        <!--------- SEZIONE SCRIPT ---------->
        <script src="elastictable.js"></script>
        <script>app.addItemRowBefore(null);</script>
    </body>
</html>
