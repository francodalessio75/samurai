//app is global variable implemented as singleton
if(typeof app==='undefined' || app===null) app = {};

app.current_translations_center_id = null;

/****** TRANSPORTER PAGE *********************************/
app.getTranslationsCentersPage = function()
{
    document.querySelector(".Footer_message").innerHTML = "sto caricando i CENTRI TRADUZIONE";
    /**
     * readTranslationsCenters is a method located in dbi.js. It takes four parameters
     * translations_center_id
     * denominationHint
     * successCallback
     * failCallback
     */
    app.readTranslationsCenters(
        null,
        null,
        function( translationsCenters )
        {
            app.refreshTranslationsCenters(translationsCenters);
            document.querySelector(".Footer_message").innerHTML = "CENTRI TRADUZIONE: " + translationsCenters.length;
        },
        function()
        {
            document.querySelector(".Footer_message").innerHTML = "non riesco a leggere i CENTRI TRADUZIONE!";
        }
    );
};

app.refreshTranslationsCenters = function( translationsCenters )
{
    //gets the translationsCenters table body template
    var itemTemplate = document.querySelector("#translations_center_template");
    // gets the table body that will be the container of template instances 
    var itemsContainer = document.querySelector(".Table tbody");
    //empty the current content in the table
    itemsContainer.innerHTML = null;
    /**
     * Iterates customers
     */
    for(var i=0; i<translationsCenters.length; i++)
    {
        var templateContent =  document.importNode(itemTemplate.content,true);
        
        //ID ASSIGNMENT 
        templateContent.querySelector("#translations_center_row").id = "translations_center_row_"+translationsCenters[i][0];
        templateContent.querySelector("#denomination").id = "denomination_"+translationsCenters[i][0];
       
        
        //content fillling
        templateContent.querySelector("#denomination_"+translationsCenters[i][0]).textContent = translationsCenters[i][1];
        
        // DEFINIZIONE DELLE CALLBACK
        templateContent.querySelector("#translations_center_row_"+translationsCenters[i][0]).onclick = app.setCurrentTranslationsCenterId(translationsCenters[i][0]);
        itemsContainer.appendChild(templateContent); 
    }
};
/**
 * Closure to preserve the translationsCenter id related to each table row.
 * Opens the translationsCenter details page.
 * @param {type} customer_id
 * @returns {Function}
 */
app.setCurrentTranslationsCenterId = function( translations_center_id )
    {
        return function()
        {
            window.open("translations_center_details.jsp?translations_center_id="+translations_center_id,'_self');
        };
    };
    
/* Create a new translationsCenter*/  /*********************** ADD CHECKS FOR NEW TRANSPORTER VALIDATION *************************/
app.createNewTranslationsCenter = function()
{
    
    var denomination = document.getElementById("new_translations_center_denomination_input").value;
    
    
    if( denomination === undefined || denomination === "")
    {
        document.querySelector(".Footer_message").innerHTML = " Campo Denominazione Obbligatorio.";
    }
    else
    {
        app.createTranslationsCenter( denomination,
            function()
            {
                location.reload(true);
            },
            function()
            {
                document.querySelector(".Footer_message").innerHTML = "non riesco a creare il CENTRO TRADUZIONI.";
            });  
    }
};

/* TRANSPORTER DETAILS PAGE */
app.getTranslationsCenterDetailsPage = function( translations_center_id)
{
    document.querySelector(".Footer_message").innerHTML = "sto caricando i dettagli del CENTRO TRADUZIONI "+translations_center_id;
    
    app.readTranslationsCenters(
        translations_center_id,
        null,
        function( translationsCenters )
        {
            app.refreshTranslationsCenterDetails(translationsCenters);
            document.querySelector(".Footer_message").innerHTML = "Dettagli CENTRO TRADUZIONI";
        },
        function()
        {
            document.querySelector(".Footer_message").innerHTML = "non riesco a leggere i dettagli del CENTRO TRADUZIONI!";
        }
    );
};
    

/* Fills translations_center details table   */
app.refreshTranslationsCenterDetails = function( translationsCenters )
{
      //gets the authors table body template
    var itemTemplate = document.querySelector("#translations_center_details_template");
    // gets the table body that will be the container of template instances 
    var itemsContainer = document.querySelector(".Details_table tbody");
    //empty the current content in the table
    itemsContainer.innerHTML = null;
    /**
     * Only one customer in array
     */
    for(var i=0; i<translationsCenters.length; i++)
    {
        var templateContent =  document.importNode(itemTemplate.content,true);
        
        //content fillling
        templateContent.querySelector("#denomination_input").value = translationsCenters[i][1];
        
        templateContent.querySelector("#notes_input").value = translationsCenters[i][2];
        
        itemsContainer.appendChild(templateContent);  
    }
};

/*Makes visible the update button and changes the look of the cell that's been changed*/
app.translationsCenterDetailsChanged = function(id)
{
    document.getElementById('updateTranslationsCenterButton').classList.add('Visible');
    document.querySelector(id).classList.add('Changed');
};

/* Add data validation cheks !!!!!!!!*/
app.translationsCenterUpdated = function( translations_center_id )
{
    var denomination = document.querySelector("#denomination_input").value;

    var notes = document.querySelector("#notes_input").value; 
    
    app.updateTranslationsCenter(translations_center_id, denomination, notes, 
        function( )
        {
            location.reload();
        },
        function()
        {
            document.querySelector(".Footer_message").innerHTML = "non riesco a modificare il CENTRO TRADUZIONI!";
        }
    );
    
};

    
console.log("translationsCenters.js");