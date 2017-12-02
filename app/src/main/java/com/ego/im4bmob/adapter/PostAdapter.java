package com.ego.im4bmob.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import com.ego.im4bmob.R;
import com.ego.im4bmob.mvp.bean.Post;
import com.ego.im4bmob.ui.PostDetailsActivity;

/**
 * Created on 17/8/31 18:23
 */

public class PostAdapter extends RecyclerView.Adapter {


    private Context mContext;
    private List<Post> mPosts;


    public PostAdapter(Context context, List<Post> posts) {
        mContext = context;
        mPosts = posts;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_post, null, false);
        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


        final Post post = mPosts.get(position);
        PostHolder postHolder = (PostHolder) holder;
        if (post.getAuthor().getAvatar() == null)
            Glide.with(mContext).load(R.mipmap.icon_message_press).into(postHolder.mIvUserAvatar);
        else
            Glide.with(mContext).load(post.getAuthor().getAvatar().getFileUrl()).into(postHolder.mIvUserAvatar);
        postHolder.mTvPostContent.setText(post.getContent());
        postHolder.mTvPostTime.setText(post.getCreatedAt());
        postHolder.mTvUserName.setText(post.getAuthor().getUsername());
        postHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(mContext, PostDetailsActivity.class).putExtra("post", post));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }


    class PostHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_user_avatar)
        ImageView mIvUserAvatar;
        @Bind(R.id.tv_user_name)
        TextView mTvUserName;
        @Bind(R.id.tv_post_content)
        TextView mTvPostContent;
        @Bind(R.id.tv_post_time)
        TextView mTvPostTime;

        public PostHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);//不能使用mContext需要使用this
        }
    }
}
