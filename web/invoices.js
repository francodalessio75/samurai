/* global fetch, Promise */

//app is global variable implemented as singleton
if(typeof app==='undefined' || app===null) app = {};

console.log('BEGIN:invoices.js'); 

//contains all filters values
app.filter = {};

//new invoice object
app.newInvoice = {};

//existing invoice object to be updated
app.invoice = {};


//current customer_id necessary to check if the orderCode inserted by he user is 
//correctly related to the customer
app.customer_id = null;

//collection of all customer orders to be suggested as invoice item
app.suggestedOrders = null;

//collection of all delivery notes row to be suggested as invoice item
app.suggestedDeliveryNotesRows = null;

//invoice rows table 
app.invoiceItemsTBody = document.querySelector("#invoiceItemsTable tbody");

//result message of callValidCode function
app.resultValidCodeMessage= "";

//amounts managment variables
app.taxable = 0;
app.tax = 0;
app.total = 0;


/*transition states managment amounts inputs , dates and checkBoxes*/
//perecednt states
app.firstCheckboxState = {};
app.secondCheckboxState = {};
app.firstCheckboxState.wasChecked = false;
app.secondCheckboxState.wasChecked = false;

//inputs
app.firstCheckBox = document.getElementById('second_amount_checkbox');
app.secondCheckBox = document.getElementById('third_amount_checkbox');
app.firstDue = document.getElementById('first_amount_input');
app.secondDue = document.getElementById('second_amount_input');
app.secondDate = document.getElementById('second_amount_date_input');
app.thirdDue = document.getElementById('third_amount_input');
app.thirdDate = document.getElementById('third_amount_date_input');

/**
 * Depending on 'state' parameter value:
 *  enables / disables dues inputs;
 *  sets checked checkboxes values and enables / disables them.
 *  enables / disables + restes dats inputs values 
 *  the policy is:
 *  one due : only the first date can be changed
 *  two dues : only the first amount and first date and second date can be changed,
 *             when the first amount changes the second chenges autmoatically.            
 *  three dues : first due an date can be changed;
 *               second due and date can be changed;
 *               third date can be changed;
 * @param {type} state
 * @returns {undefined}
 */
app.setDuesInputs = function( state, changeDates )/*changeDates avoids dates resetting on the first invoiceDetails.jsp opening in read modality see invoceDetails.jsp onload=""*/
{
    if( state === 'single')
    {
        app.firstDue.disabled = true;
        app.firstCheckBox.checked = false;
        app.secondCheckBox.disabled = true;
        app.secondCheckBox.checked = false;
        app.secondDate.disabled = true;
        if(changeDates)app.secondDate.value = "";
        app.secondDue.disabled=true;
        if(changeDates)app.thirdDate.value = "";
        app.thirdDate.disabled=true;
    }
    else if ( state === 'double')
    {
        app.firstDue.disabled = false;
        app.firstCheckBox.checked = true;
        app.secondCheckBox.disabled = false;
        app.secondCheckBox.checked = false;
        app.secondDate.disabled = false;
        if(changeDates)app.secondDate.value = "";
        app.secondDue.disabled=true;
        if(changeDates)app.thirdDate.value = "";
        app.thirdDate.disabled=true;
    }
    else if( state === 'triple')
    {
        app.firstDue.disabled = false;
        app.firstCheckBox.checked = true;
        app.secondCheckBox.disabled = false;
        app.secondCheckBox.checked = true;
        app.secondDate.disabled = false;
        //app.secondDate.value = "";
        app.secondDue.disabled=false;
        if(changeDates)app.thirdDate.value = "";
        app.thirdDate.disabled=false;
    }
};


/* the invoice_id value involves wich between creation or update must be done*/
app.invoiceConfirmed = function(  invoice_id )
{
    //if invoice_id is 0 then it is a creation
    if( invoice_id === 0 )
    {
        //creation
        app.createNewInvoice();
    }
    else
    {
        //update
        app.callUpdateInvoice( invoice_id );
    }
};

/**
 * When the user chooses the customer remaining customer fields will be filled automatically
 * @returns {undefined}
 */
