package com.example.finance.tradestrategy.fx678;

/**
 * Created by Administrator on 2017/9/8.
 */

import android.text.TextUtils;

import com.example.finance.tradestrategy.utils.ToolLog;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.Map;

public class StringUtils {
    private static final char[] DIGITS_LOWER = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};

    public static String buildKey(String paramString1, String paramString2) {
        return concatString(paramString1, "://", paramString2);
    }

    public static String bytesToHexString(byte[] paramArrayOfByte) {
        if (paramArrayOfByte == null)
            return "";
        return bytesToHexString(paramArrayOfByte, DIGITS_LOWER);
    }

    private static String bytesToHexString(byte[] paramArrayOfByte, char[] paramArrayOfChar) {
        int i = 0;
        int j = paramArrayOfByte.length;
        char[] arrayOfChar = new char[j << 1];
        for (int k = 0; k < j; k++) {
            int m = i + 1;
            arrayOfChar[i] = paramArrayOfChar[((0xF0 & paramArrayOfByte[k]) >>> 4)];
            i = m + 1;
            arrayOfChar[m] = paramArrayOfChar[(0xF & paramArrayOfByte[k])];
        }
        return new String(arrayOfChar);
    }

    public static String concatString(String paramString1, String paramString2) {
        return paramString1.length() + paramString2.length() + paramString1 + paramString2;
    }

    public static String concatString(String paramString1, String paramString2, String paramString3) {
        return paramString1.length() + paramString2.length() + paramString3.length() + paramString1 + paramString2 + paramString3;
    }

    public static String encodeQueryParams(Map<String, String> paramMap, String paramString) {
        if ((paramMap == null) || (paramMap.isEmpty()))
            return "";
        StringBuilder localStringBuilder = new StringBuilder(64);
        try {
            Iterator localIterator = paramMap.entrySet().iterator();
            while (localIterator.hasNext()) {
                Map.Entry localEntry = (Map.Entry) localIterator.next();
                if (localEntry.getKey() != null)
                    localStringBuilder.append(URLEncoder.encode((String) localEntry.getKey(), paramString)).append("=").append(URLEncoder.encode(stringNull2Empty((String) localEntry.getValue()), paramString).replace("+", "%20")).append("&");
            }
        } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
            ToolLog.e("Request", "format params failed", localUnsupportedEncodingException.getMessage());
        }

        return localStringBuilder.toString();
//            localStringBuilder.deleteCharAt(-1 + localStringBuilder.length());
    }

    public static boolean isBlank(String paramString) {
        int i;
        boolean bool1;
        if (paramString != null) {
            i = paramString.length();
            if (i != 0) ;
        } else {
            bool1 = true;
            return bool1;
        }
        for (int j = 0; ; j++) {
            if (j >= i)
                break;
            boolean bool2 = Character.isWhitespace(paramString.charAt(j));
            bool1 = false;
            if (!bool2)
                break;
        }
        label47:
        return true;
    }

    public static boolean isNotBlank(String paramString) {
        return !isBlank(paramString);
    }

    public static String longToIP(long paramLong) {
        StringBuilder localStringBuilder = new StringBuilder(16);
        long l = 1000000000L;
        do {
            localStringBuilder.append(paramLong / l);
            localStringBuilder.append('.');
            paramLong %= l;
            l /= 1000L;
        }
        while (l > 0L);
        localStringBuilder.setLength(-1 + localStringBuilder.length());
        return localStringBuilder.toString();
    }

    public static String md5ToHex(String paramString) {
        if (paramString == null)
            return null;
        try {
            String str = bytesToHexString(MessageDigest.getInstance("MD5").digest(paramString.getBytes("utf-8")));
            return str;
        } catch (Exception localException) {
        }
        return null;
    }

    public static String[] parseURL(String paramString) {
        if (TextUtils.isEmpty(paramString)) ;
        int i;
        String[] arrayOfString;
        int j;
        int k;
        do {
            String str;
            do {
                do {
                    if (paramString.startsWith("//")) {
                        paramString = "http:" + paramString;
                    }

                    i = paramString.indexOf("://");
                } while (i == -1);
                arrayOfString = new String[2];
                str = paramString.substring(0, i);
            } while ((!"http".equalsIgnoreCase(str)) && (!"https".equalsIgnoreCase(str)));

            arrayOfString[0] = str;
            j = paramString.length();
            for (k = i + 3; k < j; k++) {
                int m = paramString.charAt(k);
                if ((m == 47) || (m == 58) || (m == 63) || (m == 35)) {
                    arrayOfString[1] = paramString.substring(i + 3, k);
                    return arrayOfString;
                }
            }
        }
        while (k != j);
        arrayOfString[1] = paramString.substring(i + 3);
        return arrayOfString;
    }

    public static String simplifyString(String paramString, int paramInt) {
        if (paramString.length() <= paramInt)
            return paramString;
        return concatString(paramString.substring(0, paramInt), "......");
    }

    public static String stringNull2Empty(String paramString) {
        if (paramString == null)
            paramString = "";
        return paramString;
    }
}