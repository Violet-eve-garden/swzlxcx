package com.sysu.swzl.dao;

import com.sysu.swzl.pojo.WxUserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface WxUserInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(WxUserInfo record);

    int insertSelective(WxUserInfo record);

    WxUserInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(WxUserInfo record);

    int updateByPrimaryKey(WxUserInfo record);

    //===================自定义部分=========================//

    WxUserInfo selectByOpenId(@Param("openId") String openId);

    int updateByOpenIdSelective(WxUserInfo record);
}