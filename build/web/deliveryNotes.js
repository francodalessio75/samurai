/* global fetch, Promise */

//app is global variable implemented as singleton
if (typeof app === 'undefined' || app === null)
    app = {};

console.log('BEGIN:deliveryNotes.js');


//contains all filters values
app.filter = {};

//new delivery note object
app.newDeliveryNote = {};

//existing delivery note object to be updated
app.deliveryNote = {};


//current customer_id necessary to check if the orderCode inserted by he user is 
//correctly related to the customer
app.customer_id = null;

//collection if all customer orders to be suggested as delivery note item
app.suggestedOrders = null;

//delivery notes rows table 
app.deliveryNoteItemsTBody = document.querySelector("#deliveryNoteItemsTable tbody");

//result message of callCalidCode function
app.resultValidCodeMessage = "";

/* the deliveryNote_id value involves wich between creation or update must be done*/
app.deliveryNoteConfirmed = function (deliveryNote_id, user_id)
{
    //if deliveryNote_id is 0 then it is a creation
    if (deliveryNote_id === 0)
    {
        //creation
        app.createNewDeliveryNote( user_id );
    } else
    {
        //update
        app.callUpdateDeliveryNote(deliveryNote_id, user_id);
    }
};

/**
 * When the user chooses the customer remaining customer fields will be filled automatically
 * @returns {undefined}
 */
app.suggestDeliveryNoteFields = function ()
{
    //first delete / reset all items rows
    //iterates all rows beginning from 1 to preserve the first row
    for( var i = 1; i < app.deliveryNoteItemsTBody.rows.length; i++ )
    {
        //gathes the current row
        var currentRow = app.deliveryNoteItemsTBody.rows[i];
        app.deleteItemRow(currentRow);
    }
    //add a new row down the first row
    app.addItemRowAfter(app.deliveryNoteItemsTBody.rows[0]);
    //delete the first row
    app.deleteItemRow(app.deliveryNoteItemsTBody.rows[0]);
    
    app.customer_id = document.getElementById('denomination_select_options').value;

    app.readCustomers(app.customer_id, null,
            function (customers)
            {
                for (var i = 0; i < customers.length; i++)
                {
                    document.getElementById('address_input').value = customers[i][9] + "";
                    document.getElementById('houseNumber_input').value = customers[i][10];
                    document.getElementById('postalCode_input').value = customers[i][11];
                    document.getElementById('city_input').value = customers[i][8];
                    document.getElementById('province_input').value = customers[i][12];
                }
                app.deliveryNoteDetailsChanged("denomination_select_options");
                app.deliveryNoteDetailsChanged("address_input");
                app.deliveryNoteDetailsChanged("houseNumber_input");
                app.deliveryNoteDetailsChanged("postalCode_input");
                app.deliveryNoteDetailsChanged("city_input");
                app.deliveryNoteDetailsChanged("province_input");
                /* 2018-12-10 the fuction call has been eliminates becouse there are too many orders automatically inserted in the delivery note body*/
                //app.getSuggestedOrdersRows();
            },
            function ()
            {
                window.alert("non riesco a prelevare i dati del Cliente.");
                document.getElementById("denomination_select_options").textContent = "";
                document.getElementById("address_input").textContent = "";
                document.getElementById("houseNumber_input").textContent = "";
                document.getElementById("postalCode_input").textContent = "";
                document.getElementById('city_input').value = "";
            }
    );
};

