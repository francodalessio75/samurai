//app is global variable implemented as singleton
if(typeof app==='undefined' || app===null) app = {};

app.showRegistry = function()
{
    document.getElementById("myDropdown_registry").classList.add("Show");
};

app.hideRegistry = function()
{
    document.getElementById("myDropdown_registry").classList.remove("Show");
};

app.showTools = function()
{
    document.getElementById("myDropdown_tools").classList.add("Show");
};

app.hideTools = function()
{
    document.getElementById("myDropdown_tools").classList.remove("Show");
};




