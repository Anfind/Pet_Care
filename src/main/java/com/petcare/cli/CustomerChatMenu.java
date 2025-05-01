package com.petcare.cli;

import com.petcare.util.ConsoleUtil;

/**
 * Menu for customer chat with admins.
 */
public class CustomerChatMenu implements Menu {
    
    private final AppSession session;
    
    public CustomerChatMenu() {
        this.session = AppSession.getInstance();
    }
    
    @Override
    public boolean display() {
        if (!session.isLoggedIn()) {
            ConsoleUtil.pressEnterToContinue("Please log in to access this feature.");
            return true;
        }
        
        String[] options = {
            "View Message History",
            "Send New Message",
            "Check for Replies"
        };
        
        int choice = ConsoleUtil.displayMenu(getTitle(), options);
        
        if (choice == 0) {
            return true; // Return to parent menu
        }
        
        switch (choice) {
            case 1:
            case 2:
            case 3:
                // All cases would use MessageService and MessageDAO implementations
                ConsoleUtil.pressEnterToContinue("Chat functionality is not yet implemented.");
                break;
        }
        
        return true;
    }
    
    @Override
    public String getTitle() {
        return "CHAT WITH ADMIN";
    }
}