app.fillOrderMachinaryModel = (orderCodeInput) =>
{
    var orderCode = orderCodeInput.value.trim();

    //if a customer has been choosen
    if (orderCode !== "" && app.customer_id !== "" && app.customer_id !== undefined && app.customer_id !== null )
    {
        app.getValidityMachinaryModelByOrderCode(orderCode, app.customer_id,
            function (messages)
            {
                //cheks the result, only if the code exists and belongs to the customer 
                //writes machinary model in the cell. If not delete the content
                if (messages[0] === "NOT VALID")
                {
                    window.alert("codice lavoro non valido!");
                    orderCodeInput.value = "";
                } else if (messages[0] === "NOT BELONGS")
                {
                    window.alert("codice lavoro non appartenente al Cliente selezionato!");
                    orderCodeInput.value = "";
                } else if (messages[0] === "NOT COMPLETED")
                {
                    window.alert("codice relativo ad un lavoro non completato!");
                    orderCodeInput.value = "";
                }
                //checks if the order has been delivered / invoiced
                else if (messages[0] === "" && messages[1] === "DELIVERED")
                {
                    //confirm('Il lavoro risulta già consegnato, continuare comunque?') ? orderCodeInput.parentNode.nextElementSibling.firstElementChild.value = messages[2] : orderCodeInput.value = "";
                    if (confirm('Il lavoro risulta già consegnato, continuare comunque?'))
                    {
                        let description = '';
                    
                        description += messages[2];//descrizione lavoro

                        if( messages[3] !== '' )
                            description += '; '+messages[3];//ordine

                        if( messages[4] !== '' )
                        description += '; '+messages[4]+';';//commessa
                    
                        orderCodeInput.parentNode.nextElementSibling.firstElementChild.value = description;
                    } else
                    {
                        orderCodeInput.value = "";
                    }
                } else if (messages[0] === "" && messages[1] === " INVOICED")
                {
                    if (confirm('Il lavoro risulta già fatturato, continuare comunque?'))
                    {
                        let description = '';
                    
                        description += messages[2]+"; ";//descrizione lavoro

                        if( messages[3] !== '' )
                            description += messages[3]+"; ";//ordine

                        if( messages[4] !== '' )
                            description += messages[4]+';';//commessa

                        orderCodeInput.parentNode.nextElementSibling.firstElementChild.value = description;
                    } else
                    {
                        orderCodeInput.value = "";
                    }
                } else if (messages[0] === "" && messages[1] === "DELIVERED INVOICED")
                {
                    if (confirm('Il lavoro risulta già consegnato e fatturato, continuare comunque?'))
                    {
                        let description = '';
                    
                        description += messages[2];//descrizione lavoro

                        if( messages[3] !== '' )
                            description += '; '+messages[3];//ordine

                        if( messages[4] !== '' )
                            description += '; '+messages[4]+';';//commessa

                        orderCodeInput.parentNode.nextElementSibling.firstElementChild.value = description;
                    } else
                    {
                        orderCodeInput.value = "";
                    }
                }
                //if everythink's alright fills the macinnary model fiels
                else if (messages[0] === "" && messages[1] === "")
                {
                    let description = '';
                    
                    description += messages[2];//descrizione lavoro
                    
                    if( messages[3] !== '' )
                        description += '; '+messages[3];//ordine
                    
                    if( messages[4] !== '' )
                        description += '; '+messages[4]+';';//commessa
                    
                    orderCodeInput.parentNode.nextElementSibling.firstElementChild.value = description;
                    
                }
            },
            function ()
            {
                window.alert("non riesco a leggere la descrizione lavoro");
            });
    } else
    {
        window.alert("Prima di operare sulle righe seleziona un Cliente");
        orderCodeInput.value = "";
    }
};

/*Makes visible the update button and changes the look of the cell that's been changed*/
app.deliveryNoteDetailsChanged = function (field_id)
{
    /* INSERT FIELDS CHECKING*/
    document.getElementById(field_id).classList.add('Changed');
};

