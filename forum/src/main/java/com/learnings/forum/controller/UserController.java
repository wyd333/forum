package com.learnings.forum.controller;

import cn.hutool.system.UserInfo;
import com.learnings.forum.common.AppResult;
import com.learnings.forum.common.ResultCode;
import com.learnings.forum.config.AppConfig;
import com.learnings.forum.exception.ApplicationException;
import com.learnings.forum.model.User;
import com.learnings.forum.services.ISessionService;
import com.learnings.forum.services.IUserService;
import com.learnings.forum.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * Description: 用户
 * User: 12569
 * Date: 2023-10-17
 * Time: 22:51
 */

//对controller进行api接口的说明
@Api(tags = "用户接口")
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService userService;
    @Resource
    private ISessionService sessionService;
    private boolean resetPwdFlag;

    @Value("${upload.directory}")
    private String uploadDirectory;


    /**
     * 用户注册
     * @param username 用户名
     * @param nickname 用户昵称
     * @param password 密码
     * @param passwordRepeat 确认密码
     * @return
     */
    @ApiOperation("用户注册")
    @PostMapping("/register")
    public AppResult register(@ApiParam("用户名") @RequestParam("username") @NonNull String username,
                              @ApiParam("昵称") @RequestParam("nickname") @NonNull String nickname,
                              @ApiParam("邮箱") @RequestParam("email") @NonNull String email,
                              @ApiParam("密码") @RequestParam("password") @NonNull String password,
                              @ApiParam("确认密码") @RequestParam("password_repeat") @NonNull String passwordRepeat){
        //1-校验密码与重复密码是否相同
        if(!password.equals(passwordRepeat)) {
            log.warn(ResultCode.FAILED_TWO_PWD_NOT_SAME.toString());
            //返回错误信息
            return AppResult.failed(ResultCode.FAILED_TWO_PWD_NOT_SAME);
        }

        //2-邮箱格式校验
        if(!CheckEmailUtil.validateEmail(email)) {
            //打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            //抛出异常，统一抛出ApplicationException
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        //3-准备数据
        User user = new User();
        user.setUsername(username);
        user.setNickname(nickname);
        user.setEmail(email);
        //处理密码
            //生成盐
        String salt = UUIDUtil.UUID_32();
            //和明文密码合并，生成密码密文
        String encryptPassword = MD5Util.md5Salt(password, salt);
        user.setPassword(encryptPassword);
        user.setSalt(salt);
        user.setAvatarUrl("upload/avatar01.jpeg");

        //4-调用service层处理数据
        userService.createNormalUser(user);

        //5-返回
        return AppResult.success();
    }

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return
     */
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public AppResult login(HttpServletRequest request,
                           @ApiParam("用户名") @RequestParam("username") @NonNull String username,
                           @ApiParam("密码") @RequestParam("password") @NonNull String password) {
        //1-调用service中登录方法，返回User对象
        User user = userService.login(username, password);
        if(user == null) {
            //打印日志
            log.warn(ResultCode.FAILED_LOGIN.toString());
            //返回结果
            return AppResult.failed(ResultCode.FAILED_LOGIN);
        }

        //2-如果登录成功，将User对象设置到Session作用域中
        HttpSession session = request.getSession(true);
        session.setAttribute(AppConfig.USER_SESSION, user);
        //3-返回结果
        return AppResult.success();
    }


    @ApiOperation("获取用户信息")
    @GetMapping("/info")
    public AppResult<User> getUserInfo(HttpServletRequest request,
                                       @ApiParam("用户ID") @RequestParam(value = "id", required = false) Long id){
        //定义返回的user对象
        User user = null;
        //根据入参id的值判断user对象的获取方式
        if(id == null) {
            //如果id为null，则从session中获取当前登录的用户信息
            HttpSession session = request.getSession(false);
            //从session中获取当前登录的用户信息
            user = (User) session.getAttribute(AppConfig.USER_SESSION);
        }else{
            //如果id不为null，则从数据库中查询出用户信息
            user = userService.selectById(id);
        }
        //判断user是否为null 给前端返回有效的数据
        if(user == null) {
            return AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        //正常返回
        return AppResult.success(user);
    }

    @ApiOperation("用户退出登录")
    @GetMapping("/logout")
    public AppResult logout(HttpServletRequest request){
        //获取session对象
        HttpSession session = request.getSession(false);
        //判断session是否有效
        if(session != null) {
            //打印日志
            log.info("退出成功");
            //表示用户在已登录状态，直接销毁session
            session.invalidate();
        }
        return AppResult.success("退出成功");
    }


    @PostMapping("/modifyInfo")
    @ApiOperation("修改个人信息")
    public AppResult modifyInfo(HttpServletRequest request,
                                @ApiParam("用户名") @RequestParam(value = "username",required = false) String username,
                                @ApiParam("昵称") @RequestParam(value = "nickname",required = false) String nickname,
                                @ApiParam("性别") @RequestParam(value = "gender",required = false) Byte gender,
                                @ApiParam("邮箱") @RequestParam(value = "email",required = false) String email,
                                @ApiParam("电话号码") @RequestParam(value = "phoneNum",required = false) String phoneNum,
                                @ApiParam("个人简介") @RequestParam(value = "remark",required = false) String remark){
        //1-接收参数
        //2-非空校验，若全为空则返回错误描述
        if(StringUtil.isEmpty(username)
                && StringUtil.isEmpty(nickname)
                && StringUtil.isEmpty(email)
                && StringUtil.isEmpty(phoneNum)
                && StringUtil.isEmpty(remark)
                && gender == null) {
            //返回错误信息
            return AppResult.failed("请输入要修改的内容！");
        }
        //3-封装对象
        User updateUser = new User();
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        updateUser.setId(user.getId());
        updateUser.setUsername(username);
        updateUser.setNickname(nickname);
        updateUser.setGender(gender);
        updateUser.setEmail(email);
        updateUser.setPhoneNum(phoneNum);
        updateUser.setRemark(remark);

        //4-调用service方法
        userService.modifyInfo(updateUser);

        //5-查询最新的用户信息
        user = userService.selectById(user.getId());

        //6-把最新的用户信息设置到session中
        session.setAttribute(AppConfig.USER_SESSION,user);

        //5-返回结果
        return AppResult.success(user);
    }

    @ApiOperation("发送验证码")
    @PostMapping("/mail")
    public AppResult mail(HttpServletRequest request,
                          @ApiParam("用户名") @RequestParam("username") @NonNull String username,
                          @ApiParam("邮箱") @RequestParam("userEmail") @NonNull String userEmail) throws EmailException {
        //1-非空校验
        //根据用户输入的用户名，查询数据库中该用户信息
        User user = userService.selectByUserName(username);
        if(user == null || user.getDeleteState() == 1) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString() + "inputUsername:" + username);
            return AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE);
        }
        //2-校验邮箱是否存在
        if(StringUtil.isEmpty(user.getEmail())) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE + "用户id" + user.getId() +"未设置邮箱.");
            return AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE);
        }

        //3-判断邮箱是否对应
        if(!user.getEmail().equals(userEmail)) {
            log.warn(ResultCode.FAILED_EMAIL_ERROR + "邮箱错误！");
            return AppResult.failed(ResultCode.FAILED_EMAIL_ERROR);
        }

        //4-记录当前验证码，用于验证
        String captcha = SendEmailUtil.mail(user.getEmail());
        System.out.println("验证码是：" + captcha);
        if(captcha == null) {
            return AppResult.failed(ResultCode.FAILED_CODE_ERROR);
        }

        //5-记录到session中
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        System.out.println(sessionId);
        session.setAttribute(AppConfig.RAW_USERNAME, user.getUsername());
        session.setAttribute(AppConfig.RAW_EMAILCODE, captcha);
        //6-给用户名和验证码设置过期时间
        sessionService.setRawUsernameExpiration(sessionId, user.getUsername());
        sessionService.setRawEmailCodeExpiration(sessionId, captcha);
        resetPwdFlag = true;
        return AppResult.success();
    }

    @ApiOperation("验证邮箱验证码")
    @PostMapping("/email_code")
    public AppResult emailCode(HttpServletRequest request,
            @ApiParam("邮箱验证码") @RequestParam("emailCode") @NonNull String emailCode){
        HttpSession session = request.getSession(false);
        if(session == null) {
            return AppResult.failed(ResultCode.FAILED_EMAILCODE_EXPIRED);
        }

        String rawEmailCode = sessionService.getRawEmailCode(session.getId());
        System.out.println("从session中获取的验证码是：" + rawEmailCode);
        // 校验失败
        if(rawEmailCode == null) {
            return AppResult.failed(ResultCode.FAILED_EMAILCODE_EXPIRED);
        }
        if(!rawEmailCode.equalsIgnoreCase(emailCode)) {
            return AppResult.failed(ResultCode.FAILED_EMAILCODE);
        }
        return AppResult.success();
    }

    @ApiOperation("修改密码")
    @PostMapping("/modifyPwd")
    public AppResult modifyPassword(HttpServletRequest request,
                                    @ApiParam("旧密码") @RequestParam("oldPassword") @NonNull String oldPassword,
                                    @ApiParam("新密码") @RequestParam("newPassword") @NonNull String newPassword,
                                    @ApiParam("确认密码") @RequestParam("passwordRepeat") @NonNull String passwordRepeat){
        //1-校验新密码与确认密码是否相同
        if(!newPassword.equals(passwordRepeat)) {
            return AppResult.failed(ResultCode.FAILED_TWO_PWD_NOT_SAME);
        }
        //2-获取当前登录用户的信息
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        //调用service
        userService.modifyPassword(user.getId(),newPassword,oldPassword);
        if(session != null) {
            session.invalidate();   //让session失效
        }

        //4-返回结果
        return AppResult.success();
    }

    @ApiOperation("重置密码")
    @PostMapping("/resetPwd")
    public AppResult resetPwd(HttpServletRequest request,
                                    @ApiParam("新密码") @RequestParam("newPassword") @NonNull String newPassword,
                                    @ApiParam("确认密码") @RequestParam("passwordRepeat") @NonNull String passwordRepeat){
        if(!resetPwdFlag) {
            return AppResult.failed(ResultCode.FAILED);
        }
        //1-校验新密码与确认密码是否相同
        if(!newPassword.equals(passwordRepeat)) {
            return AppResult.failed(ResultCode.FAILED_TWO_PWD_NOT_SAME);
        }

        //2-从Session中获取正在进行修改密码操作的username
        HttpSession session = request.getSession(false);
        String rawUsername = sessionService.getRawUsername(session.getId());
        if(rawUsername == null) {
            return AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE);
        }

        //3-调用service根据username查找用户
        User user = userService.selectByUserName(rawUsername);
        if(user == null || user.getDeleteState() == 1) {
            return AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE);
        }

        //4-根据用户id和新密码 调用service
        userService.resetPwd(user.getId(), newPassword);

        //4-返回结果
        return AppResult.success();
    }

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
}