app.suggestInvoiceFields = function( orderCodeInput )
{
    //is the case on customer selecting
    if( orderCodeInput === null )
    {
        //first delete / reset all items rows
        //iterates all rows beginning from 1 to preserve the first row
        for( var i = 1; i < app.invoiceItemsTBody.rows.length; i++ )
        {
            //gathes the current row
            var currentRow = app.invoiceItemsTBody.rows[i];
            app.deleteItemRow(currentRow);
        }
        //add a new row down the first row
        app.addItemRowAfter(app.invoiceItemsTBody.rows[0]);
        //delete the first row
        app.deleteItemRow(app.invoiceItemsTBody.rows[0]);


        app.customer_id = document.getElementById('denomination_select_options').value;

        app.readCustomers( app.customer_id, null, 
            function( customers )
            {
                for( var i = 0; i < customers.length; i++ )
                {
                    document.getElementById('address_input').value = customers[i][9]+"";
                    document.getElementById('houseNumber_input').value = customers[i][10];
                    document.getElementById('postalCode_input').value = customers[i][11];
                    document.getElementById('city_input').value = customers[i][8];
                    document.getElementById('province_input').value = customers[i][12];
                    document.getElementById('vatCode_input').value = customers[i][1];
                    document.getElementById('fiscalCode_input').value = customers[i][2];
                    document.getElementById('paymentConditions_input').value = customers[i][15];

                }
                app.invoiceDetailsChanged("denomination_select_options");  
                app.invoiceDetailsChanged("address_input"); 
                app.invoiceDetailsChanged("houseNumber_input"); 
                app.invoiceDetailsChanged("postalCode_input"); 
                app.invoiceDetailsChanged("city_input"); 
                app.invoiceDetailsChanged("province_input"); 
                app.invoiceDetailsChanged("vatCode_input"); 
                app.invoiceDetailsChanged("fiscalCode_input"); 
                app.invoiceDetailsChanged("paymentConditions_input"); 

                app.getSuggestedRows(orderCodeInput);
            }, 
            function()
            {
                window.alert("non riesco a prelevare i dati del Cliente.");
                document.getElementById("denomination_select_options").textContent = "";
                document.getElementById("address_input").textContent ="";
                document.getElementById("houseNumber_input").textContent = "";
                document.getElementById("postalCode_input").textContent = "";
                document.getElementById('city_input').value ="";
                document.getElementById('province_input').value = "";
                document.getElementById('vatCode_input').value = "";
                document.getElementById('fiscalCode_input').value = "";
                document.getElementById('paymentConditions_input').value = "";
            }
        );
    }//end if on customer selecting
    else//when the user acts on a single row inserting an order code
    {
        let orderCode = orderCodeInput.value.trim();
        //if a customer has been choosen
        if( orderCode !== "" && app.customer_id !== "" && app.customer_id !== undefined && app.customer_id !== null )
        {
            app.getValidityMachinaryModelByOrderCode( orderCode,app.customer_id,
            function ( messages ) 
            {
                //cheks th result, only if the code exists and belongs to the customer 
                //writes machinary model in the cell. If not delete the content
                if( messages[0] === "NOT VALID" )
                {
                    window.alert("codice lavoro non valido!");
                    orderCodeInput.value = "";
                }
                else if( messages[0] === "NOT BELONGS" )
                {
                    window.alert("codice lavoro non appartenente al Cliente selezionato!");
                    orderCodeInput.value = "";
                }
                else if( messages[0] === "NOT COMPLETED" )
                {
                    window.alert("codice relativo ad un lavoro non completato!");
                    orderCodeInput.value = "";
                }
                //checks if the order has been invoiced
                else if( messages[0] === "" && messages[1] === " INVOICED" )
                {
                    if (confirm('Il lavoro risulta già fatturato, continuare comunque?')) 
                    {
                        /*
                        let description = '';

                        let deliveryNoteData = '';

                        description += messages[2];//descrizione lavoro

                        if( messages[3] !== '' )
                            description += '; '+messages[3];//ordine

                        if( messages[4] !== '' )
                            description += '; '+messages[4]+';';//commessa

                        if( messages[5] !== '' )
                            deliveryNoteData += deliveryNoteData;//commessa

                        orderCodeInput.parentNode.nextElementSibling.firstElementChild.value = description;

                        orderCodeInput.parentNode.nextElementSibling.firstElementChild.value = description;*/
                        app.getSuggestedRows(orderCodeInput);
                    } 
                    else
                    {

                    }
                }
                else if( messages[0] === "" && messages[1] === "DELIVERED INVOICED" )
                {
                    if (confirm('Il lavoro risulta già consegnato e fatturato, continuare comunque?')) 
                    {
                        /*
                        let description = '';

                        description += messages[2];//descrizione lavoro

                        if( messages[3] !== '' )
                            description += '; '+messages[3];//ordine

                        if( messages[4] !== '' )
                            description += '; '+messages[4]+';';//commessa

                        orderCodeInput.parentNode.nextElementSibling.firstElementChild.value = description;*/
                        app.getSuggestedRows(orderCodeInput);
                    } 
                    else
                    {

                    }
                }
                //if everythink's alright
                else if( messages[0] === "" && messages[1] === "" || messages[0] === "" && messages[1] === "DELIVERED" )
                {
                    /*
                    let description = '';

                    description += messages[2];//descrizione lavoro

                    if( messages[3] !== '' )
                        description += '; '+messages[3];//ordine

                    if( messages[4] !== '' )
                        description += '; '+messages[4]+';';//commessa

                    orderCodeInput.parentNode.nextElementSibling.firstElementChild.value = description;*/
                    app.getSuggestedRows(orderCodeInput);
                }
            },
            function() 
            {
                window.alert("non riesco a leggere la descrizione lavoro");
            });
        }
        else
        {
            window.alert("Prima di inserire un Codice Lavoro seleziona un Cliente");
            orderCodeInput.value = "";
        }
    }
};

/**
 * Called when the user inserts an orderCode
 * @param {type} orderCodeInput
 * @returns {undefined}
 */
app.fill_InvoiceOrderMachinaryModel = ( orderCodeInput ) =>
{
    var orderCode = orderCodeInput.value.trim();
    
    //if a customer has been choosen
    if( orderCode !== "" && app.customer_id !== "" && app.customer_id !== undefined && app.customer_id !== null )
    {
        app.getValidityMachinaryModelByOrderCode( orderCode,app.customer_id,
        function ( messages ) 
        {
            //cheks th result, only if the code exists and belongs to the customer 
            //writes machinary model in the cell. If not delete the content
            if( messages[0] === "NOT VALID" )
            {
                window.alert("codice lavoro non valido!");
                orderCodeInput.value = "";
            }
            else if( messages[0] === "NOT BELONGS" )
            {
                window.alert("codice lavoro non appartenente al Cliente selezionato!");
                orderCodeInput.value = "";
            }
            else if( messages[0] === "NOT COMPLETED" )
            {
                window.alert("codice relativo ad un lavoro non completato!");
                orderCodeInput.value = "";
            }
            //checks if the order has been invoiced
            else if( messages[0] === "" && messages[1] === " INVOICED" )
            {
                if (confirm('Il lavoro risulta già fatturato, continuare comunque?')) 
                {
                    let description = '';
                    
                    description += messages[2];//descrizione lavoro

                    if( messages[3] !== '' )
                        description += '; '+messages[3];//ordine

                    if( messages[4] !== '' )
                        description += '; '+messages[4]+';';//commessa

                    orderCodeInput.parentNode.nextElementSibling.firstElementChild.value = description;
                } 
                else
                {
                    
                }
            }
            else if( messages[0] === "" && messages[1] === "DELIVERED INVOICED" )
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
                } 
                else
                {
                    
                }
            }
            //if everythink's alright
            else if( messages[0] === "" && messages[1] === "" || messages[0] === "" && messages[1] === "DELIVERED" )
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
        function() 
        {
            window.alert("non riesco a leggere la descrizione lavoro");
        });
    }
    else
    {
        window.alert("Prima di inserire un Codice Lavoro seleziona un Cliente");
        orderCodeInput.value = "";
    }
};

/*Makes visible the update button and changes the look of the cell that's been changed*/
app.invoiceDetailsChanged = function(field_id)
{
    //if VAT exemmption checkbox has changed
    if( field_id === 'vat_exempt_checkbox')
    {
        //if has been checked
        if( document.getElementById(field_id).checked )
        {
            //fill notes with customer VAT exemption text
            if( app.customer_id !== null && app.customer_id !== undefined )
            {
                app.readCustomers(
                   app.customer_id,
                    null,
                   (customers)=>
                   {
                       for( var i = 0; i < customers.length; i++)
                       {   //new rules Agenzia Entrate on January the 1st 2022 
                           if(customers[i][13] === "IT"){
                            document.getElementById('notes_input').value = 'Protocollo Esenzione : ' + customers[i][23] + ' del ' +  customers[i][24].substring(6)+"/"+customers[i][24].substring(4,6)+"/"+customers[i][24].substring(0,4);
                           }else{
                               document.getElementById('notes_input').value = customers[i][22];
                           }
                           
                       }
                   },
                   ()=>
                   {
                       window.alert("non riesco a caricare il testo dell'esenzione IVA.");
                   }
                );
            }
            
            //disables the VAT rate select
            document.getElementById('vatRate').disabled = true;
            //sets the VAT rate select value as exempt
            document.getElementById('vatRate').value = 'exempt';
            //refreshes amounts
            app.refreshAmounts();
        }
        //if has beeen unchecked applies VAT and delete notes
        else
        {
            //empties the exemption text
            document.getElementById('notes_input').value = '';
            //enables the VAT rate select
            document.getElementById('vatRate').disabled = false;
            //sets the VAT rate select value as 22%
            document.getElementById('vatRate').value = '22';
            //refreshes amounts
            app.refreshAmounts();
        }
    }
    //if the vat rate select is changed
    if( field_id === 'vatRate')
    {
        //on 'exempt' is the same of checking the exemption checkbox
        if( document.getElementById(field_id).value === 'exempt')
        {
            //set checked the checkbox
            document.getElementById('vat_exempt_checkbox').checked = true;
            //calls the function
            app.invoiceDetailsChanged('vat_exempt_checkbox');
        }
        //on other values computes the tax amount
        else
        {
            //refreshes amounts
            app.refreshAmounts();
        }
    }
    /* INSERT FIELDS CHECKING*/
    document.getElementById(field_id).classList.add('Changed');
};

