/* global fetch */
if (typeof app === 'undefined' || app === null)
    app = {};

app.ping = function ()
{
    var url = encodeURI("/Samurai/gate?&op=ping");

    fetch(url, {credentials: 'same-origin'});
};

app.authenticate = function (username, password, successCallback, failCallback)
{
    var url = encodeURI("/Samurai/gate?&op=auth&username=" + username + "&password=" + password);

    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse);
                else
                    failCallback(jsonResponse.message);
            })
            .catch(error => alert(error));
};


//  USERS 


/**
 * 
 * @param {type} firstName
 * @param {type} lastName
 * @param {type} fiscalCode
 * @param {type} successCallback
 * @param {type} failCallback
 * @returns {undefined}
 */
app.createUser = function (firstName, lastName, username, password, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";

    var url = encodeURI('/Samurai/gate?&op=create_user&firstName=' + firstName + '&lastName=' + lastName + '&username=' + username + '&password=' + password);

    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.record_id);
                else
                    failCallback();
            })
            .catch(error => alert(error)).finally(() => {
        document.getElementById("modal").style.display = "none";
        document.getElementById("loader").style.display = "none";
    });
};

app.readUsers = function (user_id, firstNameHint, lastNameHint, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";

    var url = encodeURI("/Samurai/gate?&op=read_users");
    if (user_id !== null)
        url = encodeURI("/Samurai/gate?&op=read_users&user_id=" + user_id);

    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.users);
                else
                    failCallback();
            })
            .catch(error => alert(error)).finally(() => {
        document.getElementById("modal").style.display = "none";
        document.getElementById("loader").style.display = "none";
    });
};


/**
 * By updating an user administrator can change ( recovery ) users credentials
 * in case of loosing.
 * By updating an administrator can activate unactivate a user
 * @param {type} user_id
 * @param {type} username
 * @param {type} password
 * @param {type} roles
 * @param {type} active
 * @param {type} fiscalCode
 * @param {type} firstName
 * @param {type} lastName
 * @param {type} phoneNumber
 * @param {type} cellNumber
 * @param {type} email
 * @param {type} notes
 * @param {type} successCallback
 * @param {type} failCallback
 * @returns {undefined}
 */
app.updateUser = function (user_id, username, password, hourlyCost, roles, active, fiscalCode, firstName, lastName, phoneNumber, cellNumber, email, notes, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";

    var url = encodeURI("/Samurai/gate?&op=update_user");
    if (user_id !== null)
    {
        url = encodeURI("/Samurai/gate?&op=update_user&user_id=" + user_id + "&username=" + username + "&password=" + password + "&hourlyCost=" + hourlyCost + "&roles=" + roles + "&active=" + active + "&fiscalCode=" + fiscalCode + "&firstName=" + firstName + "&lastName=" + lastName + "&phoneNumber=" + phoneNumber + "&cellNumber=" + cellNumber + "&email=" + email + "&notes=" + notes);
        fetch(url, {credentials: 'same-origin'})
                .then(response => response.json())
                .then(jsonResponse =>
                {
                    if (jsonResponse.success)
                        successCallback(jsonResponse.users);
                    else
                        failCallback();
                })
                .catch(error => alert(error)).finally(() => {
            document.getElementById("modal").style.display = "none";
            document.getElementById("loader").style.display = "none";
        });
    }
};



//CUSTOMERS 

/**
 * 
 * @param {type} denomination
 * @param {type} vatCode
 * @param {type} successCallback
 * @param {type} failCallback
 * @returns {undefined}
 */
app.createCustomer = function (denomination, vatCode, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";

    var url = encodeURI('/Samurai/gate?&op=create_customer&denomination=' + denomination + '&vatCode=' + vatCode);

    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.record_id);
                else
                    failCallback();
            })
            .catch(error => alert(error)).finally(() => {
        document.getElementById("modal").style.display = "none";
        document.getElementById("loader").style.display = "none";
    });
};

app.readCustomers = function (customer_id, denominationHint, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    var url = encodeURI("/Samurai/gate?&op=read_customers");
    if (customer_id !== null)
        url = encodeURI("/Samurai/gate?&op=read_customers&customer_id=" + customer_id);

    if (denominationHint !== null)
        url += encodeURI("&denominationHint=" + denominationHint);

    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.customers);
                else
                    failCallback();
            })
            .catch(error => alert(error)).finally(() => {
        document.getElementById("modal").style.display = "none";
        document.getElementById("loader").style.display = "none";
    });
};

