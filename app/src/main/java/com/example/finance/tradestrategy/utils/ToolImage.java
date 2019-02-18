package com.example.finance.tradestrategy.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.finance.tradestrategy.R;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2017/6/9.
 */

public class ToolImage {

    public static Bitmap getImgByBase64(String datas) {
        byte [] bytes = Base64.decode(datas.getBytes(), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }

    //保存成memberId.jpeg，不分datas中格式
    public static String saveImgByBase64(String datas, String memberId) {
        int startPos = datas.indexOf(",");
        if (0 < startPos) {
            startPos += 1;
        } else {
            startPos = 0;
        }

        String path = ToolFile.getImagePath() + memberId + ".jpeg";
        if (!saveImageByData(datas.substring(startPos), path)) {
            return null;
        }

        return path;
    }

    //直接保存base64图片数据
    public static boolean saveImageByData(String datas, String path) {
        byte [] bytes = Base64.decode(datas.getBytes(), Base64.DEFAULT);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            fileOutputStream.write(bytes);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    public void testJsonToImg() {
        String value = "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 c/gf98dtp/FTY1MAAAAASUVORK5CYII=";


        String path = "/storage/emulated/0/aaa.png";
        saveImageByData(value, path);
    }

    private static RequestOptions mRequestOptions = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .fitCenter()
            .circleCrop()
            .format(DecodeFormat.PREFER_ARGB_8888)      //如果设置为rgb565,则没有透明度，剪切后背景为黑色
            .placeholder(R.mipmap.ic_user_default);

    //glide的封装
    public static void showRoundImage(ImageView view, String path) {
        Glide.with(view.getContext())
                .setDefaultRequestOptions(mRequestOptions)
                .load(path)
                .into(view);
    }
}
