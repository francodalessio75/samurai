package com.dalessio.samurai;

import com.dps.dbi.DbResult;
import com.dps.dbi.DbResultsBundle;
import com.dps.dbi.impl.SqlServerInterface;
import com.dps.dbi.impl.SqlServerInterface.Updater;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import util.MyTimer;

public class DataAccessObject {
    //public static SqlServerConnectionPool connectionPool = null;

    /*public static SqlServerInterface dbi = new SqlServerInterface()
            .address(Config.SERVER_ADDRESS)
            .name("WorkLine")
            .username("workline")
            .password("workline");*/
    public static final SqlServerInterface dbi = new SqlServerInterface("jdbc/samurai");

    static {
        //try{connectionPool = new SqlServerConnectionPool(Config.SERVER_ADDRESS,1433, "WorkLine", "workline", "workline");}
        //catch(SQLException ex){System.err.println("EXCEPTION CREATING POOL : "+ex);}
        MyTimer myTimer = new MyTimer();
        //myTimer.killConnections(dbi) ;

        dbi.disconnectAfterExecute = false;
    }

    /**
     * ***********DATE UTILITIES *********************************************
     */
    //English date format
    public static DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    //European date format
    public static DateTimeFormatter DTFE = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /**
     * Constructor
     *
     * @throws ClassNotFoundException
     */
    public DataAccessObject() throws ClassNotFoundException {
        //dbi = new SqlServerInterface().pool(connectionPool);

        /*
        dbi = new SqlServerInterface()
            .address(Config.SERVER_ADDRESS)
            .name("WorkLine")
            .username("workline")
            .password("workline");*/
        //dbi.out = null;
    }

    /**
     * Calls the DB to verify if exists an user having these credentials
     *
     * @param username
     * @param password
     * @return
     * @throws SQLException
     */
    Long authenticate(String username, String password, String session_id) throws SQLException {
        Long user_id = 0L;
        //execute the query
        DbResult dbr_user = dbi.read("reg_Users").andWhere("[username] = '" + username + "'").andWhere("[password] = '" + password + "'").go();
        /*obj2sql*/
        //if there's a row in the result
        if (dbr_user.rowsCount() == 1) {
            String sql = "select session_id from  reg_Users where user_id = " + dbr_user.getLong("user_id");
            DbResult dbr_session = dbi.execute(sql).result();
            //if the user is already logged
            if (dbr_session.getString("session_id") != null) {
                user_id = -dbr_user.getLong("user_id");
            } //if the useer is not already logged
            else {
                user_id = dbr_user.getLong("user_id");
                //inserts session_id value in db
                dbi.update("reg_Users").value("session_id", session_id).where("user_id=" + user_id).go();
            }
        }

        return user_id;
    }

    /**
     * ************************ USERS ****************************************
     */
    /**
     * Checks if the user is active
     *
     * @param user_id
     * @return true = active user
     * @throws SQLException
     */
    Boolean userActive(Long user_id) throws SQLException {
        //it is possible thet the user is already logged , in this case the method dao.authenticate( username, password,request.getSession().getId()) called 
        //in the Gate inside the method authenticate(HttpServletRequest request, JsonObject jsonResponse) gives backe negative valid user_id. Then it must be 
        // converted in positive
        if (user_id < 0) {
            user_id *= -1;
        }

        return dbi.read("reg_Users")
                .andWhere("[user_id] = " + user_id)
                .go().getBoolean("active");
    }

    /**
     * User Creation
     *
     * @param username
     * @param password
     * @return
     * @throws SQLException
     */
    Long createUser(String firstName, String lastName, String username, String password) throws SQLException {
        return dbi.create("reg_Users")
                .value("firstName", firstName)
                .value("lastName", lastName)
                .value("username", username)
                .value("password", password)
                .goAndGetId();
    }

    /**
     *
     * @param user_id
     * @param username
     * @param password
     * @param rolesHint
     * @param active
     * @param fiscalCodeHint
     * @param firstNameHint
     * @param lastNameHint
     * @param phoneNumberHint
     * @param cellNumberHint
     * @param emailHint
     * @param notesHint
     * @return
     * @throws SQLException
     */
    public DbResult readUsers(Long user_id, String username, String password, String rolesHint, Integer active, String fiscalCodeHint, String firstNameHint, String lastNameHint, String phoneNumberHint, String cellNumberHint, String emailHint, String notesHint) throws SQLException {
        return dbi.read("reg_Users").order("firstName")
                .andWhere(user_id != null, "[user_id] = " + user_id)
                .andWhere(username != null, " [username] LIKE '%" + username + "%'")
                .andWhere(password != null, " [password] LIKE '%" + password + "%'")
                .andWhere(rolesHint != null, " [roles] LIKE '%" + rolesHint + "%'")
                .andWhere(active != null, "[active] = " + active)
                .andWhere(fiscalCodeHint != null, " [fiscalCodeHint] LIKE '%" + fiscalCodeHint + "%'")
                .andWhere(firstNameHint != null, " [firstName] LIKE '%" + firstNameHint + "%'")
                .andWhere(lastNameHint != null, " [lastName] LIKE '%" + lastNameHint + "%'")
                .andWhere(phoneNumberHint != null, " [phoneNumber] LIKE '%" + phoneNumberHint + "%'")
                .andWhere(cellNumberHint != null, " [cellNumber] LIKE '%" + cellNumberHint + "%'")
                .andWhere(emailHint != null, " [email] LIKE '%" + emailHint + "%'")
                .andWhere(notesHint != null, " [notes] LIKE '%" + notesHint + "%'")
                .go();
    }

    public DbResult readUsers(Long user_id) throws SQLException {
        return dbi.read("reg_Users").order("firstName")
                .andWhere(user_id != null, " [user_id] =  " + user_id).go();
    }

    /**
     * If a parameter is null the corresponding value won't be updated
     *
     * @param user_id
     * @param username
     * @param password
     * @param roles
     * @param active
     * @param fiscalCode
     * @param firstName
     * @param lastName
     * @param phoneNumber
     * @param cellNumber
     * @param email
     * @param city
     * @param address
     * @param houseNumber
     * @param postalCode
     * @param province
     * @param notes
     * @return
     * @throws SQLException
     */
    boolean updateUser(Long user_id, String username, String password, Double hourlyCost, String role, String active, String fiscalCode, String firstName, String lastName, String phoneNumber, String cellNumber, String email, String notes) throws SQLException {
        //converts boolean
        Integer isActive = null;

        if (active != null) {
            isActive = active.equals("true") ? 1 : 0;
        }

        Updater updater = dbi.update("reg_Users")
                .where("user_id = " + user_id)
                .value(username != null, "username", username)
                .value(password != null, "password", password)
                .value(hourlyCost != null, "hourlyCost", hourlyCost)
                .value(role != null, "role", role)
                .value(isActive != null, "active", isActive)
                .value(fiscalCode != null, "fiscalCode", fiscalCode)
                .value(firstName != null, "firstName", firstName)
                .value(lastName != null, "lastName", lastName)
                .value(phoneNumber != null, "phoneNumber", phoneNumber)
                .value(cellNumber != null, "cellNumber", cellNumber)
                .value(email != null, "email", email)
                .value(notes != null, "notes", notes).go();

        //if all parameters are null return true without querying
        if (updater.getUpdateCount() == null) {
            return true;
        } else {
            return updater.getUpdateCount() == 1;
        }
    }

    /**
     * gives back the role of the user
     *
     * @param user_id
     * @return
     * @throws SQLException
     */
    public String getUserRole(Long user_id) throws SQLException {
        return dbi.read("reg_Users").andWhere("[user_id] = " + user_id).go().getString("role");
    }

    /**
     *
     * @param user_id
     * @param oldUsername
     * @param oldPassword
     * @param newPassword
     * @return
     * @throws SQLException
     */
    boolean changePassword(Long user_id, String oldUsername, String oldPassword, String newPassword) throws SQLException {
        //Retrieves old credentials
        DbResult reader = dbi.read("reg_Users")
                .andWhere("user_id = '" + user_id + "'")
                .andWhere("username = '" + oldUsername + "'")
                .andWhere("password = '" + oldPassword + "'")
                .andWhere("active = " + 1)
                .go();

        if (reader.rowsCount() == 1) {
            Updater updater = dbi.update("reg_Users");

            updater.where("user_id = " + user_id);

            //changes the password
            if (newPassword != null) {
                updater.value("password", newPassword);
            }
            //if all parameters are null return true without querying
            if (updater.go().getUpdateCount() == null) {
                return true;
            } else {
                return updater.go().getUpdateCount() == 1;
            }
        } else {
            return false;
        }

    }

    //USER ROLES
    public DbResultsBundle readUsersRoles() throws SQLException {
        //select statement
        String sql = " SELECT  DISTINCT [role] FROM reg_Users ORDER BY role ";
        //executes the statement 
        return dbi.execute(sql);
    }

    //restore user accesibility
    public void restoreUser(String session_id) {
        String sql = "UPDATE reg_Users set session_id = NULL WHERE session_id = '" + session_id + "'";
        //dbi.update( "reg_Users" ).value( "session_id", null ).where("session_id = " + session_id).go();
        dbi.execute(sql);
    }

    //restore user accesibility
    public void restoreUserByUsername(String username) throws SQLException {
        String sql = "UPDATE reg_Users set session_id = NULL WHERE username = '" + username + "'";
        //dbi.update( "reg_Users" ).value( "session_id", null ).where("username = " + username).go();
    }

    /**
     * ************************ CUSTOMERS ************************************
     */
    /**
     *
     * @param denomination
     * @param fiscalCode
     * @return
     * @throws SQLException
     */
    Long createCustomer(String denomination, String vatCode) throws SQLException {
        return dbi.create("reg_Customers")
                .value("denomination", denomination.trim()).value("vatCode", vatCode).value("idFiscaleIva_paese", "IT").value("modalitaPagamento", "").goAndGetId();
    }

    /**
     *
     * @param customer_id
     * @param vatCodeHint
     * @param fiscalCodeHint
     * @param denominationHint
     * @param phoneNumberHint
     * @param faxNumberHint
     * @param cellNumberHint
     * @param emailHint
     * @param cityHint
     * @param addressHint
     * @param houseNumberHint
     * @param postalCodeHint
     * @param provinceHint
     * @param notesHint
     * @return
     * @throws SQLException      *
     */
    public DbResult readCustomers(Long customer_id, String vatCodeHint, String fiscalCodeHint, String denominationHint, String phoneNumberHint, String faxNumberHint, String cellNumberHint, String emailHint, String cityHint, String addressHint, String houseNumberHint, String postalCodeHint, String provinceHint, String notesHint) throws SQLException {
        return dbi.read("reg_Customers").order("denomination")
                .andWhere(customer_id != null, "[customer_id] = " + customer_id)
                /* vat code is not a %LIKE% becouse is used too check if the vatcode exists in customer creation byt the user*/
                .andWhere(vatCodeHint != null, " vatCode = '" + vatCodeHint + "'")
                .andWhere(fiscalCodeHint != null, " [fiscalCode] LIKE '%" + fiscalCodeHint + "%'")
                .andWhere(denominationHint != null, " [denomination] LIKE '%" + denominationHint + "%'")
                .andWhere(phoneNumberHint != null, " [phoneNumber] LIKE '%" + phoneNumberHint + "%'")
                .andWhere(faxNumberHint != null, " [faxNumber] LIKE '%" + faxNumberHint + "%'")
                .andWhere(cellNumberHint != null, " [cellNumber] LIKE '%" + cellNumberHint + "%'")
                .andWhere(emailHint != null, " [email] LIKE '%" + emailHint + "%'")
                .andWhere(cityHint != null, " [city] LIKE '%" + cityHint + "%'")
                .andWhere(addressHint != null, " [address] LIKE '%" + addressHint + "%'")
                .andWhere(houseNumberHint != null, " [houseNumber] LIKE '%" + houseNumberHint + "%'")
                .andWhere(postalCodeHint != null, " [postalCode] LIKE '%" + postalCodeHint + "%'")
                .andWhere(provinceHint != null, " [province] LIKE '%" + provinceHint + "%'")
                .andWhere(notesHint != null, " [notes] LIKE '%" + notesHint + "%'")
                .go();
    }

    /**
     *
     * @return @throws SQLException      *
     */
    public DbResult readAllCustomers() throws SQLException {
        return dbi.read("reg_Customers")
                .column("customer_id")
                .column("denomination")
                .order("denomination")
                .go();
    }

    public String getCustomerDenominationById(Long customer_id) throws SQLException {
        return dbi.read("reg_Customers")
                .andWhere("customer_id = " + customer_id).go().getString("denomination");
    }