/**/
app.updateCustomer = function (
        customer_id,
        vatCode,
        fiscalCode,
        denomination,
        phoneNumber,
        faxNumber,
        cellNumber,
        email,
        city,
        address,
        houseNumber,
        postalCode,
        province,
        country,
        logo,
        paymentConditions,
        bank,
        CAB,
        ABI,
        IBAN,
        foreignIBAN,
        notes,
        VATExemptionText,
        univocalCode,
        pec,
        modalitaPagamento,
        vatExemptionProtocol,
        vatExemptionDate,
        successCallback, failCallback)
{
    if (customer_id !== null)
    {
        document.getElementById("modal").style.display = "block";
        document.getElementById("loader").style.display = "block";

        var url = "/Samurai/gate?&op=update_customer&customer_id=" + encodeURIComponent(customer_id) +
                "&vatCode=" + encodeURIComponent(vatCode) +
                "&fiscalCode=" + encodeURIComponent(fiscalCode) +
                "&denomination=" + encodeURIComponent(denomination) +
                "&phoneNumber=" + encodeURIComponent(phoneNumber) +
                "&faxNumber=" + encodeURIComponent(faxNumber) +
                "&cellNumber=" + encodeURIComponent(cellNumber) +
                "&email=" + encodeURIComponent(email) +
                "&city=" + encodeURIComponent(city) +
                "&address=" + encodeURIComponent(address) +
                "&houseNumber=" + encodeURIComponent(houseNumber) +
                "&postalCode=" + encodeURIComponent(postalCode) +
                "&province=" + encodeURIComponent(province) +
                "&country=" + encodeURIComponent(country) +
                "&logo=" + encodeURIComponent(logo) +
                "&paymentConditions=" + encodeURIComponent(paymentConditions) +
                "&bank=" + encodeURIComponent(bank) +
                "&CAB=" + encodeURIComponent(CAB) +
                "&ABI=" + encodeURIComponent(ABI) +
                "&IBAN=" + encodeURIComponent(IBAN) +
                "&foreignIBAN=" + encodeURIComponent(foreignIBAN) +
                "&notes=" + encodeURIComponent(notes) +
                "&VATExemptionText=" + encodeURIComponent(VATExemptionText) +
                "&univocalCode=" + encodeURIComponent(univocalCode) +
                "&pec=" + encodeURIComponent(pec) +
                "&modalitaPagamento=" + encodeURIComponent(modalitaPagamento) +
                "&vatExemptionProtocol=" + encodeURIComponent(vatExemptionProtocol) +
                "&vatExemptionDate=" + encodeURIComponent(vatExemptionDate);
        fetch(url, {credentials: 'same-origin'})
                .then(response => response.json())
                .then(jsonResponse =>
                {
                    if (jsonResponse.success)
                        successCallback(jsonResponse.customers);
                    else
                        failCallback();
                })
                .catch(error => alert(error)).finally(() => {
            document.getElementById("modal").style.display = "none";
            document.getElementById("loader").style.display = "none";
        });
    }
};

app.checkVatCode = function (vatCode, successCallback, failCallback)
{
    var url = encodeURI("/Samurai/gate?&op=check_vatcode&vatCode=" + vatCode);

    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.denominations);
                else
                    failCallback();
            })
            .catch(error => alert(error));
};


//  JOBTYPES 

/**
 * 
 * @param {type} name
 * @param {type} description
 * @param {type} successCallback
 * @param {type} failCallback
 * @returns {undefined}
 */
app.createJobType = function (name, description, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";

    var url = encodeURI('/Samurai/gate?&op=create_jobtype&name=' + name + '&description=' + description);

    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.jobType_id);
                else
                    failCallback();
            })
            .catch(error => alert(error)).finally(() => {
        document.getElementById("modal").style.display = "none";
        document.getElementById("loader").style.display = "none";
    });
};

app.readJobTypes = function (jobType_id, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";

    var url = encodeURI("/Samurai/gate?&op=read_jobtypes");
    if (jobType_id !== null)
        url = encodeURI("/Samurai/gate?&op=read_jobtypes&jobType_id=" + jobType_id);

    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.jobTypes);
                else
                    failCallback();
            })
            .catch(error => alert(error)).finally(() => {
        document.getElementById("modal").style.display = "none";
        document.getElementById("loader").style.display = "none";
    });
};
/**/
app.updateJobType = function (jobType_id, name, description, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";

    if (jobType_id !== null)
    {
        var url = encodeURI("/Samurai/gate?&op=update_jobType&jobType_id=" + jobType_id + "&name=" + name + "&description=" + description);
        fetch(url, {credentials: 'same-origin'})
                .then(response => response.json())
                .then(jsonResponse =>
                {
                    if (jsonResponse.success)
                        successCallback(jsonResponse.jobTypes);
                    else
                        failCallback();
                })
                .catch(error => alert(error)).finally(() => {
            document.getElementById("modal").style.display = "none";
            document.getElementById("loader").style.display = "none";
        });
    }
};


//   JOBSUBTYPES 

/**
 * 
 * @param {type} name
 * @param {type} description
 * @param {type} successCallback
 * @param {type} failCallback
 * @returns {undefined}
 */
app.createJobsubtype = function (name, description, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";

    var url = encodeURI('/Samurai/gate?&op=create_jobsubtype&name=' + name + '&description=' + description);

    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.jobSubtype_id);
                else
                    failCallback();
            })
            .catch(error => alert(error)).finally(() => {
        document.getElementById("modal").style.display = "none";
        document.getElementById("loader").style.display = "none";
    });
};

app.readJobSubtypes = function (jobSubtype_id, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";

    var url = encodeURI("/Samurai/gate?&op=read_jobsubtype");
    if (jobSubtype_id !== null)
        url = encodeURI("/Samurai/gate?&op=read_jobsubtype&jobSubtype_id=" + jobSubtype_id);

    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.jobSubtypes);
                else
                    failCallback();
            })
            .catch(error => alert(error)).finally(() => {
        document.getElementById("modal").style.display = "none";
        document.getElementById("loader").style.display = "none";
    });
};
/**/
app.updateJobSubtype = function (jobSubtype_id, name, description, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";

    if (jobSubtype_id !== null)
    {
        var url = encodeURI("/Samurai/gate?&op=update_jobsubtype&jobSubtype_id=" + jobSubtype_id + "&name=" + name + "&description=" + description);
        fetch(url, {credentials: 'same-origin'})
                .then(response => response.json())
                .then(jsonResponse =>
                {
                    if (jsonResponse.success)
                        successCallback(jsonResponse.jobSubtypes);
                    else
                        failCallback();
                })
                .catch(error => alert(error)).finally(() => {
            document.getElementById("modal").style.display = "none";
            document.getElementById("loader").style.display = "none";
        });
    }
};

