/* global fetch, Promise */

//app is global variable implemented as singleton
if(!app) var app = {};

console.log('BEGIN:quotes.js'); 

//contains all filters values
app.filter = {};

//new invoice object
app.newQuote = {};

//existing invoice object to be updated
app.quote = {};

app.customer_id = null;

app.user_id = null;

//invoice rows table 
app.quoteRowsTBody = document.querySelector("#quoteRowsTable tbody");

//amounts managment variables
app.amount = 0;



/* the invoice_id value involves wich between creation or update must be done*/
app.quoteConfirmed = function(  quote_id, duplicate )
{
    //if invoice_id is 0 then it is a creation
    if( quote_id === 0 || duplicate )
    {
        //creation
        app.createNewQuote();
    }
    else
    {
        //update
        app.callUpdateQuote( quote_id );
    }
};

/**
 * When the user chooses the customer remaining customer fields will be filled automatically
 * @returns {undefined}
 */
app.suggestQuoteFields = function()
{
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
            }
            app.quoteDetailsChanged("denomination_select_options");  
            app.quoteDetailsChanged("address_input"); 
            app.quoteDetailsChanged("houseNumber_input"); 
            app.quoteDetailsChanged("postalCode_input"); 
            app.quoteDetailsChanged("city_input"); 
            app.quoteDetailsChanged("province_input"); 
            
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
        }
    );
    
};

/*Makes visible the update button and changes the look of the cell that's been changed*/
app.quoteDetailsChanged = function(field_id)
{
    /* INSERT FIELDS CHECKING*/
    document.getElementById(field_id).classList.add('Changed');
};

app.createNewQuote = function()
{  
    //alert message
    let message = "";
   
    //general quote data
    //cheks each field if something is wrong writes an alert message
    
    //customer choice
    if( document.getElementById('denomination_select_options').value === "")
        message += "Scegli un Destinatario!\n";
    
    //if there is only one row and it is empty alerts tehe matter
    if (app.quoteRowsTBody.rows.length === 1  && app.quoteRowsTBody.rows[0].querySelector(".Description textarea").value === "" )
        message += " Il preventivo non ha descrizioni! ";
    
    if(message === "")
    {
        /*if there is unless one row, begins to check else alerts that the document has no rows*/
        if(app.quoteRowsTBody.rows.length > 0)
        {       
            //iterates all rows
            for( var i = 0; i < app.quoteRowsTBody.rows.length; i++ )
            {
                //gathes the current row
                var currentRow = app.quoteRowsTBody.rows[i];

                //if the rows is empty removes it from the table
                if( currentRow.querySelector(".Description textarea").value === "" &&  currentRow.querySelector(".Amount input").value === "" )
                    app.deleteItemRow(currentRow);
                //if there's no description alerts the matter
                if( currentRow.querySelector(".Description textarea").value === "" &&  !(currentRow.querySelector(".Amount input").value === "") )
                    message += " Nessuna descrizione nella riga " + (i+1)+"\n";
            }

            //if message is empty and there is unless one row creates the new quote else alerts the message
            if( message === "" && app.quoteRowsTBody.rows.length > 0 )
            {
                app.newQuote.customer_id = app.customer_id;/* custoemer_id is set on fields suggestinf on customer selection*/
                app.newQuote.user_id = document.getElementById("quote").dataset.user_id;
                app.newQuote.date = document.getElementById("date_input").value;
                app.newQuote.year = document.getElementById("date_input").value.substring(2,4);
                app.newQuote.address = document.getElementById("address_input").value;
                app.newQuote.houseNumber = document.getElementById("houseNumber_input").value;
                app.newQuote.postalCode = document.getElementById("postalCode_input").value;
                app.newQuote.city = document.getElementById("city_input").value;
                app.newQuote.province = document.getElementById("province_input").value;
                app.newQuote.firstTitle_id = document.getElementById("firstTitle_select_options").value;
                app.newQuote.firstForAttention = document.getElementById("firstForAttention_input").value;
                app.newQuote.secondTitle_id = document.getElementById("secondTitle_select_options").value;
                app.newQuote.secondForAttention = document.getElementById("secondForAttention_input").value;
                app.newQuote.subject = document.getElementById("subject_input").value;
//                app.newQuote.amount = document.getElementById("amount_input").value; Eliminated the 11th July 2019 becouse the total not always is correct, eg. when different versions of the same product are offered  
               
                //collects items rows  in a Json object calling the method in quoteElasticTable
                app.newQuote.rows = app.quoteRowsToJson();

                document.querySelector(".Footer_message").innerHTML = " Sto creando il preventivo... ";
                
                app.createQuote( app.newQuote,
                    (quote)=>
                    {
                        app.printQuote(quote.quote_id,
                        ()=>
                        {
                            window.open('quote_details.jsp?quote_id='+quote.quote_id,'_self');
                            document.querySelector(".Footer_message").innerHTML =" Preventivo Creato ";
                         
                        },
                        ()=>
                        {
                            document.querySelector(".Footer_message").innerHTML = "non riesco a produrre il pdf del preventivo. ";
                        });    
                    },
                    ()=>
                    {
                       window.alert("non riesco a creare il preventivo"); 
                    }
                );
                  
            }
            else
                window.alert( message );
        }
        else
            window.alert( "Il preventivo non ha righe!" );
    }
    else
        window.alert( message );
};

