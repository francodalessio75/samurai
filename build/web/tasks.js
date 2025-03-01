/* global fetch */

//app is global variable implemented as singleton
if(typeof app==='undefined' || app===null) app = {};

console.log('BEGIN:tasks.js');



//fields to be checked
app.hours = "";
app.orderCode = "";
app.jobSubtype_id = "";
app.date = "";
app.translations_center_id = "";
app.translationPrice = "";
app.translationCost = "";
app.variouseMaterialDesc = "";
app.variouseMaterialCost = "";
app.externalJobsDesc = "";
app.externalJobsHours = "";
app.externalJobsCost = "";
app.transfertKms = "";
app.transfertCost = "";
app.notes = "";
//contains all filters values
app.filter = {};
//user_id and user_role
app.user_id = 0;
app.user_role = "";

//boolean that manages aggreagate / detailed view
app.isAggregate = false;



/* the task_id value involves wich beteween creation or update must be done*/
app.taskConfirmed = function(  task_id, user_id )
{
    //if task_id is 0 then it is a creation
    if( task_id === 0 )
    {
        //creation
        app.createNewTask( user_id );
    }
    else
    {
        //update
        app.taskUpdated( task_id );
    }
};

/**
 * order_id, user_id, jobSubtype, date, hours, translationsCenterName, translationPrice, translationQuotation, variouseMaterialDesc, variouseMaterialCost, transfertKms, transfertCost, notes, successCallback, failCallback
 */
app.createNewTask = function( user_id )
{
    //checks empty hours fiels
    var hourValueConfirm = true;
   
    var checkingValue =  app.checkFields();
    
    if( checkingValue === "" )
    {
        if( app.hours === "0.0" || app.hours === "0" )
            hourValueConfirm = confirm("Il campo ore ha valore 0.0. Sicuri di voler proseguire?");
        if( hourValueConfirm )
        {
             document.querySelector(".Footer_message").innerHTML = " Sto creando la Lavorazione...";
         //String order_code, Long user_id, Long jobSubtype_id, String date, Double hours, Long translations_center_id, Double translationPrice, String translationQuotation, String variouseMaterialDesc, Double variouseMaterialCost, Integer transfertKms, Double transfertCost, String notes 
          app.createTask( app.orderCode, user_id, app.jobSubtype_id, app.date, app.hours, app.translations_center_id, app.translationPrice, app.translationCost, app.externalJobsDesc, app.externalJobsHours, app.externalJobsCost, app.variouseMaterialDesc, app.variouseMaterialCost, app.transfertKms, app.transfertCost, app.notes,
              function( task_id )
              {
                window.open("task_details.jsp",'_self');
                app.setCurrentTaskId( task_id )();
                document.querySelector(".Footer_message").innerHTML = "";
              },
              function()
              {
                  document.querySelector(".Footer_message").innerHTML = "non riesco a creare la lavorazione! Contattare Assistenza.";
              }
          );  
        } 
    }
    else
        alert( checkingValue );
};