//  TRANSLATIONS CENTERS 

/**
 * 
 * @param {type} denomination
 * @param {type} successCallback
 * @param {type} failCallback
 * @returns {undefined}
 */
app.createTranslationsCenter = function (denomination, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";

    var url = encodeURI('/Samurai/gate?&op=create_translations_center&denomination=' + denomination);

    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.translations_center_id);
                else
                    failCallback();
            })
            .catch(error => alert(error)).finally(() => {
        document.getElementById("modal").style.display = "none";
        document.getElementById("loader").style.display = "none";
    });
};

app.readTranslationsCenters = function (translations_center_id, denominationHint, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";

    var url = encodeURI("/Samurai/gate?&op=read_translations_centers");
    if (translations_center_id !== null)
        url = encodeURI("/Samurai/gate?&op=read_translations_centers&translations_center_id=" + translations_center_id + "&denominationHint=" + denominationHint);

    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.translationsCenters);
                else
                    failCallback();
            })
            .catch(error => alert(error)).finally(() => {
        document.getElementById("modal").style.display = "none";
        document.getElementById("loader").style.display = "none";
    });
};

/**/
app.updateTranslationsCenter = function (translations_center_id, denomination, notes, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";

    if (translations_center_id !== null)
    {
        var url = encodeURI("/Samurai/gate?&op=update_translations_center&translations_center_id=" + translations_center_id + "&denomination=" + denomination + "&notes=" + notes);
        fetch(url, {credentials: 'same-origin'})
                .then(response => response.json())
                .then(jsonResponse =>
                {
                    if (jsonResponse.success)
                        successCallback(jsonResponse.translationsCenters);
                    else
                        failCallback();
                })
                .catch(error => alert(error)).finally(() => {
            document.getElementById("modal").style.display = "none";
            document.getElementById("loader").style.display = "none";
        });
    }
};


//  ORDERS 


/**
 * Long customer_id, Long user_id, String jobType, String date, String machinaryModel, String notes
 * @param {type} customer_id
 * @param {type} user_id
 * @param {type} jobType
 * @param {type} date
 * @param {type} machinaryModel
 * @param {type} notes
 * @param {type} successCallback
 * @param {type} failCallback
 * @returns {undefined}
 */
app.createOrder = function (customer_id, user_id, jobType_id, serialNumber, date, machinaryModel, notes, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    var url = encodeURI('/Samurai/gate?&op=create_order&customer_id=' + customer_id + '&user_id=' + user_id + '&jobType_id=' + jobType_id + '&serial_number=' + serialNumber + '&date=' + date + '&machinaryModel=' + machinaryModel + '&notes=' + notes);

    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.order_id);
                else
                    failCallback();
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
};

/**
 Long order_id, Long creator_id, String order_code, Long completion_state_id, String customerDenominationHint, String machinaryModelHint, String jobTypeHint, String fromDate, String toDate
 */
app.readOrders = function (
        order_id,
        creator_id,
        order_code,
        order_description,
        completion_state_id,
        serial_number,
        availability_id,
        customer_idString,
        machinaryModelHint,
        jobType_idString,
        fromDate,
        toDate,
        successCallback,
        failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";

    var url = encodeURI("/Samurai/gate?&op=read_orders");
    if (order_id !== null)
        url = url + encodeURI("&order_id=" + order_id);
    /*adds paramters if not null*/
    if (order_code !== null)
        url = url + encodeURI("&order_code=" + order_code);

    /*adds paramters if not null*/
    if (order_description !== null)
        url = url + encodeURI("&order_description=" + order_description);

    if (creator_id !== null)
        url = url + encodeURI("&creator_id=" + creator_id);

    if (completion_state_id !== null)
        url = url + encodeURI("&completion_state_id=" + completion_state_id);
    
    if (serial_number !== null)
        url = url + encodeURI("&serial_number=" + serial_number);

    if (availability_id !== null)
        url = url + encodeURI("&availability_id=" + availability_id);

    if (customer_idString !== null)
        url = url + encodeURI("&customer_idString=" + customer_idString);

    if (machinaryModelHint !== null)
        url = url + encodeURI("&machinaryModelHint=" + machinaryModelHint);

    if (jobType_idString !== null)
        url = url + encodeURI("&jobType_idString=" + jobType_idString);

    if (fromDate !== null && fromDate !== (""))
        url = url + encodeURI("&fromDate=" + fromDate);

    if (toDate !== null && toDate !== (""))
        url = url + encodeURI("&toDate=" + toDate);

    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.orders);
                else
                    failCallback();
            })
            .catch(/*error=>alert(error+" readOrder")*/)
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
};
/*order_id, customer_id, user_id, jobType_id, completionState_id, date, code, machinaryModel, notes, closingReason, successCallback, failCallback **/
app.updateOrder = function (order_id, customer_id, user_id, jobType_id, completionState_id, date, code, machinaryModel, notes, notSuggest, ordine, commessa, dataOrdine, storyData, serialNumber, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    if (order_id !== null)
    {
        /*adds paramters if not null*/
        var url = encodeURI("/Samurai/gate?&op=update_order&order_id=" + order_id);

        if (customer_id !== null)
            url = url + encodeURI("&customer_id=" + customer_id);

        if (user_id !== null)
            url = url + encodeURI("&user_id=" + user_id);

        if (jobType_id !== null)
            url = url + encodeURI("&jobType_id=" + jobType_id);

        if (completionState_id !== null)
            url = url + encodeURI("&completionState_id=" + completionState_id);

        if (date !== null)
            url = url + encodeURI("&date=" + date);

        if (code !== null)
            url = url + encodeURI("&code=" + code);

        if (machinaryModel !== null)
            url = url + encodeURI("&machinaryModel=" + machinaryModel);

        if (notes !== null)
            url = url + encodeURI("&notes=" + notes);

        if (notSuggest !== null)
            url = url + encodeURI("&notSuggest=" + notSuggest);

        if (ordine !== null)
            url = url + encodeURI("&ordine=" + ordine);

        if (commessa !== null)
            url = url + encodeURI("&commessa=" + commessa);

        if (dataOrdine !== null)
            url = url + encodeURI("&dataOrdine=" + dataOrdine);

        if (storyData !== null)
            url = url + encodeURI("&storyData=" + storyData);
        
        if (serialNumber !== null)
            url = url + encodeURI("&serialNumber=" + serialNumber);

    }
    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback();
                else
                    failCallback();
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
};