app.createNewInvoice = function()
{  
    //alert message
    let message = "";
   
    //general invoice data
    //cheks each field if something is wrong write an alert message
    
    //customer choice
    if( document.getElementById('denomination_select_options').value === "")
        message += "Scegli un Destinatario!\n";
    
    //amounts dates
    if ( document.getElementById("first_amount_date_input").value === "" )
        message += "Inserisici una data per la prima scadenza.\n";
    
    if ( document.getElementById("second_amount_input").value !== "0.00" && document.getElementById("second_amount_date_input").value === "" )
        message += "Inserisici una data per la seconda scadenza.\n";
    
    if ( document.getElementById("third_amount_input").value !== "0.00" && document.getElementById("third_amount_date_input").value === "" )
        message += "Inserisici una data per la terza scadenza.\n";
    
    //if there is only one row and it is empty alerts tehe matter
    if (app.invoiceItemsTBody.rows.length === 1  && app.invoiceItemsTBody.rows[0].querySelector(".Code input").value === "" && app.invoiceItemsTBody.rows[0].querySelector(".Description textarea").value === "" && app.invoiceItemsTBody.rows[0].querySelector(".Quantity input").value === "")
        message += " La fattura non contiene articoli! ";
    
    /* Delivery notes rows section*/
    if(message === "")
    {
        //if there is unless one row, begins to chek else alerts that the document has no rows
        if(app.invoiceItemsTBody.rows.length > 0)
        {
                    
            //iterates all rows
            for( var i = 0; i < app.invoiceItemsTBody.rows.length; i++ )
            {
                //gathes the current row
                var currentRow = app.invoiceItemsTBody.rows[i];

                //gathes the current code withut spaces
                //var currentCode = currentRow.querySelector(".Code input").value.trim();

                //if there are more than one row checks for duplication
                // ****  SUSOPENDED BECOUSE IT WOULD BE POSSIBLE TO DELIVER MORRE THAN ONE TIME THE SAME CODE, 
                //THAT IS CAN HAPPENS TO FIND MORE THAN ONE INVOICE ROW RELATED TO THE SAME ORDER CODE *********
                /*
                if(app.invoiceItemsTBody.rows.length > 1)
                {
                    //to avoid to compare the same code, iterates the inserted codes array BEFORE to insert in it 
                    //the current code. 
                    for( var j = 0; j < app.invoiceItemsTBody.rows.length; j++ )
                    {
                        //if the code is not empty and the iteration is comparing a row susequent of the roe of external iteration : j>i
                        if( currentCode !== "" && ( j > i ) && currentCode === app.invoiceItemsTBody.rows[j].querySelector(".Code input").value.trim() )
                            message += " Il codice " + currentCode + "della riga " + (j+1) + " è stato gia inserito nella riga " + (i+1) + "\n";
                    }
                }*/

                //if the rows is empty removes it from the table
                if( currentRow.querySelector(".Code input").value === "" && currentRow.querySelector(".Description textarea").value === "" &&  currentRow.querySelector(".Quantity input").value === "" )
                    app.deleteItemRow(currentRow);
                //if there's no quantity alerts the matter
                if( !(currentRow.querySelector(".Description textarea").value === "") &&  currentRow.querySelector(".Quantity input").value === "" )
                    message += " Nessuna quantità nella riga " + (i+1) +"\n";
                //if there's no description alerts the matter
                if( currentRow.querySelector(".Description textarea").value === "" &&  !(currentRow.querySelector(".Quantity input").value === "") )
                    message += " Nessuna descrizione nella riga " + (i+1)+"\n";
            }

            //if message is empty and there is unless one row creates the new delivery note else alerts the message
            if( message === "" && app.invoiceItemsTBody.rows.length > 0 )
            {
                app.newInvoice.customer_id = app.customer_id;
                app.newInvoice.date = document.getElementById("date_input").value;
                app.newInvoice.firstAmount = document.getElementById("first_amount_input").value;
                app.newInvoice.firstAmountDate = document.getElementById("first_amount_date_input").value;
                app.newInvoice.secondAmount = document.getElementById("second_amount_input").value;
                app.newInvoice.secondAmountDate = document.getElementById("second_amount_date_input").value;
                app.newInvoice.thirdAmount = document.getElementById("third_amount_input").value;
                app.newInvoice.thirdAmountDate = document.getElementById("third_amount_date_input").value;
                app.newInvoice.taxableAmount = document.getElementById("taxable_amount_input").value;
                app.newInvoice.taxAmount = document.getElementById("tax_amount_input").value;
                app.newInvoice.vatRate = document.getElementById('vatRate').value === 'exempt' ? '0' : document.getElementById('vatRate').value;  
                app.newInvoice.totalAmount = document.getElementById("total_amount_input").value;
                app.newInvoice.paymentConditions = document.getElementById("paymentConditions_input").value;
                app.newInvoice.notes = document.getElementById("notes_input").value;
                app.newInvoice.exempt = document.getElementById("vat_exempt_checkbox").checked;
                //collects items ( rows ) in a Json object
                app.newInvoice.items = app.invoiceItemsToJson();

                document.querySelector(".Footer_message").innerHTML = " Sto creando la fattura... ";
                
                let now = new Date();
                
                let execute =
                    app.newInvoice.date !== now.toISOString().substring(0, 10)
                    || (app.newInvoice.date === now.toISOString().substring(0, 10) && confirm("Stai salvando con data odierna, premi ok per continuare altrimenti annulla"));
                
                if( execute )
                {
                    
                    app.createInvoice( app.newInvoice,
                        (invoice)=>
                        {
                            app.printInvoice(invoice.invoice_id,
                            ()=>
                            {
                                app.getXML(invoice.invoice_id,
                                ()=>
                                {
                                     window.open('invoice_details.jsp?invoice_id='+invoice.invoice_id,'_self');
                                },
                                (message)=>
                                {
                                    window.alert(message);
                                    document.querySelector(".Footer_message").innerHTML = "non riesco a produrre il file XML! ";
                                });
                            },
                            ()=>
                            {
                                document.querySelector(".Footer_message").innerHTML = "non riesco a produrre il pdf della fattura. ";
                            });    
                        },
                        ()=>
                        {
                           window.alert("non riesco a creare la fattura"); 
                        }
                    );
                }
                else
                {}   
            }
            else
                window.alert( message );
        }
        else
            window.alert( "Il documento non ha articoli!" );
    }
    else
        window.alert( message );
};

