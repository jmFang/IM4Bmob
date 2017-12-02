package com.ego.im4bmob.mvp.presenter;


import java.util.List;

import com.ego.im4bmob.bean.User;
import com.ego.im4bmob.mvp.bean.Comment;
import com.ego.im4bmob.mvp.bean.Love;
import com.ego.im4bmob.mvp.bean.Post;
import com.ego.im4bmob.mvp.model.BmobModel;
import com.ego.im4bmob.mvp.view.PostDetailsView;
import rx.functions.Action1;

/**
 * Created on 17/8/31 17:15
 */

public class PostDetailsPresenter {


    private PostDetailsView mPublishCommentView;
    private BmobModel mBmobModel;

    public PostDetailsPresenter(PostDetailsView publishCommentView) {
        mPublishCommentView = publishCommentView;
        mBmobModel = new BmobModel();
    }


    /**
     *
     * @param post
     * @param replyToComment
     * @param replyToUser
     * @param content
     */
    public void publishComment(Post post, Comment replyToComment, User replyToUser, String content){
        mPublishCommentView.showDialog();
        mBmobModel.publishComment(post,replyToComment,replyToUser,content)
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        mPublishCommentView.hideDialog();
                        mPublishCommentView.publishCommentSuccess();

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                        mPublishCommentView.hideDialog();
                        mPublishCommentView.showError(throwable);
                    }
                });
    }


    /**
     *
     * @param objectId
     */
    public void deleteComment(String objectId){
        mPublishCommentView.showDialog();
        mBmobModel.deleteComment(objectId)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {

                        mPublishCommentView.hideDialog();
                        mPublishCommentView.deleteCommentSuccess();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mPublishCommentView.hideDialog();
                        mPublishCommentView.showError(throwable);
                    }
                });
    }

    /**
     *
     * @param post
     * @param page
     * @param count
     */
    public void findComments(Post post,int page,int count){
        mPublishCommentView.showDialog();
        mBmobModel.findComments(post,page,count).subscribe(new Action1<List<Comment>>() {
            @Override
            public void call(List<Comment> comments) {

                mPublishCommentView.hideDialog();
                mPublishCommentView.showComments(comments);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {

                mPublishCommentView.hideDialog();
                mPublishCommentView.showError(throwable);
            }
        });
    }


    /**
     *
     * @param post
     */
    public void love(Post post){
        mPublishCommentView.showDialog();
        mBmobModel.love(post)
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        mPublishCommentView.hideDialog();
                        mPublishCommentView.loveSuccess();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mPublishCommentView.hideDialog();
                        mPublishCommentView.showError(throwable);
                    }
                });
    }

    /**
     *
     * @param objectId
     */
    public void unlove(String objectId){
        mPublishCommentView.showDialog();
        mBmobModel.unlove(objectId)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {

                        mPublishCommentView.hideDialog();
                        mPublishCommentView.unloveSuccess();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                        mPublishCommentView.hideDialog();
                        mPublishCommentView.showError(throwable);
                    }
                });
    }

    /**
     *
     * @param objectId
     */
    public void deletePost(String objectId){

        mPublishCommentView.showDialog();

        mBmobModel.deletePost(objectId)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {

                        mPublishCommentView.hideDialog();
                        mPublishCommentView.deletePostSuccess();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mPublishCommentView.hideDialog();
                        mPublishCommentView.showError(throwable);
                    }
                });
    }


    /**
     *
     * @param post
     */
    public void showLoves(Post post){
        mPublishCommentView.showDialog();
        mBmobModel.findLoves(post)
                .subscribe(new Action1<List<Love>>() {
                    @Override
                    public void call(List<Love> loves) {
                        mPublishCommentView.hideDialog();
                        mPublishCommentView.showLoves(loves);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }

}
