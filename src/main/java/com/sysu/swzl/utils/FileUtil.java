package com.sysu.swzl.utils;

import com.sysu.swzl.constant.FileConstant;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author 49367
 * @date 2021/5/30 13:30
 */
public class FileUtil {

    /**
     * 获取文件类型
     * @param filename
     * @return 返回空字符串表示获取失败，否则返回文件类型 ，格式为".jpg"这种格式
     */
    public static String getTypeFromFilename(String filename){
        if (!StringUtils.hasText(filename))
            return "";
        String[] split = filename.split("\\.");

        if (split.length <= 0)
            return "";

        return "." + split[split.length - 1];
    }

    /**
     * 判断是否是允许的上传类型
     * @param fileType
     * @return
     */
    public static boolean isUploadTypeByType(String fileType){
        String[] types = FileConstant.FILE_TYPE.split(",");

        Set<String> typeSet = new HashSet<>(Arrays.asList(types));

        return typeSet.contains(fileType.toLowerCase());
    }

    /**
     * 判断是否是允许的上传类型
     * @param filename
     * @return
     */
    public static boolean isUploadTypeByFilename(String filename){
        String type = getTypeFromFilename(filename);
        return isUploadTypeByType(type);
    }
}
