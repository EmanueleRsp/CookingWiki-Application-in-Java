package it.emacompany.cookingwiki;

import java.io.Serializable;

public class InfoUtente implements Serializable{
    
    public final String nome;
    public final String psw;
    public final String domanda;
    public final String risposta;

    public String getNome() {
        return nome;
    }

    public String getPsw() {
        return psw;
    }

    public String getRisposta() {
        return risposta;
    }

    public String getDomanda() {
        return domanda;
    }

    public InfoUtente(String nome, String psw, String domanda, String risposta) {
        this.nome = nome;
        this.psw = psw;
        this.risposta = risposta;
        this.domanda = domanda;
    }
        
}