app.createNewDeliveryNote = function ( user_id )
{
    //alert message
    let message = "";

    //general delivery note data
    //cheks each field if something is wrong write an alert message
    if (document.getElementById('denomination_select_options').value === "")
        message += "Scegli un Destinatario!\n";

    //if destDenomination is not "IDEM" all destination fields must be filled
    if (document.getElementById('destDenomination_input').value !== "IDEM")
    {
        if (document.getElementById('destAddress_input').value === "" || document.getElementById('destCity_input').value === "" || document.getElementById('destPostalCode_input').value === "" || document.getElementById('destProvince_input').value === "")
            message += "Controlla campi Destinazione.\n";
    }
    //cheks transport and goods data fields
    if (document.getElementById('transport_responsable_input').value === "" || document.getElementById('transport_reason_input').value === "" || document.getElementById('goods_exterior_aspect_input').value === "")
        message += "Controlla i campi relativi al trasporto ed all'aspetto dei beni.\n";


    //if there is only one row and it is empty alerts tehe matter
    if (app.deliveryNoteItemsTBody.rows.length === 1  && app.deliveryNoteItemsTBody.rows[0].querySelector(".Code input").value === "" && app.deliveryNoteItemsTBody.rows[0].querySelector(".Description textarea").value === "" && app.deliveryNoteItemsTBody.rows[0].querySelector(".Quantity input").value === "")
        message += " Il ddt non contiene articoli! ";
                
    /* Delivery notes rows section*/
    if (message === "")
    {
        //if there is unless one row, begins to chek else alerts that the document has no rows
        if (app.deliveryNoteItemsTBody.rows.length > 0)
        {
            /* insertedCodes.push( app.deliveryNoteItemsTBody.rows[0].querySelector(".Code input").value.trim() ); checks for replication*/

            //iterates all rows
            for (var i = 0; i < app.deliveryNoteItemsTBody.rows.length; i++)
            {
                //gathes the current row
                var currentRow = app.deliveryNoteItemsTBody.rows[i];

                //gathes the current code without spaces
                //var currentCode = currentRow.querySelector(".Code input").value.trim();

                //THIS CHECK HAS BEEN ELIMINATED BECAUSE ANGELO SAID THAT THERE IS THE POSSIBILITY TO 
                //DELIVER MULTIPLE ITEMS HAVING THE SAME ORDER CODE REFERENCE
                /*if there are more than one row checks for duplication
                if (app.deliveryNoteItemsTBody.rows.length > 1)
                {
                    //to avoid to compare the same code, iterates the inserted codes array BEFORE to insert in it 
                    //the current code. 
                    for (var j = 0; j < app.deliveryNoteItemsTBody.rows.length; j++)
                    {
                        //if the code is not empty and the iteration is comparing a row susequent of the roe of external iteration : j>i
                        if (currentCode !== "" && (j > i) && currentCode === app.deliveryNoteItemsTBody.rows[j].querySelector(".Code input").value.trim())
                            message += " Il codice " + currentCode + "della riga " + (j + 1) + " è stato gia inserito nella riga " + (i + 1) + "\n";
                    }
                }*/
                
                //if the rows is empty removes it from the table
                if (currentRow.querySelector(".Code input").value === "" && currentRow.querySelector(".Description textarea").value === "" && currentRow.querySelector(".Quantity input").value === "")
                    app.deleteItemRow(currentRow);
                //if there's no quantity alerts the matter
                if (!(currentRow.querySelector(".Description textarea").value === "") && currentRow.querySelector(".Quantity input").value === "")
                    message += " Nessuna quantità nella riga " + (i + 1) + "\n";
                //if there's no description alerts the matter
                if (currentRow.querySelector(".Description textarea").value === "" && !(currentRow.querySelector(".Quantity input").value === ""))
                    message += " Nessuna descrizione nella riga " + (i + 1) + "\n";
                ;
                //if there's no description and no quantity alerts the matter
                if (!(currentRow.querySelector(".Code input").value === "") && currentRow.querySelector(".Description textarea").value === "" && currentRow.querySelector(".Quantity input").value === "")
                    message += " Mancano Descrizione e Quantità alla riga " + (i + 1) + "\n";
            }

            //if message is empty and there is unless one row creates the new delivery note else alerts the message
            if (message === "" && app.deliveryNoteItemsTBody.rows.length > 0)
            {
                app.newDeliveryNote.customer_id = app.customer_id;
                app.newDeliveryNote.transporter_id = document.getElementById("transporterDenomination_select_options").value;
                app.newDeliveryNote.date = document.getElementById("date_input").value;
                app.newDeliveryNote.destDenomination = document.getElementById("destDenomination_input").value;
                app.newDeliveryNote.destAddress = document.getElementById("destAddress_input").value;
                app.newDeliveryNote.destCity = document.getElementById("destCity_input").value;
                app.newDeliveryNote.destHouseNumber = document.getElementById("destHouseNumber_input").value;
                app.newDeliveryNote.destPostalCode = document.getElementById("destPostalCode_input").value;
                app.newDeliveryNote.destProvince = document.getElementById("destProvince_input").value;
                app.newDeliveryNote.transportResponsable = document.getElementById("transport_responsable_input").value;
                app.newDeliveryNote.transportReason = document.getElementById("transport_reason_input").value;
                app.newDeliveryNote.goodsExteriorAspect = document.getElementById("goods_exterior_aspect_input").value;
                app.newDeliveryNote.packagesNumber = document.getElementById("packagesNumber_input").value;
                app.newDeliveryNote.weight = document.getElementById("weight_input").value;
                app.newDeliveryNote.notes = document.getElementById("notes_input").value;
                app.newDeliveryNote.items = app.deliveryNoteItemsToJson();
                
                //let suggestDDT = confirm('DDT Creato. Clicca su OK se vuoi che gli articoli vengano suggeriti in Fattura, altrimenti clicca su ANNULLA.');
                //removed on june the 6th 2019 so now all delivery notes are suggested but updating it is possible to change this condition
                
                app.createDeliveryNote(app.newDeliveryNote, true,
                    (deliveryNote_id)=>
                    {
                        app.printDeliveryNote(deliveryNote_id, user_id,
                            ()=>
                            {
                                //window.open("resources/DDT/DDT_"+deliveryNote_id+".pdf","_blank");
                                window.open('deliveryNote_details.jsp?deliveryNote_id='+deliveryNote_id,'_self');
                            },
                            ()=>
                            {
                                document.querySelector(".Footer_message").innerHTML = "non riesco a produrre il pdf del ddt. ";
                            });
                    },
                    ()=>
                    {
                            document.querySelector(".Footer_message").innerHTML = "non riesco a creare il ddt. ";
                    });

            } else
                window.alert(message);
        } else
            window.alert("Il documento non ha articoli!");
    } else
        window.alert(message);
};

