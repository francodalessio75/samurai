/* global fetch */

//app is global variable implemented as singleton
if(typeof app==='undefined' || app===null) app = {};

app.current_customer_id = null;
app.customers = null;
app.fullVatCodeRegex = new RegExp(/^[0-9]{11}$/); 
app.univocalSevenZeroCodeRegex = new RegExp(/^[0]{7}$/); 

/****** CUSTOMER PAGE *********************************/
app.getCustomersPage = function()
{
    document.querySelector(".Footer_message").innerHTML = "sto caricando i Clienti";

    /* readCustomers is in dbi.js*/
    app.readCustomers(
        null,
        null,
        function( customers )
        {
            app.customers = customers;
            app.refreshCustomers(customers, null);
            document.querySelector(".Footer_message").innerHTML = "CLIENTI : " + customers.length;
        },
        function()
        {
            document.querySelector(".Footer_message").innerHTML = "non riesco a leggere i Clienti!";
        }
    );
};

app.refreshCustomers = function( customers, denominationHint )
{
    //gets the customers table body template
    var itemTemplate = document.querySelector("#customer_template");
    // gets the table body that will be the container of template instances 
    var itemsContainer = document.querySelector(".Table tbody");
    //empty the current content in the table
    itemsContainer.innerHTML = null;
    /**
     * Iterates customers
     */
    for(var i=0; i<customers.length; i++)
    {
        if( denominationHint == null || ( customers[i][3].toLowerCase().startsWith(denominationHint.toLowerCase()) ) )
        {
            var templateContent =  document.importNode(itemTemplate.content,true);

            //ID ASSIGNMENT 
            templateContent.querySelector("#customer_row").id = "customer_row_"+customers[i][0];
            templateContent.querySelector("#denomination").id = "denomination_"+customers[i][0];
            //templateContent.querySelector("#phone").id = "phone_"+customers[i][0];
            //templateContent.querySelector("#e_mail").id = "e_mail_"+customers[i][0];

            //content fillling
            templateContent.querySelector("#denomination_"+customers[i][0]).textContent = customers[i][3];
            //templateContent.querySelector("#phone_"+customers[i][0]).textContent = customers[i][4];
            //templateContent.querySelector("#e_mail_"+customers[i][0]).textContent = customers[i][7];

            // DEFINIZIONE DELLE CALLBACK
            templateContent.querySelector("#customer_row_"+customers[i][0]).onclick = app.setCurrentCustomerId(customers[i][0]);
            itemsContainer.appendChild(templateContent); 
        }
    }
};

/*closure*/
app.setCurrentCustomerId = function(customer_id)
{
    return function()
    {
        window.open("customer_details.jsp?customer_id="+customer_id,'_self');
    };
};
    
