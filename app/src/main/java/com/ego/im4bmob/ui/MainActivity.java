package com.ego.im4bmob.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import com.ego.im4bmob.R;
import com.ego.im4bmob.base.BaseActivity;
import com.ego.im4bmob.bean.User;
import com.ego.im4bmob.db.NewFriendManager;
import com.ego.im4bmob.event.RefreshEvent;
import com.ego.im4bmob.ui.fragment.ContactFragment;
import com.ego.im4bmob.ui.fragment.ConversationFragment;
import com.ego.im4bmob.ui.fragment.DiscoverFragment;
import com.ego.im4bmob.ui.fragment.SetFragment;
import com.ego.im4bmob.util.IMMLeaks;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.newim.notification.BmobNotificationManager;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;

/**
 * @author :smile
 * @project:MainActivity
 * @date :2016-01-15-18:23
 */
public class MainActivity extends BaseActivity {

    @Bind(R.id.btn_conversation)
    TextView btn_conversation;
    @Bind(R.id.btn_set)
    TextView btn_set;
    @Bind(R.id.btn_contact)
    TextView btn_contact;

    @Bind(R.id.iv_conversation_tips)
    ImageView iv_conversation_tips;
    @Bind(R.id.iv_contact_tips)
    ImageView iv_contact_tips;
    @Bind(R.id.btn_discover)
    TextView mBtnDiscover;
    @Bind(R.id.main_bottom)
    LinearLayout mMainBottom;
    @Bind(R.id.line)
    LinearLayout mLine;
    @Bind(R.id.fragment_container)
    RelativeLayout mFragmentContainer;

    private TextView[] mTabs;
    private ConversationFragment conversationFragment;
    private SetFragment setFragment;
    private DiscoverFragment mDiscoverFragment;
    ContactFragment contactFragment;
    private int index;
    private int currentTabIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        final User user = BmobUser.getCurrentUser(User.class);
        //TODO 连接：3.1、登录成功、注册成功或处于登录状态重新打开应用后执行连接IM服务器的操作
        //判断用户是否登录，并且连接状态不是已连接，则进行连接操作
        if (!TextUtils.isEmpty(user.getObjectId()) &&
                BmobIM.getInstance().getCurrentStatus().getCode() != ConnectionStatus.CONNECTED.getCode()) {
            BmobIM.connect(user.getObjectId(), new ConnectListener() {
                @Override
                public void done(String uid, BmobException e) {
                    if (e == null) {
                        //服务器连接成功就发送一个更新事件，同步更新会话及主页的小红点
                        EventBus.getDefault().post(new RefreshEvent());
                        //TODO 会话：2.7、更新用户资料，用于在会话页面、聊天页面以及个人信息页面显示
                        BmobIM.getInstance().
                                updateUserInfo(new BmobIMUserInfo(user.getObjectId(),
                                        user.getUsername(), user.getAvatar()==null?null:user.getAvatar().getFileUrl()));
                    } else {
                        toast(e.getMessage());
                    }
                }
            });
            //TODO 连接：3.3、监听连接状态，可通过BmobIM.getInstance().getCurrentStatus()来获取当前的长连接状态
            BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
                @Override
                public void onChange(ConnectionStatus status) {
                    toast(status.getMsg());
                    Logger.i(BmobIM.getInstance().getCurrentStatus().getMsg());
                }
            });
        }
        //解决leancanary提示InputMethodManager内存泄露的问题
        IMMLeaks.fixFocusedViewLeak(getApplication());
    }


    @Override
    protected void initView() {
        super.initView();


        initTab();
    }

    private void initTab() {
        mTabs = new TextView[4];
        mTabs[0] = btn_conversation;
        mTabs[1] = btn_contact;
        mTabs[2] = mBtnDiscover;
        mTabs[3] = btn_set;

        conversationFragment = new ConversationFragment();
        setFragment = new SetFragment();
        mDiscoverFragment = new DiscoverFragment();
        contactFragment = new ContactFragment();

        onTabSelect(btn_conversation);
    }

    public void onTabSelect(View view) {


        switch (view.getId()) {
            case R.id.btn_conversation:
                index = 0;
                switchFragment(mCurrentFragment, conversationFragment);
                break;
            case R.id.btn_contact:
                index = 1;
                switchFragment(mCurrentFragment, contactFragment);
                break;
            case R.id.btn_discover:
                index = 2;
                switchFragment(mCurrentFragment, mDiscoverFragment);
                break;
            case R.id.btn_set:
                index = 3;
                switchFragment(mCurrentFragment, setFragment);
                break;
        }
        onTabIndex(index);
    }

    private void onTabIndex(int index) {
        mTabs[currentTabIndex].setSelected(false);
        mTabs[index].setSelected(true);
        currentTabIndex = index;
    }

    private Fragment mCurrentFragment = null;

    /**
     * 切换fragment，避免切换一直new fragment对象
     *
     * @param from
     * @param to
     */
    public void switchFragment(Fragment from, Fragment to) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mCurrentFragment == null) {
            transaction.add(R.id.fragment_container, to).commit();
            mCurrentFragment = to;
            return;
        }
        if (mCurrentFragment != to) {
            mCurrentFragment = to;
            if (!to.isAdded()) {
                if (from.isAdded()) {
                    transaction.hide(from).add(R.id.fragment_container, to).commit();
                } else {
                    transaction.add(R.id.fragment_container, to).commit();
                }
            } else {
                transaction.hide(from).show(to).commit();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //每次进来应用都检查会话和好友请求的情况
        checkRedPoint();
        //进入应用后，通知栏应取消
        BmobNotificationManager.getInstance(this).cancelNotification();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清理导致内存泄露的资源
        BmobIM.getInstance().clear();
    }

    /**
     * 注册消息接收事件
     *
     * @param event
     */
    //TODO 消息接收：8.3、通知有在线消息接收
    @Subscribe
    public void onEventMainThread(MessageEvent event) {
        checkRedPoint();
    }

    /**
     * 注册离线消息接收事件
     *
     * @param event
     */
    //TODO 消息接收：8.4、通知有离线消息接收
    @Subscribe
    public void onEventMainThread(OfflineMessageEvent event) {
        checkRedPoint();
    }

    /**
     * 注册自定义消息接收事件
     *
     * @param event
     */
    //TODO 消息接收：8.5、通知有自定义消息接收
    @Subscribe
    public void onEventMainThread(RefreshEvent event) {
        checkRedPoint();
    }

    /**
     *
     */
    private void checkRedPoint() {
        //TODO 会话：4.4、获取全部会话的未读消息数量
        int count = (int) BmobIM.getInstance().getAllUnReadCount();
        if (count > 0) {
            iv_conversation_tips.setVisibility(View.VISIBLE);
        } else {
            iv_conversation_tips.setVisibility(View.GONE);
        }
        //TODO 好友管理：是否有好友添加的请求
        if (NewFriendManager.getInstance(this).hasNewFriendInvitation()) {
            iv_contact_tips.setVisibility(View.VISIBLE);
        } else {
            iv_contact_tips.setVisibility(View.GONE);
        }
    }

}