//check fields validity on creation / update
app.checkFields = function()
{
    //message to be alerted in case of not validity
    var notValidityMessage = "";
    //fields assignment
    app.hours = document.getElementById("hours_input").value;
    app.orderCode = document.getElementById('order_code_input').value.trim();
    app.jobSubtype_id = document.getElementById("job_subtype_select_options").value;
    app.date = document.getElementById("task_date_input").value;
    app.translations_center_id = document.getElementById("translation_center_select_options").value;
    app.translationPrice = document.getElementById("translations_price_input").value;
    app.translationCost = document.getElementById("translations_cost_input").value;
    app.externalJobsDesc = document.getElementById("external_jobs_desc_input").value;
    app.externalJobsHours = document.getElementById("external_jobs_hours_input").value;
    app.externalJobsCost = document.getElementById("external_jobs_cost_input").value;
    app.variouseMaterialDesc = document.getElementById("variouse_material_desc_input").value;
    app.variouseMaterialCost = document.getElementById("variouse_material_cost_input").value;
    app.transfertKms = document.getElementById("transfert_kms_input").value;
    app.transfertCost = document.getElementById("transfert_cost_input").value;
    app.notes = document.getElementById("notes_input").value;
    
    var checkingResult = app.validOrderCode();
    //checks first order code
    if( checkingResult === "" )
    {  
        //checks hours
        notValidityMessage = app.validDate( "task_date_input", "data");
        
        //checks hours
        notValidityMessage = notValidityMessage.concat(app.validHours( "hours_input", "ore"));
        
        
        //checks job subtype
        if( app.jobSubtype_id === "" || app.jobSubtype_id === undefined )
            notValidityMessage = notValidityMessage.concat("Inserisci un tipo di lavorazione valido.\n");
        
        
        //checks tarnslationPrice
        notValidityMessage = notValidityMessage.concat(app.validPrice("translations_price_input", "costo traduzioni"));
        
        /*if( app.validPrice("translations_price_input", "costo traduzioni") !== "" )
            notValidityMessage = notValidityMessage.concat( notValidityMessage );*/
        
        //checks external jobs cost
        notValidityMessage = notValidityMessage.concat(app.validPrice("external_jobs_cost_input", "costo lavorazioni esterne"));
        /*
        notValidityMessage = app.validPrice("external_jobs_cost_input", "costo lavorazioni esterne");
        if( notValidityMessage !== "" )
            notValidityMessage = notValidityMessage.concat(notValidityMessage);*/
        
        //checks variouse material cost
        notValidityMessage = notValidityMessage.concat(app.validPrice("variouse_material_cost_input", "costo materiale"));
        /*
        notValidityMessage = app.validPrice("variouse_material_cost_input", "costo materiale");
        if( notValidityMessage !== "" )
            notValidityMessage = notValidityMessage.concat(notValidityMessage);*/
        
        //checks transfert Kms
        notValidityMessage = notValidityMessage.concat(app.validInteger("transfert_kms_input", " chilometri trasferta"));
        /*
        notValidityMessage = app.validInteger("transfert_kms_input", " chilometri trasferta");
        if( notValidityMessage !== "" )
            notValidityMessage = notValidityMessage.concat(notValidityMessage !== "");*/
        
        //checks tarnslationPrice
        notValidityMessage = notValidityMessage.concat(app.validPrice("transfert_cost_input", "costi trasferta"));
        
        /*
        notValidityMessage = app.validPrice("transfert_cost_input", "costi trasferta"); 
        if( notValidityMessage !== "" )
            notValidityMessage = notValidityMessage.concat(notValidityMessage);*/
    }
    /*else
        notValidityMessage = notValidityMessage.concat(checkingResult);*/
    
    return notValidityMessage.concat(checkingResult);
};


/**/
app.taskUpdated = function( task_id )
{
    //checks empty hours fiels
    var hourValueConfirm = true;
    
    //check fileds result message
    var message = app.checkFields();
    
    if( message === "" )
    {
        if( app.hours === "0.0" || app.hours === "0" )
            hourValueConfirm = confirm("Il campo ore ha valore 0.0. Sicuri di voler proseguire?");
        if( hourValueConfirm )
        {
            document.querySelector(".Footer_message").innerHTML = " Sto modificando la Lavorazione...";
            //from dbi -> task_id, order_code, jobSubtype, taskDate, hours, translationsCenterName, translationPrice, variouseMaterialDesc, variouseMaterialCost, transfertKms, transfertCost, notes, successCallback, failCallback
            app.updateTask( task_id, app.orderCode, app.jobSubtype_id, app.date, app.hours, app.translations_center_id, app.translationPrice, app.translationCost, app.externalJobsDesc, app.externalJobsHours, app.externalJobsCost, app.variouseMaterialDesc, app.variouseMaterialCost, app.transfertKms, app.transfertCost, app.notes,
                function( )
                {
                    window.open("task_details.jsp",'_self');
                    app.setCurrentTaskId( task_id )();
                    document.querySelector(".Footer_message").innerHTML = "";
                },
                function()
                {
                    document.querySelector(".Footer_message").innerHTML = "non riesco a modificare la lavorazione! Contattare Assistenza, codice errore: 0003 ";
                }
            ); 
        } 
    }
    else
        alert( message );
    
};

