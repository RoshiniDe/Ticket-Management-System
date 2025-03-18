import React, { useState, useEffect } from "react";
import axios from "axios";

const API_URL ="http://localhost:8080";

const TicketPool = () => {
    const [poolStatus, setPoolStatus] = useState({
        totalTickets: 0,
        ticketsCreated: 0,
        ticketsSold: 0,
        remainingTickets: 0
    });

    useEffect(() => {
        const fetchPoolStatus = async () => {
            try {
                const response = await axios.get("http://localhost:8080/control/status");
                setPoolStatus(response.data);
            } catch (err) {
                console.error("Error fetching ticket pool status:", err);
            }
        };

        // Fetch initially
        fetchPoolStatus();

        // Set up polling to fetch updates every second
        const interval = setInterval(fetchPoolStatus, 1000);

        // Cleanup
        return () => clearInterval(interval);
    }, []);

    return (
        <div className="panel">
            <h2>Ticket Pool Status</h2>
            <div className="status-grid">
                <div className="status-item">
                    <h3>Total Tickets</h3>
                    <p>{poolStatus.totalTickets}</p>
                </div>
                <div className="status-item">
                    <h3>Tickets Created</h3>
                    <p>{poolStatus.ticketsCreated}</p>
                </div>
                <div className="status-item">
                    <h3>Tickets Sold</h3>
                    <p>{poolStatus.ticketsSold}</p>
                </div>
                <div className="status-item">
                    <h3>Remaining Tickets</h3>
                    <p>{poolStatus.remainingTickets}</p>
                </div>
            </div>
        </div>
    );
};

export default TicketPool;
