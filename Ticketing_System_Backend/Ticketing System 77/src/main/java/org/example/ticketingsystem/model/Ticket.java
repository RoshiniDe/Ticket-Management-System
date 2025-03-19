package org.example.ticketingsystem.model;

public class Ticket {
    private final long ticketId;
    private final long vendorId;
    private Long customerId;

    //Indicates whether the ticket has been purchased
    private boolean purchased;

    //Constructs a new Ticket instance.
    public Ticket(long ticketId, long vendorId) {
        this.ticketId = ticketId;
        this.vendorId = vendorId;
        this.purchased = false;
    }

    //getters and setters for the  ticket properties
    public long getTicketId() {
        return ticketId;
    }

    public long getVendorId() {
        return vendorId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public boolean isPurchased() {
        return purchased;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }
}