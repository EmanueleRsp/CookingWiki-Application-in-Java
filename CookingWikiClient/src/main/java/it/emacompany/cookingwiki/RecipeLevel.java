/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.emacompany.cookingwiki;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Utente
 */
public class RecipeLevel {
    
    private static final ObservableList<String> options = 
    FXCollections.observableArrayList(
        "Molto facile",
        "Facile",
        "Medio",
        "Difficile",
        "Molto difficile"
    );

    public static ObservableList<String> getOptions() {
        return options;
    }
    
}
