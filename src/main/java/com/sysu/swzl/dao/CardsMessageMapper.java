package com.sysu.swzl.dao;

import com.sysu.swzl.pojo.CardsMessage;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface CardsMessageMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CardsMessage record);

    int insertSelective(CardsMessage record);

    CardsMessage selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CardsMessage record);

    int updateByPrimaryKey(CardsMessage record);
}