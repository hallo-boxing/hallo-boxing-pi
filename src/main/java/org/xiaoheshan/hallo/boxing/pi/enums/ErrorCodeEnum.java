package org.xiaoheshan.hallo.boxing.pi.enums;

/**
 * @author : _Chf
 * @since : 03-22-2018
 */
public enum ErrorCodeEnum {
    SYSTEM_ERROR("系统错误", -1),
    DOOR_PARAM_ERROR("柜门参数错误", 100),
    CAMERA_PARAM_ERROR("拍摄参数错误", 200),
    CAMERA_UPLOAD_ERROR("拍摄上传错误", 201),
    CHECKOUT_NONE_ERROR("没有检测到卡错误", 300),
    ;

    private String name;
    private Integer code;

    ErrorCodeEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public Integer getCode() {
        return code;
    }
}
