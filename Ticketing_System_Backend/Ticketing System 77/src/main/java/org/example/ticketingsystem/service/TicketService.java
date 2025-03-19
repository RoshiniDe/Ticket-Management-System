package org.example.ticketingsystem.service;

import org.example.ticketingsystem.model.Config;
import org.example.ticketingsystem.model.Ticket;
import org.example.ticketingsystem.model.TicketPool;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TicketService {

    private final String CONFIG_FILE_PATH = "config.json";  //path to the configuration file
    private TicketPool ticketPool; // The pool of available tickets
    private ExecutorService executor;  // Executor service to manage threads
    private final AtomicLong vendorIdGenerator = new AtomicLong(1);  // AtomicLong for generating unique vendor IDs
    private final AtomicLong customerIdGenerator = new AtomicLong(1);  // AtomicLong for generating unique customer IDs

    private final List<String> systemLogs = new CopyOnWriteArrayList<>();    // Thread-safe list to store system logs
    private static final int MAX_LOGS = 100;  //maximum number of logs to store

    // Flag to indicate if the system is initialized
    private volatile boolean isInitialized = false;

    //Starts the ticket system with the provided configuration
    public void startSystem(Config config) {

        // Create a new TicketPool based on configuration
        ticketPool = new TicketPool(config.getMaxTicketCapacity(), config.getTotalTickets());
        executor = Executors.newFixedThreadPool(config.getNumberOfVendors() + config.getNumberOfCustomers());

        // Start vendor threads
        for (int i = 0; i < config.getNumberOfVendors(); i++) {
            long vendorId = vendorIdGenerator.getAndIncrement();
            executor.execute(new Vendor(vendorId, ticketPool, config.getTicketReleaseRate()));
        }

        // Start customer threads
        for (int i = 0; i < config.getNumberOfCustomers(); i++) {
            long customerId = customerIdGenerator.getAndIncrement();
            executor.execute(new Customer(customerId, ticketPool, config.getCustomerRetrievalRate()));
        }

        isInitialized = true;
    }

    //Stops the ticket system by shutting down the thread pool
    public void stopSystem() {
        if (ticketPool != null) {
            ticketPool.stopSystem();   //notifying the TicketPool to stop operations.

        }

        if (executor != null) {
            executor.shutdownNow();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    addLog("Some threads did not terminate");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        isInitialized = false;
    }

    public boolean isSystemInitialized() {
        return isInitialized;
    }

    public Map<String, Object> getSystemStatus() {
        Map<String, Object> status = new HashMap<>();
        if (ticketPool != null) {
            status.put("totalTickets", ticketPool.getTotalTickets());
            status.put("ticketsCreated", ticketPool.getTotalTicketsCreated());
            status.put("ticketsSold", ticketPool.getTotalTicketsSold());
            status.put("remainingTickets", ticketPool.getTicketCount());
            status.put("isSoldOut", ticketPool.isSoldOut());
            status.put("isSystemStopped", ticketPool.isSystemStopped());
        } else {
            status.put("error", "System not started");
        }
        return status;
    }

    //Gets the list of system logs
    public List<String> getSystemLogs() {
        return new ArrayList<>(systemLogs);
    }

    private void addLog(String log) {
        systemLogs.add(log);
        if (systemLogs.size() > MAX_LOGS) {
            systemLogs.remove(0);
        }
    }

    //Saves the current system configuration to a JSON file.
    public void saveConfigToFile(Config config) throws IOException {
        String json = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(config);
        Files.write(Paths.get(CONFIG_FILE_PATH), json.getBytes());
    }

    //Loads the system configuration from a JSON file
    public Config loadConfigFromFile() throws IOException {
        byte[] jsonData = Files.readAllBytes(Paths.get(CONFIG_FILE_PATH));
        return new com.fasterxml.jackson.databind.ObjectMapper().readValue(jsonData, Config.class);
    }

    static class Vendor implements Runnable {
        private final long vendorId;
        private final TicketPool ticketPool;
        private final double releaseRate;

        public Vendor(long vendorId, TicketPool ticketPool, double releaseRate) {
            this.vendorId = vendorId;
            this.ticketPool = ticketPool;
            this.releaseRate = releaseRate;
        }

        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted() && !ticketPool.isSoldOut() && !ticketPool.isSystemStopped()) {
                    if (ticketPool.addTicket(vendorId)) {
                        System.out.println("Vendor " + vendorId + " added a ticket - Ticket ID: " +
                                ticketPool.getTotalTicketsCreated() +
                                " (Total: " + ticketPool.getTotalTicketsCreated() + "/" + ticketPool.getTotalTickets() + ")");
                    }
                    Thread.sleep((long) (1000 / releaseRate));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    static class Customer implements Runnable {
        private final long customerId;
        private final TicketPool ticketPool;
        private final double retrievalRate;

        public Customer(long customerId, TicketPool ticketPool, double retrievalRate) {
            this.customerId = customerId;
            this.ticketPool = ticketPool;
            this.retrievalRate = retrievalRate;
        }

        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted() && !ticketPool.isSoldOut() && !ticketPool.isSystemStopped()) {
                    Ticket ticket = ticketPool.removeTicket(customerId);
                    if (ticket != null) {
                        System.out.println("Customer " + customerId + " purchased ticket - Ticket ID: " +
                                ticket.getTicketId() + " from Vendor " + ticket.getVendorId() +
                                " (Sold: " + ticketPool.getTotalTicketsSold() + "/" + ticketPool.getTotalTickets() + ")");
                    }
                    Thread.sleep((long) (1000 / retrievalRate));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    static class SystemMonitor implements Runnable {
        private final TicketPool ticketPool;

        public SystemMonitor(TicketPool ticketPool) {
            this.ticketPool = ticketPool;
        }

        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted() && !ticketPool.isSystemStopped()) {
                    if (ticketPool.isSoldOut()) {
                        break;
                    }
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}