/**/
app.callUpdateInvoice = function( invoice_id )
{  
    document.querySelector(".Footer_message").innerHTML = "Sto modificando la fattura... ";
    app.invoice.invoice_id = invoice_id;
    //alert message
    let message = "";
   
    //general invoice data
    //cheks each field if something is wrong write an alert message
    if( document.getElementById('denomination_select_options').value === "")
        message += "Scegli un Destinatario!\n";
           
    /* invoice rows section*/
    if(message === "")
    {
        //if there is unless one row, begins to chek else alerts that the document has no rows
        if(app.invoiceItemsTBody.rows.length > 0)
        {
                    
            //iterates all rows
            for( var i = 0; i < app.invoiceItemsTBody.rows.length; i++ )
            {
                //gathes the current row
                var currentRow = app.invoiceItemsTBody.rows[i];

                //gathes the current code withut spaces
                //var currentCode = currentRow.querySelector(".Code input").value.trim();

                //if there are more than one row checks for duplication
                // ****  SUSOPENDED BECOUSE IT WOULD BE POSSIBLE TO DELIVER MORRE THAN ONE TIME THE SAME CODE, 
                //THAT IS CAN HAPPENS TO FIND MORE THAN ONE INVOICE ROW RELATED TO THE SAME ORDER CODE *********
                /*
                if(app.invoiceItemsTBody.rows.length > 1)
                {
                    //to avoid to compare the same code, iterates the inserted codes array BEFORE to insert in it 
                    //the current code. 
                    for( var j = 0; j < app.invoiceItemsTBody.rows.length; j++ )
                    {
                        //if the code is not empty and the iteration is comparing a row susequent of the roe of external iteration : j>i
                        if( currentCode !== "" && ( j > i ) && currentCode === app.invoiceItemsTBody.rows[j].querySelector(".Code input").value.trim() )
                            message += " Il codice " + currentCode + "della riga " + (j+1) + " è stato gia inserito nella riga " + (i+1) + "\n";
                    }
                }*/

                //if the rows is empty removes it from the table
                if( currentRow.querySelector(".Code input").value === "" && currentRow.querySelector(".Description textarea").value === "" &&  currentRow.querySelector(".Quantity input").value === "" )
                    app.deleteItemRow(currentRow);
                //if there's no quantity alerts the matter
                if( !(currentRow.querySelector(".Description textarea").value === "") &&  currentRow.querySelector(".Quantity input").value === "" )
                    message += " Nessuna quantità nella riga " + (i+1) +"\n";
                //if there's no description alerts the matter
                if( currentRow.querySelector(".Description textarea").value === "" &&  !(currentRow.querySelector(".Quantity input").value === "") )
                    message += " Nessuna descrizione nella riga " + (i+1)+"\n";
            }

            //if message is empty and there is unless one row creates the new delivery note else alerts the message
            if( message === "" && app.invoiceItemsTBody.rows.length > 0 )
            {
                app.invoice.customer_id = app.customer_id = document.getElementById('denomination_select_options').value;;
                app.invoice.date = document.getElementById("date_input").value;
                app.invoice.firstAmount = document.getElementById("first_amount_input").value;
                app.invoice.firstAmountDate = document.getElementById("first_amount_date_input").value;
                app.invoice.secondAmount = document.getElementById("second_amount_input").value;
                app.invoice.secondAmountDate = document.getElementById("second_amount_date_input").value;
                app.invoice.thirdAmount = document.getElementById("third_amount_input").value;
                app.invoice.thirdAmountDate = document.getElementById("third_amount_date_input").value;
                app.invoice.taxableAmount = document.getElementById("taxable_amount_input").value;
                app.invoice.taxAmount = document.getElementById("tax_amount_input").value;
                app.invoice.vatRate = document.getElementById('vatRate').value === 'exempt' ? '0' : document.getElementById('vatRate').value;  
                app.invoice.totalAmount = document.getElementById("total_amount_input").value;
                app.invoice.paymentConditions = document.getElementById("paymentConditions_input").value;
                app.invoice.notes = document.getElementById("notes_input").value;
                app.invoice.exempt = document.getElementById("vat_exempt_checkbox").checked;

                //collects items in a Json object
                app.invoice.items = app.invoiceItemsToJson();
                
                document.querySelector(".Footer_message").innerHTML = " Sto modificando la fattura... ";
                
                var now = new Date();
                
                let execute =
                    app.invoice.date !== now.toISOString().substring(0, 10)
                    || (app.invoice.date === now.toISOString().substring(0, 10) && confirm("Stai salvando con data odierna, premi ok per continuare altrimenti annulla"));
            
                if( execute )
                {
                    app.updateInvoice( app.invoice,
                    ()=>
                    {
                        app.printInvoice(app.invoice.invoice_id,
                            ()=>
                            {
                                app.getXML(app.invoice.invoice_id,
                                ()=>
                                {
                                    window.open('invoice_details.jsp?invoice_id='+app.invoice.invoice_id,'_self');
                                    document.querySelector(".Footer_message").innerHTML = "";
                                },
                                (message)=>
                                {
                                    window.alert(message);
                                    document.querySelector(".Footer_message").innerHTML = "non riesco a produrre il file XML! ";
                                });
                            },
                            ()=>
                            {
                                document.querySelector(".Footer_message").innerHTML = "non riesco a stampare la fattura. ";
                            });                       
                    },
                    ()=>
                    {
                       window.alert("non riesco a modificare la fattura"); 
                    });
                }
            }
            else
                window.alert( message );
        }
        else
            window.alert( "Il documento non ha articoli!" );
    }
    else
        window.alert( message );
};

/*** Invoices page ************************/

app.openInvoicesPage = function()
{
    //assigns to app.filter current values
    app.getFiltersValues();
    //opens tasks page       
    window.open("invoices.jsp?&filter="+encodeURIComponent(JSON.stringify(app.filter)),'_self');
};

/*retrieves filters values from the page*/
app.getFiltersValues = function()
{
    app.filter.customer_id = document.getElementById("customer_select_options").value;
    
    app.filter.from_date = document.getElementById("from_date").value; 
    
    app.filter.to_date = document.getElementById("to_date").value;
    
    app.filter.number = document.getElementById("numberFilter").value;
    
};

