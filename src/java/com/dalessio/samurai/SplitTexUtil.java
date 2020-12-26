/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dalessio.samurai;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 *
 * @author Franco
 */
public class SplitTexUtil
{
    
    public static List<String> splitText( String text, int maxLength )
    {
        //arraylist containing strings srhorter or equals than maxlength
        List<String> result = new ArrayList<>();
        
        //String joiner that separates string ( originalWords ) by space
        StringJoiner test = new StringJoiner(" ");
        StringJoiner line = new StringJoiner(" ");
        
        //array containing originalWords
        String[] words = text.trim().split(" ");
        
        //iterates all originalWords
        for ( int i = 0; i < words.length; i++ )
        {
            //adds word to test 
            test.add(words[i]);
            //if length is not max
            if( test.length() <= maxLength && !words[i].contains("*") )
            {
                //adds to line
                line.add(words[i].replace("*", ""));
            }
            else
            {
                //adds the line to result
                result.add(line.toString());
                
                //empties string joiners
                test = new StringJoiner(" ");
                line = new StringJoiner(" ");
                //add the current word if not it will be lost : see test paper  of 20/12/2018
                line.add(words[i].replace("*", ""));
                test.add(words[i].replace("*", ""));
            }
            //if is the last iteration ads to result
            if( i == ( words.length -1 ) )
                   result.add(line.toString()); 
        }
          
        
        return result;
    }
    
    /**
     * The user writes the text adopting these rules:
     * to render a group of words in bold just close them between two ash mark ( # ), e.g. 
            #questa è una scritta da stampare in grassetto#.
       To jump to a new line after a word just put at the end of the word a star ( * ).
       To make each word containing data about if it must be bold and if a new line is requested,
       a bidimensional array will be build. So for each word we have three array cells. 
       The first contains the word, the second the bold indicator and the last the new line indicator.
     * @param text the text to be separated in single words
     * @param maxLength max characters number allowed in a text line
     * @return a words bidimensional array containing for each word if it is bold and if after it a new 
     *         line is requested
     */
    public static String[][] enhancedSplitText( String text, int maxLength )
    {
        /*
            indicator to manage if a word is bold. It works actually like a flag. Infact the user indicates a group of words
            as to be rendered in bold, closing them between two ash mark,
            so the algorithm switch the flag any time it meets a word containing an ash mark.
        */
        String boldIndicator = "NORMAL";
        
        //array containing originalWords
        String[] originalWords = text.split(" ");
        
        /*
          thanks to the bold indicator state,
          each word contains in its related array cells if it is bold and bidimensional array containing for each word data about if it is bold and 
         if a new line is requested after it
        */
        String[][] toPrintWords = new String[ originalWords.length ][3]; 
        
       
        /* manages new line data*/
        
        //Test String joiner to check the text line length
        StringJoiner test = new StringJoiner(" ");
        
        //iterates all originalWords and puts a new line indicator in the third place of the array if a new line is necessary after the word
        for ( int i = 0; i < originalWords.length; i++ )
        {
            //if the word is not just a star or an ash mark puts the word in the string joiner to test the length
            if( !originalWords[i].equals("#") || !originalWords[i].equals("*"))
                test.add( originalWords[i] );
            
            /*insert new line data if:
              the length is too long;
              there is a star so the user expects a new line;
              it is the last word, so line contents will be outdistanced
            */
            if( test.length() > maxLength || originalWords[i].contains("*") )
            {
                //inserts line indicator
                toPrintWords[i][2] = "NEWLINE";
                //empties string joiner to begin a new line
                test = new StringJoiner(" ");
            }
            else
            {
                //inserts the line indicator
                toPrintWords[i][2] = "SAMELINE"; 
            }
            
            /* manages bold data*/
            
            //if the word contains the ash mark switchs the bold flag
            if( originalWords[i].contains("#" ) )
            {
                if( boldIndicator.equals("NORMAL"))
                    boldIndicator = "BOLD";
                else
                    boldIndicator = "NORMAL";
                    
            }
            
            //removes ash marks and stars
            originalWords [i] = originalWords[i].replace("#","").replace("*", "");
            
            //puts bold indicator
            toPrintWords[i][1] = boldIndicator;
            
            //ransfers words in the definitive array 
            toPrintWords[i][0] = originalWords[i];
        }
          
        return toPrintWords;
    }
    
