//app is global variable implemented as singleton
if(typeof app==='undefined' || app===null) app = {};


app.getCustomerDetailsPage = function()
{
    document.getElementById("message").innerHTML = "sto caricando i dettagli del cliente";
    /**
     * readCustomers is a method located in dbi.js. It takes three parameters
     * denominationHint
     * successCallback
     * failCallback
     */
    app.readCustomers(
        null,
        function( customers )
        {
            app.refreshCustomers(customers);
            document.getElementById("message").innerHTML = "Sono presenti " + customers.length + " clienti";
        },
        function()
        {
            document.getElementById("message").innerHTML = "non riesco a leggere i clienti!";
        }
    );
};

app.refreshCustomers = function( customers )
{
      //gets the authors table body template
    var itemTemplate = document.querySelector("#customer_template");
    // gets the table body that will be the container of template instances 
    var itemsContainer = document.querySelector("#customers tbody");
    //empty the current content in the table
    itemsContainer.innerHTML = null;
    /**
     * Iterates authors that is a 
     * @type Number
     */
    for(var i=0; i<customers.length; i++)
    {
        var templateContent =  document.importNode(itemTemplate.content,true);
        
        //ID ASSIGNMENT 
        templateContent.querySelector(".row").id = "row_"+customers[i][0];
        templateContent.querySelector(".denomination").id = "denomination_"+customers[i][0];
        
        //content fillling
        //templateContent.querySelector(".denomination").value = customers[i][3];
        templateContent.querySelectorAll("td")[0].textContent = customers[i][3];
        // DEFINIZIONE DELLE CALLBACK
        templateContent.querySelector(".denomination").onclick = app.getCustomerDetailsPage(customers[i][0]);
        itemsContainer.appendChild(templateContent);  
    }
};

/**/
app.getCustomerDetailsPage = 
    function( customer_id ) 
    {return function()
        {
            app.currentCustomerId = customer_id;
            window.open('customer_details.jsp');     
        };
    };
    
console.log("customers.js");/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