/*Makes visible the update button and changes the look of the cell that's been changed*/
app.detailsChanged = function(field_id)
{

    //cheks hours input if it is wrong sets back it to 0
    if( field_id === "hours_input" )
    {
        var message = app.validHours( "hours_input" , "ore");
        if( message !== "")
            alert( message );
    }
    
    if( field_id === "external_jobs_hours_input" )
    {
        var message = app.validHours( "external_jobs_hours_input" , "ore");
        if( message !== "")
            alert( message );
    }
        
    //checks tranfert cost if it is wrong sets it beck to 0
    if( field_id === "transfert_cost_input" )
    {
        var message =  app.validPrice( "transfert_cost_input", "Costo Trasferta" );
        if( message !== "" )
            alert( message );
        
    }
    
    //checks translations price if it is wrong sets it beck to 0
    if( field_id === ("translations_price_input") )
    {
        var message =  app.validPrice( "translations_price_input", "Prezzo Traduzioni" );
        if( message !== "" )
            alert( message );
        
    }
    
    //checks translations price if it is wrong sets it beck to 0
    if( field_id === ("translations_cost_input") )
    {
        var message =  app.validPrice( "translations_cost_input", "Costo Traduzioni" );
        if( message !== "" )
            alert( message );
        
    }
    
    //checks variouse_material_cost_input price if it is wrong sets it beck to 0
    if( field_id === ("external_jobs_cost_input") )
    {
        var message =  app.validPrice( "external_jobs_cost_input", "Costo Lavorazioni Esterne" );
        if( message !== "" )
            alert( message );
    }
    
     //checks variouse_material_cost_input price if it is wrong sets it beck to 0
    if( field_id === ("variouse_material_cost_input") )
    {
        var message =  app.validPrice( "variouse_material_cost_input", "Costo Materiale" );
        if( message !== "" )
            alert( message );
    }
     //checks kilomeetrs if it is wrong sets it beck to 0
    if( field_id === ("transfert_kms_input") )
    {
        var message =  app.validInteger( "transfert_kms_input", "Km Trasferta" );
        if( message !== "" ) 
            alert( message );
    }
    
    //checks order code checks the order code if it is wrong sets it back to "
    if( field_id === ("order_code_input") )
    {
        app.orderCodeChanged();
    }
    
    
    document.getElementById('taskConfirmedButton').classList.add('Visible');
    document.getElementById(field_id).classList.add('Changed');
};

//when an user inserts for the first time the order code or chenges it all fields 
//related to the order must be refershed
app.orderCodeChanged = function()
{
    //retrieves order details by order_code and update in jsp page the hidden fiels order_id and order_code
    var order_code = document.getElementById('order_code_input').value.trim();
    
    //order_id, creator_id, order_code, completion_state_id, availability_id, customer_idString, machinaryModelHint, jobType_idString, fromDate, toDate, successCallback, failCallback )
    app.readOrders(null, null, order_code,  null, null, null, null, null, null,null,null,null, 
        function(orders)
        {
            if( orders.length === 1 )
            {                
                for( var i = 0; i < orders.length; i++ )
                {
                    document.getElementById("orderCompletionState_value").textContent = orders[i][13];
                    //document.getElementById("closing_reason_value").textContent = orders[i][10];
                    document.getElementById("customer_denomination_value").textContent =orders[i][4];
                    document.getElementById("machinary_model_value").textContent = orders[i][12];
                    document.getElementById("job_type_value").textContent = orders[i][7];

                }
                document.getElementById("orderCompletionState_value").classList.add('Changed');
                document.getElementById("order_code_input").classList.add('Changed');
                document.getElementById("customer_denomination_value").classList.add('Changed');
                document.getElementById("machinary_model_value").classList.add('Changed');
                document.getElementById("job_type_value").classList.add('Changed');
                /*
                app.detailsChanged("orderCompletionState_value"); 
                //app.detailsChanged("closing_reason_value"); 
                app.detailsChanged("order_code_input"); 
                app.detailsChanged("customer_denomination_value"); 
                app.detailsChanged("machinary_model_value"); 
                app.detailsChanged("job_type_value"); */
            }
            else
                alert( "Nessuna corrispondenza del Codice Lavoro nella Base Dati!\n" );
        },
        function()
        {
            alert("Si è verificato un problema nella lettura del Codice Lavoro. Se il problema persiste contatta l'Assistenza. Codice errore 0001.\n");
            document.getElementById("orderCompletionState_value").textContent = "";
            //document.getElementById("closing_reason_value").textContent = "";
            document.getElementById("customer_denomination_value").textContent ="";
            document.getElementById("machinary_model_value").textContent = "";
            document.getElementById("job_type_value").textContent = "";
            document.getElementById('order_code_input').value ="";
        }
    );
};


