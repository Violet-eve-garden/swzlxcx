package com.sysu.swzl.constant;

import java.io.File;

/**
 * @author 49367
 * @date 2021/5/30 13:17
 */
public class FileConstant {
    /**文件的上传路径*/
    public final static String UPLOAD_PATH = "image" + File.separator + "upload";
    /**前端multipart/form-data上传时使用的文件名*/
    public final static String REQUEST_FILENAME = "file";
    public final static String TOKEN_PREFIX = "token";
    /**上传文件的最大大小为5mb*/
    public final static Long MAX_SIZE = 5L * 1024 * 1024;

    public final static String FILE_TYPE = ".jpg,.jpeg,.png";
}
