# FoxForum论坛系统
基于 Spring 的论坛系统-前后端分离，项目线上访问地址：http://124.223.218.170:21250/sign-in.html

项目开发时遵循软件生命周期约定的软件开发的基本流程，包括可行性分析、需求分析、概要设计、详细设计、实现、测试、部署上线与运行维护。

## 一、简介

本项目为个人B/S项目，是一个基于Java语言实现的论坛系统，实现并完善了论坛系统的业务功能，包括：

1. 普通用户注册，登录。
2. 按板块分类展示板块内的帖子信息（包括帖子列表、帖子总数等）以及帖子详情内容。
3. 用户发布，删除，回复帖子。
4. 用户给帖子点赞。
5. 用户评论帖子。
6. 查询任一用户的个人信息。
7. 支持个人主页的展示，编辑。
8. 支持头像上传。
9. 支持站内私信（包括收发私信并自动标记是否已读）。
10. 管理员可以添加/删除/修改版块。（待实现）
11. 管理员可以管理所有帖子。（待实现）
12. 支持发布图片表情。（待实现）

该项目开发使用到的技术栈包括：Spring Boot + Spring MVC + MyBatis + MySQL + Redis +（ HTML + CSS + JS + JQuery + Bootstrap）。

**项目亮点**：

1. 实现了更安全的加密算法。 
2. 采用axios请求统一处理。 
3. 引入Redis，改造了 Session 默认存储内存，存储到Redis。 
4. 复杂查询使用多线程实现，并发编程效率更高。 
5. 原生代码实现分页查询功能。 
6. 使用自定义的拦截器进行登录验证。 
7. 实现图片资源上传。 
8. 通过smtp协议实现发送邮箱验证码。 

## 二、技术选型

| 类别               | 描述                                                      |
| ------------------ | --------------------------------------------------------- |
| 架构               | 基于MVC构架，实现前后端分离                               |
| 编码格式           | UTF-8                                                     |
| 前后端交互数据格式 | JSON                                                      |
| JDK版本            | JDK 1.8                                                   |
| 服务器端技术       | SpringBoot 2.7.6<br />Spring MVC<br />MyBaits Start 2.3.0 |
| 浏览器端技术       | HTML, CSS, JavaScript<br />jQuery 3<br />Bootstrap        |
| 数据库             | MySQL 5.7                                                 |
| 项目构建工具       | Maven 3.6.3                                               |
| 版本控制工具       | Git 2.33.1 + GITHUB                                       |
| 开发工具           | IntelliJ IDEA Utimate 2022                                |
| API文档生成工具    | Swagger<br />Springfox 3.0.0                              |
| 单元测试工具       | Junit                                                     |
| 接口测试工具       | Postman                                                   |
| 自动化测试工具     | Chrome 19<br />Selenium                                   |

## 三、部分项目亮点展示

### 1、复杂查询使用多线程实现

定义线程池配置ThreadPoolConfig：

```java
package com.learnings.forum.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Created with IntelliJ IDEA.
 * Description: 线程池
 * User: 12569
 * Date: 2023-12-17
 * Time: 11:04
 */
@Configuration
public class ThreadPoolConfig {
    @Bean
    public ThreadPoolTaskExecutor taskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(10000);
        executor.setThreadNamePrefix("MyThread-");
        executor.initialize();
        return executor;
    }
}
```

以查询板块内帖子列表为例，利用并发的思路让查询板块下帖子总数与分页查询板块列表同时进行（仅展示并发编程的部分代码）：

