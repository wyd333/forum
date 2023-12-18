package com.learnings.forum.services.impl;

import com.learnings.forum.common.AppResult;
import com.learnings.forum.common.ResultCode;
import com.learnings.forum.dao.UserMapper;
import com.learnings.forum.exception.ApplicationException;
import com.learnings.forum.model.User;
import com.learnings.forum.services.IUserService;
import com.learnings.forum.utils.CheckEmailUtil;
import com.learnings.forum.utils.MD5Util;
import com.learnings.forum.utils.StringUtil;
import com.learnings.forum.utils.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Description: 实现UserService
 * User: 12569
 * Date: 2023-10-07
 * Time: 20:38
 */
@Slf4j
@Service
public class UserServiceImpl implements IUserService {
    @Resource
    private UserMapper userMapper;

    /**
     * 注册-普通用户
     * @param user 用户信息
     */
    @Override
    public void createNormalUser(User user) {
        //1-非空校验
        if(user == null || StringUtil.isEmpty(user.getUsername())
                || StringUtil.isEmpty(user.getNickname())
                || StringUtil.isEmpty(user.getEmail())
                || StringUtil.isEmpty(user.getPassword())
                || StringUtil.isEmpty(user.getSalt())) {
            //打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            //抛出异常，统一抛出ApplicationException
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        //2-按用户名查询用户信息
        User existsUser = userMapper.selectByUserName(user.getUsername());
        //2.1-判断用户是否已经存在
        if(existsUser != null) {
            log.warn(ResultCode.AILED_USER_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.AILED_USER_EXISTS));
        }

        //3-新增用户流程，设置default值
        user.setGender((byte) 2);
        user.setArticleCount(0);
        user.setIsAdmin((byte) 0);
        user.setState((byte) 0);
        user.setDeleteState((byte) 0);
        //当前日期
        Date date = new Date();
        user.setCreateTime(date);
        user.setUpdateTime(date);

        // 4-写入数据库
        // 动态插入insertSelective
        int row = userMapper.insertSelective(user);
        if(row != 1) {
            // 打印日志
            log.warn(ResultCode.FAILED_CREATE.toString());
            // 抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_CREATE));
        }

        //打印日志
        log.info("新增用户成功! username = " + user.getUsername() + ".");
    }

