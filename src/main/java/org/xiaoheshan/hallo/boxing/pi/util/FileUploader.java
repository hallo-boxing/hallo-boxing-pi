package org.xiaoheshan.hallo.boxing.pi.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.stereotype.Component;
import org.xiaoheshan.hallo.boxing.pi.config.ServerProperty;
import org.xiaoheshan.hallo.boxing.pi.enums.ResponseEnum;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author : _Chf
 * @since : 03-22-2018
 */
@Component
public class FileUploader {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploader.class);

    private static final OkHttpClient CLIENT = new OkHttpClient();
    private static final MediaType FILE_MEDIA_TYPE = MediaType.parse("application/octet-stream");
    private static String FILE_UPLOAD_URL;
    private static ServerProperty property;

    @Autowired
    public FileUploader(ServerProperty serverProperty) {
        property = serverProperty;
    }

    @PostConstruct
    private static void init() {
        FILE_UPLOAD_URL = "http://" + property.getIp() + ":8080/hallo-boxing/image/upload";
    }

    public static Integer upload(String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            return null;
        }
        ConsoleUtil.get().println("上传文件：" + file.getName());
        RequestBody fileBody = RequestBody.create(FILE_MEDIA_TYPE, file);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), fileBody)
                .build();
        Response response = CLIENT.newCall(new Request.Builder()
                .url(FILE_UPLOAD_URL)
                .post(requestBody)
                .build())
                .execute();
        if (!response.isSuccessful()) {
            return null;
        }
        JSONObject jsonObject = JSON.parseObject(response.body().string());
        return (Integer) jsonObject.get("data");
    }

}
