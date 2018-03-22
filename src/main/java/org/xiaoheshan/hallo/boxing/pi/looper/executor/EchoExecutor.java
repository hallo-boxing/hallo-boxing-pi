package org.xiaoheshan.hallo.boxing.pi.looper.executor;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author : _Chf
 * @since : 03-18-2018
 */
@Component
public class EchoExecutor implements IExecutor<String> {

    @Override
    public String execute(String... parameters) {
        StringBuilder builder = new StringBuilder("硬件端接收到：");
        Optional.ofNullable(parameters)
                .ifPresent(strings ->
                        Arrays.stream(strings).forEach(
                                param -> builder.append(param).append(',')));
        return builder.toString();
    }
}