    /**
     * If a parameter is null the corresponding value won't be updated
     *
     * @param customer_id
     * @param vatCode
     * @param fiscalCode
     * @param denomination
     * @param phoneNumber
     * @param faxNumber
     * @param cellNumber
     * @param email
     * @param city
     * @param address
     * @param houseNumber
     * @param postalCode
     * @param province
     * @param notes
     * @return
     * @throws SQLException
     */
    boolean updateCustomer(
            Long customer_id,
            String vatCode,
            String fiscalCode,
            String denomination,
            String phoneNumber,
            String faxNumber,
            String cellNumber,
            String email,
            String city,
            String address,
            String houseNumber,
            String postalCode,
            String province,
            String country,
            String logo,
            String paymentConditions,
            String bank, String CAB,
            String ABI, String IBAN,
            String foreignIBAN,
            String notes,
            String VATExemptionText,
            String univocalCode,
            String pec,
            String modalitaPagamento,
            String vatExemptionProtocol,
            String vatExemtpionDate) throws SQLException {
        String vatExemptionDateString = vatExemtpionDate == null ? null : vatExemtpionDate.replace("-", "");
        if (vatCode != null) {
            vatCode = vatCode.trim();
        }
        if (fiscalCode != null) {
            fiscalCode = fiscalCode.trim();
        }
        if (denomination != null) {
            denomination = denomination.trim();
        }
        if (phoneNumber != null) {
            phoneNumber = phoneNumber.trim();
        }
        if (faxNumber != null) {
            faxNumber = faxNumber.trim();
        }
        if (cellNumber != null) {
            cellNumber = cellNumber.trim();
        }
        if (email != null) {
            email = email.trim();
        }
        if (city != null) {
            city = city.trim();
        }
        if (address != null) {
            address = address.trim();
        }
        if (houseNumber != null) {
            houseNumber = houseNumber.trim();
        }
        if (postalCode != null) {
            postalCode = postalCode.trim();
        }
        if (province != null) {
            province = province.trim();
        }
        if (country != null) {
            country = country.trim();
        }
        if (logo != null) {
            logo = logo.trim();
        }
        if (paymentConditions != null) {
            paymentConditions = paymentConditions.trim();
        }
        if (bank != null) {
            bank = bank.trim();
        }
        if (CAB != null) {
            CAB = CAB.trim();
        }
        if (ABI != null) {
            ABI = ABI.trim();
        }
        if (IBAN != null) {
            IBAN = IBAN.trim().replace(" ", "");
        }
        if (foreignIBAN != null) {
            foreignIBAN = foreignIBAN.trim().replace(" ", "");
        }

        Updater updater = dbi.update("reg_Customers")
                .where("customer_id = " + customer_id)
                .value(vatCode != null, "vatCode", vatCode)
                .value(fiscalCode != null, "fiscalCode", fiscalCode)
                .value(denomination != null, "denomination", denomination)
                .value(phoneNumber != null, "phoneNumber", phoneNumber)
                .value(faxNumber != null, "faxNumber", faxNumber)
                .value(cellNumber != null, "cellNumber", cellNumber)
                .value(email != null, "email", email)
                .value(city != null, "city", city)
                .value(address != null, "address", address)
                .value(houseNumber != null, "houseNumber", houseNumber)
                .value(postalCode != null, "postalCode", postalCode)
                .value(province != null, "province", province)
                .value(country != null, "country", country)
                .value(country != null, "idFiscaleIva_paese", country)
                .value(logo != null, "logo", logo)
                .value(paymentConditions != null, "paymentConditions", paymentConditions)
                .value(bank != null, "bank", bank)
                .value(CAB != null, "CAB", CAB)
                .value(ABI != null, "ABI", ABI)
                .value(IBAN != null, "IBAN", IBAN)
                .value(foreignIBAN != null, "foreignIBAN", foreignIBAN)
                .value(notes != null, "notes", notes)
                .value(VATExemptionText != null, "VATExemptionText", VATExemptionText)
                .value(univocalCode != null, "univocalCode", univocalCode)
                .value(pec != null, "pec", pec)
                .value(modalitaPagamento != null, "modalitaPagamento", modalitaPagamento)
                .value(vatExemptionProtocol != null, "exemptionProtocol", vatExemptionProtocol)
                .value(vatExemptionDateString != null, "exemptionDate", vatExemptionDateString)
                .go();

        //if all paramters are null returns true
        if (updater.getUpdateCount() == null) {
            return true;
        } else {
            return updater.getUpdateCount() == 1;
        }
    }

    boolean customerLogoAdjurned(Long customer_id) throws SQLException {
        return dbi.read("reg_Customers")
                .andWhere("customer_id = " + customer_id)
                .go().getBoolean("logoAdjurned");

    }

    public void adjurnCustomerLogoByOrderId(Long order_id, Boolean value) throws SQLException {
        //retrieves the customer_id
        Long customer_id = dbi.read("dyn_Orders")
                .andWhere("order_id = " + order_id)
                .go().getLong("customer_id");

        adjurnCustomerLogoByCustomerId(customer_id, value);
    }

    public void adjurnCustomerLogoByCustomerId(Long customer_id, Boolean value) throws SQLException {
        int bitValue = value ? 1 : 0;

        //apdate the customer table
        dbi.update("reg_Customers")
                .andWhere("customer_id = " + customer_id)
                .value("logoAdjurned", bitValue)
                .go();
    }

    public String getLogoPath(Long customer_id) throws SQLException {
        return dbi.read("reg_Customers")
                .andWhere("customer_id = " + customer_id)
                .go().getString("logo");
    }

    //PAYMENTS CONDITIONS
    public DbResultsBundle readPaymentConditions() throws SQLException {
        //select statement
        String sql = " SELECT  DISTINCT [paymentConditions] FROM reg_Customers ORDER BY paymentConditions ";
        //executes the statement 
        return dbi.execute(sql);
    }

    public String getPayModeByCustomerId(Long customerId) throws SQLException {
        return readCustomers(customerId, null, null, null, null, null, null, null, null, null, null, null, null, null).getString("modalitaPagamento");
    }

    /**
     * ********************* JOB TYPES *************************************
     */
    Long createJobType(String name, String description) throws SQLException {
        return dbi.create("dic_JobTypes")
                .value("name", name).value(description != null, "description", description)
                .goAndGetId();
    }

    public DbResult readJobTypes(Long jobType_id) throws SQLException {
        return dbi.read("dic_JobTypes").order("name")
                .andWhere(jobType_id != null, "[jobType_id] = " + jobType_id)
                .go();
    }

    public String getJobTypeName(Long jobType_id) throws SQLException {
        return dbi.read("dic_JobTypes")
                .andWhere("jobType_id = " + jobType_id)
                .go().getString("name");

    }

    boolean updateJobType(Long jobType_id, String name, String description) throws SQLException {
        Updater updater = dbi.update("dic_jobTypes")
                .where("jobType_id = " + jobType_id)
                .value(name != null, "name", name)
                .value(description != null, "description", description)
                .go();

        //if all paramters are null returns true
        if (updater.getUpdateCount() == null) {
            return true;
        } else {
            return updater.getUpdateCount() == 1;
        }
    }

    /**
     * ******************** JOB SUBTYPES *************************************
     */
    Long createJobSubtype(String name, String description) throws SQLException {
        return dbi.create("dic_JobSubtypes")
                .value("name", name).value("description", description)
                .goAndGetId();
    }

    public DbResult readJobSubtypes(Long jobSubtype_id) throws SQLException {
        return dbi.read("dic_JobSubtypes").order("name")
                .andWhere(jobSubtype_id != null, "[jobSubtype_id] = " + jobSubtype_id)
                .go();
    }

    boolean updateJobSubtype(Long jobSubtype_id, String name, String description) throws SQLException {
        Updater updater = dbi.update("dic_jobSubtypes")
                .where("jobSubtype_id = " + jobSubtype_id)
                .value(name != null, "name", name)
                .value(description != null, "description", description)
                .go();
        //if all paramters are null returns true
        if (updater.getUpdateCount() == null) {
            return true;
        } else {
            return updater.getUpdateCount() == 1;
        }
    }

    public String getJobSubtypeName(Long jobSubtype_id) throws SQLException {
        return dbi.read("dic_JobSubtypes")
                .andWhere("jobSubtype_id = " + jobSubtype_id)
                .go().getString("name");
    }

    /**
     * **************** TRANSLATIONS CENTERS *********************************
     */
    Long createTranslationCenter(String denomination) throws SQLException {
        return dbi.create("dic_TranslationsCenters")
                .value("denomination", denomination)
                .goAndGetId();
    }

    public DbResult readTranslationsCenters(Long translations_center_id) throws SQLException {
        return dbi.read("dic_TranslationsCenters").order("denomination")
                .andWhere(translations_center_id != null, "[translations_center_id] = " + translations_center_id)
                .go();
    }

    boolean updateTranslationsCenter(Long translations_center_id, String denomination, String notes) throws SQLException {
        Updater updater = dbi.update("dic_TranslationsCenters")
                .where("translations_center_id = " + translations_center_id)
                .value(denomination != null, "denomination", denomination)
                .value(notes != null, "notes", notes)
                .go();

        //if all paramters are null returns true
        if (updater.getUpdateCount() == null) {
            return true;
        } else {
            return updater.getUpdateCount() == 1;
        }
    }

    /**
     * ******************** ORDERS *****************************************
     */
    /**
     *
     * @param customer_id
     * @param jobType_id
     * @return
     * @throws SQLException
     */
    Long createOrder(Long customer_id, Long user_id, Long jobType_id, String serialNumber, String date, String machinaryModel, String notes) throws SQLException {
        int year = 0;
        try {
            DTF.parse(date);
            //takes year for order code
            year = LocalDate.parse(date).getYear();
            //removes dashes to correct casting from string to int in sql server
            date = date.replace("-", "");
        } catch (Exception exception) {
            date = DTF.format(LocalDate.now()).replace("-", "");
            //takes year for order code
            year = LocalDate.now().getYear();
        }

        Long order_id = dbi.create("dyn_Orders")
                .value("customer_id", customer_id)
                .value("user_id", user_id)
                .value("jobType_id", jobType_id)
                .value("serialNumber", serialNumber)
                .value("machinaryModel", machinaryModel)
                .value("date", date)
                .value("notes", notes).goAndGetId();

        String _year = year + "";
        String lastTwo = _year.substring(_year.length() - 2);
        // correction of order_id value to obtain the desired order_code start point
        //WARNING ! changing this value involves all order code sequence corruption!
        String code = (order_id - Config.ORDER_ID_CORRECTION) + "";
        //data base query
        String sql = "UPDATE dyn_Orders SET code ='" + code + "' WHERE order_id = + " + order_id;

        dbi.execute(sql);

        return order_id;
    }

    /**
     * @param order_id
     * @param creator_id the id of the user that have created the order
     * @param order_code
     * @param order_description
     * @param completion_state_id
     * @param availability_id
     * @param serial_number
     * @param customer_idString
     * @param machinaryModelHint
     * @param jobType_idString
     * @param fromDate
     * @param toDate
     * @return
     * @throws SQLException
     */
    public DbResult readOrders(
            Long order_id, 
            Long creator_id, 
            String order_code, 
            String order_description, 
            Long completion_state_id, 
            String serialNumber, 
            Long availability_id, 
            String customer_idString, 
            String machinaryModelHint, 
            String jobType_idString, 
            String fromDate, 
            String toDate) throws SQLException {
        Long customer_id = null;

        if (customer_idString != null && !customer_idString.equals("")) {
            try {
                customer_id = Long.parseLong(customer_idString);
            } catch (NumberFormatException ex) {
            }
        }

        Long jobType_id = null;

        if (jobType_idString != null && !jobType_idString.equals("")) {
            try {
                jobType_id = Long.parseLong(jobType_idString);
            } catch (NumberFormatException ex) {
            }
        }

        if (fromDate != null && !fromDate.equals("")) {
            try {
                //removes dashes for correct casting from string to int on sql server
                fromDate = DTF.format(DTF.parse(fromDate));
                fromDate = fromDate.replace("-", "");
                fromDate = "'" + fromDate + "'";
            } catch (Exception ex) {
                System.out.println("date conversion issues");
            }
        }

        if (toDate != null && !toDate.equals("")) {
            try {
                toDate = DTF.format(DTF.parse(toDate));
                toDate = toDate.replace("-", "");
                toDate = "'" + toDate + "'";
            } catch (Exception ex) {
                System.out.println("date conversion issues");
            }
        }

        return dbi.read("dyn_Orders_view").order("code DESC")
                .andWhere(order_id != null, "[order_id] = " + order_id)
                .andWhere(creator_id != null, "[creator_id] = " + creator_id)
                .andWhere(order_code != null && !order_code.equals(""), "[code] = '" + order_code + "'")
                .andWhere(serialNumber != null && !serialNumber.equals(""), "[serialNumber] = '" + serialNumber + "'")
                .andWhere(order_description != null, "[machinaryModel] COLLATE SQL_Latin1_General_CP1_CI_AS LIKE \n'%" + order_description + "%'")
                .andWhere(completion_state_id != null, "[completion_state_id] = " + completion_state_id)
                .andWhere(availability_id != null, "[availabilty_id] = " + availability_id)
                .andWhere(customer_id != null, " [customer_id] = " + customer_id)
                .andWhere(machinaryModelHint != null, " [machinaryModel] LIKE '%" + machinaryModelHint + "%'")
                .andWhere(jobType_id != null, " [jobType_id] = " + jobType_id)
                .andWhere(fromDate != null && !fromDate.equals(""), "[date] >= " + fromDate)
                .andWhere(toDate != null && !toDate.equals(""), "[date] <= " + toDate)
                .go();
    }

