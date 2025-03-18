import React from 'react';
import './styles.css';

import Configuration from './components/Configuration';
import Reporting from './components/Reporting';
import TicketPool from './components/TicketPool';

function App() {
    return (
        <div className="container">
            <h1 className="header">Real-Time Ticketing System</h1>

            {/* Renders the Configuration component */}
            <Configuration />

            {/* Renders the Ticket Pool component */}
            <TicketPool />

            {/* Renders the Reporting component */}
            <Reporting />

            {/* Footer */}
            <div className="footer">© 2024 Real-Time Ticketing System</div>
        </div>
    );
}

export default App;
