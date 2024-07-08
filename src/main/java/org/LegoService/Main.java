package org.LegoService;

import com.dieselpoint.norm.*;
import de.vandermeer.asciitable.AsciiTable;

import java.math.BigDecimal;
import java.util.*;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        Database db = new Database();
        db.setJdbcUrl("jdbc:mysql://localhost:3306/legoservice?serverTimezone=CET");
        db.setUser("root");
        db.setPassword("basididati2024");


        infoCredenziali parametriUtente = new infoCredenziali();
        accesso(db, parametriUtente);

        opzioniScelte(db, parametriUtente);
    }

    private static void accesso(Database db, infoCredenziali parametriUtente) {
        boolean verificaUsername = true;
        boolean verificaPassword = true;

        String username = "";
        String password = "";
        String dbUsername;
        String dbPassword;
        String permessi = "";

        while(verificaUsername == true) {
            System.out.println("inserisci il tuo username:");
            Scanner sc = new Scanner(System.in);
            username = sc.nextLine();
            dbUsername = db.sql("select Username from utente where Username = ?",username).first(String.class);
            if(username.equals(dbUsername)){verificaUsername = false;}
            else{System.out.println("Non esiste un utente con questo Username");}
        }
        while(verificaPassword == true) {
            System.out.println("inserisci la tua password:");
            Scanner sc = new Scanner(System.in);
            password = sc.nextLine();
            dbPassword = db.sql("select Password from utente where Username = ? and Password = ?",username,password).first(String.class);
            if(password.equals(dbPassword)){verificaPassword = false;}
            else{System.out.println("Password sbagliata");}
        }
        permessi = db.sql("select Permessi from utente where Username = ? and Password = ?",username,password).first(String.class);

        parametriUtente.setUsername(username);
        parametriUtente.setPassword(password);
        parametriUtente.setPermessi(permessi);
    }


    private static void opzioniScelte(Database db, infoCredenziali parametriUtente) {
        boolean verificaScelta = true;

        String permessi = parametriUtente.getPermessi();
        String opzione;
        System.out.println("Ciao, che opzione scegli?");
        if(permessi.equals("Cliente")) {
            System.out.println("(1) per aggiungere il codice di un set Lego che possiedi alla tua lista");
            System.out.println("(2) per rimuovere un set Lego che possiedi dalla tua lista");
            System.out.println("(3) per vedere quali set Lego sono nella tua lista");
            System.out.println("(4) per fare una ricerca se puoi fare un set lego");
            System.out.println("(5) per fare un ordine Lego");
            System.out.println("(6) per vedere i tuoi ordini");
            System.out.println("(7) per unirti a una delle mailinglist");
            System.out.println("(8) per uscire da una delle mailinglist");
            System.out.println("(9) per vedere a che mailinglist sei iscritto");
            System.out.println("Digita il numero corrispondente alla tua scelta:");

            Scanner sc = new Scanner(System.in);
            while (verificaScelta == true) {
                opzione = sc.nextLine();

                switch (opzione) {
                    case "1":
                        aggiungiSetLegoMiei(db, parametriUtente);
                        verificaScelta = false;
                        break;
                    case "2":
                        rimuoviSetLegoMiei(db, parametriUtente);
                        verificaScelta = false;
                    case "3":
                        cheSetLegoHo(db, parametriUtente);
                        verificaScelta = false;
                        break;
                    case "4":
                        verificaPossoFareQuestoSet(db, parametriUtente);
                        verificaScelta = false;
                        break;
                    case "5":
                        inserimentoOrdineNuovo(db, parametriUtente);
                        verificaScelta = false;
                    case "6":
                        vediMieiOrdini(db, parametriUtente);
                        verificaScelta = false;
                    case "7":
                        aggiungiInMailingList(db, parametriUtente);
                        verificaScelta = false;
                        break;
                    case "8":
                        rimuoviDaMailingList(db, parametriUtente);
                        verificaScelta = false;
                        break;
                    case "9":
                        aCheMailingListSonoIscritto(db, parametriUtente);
                        verificaScelta = false;
                        break;
                    default:
                        System.out.println("Opzione non esistente!");
                }
            }
        }else if (permessi.equals("Admin")) {
            System.out.println("(1) per aggiungere un set Lego tra quelli disonibili");
            System.out.println("(2) per aggiungere dei nuovi pezzi Lego");
            System.out.println("(3) per cambiare il valore di un set Lego");
            System.out.println("(4) per vedere quali ordini non risultano pagati");
            System.out.println("(5) per aggiungere un pagamento");
            System.out.println("(6) per cambiare lo stato di un ordine da In lavorazione a Spedito");
            System.out.println("(7) per cambiare lo stato di un ordine da Spedito a Consegnato");
            System.out.println("(8) per creare una nuova mailinglist");
            System.out.println("(9) per cancellare una mailinglist");
            System.out.println("Digita il numero corrispondente alla tua scelta:");

                Scanner sc = new Scanner(System.in);
                while(verificaScelta == true) {
                    opzione = sc.nextLine();

                    switch (opzione) {
                        case "1":
                            aggiungiNuovoSetLego(db, parametriUtente);
                            verificaScelta = false;
                            break;
                        case "2":
                            aggiungiNuovoPezzo(db, parametriUtente);
                            verificaScelta = false;
                        case "3":
                            valore0SetLego(db, parametriUtente);
                            verificaScelta = false;
                        case "4":
                            ordineNonPagato(db, parametriUtente);
                            verificaScelta = false;
                            break;
                        case "5":
                            aggiungiPagamento(db, parametriUtente);
                            verificaScelta = false;
                            break;
                        case "6":
                            cambiaStatoOrdineInSpedito(db, parametriUtente);
                            verificaScelta = false;
                        case "7":
                            cambiaStatoOrdineInConsegnato(db, parametriUtente);
                            verificaScelta = false;
                        case "8":
                            creaMailingList(db, parametriUtente);
                            verificaScelta = false;
                        case "9":
                            eliminaMailingList(db, parametriUtente);
                            verificaScelta = false;
                        default:
                            System.out.println("Opzione non esistente!");
                    }
                }
        }
    }