/* Creates a new customer*/ 
app.createNewCustomer = function()
{
    //validation checks message
    var message = "";
    
    var denomination = document.getElementById("new_customer_denomination_input").value;
    var vatCode = document.getElementById("new_customer_vatCode_input").value;
    var foreignVatCode = document.getElementById("new_customer_foreign_vatCode_input").value;
    //var IBAN = document.getElementById("IBAN_input").value;
    
    //if denomination is empty
    if( denomination === null || denomination === "" )
    {
        message = message.concat("Il campo Denominazione è obbligatorio\n");
        
    }
    /*if both vat codes have been inserted*/
    if( vatCode !== "" && foreignVatCode !== "" )
    {
        message = message.concat("Solo uno dei campi Partita IVA può essere compilato.\n");
        
    } 
    if( vatCode !== "" && !app.fullVatCodeRegex.test(vatCode) )
    {
        message = message.concat("La partita IVA, quando non vuota, deve essere formata da 11 valori numerici.\n");
        
    } 
    
    //checks if the vatCode already exists, if yes ask the user if wants to continue.
    //if both vatCode and foreignVatCode are empty the check is skipped
    if( message === "" && ( vatCode !== "" || foreignVatCode !== ""))
    {
        console.log("inside first if");
        
        var code;

        if( vatCode !== "" )
        {
            code = vatCode;
        }
        else
        {
            code = foreignVatCode;
        }
        
        console.log("code:" + code );
        
        app.checkVatCode(
            code,
            function( denominations )
            {
                console.log("inside vatCode checking");
                
                if( denominations.length > 0 )
                {

                    console.log("there are other customers having the same vatCode")

                    message += " La partita iva inserita appartiene già ai seguenti Clienti:\n ";

                    for( var i = 0; i < denominations.length; i++ )
                    {
                        message += denominations[i][0];
                        if( i < denominations.length -1 )
                            message +=", ";
                    }

                    if( confirm(message + ".\nSi vuole continuare comunque?") )
                    {
                        //If denomination is not empty and only one vat code has been inserted where the italian one 
                        //has got the rigth format then creates the customer
                        if( ( denomination !== "" ) || ( vatCode === "" && denomination !== "" && foreignVatCode !== "" ) )
                        {
                            console.log("creating customer");
                            app.createCustomer( denomination,vatCode,
                                function()
                                {
                                    console.log("created customer")
                                    location.reload(true);
                                },
                                function()
                                {
                                    document.querySelector(".Footer_message").innerHTML = "non riesco a creare il cliente.";
                                });  
                        }
                    }
                    else
                    {
                        message = "Operazione annullata";
                        document.querySelector(".Footer_message").innerHTML = "Creazione nuovo utente annullata";
                        alert(message);
                    }
                }
                else
                {
                    console.log("customer having unique vatCode");
                    //If denomination is not empty and only one vat code has been inserted where the italian one 
                    //has got the rigth format then creates the customer
                    if( ( app.fullVatCodeRegex.test(vatCode) && denomination !== "" && foreignVatCode === "" ) || ( vatCode === "" && denomination !== "" && foreignVatCode !== "" ) )
                    {
                        console.log("CREAZIONE CLIENTE");
                        app.createCustomer( denomination,vatCode,
                            function()
                            {
                                console.log("CLIENTE CREATO");
                                location.reload(true);
                            },
                            function()
                            {
                                console.log("CLIENTE NON CREATO");
                                document.querySelector(".Footer_message").innerHTML = "non riesco a creare il cliente.";
                            });  
                    }
                }
            },
            function()
            {}
        );
    }
    else
    {
        if(message === "")
        {
            //If denomination is not empty and only one vat code has been inserted where the italian one 
            //has got the rigth format then creates the customer
            if( ( denomination !== "" ) || ( vatCode === "" && denomination !== "" && foreignVatCode !== "" ) )
            {
                console.log("creating customer");
                app.createCustomer( denomination,vatCode,
                    function()
                    {
                        console.log("created customer")
                        location.reload(true);
                    },
                    function()
                    {
                        document.querySelector(".Footer_message").innerHTML = "non riesco a creare il cliente.";
                    });  
            }
        }
        else
        {
            alert(message);
        }
    }
};

app.checkEmptyVatcode = function(vatcode, univocalCode)
{
    
};

/* CUSTOMER DETAILS PAGE */
app.getCustomerDetailsPage = function(customer_id)
{
    document.querySelector(".Footer_message").innerHTML = "sto caricando i dettagli del cliente"+customer_id;
    
    app.readCustomers(
        customer_id,
        null,
        function( customers )
        {
            app.refreshCustomerDetails(customers);
            document.querySelector(".Footer_message").innerHTML = "Dettagli Cliente";
        },
        function()
        {
            document.querySelector(".Footer_message").innerHTML = "non riesco a leggere i dettagli cliente!";
        }
    );
};
    

