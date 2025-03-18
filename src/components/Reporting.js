import React, {useState, useEffect, useRef} from "react";
import axios from "axios";

const API_URL = "http://localhost:8080";

const Reporting = () => {
    const [logs, setLogs] = useState([]);
    const [systemStatus, setSystemStatus] = useState("Running");
    const logsEndRef = useRef(null);



    useEffect(() => {
        const fetchSystemUpdates = async () => {
            try {
                // Fetch logs from the API
                const logsResponse = await axios.get("http://localhost:8080/control/logs");
                setLogs(logsResponse.data);

                // Fetch system status from the API
                const statusResponse = await axios.get("http://localhost:8080/control/status");
                if (statusResponse.data.isSoldOut) {
                    setSystemStatus("Sold Out");
                } else if (statusResponse.data.isSystemStopped) {
                    setSystemStatus("Stopped");
                } else {
                    setSystemStatus("Running");
                }
            } catch (err) {
                console.error("Error fetching system updates:", err);
            }
        };

        //initial fetch
        fetchSystemUpdates();

        // Set up polling to fetch updates every second
        const interval = setInterval(fetchSystemUpdates, 1000);

        // Cleanup
        return () => clearInterval(interval);
    }, []);

    return (
        <div className="panel">
            <h2>System Reporting</h2>
            <div className="system-status">
                <h3>Status: </h3>
            </div>
            <div className="logs-container">
                <h3>Activity Logs</h3>
                <div className="logs-scroll">
                    {logs.map((log, index) => (
                        <div key={index} className="log-entry">
                            {log}
                        </div>
                    ))}
                    <div ref={logsEndRef} />
                </div>
            </div>
        </div>
    );
};

export default Reporting;
