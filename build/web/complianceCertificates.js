/* global fetch, Promise */

//app is global variable implemented as singleton
if(!app) var app = {};

console.log('BEGIN:complianceCertificates.js'); 

//new certificate object
app.newCertificate = {};

app.customer_id = null;

//existing certificate object to be updated
app.certificate = {};

/* the certificate_id value involves wich between creation or update must be done*/
app.certificateConfirmed = function(  cert_id )
{
    //if invoice_id is 0 then it is a creation
    if( cert_id === null )
    {
        //creation
        app.createNewCertificate();
    }
    else
    {
        //update
        app.callUpdateCertificate( cert_id );
    }
};

/*Makes visible the update button and changes the look of the cell that's been changed*/
app.certificateDetailsChanged = function(field_id)
{
    /* INSERT FIELDS CHECKING*/
    document.getElementById(field_id).classList.add('Changed');
};

app.createNewCertificate = function()
{  
    //alert message
    let message = "";
   
    //if there is only one row and it is empty alerts tehe matter
    if ( document.querySelector("#orderDescription_input").value === "" )
        message += " Il modulo non ha Descrizione Lavoro! ";
    
    if( message === ""  )
    {
        let element = document.getElementById('certificate');
        app.newCertificate.order_id = element.dataset.order_id;
        app.newCertificate.firstTitle_id = document.getElementById("firstTitle_select_options").value;
        app.newCertificate.secondTitle_id = document.getElementById("secondTitle_select_options").value;
        app.newCertificate.year = document.getElementById("date_input").value.substring(2,4);
        app.newCertificate.date = document.getElementById("date_input").value;
        app.newCertificate.firstForAttention = document.getElementById("firstForAttention_input").value;
        app.newCertificate.secondForAttention = document.getElementById("secondForAttention_input").value;
        app.newCertificate.orderDescription = document.getElementById("orderDescription_input").value;
        app.newCertificate.customerJobOrderCode = document.getElementById("customerOrderCode_input").value;

        document.querySelector(".Footer_message").innerHTML = " Sto creando il modulo... ";

        app.createCertificate( app.newCertificate,
            (newCertificate)=>
            {
                window.open('complianceCertificate_details.jsp?order_id='+newCertificate.order_id,'_self');
                document.querySelector(".Footer_message").innerHTML =" modulo Creato ";
            },
            ()=>
            {
               window.alert("non riesco a creare il modulo"); 
            }
        );

    }
    else
        window.alert( message );
        
};

/**/
app.callUpdateCertificate = function( cert_id )
{  
    document.querySelector(".Footer_message").innerHTML = "Sto modificando il modulo... ";
    app.certificate.complianceCertificate_id = cert_id;
    //alert message
    let message = "";
   
        
    /* quote rows section*/
    if(message === "")
    {
        //if there is only one row and it is empty alerts tehe matter
        if ( document.querySelector("#orderDescription_input").value === "" )
            message += " Il modulo non ha Descrizione Lavoro! ";
    

        //if message is empty and there is unless one row creates the new delivery note else alerts the message
        if( message === ""  )
        {
            let element = document.getElementById('certificate');
            app.certificate.cert_id = element.dataset.cert_id;
            app.certificate.order_id = element.dataset.order_id;
            app.certificate.firstTitle_id = document.getElementById("firstTitle_select_options").value;
            app.certificate.secondTitle_id = document.getElementById("secondTitle_select_options").value;
            app.certificate.firstForAttention = document.getElementById("firstForAttention_input").value;
            app.certificate.secondForAttention = document.getElementById("secondForAttention_input").value;
            app.certificate.orderDescription = document.getElementById("orderDescription_input").value;
            app.certificate.customerJobOrderCode = document.getElementById("customerOrderCode_input").value;
            
            document.querySelector(".Footer_message").innerHTML = " Sto modificando il modulo... ";

            app.updateCertificate( app.certificate,
            (certificate)=>
            {
                
                window.open('complianceCertificate_details.jsp?order_id='+certificate.order_id,'_self');
                document.querySelector(".Footer_message").innerHTML = "Modulo modificato";                 
            },
            ()=>
            {
               window.alert("non riesco a modificare il modulo!"); 
            });

        }
        else
            window.alert( message );
    }
    else
        window.alert( "Il preventivo non ha righe!" );
 
};


console.log('END:complianceCertificates.js'); 


