package com.ego.im4bmob.mvp.bean;

import com.ego.im4bmob.bean.User;
import cn.bmob.v3.BmobObject;

/**
 * Created on 17/8/31 15:30
 */

public class Post extends BmobObject {
    User author;
    String content;


    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