/**/
app.callUpdateQuote = function( quote_id )
{  
    document.querySelector(".Footer_message").innerHTML = "Sto modificando il preventivo... ";
    app.quote.quote_id = quote_id;
    //alert message
    let message = "";
   
    //general invoice data
    //cheks each field if something is wrong write an alert message
    if( document.getElementById('denomination_select_options').value === "")
        message += "Scegli un Destinatario!\n";
           
    /* quote rows section*/
    if(message === "")
    {
        //if there is unless one row, begins to chek else alerts that the document has no rows
        if(app.quoteRowsTBody.rows.length > 0)
        {   
            //iterates all rows
            for( var i = 0; i < app.quoteRowsTBody.rows.length; i++ )
            {
                //gathes the current row
                var currentRow = app.quoteRowsTBody.rows[i];

                //if there's no description alerts the matter
                if( currentRow.querySelector(".Description textarea").value === ""  )
                    message += " Nessuna descrizione nella riga " + (i+1)+"\n";
            }

            //if message is empty and there is unless one row creates the new delivery note else alerts the message
            if( message === "" && app.quoteRowsTBody.rows.length > 0 )
            {
                
                app.quote.customer_id = app.customer_id;/* custoemer_id is set on fields suggestinf on customer selection*/
                app.quote.user_id = document.getElementById("quote").dataset.user_id;
                app.quote.date = document.getElementById("date_input").value;
                app.quote.year = document.getElementById("date_input").value.substring(2,4);
                app.quote.address = document.getElementById("address_input").value;
                app.quote.houseNumber = document.getElementById("houseNumber_input").value;
                app.quote.postalCode = document.getElementById("postalCode_input").value;
                app.quote.city = document.getElementById("city_input").value;
                app.quote.province = document.getElementById("province_input").value;
                app.quote.firstTitle_id = document.getElementById("firstTitle_select_options").value;
                app.quote.firstForAttention = document.getElementById("firstForAttention_input").value;
                app.quote.secondTitle_id = document.getElementById("secondTitle_select_options").value;
                app.quote.secondForAttention = document.getElementById("secondForAttention_input").value;
                app.quote.subject = document.getElementById("subject_input").value;
//                app.quote.amount = document.getElementById("amount_input").value; Eliminated the 11th July 2019 becouse the total not always is correct, eg. when different versions of the same product are offered  

                //collects items in a Json object
                app.quote.rows = app.quoteRowsToJson();
                
                document.querySelector(".Footer_message").innerHTML = " Sto modificando il preventivo... ";
                
                app.updateQuote( app.quote,
                ()=>
                {
                    app.printQuote(app.quote.quote_id,
                        ()=>
                        {
                            
                                window.open('quote_details.jsp?quote_id='+app.quote.quote_id,'_self');
                                document.querySelector(".Footer_message").innerHTML = "Preventivo modificato";
                            
                        },
                        ()=>
                        {
                            document.querySelector(".Footer_message").innerHTML = "non riesco a produrre il pdf del preventivo. ";
                        });                    
                },
                ()=>
                {
                   window.alert("non riesco a modificare il preventivo"); 
                });
                
            }
            else
                window.alert( message );
        }
        else
            window.alert( "Il preventivo non ha righe!" );
    }
    else
        window.alert( message );
};

/*** Quotes page ************************/

app.openQuotesPage = function()
{
    //assigns to app.filter current values
    app.getFiltersValues();
    //opens quotes page       
    window.open("quotes.jsp?&filter="+encodeURIComponent(JSON.stringify(app.filter)),'_self');
};

/*retrieves filters values from the page*/
app.getFiltersValues = function()
{
    app.filter.customer_id = document.getElementById("customer_select_options").value;
    
    app.filter.from_date = document.getElementById("from_date").value; 
    
    app.filter.to_date = document.getElementById("to_date").value;
    
    app.filter.number = document.getElementById("numberFilter").value;
    
};