/*retrieves filters values from the page*/
app.getAggregatedFiltersValues = function()
{
    app.filter.customer_id = document.getElementById("customer_select_options").value;
    
    app.filter.from_date = document.getElementById("from_date").value; 
    
    app.filter.to_date = document.getElementById("to_date").value;
    
    app.filter.order_code = document.getElementById("orderCode").value;
    
};

/* updtates the tasks variable in acording to current filters values in the page*/
app.filterInvoices = function()
{
    document.querySelector(".Footer_message").innerHTML = "Sto filtrando le fatture...";
    
    //assigns to app.filter current values
    app.getFiltersValues();
    
    app.readInvoices(// deliveryNote_id, customer_id, transporter_id, number, fromDate, toDate, successCallback, failCallback
        null,//invoice_id_id
        app.filter.customer_id,//customer_id
        app.filter.number,//number
        app.filter.from_date,//fromDate
        app.filter.to_date,//toDate
        function( invoices )//successCallBack
        {
            app.fillInvoicesTable(invoices);
            document.querySelector(".Footer_message").innerHTML = "FATTURE FILTRATE: " + invoices.length;
        },
        function()//failCallBack
        {
            document.querySelector(".Footer_message").innerHTML = "non riesco a filtrare le fatture! Contattare Assistenza. ";
        }
    );
};

/* updtates the tasks variable in acording to current filters values in the page*/
app.filterAggregatedInvoicesRows = function()
{
    document.querySelector(".Footer_message").innerHTML = "Sto filtrando le fatture...";
    
    //assigns to app.filter current values
    app.getAggregatedFiltersValues();
    
    app.readAggregatedInvoices(// deliveryNote_id, customer_id, transporter_id, number, fromDate, toDate, successCallback, failCallback
        null,//invoice_id_id
        app.filter.customer_id,//customer_id
        app.filter.order_code,
        app.filter.from_date,//fromDate
        app.filter.to_date,//toDate
        function( invoicesRows )//successCallBack
        {
            app.fillAggregatedInvoicesTable(invoicesRows);
            document.querySelector(".Footer_message").innerHTML = "RIGHE FATTURE FILTRATE: " + invoicesRows.length;
        },
        function()//failCallBack
        {
            document.querySelector(".Footer_message").innerHTML = "non riesco a filtrare le fatture! Contattare Assistenza. ";
        }
    );
};

/* refersh table rows*/
app.fillInvoicesTable = function( invoices )
{
    var templateItem = document.getElementById("invoiceTableRow");
    var itemsContainer = document.querySelector(".Table tbody");

    //empty the current content in the table
    itemsContainer.innerHTML = null;
    
    let progressiveAmount = 0.0;
     
    for(var i=0; i<invoices.length; i++)
    {
        var templateContent =  document.importNode(templateItem.content,true);

        templateContent.querySelector(".InvoiceTableRow").id = "row_"+invoices[i].invoice_id;
        
        templateContent.querySelector(".InvoiceTableRow").onclick = app.setCurrentInvoiceId(invoices[i].invoice_id);

        templateContent.querySelector(".InvoiceNumber").innerHTML = invoices[i].number+"-"+invoices[i].year;
        
        //formats the date
        var date = invoices[i].date;
        var year = date.substring(0,4);
        var month = date.substring(4,6);
        var day = date.substring(6,8);
        date = day+"/"+month+"/"+year;
        templateContent.querySelector(".InvoiceDate").innerHTML = date;
        
        templateContent.querySelector(".Customer").innerHTML = invoices[i].denomination;
        
        templateContent.querySelector(".Taxable").innerHTML = invoices[i].taxableAmount.toFixed(2);
        
        templateContent.querySelector(".Total").innerHTML = invoices[i].totalAmount.toFixed(2);
        
        progressiveAmount += parseFloat(invoices[i].taxableAmount);
        
        templateContent.querySelector(".Progressive").innerHTML = progressiveAmount.toFixed(2);
        
        let number = invoices[i].number;
        let shortYear = invoices[i].year;
        
        templateContent.querySelector(".Pdf").onclick = (event)=>
        {
            event.stopPropagation();
            window.open("resources/INVOICES/FATTURA_DUESSE_"+number+"_"+shortYear+".pdf","_blank");
        };
        
        itemsContainer.appendChild(templateContent); 
    }   
};

/* refersh table rows*/
app.fillAggregatedInvoicesTable = function( invoicesRows )
{
    var templateItem = document.getElementById("AggregatedInvoiceTableRow");
    var itemsContainer = document.querySelector(".Table tbody");

    //empty the current content in the table
    itemsContainer.innerHTML = null;
    
    let progressiveAmount = 0.0;
     
    for(var i=0; i<invoicesRows.length; i++)
    {
        var templateContent =  document.importNode(templateItem.content,true);

        templateContent.querySelector(".AggregatedInvoiceTableRow").id = "row_"+invoicesRows[i].invoice_id;

        templateContent.querySelector(".InvoiceNumber").innerHTML = invoices[i].number+"-"+invoices[i].year;
        
        //formats the date
        var date = invoices[i].date;
        var year = date.substring(0,4);
        var month = date.substring(4,6);
        var day = date.substring(6,8);
        date = day+"/"+month+"/"+year;
        templateContent.querySelector(".InvoiceDate").innerHTML = date;
        
        templateContent.querySelector(".Customer").innerHTML = invoices[i].denomination;
        
        templateContent.querySelector(".Taxable").innerHTML = invoices[i].taxableAmount.toFixed(2);
        
        templateContent.querySelector(".Total").innerHTML = invoices[i].totalAmount.toFixed(2);
        
        progressiveAmount += parseFloat(invoices[i].taxableAmount);
        
        templateContent.querySelector(".Progressive").innerHTML = progressiveAmount.toFixed(2);
        
        let number = invoices[i].number;
        let shortYear = invoices[i].year;
        
        templateContent.querySelector(".Pdf").onclick = (event)=>
        {
            event.stopPropagation();
            window.open("resources/INVOICES/FATTURA_DUESSE_"+number+"_"+shortYear+".pdf","_blank");
        };
        
        itemsContainer.appendChild(templateContent); 
    }   
};

/**
 * 
 * @param {type} invoice_id
 * @returns {Function}
 */
app.setCurrentInvoiceId = function( invoice_id )
{
    return function()
    {
        window.open("invoice_details.jsp?invoice_id=" + invoice_id,'_self');
    };
};

