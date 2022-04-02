/* global fetch, Promise */

//app is global variable implemented as singleton
if(typeof app==='undefined' || app===null) app = {};

console.log('BEGIN:schedule.js'); 

//contains all filters values
app.filter = {};

//new schedule date object
app.newScheduleDate = {};

/*retrieves filters values from the page*/
app.getFiltersValues = function()
{
    app.filter.customer_id = document.getElementById("customer_select_options").value;
    
    app.filter.from_date = document.getElementById("from_date").value; 
    
    app.filter.to_date = document.getElementById("to_date").value;
     
};

/* updtates the schedule variable in according to current filters values in the page*/
app.filterAmountSchedules = function()
{
    document.querySelector(".Footer_message").innerHTML = "Sto filtrando le scadenze...";
    
    //assigns to app.filter current values
    app.getFiltersValues();
    
    app.readScheduleDates(// deliveryNote_id, customer_id, transporter_id, number, fromDate, toDate, successCallback, failCallback
        app.filter.customer_id,//customer_id
        app.filter.from_date,//fromDate
        app.filter.to_date,//toDate
        function( amountSchedules )//successCallBack
        {
            app.fillAmountSchedulesTable(amountSchedules);
            document.querySelector(".Footer_message").innerHTML = "SCADENZE FILTRATE: " + amountSchedules.length;
        },
        function()//failCallBack
        {
            document.querySelector(".Footer_message").innerHTML = "non riesco a filtrare le scadenze! Contattare Assistenza. ";
        }
    );
};

/* refersh table rows*/
app.fillAmountSchedulesTable = function( scheduleDates )
{
    var templateItem = document.getElementById("scheduleDateTableRow");
    var itemsContainer = document.querySelector(".Table tbody");

    //empty the current content in the table
    itemsContainer.innerHTML = null;
    
    let progressiveAmount = 0.0;
     
    for(var i=0; i<scheduleDates.length; i++)
    {
        var templateContent =  document.importNode(templateItem.content,true);

        templateContent.querySelector(".ScheduleTableRow").id = "row_"+scheduleDates[i].invoice_id;
        
        templateContent.querySelector(".ScheduleTableRow").onclick = app.setCurrentScheduleInvoiceId(scheduleDates[i].invoice_id);
        
        //row cells content
        var date = scheduleDates[i].amountDate.substring(8,10)+"-"+scheduleDates[i].amountDate.substring(5,7)+"-"+scheduleDates[i].amountDate.substring(0,4);
        
        templateContent.querySelector(".Customer").innerHTML = scheduleDates[i].denomination;
        
        templateContent.querySelector(".InvoiceNumber").innerHTML = scheduleDates[i].number+"-"+scheduleDates[i].year;
        
        templateContent.querySelector(".ScheduleDate").innerHTML = date;
        
        templateContent.querySelector(".Total").innerHTML = scheduleDates[i].amount.toFixed(2);
        
        templateContent.querySelector(".PaymentTerms").innerHTML = scheduleDates[i].paymentTerms;
        
        progressiveAmount += parseFloat(scheduleDates[i].amount);
        
        templateContent.querySelector(".Progressive").innerHTML = progressiveAmount.toFixed(2);
        
        itemsContainer.appendChild(templateContent); 
    }   
};

/**
 * 
 * @param {type} invoice_id
 * @returns {Function}
 */
app.setCurrentScheduleInvoiceId = function( invoice_id )
{
    return function()
    {
        window.open("invoice_details.jsp?invoice_id=" + invoice_id,'_self');
    };
};

console.log('END:scheduleDates.js');

