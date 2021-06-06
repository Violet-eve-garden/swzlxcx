package com.sysu.swzl.dao;

import com.sysu.swzl.pojo.GoodsMessage;
import com.sysu.swzl.vo.GoodsMessageRespVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface GoodsMessageMapper {
    int deleteByPrimaryKey(Long id);

    int insert(GoodsMessage record);

    int insertSelective(GoodsMessage record);

    GoodsMessage selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GoodsMessage record);

    int updateByPrimaryKey(GoodsMessage record);

    //==============自定义部分===============//
    List<GoodsMessage> selectGoodsMessageInTimeOrder(@Param("num") int num, @Param("order") boolean isAsc);

    List<GoodsMessage> selectByType(@Param("type") String type);
}