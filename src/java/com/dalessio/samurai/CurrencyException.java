/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dalessio.samurai;

/**
 *
 * @author Franco
 */
public class CurrencyException extends Exception{
    public CurrencyException(){
        super ("Currency conversion issues: ");
    }
    @Override
    public String toString(){
        return getMessage()+"something has gone wrong during conversion currency -> double or vice versa";
    }
}
