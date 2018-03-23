package org.xiaoheshan.hallo.boxing.pi.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xiaoheshan.hallo.boxing.pi.util.ConsoleUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.xiaoheshan.hallo.boxing.pi.enums.ErrorCodeEnum.*;
import static org.xiaoheshan.hallo.boxing.pi.enums.ResponseEnum.*;

/**
 * @author : _Chf
 * @since : 03-22-2018
 */
public class CheckoutExecutor implements IExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckoutExecutor.class);

    private static final String NFC_COMMAND = "nfc-poll";
    private static final String INDUCE_STRING = "UID (NFCID1):";

    @Override
    public String execute(String... params) {
        ConsoleUtil.get().println("正在执行检卡命令");
        try {
            String id = "";
            Process process = Runtime.getRuntime().exec(NFC_COMMAND);
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
                ConsoleUtil.get().println("未检查到卡");
                return ERROR.getWithParam(CHECKOUT_NONE_ERROR.getCode().toString());
            }
            ConsoleUtil.get().println("完成检卡命令，ID：" + id);
            return OK.getWithParam(id);
        } catch (InterruptedException | IOException e) {
            return ERROR.getWithParam(SYSTEM_ERROR.getCode().toString());
        }
    }

    private String parseId(String data) {
        StringBuilder builder = new StringBuilder();
        String[] IDs = data.substring(data.indexOf(INDUCE_STRING) + INDUCE_STRING.length())
                .split(" ");
        for (String id : IDs) {
            builder.append(id);
        }
        return builder.toString();
    }

}
