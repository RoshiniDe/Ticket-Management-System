package org.example.ticketingsystem.controller;

import org.example.ticketingsystem.model.Config;
import org.example.ticketingsystem.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/control")
public class SystemController {

    @Autowired
    private TicketService ticketService;

    //start the ticketing system
    @PostMapping("/start")
    public ResponseEntity<String> startSystem(@RequestBody Config config) {
        try {
            ticketService.saveConfigToFile(config);
            ticketService.startSystem(config);
            return ResponseEntity.ok("System started successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error starting the system: " + e.getMessage());
        }
    }

    //stops the ticketing system
    @PostMapping("/stop")
    public ResponseEntity<String> stopSystem() {
        ticketService.stopSystem();
        return ResponseEntity.ok("System stopped successfully!");
    }

    //gets current system configuration
    @GetMapping("/config")
    public ResponseEntity<Config> getConfig() {
        try {
            return ResponseEntity.ok(ticketService.loadConfigFromFile());
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //gets current system status
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        if (!ticketService.isSystemInitialized()) {
            Map<String, Object> initialStatus = new HashMap<>();
            initialStatus.put("isInitialized", false);
            return ResponseEntity.ok(initialStatus);
        }
        return ResponseEntity.ok(ticketService.getSystemStatus());
    }

    //retrieves system logs
    @GetMapping("/logs")
    public ResponseEntity<List<String>> getLogs() {
        if (!ticketService.isSystemInitialized()) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(ticketService.getSystemLogs());
    }
}