package org.example.ticketingsystem.model;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public class TicketPool {
    private final ConcurrentLinkedQueue<Ticket> tickets;
    private final int capacity;
    private final int totalTickets;
    private final ReentrantLock lock = new ReentrantLock();
    private final AtomicLong ticketIdGenerator = new AtomicLong(1);
    private final AtomicLong totalTicketsSold = new AtomicLong(0);
    private final AtomicLong totalTicketsCreated = new AtomicLong(0);
    private volatile boolean isSoldOut = false;
    private volatile boolean isSystemStopped = false;

    public TicketPool(int capacity, int totalTickets) {
        this.capacity = capacity;
        this.totalTickets = totalTickets;
        this.tickets = new ConcurrentLinkedQueue<>();
    }

    public void stopSystem() {
        isSystemStopped = true;
        // Print final statistics
        System.out.println("\n=== FINAL SYSTEM STATUS ===");
        System.out.println("System stopped manually");
        System.out.println("Total tickets created: " + totalTicketsCreated.get());
        System.out.println("Total tickets sold: " + totalTicketsSold.get());
        System.out.println("Remaining tickets in pool: " + tickets.size());
        System.out.println("========================\n");
    }

    public boolean addTicket(long vendorId) {
        if (isSystemStopped || totalTicketsCreated.get() >= totalTickets) {
            return false;
        }

        lock.lock();
        try {
            if (tickets.size() < capacity && totalTicketsCreated.get() < totalTickets) {
                long ticketId = ticketIdGenerator.getAndIncrement();
                tickets.offer(new Ticket(ticketId, vendorId));
                totalTicketsCreated.incrementAndGet();
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public Ticket removeTicket(long customerId) {
        if (isSystemStopped || isSoldOut) {
            return null;
        }

        Ticket ticket = tickets.poll();
        if (ticket != null) {
            ticket.setCustomerId(customerId);
            ticket.setPurchased(true);
            long soldCount = totalTicketsSold.incrementAndGet();
            if (soldCount >= totalTickets) {
                isSoldOut = true;
                printFinalStatus("All tickets have been sold out!");
            }
            return ticket;
        }
        return null;
    }

    private void printFinalStatus(String reason) {
        System.out.println("\n=== FINAL SYSTEM STATUS ===");
        System.out.println(reason);
        System.out.println("Total tickets created: " + totalTicketsCreated.get());
        System.out.println("Total tickets sold: " + totalTicketsSold.get());
        System.out.println("Remaining tickets in pool: " + tickets.size());
        System.out.println("========================\n");
    }

    public boolean isSoldOut() {
        return isSoldOut;
    }

    public boolean isSystemStopped() {
        return isSystemStopped;
    }

    public int getTicketCount() {
        return tickets.size();
    }

    public long getTotalTicketsSold() {
        return totalTicketsSold.get();
    }

    public long getTotalTicketsCreated() {
        return totalTicketsCreated.get();
    }

    public int getTotalTickets() {
        return totalTickets;
    }
}