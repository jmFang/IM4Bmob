package com.ego.im4bmob.adapter;

import android.content.Context;
import android.view.View;

import java.util.Collection;

import com.ego.im4bmob.R;
import com.ego.im4bmob.adapter.base.BaseRecyclerAdapter;
import com.ego.im4bmob.adapter.base.BaseRecyclerHolder;
import com.ego.im4bmob.adapter.base.IMutlipleItem;
import com.ego.im4bmob.bean.Friend;
import com.ego.im4bmob.bean.User;
import com.ego.im4bmob.db.NewFriendManager;

/**
 * 联系人
 * 一种简洁的Adapter实现方式，可用于多种Item布局的recycleView实现，不用再写ViewHolder啦
 *
 * @author :smile
 * @project:ContactNewAdapter
 * @date :2016-04-27-14:18
 */
public class ContactAdapter extends BaseRecyclerAdapter<Friend> {

    public static final int TYPE_NEW_FRIEND = 0;
    public static final int TYPE_ITEM = 1;

    public ContactAdapter(Context context, IMutlipleItem<Friend> items, Collection<Friend> datas) {
        super(context, items, datas);
    }

    @Override
    public void bindView(BaseRecyclerHolder holder, Friend friend, int position) {
        if (holder.layoutId == R.layout.item_contact) {
            User user = friend.getFriendUser();
            //好友头像
            holder.setImageView(user == null || user.getAvatar() == null ? null : user.getAvatar().getFileUrl(), R.mipmap.icon_message_press, R.id.iv_recent_avatar);
            //好友名称
            holder.setText(R.id.tv_recent_name, user == null ? "未知" : user.getUsername());
        } else if (holder.layoutId == R.layout.header_new_friend) {
            if (NewFriendManager.getInstance(context).hasNewFriendInvitation()) {
                holder.setVisible(R.id.iv_msg_tips, View.VISIBLE);
            } else {
                holder.setVisible(R.id.iv_msg_tips, View.GONE);
            }
        }
    }

}
