/* global fetch */
if(typeof app==='undefined' || app===null) app = {};

console.log("START invoiceElasticTable.js");

app.invoiceItemsTBody = document.querySelector("#invoiceItemsTable tbody");
app.table = document.querySelector("#invoiceItemsTable");

app.display=(html)=>{app.displayDiv.innerHTML = html;};

app.newItemRow=()=>
{
    var itemTemplate = document.getElementById("item_template");
    var itemRow = document.importNode(itemTemplate.content,true);
    return itemRow;
};

app.addItemRowAfter=(row)=>
{
    app.invoiceItemsTBody.insertBefore(app.newItemRow(),row.nextElementSibling);
    
};

app.addItemRowBefore=(row)=>
{
    app.invoiceItemsTBody.insertBefore(app.newItemRow(),row);
};

app.moveItemRowUp=(row)=>
{
    app.invoiceItemsTBody.insertBefore(row,row.previousElementSibling);
};

app.moveItemRowDown=(row)=>
{
    app.invoiceItemsTBody.insertBefore(row,row.nextElementSibling.nextElementSibling);
};

app.deleteItemRow=(row)=>
{
    if(app.invoiceItemsTBody.rows.length > 1)
    {
        app.invoiceItemsTBody.removeChild(row);
        app.refreshAmounts();
    }
};

app.setRowAsInvoiced=(row)=>
{
    if(app.invoiceItemsTBody.rows.length > 1)
    {
        const deliveryNoteRow_id = row.getAttribute("data-delivery-note-row_id");
         const confirmed = window.confirm(
            "Vuoi davvero segnare questa riga come già fatturata?"
          );
        if(!confirmed){
            return;
        } else {
            console.log("inside");
            app.setDeliveryNoteRowAsInvoiced(
                deliveryNoteRow_id,
                function ()//successCallBack
                {
                    app.invoiceItemsTBody.removeChild(row);
                    app.refreshAmounts();
                    document.querySelector(".Footer_message").innerHTML = "Riga segnata come fatturata!";
                },
                function ()//failCallBack
                {
                    window.alert("Operazione fallita!");
                    document.querySelector(".Footer_message").innerHTML = "Non riesco ad eseguire l'operazione!";
                }
            );
        } 
    }
};

app.invoiceItemsToJson=()=>
{
    let jsonItems = [];
    
    Array.from(app.invoiceItemsTBody.rows).forEach((row)=>
    {
        //retrieves deliveryNoteRow_id if exists
        jsonItems.push(
        {
            code: row.querySelector(".Code input").value,
            description: row.querySelector(".Description textarea").value,
            deliveryNoteRow_id: parseInt(row.dataset.deliveryNoteRow_id),
            quantity: row.querySelector(".Quantity input").value,
            singleAmount: row.querySelector(".singleAmount input").value,
            totalAmount:  row.querySelector(".totalAmount input").value
        });
    });
    
    return jsonItems;
};


console.log("END invoiceElasticTable.js");