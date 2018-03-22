package org.xiaoheshan.hallo.boxing.pi.looper.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.xiaoheshan.hallo.boxing.pi.io.ConsoleHolder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.zip.Deflater;

/**
 * @author : _Chf
 * @since : 03-20-2018
 */
@Component
public class CameraExecutor implements IExecutor<byte[]> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CameraExecutor.class);

    private final ConsoleHolder console;

    private static final String TAKE_PHOTO_COMMAND_PREFIX = "fswebcam -d /dev/video0 --no-banner -r 320x240 ";
    private static final String PHOTO_DIRECTORY = "/home/pi/hallo/";
    private static final String JPG_SUFFIX = ".jpg";

    private ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);

    @Autowired
    public CameraExecutor(ConsoleHolder console) {
        this.console = console;
    }

    @Override
    public byte[] execute(String... parameters) {
        Assert.notNull(parameters, "参数不能为null");
        Assert.isTrue(parameters.length == 1, "参数只有一个");
        console.println("正在执行拍摄命令: " + Arrays.toString(parameters));
        LOGGER.info("正在执行拍摄命令: " + Arrays.toString(parameters));
        String name = parameters[0];
        try {
            takePhoto(name);
            console.println("完成执行拍摄命令");
            LOGGER.info("完成执行拍摄命令");
            byte[] photoBytes = getPhotoBytes(name);
            for (int i = 0; i < photoBytes.length; i++) {
                if (photoBytes[i] == 0x1A) {
                    photoBytes[i] = 0x00;
                }
            }
            console.println("图片大小：" + photoBytes.length);
            return photoBytes;
        } catch (IOException | InterruptedException e) {
            LOGGER.error("拍摄照片发送错误", e);
            return null;
        }
    }

    private void takePhoto(String name) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(TAKE_PHOTO_COMMAND_PREFIX + PHOTO_DIRECTORY + name + JPG_SUFFIX);
        process.waitFor();
    }

    private byte[] getPhotoBytes(String name) throws IOException {
        Path path = Paths.get(PHOTO_DIRECTORY + name + JPG_SUFFIX);
        FileChannel channel = FileChannel.open(path, StandardOpenOption.READ);
        int size = channel.read(buffer);
        byte[] bytes = new byte[size];
        buffer.flip();
        buffer.get(bytes);
        bytes = compress(bytes);
        buffer.clear();
        return bytes;
    }

    private byte[] compress(byte input[]) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Deflater compressor = new Deflater(1);
        try {
            compressor.setInput(input);
            compressor.finish();
            final byte[] buf = new byte[2048];
            while (!compressor.finished()) {
                int count = compressor.deflate(buf);
                bos.write(buf, 0, count);
            }
        } finally {
            compressor.end();
        }
        return bos.toByteArray();
    }

}