app.getValidityMachinaryModelByOrderCode = function (orderCode, customer_id, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    var url = encodeURI('/Samurai/gate?&op=get_validity_machinary_model_by_order_code&orderCode=' + orderCode + "&customer_id=" + customer_id);

    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.messages);
                else
                    failCallback();
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
};

app.getSuggestedOrders = function (customer_id, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    var url = encodeURI('/Samurai/gate?&op=get_suggested_orders&customer_id=' + customer_id);

    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.orders);
                else
                    failCallback();
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
};

app.validCode = function (orderCode, customer_id, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    var url = encodeURI('/Samurai/gate?&op=get_valid_code=' + orderCode + "&customer_id=" + customer_id);

    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.message);
                else
                    failCallback();
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
    ;
};

app.adjurnCover = function (order_id, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    var url = encodeURI('/Samurai/gate?&op=adjurn_cover&order_id=' + order_id);
    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback();
                else
                    failCallback();
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
    ;
};


/**************   TRANSPORTERS ************************************************/

app.createTransporter = function (denomination, fiscalCode, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    var url = encodeURI('/Samurai/gate?&op=create_transporter&denomination=' + denomination + '&fiscalCode=' + fiscalCode);

    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.transporter_id);
                else
                    failCallback();
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
    ;
};

app.readTransporters = function (transporter_id, denominationHint, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    var url = encodeURI("/Samurai/gate?&op=read_transporters");
    if (transporter_id !== null)
        url = encodeURI("/Samurai/gate?&op=read_transporters&transporter_id=" + transporter_id);

    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.transporters);
                else
                    failCallback();
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
    ;
};
/**/
app.updateTransporter = function (transporter_id, vatCode, fiscalCode, denomination, phoneNumber, faxNumber, cellNumber, email, city, address, houseNumber, postalCode, province, notes, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    if (transporter_id !== null)
    {
        var url = encodeURI("/Samurai/gate?&op=update_transporter&transporter_id=" + transporter_id + "&vatCode=" + vatCode + "&fiscalCode=" + fiscalCode + "&denomination=" + denomination + "&phoneNumber=" + phoneNumber + "&faxNumber=" + faxNumber + "&cellNumber=" + cellNumber + "&email=" + email + "&city=" + city + "&address=" + address + "&houseNumber=" + houseNumber + "&postalCode=" + postalCode + "&province=" + province + "&notes=" + notes);
        fetch(url, {credentials: 'same-origin'})
                .then(response => response.json())
                .then(jsonResponse =>
                {
                    if (jsonResponse.success)
                        successCallback(jsonResponse.transporters);
                    else
                        failCallback();
                })
                .catch(error => alert(error))
                .finally(() => {
                    document.getElementById("modal").style.display = "none";
                    document.getElementById("loader").style.display = "none";
                });
        ;
    }
};

app.machinaryModelsByCustomerId = function (customer_id, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    var url = encodeURI('/Samurai/gate?&op=get_machinary_models_by_customer_id&customer_id=' + customer_id);

    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.machinaryModels);
                else
                    failCallback();
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
    ;
};


/****************** TASKS ***************************************************/

app.taskUpload = function (task_id, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    let url = encodeURI("/Samurai/gate?&op=create_task_attachment&task_id=" + task_id);

    let files = document.getElementById("taskUploadChooser").files;
    let data = new FormData();
    for (let i = 0; i < files.length; i++)
        data.append(files[i].name, files[i], files[i].name);

    let params = {method: "POST", body: data, credentials: 'same-origin'};

    fetch(url, params)
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback();
                else
                    failCallback();
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
    ;
};

