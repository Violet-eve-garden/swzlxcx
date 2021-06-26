package com.sysu.swzl.service.Impl;

import com.sysu.swzl.common.R;
import com.sysu.swzl.constant.FileConstant;
import com.sysu.swzl.dao.UploadFileMapper;
import com.sysu.swzl.exception.BizCodeException;
import com.sysu.swzl.pojo.UploadFile;
import com.sysu.swzl.service.FileService;
import com.sysu.swzl.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Date;
import java.util.UUID;

/**
 * @author 49367
 * @date 2021/5/30 13:20
 */
@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Autowired
    private UploadFileMapper uploadFileMapper;

    /**
     * 返回空自负床表示文件保存失败
     * @param file
     * @return
     */
    @Override
    @Transactional
    public R saveImg(MultipartFile file) {
        File upload = new File(FileConstant.UPLOAD_PATH);
        if(!upload.exists()) {
           if (!upload.mkdirs())
                return R.error("内部服务错误");
        }
        String filename = file.getOriginalFilename();
        if (!FileUtil.isUploadTypeByFilename(filename))
            return R.error("文件名错误");
        if (file.getSize() > FileConstant.MAX_SIZE)
            return R.error("文件过大");
        String uuid = UUID.randomUUID().toString();

        log.info("upload url:" + upload.getAbsolutePath());

        // 最终文件名
        String finalName = uuid + filename;
        log.info("final filename: " + finalName);
        log.info("file path: " + upload + File.separator + finalName);
        File saveFile = new File(upload + File.separator + finalName);

        byte[] buffer = new byte[2048];
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(file.getInputStream() , buffer.length);
            bos = new BufferedOutputStream(new FileOutputStream(saveFile), buffer.length);
            int size;
            while ((size = bis.read(buffer)) != -1){
                bos.write(buffer, 0, size);
            }
            bos.flush();

            // 保存信息到数据库
            UploadFile uploadFile = new UploadFile();
            uploadFile.setFileName(finalName);
            uploadFile.setFilePath(upload.getAbsolutePath() + File.separator + finalName);
            uploadFile.setFileSize((int) file.getSize());
            uploadFile.setUploadTime(new Date());
            uploadFileMapper.insertSelective(uploadFile);

            return R.ok().put("filename", finalName);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("文件" + finalName + "保存失败");
            // 回滚事务
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return R.error(BizCodeException.FILE_UPLOAD_EXCEPTION.getCode(), BizCodeException.FILE_UPLOAD_EXCEPTION.getMessage());
        } finally {
            try {
                if(bis != null){
                    bis.close();
                }
                if(bos != null){
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
