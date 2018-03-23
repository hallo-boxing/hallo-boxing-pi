package org.xiaoheshan.hallo.boxing.pi.util;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @author : _Chf
 * @since : 03-23-2018
 */
public class FileUploaderTest {

    @Test
    public void upload() throws IOException {

        FileUploader.upload("F:\\颜色提取器.exe");

    }
}