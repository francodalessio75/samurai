/* global fetch */
if(typeof app==='undefined' || app===null) app = {};

console.log("START creditNoteElasticTable.js");

app.creditNoteRowsTBody = document.querySelector("#creditNoteItemsTable tbody");
app.table = document.querySelector("#creditNoteRowsTable");

app.display=(html)=>{app.displayDiv.innerHTML = html;};

app.newCNItemRow=()=>
{
    var itemTemplate = document.getElementById("item_template");
    var itemRow = document.importNode(itemTemplate.content,true);
    return itemRow;
};

app.addCNItemRowAfter=(row)=>
{
    app.creditNoteRowsTBody.insertBefore(app.newCNItemRow(),row.nextElementSibling);
    
};

app.addCNItemRowBefore=(row)=>
{
    app.creditNoteRowsTBody.insertBefore(app.newCNItemRow(),row);
};

app.moveCNItemRowUp=(row)=>
{
    app.creditNoteRowsTBody.insertBefore(row,row.previousElementSibling);
};

app.moveCNItemRowDown=(row)=>
{
    app.creditNoteRowsTBody.insertBefore(row,row.nextElementSibling.nextElementSibling);
};

app.deleteCNItemRow=(row)=>
{
    if(app.creditNoteRowsTBody.rows.length > 1)
    {
        app.creditNoteRowsTBody.removeChild(row);
        app.refreshAmounts();
    }
};

app.creditNoteItemsToJson=()=>
{
    let jsonCNItems = [];
    
    Array.from(app.creditNoteRowsTBody.rows).forEach((row)=>
    {
        //retrieves deliveryNoteRow_id if exists
        jsonCNItems.push(
        {
            description: row.querySelector(".Description textarea").value,
            quantity: row.querySelector(".Quantity input").value,
            singleAmount: row.querySelector(".SingleAmount input").value,
            totalAmount:  row.querySelector(".TotalAmount input").value
        });
    });
    
    return jsonCNItems;
};


console.log("END creditNoteElasticTable.js");