/* updtates the tasks variable in acording to current filters values in the page*/
app.filterQuotes = function()
{
    document.querySelector(".Footer_message").innerHTML = "Sto filtrando i preventivi...";
    
    //assigns to app.filter current values
    app.getFiltersValues();
    
    app.readQuotes(// deliveryNote_id, customer_id, transporter_id, number, fromDate, toDate, successCallback, failCallback
        null,//quote_id
        app.filter.customer_id,//customer_id
        app.filter.number,//number
        app.filter.from_date,//fromDate
        app.filter.to_date,//toDate
        function( quotes )//successCallBack
        {
            app.fillQuotesTable(quotes);
            document.querySelector(".Footer_message").innerHTML = "PREVENTIVI FILTRATI: " + quotes.length;
        },
        function()//failCallBack
        {
            document.querySelector(".Footer_message").innerHTML = "non riesco a filtrare i preventivi! Contattare Assistenza. ";
        }
    );
};

/* refersh table rows*/
app.fillQuotesTable = function( quotes )
{
    var templateItem = document.getElementById("quoteTableRow");
    var itemsContainer = document.querySelector(".Table tbody");

    //empty the current content in the table
    itemsContainer.innerHTML = null;
     
     
    for(var i=0; i<quotes.length; i++)
    {
        var templateContent =  document.importNode(templateItem.content,true); 
        
        //row id and callback
        templateContent.querySelector(".QuoteTableRow").id = "row_"+quotes[i].quotes_id;
        
        templateContent.querySelector(".QuoteTableRow").onclick = app.setCurrentQuoteId(quotes[i].quote_id);

        let number = quotes[i].number;
        let year = quotes[i].year;
        
        //row cells content
        var date = quotes[i].date.substring(8,10)+"-"+quotes[i].date.substring(5,7)+"-"+quotes[i].date.substring(0,4);
        
        templateContent.querySelector(".QuoteDate").innerHTML = date;
        
        templateContent.querySelector(".QuoteNumber").innerHTML = quotes[i].number+"-"+quotes[i].year;
        
        templateContent.querySelector(".Customer").innerHTML = quotes[i].customerDenomination;
        
        //templateContent.querySelector(".Amount").innerHTML = quotes[i].amount.toFixed(2) === 0 ? "" : quotes[i].amount.toFixed(2);
        
        
        templateContent.querySelector(".Pdf").onclick = (event)=>
        {
            event.stopPropagation();
            window.open('resources/QUOTES/PREVENTIVO_DUESSE_'+number+'_'+year+'.pdf','_blank');
        };
        
        itemsContainer.appendChild(templateContent); 
    }   
};

/**
 * 
 * @param {type} quote_id
 * @returns {Function}
 */
app.setCurrentQuoteId = function( quote_id )
{
    return function()
    {
        window.open("quote_details.jsp?quote_id=" + quote_id  ,'_self');
    };
};

/*fills rows table in quote_details.jsp*/
app.getQuoteRows = function( quote_id )
{
    //sets current customer_id
    app.customer_id = document.getElementById('denomination_select_options').value;

    app.readQuoteRows( quote_id,
        function(rows)
        {
            //gets the table
            var table = document.querySelector("#quoteRowsTable tbody");

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
                    //inserts description
                    table.rows[i].cells[0].firstChild.value = rows[i][0];

                    //inserts amount
                    table.rows[i].cells[1].firstChild.value = rows[i][1].toFixed(2);;

                }
            }

//            app.amount = parseFloat(document.querySelector("#amount_input").value);  Eliminated the 11th July 2019 becouse the total not always is correct, eg. when different versions of the same product are offered  
            
            app.getQuoteAttachmentsTable(quote_id);
        },
        function()
        {
            window.alert("non riesco a caricare le righe del preventivo.");
        }
    );
};


/*
 * Called each time a row amount has been changed / inserted
 * updates the quote amount
 */
app.refreshAmounts = function()
{
    //updates application variable
    app.quoteRowsTBody = document.querySelector("#quoteRowsTable tbody");
    
    //resets totals
    app.total = 0;
   
    
    //resets fields
//    document.querySelector("#amount_input").value = ""; Eliminated the 11th July 2019
    
   /*iterates all amounts in the table and adds them to obtain the total*/
    for( var i = 0; i < app.quoteRowsTBody.rows.length; i++ )
    {
        //puts zero when there's nothing inside the cell to do correct number conversion 
        if(app.quoteRowsTBody.rows[i].querySelector(".RowAmount input").value === "" )
            app.quoteRowsTBody.rows[i].querySelector(".RowAmount input").value = "0.00";
        
        var rowAmount = (Math.round( parseFloat(app.quoteRowsTBody.rows[i].querySelector(".RowAmount input").value)*100)/100);
        app.quoteRowsTBody.rows[i].querySelector(".RowAmount input").value = rowAmount.toFixed(2);
        
        //adds amount
        //app.total += rowAmount Eliminated the 11th July 2019 becouse the total not always is correct, eg. when different versions of the same product are offered  ;

    }
    
  //updates taxable, tax, and total
  //document.querySelector("#amount_input").value = app.total.toFixed(2) Eliminated the 11th July 2019 becouse the total not always is correct, eg. when different versions of the same product are offered  ;
  
};

