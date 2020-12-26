
package com.dalessio.samurai;

import com.dalessio.samurai.CurrencyException;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

/**
 *Exposes methods to convert Doubles to currency an vice versa
 * Implemented as singleton
 * @author Franco
 */
public class CurrencyUtility {
    private static CurrencyUtility instance;
    private CurrencyUtility(){
        
    }
    public static CurrencyUtility getCurrencyUtilityInstance(){
        if(instance == null)
            instance = new CurrencyUtility();
        return instance;
    }
    /**
     * takes a double and gives back a string array containing the result and the
     * currency formatted string in case of success 
     * @param amount
     * @return 
     * @throws exceptions.CurrencyException 
     */
    public String getCurrency( Double amount ) throws CurrencyException{
        //Locale for currency
        Locale locale = new Locale.Builder().setLanguage("it").setRegion("IT").build();
        Currency currentCurrency = Currency.getInstance(locale);
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        return currencyFormatter.format(amount);
    }
    /**
     * takes a currency formatted string and givesback a Double
     * it works only for italian currency format
     * @param currency
     * @return 
     * @throws exceptions.CurrencyException 
     */
    public Double getDouble( String currency) throws CurrencyException {
        //support variable
        Double dbl = 0.0;
        //if the string contains € simbol then is in a currency format 
        if( currency.contains("\u20ac")){
            //First replace every not ASCII characters ( € )
            currency = currency.replaceAll("[^\\p{ASCII}]", "");
            //then removes possible thousands separator
            currency = currency.replace(".", "");
            //finally substitutes decimal separator
            currency = currency.replace(",", ".");
            //tries to parse in case of numberformat exception throws currency exception
            try{
                dbl = Double.parseDouble(currency);
            }catch(NumberFormatException ex){
                throw new CurrencyException();
            }
        }
        else{
            //just replace decimal separator if there's comma instead of point
            currency = currency.replace(",", ".");
            //tries to parse in case of numberformat exception throws currency exception
            try{
                dbl = Double.parseDouble(currency);
            }catch(NumberFormatException ex){
                throw new CurrencyException();
            }
        }
        return dbl;
    }
}