```java
public AppResult<HashMap<String, Object>> getListByBoardIdWithPsize(
    @ApiParam("板块id") @RequestParam(value = "boardId", required = false) Long boardId,
    @ApiParam("每页帖子数") @RequestParam(value = "psize", required = false) Integer psize,
    @ApiParam("当前页码") @RequestParam(value = "pindex", required = false) Integer pindex) 
throws ExecutionException, InterruptedException {
        // 1-定义返回的结果集合和总页数
		// ...
        
        // 2-参数校正
		// ...
        
        // 3-调用service
        // 没有传入boardId时
        if(boardId == null) {   //查询所有
            // *帖子列表分页查询
            FutureTask<List<Article>> articleTask = new FutureTask<>(() -> {
                return articleService.selectAllWithPsize(finalPSize, offset);
            });
            taskExecutor.submit(articleTask);

            // *帖子总页数查询
            FutureTask<Integer> pageTask = new FutureTask<>(() -> {
                // 帖子总数
                int totalCount = articleService.selectAll().size();
                return (int) Math.ceil(totalCount * 1.0 / finalPSize);
            });
            taskExecutor.submit(pageTask);

            // *获得结果
            articles = articleTask.get(); //分页查询出的帖子列表
            size = pageTask.get();  //帖子总页数
        } else {    //传入boardId时
            // *帖子列表分页查询
            FutureTask<List<Article>> articleTask = new FutureTask<>(() -> {
                return articleService.selectAllByBoardIdWithPsize(boardId, finalPSize, offset);
            });
            taskExecutor.submit(articleTask);

            // *帖子总页数查询
            FutureTask<Integer> pageTask = new FutureTask<>(() -> {
                // 帖子总数
                int totalCount = articleService.selectAllByBoardId(boardId).size();
                return (int) Math.ceil(totalCount * 1.0 / finalPSize);
            });
            taskExecutor.submit(pageTask);

            // *获取结果
            articles = articleTask.get(); //分页查询出的帖子列表
            size = pageTask.get();  //帖子总页数
        }

        //如果查询结果集为空，则创建一个空集合
    	// ...


        // 4-组装数据
    	// ...

        // 5-响应结果
        return AppResult.success(listAndSizeMap);
    }
```

### 2、原生代码实现分页查询帖子列表功能

ArticleController层，getListByBoardIdWithPsize()方法：

```java
/**
     * 分页查询版-获取帖子列表
     * 如果传入了boardId，则按boardId号查找
     * 如果没有传入则查询全部
     * @param boardId
     * @return
     */
@ApiOperation("分页查询获取帖子列表")
@GetMapping("/get_list_by_boardId_psize")
public AppResult<HashMap<String, Object>> getListByBoardIdWithPsize(
    @ApiParam("板块id") @RequestParam(value = "boardId", required = false) Long boardId,
    @ApiParam("每页帖子数") @RequestParam(value = "psize", required = false) Integer psize,
    @ApiParam("当前页码") @RequestParam(value = "pindex", required = false) Integer pindex)
throws ExecutionException, InterruptedException {
        // 1-定义返回的结果集合和总页数
        List<Article> articles;
        Integer size;
    
        // 2-参数校正
        if(psize == null || psize <= 0) {
            psize = 10;
        }
        if(pindex == null || pindex <= 0) {
            pindex = 1;     //默认首页
        }

        // 3-调用service
        Integer finalPSize = psize;
        int offset = psize *(pindex -1);    //分页公式

        // 没有传入boardId时
        if(boardId == null) {   //查询所有
            // *帖子列表分页查询
            FutureTask<List<Article>> articleTask = new FutureTask<>(() -> {
                return articleService.selectAllWithPsize(finalPSize, offset);
            });
            taskExecutor.submit(articleTask);

            // *帖子总页数查询
            // ...
            
            // *获得结果
            articles = articleTask.get(); //分页查询出的帖子列表
            size = pageTask.get();  //帖子总页数
        } else {    //传入boardId时
            // *帖子列表分页查询
            FutureTask<List<Article>> articleTask = new FutureTask<>(() -> {
                return articleService.selectAllByBoardIdWithPsize(boardId, finalPSize, offset);
            });
            taskExecutor.submit(articleTask);

            // *帖子总页数查询
            // ...

            // *获取结果
            articles = articleTask.get(); //分页查询出的帖子列表
            size = pageTask.get();  //帖子总页数
        }

        //如果结果集为空，创建一个空集合
        if(articles == null) {
            articles = new ArrayList<>();
        }

        // 4-组装数据
        HashMap<String, Object> listAndSizeMap = new HashMap<>();
        listAndSizeMap.put("articles", articles);
        listAndSizeMap.put("size", size);

        //响应结果
        return AppResult.success(listAndSizeMap);
    }
```

ArticleServiceImpl层，selectAllWithPsize方法：

```java
@Override
public List<Article> selectAllWithPsize(Integer psize, Integer offset) {
    // 调用dao
    return articleMapper.selectAllWithPsize(psize, offset);
}
```

ArticleMapper层，接口与xml实现：

```java
List<Article> selectAllWithPsize(@Param("psize") Integer psize, @Param("offset") Integer offset);
```

