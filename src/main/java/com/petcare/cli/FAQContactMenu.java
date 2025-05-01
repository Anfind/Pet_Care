package com.petcare.cli;

import com.petcare.util.ConsoleUtil;

/**
 * Menu for FAQs and contact form.
 */
public class FAQContactMenu implements Menu {
    
    private final AppSession session;
    
    public FAQContactMenu() {
        this.session = AppSession.getInstance();
    }
    
    @Override
    public boolean display() {
        String[] options = {
            "View FAQs",
            "Search FAQs",
            "Contact Us"
        };
        
        int choice = ConsoleUtil.displayMenu(getTitle(), options);
        
        if (choice == 0) {
            return true; // Return to parent menu
        }
        
        switch (choice) {
            case 1:
            case 2:
                // FAQs would use FAQDAO implementation
                ConsoleUtil.pressEnterToContinue("FAQ functionality is not yet implemented.");
                break;
            case 3:
                contactUs();
                break;
        }
        
        return true;
    }
    
    private void contactUs() {
        ConsoleUtil.displayTitle("Contact Us");
        
        String subject = ConsoleUtil.readString("Subject");
        String message = ConsoleUtil.readString("Message");
        
        // Would use ContactMessageService to save the message
        ConsoleUtil.pressEnterToContinue("Thank you for your message. We will respond as soon as possible.");
    }
    
    @Override
    public String getTitle() {
        return "FAQ & CONTACT";
    }
}