package com.petcare.cli;

import com.petcare.model.Product;
import com.petcare.model.Service;
import com.petcare.service.ProductService;
import com.petcare.service.ServiceService;
import com.petcare.service.impl.ProductServiceImpl;
import com.petcare.service.impl.ServiceServiceImpl;
import com.petcare.util.ConsoleUtil;

import java.util.List;
import java.util.Optional;

/**
 * Menu for browsing products and services.
 */
public class BrowseProductsServicesMenu implements Menu {
    
    private final AppSession session;
    private final ProductService productService;
    private final ServiceService serviceService;
    
    public BrowseProductsServicesMenu() {
        this.session = AppSession.getInstance();
        this.productService = new ProductServiceImpl();
        this.serviceService = new ServiceServiceImpl();
    }
    
    @Override
    public boolean display() {
        if (!session.isLoggedIn()) {
            ConsoleUtil.pressEnterToContinue("Please log in to access this feature.");
            return true;
        }
        
        String[] options = {
            "Browse Products",
            "Browse Services",
            "Search Products/Services"
        };
        
        int choice = ConsoleUtil.displayMenu(getTitle(), options);
        
        if (choice == 0) {
            return true; // Return to parent menu
        }
        
        switch (choice) {
            case 1:
                browseProducts();
                break;
            case 2:
                browseServices();
                break;
            case 3:
                searchProductsAndServices();
                break;
        }
        
        return true;
    }
    
    private void browseProducts() {
        boolean exit = false;
        
        while (!exit) {
            ConsoleUtil.displayTitle("Browse Products");
            
            String[] options = {
                "View All Products",
                "View Products by Type",
                "View Product Details"
            };
            
            int choice = ConsoleUtil.displayMenu("Product Browsing Options", options);
            
            if (choice == 0) {
                exit = true;
                continue;
            }
            
            try {
                switch (choice) {
                    case 1:
                        viewAllProducts();
                        break;
                    case 2:
                        viewProductsByType();
                        break;
                    case 3:
                        viewProductDetails();
                        break;
                }
            } catch (Exception e) {
                ConsoleUtil.pressEnterToContinue("Error: " + e.getMessage());
            }
        }
    }
    
    private void viewAllProducts() {
        ConsoleUtil.displayTitle("All Products");
        
        List<Product> products = productService.getAllProducts();
        
        if (products.isEmpty()) {
            ConsoleUtil.pressEnterToContinue("No products found.");
            return;
        }
        
        displayProductsList(products);
        ConsoleUtil.pressEnterToContinue("");
    }
    
    private void viewProductsByType() {
        System.out.println("Product type options: FOOD, TOY, OTHER");
        String type = ConsoleUtil.readString("Enter product type: ").toUpperCase();
        
        try {
            List<Product> products = productService.getProductsByType(type);
            
            ConsoleUtil.displayTitle("Products of Type: " + type);
            
            if (products.isEmpty()) {
                ConsoleUtil.pressEnterToContinue("No products found of type " + type + ".");
                return;
            }
            
            displayProductsList(products);
            ConsoleUtil.pressEnterToContinue("");
        } catch (Exception e) {
            ConsoleUtil.pressEnterToContinue("Error retrieving products by type: " + e.getMessage());
        }
    }
    
    private void viewProductDetails() {
        Long productId = ConsoleUtil.readLong("Enter product ID (0 to cancel): ");
        if (productId == 0) {
            return;
        }
        
        try {
            Optional<Product> productOpt = productService.getProductById(productId);
            
            if (!productOpt.isPresent()) {
                ConsoleUtil.pressEnterToContinue("Product not found.");
                return;
            }
            
            Product product = productOpt.get();
            ConsoleUtil.displayTitle("Product Details");
            System.out.println("ID: " + product.getId());
            System.out.println("Name: " + product.getName());
            System.out.println("Description: " + product.getDescription());
            System.out.println("Price: $" + product.getPrice());
            System.out.println("Type: " + product.getType());
            
            ConsoleUtil.pressEnterToContinue("");
        } catch (Exception e) {
            ConsoleUtil.pressEnterToContinue("Error: " + e.getMessage());
        }
    }
    
    private void displayProductsList(List<Product> products) {
        System.out.printf("%-5s %-30s %-10s %-10s%n", "ID", "Name", "Price", "Type");
        System.out.println("-------------------------------------------------------");
        
        for (Product product : products) {
            System.out.printf("%-5d %-30s $%-9.2f %-10s%n",
                    product.getId(),
                    (product.getName().length() > 28 ? product.getName().substring(0, 25) + "..." : product.getName()),
                    product.getPrice(),
                    product.getType());
        }
        
        System.out.println("-------------------------------------------------------");
        System.out.println("Total: " + products.size() + " product(s)");
    }
    