/* Fills customer details table   */
app.refreshCustomerDetails = function( customers )
{
      //gets the authors table body template
    var itemTemplate = document.querySelector("#customer_details_template");
    // gets the table body that will be the container of template instances 
    var itemsContainer = document.querySelector(".Details_table tbody");
    //empty the current content in the table
    itemsContainer.innerHTML = null;
    /**
     * Only one customer in array
     */
    for(var i=0; i<customers.length; i++)
    {
        var templateContent =  document.importNode(itemTemplate.content,true);
        
        //content fillling
        templateContent.querySelector("#denomination_input").value = customers[i][3];
        templateContent.querySelector("#vat_code_input").value = customers[i][1];
        templateContent.querySelector("#fiscal_code_input").value = customers[i][2];
        templateContent.querySelector("#city_input").value = customers[i][8];
        templateContent.querySelector("#address_input").value = customers[i][9];
        templateContent.querySelector("#house_number_input").value = customers[i][10];
        templateContent.querySelector("#postal_code_input").value = customers[i][11];
        templateContent.querySelector("#province_input").value = customers[i][12];
        templateContent.querySelector("#country_input").value = customers[i][13];
        //templateContent.querySelector("#logo_input").value = customers[i][14];
        //templateContent.querySelector("#paymentConditions_input").value = customers[i][15];
        templateContent.querySelector("#bank_input").value = customers[i][16];
        templateContent.querySelector("#CAB_input").value = customers[i][17];
        templateContent.querySelector("#ABI_input").value = customers[i][18];
        templateContent.querySelector("#IBAN_input").value = customers[i][19];
        templateContent.querySelector("#foreignIBAN_input").value = customers[i][20];
        templateContent.querySelector("#notes_input").value = customers[i][21];
        templateContent.querySelector("#vat_exemption_text_input").value = customers[i][22];
        
        itemsContainer.appendChild(templateContent);  
    }
};

/*Makes visible the update button and changes the look of the cell that's been changed*/
app.customerDetailsChanged = function(id)
{
    document.getElementById('updateCustomerButton').classList.add('Visible');
    document.querySelector(id).classList.add('Changed');
};

/* Add data validation cheks !!!!!!!!*/
app.customerUpdated = function( customer_id, isItalian )
{
    //validation checks message
    var message = "";
    
    /*var IBANPatt = new RegExp(/^IT[0-9]{2}[A-Z]{1}[0-9]{5}[0-9]{5}[a-zA-Z0-9]{12}$/);
    var CABPatt = new RegExp(/^[0-9]{5}$/);
    var ABIPatt = new RegExp(/^[0-9]{5}$/);*/
    
    
    var denomination = document.querySelector("#denomination_input").value;
    var vat_code = document.querySelector("#vat_code_input").value;
    var fiscal_code = document.querySelector("#fiscal_code_input").value;
    var city = document.querySelector("#city_input").value;
    var address = document.querySelector("#address_input").value;
    var house_number = document.querySelector("#house_number_input").value; 
    var postal_code = document.querySelector("#postal_code_input").value; 
    var province = document.querySelector("#province_input").value;
    var country = document.querySelector("#country_input").value;
    //var logo = document.querySelector("#logo_input").value;
    var paymentConditions = document.querySelector("#payment_conditions_datalist_input").value;
    var bank = document.querySelector("#bank_input").value;
    var CAB = document.querySelector("#CAB_input").value;
    var ABI = document.querySelector("#ABI_input").value;
    var IBAN = document.querySelector("#IBAN_input").value;
    var foreignIBAN = document.querySelector("#foreignIBAN_input").value;
    var notes = document.querySelector("#notes_input").value; 
    var VATExemptionText = document.querySelector("#vat_exemption_text_input").value;
    var univocalCode = document.querySelector("#univocal_code_input").value;
    var pec = document.querySelector("#pec_input").value;
    var modalitaPagamento = document.querySelector("#modalitaPagamento_input").value;
    //checks denomination 
    if( denomination === null || denomination === "" )
    {
        message = message.concat("Il campo Denominazione è obbligatorio\n");
        
    }
    //checks univocal code format
    if( univocalCode.trim() !== "" && !( univocalCode.trim().length === 7 || univocalCode.trim().length === 6 )   )
    {
         message = message.concat("IL campo Codice Univoco deve essere formato da 6 o 7 valori alfanumerici\n");
        
    }
    
    //checks vatcode format if it is Italian
    if( ( isItalian && !app.fullVatCodeRegex.test(vat_code) &&  vat_code !== "") || ( vat_code === "" && !app.univocalSevenZeroCodeRegex.test(univocalCode)))
    {
         message = message.concat("IL campo Parita IVA deve essere formato da 11 valori numerici.\nLa partita IVA vuota è ammessa solo in caso di codice univoco formato sa sette zeri");
    }
    
    /*if IBAN less long then 15 charcters*/
    if( IBAN !== "" && IBAN.length < 15 || IBAN !== "" && IBAN.length > 34  )
    {
        message = message.concat("L'IBAN deve essere compreso tra 15 e 34 caratteri.\n");
        
    }

    if( message !== "")
        alert(message);
    else
    {
        app.updateCustomer(customer_id, vat_code, fiscal_code, denomination, null, null, null, null, city, address, house_number, postal_code, province, country, null, paymentConditions,  bank, CAB, ABI, IBAN, foreignIBAN, notes, VATExemptionText, univocalCode,pec,modalitaPagamento, 
            function( )
            {
                location.reload();
            },
            function()
            {
                document.querySelector(".Footer_message").innerHTML = "non riesco a modificare il Cliente!";
            }
        );
    }
    
};

