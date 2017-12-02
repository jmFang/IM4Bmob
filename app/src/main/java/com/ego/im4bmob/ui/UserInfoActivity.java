package com.ego.im4bmob.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.ego.im4bmob.R;
import com.ego.im4bmob.base.ImageLoaderFactory;
import com.ego.im4bmob.base.ParentWithNaviActivity;
import com.ego.im4bmob.bean.AddFriendMessage;
import com.ego.im4bmob.bean.User;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;

/**
 * 用户资料
 */
public class UserInfoActivity extends ParentWithNaviActivity {

    @Bind(R.id.iv_avator)
    ImageView iv_avator;
    @Bind(R.id.tv_name)
    TextView tv_name;
    @Bind(R.id.btn_add_friend)
    Button btn_add_friend;
    @Bind(R.id.btn_chat)
    Button btn_chat;


    //用户
    User user;
    //用户信息
    BmobIMUserInfo info;

    @Override
    protected String title() {
        return "个人资料";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        //导航栏
        initNaviView();
        //用户
        user = (User) getBundle().getSerializable("u");
        if (user.getObjectId().equals(getCurrentUid())) {//用户为登录用户
            btn_add_friend.setVisibility(View.GONE);
            btn_chat.setVisibility(View.GONE);
        } else {//用户为非登录用户
            btn_add_friend.setVisibility(View.VISIBLE);
            btn_chat.setVisibility(View.VISIBLE);
        }
        //构造聊天方的用户信息:传入用户id、用户名和用户头像三个参数

        BmobFile bmobFile = user.getAvatar();
        String fileUrl = null;
        String url = null;
        if (bmobFile != null) {
            try{
                fileUrl = bmobFile.getFileUrl();
                url = bmobFile.getUrl();
            }catch(Exception e){
                //TODO :handle exception
            }

        }
        info = new BmobIMUserInfo(user.getObjectId(), user.getUsername(), bmobFile == null ? null : (fileUrl == null ? (url == null ? null : url) : fileUrl));
        //加载头像
        ImageLoaderFactory.getLoader().loadAvator(iv_avator, bmobFile == null ? null : bmobFile == null ? null : (fileUrl == null ? (url == null ? null : url) : fileUrl), R.mipmap.icon_message_press);
        //显示名称
        tv_name.setText(user.getUsername());
    }


    @OnClick({R.id.btn_add_friend, R.id.btn_chat})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_add_friend:
                sendAddFriendMessage();
                break;
            case R.id.btn_chat:
                chat();
                break;
        }
    }


    /**
     * 发送添加好友的请求
     */
    //TODO 好友管理：9.7、发送添加好友请求
    private void sendAddFriendMessage() {
        if (BmobIM.getInstance().getCurrentStatus().getCode() != ConnectionStatus.CONNECTED.getCode()) {
            toast("尚未连接IM服务器");
            return;
        }
        //TODO 会话：4.1、创建一个暂态会话入口，发送好友请求
        BmobIMConversation conversationEntrance = BmobIM.getInstance().startPrivateConversation(info, true, null);
        //TODO 消息：5.1、根据会话入口获取消息管理，发送好友请求
        BmobIMConversation messageManager = BmobIMConversation.obtain(BmobIMClient.getInstance(), conversationEntrance);
        AddFriendMessage msg = new AddFriendMessage();
        User currentUser = BmobUser.getCurrentUser(User.class);
        msg.setContent("很高兴认识你，可以加个好友吗?");//给对方的一个留言信息
        Map<String, Object> map = new HashMap<>();
        map.put("name", currentUser.getUsername());//发送者姓名
        map.put("avatar", currentUser.getAvatar());//发送者的头像
        map.put("uid", currentUser.getObjectId());//发送者的uid
        msg.setExtraMap(map);
        messageManager.sendMessage(msg, new MessageSendListener() {
            @Override
            public void done(BmobIMMessage msg, BmobException e) {
                if (e == null) {//发送成功
                    toast("好友请求发送成功，等待验证");
                } else {//发送失败
                    toast("发送失败:" + e.getMessage());
                }
            }
        });
    }

    /**
     * 与陌生人聊天
     */
    private void chat() {
        if (BmobIM.getInstance().getCurrentStatus().getCode() != ConnectionStatus.CONNECTED.getCode()) {
            toast("尚未连接IM服务器");
            return;
        }
        //TODO 会话：4.1、创建一个常态会话入口，陌生人聊天
        BmobIMConversation conversationEntrance = BmobIM.getInstance().startPrivateConversation(info, null);
        Bundle bundle = new Bundle();
        bundle.putSerializable("c", conversationEntrance);
        startActivity(ChatActivity.class, bundle, false);
    }
}