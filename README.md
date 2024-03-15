# Account Management System

## Description

This Account Management System is designed to manage financial transactions, balances, and accounts efficiently. It supports creating and managing bank accounts, processing various financial transactions, and providing detailed account and transaction information.

## Key Features

- **Account Creation**: Enables users to create bank accounts with support for multiple currencies.
- **Transaction Processing**: Facilitates creating and processing financial transactions, automatically updating account balances.
- **Balance Inquiry**: Allows users to retrieve detailed account information, including balances in various currencies.
- **Transaction History**: Provides a comprehensive list of all transactions associated with an account.

## Technology Stack

- **Java**: Core programming language.
- **Spring Boot**: Application framework used to simplify the setup and configuration of applications. Utilizing `spring-boot-starter-web` for web capabilities, `spring-boot-starter-actuator` for application health and metrics, `spring-boot-starter-amqp` for advanced messaging queue protocol support, and `spring-boot-starter-data-jpa` for Java Persistence API.
- **Springdoc OpenAPI**: Integration with Spring Boot for automatic generation of API documentation using `springdoc-openapi-ui`.
- **MyBatis**: ORM framework integrated through `mybatis-spring-boot-starter` for database interaction, providing a more flexible approach to handling SQL queries and database operations.
- **PostgreSQL**: Used as the primary relational database system, integrated through `org.postgresql:postgresql`.
- **RabbitMQ**: Employed as a messaging broker to facilitate asynchronous processing and communication between different parts of the application, using Spring AMQP with `spring-boot-starter-amqp`.
- **Lombok**: Simplifies code by auto-generating boilerplate code like getters, setters, and constructors through annotations, utilizing `org.projectlombok:lombok`.
- **JUnit**: Framework for unit and integration testing, employing `org.junit.jupiter:junit-jupiter` alongside Spring's testing support via `spring-boot-starter-test`.
- **Testcontainers**: Provides lightweight, throwaway instances of common databases, Selenium web browsers, or anything else that can run in a Docker container for testing, integrating `org.testcontainers` for PostgreSQL and RabbitMQ.

## Instructions on How to Build and Run the Application

To build and run the application, you will need Docker and Docker Compose installed on your machine. Once you have these prerequisites, follow the steps below:

1. **Clone the Repository**:
   - `git clone https://github.com/kburakf/tuum.git`
   - Navigate to the project directory: `cd tuum`

2. **Start the Application**:
   - Run `docker-compose up` from the project root directory.
   - This command will start all the necessary services as defined in your `docker-compose.yml` file, including the PostgreSQL database and RabbitMQ server.

Once all services are up, the application will be accessible on the configured ports. Swagger UI can be accessed to interact with the API at: http://localhost:8080/swagger-ui/index.html#/

Please make sure that no other services are running on the same ports required by this application to avoid port conflicts.

## Explanation of Important Choices in Our Solution

### Request Validation Prior to Queue Publishing

In our application, we have implemented a strategy to validate all incoming requests before they are published to the RabbitMQ queue. This decision was driven by the need to ensure that only valid and meaningful messages are processed by our system, thereby reducing unnecessary load on our messaging infrastructure and improving overall system efficiency and reliability.

By performing request validation upfront:

- We prevent invalid data from being processed downstream, avoiding potential errors or exceptions in our business logic or database operations.
- We reduce the amount of processing needed for filtering out invalid messages at later stages, which can be particularly beneficial when the system is under high load.
- We provide immediate feedback to the sender for any invalid requests, enhancing the API's usability and the client's experience.

This approach aligns with the best practices of enterprise integration patterns, ensuring that our message processing pipeline remains clean, efficient, and focused on delivering value.

### Pessimistic Locking for Transaction Processing

To manage concurrent transactions effectively, especially in scenarios where transaction sequences are critical (such as financial IN-OUT balance operations), we've implemented pessimistic locking at the transaction level. This choice is crucial for maintaining data integrity and consistency across our system.

