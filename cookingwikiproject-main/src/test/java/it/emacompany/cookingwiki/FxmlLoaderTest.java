/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package it.emacompany.cookingwiki;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Parent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Utente
 */
public class FxmlLoaderTest {
    
    public FxmlLoaderTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of getPane method, of class FxmlLoader.
     */
    @Test
    public void testGetPane() {
        System.out.println("------Test------\n FUN:\tgetPane(String filename)\n CLASS:\tFxmlLoader\n----------------");
        FxmlLoader instance = new FxmlLoader();
        
        File folder = new File(CookingWiki.class.getResource("/it/emacompany/cookingwiki").getPath());
        File[] listOfFiles = new File(CookingWiki.class.getResource("/it/emacompany/cookingwiki").getPath()).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".fxml");
            }                 
        });

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile())
                System.out.println("File " + listOfFiles[i].getName());
            
            Parent result = instance.getPane(listOfFiles[i].getName());
            if(result == null)
                fail("Error in FxmlLoader function: cannot load all files.");
                
        }
    }
    
}
