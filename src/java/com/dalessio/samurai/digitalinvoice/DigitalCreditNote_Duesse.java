/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dalessio.samurai.digitalinvoice;

import com.dps.dbi.DbResult;
import com.dps.diginvoice.xml.DigitalInvoice;
import static com.dps.diginvoice.xml.DigitalInvoice.PrezzoUnitario3Cifre;
import static com.dps.diginvoice.xml.DigitalInvoice.SDF;
import java.text.ParseException;

/**
 *
 * @author Franco
 */
public class DigitalCreditNote_Duesse extends DigitalInvoice
{
    // -------------------------------- RIFERIMENTO AL TAG ALTRI_DATI_GESTIONALI PER L'ESENZIONE IVA --------------------------------
    
    public String testoEsenzione; //Sulla scorta delle richieste di Vodaphone Automotive tale testo ove necessario va inserito nel tag 2.1.1.11 : Causale 
    
    public DigitalCreditNote_Duesse( DbResult view_DigCreditNote_dbr, DbResult view_DigCreditNoteRows_dbr, DbResult view_CessionarioCommittente ) throws ParseException
    {
        //set single amount having three decimals
        PrezzoUnitario3Cifre = true;
        
        //Gestione codice postale e partita IVA per clienti extra CEE, Per essi il CAP è 11111 per rispettare il formato e codice fiscale e partita IVA : OO99999999999
        String customerCountryCode =  view_CessionarioCommittente.getString("idFiscaleIva_paese");
        
        
        //Nota Credito
        this.idTrasmittente_codice("IT")
            .idTrasmittente_codice("08245660017")
            .progressivoInvio("c"+view_DigCreditNote_dbr.getLong("number")+""+view_DigCreditNote_dbr.getInteger("year"))
            .formatoTrasmissione(view_DigCreditNote_dbr.getString("codiceDestinatario").length()==6?DigitalInvoice.FormatoTrasmissione.FATTURA_PA_V12:DigitalInvoice.FormatoTrasmissione.FATTURA_PRIVATI_V12)//if the univocal code has length = 6, then it is a public administration subject
            .esigibilitaIVA( view_DigCreditNote_dbr.getString("codiceDestinatario").length()==6?DigitalInvoice.EsigibilitaIVA.S:DigitalInvoice.EsigibilitaIVA.I)//if the univocal code has length = 6, then it is a public administration subject, so it is automatically applied the split payment)
            .codiceDestinatario(view_DigCreditNote_dbr.getString("codiceDestinatario"))
            .pecDestinatario((view_DigCreditNote_dbr.getString("codiceDestinatario").equals("0000000") && view_DigCreditNote_dbr.getString("pecDestinatario") != null )?view_DigCreditNote_dbr.getString("pecDestinatario"):null)
            .contattiTrasmittente_telefono("0331 220913")
            .contattiTrasmittente_email("info@duesse.it")
                
        //Tipo Documento
        .tipoDocumento( TipoDocumento.NOTA_DI_CREDITO)
        
        //PRESTATORE
        .prestatore
            .idFiscaleIva_paese("IT")
            .idFiscaleIva_codice("02677820124")
            .codiceFiscale("02677820124")
            .anagrafica_denominazione("DUESSE Service s.r.l.")
            .sede_indirizzo("Via Agusta")
            .sede_numeroCivico("51")
            .sede_comune("Samarate")
            .sede_CAP("21017")
            .sede_provincia("VA")
            .sede_nazione("IT")
            .contatti_telefono("0331 220913") 
            .contatti_fax("0331 220914") 
            .contatti_email("info@duesse.it").fine()
        
        //COMMITTENTE    
        .committente
            .idFiscaleIva_paese(customerCountryCode )
                //Gestione codice postale e partita IVA per clienti extra CEE, Per essi il CAP è 11111 per rispettare il formato e codice fiscale e partita IVA : OO99999999999
            .idFiscaleIva_codice( !customerCountryCode.equals("IT") ? "OO99999999999" : view_CessionarioCommittente.getString("idFiscaleIva_codice").trim())
            .anagrafica_denominazione(view_CessionarioCommittente.getString("anagrafica_denominazione").replace("’","'").replace("“","''").replace("”","''").trim())
            .sede_indirizzo(view_CessionarioCommittente.getString("sede_indirizzo").replace("’","'").replace("“","''").replace("”","''").trim())
            .sede_numeroCivico(view_CessionarioCommittente.getString("sede_numeroCivico").replace("’","'").replace("“","''").replace("”","''").trim())
                //Gestione codice postale e partita IVA per clienti extra CEE, Per essi il CAP è 11111 per rispettare il formato e codice fiscale e partita IVA : OO99999999999
            .sede_CAP(!customerCountryCode.equals("IT") ? "11111" : view_CessionarioCommittente.getString("sede_CAP").replace("’","'").replace("“","''").replace("”","''").trim())
            .sede_comune(view_CessionarioCommittente.getString("sede_comune").replace("’","'").replace("“","''").replace("”","''").trim())
            .sede_provincia(view_CessionarioCommittente.getString("sede_provincia").replace("’","'").replace("“","''").replace("”","''").trim())
            .sede_nazione("IT");
        
        if( committente.anagrafica_denominazione == null || committente.anagrafica_denominazione.equals(""))
        {
            committente.anagrafica_nome(view_CessionarioCommittente.getString("nome").replace("’","'").replace("“","''").replace("”","''").trim())
            .anagrafica_cognome(view_CessionarioCommittente.getString("cognome").replace("’","'").replace("“","''").replace("”","''").trim());
        }
        
        //Dati Nota Credito
        this.data(SDF.parse(view_DigCreditNote_dbr.getString("date").substring(0, 4)+"-"+view_DigCreditNote_dbr.getString("date").substring(4, 6)+"-"+view_DigCreditNote_dbr.getString("date").substring(6)))
            .numeroFattura(view_DigCreditNote_dbr.getLong("number")+"/"+view_DigCreditNote_dbr.getInteger("year"));
        
        
        if( !view_DigCreditNote_dbr.getString("VATExemptionText").trim().equals("") )
        {
            testoEsenzione = view_DigCreditNote_dbr.getString("VATExemptionText").replace("’","'").replace("“","''").replace("”","''");//is it possible to have a list of all forbidden characters ?
            this.nuovaCausale(testoEsenzione);
        }
        
        
        
        for( int i = 0; i < view_DigCreditNoteRows_dbr.rowsCount(); i++ )
        {
            DigitalInvoice.DettaglioLinee dettaglio = this.nuovaLinea();
            dettaglio.numero(view_DigCreditNoteRows_dbr.getInteger(i,"numero"))
                .quantita(Double.valueOf(view_DigCreditNoteRows_dbr.getInteger(i,"quantity")))
                .descrizione(view_DigCreditNoteRows_dbr.getString(i,"description").replace("’","'").replace("“","''").replace("”","''"))
                .prezzoUnitario(view_DigCreditNoteRows_dbr.getDouble(i,"singleAmount"))
                .aliquotaIVA(view_DigCreditNote_dbr.getDouble("aliquotaIVA"));
            
            //if vat code is italian, tax  is zero and vat exemption text is not empty then fills the Natura value to N3 ( non imponibile ) and set to be paid the 'bollo'
            if( committente.idFiscaleIva_paese.equals("IT") && view_DigCreditNote_dbr.getDouble("aliquotaIVA") == 0 &&  !view_DigCreditNote_dbr.getString("VATExemptionText").trim().equals("") )
            {
                dettaglio.natura(DigitalInvoice.Natura.NON_IMPONIBILI);
                this.bollo(true);
            }
            
            //if vat code is foreign and tax is zero, the VAT Tax must not be paid as well as the stamp, then the Natura value is set to N3 ( non soggetto ). Note that in this case the bollo must not be paid
            //THIS IS THE CASE FOR EXTRACEE INVOICES, WE ARE WAITING TI IMPLEMENT THE CASE FO NOT ITALIAN BUT INSIDE CEE INVOICES.(WRITTEN ON MARCH 14, 2019 )
            if( !committente.idFiscaleIva_paese.equals("IT") &&  view_DigCreditNote_dbr.getDouble("aliquotaIVA") == 0 )
            {
                dettaglio.natura(DigitalInvoice.Natura.NON_SOGGETTE);
                this.bollo(false);
            }
            
        }
        
        //PAGAMENTI
        
    }
}


    

