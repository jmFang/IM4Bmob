package com.ego.im4bmob.mvp.bean;

import com.ego.im4bmob.bean.User;
import cn.bmob.v3.BmobObject;

/**
 * Created on 17/8/31 15:43
 */

public class Love extends BmobObject {
    User lover;
    Post post;

    public User getLover() {
        return lover;
    }

    public void setLover(User lover) {
        this.lover = lover;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
