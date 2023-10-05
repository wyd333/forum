package com.learnings.forum.dao;

import com.learnings.forum.model.Article;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleMapper {
    int insert(Article row);

    int insertSelective(Article row);

    Article selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Article row);

    int updateByPrimaryKeyWithBLOBs(Article row);

    int updateByPrimaryKey(Article row);
}