```xml
<!--    分页查询版-查询所有未被删除的帖子列表，不包含content-->
<select id="selectAllWithPsize" resultMap="AllInfoResultMap" parameterType="java.util.Map">
    select
    u.id as u_id,
    u.avatarUrl as u_avatarUrl,
    u.nickname as u_nickname,
    a.id,
    a.boardId,
    a.userId,
    a.title,
    a.visitCount,
    a.replyCount,
    a.likeCount,
    a.state,
    a.createTime,
    a.updateTime
    from t_article a, t_user u
    where a.userId = u.id
    and a.deleteState = 0
    ORDER BY a.createTime DESC LIMIT #{psize, jdbcType=BIGINT} OFFSET #{offset, jdbcType=BIGINT};
</select>
```

### 3、使用自定义的拦截器进行登录验证

拦截器配置：

```java
package com.learnings.forum.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * Description: 全局登录拦截器
 * User: 12569
 * Date: 2023-10-28
 * Time: 21:35
 */

//配置类
@Configuration
public class AppInterceptorConfigurer implements WebMvcConfigurer {
    //注入自定义的登录拦截器
    @Resource
    private LoginInterceptor loginInterceptor;

    //设置拦截规则
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加登录拦截器
        registry.addInterceptor(loginInterceptor) // 添加用户登录拦截器
        .addPathPatterns("/**") // 拦截所有请求
        .excludePathPatterns("/user/login") // 排除登录api接⼝
        .excludePathPatterns("/user/register") // 排除注册api接⼝
        .excludePathPatterns("/user/logout") // 排除退出api接⼝
        .excludePathPatterns("/user/mail") // 排除发送邮箱验证码接口
        .excludePathPatterns("/user/email_code") // 排除验证邮箱验证码接口
        .excludePathPatterns("/user/resetPwd") // 排除重置密码接口
        .excludePathPatterns("/swagger*/**") // 排除登录swagger下所有
        .excludePathPatterns("/v3*/**") // 排除登录v3下所有，与swagger相关
        .excludePathPatterns("/dist/**") // 排除所有静态文件
        .excludePathPatterns("/image/**")
        .excludePathPatterns("/**.ico")
        .excludePathPatterns("/js/**");
    }
}
```

登录拦截器：

```java
package com.learnings.forum.interceptor;

import com.learnings.forum.config.AppConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description: 登录拦截器
 * User: 12569
 * Date: 2023-10-28
 * Time: 21:16
 */

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Value("${fox-forum.login.url}")
    private String defaultURL;
    // 添加允许访问的页面
    private final List<String> allowedURLs = Arrays.asList("/sign-in.html","/sign-up.html",
            "/return.html", "/mailBack.html","/reset_pwd.html");
    /**
     * 前置处理：对请求的预处理
     * @return true ：继续流程 <br/> false : 流程中断
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求的URI
        String requestURI = request.getRequestURI();

        // 如果请求的是允许访问的页面，则直接放行
        if (allowedURLs.contains(requestURI)) {
            return true;
        }

        //获取session对象
        HttpSession session = request.getSession(false);
        //判断session是否有效
        if(session != null && session.getAttribute(AppConfig.USER_SESSION) != null) {
            //用户为已登录状态，校验通过
            return true;
        }
        //校验URL是否正确
        if(!defaultURL.startsWith("/")) {
            defaultURL = "/" + defaultURL;
        }
        //校验不通过，跳转到登录界面
        response.sendRedirect(defaultURL);
        //中断流程
        return false;
    }
}
```

### 4、实现图片资源上传

