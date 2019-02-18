package com.example.finance.tradestrategy.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2017/9/6.
 */

public enum ToolEncrypt {
    INSTANCE;


    public static String md5Encrypt(String paramString)
    {
        StringBuilder localStringBuilder;
        try
        {
            byte[] arrayOfByte = MessageDigest.getInstance("MD5").digest(paramString.getBytes("UTF-8"));
            localStringBuilder = new StringBuilder(2 * arrayOfByte.length);
            int i = arrayOfByte.length;
            for (int j = 0; j < i; j++)
            {
                int k = arrayOfByte[j];
                if ((k & 0xFF) < 16)
                    localStringBuilder.append("0");
                localStringBuilder.append(Integer.toHexString(k & 0xFF));
            }
        }
        catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
        {
            throw new RuntimeException("Huh, MD5 should be supported?", localNoSuchAlgorithmException);
        }
        catch (UnsupportedEncodingException localUnsupportedEncodingException)
        {
            throw new RuntimeException("Huh, UTF-8 should be supported?", localUnsupportedEncodingException);
        }
        return localStringBuilder.toString();
    }
}
