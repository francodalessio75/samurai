//app is global variable implemented as singleton
if (typeof app === 'undefined' || app === null)
    app = {};


app.orders = null;
app.customers = null;
app.customerMachinaries = null;
app.html = "";

console.log("order.js");

/****** CUSTOMER PAGE *********************************/
app.getOrdersPage = function ()
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    document.querySelector(".Footer_message").innerHTML = "sto caricando i LAVORI";

    var fromDate = document.getElementById('from_date').value;
    var toDate = document.getElementById('to_date').value;
    /**
     * readOrders is a method located in dbi.js. It takes four parameters
     * order_id
     * creator_id
     * completion_state_id
     * customerDenominationHint
     * machinaryModelHint
     * jobTypeHint
     * fromDate
     * toDate
     order_id, creator_id, order_code, completion_state_id, customerDenominationHint, machinaryModelHint, jobTypeHint, fromDate, toDate, successCallback, failCallback
     * successCallback
     * failCallback
     */
    app.readOrders(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            fromDate,
            toDate,
            function (orders)
            {
                app.orders = orders;
                app.refreshOrders();
                //fills machinary models filter
                //app.getMachinaryModelsByCustomerId( "machinary_model_select_options" );
                //app.getMachinaryModelsByCustomerId( "machinary_models_datalist" );
                document.querySelector(".Footer_message").innerHTML = "LAVORI : " + orders.length;
            },
            function ()
            {
                document.querySelector(".Footer_message").innerHTML = "non riesco a leggere i LAVORI!";
            }
    );
};

app.refreshOrders = function ()
{
    //gets the customers table body template
    var itemTemplate = document.querySelector("#order_template");
    // gets the table body that will be the container of template instances 
    var itemsContainer = document.querySelector(".Table tbody");
    //empty the current content in the table
    itemsContainer.innerHTML = null;

    for (var i = 0; i < app.orders.length; i++)
    {
        var templateContent = document.importNode(itemTemplate.content, true);

        //ID ASSIGNMENT 
        templateContent.querySelector("#order_row").id = "order_row_" + app.orders[i][0];
        templateContent.querySelector("#code").id = "code_" + app.orders[i][0];
        templateContent.querySelector("#date").id = "date_" + app.orders[i][0];
        templateContent.querySelector("#customer_denomination").id = "customer_denomination_" + app.orders[i][0];
        templateContent.querySelector("#machinary_model").id = "machinary_model_" + app.orders[i][0];
        templateContent.querySelector("#job_type").id = "job_type_" + app.orders[i][0];
        templateContent.querySelector("#serial_number").id = "serial_number_" + app.orders[i][0];
        templateContent.querySelector("#completion_state").id = "completion_state_" + app.orders[i][0];

        //content fillling
        templateContent.querySelector("#code_" + app.orders[i][0]).textContent = app.orders[i][2];
        templateContent.querySelector("#date_" + app.orders[i][0]).textContent = app.orders[i][3];
        templateContent.querySelector("#customer_denomination_" + app.orders[i][0]).textContent = app.orders[i][4];
        templateContent.querySelector("#machinary_model_" + app.orders[i][0]).textContent = app.orders[i][12];
        templateContent.querySelector("#job_type_" + app.orders[i][0]).textContent = app.orders[i][7];
        templateContent.querySelector("#completion_state_" + app.orders[i][0]).textContent = app.orders[i][13];
        templateContent.querySelector("#serial_number_" + app.orders[i][0]).textContent = app.orders[i][15];

        // DEFINIZIONE DELLE CALLBACK
        templateContent.querySelector("#order_row_" + app.orders[i][0]).onclick = app.setCurrentOrderIdCustomerId(app.orders[i][0], app.orders[i][1]);
        itemsContainer.appendChild(templateContent);
    }
};

app.getCustomerMachinaries = function (element_id)
{
    app.html = "";
    //fills filter options
    if (element_id === "machinary_model_select_options")
        var customer_id = document.getElementById('customer_select_options').value;
    //fills new order machinary model datalist
    if (element_id === "machinary_models_datalist")
        var customer_id = document.getElementById('new_customer_select_options').value;


    app.getMachinaryModelsByCustomerId(customer_id,
            function ()
            {
                app.machinaryModelsByCustomerId(customer_id,
                        function (machinaryModels)
                        {
                            app.customerMachinaries = machinaryModels;
                            //select "placeholder" for filter
                            if (element_id === "machinary_model_select_options")
                                app.html = '<option value="" >DESCRIZIONE LAVORO</option>';

                            //fills options
                            for (i = 0; i < machinaryModels.length; i++)
                                app.html = app.html.concat("<option>" + machinaryModels[i].toString() + "</option>");

                            var select = document.getElementById(element_id);
                            select.innerHTML = app.html;
                        },
                        function ()
                        {
                            document.querySelector(".Footer_message").innerHTML = "non riesco a filtarare le Descrizioni Lavoro.";
                        }
                );
            });

};
//to make the execution sinchronouse I've innested two methods, in this way 
//first customerMachinaries is updated then the select menu is populated
app.getMachinaryModelsByCustomerId = function (customer_id, callBack)
{
    callBack(customer_id);
};



