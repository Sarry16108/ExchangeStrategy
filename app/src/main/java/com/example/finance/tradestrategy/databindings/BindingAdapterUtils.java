package com.example.finance.tradestrategy.databindings;

import android.databinding.BindingAdapter;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.finance.tradestrategy.R;
import com.example.finance.tradestrategy.globaldata.InitData;
import com.example.finance.tradestrategy.utils.ToolGlobalResource;
import com.example.finance.tradestrategy.utils.ToolImage;

/**
 * Created by Administrator on 2017/6/8.
 */

public class BindingAdapterUtils {

    @BindingAdapter("itemStatusTextAndColor")    //true：红，false:灰
    public static void itemStatusTextAndColor(TextView view, String userRecordStatus) {
        if (InitData.RecordStatusDraft.equals(userRecordStatus)) {
            view.setTextColor(Color.RED);
            view.setText("持仓中");
        } else {
            view.setText("已结算");
            view.setTextColor(Color.GRAY);
        }
    }

    @BindingAdapter("img_url")
    public static void roundImageByUrl(ImageView imageView, String url) {
        ToolImage.showRoundImage(imageView, url);
    }

    @BindingAdapter("textColor")
    public static void textColor(TextView view, boolean isSpecial) {
        view.setTextColor(ToolGlobalResource.getColor(isSpecial ? R.color.app_text_gray : R.color.app_text_light_gray));
    }

    @BindingAdapter("visibility")
    public static void viewVisibility(View view, boolean isVisible) {
        view.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }
//
//    @BindingAdapter("directionTextColor")
//    public static void directionTextColor(TextView textView, StockStrategy strategy) {
//        switch (strategy.getForecastLevel()) {
//            case InitAppConstant.FORECAST_LEVEL_NEUTRAL_BEARISH:
//            case InitAppConstant.FORECAST_LEVEL_BEARISH:
//            case InitAppConstant.FORECAST_LEVEL_ULTRA_BEARISH:
//                textView.setTextColor(Color.GREEN);
//                break;
//            case InitAppConstant.FORECAST_LEVEL_NEUTRAL_BULLISH:
//            case InitAppConstant.FORECAST_LEVEL_BULLISH:
//            case InitAppConstant.FORECAST_LEVEL_ULTRA_BULLISH:
//                textView.setTextColor(Color.RED);
//                break;
//            case InitAppConstant.FORECAST_LEVEL_NEUTRAL:
//            default:
//                textView.setTextColor(Color.BLACK);
//                break;
//        }
//    }
//
//    @BindingAdapter("directionTipTextColor")
//    public static void directionTipTextColor(TextView textView, StockForecast forecast) {
//        switch (forecast.getForecastLevel()) {
//            case InitAppConstant.FORECAST_LEVEL_NEUTRAL_BEARISH:
//            case InitAppConstant.FORECAST_LEVEL_BEARISH:
//            case InitAppConstant.FORECAST_LEVEL_ULTRA_BEARISH:
//                textView.setTextColor(Color.GREEN);
//                break;
//            case InitAppConstant.FORECAST_LEVEL_NEUTRAL_BULLISH:
//            case InitAppConstant.FORECAST_LEVEL_BULLISH:
//            case InitAppConstant.FORECAST_LEVEL_ULTRA_BULLISH:
//                textView.setTextColor(Color.RED);
//                break;
//            case InitAppConstant.FORECAST_LEVEL_NEUTRAL:
//            default:
//                textView.setTextColor(Color.BLACK);
//                break;
//        }
//    }
//
//    @BindingAdapter("forecastImportanceTextColor")
//    public static void forecastImportanceTextColor(TextView textView, StockStrategy strategy) {
//        switch (DatabindingUtls.getImportance(strategy.getForecastLevel())) {
//            case 1:
//                textView.setTextColor(Color.BLUE);
//                break;
//            case 2:
//                textView.setTextColor(Color.MAGENTA);
//                break;
//            case 3:
//                textView.setTextColor(Color.RED);
//                break;
//            case 0:
//            default:
//                textView.setTextColor(Color.BLACK);
//                break;
//        }
//    }
}