app.uploadQuoteAttachment = function(quote_id)
{
    app.quoteUpload( quote_id, 
    function()
    {
        //FILLS TASKS TABLE
        app.getQuoteAttachmentsTable(quote_id);
    },
    function()
    {
        window.alert("Attachment Loading Error!");
    });
    
};

app.getQuoteAttachmentsTable = function( quote_id )
{
    
    /**
     * readAuthos is a method located in dbi.js. It takes three parameters
     * denominationHint
     * successCallback
     * failCallback
     */
    app.readQuoteAttachments(
        quote_id,
        function(attachments)
        {
            app.refreshAttachments(attachments);
        },
        function()
        {
            document.querySelector(".Footer_message").innerHTML = "non riesco a leggere gli allegati!";
        }
    );
};

app.refreshAttachments = function(attachments)
{
    //gets the authors table body template
    var itemTemplate = document.querySelector("#quote_attachments_template");
    // gets the table body that will be the container of template instances 
    var itemsContainer = document.querySelector("#attachments tbody");
    //empty the current content in the table
    //check necessary becpude on duplication itemsContainer is null
    if( itemsContainer !== null )
        itemsContainer.innerHTML = null;
    /**
     * Iterates authors that is a 
     * @type Number
     */
    for(var i=0; i<attachments.length; i++)
    {
        var templateContent =  document.importNode(itemTemplate.content,true);
        
        /*actions buttons*/
        var showBtn = document.createElement("a");
        showBtn.innerHTML = "APRI";
        showBtn.className = "Button";
        showBtn.setAttribute('target',"_blank");
        
        var deleteBtn = document.createElement("input");
        deleteBtn.type = "button";
        deleteBtn.value = "ELIMINA";
        
        //ID ASSIGNMENT 
        templateContent.querySelector(".QuoteRow").id = "quote_row_"+attachments[i][0];
        templateContent.querySelector(".Date").id = "date_"+attachments[i][0];
        templateContent.querySelector(".CurrentFileName").id = "name_"+attachments[i][0];
        templateContent.querySelector(".Actions").id = "action_"+attachments[i][0];
        deleteBtn.id = "deleteBtn_"+attachments[i][0];
        showBtn.id = "showBtn_"+attachments[i][0];
        
        //buttons class assignment
        deleteBtn.className = "Button Delete";
        showBtn.className = "Button Show";
        
        //inserts buttons in table cell
        templateContent.querySelector("#action_"+attachments[i][0]).appendChild(showBtn);
        templateContent.querySelector("#action_"+attachments[i][0]).appendChild(deleteBtn);
        
        //content fillling
        //templateContent.querySelector(".Id").innerHTML = authors[i][0];
        templateContent.querySelector("#date_"+attachments[i][0]).textContent =attachments[i][3];
        templateContent.querySelector("#name_"+attachments[i][0]).textContent = attachments[i][4];
      
        
        // DEFINIZIONE DELLE CALLBACK
        //execBtn.onclick = function() { runCommand() };
        //templateContent.querySelector("#deleteBtn_"+attachments[i][0]).setAttribute( 'oncllick', 'app.deleteAttachment( attachments[i][0]);');
        templateContent.querySelector("#deleteBtn_"+attachments[i][0]).onclick = app.deleteAttachment(attachments[i][0], attachments[i][2]);
        //templateContent.querySelector("#order_row_"+app.orders[i][0]).onclick = app.setCurrentOrderIdCustomerId(app.orders[i][0], app.orders[i][1]);
        templateContent.querySelector("#showBtn_"+attachments[i][0]).setAttribute('href',"resources/QuoteAttachments/"+attachments[i][1]);
        
        itemsContainer.appendChild(templateContent); 
    } 

};

/*closure*/
app.deleteAttachment = function( attachment_id, quote_id )
{
    return function()
    {
        app.deleteQuoteAttachment( attachment_id,quote_id,
            function()
            {
                alert('Allegato Eliminato');
                app.getQuoteAttachmentsTable(quote_id);
            },
            function()
            {
                alert('Non Riesco ad Eliminare l\'Allegato');
            });
    };
};

console.log('END:quotes.js');
/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


