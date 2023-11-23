package com.learnings.forum.services.impl;

import com.learnings.forum.common.AppResult;
import com.learnings.forum.common.ResultCode;
import com.learnings.forum.dao.ArticleReplyMapper;
import com.learnings.forum.exception.ApplicationException;
import com.learnings.forum.model.ArticleReply;
import com.learnings.forum.services.IArticleReplyService;
import com.learnings.forum.services.IArticleService;
import com.learnings.forum.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:实现IArticleReplyService
 * User: 12569
 * Date: 2023-11-20
 * Time: 21:11
 */

@Slf4j
@Service
public class ArticleReplyServiceImpl implements IArticleReplyService {
    @Resource
    private ArticleReplyMapper articleReplyMapper;
    @Resource
    private IArticleService articleService;
    @Override
    public void create(ArticleReply articleReply) {
        //1-非空校验
        if(articleReply == null || articleReply.getArticleId() == null
            || articleReply.getPostUserId() == null
                || StringUtil.isEmpty(articleReply.getContent())) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //2-设置默认值
        articleReply.setReplyId(null);
        articleReply.setReplyUserId(null);
        articleReply.setLikeCount(0);
        articleReply.setState((byte) 0);
        articleReply.setDeleteState((byte) 0);
        Date date = new Date();
        articleReply.setUpdateTime(date);
        articleReply.setCreateTime(date);
        //3-写入数据库
        int row = articleReplyMapper.insertSelective(articleReply);
        if(row != 1) {
            log.warn(ResultCode.ERROR_SERVICES.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }

        //4-更新帖子表中的回复数
        articleService.addOneReplyCountById(articleReply.getArticleId());
        log.info("帖子回复成功，article Id = " + articleReply.getArticleId() + ",user Id = " + articleReply.getPostUserId());
    }

    @Override
    public List<ArticleReply> selectByArticleId(Long articleId) {
        //1-非空校验
        if(articleId == null || articleId <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //2-调用dao
        List<ArticleReply> result = articleReplyMapper.selectByArticleId(articleId);

        return result;
    }
}