    public DbResult readOrder(Long order_id) throws SQLException {
        return dbi.read("dyn_Orders_view").distinct()
                .andWhere("[order_id] = " + order_id)
                .go();
    }

    /**
     * If a parameter is null the corresponding value won't be updated
     *
     * @param order_id
     * @param customer_id
     * @param jobType_id
     * @param user_id
     * @param completionState_id
     * @param date
     * @param code
     * @param machinaryModel
     * @param notes
     * @return
     * @throws SQLException
     */
    boolean updateOrder(Long order_id, Long customer_id, Long user_id, Long jobType_id, Long completion_state_id, String date, Long code, String machinaryModel, String notes, Boolean notSuggest, String ordine, String commessa, String dataOrdine, String storyData, String serialNumber) throws SQLException {
        if (date != null) {
            date = date.replace("-", "");
        }
        if (dataOrdine != null) {
            dataOrdine = dataOrdine.replace("-", "");
        }

        Updater updater = dbi.update("dyn_Orders")
                .where("order_id = " + order_id)
                .value(customer_id != null, "customer_id", customer_id)
                .value(user_id != null, "user_id", user_id)
                .value(jobType_id != null, "jobType_id", jobType_id)
                /*
            In the GUI if completion state id is 0 ( in progress) the not suggest check box is
            checked and disabled while if the completion state is 1 ( completed ) than the check box
            is enabled.
                 */
                .value(completion_state_id != null, "completion_state_id", completion_state_id)
                /*if not suggest checkbox is checked sets suggested field as false*/
                .value(notSuggest, "suggested", 0)
                .value(!notSuggest, "suggested", 1)
                .value(date != null, "date = ", date)
                .value(code != null, "code", code)
                .value(machinaryModel != null, "machinaryModel", machinaryModel)
                .value(notes != null, "notes", notes)
                .value(ordine != null, "ordine", ordine)
                .value(commessa != null, "commessa", commessa)
                .value(dataOrdine != null, "dataOrdine", dataOrdine)
                .value(storyData != null, "storyData", storyData)
                .value(serialNumber != null, "serialNumber", serialNumber)
                .go();

        //if all parameters are null returns true
        if (updater.getUpdateCount() == null) {
            return true;
        } else {
            return updater.getUpdateCount() == 1;
        }
    }

    Long getOrderIdByOrderCode(String orderCode) throws SQLException {
        String sql = "SELECT order_id FROM dyn_Orders WHERE code = '" + orderCode + "'";
        return dbi.execute(sql).result().getLong("order_id");
    }

    String getOrderCodeByOrderId(Long order_id) throws SQLException {
        String sql = "SELECT code FROM dyn_Orders WHERE order_id = " + order_id;
        return dbi.execute(sql).result().getString("code");
    }

    public Double getOrderHours(Long order_id) throws SQLException {
        DbResult dbr_tasks = readTasksByOrderId(order_id);
        Double totalHours = 0.0;
        for (int i = 0; i < dbr_tasks.rowsCount(); i++) {
            totalHours += dbr_tasks.getDouble(i, "hours");
        }
        return totalHours;
    }

    DbResult getOrderById(Long order_id) throws SQLException {
        String sql = "SELECT * FROM dyn_Orders WHERE order_id = " + order_id;
        return dbi.execute(sql).result();
    }

    public Long getAvailabilityId(Long order_id) throws SQLException {
        DbResult dbr_order = getOrderById(order_id);

        return dbr_order.getLong("availability_id");
    }

    public Long getCompletionStateId(Long order_id) throws SQLException {
        DbResult dbr_order = getOrderById(order_id);

        return dbr_order.getLong("completion_state_id");
    }

    public DbResult getSuggestedOrdersByCustomerId(Long customer_id) throws SQLException {
        return dbi.read("dyn_Orders_view")
                .andWhere("customer_id = " + customer_id)
                .andWhere("suggested = " + 1)
                .andWhere("delivered = " + 0)
                .andWhere("invoiced = " + 0).go();
    }

    /**
     * gives back a message that says if the code is not an existing code; if
     * the code exists and not belongs to the customer ; if the code belngs to
     * the customer but the related order is not completed; if everythink is
     * alright the message returned is empty
     *
     * @param orderCode
     * @param customer_id
     * @return
     * @throws java.sql.SQLException
     */
    public String getValidCode(String orderCode, Long customer_id) throws SQLException {
        //message to be returned to the user
        String message = "";

        //boolean that tells if the code exists
        Boolean valid = false;

        //checks if the code exists
        DbResult dbr = dbi.read("dyn_Orders_view")
                .andWhere("code = " + orderCode)
                .go();

        //if the code exists
        if (dbr.rowsCount() == 1) {
            //if the order belongs to the customer
            if (dbr.getLong("customer_id").equals(customer_id)) {
                //checks if the order is completed
                if (!dbr.getLong("completionState_id").equals(2L)) {
                    message = "Il codice inserito risulta ancora \"IN CORSO\" ";
                }
            } else {
                message = "Il codice inserito non appartiene al cliente selezionato!";
            }
        } else {
            message = "Il codice inserito non esiste!";
        }

        return message;
    }

    /**
     * gets all machinary models related to the customer.
     *
     * @param customer_id
     * @return
     * @throws SQLException
     */
    public DbResult getMachinaryModelsByCustomerId(Long customer_id) throws SQLException {
        String sql = "SELECT DISTINCT machinaryModel FROM dyn_Orders_view WHERE 1=0";
        if (customer_id != null) {
            sql += "OR customer_id = " + customer_id;
        }

        return dbi.execute(sql).result();
    }

    /**
     * bY order code anf customer_id checks if the order code: exists, belongs
     * to the customer and is completed
     *
     * @param orderCode
     * @param customer_id
     * @return an complete message about the validity od the code
     * @throws SQLException
     */
    public String[] getValidityMachinaryModelByOrderCode(String orderCode, Long customer_id) throws SQLException {
        //message to be notified if something goes wrong
        String[] messages = new String[5];
        messages[0] = "";
        messages[1] = "";
        messages[2] = "";
        messages[3] = "";
        messages[4] = "";

        DbResult dbr = dbi.read("dyn_Orders")
                .andWhere("code = '" + orderCode + "'")
                .go();
        //if the order code exists
        if (dbr.rowsCount() == 1) {
            //if the order belongs to the chosen customer
            if (dbr.getLong("customer_id").equals(customer_id)) {
                //if the order is completed fills messages[2] with the machinaryModel
                if (dbr.getLong("completion_state_id").equals(2L)) {
                    messages[2] = dbr.getString("machinaryModel");
                    messages[3] = dbr.getString("ordine") != null ? dbr.getString("ordine") : "";
                    messages[4] = dbr.getString("commessa") != null ? dbr.getString("commessa") : "";
                } else {
                    messages[0] = "NOT COMPLETED";
                }
            } else {
                messages[0] = "NOT BELONGS";
            }
        } else {
            messages[0] = "NOT VALID";
        }

        //this field is made to notify if the order has been already delivered and / or invoiced
        if (isDelivered(orderCode)) {
            messages[1] = "DELIVERED";
        }
        if (isInvoiced(orderCode)) {
            messages[1] += " INVOICED";
        }

        return messages;
    }

    public DbResult getOrdersByCustomerId(Long customer_id) throws SQLException {
        return dbi.read("dyn_Orders").order("code")
                .andWhere("customer_id = " + customer_id)
                .go();
    }

    public Long getCustomerIdByOrderId(Long order_id) throws SQLException {
        return dbi.read("dyn_Orders")
                .andWhere("order_id = " + order_id)
                .go().getLong("customer_id");
    }

    public Double getOrderTotalCosts(Long order_id) throws SQLException {
        Double totalTranslationsCost = 0.0;
        Double totalExternalJobsCost = 0.0;
        Double totalVariouseMaterialCost = 0.0;
        Double totalTransfertCost = 0.0;

        DbResult tasks = readTasksByOrderId(order_id);

        for (int i = 0; i < tasks.rowsCount(); i++) {
            totalTranslationsCost += tasks.getDouble(i, "translationCost");
            totalExternalJobsCost += tasks.getDouble(i, "externalJobsCost");
            totalVariouseMaterialCost += tasks.getDouble(i, "variouseMaterialCost");
            totalTransfertCost += tasks.getDouble(i, "transfertCost");
        }

        return totalTranslationsCost + totalExternalJobsCost + totalVariouseMaterialCost + totalTransfertCost;
    }

    private void setOrderSuggestion(Long order_id, Boolean suggest) throws SQLException {
        Integer state = suggest ? 1 : 0;

        dbi.update("dyn_Orders")
                .andWhere("order_id = " + order_id)
                .value("suggested", state)
                .go();
    }

    private void setOrderDelivered(Long order_id, Boolean delivered) throws SQLException {
        Integer state = delivered ? 1 : 0;

        dbi.update("dyn_Orders")
                .andWhere("order_id = " + order_id)
                .value("delivered", state)
                .go();
    }

    private void setOrderInvoiced(Long order_id, Boolean invoiced) throws SQLException {
        Integer state = invoiced ? 1 : 0;

        dbi.update("dyn_Orders")
                .andWhere("order_id = " + order_id)
                .value("invoiced", state)
                .go();
    }

    private void adjurnStoryData(Long order_id, String newData) throws SQLException {
        //first save current story data
        DbResult dbr = dbi.read("dyn_Orders")
                .andWhere("order_id = " + order_id)
                .go();

        //adjurns data
        dbi.update("dyn_Orders")
                .andWhere("order_id = " + order_id)
                .value("storyData", dbr.getString("storyData") + " " + newData + "; ")
                .go();
    }

    private Boolean isDelivered(String orderCode) throws SQLException {
        return dbi.read("dyn_Orders")
                .andWhere("code = '" + orderCode + "'")
                .andWhere("delivered = 1 ")
                .go()
                .rowsCount() == 1;
    }

    private Boolean isInvoiced(String orderCode) throws SQLException {
        return dbi.read("dyn_Orders")
                .andWhere("code = '" + orderCode + "'")
                .andWhere("invoiced = 1 ")
                .go()
                .rowsCount() == 1;
    }

    public Boolean logoAdjurned(Long order_id) throws SQLException {
        //retrieves the customer_id
        Long customer_id = dbi.read("dyn_Orders")
                .andWhere("order_id = '" + order_id)
                .go()
                .getLong("customer_id");

        return customerLogoAdjurned(customer_id);

    }

    //TRANSPORTERS
    public Long createTransporter(String denomination, String fiscalCode) throws SQLException {
        if (denomination != null) {
            denomination = denomination.replace("'", "''");
        }

        return dbi.create("reg_Transporters")
                .value("denomination", denomination)
                .value("fiscalCode", fiscalCode).goAndGetId();
    }

    /**
     *
     * @param transporter_id
     * @param vatCodeHint
     * @param fiscalCodeHint
     * @param denominationHint
     * @param phoneNumberHint
     * @param faxNumberHint
     * @param cellNumberHint
     * @param emailHint
     * @param cityHint
     * @param addressHint
     * @param houseNumberHint
     * @param postalCodeHint
     * @param provinceHint
     * @param notesHint
     * @return
     * @throws SQLException
     */
    public DbResult readTransporters(Long transporter_id, String vatCodeHint, String fiscalCodeHint, String denominationHint, String phoneNumberHint, String faxNumberHint, String cellNumberHint, String emailHint, String cityHint, String addressHint, String houseNumberHint, String postalCodeHint, String provinceHint, String notesHint) throws SQLException {
        return dbi.read("reg_Transporters").order("denomination")
                .andWhere(transporter_id != null, "[transporter_id] = " + transporter_id)
                .andWhere(vatCodeHint != null, " [vatCode] LIKE '%" + vatCodeHint + "%'")
                .andWhere(fiscalCodeHint != null, " [fiscalCode] LIKE '%" + fiscalCodeHint + "%'")
                .andWhere(denominationHint != null, " [denomination] LIKE '%" + denominationHint + "%'")
                .andWhere(phoneNumberHint != null, " [phoneNumber] LIKE '%" + phoneNumberHint + "%'")
                .andWhere(faxNumberHint != null, " [faxNumber] LIKE '%" + faxNumberHint + "%'")
                .andWhere(cellNumberHint != null, " [cellNumber] LIKE '%" + cellNumberHint + "%'")
                .andWhere(emailHint != null, " [email] LIKE '%" + emailHint + "%'")
                .andWhere(cityHint != null, " [city] LIKE '%" + cityHint + "%'")
                .andWhere(addressHint != null, " [address] LIKE '%" + addressHint + "%'")
                .andWhere(houseNumberHint != null, " [houseNumber] LIKE '%" + houseNumberHint + "%'")
                .andWhere(postalCodeHint != null, " [postalCode] LIKE '%" + postalCodeHint + "%'")
                .andWhere(provinceHint != null, " [province] LIKE '%" + provinceHint + "%'")
                .andWhere(notesHint != null, " [notes] LIKE '%" + notesHint + "%'")
                .go();
    }

