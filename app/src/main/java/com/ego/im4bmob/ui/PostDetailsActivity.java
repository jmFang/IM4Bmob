package com.ego.im4bmob.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ego.im4bmob.R;
import com.ego.im4bmob.adapter.CommentAdapter;
import com.ego.im4bmob.adapter.LoveAdapter;
import com.ego.im4bmob.base.BaseActivity;
import com.ego.im4bmob.bean.User;
import com.ego.im4bmob.event.DeleteCommentEvent;
import com.ego.im4bmob.event.RefreshPostEvent;
import com.ego.im4bmob.event.ReplyToEvent;
import com.ego.im4bmob.mvp.bean.Comment;
import com.ego.im4bmob.mvp.bean.Love;
import com.ego.im4bmob.mvp.bean.Post;
import com.ego.im4bmob.mvp.presenter.PostDetailsPresenter;
import com.ego.im4bmob.mvp.view.PostDetailsView;
import com.ego.im4bmob.util.BmobUtils;
import com.ego.im4bmob.widget.SwipeRecyclerView;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.PushListener;

/**
 * Created on 17/8/31 16:53
 */

public class PostDetailsActivity extends BaseActivity implements PostDetailsView {


    @Bind(R.id.tool_bar)
    Toolbar mToolBar;
    @Bind(R.id.iv_user_avatar)
    ImageView mIvUserAvatar;
    @Bind(R.id.tv_user_name)
    TextView mTvUserName;
    @Bind(R.id.tv_post_content)
    TextView mTvPostContent;
    @Bind(R.id.tv_post_time)
    TextView mTvPostTime;
    @Bind(R.id.tv_post_delete)
    TextView mTvPostDelete;
    @Bind(R.id.iv_love)
    ImageView mIvLove;
    @Bind(R.id.tv_error)
    TextView mTvError;
    @Bind(R.id.btn_comment_publish)
    Button mBtnCommentPublish;
    @Bind(R.id.edt_comment_publish)
    EditText mEdtCommentPublish;
    @Bind(R.id.rl_comment_publish)
    RelativeLayout mRlCommentPublish;
    @Bind(R.id.recycle_love)
    RecyclerView mRecycleLove;
    @Bind(R.id.rl_love)
    RelativeLayout mRlLove;
    @Bind(R.id.swipe_recycle_comment)
    SwipeRecyclerView mSwipeRecycleComment;
    private Post mPost;

    private List<Love> mLoves;
    private LoveAdapter mLoveAdapter;


    private PostDetailsPresenter mPostDetailsPresenter;
    private CommentAdapter mCommentAdapter;
    private List<Comment> mComments;
    private int page = 1;
    private final int COUNT = 12;
    private final int PAGE = 1;
    private Comment mCurrentReplyTo = null;

    private Love mLove = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ButterKnife.bind(this);
        mToolBar.setTitle("帖子详情，当前用户：" + BmobUser.getCurrentUser().getUsername());
        mToolBar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        mToolBar.setBackgroundColor(ContextCompat.getColor(this, R.color.color_theme));

        mPost = (Post) getIntent().getSerializableExtra("post");
        if (mPost.getAuthor().getAvatar() == null)
            Glide.with(this).load(R.mipmap.icon_message_press).into(mIvUserAvatar);
        else
            Glide.with(this).load(mPost.getAuthor().getAvatar().getFileUrl()).into(mIvUserAvatar);

        mTvPostContent.setText(mPost.getContent());
        mTvPostTime.setText(mPost.getCreatedAt());
        mTvUserName.setText(mPost.getAuthor().getUsername());


