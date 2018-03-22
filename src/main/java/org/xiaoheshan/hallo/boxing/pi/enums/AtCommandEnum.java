package org.xiaoheshan.hallo.boxing.pi.enums;

import org.springframework.util.Assert;

/**
 * @author : _Chf
 * @since : 03-21-2018
 */
public enum AtCommandEnum {
    AT("AT"),
    SIM("AT+CPIN"),
    TCP_OPEN("AT+CIPSTART"),
    TCP_CLOSE("AT+CIPCLOSE"),
    TCP_SEND_START("AT+CIPSEND"),
    TCP_SEND_END(new String(new byte[]{0x1A})),
    ;

    private final String name;
    private StringBuilder builder;

    AtCommandEnum(String name) {
        this.name = name;
        this.builder = new StringBuilder();
    }

    public AtCommandEnum withTest() {
        builder.append("?");
        return this;
    }

    public AtCommandEnum param(String... params) {
        Assert.notNull(params);
        Assert.isTrue(params.length >= 1);
        if (builder.indexOf("=") < 0) {
            builder.append('=');
        }
        for (String param : params) {
            builder.append("\"").append(param).append("\"").append(',');
        }
        return this;
    }

    public AtCommandEnum param(Integer... params) {
        Assert.notNull(params);
        Assert.isTrue(params.length >= 1);
        if (builder.indexOf("=") < 0) {
            builder.append('=');
        }
        for (Integer param : params) {
            builder.append(param).append(',');
        }
        return this;
    }

    public String build() {
        if (builder.indexOf("=") >= 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        String result = name + builder.toString();
        builder = new StringBuilder();
        return result;
    }


    @Override
    public String toString() {
        return name;
    }
}