//fills rows table in invoce_details.jsp
app.getInvoiceRows = function( invoice_id, changeDates )
{
    //sets current customer_id
    app.customer_id = document.getElementById('denomination_select_options').value;
    
    app.readInvoiceRows( invoice_id,
        function(rows)
        {
            //gets the table
            var table = document.querySelector("#invoiceItemsTable tbody");

            //adds empty rows ( all  less one because the table already has an empty row)
            for( var i = 0; i < rows.length-1; i++ )
            {
                //gets a new table row 
                var tableRow = app.newItemRow();
                table.appendChild(tableRow);
            }

            //fills contents
            for( var i = 0; i < table.rows.length; i++)
            {
                for( var j = 0; j < table.rows[i].cells.length; j++ )
                {
                    //inserts code and disables the cell
                    table.rows[i].cells[0].firstChild.value = rows[i][0];
                    table.rows[i].cells[0].firstChild.setAttribute("disabled","true");

                    //inserts the description
                    table.rows[i].cells[1].firstChild.value = rows[i][1];

                    //inserts delivery note reference
                    table.rows[i].cells[2].firstChild.value = rows[i][2]; 
                    table.rows[i].cells[2].firstChild.setAttribute("disabled","true");
                    

                    //inserts the quantity if there'e a deliverynote row related else sets 1
                    table.rows[i].cells[3].firstChild.value = rows[i][3]; 
                    
                    //inserts the single amont
                    table.rows[i].cells[4].firstChild.value = rows[i][4].toFixed(3);
                    
                    //inserts the total amount
                    table.rows[i].cells[5].firstChild.value = rows[i][5];
                    
                    //inserts in dataset deliveryNoteRow_id necessary to create deliveryNoteRow Json Object in elasticTable.js
                    table.rows[i].dataset.deliveryNoteRow_id = rows[i][6]; 
                }
            }
            //refreshes variables
            app.firstDue = document.getElementById('first_amount_input');
            app.secondDue = document.getElementById('second_amount_input');
            app.thirdDue = document.getElementById('third_amount_input');
            
            //updates taxable, tax, and total
            app.taxable = parseFloat(document.querySelector("#taxable_amount_input").value); 
            app.tax = parseFloat(document.querySelector("#tax_amount_input").value);  
            app.total = parseFloat(document.querySelector("#total_amount_input").value);  
            
            //sets correct states of dues inputs in according to how many dues does the invoice have
            //one due : simulates transition from two dues to one
            if( app.secondDue.value === '0.00' )
            {
                app.firstCheckboxState.wasChecked = true;
                app.secondCheckboxState.wasChecked = false;
                app.checkBoxChanged('second_amount_checkbox', changeDates);
            }
            //two dues: simulates transition from three dues to two dues
            if( app.secondDue.value !== '0.00' && app.thirdDue.value === '0.00')
            {
                app.firstCheckboxState.wasChecked = true;
                app.secondCheckboxState.wasChecked = true;
                app.checkBoxChanged('third_amount_checkbox', changeDates);
            }
            //three dues: simulates transition from two dues to three dues
            if( app.secondDue.value !== '0.00' && app.thirdDue.value !== '0.00')
            {
                app.firstCheckboxState.wasChecked = true;
                app.secondCheckboxState.wasChecked = false;
                app.checkBoxChanged('third_amount_checkbox', changeDates);
            }

        },
        function()
        {
            window.alert("non riesco a caricare le righe della fattura.");
        }
    );
    
};


/**
 * retireves and suggest related to the chosen customer:
 *  orders completed and not delivered;
 *  deliveryNoteRows not invoiced;
 * @returns {undefined}
 */
app.getSuggestedRows = function( orderCodeInput )
{
    //is the case on customer selecting
    if(orderCodeInput === null)
    {
        //if there chosen customer is empty just reloads the page
        if( app.customer_id === "")
            window.open('invoice_details.jsp','_self');
        else
        {
            //gets the table
            var table = document.querySelector("#invoiceItemsTable tbody");

            //retrieves from the database all delivery notes rows not invoiced and 
            //related to the customer
            app.getSuggestedDeliveryNotesRows(app.customer_id,null,
            (deliveryNotesRows) =>
            {
                app.deliveryNotesRows = deliveryNotesRows;
                //adds so many riws as hoe many deliveryNoteRows suggested there are
                // lesss one because tha table already has got one ampty row
                for( var i = 0; i < deliveryNotesRows.length-1; i++ )
                {
                    //gets a new table row 
                    var tableRow = app.newItemRow();
                    table.appendChild(tableRow);
                }
                //if there are deliveryNoteRows to be suggested fills contents
                if( deliveryNotesRows.length > 0 )
                {
                    //fills rows except the last one that is just an available empty row
                    for( var i = 0; i < table.rows.length; i++)
                    {
                        //inserts code and disables the cell
                        table.rows[i].cells[0].firstChild.value = deliveryNotesRows[i][6];
                        table.rows[i].cells[0].firstChild.setAttribute("disabled","true");


                        //if the row refers to an order code fetchs ordine and commessa: look if this informations can be already contained in delivery note row description
                        //in this case they will be duplicated and the user must delete them manually. The problem is that if we want in any case that ordine and commessa 
                        //will be inseted automatically in the description alse when thay have been inserted in order details after that the delivery note row has been
                        //produced we must accept the possibility that the application will insert two times this informations
                        //inserts the description
                        let description = '';
                        description = deliveryNotesRows[i][7]+";  ";
                        description += deliveryNotesRows[i][12] === '' ? '' : deliveryNotesRows[i][12] + "; ";
                        description += deliveryNotesRows[i][13] === '' ? '' : deliveryNotesRows[i][13]+";";
                        table.rows[i].cells[1].firstChild.value = description;

                        //inserts delivery note reference wich cell is always disabled
                        table.rows[i].cells[2].firstChild.value =  " ddt N° " + deliveryNotesRows[i][3] + " del " + deliveryNotesRows[i][4]; 

                        //inserts quantity
                        table.rows[i].cells[3].firstChild.value = deliveryNotesRows[i][11];

                        //inserts in dataset deliveryNoteRow_id necessary to create deliveryNoteRow Json Object in elasticTable.js
                        table.rows[i].dataset.deliveryNoteRow_id = deliveryNotesRows[i][0];
                    }
                }

                //THIS FEATURE HAS BEEN ELIINATED BECOUSE THERE ARE TO MANY ORDERS TO BE SUGGESTED
                /*checks if there are orders completed and not delivered to be invoiced
                app.getSuggestedOrders(app.customer_id, 
                (orders) =>
                {
                    app.suggestedOrders = orders;
                    //adds empty rows 
                    for( var i = 0; i < orders.length; i++ )
                    {
                        //gets a new table row 
                         var tableRow = app.newItemRow();
                        table.appendChild(tableRow);
                    }
                    //if there are orders to be suggested fills contents
                    if( orders.length > 0 )
                    {
                        //fills rows starting from the first empty row that is just after the last related deliveryNote row creates before.
                        // The table has an ampty row at the beginning. Since has been added a table row for each related order, from the iteration upper limit 
                        //has been subtracted 1
                        for( var i = app.deliveryNotesRows.length; i < table.rows.length; i++)
                        {
                            //fills code and disables the cell and description
                            table.rows[i].cells[0].firstChild.value = orders[i-app.deliveryNotesRows.length][2];
                            table.rows[i].cells[0].firstChild.setAttribute("disabled","true");  
                            //inserts description
                            table.rows[i].cells[1].firstChild.value = orders[i-app.deliveryNotesRows.length][12];

                        }

                },
                () =>
                {
                    window.alert("non riesco a caricare i lavori completati da fatturare.");
                });*/
            },
            () =>
            {
                window.alert("non riesco a caricare i lavori consegnati da fatturare.");
            });    
             //If VAT exemption check box is checked fills note with VAT exemption text
            if( document.getElementById('vat_exempt_checkbox').checked)
            {
                if( app.customer_id !== null && app.customer_id !== undefined )
                {
                    app.readCustomers(
                       app.customer_id,
                        null,
                       (customers)=>
                       {
                           for( var i = 0; i < customers.length; i++)
                           {
                               document.getElementById('notes_input').value = customers[i][22];
                           }
                       },
                       ()=>
                       {
                           window.alert("non riesco a caricare il testo dell'esenzione IVA.");
                       }
                    );
                }
            }
        }
    }
    else//is the case on orderCode inserting on a single row
    {
        //gets the orderCode
        let code = orderCodeInput.value.trim();
        
        //retrieves from the database all delivery notes rows not invoiced and 
        //related to the customer
        app.getSuggestedDeliveryNotesRows(app.customer_id,code,
        (deliveryNotesRows) =>
        {
            //app.deliveryNotesRows = deliveryNotesRows; PROBABLY NOT CORRECT BECOUSE IT IS ONLY ONE ROW
            if( deliveryNotesRows.length > 0 )
            {
                //inserts code and disables the cell
                orderCodeInput.value = deliveryNotesRows[0][6];
                orderCodeInput.setAttribute("disabled","true");

                //if the row refers to an order code fetchs ordine and commessa: look if this informations can be already contained in delivery note row description
                //in this case they will be duplicated and the user must delete them manually. The problem is that if we want in any case that ordine and commessa 
                //will be inseted automatically in the description alse when thay have been inserted in order details after that the delivery note row has been
                //produced we must accept the possibility that the application will insert two times this informations
                //inserts the description
                let description = '';
                description = deliveryNotesRows[0][7]+";  ";
                description += deliveryNotesRows[0][12] === '' ? '' : deliveryNotesRows[0][12] + "; ";
                description += deliveryNotesRows[0][13] === '' ? '' : deliveryNotesRows[0][13]+";";
                orderCodeInput.parentNode.parentNode.cells[1].firstChild.value = description;

                //inserts delivery note reference wich cell is always disabled
                orderCodeInput.parentNode.parentNode.cells[2].firstChild.value =  " ddt N° " + deliveryNotesRows[0][3] + " del " + deliveryNotesRows[0][4]; 

                //inserts quantity
                orderCodeInput.parentNode.parentNode.cells[3].firstChild.value = deliveryNotesRows[0][11];

                //inserts in dataset deliveryNoteRow_id necessary to create deliveryNoteRow Json Object in elasticTable.js
                orderCodeInput.parentNode.parentNode.dataset.deliveryNoteRow_id = deliveryNotesRows[0][0];

                // is the useful snippet orderCodeInput.parentNode.nextElementSibling.firstElementChild.value = description;
            }
            else 
            {
                app.fill_InvoiceOrderMachinaryModel(orderCodeInput);
            }
        },
        () =>
        {
            window.alert("non riesco a caricare i dati del lavoro del codice inserito.");
        });
    }
};

