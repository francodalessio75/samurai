<%@page import="com.dps.dbi.DbResult"%>
<%@page import="com.dps.dbi.DbResultsBundle"%>
<%@page import="com.dalessio.samurai.DataAccessObject"%>
<%@page import="java.time.LocalDate"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.util.Date"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<%
    Long timestamp = new Date().getTime();
    
     //English date format
    DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    //European date format
    DateTimeFormatter DTFE = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    
    DbResult dbr_task = null;
    //initilizes all elements that must be empty if task_id is null
    String taskDate = "";
    String operatorFirstName = "";
    String operatorLastName = "";
    Double hours = 0.0;
    String orderCode = "";
    String customerDenomination = "";
    String orderCompletionState = "";
    String machinaryModel = "";
    String jobType = "";
    Long jobSubtype_id = 0L;
    Long translations_center_id = 0L;
    Double translationsPrice = 0.0;
    Double translationsCost = 0.0;
    String externalJobsDesc = "";
    Double externalJobsHours = 0.0;
    Double externalJobsCost = 0.0;
    String variouseMaterialDesc = "";
    Double variouseMaterialCost = 0.0;
    Integer transfertKms = 0;
    Double transfertCost = 0.0;
    String notes = "";
    Long task_id = 0L;
    //confirm button text
    String confirmButtonText = "CONFERMA CREAZIONE";
    //if task_id is not null task details must be shown in the page
    try{
        task_id = Long.parseLong(request.getParameter("task_id"));
    }catch(NumberFormatException ex ){}
    //the page is requested by clicking on a tasks table row in tasks.jsp page
    if( task_id != 0L )
    {
        //Long task_id, Long user_id, Long order_id, Long operator_id, Long operator_id, String orderCode, Long jobType_id, Long jobSubtype_id, Long customer_id, Long order_creator_id, String fromDate, String toDate, Long completion_state_id 
        DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
        dbr_task = dao.readTask( task_id );
        //taskDate
        //formats the date
        StringBuilder taskDateSB = new StringBuilder(dbr_task.getString("taskDate"));
        taskDateSB.insert(4, "-");
        taskDateSB.insert(7,"-");
        taskDate = taskDateSB.toString();
        hours = dbr_task.getDouble("hours");
        orderCode = dbr_task.getString("orderCode");
        customerDenomination = dbr_task.getString("customerDenomination");
        orderCompletionState = dbr_task.getString("orderCompletionState");
        machinaryModel = dbr_task.getString("machinaryModel");
        jobType = dao.getJobTypeName( dbr_task.getLong("jobType_id") );
        jobSubtype_id = dbr_task.getLong("jobSubtype_id");
        translations_center_id = dbr_task.getLong("translations_center_id");
        translationsPrice = dbr_task.getDouble("translationPrice");
        translationsCost = dbr_task.getDouble("translationCost");
        externalJobsDesc = dbr_task.getString("externalJobsDesc");
        externalJobsHours = dbr_task.getDouble("externalJobsHours");
        externalJobsCost = dbr_task.getDouble("externalJobsCost");
        variouseMaterialDesc = dbr_task.getString("variouseMaterialDesc");
        variouseMaterialCost = dbr_task.getDouble("variouseMaterialCost");
        transfertKms = dbr_task.getInteger("transfertKms");
        transfertCost = dbr_task.getDouble("transfertCost");
        notes = dbr_task.getString("notes");
        //UPDATE
       confirmButtonText = "CONFERMA MODIFICA";
    }
    //sets the taskdate to current day if task_id is null
    else
        taskDate = LocalDate.now().toString();
    //gets user_id from session
    Long user_id = (Long) request.getSession().getAttribute("user_id");
    //user validation
    if(user_id==null){
        
%>

    <script>window.open("/Samurai/landing.jsp","_self")</script>

<%}else
{
    DataAccessObject dao = (DataAccessObject) session.getAttribute("dao");
    DbResult dbr_user = dao.readUsers( user_id);
    String userName = dbr_user.getString("firstName") + " " + dbr_user.getString("lastName");
    String user_role = dbr_user.getString("role");
    DbResult dbr_operators = dao.readUsers(null, null, null, null, null, null, null, null, null, null, null, null);
    DbResult dbr_jobTypes = dao.readJobTypes(null);
    DbResult dbr_jobSubtypes = dao.readJobSubtypes(null);
    DbResult dbr_translationsCenters = dao.readTranslationsCenters(null);
     if( dbr_task == null )
    {
        operatorFirstName = dbr_user.getString("firstName");
        operatorLastName = dbr_user.getString("lastName");
    }
    else
    {
        operatorFirstName = dbr_task.getString("operatorFirstName");
        operatorLastName = dbr_task.getString("operatorLastName");
    }
%>

    <html>
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>Workline 2.0</title>
            <link href="/Samurai/common.css" rel="stylesheet">
            <link href="/Samurai/tasks.css" rel="stylesheet">
            <link href="/Samurai/Resources/font-awesome-4.7.0/css/font-awesome.min.css" rel="stylesheet">
        </head>
        <body onload="app.getTaskDetailsPage(<%=task_id%>, <%=user_id%>);">
            
            <div class="Header">
                <span class="SWName">Workline 2.0</span><span class="PageTitle"> Ore Lavori </span>
            </div>

            <div class="Dropdown_registry">
                <button onmouseover="app.showRegistry()" onmouseout="app.hideRegistry()" class="DropBtn Registry">ANAGRAFICHE</button>
                <div id="myDropdown_registry" class="Dropdown_content Registry">
                    <div onmouseover="app.showRegistry()" onmouseout="app.hideRegistry()" onclick="window.open('customers.jsp','_self');">ANAGRAFICA CLIENTI</div>
                    <div onmouseover="app.showRegistry()" onmouseout="app.hideRegistry()" onclick="window.open('translationsCenters.jsp','_self');">CENTRI TRADUZIONE</div>
                    <div onmouseover="app.showRegistry()" onmouseout="app.hideRegistry()" onclick="window.open('transporters.jsp','_self');">VETTORI</div>
                    <%if( "admin".equals(user_role) ){%><div onmouseover="app.showRegistry()" onmouseout="app.hideRegistry()" onclick="window.open('users.jsp','_self');">OPERATORI</div><%}%>
                    <%if( "admin".equals(user_role) ){%><div onmouseover="app.showRegistry()" onmouseout="app.hideRegistry()" onclick="window.open('jobTypes.jsp','_self');">TIPI LAVORO</div><%}%>
                    <%if( "admin".equals(user_role) ){%><div onmouseover="app.showRegistry()" onmouseout="app.hideRegistry()" onclick="window.open('jobSubtypes.jsp','_self');">TIPI LAVORAZIONE</div><%}%>
                </div>
            </div>
            
            <div class="Dropdown_tools">
                <button onmouseover="app.showTools()" onmouseout="app.hideTools()" class="DropBtn Tools">STRUMENTI</button>
                <div id="myDropdown_tools" class="Dropdown_content Tools">
                    <div onmouseover="app.showTools()" onmouseout="app.hideTools()" onclick="window.open('changeCredentials.jsp','_self');">CAMBIA PASSWORD</div>
                    <%if( "admin".equals(user_role) ){%><div onmouseover="app.showTools()" onmouseout="app.hideTools()" onclick="window.open('invoiceDateUpdating.jsp','_self');">CAMBIA DATA FATTURA</div><%}%>
                    <div onmouseover="app.showTools()" onmouseout="app.hideTools()" onclick="window.open('landing.jsp','_self');">LOG-OUT</div>
                </div>
            </div>
            
            <div class="Menu">
                <span class=" Button" onclick="window.open('orders.jsp','_self');">ELENCO LAVORI</span>
                <span class=" Button" onclick="window.open('task_details.jsp','_self');">ORE LAVORI</span>
                <span class=" Button" onclick="window.open('deliveryNote_details.jsp','_self');">DDT</span>
                <%if( "admin".equals(user_role) ){%><span class=" Button" onclick="window.open('invoice_details.jsp','_self');">FATTURE</span><%}%>
                <%if( "admin".equals(user_role) ){%><span class=" Button" onclick="window.open('quote_details.jsp','_self');">PREVENTIVI</span><%}%>
                <%if( "admin".equals(user_role) ){%><span class=" Button" onclick="window.open('creditNote_details.jsp','_self');">NOTE ACCREDITO</span><%}%>
            </div>
            
            <!-- FILTERS SECTION -->
            <div class="Filters">
                
                <div class="Filters_label">RICERCA</div>
                
                <% if( user_role.equals("admin") ){%>
                <select id="operator_select_options" >
                    <option value="" >OPERATORE</option>
                    <%for(int i=0; i<dbr_operators.rowsCount(); i++){%>
                        <option value="<%=dbr_operators.getLong(i,"user_id")%>"><%=dbr_operators.getString(i,"firstName")+" "+dbr_operators.getString(i,"lastName")%></option>
                    <%}
                }%>
                </select>
                
                <input id="order_code_Hint" placeholder="CODICE LAVORO">
                
                <select id="jobType_select_options" >
                    <option value="" > TIPO LAVORO </option>
                    <% for( int i = 0; i < dbr_jobTypes.rowsCount(); i++ )
                    {%>
                    <option value="<%=dbr_jobTypes.getLong(i,"jobType_id")%>"><%=dbr_jobTypes.getString(i,"name")%></option>
                    <%}%>
                </select>
                
                <select id="jobSubtype_select_options">
                    <option value="">TIPO LAVORAZIONE</option>
                    <% for ( int i = 0; i < dbr_jobSubtypes.rowsCount(); i++  )
                    {%>
                    <option value="<%= dbr_jobSubtypes.getLong(i,"jobSubtype_id")%>"><%= dbr_jobSubtypes.getString(i,"name") %></option>
                    <%}%>
                </select> 
                
                <div class="Filters_period_label">PERIODO</div>
                
                <table id="dates" style="border-collapse: collapse;">
                    <tr><td>Da:</td><td><input type="date" id="from_date" max="<%=LocalDate.now().toString()%>"></td></tr>
                    <tr><td>A:</td><td><input type="date" id="to_date" max="<%=LocalDate.now().toString()%>"></td></tr>
                </table>
                
                <div class="Filters_status_label">STATO LAVORO</div>
                
                <select id="completionState_select_options">
                    <option value="-1">- TUTTI -</option>
                    <option value="1">- IN CORSO -</option>
                    <option value="2">- COMPLETATI -</option>
                </select>
                <div id="refresh" class="Button" onclick="app.openTasksPage( '<%=user_role%>' );"><i  id="searchIcon" class="fa fa-search" aria-hidden="true"></i>RICERCA</div>
                
                <div id="confirm_label" class="Filters_label">CONFERMA OPERAZIONE</div>
                
                <div id="taskConfirmedButton" class="Button Operation Confirm" onclick="app.taskConfirmed(<%=task_id%>,<%=user_id%>);"><%=confirmButtonText%></div>
                
                <div id="creationRequestButton" class="Button Operation Confirm" onclick="window.open('task_details.jsp','_self');">NUOVO LAVORO</div>
            </div>    

            <!-- PAGE CONTENT -->
            <div class="Content WithFilter">
                <table class="Details_table">
                    
                    <tr class="Details_row">
                        <th class="Detail_header" id="code">CODICE LAVORO</th>
                        <td><input id="order_code_input" type="text"  value="<%=orderCode%>" onchange="app.detailsChanged('order_code_input');"></td>
                    </tr>
                    
                    <tr class="Details_row">
                        <th class="Detail_header" id="customer">CLIENTE</th>
                        <td id="customer_denomination_value"><%=customerDenomination%></td>
                    </tr>
                    
                    <tr class="Details_row">
                        <th class="Detail_header" id="status">STATO LAVORO</th>
                        <td id="orderCompletionState_value"><%=orderCompletionState%></td>
                    </tr>
                    
                    <tr class="Details_row">
                        <th class="Detail_header " id="jobType">TIPO LAVORO</th>
                        <td id="job_type_value"><%=jobType%></td>
                    </tr>
                    
                    <tr class="Details_row">
                        <th class="Detail_header" id="machinaryModel">DESCRIZIONE LAVORO</th>
                        <td id="machinary_model_value"><%=machinaryModel%></td>
                    </tr>
                    
                    <tr class="Details_row ">
                        <th class="Detail_header Input" id="date" max="<%=LocalDate.now().toString()%>">DATA LAVORAZIONE</th>
                        <td>
                            <input type="date" id="task_date_input" value="<%=taskDate%>" onchange="app.detailsChanged('task_date_input');" style="width:10rem;" ><br>
                        </td>
                    </tr>
                    
                    <tr class="Details_row">
                        <th class="Detail_header" id="operator">OPERATORE</th>
                        <td id="operator_value"><%=operatorFirstName%> <%=operatorLastName%></td>
                    </tr>
                    
                    <tr class="Details_row ">
                        <th class="Detail_header Input" id="hours">ORE</th>
                        <td><input id="hours_input" type="number" min="0" max="12" step=".25"  onchange="app.detailsChanged('hours_input');" value="<%=hours%>" ></td>
                    </tr>
                    <!--tr class="Details_row">
                        <th class="Detail_header">CHIUSURA LAVORO</th>
                        <td id="closing_reason_value"><!%=closingReason%></td>
                    </tr-->
                    <tr class="Details_row ">
                        <th class="Detail_header Input" id="jobSubtype">TIPO LAVORAZIONE</th>
                        <td>
                            <select id="job_subtype_select_options" onchange="app.detailsChanged('job_subtype_select_options');" >
                                <%for(int i=0; i<dbr_jobSubtypes.rowsCount(); i++){%>
                                <option value="<%=dbr_jobSubtypes.getLong(i,"jobSubtype_id")%>" ><%=dbr_jobSubtypes.getString(i,"name")%></option>
                                <%}%>
                            </select>
                            <script>document.getElementById("job_subtype_select_options").value=<%=jobSubtype_id%></script>
                          </td> 
                    </tr>
                    
                    <tr class="Details_row ">
                        <th class="Detail_header Input" id="translationsCenter">TRADUZIONI</th>
                        <td id="translationsRow">
                            <span class="TableLable"></span>
                            <span>
                                <select id="translation_center_select_options" onchange="app.detailsChanged('translation_center_select_options');" >
                                    <%for(int i=0; i<dbr_translationsCenters.rowsCount(); i++){%>
                                    <option value="<%=dbr_translationsCenters.getLong(i,"translations_center_id")%>" ><%=dbr_translationsCenters.getString(i,"denomination")%></option>
                                    <%}%>
                                </select>
                            </span>
                            <span class="TableLable">COSTO:</span>
                                <input   id="translations_cost_input"  type="number" step=0.01  min="0.00" max="9999999999.99" onchange="app.detailsChanged('translations_cost_input');" value="<%=translationsCost%>">  
                            <span class="TableLable">FATTURATO:</span>
                                <input   id="translations_price_input"  type="number" step=0.01  min="0.00" max="9999999999.99" onchange="app.detailsChanged('translations_price_input');" value="<%=translationsPrice%>">
                            <script>document.getElementById("translation_center_select_options").value=<%=translations_center_id%></script>
                        </td> 
                    </tr>
                    
                    <tr class="Details_row ">
                        <th class="Detail_header Input" id="externalJobs">LAVORAZIONI ESTERNE</th>
                        <td>
                            <span class="TableLable">ORE:</span>
                            <input  id="external_jobs_hours_input" type="number" min="0" step=".25" onchange="app.detailsChanged('external_jobs_hours_input');" value="<%=externalJobsHours%>">
                            <span class="TableLable">COSTO:</span>
                            <input  id="external_jobs_cost_input" type="number" step=0.01 min="0.00" max="9999999999.99" onchange="app.detailsChanged('external_jobs_cost_input');" value="<%=externalJobsCost%>">
                            <span class="TableLable">NOTE:</span>
                            <input id="external_jobs_desc_input" type="text" onchange="app.detailsChanged('external_jobs_desc_input');" value="<%=externalJobsDesc%>">
                        </td>
                    </tr>
                    
                    <tr class="Details_row ">
                        <th class="Detail_header Input" id="materials">MATERIALI</th>
                        <td>
                            <span class="TableLable">COSTO:</span>
                            <input  id="variouse_material_cost_input" type="number" step=0.01 min="0.00" max="9999999999.99" onchange="app.detailsChanged('variouse_material_cost_input');" value="<%=variouseMaterialCost%>">
                            <span class="TableLable">NOTE:</span>
                            <input id="variouse_material_desc_input" type="text" onchange="app.detailsChanged('variouse_material_desc_input');" value="<%=variouseMaterialDesc%>">
                        </td>
                    </tr>
                    
                    <tr class="Details_row ">
                        <th class="Detail_header Input" id="transfertCost"> TRASFERTA</th>
                        <td>
                            <span class="TableLable">COSTO:</span>
                            <input id="transfert_cost_input" type="number" step=0.01 min="0.00" max="9999999999.99" onchange="app.detailsChanged('transfert_cost_input');" value="<%=transfertCost%>">
                            <span class="TableLable">KM:</span>
                            <input id="transfert_kms_input" type="number" step=1 min="0" onchange="app.detailsChanged('transfert_kms_input');" value="<%=transfertKms%>">
                        </td>
                    </tr>
                    
                    <tr class="Details_row ">
                        <th class="Detail_header Input" id="notes" rows="4" cols="50">NOTE</th>
                        <td><textarea id="notes_input"  onchange="app.detailsChanged('notes_input');"><%=notes%></textarea></td>
                    </tr>
                    
                    <% if( task_id != 0L ){%>
                    <tr class="Details_row ">
                        <th class="Detail_header Input" id="notes">ALLEGA</th>
                        <td>
                            <input id='taskUploadChooser'name="file" type="file">
                            <input class="Button Operation" id="submitAttach" type="submit" value="Carica Allegato" onclick="app.uploadTaskAttachment(<%= task_id %>);">
                        </td>
                    </tr>
                    <%}%>
                    
                </table>   

                <table class="Table" id="attachments">
                    <thead>
                        <th>DATA ALLEGATO</th>
                        <th>NOME</th>
                        <th>AZIONI</th>
                    </thead>
                    <tbody></tbody>
                </table>
               
            </div>

            
                    
            <div class="Footer">
                <span class="User_name"> Utente : <%=userName%></span>
                <span class="Footer_message"></span>
            </div>

        </body>

        <template id="task_attachments_template">
            <tr class="TaskRow">
                <td class="Date" id="date"></td>
                <td class="CurrentFileName" id="currentFileName"></td>
                <td class="Actions" id="actions"></td>
            </tr>
        </template> 
        
        <script src="/Samurai/dbi.js?<%=timestamp%>"></script>
        <script src="/Samurai/tasks.js?<%=timestamp%>"></script>
        <script src="/Samurai/dropDown.js?<%=timestamp%>"></script> 
        <script>setInterval(function(){app.ping();},60000);</script>
    </html>

<%}%>