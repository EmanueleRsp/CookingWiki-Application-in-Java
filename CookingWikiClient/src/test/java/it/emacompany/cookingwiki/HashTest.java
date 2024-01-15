/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package it.emacompany.cookingwiki;

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
public class HashTest {
    
    public HashTest() {
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
     * Test of getHashedPsw method, of class Hash.
     */
    @Test
    public void testGetHashedPsw() throws Exception {
        System.out.println("getHashedPsw");
        String psw = "password";
        String expResult = "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8";
        String result = Hash.getHashedPsw(psw);
        assertEquals(result,expResult, "The hashed psw for \"password\" should be \"5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8\".");
        assertTrue(result.matches("[(\\d)a-f]*"), "The hashed psw should be made up of only exadecimal digits (0-9a-f).");
    }

        
}
