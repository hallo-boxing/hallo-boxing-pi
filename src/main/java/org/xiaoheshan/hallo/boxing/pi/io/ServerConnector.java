package org.xiaoheshan.hallo.boxing.pi.io;

import com.pi4j.io.serial.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xiaoheshan.hallo.boxing.pi.config.ServerProperty;
import org.xiaoheshan.hallo.boxing.pi.looper.ServerMessageLooper;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.xiaoheshan.hallo.boxing.pi.enums.AtCommandEnum.*;
import static org.xiaoheshan.hallo.boxing.pi.enums.GSMResponseEnum.OK;
import static org.xiaoheshan.hallo.boxing.pi.enums.GSMResponseEnum.SEND_TCP_READY;

/**
 * @author : _Chf
 * @since : 03-16-2018
 */
@Component
public class ServerConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerConnector.class);

    private Serial serial;
    private final ConsoleHolder console;
    private final ServerProperty serverProperty;
    private CountDownLatch initializedSingle;

    @Autowired
    public ServerConnector(ConsoleHolder console, ServerProperty serverProperty) {
        this.console = console;
        this.serial = SerialFactory.createInstance();
        this.serverProperty = serverProperty;
        this.initializedSingle = new CountDownLatch(1);
    }

    @PostConstruct
    private void init() {
        initSerial();
        initGsmModule();
        tryConnect();
        initializedSingle.countDown();
    }

    private void initSerial() {
        console.println("正在初始化服务连接器串口...");
        try {
            SerialConfig config = new SerialConfig()
                    .device(RaspberryPiSerial.DEFAULT_COM_PORT)
                    .baud(Baud._115200)
                    .dataBits(DataBits._8)
                    .parity(Parity.NONE)
                    .stopBits(StopBits._1)
                    .flowControl(FlowControl.NONE);
            serial.open(config);
            serial.flush();
            for (int i = 0; i < 15; i++) {
                console.print(".");
                TimeUnit.SECONDS.sleep(1);
            }
            console.println("");
            serial.discardAll();
        } catch (IOException | InterruptedException e) {
            console.println("初始化服务连接器串口失败：" + e.getMessage());
            LOGGER.error("初始化服务连接器串口失败", e);
        }
        console.println("完成初始化服务连接器串口");
    }

    private void initGsmModule() {
        console.println("正在初始化Gsm模块...");
        try {
            while (!write(SIM.withTest().build())) {
                console.println("初始化Gsm模块失败!");
                console.println("正在尝试重新初始化Gsm模块...");
                for (int i = 0; i < 5; i++) {
                    console.print(".");
                    TimeUnit.SECONDS.sleep(1);
                }
                console.println("");
            }
        } catch (InterruptedException | IOException e) {
            console.println("初始化Gsm模块发生错误");
            LOGGER.error("初始化Gsm模块发生错误", e);
        }
        console.println("初始化Gsm模块完成");
    }

    private void tryConnect() {
        console.println("正在连接服务端...");
        console.box("连接远程服务器：" + serverProperty.getName(),
                "远程服务器IP：" + serverProperty.getIp(),
                "远程服务器端口：" + serverProperty.getPort());
        String tcpOpenCommand = TCP_OPEN
                .param("TCP")
                .param(serverProperty.getIp())
                .param(serverProperty.getPort())
                .build();
        try {
            while (!write(tcpOpenCommand, 10)) {
                console.println("连接远程服务失败!");
                console.println("正在尝试重新连接...");
                for (int i = 0; i < 5; i++) {
                    console.print(".");
                    TimeUnit.SECONDS.sleep(1);
                }
                console.println("");
            }
        } catch (InterruptedException | IOException e) {
            console.println("连接服务器发生错误");
            LOGGER.error("连接服务器发生错误", e);
        }
        console.println("连接服务端完成");
    }

    public void waitConnectorInitialized() throws InterruptedException {
        this.initializedSingle.await();
    }

    public synchronized boolean send(String data) {
        if (data == null || data.isEmpty()) {
            return false;
        }
        console.println("向服务端发送数据：" + data);
        LOGGER.info("向服务端发送数据：" + data);
        try {
            if (serial.available() > 0) {
                ServerMessageLooper.offerMessage(doRead());
            }
            doSend(data);
            return OK.is(read());
        } catch (IOException | InterruptedException e) {
            console.println("向服务器发送数据发生错误：" + e.getMessage());
            LOGGER.error("向服务器发送数据发生错误", e);
        }
        return false;
    }

    public synchronized boolean send(Object data) {
        if (data == null) {
            return false;
        }
        if (data instanceof byte[]) {
            return send((byte[]) data);
        }
        return send(data.toString());
    }

    public synchronized String receive() {
        try {
            if (serial.available() > 0) {
                return doRead();
            }
        } catch (IOException | InterruptedException e) {
            console.println("接受服务端数据发生错误");
            LOGGER.warn("接受服务端数据发生错误", e);
        }
        return "";
    }

    public synchronized boolean send(byte[] bytes) {
        if (bytes == null) {
            return false;
        }
        console.println("发送大数据，数据大小：" + bytes.length);
        LOGGER.info("发送大数据，数据大小：" + bytes.length);
        try {
            if (serial.available() > 0) {
                ServerMessageLooper.offerMessage(doRead());
            }
            doSend("HB+BIG_START");
            TimeUnit.MILLISECONDS.sleep(1000);
            for (int i = 0; i < bytes.length; i += 15) {
                console.println("发送数据编号：" + i);
                if (i + 15 >= bytes.length) {
                    doSend(new String(bytes, i, bytes.length - i + 1));
                    continue;
                }
                doSend(new String(bytes, i, 15));
                TimeUnit.MILLISECONDS.sleep(2000);
            }
            console.println("发送大数据完成");
            doSend("HB+BIG_END");
            return OK.is(read());
        } catch (IOException | InterruptedException e) {
            console.println("向服务器发送数据发生错误：" + e.getMessage());
            LOGGER.error("向服务器发送数据发生错误", e);
        }
        return false;
    }

    private void doSend(String data) throws IOException, InterruptedException {
        doWrite(TCP_SEND_START.toString() + '\r');
        TimeUnit.MILLISECONDS.sleep(100);
        doWrite(data);
        doWrite(TCP_SEND_END.toString());
        TimeUnit.MILLISECONDS.sleep(100);
    }

    private boolean write(String data, int seconds) throws IOException, InterruptedException {
        if (data == null || data.isEmpty()) {
            return false;
        }
        if (serial.available() > 0) {
            ServerMessageLooper.offerMessage(doRead());
        }
        doWrite(data + '\r');
        for (int i = 0; i < seconds; i++) {
            console.print(".");
            TimeUnit.SECONDS.sleep(1);
        }
        console.println("");
        String result = read();
        return OK.is(result);
    }

    private boolean write(String data) throws IOException, InterruptedException {
        if (data == null || data.isEmpty()) {
            return false;
        }
        if (serial.available() > 0) {
            ServerMessageLooper.offerMessage(doRead());
        }
        doWrite(data + '\r');
        String result = read();
        return OK.is(result);
    }

    private String read() throws IOException, InterruptedException {
        int timeCount = 0;
        while (timeCount < 2) {
            if (serial.available() > 0) {
                return doRead();
            }
            timeCount += 1;
            TimeUnit.SECONDS.sleep(1);
        }
        return "";
    }

    private void doWrite(String data) throws IOException, InterruptedException {
        if (data == null || data.isEmpty()) {
            return;
        }
        serial.write(data);
    }

    private String doRead() throws IOException, InterruptedException {
        byte[] bytes = serial.read();
        return new String(bytes);
    }

}
