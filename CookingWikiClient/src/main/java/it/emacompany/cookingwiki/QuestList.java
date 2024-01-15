package it.emacompany.cookingwiki;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class QuestList {
    
    private static final ObservableList<String> options = 
    FXCollections.observableArrayList(
        "Il nome del tuo primo ragazzo/a?",
        "La tua squadra del cuore?",
        "Il nome del tuo professore di matematica alle superiori?",
        "Il nome del tuo primo animale?",
        "Il cognome da nubile di tua madre?",
        "Il nome della scuola elementare?",
        "Il tuo primo lavoro svolto?",
        "Il tuo libro per bambini preferito?",
        "Il tuo colore preferito?",
        "Il tuo migliore amico delle medie?",
        "La tua città preferita?"
    );

    public static ObservableList<String> getOptions() {
        return options;
    }
    
}
