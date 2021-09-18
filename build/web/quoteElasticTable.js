/* global fetch */
if(typeof app==='undefined' || app===null) app = {};

console.log("START quoteElasticTable.js");

app.quoteRowsTBody = document.querySelector("#quoteRowsTable tbody");
app.table = document.querySelector("#quoteRowsTable");

app.display=(html)=>{app.displayDiv.innerHTML = html;};

app.newItemRow=()=>
{
    var itemTemplate = document.getElementById("row_template");
    var itemRow = document.importNode(itemTemplate.content,true);
    return itemRow;
};

app.addItemRowAfter=(row)=>
{
    app.quoteRowsTBody.insertBefore(app.newItemRow(),row.nextElementSibling);
    
};

app.addItemRowBefore=(row)=>
{
    app.quoteRowsTBody.insertBefore(app.newItemRow(),row);
};

app.moveItemRowUp=(row)=>
{
    app.quoteRowsTBody.insertBefore(row,row.previousElementSibling);
};

app.moveItemRowDown=(row)=>
{
    app.quoteRowsTBody.insertBefore(row,row.nextElementSibling.nextElementSibling);
};

app.deleteItemRow=(row)=>
{
    if(app.quoteRowsTBody.rows.length > 1)
    {
        app.quoteRowsTBody.removeChild(row);
        app.refreshAmounts();
    }
};

app.quoteRowsToJson=()=>
{
    let jsonItems = [];
    
    Array.from(app.quoteRowsTBody.rows).forEach((row)=>
    {
        //retrieves deliveryNoteRow_id if exists
        jsonItems.push(
        {
            description: row.querySelector(".Description textarea").value,
            rowAmount:  row.querySelector(".RowAmount input").value
        });
    });
    
    return jsonItems;
};


console.log("END quoteElasticTable.js");