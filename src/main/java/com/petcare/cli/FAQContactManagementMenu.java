package com.petcare.cli;

import com.petcare.util.ConsoleUtil;

/**
 * Menu for FAQ and contact message management.
 */
public class FAQContactManagementMenu implements Menu {
    
    private final AppSession session;
    
    public FAQContactManagementMenu() {
        this.session = AppSession.getInstance();
    }
    
    @Override
    public boolean display() {
        if (!session.isAdmin()) {
            ConsoleUtil.pressEnterToContinue("Access denied. Admin privileges required.");
            return true;
        }
        
        String[] options = {
            "Manage FAQs",
            "View Contact Messages"
        };
        
        int choice = ConsoleUtil.displayMenu(getTitle(), options);
        
        if (choice == 0) {
            return true; // Return to parent menu
        }
        
        switch (choice) {
            case 1:
                manageFAQs();
                break;
            case 2:
                viewContactMessages();
                break;
        }
        
        return true;
    }
    
    private void manageFAQs() {
        String[] options = {
            "View All FAQs",
            "Add New FAQ",
            "Edit FAQ",
            "Delete FAQ"
        };
        
        int choice = ConsoleUtil.displayMenu("FAQ Management", options);
        
        if (choice != 0) {
            // All options would use FAQDAO implementation
            ConsoleUtil.pressEnterToContinue("FAQ management functionality is not yet implemented.");
        }
    }
    
    private void viewContactMessages() {
        String[] options = {
            "View All Messages",
            "View Unread Messages",
            "Mark Message as Read",
            "Delete Message"
        };
        
        int choice = ConsoleUtil.displayMenu("Contact Messages", options);
        
        if (choice != 0) {
            // All options would use ContactMessageDAO implementation
            ConsoleUtil.pressEnterToContinue("Contact message management functionality is not yet implemented.");
        }
    }
    
    @Override
    public String getTitle() {
        return "FAQ & CONTACT MANAGEMENT";
    }
}