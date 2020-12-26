/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dalessio.samurai.digitalinvoice;

import com.dps.diginvoice.xml.DigitalInvoice;

/**
 *
 * @author Franco
 */
public class DigitalInvoice_DueEsse_vecchia extends DigitalInvoice
{
    // -------------------------------- RIFERIMENTO AL TAG ALTRI_DATI_GESTIONALI PER L'ESENZIONE IVA --------------------------------

   /* public String testoEsenzione; //Sulla scorta delle richieste di Vodaphone Automotive tale testo ove necessario va inserito nel tag 2.1.1.11 : Causale 
    
    
    public DigitalInvoice_DueEsse_vecchia( DbResult view_DigInvoice_dbr, DbResult view_DigInvoiceRows_dbr,  DbResult view_DatiDDT_dbr, DbResult view_DatiOrdineAcquisto_dbr, DbResult view_CessionarioCommittente ) throws ParseException
    {
        DecimalFormat df = new DecimalFormat("#.00");
        
        idTrasmittente_paese = "IT";
        idTrasmittente_codice = "08245660017";
        
        progressivoInvio = view_DigInvoice_dbr.getLong("number")+""+view_DigInvoice_dbr.getInteger("year");
        
        codiceDestinatario = view_DigInvoice_dbr.getString("codiceDestinatario");
        if ( codiceDestinatario.equals("0000000") )pecDestinatario  = view_DigInvoice_dbr.getString("pecDestinatario");
        
        contattiTrasmittente_telefono = "0331 220913";
        contattiTrasmittente_email = "info@duesse.it";
        
        prestatore.idFiscaleIva_paese = "IT";
        prestatore.idFiscaleIva_codice = "02677820124";
        
        prestatore.codiceFiscale = "02677820124";
        
        prestatore.anagrafica_denominazione = "DUESSE Service s.r.l.";
        
        prestatore.sede_indirizzo = "Via Scipione Ronchetti";
        prestatore.sede_numeroCivico = "189/2";
        prestatore.sede_comune = "Cavaria con Premezzo";
        prestatore.sede_CAP = "21044";
        prestatore.sede_provincia = "VA";
        prestatore.sede_nazione = "IT";
        
        prestatore.contatti_telefono = "0331 220913"; 
        prestatore.contatti_fax = "0331 220914"; 
        prestatore.contatti_email = "info@duesse.it";
        
        committente.idFiscaleIva_paese = "IT";
        committente.idFiscaleIva_codice = view_CessionarioCommittente.getString("idFiscaleIva_codice").trim();

        committente.codiceFiscale = view_CessionarioCommittente.getString("codiceFiscale").trim();

        committente.anagrafica_denominazione = view_CessionarioCommittente.getString("anagrafica_denominazione").trim();
        
        if( committente.anagrafica_denominazione == null || committente.anagrafica_denominazione.equals(""))
        {
            committente.anagrafica_nome = view_CessionarioCommittente.getString("nome").replace("°", "").trim();
            committente.anagrafica_cognome = view_CessionarioCommittente.getString("cognome").replace("°", "").trim();
        }
        committente.sede_indirizzo = view_CessionarioCommittente.getString("sede_indirizzo").replace("°", "").trim();
        committente.sede_numeroCivico = view_CessionarioCommittente.getString("sede_numeroCivico").replace("°", "").trim();
        committente.sede_CAP = view_CessionarioCommittente.getString("sede_CAP").replace("°", "").trim();
        committente.sede_comune = view_CessionarioCommittente.getString("sede_comune").replace("°", "").trim();
        committente.sede_provincia = view_CessionarioCommittente.getString("sede_provincia").replace("°", "").trim();
        committente.sede_nazione = "IT";
        
        data = SDF.parse(view_DigInvoice_dbr.getString("date").substring(0, 4)+"-"+view_DigInvoice_dbr.getString("date").substring(4, 6)+"-"+view_DigInvoice_dbr.getString("date").substring(6));
        
        numeroFattura = view_DigInvoice_dbr.getLong("number")+"/"+view_DigInvoice_dbr.getInteger("year");
        
        aliquotaIVA = view_DigInvoice_dbr.getDouble("aliquotaIVA");
        
        for( int i = 0; i < view_DigInvoiceRows_dbr.rowsCount(); i++ )
        {
            DettaglioLinee dettaglio = new DettaglioLinee();
            dettaglio.numeroLinea = view_DigInvoiceRows_dbr.getInteger(i,"numero");
            dettaglio.codiceArticolo(new CodiceArticolo());
            if(view_DigInvoiceRows_dbr.getString("code") != null && !view_DigInvoiceRows_dbr.getString("code").equals("") )
            {
                dettaglio.codiceArticolo.CodiceTipo = "Cod.";
                dettaglio.codiceArticolo.CodiceValore =  view_DigInvoiceRows_dbr.getString("code");
            }
            dettaglio.quantita = Double.valueOf(view_DigInvoiceRows_dbr.getInteger(i,"quantita"));
            dettaglio.descrizione = view_DigInvoiceRows_dbr.getString(i,"descrizione").replace("°", "");
            dettaglio.prezzoUnitario = view_DigInvoiceRows_dbr.getDouble(i,"prezzoUnitario");
            dettaglio.aliquotaIVA = aliquotaIVA;
            if(aliquotaIVA == 0 )
                dettaglio.natura = Natura.ESENTI;
            dettaglioLinee.add(dettaglio);
        }
        
        aliquotaIVA = view_DigInvoice_dbr.getDouble("aliquotaIVA");
        
        if( aliquotaIVA == 0 )
        {
            natura = Natura.ESENTI;
            bollo = true;
            if( !view_DigInvoice_dbr.getString("VATExemptionText").trim().equals(""))
            {
                testoEsenzione = view_DigInvoice_dbr.getString("VATExemptionText").replace("°", "");//is it possible to have a list of all forbidden characters ?
                causale.add(testoEsenzione);
            }
        }
       
        DettaglioPagamento dettaglioPagamento = new DettaglioPagamento();
        dettaglioPagamento.dataScadenzaPagamento = SDF.parse(view_DigInvoice_dbr.getString("firstAmountDate").substring(0, 4)+"-"+view_DigInvoice_dbr.getString("firstAmountDate").substring(4, 6)+"-"+view_DigInvoice_dbr.getString("firstAmountDate").substring(6));
        dettaglioPagamento.importoPagamento = view_DigInvoice_dbr.getDouble("firstAmount");
        
        if( view_DigInvoice_dbr.getString("modalitaPagamento").equals("MP05") )
            dettaglioPagamento.modalitàPagamento = ModalitaPagamento.BONIFICO;
        else if( view_DigInvoice_dbr.getString("modalitaPagamento").equals("MP01") )
            dettaglioPagamento.modalitàPagamento = ModalitaPagamento.CONTANTI;
        else if( view_DigInvoice_dbr.getString("modalitaPagamento").equals("MP02") )
            dettaglioPagamento.modalitàPagamento = ModalitaPagamento.ASSEGNO;
        else if( view_DigInvoice_dbr.getString("modalitaPagamento").equals("MP12") )
            dettaglioPagamento.modalitàPagamento = ModalitaPagamento.RIBA;
        else
            dettaglioPagamento.modalitàPagamento = ModalitaPagamento.BONIFICO;
        
        dettaglioPagamento.istitutoFinanziario = view_DigInvoice_dbr.getString("bank") != null ? view_DigInvoice_dbr.getString("bank") : "";
        
        dettaglioPagamento.IBAN = view_DigInvoice_dbr.getString("IBAN") != null ? view_DigInvoice_dbr.getString("IBAN") : "";
        
        dettaglioPagamenti.add(dettaglioPagamento);
        
        if( view_DigInvoice_dbr.getDouble("secondAmount") != 0 )
        {
            dettaglioPagamento = new DettaglioPagamento();
            dettaglioPagamento.dataScadenzaPagamento = SDF.parse(view_DigInvoice_dbr.getString("secondAmountDate").substring(0, 4)+"-"+view_DigInvoice_dbr.getString("secondAmountDate").substring(4, 6)+"-"+view_DigInvoice_dbr.getString("secondAmountDate").substring(6));
            dettaglioPagamento.importoPagamento = view_DigInvoice_dbr.getDouble("secondAmount");
            
            if( view_DigInvoice_dbr.getString("modalitaPagamento").equals("MP05") )
            dettaglioPagamento.modalitàPagamento = ModalitaPagamento.BONIFICO;
        else if( view_DigInvoice_dbr.getString("modalitaPagamento").equals("MP01") )
            dettaglioPagamento.modalitàPagamento = ModalitaPagamento.CONTANTI;
        else if( view_DigInvoice_dbr.getString("modalitaPagamento").equals("MP02") )
            dettaglioPagamento.modalitàPagamento = ModalitaPagamento.ASSEGNO;
        else if( view_DigInvoice_dbr.getString("modalitaPagamento").equals("MP12") )
            dettaglioPagamento.modalitàPagamento = ModalitaPagamento.RIBA;
        else
            dettaglioPagamento.modalitàPagamento = ModalitaPagamento.BONIFICO;
            
            dettaglioPagamento.istitutoFinanziario = view_DigInvoice_dbr.getString("bank") != null ? view_DigInvoice_dbr.getString("bank") : "";
            
            dettaglioPagamento.IBAN = view_DigInvoice_dbr.getString("IBAN") != null ? view_DigInvoice_dbr.getString("IBAN") : "";
            
            dettaglioPagamenti.add(dettaglioPagamento);
        }
        
        if( view_DigInvoice_dbr.getDouble("thirdAmount") != 0 )
        {
            dettaglioPagamento = new DettaglioPagamento();
            dettaglioPagamento.dataScadenzaPagamento = SDF.parse(view_DigInvoice_dbr.getString("thirdAmountDate").substring(0, 4)+"-"+view_DigInvoice_dbr.getString("thirdAmountDate").substring(4, 6)+"-"+view_DigInvoice_dbr.getString("thirdAmountDate").substring(6));
            dettaglioPagamento.importoPagamento = view_DigInvoice_dbr.getDouble("thirdAmount");
            
            if( view_DigInvoice_dbr.getString("modalitaPagamento").equals("MP05") )
            dettaglioPagamento.modalitàPagamento = ModalitaPagamento.BONIFICO;
        else if( view_DigInvoice_dbr.getString("modalitaPagamento").equals("MP01") )
            dettaglioPagamento.modalitàPagamento = ModalitaPagamento.CONTANTI;
        else if( view_DigInvoice_dbr.getString("modalitaPagamento").equals("MP02") )
            dettaglioPagamento.modalitàPagamento = ModalitaPagamento.ASSEGNO;
        else if( view_DigInvoice_dbr.getString("modalitaPagamento").equals("MP12") )
            dettaglioPagamento.modalitàPagamento = ModalitaPagamento.RIBA;
        else
            dettaglioPagamento.modalitàPagamento = ModalitaPagamento.BONIFICO;
            
            dettaglioPagamento.istitutoFinanziario = view_DigInvoice_dbr.getString("bank") != null ? view_DigInvoice_dbr.getString("bank") : "";
            
            dettaglioPagamento.IBAN = view_DigInvoice_dbr.getString("IBAN") != null ? view_DigInvoice_dbr.getString("IBAN") : "";
            
            dettaglioPagamenti.add(dettaglioPagamento);
        }
        
        /*if( dettaglioPagamenti.size() > 1  )
            condizioniPagamento = CondizioniPagamento.RATEALE; -- già gestito nella creazione dell'XML
            
        for( int i = 0; i <  view_DatiDDT_dbr.rowsCount(); i++ )
        {
            DatiDDT datiDDT = new DatiDDT();
            datiDDT.DataDDT = SDF.parse(view_DatiDDT_dbr.getString("date").substring(0, 4)+"-"+view_DatiDDT_dbr.getString("date").substring(4, 6)+"-"+view_DatiDDT_dbr.getString("date").substring(6));
            datiDDT.NumeroDDT = view_DatiDDT_dbr.getInteger("numero")+"/"+view_DatiDDT_dbr.getInteger("year");
            datiDDT.RiferimentoNumeroLinea = view_DatiDDT_dbr.getInteger(i,"numero");
            this.datiDDT.add(datiDDT);
        }
        
        //look: for each invoice row related to an order the OrdineAcquisto view gives a result. This record, to be a real DatiOrdineAcquisto tag content
        //it must contain idDocumento and codiceCommessa. Then the check on theese fields for each view record
        for( int i = 0; i <  view_DatiOrdineAcquisto_dbr.rowsCount(); i++ )
        {
            if( !view_DatiOrdineAcquisto_dbr.getString("ordine").trim().equals("") && !view_DatiOrdineAcquisto_dbr.getString("commessa").trim().equals("") )
            {
                DatiOrdineAcquisto datiOrdineAcquisto = new DatiOrdineAcquisto();
                datiOrdineAcquisto.RiferimentoNumeroLinea = view_DatiOrdineAcquisto_dbr.getInteger(i,"numero");
                datiOrdineAcquisto.IdDocumento = view_DatiOrdineAcquisto_dbr.getString(i,"ordine").trim().replace("°", "");
                datiOrdineAcquisto.CodiceCommessaConvenzione = view_DatiOrdineAcquisto_dbr.getString(i,"commessa").trim().replace("°", "");
                this.datiOrdineAcquisto.add(datiOrdineAcquisto);
            }
        }
    }*/
    
}
