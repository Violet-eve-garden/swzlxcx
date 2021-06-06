package com.sysu.swzl.dao;

import com.sysu.swzl.pojo.UploadFile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface UploadFileMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UploadFile record);

    int insertSelective(UploadFile record);

    UploadFile selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UploadFile record);

    int updateByPrimaryKey(UploadFile record);

    //==============自定义部分===============//

    UploadFile selectByName(@Param("name") String filename);
}