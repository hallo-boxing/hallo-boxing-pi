package org.xiaoheshan.hallo.boxing.pi.looper.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xiaoheshan.hallo.boxing.pi.io.ConsoleHolder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * @author : _Chf
 * @since : 03-20-2018
 */
@Component
public class NfcExecutor implements IExecutor<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NfcExecutor.class);

    private static final String command = "nfc-poll";
    private static final String INDUCE_STRING = "UID (NFCID1):";
    private final ConsoleHolder console;

    @Autowired
    public NfcExecutor(ConsoleHolder console) {
        this.console = console;
    }

    @Override
    public String execute(String... parameters) {
        console.println("正在执行验卡命令: " + Arrays.toString(parameters));
        LOGGER.info("正在执行验卡命令: " + Arrays.toString(parameters));
        try {
            String id = "";
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.contains(INDUCE_STRING)) {
                    id = parseId(line);
                    break;
                }
            }
            process.waitFor();
            if (id.isEmpty()) {
                return "ERROR";
            }
            console.println("完成执行验卡命令");
            LOGGER.info("完成执行验卡命令");
            return "OK=" + id;
        } catch (Exception e) {
            LOGGER.error("执行Nfc命令发生错误", e);
            return "ERROR";
        }
    }

    private String parseId(String data) {
        StringBuilder builder = new StringBuilder();
        String[] IDs = data.substring(data.indexOf(INDUCE_STRING) + 1)
                .split(" ");
        for (String id : IDs) {
            builder.append(id);
        }
        return builder.toString();
    }

}
