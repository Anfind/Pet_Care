package com.petcare.cli;

/**
 * Interface for CLI menus.
 */
public interface Menu {
    
    /**
     * Display the menu and handle user input.
     * 
     * @return true if the parent menu should continue running, false otherwise
     */
    boolean display();
    
    /**
     * Get the title of this menu.
     * 
     * @return The menu title
     */
    String getTitle();
}