//Long order_id, Long user_id, String jobSubtype, String date, Double hours, String translationsCenterName, Double translationPrice, String translationQuotation, String variouseMaterialDesc, Double variouseMaterialCost, Integer transfertKms, Double transfertCost, String notes
app.createTask = function (order_code, user_id, jobSubtype_id, date, hours, translations_center_id, translationPrice, translationCost, externalJobsDesc, externalJobsHours, externalJobsCost, variouseMaterialDesc, variouseMaterialCost, transfertKms, transfertCost, notes, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    var url = encodeURI('/Samurai/gate?&op=create_task&order_code=' + order_code + '&user_id=' + user_id + '&jobSubtype_id=' + jobSubtype_id + '&date=' + date + '&hours=' + hours + '&translations_center_id=' + translations_center_id + '&translationPrice=' + translationPrice + '&translationCost=' + translationCost + '&externalJobsDesc=' + externalJobsDesc + '&externalJobsHours=' + externalJobsHours + '&externalJobsCost=' + externalJobsCost + "&variouseMaterialDesc=" + variouseMaterialDesc + '&variouseMaterialCost=' + variouseMaterialCost + '&transfertKms=' + transfertKms + '&transfertCost=' + transfertCost) + '&notes=' + encodeURIComponent(notes);

    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.task_id);
                else
                    failCallback();
            })
            .catch(error => alert(error + " createTask"))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
    ;
};

app.readTasks = function (task_id, user_id, order_id, operator_id, orderCode, orderSerialNumber, jobType_id, jobSubtype_id, customer_id, order_creator_id, fromDate, toDate, completion_state_id, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    //all tasks
    var url = encodeURI("/Samurai/gate?&op=read_tasks");
    //only one
    if (task_id !== null && task_id !== undefined)
        url = encodeURI("/Samurai/gate?&op=read_tasks&task_id=" + task_id);

    //only those related to the order
    if (user_id !== null && user_id !== undefined)
        url = encodeURI("/Samurai/gate?&op=read_tasks&user_id=" + user_id);

    //only those related to the order
    if (order_id !== null && order_id !== undefined)
        url = encodeURI("/Samurai/gate?&op=read_tasks&order_id=" + order_id);

    if (operator_id !== null && operator_id !== undefined)
        url = url + encodeURI("&operator_id=" + operator_id);

    if (orderCode !== null && orderCode !== undefined)
        url = url + encodeURI("&orderCode=" + orderCode);
    
    if (orderSerialNumber !== null && orderSerialNumber !== undefined)
        url = url + encodeURI("&orderSerialNumber=" + orderSerialNumber);

    if (jobType_id !== null && jobType_id !== undefined)
        url = url + encodeURI("&jobType_id=" + jobType_id);

    if (jobSubtype_id !== null && jobSubtype_id !== undefined)
        url = url + encodeURI("&jobSubtype_id=" + jobSubtype_id);

    if (customer_id !== null && customer_id !== undefined)
        url = url + encodeURI("&customer_id=" + customer_id);

    if (order_creator_id !== null && order_creator_id !== undefined)
        url = url + encodeURI("&order_creator_id=" + order_creator_id);

    if (fromDate !== null && fromDate !== ("") && fromDate !== undefined)
        url = url + encodeURI("&fromDate=" + fromDate);

    if (toDate !== null && toDate !== ("") && toDate !== undefined)
        url = url + encodeURI("&toDate=" + toDate);

    if (completion_state_id !== null && completion_state_id !== undefined)
        url = url + encodeURI("&completion_state_id=" + completion_state_id);

    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.tasks);
                else
                    failCallback();
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
    ;
};

//Long task_id, Long order_code, String jobSubtype, String taskDate, Double hours, Long translations_center_id, Double translationPrice, String variouseMaterialDesc, Double variouseMaterialCost, Integer transfertKms, Double transfertCost, String notes
app.updateTask = function (task_id, order_code, jobSubtype_id, taskDate, hours, translations_center_id, translationPrice, translationCost, externalJobsDesc, externalJobsHours, externalJobsCost, variouseMaterialDesc, variouseMaterialCost, transfertKms, transfertCost, notes, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    if (task_id !== null)
    {
        var url = encodeURI("/Samurai/gate?&op=update_task&task_id=" + task_id + "&order_code=" + order_code + "&jobSubtype_id=" + jobSubtype_id + "&taskDate=" + taskDate + "&hours=" + hours + "&translations_center_id=" + translations_center_id + "&translationPrice=" + translationPrice + "&translationCost=" + translationCost + "&externalJobsDesc=" + externalJobsDesc + "&externalJobsHours=" + externalJobsHours + "&externalJobsCost=" + externalJobsCost + "&variouseMaterialDesc=" + variouseMaterialDesc + "&variouseMaterialCost=" + variouseMaterialCost + "&transfertKms=" + transfertKms + "&transfertCost=" + transfertCost) + "&notes=" + encodeURIComponent(notes);
        fetch(url, {credentials: 'same-origin'})
                .then(response => response.json())
                .then(jsonResponse =>
                {
                    if (jsonResponse.success)
                        successCallback();
                    else
                        failCallback();
                })
                .catch(error => alert(error))
                .finally(() => {
                    document.getElementById("modal").style.display = "none";
                    document.getElementById("loader").style.display = "none";
                });
        ;
    }
};


/********************  TRANSPORTERS ***********************************/

app.createTransporter = function (denomination, fiscalCode, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    var url = encodeURI('/Samurai/gate?&op=create_transporter&denomination=' + denomination + '&fiscalCode=' + fiscalCode);

    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.transporter_id);
                else
                    failCallback();
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
    ;
};

app.readTransporters = function (transporter_id, denominationHint, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    var url = encodeURI("/Samurai/gate?&op=read_transporters");
    if (transporter_id !== null)
        url = encodeURI("/Samurai/gate?&op=read_transporters&transporter_id=" + transporter_id);

    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.transporters);
                else
                    failCallback();
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
    ;
};