/**/
app.callUpdateDeliveryNote = function (deliveryNote_id, user_id)
{
    app.deliveryNote.deliveryNote_id = deliveryNote_id;
    //alert message
    let message = "";

    //general delivery note data
    //cheks each field if something is wrong write an alert message
    if (document.getElementById('denomination_select_options').value === "")
        message += "Scegli un Destinatario!\n";

    //if destDenomination is not "IDEM" all destination fields must be filled
    if (document.getElementById('destDenomination_input').value !== "IDEM")
    {
        if (document.getElementById('destAddress_input').value === "" || document.getElementById('destCity_input').value === "" || document.getElementById('destPostalCode_input').value === "" || document.getElementById('destProvince_input').value === "")
            message += "Controlla campi Destinazione.\n";
    }
    //cheks transport and goods data fields
    if (document.getElementById('transport_responsable_input').value === "" || document.getElementById('transport_reason_input').value === "" || document.getElementById('goods_exterior_aspect_input').value === "")
        message += "Controlla i campi relativi al trasporto ed all'aspetto dei beni.\n";
    
    // Delivery notes rows section
    if (message === "")
    {
        //assigns rows
        //app.deliveryNoteItemsTBody = document.querySelector("#deliveryNoteItemsTable tbody");
        
        //app.callUpdateDeliveryNote
        //if there is unless one row, begins to chek else alerts that the document has no rows
        if (app.deliveryNoteItemsTBody.rows.length > 0)
        {

            //iterates all rows
            for (var i = 0; i < app.deliveryNoteItemsTBody.rows.length; i++)
            {
                //gathes the current row
                var currentRow = app.deliveryNoteItemsTBody.rows[i];

                //gathes the current code withut spaces
                //var currentCode = currentRow.querySelector(".Code input").value.trim();

                //THIS CHECK HAS BEEN ELIMINATED BECAUSE ANGELO SAID THAT THERE IS THE POSSIBILITY TO 
                //DELIVER MULTIPLE ITEMS HAVING THE SAME ORDER CODE REFERENCE
                /*if there are more than one row checks for duplication
                if (app.deliveryNoteItemsTBody.rows.length > 1)
                {
                    //to avoid to compare the same code, iterates the inserted codes array BEFORE to insert in it 
                    //the current code. 
                    for (var j = 0; j < app.deliveryNoteItemsTBody.rows.length; j++)
                    {
                        //if the code is not empty and the iteration is comparing a row susequent of the roe of external iteration : j>i
                        if (currentCode !== "" && (j > i) && currentCode === app.deliveryNoteItemsTBody.rows[j].querySelector(".Code input").value.trim())
                            message += " Il codice " + currentCode + "della riga " + (j + 1) + " è stato gia inserito nella riga " + (i + 1) + "\n";
                    }
                }*/

                //if the rows is empty removes it from the table
                if (currentRow.querySelector(".Code input").value === "" && currentRow.querySelector(".Description textarea").value === "" && currentRow.querySelector(".Quantity input").value === "")
                    app.deleteItemRow(currentRow);
                //if there's no quantity alerts the matter
                if (!(currentRow.querySelector(".Description textarea").value === "") && currentRow.querySelector(".Quantity input").value === "")
                    message += " Nessuna quantità nella riga " + (i + 1) + "\n";
                //if there's no description alerts the matter
                if (currentRow.querySelector(".Description textarea").value === "" && !(currentRow.querySelector(".Quantity input").value === ""))
                    message += " Nessuna descrizione nella riga " + (i + 1) + "\n";
                ;
                //if there's no description and no quantity alerts the matter
                if (!(currentRow.querySelector(".Code input").value === "") && currentRow.querySelector(".Description textarea").value === "" && currentRow.querySelector(".Quantity input").value === "")
                    message += " Mancano Descrizione e Quantità alla riga " + (i + 1) + "\n";
            }

            //if message is empty and there is unless one row creates the new delivery note else alerts the message
            if (message === "" && app.deliveryNoteItemsTBody.rows.length > 0)
            {
                app.deliveryNote.customer_id = document.getElementById("denomination_select_options").value;
                app.deliveryNote.transporter_id = document.getElementById("transporterDenomination_select_options").value;
                app.deliveryNote.date = document.getElementById("date_input").value;
                app.deliveryNote.destDenomination = document.getElementById("destDenomination_input").value;
                app.deliveryNote.destAddress = document.getElementById("destAddress_input").value;
                app.deliveryNote.destCity = document.getElementById("destCity_input").value;
                app.deliveryNote.destHouseNumber = document.getElementById("destHouseNumber_input").value;
                app.deliveryNote.destPostalCode = document.getElementById("destPostalCode_input").value;
                app.deliveryNote.destProvince = document.getElementById("destProvince_input").value;
                app.deliveryNote.transportResponsable = document.getElementById("transport_responsable_input").value;
                app.deliveryNote.transportReason = document.getElementById("transport_reason_input").value;
                app.deliveryNote.goodsExteriorAspect = document.getElementById("goods_exterior_aspect_input").value;
                app.deliveryNote.packagesNumber = document.getElementById("packagesNumber_input").value;
                app.deliveryNote.weight = document.getElementById("weight_input").value;
                app.deliveryNote.notes = document.getElementById("notes_input").value;
                app.deliveryNote.items = app.deliveryNoteItemsToJson();
                
                let notSuggest = document.getElementById("suggest_checkbox") === null ? false :  document.getElementById("suggest_checkbox").checked;

               
                app.updateDeliveryNote(app.deliveryNote, !notSuggest,
                (deliveryNote_id)=>
                {
                    app.printDeliveryNote(deliveryNote_id,user_id,
                        ()=>
                        {
                            window.open('deliveryNote_details.jsp?deliveryNote_id='+deliveryNote_id,'_self');
                        },
                        ()=>
                        {
                            document.querySelector(".Footer_message").innerHTML = "non riesco a stampare il ddt. ";
                        });
                },
                ()=>
                {
                    window.alert("La modifica non è andata a buon fine. Controllare che il ddt non contenga righe collegate ad una fattura");
                    document.querySelector(".Footer_message").innerHTML = "non riesco a modificare il ddt. ";
                });
                
            } else
                window.alert(message);
        } else
            window.alert("Il documento non ha articoli!");
    } else
        window.alert(message);
};

