# Pet Care Service & E-commerce Management System

A Java console application for managing a pet care service and e-commerce platform.

## Features

- **Customer Management**: Register, update, and manage customer accounts.
- **Product & Service Management**: Create, view, update, and delete products and services.
- **Order Management**: Process customer orders for products.
- **Booking Management**: Schedule and manage service appointments.
- **Review Management**: Allow customers to review services.
- **Blog Management**: Create and manage blog posts.
- **Chat System**: Direct messaging between customers and administrators.
- **FAQ & Contact**: Manage FAQ entries and customer inquiries.
- **Authentication**: Secure login, registration, and password management.

## Technical Details

- **Language**: Java 11
- **Database**: H2 (in-memory) or MySQL (configurable)
- **Persistence**: JDBC
- **Project Management**: Maven
- **Architecture**: Layered architecture (Model-DAO-Service-CLI)

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 11 or higher
- Maven 3.6 or higher
- MySQL Server (optional, if not using H2 in-memory)

### Building the Application

```bash
mvn clean package
```

This will create an executable JAR file with all dependencies in the `target` directory.

### Running the Application

```bash
java -jar target/pet-care-java-1.0-SNAPSHOT-jar-with-dependencies.jar
```

Or via Maven:

```bash
mvn exec:java -Dexec.mainClass="com.petcare.Main"
```

## Configuration

Application settings can be configured in `src/main/resources/application.properties`:

- Database type (H2 or MySQL)
- Connection details (URL, username, password)

## Default Admin Account

- Email: admin@petcare.com
- Password: admin123

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Author

[Your Name]