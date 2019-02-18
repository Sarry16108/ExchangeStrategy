package com.example.finance.tradestrategy.utils;

import java.util.Set;

/**
 * Created by yanghj on 2017/6/4.
 */

public class ToolString {

    public static String takeBeforeFirstSperator(String value, String spe) {
        int pos = value.indexOf(spe);
        if (0 < pos) {
            return value.substring(0, pos);
        }

        return "";
    }

    //t=1497242751119; Domain=uspard.com; Expires=Mon, 19-Jun-2017 04:45:51 GMT; Path=/
    public static long takeExpiredTimeFromCookie(String value) {
        int startPos = value.indexOf("Expires");
        if (0 < startPos) {
            int endPos = value.indexOf("GMT");
            if (endPos > startPos) {
                String expires = value.substring(startPos + 8, endPos + 3);
                return ToolTime.getFromGmt(expires);
            }
        }

        return 0;
    }

    public static String intToString(int value) {
        return String.valueOf(value);
    }

    public static String bytesToHexString(byte [] bytes) {
        StringBuilder builder = new StringBuilder();
        builder.append(bytes);
        return builder.toString();
    }

    public static String arySperator(Set<String> values, String sperator) {
        StringBuilder value = new StringBuilder();
        for (String item : values) {
            value.append(item);
            value.append(sperator);
        }

        if (0 < value.length()) {
            value.deleteCharAt(value.length() - 1);
        }

        return value.toString();
    }
}