//----Azioni Cliente----//
    private static void aggiungiSetLegoMiei(Database db, infoCredenziali parametriUtente){
        Scanner sc = new Scanner(System.in);
        String codiceString;
        String Username = parametriUtente.getUsername();
        int codiceInt = 0;
        boolean verifica = true;

        List<Integer> possiede = db.sql("select CodiceSet from possiede where Username = ?",Username).results(Integer.class);
        AsciiTable at = new AsciiTable();
        at.addRule();
        at.addRow("Codice Set");
        at.addRule();
        for (int indicatore:possiede){
            at.addRow(indicatore);
        }
        at.addRule();
        System.out.println(at.render());

        while(verifica == true) {
            System.out.println("Digita il codice del set Lego che vuoi aggiungere, se non ricordi il codice e vuoi tornare indietro digita 0:");
            codiceString = sc.nextLine();
            if (codiceString.equals("0")) {opzioniScelte(db, parametriUtente);}
            codiceInt = Integer.parseInt(codiceString);
            Long controllo = db.sql("select count(*) from possiede where Username = ? AND CodiceSet = ?",Username,codiceInt).first(Long.class);
            if (controllo == 0) {
                controllo = db.sql("select count(*) from setLego where CodiceSet = ?",codiceInt).first(Long.class);
                if (controllo == 0) {
                    System.out.println("Non abbiamo questo set Lego");
                }else {
                    db.sql("CALL sp_aggiungiSetLegoMiei(?,?)",Username,codiceInt).execute();
                }
                verifica = false;
            }
            else {System.out.println("Questo set lego è già nella tua lista");}
        }


        verifica = true;
        while(verifica == true) {
            System.out.println("Vuoi aggiungere un altro set Lego?");
            System.out.println("(si)");
            System.out.println("(no)");
            System.out.println("Digita la tua scelta:");
            String scelta = sc.nextLine();
            if (scelta.equals("si")) {
                aggiungiSetLegoMiei(db, parametriUtente);
                verifica = false;
            } else if (scelta.equals("no")) {
                verifica = false;
                opzioniScelte(db, parametriUtente);
            }else{System.out.println("Opzione non esistente!");}
        }
    }

    private static void rimuoviSetLegoMiei(Database db, infoCredenziali parametriUtente){
        Scanner sc = new Scanner(System.in);
        String codiceString;
        String Username = parametriUtente.getUsername();
        int codiceInt = 0;
        boolean verifica = true;

        while(verifica == true) {
        List<Integer> possiede = db.sql("select CodiceSet from possiede where Username = ?",Username).results(Integer.class);
        AsciiTable at = new AsciiTable();
        at.addRule();
        at.addRow("Codice Set");
        at.addRule();
        for (int indicatore:possiede){
            at.addRow(indicatore);
        }
        at.addRule();
        System.out.println(at.render());


        System.out.println("Digita il codice del set Lego che vuoi rimuovere, se vuoi tornare indietro digita 0:");
        codiceString = sc.nextLine();
        if (codiceString.equals("0")) {opzioniScelte(db, parametriUtente);}
        codiceInt = Integer.parseInt(codiceString);
        Long controllo = db.sql("select count(*) from possiede where Username = ? AND CodiceSet = ?",Username,codiceInt).first(Long.class);
        if (controllo == 1) {verifica = false;}
        else {System.out.println("Questo set lego non è nella tua lista");}
        }
        db.sql("CALL sp_rimuoviSetLegoMiei(?,?)",Username,codiceInt).execute();

        verifica = true;
        while(verifica == true) {
            System.out.println("Vuoi rimuovere un altro set Lego?");
            System.out.println("(si)");
            System.out.println("(no)");
            System.out.println("Digita la tua scelta:");
            String scelta = sc.nextLine();
            if (scelta.equals("si")) {
                rimuoviSetLegoMiei(db, parametriUtente);
                verifica = false;
            } else if (scelta.equals("no")) {
                verifica = false;
                opzioniScelte(db, parametriUtente);
            } else{System.out.println("Opzione non esistente!");}
        }
    }

    private static void cheSetLegoHo(Database db, infoCredenziali parametriUtente){
        Scanner sc = new Scanner(System.in);
        String Username = parametriUtente.getUsername();
        boolean verifica = true;

        List<Integer> possiede = db.sql("select CodiceSet from possiede where Username = ?",Username).results(Integer.class);
        AsciiTable at = new AsciiTable();
        at.addRule();
        at.addRow("Codice Set");
        at.addRule();
        for (int indicatore:possiede){
            at.addRow(indicatore);
        }
        at.addRule();
        System.out.println(at.render());


        while(verifica == true) {
            System.out.println("Vuoi tornare al menù principale?");
            System.out.println("(si)");
            System.out.println("(no)");
            System.out.println("Digita la tua scelta:");
            String scelta = sc.nextLine();
            if (scelta.equals("no")) {
                cheSetLegoHo(db, parametriUtente);
                verifica = false;
            } else if (scelta.equals("si")) {
                verifica = false;
                opzioniScelte(db, parametriUtente);
            } else{System.out.println("Opzione non esistente!");}
        }
    }

    private static void verificaPossoFareQuestoSet(Database db, infoCredenziali parametriUtente){
        Scanner sc = new Scanner(System.in);
        String codiceString;
        String Username = parametriUtente.getUsername();
        int codiceInt = 0;
        boolean verifica = true;

        while(verifica == true) {
            System.out.println("Digita il codice del set Lego che vuoi controllare, se non ricordi il codice e vuoi tornare indietro digita 0:");
            codiceString = sc.nextLine();
            if (codiceString.equals("0")) {opzioniScelte(db, parametriUtente);}
            codiceInt = Integer.parseInt(codiceString);
            Long controllo = db.sql("select count(*) from setlego where CodiceSet = ?", codiceInt).first(Long.class);
            if (controllo == 1) {
                verifica = false;
            } else {
                System.out.println("Non abbaimo quest set Lego");
            }
        }

        //Risolto a tentativi, vi è un bug che non fa funzionare bene
        List risultato = db.sql("CALL sp_verificaPossoFareQuestoSet(?,?)", Username, codiceInt).results(String.class);

        int risultatoInt = ((BigDecimal) risultato.getFirst()).intValueExact();

        if (risultatoInt >= 0) {System.out.println("Puoi farlo con i set Lego presenti nella tua lista");}
        else {System.out.println("NON puoi farlo con i set Lego presenti nella tua lista");}
        verifica = true;
        while(verifica == true) {
            System.out.println("Vuoi controllare un altro set Lego?");
            System.out.println("(si)");
            System.out.println("(no)");
            System.out.println("Digita la tua scelta:");
            String scelta = sc.nextLine();
            if (scelta.equals("si")) {
                verificaPossoFareQuestoSet(db, parametriUtente);
                verifica = false;
            } else if (scelta.equals("no")) {
                opzioniScelte(db, parametriUtente);
                verifica = false;
            } else{System.out.println("Opzione non esistente!");}
        }
    }

    private static void inserimentoOrdineNuovo(Database db, infoCredenziali parametriUtente){
        Scanner sc = new Scanner(System.in);
        String codiceString;
        String Username = parametriUtente.getUsername();
        int codiceInt = 0;
        int codiceSetLego = 0;
        int numero = 0;
        boolean verifica = true;

        while(verifica == true) {
            System.out.println("Che set lego vuoi ordinare? Se non ricordi il codice e vuoi tornare indietro digita 0:");
            codiceString = sc.nextLine();
            if (codiceString.equals("0")) {opzioniScelte(db, parametriUtente);}
            codiceInt = Integer.parseInt(codiceString);
            codiceSetLego = codiceInt;
            Long controllo = db.sql("select count(*) from setLego where CodiceSet = ? AND Valore > 0",codiceInt).first(Long.class);
            if (controllo == 1) {
                while(verifica == true) {
                    System.out.println("Quanti set vuoi ordinare?");
                    codiceString = sc.nextLine();
                    numero = Integer.parseInt(codiceString);
                    if (numero <= 0) {System.out.println("Non puoi ordinarne 0 o un numero negativo!");}
                    else{
                        verifica = false;
                    }
                }
            }
            else {System.out.println("Non abbaimo quest set Lego");}
        }

        verifica = true;
        Integer numeroOrdine = db.sql("select MAX(numeroOrdine) from ordine").first(Integer.class) +1;
        Transaction trans = db.startTransaction();
        try{
            db.sql("CALL sp_inserimentoOrdineNuovo(?,?,?,?)",numeroOrdine, Username, codiceSetLego, numero).execute();

            while(verifica == true) {
                System.out.println("Vuoi ordinare un altro set Lego?");
                System.out.println("(si)");
                System.out.println("(no)");
                System.out.println("Digita la tua scelta:");
                String scelta = sc.nextLine();
                if (scelta.equals("si")) {
                    while(verifica == true) {
                        System.out.println("Che set lego vuoi ordinare? Se non ricordi il codice e vuoi tornare indietro digita 0:");
                        codiceString = sc.nextLine();
                        if (codiceString.equals("0")) {
                            System.out.println("L'ordine esistente è stato inviato, verrai contattato tramite email per i metodi di pagamento. L'ordine sarà spedito una volta verificato il pagamento");
                            opzioniScelte(db, parametriUtente);
                        }
                        codiceInt = Integer.parseInt(codiceString);
                        codiceSetLego = codiceInt;
                        Long controllo = db.sql("select count(*) from setLego where CodiceSet = ? AND Valore > 0", codiceInt).first(Long.class);
                        if (controllo == 1) {
                            while (verifica == true) {
                                System.out.println("Quanti set vuoi ordinare?");
                                codiceString = sc.nextLine();
                                if (codiceString.equals("0")) {
                                    System.out.println("Non puoi ordinarne 0");
                                } else {
                                    numero = Integer.parseInt(codiceString);
                                    verifica = false;
                                }
                            }
                            verifica = false;
                        }
                        else {System.out.println("Non abbaimo quest set Lego");}
                    }
                    db.sql("CALL sp_inserimentoOrdineEsistente(?,?,?)",numeroOrdine, codiceSetLego, numero).execute();
                    verifica = true;
                } else if (scelta.equals("no")) {
                    System.out.println("L'ordine esistente è stato inviato, verrai contattato tramite email per i metodi di pagamento. L'ordine sarà spedito una volta verificato il pagamento");
                    opzioniScelte(db, parametriUtente);
                    verifica = false;
                } else{System.out.println("Opzione non esistente!");}
            }
            trans.commit();
        } catch (Throwable t) {
            trans.rollback();
        }
    }

    private static void vediMieiOrdini(Database db, infoCredenziali parametriUtente){
        Scanner sc = new Scanner(System.in);
        String Username = parametriUtente.getUsername();
        boolean verifica = true;


        List<mieiOrdini> ordine = db.sql("select NumeroOrdine,DataOrdine,Stato from ordine where Username = ?",Username).results(mieiOrdini.class);
        AsciiTable at = new AsciiTable();
        at.addRule();
        at.addRow("Numero ordine","Data Ordine","Stato");
        at.addRule();
        for (mieiOrdini indicatore:ordine){
            at.addRow(indicatore.NumeroOrdine,indicatore.DataOrdine,indicatore.Stato);
        }
        at.addRule();
        System.out.println(at.render());


        while(verifica == true) {
            System.out.println("Vuoi tornare al menù principale?");
            System.out.println("(si)");
            System.out.println("(no)");
            System.out.println("Digita la tua scelta:");
            String scelta = sc.nextLine();
            if (scelta.equals("si")) {
                opzioniScelte(db, parametriUtente);
                verifica = false;
            }else if (scelta.equals("no")) {
                verifica = false;
                vediMieiOrdini(db, parametriUtente);
            }else{System.out.println("Opzione non esistente!");}
        }
    }

    private static void aggiungiInMailingList(Database db, infoCredenziali parametriUtente){
        Scanner sc = new Scanner(System.in);
        String codiceString = "";
        String Username = parametriUtente.getUsername();
        boolean verifica = true;

        while(verifica == true) {
            List<String> partecipazione = db.sql("select DISTINCT nome from mailinglist where nome not in (SELECT nome from partecipazione where Username = ?)", Username).results(String.class);
            if (partecipazione.size() == 0) {
                System.out.println("Sei già in tutte le mailinglist");
            } else {
                AsciiTable at = new AsciiTable();
                at.addRule();
                at.addRow("Nome mailinglist");
                at.addRule();
                for (String indicatore : partecipazione) {
                    at.addRow(indicatore);
                }
                at.addRule();
                System.out.println(at.render());

                System.out.println("A quale mailinglist vuoi iscriverti?  Se non vuoi iscriverti a nessuna mailinglist e tornare indietro digita 0:");
                codiceString = sc.nextLine();
                if (codiceString.equals("0")) {opzioniScelte(db, parametriUtente);}
                Long controllo = db.sql("select count(*) from mailingList where nome = ?",codiceString).first(Long.class);
                if (controllo == 1) {
                    controllo = db.sql("select count(*) from partecipazione where nome = ? and Username = ?",codiceString,Username).first(Long.class);
                    if (controllo == 0) {
                        db.sql("CALL sp_aggiungiInMailingList(?,?)",Username,codiceString).execute();
                        verifica = false;
                    }
                    else{System.out.println("Sei già iscritto a questa mailinglist");}
                }
                else {System.out.println("Questa mailinglist non esiste");}
            }
            verifica = true;

            while (verifica == true) {
                System.out.println("Vuoi iscriverti a un altra mailing list?");
                System.out.println("(si)");
                System.out.println("(no)");
                System.out.println("Digita la tua scelta:");
                String scelta = sc.nextLine();
                if (scelta.equals("si")) {
                    aggiungiInMailingList(db, parametriUtente);
                }
                else if(scelta.equals("no")){
                    opzioniScelte(db, parametriUtente);
                    verifica = false;
                }else {
                    System.out.println("Opzione non esistente!");
                }
            }
        }
    }

    private static void rimuoviDaMailingList(Database db, infoCredenziali parametriUtente){
        Scanner sc = new Scanner(System.in);
        String codiceString = "";
        String Username = parametriUtente.getUsername();
        boolean verifica = true;

        while(verifica == true) {
            List<String> partecipazione = db.sql("select Nome from partecipazione where Username = ?",Username).results(String.class);
            AsciiTable at = new AsciiTable();
            at.addRule();
            at.addRow("Nome mailinglist");
            at.addRule();
            for (String indicatore:partecipazione){
                at.addRow(indicatore);
            }
            at.addRule();
            System.out.println(at.render());


            System.out.println("Digita il nome della mailinglist a cui vuoi disiscriverti, se vuoi tornare indietro digita 0:");
            codiceString = sc.nextLine();
            if (codiceString.equals("0")) {opzioniScelte(db, parametriUtente);}
            Long controllo = db.sql("select count(*) from partecipazione where Username = ? AND Nome = ?",Username,codiceString).first(Long.class);
            if (controllo == 1) {verifica = false;}
            else {System.out.println("Non sei iscritto a questa mailinglist");}
        }
        db.sql("CALL sp_rimuoviDaMailingList(?,?)",Username,codiceString).execute();

        verifica = true;
        while(verifica == true) {
            System.out.println("Vuoi disiscriverti da un altra mailinglist?");
            System.out.println("(si)");
            System.out.println("(no)");
            System.out.println("Digita la tua scelta:");
            String scelta = sc.nextLine();
            if (scelta.equals("si")) {
                rimuoviDaMailingList(db, parametriUtente);
                verifica = false;
            } else if (scelta.equals("no")) {
                opzioniScelte(db, parametriUtente);
                verifica = false;
            } else{System.out.println("Opzione non esistente!");}
        }
    }


    private static void aCheMailingListSonoIscritto(Database db, infoCredenziali parametriUtente){
        Scanner sc = new Scanner(System.in);
        String Username = parametriUtente.getUsername();
        boolean verifica = true;

        List<String> partecipazione = db.sql("select Nome from partecipazione where Username = ?",Username).results(String.class);
        AsciiTable at = new AsciiTable();
        at.addRule();
        at.addRow("Nome mailinglist");
        at.addRule();
        for (String indicatore:partecipazione){
            at.addRow(indicatore);
        }
        at.addRule();
        System.out.println(at.render());


        while(verifica == true) {
            System.out.println("Vuoi tornare al menù principale?");
            System.out.println("(si)");
            System.out.println("(no)");
            System.out.println("Digita la tua scelta:");
            String scelta = sc.nextLine();
            if (scelta.equals("si")) {
                opzioniScelte(db, parametriUtente);
                verifica = false;
            } else if (scelta.equals("no")) {
                aCheMailingListSonoIscritto(db, parametriUtente);
                verifica = false;
            } else{System.out.println("Opzione non esistente!");}
        }
    }

    //----Azioni Admin----//

    private static void aggiungiNuovoSetLego(Database db, infoCredenziali parametriUtente){
        Scanner sc = new Scanner(System.in);
        String codiceString;
        String collezione = "";
        int codiceInt = 0;
        int codiceSetLego = 0;
        int anno = 0;
        int numero = 0;
        int codicePezzo = 0;
        int codiceColore = 0;
        double valore = 0;
        boolean verifica = true;

        while(verifica == true) {
            System.out.println("Che set lego vuoi aggiungere? Inserisci il suo codice set. Se non ricordi i parametri e vuoi tornare indietro digita 0:");
            codiceString = sc.nextLine();
            if (codiceString.equals("0")) {
                opzioniScelte(db, parametriUtente);
            }
            codiceInt = Integer.parseInt(codiceString);
            codiceSetLego = codiceInt;
            Long controllo = db.sql("select count(*) from setLego where CodiceSet = ?", codiceInt).first(Long.class);
            if (controllo == 0) {
                System.out.println("Di che collezione è? Se non ricordi i parametri e vuoi tornare indietro digita 0:");
                codiceString = sc.nextLine();
                collezione = codiceString;
                if (codiceString.equals("0")) {
                    opzioniScelte(db, parametriUtente);
                } else {
                    while(verifica == true){
                        System.out.println("Di che anno è? Se non ricordi i parametri e vuoi tornare indietro digita 0:");
                        codiceString = sc.nextLine();
                        anno = Integer.parseInt(codiceString);
                        if (codiceString.equals("0")) {
                            opzioniScelte(db, parametriUtente);
                        } else if(anno <= 1955){System.out.println("Non esistevano i lego in quell'anno");}
                            else{
                                System.out.println("Quanto vale? Se non ricordi i parametri e vuoi tornare indietro digita 0:");
                                codiceString = sc.nextLine();
                                valore = Double.parseDouble(codiceString);
                                if (codiceString.equals("0")) {
                                opzioniScelte(db, parametriUtente);
                                } else if(valore < 0){System.out.println("Non regaliamo i Lego");}
                                    else{
                                    while (verifica == true) {
                                        System.out.println("Qual è il codice del primo pezzo? Se non ricordi i parametri e vuoi tornare indietro digita 0:");
                                        codiceString = sc.nextLine();
                                        codicePezzo = Integer.parseInt(codiceString);
                                        controllo = db.sql("select count(*) from pezzo where CodicePezzo = ?", codicePezzo).first(Long.class);
                                        if (controllo == 0) {
                                            System.out.println("Questo pezzo non esiste");
                                        } else {
                                            while (verifica == true) {
                                                System.out.println("Qual è il codice del colore? Se non ricordi i parametri e vuoi tornare indietro digita 0:");
                                                codiceString = sc.nextLine();
                                                codiceColore = Integer.parseInt(codiceString);
                                                controllo = db.sql("select count(*) from pezzo where Colore = ? and CodicePezzo = ?", codiceColore, codicePezzo).first(Long.class);
                                                if (controllo == 0) {
                                                    System.out.println("Questo colore non esiste");
                                                } else {
                                                    while (verifica == true) {
                                                        System.out.println("Quanti ce ne sono di questo pezzo? Se non ricordi i parametri e vuoi tornare indietro digita 0:");
                                                        codiceString = sc.nextLine();
                                                        numero = Integer.parseInt(codiceString);
                                                        System.out.println("numero "+numero);
                                                        if (numero <= 0) {
                                                            System.out.println("Non possono esserci 0 pezzi");
                                                        } else if (numero > 0) {verifica = false;}
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                verifica = false;
            } else {
                System.out.println("Esiste già questo set Lego");}

        }



        verifica = true;
        Transaction trans = db.startTransaction();
        try{
            db.sql("CALL sp_aggiungiNuovoSetLego(?,?,?,?,?,?,?)",codiceSetLego,anno,collezione,valore,codicePezzo,codiceColore,numero).execute();

            while(verifica == true) {
                System.out.println("Vuoi aggiungere un altro componente Lego?");
                System.out.println("(si)");
                System.out.println("(no)");
                System.out.println("Digita la tua scelta:");
                String scelta = sc.nextLine();
                if (scelta.equals("si")) {
                    while(verifica == true) {
                        System.out.println("Qual è il codice del pezzo? Se non ricordi i parametri e vuoi tornare indietro digita 0:");
                        codiceString = sc.nextLine();
                        codicePezzo = Integer.parseInt(codiceString);
                        Long controllo = db.sql("select count(*) from pezzo where CodicePezzo = ?", codicePezzo).first(Long.class);
                        if (controllo == 0) {
                            System.out.println("Questo pezzo non esiste");
                        } else {
                            while (verifica == true) {
                                System.out.println("Qual è il codice del colore? Se non ricordi i parametri e vuoi tornare indietro digita 0:");
                                codiceString = sc.nextLine();
                                codiceColore = Integer.parseInt(codiceString);
                                controllo = db.sql("select count(*) from pezzo where Colore = ? and CodicePezzo = ?", codiceColore, codicePezzo).first(Long.class);
                                if (controllo == 0) {
                                    System.out.println("Questo colore non esiste");
                                } else {
                                    while (verifica == true) {
                                        System.out.println("Quanti ce ne sono di questo pezzo? Se non ricordi i parametri e vuoi tornare indietro digita 0:");
                                        codiceString = sc.nextLine();
                                        numero = Integer.parseInt(codiceString);
                                        if (numero <= 0) {
                                            System.out.println("Non possono esserci 0 pezzi");
                                        } else verifica = false;
                                    }
                                }
                            }
                        }
                    }
                    db.sql("CALL sp_aggiungiComponentiSetLegoEsistente(?,?,?,?)",codiceSetLego,codicePezzo,codiceColore,numero).execute();
                    verifica = true;
                } else if (scelta.equals("no")) {
                    System.out.println("Il set Lego è stato aggiunto");
                    opzioniScelte(db, parametriUtente);
                    verifica = false;
                } else{System.out.println("Opzione non esistente!");}
            }
            trans.commit();
        } catch (Throwable t) {
            trans.rollback();
        }

    }

    private static void aggiungiNuovoPezzo(Database db, infoCredenziali parametriUtente) {
        Scanner sc = new Scanner(System.in);
        String codiceString;
        int codiceInt = 0;
        int codicePezzo = 0;
        int codiceColore = 0;
        boolean verifica = true;

        while (verifica == true) {
            System.out.println("Che pezzo Lego vuoi aggiungere? Inserisci il suo codice componente. Se non ricordi i parametri e vuoi tornare indietro digita 0:");
            codiceString = sc.nextLine();
            if (codiceString.equals("0")) {opzioniScelte(db, parametriUtente);}
            codiceInt = Integer.parseInt(codiceString);
            codicePezzo = codiceInt;
            if (codicePezzo >= 0) {
                System.out.println("Di che colore è? Inserisci il suo codice colore. Se non ricordi i parametri e vuoi tornare indietro digita 0:");
                codiceString = sc.nextLine();
                if (codiceString.equals("0")) {opzioniScelte(db, parametriUtente);}
                else {
                    codiceInt = Integer.parseInt(codiceString);
                    codiceColore = codiceInt;
                    if (codiceColore >= 0) {
                        Long controllo = db.sql("select count(*) from pezzo where CodicePezzo = ? AND Colore = ?", codicePezzo, codiceColore).first(Long.class);
                        if (controllo == 0) {
                            db.sql("call sp_aggiungiNuovoPezzo(?,?)", codicePezzo, codiceColore).execute();
                        } else {
                            System.out.println("Questo pezzo esiste già");
                        }
                    }else {System.out.println("Non esiste un pezzo Lego con codice colore non positivo");}
                }
            }
            else{System.out.println("Non esiste un pezzo Lego con codice non positivo");}

            System.out.println("Vuoi aggiungere un altro componente Lego?");
            System.out.println("(si)");
            System.out.println("(no)");
            System.out.println("Digita la tua scelta:");
            String scelta = sc.nextLine();
            if (scelta.equals("si")) {aggiungiNuovoPezzo(db,parametriUtente);}
            else if (scelta.equals("no")) {
                System.out.println("Il set Lego è stato aggiunto");
                opzioniScelte(db, parametriUtente);
                verifica = false;
            } else{System.out.println("Opzione non esistente!");}
        }
    }

    private static void valore0SetLego(Database db, infoCredenziali parametriUtente){
    Scanner sc = new Scanner(System.in);
    String codiceString;
    double valore = 0;
    int codiceSet = 0;
    boolean verifica = true;

        while (verifica == true) {
            System.out.println("Di che set Lego vuoi cambiare il valore? Inserisci il suo codice set. Se non ricordi i parametri e vuoi tornare indietro digita 0:");
            codiceString = sc.nextLine();
            if (codiceString.equals("0")) {
                opzioniScelte(db, parametriUtente);
            }
            codiceSet = Integer.parseInt(codiceString);
            Long controllo = db.sql("select count(*) from setLego where CodiceSet = ?", codiceSet).first(Long.class);
            if (controllo == 1) {
                while(verifica == true) {
                    System.out.println("Qual è il suo nuovo valore? Se non ricordi i parametri e vuoi tornare indietro digita 'vai indietro':");
                    codiceString = sc.nextLine();
                    valore = Double.parseDouble(codiceString);
                    if (codiceString.equals("vai indietro")) {
                        opzioniScelte(db, parametriUtente);
                    } else if (valore < 0) {
                        System.out.println("Non può avere valore negativo");
                    } else if (valore >= 0) {
                        db.sql("call sp_valore0SetLego(?,?)", codiceSet, valore).execute();
                        verifica = false;
                    }
                }
            }
            else{System.out.println("Non abbiamo questo set Lego");}
        }
    }

    private static void ordineNonPagato(Database db, infoCredenziali parametriUtente){
        Scanner sc = new Scanner(System.in);
        boolean verifica = true;

        List<ordineNonPagati> ordine = db.sql("select NumeroOrdine,Username,DataOrdine,Stato from ordine where DataSpedizione IS NULL").results(ordineNonPagati.class);
        AsciiTable at2 = new AsciiTable();
        at2.addRule();
        at2.addRow("Numero ordine","Username","Data ordine","Stato");
        at2.addRule();
        for (ordineNonPagati indicatore:ordine){
            at2.addRow(indicatore.NumeroOrdine,indicatore.Username,indicatore.DataOrdine,indicatore.Stato);
        }
        at2.addRule();
        System.out.println(at2.render());


        while(verifica == true) {
            System.out.println("Vuoi tornare al menù principale?");
            System.out.println("(si)");
            System.out.println("(no)");
            System.out.println("Digita la tua scelta:");
            String scelta = sc.nextLine();
            if (scelta.equals("si")) {
                opzioniScelte(db, parametriUtente);
                verifica = false;
            }else if (scelta.equals("no")) {ordineNonPagato(db, parametriUtente);}
            else{System.out.println("Opzione non esistente!");}
        }
    }

    private static void aggiungiPagamento(Database db, infoCredenziali parametriUtente){
        Scanner sc = new Scanner(System.in);
        String codiceString = "";
        long controllo = 0;
        int codiceOrdine = 0;
        double valore = 0;
        boolean ciclo1 = true;
        boolean ciclo2 = true;

        while(ciclo1 == true){
            System.out.println("Di che ordine vuoi aggiungere il pagamento? Se non ricordi i parametri e vuoi tornare indietro digita '0':");
            codiceString = sc.nextLine();
            if(codiceString.equals("0")) {opzioniScelte(db, parametriUtente);}
            codiceOrdine = Integer.parseInt(codiceString);
            controllo = db.sql("select count(*) from ordine where Stato = 'In lavorazione' AND NumeroOrdine = ?",codiceOrdine).first(Long.class);
            if(controllo == 0){
                System.out.println("Ordine non trovato o già pagato");
            }
            else{
                System.out.println("Di quanto è il pagamento? Se non ricordi i parametri e vuoi tornare indietro digita 'vai indietro':");
                codiceString = sc.nextLine();
                if(codiceString.equals("0")) {opzioniScelte(db, parametriUtente);}
                valore = Double.parseDouble(codiceString);
                if(valore <= 0){System.out.println("valore sbagliato");}
                else if (valore > 0){
                    db.sql("call sp_aggiungiPagamento(?,?)",valore,codiceOrdine).execute();
                    ciclo1 = false;
                }
            }
        }
        while(ciclo2 == true) {
            System.out.println("Vuoi tornare al menù principale?");
            System.out.println("(si)");
            System.out.println("(no)");
            System.out.println("Digita la tua scelta:");
            String scelta = sc.nextLine();
            if (scelta.equals("si")) {
                opzioniScelte(db, parametriUtente);
                ciclo2 = false;
            }else if (scelta.equals("no")) {ordineNonPagato(db, parametriUtente);}
            else{System.out.println("Opzione non esistente!");}
        }
    }

    private static void cambiaStatoOrdineInSpedito(Database db, infoCredenziali parametriUtente){
        Scanner sc = new Scanner(System.in);
        String codiceString = "";
        long controllo = 0;
        int codiceOrdine = 0;
        boolean ciclo1 = true;
        boolean ciclo2 = true;

        while(ciclo1 == true){
            System.out.println("Di che ordine vuoi cambiare lo stato? Se non ricordi i parametri e vuoi tornare indietro digita '0':");
            codiceString = sc.nextLine();
            if(codiceString.equals("0")) {opzioniScelte(db, parametriUtente);}
            codiceOrdine = Integer.parseInt(codiceString);
            controllo = db.sql("select count(*) from ordine where Stato = 'In lavorazione' AND NumeroOrdine = ?",codiceOrdine).first(Long.class);
            if(controllo == 0){
                System.out.println("Ordine non trovato o già pagato");
            }
            else{
                    db.sql("call sp_cambiaStatoOrdineInSpedito(?)",codiceOrdine).execute();
                    ciclo1 = false;
            }
        }
        while(ciclo2 == true) {
            System.out.println("Vuoi tornare al menù principale?");
            System.out.println("(si)");
            System.out.println("(no)");
            System.out.println("Digita la tua scelta:");
            String scelta = sc.nextLine();
            if (scelta.equals("si")) {
                opzioniScelte(db, parametriUtente);
                ciclo2 = false;
            }else if (scelta.equals("no")) {cambiaStatoOrdineInSpedito(db, parametriUtente);}
            else{System.out.println("Opzione non esistente!");}
        }
    }

    private static void cambiaStatoOrdineInConsegnato(Database db, infoCredenziali parametriUtente){
        Scanner sc = new Scanner(System.in);
        String codiceString = "";
        long controllo = 0;
        int codiceOrdine = 0;
        boolean ciclo1 = true;
        boolean ciclo2 = true;

        while(ciclo1 == true){
            System.out.println("Di che ordine vuoi cambiare lo stato? Se non ricordi i parametri e vuoi tornare indietro digita '0':");
            codiceString = sc.nextLine();
            if(codiceString.equals("0")) {opzioniScelte(db, parametriUtente);}
            codiceOrdine = Integer.parseInt(codiceString);
            controllo = db.sql("select count(*) from ordine where Stato = 'Spedito' AND NumeroOrdine = ?",codiceOrdine).first(Long.class);
            if(controllo == 0){
                System.out.println("Ordine non trovato o già consegnato");
            }
            else{
                db.sql("call sp_cambiaStatoOrdineInConsegnato(?)",codiceOrdine).execute();
                ciclo1 = false;
            }
        }
        while(ciclo2 == true) {
            System.out.println("Vuoi tornare al menù principale?");
            System.out.println("(si)");
            System.out.println("(no)");
            System.out.println("Digita la tua scelta:");
            String scelta = sc.nextLine();
            if (scelta.equals("si")) {
                opzioniScelte(db, parametriUtente);
                ciclo2 = false;
            }else if (scelta.equals("no")) {cambiaStatoOrdineInConsegnato(db, parametriUtente);}
            else{System.out.println("Opzione non esistente!");}
        }
    }

    private static void creaMailingList(Database db, infoCredenziali parametriUtente){
        Scanner sc = new Scanner(System.in);
        String codiceString = "";
        String nomeMailingList = "";
        long controllo = 0;
        boolean ciclo1 = true;
        boolean ciclo2 = true;

        while(ciclo1 == true){
            System.out.println("Come si chiamerà la nuova mailinglist? Se vuoi tornare indietro digita '0':");
            codiceString = sc.nextLine();
            nomeMailingList = codiceString;
            if(codiceString.equals("0")) {opzioniScelte(db, parametriUtente);}
            controllo = db.sql("select count(*) from mailinglist where Nome = ?",codiceString).first(Long.class);
            if(controllo != 0){
                System.out.println("Questa mailinglist esiste già");
            }
            else{
                System.out.println("Scrivi un commento di meno di 100 caratteri o se vuoi tronare indietro digita '0':");
                codiceString = sc.nextLine();
                if(codiceString.equals("0")) {opzioniScelte(db, parametriUtente);}
                else {
                    db.sql("call sp_creaMailingList(?,?)",nomeMailingList,codiceString).execute();
                    ciclo1 = false;
                }
            }
        }
        while(ciclo2 == true) {
            System.out.println("Vuoi tornare al menù principale?");
            System.out.println("(si)");
            System.out.println("(no)");
            System.out.println("Digita la tua scelta:");
            String scelta = sc.nextLine();
            if (scelta.equals("si")) {
                opzioniScelte(db, parametriUtente);
                ciclo2 = false;
            }else if (scelta.equals("no")) {creaMailingList(db, parametriUtente);}
            else{System.out.println("Opzione non esistente!");}
        }
    }

    private static void eliminaMailingList(Database db, infoCredenziali parametriUtente){
        Scanner sc = new Scanner(System.in);
        String codiceString = "";
        long controllo = 0;
        boolean ciclo1 = true;
        boolean ciclo2 = true;

        List<String> mailinglist = db.sql("select Nome from mailinglist").results(String.class);
        AsciiTable at = new AsciiTable();
        at.addRule();
        at.addRow("Nome mailinglist");
        at.addRule();
        for (String indicatore:mailinglist){
            at.addRow(indicatore);
        }
        at.addRule();
        System.out.println(at.render());

        while(ciclo1 == true){
            System.out.println("Che mailinglist vuoi eliminare? Se non ricordi i parametri e vuoi tornare indietro digita '0':");
            codiceString = sc.nextLine();
            if(codiceString.equals("0")) {opzioniScelte(db, parametriUtente);}
            controllo = db.sql("select count(*) from mailinglist where Nome = ?",codiceString).first(Long.class);
            if(controllo == 0){
                System.out.println("Mailinglist non trovata");
            }
            else{
                db.sql("call sp_eliminaMailingList(?)",codiceString).execute();
                ciclo1 = false;
            }
        }
        while(ciclo2 == true) {
            System.out.println("Vuoi tornare al menù principale?");
            System.out.println("(si)");
            System.out.println("(no)");
            System.out.println("Digita la tua scelta:");
            String scelta = sc.nextLine();
            if (scelta.equals("si")) {
                opzioniScelte(db, parametriUtente);
                ciclo2 = false;
            }else if (scelta.equals("no")) {eliminaMailingList(db, parametriUtente);}
            else{System.out.println("Opzione non esistente!");}
        }
    }
}