/* when the user presses the "RICERCA" button on task_details page, tasks page must be shown.
 * depending on filtering criteria all tasks satisfying them are put in the 
 * list. Only tasks related to the logged user will be shown, unless he's administrator.
 * In this case all tasks satisfying criteria will be shown
 */
app.openDetailedTasksPage = function(user_role)
{
    document.querySelector(".Footer_message").innerHTML = "Sto caricando le Ore Lavori... ";
    app.user_role = user_role;
    //assigns to app.filter current values
    app.getFiltersValues();
    
    //opens tasks page       
    window.open("detailedTasks.jsp?&filter="+encodeURIComponent(JSON.stringify(app.filter)),'_self');
};


/*retrieves filters values from the page*/
app.getFiltersValues = function()
{
    //retrieves filtering criteria value

    //the adminstrator GUI has the filter by operator while the operator one not. Then if the user is not administrator
    //operator_id is equal to user_id and only tasks of the requester will be shown
    if( app.user_role === "admin" || app.user_role === "consultant" )
        app.filter.operator_id = document.getElementById('operator_select_options').value; 
    else 
        app.filter.operator_id = app.user_id;
    
    app.filter.order_code = document.getElementById('order_code_Hint').value.trim();
    
    app.filter.order_serial_number = document.getElementById('order_serial_number_Hint').value.trim();
    
    app.filter.jobType_id = document.getElementById("jobType_select_options").value;
    
    app.filter.jobSubtype_id = document.getElementById("jobSubtype_select_options").value;
    
    app.filter.from_date = document.getElementById("from_date").value; 
    
    app.filter.to_date = document.getElementById("to_date").value;
    
    app.filter.completionState_id = document.getElementById("completionState_select_options").value;
    
};

   
/* updtates the tasks variable in acording to current filters values in the page*/
app.filterTasks = function( user_id, user_role )
{
    document.querySelector(".Footer_message").innerHTML = "Sto filtrando le Lavorazioni... ";
    //assigns user data
    app.user_id = user_id;
    app.user_role = user_role;
    
    //assigns to app.filter current values
    app.getFiltersValues();
    
    app.readTasks(// task_id, user_id, order_id, operator_id, orderCode, orderSerialNumber, jobType_id, jobSubtype_id, customer_id,  order_creator_id, fromDate, toDate, completion_state_id, successCallback, failCallback )
        null,//task_id
        app.user_id,//user_id
        null,//order_id
        app.filter.operator_id,//operator_id
        app.filter.order_code,//orderCode
        app.filter.order_serial_number,//orderSerialNumber
        app.filter.jobType_id,//jobType_id
        app.filter.jobSubtype_id,//jbSubtype_id
        null,//customer_id
        null,//order_creator_id
        app.filter.from_date,//fromDate
        app.filter.to_date,//toDate
        app.filter.completionState_id,//completion_state_id
        function(tasks){
            app.fillDetailedTasksTable(tasks);
            document.querySelector(".Footer_message").innerHTML = "LAVORAZIONI FILTRATE: " + tasks.length;
        },
        function()//failCallBack
        {
            document.querySelector(".Footer_message").innerHTML = "non riesco a filtrare le lavorazioni! Contattare Assistenza, codice errore : 0002 ";
        }
    );
};


