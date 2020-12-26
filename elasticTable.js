/* global fetch */
if(typeof app==='undefined' || app===null) app = {};

app.deliveryNoteItemsTBody = document.querySelector("#deliveryNoteItemsTable tbody");
app.table = document.querySelector("#deliveryNoteItemsTable");

app.display=(html)=>{app.displayDiv.innerHTML = html;};

app.newItemRow=()=>
{
    var itemTemplate = document.getElementById("item_template");
    var itemRow = document.importNode(itemTemplate.content,true);
    return itemRow;
};

app.addItemRowAfter=(row)=>
{
    app.deliveryNoteItemsTBody.insertBefore(app.newItemRow(),row.nextElementSibling);
    
};

app.addItemRowBefore=(row)=>
{
    app.deliveryNoteItemsTBody.insertBefore(app.newItemRow(),row);
};

app.moveItemRowUp=(row)=>
{
    app.deliveryNoteItemsTBody.insertBefore(row,row.previousElementSibling);
};

app.moveItemRowDown=(row)=>
{
    app.deliveryNoteItemsTBody.insertBefore(row,row.nextElementSibling.nextElementSibling);
};

app.deleteItemRow=(row)=>
{
    app.deliveryNoteItemsTBody.removeChild(row);
};

app.itemsToJson=()=>
{
    let jsonItems = [];
    
    Array.from(app.deliveryNoteItemsTBody.rows).forEach((row)=>
    {
        jsonItems.push(
        {
            code: row.querySelector(".Code input").value,
            description: row.querySelector(".Description input").value,
            quantity: row.querySelector(".Quantity input").value
        });
    });
    
    return jsonItems;
};