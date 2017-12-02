package com.ego.im4bmob.mvp.view;


import java.util.List;

import com.ego.im4bmob.mvp.bean.Post;

/**
 * Created on 17/8/31 17:47
 */

public interface ShowPostsView extends BmobView {
    void showPosts(List<Post> posts);
}
