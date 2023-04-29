//se l'oggetto app ( variabile globale ) non esiste lo crea, 
//ogni file js ha questa riga per cui app è un singleton
if (typeof app === 'undefined' || app === null)
    app = {};

//funzione chiamata dal campo username 
app.username_onKeyDown = function (event)
{
    //se viene premuto invio passa al campo password
    if (event.keyCode === 13)
        document.getElementById("password").focus();
};
//funzione chiamata ad ogni pressione di tasto quando il focus è sul campo
//password
app.password_onKeyDown = function (event)
{
    //se il tasto premutp è invio
    if (event.keyCode === 13)
    {
        document.getElementById("modal").style.display = "block";
        document.getElementById("loader").style.display = "block";
        //disabilita i campi delle credenziali
        document.getElementById("username").disabled = true;
        document.getElementById("password").disabled = true;
        //recupera 
        var username = document.getElementById("username").value;
        var password = document.getElementById("password").value;

        document.querySelector(".Footer_message").innerHTML = "sto controllando le credenziali...";

        app.authenticate(username, password,
                function ()
                {
                    window.open("/Samurai/dashboard.jsp", "_self");
                    document.getElementById("modal").style.display = "none";
                    document.getElementById("loader").style.display = "none";
                },
                function (message)
                {
                    document.querySelector(".Footer_message").innerHTML = "Login fallito!";
                    document.getElementById("username").disabled = false;
                    document.getElementById("password").disabled = false;
                    document.getElementById("modal").style.display = "none";
                    document.getElementById("loader").style.display = "none";
                }
        );
    }

};

console.log("landing.js");