/*
 * Called each time a row amount has been changed / inserted
 * first adjustes taxable tax and total then calls refreshDues function
 */
app.refreshAmounts = function()
{
    //updates application variable
    app.invoiceItemsTBody = document.querySelector("#invoiceItemsTable tbody");
    
    //resets totals
    app.total = 0;
    app.tax = 0;
    app. taxable = 0;
    
    //resets inputs fields
    document.querySelector("#taxable_amount_input").value = "";
    document.querySelector("#tax_amount_input").value = "";
    document.querySelector("#total_amount_input").value = "";
    
   //iterates all amounts in the table and adds them to obtain the total
    for( var i = 0; i < app.invoiceItemsTBody.rows.length; i++ )
    {
        //puts zero when there's nothing inside the cell to do correct number conversion 
        if(app.invoiceItemsTBody.rows[i].querySelector(".SingleAmount input").value === "" )
            app.invoiceItemsTBody.rows[i].querySelector(".SingleAmount input").value = "0.000";
        if(app.invoiceItemsTBody.rows[i].querySelector(".TotalAmount input").value === "" )
            app.invoiceItemsTBody.rows[i].querySelector(".TotalAmount input").value = "0.00";
        //truncates to two decimals 
        var singleAmount = Math.round( parseFloat(app.invoiceItemsTBody.rows[i].querySelector(".SingleAmount input").value)*1000)/1000;
        var totalAmount = singleAmount * (parseFloat(app.invoiceItemsTBody.rows[i].querySelector(".Quantity input").value)) ;
        //displays in two decimals
        app.invoiceItemsTBody.rows[i].querySelector(".SingleAmount input").value = singleAmount.toFixed(3);
        app.invoiceItemsTBody.rows[i].querySelector(".TotalAmount input").value = totalAmount.toFixed(2);
        //adds amount
        app.taxable += totalAmount;

    }
    
  //updates taxable, tax, and total
  document.querySelector("#taxable_amount_input").value = (Math.round(app.taxable*100)/100).toFixed(2);
  
  //if exempt tax are zero
  let vatRate = document.getElementById('vatRate').value !== 'exempt' ? Number(document.getElementById('vatRate').value) : 0 ;
  
  app.tax = document.getElementById('vat_exempt_checkbox').checked ? 0 : Math.round(((app.taxable/100)*vatRate)*100)/100;
  
  document.querySelector("#tax_amount_input").value = (Math.round(app.tax*100)/100).toFixed(2);
  app.total = parseFloat(app.taxable) + parseFloat(app.tax) ;
  document.querySelector("#total_amount_input").value =  app.total.toFixed(2);
  
  
  /*refreshes dues*/
  app.refreshDues(null, true);
};

/**
 * called when input dues values must be adjusted, so when a row amount 
 * has been changed / inserted or when the user change a due amount.
 * Depending on wich amount has been changed and on checkboxes state ( precedent
 * and current) adjustes dues
 * @param {type} dueInput_id when is null rows have been modified if not
 *               dues have been modified
 * @returns {undefined}
 */
