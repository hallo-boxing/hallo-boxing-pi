package org.xiaoheshan.hallo.boxing.pi.util;

/**
 * @author : _Chf
 * @since : 03-22-2018
 */
public class ThreadSleepUtil {

    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
        }
    }

}
