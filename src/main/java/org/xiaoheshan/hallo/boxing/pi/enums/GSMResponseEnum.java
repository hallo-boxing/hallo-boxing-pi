package org.xiaoheshan.hallo.boxing.pi.enums;

/**
 * @author : _Chf
 * @since : 03-16-2018
 */
public enum GSMResponseEnum {
    OK("OK"),
    ERROR("ERROR"),
    SEND_TCP_READY(">"),
    TCP_RECEIVE_PREFIX("+CIPRCV:")
    ;

    private String name;

    GSMResponseEnum(String name) {
        this.name = name;
    }

    public boolean is(String other) {
        return other.trim().toUpperCase().contains(name);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
