//global variable
app =
{
    deliveryNoteItemsTBody: document.querySelector("#deliveryNoteItemsTable tbody"),
    displayDiv: document.querySelector("#displayDiv")
};

//writes in display area html parameter content
app.display=(html)=>{app.displayDiv.innerHTML = html;};

//fetchs from itemTemplate structure a new row and retuurns it 
app.newItemRow=()=>
{
    var itemTemplate = document.getElementById("item_template");
    var itemRow = document.importNode(itemTemplate.content,true);
    return itemRow;
};

/**
 * 
 * @param {type} row
 * @returns {undefined}
 */
app.addItemRowAfter=(row)=>
{
    //retrieves the table then fetch a new row through newItemRow() method
    //then inserts it before the next sibling row that is after the current row
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

/**
 * 
 * @returns {Array|app.itemsToJson.jsonItems} 
 */
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


