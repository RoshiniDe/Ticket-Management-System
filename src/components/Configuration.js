import React, { useState } from "react";
import axios from "axios";

const API_URL = "http://localhost:8080";

function Configuration() {
    //store user inputs for configuration
    const [settings, setSettings] = useState({
        numberOfVendors: "",
        numberOfCustomers: "",
        totalTickets: "",
        ticketReleaseRate: "",
        customerRetrievalRate: "",
        maxTicketCapacity: ""
    });

    // Handle form change
    const handleChange = (e) => {
        const { name, value } = e.target;
        setSettings((prev) => ({
            ...prev,
            [name]: value
        }));
    };

    // Handle start process
    const handleStartProcess = async (e) => {
        e.preventDefault(); // Add this line

        // Validate user inputs
        if (
            parseInt(settings.numberOfVendors) <= 0 ||
            parseInt(settings.numberOfCustomers) <= 0 ||
            parseInt(settings.totalTickets) <= 0 ||
            parseFloat(settings.ticketReleaseRate) <= 0 ||
            parseFloat(settings.customerRetrievalRate) <= 0 ||
            parseInt(settings.maxTicketCapacity) <= 0
        ) {
            alert("All fields must have positive numbers greater than 0.");
            return;
        }

        try {
            const response = await axios.post("http://localhost:8080/control/start", settings);
            alert("Configuration started successfully!");
            console.log(response.data);
        } catch (err) {
            console.error("Error starting process:", err);
            alert("Failed to start configuration.");
        }
    };

    // Handle stop process
    const handleStopProcess = async () => {
        try {
            await axios.post("http://localhost:8080/control/stop");
            alert("Process stopped.");
        } catch (err) {
            console.error("Error stopping process:", err);
            alert("Failed to stop the process.");
        }
    };

    // Handle resetting the form and system state
    const handleResetSystem = () => {
        setSettings({
            numberOfVendors: "",
            numberOfCustomers: "",
            totalTickets: "",
            ticketReleaseRate: "",
            customerRetrievalRate: "",
            maxTicketCapacity: ""
        });
        console.log("System reset.");
        alert("System reset.");
    };

    return (
        <div className="panel">
            <h3>System Configuration</h3>
            <form onSubmit={(e) => e.preventDefault()}>
                <div className="input-group">
                    <label>Number Of Vendors:</label>
                    <input
                        type="number"
                        name="numberOfVendors"
                        value={settings.numberOfVendors}
                        onChange={handleChange}
                        placeholder="Enter number of vendors"
                    />
                </div>
                <div className="input-group">
                    <label>Number Of Customers:</label>
                    <input
                        type="number"
                        name="numberOfCustomers"
                        value={settings.numberOfCustomers}
                        onChange={handleChange}
                        placeholder="Enter number of customers"
                    />
                </div>
                <div className="input-group">
                    <label>Total Tickets:</label>
                    <input
                        type="number"
                        name="totalTickets"
                        value={settings.totalTickets}
                        onChange={handleChange}
                        placeholder="Enter total tickets"
                    />
                </div>
                <div className="input-group">
                    <label>Ticket Release Rate:</label>
                    <input
                        type="number"
                        name="ticketReleaseRate"
                        value={settings.ticketReleaseRate}
                        onChange={handleChange}
                        placeholder="Enter ticket release rate"
                    />
                </div>
                <div className="input-group">
                    <label>Customer Retrieval Rate:</label>
                    <input
                        type="number"
                        name="customerRetrievalRate"
                        value={settings.customerRetrievalRate}
                        onChange={handleChange}
                        placeholder="Enter customer retrieval rate"
                    />
                </div>
                <div className="input-group">
                    <label>Max Ticket Capacity:</label>
                    <input
                        type="number"
                        name="maxTicketCapacity"
                        value={settings.maxTicketCapacity}
                        onChange={handleChange}
                        placeholder="Enter max ticket capacity"
                    />
                </div>
                <div className="button-group">
                    <button type="button" className="btn" onClick={handleStartProcess}>
                        Start
                    </button>
                    <button type="button" className="btn" onClick={handleStopProcess}>
                        Stop
                    </button>
                    <button type="button" className="btn" onClick={handleResetSystem}>
                        Reset
                    </button>
                </div>
            </form>
        </div>
    );
}

export default Configuration;


