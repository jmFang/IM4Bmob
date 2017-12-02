package com.ego.im4bmob.bean;

import java.io.File;

import com.ego.im4bmob.db.NewFriend;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * @author :smile
 * @project:User
 * @date :2016-01-22-18:11
 */
public class User extends BmobUser {

    private BmobFile avatar;

    public User() {
    }

    public User(NewFriend friend) {
        setObjectId(friend.getUid());
        setUsername(friend.getName());
        setAvatar(new BmobFile(new File(friend.getAvatar())));
    }


    public BmobFile getAvatar() {
        return avatar;
    }

    public void setAvatar(BmobFile avatar) {
        this.avatar = avatar;
    }
}