        mRecycleLove.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 8);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1;
            }
        });
        mRecycleLove.setLayoutManager(gridLayoutManager);

        mSwipeRecycleComment.getRecyclerView().setHasFixedSize(true);
        mSwipeRecycleComment.getRecyclerView().setLayoutManager(new LinearLayoutManager(this));
        mSwipeRecycleComment.setOnLoadListener(new SwipeRecyclerView.OnLoadListener() {
            @Override
            public void onRefresh() {
                refresh();
            }

            @Override
            public void onLoadMore() {
                loadMore();
            }
        });

        mComments = new ArrayList<>();
        mLoves = new ArrayList<>();
        mPostDetailsPresenter = new PostDetailsPresenter(this);
        mPostDetailsPresenter.showLoves(mPost);
        mPostDetailsPresenter.findComments(mPost, PAGE, COUNT);


        /**
         * 判断帖子是否是当前用户所发，用于显示隐藏删除帖子按钮
         */
        if (BmobUser.getCurrentUser(User.class).getObjectId().equals(mPost.getAuthor().getObjectId())) {
            mTvPostDelete.setVisibility(View.VISIBLE);
        } else {
            mTvPostDelete.setVisibility(View.GONE);
        }
    }

    /**
     *
     */
    private void loadMore() {
        page = page + 1;
        mSwipeRecycleComment.setRefreshEnable(false);
        mPostDetailsPresenter.findComments(mPost, page, COUNT);
    }

    /**
     *
     */
    private void refresh() {
        page = PAGE;
        mSwipeRecycleComment.setLoadMoreEnable(false);
        mPostDetailsPresenter.findComments(mPost, page, COUNT);
    }

    @Override
    public void showDialog() {
    }

    @Override
    public void hideDialog() {
    }

    @Override
    public void showError(Throwable throwable) {
        Toast.makeText(this,throwable.getMessage(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void publishCommentSuccess() {
        // TODO 评论成功
        BmobUtils.toast(this, "评论成功！");
        mEdtCommentPublish.setText("");
        push("您有一条来自" + BmobUser.getCurrentUser().getUsername() + "的评论消息");
        refresh();
    }

    /**
     * 评论、点赞成功，给帖子的发布者推送消息
     */
    private void push(String content) {
        BmobPushManager bmobPushManager = new BmobPushManager();
        BmobQuery<BmobInstallation> query = BmobInstallation.getQuery();
        query.addWhereEqualTo("user", mPost.getAuthor());
        bmobPushManager.setQuery(query);
        bmobPushManager.pushMessage(content, new PushListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Logger.e("推送成功！");
                } else {
                    Logger.e("异常：" + e.getMessage());
                }
            }
        });
    }

    @Override
    public void deleteCommentSuccess() {
        // TODO 删除评论成功
        BmobUtils.toast(this, "删除评论成功");
        refresh();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeleteCommentEvent(DeleteCommentEvent event) {
        if (event.getComment() != null) {
            mPostDetailsPresenter.deleteComment(event.getComment().getObjectId());
            refresh();
        } else {
            Logger.e("不能删除其他用户的评论！");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReplyToEvent(ReplyToEvent event) {
        if (event.getReplyTo() != null) {

            mEdtCommentPublish.setHint("回复@" + event.getReplyTo().getCommentator().getUsername() + "：");
            mCurrentReplyTo = event.getReplyTo();
        } else {
            mEdtCommentPublish.setHint("请输入评论！");
            mCurrentReplyTo = null;
        }
    }


    @Override
    public void showComments(List<Comment> comments) {
        // TODO 显示评论列表
        mSwipeRecycleComment.setLoadMoreEnable(true);
        mSwipeRecycleComment.setRefreshEnable(true);
        if (page == PAGE) {
            mComments.clear();
            mComments.addAll(comments);
            mSwipeRecycleComment.setRefreshing(false);
            if (mComments.size() < 1) {
                mSwipeRecycleComment.setEmptyView(mTvError);
            } else {
                if (mComments.size() < COUNT) {
                    mSwipeRecycleComment.complete();
                    mSwipeRecycleComment.setLoadMoreEnable(false);
                }
                if (mCommentAdapter == null) {
                    mCommentAdapter = new CommentAdapter(this, mComments);
                    mSwipeRecycleComment.setAdapter(mCommentAdapter);
                } else {
                    mCommentAdapter.notifyDataSetChanged();
                }
            }
        } else {
            if (comments.size() < COUNT) {
                mSwipeRecycleComment.complete();
                mSwipeRecycleComment.setLoadMoreEnable(false);
            }
            mSwipeRecycleComment.stopLoadingMore();
            mComments.addAll(comments);
            mCommentAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void deletePostSuccess() {
        // TODO 删除帖子成功
        EventBus.getDefault().post(new RefreshPostEvent());
        BmobUtils.toast(this, "删除帖子成功");
        finish();
    }

    @Override
    public void loveSuccess() {
        // TODO 点赞成功
        BmobUtils.toast(this, "点赞成功");
        mPostDetailsPresenter.showLoves(mPost);
        mIvLove.setImageResource(R.mipmap.ic_love_selected);
        push("您有一条来自" + BmobUser.getCurrentUser().getUsername() + "的点赞消息");
    }

    @Override
    public void unloveSuccess() {
        // TODO 取消点赞成功
        BmobUtils.toast(this, "取消点赞成功");
        mPostDetailsPresenter.showLoves(mPost);
        mIvLove.setImageResource(R.mipmap.ic_love_nor);
    }

    @Override
    public void showLoves(List<Love> loves) {
        // TODO 显示点赞列表
        mLoves.clear();
        mLoves.addAll(loves);
        Love tmpLove = null;
        for (Love love : loves) {
            if (love.getLover().getObjectId().equals(BmobUser.getCurrentUser(User.class).getObjectId())) {
                tmpLove = love;
                mIvLove.setImageResource(R.mipmap.ic_love_selected);
            }
        }
        mLove = tmpLove;
        if (mLoveAdapter == null) {
            mLoveAdapter = new LoveAdapter(this, mLoves);
            mRecycleLove.setAdapter(mLoveAdapter);
        } else {
            mLoveAdapter.notifyDataSetChanged();
        }
    }

    @OnClick({R.id.tv_post_delete, R.id.iv_love, R.id.btn_comment_publish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_post_delete:
                mPostDetailsPresenter.deletePost(mPost.getObjectId());
                break;
            case R.id.iv_love:
                if (mLove == null) {
                    mPostDetailsPresenter.love(mPost);
                } else {
                    mPostDetailsPresenter.unlove(mLove.getObjectId());
                }
                break;
            case R.id.btn_comment_publish:
                String content = mEdtCommentPublish.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    BmobUtils.toast(this, "请输入评论内容！");
                    return;
                }
                if (mCurrentReplyTo == null) {
                    mPostDetailsPresenter.publishComment(mPost, null, null, content);
                } else {
                    mPostDetailsPresenter.publishComment(mPost, mCurrentReplyTo, mCurrentReplyTo.getCommentator(), content);
                    mCurrentReplyTo = null;
                }
                break;
        }
    }
}