    @Override
    public User selectByUserName(String username) {
        //非空校验
        if(StringUtil.isEmpty(username)) {
            // 打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //返回查询的结果
        return userMapper.selectByUserName(username);
    }

    @Override
    public User login(String username, String password) {
        //1-非空校验
        if(StringUtil.isEmpty(username) || StringUtil.isEmpty(password)) {
            // 打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_LOGIN));
        }
        //2-按用户名查询用户信息
        User user = selectByUserName(username);
        //3-对查询结果作非空校验
        if(user == null) {
            // 打印日志
            log.warn(ResultCode.FAILED_LOGIN.toString() + ", username = " + username);
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_LOGIN));
        }
        //4-对密码做校验
        String encryptPassword = MD5Util.md5Salt(password, user.getSalt());
        //5-用密文和数据库中存的用户密码进行比较
        if(!encryptPassword.equalsIgnoreCase(user.getPassword())) {
            // 打印日志
            log.warn(ResultCode.FAILED_LOGIN.toString() + " | 密码错误！username = " + username);
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_LOGIN));
        }
        //6-登录成功，打印日志，返回用户信息
        log.info("登录成功！username = " + username);
        return user;
    }

    @Override
    public User selectById(Long id) {
        //1-非空校验
        if(id == null) {
            // 打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_LOGIN));
        }
        //2-调用dao查询数据库 并获取对象
        User user = userMapper.selectByPrimaryKey(id);
        //3-返回结果
        return user;
    }

    @Override
    public void addOneArticleCountById(Long id) {
        //1-非空校验
        if(id == null || id <= 0) {
            //打印日志
            log.warn(ResultCode.FAILED_USER_ARTICLE_COUNT.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_ARTICLE_COUNT));
        }
        //2-查询用户信息
        User user = userMapper.selectByPrimaryKey(id);
        if(user == null) {
            log.warn(ResultCode.ERROR_IS_NULL.toString() + ", user id = " + id);
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_IS_NULL));
        }
        //3-创建一个用于更新的对象 更新用户的发帖数量
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setArticleCount(user.getArticleCount() + 1);
        //4-更新数据库
        int row = userMapper.updateByPrimaryKeySelective(updateUser);
        if(row != 1) {
            log.warn(ResultCode.FAILED.toString() + ", 受影响的行数 != 1");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }

    @Override
    public void subOneArticleCountById(Long id) {
        //1-非空校验
        if(id == null || id <= 0) {
            //打印日志
            log.warn(ResultCode.FAILED_USER_ARTICLE_COUNT.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_ARTICLE_COUNT));
        }
        //2-查询用户信息
        User user = userMapper.selectByPrimaryKey(id);
        if(user == null) {
            log.warn(ResultCode.ERROR_IS_NULL.toString() + ", user id = " + id);
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_IS_NULL));
        }
        //3-创建一个用于更新的对象 更新用户的发帖数量
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setArticleCount(user.getArticleCount() - 1);
        if(updateUser.getArticleCount() < 0) {
            updateUser.setArticleCount(0);
        }
        //4-更新数据库
        int row = userMapper.updateByPrimaryKeySelective(updateUser);
        if(row != 1) {
            log.warn(ResultCode.FAILED.toString() + ", 受影响的行数 != 1");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }

    @Override
    public void modifyInfo(User user) {
        //1-非空校验
        if(user == null || user.getId() == null || user.getId() <= 0) {
            //打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        //2-校验用户是否存在
        User existsUser = userMapper.selectByPrimaryKey(user.getId());
        if(existsUser == null) {
            //打印日志
            log.warn(ResultCode.FAILED_USER_NOT_EXISTS.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS));
        }

        //3-定义一个标志位用于校验参数
        boolean checkAttr = false;  //false表示没有校验通过

        //4-定义一个专门用来更新的对象，防止传入的对象设置过其他的属性
        //在使用动态sql操作数据库时，覆盖了没有经过校验的字段
        User updateUser = new User();
        //5-设置用户id
        updateUser.setId(user.getId());

        //6-对每一个参数进行校验并赋值
        //校验 username
        if(!StringUtil.isEmpty(user.getUsername())
        && !user.getUsername().equals(existsUser.getUsername())) {
            //需要更新用户名时，进行唯一性校验
            User checkUser = userMapper.selectByUserName(user.getUsername());
            if(checkUser != null) {
                //用户已存在
                log.warn(ResultCode.FAILED_USER_EXISTS.toString());
                //抛出异常
                throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_EXISTS));
            }

            //修改用户名
            updateUser.setUsername(user.getUsername());
            //更新标志位
            checkAttr = true;
        }

        //校验 nickname
        if(!StringUtil.isEmpty(user.getNickname())
                && !user.getNickname().equals(existsUser.getNickname())) {
            //修改昵称
            updateUser.setNickname(user.getNickname());
            //更新标志位
            checkAttr = true;
        }

        //校验 gender
        if(user.getGender() != null
                && !user.getGender().equals(existsUser.getGender())) {
            //修改性别
            updateUser.setGender(user.getGender());
            //合法性校验
            if(updateUser.getGender() > 2 || updateUser.getGender() < 0) {
                updateUser.setGender((byte) 2);
            }
            //更新标志位
            checkAttr = true;
        }

        //校验 email
        if(!StringUtil.isEmpty(user.getEmail())
                && !user.getEmail().equals(existsUser.getEmail())) {
            //修改邮箱
            updateUser.setEmail(user.getEmail());
            //更新标志位
            checkAttr = true;
        }

        //校验 phoneNum
        if(!StringUtil.isEmpty(user.getPhoneNum())
                && !user.getPhoneNum().equals(existsUser.getPhoneNum())) {
            //修改电话号码
            updateUser.setPhoneNum(user.getPhoneNum());
            //更新标志位
            checkAttr = true;
        }
        //校验 remark
        if(!StringUtil.isEmpty(user.getRemark())
                && !user.getRemark().equals(existsUser.getRemark())) {
            //修改个人简介
            updateUser.setRemark(user.getRemark());
            //更新标志位
            checkAttr = true;
        }

        //7-根据标志位来确定是否可以执行更新
        if(!checkAttr) {
            //打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        //8-调用dao
        int row = userMapper.updateByPrimaryKeySelective(updateUser);
        if(row != 1) {
            log.warn(ResultCode.FAILED.toString() + ", 受影响的行数 != 1");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }


    @Override
    public void modifyPassword(Long id, String newPassword, String oldPassword) {
        //1-非空校验
        if(id == null || id <= 0
                || StringUtil.isEmpty(newPassword)
                || StringUtil.isEmpty(oldPassword)) {
            //打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        //2-查询用户信息
        User user = userMapper.selectByPrimaryKey(id);
        if(user == null || user.getDeleteState() == 1) {
            //打印日志
            log.warn(ResultCode.FAILED_USER_NOT_EXISTS.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS));
        }

        //3-校验旧密码是否正确
        //对旧密码加密，获取密文
        String oldEncryptPassword = MD5Util.md5Salt(oldPassword, user.getSalt());
        //和数据库中存的当前密码进行比较
        if(!oldEncryptPassword.equalsIgnoreCase(user.getPassword())) {
            //打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));

        }

        //4-生成新的盐值
        String salt = UUIDUtil.UUID_32();
        //5-生成新密码的密文
        String encryptPassword = MD5Util.md5Salt(newPassword, salt);

        //6-构造要更新的对象
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setSalt(salt);
        updateUser.setPassword(encryptPassword);
        updateUser.setUpdateTime(new Date());

        //7-调用dao
        int row = userMapper.updateByPrimaryKeySelective(updateUser);
        if(row != 1) {
            log.warn(ResultCode.FAILED.toString() + ", 受影响的行数 != 1");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }


    @Override
    public void resetPwd(Long id, String newPassword) {
        //1-非空校验
        if(id == null || id <= 0 || StringUtil.isEmpty(newPassword)) {
            //打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        //2-查询用户信息
        User user = userMapper.selectByPrimaryKey(id);
        if(user == null || user.getDeleteState() == 1) {
            //打印日志
            log.warn(ResultCode.FAILED_USER_NOT_EXISTS.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS));
        }

        //3-生成新的盐值
        String salt = UUIDUtil.UUID_32();
        //4-生成新密码的密文
        String encryptPassword = MD5Util.md5Salt(newPassword, salt);

        //5-构造要更新的对象
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setSalt(salt);
        updateUser.setPassword(encryptPassword);
        updateUser.setUpdateTime(new Date());

        //6-调用dao
        int row = userMapper.updateByPrimaryKeySelective(updateUser);
        if(row != 1) {
            log.warn(ResultCode.FAILED.toString() + ", 受影响的行数 != 1");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }

    @Override
    public void modifyAvatar(Long id, String relativePathUrl) {
        //1-非空校验
        if(id == null || id <= 0 || StringUtil.isEmpty(relativePathUrl)) {
            //打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        //2-查询用户信息
        User user = userMapper.selectByPrimaryKey(id);
        if(user == null || user.getDeleteState() == 1) {
            //打印日志
            log.warn(ResultCode.FAILED_USER_NOT_EXISTS.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS));
        }

        //3-封装待更新的数据
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setAvatarUrl(relativePathUrl);

        //4-调用dao层
        int row = userMapper.updateByPrimaryKeySelective(updateUser);
        if(row != 1) {
            log.warn(ResultCode.FAILED.toString() + ", 受影响的行数 != 1");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }

    @Override
    public Integer selectArticleCountById(Long id) {
        //1-非空校验
        if(id == null || id <= 0) {
            //打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        //2-查询信息
        Integer articleCount = userMapper.selectArticleCountById(id);
        if(articleCount == null) {
            //打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        return articleCount;
    }

    @Override
    public Integer selectAllLikesCountById(Long id) {
        //1-非空校验
        if(id == null || id <= 0) {
            //打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        //2-查询信息
        Integer likeCount = userMapper.selectAllLikesCountById(id);
        if(likeCount == null) {
            //打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        return likeCount;
    }
}
