package com.ego.im4bmob.ui.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ego.im4bmob.R;
import com.ego.im4bmob.base.ParentWithNaviFragment;
import com.ego.im4bmob.bean.User;
import com.ego.im4bmob.event.RefreshEvent;
import com.ego.im4bmob.model.UserModel;
import com.ego.im4bmob.mvp.bean.Installation;
import com.ego.im4bmob.ui.LogActivity;
import com.ego.im4bmob.ui.image_selector.MultiImageSelector;
import com.ego.im4bmob.util.BmobUtils;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.v3.BmobInstallationManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.functions.Action1;

import static android.app.Activity.RESULT_OK;

/**
 * 设置
 *
 * @author :smile
 * @project:SetFragment
 * @date :2016-01-25-18:23
 */
public class SetFragment extends ParentWithNaviFragment {

    @Bind(R.id.v_top)
    View mVTop;
    @Bind(R.id.tv_left)
    ImageView mTvLeft;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_right)
    TextView mTvRight;
    @Bind(R.id.btn_logout)
    Button mBtnLogout;
    @Bind(R.id.civ_avatar)
    CircleImageView mCivAvatar;
    @Bind(R.id.tv_username)
    TextView mTvUsername;

    @Override
    protected String title() {
        return "设置";
    }

    public static SetFragment newInstance() {
        SetFragment fragment = new SetFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SetFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_set, container, false);
        initNaviView();
        ButterKnife.bind(this, rootView);
        String username = UserModel.getInstance().getCurrentUser().getUsername();
        mTvUsername.setText(TextUtils.isEmpty(username) ? "" : username);
        if (UserModel.getInstance().getCurrentUser().getAvatar() != null)
            Glide.with(getActivity()).load(UserModel.getInstance().getCurrentUser().getAvatar().getFileUrl()).into(mCivAvatar);
        else Glide.with(getActivity()).load(R.mipmap.icon_message_press).into(mCivAvatar);
        return rootView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.civ_avatar, R.id.btn_logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.civ_avatar:
                //TODO 修改头像

                select();
                break;
            case R.id.btn_logout:

                modifyInstallationUser();

                break;
        }
    }

    /**
     * 修改设备表的用户信息：先查询设备表中的数据，再修改数据中用户信息
     */
    private void modifyInstallationUser() {
        BmobQuery<Installation> bmobQuery = new BmobQuery<>();
        final String id = BmobInstallationManager.getInstallationId();
        bmobQuery.addWhereEqualTo("installationId", id);
        bmobQuery.findObjectsObservable(Installation.class)
                .subscribe(new Action1<List<Installation>>() {
                    @Override
                    public void call(List<Installation> installations) {

                        if (installations.size() > 0) {
                            Installation installation = installations.get(0);
                            User user = new User();
                            installation.setUser(user);
                            user.setObjectId("");
                            installation.updateObservable()
                                    .subscribe(new Action1<Void>() {
                                        @Override
                                        public void call(Void aVoid) {
                                            BmobUtils.toast(getActivity(),"更新设备用户信息成功！");
                                            /**
                                             * TODO 更新成功之后再退出
                                             */
                                            //TODO 连接：3.2、退出登录需要断开与IM服务器的连接
                                            BmobIM.getInstance().disConnect();
                                            BmobUser.logOut();
                                            startActivity(new Intent(getActivity(), LogActivity.class));
                                            getActivity().finish();
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
    public void select() {

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            MultiImageSelector.create()
                    .showCamera(true) // show camera or not. true by default
                    .count(1) // max select image size, 9 by default. used width #.multi()
                    .multi() // multi mode, default mode;
                    .start(this, REQUEST_CODE_SELECT_IMAGE);
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission_group.STORAGE,Manifest.permission_group.CAMERA},
                    REQUEST_READ_EXTERNAL_STORAGE);
        }
    }

    //TODO 权限申请

    public static final int REQUEST_CODE_SELECT_IMAGE = 0;
    public static final int REQUEST_READ_EXTERNAL_STORAGE = 1;
    public static final int REQUEST_WRITE_EXTERNAL_STORAGE = 2;
    public static final int REQUEST_CAMERA = 3;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGE) {
            if (resultCode == RESULT_OK) {
                final List<String> paths = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);
                for (String s : paths) {
                    Log.e("path", s);
                }

                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setTitle("正在上传头像……");
                progressDialog.show();
                Glide.with(getActivity()).load(paths.get(0)).into(mCivAvatar);
                final BmobFile bmobFile = new BmobFile(new File(paths.get(0)));

                bmobFile.upload(new UploadFileListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            Logger.i("上传成功");

                            progressDialog.dismiss();

                            Toast.makeText(getActivity(), "上传头像成功", Toast.LENGTH_SHORT).show();
                            final User user = BmobUser.getCurrentUser(User.class);
                            if (user == null)
                                return;
                            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                            progressDialog.setTitle("正在更新头像……");
                            progressDialog.show();
                            user.setAvatar(bmobFile);
                            user.updateObservable().subscribe(new Action1<Void>() {
                                @Override
                                public void call(Void aVoid) {
                                    Toast.makeText(getActivity(), "更新头像成功", Toast.LENGTH_SHORT).show();
                                    Logger.d("更新头像成功");
                                    progressDialog.dismiss();
                                    EventBus.getDefault().post(new RefreshEvent());
                                    //TODO 会话：2.7、更新用户资料，用于在会话页面、聊天页面以及个人信息页面显示
                                    BmobIM.getInstance().
                                            updateUserInfo(new BmobIMUserInfo(user.getObjectId(),
                                                    user.getUsername(), user.getAvatar()==null?null:user.getAvatar().getFileUrl()));
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    Toast.makeText(getActivity(), "更新头像失败：" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    Logger.e("更新头像失败：" + throwable.getMessage());
                                }
                            });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "上传头像失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Logger.e("上传失败：" + e.getMessage());
                        }

                    }
                });
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "您已经同意了读取外置存储器权限", Toast.LENGTH_SHORT).show();

                    MultiImageSelector.create()
                            .showCamera(true) // show camera or not. true by default
                            .count(1) // max select image size, 9 by default. used width #.multi()
                            .multi() // multi mode, default mode;
                            .start(this, REQUEST_CODE_SELECT_IMAGE);

                } else {
                    Toast.makeText(getActivity(), "您已经拒绝了读取外置存储器权限", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            case REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "您已经同意了写入外置存储器权限", Toast.LENGTH_SHORT).show();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast.makeText(getActivity(), "您已经拒绝了写入外置存储器权限", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            case REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "您已经同意了照相机权限", Toast.LENGTH_SHORT).show();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast.makeText(getActivity(), "您已经拒绝了照相机权限", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }
}