/* refersh table rows in according to the user role*/
app.fillDetailedTasksTable = function(tasks)
{
    var templateItem = document.getElementById("detailedTaskTableRow");
    var detailedTableBody = document.querySelector(".DetailedTable tbody");

    //empty the current content in the detailed table
    detailedTableBody.innerHTML = null;
    
    //operator total hours : total hours worked by the operator in the period
    var operatorTotalHours = 0;
    //total worked hours in the period
    let totalHours = 0;
    //sum of Xi,n, Yi,n of ( X*Y ) where X is operatorTotalHours and Y is Operator Hourly Cost
    let totalHoursCost = 0;
    //sum of all translations cost
    let totalTranslationsCost = 0;
    //sum of all tranlsaltions amount tu put in invoice
    let totalTranslationsPrice = 0;
    //sum of all external jobs hours
    let totalExternalJobsHours = 0;
    //sum of all external jobs cost
    let totalExternalJobsCost = 0;
    //sum of all materials cost
    let totalMaterialCost = 0;
    //sum of all transfert cost
    let totalTransfertCost = 0;
    //sum of totalHoursCost + totalTranslationCost + totalExternalJobsCost totalMaterialCost +totalTransfertCost
    let totalGeneralCosts = 0;
    
    
    
    for(let i=0; i<tasks.length; i++)
    {
        let templateContent =  document.importNode(templateItem.content,true);
        templateContent.querySelector(".TaskTableRow").id = "row_"+tasks[i][0];
        templateContent.querySelector(".TaskTableRow").onclick = app.setCurrentTaskId(tasks[i][0]);
        templateContent.querySelector(".TaskDate").innerHTML = tasks[i][4];
        templateContent.querySelector(".Operator").innerHTML = tasks[i][24]+ " " + tasks[i][25];
        templateContent.querySelector(".SerialNumber").innerHTML = tasks[i][32];
        templateContent.querySelector(".OrderCode").innerHTML = tasks[i][21];
        templateContent.querySelector(".CustomerDenomination").innerHTML = tasks[i][23];
        templateContent.querySelector(".CompletionState").innerHTML = tasks[i][19];
        templateContent.querySelector(".HasAttachment").innerHTML = tasks[i][31] !== 1 ? '':'<i  id="searchIcon" class="fa fa-paperclip fe-3x" aria-hidden="true"></i>';
        templateContent.querySelector(".MachinaryModel").innerHTML = tasks[i][22];
        templateContent.querySelector(".JobType").innerHTML = tasks[i][17];
        templateContent.querySelector(".JobSubtype").innerHTML = tasks[i][3];
        templateContent.querySelector(".Hours").innerHTML = tasks[i][5];
        
         //table columns only for administrators
        if( app.user_role === "admin" )
        {
            templateContent.querySelector(".HourlyCost").innerHTML = Math.trunc(tasks[i][29]).toFixed(2);
            
            templateContent.querySelector(".TranslationCost").innerHTML = Math.trunc(tasks[i][7]).toFixed(2);
            //increases variable
            totalTranslationsCost += tasks[i][7];

            templateContent.querySelector(".TranslationPrice").innerHTML = Math.trunc(tasks[i][14]).toFixed(2);
            //increases variable
            totalTranslationsPrice += tasks[i][14];

            templateContent.querySelector(".ExternalJobsHours").innerHTML = Math.trunc(tasks[i][30]).toFixed(2);
            //increases variable
            totalExternalJobsHours += tasks[i][30];

            templateContent.querySelector(".ExternalJobsCost").innerHTML = Math.trunc(tasks[i][12]).toFixed(2);
            //increases variable
            totalExternalJobsCost += tasks[i][12];

            templateContent.querySelector(".VariouseMaterialsCost").innerHTML = Math.trunc(tasks[i][13]).toFixed(2);    
            //increases variable
            totalMaterialCost += tasks[i][13];

            templateContent.querySelector(".TransfertCost").innerHTML = Math.trunc(tasks[i][11]).toFixed(2);      
            //increases variable
            totalTransfertCost += tasks[i][11];
        }
        if( app.user_role === "admin" || app.user_role === "consultant" ){
            //operator total hours
            //Since in the DB the readTasks result is ordered by operator_id it is possible
            //to compute total operator hours by reading the operator id.
            //this feature is only for adminstrator users
            //if there is only one element in array just take it and writes data
            if( tasks.length === 1 )
            {
                templateContent.querySelector(".TotalHours").innerHTML = tasks[i][5];
                //increases operator hours variable
                operatorTotalHours += tasks[i][5];
                totalHoursCost += operatorTotalHours * Math.trunc(tasks[i][29]).toFixed(2);
            }

            else if( (i+1) <  tasks.length  )
            {
                //increases operator hours variable
                operatorTotalHours += tasks[i][5];
                
                
                //compares operators if different writes the data and empties the variable
                if( tasks[i][2] !== tasks[i+1][2]  )
                {
                    templateContent.querySelector(".TotalHours").innerHTML = operatorTotalHours;
                    //increments total hours cost using the operator total before resetting the operatorTotalHours variable
                    totalHoursCost += operatorTotalHours * Math.trunc(tasks[i][29]).toFixed(2);
                    operatorTotalHours = 0;
                }
                
            }
            //arrived at last element writes current value int operator hors and inserts the last row with totals
            else if( (i+1) === tasks.length ) 
            {
                operatorTotalHours += tasks[i][5];
                totalHoursCost += operatorTotalHours * Math.trunc(tasks[i][29]).toFixed(2);
                templateContent.querySelector(".TotalHours").innerHTML = operatorTotalHours;
            }
        }
        
        //increases total hours variable
        totalHours += tasks[i][5];

        //adds rows to the table
        detailedTableBody.appendChild(templateContent); 
    }
    
    //total row for operators
    if( app.user_role === "operator" )
    {
        //creates the last row
        let lastRow = document.createElement("tr");

        let rowCells = 11;//row cells number

        //adds cells and contents in the last row
        for( var i = 0; i < rowCells; i++)
        {
            var cell = document.createElement("td");

            //fills cell if necessary
            if( i === 0 )
                cell.innerHTML = "Totale";
            else if( i === 10 )
                cell.innerHTML = totalHours;

            //adds cell in the row
            lastRow.appendChild(cell);
        }
        //adds last row to the table
        detailedTableBody.appendChild(lastRow);  
    }

    //totals row only for adminstrators
    if( app.user_role === "admin")
    {
        //creates the last row
        let lastRow = document.createElement("tr");


        let rowCells = 18;//row cells number

        //adds cells and contents in the last row
        for( var i = 0; i < rowCells; i++)
        {
            var cell = document.createElement("td");

            //fills cell if necessary
            if( i === 0 )
                cell.innerHTML = "Totale";
            else if( i === 10 )
                cell.innerHTML = totalHours;
            else if( i === 11 )
                cell.innerHTML = totalHoursCost.toFixed(2);
            else if( i === 12 )
                cell.innerHTML = Math.trunc(totalTranslationsCost).toFixed(2);     
            else if( i === 13 )
                cell.innerHTML = Math.trunc(totalTranslationsPrice).toFixed(2); 
            else if( i === 14 )
                cell.innerHTML = Math.trunc(totalExternalJobsHours).toFixed(2); 
            else if( i === 15 )
                cell.innerHTML = Math.trunc(totalExternalJobsCost).toFixed(2); 
            else if( i === 16 )
                cell.innerHTML = Math.trunc(totalMaterialCost).toFixed(2); 
            else if( i === 17 )
                cell.innerHTML = Math.trunc(totalTransfertCost).toFixed(2); 

            //adds cell in the row
            lastRow.appendChild(cell);
        }

        //adds last row to the table
        detailedTableBody.appendChild(lastRow);  
        
        //At th end of the table adds General Total Row
        const generalTotalRow = document.createElement("tr");
        const titleCell = document.createElement("td");
        titleCell.innerHTML = "TOTALE GENERALE A+B+C+D+E";
        generalTotalRow.appendChild(titleCell);
        const generalTotalCost = (totalHoursCost + totalTranslationsCost + totalExternalJobsCost + totalMaterialCost+totalTransfertCost ).toFixed(2);
        const valueCell = document.createElement("td");
        valueCell.innerHTML = generalTotalCost;
        generalTotalRow.appendChild(valueCell);
        detailedTableBody.appendChild(generalTotalRow);  
    }
    
    if( app.user_role === "consultant")
    {
        //creates the last row
        let lastRow = document.createElement("tr");

        let rowCells = 12;//row cells number

        //adds cells and contents in the last row
        for( var i = 0; i < rowCells; i++)
        {
            var cell = document.createElement("td");

            //fills cell if necessary
            if( i === 10 )
                cell.innerHTML = "Totale";
            else if( i === 11 )
                cell.innerHTML = totalHours;

            //adds cell in the row
            lastRow.appendChild(cell);
        }

        //adds last row to the table
        detailedTableBody.appendChild(lastRow);   
    }
};


