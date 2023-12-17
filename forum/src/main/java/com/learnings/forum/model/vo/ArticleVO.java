package com.learnings.forum.model.vo;

import com.learnings.forum.model.Article;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description: Article的扩展类，包含作者的点赞总数和总阅读量
 * 用于在查询帖子详情页时返回给前端
 * User: 12569
 * Date: 2023-12-17
 * Time: 12:38
 */

@Data
public class ArticleVO {
    private int likeCount;
    private int articleCount;
    private Article article;

    // 构造函数
    public ArticleVO(int likeCount, int articleCount, Article article) {
        this.likeCount = likeCount;
        this.articleCount = articleCount;
        this.article = article;
    }
}