    /**
     * If a parameter is null the corresponding value won't be updated
     *
     * @param transporter_id
     * @param vatCode
     * @param fiscalCode
     * @param denomination
     * @param phoneNumber
     * @param faxNumber
     * @param cellNumber
     * @param email
     * @param city
     * @param address
     * @param houseNumber
     * @param postalCode
     * @param province
     * @param notes
     * @return
     * @throws SQLException
     */
    boolean updateTransporter(Long transporter_id, String vatCode, String fiscalCode, String denomination, String phoneNumber, String faxNumber, String cellNumber, String email, String city, String address, String houseNumber, String postalCode, String province, String notes) throws SQLException {
        Updater updater = dbi.update("reg_Transporters")
                .where("transporter_id = " + transporter_id)
                .value(vatCode != null, "vatCode", vatCode)
                .value(fiscalCode != null, "fiscalCode", fiscalCode)
                .value(denomination != null, "denomination", denomination)
                .value(phoneNumber != null, "phoneNumber", phoneNumber)
                .value(faxNumber != null, "faxNumber", faxNumber)
                .value(cellNumber != null, "cellNumber", cellNumber)
                .value(email != null, "email", email)
                .value(city != null, "city", city)
                .value(address != null, "address", address)
                .value(houseNumber != null, "houseNumber", houseNumber)
                .value(postalCode != null, "postalCode", postalCode)
                .value(province != null, "province", province)
                .value(notes != null, "notes", notes)
                .go();

        //if all paramters are null returns true
        if (updater.getUpdateCount() == null) {
            return true;
        } else {
            return updater.getUpdateCount() == 1;
        }
    }

    //TASKS
    /**
     *
     * @param order_code
     * @param user_id the operator
     * @param jobSubtype_id
     * @return
     * @throws SQLException
     */
    Long createTask(String order_code, Long user_id, Long jobSubtype_id, String date, Double hours, Long translations_center_id, Double translationPrice, Double translationCost, String externalJobsDesc, Double externalJobsHours, Double externalJobsCost, String variouseMaterialDesc, Double variouseMaterialCost, Integer transfertKms, Double transfertCost, String notes) throws SQLException {
        Long order_id = 0L;

        try {
            DTF.parse(date);

            //removes dashes to correct casting from string to int in sql server
            date = date.replace("-", "");
        } catch (Exception exception) {
            date = DTF.format(LocalDate.now()).replace("-", "");
        }

        if (order_code != null) {
            order_id = getOrderIdByOrderCode(order_code);
        }

        Long task_id = dbi.create("dyn_Tasks")
                .value("order_id", order_id)
                .value("user_id", user_id)
                .value("jobSubtype_id", jobSubtype_id)
                .value("date", date).value("hours", hours)
                .value("translations_center_id", translations_center_id)
                .value("translationPrice", translationPrice)
                .value("translationCost", translationCost)
                .value("externalJobsDesc", externalJobsDesc)
                .value("externalJobsHours", externalJobsHours)
                .value("externalJobsCost", externalJobsCost)
                .value("variouseMaterialDesc", variouseMaterialDesc)
                .value("variouseMaterialCost", variouseMaterialCost)
                .value("transfertKms", transfertKms)
                .value("transfertCost", transfertCost)
                .value("notes", notes)
                .goAndGetId();

        //retrieves all attachments having null as task_id
        DbResult dbr_attachments = dbi.read("dyn_TasksAttachments")
                .andWhere("task_id IS NULL")
                .andWhere("user_id = " + user_id)
                .go();

        for (int i = 0; i < dbr_attachments.rowsCount(); i++) {
            dbi.update("dyn_TasksAttachments")
                    .value("task_id", task_id)
                    .andWhere("user_id = " + user_id)
                    .andWhere("task_id IS NULL")
                    .go();
        }

        return task_id;
    }

    /**
     * this method gives back all tasks satisfing filtering criteria. About
     * user_id his role will be checked. If the user is administrator tasks of
     * all users will be returned. If not anly tasks created by the user related
     * to user_id value will be returned
     *
     * @param task_id
     * @param user_id adminstrator?
     * @param operator_id
     * @param orderCode
     * @param jobType_id
     * @param customer_id
     * @param order_creator_id
     * @param jobSubtype_id
     * @param fromDate
     * @param toDate
     * @param completion_state_id
     * @param order_id
     * @return
     * @throws SQLException dbi.js -> tasks.js -> task_id, order_id,
     * operator_id, operatorHint, customerDenominationHint, orderCreatorHint,
     * fromDate, toDate, completion_state_id gate ->
     */
    public DbResult readTasks(Long task_id, Long user_id, Long order_id, Long operator_id, String orderCode, String orderSerialNumber, Long jobType_id, Long jobSubtype_id, Long customer_id, Long order_creator_id, String fromDate, String toDate, Long completion_state_id) throws SQLException {
        if (fromDate != null && !fromDate.equals("")) {
            try {
                //removes dashes for correct casting from string to int on sql server
                fromDate = DTF.format(DTF.parse(fromDate));
                fromDate = fromDate.replace("-", "");
                fromDate = "'" + fromDate + "'";
            } catch (Exception ex) {
                System.out.println("date conversion issues");
            }
        }

        if (toDate != null && !toDate.equals("")) {
            try {
                toDate = DTF.format(DTF.parse(toDate));
                toDate = toDate.replace("-", "");
                toDate = "'" + toDate + "'";
            } catch (Exception ex) {
                System.out.println("date conversion issues");
            }
        }

        DbResult tasks_dbr = dbi.read("dyn_Tasks_view").order("operator_id, taskDate DESC")
                .andWhere(getUserRole(user_id).equals("operator"), "operator_id = " + user_id)
                .andWhere(task_id != null, "task_id = " + task_id)
                .andWhere(order_id != null, "order_id = " + order_id)
                .andWhere(operator_id != null, "operator_id = " + operator_id)
                .andWhere(orderCode != null && !orderCode.equals(""), "orderCode = " + orderCode)
                .andWhere(orderSerialNumber != null && !orderSerialNumber.equals(""), "serialNumber = '" + orderSerialNumber +"'")
                .andWhere(jobType_id != null, "jobType_id = " + jobType_id)
                .andWhere(jobSubtype_id != null, "jobSubtype_id = " + jobSubtype_id)
                .andWhere(customer_id != null, "customer_id = " + customer_id)
                .andWhere(order_creator_id != null, "order_creator_id = " + order_creator_id)
                .andWhere(fromDate != null && !fromDate.equals(""), " taskDate >= " + fromDate)
                .andWhere(toDate != null && !toDate.equals(""), " taskDate <= " + toDate)
                .andWhere(completion_state_id != null && completion_state_id > 0, "completion_state_id = " + completion_state_id)
                .go();

        return tasks_dbr;
    }

    public DbResult readTask(Long task_id) throws SQLException {
        return dbi.read("dyn_Tasks_view")
                .andWhere("[task_id] = " + task_id)
                .go();
    }

    DbResult readTasksByOrderId(Long order_id) throws SQLException {
        return dbi.read("dyn_Tasks_view")
                .andWhere("[order_id] = " + order_id)
                .go();
    }

    /**
     * If a parameter is null the corresponding value won't be updated
     *
     * @param task_id
     * @param order_id
     * @param user_id
     * @param jobSubtype_id
     * @param date
     * @param hours
     * @param translationCenterName
     * @param translationPrice
     * @param translationQuotation
     * @param externalJobsDesc
     * @param externalJobsCost
     * @param variouseMaterialDesc
     * @param variouseMaterialCost
     * @param transferKms
     * @param restauranrCost
     * @param hotelCost
     * @return
     * @throws SQLException
     */
    boolean updateTask(Long task_id, String order_code, Long jobSubtype_id, String taskDate, Double hours, Long translations_center_id, Double translationPrice, Double translationCost, String externalJobsDesc, Double externalJobsHours, Double externalJobsCost, String variouseMaterialDesc, Double variouseMaterialCost, Integer transfertKms, Double transfertCost, String notes) throws SQLException {
        
        dbi.update("dyn_Tasks")
            .andWhere("task_id = " + task_id)
            .value(jobSubtype_id != null, "jobSubtype_id", jobSubtype_id)
            .value(order_code != null, "order_id", getOrderIdByOrderCode(order_code))
            .value(taskDate != null, "date", taskDate.replace("-", ""))
            .value(hours != null, "hours", hours)
            .value(translations_center_id != null, "translations_center_id", translations_center_id)
            .value(translationPrice != null, "translationPrice", translationPrice)
            .value(translationCost != null, "translationCost", translationCost)
            .value(externalJobsDesc != null, "externalJobsDesc", externalJobsDesc)
            .value(externalJobsHours != null, "externalJobsHours", externalJobsHours)
            .value(externalJobsCost != null, "externalJobsCost", externalJobsCost)
            .value(variouseMaterialDesc != null, "variouseMaterialDesc", variouseMaterialDesc)
            .value(variouseMaterialCost != null, "variouseMaterialCost", variouseMaterialCost)
            .value(transfertKms != null, "transfertKms", transfertKms)
            .value(transfertCost != null, "transfertCost", transfertCost)
            .value(notes != null, "notes", notes)
            .go();
        
        DbResult dbr = dbi.read("dyn_Tasks")
            .andWhere("task_id = " + task_id)
            .go();
        
        return !dbr.isEmpty();
//        if (updater.getUpdateCount() == null) {
//            return true;
//        } else {
//            return updater.getUpdateCount() == 1;
//        }
    }

    /**
     *
     * @param task_id
     * @return
     * @throws SQLException
     */
    boolean deleteTask(Long task_id) throws SQLException {
        return dbi.delete("dyn_Tasks").where("task_id = " + task_id)
                .go().getDeleteCount() == 1;
    }

    //COMPLETION STATES
    public DbResult readCompletionStates() throws SQLException {
        return dbi.read("dic_CompletionStates")
                .go();
    }

    //AVAILABILITY
    public DbResult readAvailability() throws SQLException {
        return dbi.read("dic_Availability")
                .go();
    }

    //ATTACHMENTS
    public void setCustomerLogo(Long customer_id, String localFileName) throws SQLException {
        dbi.update("reg_Customers")
                .andWhere("customer_id = " + customer_id)
                .value("logo", localFileName)
                .go();
    }

    /**
     * associates the file with the task and sets the task column hasAttachment
     * to 1
     *
     * @param task_id
     * @param user_id
     * @param originalFileName
     * @return
     * @throws SQLException
     */
    Long createTaskAttachment(Long task_id, Long user_id, String originalFileName) throws SQLException {
        String date = DTF.format(LocalDate.now()).replace("-", "");

        Long result = dbi.create("dyn_TasksAttachments")
                .value("task_id", task_id)
                .value("user_id", user_id)
                .value("date", date)
                .value("originalFileName", originalFileName)
                .goAndGetId();

        dbi.update("dyn_Tasks")
                .andWhere("task_id = " + task_id)
                .value("hasAttachment", 1)
                .go();

        return result;
    }

    Boolean setTaskAttachmentName(String originalFileName, String currentFileName, Long taskAttachment_id) throws SQLException {
        Updater updater = dbi.update("dyn_TasksAttachments")
                .andWhere("taskAttachment_id = " + taskAttachment_id)
                .value("currentFileName", currentFileName)
                .go();

        return (updater.getUpdateCount() == 1);
    }

    public DbResult readTaskAttachments(Long task_id, Long user_id) throws SQLException {
        return dbi.read("dyn_TasksAttachments").order("date DESC")
                .andWhere(task_id == null, "task_id IS NULL AND user_id = " + user_id)
                .andWhere(task_id != null, "task_id = " + task_id)
                .go();
    }

    public void deleteNullTaskAttachments(Long user_id) throws SQLException {
        //Path of the attachment
        Path p1;

        //retrieves all attachments having null as task_id
        DbResult dbr_attachments = dbi.read("dyn_TasksAttachments")
                .andWhere("task_id IS NULL")
                .andWhere("user_id = " + user_id)
                .go();

        for (int i = 0; i < dbr_attachments.rowsCount(); i++) {
            //first removes the file from the folder then from the DB
            p1 = Paths.get(Config.TASKS_ATTACH_DIR + dbr_attachments.getString(i, "currentFileName"));
            try {
                Files.deleteIfExists(p1);
            } catch (IOException ex) {
            }

            dbi.delete("dyn_TasksAttachments")
                    .andWhere("taskAttachment_id = " + dbr_attachments.getLong(i, "taskAttachment_id"))
                    .andWhere("user_id = " + dbr_attachments.getLong(i, "user_id"))
                    .go();
        }
    }

