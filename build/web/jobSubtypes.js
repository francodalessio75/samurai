//app is global variable implemented as singleton
if(typeof app==='undefined' || app===null) app = {};

app.current_transporter_id = null;

/****** JOBTYPES PAGE *********************************/
app.getJobSubtypesPage = function()
{
    document.querySelector(".Footer_message").innerHTML = "sto caricando i Tipi Lavoro";
    
    app.readJobSubtypes(
        null,
        function( jobSubtypes )
        {
            app.refreshJobSubtypes(jobSubtypes);
            document.querySelector(".Footer_message").innerHTML = "Tipi Lavorazione: " + jobSubtypes.length;
        },
        function()
        {
            document.querySelector(".Footer_message").innerHTML = "non riesco a leggere i Tipi Lavorazione!";
        }
    );
};

app.refreshJobSubtypes = function( jobSubtypes )
{
    //gets the transporters table body template
    var itemTemplate = document.querySelector("#jobSubtype_template");
    // gets the table body that will be the container of template instances 
    var itemsContainer = document.querySelector(".Table tbody");
    //empty the current content in the table
    itemsContainer.innerHTML = null;
    /**
     * Iterates customers
     */
    for(var i=0; i<jobSubtypes.length; i++)
    {
        var templateContent =  document.importNode(itemTemplate.content,true);
        
        //ID ASSIGNMENT 
        templateContent.querySelector("#jobSubtype_row").id = "jobSubtype_row_"+jobSubtypes[i][0];
        templateContent.querySelector("#name").id = "name_"+jobSubtypes[i][0];
        //templateContent.querySelector("#e_mail").id = "e_mail_"+transporters[i][0];
        
        //content fillling
        templateContent.querySelector("#name_"+jobSubtypes[i][0]).textContent = jobSubtypes[i][1];
        
        // DEFINIZIONE DELLE CALLBACK
        templateContent.querySelector("#jobSubtype_row_"+jobSubtypes[i][0]).onclick = app.setCurrentJobSubtypeId(jobSubtypes[i][0]);
        itemsContainer.appendChild(templateContent); 
    }
};
/**
 * Closure to preserve the transporter id related to each table row.
 * Opens the transporter details page.
 * @param {type} customer_id
 * @returns {Function}
 */
app.setCurrentJobSubtypeId = function( jobSubtype_id )
    {
        return function()
        {
            window.open("jobSubtype_details.jsp?jobSubtype_id="+jobSubtype_id,'_self');
        };
    };
    
/* Create a new transporter*/  /*********************** ADD CHECKS FOR NEW TRANSPORTER VALIDATION *************************/
app.createNewJobSubtype = function()
{
    
    var name = document.getElementById("new_jobSubtype_name_input").value;
    
    
    if( name === undefined || name === "")
    {
        document.querySelector(".Footer_message").innerHTML = " Campo Nome Obbligatorio.";
    }
    else
    {
        app.createJobsubtype( name,"",
            function()
            {
                location.reload(true);
            },
            function()
            {
                document.querySelector(".Footer_message").innerHTML = "non riesco a creare il Tipo Lavorazione.";
            });  
    }
};

/* TRANSPORTER DETAILS PAGE */
app.getJobSubtypeDetailsPage = function( jobSubtype_id)
{
    document.querySelector(".Footer_message").innerHTML = "sto caricando i dettagli del Tipo Lavorazione "+jobSubtype_id;
    
    app.readJobSubtypes(
        jobSubtype_id,
        function( jobSubtypes )
        {
            app.refreshJobSubtypeDetails(jobSubtypes);
            document.querySelector(".Footer_message").innerHTML = "Dettagli Tipo Lavorazione";
        },
        function()
        {
            document.querySelector(".Footer_message").innerHTML = "non riesco a leggere i dettagli del Tipo Lavorazione!";
        }
    );
};
    

/* Fills transporter details table   */
app.refreshJobSubtypeDetails = function( jobSubtypes )
{
      //gets the authors table body template
    var itemTemplate = document.querySelector("#jobSubtype_details_template");
    // gets the table body that will be the container of template instances 
    var itemsContainer = document.querySelector(".Details_table tbody");
    //empty the current content in the table
    itemsContainer.innerHTML = null;
    /**
     * Only one customer in array
     */
    for(var i=0; i<jobSubtypes.length; i++)
    {
        var templateContent =  document.importNode(itemTemplate.content,true);
        
        //content fillling
        templateContent.querySelector("#name_input").value = jobSubtypes[i][1];
        templateContent.querySelector("#description_input").value = jobSubtypes[i][2];
        
        itemsContainer.appendChild(templateContent);  
    }
};

/*Makes visible the update button and changes the look of the cell that's been changed*/
app.jobSubtypeDetailsChanged = function(id)
{
    document.getElementById('updateJobSubtypeButton').classList.add('Visible');
    document.querySelector(id).classList.add('Changed');
};

/* Add data validation cheks !!!!!!!!*/
app.jobSubtypeUpdated = function( jobSubtype_id )
{
    var name = document.querySelector("#name_input").value;
    var description = document.querySelector("#description_input").value;

    if( name === undefined || name === "")
    {
        alert(" Campo Nome Obbligatorio.");
    }
    else
    {
        app.updateJobSubtype( jobSubtype_id, name, description, 
            function( )
            {
                location.reload();
            },
            function()
            {
                document.querySelector(".Footer_message").innerHTML = "non riesco a modificare il Tipo Lavorazione!";
            }
        );
    }
};

    
console.log("jobSubtypes.js");