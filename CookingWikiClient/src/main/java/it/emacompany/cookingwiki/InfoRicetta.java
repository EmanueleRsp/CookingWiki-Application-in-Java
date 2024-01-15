package it.emacompany.cookingwiki;

import java.io.Serializable;
import java.util.List;

public class InfoRicetta implements Serializable{

    public String autore;

    public String titolo;
    public String difficolta;
    public String preparazione;
    public String cottura;
    public String numeroDosi;
    public String costo;
    public String presentazione;
    
    public List<Ingrediente> ingredienti;
    public List<String> passi;
    
    public String conservazione;
    public String suggerimenti;
    
    public List<String> tags;

    public InfoRicetta(String autore, String titolo, String difficolta, String preparazione, String cottura, String numeroDosi, String costo, String presentazione, List<Ingrediente> ingredienti, List<String> passi, String conservazione, String suggerimenti, List<String> tags) {
        this.autore = autore;
        this.titolo = titolo;
        this.difficolta = difficolta;
        this.preparazione = preparazione;
        this.cottura = cottura;
        this.numeroDosi = numeroDosi;
        this.costo = costo;
        this.presentazione = presentazione;
        this.ingredienti = ingredienti;
        this.passi = passi;
        this.conservazione = conservazione;
        this.suggerimenti = suggerimenti;
        this.tags = tags;
    } 
    
    public InfoRicetta(String autore, String titolo) {
        this.autore = autore;
        this.titolo = titolo;
    } 
    
}
