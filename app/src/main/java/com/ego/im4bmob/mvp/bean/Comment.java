package com.ego.im4bmob.mvp.bean;

import com.ego.im4bmob.bean.User;
import cn.bmob.v3.BmobObject;

/**
 * Created on 17/8/31 15:45
 */

public class Comment extends BmobObject {
    User commentator;
    Post post;
    Comment replyToComment;
    User replyToUser;
    String content;


    public User getCommentator() {
        return commentator;
    }

    public void setCommentator(User commentator) {
        this.commentator = commentator;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Comment getReplyToComment() {
        return replyToComment;
    }

    public void setReplyToComment(Comment replyToComment) {
        this.replyToComment = replyToComment;
    }

    public User getReplyToUser() {
        return replyToUser;
    }

    public void setReplyToUser(User replyToUser) {
        this.replyToUser = replyToUser;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