/**
 * Closure to preserve the order id related to each table row.
 * Opens the order details page.
 * @param {type} order_id
 * @param {type} customer_id
 * @returns {Function}
 */
app.setCurrentOrderIdCustomerId = function (order_id, customer_id)
{
    return function ()
    {
        //window.open("customer_details.jsp?customer_id="+customer_id,'_self');
        window.open("order_details.jsp?order_id=" + order_id + "&customer_id=" + customer_id, '_self');
    };
};

/* Create a new order*/  /*********************** ADD CHECKS FOR NEW CUSTOMER VALIDATION *************************/


/**
 * Long customer_id, Long user_id, String jobType, String date, String machinaryModel, String notes
 * @param {type} user_id
 * @returns {undefined}
 */
app.createNewOrder = function (user_id)
{
    var message = "";
    var customer_id = document.getElementById("new_customer_select_options").value;
    var jobType_id = document.getElementById("new_job_type_select_options").value;
    var machinaryModel = document.getElementById("new_order_machinary_model").value;
    var serialNumber = document.getElementById("new_order_serial_number").value;
    //checks for customer
    if (customer_id === "" || customer_id === undefined)
    {
        message = "Cliente non selezionato.\n";
    }
    if (jobType_id === "" || jobType_id === undefined)
    {
        message = message.concat("Tipo Lavoro non selezionato.\n");
    }
    if (machinaryModel === "" || machinaryModel === undefined)
    {
        message = message.concat("Inserisci una Descrizione Lavoro.\n");
    }
    if (message !== "")
        alert(message);
    else
    {
        var date = document.getElementById("new_order_date").value;
        var notes = document.getElementById("new_order_notes").value;

        app.createOrder(customer_id, user_id, jobType_id, serialNumber, date, machinaryModel, notes,
                function ()
                {
                    location.reload(true);
                },
                function ()
                {
                    document.querySelector(".Footer_message").innerHTML = "non riesco a creare il Lavoro.";
                });
    }
};

/* ORDER DETAILS PAGE */

/*Makes visible the update button and changes the look of the cell that's been changed,
 if the change is about completion state cheks if the oder has been set has completed.
 if yes set the order as suggested for autocompletion for delivery notes and invoices*/
app.orderDetailsChanged = function (field_id)
{
    document.getElementById('updateOrderButton').classList.add('Visible');
    document.querySelector(field_id).classList.add('Changed');
    if (field_id === '#suggested_checkbox')
        document.querySelector(field_id).parentNode.classList.add('Changed');
};

/**/
app.orderUpdated = function (order_id)
{
    document.querySelector(".Footer_message").innerHTML = "sto modificando il Lavoro...";
    var message = "";

    var job_type_id = document.querySelector("#job_type_select_options").value;
    var ordine = document.querySelector("#ordine_input").value;
    var commessa = document.querySelector("#commessa_input").value;
    var storyData = document.querySelector("#story_data_input").value;
    var serialNumber = document.querySelector("#serial_number_input").value;

    if (job_type_id === "" || job_type_id === undefined)
    {
        message = message.concat(" per modificare è necessario selezionare un tipo lavoro.\n");
        document.querySelector(".Footer_message").innerHTML = message;
    } else if (ordine.length > 20)
    {
        message = message.concat("Il campo ordine può contenere massimo 20 caratteri.\n");
        window.alert(message);
        document.querySelector(".Footer_message").innerHTML = message;
    } else if (commessa.length > 100)
    {
        message = message.concat("Il campo commessa può contenere massimo 100 caratteri.\n");
        window.alert(message);
        document.querySelector(".Footer_message").innerHTML = message;
    } else
    {
        var completion_state_id = document.querySelector("#completion_state_select_options").value;
        var customer_id = document.querySelector("#customer_select_options").value;
        var machinaryModel = document.querySelector("#machinary_model_input").value;
        var notSuggest = document.querySelector('#suggested_checkbox').checked;
        var notes = document.querySelector("#notes_input").value;
        var dataOrdine = document.getElementById("dataOrdine_input").value;

        //var availability_id = document.querySelector("#availability_options").value; **it has been eliminated after the introduction of storyData field in dyn_Orders table**

        ///order_id, customer_id, user_id, jobType_id, completionState_id, date, code, machinaryModel, notes, closingReason, successCallback, failCallback 
        app.updateOrder(order_id, customer_id, null, job_type_id, completion_state_id, null, null, machinaryModel, notes, notSuggest, ordine, commessa, dataOrdine, storyData, serialNumber,
                function ( )
                {
                    location.reload();
                },
                function ()
                {
                    document.querySelector(".Footer_message").innerHTML = "non riesco a modificare il lavoro!";
                });
    }
};

/**
 * 
 */
app.jobTypeDatalistonChange = function (jobTypes)
{
    var jobTypeHint = document.getElementById("job_type_datalist_input").value;
    //iterates the array and if find a value containing th hint update input value and asks from the iteration
    //if doesn't find any valid value empties the input
    for (var i = 0; i < jobTypes.length; i++)
    {
        if (jobTypes[i].jobType.toLowerCase().includes(jobTypeHint.toLowerCase()))
        {
            document.getElementById("jobTypeHint").value = jobTypes[i].jobType;
            break;
        } else
            document.getElementById("job_type_datalist_input").value = "";
    }
};


