package it.emacompany.CookingWikiServer;

import java.io.Serializable;

public class PrevRec implements Serializable{
    
    public String nome;
    public String autore;
    public String presentazione;
    public String difficolta;
    public String preparazione;
    public String cottura;
    public String dosi;
    public String costo;

    public PrevRec(String nome, String autore, String presentazione, String difficolta, String preparazione, String cottura, String dosi, String costo) {
        this.nome = nome;
        this.autore = autore;
        this.presentazione = presentazione;
        this.difficolta = difficolta;
        this.preparazione = preparazione;
        this.cottura = cottura;
        this.dosi = dosi;
        this.costo = costo;
    }

    public PrevRec(String nome, String autore, String presentazione) {
        this.nome = nome;
        this.autore = autore;
        this.presentazione = presentazione;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getAutore() {
        return autore;
    }

    public void setAutore(String autore) {
        this.autore = autore;
    }

    public String getPresentazione() {
        return presentazione;
    }

    public void setPresentazione(String presentazione) {
        this.presentazione = presentazione;
    }

    public String getDifficolta() {
        return difficolta;
    }

    public void setDifficolta(String difficolta) {
        this.difficolta = difficolta;
    }

    public String getPreparazione() {
        return preparazione;
    }

    public void setPreparazione(String preparazione) {
        this.preparazione = preparazione;
    }

    public String getCottura() {
        return cottura;
    }

    public void setCottura(String cottura) {
        this.cottura = cottura;
    }

    public String getDosi() {
        return dosi;
    }

    public void setDosi(String dosi) {
        this.dosi = dosi;
    }

    public String getCosto() {
        return costo;
    }

    public void setCosto(String costo) {
        this.costo = costo;
    }
    
}
