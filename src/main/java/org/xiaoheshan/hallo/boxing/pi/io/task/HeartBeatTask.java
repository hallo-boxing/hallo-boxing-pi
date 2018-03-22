package org.xiaoheshan.hallo.boxing.pi.io.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.xiaoheshan.hallo.boxing.pi.io.ServerConnector;

/**
 * @author : _Chf
 * @since : 03-19-2018
 */
@Component
public class HeartBeatTask implements Runnable {

    private final ServerConnector connector;

    @Autowired
    public HeartBeatTask(ServerConnector connector) {
        this.connector = connector;
        try {
            this.connector.waitConnectorInitialized();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    @Scheduled(fixedDelay = 90000)
    public void run() {
        connector.send("HB+HEART_BERT");
    }
}
