package com.petcare.cli;

import com.petcare.model.Product;
import com.petcare.model.Service;
import com.petcare.service.ProductService;
import com.petcare.service.ServiceService;
import com.petcare.service.impl.ProductServiceImpl;
import com.petcare.service.impl.ServiceServiceImpl;
import com.petcare.util.ConsoleUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Menu for product and service management (admin only).
 */
public class ProductServiceManagementMenu implements Menu {
    
    private final AppSession session;
    private final ProductService productService;
    private final ServiceService serviceService;
    private final Scanner scanner;
    
    public ProductServiceManagementMenu() {
        this.session = AppSession.getInstance();
        this.productService = new ProductServiceImpl();
        this.serviceService = new ServiceServiceImpl();
        this.scanner = new Scanner(System.in);
    }
    
    @Override
    public boolean display() {
        if (!session.isAdmin()) {
            ConsoleUtil.pressEnterToContinue("Access denied. Admin privileges required.");
            return true;
        }
        
        String[] options = {
            "Manage Products",
            "Manage Services"
        };
        
        int choice = ConsoleUtil.displayMenu(getTitle(), options);
        
        if (choice == 0) {
            return true; // Return to parent menu
        }
        
        switch (choice) {
            case 1:
                manageProducts();
                break;
            case 2:
                manageServices();
                break;
        }
        
        return true;
    }
    
    private void manageProducts() {
        boolean exit = false;
        
        while (!exit) {
            ConsoleUtil.displayTitle("Product Management");
            
            String[] options = {
                "List All Products",
                "View Product Details",
                "Add New Product",
                "Edit Product",
                "Delete Product",
                "Search Products",
                "View Products by Type"
            };
            
            int choice = ConsoleUtil.displayMenu("Product Management Options", options);
            
            if (choice == 0) {
                exit = true;
                continue;
            }
            
            try {
                switch (choice) {
                    case 1:
                        listAllProducts();
                        break;
                    case 2:
                        viewProductDetails();
                        break;
                    case 3:
                        addNewProduct();
                        break;
                    case 4:
                        editProduct();
                        break;
                    case 5:
                        deleteProduct();
                        break;
                    case 6:
                        searchProducts();
                        break;
                    case 7:
                        viewProductsByType();
                        break;
                }
            } catch (Exception e) {
                ConsoleUtil.pressEnterToContinue("Error: " + e.getMessage());
            }
        }
    }
    
    private void listAllProducts() {
        ConsoleUtil.displayTitle("All Products");
        
        List<Product> products = productService.getAllProducts();
        
        if (products.isEmpty()) {
            ConsoleUtil.pressEnterToContinue("No products found.");
            return;
        }
        
        displayProductsList(products);
        ConsoleUtil.pressEnterToContinue("");
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
    
    private void addNewProduct() {
        ConsoleUtil.displayTitle("Add New Product");
        
        String name = ConsoleUtil.readString("Product name: ");
        String description = ConsoleUtil.readString("Product description: ");
        BigDecimal price = ConsoleUtil.readBigDecimal("Product price ($): ");
        
        System.out.println("Product type options: FOOD, TOY, OTHER");
        String type = ConsoleUtil.readString("Product type: ").toUpperCase();
        
        try {
            Product product = productService.createProduct(name, description, price, type);
            ConsoleUtil.pressEnterToContinue("Product added successfully. ID: " + product.getId());
        } catch (Exception e) {
            ConsoleUtil.pressEnterToContinue("Error adding product: " + e.getMessage());
        }
    }
    
    private void editProduct() {
        Long productId = ConsoleUtil.readLong("Enter product ID to edit (0 to cancel): ");
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
            ConsoleUtil.displayTitle("Edit Product (ID: " + productId + ")");
            
            System.out.println("Current name: " + product.getName());
            System.out.print("New name (press Enter to keep current): ");
            String input = scanner.nextLine().trim();
            String name = input.isEmpty() ? product.getName() : input;
            
            System.out.println("Current description: " + product.getDescription());
            System.out.print("New description (press Enter to keep current): ");
            input = scanner.nextLine().trim();
            String description = input.isEmpty() ? product.getDescription() : input;
            
            System.out.println("Current price: $" + product.getPrice());
            System.out.print("New price (press Enter to keep current): ");
            input = scanner.nextLine().trim();
            BigDecimal price = product.getPrice();
            if (!input.isEmpty()) {
                try {
                    price = new BigDecimal(input);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid price format. Using current price.");
                }
            }
            
            System.out.println("Current type: " + product.getType());
            System.out.println("Product type options: FOOD, TOY, OTHER");
            System.out.print("New type (press Enter to keep current): ");
            input = scanner.nextLine().trim().toUpperCase();
            String type = input.isEmpty() ? product.getType() : input;
            
            Product updatedProduct = productService.updateProduct(productId, name, description, price, type);
            
            if (updatedProduct != null) {
                ConsoleUtil.pressEnterToContinue("Product updated successfully.");
            } else {
                ConsoleUtil.pressEnterToContinue("Failed to update product.");
            }
        } catch (Exception e) {
            ConsoleUtil.pressEnterToContinue("Error updating product: " + e.getMessage());
        }
    }
    