//Long order_id, Long creator_id, Long completion_state_id, String customerDenominationHint, String fromDate, String toDate
app.filterOrders = function ()
{
    var orderCode = document.getElementById('order_code_Hint').value;
    
    var serialNumber = document.getElementById('serial_number_Hint').value;

    var customer_idString = document.getElementById('customer_select_options').value;

    var orderDescription_idString = document.getElementById('search_order_description').value;

    //var machinaryModelHint = document.getElementById('machinary_model_select_options').value;

    var jobType_idString = document.getElementById('job_type_select_options').value;

    var fromDate = document.getElementById('from_date').value;
    var toDate = document.getElementById('to_date').value;

    var completion_state_id = document.getElementById('status').value;

//  var availability_id = document.getElementById('availability').value;

    //if status is -1 means all completion states
    if (completion_state_id === "-1")
    {
        completion_state_id = null;
    }

    //id orderCode is not empty just empty all other criteria and serch the order only by orderCode first checks 
    //if the order exists
    //order_id, creator_id, order_code, order_description,completion_state_id, serial_number, availability_id, customer_idString, machinaryModelHint, jobType_idString, fromDate, toDate, successCallback, failCallback )
    orderCode = orderCode === "" ? null : orderCode;
    serialNumber = serialNumber === "" ? null : serialNumber;
    if (orderCode !== null )
    {
        app.readOrders(
                null,
                null,
                orderCode,
                orderDescription_idString,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                function (orders)
                {
                    //checks if the order exists
                    if (orders.length === 1)
                    {
                        app.orders = orders;
                        app.refreshOrders();
                        document.querySelector(".Footer_message").innerHTML = "LAVORI FILTRATI: " + orders.length;
                    } else
                    {
                        window.alert(" codice non troavato!");
                    }
                },
                function ()
                {
                    document.querySelector(".Footer_message").innerHTML = "non riesco a filtrare i LAVORI!";
                }
        );
    } else if ( serialNumber !== null){
        app.readOrders(
                null,
                null,
                null,
                orderDescription_idString,
                null,
                serialNumber,
                null,
                null,
                null,
                null,
                null,
                null,
                function (orders)
                {
                    //checks if the order exists
                    if (orders.length === 1)
                    {
                        app.orders = orders;
                        app.refreshOrders();
                        document.querySelector(".Footer_message").innerHTML = "LAVORI FILTRATI: " + orders.length;
                    } else
                    {
                        window.alert(" codice non troavato!");
                    }
                },
                function ()
                {
                    document.querySelector(".Footer_message").innerHTML = "non riesco a filtrare i LAVORI!";
                }
        );
    }
    
    else
    {
        /**
         * readOrders is a method located in dbi.js. It takes four parameters
         * order_id
         * creator_id
         * completion_state_id
         * customerDenominationHint
         * machinaryModelHint
         * jobTypeHint
         * fromDate
         * toDate
         * order_id, creator_id, order_code, completion_state_id, customerDenominationHint, machinaryModelHint, jobTypeHint, fromDate, toDate, successCallback, failCallback
         * successCallback
         * failCallback
         */
        app.readOrders(
                null,
                null,
                null,
                orderDescription_idString,
                completion_state_id,
                serialNumber,
                null,
                customer_idString,
                null,
                jobType_idString,
                fromDate,
                toDate,
                function (orders)
                {
                    app.orders = orders;
                    app.refreshOrders();
                    document.querySelector(".Footer_message").innerHTML = "LAVORI FILTRATI: " + orders.length;
                },
                function ()
                {
                    document.querySelector(".Footer_message").innerHTML = "non riesco a filtrare i LAVORI!";
                }
        );
    }
};

//when the user changes the completion state of an order also the checkbox to suggest the 
//order on deliverynote/invoice coul change. The rules are:
//if the completion state is inprogress the check box to not suggest the order is checked and 
//disbled while if the completion state is switched from  in progress to completed the checkbox will 
//be enabled and it'll be not checked by default. It is always allowed to change the check box state provided that the completion 
//state is completed
app.setSuggestion = function ( )
{
    //if is completed enables the checkbox
    if (document.getElementById("completion_state_select_options").value === "2")
    {
        document.getElementById("suggested_checkbox").disabled = false;
        document.getElementById("suggested_checkbox").checked = false;
    }
    //if is in progress is checked and disabled
    else
    {
        document.getElementById("suggested_checkbox").disabled = true;
        document.getElementById("suggested_checkbox").checked = true;
    }
};

app.printCover = function (order_id)
{
    app.adjurnCover(
            order_id,
            () =>
    {
        window.open("resources/ORDER_COVERS/ORDER_COVER_" + order_id + ".pdf", "_blank");
    },
            () =>
    {
        document.querySelector(".Footer_message").innerHTML = "non riesco a stampare la cartella.";
    }
    );
};


console.log("order.js");