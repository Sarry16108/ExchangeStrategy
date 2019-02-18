package com.example.finance.tradestrategy.customviews;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.RadioButton;

import com.example.finance.tradestrategy.BR;
import com.example.finance.tradestrategy.R;
import com.example.finance.tradestrategy.adapters.CommonAdapter;
import com.example.finance.tradestrategy.databinding.PopupMultipleSelectListBinding;
import com.example.finance.tradestrategy.databinding.PopupSingleSelectListBinding;
import com.example.finance.tradestrategy.entity.ItemSelectEntity;
import com.example.finance.tradestrategy.utils.ToolGlobalResource;

import java.util.List;

/**
 * Created by Administrator on 2017/8/21.
 */

public enum  ToolPopupWindow {
    INSTANCE;

    public void showMultipleSelectList(Context context, View anchor, PopupWindow.OnDismissListener onDismissListener, final List<ItemSelectEntity> items) {
        CommonAdapter commonAdapter = new CommonAdapter<ItemSelectEntity>(context, R.layout.layout_item_popup_multiple_select, items) {

            @Override
            public void convert(NewViewHolder holder, int pos, final ItemSelectEntity itemSelectEntity) {
                holder.setVariable(BR.value, itemSelectEntity.getValue());
                holder.setVariable(BR.checked, itemSelectEntity.isChecked());
                holder.setVariable(BR.onBindClick, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.itemSelected:
                                itemSelectEntity.setChecked(((CheckBox)v).isChecked());
                                break;
                        }
                    }
                });
            }
        };

        PopupMultipleSelectListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.popup_multiple_select_list, null, false);
        binding.showDatas.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        binding.showDatas.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        binding.showDatas.setAdapter(commonAdapter);

        PopupWindow popupWindow = new PopupWindow(binding.getRoot());
        popupWindow.setBackgroundDrawable(new ColorDrawable(ToolGlobalResource.getColor(R.color.gray_translucent)));
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(anchor);
        popupWindow.setOnDismissListener(onDismissListener);
    }


    public void showSingleSelectList(Context context, View anchor, PopupWindow.OnDismissListener onDismissListener, final List<ItemSelectEntity> items) {
        PopupSingleSelectListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.popup_single_select_list, null, false);

        int selectId = -1, idStart = 0;
        for (final ItemSelectEntity item : items) {
            RadioButton radioButton = new RadioButton(context);
            radioButton.setId(idStart++);
            if (item.isChecked()) {
                selectId = radioButton.getId();
            }

//            radioButton.setChecked(item.isChecked()); //或者radioButton.toggle();会使该项一直处于选中状态
            radioButton.setText(item.getValue());
            radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    item.setChecked(isChecked);
                }
            });
            binding.showDatas.addView(radioButton);
        }

        if (-1 != selectId) {
            binding.showDatas.check(selectId);
        }

        PopupWindow popupWindow = new PopupWindow(binding.getRoot());
        popupWindow.setBackgroundDrawable(new ColorDrawable(ToolGlobalResource.getColor(R.color.gray_translucent)));
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(anchor);
        popupWindow.setOnDismissListener(onDismissListener);
    }


}
