package com.ego.im4bmob.mvp.presenter;


import com.ego.im4bmob.mvp.model.BmobModel;
import com.ego.im4bmob.mvp.view.PublishPostView;
import rx.functions.Action1;

/**
 * Created on 17/8/31 17:15
 */

public class PublishPostPresenter {


    private PublishPostView mPublishCommentView;
    private BmobModel mBmobModel;

    public PublishPostPresenter(PublishPostView publishCommentView) {
        mPublishCommentView = publishCommentView;
        mBmobModel = new BmobModel();
    }

    public void publishPost(String content) {
        mPublishCommentView.showDialog();
        mBmobModel.publishPost(content)
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        mPublishCommentView.hideDialog();
                        mPublishCommentView.publishSuccess();

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                        mPublishCommentView.hideDialog();
                        mPublishCommentView.showError(throwable);
                    }
                });
    }
}