//removes italian vat code button and foreign vat code input. shows italian vat code input 
app.italianVatcode = function()
{
    //empty foreign vat code input
    document.querySelector('#new_customer_foreign_vatCode_input').value = "";
    //hides foreign vat code input
    document.querySelector('.Foreign_vatcode').classList.remove('Visible');
    //hides italian vat code button
    document.querySelector('.Italian_vatcode_button').classList.remove('Visible');
    //shows italian vat code input
    document.querySelector('.Italian_vatcode').classList.add('Visible');
    //shows foreign button
    document.querySelector('.Foreign_vatcode_button').classList.add('Visible');
    
};

app.uploadLogo = function(customer_id)
{
    var file = document.getElementById("logo_file").files[0];

    // CREA E INIZIALIZZA IL FILE READER
    var reader = new FileReader();

    reader.fileName = file.name;
    reader.fileSize = file.size;

    // CALLBACK DI LETTURA FILE
    reader.onload = function(event)
    {
        /*instantiotes the request
        var xhr = new XMLHttpRequest();
        //when seerver unswers 
        xhr.onreadystatechange = function()
        {
            //if the operation has been executed succesfully
            if(this.readyState===4 && this.status===200)
            {
                //converts in Json the text response coming from the servlet in turn 
                //converted in text from a Json object
                var jsonResponse = JSON.parse(this.responseText);
                //reads the success boolean parmeter 
                if(jsonResponse.success)
                    successCallback();
                else
                    failCallback();
            }
        };
        //open the channel and sends the request to the server through the servlet "gate"
        xhr.open("GET","/Samurai/gate&op=upload_logo&originalFileName="+encodeURIComponent(file.name)+"&fileSize="+file.size+"&customer_id="+customer_id+"&file="+event.target.result);
        xhr.send();*/
        
        let url = "/Samurai/gate";
        
        let data =
            "&op=upload_logo"+
            "&fileName="+encodeURIComponent(file.name)+
            "&fileSize="+file.size+
            "&customer_id="+customer_id+
            "&file="+event.target.result;
    
        let options =
        {
            method:"POST",
            headers: {"Content-type": "application/x-www-form-urlencoded; charset=UTF-8"},
            body:data,
            credentials:"include"
        };
                
        fetch(url,options)
            .then(response=>response.json())
            .then(json=>
                {
                    alert("LOGO CARICATO ");
                    window.open("customer_details.jsp?customer_id="+customer_id,'_self');
                })
            .catch(error=>alert("ERROR : "+error));
    };

    // LEGGE IL FILE DA DISCO
    reader.readAsDataURL(file);   
};
    

app.filterCustomers = function( )
{   
    var denominationHint = document.getElementById("denomination_filter").value;
    app.refreshCustomers(app.customers, denominationHint);
    
};

console.log("customers.js");