package com.ego.im4bmob.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ego.im4bmob.R;
import com.ego.im4bmob.adapter.base.BaseViewHolder;
import com.ego.im4bmob.base.ImageLoaderFactory;
import com.ego.im4bmob.bean.User;
import com.ego.im4bmob.ui.UserInfoActivity;

import java.text.SimpleDateFormat;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.v3.datatype.BmobFile;

/**
 * 接收到的视频类型--这是举个例子，并没有展示出视频缩略图等信息，开发者可自行设置
 */
public class ReceiveVideoHolder extends BaseViewHolder {

  @Bind(R.id.iv_avatar)
  protected ImageView iv_avatar;

  @Bind(R.id.tv_time)
  protected TextView tv_time;

  @Bind(R.id.tv_message)
  protected TextView tv_message;

  public ReceiveVideoHolder(Context context, ViewGroup root, OnRecyclerViewListener onRecyclerViewListener) {
    super(context, root, R.layout.item_chat_received_message,onRecyclerViewListener);
  }

  @OnClick({R.id.iv_avatar})
  public void onAvatarClick(View view) {

  }

  @Override
  public void bindData(Object o) {
    final BmobIMMessage message = (BmobIMMessage)o;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
    String time = dateFormat.format(message.getCreateTime());
    tv_time.setText(time);
    final BmobIMUserInfo info = message.getBmobIMUserInfo();
    ImageLoaderFactory.getLoader().loadAvator(iv_avatar,info != null ? info.getAvatar() : null, R.mipmap.icon_message_press);
    String content =  message.getContent();
    tv_message.setText("接收到的视频文件："+content);
    iv_avatar.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        toast("点击" + info.getName() + "的头像");
        Bundle bundle = new Bundle();
        User user = new User();
        BmobFile bmobFile = new BmobFile();
        bmobFile.setUrl(info.getAvatar());
        user.setAvatar(bmobFile);
        user.setUsername(info.getName());
        user.setObjectId(info.getUserId());
        bundle.putSerializable("u", user);
        startActivity(UserInfoActivity.class,bundle);
      }
    });

    tv_message.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          toast("点击"+message.getContent());
          if(onRecyclerViewListener!=null){
            onRecyclerViewListener.onItemClick(getAdapterPosition());
          }
        }
    });

    tv_message.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        if (onRecyclerViewListener != null) {
          onRecyclerViewListener.onItemLongClick(getAdapterPosition());
        }
        return true;
      }
    });
  }

  public void showTime(boolean isShow) {
    tv_time.setVisibility(isShow ? View.VISIBLE : View.GONE);
  }
}