    /**
     * The user writes the originalText adopting these rules:
 to render a group of words in bold just close them between two ash mark ( # ), e.g. 
            #questa è una scritta da stampare in grassetto#.
        This method gives back an IText Paragraph ready to be inserted in a rectangle
     * @param originalText the originalText to be formatted and inserted in the paragraph
     * @return e pargraph
     */
    public static List<Text> enhancedSplitText( String originalText ) throws IOException
    {
        //Styles
        Style normalBold = new Style() ;
        normalBold.setFont(PdfFontFactory.createFont( FontConstants.TIMES_ROMAN)).setFontSize(6).setVerticalAlignment(VerticalAlignment.BOTTOM).setPaddingTop(0).setPaddingBottom(0).setBold() ;
        
        //Styles
        Style normal = new Style() ;
        normal.setFont(PdfFontFactory.createFont( FontConstants.TIMES_ROMAN)).setFontSize(6).setVerticalAlignment(VerticalAlignment.BOTTOM).setPaddingTop(0).setPaddingBottom(0);

        
        Boolean boldFlag = false;
        
        List<Text> texts = new ArrayList<>();
        
        //array containing originalWords
        String[] originalWords = originalText.split(" ");
        
        //for each word manages new line and bold.If bold allies the bold style, id new line concatenates \n
        for( String s : originalWords )
        {
            //if there is an ash mark switchs bold indicator
            if( s.contains("#") )
               boldFlag = !boldFlag;
            
            //if there's a new line concatenates \n
            if( s.contains("*"))
                s += "\n";
            
            //removes unwanted characters
            s = s.replace("#","").replace("*", "");
            
            //adds a space
            s += " ";
           
            //gets the originalText object from the word
            Text text = new Text( s );
            
            if( boldFlag )
                text.addStyle(normalBold);
            else
                text.addStyle(normal);
            
            texts.add(text);
        }
        
        return texts;
    }
    
    /**
     * The user writes the originalText adopting these rules:
        to render a group of words in bold just close them between two ash mark ( # ), e.g. 
            #questa è una scritta da stampare in grassetto#.
        This method gives back a single formatted paragraph
     * @param originalText the originalText to be formatted and inserted in the paragraph
     * @return e pargraph
     */
    public static Paragraph getParagraph( String originalText ) throws IOException
    {
        //Styles
        Style normalBold = new Style() ;
        normalBold.setFont(PdfFontFactory.createFont( FontConstants.TIMES_ROMAN)).setFontSize(10).setVerticalAlignment(VerticalAlignment.BOTTOM).setPaddingTop(0).setPaddingBottom(0).setBold() ;
        
        //Styles
        Style normal = new Style() ;
        normal.setFont(PdfFontFactory.createFont( FontConstants.TIMES_ROMAN)).setFontSize(10).setVerticalAlignment(VerticalAlignment.BOTTOM).setPaddingTop(0).setPaddingBottom(0);

        
        Boolean boldFlag = false;
        
        Paragraph paragraph = new Paragraph();
        
        //array containing originalWords
        String[] originalWords = originalText.split(" ");
        
        //for each word manages new line and bold.If bold allies the bold style, id new line concatenates \n
        for( String s : originalWords )
        {
            //if there is an ash mark switchs bold indicator
            if( s.contains("#") )
               boldFlag = !boldFlag;
            
            //if there's a new line concatenates \n
            if( s.contains("*"))
                s += "\n";
            
            //removes unwanted characters
            s = s.replace("#","").replace("*", "");
            
            //adds a space
            s += " ";
           
            //gets the originalText object from the word
            Text text = new Text( s );
            
            if( boldFlag )
                text.addStyle(normalBold);
            else
                text.addStyle(normal);
            
            paragraph.add(text);
        }
        
        return paragraph;
    }
    
    /**
     * This method gives back a, IText paragraphs collection. Each paragraph 
     * is build considering data about bold words and new lines, so in pdf 
     * document each paragraph can be put in a rectangle and each rectangle is 
     * a document line.
     * @param originalText
     * @param maxLength
     * @param fontSize the fontsize
     * @return
     * @throws IOException 
     */
    public static List<Paragraph> getParagraphs( String originalText, int maxLength, int fontSize ) throws IOException
    {
        //Styles
        Style normalBold = new Style() ;
        normalBold.setFont(PdfFontFactory.createFont( FontConstants.TIMES_ROMAN)).setFontSize(fontSize).setVerticalAlignment(VerticalAlignment.BOTTOM).setPaddingTop(0).setPaddingBottom(0).setBold() ;
        
        //Styles
        Style normal = new Style() ;
        normal.setFont(PdfFontFactory.createFont( FontConstants.TIMES_ROMAN)).setFontSize(fontSize).setVerticalAlignment(VerticalAlignment.BOTTOM).setPaddingTop(0).setPaddingBottom(0);

        String[][] toPrintWords = enhancedSplitText(originalText, maxLength );
        
        //create a collection of Paragraphs
        List<Paragraph> paragraphs = new ArrayList<>();
        //adds the first paragraph
        paragraphs.add(new Paragraph());
                    
        /* for each word applies the style and puts it in the current paragraph or,
           if a new line is requested creates a new paragraph
        */
        for( int k = 0; k < toPrintWords.length; k++ )
        {
            //creates original Text
            Text text = new Text( toPrintWords[k][0] + " ");
            //adds the style
            if( toPrintWords[k][1].equals("BOLD"))
                text.addStyle(normalBold);
            else
                text.addStyle(normal);
            //adds the originalText to the last collection paragraph
            paragraphs.get(paragraphs.size() - 1 ).add(text.setFontSize(10).setTextAlignment(TextAlignment.JUSTIFIED)).setCharacterSpacing(.8f);  
            
            //if the word inposes a new line puts a new paragraph in the collection
            if( toPrintWords[k][2].equals("NEWLINE"))
                paragraphs.add(new Paragraph());
        }
        
        //finallyputs a new empty paragraph to outdistance items content
        //adds the first paragraph
        paragraphs.add(new Paragraph().add(new Text("")));
        
        return paragraphs;   
    }
   
}
