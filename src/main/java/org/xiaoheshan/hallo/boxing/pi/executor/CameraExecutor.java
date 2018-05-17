package org.xiaoheshan.hallo.boxing.pi.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xiaoheshan.hallo.boxing.pi.util.ConsoleUtil;
import org.xiaoheshan.hallo.boxing.pi.util.FileUploader;

import java.io.IOException;
import java.util.Arrays;

import static org.xiaoheshan.hallo.boxing.pi.enums.ErrorCodeEnum.*;
import static org.xiaoheshan.hallo.boxing.pi.enums.ResponseEnum.ERROR;
import static org.xiaoheshan.hallo.boxing.pi.enums.ResponseEnum.OK;

/**
 * @author : _Chf
 * @since : 03-22-2018
 */
public class CameraExecutor implements IExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CameraExecutor.class);

    private static final String TAKE_PHOTO_COMMAND_PREFIX = "fswebcam -d /dev/video0 --no-banner -r 320x240 ";
    private static final String PHOTO_DIRECTORY = "/home/pi/hallo/";
    private static final String JPG_SUFFIX = ".jpg";

    @Override
    public String execute(String... params) {
        if (params == null || params.length != 1) {
            LOGGER.error(CAMERA_PARAM_ERROR.getName());
            return ERROR.getWithParam(CAMERA_PARAM_ERROR.getCode().toString());
        }
        ConsoleUtil.get().println("正在执行拍摄命令: " + Arrays.toString(params));
        Integer imageId = null;
        try {
            String fileName = PHOTO_DIRECTORY + params[0] + JPG_SUFFIX;
            takePhoto(fileName);
            imageId = FileUploader.upload(fileName);
            if (imageId == null) {
                ConsoleUtil.get().println("上传照片发生错误");
                return ERROR.getWithParam(CAMERA_UPLOAD_ERROR.getCode().toString());
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.error("执行拍摄命令发生错误", e);
            return ERROR.getWithParam(SYSTEM_ERROR.getCode().toString());
        }
        ConsoleUtil.get().println("执行拍摄命令完成");
        return OK.getWithParam(imageId.toString());
    }

    private void takePhoto(String fileName) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(TAKE_PHOTO_COMMAND_PREFIX + fileName);
        process.waitFor();
    }
}
