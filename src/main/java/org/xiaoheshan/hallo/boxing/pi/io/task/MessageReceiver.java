package org.xiaoheshan.hallo.boxing.pi.io.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.xiaoheshan.hallo.boxing.pi.io.ServerConnector;
import org.xiaoheshan.hallo.boxing.pi.looper.ServerMessageLooper;

import static org.xiaoheshan.hallo.boxing.pi.enums.GSMResponseEnum.TCP_RECEIVE_PREFIX;

/**
 * @author : _Chf
 * @since : 03-19-2018
 */
@Component
public class MessageReceiver implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageReceiver.class);

    private final ServerConnector connector;

    @Autowired
    public MessageReceiver(ServerConnector connector) {
        this.connector = connector;
        try {
            this.connector.waitConnectorInitialized();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    @Scheduled(fixedDelay = 1000)
    public void run() {
        String message = connector.receive();
        if (message.isEmpty()) {
            return;
        }
        if (TCP_RECEIVE_PREFIX.is(message)) {
            ServerMessageLooper.offerMessage(message);
            return;
        }
        LOGGER.warn("接受到未知数据：" + message);
    }
}
