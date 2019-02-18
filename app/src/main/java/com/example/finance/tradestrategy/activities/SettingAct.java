package com.example.finance.tradestrategy.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;

import com.android.databinding.library.baseAdapters.BR;
import com.example.finance.tradestrategy.R;
import com.example.finance.tradestrategy.base.BaseApplication;
import com.example.finance.tradestrategy.baseui.BaseActivity;
import com.example.finance.tradestrategy.databinding.ActSettingBinding;
import com.example.finance.tradestrategy.entity.BaseResponse;
import com.example.finance.tradestrategy.entity.NetCallback;
import com.example.finance.tradestrategy.entity.SearchSuggest;
import com.example.finance.tradestrategy.globaldata.InitNetInfo;
import com.example.finance.tradestrategy.globaldata.MessageId;
import com.example.finance.tradestrategy.utils.ToolKeyBoard;
import com.example.finance.tradestrategy.utils.ToolRequest;
import com.example.finance.tradestrategy.utils.ToolToast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/14.
 */

public class SettingAct extends BaseActivity implements View.OnClickListener, NetCallback {

    private ActSettingBinding   mBinding;
    private List<String> mAddCode = new ArrayList<>(3);;
    private List<String> mRemoveCode = new ArrayList<>(3);;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.act_setting);
        mBinding.setVariable(BR.onBindClick, this);
        mBinding.stockCode.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add:
                addNew();
                ToolKeyBoard.closeKeybord(mBinding.stockCode);
                break;
            case R.id.remove:
                removeCode();
                ToolKeyBoard.closeKeybord(mBinding.stockCode);
                break;
            case R.id.back:
                onBackPressed();
                break;
            case R.id.finish:
                finishEdit();
                break;
        }
    }

    private void addNew() {
        final String code = mBinding.stockCode.getText().toString();
        if (TextUtils.isEmpty(code.trim())) {
            ToolToast.shortToast(this, "代码不能为空");
            return;
        }

        if (BaseApplication.mStockCodes.contains(code) || mAddCode.contains(code)) {
            ToolToast.shortToast(this, "已添加该代码");
            return;
        }

        ToolRequest.getInstance().searchByStockCode(code, new NetCallback() {
            @Override
            public void onError(String method, int connCode, String data) {
                packMsgAndSend(MessageId.TOAST_TIP, data);
            }

            @Override
            public void onSuccess(String method, BaseResponse data) {
                SearchSuggest searchSuggest = (SearchSuggest) data;
                if (1 == searchSuggest.getItems().size() && code.equals(searchSuggest.getItems().get(0).getSymbol())) {
                    SearchSuggest.SimilarCode similarCode = searchSuggest.getItems().get(0);
                    packMsgAndSend(MessageId.TOAST_TIP_LONG, "添加成功：" + similarCode.getNameCN() + " 代码：" + similarCode.getSymbol());
                    mAddCode.add(similarCode.getSymbol());
                } else {
                    packMsgAndSend(MessageId.TOAST_TIP_LONG, "未找到该股票代码，请正确填写后再试");
                }
            }
        });
    }

    private void removeCode() {
        String code = mBinding.stockCode.getText().toString();
        if (TextUtils.isEmpty(code.trim())) {
            ToolToast.shortToast(this, "代码不能为空");
            return;
        }

        if (!BaseApplication.mStockCodes.contains(code) && !mAddCode.contains(code)) {
            ToolToast.shortToast(this, "还未添加该代码");
            return;
        }

        //还未加入监控列表中的，只是在mAddCode删除
        if (mAddCode.contains(code)) {
            mAddCode.remove(code);
        } else {
            mRemoveCode.add(code);
        }

        ToolToast.shortToast(this, "已成功加入移除队列");
    }
    @Override
    public void onError(String method, int connCode, String data) {
        packMsgAndSend(MessageId.TOAST_TIP, data);
    }

    @Override
    public void onSuccess(String method, BaseResponse data) {
    }


    private void finishEdit() {
        if (0 < mAddCode.size() || 0 < mRemoveCode.size()) {
            Intent intent = new Intent();
            intent.putExtra("newCodes", mAddCode.toArray(new String[0]));
            intent.putExtra("removeCodes", mRemoveCode.toArray(new String[0]));
            setResult(RESULT_OK, intent);
        }

        finish();
    }
}