/**
 * Closure to preserve the order id related to each table row.
 * Opens the order details page.
 * @param {type} task_id
 * @returns {Function}
 */
app.setCurrentTaskId = function( task_id )
{
    return function()
    {
        //window.open("customer_details.jsp?customer_id="+customer_id,'_self');
        window.open("task_details.jsp?task_id=" + task_id,'_self');
    };
};
    
/* TASKS DETAILS PAGE */

app.getTaskDetailsPage = function( task_id, user_id )
{
    app.deleteNullTaskAttachments( task_id, user_id,
            function(task_id)
            {
                app.getTaskAttachmentsTable(task_id);
            },
            function()
            {
                alert('Aggiornamento allegati non riuscito!');
            });
};

/****** CHECK VALIDITY FILEDS UTILITIES ***/

//checks prices validity
app.validPrice = function( field_id, fieldName )
{
    //message to be returned
    var message="";
    var priceString  = document.getElementById(field_id).value;
    //tries parse float
    if( !isNaN(parseFloat(priceString)) )
    {
        //truncates decimals
        //shifts right two times the decimal point
        var priceNumber = parseFloat(priceString)*100;
        //truncates all decimals
        priceNumber = Math.floor(priceNumber);
        //shifts left two times
        priceNumber /= 100;
        document.getElementById(field_id).value = priceNumber;
    }
    else
    {
        document.getElementById(field_id).value = 0.0;
        message = " Formato " + fieldName + " non valido.\n";
    }
    return message;
};