    private void browseServices() {
        boolean exit = false;
        
        while (!exit) {
            ConsoleUtil.displayTitle("Browse Services");
            
            String[] options = {
                "View All Services",
                "View Services by Maximum Duration",
                "View Services Sorted by Price",
                "View Service Details"
            };
            
            int choice = ConsoleUtil.displayMenu("Service Browsing Options", options);
            
            if (choice == 0) {
                exit = true;
                continue;
            }
            
            try {
                switch (choice) {
                    case 1:
                        viewAllServices();
                        break;
                    case 2:
                        viewServicesByMaxDuration();
                        break;
                    case 3:
                        viewServicesSortedByPrice();
                        break;
                    case 4:
                        viewServiceDetails();
                        break;
                }
            } catch (Exception e) {
                ConsoleUtil.pressEnterToContinue("Error: " + e.getMessage());
            }
        }
    }
    
    private void viewAllServices() {
        ConsoleUtil.displayTitle("All Services");
        
        List<Service> services = serviceService.getAllServices();
        
        if (services.isEmpty()) {
            ConsoleUtil.pressEnterToContinue("No services found.");
            return;
        }
        
        displayServicesList(services);
        ConsoleUtil.pressEnterToContinue("");
    }
    
    private void viewServicesByMaxDuration() {
        int maxDuration = ConsoleUtil.readInt("Enter maximum duration in minutes: ");
        
        try {
            List<Service> services = serviceService.getServicesByMaxDuration(maxDuration);
            
            ConsoleUtil.displayTitle("Services with Duration ≤ " + maxDuration + " min");
            
            if (services.isEmpty()) {
                ConsoleUtil.pressEnterToContinue("No services found with duration less than or equal to " + maxDuration + " minutes.");
                return;
            }
            
            displayServicesList(services);
            ConsoleUtil.pressEnterToContinue("");
        } catch (Exception e) {
            ConsoleUtil.pressEnterToContinue("Error retrieving services by duration: " + e.getMessage());
        }
    }
    
    private void viewServicesSortedByPrice() {
        String order = ConsoleUtil.readString("Sort by price (A for ascending, D for descending): ").toUpperCase();
        boolean ascending = !order.startsWith("D");
        
        try {
            List<Service> services = serviceService.getServicesSortedByPrice(ascending);
            
            ConsoleUtil.displayTitle("Services Sorted by Price (" + (ascending ? "Low to High" : "High to Low") + ")");
            
            if (services.isEmpty()) {
                ConsoleUtil.pressEnterToContinue("No services found.");
                return;
            }
            
            displayServicesList(services);
            ConsoleUtil.pressEnterToContinue("");
        } catch (Exception e) {
            ConsoleUtil.pressEnterToContinue("Error sorting services by price: " + e.getMessage());
        }
    }
    
    private void viewServiceDetails() {
        Long serviceId = ConsoleUtil.readLong("Enter service ID (0 to cancel): ");
        if (serviceId == 0) {
            return;
        }
        
        try {
            Optional<Service> serviceOpt = serviceService.getServiceById(serviceId);
            
            if (!serviceOpt.isPresent()) {
                ConsoleUtil.pressEnterToContinue("Service not found.");
                return;
            }
            
            Service service = serviceOpt.get();
            ConsoleUtil.displayTitle("Service Details");
            System.out.println("ID: " + service.getId());
            System.out.println("Name: " + service.getName());
            System.out.println("Description: " + service.getDescription());
            System.out.println("Price: $" + service.getPrice());
            System.out.println("Duration: " + service.getDurationMinutes() + " minutes");
            
            ConsoleUtil.pressEnterToContinue("");
        } catch (Exception e) {
            ConsoleUtil.pressEnterToContinue("Error: " + e.getMessage());
        }
    }
    
    private void displayServicesList(List<Service> services) {
        System.out.printf("%-5s %-30s %-10s %-10s%n", "ID", "Name", "Price", "Duration");
        System.out.println("-------------------------------------------------------");
        
        for (Service service : services) {
            System.out.printf("%-5d %-30s $%-9.2f %-10d min%n",
                    service.getId(),
                    (service.getName().length() > 28 ? service.getName().substring(0, 25) + "..." : service.getName()),
                    service.getPrice(),
                    service.getDurationMinutes());
        }
        
        System.out.println("-------------------------------------------------------");
        System.out.println("Total: " + services.size() + " service(s)");
    }
    
    private void searchProductsAndServices() {
        String searchText = ConsoleUtil.readString("Enter search term: ");
        
        try {
            ConsoleUtil.displayTitle("Search Results for: " + searchText);
            
            // Search for products
            List<Product> products = productService.searchProducts(searchText);
            if (!products.isEmpty()) {
                System.out.println("\nMATCHING PRODUCTS:");
                displayProductsList(products);
            } else {
                System.out.println("\nNo matching products found.");
            }
            
            // Search for services
            List<Service> services = serviceService.searchServices(searchText);
            if (!services.isEmpty()) {
                System.out.println("\nMATCHING SERVICES:");
                displayServicesList(services);
            } else {
                System.out.println("\nNo matching services found.");
            }
            
            if (products.isEmpty() && services.isEmpty()) {
                ConsoleUtil.pressEnterToContinue("No products or services found matching the search term.");
            } else {
                ConsoleUtil.pressEnterToContinue("");
            }
        } catch (Exception e) {
            ConsoleUtil.pressEnterToContinue("Error searching: " + e.getMessage());
        }
    }
    
    @Override
    public String getTitle() {
        return "BROWSE PRODUCTS & SERVICES";
    }
}