package com.ego.im4bmob.adapter;

import android.content.Context;
import android.view.View;

import com.orhanobut.logger.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.ego.im4bmob.Config;
import com.ego.im4bmob.R;
import com.ego.im4bmob.adapter.base.BaseRecyclerAdapter;
import com.ego.im4bmob.adapter.base.BaseRecyclerHolder;
import com.ego.im4bmob.adapter.base.IMutlipleItem;
import com.ego.im4bmob.bean.AgreeAddFriendMessage;
import com.ego.im4bmob.bean.User;
import com.ego.im4bmob.db.NewFriend;
import com.ego.im4bmob.db.NewFriendManager;
import com.ego.im4bmob.model.UserModel;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * @author :smile
 * @project:NewFriendAdapter
 * @date :2016-04-27-14:18
 */
public class NewFriendAdapter extends BaseRecyclerAdapter<NewFriend> {

    public NewFriendAdapter(Context context, IMutlipleItem<NewFriend> items, Collection<NewFriend> datas) {
        super(context, items, datas);
    }

    @Override
    public void bindView(final BaseRecyclerHolder holder, final NewFriend add, int position) {
        holder.setImageView(add == null ? null : add.getAvatar(), R.mipmap.icon_message_press, R.id.iv_recent_avatar);
        holder.setText(R.id.tv_recent_name, add == null ? "未知" : add.getName());
        holder.setText(R.id.tv_recent_msg, add == null ? "未知" : add.getMsg());
        Integer status = add.getStatus();
        //当状态是未添加或者是已读未添加
        if (status == null || status == Config.STATUS_VERIFY_NONE || status == Config.STATUS_VERIFY_READED) {
            holder.setText(R.id.btn_aggree, "接受");
            holder.setEnabled(R.id.btn_aggree, true);
            holder.setOnClickListener(R.id.btn_aggree, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    agreeAdd(add, new SaveListener<Object>() {
                        @Override
                        public void done(Object o, BmobException e) {
                            if (e == null) {
                                holder.setText(R.id.btn_aggree, "已添加");
                                holder.setEnabled(R.id.btn_aggree, false);
                            } else {
                                holder.setEnabled(R.id.btn_aggree, true);
                                Logger.e("添加好友失败:" + e.getMessage());
                                toast("添加好友失败:" + e.getMessage());
                            }
                        }
                    });
                }
            });
        } else {
            holder.setText(R.id.btn_aggree, "已添加");
            holder.setEnabled(R.id.btn_aggree, false);
        }
    }

    /**
     * TODO 好友管理：9.10、添加到好友表中再发送同意添加好友的消息
     *
     * @param add
     * @param listener
     */
    private void agreeAdd(final NewFriend add, final SaveListener<Object> listener) {
        User user = new User();
        user.setObjectId(add.getUid());
        UserModel.getInstance()
                .agreeAddFriend(user, new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
                            sendAgreeAddFriendMessage(add, listener);
                        } else {
                            Logger.e(e.getMessage());
                            listener.done(null, e);
                        }
                    }
                });
    }

    /**
     * 发送同意添加好友的消息
     */
    //TODO 好友管理：9.8、发送同意添加好友
    private void sendAgreeAddFriendMessage(final NewFriend add, final SaveListener<Object> listener) {
        if (BmobIM.getInstance().getCurrentStatus().getCode() != ConnectionStatus.CONNECTED.getCode()) {
            toast("尚未连接IM服务器");
            return;
        }
        BmobIMUserInfo info = new BmobIMUserInfo(add.getUid(), add.getName(), add.getAvatar());
        //TODO 会话：4.1、创建一个暂态会话入口，发送同意好友请求
        BmobIMConversation conversationEntrance = BmobIM.getInstance().startPrivateConversation(info, true, null);
        //TODO 消息：5.1、根据会话入口获取消息管理，发送同意好友请求
        BmobIMConversation messageManager = BmobIMConversation.obtain(BmobIMClient.getInstance(), conversationEntrance);
        //而AgreeAddFriendMessage的isTransient设置为false，表明我希望在对方的会话数据库中保存该类型的消息
        AgreeAddFriendMessage msg = new AgreeAddFriendMessage();
        final User currentUser = BmobUser.getCurrentUser(User.class);
        msg.setContent("我通过了你的好友验证请求，我们可以开始 聊天了!");//这句话是直接存储到对方的消息表中的
        Map<String, Object> map = new HashMap<>();
        map.put("msg", currentUser.getUsername() + "同意添加你为好友");//显示在通知栏上面的内容
        map.put("uid", add.getUid());//发送者的uid-方便请求添加的发送方找到该条添加好友的请求
        map.put("time", add.getTime());//添加好友的请求时间
        msg.setExtraMap(map);
        messageManager.sendMessage(msg, new MessageSendListener() {
            @Override
            public void done(BmobIMMessage msg, BmobException e) {
                if (e == null) {//发送成功
                    NewFriendManager.getInstance(context).updateNewFriend(add, Config.STATUS_VERIFIED);
                    listener.done(msg, e);
                } else {//发送失败
                    Logger.e(e.getMessage());
                    listener.done(msg, e);
                }
            }
        });
    }
}
