/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.emacompany.CookingWikiServer;

import java.io.Serializable;

/**
 *
 * @author Utente
 */
public class Ingrediente implements Serializable{
    
    public String nome;
    public String quantita;

    
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getQuantita() {
        return quantita;
    }

    public void setQuantita(String quantita) {
        this.quantita = quantita;
    }

    public Ingrediente(String nome, String quantita) {
        this.nome = nome;
        this.quantita = quantita;
    }

    @Override
    public String toString() {
        return "Ingrediente{" + "nome=" + nome + ", quantita=" + quantita + '}';
    }
    
    
}
