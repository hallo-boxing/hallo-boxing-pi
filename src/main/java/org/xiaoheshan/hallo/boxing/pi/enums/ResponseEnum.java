package org.xiaoheshan.hallo.boxing.pi.enums;

import org.springframework.util.Assert;

/**
 * @author : _Chf
 * @since : 03-22-2018
 */
public enum ResponseEnum {
    OK("OK"),
    ERROR("ERROR"),
    ;

    private String name;

    ResponseEnum(String name) {
        this.name = name;
    }

    public String get() {
        return this.name;
    }

    public String getWithParam(String... params) {
        Assert.notNull(params);
        Assert.isTrue(params.length >= 1);
        StringBuilder builder = new StringBuilder(name);
        builder.append('=');
        for (String param : params) {
            builder.append(param).append(',');
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

}
