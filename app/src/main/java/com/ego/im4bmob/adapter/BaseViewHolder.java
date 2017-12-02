package com.ego.im4bmob.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.ButterKnife;
import com.ego.im4bmob.base.BaseActivity;


public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {

  OnRecyclerViewListener onRecyclerViewListener;
  protected Context context;

  public BaseViewHolder(Context context, ViewGroup root, int layoutRes,OnRecyclerViewListener listener) {
    super(LayoutInflater.from(context).inflate(layoutRes, root, false));
    this.context=context;
    ButterKnife.bind(this, itemView);
    this.onRecyclerViewListener =listener;
    itemView.setOnClickListener(this);
    itemView.setOnLongClickListener(this);
  }

  public Context getContext() {
    return itemView.getContext();
  }

  public abstract void bindData(T t);

  private Toast toast;
  public void toast(final Object obj) {
    try {
      ((BaseActivity)context).runOnUiThread(new Runnable() {

        @Override
        public void run() {
          if (toast == null)
            toast = Toast.makeText(context,"", Toast.LENGTH_SHORT);
          toast.setText(obj.toString());
          toast.show();
        }
      });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onClick(View v) {
    if(onRecyclerViewListener!=null){
      onRecyclerViewListener.onItemClick(getAdapterPosition());
    }
  }

  @Override
  public boolean onLongClick(View v) {
    if(onRecyclerViewListener!=null){
      onRecyclerViewListener.onItemLongClick(getAdapterPosition());
    }
    return true;
  }

}