/*retrieves filters values from the page*/
app.getFiltersValues = function ()
{
    app.filter.customer_id = document.getElementById("customer_select_options").value;

    app.filter.from_date = document.getElementById("from_date").value;

    app.filter.to_date = document.getElementById("to_date").value;
    
    app.filter.number = document.getElementById("numberFilter").value;

    return app.filter;
};

/*** Delivery Notes page ************************/

app.openDeliveryNotesPage = function ()
{
    document.querySelector(".Footer_message").innerHTML = "Sto caricando i DDT... ";
    //assigns to app.filter current values
    app.filter = app.getFiltersValues();
    //opens tasks page       
    window.open("deliveryNotes.jsp?&filter=" + encodeURIComponent(JSON.stringify(app.filter)), '_self');
};

/* updtates the tasks variable in acording to current filters values in the page*/
app.filterDeliveryNotes = function ()
{
    document.querySelector(".Footer_message").innerHTML = "Sto filtrando i DDT...";
    
    //assigns to app.filter current values
    var filter = app.getFiltersValues();

    app.readDeliveryNotes(// deliveryNote_id, customer_id, transporter_id, number, fromDate, toDate, successCallback, failCallback
            null, //deliveryNote_id
            filter.customer_id, //customer_id
            null, //transporter_id
            filter.number,
            filter.from_date, //fromDate
            filter.to_date, //toDate
            function (deliveryNotes)//successCallBack
            {
                app.fillDeliveryNotesTable(deliveryNotes);
                document.querySelector(".Footer_message").innerHTML = "DDT FILTRATI: " + deliveryNotes.length;
            },
            function ()//failCallBack
            {
                document.querySelector(".Footer_message").innerHTML = "non riesco a filtrare i DDT! Contattare Assistenza. ";
            }
    );
};

