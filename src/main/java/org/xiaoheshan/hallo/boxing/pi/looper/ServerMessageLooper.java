package org.xiaoheshan.hallo.boxing.pi.looper;

import org.xiaoheshan.hallo.boxing.pi.constant.ServerCommandConstant;
import org.xiaoheshan.hallo.boxing.pi.looper.executor.Executors;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import static org.xiaoheshan.hallo.boxing.pi.constant.ServerCommandConstant.*;

/**
 * @author : _Chf
 * @since : 03-17-2018
 */
public class ServerMessageLooper {

    private static final BlockingDeque<String> MESSAGE_QUEUE = new LinkedBlockingDeque<String>();

    public static void offerMessage(String message) {
        MESSAGE_QUEUE.offer(message);
    }

    public static void loop() {
        for (;;) {
            String peekMessage = null;
            try {
                peekMessage = MESSAGE_QUEUE.take();
                if (checkMessage(peekMessage)) {
                    Executors.execute(parseMessage(peekMessage));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean checkMessage(String message) {
        return message.contains(SERVER_COMMAND_PREFIX);
    }

    private static String parseMessage(String message) {
        int start = message.indexOf(SERVER_COMMAND_PREFIX) + SERVER_COMMAND_PREFIX.length();
        int end = message.indexOf(SERVER_COMMAND_SUFFIX, start + 1);
        return message.substring(start, end);
    }

}
