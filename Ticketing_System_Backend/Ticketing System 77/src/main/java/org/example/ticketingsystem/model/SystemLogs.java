package org.example.ticketingsystem.model;

import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SystemLogs {
    private final CopyOnWriteArrayList<String> logs = new CopyOnWriteArrayList<>();  //thread safe list to store system logs.
    private static final int MAX_LOGS = 100;  //Maximum number of logs to store.

    public void addLog(String log) {
        logs.add(log);
        if (logs.size() > MAX_LOGS) {
            logs.remove(0);
        }
    }

    //returns list of last 100 logs
    public List<String> getLast100Logs() {
        return new ArrayList<>(logs);
    }
}
