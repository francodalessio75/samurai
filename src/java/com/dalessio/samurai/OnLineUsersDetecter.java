/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dalessio.samurai;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * 
 *  Franco D'Alessio :
 * https://stackoverflow.com/questions/4069444/getting-a-list-of-active-sessions-in-tomcat-using-java
 */
public class OnLineUsersDetecter implements HttpSessionListener
{
    private static int numberOfUsersOnline;

    public OnLineUsersDetecter() {
        numberOfUsersOnline = 0;
    }

    public static int getNumberOfUsersOnline() { 
        return numberOfUsersOnline;
    }

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        System.out.println("Session created by Id : " + event.getSession().getId());
        synchronized (this) {
            numberOfUsersOnline++;
        }
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        System.out.println("Session destroyed by Id : " + event.getSession().getId());
        synchronized (this) {
            numberOfUsersOnline--;
        }
    }
}
