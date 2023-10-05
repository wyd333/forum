package com.learnings.forum.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created with IntelliJ IDEA.
 * Description: 配置MyBatis的扫描路径
 * User: 12569
 * Date: 2023-10-05
 * Time: 22:31
 */

//加入Spring
@Configuration
//具体配置
@MapperScan("com.learnings.forum.dao")
public class MybatisConfig {
}