    /**
     * deletes the attachment and if it is the last attachment of the task
     * update the coulmnn has attachment to 0
     *
     * @param attachment_id
     * @throws SQLException
     */
    public void deleteTaskAttachments(Long taskAttachment_id) throws SQLException {
        //gets task id
        Long task_id = dbi.read("dyn_TasksAttachments")
                .andWhere("taskAttachment_id = " + taskAttachment_id)
                .go()
                .getLong("task_id");

        // dletes the fiel from the filesystem and from the DB 
        //Path of the attachment
        Path p1;
        //retrieves all attachments having null as task_id
        DbResult dbr_attachments = dbi.read("dyn_TasksAttachments")
                .andWhere("taskAttachment_id = " + taskAttachment_id)
                .go();

        for (int i = 0; i < dbr_attachments.rowsCount(); i++) {
            //first removes the file from the folder then from the DB
            p1 = Paths.get(Config.TASKS_ATTACH_DIR + dbr_attachments.getString(i, "currentFileName"));
            try {
                Files.deleteIfExists(p1);
            } catch (IOException ex) {
            }

            dbi.delete("dyn_TasksAttachments")
                    .andWhere("taskAttachment_id = " + dbr_attachments.getLong(i, "taskAttachment_id"))
                    .go();
        }

        //reads all attachments having the same value as task_id
        Integer attachmentsNumber = dbi.read("dyn_TasksAttachments")
                .andWhere("task_id = " + task_id)
                .go()
                .rowsCount();
        //if theere are no other attacments set to 0 the hasAttachment value of the task
        if (attachmentsNumber == 0) {
            dbi.update("dyn_Tasks")
                    .andWhere("task_id = " + task_id)
                    .value("hasAttachment", 0)
                    .go();
        }
    }

    //DELIVERY NOTES
    /* EXISTS STORED PROCEDURE TO GET NEXT AVAILABLE DELIVERY NOTE NUMBER : spGetDeliveryNoteProgressive */
    /**
     *
     * @param deliveryNote
     * @param suggested
     * @return
     * @throws SQLException
     */
    public Long createDeliveryNote(DeliveryNote deliveryNote, Boolean suggested) throws SQLException {
        String dateString = deliveryNote.date.replace("-", "");
        int year = Integer.parseInt(deliveryNote.date.substring(2, 4));
        int suggest = suggested ? 1 : 0;

        /* FIRST DELIVERY NOTE NUMBER MANAGMENT
        the first value is the number of the first delivery note of the year specified in the 
          stored procedure [spGetDeliveryNoteProgressive] of the DB*/
        String sql
                = "DECLARE @new_identity bigint EXECUTE spCreateDeliveryNote "
                + /*first delivery note number of the specified year in the store procedure*/ 377 + ", "
                + deliveryNote.customer_id + ", "
                + deliveryNote.transporter_id + ", "
                + year + ", '"
                + dateString + "', '"
                + deliveryNote.destDenomination.replace("'", "''") + "', '"
                + deliveryNote.destCity.replace("'", "''") + "', '"
                + deliveryNote.destAddress.replace("'", "''") + "', '"
                + deliveryNote.destHouseNumber + "', '"
                + deliveryNote.destPostalCode + "', '"
                + deliveryNote.destProvince.replace("'", "''") + "', '"
                + deliveryNote.transportResponsable.replace("'", "''") + "', '"
                + deliveryNote.transportReason.replace("'", "''") + "', '"
                + deliveryNote.goodsExteriorAspect.replace("'", "''") + "', "
                + deliveryNote.packagesNumber + ", "
                + deliveryNote.weight + ", '"
                + deliveryNote.notes.replace("'", "''") + "', "
                + "0, "
                + suggest + ", "
                + "  @new_identity OUTPUT";

        deliveryNote.deliveryNote_id = dbi.execute(sql).results[1].getLong("lastId");

        //set suggested value
        setDeliveryNoteSuggested(deliveryNote.deliveryNote_id, suggested);

        for (DeliveryNote.Item item : deliveryNote.items) {
            addDeliveryNoteRow(item, deliveryNote.deliveryNote_id);
        }

        return deliveryNote.deliveryNote_id;
    }

    public DbResult readDeliveryNotes(
            Long deliveryNote_id,
            Long customer_id,
            Long transporter_id,
            String number,
            LocalDate fromDate,
            LocalDate toDate) throws SQLException {
        String fromDateString = null;
        String toDateString = null;

        if (fromDate == null) {
            fromDateString = "19000101";
        } else {
            fromDateString = DateTimeFormatter.ISO_LOCAL_DATE.format(fromDate).replace("-", "");
        }

        if (toDate == null) {
            toDateString = "30000101";
        } else {
            toDateString = DateTimeFormatter.ISO_LOCAL_DATE.format(toDate).replace("-", "");
        }

        String num = number != null && !number.equals("") && number.contains("-") ? number.split("-")[0] : null;
        String year = number != null && !number.equals("") && number.contains("-") ? number.split("-")[1] : null;

        return dbi.read("dyn_DeliveryNotes_view").order("number DESC")
                .andWhere(deliveryNote_id != null, "deliveryNote_id = " + deliveryNote_id)
                .andWhere(customer_id != null, "customer_id = " + customer_id)
                .andWhere(transporter_id != null, "transporter_id = " + transporter_id)
                .andWhere(number != null && !number.equals(""), "number = " + num)
                .andWhere(number != null && !number.equals(""), "year = " + year)
                .andWhere(" date >= '" + fromDateString + "'")
                .andWhere(" date <= '" + toDateString + "'")
                .go();
    }

    public DbResult readAllDeliveryNotesFromTable() throws SQLException {
        return dbi.execute("select * from dyn_DeliveryNotes").result();
    }

    public DbResult getTransportReasons() throws SQLException {
        String sql = "SELECT DISTINCT transportReason FROM dyn_DeliveryNotes";

        return dbi.execute(sql).result();
    }

    public DbResult getTransportResponsables() throws SQLException {
        String sql = "SELECT DISTINCT transportResponsable FROM dyn_DeliveryNotes";

        return dbi.execute(sql).result();
    }

    public DbResult getGoodsExteriorAspect() throws SQLException {
        String sql = "SELECT DISTINCT goodsExteriorAspect FROM dyn_DeliveryNotes";

        return dbi.execute(sql).result();
    }

    public DbResult readDeliveryNoteRows(Long deliveryNote_id) throws SQLException {
        return dbi.read("dyn_DeliveryNotesRows").order("deliveryNoteRow_id")
                .andWhere(deliveryNote_id != null, "[deliveryNote_id] = " + deliveryNote_id)
                .go();
    }

    public DbResult readDeliveryNotesRows(DbResult deliveryNotes) throws SQLException {
        return dbi.read("dyn_DeliveryNotesRows")
                .andWhere("deliveryNote_id = " + deliveryNotes.getLong("deliveryNote_id"))
                .go();
    }

    public Long updateDeliveryNote(DeliveryNote deliveryNote, Boolean suggested) throws SQLException {
        dbi.update("dyn_DeliveryNotes")
                .andWhere("deliveryNote_id = " + deliveryNote.deliveryNote_id)
                .value("customer_id", deliveryNote.customer_id)
                .value("transporter_id", deliveryNote.transporter_id)
                .value("destDenomination", deliveryNote.destDenomination)
                .value("destCity", deliveryNote.destCity)
                .value("destAddress", deliveryNote.destAddress)
                .value("destHouseNumber", deliveryNote.destHouseNumber)
                .value("destPostalCode", deliveryNote.destPostalCode)
                .value("destProvince", deliveryNote.destProvince)
                .value("transportResponsable", deliveryNote.transportResponsable)
                .value("transportReason", deliveryNote.transportReason)
                .value("goodsExteriorAspect", deliveryNote.goodsExteriorAspect)
                .value("packagesNumber", deliveryNote.packagesNumber)
                .value("weight", deliveryNote.weight)
                .value("notes", deliveryNote.notes)
                .go();

        //sets suggested value 
        setDeliveryNoteSuggested(deliveryNote.deliveryNote_id, suggested);

        //first delete all existing rows 
        dbi.delete("dyn_DeliveryNotesRows")
                .where("deliveryNote_id = " + deliveryNote.deliveryNote_id)
                .go();

        //than creates new rows
        for (DeliveryNote.Item item : deliveryNote.items) {
            addDeliveryNoteRow(item, deliveryNote.deliveryNote_id);
        }

        return deliveryNote.deliveryNote_id;
    }

    private Long addDeliveryNoteRow(DeliveryNote.Item item, Long deliveryNote_id) throws SQLException {
        //result
        Long result;
        //if there's an order code retrieves the order_id
        //sets to don't suggest it in future
        Long order_id = null;
        if (item.code != null && !(item.code.equals(""))) {
            //retrieves order_id
            order_id = getOrderIdByOrderCode(item.code);

            //sets to be not suggested next times
            setOrderSuggestion(order_id, false);

            //sets the order as a delivered one
            setOrderDelivered(order_id, true);
        }
        //creates the row
        result = dbi.create("dyn_DeliveryNotesRows")
                .value("deliveryNote_id", deliveryNote_id)
                .value("order_id", order_id)
                .value("code", item.code)
                .value("description", item.description).value("quantity", item.quantity)
                .goAndGetId();

        //adjurns story data if the row has been created
        if (result != null && order_id != null) {
            adjurnStoryData(order_id, "Consegnato: " + getDeliveryNoteNumberAndDate(deliveryNote_id));
        }

        return result;
    }

    public DbResult getDeliveryNoteRowByOrderCode(String orderCode) throws SQLException {
        return dbi.read("dyn_DeliveryNotesRows")
                .andWhere("code = " + orderCode)
                .go();
    }

    public String getDeliveryNoteNumberAndDate(Long deliveryNote_id) throws SQLException {
        DbResult dbr = dbi.read("dyn_DeliveryNotes")
                .andWhere("deliveryNote_id = " + deliveryNote_id)
                .go();

        String date = dbr.getString("date");
        String year = date.substring(0, 4);
        String month = date.substring(4, 6);
        String day = date.substring(6, 8);
        date = day + "/" + month + "/" + year;

        return "ddt n° " + dbr.getInteger("number") + " del " + date;
    }

    public String getDeliveryNoteNumber(Long deliveryNote_id) throws SQLException {
        return dbi.read("dyn_DeliveryNotes")
                .andWhere("deliveryNote_id = " + deliveryNote_id)
                .go().getInteger("number").toString();
    }

    public String getDeliveryNoteDate(Long deliveryNote_id) throws SQLException {
        DbResult dbr = dbi.read("dyn_DeliveryNotes")
                .andWhere("deliveryNote_id = " + deliveryNote_id)
                .go();

        String date = dbr.getString("date");
        String year = date.substring(0, 4);
        String month = date.substring(4, 6);
        String day = date.substring(6, 8);
        date = day + "/" + month + "/" + year;

        return date;
    }

    private void setDeliveryNoteRowSuggestion(Long deliveryNoteRow_id, Boolean suggest) throws SQLException {
        Integer state = suggest ? 1 : 0;

        dbi.update("dyn_DeliveryNotesRows")
                .andWhere("deliveryNoteRow_id = " + deliveryNoteRow_id)
                .value("suggested", state)
                .go();
    }

    private void setDeliveryNoteRowInvoiced(Long deliveryNoteRow_id, Boolean invoiced) throws SQLException {
        Integer state = invoiced ? 1 : 0;

        dbi.update("dyn_DeliveryNotesRows")
                .andWhere("deliveryNoteRow_id = " + deliveryNoteRow_id)
                .value("invoiced", state)
                .go();
    }

    private void setDeliveryNoteSuggested(Long deliveryNote_id, Boolean suggested) throws SQLException {
        Integer suggest = suggested ? 1 : 0;

        dbi.update("dyn_DeliveryNotes")
                .andWhere("deliveryNote_id = " + deliveryNote_id)
                .value("suggested", suggest)
                .go();
    }

    private void setDeliveryNoteInvoiced(Long deliveryNote_id, Boolean invoiced) throws SQLException {
        Integer invoiced_ = invoiced ? 1 : 0;

        dbi.update("dyn_DeliveryNotes")
                .andWhere("deliveryNote_id = " + deliveryNote_id)
                .value("invoiced", invoiced_)
                .go();
    }

    /**
     * returns the order_id related to the deliveryNoterow
     *
     * @param deliveryNoteRow_id
     * @return
     * @throws SQLException
     */
    private Long getDeliveryNoteRowOrderId(Long deliveryNoteRow_id) throws SQLException {
        return dbi.read("dyn_DeliveryNotesRows")
                .andWhere("deliveryNoteRow_id = " + deliveryNoteRow_id)
                .go().getLong("order_id");
    }

    public DbResult getDeliveryNoteRowsByDeliveryNoteId(Long deliveryNote_id) throws SQLException {
        return dbi.read("dyn_DeliveryNotesRows")
                .andWhere("deliveryNote_id = " + deliveryNote_id)
                .go();

    }

    /**
     * If all rows of deliverynote wich delivery note row belongs have been
     * already invoiced sets the deliverynote to not to be iterated in
     * getSuggestedDeliveryNotesRowsByCustomerId( customer_id)
     *
     * @param deliveryNoteRow_id belonging to the deliveryNote that must be
     * checked
     * @throws SQLException
     */
    private void setDeliveryNoteSuggested(Long deliveryNoteRow_id) throws SQLException {
        //how many rows have been invoiced
        Integer invoicedRows = 0;

        Long deliveryNote_id = dbi.read("dyn_DeliveryNotesRows")
                .andWhere("deliveryNoteRow_id = " + deliveryNoteRow_id)
                .go().getLong("deliveryNote_id");

        //gets all deliveryNote rows
        DbResult dbrRows = dbi.read("dyn_DeliveryNotesRows")
                .andWhere("deliveryNote_id = " + deliveryNote_id)
                .go();

        //counts how many rows have been invoiced
        for (int i = 0; i < dbrRows.rowsCount(); i++) {
            if (dbrRows.getBoolean(i, "invoiced")) {
                invoicedRows++;
            }
        }

        //if invoiced rows = deliveryNote rows sets deòievryNote to not to be considered
        //for invoices anymore
        if (dbrRows.rowsCount() == invoicedRows) {
            dbi.update("dyn_DeliveryNotes")
                    .andWhere("deliveryNote_id = " + deliveryNote_id)
                    .value("invoiced", 1)
                    .go();
        }
    }

