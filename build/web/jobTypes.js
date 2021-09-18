//app is global variable implemented as singleton
if(typeof app==='undefined' || app===null) app = {};

app.current_transporter_id = null;

/****** JOBTYPES PAGE *********************************/
app.getJobTypesPage = function()
{
    document.querySelector(".Footer_message").innerHTML = "sto caricando i Tipi Lavoro";
    
    app.readJobTypes(
        null,
        function( jobTypes )
        {
            app.refreshJobTypes(jobTypes);
            document.querySelector(".Footer_message").innerHTML = "Tipi Lavoro: " + jobTypes.length;
        },
        function()
        {
            document.querySelector(".Footer_message").innerHTML = "non riesco a leggere i Tipi Lavoro!";
        }
    );
};

app.refreshJobTypes = function( jobTypes )
{
    //gets the transporters table body template
    var itemTemplate = document.querySelector("#jobType_template");
    // gets the table body that will be the container of template instances 
    var itemsContainer = document.querySelector(".Table tbody");
    //empty the current content in the table
    itemsContainer.innerHTML = null;
    /**
     * Iterates customers
     */
    for(var i=0; i<jobTypes.length; i++)
    {
        var templateContent =  document.importNode(itemTemplate.content,true);
        
        //ID ASSIGNMENT 
        templateContent.querySelector("#jobType_row").id = "jobType_row_"+jobTypes[i][0];
        templateContent.querySelector("#name").id = "name_"+jobTypes[i][0];
        //templateContent.querySelector("#e_mail").id = "e_mail_"+transporters[i][0];
        
        //content fillling
        templateContent.querySelector("#name_"+jobTypes[i][0]).textContent = jobTypes[i][1];
        
        // DEFINIZIONE DELLE CALLBACK
        templateContent.querySelector("#jobType_row_"+jobTypes[i][0]).onclick = app.setCurrentJobTypeId(jobTypes[i][0]);
        itemsContainer.appendChild(templateContent); 
    }
};


/**
 * 
 * @param {type} jobType_id
 * @returns {Function}
 */
app.setCurrentJobTypeId = function( jobType_id )
    {
        return function()
        {
            window.open("jobType_details.jsp?jobType_id="+jobType_id,'_self');
        };
    };
    

app.createNewJobType = function()
{
    
    var name = document.getElementById("new_jobType_name_input").value;
    
    
    if( name === undefined || name === "")
    {
        document.querySelector(".Footer_message").innerHTML = " Campo Nome Obbligatorio.";
    }
    else
    {
        app.createJobType( name,"",
            function()
            {
                location.reload(true);
            },
            function()
            {
                document.querySelector(".Footer_message").innerHTML = "non riesco a creare il Tipo Lavoro.";
            });  
    }
};

/* TRANSPORTER DETAILS PAGE */
app.getJobTypeDetailsPage = function( jobType_id)
{
    document.querySelector(".Footer_message").innerHTML = "sto caricando i dettagli del Tipo Lavoro"+jobType_id;
    
    app.readJobTypes(
        jobType_id,
        function( jobTypes )
        {
            app.refreshJobTypeDetails(jobTypes);
            document.querySelector(".Footer_message").innerHTML = "Dettagli Tipo Lavoro";
        },
        function()
        {
            document.querySelector(".Footer_message").innerHTML = "non riesco a leggere i dettagli del Tipo Lavoro!";
        }
    );
};
    

/* Fills transporter details table   */
app.refreshJobTypeDetails = function( jobTypes )
{
      //gets the authors table body template
    var itemTemplate = document.querySelector("#jobType_details_template");
    // gets the table body that will be the container of template instances 
    var itemsContainer = document.querySelector(".Details_table tbody");
    //empty the current content in the table
    itemsContainer.innerHTML = null;
    /**
     * Only one customer in array
     */
    for(var i=0; i<jobTypes.length; i++)
    {
        var templateContent =  document.importNode(itemTemplate.content,true);
        
        //content fillling
        templateContent.querySelector("#name_input").value = jobTypes[i][1];
        templateContent.querySelector("#description_input").value = jobTypes[i][2];
        
        itemsContainer.appendChild(templateContent);  
    }
};

/*Makes visible the update button and changes the look of the cell that's been changed*/
app.jobTypeDetailsChanged = function(id)
{
    document.getElementById('updateJobTypeButton').classList.add('Visible');
    document.querySelector(id).classList.add('Changed');
};


app.jobTypeUpdated = function( jobType_id )
{
    var name = document.querySelector("#name_input").value;
    var description = document.querySelector("#description_input").value;

    if( name === undefined || name === "")
    {
        alert(" Campo Nome Obbligatorio.");
    }
    else
    {
        app.updateJobType( jobType_id, name, description, 
            function( )
            {
                location.reload();
            },
            function()
            {
                document.querySelector(".Footer_message").innerHTML = "non riesco a modificare il VETTORE!";
            }
        );
    }
};

    
console.log("jobTypes.js");