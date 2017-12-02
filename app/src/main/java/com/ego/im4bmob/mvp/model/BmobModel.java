package com.ego.im4bmob.mvp.model;


import java.util.List;

import com.ego.im4bmob.bean.User;
import com.ego.im4bmob.mvp.bean.Comment;
import com.ego.im4bmob.mvp.bean.Love;
import com.ego.im4bmob.mvp.bean.Post;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created on 17/7/5 09:36
 */

public class BmobModel {

    public BmobModel() {

    }


    /**
     * 登录
     *
     * @param account  账号/手机密码
     * @param password 密码
     * @return
     */
    public Observable<User> login(String account, String password) {
        return BmobUser.loginByAccountObservable(User.class, account, password);
    }

    /**
     * 验证手机号后或者登陆后才能修改密码
     *
     * @param sms  短信验证码
     * @param newP 新密码
     * @return
     */
    public Observable<Void> modifyPassword(String sms, String newP) {
        return BmobUser.resetPasswordBySMSCodeObservable(sms, newP);
    }


    /**
     * 发送验证码
     *
     * @param phone
     * @param template
     * @return
     */
    public Observable<Integer> sendMessage(String phone, String template) {
        return BmobSMS.requestSMSCodeObservable(phone, template);
    }


    /**
     * 验证而后注册
     *
     * @param phone    手机号码
     * @param sms      短信验证码
     * @param password 密码
     * @return
     */
    public Observable<User> registerByPhone(final String phone, String sms, final String password) {
        return BmobSMS.verifySmsCodeObservable(phone, sms).concatMap(new Func1<Void, Observable<User>>() {
            @Override
            public Observable<User> call(Void aVoid) {
                User user = new User();
                user.setUsername(phone);
                user.setPassword(password);
                user.setMobilePhoneNumber(phone);
                user.setMobilePhoneNumberVerified(true);
                return user.signUpObservable(User.class);
            }
        });
    }


    /**
     * 一键注册
     *
     * @param account
     * @param password
     * @return
     */
    public Observable<User> oneKeyRegister(final String account, final String password) {
        User user = new User();
        user.setUsername(account);
        user.setPassword(password);
        return user.signUpObservable(User.class);
    }


    /**
     * 发布帖子
     *
     * @param content 帖子内容
     * @return
     */
    public Observable<String> publishPost(String content) {
        Post post = new Post();
        post.setAuthor(BmobUser.getCurrentUser(User.class));
        post.setContent(content);
        return post.saveObservable();
    }


    /**
     * 删除帖子
     *
     * @param objectId 帖子id
     * @return
     */
    public Observable<Void> deletePost(String objectId) {
        Post post = new Post();
        return post.deleteObservable(objectId);
    }


    /**
     * 查找帖子
     *
     * @param page  第几页
     * @param count 每页个数
     * @return
     */
    public Observable<List<Post>> findPosts(int page, int count) {
        BmobQuery<Post> bmobQuery = new BmobQuery<>();
        bmobQuery.setLimit(count);
        bmobQuery.include("author");//将author字段的内容也列出
        bmobQuery.order("-createdAt");//以创建时间为倒序排出列表
        int skip = (page - 1) * count;
        bmobQuery.setSkip(skip);
        return bmobQuery.findObjectsObservable(Post.class);
    }


    /**
     * 发布评论
     *
     * @param post           所评论的帖子
     * @param replyToComment 所回复的评论，如果是直接评论帖子则设置为null
     * @param replyToUser 所回复的用户
     * @param content        评论的内容
     * @return
     */
    public Observable<String> publishComment(Post post, Comment replyToComment, User replyToUser, String content) {
        Comment comment = new Comment();
        comment.setPost(post);
        if (replyToComment != null)
            comment.setReplyToComment(replyToComment);
        if (replyToUser!=null)
            comment.setReplyToUser(replyToUser);
        comment.setContent(content);
        comment.setCommentator(BmobUser.getCurrentUser(User.class));
        return comment.saveObservable();
    }

    /**
     * 删除评论
     *
     * @param objectId
     * @return
     */
    public Observable<Void> deleteComment(String objectId) {
        Comment comment = new Comment();
        return comment.deleteObservable(objectId);
    }


    /**
     * 查找帖子的评论
     *
     * @param post  帖子
     * @param page  第几页
     * @param count 每页个数
     * @return
     */
    public Observable<List<Comment>> findComments(Post post, int page, int count) {
        BmobQuery<Comment> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("post", post);
        bmobQuery.setLimit(count);
        bmobQuery.include("replyToComment,replyToUser,commentator");//将其他字段的内容也列出
        int skip = (page - 1) * count;
        bmobQuery.setSkip(skip);
        return bmobQuery.findObjectsObservable(Comment.class);
    }


    /**
     * 点赞
     *
     * @param post 所点赞的帖子
     * @return
     */
    public Observable<String> love(Post post) {
        Love love = new Love();
        love.setPost(post);
        love.setLover(BmobUser.getCurrentUser(User.class));
        return love.saveObservable();
    }


    /**
     * 取消点赞
     *
     * @param objectId
     * @return
     */
    public Observable<Void> unlove(String objectId) {
        Love love = new Love();
        return love.deleteObservable(objectId);
    }


    /**
     * 查找帖子的全部点赞
     *
     * @param post 帖子
     * @return
     */
    public Observable<List<Love>> findLoves(Post post) {
        BmobQuery<Love> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("post", post);
        bmobQuery.include("lover");
        return bmobQuery.findObjectsObservable(Love.class);
    }

}
