package org.xiaoheshan.hallo.boxing.pi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.xiaoheshan.hallo.boxing.pi.looper.ServerMessageLooper;

/**
 * @author : _Chf
 * @since : 03-14-2018
 */
@SpringBootApplication
public class RaspberryPiApplication {
    public static void main(String[] args) {
        System.setProperty("pi4j.linking", "dynamic");
        SpringApplication.run(RaspberryPiApplication.class, args);
        ServerMessageLooper.loop();
    }
}
