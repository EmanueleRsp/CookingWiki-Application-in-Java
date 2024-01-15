package it.emacompany.cookingwiki;

import java.io.Serializable;

public class SearchByNameData implements Serializable{
    
    public int dim;
    public int lastCheck;
    public String lastName;
    public String lastAutore;
    public String name;

    public SearchByNameData(int dim, int lastCheck, String lastName, String lastAutore, String name) {
        this.dim = dim;
        this.lastCheck = lastCheck;
        this.lastName = lastName;
        this.lastAutore = lastAutore;
        this.name = name;
    }
    
}