```java
/**
     * 修改用户头像
     * @param request
     * @param photo
     * @return
     */

@ApiOperation("修改用户头像")
@PostMapping("/sub_photo")
public AppResult updatePhoto(HttpServletRequest request,
                             @ApiParam("图片参数") @NonNull @RequestPart("myphoto") MultipartFile photo) throws IOException {

    // 1-要上传的文件名
    String originalFileName = photo.getOriginalFilename();
    if(originalFileName == null) {
        return AppResult.failed(ResultCode.ERROR_PHOTO);
    }

    // 2-获取用户信息(1.删除旧头像需要原来的旧头像的路径 2.修改图片需要用户 id)
    HttpSession session = request.getSession(false);
    if(session == null) {
        log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
        return AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE);
    }

    User user = (User) session.getAttribute(AppConfig.USER_SESSION);
    if(user == null || user.getDeleteState() == 1) {
        return AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS);
    }

    // 3-获取文件后缀
    String suffix = originalFileName.substring(originalFileName.lastIndexOf("."));
    // 4-生成图片名称，使用 UUID 避免相同图片名冲突，加上图片后缀
    String photoName = UUIDUtil.UUID_32() + suffix;

    // 5-图片保存路径
    String relativePathUrl = uploadDirectory + photoName;
    String absolutePathUrl = AppConfig.ABSOLUTE_PATH + photoName;
    Path filePath = Paths.get(absolutePathUrl);

    // 6-不是修改默认头像的话就将旧头像删除即可
    if(!StringUtil.isEmpty(user.getAvatarUrl()) && !user.getAvatarUrl().equals("upload/avatar01.jpeg")) {
        String oldAvatarPath = AppConfig.ABSOLUTE_PATH + user.getAvatarUrl().split("/")[1];
        File oldAvatar = new File(oldAvatarPath);
        boolean b = oldAvatar.delete();
        System.out.println(b);
    }

    // 7-生成文件
    //将上传文件绝对路径保存到服务器文件系统
    Files.write(filePath, photo.getBytes());

    //保存图片相对路径到数据库中
    userService.modifyAvatar(user.getId(), relativePathUrl);

    user.setAvatarUrl(relativePathUrl);
    user.setUpdateTime(new Date());
    session.setAttribute(AppConfig.USER_SESSION, user);

    System.out.println("新头像的地址：" + user.getAvatarUrl());
    //将图片相对路径返回给前端
    return AppResult.success(user);
}
```



### 5、通过smtp协议实现发送邮箱验证码。 

邮箱格式判断：

```java
public static Boolean validateEmail(String email) {
    // 邮箱验证的正则表达式
    String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    // 编译正则表达式
    Pattern pattern = Pattern.compile(regex);

    // 匹配邮箱
    Matcher matcher = pattern.matcher(email);

    // 如果不匹配，则返回false
    return matcher.matches();
}
```

验证码生成工具：

```java
/**
 * Created with IntelliJ IDEA.
 * Description: 验证码工具
 * User: 12569
 * Date: 2023-12-02
 * Time: 14:16
 */
public class CodeUtil {
    public static final int CODE_LENGTH = 6;

    /**
     * 生成 CODE_LENGTH 长度的随机验证码
     * @return 生成的验证码
     */
    public String createCode() {
        String charset = "0123456789"; // 可以包含在验证码中的字符集合
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            int randomIndex = random.nextInt(charset.length());
            code.append(charset.charAt(randomIndex));
        }
        return code.toString();
    }
}
```

发送邮件：

```java
package com.learnings.forum.utils;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

/**
 * Created with IntelliJ IDEA.
 * Description: 邮箱发送验证码
 * User: 12569
 * Date: 2023-12-02
 * Time: 14:25
 */
public class SendEmailUtil {

    /**
     * 发送邮箱验证码的实现
     * @return 邮箱验证码
     * @throws EmailException
     */
    public static String mail(String Email) throws EmailException {
        CodeUtil codeUtil = new CodeUtil();
        // 1-创建一个HtmlEmail实例对象
        HtmlEmail email = new HtmlEmail();
        // 2-设置邮箱的SMTP服务器
        email.setHostName("smtp.163.com");
        // 3-设置发送的字符类型
        email.setCharset("utf-8");
        // 4-设置收件人
        email.addTo(Email);
        // 5-发送人的邮箱为自己的，用户名可以随便填
        email.setFrom("yidan8080@163.com","FoxForum");
        // 6-设置发送人的邮箱和用户名和授权码(授权码是自己设置的)
        email.setAuthentication("yidan8080@163.com","DQBBNQUDJTEFGAAH");
        // 7-设置发送主题
        email.setSubject("找回密码验证");
        String Captcha = codeUtil.createCode();
        // 8-设置发送内容
        email.setMsg("[FoxForum-技术论坛] 您正在找回密码，验证码为："+String.valueOf(Captcha)+"\n" +
                "验证码有效期为 5 分钟，请及时填写！如非本人操作请忽略。");
        // 9-进行发送
        email.send();

        return Captcha;
    }
}
```



## 四、联系作者

QQ账号：1256970054

邮箱：1256970054@qq.com