/**/
app.updateTransporter = function (transporter_id, vatCode, fiscalCode, denomination, phoneNumber, faxNumber, cellNumber, email, city, address, houseNumber, postalCode, province, notes, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    if (transporter_id !== null)
    {
        var url = encodeURI("/Samurai/gate?&op=update_transporter&transporter_id=" + transporter_id + "&vatCode=" + vatCode + "&fiscalCode=" + fiscalCode + "&denomination=" + denomination + "&phoneNumber=" + phoneNumber + "&faxNumber=" + faxNumber + "&cellNumber=" + cellNumber + "&email=" + email + "&city=" + city + "&address=" + address + "&houseNumber=" + houseNumber + "&postalCode=" + postalCode + "&province=" + province + "&notes=" + notes);
        fetch(url, {credentials: 'same-origin'})
                .then(response => response.json())
                .then(jsonResponse =>
                {
                    if (jsonResponse.success)
                        successCallback(jsonResponse.transporters);
                    else
                        failCallback();
                })
                .catch(error => alert(error))
                .finally(() => {
                    document.getElementById("modal").style.display = "none";
                    document.getElementById("loader").style.display = "none";
                });
        ;
    }
};



/*********** CREDENTIALS **********************************/

app.changePassword = function ()
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    
    var oldUsername = document.getElementById('old_username').value;
    var oldPassword = document.getElementById('old_password').value;
    var newPassword = document.getElementById('new_password').value;
    if (oldUsername === "" || oldUsername === undefined || oldPassword === "" || oldPassword === undefined || newPassword === "" || newPassword === undefined)
    {
        alert("I campi sono tutti obbligatori!");
    } else
    {
        var url = encodeURI("/Samurai/gate?&op=change_password&oldUsername=" + oldUsername + "&oldPassword=" + oldPassword + "&newPassword=" + newPassword);
        fetch(url, {credentials: 'same-origin'})
                .then(response => response.json())
                .then(jsonResponse =>
                {
                    if (jsonResponse.success)
                    {
                        alert("Password aggiornata");
                        window.open('dashboard.jsp', '_self');
                    } else
                        alert("Aggiornamento password non riuscito. Controlla che le credenziali attuali siano corrette.");
                })
                .catch(error => alert(error))
                .finally(() => {
                    document.getElementById("modal").style.display = "none";
                    document.getElementById("loader").style.display = "none";
                });
        ;
    }

};

/**************************** ATTACHMENTS ***********************************/


/*Deletes all task attachments having null as task_id*/
app.deleteNullTaskAttachments = function (task_id, user_id, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    var url = encodeURI("/Samurai/gate?&op=delete_null_task_attachments&user_id = " + user_id);
    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(task_id);
                else
                    failCallback();
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
    ;
};

app.readTaskAttachments = function (task_id, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    var url = encodeURI("/Samurai/gate?&op=read_task_attachments&task_id=" + task_id);

    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.taskAttachments);
                else
                    failCallback();
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
    ;
};


app.deleteTaskAttachment = function (attachment_id, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    var url = encodeURI("/Samurai/gate?&op=delete_task_attachment&attachment_id=" + attachment_id);
    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.attachment_id);
                else
                    failCallback();
            })
            .catch(/*error=>alert(error)*/)
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
    ;
};

/******************* DELIVERY NOTES ***************************************/

app.createDeliveryNote = function (newDeliveryNote, suggestRows, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    var url = '/Samurai/gate?&op=create_delivery_note&suggest=' + suggestRows;

    let params = {method: "POST", body: JSON.stringify(newDeliveryNote)};

    fetch(url, params)
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.deliveryNote_id);
                else
                    failCallback();
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
};

app.readDeliveryNotes = function (deliveryNote_id, customer_id, transporter_id, number, fromDate, toDate, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    //all tasks
    var url = encodeURI("/Samurai/gate?&op=read_delivery_notes");

    //if number is not empty all other criteria must be ignored
    if (number !== "" && number !== null && number !== undefined) {
        url = url + encodeURI("&number=" + number);
    } else {

        if (deliveryNote_id !== null && deliveryNote_id !== undefined)
            url = url + encodeURI("&deliveryNote_id=" + deliveryNote_id);

        if (customer_id !== null && customer_id !== undefined)
            url = url + encodeURI("&customer_id=" + customer_id);

        if (transporter_id !== null && transporter_id !== undefined)
            url = url + encodeURI("&transporter_id=" + transporter_id);

        if (fromDate !== null && fromDate !== ("") && fromDate !== undefined)
            url = url + encodeURI("&fromDate=" + fromDate);

        if (toDate !== null && toDate !== ("") && toDate !== undefined)
            url = url + encodeURI("&toDate=" + toDate);
    }

    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.deliveryNotes);
                else
                    failCallback();
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
};

app.readDeliveryNoteRows = function (deliveryNote_id, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    var url = encodeURI("/Samurai/gate?&op=read_delivery_note_rows&deliveryNote_id=" + deliveryNote_id);
    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.rows);
                else
                    failCallback();
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
};
//customer_id, transporter_id, date, destDenomination, destCity, destAddress, destHouseNumber,  destPostalCode,  destProvince,  transportResponsable,  transportReason,  goodsExteriorAspect,  packagesNumber,  weight,  notes

app.updateDeliveryNote = function (deliveryNote, suggest, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    var url = '/Samurai/gate?&suggest=' + suggest + '&op=update_delivery_note';

    let params = {method: "POST", body: JSON.stringify(deliveryNote)};

    fetch(url, params)
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.deliveryNote_id);
                else
                    failCallback();
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
};

