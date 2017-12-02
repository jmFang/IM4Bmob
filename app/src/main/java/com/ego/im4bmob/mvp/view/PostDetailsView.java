package com.ego.im4bmob.mvp.view;


import java.util.List;

import com.ego.im4bmob.mvp.bean.Comment;
import com.ego.im4bmob.mvp.bean.Love;

/**
 * Created on 17/8/31 17:14
 */

public interface PostDetailsView extends BmobView{
    //评论相关
    void publishCommentSuccess();
    void deleteCommentSuccess();
    void showComments(List<Comment> comments);

    //帖子相关
    void deletePostSuccess();

    //点赞相关
    void loveSuccess();
    void unloveSuccess();
    void showLoves(List<Love> loves);

}