/* refersh table rows*/
app.fillDeliveryNotesTable = function (deliveryNotes)
{
    var templateItem = document.getElementById("deliveryNoteTableRow");
    var itemsContainer = document.querySelector(".Table tbody");

    //empty the current content in the table
    itemsContainer.innerHTML = null;

    for (var i = 0; i < deliveryNotes.length; i++)
    {
        var templateContent = document.importNode(templateItem.content, true);

        templateContent.querySelector(".DeliveryNoteTableRow").id = "row_" + deliveryNotes[i].deliveryNote_id;

        templateContent.querySelector(".DeliveryNoteTableRow").onclick = app.setCurrentDeliveryNoteId(deliveryNotes[i].deliveryNote_id);

        templateContent.querySelector(".DeliveryNoteNumber").innerHTML = deliveryNotes[i].number + "-" + deliveryNotes[i].year;
        
        templateContent.querySelector(".Invoiced").innerHTML = deliveryNotes[i].invoiced  ? '<i class="fa fa-money" aria-hidden="true"></i>' : '';

        //formats the date
        var date = deliveryNotes[i].date;
        var year = date.substring(0, 4);
        var month = date.substring(4, 6);
        var day = date.substring(6, 8);
        date = day + "/" + month + "/" + year;
        templateContent.querySelector(".DeliveryNoteDate").innerHTML = date;

        templateContent.querySelector(".Customer").innerHTML = deliveryNotes[i].denomination;
        
        //templateContent.querySelector(".Pdf").onclick = app.callPrintDeliveryNote(deliveryNotes[i].deliveryNote_id);

        let id = deliveryNotes[i].deliveryNote_id;
        let number = deliveryNotes[i].number;

        
        templateContent.querySelector(".Pdf").innerHTML = "PDF";
        
        templateContent.querySelector(".Pdf").onclick = (event)=>
        {
            event.stopPropagation();
            //window.open("resources/DDT/DDT_"+id+".pdf","_blank"); old way
            window.open("resources/DDT/DDT_DUESSE_"+number+"_"+date.substring(8, 10) + ".pdf","_blank");
            
            /*this snippet allows in debug to create from scratch the deliverynote simply by clicking on the las cell of the row
             * useful for testing pdf layut effects withou creating every times a new delivery note
            app.printDeliveryNote(id,
            ()=>
                {
                    window.open("resources/DDT/DDT_"+id+".pdf","_blank");
                    window.open('deliveryNote_details.jsp','_self');
                },
                ()=>
                {
                    document.querySelector(".Footer_message").innerHTML = "non riesco a stampare il ddt. ";
                }
            );*/
             
        };

        itemsContainer.appendChild(templateContent);
    }
};