    public DbResult getDeliveryNoteByDeliveryNoteRowId(Long deliveryNoteRow_id) throws SQLException {
        Long deliveryNote_id = dbi.read("dyn_DeliveryNotesRows")
                .where("deliveryNoteRow_id = " + deliveryNoteRow_id)
                .go().getLong("deliveryNote_id");

        return dbi.read("dyn_DeliveryNotes").where("deliveryNote_id = " + deliveryNote_id).go();
    }

    //INVOICES
    public DbResult createInvoice(Invoice invoice) throws SQLException {
        String dateString = invoice.date.replace("-", "");
        String firstDateString = invoice.firstAmountDate.replace("-", "");
        String secondDateString = invoice.secondAmountDate.replace("-", "");
        String thirdDateString = invoice.thirdAmountDate.replace("-", "");
        int exempt = invoice.exempt ? 1 : 0;
        int year = Integer.parseInt(dateString.substring(2, 4));

        //get the payment modality of the customer
        String payMode = getPayModeByCustomerId(invoice.customer_id);

        /* FIRST INVOICE NUMBER MANAGMENT
        the first value is the number of the first invoice of the year specified in the 
          stored procedure [spGetInvoiceProgressive] of the DB*/
        String sql
                = "DECLARE @new_identity bigint EXECUTE spCreateInvoice "
                + /*first invoice number of the specified year in the store procedure*/ 1 + ", "
                + invoice.customer_id + ", "
                + year + ", '"
                + dateString + "', "
                + invoice.firstAmount + ", '"
                + firstDateString + "', "
                + invoice.secondAmount + ", '"
                + secondDateString + "', "
                + invoice.thirdAmount + ", '"
                + thirdDateString + "', "
                + invoice.taxableAmount + ", "
                + invoice.taxAmount + ", "
                + invoice.totalAmount + ", '"
                + invoice.paymentConditions.replace("'", "''") + "', '"
                + invoice.notes.replace("'", "''") + "', "
                + exempt + ", "
                + null + ","
                + null + ","
                + null + "," /**
                 * *** digitall invoice fields
                 */
                + null + ","
                + invoice.vatRate + ","/*aliquotaIVA*/
                + "'IT',"/*idTrasmittente_paese*/
                + "'08245660017',"/*idTrasmittente_codice*/
                + "'" + payMode + "',"
                + "  @new_identity OUTPUT";

        invoice.invoice_id = dbi.execute(sql).results[1].getLong("lastId");

        for (int i = 0; i < invoice.items.size(); i++) {
            //adds the row
            addInvoiceRow(invoice.items.get(i), invoice.invoice_id, i + 1);
            /*
            if the invoice row refers to a deliverynote row and there are no other
            deliverynote row to suggest sets the deliverynote to not be suggested anymore     
             */
            if (invoice.items.get(i).deliveryNoteRow_id != null) {
                setDeliveryNoteSuggested(invoice.items.get(i).deliveryNoteRow_id);
            }
        }

        //creates the related three amount schedule dates
        sql = "INSERT INTO dyn_AmountScheduleDates ( invoice_id, customer_id, ordinal, amount, amountDate ) "
                + "SELECT invoice_id, customer_id, 1, firstAmount, firstAmountDate FROM dyn_Invoices WHERE invoice_id = " + invoice.invoice_id
                + "INSERT INTO dyn_AmountScheduleDates ( invoice_id, customer_id, ordinal, amount, amountDate ) "
                + "SELECT invoice_id, customer_id, 2, secondAmount, secondAmountDate FROM dyn_Invoices WHERE invoice_id = " + invoice.invoice_id
                + "INSERT INTO dyn_AmountScheduleDates ( invoice_id, customer_id, ordinal, amount,amountDate ) "
                + "SELECT invoice_id, customer_id, 3, thirdAmount, thirdAmountDate FROM dyn_Invoices WHERE invoice_id = " + invoice.invoice_id;

        dbi.execute(sql);

        return readInvoices(invoice.invoice_id, null, null, null, null);
    }

    private Long addInvoiceRow(Invoice.Item item, Long invoice_id, int numero) throws SQLException {
        //result
        Long result;

        //order_id when related
        Long order_id = null;

        //first checks if the invoice row is related to a deliveryNoteRow
        if (item.deliveryNoteRow_id != null) {
            //then sets the deliveryNoteRow as invoiced and to be not suggested again
            setDeliveryNoteRowInvoiced(item.deliveryNoteRow_id, true);
            setDeliveryNoteRowSuggestion(item.deliveryNoteRow_id, false);
            //as requested by Paolo on march the 8th 2021 just one row is enough to set as invoiced all the delivery note
            setDeliveryNoteInvoiced(getDeliveryNoteByDeliveryNoteRowId(item.deliveryNoteRow_id).getLong("deliveryNote_id"), true);

            //cheks if the deliveryNoteRow refers to an order
            if (getDeliveryNoteRowOrderId(item.deliveryNoteRow_id) != null) {
                //assigns order_id value
                order_id = getDeliveryNoteRowOrderId(item.deliveryNoteRow_id);
                // if yes sets the order as invoiced
                setOrderInvoiced(order_id, true);

            }
        }

        //checks if the invoice row refers to an order but not to a deliveryNote row
        if (order_id == null && item.code != null && !item.code.equals("")) {
            order_id = getOrderIdByOrderCode(item.code);
            //then sets the order as invoiced
            setOrderInvoiced(order_id, true);
            //sets to be not suggested nex times
            setOrderSuggestion(order_id, false);
        }

        //creates the row
        result = dbi.create("dyn_invoicesRows")
                .value("invoice_id", invoice_id)
                .value("deliveryNoteRow_id", item.deliveryNoteRow_id)
                .value("code", item.code).value("order_id", order_id)
                .value("description", item.description)
                .value("quantity", item.quantity)
                .value("singleAmount", item.singleAmount)
                .value("totalAmount", item.totalAmount)
                .value("numero", numero)
                .goAndGetId();

        //adjurns story data if the row has been created
        if (result != null && order_id != null) {
            adjurnStoryData(order_id, "Fatturato: " + getInvoiceNumberAndDate(invoice_id));
        }

        return result;
    }

    public DbResult readInvoices(
            Long invoice_id,
            Long customer_id,
            String number,
            LocalDate fromDate,
            LocalDate toDate) throws SQLException {
        String fromDateString = null;
        String toDateString = null;

        if (fromDate == null) {
            fromDateString = "19000101";
        } else {
            fromDateString = DateTimeFormatter.ISO_LOCAL_DATE.format(fromDate).replace("-", "");
        }

        if (toDate == null) {
            toDateString = "30000101";
        } else {
            toDateString = DateTimeFormatter.ISO_LOCAL_DATE.format(toDate).replace("-", "");
        }

//        if(fromDateString == null){
//            fromDateString = DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDateTime.of(fromDate, LocalTime.now()));
//        }
//        String toDateString = DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDateTime.of(toDate, LocalTime.now()));
        String num = number != null && !number.equals("") && number.contains("-") ? number.split("-")[0] : null;
        String year = number != null && !number.equals("") && number.contains("-") ? number.split("-")[1] : null;

        long start = new Date().getTime();
        System.out.println("READING INVOICES [DataAccessObject.readInvoices]");

        DbResult out = dbi.read("dyn_Invoices_view").order("number")
                .andWhere(invoice_id != null, "invoice_id = " + invoice_id)
                .andWhere(customer_id != null, "customer_id = " + customer_id)
                .andWhere(number != null && !number.equals(""), "number = " + num)
                .andWhere(number != null && !number.equals(""), "year = " + year)
                .andWhere(" date >= '" + fromDateString + "'")
                .andWhere(" date <= '" + toDateString + "'")
                .order("year,number")
                .go();

        System.out.println("INVOICES READ [DataAccessObject.readInvoices] : elapsed msec " + (new Date().getTime() - start));

        return out;
    }
    
    public DbResult readAggregatedInvoices(
            Long customer_id,
            String orderCode,
            LocalDate fromDate,
            LocalDate toDate) throws SQLException {
        String fromDateString = null;
        String toDateString = null;

        if (fromDate == null) {
            fromDateString = "19000101";
        } else {
            fromDateString = DateTimeFormatter.ISO_LOCAL_DATE.format(fromDate).replace("-", "");
        }

        if (toDate == null) {
            toDateString = "30000101";
        } else {
            toDateString = DateTimeFormatter.ISO_LOCAL_DATE.format(toDate).replace("-", "");
        }

        System.out.println("READING AGGREGATED INVOICES [DataAccessObject.readInvoices]");

        DbResult out = dbi.read("dyn_Aggregated_Invoices_view")
                .andWhere(customer_id != null, "customer_id = " + customer_id)
                .andWhere(" date >= '" + fromDateString + "'")
                .andWhere(" date <= '" + toDateString + "'")
                .order("customerDenomination,orderCode")
                .go();

        return out;
    }

