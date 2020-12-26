/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dalessio.samurai;

import java.sql.SQLException;

/**
 * Note: to active all tests substitute in all file //- with "", but be careful there are methods that change the state of the Application persistence Data!!!!
 * @author Franco
 */
public class DAOTesting 
{
    DataAccessObject dao = null;
    
    DAOTesting()
    {
        try{
        dao = new DataAccessObject();
       }catch( ClassNotFoundException ex ){}
    }
    
    
    public static void main(String[] args) throws ClassNotFoundException, SQLException
    {
        DAOTesting test = new DAOTesting();
        DataAccessObject dao = test.dao;
        
        //test authenticate
        //-System.out.println( dao.authenticate("franco", "franco", "session_id") > 0 ? " authentication ok " : "authentication failed " );
        
        //test userActive
        //-System.out.println( dao.userActive(10010L) ? "user active ok " : "userActive failed ");
        
        //test user creating
        //-Long newUser_id = dao.createUser("ciccio", "Ingrassia", "ciccio", "ciccio");
        //-System.out.println(  newUser_id != null  ? "user creation ok " + newUser_id : "user creation failed ");
        
        //test user role
        //-System.out.println( dao.getUserRole(10010L));
        
        //System.out.println(dao.changePassword( 20013L, "ciccio", "ciccio", "ciccia"));
        
    }
    
}