app.validDate = (field_id, fieldName)=>{
    var message = "";
     //retrieves the value
    var dateString = document.getElementById(field_id).value;
    
    if( dateString === null || dateString === "" )
        message = "Inserisci una Data Lavorazione Valida.\n";
    
    return message;
};

//checks time values
app.validHours = function( field_id, fieldName )
{
    var message = "";
     //retrieves the value
    var hourString = document.getElementById(field_id).value;
    //removes the dot
    hourString = hourString.replace( ".", '');
    hoursFloat = parseFloat(hourString);
    //tries to parse the number
    if( !isNaN(hoursFloat)  )
    {
        //alert("parsable");
        //takes again the valur and checks the range
        hourString = document.getElementById(field_id).value;
        //takes the value
        var hoursFloat = parseFloat(hourString);
        //alert("parsed to" + hoursFloat);
        //checks the range
        if( 0 <= hoursFloat &&  hoursFloat  <= 12 || field_id === 'external_jobs_hours_input')
        {
            //alert('in range');
            if(hoursFloat !== 12 )
            {
                //takes the decimal part an round it
                var integer = Math.floor(hoursFloat);
                //alert("integer: " + integer );
                var decimal = hoursFloat - integer; 
                //alert("decimal" + decimal );
                if( decimal !== 0 && decimal <= 0.25 )
                {
                    //alert("first if" + decimal );
                    decimal = 0.25;
                    //alert("first if" + decimal );
                }
                else if ( decimal > 0.25 && decimal <= 0.5 )
                {
                    //alert("second if" + decimal );
                    decimal = 0.5;
                    //alert("second if" + decimal );
                }
                else if( decimal > 0.5 && decimal <= 0.75 )
                {
                    //alert("third if" + decimal );
                    decimal = 0.75;
                    //alert("third if" + decimal );
                }
                else if( decimal  > 0.75 )
                {
                    //alert("forth if" + decimal );
                    decimal = 0.75;
                    //alert("forth if" + decimal );
                }
                //rebuids the number
                hoursFloat = integer + decimal;
                //alert("result " + hoursFloat );
                //writes the value in the field
                document.getElementById(field_id).value = hoursFloat;
            }
            else
            {
                //if the value is 12 leaves it like that
                document.getElementById(field_id).value = hoursFloat;
                //alert("result " + hoursFloat );
            }

        }
        else
        {
            message = 'Valore ' + fieldName + ' ore inserito non valido. Deve essere compreso tre 0 e 12.\n';
            document.getElementById(field_id).value = 0.0;
        }
    }
    else
    {
        message = 'Valore ' + fieldName + ' ore inserito non valido.\n';
        document.getElementById(field_id).value = 0.0;
    }
    return message;
};

