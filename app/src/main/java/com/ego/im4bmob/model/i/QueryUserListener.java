package com.ego.im4bmob.model.i;

import com.ego.im4bmob.bean.User;
import cn.bmob.newim.listener.BmobListener1;
import cn.bmob.v3.exception.BmobException;

/**
 * @author :smile
 * @project:QueryUserListener
 * @date :2016-02-01-16:23
 */
public abstract class QueryUserListener extends BmobListener1<User> {

    public abstract void done(User s, BmobException e);

    @Override
    protected void postDone(User o, BmobException e) {
        done(o, e);
    }
}
