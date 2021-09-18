//app is global variable implemented as singleton
if(typeof app==='undefined' || app===null) app = {};

app.current_transporter_id = null;

/****** TRANSPORTER PAGE *********************************/
app.getTransportersPage = function()
{
    document.querySelector(".Footer_message").innerHTML = "sto caricando i VETTORI";
    /**
     * readTransporters is a method located in dbi.js. It takes four parameters
     * transporter_id
     * denominationHint
     * successCallback
     * failCallback
     */
    app.readTransporters(
        null,
        null,
        function( transporters )
        {
            app.refreshTransporters(transporters);
            document.querySelector(".Footer_message").innerHTML = "VETTORI: " + transporters.length;
        },
        function()
        {
            document.querySelector(".Footer_message").innerHTML = "non riesco a leggere i VETTORI!";
        }
    );
};

app.refreshTransporters = function( transporters )
{
    //gets the transporters table body template
    var itemTemplate = document.querySelector("#transporter_template");
    // gets the table body that will be the container of template instances 
    var itemsContainer = document.querySelector(".Table tbody");
    //empty the current content in the table
    itemsContainer.innerHTML = null;
    /**
     * Iterates customers
     */
    for(var i=0; i<transporters.length; i++)
    {
        var templateContent =  document.importNode(itemTemplate.content,true);
        
        //ID ASSIGNMENT 
        templateContent.querySelector("#transporter_row").id = "transporter_row_"+transporters[i][0];
        templateContent.querySelector("#denomination").id = "denomination_"+transporters[i][0];
        templateContent.querySelector("#address").id = "address_"+transporters[i][0];
        //templateContent.querySelector("#e_mail").id = "e_mail_"+transporters[i][0];
        
        //content fillling
        templateContent.querySelector("#denomination_"+transporters[i][0]).textContent = transporters[i][3];
        templateContent.querySelector("#address_"+transporters[i][0]).textContent = transporters[i][9]+", "+transporters[i][10]+" "+transporters[i][11]+" "+transporters[i][8]+" "+transporters[i][12];
        //templateContent.querySelector("#e_mail_"+transporters[i][0]).textContent = transporters[i][7];
        
        // DEFINIZIONE DELLE CALLBACK
        templateContent.querySelector("#transporter_row_"+transporters[i][0]).onclick = app.setCurrentTransporterId(transporters[i][0]);
        itemsContainer.appendChild(templateContent); 
    }
};
/**
 * Closure to preserve the transporter id related to each table row.
 * Opens the transporter details page.
 * @param {type} customer_id
 * @returns {Function}
 */
app.setCurrentTransporterId = function( transporter_id )
    {
        return function()
        {
            window.open("transporter_details.jsp?transporter_id="+transporter_id,'_self');
        };
    };
    
/* Create a new transporter*/  /*********************** ADD CHECKS FOR NEW TRANSPORTER VALIDATION *************************/
app.createNewTransporter = function()
{
    
    var denomination = document.getElementById("new_transporter_denomination_input").value;
    
    
    if( denomination === undefined || denomination === "")
    {
        document.querySelector(".Footer_message").innerHTML = " Campo Denominazione Obbligatorio.";
    }
    else
    {
        app.createTransporter( denomination,null,
            function()
            {
                location.reload(true);
            },
            function()
            {
                document.querySelector(".Footer_message").innerHTML = "non riesco a creare il VETTORE.";
            });  
    }
};

/* TRANSPORTER DETAILS PAGE */
app.getTransporterDetailsPage = function( transporter_id)
{
    document.querySelector(".Footer_message").innerHTML = "sto caricando i dettagli del vettore"+transporter_id;
    
    app.readTransporters(
        transporter_id,
        null,
        function( transporters )
        {
            app.refreshTransporterDetails(transporters);
            document.querySelector(".Footer_message").innerHTML = "Dettagli VETTORE";
        },
        function()
        {
            document.querySelector(".Footer_message").innerHTML = "non riesco a leggere i dettagli VETTORE!";
        }
    );
};
    

/* Fills transporter details table   */
app.refreshTransporterDetails = function( trasnporters )
{
      //gets the authors table body template
    var itemTemplate = document.querySelector("#transporter_details_template");
    // gets the table body that will be the container of template instances 
    var itemsContainer = document.querySelector(".Details_table tbody");
    //empty the current content in the table
    itemsContainer.innerHTML = null;
    /**
     * Only one customer in array
     */
    for(var i=0; i<trasnporters.length; i++)
    {
        var templateContent =  document.importNode(itemTemplate.content,true);
        
        //content fillling
        templateContent.querySelector("#denomination_input").value = trasnporters[i][3];
        //templateContent.querySelector("#vat_code_input").value = trasnporters[i][1];
        //templateContent.querySelector("#fiscal_code_input").value = trasnporters[i][2];
        //templateContent.querySelector("#phone_input").value = trasnporters[i][4];
        //templateContent.querySelector("#fax_input").value = trasnporters[i][5];
        //templateContent.querySelector("#mobile_input").value = trasnporters[i][6];
        //templateContent.querySelector("#e_mail_input").value = trasnporters[i][7];
        templateContent.querySelector("#city_input").value = trasnporters[i][8];
        templateContent.querySelector("#address_input").value = trasnporters[i][9];
        templateContent.querySelector("#house_number_input").value = trasnporters[i][10];
        templateContent.querySelector("#postal_code_input").value = trasnporters[i][11];
        templateContent.querySelector("#province_input").value = trasnporters[i][12];
        templateContent.querySelector("#notes_input").value = trasnporters[i][13];
        
        itemsContainer.appendChild(templateContent);  
    }
};

/*Makes visible the update button and changes the look of the cell that's been changed*/
app.transporterDetailsChanged = function(id)
{
    document.getElementById('updateTransporterButton').classList.add('Visible');
    document.querySelector(id).classList.add('Changed');
};

/* Add data validation cheks !!!!!!!!*/
app.transporterUpdated = function( transporter_id )
{
    var denomination = document.querySelector("#denomination_input").value;
    //var vat_code = document.querySelector("#vat_code_input").value;
    //var fiscal_code = document.querySelector("#fiscal_code_input").value;
    //var phone = document.querySelector("#phone_input").value;
    //var fax = document.querySelector("#fax_input").value;
    //var mobile = document.querySelector("#mobile_input").value;
    //var e_mail = document.querySelector("#e_mail_input").value;
    var city = document.querySelector("#city_input").value;
    var address = document.querySelector("#address_input").value;
    var house_number = document.querySelector("#house_number_input").value; 
    var postal_code = document.querySelector("#postal_code_input").value; 
    var province = document.querySelector("#province_input").value; 
    var notes = document.querySelector("#notes_input").value; 
    
    app.updateTransporter(transporter_id, null, null, denomination, null, null, null, null, city, address, house_number, postal_code, province, notes, 
        function( )
        {
            location.reload();
        },
        function()
        {
            document.querySelector(".Footer_message").innerHTML = "non riesco a modificare il VETTORE!";
        }
    );
    
};

    
console.log("transporters.js");