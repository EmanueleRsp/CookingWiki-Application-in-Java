-- ==================================================================== --
--    SCRIPT PER LA CREAZIONE DEL DATABASE 'emanuele_respino_616704'  	--
-- ==================================================================== --


SET FOREIGN_KEY_CHECKS = 0;
DROP DATABASE IF EXISTS emanuele_respino_616704;
CREATE DATABASE emanuele_respino_616704; 
USE emanuele_respino_616704;


-- ------------------------------ --
--  Table structure for `utente`  --
-- ------------------------------ --
CREATE TABLE utente (
	user VARCHAR(45) NOT NULL,
    password VARCHAR(100) NOT NULL,
    domanda VARCHAR(45) NOT NULL,
    risposta VARCHAR(45) NOT NULL,
    
    PRIMARY KEY(user)
)ENGINE = InnoDB DEFAULT CHARSET = latin1;


-- ------------------------------- --
--  Table structure for `ricetta`  --
-- ------------------------------- --
CREATE TABLE ricetta (
    nome VARCHAR(45) NOT NULL,
    autore VARCHAR(45) NOT NULL,
    data_creazione DATETIME NOT NULL,
    difficolta VARCHAR(45) NOT NULL,
    preparazione VARCHAR(10) NOT NULL,
    cottura VARCHAR(10) NOT NULL,
    dosi VARCHAR(20) NOT NULL,
    costo VARCHAR(15) NOT NULL,
    presentazione TEXT NOT NULL,
    conservazione TEXT NOT NULL,
    suggerimenti TEXT NOT NULL,
    
    PRIMARY KEY(nome, autore),
    FOREIGN KEY(autore) REFERENCES utente(user) ON DELETE CASCADE
    
)ENGINE = InnoDB DEFAULT CHARSET = latin1;


-- ---------------------------------- --
--  Table structure for `ingrediente` --
-- ---------------------------------- --
CREATE TABLE ingrediente (
	ingrediente VARCHAR(45) NOT NULL,
    ricetta VARCHAR(45) NOT NULL,
    autorericetta VARCHAR(45) NOT NULL,
    quantita VARCHAR(45) NOT NULL,
    
    PRIMARY KEY(ingrediente, ricetta, autorericetta),
    FOREIGN KEY(ricetta, autorericetta) REFERENCES ricetta(nome, autore) ON DELETE CASCADE
    
)ENGINE = InnoDB DEFAULT CHARSET = latin1;


-- ---------------------------- --
--  Table structure for `lista` --
-- ---------------------------- --
/*CREATE TABLE lista (
	proprietariolista VARCHAR(45) NOT NULL,
	autorericetta VARCHAR(45) NOT NULL,
    nomericetta VARCHAR(45) NOT NULL,
    ingrediente VARCHAR(45) NOT NULL,
    
    PRIMARY KEY(proprietariolista, autorericetta, nomericetta, ingrediente),
    FOREIGN KEY(proprietariolista) REFERENCES utente(user),
    FOREIGN KEY(ingrediente, nomericetta, autorericetta) REFERENCES ingrediente(ingrediente, ricetta, autorericetta)
)ENGINE = InnoDB DEFAULT CHARSET = latin1;*/


-- ---------------------------------- --
--  Table structure for `ricettario` --
-- ---------------------------------- --
CREATE TABLE ricettario (
	utente VARCHAR(45) NOT NULL,
    ricetta VARCHAR(45) NOT NULL,
    autorericetta VARCHAR(45) NOT NULL,
    
    PRIMARY KEY(utente, ricetta, autorericetta),
    FOREIGN KEY(utente) REFERENCES utente(user) ON DELETE CASCADE,
    FOREIGN KEY(ricetta, autorericetta) REFERENCES ricetta(nome, autore) ON DELETE CASCADE
    
)ENGINE = InnoDB DEFAULT CHARSET = latin1;


-- -------------------------- --
--  Table structure for `tag` --
-- -------------------------- --
CREATE TABLE tag (
    ricetta VARCHAR(45) NOT NULL,
    autorericetta VARCHAR(45) NOT NULL,
    tag VARCHAR(45) NOT NULL,
    
    PRIMARY KEY(ricetta, autorericetta, tag),
    FOREIGN KEY(ricetta, autorericetta) REFERENCES ricetta(nome, autore) ON DELETE CASCADE
    
)ENGINE = InnoDB DEFAULT CHARSET = latin1;


-- -------------------------------- --
--  Table structure for `passaggio` --
-- -------------------------------- --
CREATE TABLE passaggio (
    ricetta VARCHAR(45) NOT NULL,
    autorericetta VARCHAR(45) NOT NULL,
    numero INTEGER NOT NULL,
    testo TEXT NOT NULL,
    
    PRIMARY KEY(ricetta, autorericetta, numero),
    FOREIGN KEY(ricetta, autorericetta) REFERENCES ricetta(nome, autore) ON DELETE CASCADE
    
)ENGINE = InnoDB DEFAULT CHARSET = latin1;