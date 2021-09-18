//app is global variable implemented as singleton
if(typeof app==='undefined' || app===null) app = {};


/****** CUSTOMER PAGE *********************************/
app.getUsersPage = function()
{
    document.querySelector(".Footer_message").innerHTML = "sto caricando gli utenti";
    
    /* red users is in dbi.js*/
    app.readUsers(
        null,//user_id 
        null,//firstNameHint
        null,//lastNameHint
        function( users )//successCallback
        {
            app.refreshUsers( users );
            document.querySelector(".Footer_message").innerHTML = "UTENTI: " + users.length;
        },
        function()//failCallback
        {
            document.querySelector(".Footer_message").innerHTML = "non riesco a leggere gli utenti!";
        }
    );
};


app.refreshUsers = function( users )
{
    //gets the users table body template
    var itemTemplate = document.querySelector("#user_template");
    
    // gets the table body that will be the container of template instances 
    var itemsContainer = document.querySelector(".Table tbody");
    
    //empty the current content in the table
    itemsContainer.innerHTML = null;
    
    /**
     * Iterates users
     */
    for(var i=0; i<users.length; i++)
    {
        var templateContent =  document.importNode(itemTemplate.content,true);
        
        //ID ASSIGNMENT 
        templateContent.querySelector("#user_row").id = "user_row_"+users[i][0];
        templateContent.querySelector("#first_name").id = "first_name_"+users[i][0];
        templateContent.querySelector("#last_name").id = "last_name_"+users[i][0];
        templateContent.querySelector("#e_mail").id = "e_mail_"+users[i][0];
        
        //content fillling
        templateContent.querySelector("#first_name_"+users[i][0]).textContent = users[i][6];
        templateContent.querySelector("#last_name_"+users[i][0]).textContent = users[i][7];
        templateContent.querySelector("#e_mail_"+users[i][0]).textContent = users[i][10];
        
        // DEFINIZIONE DELLE CALLBACK
        templateContent.querySelector("#user_row_"+users[i][0]).onclick = app.setCurrentUserId(users[i][0]);//CLOSURE
        itemsContainer.appendChild(templateContent); 
    }
};

/*CLOSURE*/
app.setCurrentUserId = function(user_id)
{
    return function()
    {
        window.open("user_details.jsp?selected_user_id=" + user_id,'_self');
    };
};
    
/* CreateS a new user*/  
app.createNewUser = function()
{
    var firstName = document.getElementById("new_user_first_name_input").value;
    var lastName = document.getElementById("new_user_last_name_input").value;
    var username = document.getElementById("new_user_username_input").value;
    var password = document.getElementById("new_user_password_input").value;
    
    if( firstName === undefined || firstName === "" || lastName === undefined || lastName === "" || username === undefined || username === "" || password === undefined || password === "" )
    {
        alert(" I campi sono tutti obbligatori");
    }
    else
    {
        app.createUser( firstName, lastName, username, password, 
            function()
            {
                location.reload(true);
            },
            function()
            {
                document.querySelector(".Footer_message").innerHTML = "non riesco a creare l'utente. Codice errore 0004";
            });  
    }
};


/*Makes visible the update button and changes the look of the cell that's been changed*/
app.userDetailsChanged = function( field_id )
{
    document.getElementById('updateUserButton').classList.add('Visible');
    document.querySelector( field_id ).classList.add('Changed');;  
};

/* Add data validation cheks !!!!!!!!*/
app.userUpdated = function( user_id )
{
    var first_name = document.querySelector("#first_name_input").value;
    var last_name = document.querySelector("#last_name_input").value;
    var username = document.querySelector("#username_input").value;
    var password = document.querySelector("#password_input").value;
    var hourlyCost = document.querySelector("#hourlyCost_input").value;
    var phone = document.querySelector("#phone_input").value;
    var mobile = document.querySelector("#mobile_input").value;
    var e_mail = document.querySelector("#e_mail_input").value;
    var notes = document.querySelector("#notes_input").value;
    var roles = document.querySelector("#role_select_options").value;
    var active = document.querySelector("#active_select_options").value;
    //var fiscal_code = document.querySelector("#fiscal_code_input").value;
    
    if( first_name === undefined || first_name === "" || last_name === undefined || last_name === "" || username === undefined || username === "" || password === undefined || password === "" )
    {
        alert(" I campi nome, cognome, username e password sono tutti obbligatori.");
    }
    else
    {
        /*username and password are null so never modified */
        app.updateUser( user_id, username, password, hourlyCost, roles, active, null, first_name, last_name, phone, mobile, e_mail, notes, 
            function( )
            {
                location.reload();
            },
            function()
            {
                document.querySelector(".Footer_message").innerHTML = "non riesco a modificare l'utente!";
            }
        );  
    }
};

    
console.log("users.js");