package com.ego.im4bmob.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import com.ego.im4bmob.R;
import com.ego.im4bmob.mvp.bean.Love;

/**
 * Created on 17/9/1 14:40
 */

public class LoveAdapter extends RecyclerView.Adapter {


    private Context mContext;
    private List<Love> mLoves;

    public LoveAdapter(Context context, List<Love> loves) {
        mContext = context;
        mLoves = loves;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_love, null, false);
        return new LoveHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        LoveHolder loveHolder = (LoveHolder) holder;
        Love love = mLoves.get(position);
        if (love.getLover().getAvatar() == null)
            Glide.with(mContext).load(R.mipmap.icon_message_press).into(loveHolder.mIvLoveAvatar);
        else
            Glide.with(mContext).load(love.getLover().getAvatar().getFileUrl()).into(loveHolder.mIvLoveAvatar);
    }

    @Override
    public int getItemCount() {
        return mLoves.size();
    }


    class LoveHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.iv_love_avatar)
        ImageView mIvLoveAvatar;
        public LoveHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