//checks integer values
app.validInteger = function( field_id, fieldName )
{
    var message = "";
    //retireves the value
    var transfertKilometersString = document.getElementById(field_id).value;
    //tries parse float
    if( isNaN(parseInt(transfertKilometersString)) )
    {
        message = 'Valore ' + fieldName + ' non valido!\n';
        document.getElementById(field_id).value = 0;
    }
    return message;
};

/*checks if tin database there is an order having the order-code inserted by the user*/
app.validOrderCode = function()
{
    var message = "";
    //retrieves order details by order_code and update in jsp page the hidden fiels order_id and order_code
    var order_code = document.getElementById('order_code_input').value;
    if(order_code !== "" && order_code !== undefined )
    {
        //order_id, creator_id, order_code, completion_state_id, customerDenominationHint, machinaryModelHint, jobTypeHint, fromDate, toDate, successCallback, failCallback )
        app.readOrders(null, null, order_code,  null, null, null, null, null,null,null,null,
            function(orders)
            {
                if( orders.length === 1 )
                {
                    //message continues to be equal to "";
                }
                else
                    message = "Valore Codice Ordine non valido.\n";
            },
            function()
            {
                message = "Si è verificato un problema nella lettura del codice.\n";
            }
        );
    }
    else
        message = "Inserisci un codice lavoro valido\n";
    
    return message;
};


app.uploadTaskAttachment = function(task_id, user_id)
{
    app.taskUpload( task_id, 
    function()
    {
        //FILLS TASKS TABLE
        app.getTaskAttachmentsTable(task_id);
    },
    function()
    {
        window.alert("Attachment Looading Error!");
    });
};


app.getTaskAttachmentsTable = function( task_id )
{
    
    /**
     * readAuthos is a method located in dbi.js. It takes three parameters
     * denominationHint
     * successCallback
     * failCallback
     */
    app.readTaskAttachments(
        task_id,
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
    var itemTemplate = document.querySelector("#task_attachments_template");
    // gets the table body that will be the container of template instances 
    var itemsContainer = document.querySelector("#attachments tbody");
    //empty the current content in the table
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
        templateContent.querySelector(".TaskRow").id = "task_row_"+attachments[i][0];
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
        templateContent.querySelector("#date_"+attachments[i][0]).textContent = attachments[i][1];
        templateContent.querySelector("#name_"+attachments[i][0]).textContent = attachments[i][2];
      
        
        // DEFINIZIONE DELLE CALLBACK
        //execBtn.onclick = function() { runCommand() };
        //templateContent.querySelector("#deleteBtn_"+attachments[i][0]).setAttribute( 'oncllick', 'app.deleteAttachment( attachments[i][0]);');
        templateContent.querySelector("#deleteBtn_"+attachments[i][0]).onclick = app.deleteAttachment(attachments[i][0], attachments[i][3]);
        //templateContent.querySelector("#order_row_"+app.orders[i][0]).onclick = app.setCurrentOrderIdCustomerId(app.orders[i][0], app.orders[i][1]);
        templateContent.querySelector("#showBtn_"+attachments[i][0]).setAttribute('href',"resources/TaskAttachments/"+attachments[i][2]);
        
        itemsContainer.appendChild(templateContent); 
    } 

};

/*closure*/
app.deleteAttachment = function( attachment_id, task_id )
{
    return function()
    {
        app.deleteTaskAttachment( attachment_id,
            function()
            {
                alert('Allegato Eliminato');
                app.getTaskAttachmentsTable(task_id);
            },
            function()
            {
                alert('Non Riesco ad Eliminare l\'Allegato');
            });
    };
};
    
    

console.log('END:tasks.js');