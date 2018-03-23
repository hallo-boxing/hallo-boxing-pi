package org.xiaoheshan.hallo.boxing.pi.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xiaoheshan.hallo.boxing.pi.net.ServerConnector;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : _Chf
 * @since : 03-22-2018
 */
@Component
public class Executors {

    private static final Logger LOGGER = LoggerFactory.getLogger(Executors.class);

    private static final Map<String, IExecutor> COMMAND_REGISTRY;
    private static ServerConnector connector;

    static {
        COMMAND_REGISTRY = new HashMap<String, IExecutor>();
        COMMAND_REGISTRY.put("CAMERA", new CameraExecutor());
        COMMAND_REGISTRY.put("CHECKOUT", new CheckoutExecutor());
        COMMAND_REGISTRY.put("DOOR", new DoorExecutor());
    }

    @Autowired
    public Executors(ServerConnector serverConnector) {
        connector = serverConnector;
    }

    public static void execute(String command) {
        String[] commandAndParams = command.split("=");
        if (COMMAND_REGISTRY.containsKey(commandAndParams[0])) {
            String result = COMMAND_REGISTRY.get(commandAndParams[0])
                    .execute(commandAndParams.length <= 1 ? null : commandAndParams[1].split(","));
            connector.send(result);
            return;
        }
        LOGGER.warn("未匹配到该消息的执行器");
    }

}
