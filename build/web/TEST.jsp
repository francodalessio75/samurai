<%@page import="java.awt.Label"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Long[] id = new Long[]{1l,2l,3l,4l,5l};
    String[] label = new String[]{"nicola","giuseppe","pasquale","gianluca","franco"};
    
    /* only suggest datalis*********
    
    Test for datalist that only suggests. On change avent there are two options:
     1)the hint in the input isn't contained in any options -> the input became empty;
     2) the hint is contained in one or more options -> the input is filled by the first applicable value.*/
    //values array
    String[] JSPjobTypes = new String[]{"manuale", "Manuale istruzioni", "traduzione", "Elaborazione", "Elaborazione testi", "Eleborazione immagini"};
    //ids array
    Long[] JSPjobTypesIds = new Long[JSPjobTypes.length];
    //poulates ids array
    for( int i = 0 ; i < JSPjobTypes.length; i++ )
        JSPjobTypesIds[i]=Long.parseLong(i+"");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <input type="number" id="numberfloat" step=".25" min="0" max="12" pattern="[0-9]*\.[0-9]+|[0-9]+" onchange="checkHours();" >
        <script>
            var checkHours = function()
            {
                //retrieves the vaue
                var hourString = document.getElementById('numberfloat').value;
                alert("taken" + hourString);
                //removes the dot
                hourString = hourString.replace( ".", '');
                alert("without the dot" + hourString);
                //tries to parse the number
                if( parseFloat(hourString) !== NaN )
                {
                    alert("parsable");
                    
                    //takes again the valur and checks the range
                    hourString = document.getElementById('numberfloat').value;
                    //takes the value
                    var hoursFloat = parseFloat(hourString);
                    alert("parsed to" + hoursFloat);
                    //checks the range
                    if( 0 < hoursFloat &&  hoursFloat  <= 12 )
                    {
                        alert('in range');
                        if(hoursFloat !== 12 )
                        {
                            //takes the decimal part an round it
                            var integer = Math.floor(hoursFloat);
                            alert("integer: " + integer );
                            var decimal = hoursFloat - integer; 
                             alert("decimal" + decimal );
                            if( decimal <= 0.25 )
                            {
                                alert("first if" + decimal );
                                decimal = 0.25;
                                alert("first if" + decimal );
                            }
                            else if ( decimal > 0.25 && decimal <= 0.5 )
                            {
                                alert("second if" + decimal );
                                decimal = 0.5;
                                alert("second if" + decimal );
                            }
                            else if( decimal > 0.5 && decimal <= 0.75 )
                            {
                                alert("third if" + decimal );
                                decimal = 0.75;
                                alert("third if" + decimal );
                            }
                            else if( decimal  > 0.75 )
                            {
                                alert("forth if" + decimal );
                                decimal = 0.75;
                                alert("forth if" + decimal );
                            }
                            //rebuids the number
                            hoursFloat = integer + decimal;
                            alert("result " + hoursFloat );
                            //writes the value in the field
                            document.getElementById('numberfloat').value = hoursFloat;
                        }
                        else
                        {
                            document.getElementById('numberfloat').value = hoursFloat;
                            alert("result " + hoursFloat );
                        }
                            
                    }
                    else
                        alert('out of range');
                }
                else
                {
                    alert('not a number');
                     document.getElementById('numberfloat').value = 0.0;
                }
            };
            
        </script>
        <input id="hint" type="text" onkeyup="updateSelect();"><br>
        <select id="data"></select>
        <input type="button" onclick="save();" value="SALVA"><br>
        <input type="date" id="datePicker" >
        <script>
            document.getElementById('datePicker').valueAsDate = new Date();
            //document.getElementById('datePicker').value = new Date().toDateInputValue();
            //var today = moment().format('DD-MM-YYYY');
           // document.getElementById("datePicker").value = today; 
        </script>
        
        <!-- ************only suggest datalis****************************   --> 
        <input type="text" list="job_type_datalist" id="jobTypeHint" onchange="onGoAway();">
            <datalist  id="job_type_datalist" >
                <%for( int i = 0; i < JSPjobTypes.length; i++ ){%>
                <option id= <%=i%>><%=JSPjobTypes[i]%></option>
                <%}%>
            </datalist> 
            <script>
                //creates the array having two properties for each element : id and jobType
                /*var jobTypes = [];
                <!--%for(int i=0; i<jobTypes.length; i++){%>
                    jobTypes.push({id:<!--%=id[i]%>, jobType"<!--%=label[i]%>"});
                <!--%}%>*/
                var onGoAway = function()
                {
                    //creates a javascript array for the JSP array
                    var data = [];
                    //populates the array by id and value
                    <%for(int i = 0; i < JSPjobTypes.length; i++){%>
                        data.push({id:<%=JSPjobTypesIds[i]%>, jobType:"<%=JSPjobTypes[i]%>"});
                    <%}%>
                    /*takes the current value in input field*/
                    var jobTypeHint = document.getElementById("jobTypeHint").value;
                    //iterates the array and if find a value containing th hint update input value and asks from the iteration
                    //if doesn't find any valid value empties the input
                    for(var i=0; i<data.length; i++)
                    {
                        if( data[i].jobType.toLowerCase().includes(jobTypeHint.toLowerCase()))
                        {
                            document.getElementById("jobTypeHint").value = data[i].jobType;
                            break;
                        }
                         else
                                document.getElementById("jobTypeHint").value = "";
                    }
                };  
            </script>
 <!--******************************************************************************-->                 
    
    
    <script>
        var data = [];
        
        <%for(int i=0; i<label.length; i++){%>
            data.push({id:<%=id[i]%>, label:"<%=label[i]%>"});
        <%}%>
        
        function updateSelect()
        {
            var hint = document.getElementById("hint").value;
            
            var html = "";
            
            for(var i=0; i<data.length; i++)
            {
                if(data[i].label.toLowerCase().includes(hint.toLowerCase()))
                {
                    html += "<option value="+data[i].id+">"+data[i].id+" - "+data[i].label+"</option>\n";
                }
            }
            
            document.getElementById("data").innerHTML = html;
        }
        
        function save()
        {
            var id = document.getElementById("data").value;
            alert(id);
        }
        
        updateSelect();
        
        alertDate = function
        {
          alert(document.getElementById("datePicker").value);  
        };
    </script>
    
    
    
   
</html>
