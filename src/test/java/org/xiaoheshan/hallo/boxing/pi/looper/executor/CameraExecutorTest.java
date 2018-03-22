package org.xiaoheshan.hallo.boxing.pi.looper.executor;

import org.junit.Test;
import org.xiaoheshan.hallo.boxing.pi.io.ConsoleHolder;

import static org.junit.Assert.*;

/**
 * @author : _Chf
 * @since : 03-21-2018
 */
public class CameraExecutorTest {

    @Test
    public void execute() {
        new CameraExecutor(new ConsoleHolder()).execute("123");
    }
}