    public DbResult readInvoicesSchedule(
            Long customer_id,
            LocalDate fromDate,
            LocalDate toDate) throws SQLException {
        if (fromDate == null) {
            fromDate = LocalDate.of(1900, 1, 1);
        }

        if (toDate == null) {
            toDate = LocalDate.of(3000, 1, 1);
        }

        String fromDateString = DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDateTime.of(fromDate, LocalTime.now()));
        String toDateString = DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDateTime.of(toDate, LocalTime.now()));

        DbResult out = dbi.read("dyn_Invoices_view").order("number")
                .andWhere(customer_id != null, "customer_id = " + customer_id)
                .andWhere(" date >= '" + fromDateString + "'")
                .andWhere(" date <= '" + toDateString + "'")
                .go();

        return out;
    }

    public DbResult readInvoiceRows(Long invoice_id) throws SQLException {
        return dbi.read("dyn_InvoicesRows_view").order("invoiceRow_id")
                .andWhere(invoice_id != null, "[invoice_id] = " + invoice_id)
                .go();
    }

    public DbResult readInvoicesRows(DbResult invoices) throws SQLException {
        return dbi.read("dyn_invoicesRows")
                .andWhere("invoice_id = " + invoices.getLong("invoice_id"))
                .go();
    }

    private String getInvoiceNumberAndDate(Long invoice_id) throws SQLException {
        DbResult dbr = dbi.read("dyn_Invoices")
                .andWhere("invoice_id = " + invoice_id)
                .go();

        String date = dbr.getString("date");
        String year = date.substring(0, 4);
        String month = date.substring(4, 6);
        String day = date.substring(6, 8);
        date = day + "/" + month + "/" + year;

        return "fatt. n° " + dbr.getLong("number") + " del " + date;
    }

    public Long updateInvoice(Invoice invoice) throws SQLException {
        String firstDateString = invoice.firstAmountDate.replace("-", "");
        String secondDateString = invoice.secondAmountDate.replace("-", "");
        String thirdDateString = invoice.thirdAmountDate.replace("-", "");

        dbi.update("dyn_Invoices")
                .andWhere("invoice_id = " + invoice.invoice_id)
                .value("customer_id", invoice.customer_id)
                .value("number", getInvoiceNumberByInvoiceId(invoice.invoice_id))
                .value("firstAmount", invoice.firstAmount)
                .value("firstAmountDate", firstDateString)
                .value("secondAmount", invoice.secondAmount)
                .value("secondAmountDate", secondDateString)
                .value("thirdAmount", invoice.thirdAmount)
                .value("thirdAmountDate", thirdDateString)
                .value("taxableAmount", invoice.taxableAmount)
                .value("taxAmount", invoice.taxAmount)
                .value("aliquotaIVA", invoice.vatRate)
                .value("totalAmount", invoice.totalAmount)
                .value("paymentConditions", invoice.paymentConditions)
                .value("notes", invoice.notes)
                .value("exempt", invoice.exempt ? 1 : 0)
                .go();

        //gets all deliveryNoteRows involved and set them as again to be suggested , likewise the deliverynote itself
        DbResult invoiceRows_dbr = dbi.read("dyn_invoicesRows").andWhere("invoice_id = " + invoice.invoice_id).go();
        for (int i = 0; i < invoiceRows_dbr.rowsCount(); i++) {
            if (invoiceRows_dbr.getLong(i, "deliveryNoteRow_id") != null) {
                //gets the delivery note row
                DbResult deliveryNoteRow = dbi.read("dyn_deliveryNotesRows").where("deliveryNoteRow_id = " + invoiceRows_dbr.getLong(i, "deliveryNoteRow_id")).go();
                //sets as to be suggested the deliveryNote and the deliveryNoteRow
                dbi.update("dyn_DeliveryNotes").where("deliveryNote_id = " + deliveryNoteRow.getLong("deliveryNote_id")).value("invoiced", 0).value("suggested", 1).go();
                dbi.update("dyn_DeliveryNotesRows").where("deliveryNoteRow_id = " + deliveryNoteRow.getLong("deliveryNoteRow_id")).value("invoiced", 0).value("suggested", 1).go();
            }
        }

        //first delete all existing rows 
        dbi.delete("dyn_invoicesRows")
                .andWhere("invoice_id = " + invoice.invoice_id)
                .go();

        //than creates new rows
        for (int i = 0; i < invoice.items.size(); i++) {
            addInvoiceRow(invoice.items.get(i), invoice.invoice_id, i + 1);
        }

        //updates related a amount schedule dates
        dbi.update("dyn_AmountScheduleDates")
                .andWhere("invoice_id = " + invoice.invoice_id)
                .andWhere("ordinal = 1")
                .value("amount", invoice.firstAmount)
                .value("amountDate", invoice.firstAmountDate.replace("-", ""))
                .go();
        dbi.update("dyn_AmountScheduleDates")
                .andWhere("invoice_id = " + invoice.invoice_id)
                .andWhere("ordinal = 2")
                .value("amount", invoice.secondAmount)
                .value("amountDate", invoice.secondAmountDate.replace("-", ""))
                .go();
        dbi.update("dyn_AmountScheduleDates")
                .andWhere("invoice_id = " + invoice.invoice_id)
                .andWhere("ordinal = 3")
                .value("amount", invoice.thirdAmount)
                .value("amountDate", invoice.thirdAmountDate.replace("-", ""))
                .go();

        return invoice.invoice_id;
    }

    public DbResult getSuggestedDeliveryNotesRows(Long customer_id, String orderCode) throws SQLException {
        return dbi.read("dyn_DeliveryNotesRows_view")
                .andWhere("customer_id = " + customer_id)
                .andWhere("deliveryNoteInvoiced = " + 0)
                .andWhere("rowInvoiced = " + 0)
                .andWhere("suggested = " + 1)
                .andWhere("deliveryNoteSuggested = " + 1)
                .andWhere(!orderCode.equals("null"), "code = '" + orderCode + "'")
                .go();
    }

    private Long getInvoiceNumberByInvoiceId(Long invoice_id) throws SQLException {
        return dbi.read("dyn_Invoices")
                .andWhere("invoice_id = " + invoice_id)
                .go()
                .getLong("number");
    }

    public DbResult getInvoiceRowsByInvoiceId(Long invoice_id) throws SQLException {
        return dbi.read("dyn_invoicesRows")
                .andWhere("invoice_id = " + invoice_id)
                .go();

    }

    public DbResult getInvoiceDeliveryNotes(Long invoice_id) throws SQLException {
        //retrives all deliverynote ids related to the invoice 
        String sql = "select * from dyn_invoicesRows where invoice_id = " + invoice_id;
        DbResult dbr_RelatedInvoiceRows = dbi.execute(sql).result();

        //retrives all deliverynotes rows related to the invoice 
        sql = "select * from dyn_deliveryNotesRows where 1 = 0 ";
        for (int i = 0; i < dbr_RelatedInvoiceRows.rowsCount(); i++) {
            if (dbr_RelatedInvoiceRows.getLong(i, "deliveryNoteRow_id") != null) {
                sql += " or deliveryNoteRow_id = " + dbr_RelatedInvoiceRows.getLong(i, "deliveryNoteRow_id");
            }
        }

        //related deliveryNotesRows
        DbResult dbr_DeliveryNotesRows = null;
        if (dbi.execute(sql).results.length > 0)//cheks if there are delivery notes rows
        {
            dbr_DeliveryNotesRows = dbi.execute(sql).result();
        }

        DbResult dbr_DeliveryNotes = null;

        //retrieves all related deliveryNotes
        sql = "select * from dyn_deliveryNotes where 1 = 0 ";

        if (dbr_DeliveryNotesRows != null) {
            for (int i = 0; i < dbr_DeliveryNotesRows.rowsCount(); i++) {
                sql += " or deliveryNote_id = " + dbr_DeliveryNotesRows.getLong(i, "deliveryNote_id");
            }
            //related deliveryNotesRows
            dbr_DeliveryNotes = dbi.execute(sql).result();
        }

        return dbr_DeliveryNotes;
    }

    public DbResult getInvoiceRelatedDeliveryNotesRows(Long invoice_id) throws SQLException {
        DbResult invoiceRows = dbi.read("dyn_invoicesRows")
                .andWhere("invoice_id = " + invoice_id)
                .go();

        DbResult deliveryNotesRows;

        String sql = "select * from dyn_DeliveryNotesRows where 1 = 0 ";
        for (int i = 0; i < invoiceRows.rowsCount(); i++) {
            if (invoiceRows.getLong(i, "deliveryNoteRow_id") != null) {
                sql += " or deliveryNoteRow_id = " + invoiceRows.getLong(i, "deliveryNoteRow_id");
            }
        }
        deliveryNotesRows = dbi.execute(sql).result();

        return deliveryNotesRows;
    }

    public Boolean updateInvoiceDate(Long number, Integer year, String date) throws SQLException {
        dbi.update("dyn_Invoices").andWhere("number=" + number).andWhere("year=" + year).value("date", date).go();

        return dbi.read("dyn_Invoices").andWhere("number=" + number).andWhere("year=" + year).go().getString("date").equals(date);
    }

    //DIGITAL INVOICES
    public DbResult readDigInvoice(Long invoice_id) throws SQLException {
        String sql = "SELECT * FROM view_DigInvoice WHERE invoice_id =  " + invoice_id;

        return dbi.execute(sql).result();

    }

    public DbResult readDigInvoiceRows(Long invoice_id) throws SQLException {
        return dbi.read("view_DigInvoiceRows")
                .andWhere("[invoice_id] = " + invoice_id)
                .go();
    }

    public DbResult readCessionarioCommittente(Long invoice_id) throws SQLException {
        return dbi.read("view_CessionarioCommittente")
                .andWhere("[invoice_id] = " + invoice_id)
                .go();
    }

    public DbResult readDatiDDT(Long invoice_id) throws SQLException {
        return dbi.read("view_DatiDDT")
                .andWhere("[invoice_id] = " + invoice_id)
                .go();
    }

    public DbResult readDatiOrdineAcquisto(Long invoice_id) throws SQLException {
        return dbi.read("view_DatiOrdineAcquisto")
                .andWhere("[invoice_id] = " + invoice_id)
                .go();
    }

    public String getLastInvoiceDate() throws SQLException {
        String sql = "SELECT MAX(date) as date from dyn_Invoices";
        return dbi.execute(sql).result().getString("date");
    }

    //QUOTES
    public Quote createQuote(Quote quote) throws SQLException {
        String date = DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDateTime.of(quote.date, LocalTime.now()));

        /* FIRST INVOICE NUMBER MANAGMENT
        the first value is the number of the first quote number of alla quotes [spGetInvoiceProgressive] of the DB*/
        //writes the quote in DB
        String sql
                = "DECLARE @new_identity bigint EXECUTE spCreateQuote "
                + /*first quote number to start from*/ 1 + ", "
                + quote.customer_id + ", "
                + quote.year + ", "
                + quote.user_id + ", "
                + quote.firstTitle_id + ", "
                + quote.secondTitle_id + ", '"
                + date + "', '"
                + quote.address.replace("'", "''") + "', '"
                + quote.houseNumber + "', '"
                + quote.postalCode + "', '"
                + quote.city.replace("'", "''") + "', '"
                + quote.province + "', '"
                + quote.firstForAttention.replace("'", "''") + "', '"
                + quote.secondForAttention.replace("'", "''") + "', '"
                + quote.subject.replace("'", "''") + "', "
                + quote.amount + ", "
                + "  @new_identity OUTPUT";

        //assigns the quote_id in the Quote object
        Long quote_id = dbi.execute(sql).results[1].getLong("lastId");

        quote.quote_id = quote_id;

        //creates quote rows in DB and assigns quote_id int Quotq object rows
        for (int i = 0; i < quote.rows.size(); i++) {
            //assigns quote_id
            quote.rows.get(i).quote_id = quote_id;
            //writes in DB
            addQuoteRow(quote.rows.get(i));
        }

        return quote;
    }

    private Quote.Row addQuoteRow(Quote.Row row) throws SQLException {
        //creates the row
        dbi.create("dyn_QuotesRows")
                .value("quote_id", row.quote_id)
                .value("description", row.description)
                .value("rowAmount", row.rowAmount)
                .goAndGetId();

        return row;
    }

    public List<Quote> readQuotes(
            Long quote_id,
            Long customer_id,
            String number,
            LocalDate fromDate,
            LocalDate toDate) throws SQLException {
        if (fromDate == null) {
            fromDate = LocalDate.of(1900, 1, 1);
        }

        if (toDate == null) {
            toDate = LocalDate.of(3000, 1, 1);
        }

        String fromDateString = DateTimeFormatter.ISO_LOCAL_DATE.format(fromDate).replace("-", "");
        String toDateString = DateTimeFormatter.ISO_LOCAL_DATE.format(toDate).replace("-", "");

        String num = number != null && !number.equals("") && number.contains("-") ? number.split("-")[0] : null;
        String year = number != null && !number.equals("") && number.contains("-") ? number.split("-")[1] : null;

        //gets quotes from db
        DbResult quotes_dbr = dbi.read("dyn_Quote_view")
                .andWhere(quote_id != null, "quote_id = " + quote_id)
                .andWhere(customer_id != null, "customer_id = " + customer_id)
                .andWhere(number != null && !number.equals(""), "number = " + num)
                .andWhere(number != null && !number.equals(""), "year = " + year)
                .andWhere(" date >= '" + fromDateString + "'")
                .andWhere(" date <= '" + toDateString + "'")
                .go();

        //List of quotes
        List<Quote> quotes = new ArrayList<>();

        //for each quotes record creates a Quote instance and put it in the collection
        for (int i = 0; i < quotes_dbr.rowsCount(); i++) {
            //get quote rows from the db
            DbResult rows_dbr = dbi.read("dyn_QuotesRows").where("quote_id = " + quotes_dbr.getLong(i, "quote_id")).go();
            //create the Quote instance
            Quote quote = new Quote(quotes_dbr.record(i), rows_dbr);

            quotes.add(quote);
        }

        /* GIANLUCA'S METHOD
        DbResult quoteRows_dbr = dbi.read("dyn_QuotesRows")
            .andIsIn("quote_id",quote_dbr.getColumnData("quote_id"))
            .go();
        
        quote_dbr.addSubResultSet(quoteRows_dbr,"rows","quote_id",Long.class);*/
        return quotes;
    }

    public Quote readQuote(Long quote_id) throws SQLException {
        DbResult quote_dbr = dbi.read("dyn_Quote_view").where("quote_id = " + quote_id).go();
        DbResult rows_dbr = dbi.read("dyn_QuotesRows").where("quote_id = " + quote_id).go();

        return new Quote(quote_dbr.record(), rows_dbr);
    }

    public DbResult readQuoteRows(Long quote_id) throws SQLException {
        return dbi.read("dyn_QuotesRows")
                .andWhere(quote_id != null, "[quote_id] = " + quote_id)
                .go();
    }

    public Quote updateQuote(Quote quote) throws SQLException {
        String dateTime = DateTimeFormatter.ISO_LOCAL_DATE.format(quote.date).replace("-", "");

        dbi.update("dyn_Quotes")
                .andWhere("quote_id = " + quote.quote_id)
                .value("customer_id", quote.customer_id)
                .value("user_id", quote.user_id)
                .value("firstTitle_id", quote.firstTitle_id)
                .value("secondTitle_id", quote.secondTitle_id)
                .value("date", dateTime)
                .value("year", quote.year)
                .value("address", quote.address)
                .value("houseNumber", quote.houseNumber)
                .value("postalCode", quote.postalCode)
                .value("city", quote.city)
                .value("province", quote.province)
                .value("firstForAttention", quote.firstForAttention)
                .value("secondForAttention", quote.secondForAttention)
                .value("subject", quote.subject)
                .value("amount", quote.amount)
                .go();

        //deletes all quote rows from db
        dbi.delete("dyn_QuotesRows").where("quote_id = " + quote.quote_id).go();

        //writes rows on db
        //creates quote rows in DB and assigns quote_id int Quotq object rows
        for (int i = 0; i < quote.rows.size(); i++) {
            //assigns quote_id
            quote.rows.get(i).quote_id = quote.quote_id;
            //writes in DB
            addQuoteRow(quote.rows.get(i));
        }

        DbResult quote_dbr = dbi.read("dyn_Quote_view").where("quote_id = " + quote.quote_id).go();
        DbResult rows_dbr = dbi.read("dyn_QuotesRows").where("quote_id = " + quote.quote_id).go();

        return new Quote(quote_dbr.record(), rows_dbr);
    }

    public Long createQuoteAttachment(Long quote_id, Long user_id, String originalFileName) throws SQLException, IOException, ClassNotFoundException {
        String date = DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDateTime.now());

        return dbi.create("dyn_QuotesAttachments")
                .value("quote_id", quote_id)
                .value("user_id", user_id)
                .value("date", date)
                .value("originalFileName", originalFileName)
                .goAndGetId();

    }

    Boolean setQuoteAttachmentName(String originalFileName, String currentFileName, Long quoteAttachment_id) throws SQLException {
        Updater updater = dbi.update("dyn_QuotesAttachments")
                .andWhere("quoteAttachment_id = " + quoteAttachment_id)
                .value("currentFileName", currentFileName)
                .go();

        return (updater.getUpdateCount() == 1);
    }

    public DbResult readQuoteAttachments(Long quote_id, Long user_id) throws SQLException {
        return dbi.read("dyn_QuotesAttachments")
                .andWhere(quote_id == null, "quote_id IS NULL AND user_id = " + user_id)
                .andWhere(quote_id != null, "quote_id = " + quote_id)
                .go();
    }

    public void deleteQuoteAttachment(Long attachment_id) throws SQLException {
        //Path of the attachment
        Path p1;
        //retrieves all attachments having null as task_id
        DbResult dbr_attachment = dbi.read("dyn_QuotesAttachments")
                .andWhere("quoteAttachment_id = " + attachment_id)
                .go();

        //first removes the file from the folder then from the DB
        p1 = Paths.get(Config.QUOTES_ATTACH_DIR + dbr_attachment.getString("currentFileName"));
        try {
            Files.deleteIfExists(p1);
        } catch (IOException ex) {
        }

        dbi.delete("dyn_QuotesAttachments")
                .andWhere("quoteAttachment_id = " + dbr_attachment.getLong("quoteAttachment_id"))
                .go();

    }

    //Titles
    Long createTitle(String title) throws SQLException {
        return dbi.create("dic_Titles")
                .value("title", title)
                .goAndGetId();
    }

    public DbResult readTitles(Long title_id) throws SQLException {
        return dbi.read("dic_Titles")
                .andWhere(title_id != null, "[title_id] = " + title_id)
                .go();
    }

    boolean updateTitle(Long title_id, String title) throws SQLException {
        Updater updater = dbi.update("dic_Titles")
                .where("title_id = " + title_id)
                .value(title != null, "title", title)
                .go();

        //if all paramters are null returns true
        if (updater.getUpdateCount() == null) {
            return true;
        } else {
            return updater.getUpdateCount() == 1;
        }
    }

    public String getTitleName(Long title_id) throws SQLException {
        return dbi.read("dic_Titles").where("title_id = " + title_id).go().getString("title");
    }

    //COMPLIANCE CERTIFICATES
    public ComplianceCertificate createComplianceCertificate(ComplianceCertificate compCert) throws SQLException {
        String date = DateTimeFormatter.ISO_LOCAL_DATE.format(compCert.date);

        /* FIRST COMPLIANCE CERTIFICATE NUMBER MANAGMENT
        the first value is the number of the first certificate number of all certificates [spGetComplianceCertificateProgressive] of the DB*/
        //writes the certificate in DB
        String sql
                = "DECLARE @new_identity bigint EXECUTE spCreateComplianceCertificate "
                + /*first certificate number to start from*/ 1 + ", "
                + compCert.order_id + ", "
                + compCert.firstTitle_id + ", "
                + compCert.secondTitle_id + ", "
                + compCert.year + ", '"
                + date + "', '"
                + compCert.firstForAttention.replaceAll("'", "''") + "', '"
                + compCert.secondForAttention.replaceAll("'", "''") + "', '"
                + compCert.customerJobOrderCode + "', '"
                + compCert.orderDescription.replaceAll("'", "''") + "', "
                + "  @new_identity OUTPUT";

        //assigns the quote_id in the Quote object
        Long compCert_id = dbi.execute(sql).results[1].getLong("lastId");

        compCert.complianceCertificate_id = compCert_id;

        //COMPLETES PROPOERTIES INSTANCE ASSIGNING THAMKS THE RELATED ORDER VIEW
        DbResult compCertDbr = readComplianceCertificateDbr(compCert.order_id);
        compCert.customer_id = compCertDbr.getLong("customer_id");
        compCert.number = compCertDbr.getLong("number");
        compCert.customerDenomination = compCertDbr.getString("customerDenomination");
        compCert.firstTitle = compCertDbr.getString("compCertFirstTitle");
        compCert.secondTitle = compCertDbr.getString("compCertSecondTitle");
        compCert.address = compCertDbr.getString("customerAddress");
        compCert.houseNumber = compCertDbr.getString("customerHouseNumber");
        compCert.postalCode = compCertDbr.getString("customerPostalCode");
        compCert.city = compCertDbr.getString("customerCity");
        compCert.province = compCertDbr.getString("customerProvince");
        compCert.orderCode = compCertDbr.getString("code");
        compCert.firstName = compCertDbr.getString("creatorFirstName");
        compCert.lastName = compCertDbr.getString("creatorLastName");

        return compCert;
    }

    public ComplianceCertificate readComplianceCertificate(Long cert_id) throws SQLException {
        DbResult compCert_dbr = dbi.read("dyn_ComplianceCertificates_view").where("complianceCertificate_id = " + cert_id).go();

        return new ComplianceCertificate(compCert_dbr);
    }

    public DbResult readComplianceCertificateDbr(Long order_id) throws SQLException {
        return dbi.read("dyn_ComplianceCertificates_view").where("order_id = " + order_id).go();

    }

    public ComplianceCertificate updateComplianceCertificate(ComplianceCertificate compCert) throws SQLException {
        String dateTime = DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDateTime.of(compCert.date, LocalTime.now()));

        dbi.update("dyn_ComplianceCertificates")
                .andWhere("complianceCertificate_id = " + compCert.complianceCertificate_id)
                .value("firstTitle_id", compCert.firstTitle_id)
                .value("secondTitle_id", compCert.secondTitle_id)
                .value("date", dateTime)
                .value("year", compCert.year)
                .value("firstForAttention", compCert.firstForAttention)
                .value("secondForAttention", compCert.secondForAttention)
                .value("customerJobCode", compCert.customerJobOrderCode)
                .value("orderDescription", compCert.orderDescription)
                .go();

        DbResult compCert_dbr = dbi.read("dyn_ComplianceCertificates_view").where("complianceCertificate_id = " + compCert.complianceCertificate_id).go();

        return new ComplianceCertificate(compCert_dbr);
    }

    //CREDIT NOTES
    public DbResult createCreditNote(CreditNote creditNote) throws SQLException {
        String dateString = creditNote.date.replace("-", "");
        int exempt = creditNote.exempt ? 1 : 0;
        int year = Integer.parseInt(dateString.substring(2, 4));

        /* FIRST CREDIT NOTE NUMBER MANAGMENT
        the first value is the number of the first credit note of the year specified in the 
          stored procedure [spGetCreditNoteProgressive] of the DB*/
        String sql
                = "DECLARE @new_identity bigint EXECUTE spCreateCreditNote "
                + /*first invoice number of the specified year in the store procedure*/ 1 + ", "
                + creditNote.customer_id + ", "
                + year + ", '"
                + dateString + "', "
                + creditNote.taxableAmount + ", "
                + creditNote.taxAmount + ", "
                + creditNote.totalAmount + ", '"
                + creditNote.notes.replace("'", "''") + "', "
                + exempt + ", "
                + creditNote.vatRate + ","/*aliquotaIVA*/
                + "  @new_identity OUTPUT";

        creditNote.creditNote_id = dbi.execute(sql).results[1].getLong("lastId");

        for (int i = 0; i < creditNote.items.size(); i++) {
            //adds the row
            addCreditNoteRow(creditNote.items.get(i), creditNote.creditNote_id, i + 1);
        }

        return readCreditNotes(creditNote.creditNote_id, null, null, null, null);
    }

    private Long addCreditNoteRow(CreditNote.Item item, Long creditNote_id, int numero) throws SQLException {
        //result
        Long result;

        //creates the row
        result = dbi.create("dyn_CreditNotesRows")
                .value("creditNote_id", creditNote_id)
                .value("description", item.description)
                .value("quantity", item.quantity)
                .value("singleAmount", item.singleAmount)
                .value("totalAmount", item.totalAmount)
                .value("numero", numero)
                .goAndGetId();

        return result;
    }

    public DbResult readCreditNotes(Long creditNote_id, Long customer_id, Integer number, String fromDate, String toDate) throws SQLException {
        if (fromDate != null && !fromDate.equals("")) {
            try {
                //removes dashes for correct casting from string to int on sql server
                fromDate = DTF.format(DTF.parse(fromDate));
                fromDate = fromDate.replace("-", "");
                fromDate = "'" + fromDate + "'";
            } catch (Exception ex) {
                System.out.println("date conversion issues");
            }
        }

        if (toDate != null && !toDate.equals("")) {
            try {
                toDate = DTF.format(DTF.parse(toDate));
                toDate = toDate.replace("-", "");
                toDate = "'" + toDate + "'";
            } catch (Exception ex) {
                System.out.println("date conversion issues");
            }
        }

        long start = new Date().getTime();
        System.out.println("READING CREDIT NOTES [DataAccessObject.readCreditNotes]");

        DbResult out = dbi.read("dyn_CreditNotes_view").order("number")
                .andWhere(creditNote_id != null, "creditNote_id = " + creditNote_id)
                .andWhere(customer_id != null, "customer_id = " + customer_id)
                .andWhere(number != null, "number = " + number)
                .andWhere(fromDate != null && !fromDate.equals(""), " date >= " + fromDate)
                .andWhere(toDate != null && !toDate.equals(""), " date <= " + toDate)
                .go();

        System.out.println("CREDIT NOTES READ [DataAccessObject.readCreditNotes] : elapsed msec " + (new Date().getTime() - start));

        return out;
    }

    public DbResult readCreditNoteRows(Long creditNote_id) throws SQLException {
        return dbi.read("dyn_CreditNotesRows").order("creditNoteRow_id")
                .andWhere(creditNote_id != null, "[creditNote_id] = " + creditNote_id)
                .go();
    }

    public DbResult readCreditNoteRows(List<Long> creditNotesIds) throws SQLException {
        return dbi.read("dyn_CreditNotesRows").order("creditNoteRow_id")
                .andIsIn("creditNote_id", creditNotesIds)
                .go();
    }

    public Long updateCreditNote(CreditNote creditNote) throws SQLException {
        String dateString = creditNote.date.replace("-", "");

        dbi.update("dyn_CreditNotes")
                .andWhere("creditNote_id = " + creditNote.creditNote_id)
                .value("customer_id", creditNote.customer_id)
                .value("date", dateString)
                .value("taxableAmount", creditNote.taxableAmount)
                .value("taxAmount", creditNote.taxAmount)
                .value("aliquotaIVA", creditNote.vatRate)
                .value("totalAmount", creditNote.totalAmount)
                .value("notes", creditNote.notes)
                .value("exempt", creditNote.exempt ? 1 : 0)
                .go();

        //first delete all existing rows 
        dbi.delete("dyn_CreditNotesRows")
                .andWhere("creditNote_id = " + creditNote.creditNote_id)
                .go();

        //than creates new rows
        for (int i = 0; i < creditNote.items.size(); i++) {
            addCreditNoteRow(creditNote.items.get(i), creditNote.creditNote_id, i + 1);
        }

        return creditNote.creditNote_id;
    }

    public String getLastCreditNoteDate() throws SQLException {
        String sql = "SELECT MAX(date) as date from dyn_CreditNotes";
        return dbi.execute(sql).result().getString("date");
    }

    //DIGITAL CREDIT NOTES
    public DbResult readDigCreditNote(Long creditNote_id) throws SQLException {
        String sql = "SELECT * FROM view_DigCreditNote WHERE creditNote_id =  " + creditNote_id;

        return dbi.execute(sql).result();

    }

    public DbResult readDigCreditNoteRows(Long creditNote_id) throws SQLException {
        return dbi.read("dyn_CreditNotesRows")
                .andWhere("[creditNote_id] = " + creditNote_id)
                .go();
    }

    public DbResult readCNCessionarioCommittente(Long creditNote_id) throws SQLException {
        return dbi.read("view_CNCessionarioCommittente")
                .andWhere("[creditNote_id] = " + creditNote_id)
                .go();
    }

    //AMOUNT SCHEDULE DATES
    public List<AmountSchedule> readAmountSchedules(
            Long customer_id,
            String fromDate,
            String toDate) throws SQLException {
        if (fromDate == null) {
            fromDate = "19000101";
        }

        if (toDate == null) {
            toDate = "30001231";
        }

        //gets quotes from db
        DbResult amountSchedules_dbr = dbi.read("dyn_AmountSchedule_view")
                .andWhere(customer_id != null, "customer_id = " + customer_id)
                .andWhere(" amountDate >= '" + fromDate + "'")
                .andWhere(" amountDate <= '" + toDate + "'")
                .order("amountDate")
                .go();

        //List of amount schedules
        List<AmountSchedule> amountSchedules = new ArrayList<>();

        //for each quotes record creates a Quote instance and put it in the collection
        for (int i = 0; i < amountSchedules_dbr.rowsCount(); i++) {
            //create the Quote instance
            AmountSchedule amountSchedule = new AmountSchedule(amountSchedules_dbr.record(i));

            amountSchedules.add(amountSchedule);
        }

        return amountSchedules;
    }

}
