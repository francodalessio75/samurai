/* global fetch, Promise */

//app is global variable implemented as singleton
if(typeof app==='undefined' || app===null) app = {};

console.log('BEGIN:creditNotes.js'); 

//contains all filters values
app.filter = {};

//new creditNote object
app.newCreditNote = {};

//existing creditNote object to be updated
app.creditNote = {};


//creditNote rows table 
app.creditNoteItemsTBody = document.querySelector("#creditNoteItemsTable tbody");

//result message of callValidCode function
//app.resultValidCodeMessage= "";

//amounts managment variables
app.taxable = 0;
app.tax = 0;
app.total = 0;


/* the credtiNote value involves wich between creation or update must be done*/
app.creditNoteConfirmed = function(  credtitNote_id )
{
    //if credtitNote_id is 0 then it is a creation
    if( credtitNote_id === 0 )
    {
        //creation
        app.createNewCreditNote();
    }
    else
    {
        //update
        app.callUpdateCreditNote( credtitNote_id );
    }
};

/**
 * When the user chooses the customer remaining customer fields will be filled automatically
 * @returns {undefined}
 */
app.suggestCreditNoteFields = function()
{
    
    //first delete / reset all items rows
    //iterates all rows beginning from 1 to preserve the first row
    for( var i = 1; i < app.creditNoteItemsTBody.rows.length; i++ )
    {
        //gathes the current row
        var currentRow = app.creditNoteItemsTBody.rows[i];
        app.deleteItemRow(currentRow);
    }
    //add a new row down the first row
    app.addCNItemRowAfter(app.creditNoteItemsTBody.rows[0]);
    //delete the first row
    app.deleteCNItemRow(app.creditNoteItemsTBody.rows[0]);


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

            }
            app.creditNoteDetailsChanged("denomination_select_options");  
            app.creditNoteDetailsChanged("address_input"); 
            app.creditNoteDetailsChanged("houseNumber_input"); 
            app.creditNoteDetailsChanged("postalCode_input"); 
            app.creditNoteDetailsChanged("city_input"); 
            app.creditNoteDetailsChanged("province_input"); 
            app.creditNoteDetailsChanged("vatCode_input"); 
            app.creditNoteDetailsChanged("fiscalCode_input"); 

            app.getSuggestedRows();
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
        }
    );
};


/*Makes visible the update button and changes the look of the cell that's been changed*/
app.creditNoteDetailsChanged = function(field_id)
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
            app.creditNoteDetailsChanged('vat_exempt_checkbox');
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