Pessimistic locking ensures that when a transaction is being processed, no other transactions can interfere or modify the involved data until the first transaction is complete. This is particularly important for balance-related operations where the accuracy and consistency of each transaction can significantly impact the overall system state.

Key benefits of using pessimistic locking in our context include:

- **Data Consistency**: By locking the transaction record, we ensure that all operations are executed in a controlled and serialized manner, preserving the integrity of our financial data.
- **Concurrency Control**: Pessimistic locking helps us manage concurrency in high-load environments, preventing issues like lost updates or dirty reads that could arise in less controlled concurrency models.
- **Reliability**: With this locking mechanism, our system can reliably process a high volume of concurrent transactions without risking data anomalies or integrity issues.

While pessimistic locking can introduce performance overhead due to its restrictive nature, we've determined that the benefits of data integrity and consistency far outweigh these costs in our specific use case of financial transaction processing.

## Performance Estimation

- The application's transaction handling capacity on a development machine would depend on various factors, including hardware specifications and application configuration. A rough estimate could be obtained by performing load testing under controlled conditions.

## Scaling Considerations

To scale the application horizontally:

- **Statelessness**: Ensure the banking application components, like APIs handling account and transaction operations, are stateless so any instance can serve any request.
- **Load Balancing**: Implement load balancers to evenly distribute requests across multiple service instances, ensuring no single instance is overwhelmed, especially during peak traffic periods.
- **Database Replication and Sharding**: Use database replication to distribute read loads and sharding to distribute write loads, ensuring your database can scale with your application and maintain quick response times for balance checks and transaction records.
- **Microservices**: Adopt a microservices architecture to isolate different functionalities (e.g., account management, transaction processing, currency conversion), allowing for independent scaling and easier maintenance.
- **Monitoring and Auto-scaling**: Implement robust monitoring for all components of the application to detect performance bottlenecks or potential failures. Use auto-scaling to automatically adjust the number of active instances based on current load.
- **Consistency and Transactions**: Ensure that the system maintains data consistency across all operations, especially important in financial applications. Use distributed transactions where necessary and ensure ACID properties are respected.

### Load Results

| Label             | # Samples | Average | Min | Max  | Std. Dev. | Error % | Throughput | Received KB/sec | Sent KB/sec | Avg. Bytes |
|-------------------|-----------|---------|-----|------|-----------|---------|------------|-----------------|-------------|------------|
| Create transaction| 10000     | 7640    | 114 | 38420| 8145.68   | 0.000%  | 216.36592  | 79.66           | 70.57       | 377.0      |
| Create transaction| 21939     | 11658   | 4   | 81813| 10344.45  | 0.009%  | 74.71877   | 27.52           | 24.37       | 377.2      |

### Test Results

| Package       | Class, % | Methods, % | Lines, % |
|---------------|----------|------------|----------|
| org.example   | 88% (39/44) | 89% (140/156) | 89% (260/291) |
| └ config      | 50% (1/2)   | 12% (1/8)     | 27% (5/18)    |
| └ controller  | 100% (4/4)  | 100% (12/12)  | 100% (28/28)  |
| └ dto         | 100% (10/10)| 100% (10/10)  | 98% (57/58)   |
| └ enumtypes   | 100% (2/2)  | 100% (4/4)    | 100% (4/4)    |
| └ exception   | 91% (11/12) | 100% (11/11)  | 100% (11/11)  |
| └ handler     | 100% (1/1)  | 75% (3/4)     | 62% (5/8)     |
| └ mapper      | 100% (0/0)  | 0% (0/0)      | 0% (0/0)      |
| └ messaging   | 75% (3/4)   | 100% (5/5)    | 80% (8/10)    |
| └ model       | 100% (5/5)  | 85% (34/40)   | 85% (34/40)   |
| └ properties  | 0% (0/1)    | 100% (0/0)    | 100% (0/0)    |
| └ service     | 100% (2/2)  | 92% (12/13)   | 94% (93/98)   |
| └ Main        | 0% (0/1)    | 0% (0/1)      | 0% (0/1)      |