app.getSuggestedDeliveryNotesRows = function (customer_id, orderCode, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    var url = encodeURI('/Samurai/gate?&op=get_suggested_delivery_notes_rows&customer_id=' + customer_id + '&orderCode=' + orderCode);

    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.deliveryNotesRows);
                else
                    failCallback();
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
};


app.printDeliveryNote = function (deliveryNote_id, user_id, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    var url = encodeURI('/Samurai/gate?&op=print_delivery_note&deliveryNote_id=' + deliveryNote_id + "&user_id=" + user_id);
    //var url = encodeURI( '/Samurai/gate?&op=create_all_new_pdf_files&deliveryNote_id='+deliveryNote_id + "&user_id=" + user_id );


    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse);
                else
                    failCallback(jsonResponse);
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
};

/******************* INVOICES ***************************************/

app.createInvoice = function (newInvoice, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    var url = '/Samurai/gate?&op=create_invoice';

    let params = {method: "POST", body: JSON.stringify(newInvoice)};

    fetch(url, params)
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.invoice);
                else
                    failCallback();
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
};

app.readInvoices = function (invoice_id, customer_id, number, fromDate, toDate, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    //all tasks
    var url = encodeURI("/Samurai/gate?&op=read_invoices");

    if (number !== "" && number !== null && number !== undefined) {
        url = url + encodeURI("&number=" + number);
    } else {

        if (invoice_id !== null && invoice_id !== undefined)
            url = url + encodeURI("&invoice_id=" + invoice_id);

        if (customer_id !== null && customer_id !== undefined)
            url = url + encodeURI("&customer_id=" + customer_id);

        if (fromDate !== null && fromDate !== ("") && fromDate !== undefined)
            url = url + encodeURI("&fromDate=" + fromDate);

        if (toDate !== null && toDate !== ("") && toDate !== undefined)
            url = url + encodeURI("&toDate=" + toDate);
    }

    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.invoices);
                else
                    failCallback();
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
};

app.readInvoiceRows = function (invoice_id, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    var url = encodeURI("/Samurai/gate?&op=read_invoice_rows&invoice_id=" + invoice_id);
    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.rows);
                else
                    failCallback(jsonResponse.message);
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
};
//customer_id, transporter_id, date, destDenomination, destCity, destAddress, destHouseNumber,  destPostalCode,  destProvince,  transportResponsable,  transportReason,  goodsExteriorAspect,  packagesNumber,  weight,  notes

app.updateInvoice = function (invoice, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    var url = '/Samurai/gate?&op=update_invoice';

    let params = {method: "POST", body: JSON.stringify(invoice)};

    fetch(url, params)
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.invoice_id);
                else
                    failCallback();
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
};

app.printInvoice = function (invoice_id, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    var url = encodeURI('/Samurai/gate?&op=print_invoice&invoice_id=' + invoice_id);

    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback();
                else
                    failCallback();
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
};

app.getXML = function (invoice_id, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    var url = encodeURI('/Samurai/gate?&op=get_xml&invoice_id=' + invoice_id);

    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback();
                else
                    failCallback(jsonResponse.message);
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
};

app.updateInvoiceDate = function (date, number, year, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    var url = encodeURI('/Samurai/gate?&op=update_invoice_date&date=' + date + '&number=' + number + '&year=' + year);

    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback();
                else
                    failCallback(jsonResponse.message);
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
};



// QUOTES
app.createQuote = function (newQuote, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    var url = '/Samurai/gate?&op=create_quote';

    let params = {method: "POST", body: JSON.stringify(newQuote)};

    fetch(url, params)
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.quote);
                else
                    failCallback();
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
};

app.updateQuote = function (quote, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    var url = '/Samurai/gate?&op=update_quote';

    let params = {method: "POST", body: JSON.stringify(quote)};

    fetch(url, params)
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.quote);
                else
                    failCallback();
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
};

app.readQuotes = function (quote_id, customer_id, number, fromDate, toDate, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    //all tasks
    var url = encodeURI("/Samurai/gate?&op=read_quotes");

    //if the search is by number all ather filters mus be ignored
    if (number !== null && number !== undefined && number !== "") {
        url = url + encodeURI("&number=" + number);
    } else {

        if (quote_id !== null && quote_id !== undefined)
            url = url + encodeURI("&quote_id=" + quote_id);

        if (customer_id !== null && customer_id !== undefined)
            url = url + encodeURI("&customer_id=" + customer_id);

        if (number !== null && number !== undefined)
            url = url + encodeURI("&number=" + number);

        if (fromDate !== null && fromDate !== ("") && fromDate !== undefined)
            url = url + encodeURI("&fromDate=" + fromDate);

        if (toDate !== null && toDate !== ("") && toDate !== undefined)
            url = url + encodeURI("&toDate=" + toDate);
    }

    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.quotes);
                else
                    failCallback();
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
};

app.readQuoteRows = function (quote_id, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    var url = encodeURI("/Samurai/gate?&op=read_quote_rows&quote_id=" + quote_id);
    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.rows);
                else
                    failCallback(jsonResponse.message);
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
};

app.quoteUpload = function (quote_id, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    let url = encodeURI("/Samurai/gate?&op=create_quote_attachment&quote_id=" + quote_id);

    let files = document.getElementById("quoteUploadChooser").files;
    let data = new FormData();
    for (let i = 0; i < files.length; i++)
        data.append(files[i].name, files[i], files[i].name);

    let params = {method: "POST", body: data, credentials: 'same-origin'};

    fetch(url, params)
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback();
                else
                    failCallback();
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
};

