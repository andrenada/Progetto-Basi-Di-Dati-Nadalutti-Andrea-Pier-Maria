CREATE DATABASE IF NOT EXISTS LegoService;
USE LegoService;

SET foreign_key_checks = 0;

DROP TABLE IF EXISTS MailingList;
DROP TABLE IF EXISTS Partecipazione;
DROP TABLE IF EXISTS Utente;
DROP TABLE IF EXISTS Possiede;
DROP TABLE IF EXISTS SetLego;
DROP TABLE IF EXISTS Composizione;
DROP TABLE IF EXISTS Pezzo;
DROP TABLE IF EXISTS Ordine;
DROP TABLE IF EXISTS Pagamento;
DROP TABLE IF EXISTS DettagliOrdine;




CREATE TABLE MailingList (
    Nome VARCHAR(30) PRIMARY KEY,
    Commento VARCHAR(100) NOT NULL
);

CREATE TABLE Partecipazione (
    Username VARCHAR(20),
    Nome VARCHAR(30),
    PRIMARY KEY(Username, Nome),
    FOREIGN KEY(Username) REFERENCES Utente (Username),
    FOREIGN KEY(Nome) REFERENCES MailingList (Nome)
);

CREATE TABLE Utente (
    Username VARCHAR(20) PRIMARY KEY,
    Password VARCHAR(20) NOT NULL,
    Nome VARCHAR(30) NOT NULL,
    Cognome VARCHAR(20) NOT NULL,
    Email VARCHAR(50) NOT NULL,
    Permessi VARCHAR(7) CHECK( Permessi IN ('Cliente', 'Admin'))
);

CREATE TABLE Possiede (
    Username VARCHAR(20),
    CodiceSet INT(6),
    PRIMARY KEY(Username, CodiceSet),
    FOREIGN KEY(Username) REFERENCES Utente (Username),
    FOREIGN KEY(CodiceSet) REFERENCES SetLego (CodiceSet)
);

CREATE TABLE SetLego (
    CodiceSet INT(6) PRIMARY KEY,
    Anno INT(4) NOT NULL,
    Collezione VARCHAR(20) NOT NULL,
    Valore DOUBLE(6, 2) NOT NULL, CHECK(Valore >= 0)
);

CREATE TABLE Composizione (
    CodiceSet INT(6),
    CodicePezzo INT(8),
    Colore INT(3),
    Quantità INT(3) NOT NULL,
    PRIMARY KEY(CodiceSet, CodicePezzo, Colore),
    FOREIGN KEY(CodiceSet) REFERENCES SetLego (CodiceSet),
    FOREIGN KEY(CodicePezzo, Colore) REFERENCES Pezzo (CodicePezzo, Colore)
);

CREATE TABLE Pezzo (
    CodicePezzo INT(5),
    Colore INT(3),
    PRIMARY KEY(CodicePezzo, Colore)
);

CREATE TABLE Ordine (
    NumeroOrdine INT(8) PRIMARY KEY AUTO_INCREMENT,
    DataOrdine DATE NOT NULL,
    Stato VARCHAR(15) CHECK ( Stato IN ('In lavorazione', 'Spedito', 'Consegnato') ),
    DataSpedizione DATE,
    Username VARCHAR(20) NOT NULL,
    FOREIGN KEY(Username) REFERENCES Utente (Username)
);

