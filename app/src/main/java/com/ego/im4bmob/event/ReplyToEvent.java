package com.ego.im4bmob.event;


import com.ego.im4bmob.mvp.bean.Comment;

/**
 * Created on 17/9/1 17:42
 */

public class ReplyToEvent {
    Comment replyTo;

    public ReplyToEvent(Comment replyTo) {
        this.replyTo = replyTo;
    }

    public Comment getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(Comment replyTo) {
        this.replyTo = replyTo;
    }
}
