package org.xiaoheshan.hallo.boxing.pi.looper.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xiaoheshan.hallo.boxing.pi.io.ConsoleHolder;
import org.xiaoheshan.hallo.boxing.pi.io.ServerConnector;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

import static org.xiaoheshan.hallo.boxing.pi.constant.ServerCommandConstant.*;

/**
 * @author : _Chf
 * @since : 03-18-2018
 */
@Component
public class Executors {

    private static final Logger LOGGER = LoggerFactory.getLogger(Executors.class);

    private static ServerConnector connector;
    private static BeanFactory beanFactory;
    private static ConsoleHolder console;
    private static final Map<String, IExecutor> executorMap = new HashMap<String, IExecutor>();

    @Autowired
    public Executors(BeanFactory factory, ServerConnector serverConnector, ConsoleHolder consoleHolder) {
        beanFactory = factory;
        connector = serverConnector;
        console = consoleHolder;
    }

    @PostConstruct
    public static void registryExecutors() {
        executorMap.put(SERVER_COMMAND_ECHO, beanFactory.getBean(EchoExecutor.class));
        executorMap.put(SERVER_COMMAND_DOOR, beanFactory.getBean(DoorExecutor.class));
        executorMap.put(SERVER_COMMAND_NFC, beanFactory.getBean(NfcExecutor.class));
        executorMap.put(SERVER_COMMAND_CAMERA, beanFactory.getBean(CameraExecutor.class));
    }

    public static void execute(String message) {
        console.println("正在执行：" + message);
        for (Map.Entry<String, IExecutor> entry : executorMap.entrySet()) {
            if (message.startsWith(entry.getKey())) {
                String[] params = null;
                if (message.contains("=")) {
                    params = message.substring(message.indexOf('=') + 1).split(",");
                }
                connector.send(entry.getValue().execute(params));
                return;
            }
        }
        LOGGER.warn("没有匹配到该消息的执行器：" + message);
    }

}