CREATE TABLE Pagamento(
    NumeroPagamento INT(8) PRIMARY KEY AUTO_INCREMENT,
    DataPagamento DATE NOT NULL,
    Quantitativo DOUBLE (10,2) NOT NULL, CHECK(Quantitativo >= 0),
    NumeroOrdine INT(8) NOT NULL,
    FOREIGN KEY(NumeroOrdine) REFERENCES Ordine (NumeroOrdine)  ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE DettagliOrdine(
    NumeroOrdine INT(8) NOT NULL,
    CodiceSet INT(6) NOT NULL,
    Numero INT(3) NOT NULL,
    PRIMARY KEY(NumeroOrdine, CodiceSet),
    FOREIGN KEY(NumeroOrdine) REFERENCES Ordine (NumeroOrdine)  ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(CodiceSet) REFERENCES SetLego (CodiceSet)
);



-- Inserimento dati nella tabella Utente
INSERT INTO Utente (Username, Password, Nome, Cognome, Email, Permessi) VALUES
('Utente1', 'pass1', 'Mario', 'Rossi', 'mario.rossi@example.com', 'Cliente'),
('Utente2', 'pass2', 'Luigi', 'Verdi', 'luigi.verdi@example.com', 'Cliente'),
('admin1', 'adminpass', 'Admin', 'User', 'admin.user@example.com', 'Admin');

-- Inserimento dati nella tabella MailingList
INSERT INTO MailingList (Nome, Commento) VALUES
('News', 'Ultime novità sui set LEGO'),
('Offerte', 'Offerte e sconti sui set LEGO');

-- Inserimento dati nella tabella Partecipazione
INSERT INTO Partecipazione (Username, Nome) VALUES
('Utente1', 'News'),
('Utente1', 'Offerte'),
('Utente2', 'News');

-- Inserimento dati nella tabella SetLego
INSERT INTO SetLego (CodiceSet, Anno, Collezione, Valore) VALUES
(10500, 2010, 'City', 0),
(10179, 2007, 'Star Wars', 499.99),
(10256, 2017, 'Creator Expert', 279.99),
(21318, 2019, 'Ideas', 199.99),
(10599, 2009, 'City', 49.99),
(10368, 2020, 'Technic', 229.99);



-- Inserimento dati nella tabella Pezzo
INSERT INTO Pezzo (CodicePezzo, Colore) VALUES
(3001, 1), -- 1 potrebbe rappresentare il colore rosso
(3001, 2), -- 2 potrebbe rappresentare il colore blu
(3002, 1),
(3006, 3),
(3007, 9),
(3015, 1),
(3015, 2);

-- Inserimento dati nella tabella Composizione
INSERT INTO Composizione (CodiceSet, CodicePezzo, Colore, Quantità) VALUES
(10500, 3002, 1, 50),
(10500, 3001, 1, 220),
(10179, 3001, 2, 300),
(10256, 3001, 2, 500),
(21318, 3002, 1, 150),
(10599, 3007, 9, 60),
(10599, 3002, 1, 120),
(10599, 3015, 1, 50),
(10368, 3006, 3, 80),
(10368, 3001, 3, 100);

-- Inserimento dati nella tabella Possiede
INSERT INTO Possiede (Username, CodiceSet) VALUES
('Utente1', 10500),
('Utente1', 10179),
('Utente2', 10256),
('Utente2', 10368),
('Utente2', 21318);

-- Inserimento dati nella tabella Ordine
INSERT INTO Ordine (DataOrdine, Stato, DataSpedizione, Username) VALUES
('2023-06-15', 'Consegnato', '2023-06-20', 'Utente1');

INSERT INTO Ordine (DataOrdine, Stato, Username) VALUES
('2023-07-05', 'In lavorazione', 'Utente1');

-- Inserimento dati nella tabella DettagliOrdine
INSERT INTO DettagliOrdine (NumeroOrdine, CodiceSet, Numero) VALUES
(1, 10179, 1),
(2, 10256, 1);

-- Inserimento dati nella tabella Pagamento
INSERT INTO Pagamento (DataPagamento, Quantitativo, NumeroOrdine) VALUES
('2023-06-15', 499.99, 1);

-- Riattiva il controllo delle chiavi esterne
SET foreign_key_checks = 1;


/*------TRIGGERS------*/

/* TRIGGER: due utenti con username diversi non possono avere la stessa email */
DELIMITER $$
CREATE TRIGGER trg_2usernameDifferenteEmail
    BEFORE INSERT ON Utente
    FOR EACH ROW
    BEGIN
        IF EXISTS (SELECT*FROM Utente WHERE Email = NEW.Email) THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Questa mail è già usata da un altro utente';
        end if;
    END $$
DELIMITER ;


/* TRIGGER: un ordine futuro non può contenre un set lego con valore zero */
DELIMITER $$
CREATE TRIGGER trg_nuovoOrdineSetNonDisponibile
    BEFORE INSERT ON dettagliordine
    FOR EACH ROW
    BEGIN
        IF EXISTS (SELECT*FROM SetLego WHERE CodiceSet = NEW.CodiceSet AND Valore = 0) THEN
            SIGNAL SQLSTATE '45001' SET MESSAGE_TEXT = "Questo set Lego non è disponibile per l'acquisto";
        end if;
    END $$
DELIMITER ;


/* TRIGGER: non può cambiare lo stato di un ordine da 'in lavorazione' a 'spedito' se non vi è un numero pagamento associato */
DELIMITER $$
CREATE TRIGGER trg_cambioStatoOrdineSenzaVerificaPagamento
    BEFORE UPDATE ON ordine
    FOR EACH ROW
    BEGIN
        IF NOT EXISTS(SELECT DataPagamento FROM pagamento WHERE NumeroOrdine = OLD.NumeroOrdine) THEN
            SIGNAL SQLSTATE '45002' SET MESSAGE_TEXT = 'Questo ordine non è ancora stato pagato';
        end if;
    END $$
DELIMITER ;


/* TRIGGER: un ordine non può avere zero di un set */
DELIMITER $$
CREATE TRIGGER trg_nuovoOrdineQuantitàPerSetZero
    BEFORE INSERT ON dettagliordine
    FOR EACH ROW
    BEGIN
        IF NEW.Numero <= 0 THEN
            SIGNAL SQLSTATE '45003' SET MESSAGE_TEXT = 'Devi acquistare almeno un numero pari a 1 di questo set';
        end if;
    END $$
DELIMITER ;


/* TRIGGER: un set non può avere un numero pari a zero di componenti in componenti */
DELIMITER $$
CREATE TRIGGER trg_nuovoSetQuantitàPerComponenteZero
    BEFORE INSERT ON composizione
    FOR EACH ROW
    BEGIN
        IF NEW.Quantità = 0 THEN
            SIGNAL SQLSTATE '45004' SET MESSAGE_TEXT = 'Un set non può avere zero di un componente';
        end if;
    END $$
DELIMITER ;


/*------STORED PROCEDURE------*/

/* SP: cliente vuole vedere a che mailing list è iscritto */
DELIMITER $$
CREATE PROCEDURE sp_aCheMailingListSonoIscritto(IN inputUsername VARCHAR(20))
BEGIN
    SELECT Nome FROM partecipazione WHERE Username = inputUsername;
END $$
DELIMITER ;


/* SP: cliente vuole iscriversi a una mailing list */
DELIMITER $$
CREATE PROCEDURE sp_aggiungiInMailingList(IN inputUsername VARCHAR(20), IN inputNome VARCHAR(30))
BEGIN
    INSERT INTO partecipazione (Username, Nome) VALUE (inputUsername, inputNome);
END $$
DELIMITER ;


/* SP: cliente vuole disiscriversi a una mailing list */
DELIMITER $$
CREATE PROCEDURE sp_rimuoviDaMailingList(IN inputUsername VARCHAR(20), IN inputNome VARCHAR(30))
BEGIN
    DELETE FROM partecipazione WHERE Username = inputUsername AND Nome = inputNome;
END $$
DELIMITER ;


/* SP: cliente vuole vedere che set lego ha */
DELIMITER $$
CREATE PROCEDURE sp_cheSetLegoHo(IN inputUsername VARCHAR(20))
BEGIN
    SELECT CodiceSet FROM possiede WHERE Username = inputUsername;
END $$
DELIMITER ;


/* SP: cliente vuole aggiungere un set lego che possiede */
DELIMITER $$
CREATE PROCEDURE sp_aggiungiSetLegoMiei(IN inputUsername VARCHAR(20), IN inputCodiceSet INT(6))
BEGIN
    INSERT INTO possiede (Username, CodiceSet) VALUE (inputUsername, inputCodiceSet);
END $$
DELIMITER ;


/* SP: cliente vuole rimuovere un set lego tra quelli che ha */
DELIMITER $$
CREATE PROCEDURE sp_rimuoviSetLegoMiei(IN inputUsername VARCHAR(20), IN inputCodiceSet INT(6))
BEGIN
    DELETE FROM possiede WHERE Username = inputUsername AND CodiceSet = inputCodiceSet;
END $$
DELIMITER ;


/* SP: il cliente vuole vedere se può fare un determinato set da quelli che possiede */
DELIMITER $$
CREATE PROCEDURE sp_verificaPossoFareQuestoSet(IN inputUsername VARCHAR(20), IN inputCodiceSet INT(6))
BEGIN
    SELECT MIN(somma) FROM(
        SELECT SUM(Quantità) as somma FROM (
            SELECT CodicePezzo, Quantità FROM (
                (SELECT codiceSet FROM possiede
                INNER JOIN setLego USING(codiceSet) WHERE Username = inputUsername) AS setCheHa
            INNER JOIN composizione USING(codiceSet))
        UNION ALL
        SELECT CodicePezzo, Quantità*-1 FROM composizione WHERE CodiceSet = inputCodiceSet) AS confronto
    GROUP BY CodicePezzo) AS verifica;
END $$
DELIMITER ;


/* SP: cliente vuole creare un ordine */
DELIMITER $$
CREATE PROCEDURE sp_inserimentoOrdineNuovo(IN inputNumeroOrdine INT(8),IN inputUsername VARCHAR(20),IN inputCodiceSet INT(6), IN inputNumero INT(3))
BEGIN
    START transaction;
       SET @dataOrdine = CURDATE();

       INSERT INTO Ordine (NumeroOrdine, DataOrdine, Stato, Username) VALUES (inputNumeroOrdine,@dataOrdine,'In lavorazione',inputUsername);
       INSERT INTO dettagliordine (NumeroOrdine, CodiceSet, Numero) VALUES (inputNumeroOrdine,inputCodiceSet,inputNumero);
    commit;
END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_inserimentoOrdineEsistente(IN inputNumeroOrdine INT(8), IN inputCodiceSet INT(6), IN inputNumero INT(3))
BEGIN
    INSERT INTO dettagliordine (NumeroOrdine, CodiceSet, Numero) VALUES (inputNumeroOrdine,inputCodiceSet,inputNumero);
END $$
DELIMITER ;


/* SP: cliente vuole vedere che ordini ha fatto */
DELIMITER $$
CREATE PROCEDURE sp_vediMieiOrdini(IN inputUsername VARCHAR(20))
BEGIN
    SELECT ordine.NumeroOrdine, dettagliordine.CodiceSet, dettagliordine.Numero, ordine.DataOrdine, ordine.Stato, ordine.DataSpedizione FROM ordine
    INNER JOIN dettagliordine USING(NumeroOrdine)
    WHERE Username = inputUsername;
END $$
DELIMITER ;


/* SP: admin vuole poter aggiungere un set lego */
DELIMITER $$
CREATE PROCEDURE sp_aggiungiNuovoSetLego(IN inputCodiceSet INT(6), IN inputAnno INT(4), IN inputCollezione VARCHAR(20), IN inputValore DOUBLE, IN inputCodicePezzo INT(8), IN inputColore INT(3), IN inputQuantità INT(3))
BEGIN
    START transaction;
       INSERT INTO setlego (CodiceSet, Anno, Collezione, Valore) VALUES (inputCodiceSet,inputAnno,inputCollezione,inputValore);
       INSERT INTO composizione (CodiceSet, CodicePezzo, Colore, Quantità) VALUES (inputCodiceSet,inputCodicePezzo,inputColore,inputQuantità);
    commit;
END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_aggiungiComponentiSetLegoEsistente(IN inputCodiceSet INT(6), IN inputCodicePezzo INT(8), IN inputColore INT(3), IN inputQuantità INT(3))
BEGIN
    INSERT INTO composizione (CodiceSet, CodicePezzo,Colore, Quantità) VALUE (inputCodiceSet,inputCodicePezzo,inputColore,inputQuantità);
END $$
DELIMITER ;


/* SP: admin vuole poter aggiungere dei nuovi pezzi */
DELIMITER $$
CREATE PROCEDURE sp_aggiungiNuovoPezzo(IN inputCodicePezzo INT(5), IN inputColorePezzo INT(3))
BEGIN
    INSERT INTO Pezzo (CodicePezzo, Colore) VALUES (inputCodicePezzo,inputColorePezzo);
END $$
DELIMITER ;


/* SP: admin vuole cambiare il valore di un SetLego */
DELIMITER $$
CREATE PROCEDURE sp_valore0SetLego(IN inputCodiceSet INT(6), IN inputValore DOUBLE)
BEGIN
    UPDATE setlego
        SET Valore = inputValore WHERE CodiceSet = inputCodiceSet;
END $$
DELIMITER ;

/* SP: admin vuole poter vedere quali ordini non risultano pagati (pertatno avranno come stato 'In lavorazione') */
DELIMITER $$
CREATE PROCEDURE sp_ordineNonPagato()
BEGIN
    SELECT*FROM ordine WHERE STATO = 'In lavorazione';
END $$
DELIMITER ;


/* SP: admin vuole poter aggiungere un pagamento */
DELIMITER $$
CREATE PROCEDURE sp_aggiungiPagamento(IN inputQuantitativo DOUBLE, IN inputNumeroOrdine INT(8))
BEGIN
    INSERT INTO Pagamento (DataPagamento, Quantitativo, NumeroOrdine) VALUES (CURDATE(),inputQuantitativo,inputNumeroOrdine);
END $$
DELIMITER ;


/* SP: admin vuole poter cambiare lo stato di un ordine da In lavorazione a Spedito*/
DELIMITER $$
CREATE PROCEDURE sp_cambiaStatoOrdineInSpedito(IN inputNumeroOrdine INT(8))
BEGIN
    UPDATE ordine
        SET Stato = 'Spedito', DataSpedizione = CURDATE()
        WHERE NumeroOrdine = inputNumeroOrdine;
END $$
DELIMITER ;


/* SP: admin vuole poter cambiare lo stato di un ordine da In lavorazione a Spedito*/
DELIMITER $$
CREATE PROCEDURE sp_cambiaStatoOrdineInConsegnato(IN inputNumeroOrdine INT(8))
BEGIN
    UPDATE ordine
        SET Stato = 'Consegnato', DataSpedizione = CURDATE()
        WHERE NumeroOrdine = inputNumeroOrdine;
END $$
DELIMITER ;


/* SP: admin vuole poter creare una nuova mailinglist */
DELIMITER $$
CREATE PROCEDURE sp_creaMailingList(IN inputNome VARCHAR(30), IN inputCommento VARCHAR(100))
BEGIN
    INSERT INTO mailinglist (Nome, Commento) VALUES (inputNome, inputCommento);
END $$
DELIMITER ;


/* SP: admin vuole eliminare una mailinglist e chi vi fa parte */
DELIMITER $$
CREATE PROCEDURE sp_eliminaMailingList(IN inputNome VARCHAR(30))
BEGIN
    START transaction;
        DELETE FROM partecipazione WHERE Nome = inputNome;
        DELETE FROM mailinglist WHERE Nome = inputNome;
    commit;
END $$
DELIMITER ;

