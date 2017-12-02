package com.ego.im4bmob.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import com.ego.im4bmob.R;
import com.ego.im4bmob.bean.User;
import com.ego.im4bmob.event.DeleteCommentEvent;
import com.ego.im4bmob.event.ReplyToEvent;
import com.ego.im4bmob.mvp.bean.Comment;
import cn.bmob.v3.BmobUser;

/**
 * Created on 17/9/1 12:08
 */

public class CommentAdapter extends RecyclerView.Adapter {



    private Context mContext;
    private List<Comment> mComments;

    public CommentAdapter(Context context, List<Comment> comments) {
        mContext = context;
        mComments = comments;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_comment, null, false);
        return new CommentHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        CommentHolder commentHolder = (CommentHolder) holder;
        final Comment comment = mComments.get(position);
        if (comment.getCommentator().getAvatar() == null)
            Glide.with(mContext).load(R.mipmap.icon_message_press).into(commentHolder.mIvCommentAvatar);
        else
            Glide.with(mContext).load(comment.getCommentator().getAvatar().getFileUrl()).into(commentHolder.mIvCommentAvatar);

        if (comment.getReplyToComment() == null) {

            commentHolder.mTvCommentContent.setText(comment.getContent());
        } else {
            String content = "回复" + "<font color='#007BFF' >" + comment.getReplyToUser().getUsername() + "</font>" + "：" + comment.getContent();
            commentHolder.mTvCommentContent.setText(Html.fromHtml(content));
        }
        commentHolder.mTvCommentTime.setText(comment.getCreatedAt());
        commentHolder.mTvCommentUserName.setText(comment.getCommentator().getUsername());


        commentHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (BmobUser.getCurrentUser().getObjectId().equals(comment.getCommentator().getObjectId())) {
                    //TODO 长按删除自己的评论，使用event bus通知
                    EventBus.getDefault().post(new DeleteCommentEvent(comment));
                } else {
                    EventBus.getDefault().post(new DeleteCommentEvent(null));
                }
                return false;
            }
        });

        commentHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String objectId = BmobUser.getCurrentUser(User.class).getObjectId();
                if (!objectId.equals(comment.getCommentator().getObjectId())) {
                    //TODO 点击回复别人的评论，使用event bus通知
                    EventBus.getDefault().post(new ReplyToEvent(comment));
                } else {
                    EventBus.getDefault().post(new ReplyToEvent(null));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }


    class CommentHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.iv_comment_avatar)
        ImageView mIvCommentAvatar;
        @Bind(R.id.tv_comment_user_name)
        TextView mTvCommentUserName;
        @Bind(R.id.tv_comment_time)
        TextView mTvCommentTime;
        @Bind(R.id.tv_comment_content)
        TextView mTvCommentContent;
        public CommentHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
