# 修改回复表中的非空校验
ALTER TABLE `forum_db`.`t_article_reply`
MODIFY COLUMN `replyId` bigint(20) NULL COMMENT '关联回复编号，支持楼中楼' AFTER `postUserId`,
MODIFY COLUMN `replyUserId` bigint(20) NULL COMMENT '楼主下的回复用戶编号，支持楼中楼' AFTER `replyId`;