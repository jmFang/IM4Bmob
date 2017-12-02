package com.ego.im4bmob.mvp.view;

/**
 * Created on 17/7/5 09:22
 */

public interface BmobView {
    void showDialog();
    void hideDialog();
    void showError(Throwable throwable);
}