app.readQuoteAttachments = function (quote_id, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    var url = encodeURI("/Samurai/gate?&op=read_quote_attachments&quote_id=" + quote_id);

    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.quoteAttachments);
                else
                    failCallback();
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
};

app.deleteQuoteAttachment = function (attachment_id, quote_id, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    var url = encodeURI("/Samurai/gate?&op=delete_quote_attachment&attachment_id=" + attachment_id + "&quote_id=" + quote_id);
    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.quote_id);
                else
                    failCallback();
            })
            .catch(/*error=>alert(error)*/)
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
};

app.printQuote = function (quote_id, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    var url = encodeURI('/Samurai/gate?&op=print_quote&quote_id=' + quote_id);

    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback();
                else
                    failCallback();
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
};

app.getSinglePdf = function (quote_id, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    var url = encodeURI('/Samurai/gate?&op=merge_pdf_quote&quote_id=' + quote_id);

    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback();
                else
                    failCallback();
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
};


//COMPLIANCE CERTIFICATES
app.createCertificate = function (newCertificate, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    var url = '/Samurai/gate?&op=create_certificate&newCertificate=' + encodeURIComponent(JSON.stringify(newCertificate));

    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.certificate);
                else
                    failCallback();
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
};

app.readCertificate = function (cert_id, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    var url = encodeURI("/Samurai/gate?&op=read_certificate&cert_id=" + cert_id);
    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.certificate);
                else
                    failCallback(jsonResponse.message);
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
};

app.updateCertificate = function (certificate, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    var url = '/Samurai/gate?&op=update_certificate&certificate=' + encodeURIComponent(JSON.stringify(certificate));

    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.certificate);
                else
                    failCallback();
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
};

//                 CREDTI NOTES

app.createCreditNote = function (newCreditNote, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    var url = '/Samurai/gate?&op=create_credit_note';

    let params = {method: "POST", body: JSON.stringify(newCreditNote)};

    fetch(url, params)
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.creditNote);
                else
                    failCallback();
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
};

app.readCreditNotes = function (creditNote_id, customer_id, number, fromDate, toDate, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    //all tasks
    var url = encodeURI("/Samurai/gate?&op=read_credit_notes");

    if (creditNote_id !== null && creditNote_id !== undefined)
        url = url + encodeURI("&creditNote_id=" + creditNote_id);

    if (customer_id !== null && customer_id !== undefined)
        url = url + encodeURI("&customer_id=" + customer_id);

    if (number !== null && number !== undefined)
        url = url + encodeURI("&number=" + number);

    if (fromDate !== null && fromDate !== ("") && fromDate !== undefined)
        url = url + encodeURI("&fromDate=" + fromDate);

    if (toDate !== null && toDate !== ("") && toDate !== undefined)
        url = url + encodeURI("&toDate=" + toDate);

    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.creditNotes);
                else
                    failCallback();
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
};

app.readCreditNoteRows = function (creditNote_id, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";

    var url = encodeURI("/Samurai/gate?&op=read_credit_note_rows&creditNote_id=" + creditNote_id);

    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.rows);
                else
                    failCallback(jsonResponse.message);
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
};
//customer_id, transporter_id, date, destDenomination, destCity, destAddress, destHouseNumber,  destPostalCode,  destProvince,  transportResponsable,  transportReason,  goodsExteriorAspect,  packagesNumber,  weight,  notes

app.updateCreditNote = function (creditNote, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    var url = '/Samurai/gate?&op=update_credit_note';

    let params = {method: "POST", body: JSON.stringify(creditNote)};

    fetch(url, params)
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.creditNote_id);
                else
                    failCallback();
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
};
//to be implemented
app.printCreditNote = function (creditNote_id, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    var url = encodeURI('/Samurai/gate?&op=print_credit_note&creditNote_id=' + creditNote_id);

    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback();
                else
                    failCallback();
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
};
//to be implemented
app.getCreditNoteXML = function (creditNote_id, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    var url = encodeURI('/Samurai/gate?&op=get_credit_note_xml&creditNote_id=' + creditNote_id);

    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback();
                else
                    failCallback(jsonResponse.message);
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
};

//SCHEDULE DATES
app.readScheduleDates = function (customer_id, fromDate, toDate, successCallback, failCallback)
{
    document.getElementById("modal").style.display = "block";
    document.getElementById("loader").style.display = "block";
    //all tasks
    var url = encodeURI("/Samurai/gate?&op=read_amount_schedule_dates");

    if (customer_id !== null && customer_id !== undefined)
        url = url + encodeURI("&customer_id=" + customer_id);

    if (fromDate !== null && fromDate !== ("") && fromDate !== undefined) {
        url = url + encodeURI("&fromDate=" + fromDate);
    }

    if (toDate !== null && toDate !== ("") && toDate !== undefined)
        url = url + encodeURI("&toDate=" + toDate);

    fetch(url, {credentials: 'same-origin'})
            .then(response => response.json())
            .then(jsonResponse =>
            {
                if (jsonResponse.success)
                    successCallback(jsonResponse.amountSchedules);
                else
                    failCallback();
            })
            .catch(error => alert(error))
            .finally(() => {
                document.getElementById("modal").style.display = "none";
                document.getElementById("loader").style.display = "none";
            });
};