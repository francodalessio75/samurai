/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dalessio.samurai.digitalinvoice;

import com.dps.dbi.DbResult;
import com.dps.diginvoice.xml.DigitalInvoice;
import static com.dps.diginvoice.xml.DigitalInvoice.SDF;
import java.text.ParseException;

/**
 *
 * @author Franco
 */
public class DigitalInvoice_DueEsse extends DigitalInvoice
{
    // -------------------------------- RIFERIMENTO AL TAG ALTRI_DATI_GESTIONALI PER L'ESENZIONE IVA --------------------------------
    
    public String testoEsenzione; //Sulla scorta delle richieste di Vodaphone Automotive tale testo ove necessario va inserito nel tag 2.1.1.11 : Causale 
    
    public DigitalInvoice_DueEsse( DbResult view_DigInvoice_dbr, DbResult view_DigInvoiceRows_dbr,  DbResult view_DatiDDT_dbr, DbResult view_DatiOrdineAcquisto_dbr, DbResult view_CessionarioCommittente ) throws ParseException
    {
        //set single amount having three decimals
        PrezzoUnitario3Cifre = true;
        
        //Gestione codice postale e partita IVA per clienti extra CEE, Per essi il CAP è 11111 per rispettare il formato e codice fiscale e partita IVA : OO99999999999
        String customerCountryCode =  view_CessionarioCommittente.getString("idFiscaleIva_paese");
        
        
        //FATTURA
        this.idTrasmittente_codice("IT")
            .idTrasmittente_codice("08245660017")
            .progressivoInvio(view_DigInvoice_dbr.getLong("number")+""+view_DigInvoice_dbr.getInteger("year"))
            .formatoTrasmissione(view_DigInvoice_dbr.getString("codiceDestinatario").length()==6?FormatoTrasmissione.FATTURA_PA_V12:FormatoTrasmissione.FATTURA_PRIVATI_V12)//if the univocal code has length = 6, then it is a public administration subject
            .esigibilitaIVA( view_DigInvoice_dbr.getString("codiceDestinatario").length()==6?EsigibilitaIVA.S:EsigibilitaIVA.I)//if the univocal code has length = 6, then it is a public administration subject, so it is automatically applied the split payment)
            .codiceDestinatario(view_DigInvoice_dbr.getString("codiceDestinatario"))
            .pecDestinatario((view_DigInvoice_dbr.getString("codiceDestinatario").equals("0000000") && view_DigInvoice_dbr.getString("pecDestinatario") != null )?view_DigInvoice_dbr.getString("pecDestinatario"):null)
            .contattiTrasmittente_telefono("0331 220913")
            .contattiTrasmittente_email("info@duesse.it")
        
        //PRESTATORE
        .prestatore
            .idFiscaleIva_paese("IT")
            .idFiscaleIva_codice("02677820124")
            .codiceFiscale("02677820124")
            .anagrafica_denominazione("DUESSE Service s.r.l.")
            .sede_indirizzo("Via Scipione Ronchetti")
            .sede_numeroCivico("189/2")
            .sede_comune("Cavaria con Premezzo")
            .sede_CAP("21044")
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
                //Gestione codice postale e partita IVA per clienti extra CEE, Per essi il CAP è 11111 per rispettare il formato e codice fiscale e partita IVA : OO99999999999
            .codiceFiscale(!customerCountryCode.equals("IT") ? "OO99999999999" : view_CessionarioCommittente.getString("codiceFiscale").trim())
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
        
        //Dati Fattura
        this.data(SDF.parse(view_DigInvoice_dbr.getString("date").substring(0, 4)+"-"+view_DigInvoice_dbr.getString("date").substring(4, 6)+"-"+view_DigInvoice_dbr.getString("date").substring(6)))
            .numeroFattura(view_DigInvoice_dbr.getLong("number")+"/"+view_DigInvoice_dbr.getInteger("year"));
        
        
        if( !view_DigInvoice_dbr.getString("VATExemptionText").trim().equals("") )
        {
            testoEsenzione = view_DigInvoice_dbr.getString("VATExemptionText").replace("’","'").replace("“","''").replace("”","''");//is it possible to have a list of all forbidden characters ?
            this.nuovaCausale(testoEsenzione);
        }
        
        
        
        for( int i = 0; i < view_DigInvoiceRows_dbr.rowsCount(); i++ )
        {
            DettaglioLinee dettaglio = this.nuovaLinea();
            dettaglio.numero(view_DigInvoiceRows_dbr.getInteger(i,"numero"))
                .quantita(Double.valueOf(view_DigInvoiceRows_dbr.getInteger(i,"quantita")))
                .descrizione(view_DigInvoiceRows_dbr.getString(i,"descrizione").replace("’","'").replace("“","''").replace("”","''"))
                .prezzoUnitario(view_DigInvoiceRows_dbr.getDouble(i,"prezzoUnitario"))
                .aliquotaIVA(view_DigInvoice_dbr.getDouble("aliquotaIVA"));
            
            //if vat code is italian, tax  is zero and vat exemption text is not empty then fills the Natura value to N3 ( non imponibile ) and set to be paid the 'bollo'
            if( committente.idFiscaleIva_paese.equals("IT") && view_DigInvoice_dbr.getDouble("aliquotaIVA") == 0 &&  !view_DigInvoice_dbr.getString("VATExemptionText").trim().equals("") )
            {
                dettaglio.natura(DigitalInvoice.Natura.NON_IMPONIBILI);
                this.bollo(true);
            }
            
            //if vat code is foreign and tax is zero, the VAT Tax must not be paid as well as the stamp, then the Natura value is set to N3 ( non soggetto ). Note that in this case the bollo must not be paid
            //THIS IS THE CASE FOR EXTRACEE INVOICES, WE ARE WAITING TI IMPLEMENT THE CASE FO NOT ITALIAN BUT INSIDE CEE INVOICES.(WRITTEN ON MARCH 14, 2019 )
            if( !committente.idFiscaleIva_paese.equals("IT") &&  view_DigInvoice_dbr.getDouble("aliquotaIVA") == 0 )
            {
                dettaglio.natura(DigitalInvoice.Natura.NON_SOGGETTE);
                this.bollo(false);
            }
            
            //if there is a codice articolo to add
            if(view_DigInvoiceRows_dbr.getString("code") != null && !view_DigInvoiceRows_dbr.getString("code").equals("") )
            {
                dettaglio.nuovoCodiceArticolo("Cod.", view_DigInvoiceRows_dbr.getString("code"));
            }
            //adds the dettaglio linea
            
        }
        
        //PAGAMENTI
        DettaglioPagamento pagamento = this.nuovoPagamento();
            pagamento.dataScadenzaPagamento(SDF.parse(view_DigInvoice_dbr.getString("firstAmountDate").substring(0, 4)+"-"+view_DigInvoice_dbr.getString("firstAmountDate").substring(4, 6)+"-"+view_DigInvoice_dbr.getString("firstAmountDate").substring(6)))
            .importoPagamento(view_DigInvoice_dbr.getDouble("firstAmount"));
        
        if( view_DigInvoice_dbr.getString("modalitaPagamento").equals("MP05") )
            pagamento.modalitàPagamento(DigitalInvoice.ModalitaPagamento.BONIFICO);
        else if( view_DigInvoice_dbr.getString("modalitaPagamento").equals("MP01") )
            pagamento.modalitàPagamento(DigitalInvoice.ModalitaPagamento.CONTANTI);
        else if( view_DigInvoice_dbr.getString("modalitaPagamento").equals("MP02") )
            pagamento.modalitàPagamento(DigitalInvoice.ModalitaPagamento.ASSEGNO);
        else if( view_DigInvoice_dbr.getString("modalitaPagamento").equals("MP12") )
            pagamento.modalitàPagamento(DigitalInvoice.ModalitaPagamento.RIBA);
        else
            pagamento.modalitàPagamento(DigitalInvoice.ModalitaPagamento.BONIFICO);
        
        pagamento.istitutoFinaziario(view_DigInvoice_dbr.getString("bank") != null ? view_DigInvoice_dbr.getString("bank") : "");
        
        pagamento.iban(view_DigInvoice_dbr.getString("IBAN") != null ? view_DigInvoice_dbr.getString("IBAN") : "");
        
        
        if( view_DigInvoice_dbr.getDouble("secondAmount") != 0 )
        {
            DettaglioPagamento pagamento_2 = this.nuovoPagamento();
            pagamento_2.dataScadenzaPagamento(SDF.parse(view_DigInvoice_dbr.getString("secondAmountDate").substring(0, 4)+"-"+view_DigInvoice_dbr.getString("secondAmountDate").substring(4, 6)+"-"+view_DigInvoice_dbr.getString("secondAmountDate").substring(6)))
            .importoPagamento(view_DigInvoice_dbr.getDouble("secondAmount"));
        
            if( view_DigInvoice_dbr.getString("modalitaPagamento").equals("MP05") )
                pagamento_2.modalitàPagamento(DigitalInvoice.ModalitaPagamento.BONIFICO);
            else if( view_DigInvoice_dbr.getString("modalitaPagamento").equals("MP01") )
                pagamento_2.modalitàPagamento(DigitalInvoice.ModalitaPagamento.CONTANTI);
            else if( view_DigInvoice_dbr.getString("modalitaPagamento").equals("MP02") )
                pagamento_2.modalitàPagamento(DigitalInvoice.ModalitaPagamento.ASSEGNO);
            else if( view_DigInvoice_dbr.getString("modalitaPagamento").equals("MP12") )
                pagamento_2.modalitàPagamento(DigitalInvoice.ModalitaPagamento.RIBA);
            else
                pagamento_2.modalitàPagamento(DigitalInvoice.ModalitaPagamento.BONIFICO);

            pagamento_2.istitutoFinaziario(view_DigInvoice_dbr.getString("bank") != null ? view_DigInvoice_dbr.getString("bank") : "");

            pagamento_2.iban(view_DigInvoice_dbr.getString("IBAN") != null ? view_DigInvoice_dbr.getString("IBAN") : "");
        }
        
        if( view_DigInvoice_dbr.getDouble("thirdAmount") != 0 )
        {
            DettaglioPagamento pagamento_3 = this.nuovoPagamento();
            pagamento_3.dataScadenzaPagamento(SDF.parse(view_DigInvoice_dbr.getString("thirdAmountDate").substring(0, 4)+"-"+view_DigInvoice_dbr.getString("secondAmountDate").substring(4, 6)+"-"+view_DigInvoice_dbr.getString("secondAmountDate").substring(6)))
            .importoPagamento(view_DigInvoice_dbr.getDouble("thirdAmount"));
        
            if( view_DigInvoice_dbr.getString("modalitaPagamento").equals("MP05") )
                pagamento_3.modalitàPagamento(DigitalInvoice.ModalitaPagamento.BONIFICO);
            else if( view_DigInvoice_dbr.getString("modalitaPagamento").equals("MP01") )
                pagamento_3.modalitàPagamento(DigitalInvoice.ModalitaPagamento.CONTANTI);
            else if( view_DigInvoice_dbr.getString("modalitaPagamento").equals("MP02") )
                pagamento_3.modalitàPagamento(DigitalInvoice.ModalitaPagamento.ASSEGNO);
            else if( view_DigInvoice_dbr.getString("modalitaPagamento").equals("MP12") )
                pagamento_3.modalitàPagamento(DigitalInvoice.ModalitaPagamento.RIBA);
            else
                pagamento_3.modalitàPagamento(DigitalInvoice.ModalitaPagamento.BONIFICO);

            pagamento_3.istitutoFinaziario(view_DigInvoice_dbr.getString("bank") != null ? view_DigInvoice_dbr.getString("bank") : "");

            pagamento_3.iban(view_DigInvoice_dbr.getString("IBAN") != null ? view_DigInvoice_dbr.getString("IBAN") : "");
        }
    
        
        /*if( dettaglioPagamenti.size() > 1  )
            condizioniPagamento = CondizioniPagamento.RATEALE; -- già gestito nella creazione dell'XML*/ 
            
        for( int i = 0; i <  view_DatiDDT_dbr.rowsCount(); i++ )
        {
            this.nuovoDDT()
                .dataDDT(SDF.parse(view_DatiDDT_dbr.getString(i,"date").substring(0, 4)+"-"+view_DatiDDT_dbr.getString(i,"date").substring(4, 6)+"-"+view_DatiDDT_dbr.getString(i,"date").substring(6)))
                .numeroDDT(view_DatiDDT_dbr.getInteger(i,"number")+"/"+view_DatiDDT_dbr.getInteger(i,"year"))
                .riferimentoNumeroLinea(view_DatiDDT_dbr.getInteger(i,"numero"));
        }
        
        //look: for each invoice row related to an order the OrdineAcquisto view gives a result. This record, to be a real DatiOrdineAcquisto tag content
        //it must contain idDocumento 
        for( int i = 0; i <  view_DatiOrdineAcquisto_dbr.rowsCount(); i++ )
        {
            if( view_DatiOrdineAcquisto_dbr.getString(i,"ordine") != null && !view_DatiOrdineAcquisto_dbr.getString(i,"ordine").equals("") )
            {
                // id documento is mandatory in XML rifNumLinea is granted by the code
                DatiOrdineAcquisto ordine = this.nuovoOrdineAcquisto()
                    .idDocumento(view_DatiOrdineAcquisto_dbr.getString(i,"ordine").trim().replace("’","'").replace("“","''").replace("”","''"))
                    .riferimentoNumeroLinea(view_DatiOrdineAcquisto_dbr.getInteger(i,"numero"));
                
                //adds date if there is one
                if( view_DatiOrdineAcquisto_dbr.getString(i,"dataOrdine").length() == 8 )
                    ordine.data(SDF.parse(view_DatiOrdineAcquisto_dbr.getString(i,"dataOrdine").substring(0, 4)+"-"+view_DatiOrdineAcquisto_dbr.getString(i,"dataOrdine").substring(4, 6)+"-"+view_DatiOrdineAcquisto_dbr.getString(i,"dataOrdine").substring(6)));
                
                //adds commessa if there is one
                if(  view_DatiOrdineAcquisto_dbr.getString(i,"commessa") != null && !view_DatiOrdineAcquisto_dbr.getString(i,"commessa").equals("")  )
                    ordine.codiceCommessaConvenzione(view_DatiOrdineAcquisto_dbr.getString(i,"commessa").trim().replace("’","'").replace("“","''").replace("”","''"));
                
            }
        }
    }
}


    

