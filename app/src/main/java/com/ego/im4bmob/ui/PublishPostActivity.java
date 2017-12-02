package com.ego.im4bmob.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.ego.im4bmob.R;
import com.ego.im4bmob.base.BaseActivity;
import com.ego.im4bmob.event.RefreshPostEvent;
import com.ego.im4bmob.mvp.presenter.PublishPostPresenter;
import com.ego.im4bmob.mvp.view.PublishPostView;
import com.ego.im4bmob.util.BmobUtils;

/**
 * Created on 17/8/31 16:54
 */

public class PublishPostActivity extends BaseActivity implements PublishPostView {


    @Bind(R.id.tool_bar)
    Toolbar mToolBar;
    @Bind(R.id.btn_publish)
    Button mBtnPublish;
    @Bind(R.id.edt_post_content)
    EditText mEdtPostContent;
    private PublishPostPresenter mPublishPostPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_post);
        ButterKnife.bind(this);
        mToolBar.setTitle("发布帖子");
        mToolBar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        mToolBar.setBackgroundColor(ContextCompat.getColor(this, R.color.color_theme));
        mPublishPostPresenter = new PublishPostPresenter(this);
    }

    @OnClick(R.id.btn_publish)
    public void onViewClicked() {
        String content = mEdtPostContent.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            BmobUtils.toast(this, "请输入帖子内容！");
            return;
        }
        mPublishPostPresenter.publishPost(content);
    }

    @Override
    public void showDialog() {

    }

    @Override
    public void hideDialog() {

    }

    @Override
    public void showError(Throwable throwable) {

        BmobUtils.toast(this, throwable.getMessage());
    }

    @Override
    public void publishSuccess() {


        EventBus.getDefault().post(new RefreshPostEvent());
        BmobUtils.toast(this, "发表帖子成功");
        finish();


    }
}
