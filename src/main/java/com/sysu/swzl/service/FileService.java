package com.sysu.swzl.service;

import com.sysu.swzl.common.R;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 49367
 * @date 2021/5/30 13:20
 */
public interface FileService {
    /**
     * 保存文件
     * @param file
     * @return 返回文件名
     */
    R saveImg(MultipartFile file);
}