    private void deleteProduct() {
        Long productId = ConsoleUtil.readLong("Enter product ID to delete (0 to cancel): ");
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
            
            System.out.println("You are about to delete the following product:");
            System.out.println("ID: " + product.getId());
            System.out.println("Name: " + product.getName());
            System.out.println("Price: $" + product.getPrice());
            System.out.println("Type: " + product.getType());
            
            String confirm = ConsoleUtil.readString("Are you sure you want to delete this product? (y/n): ");
            
            if (confirm.equalsIgnoreCase("y")) {
                boolean deleted = productService.deleteProduct(productId);
                
                if (deleted) {
                    ConsoleUtil.pressEnterToContinue("Product deleted successfully.");
                } else {
                    ConsoleUtil.pressEnterToContinue("Failed to delete product.");
                }
            } else {
                ConsoleUtil.pressEnterToContinue("Product deletion cancelled.");
            }
        } catch (Exception e) {
            ConsoleUtil.pressEnterToContinue("Error deleting product: " + e.getMessage());
        }
    }
    
    private void searchProducts() {
        String searchText = ConsoleUtil.readString("Enter search term: ");
        
        try {
            List<Product> products = productService.searchProducts(searchText);
            
            ConsoleUtil.displayTitle("Search Results for: " + searchText);
            
            if (products.isEmpty()) {
                ConsoleUtil.pressEnterToContinue("No products found matching the search term.");
                return;
            }
            
            displayProductsList(products);
            ConsoleUtil.pressEnterToContinue("");
        } catch (Exception e) {
            ConsoleUtil.pressEnterToContinue("Error searching products: " + e.getMessage());
        }
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
    
    private void manageServices() {
        boolean exit = false;
        
        while (!exit) {
            ConsoleUtil.displayTitle("Service Management");
            
            String[] options = {
                "List All Services",
                "View Service Details",
                "Add New Service",
                "Edit Service",
                "Delete Service",
                "Search Services",
                "View Services by Duration",
                "Sort Services by Price"
            };
            
            int choice = ConsoleUtil.displayMenu("Service Management Options", options);
            
            if (choice == 0) {
                exit = true;
                continue;
            }
            
            try {
                switch (choice) {
                    case 1:
                        listAllServices();
                        break;
                    case 2:
                        viewServiceDetails();
                        break;
                    case 3:
                        addNewService();
                        break;
                    case 4:
                        editService();
                        break;
                    case 5:
                        deleteService();
                        break;
                    case 6:
                        searchServices();
                        break;
                    case 7:
                        viewServicesByDuration();
                        break;
                    case 8:
                        sortServicesByPrice();
                        break;
                }
            } catch (Exception e) {
                ConsoleUtil.pressEnterToContinue("Error: " + e.getMessage());
            }
        }
    }
    
    private void listAllServices() {
        ConsoleUtil.displayTitle("All Services");
        
        List<Service> services = serviceService.getAllServices();
        
        if (services.isEmpty()) {
            ConsoleUtil.pressEnterToContinue("No services found.");
            return;
        }
        
        displayServicesList(services);
        ConsoleUtil.pressEnterToContinue("");
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
    
    private void addNewService() {
        ConsoleUtil.displayTitle("Add New Service");
        
        String name = ConsoleUtil.readString("Service name: ");
        String description = ConsoleUtil.readString("Service description: ");
        BigDecimal price = ConsoleUtil.readBigDecimal("Service price ($): ");
        int durationMinutes = ConsoleUtil.readInt("Duration in minutes: ");
        
        try {
            Service service = serviceService.createService(name, description, price, durationMinutes);
            ConsoleUtil.pressEnterToContinue("Service added successfully. ID: " + service.getId());
        } catch (Exception e) {
            ConsoleUtil.pressEnterToContinue("Error adding service: " + e.getMessage());
        }
    }
    
    private void editService() {
        Long serviceId = ConsoleUtil.readLong("Enter service ID to edit (0 to cancel): ");
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
            ConsoleUtil.displayTitle("Edit Service (ID: " + serviceId + ")");
            
            System.out.println("Current name: " + service.getName());
            System.out.print("New name (press Enter to keep current): ");
            String input = scanner.nextLine().trim();
            String name = input.isEmpty() ? service.getName() : input;
            
            System.out.println("Current description: " + service.getDescription());
            System.out.print("New description (press Enter to keep current): ");
            input = scanner.nextLine().trim();
            String description = input.isEmpty() ? service.getDescription() : input;
            
            System.out.println("Current price: $" + service.getPrice());
            System.out.print("New price (press Enter to keep current): ");
            input = scanner.nextLine().trim();
            BigDecimal price = service.getPrice();
            if (!input.isEmpty()) {
                try {
                    price = new BigDecimal(input);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid price format. Using current price.");
                }
            }
            
            System.out.println("Current duration: " + service.getDurationMinutes() + " minutes");
            System.out.print("New duration in minutes (press Enter to keep current): ");
            input = scanner.nextLine().trim();
            int durationMinutes = service.getDurationMinutes();
            if (!input.isEmpty()) {
                try {
                    durationMinutes = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid duration format. Using current duration.");
                }
            }
            
            Service updatedService = serviceService.updateService(serviceId, name, description, price, durationMinutes);
            
            if (updatedService != null) {
                ConsoleUtil.pressEnterToContinue("Service updated successfully.");
            } else {
                ConsoleUtil.pressEnterToContinue("Failed to update service.");
            }
        } catch (Exception e) {
            ConsoleUtil.pressEnterToContinue("Error updating service: " + e.getMessage());
        }
    }
    
    private void deleteService() {
        Long serviceId = ConsoleUtil.readLong("Enter service ID to delete (0 to cancel): ");
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
            
            System.out.println("You are about to delete the following service:");
            System.out.println("ID: " + service.getId());
            System.out.println("Name: " + service.getName());
            System.out.println("Price: $" + service.getPrice());
            System.out.println("Duration: " + service.getDurationMinutes() + " minutes");
            
            String confirm = ConsoleUtil.readString("Are you sure you want to delete this service? (y/n): ");
            
            if (confirm.equalsIgnoreCase("y")) {
                boolean deleted = serviceService.deleteService(serviceId);
                
                if (deleted) {
                    ConsoleUtil.pressEnterToContinue("Service deleted successfully.");
                } else {
                    ConsoleUtil.pressEnterToContinue("Failed to delete service.");
                }
            } else {
                ConsoleUtil.pressEnterToContinue("Service deletion cancelled.");
            }
        } catch (Exception e) {
            ConsoleUtil.pressEnterToContinue("Error deleting service: " + e.getMessage());
        }
    }
    
    private void searchServices() {
        String searchText = ConsoleUtil.readString("Enter search term: ");
        
        try {
            List<Service> services = serviceService.searchServices(searchText);
            
            ConsoleUtil.displayTitle("Search Results for: " + searchText);
            
            if (services.isEmpty()) {
                ConsoleUtil.pressEnterToContinue("No services found matching the search term.");
                return;
            }
            
            displayServicesList(services);
            ConsoleUtil.pressEnterToContinue("");
        } catch (Exception e) {
            ConsoleUtil.pressEnterToContinue("Error searching services: " + e.getMessage());
        }
    }
    
    private void viewServicesByDuration() {
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
    
    private void sortServicesByPrice() {
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
    
    @Override
    public String getTitle() {
        return "PRODUCT & SERVICE MANAGEMENT";
    }
}