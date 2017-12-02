package com.ego.im4bmob.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import butterknife.Bind;
import com.ego.im4bmob.R;
import com.ego.im4bmob.adapter.base.BaseViewHolder;
import com.ego.im4bmob.base.ImageLoaderFactory;
import com.ego.im4bmob.bean.User;
import com.ego.im4bmob.ui.UserInfoActivity;
import cn.bmob.newim.bean.BmobIMAudioMessage;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMSendStatus;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;

/**
 * 发送的语音类型
 */
public class SendVoiceHolder extends BaseViewHolder {

  @Bind(R.id.iv_avatar)
  protected ImageView iv_avatar;

  @Bind(R.id.iv_fail_resend)
  protected ImageView iv_fail_resend;

  @Bind(R.id.tv_time)
  protected TextView tv_time;

  @Bind(R.id.tv_voice_length)
  protected TextView tv_voice_length;
  @Bind(R.id.iv_voice)
  protected ImageView iv_voice;

  @Bind(R.id.tv_send_status)
  protected TextView tv_send_status;

  @Bind(R.id.progress_load)
  protected ProgressBar progress_load;

  BmobIMConversation c;
  public SendVoiceHolder(Context context, ViewGroup root,BmobIMConversation c,OnRecyclerViewListener onRecyclerViewListener) {
    super(context, root, R.layout.item_chat_sent_voice,onRecyclerViewListener);
    this.c =c;
  }

  @Override
  public void bindData(Object o) {
    BmobIMMessage msg = (BmobIMMessage)o;
    //用户信息的获取必须在buildFromDB之前，否则会报错'Entity is detached from DAO context'
    final BmobIMUserInfo info = msg.getBmobIMUserInfo();
    ImageLoaderFactory.getLoader().loadAvator(iv_avatar,info != null ? info.getAvatar() : null, R.mipmap.icon_message_press);
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
    String time = dateFormat.format(msg.getCreateTime());
    tv_time.setText(time);
    //使用buildFromDB方法转化成指定类型的消息
    final BmobIMAudioMessage  message = BmobIMAudioMessage.buildFromDB(true,msg);
    tv_voice_length.setText(message.getDuration()+"\''");

    int status =message.getSendStatus();
    if (status == BmobIMSendStatus.SEND_FAILED.getStatus()||status == BmobIMSendStatus.UPLOAD_FAILED.getStatus()) {//发送失败/上传失败
        iv_fail_resend.setVisibility(View.VISIBLE);
        progress_load.setVisibility(View.GONE);
        tv_send_status.setVisibility(View.INVISIBLE);
        tv_voice_length.setVisibility(View.INVISIBLE);
    } else if (status== BmobIMSendStatus.SENDING.getStatus()) {
        progress_load.setVisibility(View.VISIBLE);
        iv_fail_resend.setVisibility(View.GONE);
        tv_send_status.setVisibility(View.INVISIBLE);
        tv_voice_length.setVisibility(View.INVISIBLE);
    } else {//发送成功
        iv_fail_resend.setVisibility(View.GONE);
        progress_load.setVisibility(View.GONE);
        tv_send_status.setVisibility(View.GONE);
        tv_voice_length.setVisibility(View.VISIBLE);
    }

    iv_voice.setOnClickListener(new NewRecordPlayClickListener(getContext(),message,iv_voice));

    iv_voice.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
          if (onRecyclerViewListener != null) {
              onRecyclerViewListener.onItemLongClick(getAdapterPosition());
          }
          return true;
      }
    });

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
    //重发
    iv_fail_resend.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        c.resendMessage(message, new MessageSendListener() {
          @Override
          public void onStart(BmobIMMessage msg) {
              progress_load.setVisibility(View.VISIBLE);
              iv_fail_resend.setVisibility(View.GONE);
              tv_send_status.setVisibility(View.INVISIBLE);
          }

          @Override
          public void done(BmobIMMessage msg, BmobException e) {
            if(e==null){
                tv_send_status.setVisibility(View.VISIBLE);
                tv_send_status.setText("已发送");
                iv_fail_resend.setVisibility(View.GONE);
                progress_load.setVisibility(View.GONE);
            }else{
                iv_fail_resend.setVisibility(View.VISIBLE);
                progress_load.setVisibility(View.GONE);
                tv_send_status.setVisibility(View.INVISIBLE);
            }
          }
        });
      }
    });
  }

  public void showTime(boolean isShow) {
    tv_time.setVisibility(isShow ? View.VISIBLE : View.GONE);
  }
}
