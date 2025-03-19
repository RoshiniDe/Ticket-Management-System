# Ticketing System Backend

## Overview

This is a multi-threaded ticket trading system implemented in Java using Spring Boot. The application simulates a concurrent environment where vendors create tickets and customers purchase them, with built-in thread-safe mechanisms to handle ticket sales.

## Key Features

- Concurrent ticket creation and sales
- Thread-safe ticket pool management
- Configurable system parameters
- Real-time system status tracking
- Logging capabilities

## System Components

### Main Classes

1. **TicketService**: 
   - Manages the overall ticket trading system
   - Handles system initialization and shutdown
   - Coordinates vendor and customer threads

2. **TicketPool**: 
   - Manages the collection of tickets
   - Ensures thread-safe ticket addition and removal
   - Tracks total tickets, sold tickets, and system state

3. **Config**: 
   - Configures system parameters such as:
     - Number of vendors
     - Number of customers
     - Total ticket count
     - Ticket release and retrieval rates
     - Maximum ticket capacity

### Concurrent Entities

- **Vendor**: Threads that create and add tickets to the pool
- **Customer**: Threads that retrieve and purchase tickets
- **SystemMonitor**: Optional monitoring thread to track system state

## Configuration

The system can be configured via a `config.json` file with the following parameters:

- `numberOfVendors`: Number of vendor threads
- `numberOfCustomers`: Number of customer threads
- `totalTickets`: Total number of tickets to be created
- `ticketReleaseRate`: Rate at which vendors create tickets
- `customerRetrievalRate`: Rate at which customers attempt to purchase tickets
- `maxTicketCapacity`: Maximum number of tickets in the pool at any time

## Concurrency Features

- Uses `ConcurrentLinkedQueue` for thread-safe ticket storage
- Implements `ReentrantLock` for critical section management
- Utilizes `AtomicLong` for thread-safe counting
- Supports graceful system shutdown

## Logging

- Maintains a log of system events
- Stores up to 100 most recent log entries
- Provides methods to retrieve system logs

## Endpoints (CORS Configured)

The backend supports CORS for a frontend application running on `http://localhost:3000` with the following methods:
- GET
- POST
- PUT
- DELETE

## System States

- **Initialized**: System is running, vendors and customers are active
- **Sold Out**: All tickets have been sold
- **Stopped**: System has been manually stopped

## Usage Example

```java
// Create configuration
Config config = new Config();
config.setNumberOfVendors(5);
config.setNumberOfCustomers(10);
config.setTotalTickets(100);
// ... set other parameters

// Start the ticket trading system
ticketService.startSystem(config);

// Later, stop the system
ticketService.stopSystem();
```

## Dependencies

- Spring Boot
- Jackson (for JSON serialization)
- Java Concurrent Utilities

## Potential Improvements

- Add more comprehensive error handling
- Implement more advanced logging
- Create more sophisticated monitoring mechanisms
- Add persistent storage for ticket and transaction data

## Performance Considerations

- Thread pool size is fixed based on vendor and customer count
- Ticket creation and retrieval have configurable rates
- Uses non-blocking data structures for improved concurrency


## Contributors

- Author: Roshini De Silva