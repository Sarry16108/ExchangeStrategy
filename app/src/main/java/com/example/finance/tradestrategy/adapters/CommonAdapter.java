package com.example.finance.tradestrategy.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by yanghj on 2017/6/2.
 */

public abstract class CommonAdapter<T> extends RecyclerView.Adapter<CommonAdapter.NewViewHolder>
{
    protected Context mContext;
    protected int mLayoutId;
    protected List<T> mDatas;
    private int      mMaxSize = -1;     //数据最大数
    protected LayoutInflater mInflater;
    private OnClickListener    mClickListener;
    private OnLongClickListener    mLongClickListener;

    public CommonAdapter(Context context, int layoutId, List<T> datas)
    {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mLayoutId = layoutId;
        if (null == datas) {
            mDatas = new ArrayList<>(10);
        } else {
            mDatas = datas;
        }
    }

    public interface OnClickListener<T>{
        void onClick(NewViewHolder holder, int postion, T value);
    }

    public interface OnLongClickListener<T>{
        void onClick(NewViewHolder holder, int postion, T value);
    }

    public static class NewViewHolder extends RecyclerView.ViewHolder {
        public ViewDataBinding mBinding;
        public NewViewHolder(View itemView) {
            super(itemView);
//            mBinding = DataBindingUtil.bind(itemView);
        }
        public void setDataBinding(ViewDataBinding dataBinding) {
            mBinding = dataBinding;
        }

        public void setVariable(int variableId, Object obj) {
            mBinding.setVariable(variableId, obj);
        }
    }
    @Override
    public NewViewHolder onCreateViewHolder(final ViewGroup parent, int viewType)
    {
//        View view = View.inflate(mContext, mLayoutId, parent);
        ViewDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), mLayoutId, parent, false);
        NewViewHolder viewHolder = new NewViewHolder(dataBinding.getRoot());
        viewHolder.setDataBinding(dataBinding);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final NewViewHolder holder, final int position)
    {
        final T obj = mDatas.get(position);
        convert(holder, position, mDatas.get(position));
        if (null != mClickListener) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.onClick(holder, position, obj);
                }
            });
        }

        if (null != mLongClickListener) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mLongClickListener.onClick(holder, position, obj);
                    return true;
                }
            });
        }

    }

    public abstract void convert(NewViewHolder holder, int pos, T t);

    public void setOnClickListener(OnClickListener listener){
        mClickListener = listener;
    }

    public void setOnLongClickListener(OnLongClickListener listener) {
        mLongClickListener = listener;
    }

    @Override
    public int getItemCount()
    {
        return mDatas.size();
    }

    public void update() {
        notifyDataSetChanged();
    }

    public void updateItem(int index) {
        notifyItemChanged(index);
    }


    public List<T> getDatas() {
        return mDatas;
    }

    public void setMaxSize(int size) {
        mMaxSize = size;
    }

    public synchronized void appendItem(T item) {
        if (null == mDatas) {
            return;
        }

        mDatas.add(item);
        checkMaxSize();
        notifyDataSetChanged();
    }


    public synchronized void addHeadItem(T item) {
        if (null == mDatas) {
            return;
        }

        mDatas.add(0, item);
        checkMaxSize();
        notifyDataSetChanged();
    }

    public synchronized void replace(List<T> list) {
        mDatas.clear();
        mDatas.addAll(list);
        checkMaxSize();
        notifyDataSetChanged();
    }

    public synchronized void replace(Collection<T> list) {
        mDatas.clear();
        mDatas.addAll(list);
        checkMaxSize();
        notifyDataSetChanged();
    }

    //达到数量上限时候，删除最后的数据
    private synchronized void checkMaxSize() {
        if (0 < mMaxSize && mDatas.size() > mMaxSize) {
            int pos = mDatas.size() - 1;
            for (int i = pos; i > mMaxSize - 1; --i) {
                mDatas.remove(i);
            }
        }
    }

    public synchronized void removeFromEnd(int count) {
        if (mDatas.size() <= count) {
            mDatas.clear();
        } else {
            int pos = mDatas.size() - 1;
            for (int i = pos; i > pos - count; --i) {
                mDatas.remove(i);
            }
        }

        notifyDataSetChanged();
    }
}