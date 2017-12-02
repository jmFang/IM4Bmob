package com.ego.im4bmob.ui;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.transition.Explode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ego.im4bmob.R;
import com.ego.im4bmob.bean.User;
import com.ego.im4bmob.model.UserModel;
import com.ego.im4bmob.mvp.bean.Installation;
import com.ego.im4bmob.util.BmobUtils;
import com.orhanobut.logger.Logger;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobInstallationManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import rx.functions.Action1;

public class LogActivity extends AppCompatActivity {


    @Bind(R.id.et_username)
    EditText mEtUsername;
    @Bind(R.id.et_password)
    EditText mEtPassword;
    @Bind(R.id.bt_go)
    Button mBtGo;
    @Bind(R.id.cv)
    CardView mCv;
    @Bind(R.id.fab)
    FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        ButterKnife.bind(this);

    }
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    // using @TargeApi instead of @SuppressLint("NewApi")
    @OnClick({R.id.bt_go, R.id.fab})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                getWindow().setExitTransition(null);
                getWindow().setEnterTransition(null);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options =
                            ActivityOptions.makeSceneTransitionAnimation(this, mFab, mFab.getTransitionName());
                    startActivity(new Intent(this, RegActivity.class), options.toBundle());
                } else {
                    startActivity(new Intent(this, RegActivity.class));
                }
                break;
            case R.id.bt_go:

                UserModel.getInstance().login(mEtUsername.getText().toString(), mEtPassword.getText().toString(), new LogInListener() {

                    @Override
                    public void done(Object o, BmobException e) {
                        if (e == null) {
                            //登录成功
                            modifyInstallationUser((User)o);
                            Explode explode = new Explode();
                            explode.setDuration(500);

                            getWindow().setExitTransition(explode);
                            getWindow().setEnterTransition(explode);
                            ActivityOptionsCompat oc2 = ActivityOptionsCompat.makeSceneTransitionAnimation(LogActivity.this);
                            Intent i2 = new Intent(LogActivity.this, MainActivity.class);
                            startActivity(i2, oc2.toBundle());
                        } else {
                            Logger.e(e.getMessage() + "(" + e.getErrorCode() + ")");
                            Toast.makeText(LogActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                break;
        }
    }

    /**
     * 修改设备表的用户信息：先查询设备表中的数据，再修改数据中用户信息
     *
     * @param user
     */
    private void modifyInstallationUser(final User user) {
        BmobQuery<Installation> bmobQuery = new BmobQuery<>();
        final String id = BmobInstallationManager.getInstallationId();
        bmobQuery.addWhereEqualTo("installationId", id);
        bmobQuery.findObjectsObservable(Installation.class)
                .subscribe(new Action1<List<Installation>>() {
                    @Override
                    public void call(List<Installation> installations) {

                        if (installations.size() > 0) {
                            Installation installation = installations.get(0);
                            installation.setUser(user);
                            installation.updateObservable()
                                    .subscribe(new Action1<Void>() {
                                        @Override
                                        public void call(Void aVoid) {
                                            BmobUtils.toast(LogActivity.this, "更新设备用户信息成功！");
                                        }
                                    }, new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {
                                            Logger.e("更新设备用户信息失败：" + throwable.getMessage());
                                        }
                                    });

                        } else {
                            Logger.e("后台不存在此设备Id的数据，请确认此设备Id是否正确！\n" + id);
                        }

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Logger.e("查询设备数据失败：" + throwable.getMessage());
                    }
                });
    }
}