app.refreshDues = function( dueInput_id, toBeRefreshed)/* to be refreshed together with changeData is a boolean that avoids that amounts are changed when 
                                                        * an invoice is opened for the first time*/
{
    if( toBeRefreshed )
    {
        //rounded total invoice amount
        var total = Math.round( app.total*100)/100;
        //rounded first due value
        var firstValue = Math.round( app.firstDue.value*100)/100;
        //rounded second due value
        var secondValue = Math.round( app.secondDue.value*100)/100;
        //total invoice amount / 2
        var halfDifference = Math.round( ( ( total)/2 )*100 ) / 100 ;
        //total invoice amount / 3
        var thirdDifference = Math.round( ( ( total)/3 )*100 ) / 100 ;

        //if dueInput is null user has modified rows but not dues
        //just updates active dues inputs
        if( dueInput_id === null )
        {
            //refreshes the total
            //if there's only one active amount input updates only it
            if( !app.firstCheckBox.checked && !app.secondCheckBox.checked )
            {
                app.firstDue.value = (total).toFixed(2);
                app.secondDue.value = (0).toFixed(2);
                //resets the third input
                app.thirdDue.value = (0).toFixed(2);
            }

            //if there are two  active amounts distributes the total on both
            if( app.firstCheckBox.checked && !app.secondCheckBox.checked )
            {
                app.firstDue.value = (halfDifference).toFixed(2);
                app.secondDue.value = (total - halfDifference).toFixed(2);
                //resets the third input
                app.thirdDue.value = (0).toFixed(2);
            }

            //if there are three  active amounts distributes the total between them
            if( app.firstCheckBox.checked && app.secondCheckBox.checked )
            {
                app.firstDue.value = (thirdDifference).toFixed(2);
                app.secondDue.value = (thirdDifference).toFixed(2);
                app.thirdDue.value = (total - thirdDifference -thirdDifference ).toFixed(2); 
            }
        }
        //if dueInput_id is the first one
        if( dueInput_id === "first_amount_input")
        {
            //if the amount inserted in the first amount input is greater then the total the application just 
            //make it equals to the total
            if( firstValue > total )
            {
                app.firstDue.value = (total).toFixed(2);
                firstValue = total;
            }

            //computes the difference
            var difference = total - firstValue;
            var halfDifference = Math.round( ( difference / 2 )*100 ) / 100 ;

            //if there are three  active amounts distributes the difference between the second and the third
            if( app.firstCheckBox.checked && app.secondCheckBox.checked )
            {
                app.firstDue.value = (firstValue).toFixed(2);

                app.secondDue.value = (halfDifference).toFixed(2);

                app.thirdDue.value = (total - firstValue - halfDifference).toFixed(2);
            }

            //if there are two  active amounts puts the difference on the second one
            if( app.firstCheckBox.checked && !app.secondCheckBox.checked )
            {
                //rounds the first value
                app.firstDue.value = (firstValue).toFixed(2);

                app.secondDue.value = (difference).toFixed(2);

                app.thirdDue.value = (0).toFixed(2);

            }

            //note one due is not contemplated becouse when due is one the amount nput is disabled
        }

        //if dueInput_id is the second one forced there are three dues and the first and second amount inputs active
        if( dueInput_id === "second_amount_input")
        {
            //if the amount inserted in the first amount input is greater then the total the application just 
            //make it equals to the total
            if( secondValue > ( total - firstValue ) )
            {
                app.secondDue.value = (total - firstValue).toFixed(2);
                secondValue = total - firstValue;
            }

            //rounds the first value
            app.firstDue.value = (firstValue).toFixed(2);

            app.secondDue.value = (secondValue).toFixed(2);

            app.thirdDue.value = (total - firstValue - secondValue).toFixed(2);

        }
    }
};

/**
 * called when an enabling dues checkbox state has been changed by the user.
 * Depending on:
 *  wich checkbox state has changed and wich was the precedent checkboxes states
 *  adjusts new precedent check boxes states and call setDuesInputs function
 *  giving as parmater 'sungle', 'duouble' or 'triple' consitently with precedent 
 *  checkboxes state and current checkboxes state implied by user action
 *  
 * @param {type} checkbox_id : wich checkbox is the source of the user action
 * @returns {undefined}
 */
app.checkBoxChanged = function( checkbox_id, changeData )/*if change data is false dates and amounts won't be resettted look at invoiceDetails.jsp onload value*/
{
    //event from first checkbox
    if( checkbox_id === 'second_amount_checkbox')
    {
        //if was checked we move from double dues to single or from triple to single
        if( app.firstCheckboxState.wasChecked )
        {
            app.firstCheckboxState.wasChecked = false;
            app.setDuesInputs( 'single', changeData );
            //also second checkbox state must be resetted in according to single amount state
            app.secondCheckboxState.wasChecked = false;
            //calls refreshDues to update amounts
            app.refreshDues(null,changeData);
        }
        //if was unchecked we move from single due to double due
        else
        {
            app.firstCheckboxState.wasChecked = true;
            app.setDuesInputs( 'double', changeData );
            //calls refreshDues to update amounts
            app.refreshDues(null, changeData);
        }
    }
    //event from second checkbox
    if( checkbox_id === 'third_amount_checkbox')
    {
        //if was checked we move from triple due to double 
        if( app.secondCheckboxState.wasChecked )
        {
            app.secondCheckboxState.wasChecked = false;
            app.setDuesInputs( 'double', changeData );
            //calls refreshDues to update amounts
            app.refreshDues(null, changeData);
        }
        //if was unchecked we move from double due to triple 
        else
        {
            app.secondCheckboxState.wasChecked = true;
            app.setDuesInputs( 'triple', changeData );
            //calls refreshDues to update amounts
            app.refreshDues(null, changeData);
        }
            
    }
};

app.checkCustomerChoosen = (input)=>
{
    
    if( app.customer_id === '' || app.customer_id === null || app.customer_id === undefined )
    {
        input.value = '';
        window.alert("Seleziona un Cliente!");
    }
};

app.changeInvoiceDate = ()=>
{
    let date = document.getElementById("date_input").value;
    let completeNumber = document.getElementById("number_input").value;
    let number = completeNumber.substring(0,completeNumber.indexOf("-")).trim();
    let year = completeNumber.substring(completeNumber.indexOf("-")).replace("-","").trim();
    
    if( date !== "" && number !== "" && year !== "" )
    {
        app.updateInvoiceDate(// deliveryNote_id, customer_id, transporter_id, number, fromDate, toDate, successCallback, failCallback
        date,//invoice_id_id
        number,//customer_id
        year,//number
        function()//successCallBack
        {
            window.alert("Data fattura modificata! E' necessario ora allineare i dati eseguendo una modifica dalla vista di dettaglio fattura.");
            document.querySelector(".Footer_message").innerHTML = "Data fattura modificata!";
        },
        function()//failCallBack
        {
            window.alert("Operazione fallita!");
            document.querySelector(".Footer_message").innerHTML = "Non riesco a modificare la data della fattura!";
        }
    );
    }
    else
        window.alert("Formato dati non valido!");
};    
console.log('END:invoices.js');
