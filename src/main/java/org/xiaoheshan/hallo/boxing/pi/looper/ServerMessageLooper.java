package org.xiaoheshan.hallo.boxing.pi.looper;

import org.xiaoheshan.hallo.boxing.pi.executor.Executors;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author : _Chf
 * @since : 03-22-2018
 */
public class ServerMessageLooper {

    private static final BlockingQueue<String> MESSAGE_QUEUE = new LinkedBlockingDeque<String>();

    public static void loop() {
        for (;;) {
            try {
                String message = MESSAGE_QUEUE.take();
                Executors.execute(message);
            } catch (InterruptedException ignored) {
            }
        }
    }

    public static void offerMessage(String message) {
        MESSAGE_QUEUE.offer(message);
    }

}