app.createNewCreditNote = function()
{  
    //alert message
    let message = "";
   
    //general creditNote data
    //cheks each field if something is wrong write an alert message
    
    //customer choice
    if( document.getElementById('denomination_select_options').value === "")
        message += "Scegli un Destinatario!\n";
    
    //if there is only one row and it is empty alerts tehe matter
    if (app.creditNoteItemsTBody.rows.length === 1 && app.creditNoteItemsTBody.rows[0].querySelector(".Description textarea").value === "" && app.creditNoteItemsTBody.rows[0].querySelector(".Quantity input").value === "")
        message += " La nota di accredito non contiene articoli! ";
    
    /* Delivery notes rows section*/
    if(message === "")
    {
        //if there is unless one row, begins to chek else alerts that the document has no rows
        if(app.creditNoteItemsTBody.rows.length > 0)
        {
                    
            //iterates all rows
            for( var i = 0; i < app.creditNoteItemsTBody.rows.length; i++ )
            {
                //gathes the current row
                var currentRow = app.creditNoteItemsTBody.rows[i];

                //if there's no description alerts the matter
                if( currentRow.querySelector(".Description textarea").value === "" &&  !(currentRow.querySelector(".Quantity input").value === "") )
                    message += " Nessuna descrizione nella riga " + (i+1)+"\n";
            }

            //if message is empty and there is unless one row creates the new credit note else alerts the message
            if( message === "" && app.creditNoteItemsTBody.rows.length > 0 )
            {
                app.newCreditNote.customer_id = app.customer_id;
                app.newCreditNote.date = document.getElementById("date_input").value;
                app.newCreditNote.taxableAmount = document.getElementById("taxable_amount_input").value;
                app.newCreditNote.taxAmount = document.getElementById("tax_amount_input").value;
                app.newCreditNote.vatRate = document.getElementById('vatRate').value === 'exempt' ? '0' : document.getElementById('vatRate').value;  
                app.newCreditNote.totalAmount = document.getElementById("total_amount_input").value;
                app.newCreditNote.notes = document.getElementById("notes_input").value;
                app.newCreditNote.exempt = document.getElementById("vat_exempt_checkbox").checked;
                
                //collects items ( rows ) in a Json object
                app.newCreditNote.items = app.creditNoteItemsToJson();

                document.querySelector(".Footer_message").innerHTML = " Sto creando la nota di accredito... ";
                
                let now = new Date();
                
                let execute =
                    app.newCreditNote.date !== now.toISOString().substring(0, 10)
                    || (app.newCreditNote.date === now.toISOString().substring(0, 10) && confirm("Stai salvando con data odierna, premi ok per continuare altrimenti annulla"));
                
                if( execute )
                {
                    app.createCreditNote( app.newCreditNote,
                        (creditNote)=>
                        {
                            app.printCreditNote(creditNote.creditNote_id,
                            ()=>
                            {
                                app.getCreditNoteXML(creditNote.creditNote_id,
                                ()=>
                                {
                                     window.open('creditNote_details.jsp?creditNote_id='+creditNote.creditNote_id,'_self');
                                },
                                (message)=>
                                {
                                    window.alert(message);
                                    document.querySelector(".Footer_message").innerHTML = "non riesco a produrre il file XML! ";
                                });
                            },
                            ()=>
                            {
                                document.querySelector(".Footer_message").innerHTML = "non riesco a produrre il pdf della nota di accredito. ";
                            });    
                        },
                        ()=>
                        {
                           window.alert("non riesco a creare la nota di accredito"); 
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
app.callUpdateCreditNote = function( creditNote_id )
{  
    document.querySelector(".Footer_message").innerHTML = "Sto modificando la nota di accredito... ";
    app.creditNote.creditNote_id = creditNote_id;
    //alert message
    let message = "";
   
    //general creditNote data
    //cheks each field if something is wrong write an alert message
    if( document.getElementById('denomination_select_options').value === "")
        message += "Scegli un Destinatario!\n";
           
    /* creditNote rows section*/
    if(message === "")
    {
        //if there is unless one row, begins to chek else alerts that the document has no rows
        if(app.creditNoteItemsTBody.rows.length > 0)
        {
                    
            //iterates all rows
            for( var i = 0; i < app.creditNoteItemsTBody.rows.length; i++ )
            {
                //gathes the current row
                var currentRow = app.creditNoteItemsTBody.rows[i];

                //if there's no description alerts the matter
                if( currentRow.querySelector(".Description textarea").value === "" &&  !(currentRow.querySelector(".Quantity input").value === "") )
                    message += " Nessuna descrizione nella riga " + (i+1)+"\n";
            }

            //if message is empty and there is unless one row creates the new credit note else alerts the message
            if( message === "" && app.creditNoteItemsTBody.rows.length > 0 )
            {
                app.creditNote.customer_id = app.customer_id = document.getElementById('denomination_select_options').value;;
                app.creditNote.date = document.getElementById("date_input").value;
                app.creditNote.taxableAmount = document.getElementById("taxable_amount_input").value;
                app.creditNote.taxAmount = document.getElementById("tax_amount_input").value;
                app.creditNote.vatRate = document.getElementById('vatRate').value === 'exempt' ? '0' : document.getElementById('vatRate').value;  
                app.creditNote.totalAmount = document.getElementById("total_amount_input").value;
                app.creditNote.notes = document.getElementById("notes_input").value;
                app.creditNote.exempt = document.getElementById("vat_exempt_checkbox").checked;

                //collects items in a Json object
                app.creditNote.items = app.creditNoteItemsToJson();
                
                document.querySelector(".Footer_message").innerHTML = " Sto modificando la nota di accredito... ";
                
                var now = new Date();
                
                let execute =
                    app.creditNote.date !== now.toISOString().substring(0, 10)
                    || (app.creditNote.date === now.toISOString().substring(0, 10) && confirm("Stai salvando con data odierna, premi ok per continuare altrimenti annulla"));
            
                if( execute )
                {
                    app.updateCreditNote( app.creditNote,
                    ()=>
                    {
                        app.printCreditNote(app.creditNote.creditNote_id,
                            ()=>
                            {
                                app.getCreditNoteXML(app.creditNote.creditNote_id,
                                ()=>
                                {
                                    window.open('creditNote_details.jsp?creditNote_id='+app.creditNote.creditNote_id,'_self');
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
                                document.querySelector(".Footer_message").innerHTML = "non riesco a stampare la nota di accredito. ";
                            });                       
                    },
                    ()=>
                    {
                       window.alert("non riesco a modificare la nota di accredito"); 
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

app.openCreditNotesPage = function()
{
    //assigns to app.filter current values
    app.getFiltersValues();
    //opens tasks page       
    window.open("creditNotes.jsp?&filter="+encodeURIComponent(JSON.stringify(app.filter)),'_self');
};

/*retrieves filters values from the page*/
app.getFiltersValues = function()
{
    app.filter.customer_id = document.getElementById("customer_select_options").value;
    
    app.filter.from_date = document.getElementById("from_date").value; 
    
    app.filter.to_date = document.getElementById("to_date").value;
    
};

/* updtates the tasks variable in acording to current filters values in the page*/
app.filterCreditNotes = function()
{
    document.querySelector(".Footer_message").innerHTML = "Sto filtrando le note di accredito...";
    
    //assigns to app.filter current values
    app.getFiltersValues();
    
    app.readCreditNotes(// creditNote_id, customer_id, number, fromDate, toDate, successCallback, failCallback
        null,//creditNote_id
        app.filter.customer_id,//customer_id
        null,//number
        app.filter.from_date,//fromDate
        app.filter.to_date,//toDate
        function( creditNotes )//successCallBack
        {
            app.fillCreditNotesTable(creditNotes);
            document.querySelector(".Footer_message").innerHTML = "NOTE ACCREDITO FILTRATE: " + creditNotes.length;
        },
        function()//failCallBack
        {
            document.querySelector(".Footer_message").innerHTML = "non riesco a filtrare le note di accredito! Contattare Assistenza. ";
        }
    );
};

/* refersh table rows*/
app.fillCreditNotesTable = function( creditNotes )
{
    var templateItem = document.getElementById("creditNoteTableRow");
    var itemsContainer = document.querySelector(".Table tbody");

    //empty the current content in the table
    itemsContainer.innerHTML = null;
    
    let progressiveAmount = 0.0;
     
    for(var i=0; i<creditNotes.length; i++)
    {
        var templateContent =  document.importNode(templateItem.content,true);

        templateContent.querySelector(".CreditNoteTableRow").id = "row_"+creditNotes[i].creditNote_id;
        
        templateContent.querySelector(".CreditNoteTableRow").onclick = app.setCurrentCreditNoteId(creditNotes[i].creditNote_id);

        templateContent.querySelector(".CreditNoteNumber").innerHTML = creditNotes[i].number+"-"+creditNotes[i].year;
        
        //formats the date
        var date = creditNotes[i].date;
        var year = date.substring(0,4);
        var month = date.substring(4,6);
        var day = date.substring(6,8);
        date = day+"/"+month+"/"+year;
        templateContent.querySelector(".CreditNoteDate").innerHTML = date;
        
        templateContent.querySelector(".Customer").innerHTML = creditNotes[i].denomination;
        
        templateContent.querySelector(".Taxable").innerHTML = creditNotes[i].taxableAmount.toFixed(2);
        
        progressiveAmount += parseFloat(creditNotes[i].taxableAmount);
        
        templateContent.querySelector(".Progressive").innerHTML = progressiveAmount.toFixed(2);
        
        let number = creditNotes[i].number;
        let shortYear = creditNotes[i].year;
        
        templateContent.querySelector(".Pdf").onclick = (event)=>
        {
            event.stopPropagation();
            window.open("resources/CREDIT_NOTES/NOTA_ACCREDITO_DUESSE_"+number+"_"+shortYear+".pdf","_blank");
        };
        
        itemsContainer.appendChild(templateContent); 
    }   
};

/**
 * 
 * @param {type} creditNote_id
 * @returns {Function}
 */
app.setCurrentCreditNoteId = function( creditNote_id )
{
    return function()
    {
        window.open("creditNote_details.jsp?creditNote_id=" + creditNote_id,'_self');
    };
};

//fills rows table in invoce_details.jsp
app.getCreditNoteRows = function( creditNote_id )
{
    //sets current customer_id
    app.customer_id = document.getElementById('denomination_select_options').value;
    
    app.readCreditNoteRows( creditNote_id,
        function(rows)
        {
            //gets the table
            var table = document.querySelector("#creditNoteItemsTable tbody");

            //adds empty rows ( all  less one because the table already has an empty row)
            for( var i = 0; i < rows.length-1; i++ )
            {
                //gets a new table row 
                var tableRow = app.newCNItemRow();
                table.appendChild(tableRow);
            }

            //fills contents
            for( var i = 0; i < table.rows.length; i++)
            {
                for( var j = 0; j < table.rows[i].cells.length; j++ )
                {
                    //inserts the description
                    table.rows[i].cells[0].firstChild.value = rows[i][1];

                    //inserts the quantity if there'e a deliverynote row related else sets 1
                    table.rows[i].cells[1].firstChild.value = rows[i][0]; 
                    
                    //inserts the single amont
                    table.rows[i].cells[2].firstChild.value = rows[i][2].toFixed(2);
                    
                    //inserts the total amount
                    table.rows[i].cells[3].firstChild.value = rows[i][3].toFixed(2);
                }
            }
            
            //updates taxable, tax, and total
            app.taxable = parseFloat(document.querySelector("#taxable_amount_input").value); 
            app.tax = parseFloat(document.querySelector("#tax_amount_input").value);  
            app.total = parseFloat(document.querySelector("#total_amount_input").value);  
            
        },
        function()
        {
            window.alert("non riesco a caricare le righe della nota di accredito.");
        }
    );
    
};


/**
 * retireves and suggest related to the chosen customer:
 *  orders completed and not delivered;
 *  deliveryNoteRows not creditNoted;
 * @returns {undefined}
 */
app.getSuggestedRows = function()
{
    //if there chosen customer is empty just reloads the page
    if( app.customer_id === "")
        window.open('creditNote_details.jsp','_self');
    else
    {
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
};

/*
 * Called each time a row amount has been changed / inserted
 * first adjustes taxable tax and total then calls refreshDues function
 */
app.refreshAmounts = function()
{
    //updates application variable
    app.creditNoteItemsTBody = document.querySelector("#creditNoteItemsTable tbody");
    
    //resets totals
    app.total = 0;
    app.tax = 0;
    app. taxable = 0;
    
    //resets inputs fields
    document.querySelector("#taxable_amount_input").value = "";
    document.querySelector("#tax_amount_input").value = "";
    document.querySelector("#total_amount_input").value = "";
    
   //iterates all amounts in the table and adds them to obtain the total
    for( var i = 0; i < app.creditNoteItemsTBody.rows.length; i++ )
    {
        //puts zero when there's nothing inside the cell to do correct number conversion 
        if(app.creditNoteItemsTBody.rows[i].querySelector(".SingleAmount input").value === "" )
            app.creditNoteItemsTBody.rows[i].querySelector(".SingleAmount input").value = "0.000";
        if(app.creditNoteItemsTBody.rows[i].querySelector(".TotalAmount input").value === "" )
            app.creditNoteItemsTBody.rows[i].querySelector(".TotalAmount input").value = "0.00";
        //truncates to two decimals 
        var singleAmount = Math.round( parseFloat(app.creditNoteItemsTBody.rows[i].querySelector(".SingleAmount input").value)*1000)/1000;
        var totalAmount = singleAmount * (parseInt(app.creditNoteItemsTBody.rows[i].querySelector(".Quantity input").value)) ;
        //displays in two decimals
        app.creditNoteItemsTBody.rows[i].querySelector(".SingleAmount input").value = singleAmount.toFixed(3);
        app.creditNoteItemsTBody.rows[i].querySelector(".TotalAmount input").value = totalAmount.toFixed(2);
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
  
};

app.checkCustomerChoosen = (input)=>
{
    
    if( app.customer_id === '' || app.customer_id === null || app.customer_id === undefined )
    {
        input.value = '';
        window.alert("Seleziona un Cliente!");
    }
};

//app.changeInvoiceDate = ()=>
//{
//    let date = document.getElementById("date_input").value;
//    let completeNumber = document.getElementById("number_input").value;
//    let number = completeNumber.substring(0,completeNumber.indexOf("-")).trim();
//    let year = completeNumber.substring(completeNumber.indexOf("-")).replace("-","").trim();
//    
//    if( date !== "" && number !== "" && year !== "" )
//    {
//        app.updateInvoiceDate(// deliveryNote_id, customer_id, transporter_id, number, fromDate, toDate, successCallback, failCallback
//        date,//creditNote_id_id
//        number,//customer_id
//        year,//number
//        function()//successCallBack
//        {
//            window.alert("Data fattura modificata!");
//            document.querySelector(".Footer_message").innerHTML = "Data fattura modificata!";
//        },
//        function()//failCallBack
//        {
//            window.alert("Operazione fallita!");
//            document.querySelector(".Footer_message").innerHTML = "Non riesco a modificare la data della fattura!";
//        }
//    );
//    }
//    else
//        window.alert("Formato dati non valido!");
//};    
console.log('END:creditNotes.js');