/**
 * 
 * @param {type} deliveryNote_id
 * @returns {Function}
 */
app.setCurrentDeliveryNoteId = function (deliveryNote_id)
{
    return function ()
    {
        //window.open("customer_details.jsp?customer_id="+customer_id,'_self');
        window.open("deliveryNote_details.jsp?deliveryNote_id=" + deliveryNote_id, '_self');
    };
};

//fills rows table in deliveryNotes_details.jsp
app.getDeliveryNoteRows = function (deliveryNote_id)
{
    //sets current customer_id
    app.customer_id = document.getElementById('denomination_select_options').value;
    
    //fills delivery note rows
    app.readDeliveryNoteRows(deliveryNote_id,
        function (rows)
        {
            //gets the table
            var table = document.querySelector("#deliveryNoteItemsTable tbody");

            //adds empty rows ( all  less one because the table already has an empty row)
            for (var i = 0; i < rows.length - 1; i++)
            {
                //gets a new table row 
                var tableRow = app.newItemRow();
                table.appendChild(tableRow);
            }

            //fills contents
            for (var i = 0; i < table.rows.length; i++)
            {
                for (var j = 0; j < table.rows[i].cells.length; j++)
                {
                    //assigns class and content
                    table.rows[i].cells[0].firstChild.value = rows[i][2];
                    table.rows[i].cells[1].firstChild.value = rows[i][3];
                    table.rows[i].cells[2].firstChild.value = rows[i][4];
                }
            }

        },
        function ()
        {
            window.alert("non riesco a caricare le righe del ddt.");
        });

};

//retrieves suggested orders for that customer that is all customer orders
//completed and not delivered neither invoiced
app.getSuggestedOrdersRows = function ()
{
    //if there chosen customer is empty just reloads the page
    if (app.customer_id === "")
        window.open('deliveryNote_details.jsp', '_self');
    else
    {

        //gets the table
        var table = document.querySelector("#deliveryNoteItemsTable tbody");

        app.getSuggestedOrders(app.customer_id,
                (orders) =>
        {
            app.suggestedOrders = orders;
            //adds empty rows 
            for (var i = 0; i < orders.length; i++)
            {
                //gets a new table row 
                var tableRow = app.newItemRow();
                table.appendChild(tableRow);
            }
            //if there are orders to be suggested fills contents
            if (orders.length > 0)
            {
                //fills rows except the last one that is jus an available empty row
                for (var i = 0; i < table.rows.length - 1; i++)
                {
                    for (var j = 0; j < table.rows[i].cells.length; j++)
                    {
                        //fills code and description disables code cell
                        table.rows[i].cells[0].firstChild.value = orders[i][2];
                        table.rows[i].cells[0].firstChild.setAttribute("disabled","true");
                        table.rows[i].cells[1].firstChild.value = orders[i][12];
                    }
                }
            }
        },
                () =>
        {
            window.alert("non riesco a caricare i codici da fatturare.");
        });
    }

};


app.callPrintDeliveryNote = function( deliveryNote_id)
{
    //window.alert(deliveryNote_id);
    
    app.printDeliveryNote(
        deliveryNote_id, 
        function()
        {
            
        }, 
        function ()
        {
            alert("non riesco a stampare il ddt #"+deliveryNote_id);
        });
};

console.log('END:deliveryNotes.js');
