package com.ego.im4bmob.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.orhanobut.logger.Logger;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import com.ego.im4bmob.R;
import com.ego.im4bmob.adapter.OnRecyclerViewListener;
import com.ego.im4bmob.adapter.SearchUserAdapter;
import com.ego.im4bmob.base.ParentWithNaviActivity;
import com.ego.im4bmob.bean.User;
import com.ego.im4bmob.model.BaseModel;
import com.ego.im4bmob.model.UserModel;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * 搜索用户
 *
 * @author :smile
 * @project:SearchUserActivity
 * @date :2016-01-25-18:23
 */
public class SearchUserActivity extends ParentWithNaviActivity {

    @Bind(R.id.et_find_name)
    EditText et_find_name;
    @Bind(R.id.sw_refresh)
    SwipeRefreshLayout sw_refresh;
    @Bind(R.id.btn_search)
    Button btn_search;
    @Bind(R.id.rc_view)
    RecyclerView rc_view;
    LinearLayoutManager layoutManager;
    SearchUserAdapter adapter;

    @Override
    protected String title() {
        return "搜索用户";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        initNaviView();
        adapter = new SearchUserAdapter();
        layoutManager = new LinearLayoutManager(this);
        rc_view.setLayoutManager(layoutManager);
        rc_view.setAdapter(adapter);
        sw_refresh.setEnabled(true);
        sw_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                query();
            }
        });
        adapter.setOnRecyclerViewListener(new OnRecyclerViewListener() {
            @Override
            public void onItemClick(int position) {
                Bundle bundle = new Bundle();
                User user = adapter.getItem(position);
                bundle.putSerializable("u", user);
                startActivity(UserInfoActivity.class, bundle, false);
            }

            @Override
            public boolean onItemLongClick(int position) {
                return true;
            }
        });
    }

    @OnClick(R.id.btn_search)
    public void onSearchClick(View view) {
        sw_refresh.setRefreshing(true);
        query();
    }

    public void query() {
        String name = et_find_name.getText().toString();
        if (TextUtils.isEmpty(name)) {
            toast("请填写用户名");
            sw_refresh.setRefreshing(false);
            return;
        }
        UserModel.getInstance().queryUsers(name, BaseModel.DEFAULT_LIMIT,
                new FindListener<User>() {
                    @Override
                    public void done(List<User> list, BmobException e) {
                        if (e == null) {
                            sw_refresh.setRefreshing(false);
                            adapter.setDatas(list);
                            adapter.notifyDataSetChanged();
                        } else {
                            sw_refresh.setRefreshing(false);
                            adapter.setDatas(null);
                            adapter.notifyDataSetChanged();
                            Logger.e(e);
                        }
                    }
                }


        );
    }

}
